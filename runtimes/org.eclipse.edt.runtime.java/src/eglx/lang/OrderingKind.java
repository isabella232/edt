package eglx.lang;
public enum OrderingKind {
	byKey(1),
	byInsertion(2),
	none(3);
	private final int value;
	OrderingKind(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
