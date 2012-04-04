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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.impl.EObjectImpl;
import org.eclipse.edt.mof.impl.NullableSlot;
import org.eclipse.edt.mof.impl.Slot;
import org.eclipse.edt.mof.serialization.SerializationException;
import org.eclipse.edt.mof.serialization.Serializer;


@SuppressWarnings("unchecked")
public class BinarySerializer implements Serializer {

	HashMap constantPoolMap;
	List constantPoolList;
	
	HashMap<EObject, Integer> eObjectMap;
	List<EObject> eObjectList;

	OutputStream outputStream;

	ByteArrayOutputStream byteStream;

	public void serialize(EObject object) throws SerializationException {
		byteStream = new ByteArrayOutputStream();
		outputStream = new BufferedOutputStream(byteStream);
		writeUint2(PersistenceConstants.EObject);
		writeEObject(object);
		byte[] objectBytes = getContents();
		byteStream = new ByteArrayOutputStream();
		outputStream = new BufferedOutputStream(byteStream);
		primSerialize(objectBytes);
	}
	
	private List getConstantPoolList() {
		if (constantPoolList == null) {
			constantPoolList = new ArrayList();
			// The first entry in the pool list is reserved for null!
			constantPoolList.add(null);
		}
		return constantPoolList;
	}

	private HashMap getConstantPoolMap() {
		if (constantPoolMap == null) {
			constantPoolMap = new HashMap();
		}
		return constantPoolMap;
	}

	private List<EObject> getEObjectList() {
		if (eObjectList == null) {
			eObjectList = new ArrayList<EObject>();
		}
		return eObjectList;
	}

	private HashMap<EObject, Integer> getEObjectMap() {
		if (eObjectMap == null) {
			eObjectMap = new HashMap<EObject, Integer>();
		}
		return eObjectMap;
	}
	
	private void primSerialize(byte[] partBytes) throws SerializationException {
		writeMagic();
		writeVersion();
//		writeBytesWithLength(type.getMD5HashValue());
		writePool();
		writeUint4(eObjectList.size());
		writeBytes(partBytes);
	}

	private void writeVersion() throws SerializationException {
		writeUint2(PersistenceConstants.MajorVersion);
		writeUint2(PersistenceConstants.MinorVersion);
	}

	private void writePool() throws SerializationException {

		int size = getConstantPoolList().size();
		if (isLargerThanUint4(size)) {
			// TODO: EGLMessage msg = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_CONSTANT_POOL_TOO_BIG, null);
			throw new SerializationException("Constant pool too large");
		}

		writeUint4(size + 1);

		Iterator i = getConstantPoolList().iterator();

		while (i.hasNext()) {

			Object obj = i.next();

			if (obj == null) {
				writeUint2(PersistenceConstants.Null);
				continue;
			}

			if (obj instanceof String) {
				writeString((String) obj);
				continue;
			}
			
		}

	}

	private void writeMagic() throws SerializationException {
		writeUint4(PersistenceConstants.Magic_Number);
	}

	private void writeString(String string) throws SerializationException {
		try {
			writeUint2(PersistenceConstants.String);
			writeBytesWithLength(string.getBytes("UTF8"));
		} catch (UnsupportedEncodingException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}
	
	private void writeUint2(int integer) throws SerializationException {
		byte[] buf = new byte[2];

		buf[0] = (byte) ((integer & 0x0000FF00) >> 8);
		buf[1] = (byte) (integer & 0x000000FF);

		writeBytes(buf);
	}

	private void writeUint4(long aLong) throws SerializationException {
		byte[] buf = new byte[4];

		buf[0] = (byte) ((aLong & 0xFF000000L) >> 24);
		buf[1] = (byte) ((aLong & 0x00FF0000L) >> 16);
		buf[2] = (byte) ((aLong & 0x0000FF00L) >> 8);
		buf[3] = (byte) (aLong & 0x000000FFL);

		writeBytes(buf);
	}

	public byte[] getContents() throws SerializationException {
		try {
			outputStream.flush();
			byte[] bytes = byteStream.toByteArray();
			outputStream.close();
			return bytes;
		} catch (IOException e) {
			throw new SerializationException(e.getMessage(), e);
		}

	}

	private void writeBytes(byte[] bytes) throws SerializationException {
		try {
			outputStream.write(bytes);
		} catch (IOException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	private void writeBytesWithLength(byte[] bytes) throws SerializationException {
		writeUint2(bytes.length);
		writeBytes(bytes);
	}

	private void writePoolIndex(Object obj) throws SerializationException {
		writeUint4(getPoolIndex(obj));
	}

	private int getPoolIndex(Object object) {

		if (object == null) {
			return 0;
		}

		Integer i = (Integer) getConstantPoolMap().get(object);
		if (i != null) {
			return i.intValue();
		}

		int len = getConstantPoolList().size();
		getConstantPoolList().add(object);
		getConstantPoolMap().put(object, new Integer(len));
		
		return len;
	}
	
	
	private void writeEObject(EObject object) throws SerializationException {
		Integer i = getEObjectMap().get(object);
		if (i == null) {
			i = getEObjectList().size();
			getEObjectMap().put((EObject)object, getEObjectList().size());
			eObjectList.add((EObject)object);
		}
		writeUint4(i);
		writeEObjectSlots((EClass)object.getEClass(), ((EObjectImpl)object).getSlots());
	}

	private void writeObject(Object object) throws SerializationException {
		writeObject(object, false);
	}
	
	private void writeObject(Object object, boolean containment) throws SerializationException {

		if (object == null) {
			writeUint2(PersistenceConstants.Null);
			return;
		}
		if (object instanceof EObject) {
			if ( containment ) {
				writeUint2(PersistenceConstants.EObject);
				writeEObject((EObject)object);
			}
			else {
				Integer i = getEObjectMap().get((EObject)object);
				if (i == null) {
					if (object instanceof MofSerializable) {
						writeUint2(PersistenceConstants.TypeReference);
						String key = ((MofSerializable)object).getMofSerializationKey();
						writePoolIndex(key);
						return;
					}
					i = getEObjectList().size();
					eObjectList.add((EObject)object);
					getEObjectMap().put((EObject)object, i);
				}
				writeUint2(PersistenceConstants.EObjectReference);
				writeUint4(i);
			}
			return;
		}
		
		if (object instanceof Slot) {
			if (object instanceof NullableSlot) {
				writeUint2(PersistenceConstants.NullableSlot);
				writeObject(((Slot)object).isNull(), containment);
			} else {
				writeUint2(PersistenceConstants.Slot);
			}
			writeObject(((Slot)object).get(), containment);
			return;
		}

		if (object instanceof String) {
			writeUint2(PersistenceConstants.PoolEntry);
			writePoolIndex(object);
			return;
		}

		if (object instanceof BigInteger) {
			writeUint2(PersistenceConstants.BigInteger);
			writePoolIndex(((BigInteger) object).toString());
			return;
		}

		if (object instanceof Long) {
			writeUint2(PersistenceConstants.Long);
			writePoolIndex(((Long) object).toString());
			return;
		}

		if (object instanceof Integer) {
			writeUint2(PersistenceConstants.Integer);
			writePoolIndex(((Integer) object).toString());
			return;
		}

		if (object instanceof Float) {
			writeUint2(PersistenceConstants.Float);
			writePoolIndex(((Float) object).toString());
			return;
		}

		if (object instanceof Double) {
			writeUint2(PersistenceConstants.Double);
			writePoolIndex(((Double) object).toString());
			return;
		}

		if (object instanceof BigDecimal) {
			writeUint2(PersistenceConstants.BigDecimal);
			writePoolIndex(((BigDecimal) object).toString());
			return;
		}

		if (object instanceof Boolean) {
			if (((Boolean) object).booleanValue()) {
				writeUint2(PersistenceConstants.BooleanTrue);
			} else {
				writeUint2(PersistenceConstants.BooleanFalse);
			}
			return;
		}
		
		if (object instanceof Enum) {
			writeUint2(PersistenceConstants.Enumeration);
			writePoolIndex(((Enum)object).getClass().getName());
			writePoolIndex(((Enum)object).name());
			return;
		}
		
		if (object instanceof Integer[]) {
			writeUint2(PersistenceConstants.IntegerArray);
			writeIntegerArray((Integer[]) object);
			return;
		}

		if (object instanceof Integer[][]) {
			writeUint2(PersistenceConstants.IntegerArrayArray);
			writeIntegerArrayArray((Integer[][]) object);
			return;
		}

		if (object instanceof String[]) {
			writeUint2(PersistenceConstants.StringArray);
			writeStringArray((String[]) object);
			return;
		}

		if (object instanceof String[][]) {
			writeUint2(PersistenceConstants.StringArrayArray);
			writeStringArrayArray((String[][]) object);
			return;
		}

		if (object instanceof Slot[]) {
			writeUint2(PersistenceConstants.SlotArray);
			writeObjectArray((Object[]) object);
			return;
		}
		
		if (object instanceof Object[]) {
			writeUint2(PersistenceConstants.ObjectArray);
			writeObjectArray((Object[]) object);
			return;
		}

		if (object instanceof List) {
			writeUint2(PersistenceConstants.List);
			writeList((List)object, containment);
			return;
		}

		// TODO handle any other types that are in the pool!

		System.err.println("PRH - object = " + object.getClass().toString());
		writeObject("UNKNOWN SERIALIZATION TYPE: " + object.getClass());

		return;

	}
	
	private void writeEObjectSlots(EClass eClass, Slot[] slots) {
		writeUint2(PersistenceConstants.SlotArray);
		writeUint2(slots.length);
		for (int i = 0; i < slots.length; i++) {
			Slot slot = slots[i];
			EField field = eClass.getEField(i);
			if (!field.isTransient()) {
				writeObject(slot, field.getContainment());
			} 
			else {
				if (field.getEClass() instanceof EDataType) {
					writeObject(((EDataType)field.getEClass()).getDefaultValue());
				}
				else {
					writeObject(null);
				}
			}
		}		
	}

	private void writeIntegerArray(Integer[] arr) throws SerializationException {
		writeUint2(arr.length);
		for (int i = 0; i < arr.length; i++) {
			writePoolIndex(arr[i].toString());
		}
	}

	private void writeIntegerArrayArray(Integer[][] arr) throws SerializationException {
		writeUint2(arr.length);
		for (int i = 0; i < arr.length; i++) {
			writeIntegerArray(arr[i]);
		}
	}

	private void writeStringArray(String[] arr) throws SerializationException {
		writeUint2(arr.length);
		for (int i = 0; i < arr.length; i++) {
			writePoolIndex(arr[i]);
		}
	}

	private void writeStringArrayArray(String[][] arr) throws SerializationException {
		writeUint2(arr.length);
		for (int i = 0; i < arr.length; i++) {
			writeStringArray(arr[i]);
		}
	}

	private void writeObjectArray(Object[] arr) throws SerializationException {
		writeUint2(arr.length);
		for (int i = 0; i < arr.length; i++) {
			writeObject(arr[i]);
		}
	}     
   
	private void writeList(List list, boolean containment) {
		writeUint2(list.size());
		for (int i = 0; i < list.size(); i++) {
			writeObject(list.get(i), containment);
		}
	}
	
	private boolean isLargerThanUint4(int anInt) {
		return (anInt & 0xF0000000) != 0;
	}
}
