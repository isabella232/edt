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
package org.eclipse.edt.gen.eunit;

import org.eclipse.edt.mof.egl.EnumerationEntry;

public enum TestDriverTargetLanguageKind {
	JAVA,
	JAVASCRIPT;

//The following are the EGL enumeration definition
//	
//Enumeration targetLangKind
//	JAVA = 1,
//	JAVASCRIPT = 2
//end		
	private final static String EGL_targetLangKind_JAVA = "JAVA";
	private final static String EGL_targetLangKind_JAVASCRIPT = "JAVASCRIPT";

	
	public boolean doesTargetLangTypeMatch(EnumerationEntry eglTestTargetLangEnum){		
		String eglTestTargetLang = eglTestTargetLangEnum.getName();
		switch(this){
		case JAVA:
			if(eglTestTargetLang.equalsIgnoreCase(EGL_targetLangKind_JAVA))
				return true;
			break;
		case JAVASCRIPT:
			if(eglTestTargetLang.equalsIgnoreCase(EGL_targetLangKind_JAVASCRIPT))
				return true;
			break;
		}
		
		return false;
	}	
}
