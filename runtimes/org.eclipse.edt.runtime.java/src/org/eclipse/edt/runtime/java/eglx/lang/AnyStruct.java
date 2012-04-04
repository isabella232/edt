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
package org.eclipse.edt.runtime.java.eglx.lang;

import java.io.Serializable;

import org.eclipse.edt.javart.ByteStorage;
import org.eclipse.edt.javart.Constants;

import eglx.lang.AnyException;

import org.eclipse.edt.javart.util.JavartUtil;

/**
 * Abstract super class for all types that have to be able to represent their values
 * as a byte array.  This is necessary for types used in stand alone fields or structured
 * fields that are part of structured records.
 * 
 * @author twilson
 *
 */
public abstract class AnyStruct extends AnyValue implements Serializable {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	private static final byte ZERO = (byte)0;

	protected ByteStorage buffer;
	
	protected int offset;
	
	protected int sizeInBytes;
			
	protected AnyStruct() {
		super();
	}
	
	/**
	 * Constructor for creating a value whose content is a ByteStorage buffer
	 * @param sizeInBytes
	 */
	protected AnyStruct(int sizeInBytes) {
		this.offset = 0;
		this.sizeInBytes = sizeInBytes;
	}
	
	/**
	 * Create new structure but sharing the buffer from some other structure
	 * @param buffer
	 * @param offset
	 * @param length
	 */
	protected AnyStruct(ByteStorage buffer, int offset, int sizeInBytes) {
		this.buffer = buffer;
		this.offset = offset;
		this.sizeInBytes = sizeInBytes;
	}

	public abstract Object defaultValue();
		
	public byte[] getBytes() {
		return buffer.getBytes();
	}
	
	public ByteStorage buffer() {
		return buffer;
	}

	public int sizeInBytes() {
		return sizeInBytes;
	}
	
	@Override
	public void ezeSetEmpty() {
		byte[] bytes = buffer.getBytes();
		for(int i=offset; i<sizeInBytes; i++) {
			bytes[i] = ZERO;
		}
	}

	@Override
	public void ezeCopy(eglx.lang.AnyValue src) {
		AnyStruct source = (AnyStruct)src;
		int count = source.sizeInBytes() > sizeInBytes() ? sizeInBytes() : source.sizeInBytes();
		java.lang.System.arraycopy(source, source.offset, getBytes(), offset, count);
	}
	
	@Override
	public void ezeCopy(Object source) {
		ezeCopy((AnyStruct)source);
		
	}

	public <T extends AnyStruct> T substring(int start, int end) throws AnyException {
		JavartUtil.checkSubstringIndices( start, end, sizeInBytes-offset);
		T newValue = ezeNewValue();
		newValue.buffer = buffer;
		newValue.offset = start;
		return newValue;
	}

	@Override
	public <T extends eglx.lang.AnyValue> T ezeNewValue(Object... args) {
		// TODO Auto-generated method stub
		return null;
	}
}
