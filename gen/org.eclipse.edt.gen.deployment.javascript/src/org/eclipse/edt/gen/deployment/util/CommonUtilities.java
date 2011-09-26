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
package org.eclipse.edt.gen.deployment.util;

import java.util.LinkedHashSet;

import org.eclipse.edt.gen.deployment.javascript.Constants;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.InternUtil;

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
	
	public static boolean isRUIPropertiesLibrary( Object obj )
	{
		if ( obj instanceof Element )
		{
			return ((Element)obj).getAnnotation( Constants.RUI_PROPERTIES_LIBRARY ) != null;
		}

		return false;
	}	
	
	public static String getPropertiesFile( Library ruiPropertiesLibrary) 
	{
		// Do not Alias the name of this library for the properties file - it is referenced as a string at runtime
		String result = null;
		Annotation annotation = ruiPropertiesLibrary.getAnnotation(InternUtil.intern("RUIPropertiesLibrary"));
		
		if(annotation != null){
			String value = (String)annotation.getValue(InternUtil.intern("propertiesFile"));
			if(value != null && value.length() > 0){
				result = value;
			}
		}
		
		if(result == null){
			result = ruiPropertiesLibrary.getId();
		}
		return result;
	}
	
	public static boolean isUserClass(Part part) {
		return isUserDefined(part) && (part instanceof EGLClass);
	}
	
	public static boolean isUserPart(Part part){
		return isUserDefined(part) && (part instanceof EGLClass || part instanceof Enumeration);
	}
	
	private static boolean isUserDefined(Part part){
		return !part.getFullyQualifiedName().startsWith("egl") && !part.getFullyQualifiedName().startsWith("eglx");
	}
	
	public static void addToDependentList(LinkedHashSet dependentFiles, String part) {
		if(dependentFiles.contains(part)){
			dependentFiles.remove(part);
		}
		dependentFiles.add(part);
	}

}
