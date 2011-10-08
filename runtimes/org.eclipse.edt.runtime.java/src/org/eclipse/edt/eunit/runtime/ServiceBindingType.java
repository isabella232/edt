package org.eclipse.edt.eunit.runtime;
public enum ServiceBindingType {
	DEDICATED(1),
	DEVELOP(2),
	DEPLOYED(3);
	private final int value;
	private ServiceBindingType(int value) {
		this.value = value;
	}
	private ServiceBindingType() {
		value = -1;
	}
	public int getValue() {
		return value;
	}
}
