/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.runtime.java.egl.lang;

import java.lang.reflect.Field;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.TypeConstraints;

public class AnyException extends JavartException implements egl.lang.AnyException {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public String message;

	public AnyException() {
		super();
	}

	public AnyException(String id) {
		super(id);
	}

	public AnyException(String id, String message) {
		super(id, message);
		this.message = message;
		ezeInitialize();
	}

	public AnyException(Throwable ex) {
		super(ex);
		ezeInitialize();
	}

	public Object clone() throws CloneNotSupportedException {
		AnyException ezeClone = (AnyException) super.clone();
		return ezeClone;
	}

	@Override
	public void ezeInitialize() {}

	@Override
	public egl.lang.EglAny ezeGet(String name) throws JavartException {
		try {
			Field field = this.getClass().getField(name);
			Object value = field.get(this);
			return value instanceof egl.lang.EglAny ? (egl.lang.EglAny) value : EglAny.ezeBox(value);
		}
		catch (Exception e) {
			throw new DynamicAccessException(e);
		}
	}

	@Override
	public String ezeName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void ezeSet(String name, Object value) throws JavartException {
		try {
			Field field = this.getClass().getField(name);
			field.set(this, value);
		}
		catch (Exception e) {
			throw new DynamicAccessException(e);
		}
	}

	@Override
	public TypeConstraints ezeTypeConstraints(String fieldName) {
		return null;
	}

	@Override
	public String ezeTypeSignature() {
		return this.getClass().getName();
	}

	public String toString() {
		String msg = getLocalizedMessage();
		if (msg == null) {
			return ezeTypeSignature();
		} else {
			String typeSig = ezeTypeSignature();
			return new StringBuilder(typeSig + 1 + msg.length()).append(typeSig).append(' ').append(msg).toString();
		}
	}

	@Override
	public void setMessage(String message) {
		this.message = message;

	}

	@Override
	public String getMessage() {
		return message == null ? super.getMessage() : message;
	}

	@Override
	public AnyException ezeUnbox() {
		return this;
	}

}
