package eglx.ui.rui;
public enum PurposeKind {
	FOR_DISPLAY(1),
	FOR_CREATE(2),
	FOR_UPDATE(3);
	private final int value;
	PurposeKind(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
