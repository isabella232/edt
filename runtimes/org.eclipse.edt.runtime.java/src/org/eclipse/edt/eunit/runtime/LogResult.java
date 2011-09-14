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
import org.eclipse.edt.runtime.java.egl.lang.EglAny;
import org.eclipse.edt.runtime.java.egl.lang.EDate;
import java.util.Calendar;
import org.eclipse.edt.runtime.java.egl.lang.EString;
import java.lang.String;
import org.eclipse.edt.eunit.runtime.ConstantsLib;
import org.eclipse.edt.runtime.java.egl.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.eunit.runtime.Status;
import eglx.lang.MathLib;
import org.eclipse.edt.eunit.runtime.Log;
import org.eclipse.edt.runtime.java.egl.lang.EDecimal;
import java.math.BigDecimal;
import org.eclipse.edt.eunit.runtime.AssertionFailedException;
import org.eclipse.edt.runtime.java.egl.lang.EglList;
import org.eclipse.edt.runtime.java.egl.lang.EFloat;
import java.lang.Double;
import org.eclipse.edt.runtime.java.egl.lang.ETimestamp;
import org.eclipse.edt.runtime.java.egl.lang.EBigint;
import java.lang.Long;
import org.eclipse.edt.runtime.java.egl.lang.EBoolean;
import java.lang.Boolean;
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
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(str.ezeUnbox()), null)) || ((str.ezeUnbox()).equals("")))) {
			str.ezeCopy("OK");
		}
		s.reason = str.ezeUnbox();
	}
	public void failed(AnyBoxedObject<String> str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SFAILED;
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(str.ezeUnbox()), null)) || ((str.ezeUnbox()).equals("")))) {
			str.ezeCopy("FAILED");
		}
		s.reason = str.ezeUnbox();
	}
	public void error(AnyBoxedObject<String> str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SERROR;
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(str.ezeUnbox()), null)) || ((str.ezeUnbox()).equals("")))) {
			str.ezeCopy("ERROR");
		}
		s.reason = str.ezeUnbox();
	}
	public void skipped(AnyBoxedObject<String> str) {
		s.code = eze_Lib_org_eclipse_edt_eunit_runtime_ConstantsLib().SNOT_RUN;
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(str.ezeUnbox()), null)) || ((str.ezeUnbox()).equals("")))) {
			str.ezeCopy("SKIPPED");
		}
		s.reason = str.ezeUnbox();
	}
	public void assertTrue(String failedReason, boolean testCondition) {
		if (testCondition) {
			AnyBoxedObject<String> eze$Temp6 = null;
			eze$Temp6 = EglAny.ezeWrap("OK");
			passed(eze$Temp6);
		}
		else {
			AnyBoxedObject<String> eze$Temp7 = null;
			eze$Temp7 = EglAny.ezeWrap(failedReason);
			failed(eze$Temp7);
			failedReason = eze$Temp7.ezeUnbox();
			AssertionFailedException eze$Temp8 = (AssertionFailedException) null;
			{
				AssertionFailedException eze$SettingTarget1 = (AssertionFailedException) null;
				eze$SettingTarget1 = new AssertionFailedException();
				eze$SettingTarget1.setMessage(s.reason);
				eze$Temp8 = eze$SettingTarget1;
			}
			throw eze$Temp8;
		}
	}
	public void assertTrue1(boolean testCondition) {
		String eze$Temp9 = Constants.EMPTY_STRING;
		eze$Temp9 = "";
		assertTrue(eze$Temp9, testCondition);
	}
	public void assertBigIntEqual(String message, long expected, long actual) {
		boolean isEqual = false;
		isEqual = (expected == actual);
		expectAssertTrue(message, EBigint.ezeBox(expected), EBigint.ezeBox(actual), isEqual);
	}
	public void assertBigIntEqual1(long expected, long actual) {
		String eze$Temp10 = Constants.EMPTY_STRING;
		eze$Temp10 = "";
		assertBigIntEqual(eze$Temp10, expected, actual);
	}
	public void assertStringEqual(String message, String expected, String actual) {
		boolean isEqual = false;
		isEqual = ((expected).equals(actual));
		expectAssertTrue(message, EString.ezeBox(expected), EString.ezeBox(actual), isEqual);
	}
	public void assertStringEqual1(String expected, String actual) {
		String eze$Temp11 = Constants.EMPTY_STRING;
		eze$Temp11 = "";
		assertStringEqual(eze$Temp11, expected, actual);
	}
	public void assertStringArrayEqual(String message, egl.lang.EglList<String> expected, egl.lang.EglList<String> actual) {
		boolean isArrayEqual = false;
		isArrayEqual = true;
		int expectedSize = 0;
		expectedSize = expected.getSize();
		int actualSize = 0;
		actualSize = actual.getSize();
		String failedReason = Constants.EMPTY_STRING;
		if ((expectedSize == actualSize)) {
			String failedHeader = Constants.EMPTY_STRING;
			failedHeader = "Array element No.[";
			String expectedValues = Constants.EMPTY_STRING;
			expectedValues = ((EXPECTEDHEADER) + "[");
			String actualValues = Constants.EMPTY_STRING;
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
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.notEquals(EString.ezeBox(message), null)) && (!(message).equals("")))) {
			;
			failedReason = ((((message) + " - ")) + failedReason);
		}
		assertTrue(failedReason, isArrayEqual);
	}
	public void assertStringArrayEqual1(egl.lang.EglList<String> expected, egl.lang.EglList<String> actual) {
		String eze$Temp18 = Constants.EMPTY_STRING;
		eze$Temp18 = "";
		assertStringArrayEqual(eze$Temp18, expected, actual);
	}
	public void assertDateEqual(String message, Calendar expected, Calendar actual) {
		boolean isEqual = false;
		isEqual = (EDate.equals(expected, actual));
		expectAssertTrue(message, EDate.ezeBox(expected), EDate.ezeBox(actual), isEqual);
	}
	public void assertDateEqual1(Calendar expected, Calendar actual) {
		String eze$Temp19 = Constants.EMPTY_STRING;
		eze$Temp19 = "";
		assertDateEqual(eze$Temp19, expected, actual);
	}
	public void assertTimestampEqual(String message, Calendar expected, Calendar actual) {
		boolean isEqual = false;
		isEqual = (ETimestamp.equals(expected, actual));
		expectAssertTrue(message, ETimestamp.ezeBox(expected, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE), ETimestamp.ezeBox(actual, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE), isEqual);
	}
	public void assertTimestampEqual1(Calendar expected, Calendar actual) {
		String eze$Temp20 = Constants.EMPTY_STRING;
		eze$Temp20 = "";
		assertTimestampEqual(eze$Temp20, expected, actual);
	}
	public void assertDecimalEqual(String message, BigDecimal expected, BigDecimal actual) {
		boolean isEqual = false;
		isEqual = (EDecimal.equals(expected, actual));
		expectAssertTrue(message, EDecimal.ezeBox(expected), EDecimal.ezeBox(actual), isEqual);
	}
	public void assertDecimalEqual1(BigDecimal expected, BigDecimal actual) {
		String eze$Temp21 = Constants.EMPTY_STRING;
		eze$Temp21 = "";
		assertDecimalEqual(eze$Temp21, expected, actual);
	}
	public void assertFloatEqual(String message, double expected, double actual) {
		double normalExpected = 0.0;
		double normalActual = 0.0;
		double delta = 0.0;
		int mantissaExpected = 0;
		int mantissaActual = 0;
		String signExpected = Constants.EMPTY_STRING;
		String signActual = Constants.EMPTY_STRING;
		double deltaLimit = 0.0;
		deltaLimit = 1E-14;
		AnyBoxedObject<Integer> eze$Temp22 = EglAny.ezeWrap(0);
		AnyBoxedObject<String> eze$Temp23 = EglAny.ezeWrap(Constants.EMPTY_STRING);
		normalExpected = normalFloat(expected, eze$Temp22, eze$Temp23);
		mantissaExpected = eze$Temp22.ezeUnbox();
		signExpected = eze$Temp23.ezeUnbox();
		AnyBoxedObject<Integer> eze$Temp24 = EglAny.ezeWrap(0);
		AnyBoxedObject<String> eze$Temp25 = EglAny.ezeWrap(Constants.EMPTY_STRING);
		normalActual = normalFloat(actual, eze$Temp24, eze$Temp25);
		mantissaActual = eze$Temp24.ezeUnbox();
		signActual = eze$Temp25.ezeUnbox();
		delta = (normalExpected - normalActual);
		delta = MathLib.abs(delta);
		boolean isEqual = false;
		isEqual = ((((signExpected).equals(signActual)) && (mantissaExpected == mantissaActual)) && (delta < deltaLimit));
		expectAssertTrue(message, EFloat.ezeBox(expected), EFloat.ezeBox(actual), isEqual);
	}
	public void assertFloatEqual1(double expected, double actual) {
		String eze$Temp26 = Constants.EMPTY_STRING;
		eze$Temp26 = "";
		assertFloatEqual(eze$Temp26, expected, actual);
	}
	public void expectAssertTrue(String message, egl.lang.EglAny expected, egl.lang.EglAny actual, boolean isEqual) {
		String failedReason = Constants.EMPTY_STRING;
		failedReason = expect(expected, actual);
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.notEquals(EString.ezeBox(message), null)) && (!(message).equals("")))) {
			failedReason = ((((message) + " - ")) + failedReason);
		}
		assertTrue(failedReason, isEqual);
	}
	public String expect(egl.lang.EglAny expected, egl.lang.EglAny actual) {
		String standardMsg = Constants.EMPTY_STRING;
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
