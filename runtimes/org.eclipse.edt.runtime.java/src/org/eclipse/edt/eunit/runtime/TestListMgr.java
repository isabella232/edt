package org.eclipse.edt.eunit.runtime;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.eunit.runtime.MultiStatus;
import org.eclipse.edt.runtime.java.eglx.lang.EList;
import java.util.List;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
@javax.xml.bind.annotation.XmlRootElement(name="TestListMgr")
public class TestListMgr extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public MultiStatus ms;
	@javax.xml.bind.annotation.XmlTransient
	public int nextTestIndex;
	@javax.xml.bind.annotation.XmlTransient
	public List<org.eclipse.edt.javart.Delegate> runTestMtds;
	@javax.xml.bind.annotation.XmlTransient
	public List<org.eclipse.edt.javart.Delegate> LibraryStartTests;
	@javax.xml.bind.annotation.XmlTransient
	public int nextLibIndex;
	public TestListMgr() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
		ms = new MultiStatus();
		runTestMtds = EList.ezeNew(org.eclipse.edt.javart.Delegate.class);
		LibraryStartTests = EList.ezeNew(org.eclipse.edt.javart.Delegate.class);
		nextTestIndex = (int)((short) 1);
		nextLibIndex = (int)((short) 1);
	}
	@org.eclipse.edt.javart.json.Json(name="ms", clazz=MultiStatus.class, asOptions={})
	public MultiStatus getMs() {
		return (ms);
	}
	public void setMs( MultiStatus ezeValue ) {
		this.ms = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="nextTestIndex", clazz=EInt.class, asOptions={})
	public int getNextTestIndex() {
		return (nextTestIndex);
	}
	public void setNextTestIndex( int ezeValue ) {
		this.nextTestIndex = ezeValue;
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
	public void nextTest(String testId) {
		ms.addStatus(testId);
		boolean eze$Temp1;
		eze$Temp1 = (nextTestIndex < EList.getSize(runTestMtds));
		if (eze$Temp1) {
			nextTestIndex += (int)((short) 1);
			runTestMtds.get(nextTestIndex - 1).invoke();
		}
	}
	public void nextTestLibrary() {
		boolean eze$Temp3;
		eze$Temp3 = (nextLibIndex < EList.getSize(LibraryStartTests));
		if (eze$Temp3) {
			nextLibIndex += (int)((short) 1);
			LibraryStartTests.get(nextLibIndex - 1).invoke();
		}
	}
}
