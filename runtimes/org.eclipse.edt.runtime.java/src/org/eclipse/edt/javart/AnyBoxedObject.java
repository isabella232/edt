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

import java.util.List;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;

import eglx.lang.AnyException;
import eglx.lang.AnyValue;
import eglx.lang.DynamicAccessException;
import eglx.lang.TypeCastException;

public class AnyBoxedObject<R> implements eglx.lang.EAny, BoxedValue {

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
	public Object ezeGet(String name) throws AnyException {
		if (object instanceof EAny) {
			return ((EAny)object).ezeGet(name);
		}
		else {
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = name;
			throw dax.fillInMessage( Message.DYNAMIC_ACCESS_FAILED, name, this );
		}
	}

	@Override
	public Object ezeGet(int index) throws AnyException {
		Object unboxed = ezeUnbox();
		if (unboxed instanceof List)
			return EAny.asAny(((List<?>) unboxed).get(index));
		else {
			TypeCastException tcx = new TypeCastException();
			tcx.castToName = "list";
			tcx.actualTypeName = unboxed.getClass().getName();
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, unboxed, tcx.actualTypeName,
					tcx.castToName );
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
		if (object instanceof EAny) {
			((EAny)object).ezeSet(name, value);
		}
		else {
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = name;
			throw dax.fillInMessage( Message.DYNAMIC_ACCESS_FAILED, name, this );
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
		return super.clone();
	}

}
