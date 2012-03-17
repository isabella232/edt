package org.eclipse.edt.jtopen.data.common;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import org.eclipse.edt.jtopen.data.common.SystemDefinition;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import java.lang.Object;
import eglx.lang.AnyException;
import eglx.jtopen.JTOpenConnections;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import com.ibm.as400.access.AS400;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="CommonLib")
public class CommonLib extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	private static final int ezeConst_NO_WAIT = (int)(short)((short) 0);
	public final int NO_WAIT = ezeConst_NO_WAIT;
	private static final int ezeConst_WAIT_FOREVER = (int)(short)((short) -1);
	public final int WAIT_FOREVER = ezeConst_WAIT_FOREVER;
	private static final int ezeConst_DATA_AREA_TYPE_CHAR = (int)(short)((short) 1);
	public final int DATA_AREA_TYPE_CHAR = ezeConst_DATA_AREA_TYPE_CHAR;
	private static final int ezeConst_DATA_AREA_TYPE_DEC = (int)(short)((short) 2);
	public final int DATA_AREA_TYPE_DEC = ezeConst_DATA_AREA_TYPE_DEC;
	private static final int ezeConst_DATA_AREA_TYPE_LOG = (int)(short)((short) 3);
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
	@org.eclipse.edt.javart.json.Json(name="NO_WAIT", clazz=EInt.class, asOptions={})
	public int getNO_WAIT() {
		return NO_WAIT;
	}
	@org.eclipse.edt.javart.json.Json(name="WAIT_FOREVER", clazz=EInt.class, asOptions={})
	public int getWAIT_FOREVER() {
		return WAIT_FOREVER;
	}
	@org.eclipse.edt.javart.json.Json(name="DATA_AREA_TYPE_CHAR", clazz=EInt.class, asOptions={})
	public int getDATA_AREA_TYPE_CHAR() {
		return DATA_AREA_TYPE_CHAR;
	}
	@org.eclipse.edt.javart.json.Json(name="DATA_AREA_TYPE_DEC", clazz=EInt.class, asOptions={})
	public int getDATA_AREA_TYPE_DEC() {
		return DATA_AREA_TYPE_DEC;
	}
	@org.eclipse.edt.javart.json.Json(name="DATA_AREA_TYPE_LOG", clazz=EInt.class, asOptions={})
	public int getDATA_AREA_TYPE_LOG() {
		return DATA_AREA_TYPE_LOG;
	}
	@org.eclipse.edt.javart.json.Json(name="KEYED_QUEUE_SEARCH_TYPE_EQ", clazz=EString.class, asOptions={})
	public String getKEYED_QUEUE_SEARCH_TYPE_EQ() {
		return KEYED_QUEUE_SEARCH_TYPE_EQ;
	}
	@org.eclipse.edt.javart.json.Json(name="KEYED_QUEUE_SEARCH_TYPE_NE", clazz=EString.class, asOptions={})
	public String getKEYED_QUEUE_SEARCH_TYPE_NE() {
		return KEYED_QUEUE_SEARCH_TYPE_NE;
	}
	@org.eclipse.edt.javart.json.Json(name="KEYED_QUEUE_SEARCH_TYPE_LT", clazz=EString.class, asOptions={})
	public String getKEYED_QUEUE_SEARCH_TYPE_LT() {
		return KEYED_QUEUE_SEARCH_TYPE_LT;
	}
	@org.eclipse.edt.javart.json.Json(name="KEYED_QUEUE_SEARCH_TYPE_LE", clazz=EString.class, asOptions={})
	public String getKEYED_QUEUE_SEARCH_TYPE_LE() {
		return KEYED_QUEUE_SEARCH_TYPE_LE;
	}
	@org.eclipse.edt.javart.json.Json(name="KEYED_QUEUE_SEARCH_TYPE_GT", clazz=EString.class, asOptions={})
	public String getKEYED_QUEUE_SEARCH_TYPE_GT() {
		return KEYED_QUEUE_SEARCH_TYPE_GT;
	}
	@org.eclipse.edt.javart.json.Json(name="KEYED_QUEUE_SEARCH_TYPE_GE", clazz=EString.class, asOptions={})
	public String getKEYED_QUEUE_SEARCH_TYPE_GE() {
		return KEYED_QUEUE_SEARCH_TYPE_GE;
	}
	@org.eclipse.edt.javart.json.Json(name="DATA_QUEUE_EXCEPTION_KEYED_NOT_KEYED", clazz=EString.class, asOptions={})
	public String getDATA_QUEUE_EXCEPTION_KEYED_NOT_KEYED() {
		return DATA_QUEUE_EXCEPTION_KEYED_NOT_KEYED;
	}
	@org.eclipse.edt.javart.json.Json(name="DATA_QUEUE_EXCEPTION_NOT_KEYED_KEYED", clazz=EString.class, asOptions={})
	public String getDATA_QUEUE_EXCEPTION_NOT_KEYED_KEYED() {
		return DATA_QUEUE_EXCEPTION_NOT_KEYED_KEYED;
	}
	@org.eclipse.edt.javart.json.Json(name="DATA_QUEUE_EXCEPTION_NOT_OPEN", clazz=EString.class, asOptions={})
	public String getDATA_QUEUE_EXCEPTION_NOT_OPEN() {
		return DATA_QUEUE_EXCEPTION_NOT_OPEN;
	}
	@org.eclipse.edt.javart.json.Json(name="DATA_AREA_EXCEPTION_NOT_OPEN", clazz=EString.class, asOptions={})
	public String getDATA_AREA_EXCEPTION_NOT_OPEN() {
		return DATA_AREA_EXCEPTION_NOT_OPEN;
	}
	@org.eclipse.edt.javart.json.Json(name="DATA_AREA_EXCEPTION_INVALID_AREA_TYPE", clazz=EString.class, asOptions={})
	public String getDATA_AREA_EXCEPTION_INVALID_AREA_TYPE() {
		return DATA_AREA_EXCEPTION_INVALID_AREA_TYPE;
	}
	@org.eclipse.edt.javart.json.Json(name="DATA_QUEUE_KEYED", clazz=EString.class, asOptions={})
	public String getDATA_QUEUE_KEYED() {
		return DATA_QUEUE_KEYED;
	}
	@org.eclipse.edt.javart.json.Json(name="DATA_QUEUE_NOT_KEYED", clazz=EString.class, asOptions={})
	public String getDATA_QUEUE_NOT_KEYED() {
		return DATA_QUEUE_NOT_KEYED;
	}
	public void returnAS400ToPool(AS400 system) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(system, null))) {
			JTOpenConnections.getAS400ConnectionPool().returnConnectionToPool(system);
		}
	}
	public AS400 getAS400FromPool(SystemDefinition systemDef) {
		AS400 system = new AS400();
		try {
			boolean eze$Temp2;
			eze$Temp2 = (EInt.notEquals(EString.length(EString.clip(systemDef.password)), (int)(short)((short) 0)));
			if (eze$Temp2) {
				system = JTOpenConnections.getAS400ConnectionPool().getConnection(systemDef.systemName, systemDef.userId, systemDef.password);
			}
			else {
				system = JTOpenConnections.getAS400ConnectionPool().getConnection(systemDef.systemName, systemDef.userId);
			}
		}
		catch ( java.lang.Exception eze$Temp4 )
		{
			org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp4 );
			AnyException exception;
			if ( eze$Temp4 instanceof AnyException )
			{
				exception = (AnyException)eze$Temp4;
			}
			else
			{
				exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp4);
			}
			{
				throw exception;
			}
		}
		return system;
	}
}
