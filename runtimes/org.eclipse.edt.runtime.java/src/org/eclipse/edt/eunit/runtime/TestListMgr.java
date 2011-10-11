/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.eunit.runtime;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.eunit.runtime.LogResult;
import org.eclipse.edt.eunit.runtime.AssertionFailedException;
import org.eclipse.edt.eunit.runtime.MultiStatus;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import org.eclipse.edt.eunit.runtime.ServiceBindingType;
import org.eclipse.edt.runtime.java.eglx.lang.EList;
import java.util.List;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import org.eclipse.edt.eunit.runtime.Status;
import eglx.lang.AnyException;
@javax.xml.bind.annotation.XmlRootElement(name="TestListMgr")
public class TestListMgr extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public ServiceBindingType bindingType;
	@javax.xml.bind.annotation.XmlTransient
	public MultiStatus ms;
	@javax.xml.bind.annotation.XmlTransient
	public int testIndex;
	@javax.xml.bind.annotation.XmlTransient
	public List<String> testMethodNames;
	@javax.xml.bind.annotation.XmlTransient
	public List<org.eclipse.edt.javart.Delegate> runTestMtds;
	@javax.xml.bind.annotation.XmlTransient
	public String testLibName;
	@javax.xml.bind.annotation.XmlTransient
	public List<org.eclipse.edt.javart.Delegate> LibraryStartTests;
	@javax.xml.bind.annotation.XmlTransient
	public int libIndex;
	public LogResult eze_Lib_org_eclipse_edt_eunit_runtime_LogResult;
	public TestListMgr() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
		ms = new MultiStatus();
		testMethodNames = EList.ezeNew(String.class);
		runTestMtds = EList.ezeNew(org.eclipse.edt.javart.Delegate.class);
		testLibName = "";
		LibraryStartTests = EList.ezeNew(org.eclipse.edt.javart.Delegate.class);
		bindingType = ServiceBindingType.DEDICATED;
		testIndex = (int)((short) 1);
		libIndex = (int)((short) 1);
	}
	@org.eclipse.edt.javart.json.Json(name="bindingType", clazz=ServiceBindingType.class, asOptions={})
	public ServiceBindingType getBindingType() {
		return (bindingType);
	}
	public void setBindingType( ServiceBindingType ezeValue ) {
		this.bindingType = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="ms", clazz=MultiStatus.class, asOptions={})
	public MultiStatus getMs() {
		return (ms);
	}
	public void setMs( MultiStatus ezeValue ) {
		this.ms = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="testIndex", clazz=EInt.class, asOptions={})
	public int getTestIndex() {
		return (testIndex);
	}
	public void setTestIndex( int ezeValue ) {
		this.testIndex = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="testMethodNames", clazz=EString.class, asOptions={})
	public List<String> getTestMethodNames() {
		return (testMethodNames);
	}
	public void setTestMethodNames( List<String> ezeValue ) {
		this.testMethodNames = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="runTestMtds", clazz=org.eclipse.edt.javart.Delegate.class, asOptions={})
	public List<org.eclipse.edt.javart.Delegate> getRunTestMtds() {
		return (runTestMtds);
	}
	public void setRunTestMtds( List<org.eclipse.edt.javart.Delegate> ezeValue ) {
		this.runTestMtds = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="testLibName", clazz=EString.class, asOptions={})
	public String getTestLibName() {
		return (testLibName);
	}
	public void setTestLibName( String ezeValue ) {
		this.testLibName = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="LibraryStartTests", clazz=org.eclipse.edt.javart.Delegate.class, asOptions={})
	public List<org.eclipse.edt.javart.Delegate> getLibraryStartTests() {
		return (LibraryStartTests);
	}
	public void setLibraryStartTests( List<org.eclipse.edt.javart.Delegate> ezeValue ) {
		this.LibraryStartTests = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="libIndex", clazz=EInt.class, asOptions={})
	public int getLibIndex() {
		return (libIndex);
	}
	public void setLibIndex( int ezeValue ) {
		this.libIndex = ezeValue;
	}
	public LogResult eze_Lib_org_eclipse_edt_eunit_runtime_LogResult() {
		if (eze_Lib_org_eclipse_edt_eunit_runtime_LogResult == null) {
			eze_Lib_org_eclipse_edt_eunit_runtime_LogResult = (LogResult)org.eclipse.edt.javart.Runtime.getRunUnit().loadLibrary("org.eclipse.edt.eunit.runtime.LogResult");
		}
		return eze_Lib_org_eclipse_edt_eunit_runtime_LogResult;
	}
	public void nextTest() {
		int testMethodNamesSize;
		testMethodNamesSize = EList.getSize(testMethodNames);
		String testId;
		testId = ((testLibName) + "::");
		if ((testIndex <= testMethodNamesSize)) {
			testId += testMethodNames.get(testIndex - 1);
		}
		else {
			if ((testIndex == (testMethodNamesSize + (int)((short) 1)))) {
				testId += "endTest";
			}
			else {
				testId += "INVALIDINDEXFOUND!!!";
			}
		}
		ms.addStatus(testId);
		boolean eze$Temp3;
		eze$Temp3 = (testIndex < EList.getSize(runTestMtds));
		if (eze$Temp3) {
			testIndex += (int)((short) 1);
			runTestMtds.get(testIndex - 1).invoke();
		}
	}
	public void nextTestLibrary() {
		boolean eze$Temp5;
		eze$Temp5 = (libIndex < EList.getSize(LibraryStartTests));
		if (eze$Temp5) {
			libIndex += (int)((short) 1);
			LibraryStartTests.get(libIndex - 1).invoke();
		}
	}
	public void handleCallBackException(AnyException exp) {
		String str;
		str = (((((("Caught service exception: ") + exp.getMessageID())) + ": ")) + exp.getMessage());
		AnyBoxedObject<String> eze$Temp7;
		eze$Temp7 = EAny.ezeWrap(str);
		eze_Lib_org_eclipse_edt_eunit_runtime_LogResult().error(eze$Temp7);
		str = eze$Temp7.ezeUnbox();
		String testId;
		testId = this.testMethodNames.get(this.testIndex - 1);
		nextTest();
	}
	public void caughtFailedAssertion(AssertionFailedException exp) {
		String assertMsg;
		assertMsg = "Assertion failed for: ";
		Status s = null;
		s = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(eze_Lib_org_eclipse_edt_eunit_runtime_LogResult().getStatus(), s);
		s.reason = ((assertMsg) + s.reason);
	}
	public String getBindingTypeString(ServiceBindingType bType) {
		if ((bType == ServiceBindingType.DEDICATED)) {
			return "DEDICATED_Binding";
		}
		else {
			if ((bType == ServiceBindingType.DEVELOP)) {
				return "DEVELOP_Binding";
			}
			else {
				if ((bType == ServiceBindingType.DEPLOYED)) {
					return "DEPLOYED_Binding";
				}
				else {
					return "UNKNOWN Binding Type - NOT supported";
				}
			}
		}
	}
}
