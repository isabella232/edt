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
