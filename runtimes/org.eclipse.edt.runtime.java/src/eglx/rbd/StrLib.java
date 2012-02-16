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
import org.eclipse.edt.runtime.java.eglx.lang.EDecimal;
import java.math.BigDecimal;
import eglx.lang.StringLib;
import org.eclipse.edt.runtime.java.eglx.lang.EDate;
import java.util.Calendar;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.runtime.java.eglx.lang.EBoolean;
import java.lang.Boolean;
import org.eclipse.edt.runtime.java.eglx.lang.EFloat;
import java.lang.Double;
import org.eclipse.edt.runtime.java.eglx.lang.ESmallint;
import java.lang.Short;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import org.eclipse.edt.runtime.java.eglx.lang.ETimestamp;
import org.eclipse.edt.runtime.java.eglx.lang.ESmallfloat;
import java.lang.Float;
import org.eclipse.edt.runtime.java.eglx.lang.EBigint;
import java.lang.Long;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
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
	public String booleanAsString(Boolean value) {
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
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(source, null))) {
			return null;
		}
		else {
			String eze$Temp4;
			eze$Temp4 = EString.clip(source);
			return eze$Temp4;
		}
	}
	public String clip(String source, Integer clipType) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(source, null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(clipType, null)))) {
			return null;
		}
		else {
			{
				EzeLabel_eze_caselabel_0: if ((EInt.equals(clipType, (int)(short)((short) 0)))) {
					String eze$Temp7;
					eze$Temp7 = EString.trim(source);
					return eze$Temp7;
				}
				else {
					if ((EInt.equals(clipType, (int)(short)((short) 1)))) {
						String eze$Temp9;
						eze$Temp9 = EString.clipLeading(source);
						return eze$Temp9;
					}
					else {
						if ((EInt.equals(clipType, (int)(short)((short) 2)))) {
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
	}
	public String formatDate(Calendar dateValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(dateValue, null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(format, null)))) {
			return null;
		}
		else {
			Calendar eze$Temp14;
			eze$Temp14 =  (Calendar) org.eclipse.edt.javart.util.JavartUtil.checkNullable(dateValue);
			String eze$Temp15;
			eze$Temp15 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp13;
			eze$Temp13 = StringLib.format(eze$Temp14, eze$Temp15);
			return eze$Temp13;
		}
	}
	public String formatNumber(Short intValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(intValue, null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(format, null)))) {
			return null;
		}
		else {
			short eze$Temp18;
			eze$Temp18 =  (Short) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp19;
			eze$Temp19 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp17;
			eze$Temp17 = StringLib.format(eze$Temp18, eze$Temp19);
			return eze$Temp17;
		}
	}
	public String formatNumber(Integer intValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(intValue, null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(format, null)))) {
			return null;
		}
		else {
			int eze$Temp22;
			eze$Temp22 =  (Integer) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp23;
			eze$Temp23 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp21;
			eze$Temp21 = StringLib.format(eze$Temp22, eze$Temp23);
			return eze$Temp21;
		}
	}
	public String formatNumber(Long intValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(intValue, null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(format, null)))) {
			return null;
		}
		else {
			long eze$Temp26;
			eze$Temp26 =  (Long) org.eclipse.edt.javart.util.JavartUtil.checkNullable(intValue);
			String eze$Temp27;
			eze$Temp27 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp25;
			eze$Temp25 = StringLib.format(eze$Temp26, eze$Temp27);
			return eze$Temp25;
		}
	}
	public String formatNumber(BigDecimal decValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(decValue, null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(format, null)))) {
			return null;
		}
		else {
			String eze$Temp30;
			eze$Temp30 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp29;
			eze$Temp29 = StringLib.format(decValue, eze$Temp30);
			return eze$Temp29;
		}
	}
	public String formatNumber(Float floatValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(floatValue, null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(format, null)))) {
			return null;
		}
		else {
			float eze$Temp33;
			eze$Temp33 =  (Float) org.eclipse.edt.javart.util.JavartUtil.checkNullable(floatValue);
			String eze$Temp34;
			eze$Temp34 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp32;
			eze$Temp32 = StringLib.format(eze$Temp33, eze$Temp34);
			return eze$Temp32;
		}
	}
	public String formatNumber(Double floatValue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(floatValue, null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(format, null)))) {
			return null;
		}
		else {
			double eze$Temp37;
			eze$Temp37 =  (Double) org.eclipse.edt.javart.util.JavartUtil.checkNullable(floatValue);
			String eze$Temp38;
			eze$Temp38 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp36;
			eze$Temp36 = StringLib.format(eze$Temp37, eze$Temp38);
			return eze$Temp36;
		}
	}
	public String formatTimeStamp(Calendar timestampvalue, String format) {
		if (((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(timestampvalue, null)) || (org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(format, null)))) {
			return null;
		}
		else {
			String eze$Temp41;
			eze$Temp41 =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(format);
			String eze$Temp40;
			eze$Temp40 = StringLib.format(timestampvalue, eze$Temp41);
			return eze$Temp40;
		}
	}
	public String getNextToken(String source, AnyBoxedObject<Integer> index, String delimiters) {
		int charIndex;
		charIndex = EInt.asInt((EDecimal.plus((EInt.divide(index.ezeUnbox(), (int)(short)((short) 2))), EDecimal.asDecimal((short) 1))));
		AnyBoxedObject<Integer> eze$Temp42;
		eze$Temp42 = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(charIndex);
		String token;
		token = StringLib.getNextToken(source, eze$Temp42, delimiters);
		charIndex = org.eclipse.edt.javart.util.JavartUtil.checkNullable(eze$Temp42.ezeUnbox());
		index.ezeCopy((((charIndex - (int)(short)((short) 1)) * (int)(short)((short) 2)) + (int)(short)((short) 1)));
		return token;
	}
	public int getTokenCount(String source, String delimiters) {
		int eze$Temp43;
		eze$Temp43 = StringLib.getTokenCount(source, delimiters);
		return eze$Temp43;
	}
	public int indexOf(String source, String pattern) {
		int eze$Temp44;
		eze$Temp44 = EString.indexOf(source, pattern);
		return eze$Temp44;
	}
	public int indexOf(String source, String pattern, Integer startIndex) {
		int eze$Temp45;
		eze$Temp45 = EString.indexOf(source, pattern, startIndex);
		return eze$Temp45;
	}
	public String lowerCase(String characterItem) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(characterItem, null))) {
			return null;
		}
		else {
			String eze$Temp47;
			eze$Temp47 = EString.toLowerCase(characterItem);
			return eze$Temp47;
		}
	}
	public String spaces(Integer characterCount) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(characterCount, null))) {
			return null;
		}
		else {
			String result = "";
			String fiftyBlanks;
			fiftyBlanks = "                                                  ";
			while ((EInt.compareTo(characterCount, (int)(short)((short) 50)) >= 0)) {
				result = ((result) + fiftyBlanks);
				characterCount -= (int)(short)((short) 50);
			}
			if ((EInt.compareTo(characterCount, (int)(short)((short) 0)) > 0)) {
				result = ((result) + EString.substring(fiftyBlanks, (int)(short)((short) 1), characterCount));
			}
			return result;
		}
	}
	public String upperCase(String characterItem) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(characterItem, null))) {
			return null;
		}
		else {
			String eze$Temp52;
			eze$Temp52 = EString.toUpperCase(characterItem);
			return eze$Temp52;
		}
	}
}
