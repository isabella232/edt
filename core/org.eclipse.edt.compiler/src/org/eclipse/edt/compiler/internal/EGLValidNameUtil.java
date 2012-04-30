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
package org.eclipse.edt.compiler.internal;

import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;


public class EGLValidNameUtil
{
	public static String getValidEglName(String eglName)
	{
		return getValidEglName( eglName, false );
	}
	public static String getValidEglPackageName(String eglName)
	{
		return  getValidEglName( eglName, true );
	}
	private static String getValidEglName(String eglName, final boolean forPackage)
	{
		if (eglName == null)
			return "null";
		
	    if (EGLKeywordHandler.getKeywordHashSet().contains(eglName.toLowerCase()))
	    {
	        return "_" + eglName;
	    }
	    else if (!EGLNameValidator.mildValidateCharacters(eglName, new ICompilerOptions() {
	    }))
	    {
	        return fixName(eglName);
	    }
	    else if (EGLNameValidator.startsWithEZE(eglName))
	    {
	        return "_" + eglName;
	    }
	    else
	    {
	        return eglName;
	    }
	}
	
	public static String fixName(String name)
	{
	    if (name == null)
	        return "null";
	    
	    if (name.length() == 0)
	        return name;
	    
	    StringBuffer eName = new StringBuffer(name);
		if (!Character.isJavaIdentifierStart(eName.charAt(0)))
			// '$' and '_' are already allowed by isJavaIdentifierStart 			
				eName.replace(0, 1, "_");

		for (int i = 1; i < name.length(); i++) 
		{
			if (!Character.isJavaIdentifierPart(name.charAt(i)))
			{
			// '$' and '_' are already allowed by isJavaIdentifierPart 				
			    eName.replace(i, i+1, "_");
			}
		} 
		
		return eName.toString();
	}

}
