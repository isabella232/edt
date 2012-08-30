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

import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeParameter;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.utils.EList;
import org.eclipse.edt.mof.utils.NameUtile;


public abstract class ClassifierImpl extends NamedElementImpl implements Classifier {
	private static int Slot_fileName=0;
	private static int Slot_hasCompileErrors=1;
	private static int Slot_packageName=2;
	private static int Slot_typeParameters=3;
	private static int totalSlots = 4;
	
	private String packageName;
	
	public static int totalSlots() {
		return totalSlots + NamedElementImpl.totalSlots();
	}
	
	static {
		int offset = NamedElementImpl.totalSlots();
		Slot_typeParameters += offset;
		Slot_fileName += offset;
		Slot_hasCompileErrors += offset;
		Slot_packageName += offset;
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
		for (Annotation ann : getAnnotations()) {
			if (ann instanceof Stereotype) return (Stereotype)ann;
		}
		return null;
	}
	
	@Override
	public String getTypeSignature() {
		if (getCaseSensitivePackageName().length() == 0) {
			return getCaseSensitiveName();
		}
		return getCaseSensitivePackageName()+"."+getCaseSensitiveName();
	}
	
	@Override
	public String getMofSerializationKey() {
		return InternUtil.intern(EGL_KeyScheme + KeySchemeDelimiter + getTypeSignature().toUpperCase().toLowerCase());
	}
	
	@Override
	public Boolean equals(Type eglType) {
		if (!(eglType instanceof Classifier)) return false;
		return this.getMofSerializationKey().equals(((Classifier)eglType).getMofSerializationKey());
//		return this == eglType;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Type) {
			return equals((Type)o);
		}
		return super.equals(o);
	}

	@Override
	public Classifier getClassifier() {
		return this;
	}
	
	@Override
	public boolean isNativeType() {
		// Default implementation
		return false;
	}

	@Override
	public boolean isInstantiable() {
		return false;
	}

	
}
