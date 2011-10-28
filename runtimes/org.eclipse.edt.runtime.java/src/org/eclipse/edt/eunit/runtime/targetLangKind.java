package org.eclipse.edt.eunit.runtime;
public enum targetLangKind {
	JAVA(1),
	JAVASCRIPT(2);
	private final int value;
	private targetLangKind(int value) {
		this.value = value;
	}
	private targetLangKind() {
		value = -1;
	}
	public int getValue() {
		return value;
	}
}
