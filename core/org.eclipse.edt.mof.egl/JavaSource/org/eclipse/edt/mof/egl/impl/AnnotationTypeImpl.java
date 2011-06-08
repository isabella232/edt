/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeParameter;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.impl.EClassImpl;
import org.eclipse.edt.mof.utils.EList;


public class AnnotationTypeImpl extends EClassImpl implements AnnotationType {
	private static int Slot_targets=0;
	private static int Slot_annotations=1;
	private static int Slot_name=2;
	private static int Slot_fileName=3;
	private static int Slot_hasCompileErrors=4;
	private static int Slot_packageName=5;
	private static int Slot_typeParameters=6;
	private static int Slot_accessKind=7;
	private static int totalSlots = 8;
	
	public static int totalSlots() {
		return totalSlots + EClassImpl.totalSlots();
	}
	
	static {
		int offset = EClassImpl.totalSlots();
		Slot_targets += offset;
		Slot_annotations += offset;
		Slot_name += offset;
		Slot_typeParameters += offset;
		Slot_fileName += offset;
		Slot_hasCompileErrors += offset;
		Slot_packageName += offset;
		Slot_accessKind += offset;
	}
	
	@Override
	public List<EClass> getSuperTypes() {
		if (super.getSuperTypes().isEmpty()) {
			super.getSuperTypes().add(IrFactory.INSTANCE.getAnnotationEClass());
		}
		return super.getSuperTypes();
	}

	protected List<EClass> primGetSuperTypes() {
		return super.getSuperTypes();
	}
	
	@Override
	/**
	 * Overridden here as AnnotationTypes should be treated as EGL types and their serialization
	 * key should follow the EGL Key Scheme.
	 */
	public String getMofSerializationKey() {
		return InternUtil.intern(EGL_KeyScheme + KeySchemeDelimiter + getTypeSignature().toUpperCase().toLowerCase());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ElementKind> getTargets() {
		return (List<ElementKind>)slotGet(Slot_targets);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Annotation> getAnnotations() {
		return (List<Annotation>)slotGet(Slot_annotations);
	}
	
	@Override
	public String getName() {
		return (String)slotGet(Slot_name);
	}
	
	@Override
	public void setName(String value) {
		slotSet(Slot_name, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TypeParameter> getTypeParameters() {
		return (List<TypeParameter>)slotGet(Slot_typeParameters);
	}
	
	@Override
	public String getFileName() {
		return (String)slotGet(Slot_fileName);
	}
	
	@Override
	public void setFileName(String value) {
		slotSet(Slot_fileName, value);
	}
	
	@Override
	public Boolean hasCompileErrors() {
		return (Boolean)slotGet(Slot_hasCompileErrors);
	}
	
	@Override
	public void setHasCompileErrors(Boolean value) {
		slotSet(Slot_hasCompileErrors, value);
	}
	
	@Override
	public String getPackageName() {
		return (String)slotGet(Slot_packageName);
	}
	
	@Override
	public void setPackageName(String value) {
		slotSet(Slot_packageName, value);
	}
		
	@Override 
	public List<Stereotype> getStereotypes() {
		List<Stereotype> list = new EList<Stereotype>();
		for (Annotation ann : getAnnotations()) {
			if (ann instanceof Stereotype) list.add((Stereotype)ann);
		}
		return list;
	}

	@Override
	public Stereotype getStereotype() {
		return getStereotypes().isEmpty() ? null : getStereotypes().get(0);
	}
	
	@Override
	public AccessKind getAccessKind() {
		return (AccessKind)slotGet(Slot_accessKind);
	}
	
	@Override
	public void setAccessKind(AccessKind value) {
		slotSet(Slot_accessKind, value);
	}
	
	@Override
	public Boolean equals(Type eglType) {
		return this == eglType;
	}
	
	@Override
	public Classifier getClassifier() {
		return this;
	}
	
	@Override
	public String getTypeSignature() {
		return getPackageName()+"."+getName();
	}

	@Override
	public String getFullyQualifiedName() {
		return getETypeSignature();
	}

	@Override
	public Stereotype getSubType() {
		return getStereotype();
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
	public boolean isNativeType() {
		return false;
	}
	
	@Override
	public boolean isInstantiable() {
		return false;
	}
	
}
