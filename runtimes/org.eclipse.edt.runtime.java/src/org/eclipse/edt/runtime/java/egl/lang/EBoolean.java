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

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Executable;
import org.eclipse.edt.javart.JavartException;

public class EBoolean extends AnyBoxedObject<Boolean> {
	private static final long serialVersionUID = 80L;
	public static final boolean DefaultValue = false;

		
	private EBoolean(Boolean value) { super(value); }
	
	public static EBoolean ezeBox(Boolean value) {
		return new EBoolean(value);
	}

	public static Boolean ezeCast(Object value) throws JavartException {
		return (Boolean)AnyObject.ezeCast(value, "asBoolean", EBoolean.class, null, null);
	}
	
	public static boolean ezeIsa(Object value) {
		return value instanceof EBoolean;
	}

	public static boolean asBoolean(Executable program, Number number) throws JavartException {
		if (number == null) {
			throw new NullValueException();
		}
		return number.floatValue() != 0;
	}
	
}
