/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.javascript.internal.launching;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.debug.ui.launching.EGLLaunchableTester;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.rui.utils.Util;

public class RuiLaunchableTester extends EGLLaunchableTester
{
	private static final String PROPERTY_IS_RUI_HANDLER = "isRuiHandler"; //$NON-NLS-1$
	
	@Override
	public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
	{
		Object element = resolveElement( receiver );
		if ( element == null )
		{
			return false;
		}
		
		if ( PROPERTY_IS_RUI_HANDLER.equals( property ) )
		{
			return isRuiHandler( element );
		}
		
		return false;
	}
	
	private boolean isRuiHandler( Object element )
	{
		if ( element instanceof IEGLFile )
		{
			return Util.isVESupportType( (IEGLFile)element, null );
		}
		else if ( element instanceof IFile && "egl".equalsIgnoreCase( ((IFile)element).getFileExtension() ) )// TODO remove second part when bug 377007 is fixed //$NON-NLS-1$
		{
			return Util.isVESupportType( (IFile)element );
		}
		return false;
	}
}
