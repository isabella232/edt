/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugElement;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.internal.core.java.EGLJavaFunctionVariable;
import org.eclipse.jdt.internal.debug.ui.JDIModelPresentation;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.graphics.Image;

/**
 * Model presentation for handling labels, images, editors, etc.
 */
@SuppressWarnings("restriction")
public class EGLJavaModelPresentation extends JDIModelPresentation
{
	@Override
	public String getText( Object element )
	{
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
}
