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
import java.lang.Integer;
import java.lang.Long;
import java.math.BigDecimal;
import java.lang.Double;
import java.lang.Boolean;
import java.util.Calendar;
import eglx.lang.StringLib;
import java.lang.String;
import java.lang.Short;

import org.eclipse.edt.runtime.java.eglx.lang.*;

import java.lang.Float;
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
		defaultDateFormat = "";
		defaultTimeFormat = "";
		defaultTimeStampFormat = "";
		defaultMoneyFormat = "";
		defaultNumericFormat = "";
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
		int eze$Temp2;
		eze$Temp2 = EString.length(EString.clip(text));
		return eze$Temp2;
	}
	public String clip(String source) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(source), null))) {
			return null;
		}
		else {
			String eze$Temp4;
			eze$Temp4 = EString.clip(source);
			return eze$Temp4;
		}
	}
	public String clip(String source, Integer clipType) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(source), null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EInt.ezeBox(clipType), null)))) {
			return null;
		}
		else {
			if ((EInt.equals(clipType, (int)((short) 0)))) {
				String eze$Temp7;
				eze$Temp7 = EString.trim(source);
				return eze$Temp7;
			}
			else {
				if ((EInt.equals(clipType, (int)((short) 1)))) {
					String eze$Temp9;
					eze$Temp9 = EString.clipLeading(source);
					return eze$Temp9;
				}
				else {
					if ((EInt.equals(clipType, (int)((short) 2)))) {
						String eze$Temp11;
						eze$Temp11 = EString.clip(source);
						return eze$Temp11;
					}
					else {
						return source;
					}
				}
			}
		}
	}
	public String formatDate(Calendar dateValue) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EDate.ezeBox(dateValue), null))) {
			return null;
		}
		else {
			Calendar eze$Temp14;
			eze$Temp14 =  (Calendar) org.eclipse.edt.javart.util.JavartUtil.checkNullable(dateValue);
			String eze$Temp13;
			eze$Temp13 = StringLib.format(eze$Temp14, defaultDateFormat);
			return eze$Temp13;
		}
	}
	public String formatDate(Calendar dateValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EDate.ezeBox(dateValue), null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			Calendar eze$Temp17;
			eze$Temp17 =  (Calendar) org.eclipse.edt.javart.util.JavartUtil.checkNullable(dateValue);
			String eze$Temp18;
			eze$Temp18 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp16;
			eze$Temp16 = StringLib.format(eze$Temp17, eze$Temp18);
			return eze$Temp16;
		}
	}
	public String formatNumber(Short intValue) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(ESmallint.ezeBox(intValue), null))) {
			return null;
		}
		else {
			short eze$Temp21;
			eze$Temp21 =  (Short) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp20;
			eze$Temp20 = StringLib.format(eze$Temp21, defaultNumericFormat);
			return eze$Temp20;
		}
	}
	public String formatNumber(Short intValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(ESmallint.ezeBox(intValue), null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			short eze$Temp24;
			eze$Temp24 =  (Short) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp25;
			eze$Temp25 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp23;
			eze$Temp23 = StringLib.format(eze$Temp24, eze$Temp25);
			return eze$Temp23;
		}
	}
	public String formatNumber(Integer intValue) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EInt.ezeBox(intValue), null))) {
			return null;
		}
		else {
			int eze$Temp28;
			eze$Temp28 =  (Integer) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp27;
			eze$Temp27 = StringLib.format(eze$Temp28, defaultNumericFormat);
			return eze$Temp27;
		}
	}
	public String formatNumber(Integer intValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EInt.ezeBox(intValue), null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			int eze$Temp31;
			eze$Temp31 =  (Integer) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp32;
			eze$Temp32 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp30;
			eze$Temp30 = StringLib.format(eze$Temp31, eze$Temp32);
			return eze$Temp30;
		}
	}
	public String formatNumber(Long intValue) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EBigint.ezeBox(intValue), null))) {
			return null;
		}
		else {
			long eze$Temp35;
			eze$Temp35 =  (Long) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp34;
			eze$Temp34 = StringLib.format(eze$Temp35, defaultNumericFormat);
			return eze$Temp34;
		}
	}
	public String formatNumber(Long intValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EBigint.ezeBox(intValue), null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			long eze$Temp38;
			eze$Temp38 =  (Long) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp39;
			eze$Temp39 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp37;
			eze$Temp37 = StringLib.format(eze$Temp38, eze$Temp39);
			return eze$Temp37;
		}
	}
	public String formatNumber(BigDecimal decValue) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EDecimal.ezeBox(decValue), null))) {
			return null;
		}
		else {
			String eze$Temp41;
			eze$Temp41 = StringLib.format(decValue, defaultNumericFormat);
			return eze$Temp41;
		}
	}
	public String formatNumber(BigDecimal decValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EDecimal.ezeBox(decValue), null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			String eze$Temp44;
			eze$Temp44 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp43;
			eze$Temp43 = StringLib.format(decValue, eze$Temp44);
			return eze$Temp43;
		}
	}
	public String formatNumber(Float floatValue) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(ESmallfloat.ezeBox(floatValue), null))) {
			return null;
		}
		else {
			float eze$Temp47;
			eze$Temp47 =  (Float) org.eclipse.edt.javart.util.JavartUtil.checkNullable(floatValue);
			String eze$Temp46;
			eze$Temp46 = StringLib.format(eze$Temp47, defaultNumericFormat);
			return eze$Temp46;
		}
	}
	public String formatNumber(Float floatValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(ESmallfloat.ezeBox(floatValue), null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			float eze$Temp50;
			eze$Temp50 =  (Float) org.eclipse.edt.javart.util.JavartUtil.checkNullable(floatValue);
			String eze$Temp51;
			eze$Temp51 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp49;
			eze$Temp49 = StringLib.format(eze$Temp50, eze$Temp51);
			return eze$Temp49;
		}
	}
	public String formatNumber(Double floatValue) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EFloat.ezeBox(floatValue), null))) {
			return null;
		}
		else {
			double eze$Temp54;
			eze$Temp54 =  (Double) org.eclipse.edt.javart.util.JavartUtil.checkNullable(floatValue);
			String eze$Temp53;
			eze$Temp53 = StringLib.format(eze$Temp54, defaultNumericFormat);
			return eze$Temp53;
		}
	}
	public String formatNumber(Double floatValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EFloat.ezeBox(floatValue), null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			double eze$Temp57;
			eze$Temp57 =  (Double) org.eclipse.edt.javart.util.JavartUtil.checkNullable(floatValue);
			String eze$Temp58;
			eze$Temp58 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp56;
			eze$Temp56 = StringLib.format(eze$Temp57, eze$Temp58);
			return eze$Temp56;
		}
	}
	public String formatTimeStamp(Calendar timestampvalue) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(ETimestamp.ezeBox(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.FRACTION6_CODE), null))) {
			return null;
		}
		else {
			Calendar eze$Temp61;
			eze$Temp61 =  (Calendar) org.eclipse.edt.javart.util.JavartUtil.checkNullable(ETimestamp.ezeCast(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE));
			String eze$Temp60;
			eze$Temp60 = StringLib.format(eze$Temp61, defaultTimeStampFormat);
			return eze$Temp60;
		}
	}
	public String formatTimeStamp(Calendar timestampvalue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(ETimestamp.ezeBox(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.FRACTION6_CODE), null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			Calendar eze$Temp64;
			eze$Temp64 =  (Calendar) org.eclipse.edt.javart.util.JavartUtil.checkNullable(ETimestamp.ezeCast(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE));
			String eze$Temp65;
			eze$Temp65 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp63;
			eze$Temp63 = StringLib.format(eze$Temp64, eze$Temp65);
			return eze$Temp63;
		}
	}
	public String getNextToken(AnyBoxedObject<String> source, AnyBoxedObject<Integer> index, String delimiters) {
		int charIndex;
		charIndex = EInt.asInt((EDecimal.plus((EInt.divide(index.ezeUnbox(), (int)((short) 2))), EDecimal.asDecimal(ESmallint.ezeBox((short) 1)))));
		AnyBoxedObject<Integer> eze$Temp66;
		eze$Temp66 = EAny.ezeWrap(charIndex);
		String token;
		token = StringLib.getNextToken(source.ezeUnbox(), eze$Temp66, delimiters);
		charIndex = eze$Temp66.ezeUnbox();
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(EString.ezeBox(token), null))) {
			index.ezeCopy((((charIndex - (int)((short) 1)) * (int)((short) 2)) + (int)((short) 1)));
		}
		return token;
	}
	public int getTokenCount(String source, String delimiters) {
		int eze$Temp68;
		eze$Temp68 = StringLib.getTokenCount(source, delimiters);
		return eze$Temp68;
	}
	public int indexOf(AnyBoxedObject<String> source, String pattern) {
		int eze$Temp69;
		eze$Temp69 = EString.indexOf(source.ezeUnbox(), pattern);
		return eze$Temp69;
	}
	public int indexOf(AnyBoxedObject<String> source, String pattern, int startIndex) {
		int eze$Temp70;
		eze$Temp70 = EString.indexOf(source.ezeUnbox(), pattern, startIndex);
		return eze$Temp70;
	}
	public String lowerCase(String characterItem) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(characterItem), null))) {
			return null;
		}
		else {
			String eze$Temp72;
			eze$Temp72 = EString.toLowerCase(characterItem);
			return eze$Temp72;
		}
	}
	public String spaces(Integer characterCount) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EInt.ezeBox(characterCount), null))) {
			return null;
		}
		else {
			String result = "";
			String fiftyBlanks;
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
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(EString.ezeBox(characterItem), null))) {
			return null;
		}
		else {
			String eze$Temp77;
			eze$Temp77 = EString.toUpperCase(characterItem);
			return eze$Temp77;
		}
	}
}
