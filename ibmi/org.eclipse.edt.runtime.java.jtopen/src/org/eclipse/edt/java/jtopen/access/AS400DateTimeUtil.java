package org.eclipse.edt.java.jtopen.access;

import java.util.TimeZone;

import org.eclipse.edt.javart.util.DateTimeUtil;

import com.ibm.as400.access.AS400;

public class AS400DateTimeUtil {
	static TimeZone getIBMiTimezoneID(AS400 system) {
		TimeZone tz = null;
		if(system != null){
			try {
				tz = system.getTimeZone();
			} catch (Exception e) {
			}
		}
		if(tz == null){
			tz = DateTimeUtil.getBaseCalendar().getTimeZone();
		}
		return tz;
	}
	static TimeZone getIBMiTimezoneID(String timeZoneID) {
		TimeZone tz = null;
		if(timeZoneID != null && !timeZoneID.isEmpty()){
			tz = TimeZone.getTimeZone(timeZoneID);
		}
		if(tz == null){
			tz = DateTimeUtil.getBaseCalendar().getTimeZone();
		}
		return tz;
	}

}
