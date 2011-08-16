package eglx.services;
public enum ServiceKind {
	EGL(1),
	WEB(2),
	NATIVE(3),
	REST(4);
	private final int value;
	ServiceKind(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
