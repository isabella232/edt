package org.eclipse.edt.java.jtopen.access;

import com.ibm.as400.access.AS400DataType;

public class AS400Array extends com.ibm.as400.access.AS400Array {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8L;
	
	private int elementCount;
	public AS400Array(AS400DataType type, int size) {
		super(type, size);
		elementCount = size;
	}
	public int getElementCount() {
		return elementCount;
	}
	public void setElementCount(int elementCount) {
		this.elementCount = elementCount;
	}
}
