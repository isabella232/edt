package org.eclipse.edt.mof.eglx.services;

import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class Utils {
	public static boolean isIHTTP(Type type) {
		ExternalType http = (ExternalType)TypeUtils.getEGLType("eglx.http.IHttp");
		return type instanceof ExternalType && 
				type.equals(http) || ((ExternalType)type).isSubtypeOf(http);
	}
}
