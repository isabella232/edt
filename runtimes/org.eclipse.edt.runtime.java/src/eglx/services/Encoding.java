package eglx.services;
public enum Encoding {
	NONE(1),
	JSON(2),
	XML(3),
	_FORM(4),
	USE_CONTENTTYPE(5);
	private final int value;
	Encoding(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
