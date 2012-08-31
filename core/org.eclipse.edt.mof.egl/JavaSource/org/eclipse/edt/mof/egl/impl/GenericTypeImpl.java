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

import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeParameter;


public class GenericTypeImpl extends TypeImpl implements GenericType {
	private static int Slot_classifier=0;
	private static int Slot_typeArguments=1;
	private static int Slot_typeParameter=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + TypeImpl.totalSlots();
	}
	
	static {
		int offset = TypeImpl.totalSlots();
		Slot_classifier += offset;
		Slot_typeArguments += offset;
		Slot_typeParameter += offset;
	}
	@Override
	public Classifier getClassifier() {
		return (Classifier)slotGet(Slot_classifier);
	}
	
	@Override
	public void setClassifier(Classifier value) {
		slotSet(Slot_classifier, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Type> getTypeArguments() {
		return (List<Type>)slotGet(Slot_typeArguments);
	}
	
	
	@Override
	public void addTypeArgument(Type typeArg) {
		// TODO: Handle type checking against the TypeParameters if desired
		getTypeArguments().add(typeArg);
	}

	@Override
	public TypeParameter getTypeParameter() {
		return (TypeParameter)slotGet(Slot_typeParameter);
	}
	
	@Override
	public void setTypeParameter(TypeParameter value) {
		slotSet(Slot_typeParameter, value);
	}

	@Override
	public Boolean equals(Type eglType) {
		boolean isValid = 
				(eglType instanceof GenericType) && 
				(
					(getTypeParameter() != null && getClassifier() == null) || 
					(getClassifier().equals(eglType.getClassifier()) && getTypeArguments().size() == ((GenericType)eglType).getTypeArguments().size())
				);
		if (isValid) {
			if (getTypeParameter() != null && getClassifier() == null) {
				isValid = ((GenericType)eglType).getTypeParameter() != null && getTypeParameter().getName().equalsIgnoreCase(((GenericType)eglType).getTypeParameter().getName());
			}
			else {
				for (int i=0; i<getTypeArguments().size(); i++) {
					isValid = getTypeArguments().get(i).equals(((GenericType)eglType).getTypeArguments().get(i));
					if (!isValid) break;
				}
			}
		}
		return isValid;
	}

	@Override
	public String getTypeSignature() {
		StringBuffer signature = new StringBuffer();
		if (getTypeParameter() != null && getClassifier() == null) {
			signature.append('<');
			signature.append(getTypeParameter().getName());
			signature.append('>');
		}
		else {
			if (getClassifier() != null) {
				signature.append(getClassifier().getTypeSignature());
			}
			else {
				signature.append("null");
			}
			if (!getTypeArguments().isEmpty()) {
				signature.append('<');
				int size = getTypeArguments().size();
				for (int i=0; i<size; i++) {
					Type type = getTypeArguments().get(i);
					signature.append(modifySignature(type.getTypeSignature()));
					if (i < size-1) signature.append(TypeArgDelimiter);
				}
				signature.append('>');
			}
		}
		return signature.toString();
	}
	
	public String modifySignature(String typeSignature) {
		// Default implementation does nothing
		// Subclasses can override this method to add
		// specific modifications to the typeSignature
		return typeSignature;
	}

	@Override
	public Type resolveTypeParameter(Type type) {
		if (type instanceof GenericType) {
			GenericType generic = (GenericType)type;
			if (!generic.getTypeArguments().isEmpty()) {
				return generic.getTypeArguments().get(0);
			}
		}
		return type;
	}
	
}
