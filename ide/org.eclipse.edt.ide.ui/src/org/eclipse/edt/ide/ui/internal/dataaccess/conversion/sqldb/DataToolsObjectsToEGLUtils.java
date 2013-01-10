/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb;

import java.io.File;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.eclipse.edt.ide.ui.internal.templates.TemplateEngine;

public class DataToolsObjectsToEGLUtils {

	public static String getReplacedString(String value, Hashtable<String, String> variables) {
		StringBuffer res = new StringBuffer();
		StringTokenizer st = new StringTokenizer(value, "\\$", true);
		Pattern p = Pattern.compile("\\{.*\\}.*", Pattern.DOTALL);
		while (st.hasMoreTokens()) {
			String t = st.nextToken();
			if (t.equals("$")) {
				if (st.hasMoreTokens()) {
					t = st.nextToken();
					if (p.matcher(t).matches()) {
						String varName = t.substring(1, t.indexOf("}"));
						String rep = variables.get(varName);
						res.append(rep);
						res.append(t.substring(t.indexOf("}") + 1));
					}else{
						res.append(t);
					}
				}
			} else {
				res.append(t);
			}
		}
		// System.out.println(res);
		return res.toString();
	}
	
	public static void cleanTableVariable(Hashtable<String, String> variables){
		
		variables.remove(DataToolsSqlTemplateConstants.SEARCH_METHOD_PARAM_DEF);
		variables.remove(DataToolsSqlTemplateConstants.SEARCH_METHOD_PARAM);
		variables.remove(DataToolsSqlTemplateConstants.SEARCH_RECORD_KEY_ASSIGN);
	}
	
	public static String getEGLFilePath(String packageName, String fileName){
		String path = packageName.replace(".", File.separator);
		return path + File.separator + fileName + ".egl";
	}
	

	public static String getPackageName(String fullQualifiedFileName){
		if(fullQualifiedFileName.startsWith(File.separator)){
			fullQualifiedFileName = fullQualifiedFileName.substring(1);
		}
		int lastIndex = fullQualifiedFileName.lastIndexOf(File.separator);
		if(lastIndex < 0){
			return "";
		}else{
			return fullQualifiedFileName.substring(0, lastIndex).replace(File.separator, ".");
		}
	}

	public static String getEGLFileName(String fullQualifiedFileName){
		if(fullQualifiedFileName.startsWith(File.separator)){
			fullQualifiedFileName = fullQualifiedFileName.substring(1);
		}
		int startIndex = fullQualifiedFileName.lastIndexOf(File.separator);
		int lastIndex = fullQualifiedFileName.lastIndexOf(".egl");
		if(startIndex < 0){
			return fullQualifiedFileName.substring(0, lastIndex);
		}else{
			return fullQualifiedFileName.substring(startIndex+1, lastIndex);
		}
	}
	
}
