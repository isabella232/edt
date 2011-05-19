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

import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.edt.debug.internal.core.java.EGLJavaDebugElement;
import org.eclipse.edt.debug.internal.core.java.EGLJavaFunctionContainerValue;
import org.eclipse.edt.debug.internal.core.java.EGLJavaFunctionValue;
import org.eclipse.edt.debug.internal.core.java.EGLJavaFunctionVariable;
import org.eclipse.edt.debug.internal.core.java.EGLJavaValue;
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
		if ( element instanceof EGLJavaDebugElement )
		{
			element = ((EGLJavaDebugElement)element).getJavaElement();
		}
		
		return super.getText( element );
	}
	
	@Override
	public void computeDetail( IValue value, IValueDetailListener listener )
	{
		if ( value instanceof EGLJavaFunctionValue || value instanceof EGLJavaFunctionContainerValue )
		{
			listener.detailComputed( value, "" ); //$NON-NLS-1$
			return;
		}
		
		if ( value instanceof EGLJavaValue )
		{
			value = ((EGLJavaValue)value).getJavaValue();
		}
		
		super.computeDetail( value, listener );
	}
	
	@Override
	public Image getImage( Object item )
	{
		if ( item instanceof EGLJavaFunctionVariable )
		{
			return getJavaElementImageRegistry().get(
					JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_LOCAL_VARIABLE ) );
		}
		
		if ( item instanceof EGLJavaDebugElement )
		{
			item = ((EGLJavaDebugElement)item).getJavaElement();
		}
		
		return super.getImage( item );
	}
}
