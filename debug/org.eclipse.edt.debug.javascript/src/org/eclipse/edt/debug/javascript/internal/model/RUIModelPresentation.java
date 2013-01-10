/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.javascript.internal.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugEditorPresentation;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.edt.debug.javascript.internal.launching.IRUIDebugConstants;
import org.eclipse.edt.ide.core.utils.BinaryReadOnlyFile;
import org.eclipse.edt.ide.debug.javascript.internal.utils.RUIDebugUtil;
import org.eclipse.edt.ide.rui.utils.Util;
import org.eclipse.edt.ide.ui.internal.editor.BinaryEditorInput;
import org.eclipse.edt.ide.ui.internal.editor.IEvEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

public class RUIModelPresentation extends LabelProvider implements IDebugModelPresentation, IDebugEditorPresentation
{
	protected boolean fShowTypes = false;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute( String attribute, Object value )
	{
		if ( value == null )
		{
			return;
		}
		if ( attribute.equals( IDebugModelPresentation.DISPLAY_VARIABLE_TYPE_NAMES ) )
		{
			fShowTypes = ((Boolean)value).booleanValue();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage( Object element )
	{
		if ( element instanceof RUIVariable )
		{
			return RUIDebugUtil.getImage( IRUIDebugConstants.RUI_ICON_VARIABLE );
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText( Object element )
	{
		if ( element instanceof RUIVariable )
		{
			return ((RUIVariable)element).getLabel( fShowTypes );
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#computeDetail(org.eclipse.debug.core.model.IValue, org.eclipse.debug.ui.IValueDetailListener)
	 */
	@Override
	public void computeDetail( IValue value, IValueDetailListener listener )
	{
		String valueString = null;
		try
		{
			valueString = value.getValueString();
		}
		catch ( DebugException e )
		{
		}
		listener.detailComputed( value, valueString );
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(java.lang.Object)
	 */
	@Override
	public IEditorInput getEditorInput( Object element )
	{
		if ( element instanceof BinaryReadOnlyFile )
		{
			return new BinaryEditorInput( (BinaryReadOnlyFile)element );
		}
		else if ( element instanceof IFile )
		{
			return new FileEditorInput( (IFile)element );
		}
		if ( element instanceof ILineBreakpoint )
		{
			return new FileEditorInput( (IFile)((ILineBreakpoint)element).getMarker().getResource() );
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(org.eclipse.ui.IEditorInput, java.lang.Object)
	 */
	@Override
	public String getEditorId( IEditorInput input, Object inputObject )
	{
		if ( input instanceof IFileEditorInput )
		{
			if ( inputObject instanceof IFile )
			{
				IFile file = (IFile)inputObject;
				
				if ( Util.isVESupportType( file ) )
				{
					return "org.eclipse.edt.ide.rui.visualeditor.EvEditor"; //$NON-NLS-1$
				}
				
				IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor( file.getName() );
				if ( desc != null )
				{
					return desc.getId();
				}
			}
			else if ( inputObject instanceof IBreakpoint )
			{
				IResource resource = ((IBreakpoint)inputObject).getMarker().getResource();
				if ( resource instanceof IFile )
				{
					IFile file = (IFile)resource;
					
					if ( Util.isVESupportType( file ) )
					{
						return "org.eclipse.edt.ide.rui.visualeditor.EvEditor"; //$NON-NLS-1$
					}
					
					IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor( file.getName() );
					if ( desc != null )
					{
						return desc.getId();
					}
				}
			}
		}
		
		try
		{
			return IDE.getEditorDescriptor( input.getName() ).getId();
		}
		catch ( PartInitException e )
		{
			return null;
		}
	}
	
	@Override
	public boolean addAnnotations( IEditorPart editor, IStackFrame frame )
	{
		// Use this hook to make sure the visual editor is on the source tab.
		if ( editor instanceof IEvEditor )
		{
			((IEvEditor)editor).showSourcePage();
		}
		return false;
	}
	
	@Override
	public void removeAnnotations( IEditorPart editor, IThread thread )
	{
		// Nothing to do.
	}
}
