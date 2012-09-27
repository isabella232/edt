/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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
