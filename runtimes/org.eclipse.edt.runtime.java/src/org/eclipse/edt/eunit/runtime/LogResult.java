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
import java.math.BigDecimal;
import org.eclipse.edt.eunit.runtime.AssertionFailedException;
import java.lang.Boolean;
import java.lang.Double;
import java.lang.String;
import eglx.lang.MathLib;
import java.util.Calendar;
import org.eclipse.edt.eunit.runtime.ConstantsLib;
import org.eclipse.edt.eunit.runtime.Log;
import java.lang.Long;
import org.eclipse.edt.eunit.runtime.Status;
import org.eclipse.edt.runtime.java.eglx.lang.*;

import java.lang.Integer;
@javax.xml.bind.annotation.XmlRootElement(name="LogResult")
public class LogResult extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public Log outR;
	@javax.xml.bind.annotation.XmlTransient
	public Status s;
	private static final String ezeConst_ACTUALHEADER = "Actual value = ";
	public final String ACTUALHEADER = ezeConst_ACTUALHEADER;
	private static final String ezeConst_EXPECTEDHEADER = "Expected value = ";
	public final String EXPECTEDHEADER = ezeConst_EXPECTEDHEADER;
	private static final String ezeConst_ACTUALSIZEHEADER = "Actual array size = ";
	public final String ACTUALSIZEHEADER = ezeConst_ACTUALSIZEHEADER;
	private static final String ezeConst_EXPECTEDSIZEHEADER = "Exepcted array size = ";
	public final String EXPECTEDSIZEHEADER = ezeConst_EXPECTEDSIZEHEADER;
	public ConstantsLib eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib;
	public LogResult() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
		outR = new Log();
		s = new Status();
	}
	@org.eclipse.edt.javart.json.Json(name="outR", clazz=Log.class, asOptions={})
	public Log getOutR() {
		return (outR);
	}
	public void setOutR( Log ezeValue ) {
		this.outR = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="s", clazz=Status.class, asOptions={})
	public Status getS() {
		return (s);
	}
	public void setS( Status ezeValue ) {
		this.s = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="ACTUALHEADER", clazz=EString.class, asOptions={})
	public String getACTUALHEADER() {
		return (ACTUALHEADER);
	}
	@org.eclipse.edt.javart.json.Json(name="EXPECTEDHEADER", clazz=EString.class, asOptions={})
	public String getEXPECTEDHEADER() {
		return (EXPECTEDHEADER);
	}
	@org.eclipse.edt.javart.json.Json(name="ACTUALSIZEHEADER", clazz=EString.class, asOptions={})
	public String getACTUALSIZEHEADER() {
		return (ACTUALSIZEHEADER);
	}
	@org.eclipse.edt.javart.json.Json(name="EXPECTEDSIZEHEADER", clazz=EString.class, asOptions={})
	public String getEXPECTEDSIZEHEADER() {
		return (EXPECTEDSIZEHEADER);
	}
	public ConstantsLib eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib() {
		if (eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib == null) {
			eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib = (ConstantsLib)org.eclipse.edt.javart.Runtime.getRunUnit().loadLibrary("org.eclipse.edt.eunit.runtime.ConstantsLib");
		}
		return eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib;
	}
	public void clearResults() {
		outR.msg = "";
		s.code = (int)((short) -1);
		s.reason = "";
	}
	public Status getStatus() {
		return s;
	}
	public Log getLog() {
		return outR;
	}
	public void logStdOut(String logmsg) {
		outR.msg += eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().NEWLINE;
		outR.msg += logmsg;
	}
	public void passed(AnyBoxedObject<String> str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SPASSED;
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(str.ezeUnbox()), null)) || ((str.ezeUnbox()).equals("")))) {
			str.ezeCopy("OK");
		}
		s.reason = str.ezeUnbox();
	}
	public void failed(AnyBoxedObject<String> str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SFAILED;
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(str.ezeUnbox()), null)) || ((str.ezeUnbox()).equals("")))) {
			str.ezeCopy("FAILED");
		}
		s.reason = str.ezeUnbox();
	}
	public void error(AnyBoxedObject<String> str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SERROR;
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(str.ezeUnbox()), null)) || ((str.ezeUnbox()).equals("")))) {
			str.ezeCopy("ERROR");
		}
		s.reason = str.ezeUnbox();
	}
	public void skipped(AnyBoxedObject<String> str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SNOT_RUN;
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(str.ezeUnbox()), null)) || ((str.ezeUnbox()).equals("")))) {
			str.ezeCopy("SKIPPED");
		}
		s.reason = str.ezeUnbox();
	}
	public void assertTrue(String failedReason, boolean testCondition) {
		if (testCondition) {
			AnyBoxedObject<String> eze$Temp6;
			eze$Temp6 = EAny.ezeWrap("OK");
			passed(eze$Temp6);
		}
		else {
			AnyBoxedObject<String> eze$Temp7;
			eze$Temp7 = EAny.ezeWrap(failedReason);
			failed(eze$Temp7);
			failedReason = eze$Temp7.ezeUnbox();
			AssertionFailedException eze$Temp8 = new AssertionFailedException();
			{
				AssertionFailedException eze$SettingTarget1;
				eze$SettingTarget1 = new AssertionFailedException();
				eze$SettingTarget1.setMessage(s.reason);
				eze$Temp8 = eze$SettingTarget1;
			}
			throw eze$Temp8;
		}
	}
	public void assertTrue1(boolean testCondition) {
		String eze$Temp9;
		eze$Temp9 = "";
		assertTrue(eze$Temp9, testCondition);
	}
	public void assertBigIntEqual(String message, long expected, long actual) {
		boolean isEqual;
		isEqual = (expected == actual);
		expectAssertTrue(message, EBigint.ezeBox(expected), EBigint.ezeBox(actual), isEqual);
	}
	public void assertBigIntEqual1(long expected, long actual) {
		String eze$Temp10;
		eze$Temp10 = "";
		assertBigIntEqual(eze$Temp10, expected, actual);
	}
	public void assertStringEqual(String message, String expected, String actual) {
		boolean isEqual;
		isEqual = ((expected).equals(actual));
		expectAssertTrue(message, EString.ezeBox(expected), EString.ezeBox(actual), isEqual);
	}
	public void assertStringEqual1(String expected, String actual) {
		String eze$Temp11;
		eze$Temp11 = "";
		assertStringEqual(eze$Temp11, expected, actual);
	}
	public void assertStringArrayEqual(String message, eglx.lang.EList<String> expected, eglx.lang.EList<String> actual) {
		boolean isArrayEqual;
		isArrayEqual = true;
		int expectedSize;
		expectedSize = expected.getSize();
		int actualSize;
		actualSize = actual.getSize();
		String failedReason = "";
		if ((expectedSize == actualSize)) {
			String failedHeader;
			failedHeader = "Array element No.[";
			String expectedValues;
			expectedValues = ((EXPECTEDHEADER) + "[");
			String actualValues;
			actualValues = ((ACTUALHEADER) + "[");
			{
				int i = 0;
				for (i = (short) 1; i <= expectedSize; i += 1) {
					if ((!(expected.get(i - 1)).equals(actual.get(i - 1)))) {
						if (!(isArrayEqual)) {
							failedHeader += ", ";
						}
						isArrayEqual = false;
						failedHeader += EString.asString(i);
					}
					expectedValues += expected.get(i - 1);
					actualValues += actual.get(i - 1);
					if ((i != expectedSize)) {
						expectedValues += ", ";
						actualValues += ", ";
					}
				}
			}
			failedHeader += "] differs; ";
			expectedValues += "]; ";
			actualValues += "] ";
			failedReason = ((((failedHeader) + expectedValues)) + actualValues);
		}
		else {
			isArrayEqual = false;
			failedReason = (((((((((((((((("Failed: ") + EXPECTEDSIZEHEADER)) + "'")) + EString.asString(expectedSize))) + "' ")) + ACTUALSIZEHEADER)) + "'")) + EString.asString(actualSize))) + "' ");
		}
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(EString.ezeBox(message), null)) && (!(message).equals("")))) {
			;
			failedReason = ((((message) + " - ")) + failedReason);
		}
		assertTrue(failedReason, isArrayEqual);
	}
	public void assertStringArrayEqual1(eglx.lang.EList<String> expected, eglx.lang.EList<String> actual) {
		String eze$Temp18;
		eze$Temp18 = "";
		assertStringArrayEqual(eze$Temp18, expected, actual);
	}
	public void assertDateEqual(String message, Calendar expected, Calendar actual) {
		boolean isEqual;
		isEqual = (EDate.equals(expected, actual));
		expectAssertTrue(message, EDate.ezeBox(expected), EDate.ezeBox(actual), isEqual);
	}
	public void assertDateEqual1(Calendar expected, Calendar actual) {
		String eze$Temp19;
		eze$Temp19 = "";
		assertDateEqual(eze$Temp19, expected, actual);
	}
	public void assertTimestampEqual(String message, Calendar expected, Calendar actual) {
		boolean isEqual;
		isEqual = (ETimestamp.equals(expected, actual));
		expectAssertTrue(message, ETimestamp.ezeBox(expected, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE), ETimestamp.ezeBox(actual, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE), isEqual);
	}
	public void assertTimestampEqual1(Calendar expected, Calendar actual) {
		String eze$Temp20;
		eze$Temp20 = "";
		assertTimestampEqual(eze$Temp20, expected, actual);
	}
	public void assertDecimalEqual(String message, BigDecimal expected, BigDecimal actual) {
		boolean isEqual;
		isEqual = (EDecimal.equals(expected, actual));
		expectAssertTrue(message, EDecimal.ezeBox(expected), EDecimal.ezeBox(actual), isEqual);
	}
	public void assertDecimalEqual1(BigDecimal expected, BigDecimal actual) {
		String eze$Temp21;
		eze$Temp21 = "";
		assertDecimalEqual(eze$Temp21, expected, actual);
	}
	public void assertFloatEqual(String message, double expected, double actual) {
		double normalExpected = 0.0;
		double normalActual = 0.0;
		double delta = 0.0;
		int mantissaExpected = 0;
		int mantissaActual = 0;
		String signExpected = "";
		String signActual = "";
		double deltaLimit;
		deltaLimit = 1E-14;
		AnyBoxedObject<Integer> eze$Temp22 = EAny.ezeWrap(0);
		AnyBoxedObject<String> eze$Temp23 = EAny.ezeWrap("");
		normalExpected = normalFloat(expected, eze$Temp22, eze$Temp23);
		mantissaExpected = eze$Temp22.ezeUnbox();
		signExpected = eze$Temp23.ezeUnbox();
		AnyBoxedObject<Integer> eze$Temp24 = EAny.ezeWrap(0);
		AnyBoxedObject<String> eze$Temp25 = EAny.ezeWrap("");
		normalActual = normalFloat(actual, eze$Temp24, eze$Temp25);
		mantissaActual = eze$Temp24.ezeUnbox();
		signActual = eze$Temp25.ezeUnbox();
		delta = (normalExpected - normalActual);
		delta = MathLib.abs(delta);
		boolean isEqual;
		isEqual = ((((signExpected).equals(signActual)) && (mantissaExpected == mantissaActual)) && (delta < deltaLimit));
		expectAssertTrue(message, EFloat.ezeBox(expected), EFloat.ezeBox(actual), isEqual);
	}
	public void assertFloatEqual1(double expected, double actual) {
		String eze$Temp26;
		eze$Temp26 = "";
		assertFloatEqual(eze$Temp26, expected, actual);
	}
	public void expectAssertTrue(String message, eglx.lang.EAny expected, eglx.lang.EAny actual, boolean isEqual) {
		String failedReason;
		failedReason = expect(expected, actual);
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(EString.ezeBox(message), null)) && (!(message).equals("")))) {
			failedReason = ((((message) + " - ")) + failedReason);
		}
		assertTrue(failedReason, isEqual);
	}
	public String expect(eglx.lang.EAny expected, eglx.lang.EAny actual) {
		String standardMsg;
		standardMsg = (((((((((((((((("Failed: ") + EXPECTEDHEADER)) + "'")) + EString.ezeCast(expected))) + "' ")) + ACTUALHEADER)) + "'")) + EString.ezeCast(actual))) + "' ");
		return standardMsg;
	}
	public double normalFloat(double afloat, AnyBoxedObject<Integer> mantissa, AnyBoxedObject<String> sign) {
		mantissa.ezeCopy((int)((short) 0));
		if ((afloat >= (double)((short) 0))) {
			sign.ezeCopy("+");
		}
		else {
			sign.ezeCopy("-");
			afloat = (afloat * (double)((short) -1));
		}
		if ((afloat != (double)((short) 0))) {
			while ((afloat < (double)((short) 1))) {
				afloat = (afloat * (double)((short) 10));
				mantissa.ezeCopy((mantissa.ezeUnbox() - (int)((short) 1)));
			}
			while ((afloat >= (double)((short) 10))) {
				afloat = (EFloat.divide(afloat, (double)((short) 10)));
				mantissa.ezeCopy((mantissa.ezeUnbox() + (int)((short) 1)));
			}
		}
		return afloat;
	}
}
