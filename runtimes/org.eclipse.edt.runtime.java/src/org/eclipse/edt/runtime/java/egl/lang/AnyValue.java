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
package org.eclipse.edt.runtime.java.egl.lang;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;


public abstract class AnyValue extends AnyObject implements egl.lang.AnyValue { 	
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	public AnyValue() { super(); }
	
	public abstract void ezeSetEmpty();
	public abstract void ezeCopy(Object source);
	public abstract void ezeCopy(egl.lang.AnyValue source);
	public abstract <T extends egl.lang.AnyValue> T ezeNewValue(Object...args) throws JavartException;
	
	public static <V extends egl.lang.AnyValue> V ezeCopyTo(egl.lang.AnyValue source, V target) throws JavartException {
		if (source == null) {
			if (target != null) {
				target.ezeSetEmpty();
			}
		}
		else {
			if (target == null) {
				target = source.ezeNewValue();
			}
			target.ezeCopy(source);
		}
		return target;
	}
}
