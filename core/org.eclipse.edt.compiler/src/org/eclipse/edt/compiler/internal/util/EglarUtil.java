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
package org.eclipse.edt.compiler.internal.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.ICompiler;

public class EglarUtil {

	public static final String EDT_JAR_EXTENSION = ".eglar";
	public static final String EDT_MOF_EXTENSION = ".mofar";

	public static List<File> getAllSystemEglars(ICompiler compiler) {
		List<File> list = new ArrayList<File>();
		if (compiler == null || compiler.getSystemEnvironmentPath() == null) {
			return list;
		}
		String pathEntries = compiler.getSystemEnvironmentPath();
		String[] paths = NameUtil.toStringArray(pathEntries, File.pathSeparator);
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			list.addAll(getAllEglarsInPath(path));
		}
		return list;
	}
	
	public static List<File> getAllEglarsInPath(String path) {
		
		File libfolder = new File(path);
		List<File> list = new ArrayList<File>();
		if (libfolder.exists() && libfolder.isDirectory()){
			File[] files = libfolder.listFiles();

		  	for (int i = 0; i < files.length; i++){
		  		File file = files[i];
		  		if (file.isFile()) {
			  		if (file.getName().endsWith(EDT_JAR_EXTENSION) || file.getName().endsWith(EDT_MOF_EXTENSION)){
			  			list.add(file);
		  			}
		  		}		  		
			 }	
	
		}
		return list;
	}
}
