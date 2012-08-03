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

import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Type;

public class ParameterizedTypeImpl extends TypeImpl implements ParameterizedType {
	private static int Slot_parameterizableType=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + TypeImpl.totalSlots();
	}
	
	static {
		int offset = TypeImpl.totalSlots();
		Slot_parameterizableType += offset;
	}
	
	@Override
	public Classifier getClassifier() {
		return getParameterizableType();
	}
	
	@Override
	public ParameterizableType getParameterizableType() {
		return (ParameterizableType)slotGet(Slot_parameterizableType);
	}
	
	@Override
	public void setParameterizableType(ParameterizableType value) {
		slotSet(Slot_parameterizableType, value);
	}
	
	@Override
	public Boolean equals(Type eglType) {
		return eglType instanceof ParameterizedType && getClassifier().equals(eglType.getClassifier()) && typeArgsEqual(eglType);	
	}

	@Override
	public String getTypeSignature() {
		return getClassifier().getTypeSignature() + typeArgsSignature();
	}
	
	@Override
	// Returns -1 as default.  Subclasses return size as appropriate
	public int getSizeInBytes() { return -1; };
	
	String typeArgsSignature() {
		return "()";
	}
	
	/**
	 * Default implementation returns true for PrimitiveTypes that have no type args
	 * @param type
	 * @return
	 */
	public boolean typeArgsEqual(Type type) {
		return true;
	}

	@Override
	public int getMaxNumberOfParms() {
		return 1;
	}

	@Override
	public int getMinNumberOfParms() {
		return 1;
	}

}
