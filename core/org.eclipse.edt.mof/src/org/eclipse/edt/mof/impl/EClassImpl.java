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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EFunction;
import org.eclipse.edt.mof.EMember;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.serialization.ProxyEObject;
import org.eclipse.edt.mof.utils.EList;


public class EClassImpl extends EClassifierImpl implements EClass {
	private static final long serialVersionUID = 1L;
	
	private static int Slot_superTypes = 0;
	private static int Slot_isAbstract = 1;
	private static int Slot_isInterface = 2;
	protected static int Slot_fields = 3;
	protected static int Slot_functions = 4;
	private static int totalSlots = 5; 
	
	public static int totalSlots() {
		return totalSlots + EClassifierImpl.totalSlots();
	}
	
	static { 
		int offset = EClassifierImpl.totalSlots();
		Slot_superTypes += offset;
		Slot_isAbstract += offset;
		Slot_isInterface += offset;
		Slot_fields += offset;
		Slot_functions += offset;
	}
	
	protected Class<EObject> clazz;
	protected boolean noClazz = false;
	
	private List<EField> allFields;

	public EClassImpl() { super(); }
	
	public EClassImpl(boolean init) {
		if (init) {
			EClass eClassClass = MofFactory.INSTANCE.getEClassClass();
			eClassClass.initialize(this);
		}			
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<EClass> getSuperTypes() {
		if (slotGet(Slot_superTypes) == null) {
			slotSet(Slot_superTypes, new EList<EClass>());
		}
		return (List<EClass>)slotGet(Slot_superTypes);
	}

	/**
	 * Basic method to use to add supertypes to an EClass which will
	 * validate basic inheritance rules as well as add appropriate
	 * fields for handling multiple inheritance
	 * @param eClass
	 */
	@Override
	public void addSuperTypes(List<EClass> superTypes) {
		if (superTypes.isEmpty()) return;
		
		getSuperTypes().addAll(superTypes);
		if (superTypes.size() > 1) {
			List<EField> fields = new ArrayList<EField>();
			collectAllUniqueFields(fields);
			for (EField field : fields) {
				addMember((EField)field.clone());
			}
			allFields = null;
		}
		return;
	}

	@Override
	public boolean isAbstract() {
		if (slotGet(Slot_isAbstract) == null) {
			slotSet(Slot_isAbstract, Boolean.FALSE);
		}
		return (Boolean)slotGet(Slot_isAbstract);
	}

	@Override
	public void setIsAbstract(boolean value) {
		slotSet(Slot_isAbstract, value);
	}

	@Override
	public boolean isInterface() {
		if (slotGet(Slot_isInterface) == null) {
			slotSet(Slot_isInterface, Boolean.FALSE);
		}
		return (Boolean)slotGet(Slot_isInterface);
	}

	@Override
	public void setIsInterface(boolean value) {
		slotSet(Slot_isInterface, value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EField> getEFields() {
		if (slotGet(Slot_fields) == null) {
			slotSet(Slot_fields, new EList<EField>());
		}
		return (List<EField>)slotGet(Slot_fields);
	}
	

	
	@Override
	// Handle returning fields from super type as well
	public EField getEField(String name) {
		EField field = null;
		for (EField f : getEFields()) {
			if (f.getName().equalsIgnoreCase(name))
				return f;
		}
		if (field == null) {
			if (!getSuperTypes().isEmpty()) {
				field = getSuperTypes().get(0).getEField(name);
			}
		}
		return field;
	}
	
	@Override
	public EField getEField(int index) {
		return getAllEFields().get(index);
	}

	@Override
	public List<EField> getAllEFields() {
		List<EField> allFields = new EList<EField>();
		allFields.addAll(getEFields());
		if (!getSuperTypes().isEmpty()) {
			allFields.addAll(0, getSuperTypes().get(0).getAllEFields());
		}
		return allFields;
	}
	
	private void collectAllUniqueFields(List<EField> fields) {		
		if (getSuperTypes().isEmpty()) {
			fields.addAll(getEFields());;
		}
		else {
			List<EField> common = getAllEFields();
			for (EClass superType : getSuperTypes()) {
				for (EField field : superType.getAllEFields()) {
					if (common.indexOf(field) == -1 && fields.indexOf(field) == -1) {
						fields.add(field);
					}
				}
			}
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<EFunction> getEFunctions() {
		if (slotGet(Slot_functions) == null) {
			slotSet(Slot_functions, new EList<EFunction>());
		}
		return (List<EFunction>)slotGet(Slot_functions);
	}
	
	@Override
	public void addMember(EMember mbr) {
		mbr.setDeclarer(this);
		if (mbr instanceof EField) {
			getEFields().add((EField)mbr);
			allFields = null;
		}
		else if (mbr instanceof EFunction) {
			getEFunctions().add((EFunction)mbr);
		}
		else {
			throw new IllegalArgumentException("Object " + mbr.toString() + " is not a valid member type for " + this.toString());
		}
	}


	public int getTotalSlots() {
		int totalSlots = getEFields().size();
		if (!getSuperTypes().isEmpty()) {
			totalSlots += ((EClassImpl)getSuperTypes().get(0)).getTotalSlots();
		}
		return totalSlots;
	}
	
	@Override
	public EObject newInstance() {
		return newInstance(true, true);
	}
	
	@Override
	public EObject newInstance(boolean init) {
		return newInstance(init, true);
	}

	@Override
	public EObject newInstance(boolean init, boolean useInitialValues) {
		EObject obj = null;
		if (getClazz() == null) { 
			obj = newEObject();
		} else {
			try {
				obj = clazz.newInstance();
			} catch (Exception e) { 
				obj = newEObject();
			}
		}
		if (init) {
			initialize(obj, useInitialValues);
		}
		return obj;
	}
	
	/**
	 * Override this method is subclasses to create different EObject implementation class
	 * @return
	 */
	public EObject newEObject() {
		return new EObjectImpl();
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * This method works on the convention that the implementation package
	 * is named by extending the interface package with "impl" and that the
	 * class names are referenced by extending the Interface class name with
	 * "Impl".  Failure to follow this convention results in the method always
	 * returning null.
	 */
	protected Class<EObject> getClazz() {
		if (noClazz) return null;
		if (clazz == null) {
			try {
				String typeSignature = getImplTypeSignature();
				clazz = (Class<EObject>)Class.forName(typeSignature);
			} catch (ClassNotFoundException e) {
				for (EClass superType : getSuperTypes()) {
					clazz = ((EClassImpl)superType).getClazz();
					if (clazz != null) break;
				}
				if (clazz == null) noClazz = true;
			}
		}
		return clazz;
	}
	
	public String getImplTypeSignature() {
		return getCaseSensitivePackageName() + ".impl." + getName() + "Impl";
	}
	
	@Override
	public void initialize(EObject object) {
		initialize(object, true);
	}
	
	@Override
	public void initialize(EObject object, boolean useInitialValues) {
		((EObjectImpl)object).init(this, useInitialValues);
	}

	@Override
	public boolean isInstance(EObject object) {
		if (object == null) return true;
		if (object instanceof ProxyEObject) return true;
		if (!(object instanceof EObject)) return false;
		EClass type = (EClass)((EObject)object).getEClass();
		return ( type == this || type.isSubClassOf(this));
	}
	
	@Override
	public boolean isSubClassOf(EClass eClass) {
		if (!getSuperTypes().isEmpty()) {
			for (EClass superType : getSuperTypes()) {
				if (superType == eClass) {
					return true;
				}
			}
			for (EClass superType : getSuperTypes()) {
				if (superType.isSubClassOf(eClass)) return true;
			}
			return false;
		}
		else {
			return false;
		}
	}
}
