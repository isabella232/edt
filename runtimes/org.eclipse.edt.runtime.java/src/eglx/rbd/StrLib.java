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
package eglx.rbd;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.egl.lang.EFloat;
import java.lang.Double;
import org.eclipse.edt.runtime.java.egl.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.egl.lang.ESmallint;
import java.lang.Short;
import org.eclipse.edt.runtime.java.egl.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.runtime.java.egl.lang.EBigint;
import java.lang.Long;
import eglx.lang.StringLib;
import org.eclipse.edt.runtime.java.egl.lang.EDate;
import java.util.Calendar;
import org.eclipse.edt.runtime.java.egl.lang.ESmallfloat;
import java.lang.Float;
import org.eclipse.edt.runtime.java.egl.lang.ETimestamp;
import org.eclipse.edt.runtime.java.egl.lang.EDecimal;
import java.math.BigDecimal;
import org.eclipse.edt.runtime.java.egl.lang.EglAny;
@javax.xml.bind.annotation.XmlRootElement(name="StrLib")
public class StrLib extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public String defaultDateFormat;
	@javax.xml.bind.annotation.XmlTransient
	public String defaultTimeFormat;
	@javax.xml.bind.annotation.XmlTransient
	public String defaultTimeStampFormat;
	@javax.xml.bind.annotation.XmlTransient
	public String defaultMoneyFormat;
	@javax.xml.bind.annotation.XmlTransient
	public String defaultNumericFormat;
	public StrLib() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
		defaultDateFormat = Constants.EMPTY_STRING;
		defaultTimeFormat = Constants.EMPTY_STRING;
		defaultTimeStampFormat = Constants.EMPTY_STRING;
		defaultMoneyFormat = Constants.EMPTY_STRING;
		defaultNumericFormat = Constants.EMPTY_STRING;
	}
	@org.eclipse.edt.javart.json.Json(name="defaultDateFormat", clazz=EString.class, asOptions={})
	public String getDefaultDateFormat() {
		return (defaultDateFormat);
	}
	public void setDefaultDateFormat( String ezeValue ) {
		this.defaultDateFormat = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="defaultTimeFormat", clazz=EString.class, asOptions={})
	public String getDefaultTimeFormat() {
		return (defaultTimeFormat);
	}
	public void setDefaultTimeFormat( String ezeValue ) {
		this.defaultTimeFormat = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="defaultTimeStampFormat", clazz=EString.class, asOptions={})
	public String getDefaultTimeStampFormat() {
		return (defaultTimeStampFormat);
	}
	public void setDefaultTimeStampFormat( String ezeValue ) {
		this.defaultTimeStampFormat = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="defaultMoneyFormat", clazz=EString.class, asOptions={})
	public String getDefaultMoneyFormat() {
		return (defaultMoneyFormat);
	}
	public void setDefaultMoneyFormat( String ezeValue ) {
		this.defaultMoneyFormat = ezeValue;
	}
	@org.eclipse.edt.javart.json.Json(name="defaultNumericFormat", clazz=EString.class, asOptions={})
	public String getDefaultNumericFormat() {
		return (defaultNumericFormat);
	}
	public void setDefaultNumericFormat( String ezeValue ) {
		this.defaultNumericFormat = ezeValue;
	}
	public String booleanAsString(boolean value) {
		if (value) {
			return "true";
		}
		else {
			return "false";
		}
	}
	public int characterLen(String text) {
		int eze$Temp2 = 0;
		eze$Temp2 = EString.length(EString.trim(text));
		return eze$Temp2;
	}
	public String clip(String source) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(source), null))) {
			return null;
		}
		else {
			String eze$Temp4 = Constants.EMPTY_STRING;
			eze$Temp4 = EString.clip(source);
			return eze$Temp4;
		}
	}
	public String clip(String source, Integer clipType) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(source), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EInt.ezeBox(clipType), null)))) {
			return null;
		}
		else {
			String eze$Temp6 = Constants.EMPTY_STRING;
			eze$Temp6 = EString.trim(source);
			if ((EInt.equals(clipType, (int)((short) 0)))) {
				return eze$Temp6;
			}
			else {
				if ((EInt.equals(clipType, (int)((short) 1)))) {
					return EString.clipLeading(source);
				}
				else if ((EInt.equals(clipType, (int)((short) 2)))) {
					return EString.clip(source);
				}
				else {
					return source;
				}
			}
		}
	}
	public String formatDate(Calendar dateValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EDate.ezeBox(dateValue), null))) {
			return null;
		}
		else {
			Calendar eze$Temp12 = EDate.defaultValue();
			eze$Temp12 =  (Calendar) org.eclipse.edt.javart.util.JavartUtil.checkNullable(dateValue);
			String eze$Temp11 = Constants.EMPTY_STRING;
			eze$Temp11 = StringLib.format(eze$Temp12, defaultDateFormat);
			return eze$Temp11;
		}
	}
	public String formatDate(Calendar dateValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EDate.ezeBox(dateValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			Calendar eze$Temp15 = EDate.defaultValue();
			eze$Temp15 =  (Calendar) org.eclipse.edt.javart.util.JavartUtil.checkNullable(dateValue);
			String eze$Temp16 = Constants.EMPTY_STRING;
			eze$Temp16 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp14 = Constants.EMPTY_STRING;
			eze$Temp14 = StringLib.format(eze$Temp15, eze$Temp16);
			return eze$Temp14;
		}
	}
	public String formatNumber(Short intValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(ESmallint.ezeBox(intValue), null))) {
			return null;
		}
		else {
			short eze$Temp19 = (short) 0;
			eze$Temp19 =  (Short) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp18 = Constants.EMPTY_STRING;
			eze$Temp18 = StringLib.format(eze$Temp19, defaultNumericFormat);
			return eze$Temp18;
		}
	}
	public String formatNumber(Short intValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(ESmallint.ezeBox(intValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			short eze$Temp22 = (short) 0;
			eze$Temp22 =  (Short) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp23 = Constants.EMPTY_STRING;
			eze$Temp23 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp21 = Constants.EMPTY_STRING;
			eze$Temp21 = StringLib.format(eze$Temp22, eze$Temp23);
			return eze$Temp21;
		}
	}
	public String formatNumber(Integer intValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EInt.ezeBox(intValue), null))) {
			return null;
		}
		else {
			int eze$Temp26 = 0;
			eze$Temp26 =  (Integer) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp25 = Constants.EMPTY_STRING;
			eze$Temp25 = StringLib.format(eze$Temp26, defaultNumericFormat);
			return eze$Temp25;
		}
	}
	public String formatNumber(Integer intValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EInt.ezeBox(intValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			int eze$Temp29 = 0;
			eze$Temp29 =  (Integer) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp30 = Constants.EMPTY_STRING;
			eze$Temp30 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp28 = Constants.EMPTY_STRING;
			eze$Temp28 = StringLib.format(eze$Temp29, eze$Temp30);
			return eze$Temp28;
		}
	}
	public String formatNumber(Long intValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EBigint.ezeBox(intValue), null))) {
			return null;
		}
		else {
			long eze$Temp33 = (long) 0;
			eze$Temp33 =  (Long) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp32 = Constants.EMPTY_STRING;
			eze$Temp32 = StringLib.format(eze$Temp33, defaultNumericFormat);
			return eze$Temp32;
		}
	}
	public String formatNumber(Long intValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EBigint.ezeBox(intValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			long eze$Temp36 = (long) 0;
			eze$Temp36 =  (Long) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp37 = Constants.EMPTY_STRING;
			eze$Temp37 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp35 = Constants.EMPTY_STRING;
			eze$Temp35 = StringLib.format(eze$Temp36, eze$Temp37);
			return eze$Temp35;
		}
	}
	public String formatNumber(BigDecimal decValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EDecimal.ezeBox(decValue), null))) {
			return null;
		}
		else {
			String eze$Temp39 = Constants.EMPTY_STRING;
			eze$Temp39 = StringLib.format(decValue, defaultNumericFormat);
			return eze$Temp39;
		}
	}
	public String formatNumber(BigDecimal decValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EDecimal.ezeBox(decValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			String eze$Temp42 = Constants.EMPTY_STRING;
			eze$Temp42 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp41 = Constants.EMPTY_STRING;
			eze$Temp41 = StringLib.format(decValue, eze$Temp42);
			return eze$Temp41;
		}
	}
	public String formatNumber(Float floatValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(ESmallfloat.ezeBox(floatValue), null))) {
			return null;
		}
		else {
			float eze$Temp45 = 0.0f;
			eze$Temp45 =  (Float) org.eclipse.edt.javart.util.JavartUtil.checkNullable(floatValue);
			String eze$Temp44 = Constants.EMPTY_STRING;
			eze$Temp44 = StringLib.format(eze$Temp45, defaultNumericFormat);
			return eze$Temp44;
		}
	}
	public String formatNumber(Float floatValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(ESmallfloat.ezeBox(floatValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			float eze$Temp48 = 0.0f;
			eze$Temp48 =  (Float) org.eclipse.edt.javart.util.JavartUtil.checkNullable(floatValue);
			String eze$Temp49 = Constants.EMPTY_STRING;
			eze$Temp49 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp47 = Constants.EMPTY_STRING;
			eze$Temp47 = StringLib.format(eze$Temp48, eze$Temp49);
			return eze$Temp47;
		}
	}
	public String formatNumber(Double floatValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EFloat.ezeBox(floatValue), null))) {
			return null;
		}
		else {
			double eze$Temp52 = 0.0;
			eze$Temp52 =  (Double) org.eclipse.edt.javart.util.JavartUtil.checkNullable(floatValue);
			String eze$Temp51 = Constants.EMPTY_STRING;
			eze$Temp51 = StringLib.format(eze$Temp52, defaultNumericFormat);
			return eze$Temp51;
		}
	}
	public String formatNumber(Double floatValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EFloat.ezeBox(floatValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			double eze$Temp55 = 0.0;
			eze$Temp55 =  (Double) org.eclipse.edt.javart.util.JavartUtil.checkNullable(floatValue);
			String eze$Temp56 = Constants.EMPTY_STRING;
			eze$Temp56 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp54 = Constants.EMPTY_STRING;
			eze$Temp54 = StringLib.format(eze$Temp55, eze$Temp56);
			return eze$Temp54;
		}
	}
	public String formatTimeStamp(Calendar timestampvalue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(ETimestamp.ezeBox(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.FRACTION6_CODE), null))) {
			return null;
		}
		else {
			Calendar eze$Temp59 = ETimestamp.defaultValue();
			eze$Temp59 =  (Calendar) org.eclipse.edt.javart.util.JavartUtil.checkNullable(ETimestamp.ezeCast(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE));
			String eze$Temp58 = Constants.EMPTY_STRING;
			eze$Temp58 = StringLib.format(eze$Temp59, defaultTimeStampFormat);
			return eze$Temp58;
		}
	}
	public String formatTimeStamp(Calendar timestampvalue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(ETimestamp.ezeBox(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.FRACTION6_CODE), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			Calendar eze$Temp62 = ETimestamp.defaultValue();
			eze$Temp62 =  (Calendar) org.eclipse.edt.javart.util.JavartUtil.checkNullable(ETimestamp.ezeCast(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE));
			String eze$Temp63 = Constants.EMPTY_STRING;
			eze$Temp63 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp61 = Constants.EMPTY_STRING;
			eze$Temp61 = StringLib.format(eze$Temp62, eze$Temp63);
			return eze$Temp61;
		}
	}
	public String getNextToken(AnyBoxedObject<String> source, AnyBoxedObject<Integer> index, String delimiters) {
		int charIndex = 0;
		charIndex = EInt.asInt((EDecimal.plus((EInt.divide(index.ezeUnbox(), (int)((short) 2))), EDecimal.asDecimal(ESmallint.ezeBox((short) 1)))));
		String token = null;
		AnyBoxedObject<Integer> eze$Temp64 = null;
		eze$Temp64 = EglAny.ezeWrap(charIndex);
		token = StringLib.getNextToken(source.ezeUnbox(), eze$Temp64, delimiters);
		charIndex = eze$Temp64.ezeUnbox();
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.notEquals(EString.ezeBox(token), null))) {
			index.ezeCopy((((charIndex - (int)((short) 1)) * (int)((short) 2)) + (int)((short) 1)));
		}
		return token;
	}
	public int getTokenCount(String source, String delimiters) {
		int eze$Temp66 = 0;
		eze$Temp66 = StringLib.getTokenCount(source, delimiters);
		return eze$Temp66;
	}
	public int indexOf(AnyBoxedObject<String> source, String pattern) {
		int eze$Temp67 = 0;
		eze$Temp67 = EString.indexOf(source.ezeUnbox(), pattern);
		return eze$Temp67;
	}
	public int indexOf(AnyBoxedObject<String> source, String pattern, int startIndex) {
		int eze$Temp68 = 0;
		eze$Temp68 = EString.indexOf(source.ezeUnbox(), pattern, startIndex);
		return eze$Temp68;
	}
	public String lowerCase(String characterItem) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(characterItem), null))) {
			return null;
		}
		else {
			String eze$Temp70 = Constants.EMPTY_STRING;
			eze$Temp70 = EString.toLowerCase(characterItem);
			return eze$Temp70;
		}
	}
	public String spaces(Integer characterCount) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EInt.ezeBox(characterCount), null))) {
			return null;
		}
		else {
			String result = Constants.EMPTY_STRING;
			String fiftyBlanks = Constants.EMPTY_STRING;
			fiftyBlanks = "                                                  ";
			while ((EInt.compareTo(characterCount, (int)((short) 50)) >= 0)) {
				result += fiftyBlanks;
				characterCount -= (int)((short) 50);
			}
			if ((EInt.compareTo(characterCount, (int)((short) 0)) > 0)) {
				result += EString.substring(fiftyBlanks, (int)((short) 1), characterCount);
			}
			return result;
		}
	}
	public String upperCase(String characterItem) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(characterItem), null))) {
			return null;
		}
		else {
			String eze$Temp75 = Constants.EMPTY_STRING;
			eze$Temp75 = EString.toUpperCase(characterItem);
			return eze$Temp75;
		}
	}
}
