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
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.impl.DynamicEObject;


public class AnnotationDynamicImpl extends DynamicEObject implements Annotation {

	@Override
	public Object getValue() {
		return eGet(0);
	}

	@Override
	public Object getValue(String key) {
		return eGet(key);
	}

	@Override
	public void setValue(Object value) {
		eSet(0, value);
	}

	@Override
	public void setValue(String key, Object value) {
		eSet(key, value);
	}

	@Override
	public void addAnnotation(Annotation ann) {
		getAnnotations().add(ann);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Annotation> getAnnotations() {
		return (List<Annotation>)eGet("annotations");
	}

	@Override
	public EMetadataObject getMetadata(String typeName) {
		return getAnnotation(typeName);
	}

	@Override
	public EMetadataObject getMetadata(EClass annType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<EMetadataObject> getMetadataList() {
		throw new UnsupportedOperationException();
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

}
