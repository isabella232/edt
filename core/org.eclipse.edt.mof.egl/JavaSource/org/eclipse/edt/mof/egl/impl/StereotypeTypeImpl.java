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
package org.eclipse.edt.mof.egl.impl;

import java.util.List;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.StereotypeType;


public class StereotypeTypeImpl extends AnnotationTypeImpl implements StereotypeType {
	private static int Slot_memberAnnotations=0;
	private static int Slot_isReferenceType=1;
	private static int Slot_partType=2;
	private static int Slot_defaultSuperType=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + AnnotationTypeImpl.totalSlots();
	}
	
	static {
		int offset = AnnotationTypeImpl.totalSlots();
		Slot_memberAnnotations += offset;
		Slot_isReferenceType += offset;
		Slot_partType += offset;
		Slot_defaultSuperType += offset;
	}
	
	@Override
	public List<EClass> getSuperTypes() {
		if (primGetSuperTypes().isEmpty()) {
			primGetSuperTypes().add(IrFactory.INSTANCE.getStereotypeEClass());
		}
		return primGetSuperTypes();
	}


	
	@SuppressWarnings("unchecked")
	@Override
	public List<AnnotationType> getMemberAnnotations() {
		return (List<AnnotationType>)slotGet(Slot_memberAnnotations);
	}

	@Override
	public boolean isReferenceType() {
		return (Boolean)slotGet(Slot_isReferenceType);
	}
	
	@Override
	public void setIsReferenceType(boolean value) {
		slotSet(Slot_isReferenceType, value);
	}
	
	@Override
	public MofSerializable getPartType() {
		return (MofSerializable)slotGet(Slot_partType);
	}

	@Override
	public void setPartType(MofSerializable eClass) {
		slotSet(Slot_partType, eClass);
	}

	@Override
	public MofSerializable getDefaultSuperType() {
		return (MofSerializable)slotGet(Slot_defaultSuperType);
	}

	@Override
	public void setDefaultSuperType(MofSerializable eClass) {
		slotSet(Slot_defaultSuperType, eClass);
	}



	public EObject newDefaultSuperType() {
		return new StereotypeImpl();
	}
	
}
