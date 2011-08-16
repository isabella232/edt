package eglx.services;
import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Program;
public class ServiceBindingException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	public ServiceBindingException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((ServiceBindingException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
	}
	public ServiceBindingException ezeNewValue(Object... args) {
		return new ServiceBindingException();
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
