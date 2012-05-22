/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.serialization.binary;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.impl.EObjectImpl;
import org.eclipse.edt.mof.impl.NullableSlot;
import org.eclipse.edt.mof.impl.Slot;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Deserializer;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.eclipse.edt.mof.serialization.ProxyEClass;
import org.eclipse.edt.mof.serialization.ProxyEObject;
import org.eclipse.edt.mof.utils.EList;


@SuppressWarnings("unchecked")
public class BinaryDeserializer implements Deserializer {
	List constantPoolList;
	EObject[] eObjectArray;
	Map<ProxyEObject, Integer> proxies;

	InputStream inputStream;

	MofFactory factory = MofFactory.INSTANCE;
	IEnvironment env;
	
	int majorVersion ;
	int minorVersion;
	
	boolean readMinimal = false;
	boolean doInstantiateOnRead = true;

	public BinaryDeserializer(InputStream input, IEnvironment env) {
		inputStream = input;
		proxies = new HashMap<ProxyEObject, Integer>();
		this.env = env;
	}
	
	public EObject deserialize() throws DeserializationException {
		if (inputStream == null) { throw new DeserializationException("Input not set"); }
		readMagic();
		readVersion();
//		byte[] md5Key = readBytes();
		readPool();
		int size = (int)readUint4();
		eObjectArray = new EObject[size];
		EObject obj = (EObject)readObject();
		for (Entry<ProxyEObject, Integer> entry : proxies.entrySet()) {
			ProxyEObject proxy = entry.getKey();
			EObject eObject = eObjectArray[entry.getValue()];
			proxy.updateReferences(eObject);
		}
//		type.setMD5HashValue(md5Key);

		return obj;
	}
		
	private List getConstantPoolList() {
		if (constantPoolList == null) {
			constantPoolList = new ArrayList();
		}
		return constantPoolList;
	}

	private byte[] getMD5HashKey(BufferedInputStream stream) throws DeserializationException{
		inputStream = stream;
		readMagic();
		readVersion();
		return readBytes();
	}


	private void readMagic() throws DeserializationException{
		long magic = readUint4();
		if (magic != PersistenceConstants.Magic_Number) {
			
			//EGLMessage msg = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_NOT_AN_IR, null);
			throw new DeserializationException("Not a valid Type to deserialize");
		}
	}
	
	private boolean hasUint2PoolSize() {
		if (majorVersion > 7) {
			return false;
		}
		if (minorVersion > 1) {
			return false;
		}
		return true;
	}

	public long readUint4() throws DeserializationException{
		byte[] bytes = new byte[4];
		readBytes(bytes);

		long l = 0;
		l |= bytes[0] & 0xFF;
		l <<= 8;
		l |= bytes[1] & 0xFF;
		l <<= 8;
		l |= bytes[2] & 0xFF;
		l <<= 8;
		l |= bytes[3] & 0xFF;
		return l;
	}

	public int readUint2() throws DeserializationException{
		byte[] bytes = new byte[2];
		readBytes(bytes);

		int i = 0;
		i |= bytes[0] & 0xFF;
		i <<= 8;
		i |= bytes[1] & 0xFF;
		return i;
	}

	public void readBytes(byte[] bytes) throws DeserializationException{
		try {
			inputStream.read(bytes);
		} catch (IOException e) {
			throw new DeserializationException(e.getMessage(), e);
		}
	}
	
	public byte[] readBytes() throws DeserializationException{
		int len = readUint2();
		byte[] bytes = new byte[len];
		readBytes(bytes);
		return bytes;

	}

	private void readVersion() throws DeserializationException{
		
		majorVersion = readUint2();
		minorVersion = readUint2();
		
		if (majorVersion < PersistenceConstants.MajorVersion) {
			return;
		}
		
		if (majorVersion > PersistenceConstants.MajorVersion || minorVersion > PersistenceConstants.MinorVersion) {
			// EGLMessage msg = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_INCOMPATIBLE_VERSION, null, new String[] {Integer.toString(majorVersion), Integer.toString(minorVersion), Integer.toString(PersistenceConstants.MajorVersion), Integer.toString(PersistenceConstants.MinorVersion)});
			throw new DeserializationException("Incompatible version");
		}
	}

	private void readPool() throws DeserializationException{
		int length;
		if (hasUint2PoolSize()) {
			length = readUint2() - 1;
		}
		else {
			length = ((int)readUint4()) - 1;
		}
		
		for (int i = 0; i < length; i++) {
			getConstantPoolList().add(readPoolEntry());
		}
	}

	private Object readPoolEntry() throws DeserializationException{
		int kind = readUint2();

		switch (kind) {
		case PersistenceConstants.Null:
			return null;
		case PersistenceConstants.String:
			return readString();
		case PersistenceConstants.TypeReference:
			return readString();
		default:
			// EGLMessage msg = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_UNKNOWN_OBJECT_IN_POOL, null, new String[] {Integer.toString(kind)});
			throw new DeserializationException("Unknown Pool entry type");
		}
	}

		
	private String readString() throws DeserializationException{
		try {
			byte[] bytes = readBytes();
			return new String(bytes, "UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new DeserializationException(e.getMessage(), e);
		}
	}

	private Object readObjectAtPoolOffset() throws DeserializationException{
		int index = (int)readUint4();
		if (index > getConstantPoolList().size() - 1) {
			// EGLMessage msg = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_CONSTANT_POOL_INDEX_OUT_OF_RANGE, null, new String[] {Integer.toString(index)});
			throw new DeserializationException("Constant pool index access out of range: " + Integer.toString(index));
		}
		return getConstantPoolList().get(index);
	}
	
	private EObject readEObjectAtIndex() throws DeserializationException{
		int index = (int)readUint4();
		if (index > eObjectArray.length - 1) {
			// EGLMessage msg = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_CONSTANT_POOL_INDEX_OUT_OF_RANGE, null, new String[] {Integer.toString(index)});
			throw new DeserializationException("EObject list index access out of range: " + Integer.toString(index));
		}
		EObject obj = eObjectArray[index];
		if (obj == null) {
			obj = new ProxyEObject();
			eObjectArray[index] = obj;
//			((ProxyObject)obj).registerReference(eObjectArray, index);
			proxies.put((ProxyEObject)obj, index);
		}
		return obj;
	}

	private Object readObject() throws DeserializationException{
		Object obj = readObject(readUint2());
		return obj;

	}

	private Object readObject(int kind) throws DeserializationException{
		switch (kind) {
		
			case PersistenceConstants.Null:
				return null;
		
			case PersistenceConstants.PoolEntry:
				return readObjectAtPoolOffset();
			
			case PersistenceConstants.TypeReference: {
				String typeSignature = (String)readObjectAtPoolOffset();
				EObject type = null;
				try {
					type = env.find(typeSignature);
				} catch (MofObjectNotFoundException e) {
					throw new DeserializationException(e);
				}
				return type;
			}
			//Objects			
			case PersistenceConstants.EObjectReference: 
				return readEObjectAtIndex();
			
			case PersistenceConstants.EObject: {
				int index = (int)readUint4();
				Slot[] slots = (Slot[])readObject();
				EObject obj;
				if (slots[0].get() instanceof ProxyEClass) {
					ProxyEClass type = (ProxyEClass)slots[0].get();
					obj = new ProxyEObject();
					type.getProxyObjects().add((ProxyEObject)obj);
					((ProxyEObject)obj).slots = slots;
				} else {
					if (doInstantiateOnRead) {
						EClass type = (EClass)slots[0].get();
						obj = type.newInstance(false);
						((EObjectImpl)obj).setSlots(slots);
					}
					else {
						obj = null;
					}
				}
				eObjectArray[index] = obj;
				return obj;				
			}
			case PersistenceConstants.Slot: {
				Slot slot = new Slot();
				slot.set(readObject());
				return slot;
			}
			case PersistenceConstants.NullableSlot: {
				NullableSlot slot = new NullableSlot();
				slot.setIsNull((Boolean)readObjectAtPoolOffset());
				slot.set(readObject());
				return slot;
			}
			case PersistenceConstants.BigInteger:
				return new BigInteger((String)readObjectAtPoolOffset());
			case PersistenceConstants.Long:
				return new Long((String)readObjectAtPoolOffset());
			case PersistenceConstants.Integer:
				return new Integer((String)readObjectAtPoolOffset());
			case PersistenceConstants.Float:
				return new Float((String)readObjectAtPoolOffset());
			case PersistenceConstants.Double:
				return new Double((String)readObjectAtPoolOffset());
			case PersistenceConstants.BigDecimal:
				return new BigDecimal((String)readObjectAtPoolOffset());
			case PersistenceConstants.Enumeration: {
				String name = (String)readObjectAtPoolOffset();
				String entry = (String)readObjectAtPoolOffset();
				Class<Enum> enumClass;
				try {
					enumClass = (Class<Enum>)Class.forName(name);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
				return Enum.valueOf(enumClass, entry);
			}
			case PersistenceConstants.BooleanTrue:
				return Boolean.TRUE;
			case PersistenceConstants.BooleanFalse:
				return Boolean.FALSE;
			case PersistenceConstants.IntegerArray:
				return readIntegerArray();
			case PersistenceConstants.IntegerArrayArray:
				return readIntegerArrayArray();
			case PersistenceConstants.StringArray:
				return readStringArray();
			case PersistenceConstants.StringArrayArray:
				return readStringArrayArray();
			case PersistenceConstants.SlotArray:
				return readSlotArray();
			case PersistenceConstants.ObjectArray:
				return readObjectArray();
			case PersistenceConstants.List:
				return readList();
			

		default:
			throw new DeserializationException("Unknown Object type: " + Integer.toString(kind));
		}
	}

	private boolean readBoolean() throws DeserializationException{
		byte[] value = new byte[1];
		readBytes(value);
		if ((value[0] & 0xFF) == 0) {
			return false;
		}
		else {
			return true;
		}
	}
			
	private Integer[] readIntegerArray() throws DeserializationException{
		int len = readUint2();
		Integer[] arr = new Integer[len];
		for (int i = 0; i < len; i++) {
			arr[i] = new Integer((String)readObjectAtPoolOffset());
		}
		return arr;
	}

	private Integer[][] readIntegerArrayArray() throws DeserializationException{
		int len = readUint2();
		Integer[][] arr = new Integer[len][];
		for (int i = 0; i < len; i++) {
			arr[i] = (Integer[]) readIntegerArray();
		}
		return arr;
	}

	private String[] readStringArray()throws DeserializationException {
		int len = readUint2();
		String[] arr = new String[len];
		for (int i = 0; i < len; i++) {
			arr[i] = (String)readObjectAtPoolOffset();
		}
		return arr;
	}

	private String[][] readStringArrayArray()throws DeserializationException {
		int len = readUint2();
		String[][] arr = new String[len][];
		for (int i = 0; i < len; i++) {
			arr[i] = (String[]) readStringArray();
		}
		return arr;
	}

	private Slot[] readSlotArray()throws DeserializationException {
		int len = readUint2();
		Slot[] arr = new Slot[len];
		if (doInstantiateOnRead) {
			arr[0] = (Slot)readObject(); // Get the EClass
			for (int i = 1; i < len; i++) {
				Object o;
				if (readMinimal) {
					if (arr[0].get() instanceof EClass) {
						EClass eClass = (EClass)arr[0].get();
						EField eField = eClass.getEField(i);
						// TODO: Implement this flag in EField class and Bootstrap
						if (false /*eField.isExcludedOnMinimalRead()*/) {
							doInstantiateOnRead = false;
							readObject();
							doInstantiateOnRead = true;
							arr[i] = new Slot();
						}
					}
					else {				
						o = readObject();
						if (o == null) { o = new Slot(); }
						arr[i] = (Slot)o;
						if (arr[i].get() instanceof ProxyEObject) {
							((ProxyEObject)arr[i].get()).registerReference(arr, i);
						}
					}
				}
				else {
					o = readObject();
					if (o == null) { o = new Slot(); }
					arr[i] = (Slot)o;
					if (arr[i].get() instanceof ProxyEObject) {
						((ProxyEObject)arr[i].get()).registerReference(arr, i);
					}
				}
			}
		}
		else {
			for (int i = 1; i < len; i++) {
				readObject();
			}
		}
		return arr;
	}

	private Object[] readObjectArray()throws DeserializationException {
		int len = readUint2();
		Object[] arr = new Object[len];
		for (int i = 0; i < len; i++) {
			arr[i] = readObject();
		}
		return arr;
	}
	
	private List readList()throws DeserializationException {
		int len = readUint2();
		List list = new EList();
		for (int i = 0; i < len; i++) {
			Object obj = readObject();
			list.add(i, obj);
		}
		return list;
	}

	
	private int getMajorVersion() {
		return majorVersion;
	}

	private int getMinorVersion() {
		return minorVersion;
	}
	
}
