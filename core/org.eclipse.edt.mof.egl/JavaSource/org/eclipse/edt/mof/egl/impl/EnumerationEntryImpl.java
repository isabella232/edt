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
package org.eclipse.edt.mof.egl.impl;

import java.util.List;

import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.EMemberContainer;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.impl.EEnumLiteralImpl;
import org.eclipse.edt.mof.utils.NameUtile;


public class EnumerationEntryImpl extends EEnumLiteralImpl implements EnumerationEntry {
	private static int Slot_annotations=0;
	private static int Slot_name=1;
	private static int Slot_type=2;
	private static int Slot_isNullable=3;
	private static int Slot_IsStatic=4;
	private static int Slot_isAbstract=5;
	private static int Slot_AccessKind=6;
	private static int Slot_Container=7;
	private static int totalSlots = 8;
	
	private String name;
	
	public static int totalSlots() {
		return totalSlots + EEnumLiteralImpl.totalSlots();
	}
	
	static {
		int offset = EEnumLiteralImpl.totalSlots();
		Slot_annotations += offset;
		Slot_name += offset;
		Slot_type += offset;
		Slot_isNullable += offset;
		Slot_IsStatic += offset;
		Slot_isAbstract += offset;
		Slot_AccessKind += offset;
		Slot_Container += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Annotation> getAnnotations() {
		return (List<Annotation>)slotGet(Slot_annotations);
	}
	
	@Override
	public String getName() {
		if (name == null) {
			name = NameUtile.getAsName(getCaseSensitiveName());
		}
		return name;
	}
	
	@Override
	public String getCaseSensitiveName() {
		return (String)slotGet(Slot_name);
	}
	
	@Override
	public void setName(String value) {
		slotSet(Slot_name, value);
	}
		
	@Override
	public Type getType() {
		return (Type)getContainer();
	}

	@Override
	public void setType(Type value) {
		slotSet(Slot_type, value);
	}
	
	@Override
	public boolean isNullable() {
		return (Boolean)slotGet(Slot_isNullable);
	}
	
	@Override
	public void setIsNullable(boolean value) {
		slotSet(Slot_isNullable, value);
	}
	
	@Override
	public Boolean IsStatic() {
		return (Boolean)slotGet(Slot_IsStatic);
	}
	
	@Override
	public void setIsStatic(Boolean value) {
		slotSet(Slot_IsStatic, value);
	}
	
	@Override
	public Boolean isAbstract() {
		return (Boolean)slotGet(Slot_isAbstract);
	}
	
	@Override
	public void setIsAbstract(Boolean value) {
		slotSet(Slot_isAbstract, value);
	}
	
	@Override
	public AccessKind getAccessKind() {
		return (AccessKind)slotGet(Slot_AccessKind);
	}
	
	@Override
	public void setAccessKind(AccessKind value) {
		slotSet(Slot_AccessKind, value);
	}
	
	@Override
	public Container getContainer() {
		return (Container)slotGet(Slot_Container);
	}
	
	@Override
	public EMemberContainer getDeclarer() {
		EMemberContainer decl = super.getDeclarer();
		if (decl == null) {
			decl = (EEnum)getContainer();
		}
		return decl;
	}
	
	@Override
	public void setContainer(Container value) {
		slotSet(Slot_Container, value);
	}

	@Override
	public Boolean isStatic() {
		return true;
	}

	@Override
	public String getId() {
		return getName();
	}

	@Override
	public void addAnnotation(Annotation ann) {
		getAnnotations().add(ann);
	}

	@Override
	public Annotation getAnnotation(String name) {
		for (int i=getAnnotations().size()-1; i>=0; i--) {
			Annotation ann = getAnnotations().get(i);
			if (ann.getEClass().getETypeSignature().equalsIgnoreCase(name)) {
				return ann;
			}
		}
		return null;
	}

	@Override
	public Annotation getAnnotation(AnnotationType type) {
		for (int i=getAnnotations().size()-1; i>=0; i--) {
			Annotation ann = getAnnotations().get(i);
			if (ann.getEClass().equals(type)) {
				return ann;
			}
		}
		return null;
	}

	@Override
	public void removeAnnotation(Annotation ann) {
		getAnnotations().remove(ann);
		
	}

	@Override
	public Element resolveElement() {
		return this;
	}

	@Override
	public Member resolveMember() {
		return this;
	}
	
}
