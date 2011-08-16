package egl.lang;
import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Program;
public class DynamicAccessException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String key;
	public DynamicAccessException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((DynamicAccessException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.key = ((DynamicAccessException) source).key;
	}
	public DynamicAccessException ezeNewValue(Object... args) {
		return new DynamicAccessException();
	}
	public void ezeSetEmpty() {
		key = Constants.EMPTY_STRING;
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
		key = Constants.EMPTY_STRING;
	}
	public String getKey() {
		return (key);
	}
	public void setKey( String ezeValue ) {
		this.key = ezeValue;
	}
}
