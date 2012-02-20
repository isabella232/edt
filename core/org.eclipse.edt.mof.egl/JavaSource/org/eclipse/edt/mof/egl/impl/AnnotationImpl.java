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
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.NullLiteral;


public class AnnotationImpl extends ElementImpl implements Annotation {
	
	@Override
	public Object getValue() {
		if (getEClass().getEFields().isEmpty()) return null;
		
		Object value = eGet(getEClass().getEFields().get(0));
		if (value instanceof NullLiteral) {
			value = null;
		}
		return value;
	}
	
	@Override
	public void setValue(Object value) {
		if (getEClass().getEFields().isEmpty()) return;
		eSet(getEClass().getEFields().get(0), value);
	}
	
	@Override
	public Object getValue(String key) {
		Object value = eGet(key);
		if (value instanceof NullLiteral) {
			value = null;
		}
		return value;
	}
	
	@Override
	public void setValue(String key,Object value) {
		eSet(key, value);
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
}
