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
package eglx.lang;

public interface AnyValue extends EAny { 	
		
	public abstract void ezeSetEmpty();
	public abstract void ezeCopy(Object source);
	public abstract void ezeCopy(AnyValue source);
	public abstract <T extends AnyValue> T ezeNewValue(Object...args) throws AnyException;
	
}
