package eglx.rbd;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.egl.lang.ETimestamp;
import java.util.Calendar;
import org.eclipse.edt.runtime.java.egl.lang.EBigint;
import java.lang.Long;
import org.eclipse.edt.runtime.java.egl.lang.ESmallint;
import java.lang.Short;
import org.eclipse.edt.runtime.java.egl.lang.EglAny;
import org.eclipse.edt.runtime.java.egl.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.runtime.java.egl.lang.EDecimal;
import java.math.BigDecimal;
import org.eclipse.edt.runtime.java.egl.lang.EFloat;
import java.lang.Double;
import org.eclipse.edt.runtime.java.egl.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.egl.lang.EDate;
import org.eclipse.edt.runtime.java.egl.lang.ESmallfloat;
import java.lang.Float;
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
	public StrLib( RunUnit ru ) {
		super( ru );
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
			eze$Temp12 = dateValue == null ? EDate.defaultValue() : dateValue;
			String eze$Temp11 = Constants.EMPTY_STRING;
			eze$Temp11 = eglx.lang.StrLib.format(eze$Temp12, defaultDateFormat);
			return eze$Temp11;
		}
	}
	public String formatDate(Calendar dateValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EDate.ezeBox(dateValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			Calendar eze$Temp15 = EDate.defaultValue();
			eze$Temp15 = dateValue == null ? EDate.defaultValue() : dateValue;
			String eze$Temp16 = Constants.EMPTY_STRING;
			eze$Temp16 = format == null ? Constants.EMPTY_STRING : format;
			String eze$Temp14 = Constants.EMPTY_STRING;
			eze$Temp14 = eglx.lang.StrLib.format(eze$Temp15, eze$Temp16);
			return eze$Temp14;
		}
	}
	public String formatNumber(Short intValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(ESmallint.ezeBox(intValue), null))) {
			return null;
		}
		else {
			short eze$Temp19 = (short) 0;
			eze$Temp19 = intValue == null ? (short) 0 : intValue;
			String eze$Temp18 = Constants.EMPTY_STRING;
			eze$Temp18 = eglx.lang.StrLib.format(eze$Temp19, defaultNumericFormat);
			return eze$Temp18;
		}
	}
	public String formatNumber(Short intValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(ESmallint.ezeBox(intValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			short eze$Temp22 = (short) 0;
			eze$Temp22 = intValue == null ? (short) 0 : intValue;
			String eze$Temp23 = Constants.EMPTY_STRING;
			eze$Temp23 = format == null ? Constants.EMPTY_STRING : format;
			String eze$Temp21 = Constants.EMPTY_STRING;
			eze$Temp21 = eglx.lang.StrLib.format(eze$Temp22, eze$Temp23);
			return eze$Temp21;
		}
	}
	public String formatNumber(Integer intValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EInt.ezeBox(intValue), null))) {
			return null;
		}
		else {
			int eze$Temp26 = 0;
			eze$Temp26 = intValue == null ? 0 : intValue;
			String eze$Temp25 = Constants.EMPTY_STRING;
			eze$Temp25 = eglx.lang.StrLib.format(eze$Temp26, defaultNumericFormat);
			return eze$Temp25;
		}
	}
	public String formatNumber(Integer intValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EInt.ezeBox(intValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			int eze$Temp29 = 0;
			eze$Temp29 = intValue == null ? 0 : intValue;
			String eze$Temp30 = Constants.EMPTY_STRING;
			eze$Temp30 = format == null ? Constants.EMPTY_STRING : format;
			String eze$Temp28 = Constants.EMPTY_STRING;
			eze$Temp28 = eglx.lang.StrLib.format(eze$Temp29, eze$Temp30);
			return eze$Temp28;
		}
	}
	public String formatNumber(Long intValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EBigint.ezeBox(intValue), null))) {
			return null;
		}
		else {
			long eze$Temp33 = (long) 0;
			eze$Temp33 = intValue == null ? (long) 0 : intValue;
			String eze$Temp32 = Constants.EMPTY_STRING;
			eze$Temp32 = eglx.lang.StrLib.format(eze$Temp33, defaultNumericFormat);
			return eze$Temp32;
		}
	}
	public String formatNumber(Long intValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EBigint.ezeBox(intValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			long eze$Temp36 = (long) 0;
			eze$Temp36 = intValue == null ? (long) 0 : intValue;
			String eze$Temp37 = Constants.EMPTY_STRING;
			eze$Temp37 = format == null ? Constants.EMPTY_STRING : format;
			String eze$Temp35 = Constants.EMPTY_STRING;
			eze$Temp35 = eglx.lang.StrLib.format(eze$Temp36, eze$Temp37);
			return eze$Temp35;
		}
	}
	public String formatNumber(BigDecimal decValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EDecimal.ezeBox(decValue), null))) {
			return null;
		}
		else {
			BigDecimal eze$Temp40 = BigDecimal.ZERO;
			eze$Temp40 = decValue == null ? BigDecimal.ZERO : decValue;
			String eze$Temp39 = Constants.EMPTY_STRING;
			eze$Temp39 = eglx.lang.StrLib.format(eze$Temp40, defaultNumericFormat);
			return eze$Temp39;
		}
	}
	public String formatNumber(BigDecimal decValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EDecimal.ezeBox(decValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			BigDecimal eze$Temp43 = BigDecimal.ZERO;
			eze$Temp43 = decValue == null ? BigDecimal.ZERO : decValue;
			String eze$Temp44 = Constants.EMPTY_STRING;
			eze$Temp44 = format == null ? Constants.EMPTY_STRING : format;
			String eze$Temp42 = Constants.EMPTY_STRING;
			eze$Temp42 = eglx.lang.StrLib.format(eze$Temp43, eze$Temp44);
			return eze$Temp42;
		}
	}
	public String formatNumber(Float floatValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(ESmallfloat.ezeBox(floatValue), null))) {
			return null;
		}
		else {
			float eze$Temp47 = 0.0f;
			eze$Temp47 = floatValue == null ? 0.0f : floatValue;
			String eze$Temp46 = Constants.EMPTY_STRING;
			eze$Temp46 = eglx.lang.StrLib.format(eze$Temp47, defaultNumericFormat);
			return eze$Temp46;
		}
	}
	public String formatNumber(Float floatValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(ESmallfloat.ezeBox(floatValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			float eze$Temp50 = 0.0f;
			eze$Temp50 = floatValue == null ? 0.0f : floatValue;
			String eze$Temp51 = Constants.EMPTY_STRING;
			eze$Temp51 = format == null ? Constants.EMPTY_STRING : format;
			String eze$Temp49 = Constants.EMPTY_STRING;
			eze$Temp49 = eglx.lang.StrLib.format(eze$Temp50, eze$Temp51);
			return eze$Temp49;
		}
	}
	public String formatNumber(Double floatValue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EFloat.ezeBox(floatValue), null))) {
			return null;
		}
		else {
			double eze$Temp54 = 0.0f;
			eze$Temp54 = floatValue == null ? 0.0f : floatValue;
			String eze$Temp53 = Constants.EMPTY_STRING;
			eze$Temp53 = eglx.lang.StrLib.format(eze$Temp54, defaultNumericFormat);
			return eze$Temp53;
		}
	}
	public String formatNumber(Double floatValue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EFloat.ezeBox(floatValue), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			double eze$Temp57 = 0.0f;
			eze$Temp57 = floatValue == null ? 0.0f : floatValue;
			String eze$Temp58 = Constants.EMPTY_STRING;
			eze$Temp58 = format == null ? Constants.EMPTY_STRING : format;
			String eze$Temp56 = Constants.EMPTY_STRING;
			eze$Temp56 = eglx.lang.StrLib.format(eze$Temp57, eze$Temp58);
			return eze$Temp56;
		}
	}
	public String formatTimeStamp(Calendar timestampvalue) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(ETimestamp.ezeBox(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.FRACTION6_CODE), null))) {
			return null;
		}
		else {
			Calendar eze$Temp61 = ETimestamp.defaultValue();
			eze$Temp61 = ETimestamp.asTimestamp(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE) == null ? ETimestamp.defaultValue() : ETimestamp.asTimestamp(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE);
			String eze$Temp60 = Constants.EMPTY_STRING;
			eze$Temp60 = eglx.lang.StrLib.format(eze$Temp61, defaultTimeStampFormat);
			return eze$Temp60;
		}
	}
	public String formatTimeStamp(Calendar timestampvalue, String format) {
		if (((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(ETimestamp.ezeBox(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.FRACTION6_CODE), null)) || (org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(format), null)))) {
			return null;
		}
		else {
			Calendar eze$Temp64 = ETimestamp.defaultValue();
			eze$Temp64 = ETimestamp.asTimestamp(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE) == null ? ETimestamp.defaultValue() : ETimestamp.asTimestamp(timestampvalue, ETimestamp.YEAR_CODE, ETimestamp.SECOND_CODE);
			String eze$Temp65 = Constants.EMPTY_STRING;
			eze$Temp65 = format == null ? Constants.EMPTY_STRING : format;
			String eze$Temp63 = Constants.EMPTY_STRING;
			eze$Temp63 = eglx.lang.StrLib.format(eze$Temp64, eze$Temp65);
			return eze$Temp63;
		}
	}
	public String getNextToken(AnyBoxedObject<String> source, AnyBoxedObject<Integer> index, String delimiters) {
		AnyBoxedObject<Integer> eze$Temp67 = null;
		eze$Temp67 = EglAny.ezeWrap(index.ezeUnbox());
		String eze$Temp66 = Constants.EMPTY_STRING;
		eze$Temp66 = eglx.lang.StrLib.getNextToken(source.ezeUnbox(), eze$Temp67, delimiters);
		index.ezeCopy(eze$Temp67.ezeUnbox());
		return eze$Temp66;
	}
	public int getTokenCount(String source, String delimiters) {
		int eze$Temp68 = 0;
		eze$Temp68 = eglx.lang.StrLib.getTokenCount(source, delimiters);
		return eze$Temp68;
	}
	public int indexOf(AnyBoxedObject<String> source, String pattern) {
		int eze$Temp69 = 0;
		eze$Temp69 = EString.indexOf(source.ezeUnbox(), pattern);
		return eze$Temp69;
	}
	public int indexOf(AnyBoxedObject<String> source, String pattern, int startIndex) {
		int eze$Temp70 = 0;
		eze$Temp70 = EString.indexOf(source.ezeUnbox(), pattern, startIndex);
		return eze$Temp70;
	}
	public String lowerCase(String characterItem) {
		if ((org.eclipse.edt.runtime.java.egl.lang.NullType.equals(EString.ezeBox(characterItem), null))) {
			return null;
		}
		else {
			String eze$Temp72 = Constants.EMPTY_STRING;
			eze$Temp72 = EString.toLowerCase(characterItem);
			return eze$Temp72;
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
			String eze$Temp77 = Constants.EMPTY_STRING;
			eze$Temp77 = EString.toUpperCase(characterItem);
			return eze$Temp77;
		}
	}
}
