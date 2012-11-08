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

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ArrayLiteral;
import org.eclipse.edt.mof.egl.BooleanLiteral;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FloatingPointLiteral;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Literal;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PrimitiveTypeLiteral;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.TextTypeLiteral;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeParameter;
import org.eclipse.edt.mof.impl.EClassImpl;
import org.eclipse.edt.mof.impl.EObjectImpl;
import org.eclipse.edt.mof.utils.EList;
import org.eclipse.edt.mof.utils.NameUtile;


public class AnnotationTypeImpl extends EClassImpl implements AnnotationType {
	private static int Slot_targets=0;
	private static int Slot_validationClass=1;
	private static int Slot_annotations=2;
	private static int Slot_name=3;
	private static int Slot_fileName=4;
	private static int Slot_hasCompileErrors=5;
	private static int Slot_packageName=6;
	private static int Slot_typeParameters=7;
	private static int Slot_accessKind=8;
	private static int totalSlots = 9;
	
	private String packageName;
	private String name;
	
	public static int totalSlots() {
		return totalSlots + EClassImpl.totalSlots();
	}
	
	static {
		int offset = EClassImpl.totalSlots();
		Slot_targets += offset;
		Slot_validationClass += offset;
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
		return NameUtile.getAsName(EGL_KeyScheme + KeySchemeDelimiter + getTypeSignature().toUpperCase().toLowerCase());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ElementKind> getTargets() {
		return (List<ElementKind>)slotGet(Slot_targets);
	}
	
	@Override
	public String getValidationProxy() {
		return (String)slotGet(Slot_validationClass);
	}

	@Override
	public void setValidationProxy(String className) {
		slotSet(Slot_validationClass, className);
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
		if (getCaseSensitivePackageName().length() == 0) {
			return getCaseSensitiveName();
		}
		return getCaseSensitivePackageName()+"."+getCaseSensitiveName();
	}

	@Override
	public String getFullyQualifiedName() {
		return getETypeSignature();
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
	
	@Override
	public void initialize(EObject object, boolean useInitialValues) {
		super.initialize(object, useInitialValues);
		
		if (!useInitialValues) {
			return;
		}
		
		if (!(object instanceof EObjectImpl)) {
			return;
		}
		
		for (EField field : getEFields()) {
			if (field.getEType() instanceof EEnum && field.getInitialValue() instanceof Name) {
				EEnumLiteral lit = ((EEnum)field.getEType()).getEEnumLiteral(((Name)field.getInitialValue()).getId());
				((EObjectImpl)object).slotSet(field, lit);
			}
			else if (field.getInitialValue() instanceof Literal) {
				((EObjectImpl)object).slotSet(field, getLiteralValue((Literal)field.getInitialValue()));
			}
		}

	}
	
	private Object getLiteralValue(Literal lit) {
		if (lit instanceof BooleanLiteral) {
			return ((BooleanLiteral)lit).booleanValue();
		}
		
		if (lit instanceof NullLiteral) {
			return null;
		}
		
		if (lit instanceof TextTypeLiteral) {
			return ((TextTypeLiteral)lit).getValue();
		}
		
		if (lit instanceof IntegerLiteral) {
			return new Integer(((IntegerLiteral)lit).getValue());
		}
		
		if (lit instanceof FloatingPointLiteral) {
			return new Float(((FloatingPointLiteral)lit).getValue());
		}
		
		if (lit instanceof PrimitiveTypeLiteral) {
			return ((PrimitiveTypeLiteral)lit).getObjectValue();
		}
		
		if (lit instanceof ArrayLiteral) {
			EList<Object> list = new EList<Object>();
			for (Expression exp : ((ArrayLiteral)lit).getEntries()) {
				if (exp instanceof Literal) {
					list.add(getLiteralValue((Literal)exp));
				}
				else {
					list.add(exp);
				}
			}
			return list;
		}
		return null;
		
	}

	@Override
	public Element resolveElement() {
		return this;
	}

	@Override
	public Part resolvePart() {
		return this;
	}
	
}
