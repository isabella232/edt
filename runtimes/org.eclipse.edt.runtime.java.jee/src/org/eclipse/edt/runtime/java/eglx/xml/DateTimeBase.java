/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.runtime.java.eglx.xml;

import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;


abstract class DateTimeBase extends XmlAdapter<String, Calendar> {

	private static TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
	protected Calendar convertToGmt(Calendar cal){
		Calendar gmt = Calendar.getInstance(gmtTimeZone);
		gmt.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
		gmt.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		gmt.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		gmt.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
		gmt.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		gmt.set(Calendar.SECOND, cal.get(Calendar.SECOND));
		gmt.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));
		gmt.getTimeInMillis();
		return gmt;
	}
	
	protected static String stripTimeZone(String val){
		StringBuilder str = new StringBuilder(val);
		if(str.charAt(str.length() - 1) == 'Z'){
			str.deleteCharAt(str.length() - 1);
		}
		return str.toString();

	}
}
