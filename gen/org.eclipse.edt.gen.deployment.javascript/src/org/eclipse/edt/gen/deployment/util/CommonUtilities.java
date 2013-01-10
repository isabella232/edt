/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.deployment.util;

import java.util.LinkedHashSet;

import org.eclipse.edt.gen.deployment.javascript.Constants;
import org.eclipse.edt.mof.egl.Element;

public class CommonUtilities {

	public static boolean isRUIHandler(Object obj) {
		if (obj instanceof Element) {
			return ((Element) obj).getAnnotation( Constants.RUI_HANDLER ) != null;
		}
	
		return false;
	}
	
	public static boolean isRUIWidget( Object obj )
	{
		if ( obj instanceof Element )
		{
			return ((Element)obj).getAnnotation( Constants.RUI_WIDGET ) != null;
		}

		return false;
	}
	
	public static String toIncludeDDName( String eglddPath ) {
		return eglddPath.substring( eglddPath.lastIndexOf( "/" ) + 1, eglddPath.lastIndexOf( "." ) ).toLowerCase();
	}
	
	public static void addToDependentList(LinkedHashSet dependentFiles, String part) {
		if(dependentFiles.contains(part)){
			dependentFiles.remove(part);
		}
		dependentFiles.add(part);
	}

}
