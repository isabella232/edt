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


import java.util.List;

import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EFactory;
import org.eclipse.edt.mof.ETypeParameter;
import org.eclipse.edt.mof.utils.EList;


@SuppressWarnings("unchecked")
public class EClassifierImpl extends ENamedElementImpl implements EClassifier {
	private static final long serialVersionUID = 1L;
	
	private static int Slot_packageName = 0;
	private static int Slot_eTypeParameters = 1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + ENamedElementImpl.totalSlots();
	}
	
	static { 
		int offset = ENamedElementImpl.totalSlots();
		Slot_packageName += offset;
		Slot_eTypeParameters += offset;
	}
		
	private byte[] md5HashValue;
	
	protected byte[] getMD5HashValue() {
		return md5HashValue;
	}
	protected void setMD5HashValue(byte[] value) {
		md5HashValue = value;
	}
	
	@Override
	public String getPackageName() {
		return (String)slotGet(Slot_packageName);
	}

	@Override
	public void setPackageName(String name) {
		slotSet(Slot_packageName, name);
	}
				
	@Override
	public String getETypeSignature() {
		String typeSignature;
		String pkgName = getPackageName();
		if (pkgName == null || pkgName.equals(""))
			typeSignature = getName();
		else 
			typeSignature = pkgName + "." + getName();
		return typeSignature;
	}
		
	@Override
	public String getMofSerializationKey() {
		return getETypeSignature();
	}
	
	public EFactory getEFactory() {
		EFactory factory = EFactory.Registry.INSTANCE.get(getPackageName());
		if (factory == null) {
			factory = new EFactoryImpl();
			factory.setPackageName(getPackageName());
			EFactory.Registry.INSTANCE.put(getPackageName(), factory);
		}
		return factory;
	}
	
	@Override
	public List<ETypeParameter> getETypeParameters() {
		if (slotGet(Slot_eTypeParameters) == null) {
			slotSet(Slot_eTypeParameters ,new EList<ETypeParameter>());
		}
		return (List<ETypeParameter>) slotGet(Slot_eTypeParameters);
	}
	
	@Override
	public EClassifier getEClassifier() {
		return this;
	}
	
	@Override
	public String toStringHeader() {
		StringBuffer signature = new StringBuffer();
		signature.append(super.toStringHeader() + " - " + getETypeSignature());
		if (!getETypeParameters().isEmpty()) {
			signature.append('<');
			int size = getETypeParameters().size();
			for (int i=0; i<size; i++) {
				ETypeParameter type = getETypeParameters().get(i);
				signature.append(type.getName());
				if (i < size-1) signature.append(", ");
			}
			signature.append('>');
		}
		return signature.toString();
	}

	
}
