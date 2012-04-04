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
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.impl.EObjectImpl;


public abstract class ElementImpl extends EObjectImpl implements Element {
	private static int Slot_annotations=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + EObjectImpl.totalSlots();
	}
	
	static {
		int offset = EObjectImpl.totalSlots();
		Slot_annotations += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Annotation> getAnnotations() {
		return (List<Annotation>)slotGet(Slot_annotations);
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
	public void addAnnotation(Annotation ann) {
		getAnnotations().add(ann);
	}
	
	@Override
	public void removeAnnotation(Annotation ann) {
		getAnnotations().remove(ann);
	}
}
