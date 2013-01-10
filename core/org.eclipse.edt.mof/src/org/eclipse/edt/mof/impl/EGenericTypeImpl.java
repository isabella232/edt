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

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.utils.EList;


public class EGenericTypeImpl extends EModelElementImpl implements EGenericType {
	private static final long serialVersionUID = 1L;
	
	private static int Slot_eClassifier = 0;
	private static int Slot_eTypeArguments = 1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + EModelElementImpl.totalSlots();
	}
	
	static { 
		int offset = EModelElementImpl.totalSlots();
		Slot_eClassifier += offset;
		Slot_eTypeArguments += offset;
	}

	@Override
	public EClassifier getEClassifier() {
		return (EClassifier)slotGet(Slot_eClassifier);
	}
	
	@Override
	public void setEClassifier(EClassifier type) {
		slotSet(Slot_eClassifier, type);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EType> getETypeArguments() {
		if (slotGet(Slot_eTypeArguments) == null) {
			slotSet(Slot_eTypeArguments, new EList());
		}
		return (List<EType>)slotGet(Slot_eTypeArguments);
	}


	@Override
	public String getETypeSignature() {
		StringBuffer signature = new StringBuffer();
		signature.append(getEClassifier().getETypeSignature());
		if (!getETypeArguments().isEmpty()) {
			signature.append('<');
			int size = getETypeArguments().size();
			for (int i=0; i<size; i++) {
				EType type = getETypeArguments().get(i);
				signature.append(type.getETypeSignature());
				if (i < size-1) signature.append(":");
			}
			signature.append('>');
		}
		return signature.toString();
	}
	
	@Override
	public String getMofSerializationKey() {
		StringBuffer signature = new StringBuffer();
		signature.append(getEClassifier().getETypeSignature());
		if (!getETypeArguments().isEmpty()) {
			signature.append('<');
			int size = getETypeArguments().size();
			for (int i=0; i<size; i++) {
				EType type = getETypeArguments().get(i);
				signature.append(type.getMofSerializationKey());
				if (i < size-1) signature.append(":");
			}
			signature.append('>');
		}
		return signature.toString();
	}

	@Override
	public boolean addETypeArgument(EType argument) {
		int parmSize = getEClassifier().getETypeParameters().size();
		int argsSize = getETypeArguments().size();
		if (argsSize >= parmSize) {
			throw new IllegalArgumentException("Too many type arguments for type " + getETypeSignature());
		}
		else {
			// TODO: this is not all the checking that should be done
			List<EType> parmTypeBounds = getEClassifier().getETypeParameters().get(argsSize).getBounds();
			if (parmTypeBounds != null && !parmTypeBounds.isEmpty()) {
				EType parmType = parmTypeBounds.get(0);
				if (argument == parmType || argument.equals(parmType) || (
						argument instanceof EClass && 
						parmType instanceof EClass && 
						((EClass)argument).isSubClassOf((EClass)parmType))) {
					getETypeArguments().add(argument);
					return true;
				}
			}
			else {
				getETypeArguments().add(argument);
				return true;
			}
		}
		return false;
	}

}
