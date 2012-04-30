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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeParameter;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.impl.EEnumImpl;
import org.eclipse.edt.mof.utils.NameUtile;


public class EnumerationImpl extends EEnumImpl implements Enumeration {
	private static int Slot_annotations=0;
	private static int Slot_name=1;
	private static int Slot_filename=2;
	private static int Slot_hasCompileErrors=3;
	private static int Slot_packageName=4;
	private static int Slot_typeParameters=5;
	private static int Slot_accessKind=6;
	private static int totalSlots = 7;
	
	private List<StructPart> superTypes;
	private String name;
	private String packageName;

	
	public static int totalSlots() {
		return totalSlots + EEnumImpl.totalSlots();
	}
	
	static {
		int offset = EEnumImpl.totalSlots();
		Slot_annotations += offset;
		Slot_name += offset;
		Slot_typeParameters += offset;
		Slot_filename += offset;
		Slot_hasCompileErrors += offset;
		Slot_packageName += offset;
		Slot_accessKind += offset;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TypeParameter> getTypeParameters() {
		return (List<TypeParameter>)slotGet(Slot_typeParameters);
	}
	
	@Override
	public String getFilename() {
		return (String)slotGet(Slot_filename);
	}
	
	@Override
	public void setFilename(String value) {
		slotSet(Slot_filename, value);
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
		if (packageName == null) {
			packageName = NameUtile.getAsName(getCaseSensitivePackageName());
		}
		return packageName;
	}

	@Override
	public String getCaseSensitivePackageName() {
		return (String)slotGet(Slot_packageName);
	}
	
	@Override
	public void setPackageName(String value) {
		slotSet(Slot_packageName, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Stereotype> getStereotypes() {
		List<Stereotype> list = new ArrayList<Stereotype>();
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
	public List<EEnumLiteral> getEntries() {
		return getLiterals();
	}
	
	@Override
	public EnumerationEntry getEntry(String name) {
		for (EEnumLiteral entry : getEntries()) {
			if (entry.getName().equalsIgnoreCase(name)) {
				return (EnumerationEntry)entry;
			}
		}
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		return getPackageName() + "." + getName();
	}

	@Override
	public Stereotype getSubType() {
		return getStereotype();
	}

	@Override
	public String getFileName() {
		return getFilename();
	}

	@Override
	public void setFileName(String value) {
		setFilename(value);
		
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
	public Boolean equals(Type eglType) {
		return this == eglType;
	}

	@Override
	public Classifier getClassifier() {
		return this;
	}

	@Override
	public String getTypeSignature() {
		return getFullyQualifiedName();
	}

	@Override
	public void addMember(Member mbr) {
		if (mbr instanceof EnumerationEntry) {
			getEntries().add((EnumerationEntry)mbr);
			mbr.setContainer(this);
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public List<Member> getMembers() {
		List<Member> mbrs = new ArrayList<Member>();
		for (EEnumLiteral entry : getEntries()) 
			mbrs.add((EnumerationEntry)entry);
		return mbrs;
	}
	
	@Override
	public String getMofSerializationKey() {
		return InternUtil.intern(EGL_KeyScheme + KeySchemeDelimiter + getTypeSignature().toUpperCase().toLowerCase());
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
	public List<Member> getAllMembers() {
		return getMembers();
	}

	@Override
	public boolean isInstantiable() {
		return false;
	}

	@Override
	public List<StructPart> getSuperTypes() {
		if (superTypes == null) {
			superTypes = new ArrayList<StructPart>();
			superTypes.add((StructPart)IRUtils.getEGLType(MofConversion.Type_AnyEnumeration));
		}
		return superTypes;
	}

	@Override
	public boolean isSubtypeOf(StructPart part) {
		if (!getSuperTypes().isEmpty()) {
			for (StructPart superType : getSuperTypes()) {
				if (superType.equals(part)) {
					return true;
				}
			}
			for (StructPart superType : getSuperTypes()) {
				if (superType.isSubtypeOf(part)) return true;
			}
			return false;
		}
		else {
			return false;
		}
	}
}
