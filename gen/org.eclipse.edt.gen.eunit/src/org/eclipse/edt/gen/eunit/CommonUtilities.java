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
package org.eclipse.edt.gen.eunit;

import org.eclipse.edt.mof.egl.Part;

public class CommonUtilities {

	public static final String exeTestMethodName = "invokeTheTest";
	public static final String runningTestMethodName = "runningTest";
	public static final String endTestMethodName = "endTest";
	public static final String EUNITGEN_ROOT = "eunitgen";
	public static final String EUNITRUNTIME_PACKAGENAME = "org.eclipse.edt.eunit.runtime";	
	
	public static String getECKGenPackageName(Part part){
		//prepend "eckgen" to the part package name		
		String packageName = part.getCaseSensitivePackageName();
		String eckgenPkgName = prependECKGen(packageName);
		return eckgenPkgName;	
	}

	public static String prependECKGen(String packageName) {
		String eckgenPkgName = CommonUtilities.EUNITGEN_ROOT;
		if(packageName != null && packageName.length()>0){
			eckgenPkgName += ".";
			eckgenPkgName += packageName;
		}
		return eckgenPkgName;
	}

	public static String getECKGenPartFQName(Part part){
		String fqName = part.getFullyQualifiedName();
		return prependECKGen(fqName);
	}


}
