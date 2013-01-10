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
package org.eclipse.edt.runtime.java.eglx.lang;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;

import eglx.lang.AnyException;


public abstract class AnyValue extends EAny implements eglx.lang.AnyValue { 	
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	public AnyValue() { super(); }
	
	public abstract void ezeSetEmpty();
	public abstract void ezeCopy(Object source);
	public abstract void ezeCopy(eglx.lang.AnyValue source);
	public abstract <T extends eglx.lang.AnyValue> T ezeNewValue(Object...args) throws AnyException;
	
	public static <V extends eglx.lang.AnyValue> V ezeCopyTo(eglx.lang.AnyValue source, V target) throws AnyException {
		if (source == null)
			target = null;
		else {
			if (target == null) {
				target = source.ezeNewValue();
			}
			target.ezeCopy(source);
		}
		return target;
	}
	
	public static <V extends eglx.lang.AnyValue> V ezeCopyTo(AnyBoxedObject<? extends eglx.lang.AnyValue> source, V target) throws AnyException {
		if (source == null || source.ezeUnbox() == null)
			target = null;
		else {
			if (target == null) {
				target = source.ezeUnbox().ezeNewValue();
			}
			target.ezeCopy(source.ezeUnbox());
		}
		return target;
	}
}
