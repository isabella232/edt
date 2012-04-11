/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package eglx.rbd;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EBoolean;
import java.lang.Boolean;
import org.eclipse.edt.runtime.java.eglx.lang.ESmallint;
import java.lang.Short;
import eglx.lang.StringLib;
import org.eclipse.edt.runtime.java.eglx.lang.EDate;
import java.util.Calendar;
import org.eclipse.edt.runtime.java.eglx.lang.ETimestamp;
import org.eclipse.edt.runtime.java.eglx.lang.EFloat;
import java.lang.Double;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import java.lang.Object;
import org.eclipse.edt.runtime.java.eglx.lang.EDecimal;
import java.math.BigDecimal;
import org.eclipse.edt.runtime.java.eglx.lang.ESmallfloat;
import java.lang.Float;
import org.eclipse.edt.runtime.java.eglx.lang.EBigint;
import java.lang.Long;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="StrLib")
public class StrLib extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	public StrLib() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
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
		return EString.length(EString.clip(text));
	}
	public String clip(String source) {
		if ((source == null)) {
			return null;
		}
		else {
			return EString.clip(source);
		}
	}
	public String clip(String source, Integer clipType) {
		if (((source == null) || (clipType == null))) {
			return null;
		}
		else {
			{
				EzeLabel_eze_caselabel_0: if ((EInt.equals(clipType, 0))) {
					return EString.trim(source);
				}
				else {
					if ((EInt.equals(clipType, 1))) {
						return EString.clipLeading(source);
					}
					else {
						if ((EInt.equals(clipType, 2))) {
							return EString.clip(source);
						}
						else {
							return source;
						}
					}
				}
			}
		}
	}
	public String formatDate(Calendar dateValue, String format) {
		if (((dateValue == null) || (format == null))) {
			return null;
		}
		else {
			Calendar eze$Temp9;
			eze$Temp9 =  (Calendar) org.eclipse.edt.javart.util.JavartUtil.checkNullable(dateValue);
			String eze$Temp8;
			eze$Temp8 = StringLib.format(eze$Temp9, org.eclipse.edt.javart.util.JavartUtil.checkNullable(format));
			return eze$Temp8;
		}
	}
	public String formatNumber(Short intValue, String format) {
		if (((intValue == null) || (format == null))) {
			return null;
		}
		else {
			short eze$Temp12;
			eze$Temp12 =  (Short) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp11;
			eze$Temp11 = StringLib.format(eze$Temp12, org.eclipse.edt.javart.util.JavartUtil.checkNullable(format));
			return eze$Temp11;
		}
	}
	public String formatNumber(Integer intValue, String format) {
		if (((intValue == null) || (format == null))) {
			return null;
		}
		else {
			int eze$Temp15;
			eze$Temp15 =  (Integer) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp14;
			eze$Temp14 = StringLib.format(eze$Temp15, org.eclipse.edt.javart.util.JavartUtil.checkNullable(format));
			return eze$Temp14;
		}
	}
	public String formatNumber(Long intValue, String format) {
		if (((intValue == null) || (format == null))) {
			return null;
		}
		else {
			long eze$Temp18;
			eze$Temp18 =  (Long) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp17;
			eze$Temp17 = StringLib.format(eze$Temp18, org.eclipse.edt.javart.util.JavartUtil.checkNullable(format));
			return eze$Temp17;
		}
	}
	public String formatNumber(BigDecimal decValue, String format) {
		if (((decValue == null) || (format == null))) {
			return null;
		}
		else {
			return StringLib.format(org.eclipse.edt.javart.util.JavartUtil.checkNullable(decValue), org.eclipse.edt.javart.util.JavartUtil.checkNullable(format));
		}
	}
	public String formatNumber(Float floatValue, String format) {
		if (((floatValue == null) || (format == null))) {
			return null;
		}
		else {
			float eze$Temp22;
			eze$Temp22 =  (Float) org.eclipse.edt.javart.util.JavartUtil.checkNullable(floatValue);
			String eze$Temp21;
			eze$Temp21 = StringLib.format(eze$Temp22, org.eclipse.edt.javart.util.JavartUtil.checkNullable(format));
			return eze$Temp21;
		}
	}
	public String formatNumber(Double floatValue, String format) {
		if (((floatValue == null) || (format == null))) {
			return null;
		}
		else {
			double eze$Temp25;
			eze$Temp25 =  (Double) org.eclipse.edt.javart.util.JavartUtil.checkNullable(floatValue);
			String eze$Temp24;
			eze$Temp24 = StringLib.format(eze$Temp25, org.eclipse.edt.javart.util.JavartUtil.checkNullable(format));
			return eze$Temp24;
		}
	}
	public String formatTimeStamp(Calendar timestampvalue, String format) {
		if (((timestampvalue == null) || (format == null))) {
			return null;
		}
		else {
			return StringLib.format(org.eclipse.edt.javart.util.JavartUtil.checkNullable(timestampvalue), org.eclipse.edt.javart.util.JavartUtil.checkNullable(format));
		}
	}
	public String getNextToken(String source, AnyBoxedObject<Integer> index, String delimiters) {
		int charIndex;
		charIndex = EInt.asInt((EDecimal.plus((EInt.divide(index.ezeUnbox(), 2)), EDecimal.asDecimal((short) 1))));
		AnyBoxedObject<Integer> eze$Temp27;
		eze$Temp27 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(charIndex);
		String token;
		token = StringLib.getNextToken(source, eze$Temp27, delimiters);
		charIndex = org.eclipse.edt.javart.util.JavartUtil.checkNullable(eze$Temp27.ezeUnbox());
		index.ezeCopy((((charIndex - 1) * 2) + 1));
		return token;
	}
	public int getTokenCount(String source, String delimiters) {
		return StringLib.getTokenCount(source, delimiters);
	}
	public int indexOf(String source, String pattern) {
		return EString.indexOf(source, pattern);
	}
	public int indexOf(String source, String pattern, int startIndex) {
		return EString.indexOf(source, pattern, startIndex);
	}
	public String lowerCase(String characterItem) {
		if ((characterItem == null)) {
			return null;
		}
		else {
			return EString.toLowerCase(characterItem);
		}
	}
	public String spaces(Integer characterCount) {
		if ((characterCount == null)) {
			return null;
		}
		else {
			String result = "";
			String fiftyBlanks;
			fiftyBlanks = "                                                  ";
			while ((EInt.compareTo(characterCount, 50) >= 0)) {
				result = ((result) + fiftyBlanks);
				characterCount -= 50;
			}
			if ((EInt.compareTo(characterCount, 0) > 0)) {
				result = ((result) + EString.substring(fiftyBlanks, 1, characterCount));
			}
			return result;
		}
	}
	public String upperCase(String characterItem) {
		if ((characterItem == null)) {
			return null;
		}
		else {
			return EString.toUpperCase(characterItem);
		}
	}
}
