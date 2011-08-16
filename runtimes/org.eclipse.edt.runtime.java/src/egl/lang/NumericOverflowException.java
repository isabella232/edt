package egl.lang;
import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Program;
public class NumericOverflowException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	public NumericOverflowException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((NumericOverflowException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
	}
	public NumericOverflowException ezeNewValue(Object... args) {
		return new NumericOverflowException();
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
