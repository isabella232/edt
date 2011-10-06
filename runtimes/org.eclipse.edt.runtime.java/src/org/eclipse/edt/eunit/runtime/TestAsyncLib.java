package org.eclipse.edt.eunit.runtime;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.runtime.java.eglx.lang.EList;
import java.util.List;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
@javax.xml.bind.annotation.XmlRootElement(name="TestAsyncLib")
public class TestAsyncLib extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public List<org.eclipse.edt.javart.Delegate> runTestMtds;
	@javax.xml.bind.annotation.XmlTransient
	public List<org.eclipse.edt.javart.Delegate> LibraryStartTests;
	@javax.xml.bind.annotation.XmlTransient
	public int nextLibIndex;
	public TestAsyncLib() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
		runTestMtds = EList.ezeNew(org.eclipse.edt.javart.Delegate.class);
		LibraryStartTests = EList.ezeNew(org.eclipse.edt.javart.Delegate.class);
		nextLibIndex = (int)((short) 1);
	}
	@org.eclipse.edt.javart.json.Json(name="runTestMtds", clazz=org.eclipse.edt.javart.Delegate.class, asOptions={})
	public List<org.eclipse.edt.javart.Delegate> getRunTestMtds() {
		return (runTestMtds);
	}
	public void setRunTestMtds( List<org.eclipse.edt.javart.Delegate> ezeValue ) {
		this.runTestMtds = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="LibraryStartTests", clazz=org.eclipse.edt.javart.Delegate.class, asOptions={})
	public List<org.eclipse.edt.javart.Delegate> getLibraryStartTests() {
		return (LibraryStartTests);
	}
	public void setLibraryStartTests( List<org.eclipse.edt.javart.Delegate> ezeValue ) {
		this.LibraryStartTests = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="nextLibIndex", clazz=EInt.class, asOptions={})
	public int getNextLibIndex() {
		return (nextLibIndex);
	}
	public void setNextLibIndex( int ezeValue ) {
		this.nextLibIndex = ezeValue;
	}
	public void nextTestLibrary() {
		boolean eze$Temp1;
		eze$Temp1 = (nextLibIndex < EList.getSize(LibraryStartTests));
		if (eze$Temp1) {
			nextLibIndex += (int)((short) 1);
			LibraryStartTests.get(nextLibIndex - 1).invoke();
		}
	}
}
