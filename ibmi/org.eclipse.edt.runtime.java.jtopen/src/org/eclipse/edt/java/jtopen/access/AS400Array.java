package org.eclipse.edt.java.jtopen.access;

import com.ibm.as400.access.AS400DataType;

public class AS400Array extends com.ibm.as400.access.AS400Array {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8L;
	
	private Class<?> eglElementType;
	public AS400Array(AS400DataType type, Class<?> eglElementType, int size) {
		super(type, size);
		this.eglElementType = eglElementType;
	}
	public Class<?> getEGLElementType() {
		return eglElementType;
	}
}
