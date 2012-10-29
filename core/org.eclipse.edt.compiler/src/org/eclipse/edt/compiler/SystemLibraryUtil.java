/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

public class SystemLibraryUtil {
	
	public static String getSystemLibraryPath(Class locatorClass, String subDir){

		String name = locatorClass.getCanonicalName();
		name = "/" + name.replace('.', '/') +  ".class";
		URL classUrl = locatorClass.getResource(name);
		
		if (classUrl != null && classUrl.getProtocol().equals( "file" )){
			String initializerPath = classUrl.getFile();
			
			initializerPath = getSystemLibraryPath(initializerPath, name, subDir);
			
			try {
				initializerPath = URLDecoder.decode(initializerPath, System.getProperty("file.encoding"));
			} catch (UnsupportedEncodingException e) {
			}
			
			return initializerPath;
		}else if (classUrl != null && classUrl.getProtocol().equals( "jar" )){
			String initializerPath = classUrl.getFile();
			if (initializerPath.startsWith("file:")){
				initializerPath = initializerPath.substring(5);
			}								
			
			initializerPath = getSystemLibraryPath(initializerPath, name, subDir);
			try {
				initializerPath = URLDecoder.decode(initializerPath, System.getProperty("file.encoding"));
			} catch (UnsupportedEncodingException e) {
			}			
			
			return initializerPath;
		}
		
		return null;
	}
	
	private static String getSystemLibraryPath(String fileURL, String qualClassName, String subDir){
		String initializerPath = fileURL.replace('\\', '/');
		int index = initializerPath.lastIndexOf(qualClassName);
		initializerPath = initializerPath.substring(0, index);
		try {
			initializerPath = new URI(initializerPath).getPath();
		}
		catch ( URISyntaxException e ){
			// Fall back on previous routine.
			initializerPath = initializerPath.replaceAll("%20", " ");
		}
		
		boolean isJar = initializerPath.endsWith(".jar!");
		
		//Strip off last segment (either the jar file name, or the bin directory name)
		index = initializerPath.lastIndexOf("/");
		initializerPath = initializerPath.substring(0, index);
		
		if (isJar) {
			//Strip off the directory containing the jar file
			index = initializerPath.lastIndexOf("/");
			initializerPath = initializerPath.substring(0, index + 1);
			//Add back in the sub directory
			return initializerPath + subDir;
		}
		else {
			return initializerPath + "/" + subDir;
		}
				
	}

}
