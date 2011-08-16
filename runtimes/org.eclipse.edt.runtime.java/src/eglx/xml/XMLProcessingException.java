package eglx.xml;
import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Program;
public class XMLProcessingException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String detail;
	public XMLProcessingException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((XMLProcessingException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.detail = ((XMLProcessingException) source).detail;
	}
	public XMLProcessingException ezeNewValue(Object... args) {
		return new XMLProcessingException();
	}
	public void ezeSetEmpty() {
		detail = Constants.EMPTY_STRING;
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
		detail = Constants.EMPTY_STRING;
	}
	public String getDetail() {
		return (detail);
	}
	public void setDetail( String ezeValue ) {
		this.detail = ezeValue;
	}
}
