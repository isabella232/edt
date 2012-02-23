package org.eclipse.edt.java.jtopen.access;

import java.util.TimeZone;

import org.eclipse.edt.javart.util.DateTimeUtil;

import eglx.jtopen.IBMiConnection;

public class AS400DateTimeUtil {
	private static TimeZone getIBMiTimezone(IBMiConnection conn) {
		TimeZone tz = null;
		String tzStr;
		//if an an override is specified on the IBMi connection use that
		//otherwise try to use the connection's timezone
		if(conn != null && (tzStr = conn.getTimezone()) != null && !tzStr.isEmpty()){
			try {
				tz = getIBMiTimezone(tzStr);
			} catch (Exception e) {
			}
		}
		else if(conn != null){
			try {
				if(conn.getAS400() != null){
					tz = conn.getAS400().getTimeZone();
				}
			} catch (Exception e) {
			}
		}
		//as a last resort assume the timezone is the application's timezone
		if(tz == null){
			tz = DateTimeUtil.getBaseCalendar().getTimeZone();
		}
		return tz;
	}
	private static TimeZone getIBMiTimezone(String timeZoneID) {
		TimeZone tz = null;
		if(timeZoneID != null && !timeZoneID.isEmpty()){
			tz = TimeZone.getTimeZone(timeZoneID);
		}
		if(tz == null){
			tz = DateTimeUtil.getBaseCalendar().getTimeZone();
		}
		return tz;
	}
	static TimeZone getIBMiTimezone(String timeZoneID, IBMiConnection conn) {
		return timeZoneID != null ? getIBMiTimezone(timeZoneID) : getIBMiTimezone(conn);
	}
	static String getConnSeparator(String seperator, IBMiConnection conn) {
		if(seperator != null && seperator.isEmpty()){
			String connSeparatorChar = conn.getDateSeparatorChar();
			if(connSeparatorChar != null && "null".equalsIgnoreCase(connSeparatorChar)){
				seperator = null;
			}
			else if(connSeparatorChar != null && !connSeparatorChar.isEmpty()){
				seperator = connSeparatorChar;
			}
		}
		return seperator;
	}
}
