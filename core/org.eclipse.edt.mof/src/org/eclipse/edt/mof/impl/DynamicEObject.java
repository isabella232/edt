/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.impl;

import java.util.List;

import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.eclipse.edt.mof.utils.EList;


public class DynamicEObject extends EObjectImpl implements EObject, Dynamic {
	
	@Override
	public Object eGet(String name) {
		try {
			return internalGet(name);
		}
		catch (IllegalArgumentException ex) {
			return null;
		}		
	}

	@Override
	public Object eGet(Integer index) {
		try {
			return internalGet(index);
		}
		catch (IllegalArgumentException ex) {
			return null;
		}		
	}

	@Override
	@SuppressWarnings("unchecked")
	public void eSet(String name, Object value) {
		try {
			if (value instanceof String && isTypeEncoded((String)value)) {
				value = getEncodedValue((String)value);
			}
			internalSet(name, value);
		}
		catch (IllegalArgumentException ex) {
			Slot[] newSlots = new Slot[slots.length + 1];
			System.arraycopy(slots,0, newSlots, 0, slots.length);
			slots = newSlots;
			EField field = MofFactory.INSTANCE.createEField(true);
			field.setName(name);
			Object real = value;
			if (value instanceof String && isTypeEncoded((String)value)) {
				real = getEncodedValue((String)value);
			}
			field.setEType(typeFromValue(real));
			getEClass().addMember(field);
			if (value instanceof List) {
				internalSet(field, value);
			}
			else if (real instanceof Object[]) {
				List list = new EList();
				for (Object o : (Object[])value) {
					list.add(o);
				}
				internalSet(field, list);
			}
			else {
				internalSet(field, real);
			}
		}		
	}

	@Override
	@SuppressWarnings("unchecked")
	public void eSet(Integer index, Object value) {
		try {
			internalSet(0, value);
		}
		catch (IllegalArgumentException ex) {
			Slot[] newSlots = new Slot[slots.length + 1];
			System.arraycopy(slots,0, newSlots, 0, slots.length);
			slots = newSlots;
			EField field = MofFactory.INSTANCE.createEField(true);
			field.setName("value");
			field.setEType(typeFromValue(value));
			getEClass().addMember(field);
			if (value instanceof List) {
				List list = new EList();
				list.addAll((List)value);
				internalSet(field, list);
			}
			else if (value instanceof Object[]) {
				List list = new EList();
				for (Object o : (Object[])value) {
					list.add(o);
				}
				internalSet(field, list);
			}
			else {
				internalSet(field, value);
			}
		}
	}

	private EType typeFromValue(Object value) {
		if (value instanceof EObject) {
			return ((EObject)value).getEClass();
		}
		if (value instanceof String) {
			if (isTypeEncoded((String)value)) {
				return typeFromValue(getEncodedValue((String)value));
			}
			else {
				return MofFactory.INSTANCE.getEStringEDataType();
			}
		}
		if (value instanceof Integer) {
			return MofFactory.INSTANCE.getEIntEDataType();
		}
		if (value instanceof Boolean) {
			return MofFactory.INSTANCE.getEBooleanEDataType();
		}
		if (value instanceof List || value instanceof Object[]) {
			EGenericType type = MofFactory.INSTANCE.createEGenericType(true);
			type.setEClassifier(MofFactory.INSTANCE.getEListEDataType());
			type.getETypeArguments().add(MofFactory.INSTANCE.getJavaObjectEDataType());
			return type;
		}
		return MofFactory.INSTANCE.getJavaObjectEDataType();
	}
	
	private boolean isTypeEncoded(String value) {
		return ( 
			value.startsWith(IntTypePrefix)
			|| value.startsWith(BooleanTypePrefix)
			|| value.startsWith(FloatTypePrefix)
			|| value.startsWith(MofTypePrefix)
		);
	}
	
	private Object getEncodedValue(String value) {
		int i = value.indexOf(":");
		if (i != -1) {
			String prefix = value.substring(0,i+1);
			if (prefix.equals(IntTypePrefix)) {
				return Integer.parseInt(value.substring(i+1));
			}
			if (prefix.equals(BooleanTypePrefix)) {
				return Boolean.parseBoolean(value.substring(i+1));
			}
			if (prefix.equals(FloatTypePrefix)) {
				return Float.parseFloat(value.substring(i+1));
			}
			if (prefix.equals(MofTypePrefix)) {
				try {
					return Environment.getCurrentEnv().find(value.substring(i+1));
				} catch (Exception e) {
					return null;
				}
			}
		}
		return value;
	}
}
