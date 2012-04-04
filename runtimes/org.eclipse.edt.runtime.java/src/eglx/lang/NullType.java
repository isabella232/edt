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

import org.eclipse.edt.javart.AnyBoxedObject;

public interface NullType extends EAny { 	
		
	public <R extends Object> Boolean equals(AnyBoxedObject<R> s1, AnyBoxedObject<R> s2);
	public <R extends Object> Boolean notEquals(AnyBoxedObject<R> s1, AnyBoxedObject<R> s2);
	public NullType asNullType(EAny source);
	
}
