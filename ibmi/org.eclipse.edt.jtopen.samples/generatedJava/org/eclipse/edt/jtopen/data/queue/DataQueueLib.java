package org.eclipse.edt.jtopen.data.queue;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import com.ibm.as400.access.QSYSObjectPathName;
import eglx.lang.TypeCastException;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import java.lang.Object;
import org.eclipse.edt.jtopen.data.common.iDataAccessException;
import eglx.lang.NullValueException;
import com.ibm.as400.access.KeyedDataQueueEntry;
import org.eclipse.edt.jtopen.data.queue.DataQueueDefinition;
import com.ibm.as400.access.DataQueueEntry;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.jtopen.data.common.CommonLib;
import org.eclipse.edt.runtime.java.eglx.lang.EBoolean;
import java.lang.Boolean;
import eglx.lang.AnyException;
import com.ibm.as400.access.BaseDataQueue;
import com.ibm.as400.access.KeyedDataQueue;
import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.AS400;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="DataQueueLib")
public class DataQueueLib extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	public CommonLib eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib;
	public DataQueueLib() {
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
	public void addElement(DataQueueDefinition dataQueueDef, Object elementData) {
		BaseDataQueue dataQueue;
		dataQueue = openDataQueue(dataQueueDef);
		check4TypeCast(false, dataQueue, "addElement");
		try {
			org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(dataQueue, DataQueue.class).write(EString.ezeCast(elementData));
			returnAS400ToPool(dataQueue);
		}
		catch ( java.lang.Exception eze$Temp1 )
		{
			org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp1 );
			AnyException exception;
			if ( eze$Temp1 instanceof AnyException )
			{
				exception = (AnyException)eze$Temp1;
			}
			else
			{
				exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp1);
			}
			{
				returnAS400ToPool(dataQueue);
				DataQueueDefinition eze$Temp2 = null;
				eze$Temp2 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataQueueDef, eze$Temp2);
				throwDataQueueException(exception, eze$Temp2, "addElement");
			}
		}
	}
	public void addElement(DataQueueDefinition dataQueueDef, Object elementData, Object keyData) {
		BaseDataQueue dataQueue;
		dataQueue = openDataQueue(dataQueueDef);
		check4TypeCast(true, dataQueue, "addElement");
		try {
			org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(dataQueue, KeyedDataQueue.class).write(EString.ezeCast(keyData), EString.ezeCast(elementData));
			returnAS400ToPool(dataQueue);
		}
		catch ( java.lang.Exception eze$Temp3 )
		{
			org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp3 );
			AnyException exception;
			if ( eze$Temp3 instanceof AnyException )
			{
				exception = (AnyException)eze$Temp3;
			}
			else
			{
				exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp3);
			}
			{
				returnAS400ToPool(dataQueue);
				DataQueueDefinition eze$Temp4 = null;
				eze$Temp4 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataQueueDef, eze$Temp4);
				throwDataQueueException(exception, eze$Temp4, "addElement");
			}
		}
	}
	public boolean getNextElement(DataQueueDefinition dataQueueDef, Object elementData, AnyBoxedObject<Integer> wait) {
		BaseDataQueue dataQueue;
		dataQueue = openDataQueue(dataQueueDef);
		check4TypeCast(false, dataQueue, "getNextElement");
		DataQueueEntry dataQueueEntry;
		dataQueueEntry = null;
		try {
			dataQueueEntry = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(dataQueue, DataQueue.class).read(wait.ezeUnbox());
		}
		catch ( java.lang.Exception eze$Temp5 )
		{
			org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp5 );
			AnyException exception;
			if ( eze$Temp5 instanceof AnyException )
			{
				exception = (AnyException)eze$Temp5;
			}
			else
			{
				exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp5);
			}
			{
				returnAS400ToPool(dataQueue);
				DataQueueDefinition eze$Temp6 = null;
				eze$Temp6 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataQueueDef, eze$Temp6);
				throwDataQueueException(exception, eze$Temp6, "getNextElement");
			}
		}
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(dataQueueEntry, null))) {
			try {
				elementData = EAny.asAny(dataQueueEntry.getString());
				returnAS400ToPool(dataQueue);
			}
			catch ( java.lang.Exception eze$Temp8 )
			{
				org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp8 );
				AnyException exception;
				if ( eze$Temp8 instanceof AnyException )
				{
					exception = (AnyException)eze$Temp8;
				}
				else
				{
					exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp8);
				}
				{
					returnAS400ToPool(dataQueue);
					DataQueueDefinition eze$Temp9 = null;
					eze$Temp9 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataQueueDef, eze$Temp9);
					throwDataQueueException(exception, eze$Temp9, "getNextElement");
				}
			}
		}
		return (org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(dataQueueEntry, null));
	}
	public boolean getNextElement(DataQueueDefinition dataQueueDef, Object elementData, AnyBoxedObject<Integer> wait, Object keyData, String searchType) {
		BaseDataQueue dataQueue;
		dataQueue = openDataQueue(dataQueueDef);
		check4TypeCast(true, dataQueue, "getNextElement");
		KeyedDataQueueEntry dataQueueEntry;
		dataQueueEntry = null;
		try {
			dataQueueEntry = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(dataQueue, KeyedDataQueue.class).read(EString.clip(EString.ezeCast(keyData)), wait.ezeUnbox(), EString.clip(searchType));
		}
		catch ( java.lang.Exception eze$Temp10 )
		{
			org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp10 );
			AnyException exception;
			if ( eze$Temp10 instanceof AnyException )
			{
				exception = (AnyException)eze$Temp10;
			}
			else
			{
				exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp10);
			}
			{
				returnAS400ToPool(dataQueue);
				DataQueueDefinition eze$Temp11 = null;
				eze$Temp11 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataQueueDef, eze$Temp11);
				throwDataQueueException(exception, eze$Temp11, "getNextElement");
			}
		}
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(dataQueueEntry, null))) {
			try {
				elementData = EAny.asAny(dataQueueEntry.getString());
				returnAS400ToPool(dataQueue);
			}
			catch ( java.lang.Exception eze$Temp13 )
			{
				org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp13 );
				AnyException exception;
				if ( eze$Temp13 instanceof AnyException )
				{
					exception = (AnyException)eze$Temp13;
				}
				else
				{
					exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp13);
				}
				{
					returnAS400ToPool(dataQueue);
					DataQueueDefinition eze$Temp14 = null;
					eze$Temp14 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataQueueDef, eze$Temp14);
					throwDataQueueException(exception, eze$Temp14, "getNextElement");
				}
			}
		}
		return (org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(dataQueueEntry, null));
	}
	public boolean browseNextElement(DataQueueDefinition dataQueueDef, Object elementData, AnyBoxedObject<Integer> wait) {
		BaseDataQueue dataQueue;
		dataQueue = openDataQueue(dataQueueDef);
		check4TypeCast(false, dataQueue, "browseNextElement");
		DataQueueEntry dataQueueEntry;
		dataQueueEntry = null;
		try {
			dataQueueEntry = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(dataQueue, DataQueue.class).peek(wait.ezeUnbox());
		}
		catch ( java.lang.Exception eze$Temp15 )
		{
			org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp15 );
			AnyException exception;
			if ( eze$Temp15 instanceof AnyException )
			{
				exception = (AnyException)eze$Temp15;
			}
			else
			{
				exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp15);
			}
			{
				returnAS400ToPool(dataQueue);
				DataQueueDefinition eze$Temp16 = null;
				eze$Temp16 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataQueueDef, eze$Temp16);
				throwDataQueueException(exception, eze$Temp16, "browseNextElement");
			}
		}
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(dataQueueEntry, null))) {
			try {
				elementData = EAny.asAny(dataQueueEntry.getString());
				returnAS400ToPool(dataQueue);
			}
			catch ( java.lang.Exception eze$Temp18 )
			{
				org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp18 );
				AnyException exception;
				if ( eze$Temp18 instanceof AnyException )
				{
					exception = (AnyException)eze$Temp18;
				}
				else
				{
					exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp18);
				}
				{
					returnAS400ToPool(dataQueue);
					DataQueueDefinition eze$Temp19 = null;
					eze$Temp19 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataQueueDef, eze$Temp19);
					throwDataQueueException(exception, eze$Temp19, "browseNextElement");
				}
			}
		}
		return (org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(dataQueueEntry, null));
	}
	public boolean browseNextElement(DataQueueDefinition dataQueueDef, Object elementData, AnyBoxedObject<Integer> wait, Object keyData, String searchType) {
		BaseDataQueue dataQueue;
		dataQueue = openDataQueue(dataQueueDef);
		check4TypeCast(true, dataQueue, "browseNextElement");
		KeyedDataQueueEntry dataQueueEntry = null;
		try {
			dataQueueEntry = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(dataQueue, KeyedDataQueue.class).peek(EString.clip(EString.ezeCast(keyData)), wait.ezeUnbox(), EString.clip(searchType));
		}
		catch ( java.lang.Exception eze$Temp20 )
		{
			org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp20 );
			AnyException exception;
			if ( eze$Temp20 instanceof AnyException )
			{
				exception = (AnyException)eze$Temp20;
			}
			else
			{
				exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp20);
			}
			{
				returnAS400ToPool(dataQueue);
				DataQueueDefinition eze$Temp21 = null;
				eze$Temp21 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataQueueDef, eze$Temp21);
				throwDataQueueException(exception, eze$Temp21, "browseNextElement");
			}
		}
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(dataQueueEntry, null))) {
			try {
				elementData = EAny.asAny(dataQueueEntry.getString());
				returnAS400ToPool(dataQueue);
			}
			catch ( java.lang.Exception eze$Temp23 )
			{
				org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp23 );
				AnyException exception;
				if ( eze$Temp23 instanceof AnyException )
				{
					exception = (AnyException)eze$Temp23;
				}
				else
				{
					exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp23);
				}
				{
					returnAS400ToPool(dataQueue);
					DataQueueDefinition eze$Temp24 = null;
					eze$Temp24 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataQueueDef, eze$Temp24);
					throwDataQueueException(exception, eze$Temp24, "browseNextElement");
				}
			}
		}
		return (org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(dataQueueEntry, null));
	}
	public void clearQueue(DataQueueDefinition dataQueueDef) {
		BaseDataQueue dataQueue;
		dataQueue = openDataQueue(dataQueueDef);
		check4NullQueue(dataQueue, "clearQueue");
		try {
			dataQueue.clear();
			returnAS400ToPool(dataQueue);
		}
		catch ( java.lang.Exception eze$Temp25 )
		{
			org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp25 );
			AnyException exception;
			if ( eze$Temp25 instanceof AnyException )
			{
				exception = (AnyException)eze$Temp25;
			}
			else
			{
				exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp25);
			}
			{
				returnAS400ToPool(dataQueue);
				DataQueueDefinition eze$Temp26 = null;
				eze$Temp26 = org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(dataQueueDef, eze$Temp26);
				throwDataQueueException(exception, eze$Temp26, "clearQueue");
			}
		}
	}
	private BaseDataQueue openDataQueue(DataQueueDefinition dataQueueDef) {
		BaseDataQueue dataQueue = null;
		QSYSObjectPathName path;
		path = new QSYSObjectPathName(EString.clip(dataQueueDef.libname), EString.clip(dataQueueDef.qname), "DTAQ");
		dataQueueDef.path = path.getPath();
		AS400 system;
		system = eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().getAS400FromPool(dataQueueDef.systemDef);
		if (dataQueueDef.keyed) {
			dataQueue = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(new KeyedDataQueue(system, dataQueueDef.path), BaseDataQueue.class);
		}
		else {
			dataQueue = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(new DataQueue(system, dataQueueDef.path), BaseDataQueue.class);
		}
		return org.eclipse.edt.javart.util.JavartUtil.checkNullable(dataQueue);
	}
	private void returnAS400ToPool(BaseDataQueue dataQueue) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(dataQueue, null))) {
			eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().returnAS400ToPool(dataQueue.getSystem());
		}
	}
	private void throwDataQueueException(AnyException exception, DataQueueDefinition dataQueueDef, String functioName) {
		iDataAccessException newException;
		newException = new iDataAccessException();
		newException.functionName = functioName;
		newException.setMessage(((newException.getMessage()) + exception.getMessage()));
		newException.path =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(dataQueueDef.path);
		newException.exception = exception;
		throw newException;
	}
	private void check4NullQueue(BaseDataQueue queue, String functioName) {
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.equals(queue, null))) {
			iDataAccessException newException;
			newException = new iDataAccessException();
			NullValueException nullException;
			nullException = new NullValueException();
			newException.functionName = functioName;
			newException.setMessage(((newException.getMessage()) + eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().DATA_QUEUE_EXCEPTION_NOT_OPEN));
			newException.exception = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(nullException, AnyException.class);
			throw newException;
		}
	}
	private void check4TypeCast(Boolean shouldBeKeyed, BaseDataQueue queue, String functioName) {
		check4NullQueue(queue, functioName);
		if (((shouldBeKeyed && !(org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeIsa(queue, KeyedDataQueue.class))) || (!(shouldBeKeyed) && !(org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeIsa(queue, DataQueue.class))))) {
			iDataAccessException newException;
			newException = new iDataAccessException();
			TypeCastException typeCastException;
			typeCastException = new TypeCastException();
			newException.functionName = functioName;
			if (org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeIsa(queue, KeyedDataQueue.class)) {
				newException.setMessage(((newException.getMessage()) + eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().DATA_QUEUE_EXCEPTION_KEYED_NOT_KEYED));
				typeCastException.actualTypeName = eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().DATA_QUEUE_NOT_KEYED;
				typeCastException.castToName = eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().DATA_QUEUE_KEYED;
			}
			else {
				newException.setMessage(((newException.getMessage()) + eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().DATA_QUEUE_EXCEPTION_NOT_KEYED_KEYED));
				typeCastException.actualTypeName = eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().DATA_QUEUE_KEYED;
				typeCastException.castToName = eze_Lib_org_eclipse_edt_jtopen_data_common_CommonLib().DATA_QUEUE_NOT_KEYED;
			}
			newException.exception = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(typeCastException, AnyException.class);
			newException.path = queue.getPath();
			throw newException;
		}
	}
}
