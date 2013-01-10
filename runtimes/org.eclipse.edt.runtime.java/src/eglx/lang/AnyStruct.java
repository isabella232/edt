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
package eglx.lang;

import org.eclipse.edt.javart.ByteStorage;

/**
 * Abstract super class for all types that have to be able to represent their values
 * as a byte array.  This is necessary for types containing structured
 * fields that are part of structured records.
 * 
 * @author twilson
 *
 */
public interface AnyStruct extends AnyValue {
	public abstract Object defaultValue();
		
	public byte[] getBytes(); 
	
	public ByteStorage buffer(); 

	public int sizeInBytes(); 
	
	@Override
	public void ezeSetEmpty();

	public <T extends AnyStruct> T substring(int start, int end) throws AnyException; 
}
