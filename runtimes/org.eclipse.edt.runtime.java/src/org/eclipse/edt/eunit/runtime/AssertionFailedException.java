package org.eclipse.edt.eunit.runtime;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import egl.lang.AnyException;
@javax.xml.bind.annotation.XmlRootElement(name="AssertionFailedException")
public class AssertionFailedException extends egl.lang.AnyException {
	private static final long serialVersionUID = 10L;
	public AssertionFailedException() {
		super();
		ezeInitialize();
	}
	public void ezeCopy(Object source) {
		ezeCopy((AssertionFailedException) source);
	}
	public void ezeCopy(egl.lang.AnyValue source) {
	}
	public AssertionFailedException ezeNewValue(Object... args) {
		return new AssertionFailedException();
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
