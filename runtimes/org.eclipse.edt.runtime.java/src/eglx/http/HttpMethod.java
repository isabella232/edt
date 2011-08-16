package eglx.http;
public enum HttpMethod {
	_GET(1),
	POST(2),
	_DELETE(3),
	PUT(4);
	private final int value;
	HttpMethod(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
