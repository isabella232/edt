package org.eclipse.edt.runtime.java.eglx.xml;

import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.edt.runtime.java.eglx.lang.EDate;

public class DateAdapter extends DateTimeBase {

	@Override
	public String marshal(Calendar v) throws Exception {
		return stripTimeZone(DatatypeConverter.printDate(convertToGmt(v)));
	}

	@Override
	public Calendar unmarshal(String v) throws Exception {
		return EDate.asDate(DatatypeConverter.parseDate(v));
	}

}
