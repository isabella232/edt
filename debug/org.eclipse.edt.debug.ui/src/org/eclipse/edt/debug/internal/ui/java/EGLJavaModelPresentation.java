/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.ui.java;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.breakpoints.EGLBreakpoint;
import org.eclipse.edt.debug.core.breakpoints.EGLLineBreakpoint;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugElement;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.internal.core.java.EGLJavaDebugTarget;
import org.eclipse.edt.debug.internal.core.java.EGLJavaFunctionVariable;
import org.eclipse.edt.debug.internal.core.java.EGLJavaStackFrame;
import org.eclipse.edt.debug.internal.core.java.EGLJavaThread;
import org.eclipse.edt.debug.internal.ui.EDTDebugUIPlugin;
import org.eclipse.edt.debug.internal.ui.actions.BreakpointUtils;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.utils.BinaryReadOnlyFile;
import org.eclipse.edt.ide.ui.internal.editor.BinaryEditorInput;
import org.eclipse.edt.ide.ui.internal.util.EditorUtility;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.internal.debug.ui.JDIModelPresentation;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;

/**
 * Model presentation for handling labels, images, editors, etc.
 */
@SuppressWarnings("restriction")
public class EGLJavaModelPresentation extends JDIModelPresentation
{
	@Override
	public String getText( Object element )
	{
		if ( element instanceof EGLBreakpoint )
		{
			return getBreakpointText( (EGLBreakpoint)element );
		}
		
		if ( element instanceof IEGLJavaVariable )
		{
			try
			{
				IEGLJavaVariable var = (IEGLJavaVariable)element;
				StringBuilder buf = new StringBuilder( 100 );
				
				if ( isShowVariableTypeNames() )
				{
					String type = var.getReferenceTypeName();
					if ( type != null && (type = type.trim()).length() != 0 )
					{
						buf.append( type );
						buf.append( " " );//$NON-NLS-1$
					}
				}
				
				buf.append( var.getName() );
				String value = var.getValue().getValueString();
				if ( value != null )
				{
					buf.append( " = " ); //$NON-NLS-1$
					buf.append( value );
				}
				return buf.toString();
			}
			catch ( DebugException e )
			{
			}
		}
		
		if ( element instanceof EGLJavaStackFrame )
		{
			try
			{
				EGLJavaStackFrame frame = (EGLJavaStackFrame)element;
				if ( frame.isEGLStratum() )
				{
					return frame.getLabel();
				}
			}
			catch ( DebugException de )
			{
			}
		}
		else if ( element instanceof EGLJavaThread )
		{
			EGLJavaDebugTarget target = ((EGLJavaThread)element).getEGLJavaDebugTarget();
			if ( !target.supportsSourceDebugExtension() )
			{
				// When a bp is hit, the Java breakpoint info is displayed. Use our own label to show the EGL breakpoint info.
				IBreakpoint[] bps = ((EGLJavaThread)element).getJavaThread().getBreakpoints();
				if ( bps != null && bps.length > 0 && bps[ 0 ] instanceof IJavaLineBreakpoint )
				{
					EGLLineBreakpoint bp = ((EGLJavaDebugTarget)target).findCorrespondingBreakpoint( (IJavaLineBreakpoint)bps[ 0 ] );
					if ( bp != null )
					{
						IMarker marker = bp.getMarker();
						if ( marker != null && marker.exists() )
						{
							try
							{
								return NLS.bind( EGLJavaMessages.ThreadLabelSuspendedAtBreakpoint, new Object[] {
										((EGLJavaThread)element).getJavaThread().getName(), String.valueOf( bp.getLineNumber() ),
										marker.getResource().getName() } );
							}
							catch ( CoreException ce )
							{
								EDTDebugUIPlugin.log( ce );
							}
						}
					}
				}
			}
		}
		
		if ( element instanceof IEGLJavaDebugElement )
		{
			element = ((IEGLJavaDebugElement)element).getJavaDebugElement();
		}
		
		return super.getText( element );
	}
	
	@Override
	public void computeDetail( IValue value, IValueDetailListener listener )
	{
		if ( value instanceof IEGLJavaValue )
		{
			IEGLJavaValue eglValue = (IEGLJavaValue)value;
			
			String detail = eglValue.computeDetail();
			if ( detail != null )
			{
				listener.detailComputed( value, detail );
				return;
			}
			
			if ( eglValue.getJavaValue() != null )
			{
				value = eglValue.getJavaValue();
			}
		}
		
		super.computeDetail( value, listener );
	}
	
	@Override
	public Image getImage( Object item )
	{
		if ( item instanceof EGLJavaFunctionVariable )
		{
			return getJavaElementImageRegistry().get( JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_LOCAL_VARIABLE ) );
		}
		
		if ( item instanceof EGLBreakpoint )
		{
			return getBreakpointImage( (EGLBreakpoint)item );
		}
		
		if ( item instanceof IMarker )
		{
			IBreakpoint bp = DebugPlugin.getDefault().getBreakpointManager().getBreakpoint( (IMarker)item );
			if ( bp instanceof EGLBreakpoint )
			{
				return getBreakpointImage( (EGLBreakpoint)bp );
			}
		}
		
		if ( item instanceof IAdaptable )
		{
			IEGLJavaVariable var = (IEGLJavaVariable)((IAdaptable)item).getAdapter( IEGLJavaVariable.class );
			if ( var != null )
			{
				if ( var.isLocal() )
				{
					return getJavaElementImageRegistry().get( JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_LOCAL_VARIABLE ) );
				}
				return getJavaElementImageRegistry().get( JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_PUBLIC ) );
			}
		}
		
		if ( item instanceof IEGLJavaDebugElement )
		{
			item = ((IEGLJavaDebugElement)item).getJavaDebugElement();
		}
		
		return super.getImage( item );
	}
	
	private Image getBreakpointImage( EGLBreakpoint bp )
	{
		try
		{
			if ( bp instanceof EGLLineBreakpoint && ((EGLLineBreakpoint)bp).isRunToLine() )
			{
				return null;
			}
		}
		catch ( CoreException e )
		{
			EDTDebugUIPlugin.log( e );
		}
		
		boolean enabled = true;
		try
		{
			enabled = bp.isEnabled();
		}
		catch ( CoreException e )
		{
		}
		
		if ( enabled )
		{
			return DebugUITools.getImage( IDebugUIConstants.IMG_OBJS_BREAKPOINT );
		}
		else
		{
			return DebugUITools.getImage( IDebugUIConstants.IMG_OBJS_BREAKPOINT_DISABLED );
		}
	}
	
	private String getBreakpointText( EGLBreakpoint bp )
	{
		if ( bp instanceof EGLLineBreakpoint )
		{
			EGLLineBreakpoint lineBP = (EGLLineBreakpoint)bp;
			try
			{
				String name = bp.getTypeName();
				if ( name == null )
				{
					return EGLJavaMessages.LineBreakpointUnkown;
				}
				
				return NLS.bind( EGLJavaMessages.LineBreakpointLabel, new Object[] { name, lineBP.getLineNumber() } );
			}
			catch ( CoreException e )
			{
				EDTDebugCorePlugin.log( e );
				return EGLJavaMessages.LineBreakpointUnkown;
			}
		}
		return null;
	}
	
	public IEditorInput getEditorInput( Object item )
	{
		IClassFile classFile = null;
		if ( item instanceof IMarker )
		{
			item = DebugPlugin.getDefault().getBreakpointManager().getBreakpoint( (IMarker)item );
		}
		
		if ( item instanceof EGLBreakpoint )
		{
			IEGLElement element = BreakpointUtils.getElement( (EGLBreakpoint)item );
			if ( element != null )
			{
				if ( element.getElementType() == IEGLElement.CLASS_FILE )
				{
					classFile = (IClassFile)element;
					String srcName = BreakpointUtils.getRelativeBreakpointPath( (EGLBreakpoint)item );
					if ( srcName != null )
					{
						item = EditorUtility.getBinaryReadonlyFile( element.getEGLProject().getProject(), element.getPath().toString(), srcName );
					}
				}
				else
				{
					item = element.getResource();
				}
			}
			else
			{
				item = ((EGLBreakpoint)item).getMarker().getResource();
			}
		}
		
		if ( item instanceof BinaryReadOnlyFile )
		{
			return new BinaryEditorInput( (BinaryReadOnlyFile)item, classFile );
		}
		
		return super.getEditorInput( item );
	}
}
