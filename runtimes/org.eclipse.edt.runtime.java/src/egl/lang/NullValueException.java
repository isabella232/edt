package egl.lang;
import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Program;
public class NullValueException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	public NullValueException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((NullValueException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
	}
	public NullValueException ezeNewValue(Object... args) {
		return new NullValueException();
	}
	public void ezeSetEmpty() {
	}
	public boolean isVariableDataLength() {
		return false;
	}
	public void loadFromBuffer(ByteStorage buffer, Program program) {
	}
	public int sizeInBytes() {
		return 0;
	}
	public void storeInBuffer(ByteStorage buffer) {
	}
	public void ezeInitialize() {
	}
}
