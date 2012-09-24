package org.eclipse.edt.jtopen.data.common;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.jtopen.data.common.SystemDefinition;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import eglx.jtopen.JTOpenConnections;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import com.ibm.as400.access.AS400;
import eglx.lang.AnyException;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import java.lang.Object;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="CommonLib")
public class CommonLib extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	private static final int ezeConst_NO_WAIT = 0;
	public final int NO_WAIT = ezeConst_NO_WAIT;
	private static final int ezeConst_WAIT_FOREVER = -1;
	public final int WAIT_FOREVER = ezeConst_WAIT_FOREVER;
	private static final int ezeConst_DATA_AREA_TYPE_CHAR = 1;
	public final int DATA_AREA_TYPE_CHAR = ezeConst_DATA_AREA_TYPE_CHAR;
	private static final int ezeConst_DATA_AREA_TYPE_DEC = 2;
	public final int DATA_AREA_TYPE_DEC = ezeConst_DATA_AREA_TYPE_DEC;
	private static final int ezeConst_DATA_AREA_TYPE_LOG = 3;
	public final int DATA_AREA_TYPE_LOG = ezeConst_DATA_AREA_TYPE_LOG;
	private static final String ezeConst_KEYED_QUEUE_SEARCH_TYPE_EQ = "EQ";
	public final String KEYED_QUEUE_SEARCH_TYPE_EQ = ezeConst_KEYED_QUEUE_SEARCH_TYPE_EQ;
	private static final String ezeConst_KEYED_QUEUE_SEARCH_TYPE_NE = "NE";
	public final String KEYED_QUEUE_SEARCH_TYPE_NE = ezeConst_KEYED_QUEUE_SEARCH_TYPE_NE;
	private static final String ezeConst_KEYED_QUEUE_SEARCH_TYPE_LT = "LT";
	public final String KEYED_QUEUE_SEARCH_TYPE_LT = ezeConst_KEYED_QUEUE_SEARCH_TYPE_LT;
	private static final String ezeConst_KEYED_QUEUE_SEARCH_TYPE_LE = "LE";
	public final String KEYED_QUEUE_SEARCH_TYPE_LE = ezeConst_KEYED_QUEUE_SEARCH_TYPE_LE;
	private static final String ezeConst_KEYED_QUEUE_SEARCH_TYPE_GT = "GT";
	public final String KEYED_QUEUE_SEARCH_TYPE_GT = ezeConst_KEYED_QUEUE_SEARCH_TYPE_GT;
	private static final String ezeConst_KEYED_QUEUE_SEARCH_TYPE_GE = "GE";
	public final String KEYED_QUEUE_SEARCH_TYPE_GE = ezeConst_KEYED_QUEUE_SEARCH_TYPE_GE;
	private static final String ezeConst_DATA_QUEUE_EXCEPTION_KEYED_NOT_KEYED = "The queue was opened as keyed but accessed as not keyed.";
	public final String DATA_QUEUE_EXCEPTION_KEYED_NOT_KEYED = ezeConst_DATA_QUEUE_EXCEPTION_KEYED_NOT_KEYED;
	private static final String ezeConst_DATA_QUEUE_EXCEPTION_NOT_KEYED_KEYED = "The queue was opened as not keyed but accessed as keyed.";
	public final String DATA_QUEUE_EXCEPTION_NOT_KEYED_KEYED = ezeConst_DATA_QUEUE_EXCEPTION_NOT_KEYED_KEYED;
	private static final String ezeConst_DATA_QUEUE_EXCEPTION_NOT_OPEN = "The dataqueue was not opened or there was an error opening the queue.";
	public final String DATA_QUEUE_EXCEPTION_NOT_OPEN = ezeConst_DATA_QUEUE_EXCEPTION_NOT_OPEN;
	private static final String ezeConst_DATA_AREA_EXCEPTION_NOT_OPEN = "The dataarea was not opened or there was an error opening the queue.";
	public final String DATA_AREA_EXCEPTION_NOT_OPEN = ezeConst_DATA_AREA_EXCEPTION_NOT_OPEN;
	private static final String ezeConst_DATA_AREA_EXCEPTION_INVALID_AREA_TYPE = "The dataarea type is invalid.";
	public final String DATA_AREA_EXCEPTION_INVALID_AREA_TYPE = ezeConst_DATA_AREA_EXCEPTION_INVALID_AREA_TYPE;
	private static final String ezeConst_DATA_QUEUE_KEYED = "KeyedDataQueue";
	public final String DATA_QUEUE_KEYED = ezeConst_DATA_QUEUE_KEYED;
	private static final String ezeConst_DATA_QUEUE_NOT_KEYED = "DataQueue";
	public final String DATA_QUEUE_NOT_KEYED = ezeConst_DATA_QUEUE_NOT_KEYED;
	public CommonLib() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
	}
	public int getNO_WAIT() {
		return NO_WAIT;
	}
	public int getWAIT_FOREVER() {
		return WAIT_FOREVER;
	}
	public int getDATA_AREA_TYPE_CHAR() {
		return DATA_AREA_TYPE_CHAR;
	}
	public int getDATA_AREA_TYPE_DEC() {
		return DATA_AREA_TYPE_DEC;
	}
	public int getDATA_AREA_TYPE_LOG() {
		return DATA_AREA_TYPE_LOG;
	}
	public String getKEYED_QUEUE_SEARCH_TYPE_EQ() {
		return KEYED_QUEUE_SEARCH_TYPE_EQ;
	}
	public String getKEYED_QUEUE_SEARCH_TYPE_NE() {
		return KEYED_QUEUE_SEARCH_TYPE_NE;
	}
	public String getKEYED_QUEUE_SEARCH_TYPE_LT() {
		return KEYED_QUEUE_SEARCH_TYPE_LT;
	}
	public String getKEYED_QUEUE_SEARCH_TYPE_LE() {
		return KEYED_QUEUE_SEARCH_TYPE_LE;
	}
	public String getKEYED_QUEUE_SEARCH_TYPE_GT() {
		return KEYED_QUEUE_SEARCH_TYPE_GT;
	}
	public String getKEYED_QUEUE_SEARCH_TYPE_GE() {
		return KEYED_QUEUE_SEARCH_TYPE_GE;
	}
	public String getDATA_QUEUE_EXCEPTION_KEYED_NOT_KEYED() {
		return DATA_QUEUE_EXCEPTION_KEYED_NOT_KEYED;
	}
	public String getDATA_QUEUE_EXCEPTION_NOT_KEYED_KEYED() {
		return DATA_QUEUE_EXCEPTION_NOT_KEYED_KEYED;
	}
	public String getDATA_QUEUE_EXCEPTION_NOT_OPEN() {
		return DATA_QUEUE_EXCEPTION_NOT_OPEN;
	}
	public String getDATA_AREA_EXCEPTION_NOT_OPEN() {
		return DATA_AREA_EXCEPTION_NOT_OPEN;
	}
	public String getDATA_AREA_EXCEPTION_INVALID_AREA_TYPE() {
		return DATA_AREA_EXCEPTION_INVALID_AREA_TYPE;
	}
	public String getDATA_QUEUE_KEYED() {
		return DATA_QUEUE_KEYED;
	}
	public String getDATA_QUEUE_NOT_KEYED() {
		return DATA_QUEUE_NOT_KEYED;
	}
	public void returnAS400ToPool(AS400 system) {
		if ((system != null)) {
			JTOpenConnections.getAS400ConnectionPool().returnConnectionToPool(system);
		}
	}
	public AS400 getAS400FromPool(SystemDefinition systemDef) {
		AS400 system = new AS400();
		try {
			if ((EInt.notEquals(EString.length(EString.clip(systemDef.password)), 0))) {
				system =  (AS400) org.eclipse.edt.javart.util.JavartUtil.checkNullable(JTOpenConnections.getAS400ConnectionPool().getConnection(systemDef.systemName, systemDef.userId, systemDef.password));
			}
			else {
				system =  (AS400) org.eclipse.edt.javart.util.JavartUtil.checkNullable(JTOpenConnections.getAS400ConnectionPool().getConnection(systemDef.systemName, systemDef.userId));
			}
		}
		catch ( java.lang.Exception eze$Temp3 ) {
			org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp3 );
			AnyException exception;
			if ( eze$Temp3 instanceof AnyException ) {
				exception = (AnyException)eze$Temp3;
			}
			else {
				exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp3);
			}
			{
				throw exception;
			}
		}
		return system;
	}
}
