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
package org.eclipse.edt.javart;

import org.eclipse.edt.runtime.java.egl.lang.DynamicAccessException;

import egl.lang.AnyObject;
import egl.lang.AnyValue;

public class AnyBoxedObject<R> implements AnyObject, BoxedValue {

	public R object;
	
	public AnyBoxedObject(R object) {
		this.object = object;
	}
	
	@Override
	public R ezeUnbox() {
		return object;
	}

	public void ezeCopy(R value) {
		if (object == null || !(value instanceof AnyValue)) {
			this.object = value;
		}
		else {
			((AnyValue)object).ezeCopy(value);
		}
	}

	@Override
	public AnyObject ezeGet(String name) throws JavartException {
		if (object instanceof AnyObject) {
			return ((AnyObject)object).ezeGet(name);
		}
		else {
			throw new DynamicAccessException();
		}

	}

	@Override
	public void ezeInitialize() throws JavartException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String ezeName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void ezeSet(String name, Object value) throws JavartException {
		if (object instanceof AnyObject) {
			((AnyObject)object).ezeSet(name, value);
		}
		else {
			throw new DynamicAccessException();
		}
	}

	@Override
	public TypeConstraints ezeTypeConstraints(String fieldName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String ezeTypeSignature() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
