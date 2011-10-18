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
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import org.eclipse.edt.eunit.runtime.Status;
import org.eclipse.edt.eunit.runtime.ConstantsLib;
import org.eclipse.edt.eunit.runtime.AssertionFailedException;
import org.eclipse.edt.eunit.runtime.MultiStatus;
import org.eclipse.edt.runtime.java.eglx.lang.EList;
import java.util.List;
import eglx.services.ServiceInvocationException;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import eglx.lang.SysLib;
import org.eclipse.edt.eunit.runtime.LogResult;
import org.eclipse.edt.eunit.runtime.ServiceBindingType;
import eglx.lang.AnyException;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
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
	private int libIndex;
	public ConstantsLib eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib;
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
	public ConstantsLib eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib() {
		if (eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib == null) {
			eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib = (ConstantsLib)org.eclipse.edt.javart.Runtime.getRunUnit().loadLibrary("org.eclipse.edt.eunit.runtime.ConstantsLib");
		}
		return eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib;
	}
	public LogResult eze_Lib_org_eclipse_edt_eunit_runtime_LogResult() {
		if (eze_Lib_org_eclipse_edt_eunit_runtime_LogResult == null) {
			eze_Lib_org_eclipse_edt_eunit_runtime_LogResult = (LogResult)org.eclipse.edt.javart.Runtime.getRunUnit().loadLibrary("org.eclipse.edt.eunit.runtime.LogResult");
		}
		return eze_Lib_org_eclipse_edt_eunit_runtime_LogResult;
	}
	public void nextTest() {
		String testId;
		testId = getTestIdString();
		SysLib.writeStdout((("Running test: ") + testId));
		ms.addStatus(testId);
		boolean eze$Temp1;
		eze$Temp1 = (testIndex < EList.getSize(runTestMtds));
		if (eze$Temp1) {
			testIndex += (int)((short) 1);
			runTestMtds.get(testIndex - 1).invoke();
		}
	}
	private String getTestIdString() {
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
		return testId;
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
		if (EAny.ezeIsa(exp, ServiceInvocationException.class)) {
			ServiceInvocationException sexp;
			sexp = EAny.ezeCast(exp, ServiceInvocationException.class);
			String s1;
			s1 = (("detail1:") + sexp.detail1);
			String s2;
			s2 = (("detail2:") + sexp.detail2);
			String s3;
			s3 = (("detail3:") + sexp.detail3);
			str = ((str) + eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().NEWLINE);
			str = ((((str) + s1)) + eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().NEWLINE);
			str = ((((str) + s2)) + eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().NEWLINE);
			str = ((((str) + s3)) + eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().NEWLINE);
		}
		AnyBoxedObject<String> eze$Temp8;
		eze$Temp8 = EAny.ezeWrap(str);
		eze_Lib_org_eclipse_edt_eunit_runtime_LogResult().error(eze$Temp8);
		str = eze$Temp8.ezeUnbox();
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
	public void caughtAnyException(AnyException exp) {
		String expMsg;
		expMsg = (("uncaught exception for: ") + getTestIdString());
		AnyBoxedObject<String> eze$Temp9;
		eze$Temp9 = EAny.ezeWrap(expMsg);
		eze_Lib_org_eclipse_edt_eunit_runtime_LogResult().error(eze$Temp9);
		expMsg = eze$Temp9.ezeUnbox();
	}
	public String getBindingTypeString(ServiceBindingType bType) {
		if ((bType == ServiceBindingType.DEDICATED)) {
			return "DEDICATED_BINDING";
		}
		else {
			if ((bType == ServiceBindingType.DEVELOP)) {
				return "DEVELOP_BINDING";
			}
			else {
				if ((bType == ServiceBindingType.DEPLOYED)) {
					return "DEPLOYED_BINDING";
				}
				else {
					return "UNKNOWN Binding Type - NOT supported";
				}
			}
		}
	}
}
