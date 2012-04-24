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
