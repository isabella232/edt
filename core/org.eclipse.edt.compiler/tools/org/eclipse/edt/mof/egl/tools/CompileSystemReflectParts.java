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
package org.eclipse.edt.mof.egl.tools;

import org.eclipse.edt.mof.egl.compiler.EGL2IR;
import org.eclipse.edt.mof.egl.utils.IRUtils;



//must be invoked so that the current directory is the workspace root


public class CompileSystemReflectParts {

	public static void main(String[] args) {
		String rootDir = ".";
		
		String eglPathKey = "-eglPath";
		String eglPathValue = rootDir + "\\org.eclipse.edt.mof.egl\\EGLSource;" + rootDir + "\\org.eclipse.edt.compiler\\SystemPackageSrc;";
		String irDestKey = "-irDestination";
		String irDestValue = rootDir + "\\org.eclipse.edt.compiler\\SystemPackageBin";
		String outFormat = "-xmlOut";
		String vagCompat = "-isVAGCompatible";
		
		String[] dirs = new String[] {
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\lang\\reflect\\refTypes\\*.egl",   //must be in this order because ParameterizableType has problems
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\lang\\reflect\\*.egl",   
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\lang\\reflect\\mof\\*.egl",
				"\\org.eclipse.edt.compiler\\SystemPackageSrc\\egl\\lang\\*.egl",  
		};
		
		for (int i = 0; i < dirs.length; i++) {
			EGL2IR.main(new String[] {
					eglPathKey,
					eglPathValue,
					irDestKey,
					irDestValue,
					outFormat,
					vagCompat,
					rootDir + dirs[i]
					
			});
		}
	}
	
	
}
