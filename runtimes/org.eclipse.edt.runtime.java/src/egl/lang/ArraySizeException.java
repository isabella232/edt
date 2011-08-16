package egl.lang;
import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Program;
public class ArraySizeException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public int size;
	public ArraySizeException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((ArraySizeException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.size = ((ArraySizeException) source).size;
	}
	public ArraySizeException ezeNewValue(Object... args) {
		return new ArraySizeException();
	}
	public void ezeSetEmpty() {
		size = 0;
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
		size = 0;
	}
	public int getSize() {
		return (size);
	}
	public void setSize( int ezeValue ) {
		this.size = ezeValue;
	}
}
