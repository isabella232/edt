/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.tools;

import org.eclipse.edt.mof.egl.compiler.EGL2IR;
import org.eclipse.edt.mof.egl.utils.IRUtils;



//must be invoked so that the current directory is the workspace root


public class CompileSystemEglParts {

	public static void main(String[] args) {
		String rootDir = ".";
		
		String eglPathKey = "-eglPath";
		String eglPathValue = rootDir + "\\org.eclipse.edt.mof.egl\\EGLSource;" + rootDir + "\\org.eclipse.edt.compiler\\SystemPackageSrc;";
//		String sysRootKey = "-systemRoot";
//		String sysRootValue = rootDir + "\\org.eclipse.edt.mof.egl\\EGL_MOF";
		String irDestKey = "-irDestination";
		String irDestValue = rootDir + "\\org.eclipse.edt.compiler\\SystemPackageBin";
		String outFormat = "-xmlOut";
		String vagCompat = "-isVAGCompatible";
		
		String[] dirs = new String[] {
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\core\\*.egl",
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\ui\\*.egl",
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\ui\\console\\*.egl",
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\ui\\jasper\\*.egl",
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\ui\\jsf\\*.egl", 
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\ui\\text\\*.egl",  
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\ui\\webTransaction\\*.egl", 
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\java\\*.egl",  
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\idl\\java\\*.egl", 
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\io\\dli\\*.egl",  
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\platform\\*.egl",  
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\platform\\i5os\\*.egl",  
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\io\\file\\*.egl", 
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\io\\mq\\*.egl", 
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\io\\sql\\*.egl",  
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\vg\\*.egl", 
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\report\\birt\\*.egl", 
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\report\\text\\*.egl",  
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\javascript\\*.egl",  
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\ui\\rui\\*.egl",  
		};
		
		for (int i = 0; i < dirs.length; i++) {
			EGL2IR.main(new String[] {
					eglPathKey,
					eglPathValue,
//					sysRootKey,
//					sysRootValue,
					irDestKey,
					irDestValue,
					outFormat,
					vagCompat,
					rootDir + dirs[i]
					
			});
		}
		
	}
	
	
}
