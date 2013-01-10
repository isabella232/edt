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
package eglx.persistence.sql;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.util.JavartUtil;

import eglx.lang.AnyException;

public class SQLUtilities {

	public static AnyException makeEglException(Throwable ex) {

		if (ex instanceof AnyException) {
			return (AnyException) ex;
		}

		String msg = ex.getMessage();
		String className = ex.getClass().getName();
		if (msg == null || msg.length() == 0) {
			msg = className;
		}
		if (ex instanceof java.sql.SQLException) {
			boolean isWarning = ex instanceof java.sql.SQLWarning;
			SQLException sqlx = isWarning ? new SQLWarning() : new SQLException();
			java.sql.SQLException caught = (java.sql.SQLException) ex;
			String state = caught.getSQLState();
			int code = caught.getErrorCode();
			sqlx.setSQLState(state);
			sqlx.setErrorCode(code);
			sqlx.initCause(ex);
			java.sql.SQLException nextEx = caught.getNextException();
			if (nextEx != null) {
				sqlx.setNextException((SQLException) makeEglException(nextEx));
			}
			if (isWarning) {
				java.sql.SQLWarning nextWarn = ((java.sql.SQLWarning) caught).getNextWarning();
				if (nextWarn != null) {
					((SQLWarning) sqlx).setNextWarning((SQLWarning) makeEglException(nextWarn));
				}
			}
			return sqlx.fillInMessage(Message.SQL_EXCEPTION_CAUGHT, msg, state, code);
		} else {
			return JavartUtil.makeEglException(ex);
		}
	}
}
