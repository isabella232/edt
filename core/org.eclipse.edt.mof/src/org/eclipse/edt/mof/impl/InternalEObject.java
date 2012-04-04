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
package org.eclipse.edt.mof.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EVisitor;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.serialization.ProxyEObject;
import org.eclipse.edt.mof.utils.EList;


public class InternalEObject implements Cloneable {
	
	private static class Cloner {
		static Map<InternalEObject, InternalEObject> alreadyCloned = null;
		static Stack<InternalEObject> cloneStack = new Stack<InternalEObject>();
		
		static InternalEObject clone(InternalEObject obj) {
			if (alreadyCloned == null) {
				alreadyCloned = new HashMap<InternalEObject, InternalEObject>(); 
			}
			InternalEObject cloned = alreadyCloned.get(obj);
			if (cloned == null) {
				cloned = (InternalEObject)obj.internalEType().newInstance(false);
				alreadyCloned.put(obj, cloned);
				obj.primClone(cloned);
			}
			return cloned;
		}
		
		static void cloneFinished() {
			cloneStack.pop();
			if (cloneStack.isEmpty()) {
				alreadyCloned = null;
			}
		}
		
		static void cloneStart(InternalEObject obj) {
			cloneStack.push(obj);
		}
	}
	
	protected Slot[] slots;
	
	Object internalGet(EField field) {
		int index = calculateIndex(field);
		if (slots[index] == null) {
			slots[index] = (field.isNullable()) ? new NullableSlot() : new Slot();
		}
		return slots[index].get();
	}
	
	void internalSet(EField field, Object value) {
		int index = calculateIndex(field);
		if (slots[index] == null) {
			slots[index] = field.isNullable() ? new NullableSlot() : new Slot();
		}
		slotSet(index, value);
		
//		if (isValidToSet(field, value)) {
//			slotSet(index, value);
//		} else {
//			throw new IllegalArgumentException("Object type " + value.getClass().getName() + " not valid for field " + field.getName());
//		}
	}
	
	Object internalGet(String name) {
		EField field = ((EClass)internalEType()).getEField(name);
		if (field != null) {
			return internalGet(field);
		}
		else {
			throw new IllegalArgumentException("No such field named: " + name + " in EClass " + this.internalEType().getETypeSignature());
		}
	}

	void internalSet(String name, Object value) {
		EField field = ((EClass)internalEType()).getEField(name);
		if (field != null) {
			internalSet(field, value);
		}
		else {
			throw new IllegalArgumentException("No such field named: " + name + " in EClass " + this.internalEType().getETypeSignature());
		}
	}
	
	Object internalGet(Integer index) {
		EField field = ((EClass)internalEType()).getEFields().get(index);
		return internalGet(field);
	}

	void internalSet(Integer index, Object value) {
		if (index >= internalEType().getEFields().size()) {
			throw new IllegalArgumentException("No field at index: " + index);
		}
		EField field = internalEType().getEFields().get(index);
		internalSet(field, value);
	}
	
	boolean internalIsNull(EField field) {
		int i = calculateIndex(field);
		return slots[i].isNull();
	}
	
	EClass internalEType() {
		return (EClass)slots[0].value;
	}
	
	void init(EClassImpl eClass) {
		if (getSlots() == null) {
			setSlots(new Slot[eClass.getTotalSlots()]);
		}
		for (EField field : eClass.getEFields()) {
			if (field.getEType() != null) {
				EClassifier type = field.getEType().getEClassifier();
				if (field.getInitialValue() != null) {
					slotSet(field, field.getInitialValue());
				}
				else if (type instanceof EDataType && !field.isNullable()) {
					Object defaultValue = ((EDataType)type).getDefaultValue();
					slotSet(field, defaultValue);
				}
			}
		}
		if (!eClass.getSuperTypes().isEmpty()) {
			init((EClassImpl)eClass.getSuperTypes().get(0));
		}
		slotSet(0, eClass);
	}

	public int calculateIndex(EField field) {
		Integer I = ((EFieldImpl)field).slotIndex;
		if (I == null) {
			EClass type = (EClass)field.getDeclarer();
			int i = type.getEFields().indexOf(field);
			if (type instanceof EClass) {
				EClassImpl superType = (EClassImpl)
					((((EClass)type).getSuperTypes().isEmpty()) 
							? null 
							: ((EClass)type).getSuperTypes().get(0));
				if ( superType != null ) {
					i += superType.getTotalSlots();
				}
			}
			((EFieldImpl)field).slotIndex = i;
			return i;
		}
		else return I;
	}

	public Object slotGet(int i) {
		if (slots[i] == null) {
			slots[i] = new Slot();
		}
		return slots[i].get();
	}
	
	public void slotSet(int i, Object value) {
		if (slots[i] == null) {
			slots[i] = new Slot();
		}
		slots[i].set(value);
		if (value instanceof ProxyEObject) {
			((ProxyEObject)value).registerReference(slots, i);
		}
	}
	
	public void slotSet(EField field, Object value) {
		int i = calculateIndex(field);
		if (slots[i] == null) {
			slots[i] = field.isNullable() ? new NullableSlot() : new Slot();
		}
		slots[i].set(value);
	}
	
	public Slot[] getSlots() {
		return slots;
	}
	
	public void setSlots(Slot[] slots) {
		this.slots = slots;
	}
	
	public void accept(EVisitor visitor) {
		boolean visitChildren = ((AbstractVisitor)visitor).primVisit(this);
		if (visitChildren) {
			visitChildren(visitor);
		}
		((AbstractVisitor)visitor).primEndVisit(this);
	}
	
	public void visitChildren(EVisitor visitor) {
		int i = -1;
		boolean isTracking = visitor.isTrackingParent();
		if (visitor.isTrackingParent()) {
			visitor.pushParent((EObject)this);
		}
		for (Slot slot : slots) {
			i++;
			// bypass the first slot which contains the type of the EObject being visited
			if (i == 0) {
				continue;
			}
			if (slot != null && slot.value != null) {
				if (slot.value instanceof EObject && slot.value != this) {
					if (isTracking) {
						visitor.pushSlotIndex(i);
					}
					((EObject)slot.value).accept(visitor);
					if (isTracking) {
						visitor.popSlotIndex();
					}
			}
				else if (slot.value instanceof List) {
					if (isTracking) 
						visitor.pushParent(slot.value);
					int j = 0;
					for (Object obj : (List)slot.value) {
						if (obj != null && obj instanceof EObject && obj != this) {
							if (isTracking)
								visitor.pushSlotIndex(j);
							((EObject)obj).accept(visitor);
							if (isTracking)
								visitor.popSlotIndex();
						}
						j++;
					}
					if (isTracking) 
						visitor.popParent();
				}
			}
		}
		if (visitor.isTrackingParent()) {
			visitor.popParent();
		}
	}
	
	// TODO: Expensive operation 
	@SuppressWarnings("unused")
	private boolean isValidToSet(EField field, Object value) {
		if (value == null) return true;
		boolean isValid = false;
		EClassifier type = field.getEType().getEClassifier();
		
		if (value instanceof EEnumLiteral) {
			isValid = ((EEnumLiteral)value).getDeclarer().equals(type);
		}
		else if (value instanceof EObject) {
			isValid = ((EObject)value).getEClass().isInstance((EObject)value);
		}
		else {
			if (value instanceof List) {
				isValid = (((EDataType)type).getJavaClassName().equals("java.util.List"));
			}
			else {
				isValid = ((EDataType)type).getJavaClassName().equals(value.getClass().getName())
					|| ((EDataType)type).getJavaClassName().equals("java.lang.Object");
			}
		}
		return isValid;
	}
	
	public Object clone() {
		Cloner.cloneStart(this);
		Object obj = Cloner.clone(this);
		Cloner.cloneFinished();
		return obj;
	}
	
	public static Object cloneIfNeeded(Object value) {
		// References to MofSerializable should not be cloned
		if (value instanceof MofSerializable) {
			InternalEObject hasClone = Cloner.alreadyCloned.get(value);
			if (hasClone != null) {
				return hasClone;
			}
			else {
				return value;
			}
		}
		else if (value instanceof EObject) {
			return ((EObject)value).clone();
		}
		else if (value instanceof EList) {
			return ((EList)value).clone();
		}
		else {
			return value;
		}
	}
	
	private InternalEObject primClone(InternalEObject cloned) {
		cloned.slots = new Slot[slots.length];
		for (int i=0; i<slots.length; i++) {
			cloned.slots[i] = slots[i] == null ? new Slot() : (Slot)slots[i].clone();
			Object value = slotGet(i);
			cloned.slots[i].set(cloneIfNeeded(value));
		}
		return cloned;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (slots == null) {
			buffer.append("Invalid EObject - no slots allocated");
			return buffer.toString();
		}
		buffer.append(toStringHeader());
		buffer.append("\n\t");
		EClass type = internalEType();
		if (type == null) {
			buffer.append("Invalid EObject - no EClass specified");
		}
		else {
			for (int i=0; i<slots.length; i++) {
				if (type.getEField(i) != null)
					buffer.append(type.getEField(i).getName());
				else {
					buffer.append("Error - null Field");
				}
				buffer.append(" -> ");
				Object value = slotGet(i);
				if (value instanceof EObject)
					buffer.append(((EObject)value).toStringHeader());
				else
					buffer.append(value == null ? "null" : value.toString());
				buffer.append("\n\t");
			}
		}
		return buffer.toString();
	}
	
	public String toStringHeader() {
		return "Instance of: " + internalEType().getETypeSignature();
	}


}
