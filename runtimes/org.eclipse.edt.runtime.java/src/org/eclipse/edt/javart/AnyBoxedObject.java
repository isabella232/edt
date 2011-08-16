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

import egl.lang.AnyException;
import egl.lang.DynamicAccessException;
import egl.lang.EglAny;
import egl.lang.AnyValue;

public class AnyBoxedObject<R> implements EglAny, BoxedValue {

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
	public EglAny ezeGet(String name) throws AnyException {
		if (object instanceof EglAny) {
			return ((EglAny)object).ezeGet(name);
		}
		else {
			throw new DynamicAccessException();
		}

	}

	@Override
	public void ezeInitialize() throws AnyException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String ezeName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void ezeSet(String name, Object value) throws AnyException {
		if (object instanceof EglAny) {
			((EglAny)object).ezeSet(name, value);
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
