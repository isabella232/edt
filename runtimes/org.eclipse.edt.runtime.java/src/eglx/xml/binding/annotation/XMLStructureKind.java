package eglx.xml.binding.annotation;
public enum XMLStructureKind {
	choice(1),
	sequence(2),
	simpleContent(3),
	unordered(4);
	private final int value;
	XMLStructureKind(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
