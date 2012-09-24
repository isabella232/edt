package org.eclipse.edt.jtopen.data.area;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import com.ibm.as400.access.DataArea;
import org.eclipse.edt.jtopen.data.common.CommonLib;
import org.eclipse.edt.jtopen.data.area.DataAreaDefinition;
import com.ibm.as400.access.DecimalDataArea;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.CharacterDataArea;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import eglx.lang.NullValueException;
import org.eclipse.edt.runtime.java.eglx.lang.EDecimal;
import java.math.BigDecimal;
import com.ibm.as400.access.AS400;
import org.eclipse.edt.jtopen.data.common.iDataAccessException;
import eglx.lang.AnyException;
import com.ibm.as400.access.LogicalDataArea;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import java.lang.Object;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="DataAreaLib")
public class DataAreaLib extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	public CommonLib eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib;
	public DataAreaLib() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
	}
	public CommonLib eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib() {
		if (eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib == null) {
			eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib = (CommonLib)org.eclipse.edt.javart.Runtime.getRunUnit().loadLibrary("org.eclipse.edt.jtopen.data.common.CommonLib");
		}
		return eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib;
	}
	public Object getDataArea(DataAreaDefinition dataAreaDef) {
		DataArea dataArea;
		dataArea = openDataArea(dataAreaDef);
		Object retVal = null;
		check4NullArea(dataArea, dataAreaDef.path, "getDataArea");
		try {
			if (org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeIsa(dataArea, CharacterDataArea.class)) {
				retVal = EAny.asAny(org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(dataArea, CharacterDataArea.class).read());
			}
			else {
				if (org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeIsa(dataArea, DecimalDataArea.class)) {
					retVal = EAny.asAny(org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(dataArea, DecimalDataArea.class).read());
				}
				else {
					retVal = EAny.asAny(org.eclipse.edt.runtime.java.eglx.lang.EBoolean.ezeBox(org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(dataArea, LogicalDataArea.class).read()));
				}
			}
			returnAS400ToPool(dataArea);
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
				returnAS400ToPool(dataArea);
				DataAreaDefinition eze$Temp4 = null;
				eze$Temp4 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataAreaDef, eze$Temp4);
				throwDataAreaException(exception, eze$Temp4, "getDataArea");
			}
		}
		return org.eclipse.edt.javart.util.JavartUtil.checkNullable(retVal);
	}
	public void updateDataArea(DataAreaDefinition dataAreaDef, Object elementData) {
		DataArea dataArea;
		dataArea = openDataArea(dataAreaDef);
		check4NullArea(dataArea, dataAreaDef.path, "updateDataArea");
		try {
			if (org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeIsa(dataArea, CharacterDataArea.class)) {
				org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(dataArea, CharacterDataArea.class).write(EString.ezeCast(elementData));
			}
			else {
				if (org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeIsa(dataArea, DecimalDataArea.class)) {
					org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(dataArea, DecimalDataArea.class).write(EDecimal.ezeCast(elementData));
				}
				else {
					boolean eze$Temp7;
					eze$Temp7 = org.eclipse.edt.runtime.java.eglx.lang.EBoolean.ezeCast(elementData);
					org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(dataArea, LogicalDataArea.class).write(eze$Temp7);
				}
			}
			returnAS400ToPool(dataArea);
		}
		catch ( java.lang.Exception eze$Temp8 ) {
			org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp8 );
			AnyException exception;
			if ( eze$Temp8 instanceof AnyException ) {
				exception = (AnyException)eze$Temp8;
			}
			else {
				exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp8);
			}
			{
				returnAS400ToPool(dataArea);
				DataAreaDefinition eze$Temp9 = null;
				eze$Temp9 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataAreaDef, eze$Temp9);
				throwDataAreaException(exception, eze$Temp9, "updateDataArea");
			}
		}
	}
	private void check4NullArea(DataArea dataarea, String path, String functioName) {
		if ((dataarea == null)) {
			iDataAccessException newException;
			newException = new iDataAccessException();
			NullValueException nullException;
			nullException = new NullValueException();
			newException.functionName = functioName;
			newException.setMessage(((newException.getMessage()) + eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().DATA_QUEUE_EXCEPTION_NOT_OPEN));
			newException.exception = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(nullException, AnyException.class);
			if ((path != null)) {
				newException.path =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(path);
			}
			throw newException;
		}
	}
	private void throwDataAreaException(AnyException exception, DataAreaDefinition dataAreaDef, String functioName) {
		iDataAccessException newException;
		newException = new iDataAccessException();
		newException.functionName = functioName;
		newException.setMessage(((newException.getMessage()) + exception.getMessage()));
		newException.exception = exception;
		newException.path =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(dataAreaDef.path);
		throw newException;
	}
	private DataArea openDataArea(DataAreaDefinition dataAreaDef) {
		DataArea dataArea = null;
		QSYSObjectPathName path;
		path = new QSYSObjectPathName(EString.clip(dataAreaDef.libname), EString.clip(dataAreaDef.areaname), "DTAARA");
		dataAreaDef.path = path.getPath();
		AS400 system;
		system = eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().getAS400FromPool(dataAreaDef.systemDef);
		{
			EzeLabel_eze_caselabel_0: if ((org.eclipse.edt.runtime.java.eglx.lang.EInt.equals(dataAreaDef.areaType, eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().DATA_AREA_TYPE_CHAR))) {
				dataArea = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(new CharacterDataArea(system, dataAreaDef.path), DataArea.class);
			}
			else {
				if ((org.eclipse.edt.runtime.java.eglx.lang.EInt.equals(dataAreaDef.areaType, eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().DATA_AREA_TYPE_DEC))) {
					dataArea = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(new DecimalDataArea(system, dataAreaDef.path), DataArea.class);
				}
				else {
					if ((org.eclipse.edt.runtime.java.eglx.lang.EInt.equals(dataAreaDef.areaType, eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().DATA_AREA_TYPE_LOG))) {
						dataArea = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(new LogicalDataArea(system, dataAreaDef.path), DataArea.class);
					}
					else {
						eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().returnAS400ToPool(system);
						iDataAccessException newException;
						newException = new iDataAccessException();
						newException.functionName = "openDataArea";
						newException.setMessage(((newException.getMessage()) + eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().DATA_AREA_EXCEPTION_INVALID_AREA_TYPE));
						newException.path =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(dataAreaDef.path);
						throw newException;
					}
				}
			}
		}
		return org.eclipse.edt.javart.util.JavartUtil.checkNullable(dataArea);
	}
	private void returnAS400ToPool(DataArea dataArea) {
		if ((dataArea != null)) {
			eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().returnAS400ToPool(org.eclipse.edt.javart.util.JavartUtil.checkNullable(dataArea.getSystem()));
		}
	}
}
