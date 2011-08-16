package egl.lang;
import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Program;
public class InvalidPatternException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String pattern;
	public InvalidPatternException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((InvalidPatternException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
		this.pattern = ((InvalidPatternException) source).pattern;
	}
	public InvalidPatternException ezeNewValue(Object... args) {
		return new InvalidPatternException();
	}
	public void ezeSetEmpty() {
		pattern = Constants.EMPTY_STRING;
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
		pattern = Constants.EMPTY_STRING;
	}
	public String getPattern() {
		return (pattern);
	}
	public void setPattern( String ezeValue ) {
		this.pattern = ezeValue;
	}
}
