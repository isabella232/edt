package org.eclipse.edt.mof.eglx.jtopen;

import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class Utils {

	public static boolean isIBMiConnection(Type type) {
			Part ibmiConnection = (Part)IRUtils.getEGLType("eglx.jtopen.IBMiConnection");
			return type instanceof ExternalType && ((ExternalType)type).isSubtypeOf((ExternalType)ibmiConnection);
	}
}
