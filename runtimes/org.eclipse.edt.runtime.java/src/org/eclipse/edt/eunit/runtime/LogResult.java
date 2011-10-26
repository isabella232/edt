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
import org.eclipse.edt.runtime.java.eglx.lang.EBoolean;
import java.lang.Boolean;
import org.eclipse.edt.runtime.java.eglx.lang.EBigint;
import java.lang.Long;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import org.eclipse.edt.eunit.runtime.Status;
import org.eclipse.edt.eunit.runtime.Log;
import org.eclipse.edt.runtime.java.eglx.lang.EDate;
import java.util.Calendar;
import org.eclipse.edt.runtime.java.eglx.lang.EDecimal;
import java.math.BigDecimal;
import eglx.lang.MathLib;
import org.eclipse.edt.runtime.java.eglx.lang.EFloat;
import java.lang.Double;
import org.eclipse.edt.runtime.java.eglx.lang.EList;
import java.util.List;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.runtime.java.eglx.lang.ETimestamp;
import org.eclipse.edt.eunit.runtime.ConstantsLib;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import org.eclipse.edt.eunit.runtime.AssertionFailedException;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="LogResult")
public class LogResult extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	private Log outR;
	@javax.xml.bind.annotation.XmlTransient
	private Status s;
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
	public void passed(AnyBoxedObject<Object> str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SPASSED;
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(((String)str.ezeUnbox())), null)) || ((((String)str.ezeUnbox())).equals("")))) {
			str.ezeCopy("OK");
		}
		s.reason = ((String)str.ezeUnbox());
	}
	public void failed(AnyBoxedObject<Object> str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SFAILED;
		str.ezeCopy((("FAILED - ") + ((String)str.ezeUnbox())));
		s.reason = ((String)str.ezeUnbox());
	}
	public void error(AnyBoxedObject<Object> str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SERROR;
		str.ezeCopy((("ERROR - ") + ((String)str.ezeUnbox())));
		s.reason = ((String)str.ezeUnbox());
	}
	public void skipped(AnyBoxedObject<Object> str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SNOT_RUN;
		str.ezeCopy((("SKIPPED - ") + ((String)str.ezeUnbox())));
		s.reason = ((String)str.ezeUnbox());
	}
	private void assertTrueException(String failedReason, boolean testCondition, boolean throwsFailException) {
		if (testCondition) {
			AnyBoxedObject<Object> eze$Temp3;
			eze$Temp3 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap("OK");
			passed(eze$Temp3);
		}
		else {
			AnyBoxedObject<Object> eze$Temp4;
			eze$Temp4 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(failedReason);
			failed(eze$Temp4);
			failedReason = ((String)eze$Temp4.ezeUnbox());
			if (throwsFailException) {
				AssertionFailedException eze$Temp6 = null;
				{
					AssertionFailedException eze$SettingTarget1;
					eze$SettingTarget1 = new AssertionFailedException();
					eze$SettingTarget1.setMessage(s.reason);
					eze$Temp6 = eze$SettingTarget1;
				}
				throw eze$Temp6;
			}
		}
	}
	public void assertTrue(String failedReason, boolean testCondition) {
		assertTrueException(failedReason, testCondition, true);
	}
	public void assertTrue1(boolean testCondition) {
		assertTrue("", testCondition);
	}
	public void assertBigIntEqual(String message, long expected, long actual) {
		boolean isEqual;
		isEqual = (expected == actual);
		expectAssertTrue(message, EBigint.ezeBox(expected), EBigint.ezeBox(actual), isEqual);
	}
	public void assertBigIntEqual1(long expected, long actual) {
		assertBigIntEqual("", expected, actual);
	}
	public void assertStringEqual(String message, String expected, String actual) {
		boolean isEqual;
		isEqual = ((expected).equals(actual));
		expectAssertTrue(message, EString.ezeBox(expected), EString.ezeBox(actual), isEqual);
	}
	public void assertStringEqual1(String expected, String actual) {
		assertStringEqual("", expected, actual);
	}
	public void assertStringArrayEqual(String message, List<String> expected, List<String> actual) {
		boolean isArrayEqual;
		isArrayEqual = true;
		int expectedSize;
		expectedSize = EList.getSize(expected);
		int actualSize;
		actualSize = EList.getSize(actual);
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
				for (i = (int)((short) 1); (i <= expectedSize); i = (i + (int)((short) 1))) {
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
	public void assertStringArrayEqual1(List<String> expected, List<String> actual) {
		assertStringArrayEqual("", expected, actual);
	}
	public void assertDateEqual(String message, Calendar expected, Calendar actual) {
		boolean isEqual;
		isEqual = (EDate.equals(expected, actual));
		expectAssertTrue(message, EDate.ezeBox(expected), EDate.ezeBox(actual), isEqual);
	}
	public void assertDateEqual1(Calendar expected, Calendar actual) {
		assertDateEqual("", expected, actual);
	}
	public void assertTimestampEqual(String message, Calendar expected, Calendar actual) {
		boolean isEqual;
		isEqual = (ETimestamp.equals(expected, actual));
		expectAssertTrue(message, ETimestamp.ezeBox(expected), ETimestamp.ezeBox(actual), isEqual);
	}
	public void assertTimestampEqual1(Calendar expected, Calendar actual) {
		assertTimestampEqual("", expected, actual);
	}
	public void assertDecimalEqual(String message, BigDecimal expected, BigDecimal actual) {
		boolean isEqual;
		isEqual = (EDecimal.equals(expected, actual));
		expectAssertTrue(message, EDecimal.ezeBox(expected), EDecimal.ezeBox(actual), isEqual);
	}
	public void assertDecimalEqual1(BigDecimal expected, BigDecimal actual) {
		assertDecimalEqual("", expected, actual);
	}
	public void assertFloatEqual(String message, double expected, double actual) {
		boolean isEqual;
		isEqual = isFloatEqual(expected, actual);
		expectAssertTrue(message, EFloat.ezeBox(expected), EFloat.ezeBox(actual), isEqual);
	}
	public void assertFloatEqual1(double expected, double actual) {
		assertFloatEqual("", expected, actual);
	}
	private boolean isFloatEqual(double expected, double actual) {
		double normalExpected = 0.0;
		double normalActual = 0.0;
		double delta = 0.0;
		int mantissaExpected = 0;
		int mantissaActual = 0;
		String signExpected = "";
		String signActual = "";
		double deltaLimit;
		deltaLimit = 1E-14;
		AnyBoxedObject<Object> eze$Temp13 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(0);
		AnyBoxedObject<Object> eze$Temp14 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap("");
		normalExpected = normalFloat(expected, eze$Temp13, eze$Temp14);
		mantissaExpected = ((Integer)eze$Temp13.ezeUnbox());
		signExpected = ((String)eze$Temp14.ezeUnbox());
		AnyBoxedObject<Object> eze$Temp15 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(0);
		AnyBoxedObject<Object> eze$Temp16 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap("");
		normalActual = normalFloat(actual, eze$Temp15, eze$Temp16);
		mantissaActual = ((Integer)eze$Temp15.ezeUnbox());
		signActual = ((String)eze$Temp16.ezeUnbox());
		delta = (normalExpected - normalActual);
		delta = MathLib.abs(delta);
		boolean isEqual;
		isEqual = ((((signExpected).equals(signActual)) && (mantissaExpected == mantissaActual)) && (delta < deltaLimit));
		return isEqual;
	}
	private void expectAssertTrue(String message, eglx.lang.EAny expected, eglx.lang.EAny actual, boolean isEqual) {
		String failedReason;
		failedReason = buildFailedReason(message, expected, actual);
		assertTrue(failedReason, isEqual);
	}
	private String buildFailedReason(String message, eglx.lang.EAny expected, eglx.lang.EAny actual) {
		String failedReason;
		failedReason = expect(expected, actual);
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(EString.ezeBox(message), null)) && (!(message).equals("")))) {
			failedReason = ((((message) + " - ")) + failedReason);
		}
		return failedReason;
	}
	private String expect(eglx.lang.EAny expected, eglx.lang.EAny actual) {
		String standardMsg;
		standardMsg = (((((((((((((((("Failed: ") + EXPECTEDHEADER)) + "'")) + EString.ezeCast(expected))) + "' ")) + ACTUALHEADER)) + "'")) + EString.ezeCast(actual))) + "' ");
		return standardMsg;
	}
	private double normalFloat(double afloat, AnyBoxedObject<Object> mantissa, AnyBoxedObject<Object> sign) {
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
				mantissa.ezeCopy((((Integer)mantissa.ezeUnbox()) - (int)((short) 1)));
			}
			while ((afloat >= (double)((short) 10))) {
				afloat = (EFloat.divide(afloat, (double)((short) 10)));
				mantissa.ezeCopy((((Integer)mantissa.ezeUnbox()) + (int)((short) 1)));
			}
		}
		return afloat;
	}
}
