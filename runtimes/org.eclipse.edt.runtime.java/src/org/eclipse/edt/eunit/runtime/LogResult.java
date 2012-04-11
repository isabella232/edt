/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.eunit.runtime;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EBoolean;
import java.lang.Boolean;
import eglx.lang.MathLib;
import org.eclipse.edt.eunit.runtime.Log;
import org.eclipse.edt.runtime.java.eglx.lang.EDate;
import java.util.Calendar;
import org.eclipse.edt.runtime.java.eglx.lang.EFloat;
import java.lang.Double;
import org.eclipse.edt.runtime.java.eglx.lang.ETimestamp;
import org.eclipse.edt.runtime.java.eglx.lang.EDecimal;
import java.math.BigDecimal;
import org.eclipse.edt.eunit.runtime.AssertionFailedException;
import org.eclipse.edt.runtime.java.eglx.lang.EBigint;
import java.lang.Long;
import org.eclipse.edt.eunit.runtime.Status;
import org.eclipse.edt.eunit.runtime.ConstantsLib;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import java.lang.Object;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.runtime.java.eglx.lang.EList;
import java.util.List;
import org.eclipse.edt.runtime.java.eglx.lang.ESmallfloat;
import java.lang.Float;
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
		Log eze$Temp1 = null;
		return (org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(outR, eze$Temp1));
	}
	public void setOutR(Log ezeValue) {
		outR = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(ezeValue, outR);
	}
	@org.eclipse.edt.javart.json.Json(name="s", clazz=Status.class, asOptions={})
	public Status getS() {
		Status eze$Temp2 = null;
		return (org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(s, eze$Temp2));
	}
	public void setS(Status ezeValue) {
		s = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(ezeValue, s);
	}
	@org.eclipse.edt.javart.json.Json(name="ACTUALHEADER", clazz=EString.class, asOptions={})
	public String getACTUALHEADER() {
		return ACTUALHEADER;
	}
	@org.eclipse.edt.javart.json.Json(name="EXPECTEDHEADER", clazz=EString.class, asOptions={})
	public String getEXPECTEDHEADER() {
		return EXPECTEDHEADER;
	}
	@org.eclipse.edt.javart.json.Json(name="ACTUALSIZEHEADER", clazz=EString.class, asOptions={})
	public String getACTUALSIZEHEADER() {
		return ACTUALSIZEHEADER;
	}
	@org.eclipse.edt.javart.json.Json(name="EXPECTEDSIZEHEADER", clazz=EString.class, asOptions={})
	public String getEXPECTEDSIZEHEADER() {
		return EXPECTEDSIZEHEADER;
	}
	public ConstantsLib eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib() {
		if (eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib == null) {
			eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib = (ConstantsLib)org.eclipse.edt.javart.Runtime.getRunUnit().loadLibrary("org.eclipse.edt.eunit.runtime.ConstantsLib");
		}
		return eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib;
	}
	public void clearResults() {
		outR.msg = "";
		s.code = -1;
		s.reason = "";
	}
	public Status getStatus() {
		Status eze$Temp3 = null;
		return (org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(s, eze$Temp3));
	}
	public Log getLog() {
		Log eze$Temp4 = null;
		return (org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(outR, eze$Temp4));
	}
	public void logStdOut(String logmsg) {
		outR.msg += eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().NEWLINE;
		outR.msg += logmsg;
	}
	public void passed(String str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SPASSED;
		if (((str == null) || ((str).equals("")))) {
			str = "OK";
		}
		s.reason = str;
	}
	public void failed(String str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SFAILED;
		str = (("FAILED - ") + str);
		s.reason = str;
	}
	public void error(String str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SERROR;
		str = (("ERROR - ") + str);
		s.reason = str;
	}
	public void skipped(String str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SNOT_RUN;
		str = (("SKIPPED - ") + str);
		s.reason = str;
	}
	private void assertTrueException(String failedReason, boolean testCondition, boolean throwsFailException) {
		if (testCondition) {
			passed("OK");
		}
		else {
			failed(failedReason);
			if (throwsFailException) {
				AssertionFailedException eze$LNNTemp8 = null;
				{
					AssertionFailedException eze$SettingTarget1;
					eze$SettingTarget1 = new AssertionFailedException();
					eze$SettingTarget1.setMessage(s.reason);
					eze$LNNTemp8 = eze$SettingTarget1;
				}
				throw eze$LNNTemp8;
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
		isEqual = (EBigint.equals(expected, actual));
		expectAssertTrue(message, EAny.asAny(EBigint.ezeBox(expected)), EAny.asAny(EBigint.ezeBox(actual)), isEqual);
	}
	public void assertBigIntEqual1(long expected, long actual) {
		assertBigIntEqual("", expected, actual);
	}
	public void assertStringEqual(String message, String expected, String actual) {
		boolean isEqual;
		isEqual = ((expected).equals(actual));
		expectAssertTrue(message, EAny.asAny(expected), EAny.asAny(actual), isEqual);
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
		if ((EInt.equals(expectedSize, actualSize))) {
			String failedHeader;
			failedHeader = "Array element No.[";
			String expectedValues;
			expectedValues = ((EXPECTEDHEADER) + "[");
			String actualValues;
			actualValues = ((ACTUALHEADER) + "[");
			{
				int i = 0;
				for (i = 1; (i <= expectedSize); i = (i + 1)) {
					if ((!(expected.get(org.eclipse.edt.javart.util.JavartUtil.checkIndex(i - 1, expected))).equals(actual.get(org.eclipse.edt.javart.util.JavartUtil.checkIndex(i - 1, actual))))) {
						if (!(isArrayEqual)) {
							failedHeader += ", ";
						}
						isArrayEqual = false;
						failedHeader += EString.asString(i);
					}
					expectedValues += expected.get(org.eclipse.edt.javart.util.JavartUtil.checkIndex(i - 1, expected));
					actualValues += actual.get(org.eclipse.edt.javart.util.JavartUtil.checkIndex(i - 1, actual));
					if ((EInt.notEquals(i, expectedSize))) {
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
		if (((message != null) && (!(message).equals("")))) {
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
		expectAssertTrue(message, EAny.asAny(EDate.ezeBox(expected)), EAny.asAny(EDate.ezeBox(actual)), isEqual);
	}
	public void assertDateEqual1(Calendar expected, Calendar actual) {
		assertDateEqual("", expected, actual);
	}
	public void assertTimestampEqual(String message, Calendar expected, Calendar actual) {
		boolean isEqual;
		isEqual = (ETimestamp.equals(expected, actual));
		expectAssertTrue(message, EAny.asAny(ETimestamp.ezeBox(expected)), EAny.asAny(ETimestamp.ezeBox(actual)), isEqual);
	}
	public void assertTimestampEqual1(Calendar expected, Calendar actual) {
		assertTimestampEqual("", expected, actual);
	}
	public void assertDecimalEqual(String message, BigDecimal expected, BigDecimal actual) {
		boolean isEqual;
		isEqual = (EDecimal.equals(expected, actual));
		expectAssertTrue(message, EAny.asAny(expected), EAny.asAny(actual), isEqual);
	}
	public void assertDecimalEqual1(BigDecimal expected, BigDecimal actual) {
		assertDecimalEqual("", expected, actual);
	}
	public void assertFloatEqual(String message, double expected, double actual) {
		boolean isEqual;
		isEqual = isFloatEqual(expected, actual);
		expectAssertTrue(message, EAny.asAny(EFloat.ezeBox(expected)), EAny.asAny(EFloat.ezeBox(actual)), isEqual);
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
		int eze$Temp15 = 0;
		AnyBoxedObject<Integer> eze$Temp16;
		eze$Temp16 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(eze$Temp15);
		String eze$Temp17 = "";
		AnyBoxedObject<String> eze$Temp18;
		eze$Temp18 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(eze$Temp17);
		normalExpected = normalFloat(expected, eze$Temp16, eze$Temp18);
		mantissaExpected = org.eclipse.edt.javart.util.JavartUtil.checkNullable(eze$Temp16.ezeUnbox());
		signExpected = org.eclipse.edt.javart.util.JavartUtil.checkNullable(eze$Temp18.ezeUnbox());
		int eze$Temp19 = 0;
		AnyBoxedObject<Integer> eze$Temp20;
		eze$Temp20 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(eze$Temp19);
		String eze$Temp21 = "";
		AnyBoxedObject<String> eze$Temp22;
		eze$Temp22 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(eze$Temp21);
		normalActual = normalFloat(actual, eze$Temp20, eze$Temp22);
		mantissaActual = org.eclipse.edt.javart.util.JavartUtil.checkNullable(eze$Temp20.ezeUnbox());
		signActual = org.eclipse.edt.javart.util.JavartUtil.checkNullable(eze$Temp22.ezeUnbox());
		delta = (normalExpected - normalActual);
		delta = MathLib.abs(delta);
		boolean isEqual;
		isEqual = ((((signExpected).equals(signActual)) && (EInt.equals(mantissaExpected, mantissaActual))) && (delta < deltaLimit));
		return isEqual;
	}
	public void assertSmallFloatEqual(String message, float expected, float actual) {
		boolean isEqual;
		isEqual = isSmallFloatEqual(expected, actual);
		expectAssertTrue(message, EAny.asAny(ESmallfloat.ezeBox(expected)), EAny.asAny(ESmallfloat.ezeBox(actual)), isEqual);
	}
	public void assertSmallFloatEqual1(float expected, float actual) {
		assertSmallFloatEqual("", expected, actual);
	}
	private boolean isSmallFloatEqual(float expected, float actual) {
		float normalExpected = 0.0f;
		float normalActual = 0.0f;
		float delta = 0.0f;
		int mantissaExpected = 0;
		int mantissaActual = 0;
		String signExpected = "";
		String signActual = "";
		double deltaLimit;
		deltaLimit = 1E-4;
		int eze$Temp23 = 0;
		AnyBoxedObject<Integer> eze$Temp24;
		eze$Temp24 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(eze$Temp23);
		String eze$Temp25 = "";
		AnyBoxedObject<String> eze$Temp26;
		eze$Temp26 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(eze$Temp25);
		normalExpected = normalSmallFloat(expected, eze$Temp24, eze$Temp26);
		mantissaExpected = org.eclipse.edt.javart.util.JavartUtil.checkNullable(eze$Temp24.ezeUnbox());
		signExpected = org.eclipse.edt.javart.util.JavartUtil.checkNullable(eze$Temp26.ezeUnbox());
		int eze$Temp27 = 0;
		AnyBoxedObject<Integer> eze$Temp28;
		eze$Temp28 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(eze$Temp27);
		String eze$Temp29 = "";
		AnyBoxedObject<String> eze$Temp30;
		eze$Temp30 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(eze$Temp29);
		normalActual = normalSmallFloat(actual, eze$Temp28, eze$Temp30);
		mantissaActual = org.eclipse.edt.javart.util.JavartUtil.checkNullable(eze$Temp28.ezeUnbox());
		signActual = org.eclipse.edt.javart.util.JavartUtil.checkNullable(eze$Temp30.ezeUnbox());
		delta = (normalExpected - normalActual);
		delta = MathLib.abs(delta);
		boolean isEqual;
		isEqual = ((((signExpected).equals(signActual)) && (EInt.equals(mantissaExpected, mantissaActual))) && ((double)(delta) < deltaLimit));
		return isEqual;
	}
	private void expectAssertTrue(String message, Object expected, Object actual, boolean isEqual) {
		String failedReason;
		failedReason = buildFailedReason(message, expected, actual);
		assertTrue(failedReason, isEqual);
	}
	private String buildFailedReason(String message, Object expected, Object actual) {
		String failedReason;
		failedReason = expect(expected, actual);
		if (((message != null) && (!(message).equals("")))) {
			failedReason = ((((message) + " - ")) + failedReason);
		}
		return failedReason;
	}
	private String expect(Object expected, Object actual) {
		String standardMsg;
		standardMsg = (((((((((((((((("Failed: ") + EXPECTEDHEADER)) + "'")) + EString.ezeCast(expected))) + "' ")) + ACTUALHEADER)) + "'")) + EString.ezeCast(actual))) + "' ");
		return standardMsg;
	}
	private double normalFloat(double afloat, AnyBoxedObject<Integer> mantissa, AnyBoxedObject<String> sign) {
		mantissa.ezeCopy(0);
		if ((afloat >= (double)((short) 0))) {
			sign.ezeCopy("+");
		}
		else {
			sign.ezeCopy("-");
			afloat = (afloat * (double)((short) -1));
		}
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(afloat, null)) && (EFloat.notEquals(afloat, (double)((short) 0))))) {
			while ((afloat < (double)((short) 1))) {
				afloat = (afloat * (double)((short) 10));
				mantissa.ezeCopy((mantissa.ezeUnbox() - 1));
			}
			while ((afloat >= (double)((short) 10))) {
				afloat = (EFloat.divide(afloat, (double)((short) 10)));
				mantissa.ezeCopy((mantissa.ezeUnbox() + 1));
			}
		}
		return afloat;
	}
	private float normalSmallFloat(float afloat, AnyBoxedObject<Integer> mantissa, AnyBoxedObject<String> sign) {
		mantissa.ezeCopy(0);
		if ((afloat >= (float)((short) 0))) {
			sign.ezeCopy("+");
		}
		else {
			sign.ezeCopy("-");
			afloat = (afloat * (float)((short) -1));
		}
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(afloat, null)) && (ESmallfloat.notEquals(afloat, (float)((short) 0))))) {
			while ((afloat < (float)((short) 1))) {
				afloat = (afloat * (float)((short) 10));
				mantissa.ezeCopy((mantissa.ezeUnbox() - 1));
			}
			while ((afloat >= (float)((short) 10))) {
				afloat = (ESmallfloat.divide(afloat, (float)((short) 10)));
				mantissa.ezeCopy((mantissa.ezeUnbox() + 1));
			}
		}
		return afloat;
	}
}
