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
package org.eclipse.edt.mof.utils;

import java.util.ArrayList;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.impl.InternalEObject;
import org.eclipse.edt.mof.serialization.ProxyEObject;


public class EList<E> extends ArrayList<E> implements Cloneable {
	private static final long serialVersionUID = 1L;
	
	public EList() {
		super();
	}
	public EList(int size) {
		super(size);
	}
	
	boolean isReadOnly = false;

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	@Override
	public boolean add(E arg0) {
		if (isReadOnly) throw new UnsupportedOperationException();
		boolean result = super.add(arg0);
		if (arg0 instanceof ProxyEObject) {
			((ProxyEObject)arg0).registerReference(this, this.size()-1);
		}
		return result;
	}

	@Override
	public void add(int arg0, E arg1) {
		if (isReadOnly) throw new UnsupportedOperationException();
		super.add(arg0, arg1);
		if (arg1 instanceof ProxyEObject) {
			((ProxyEObject)arg1).registerReference(this, arg0);
		}
	}
	
	
	
	@Override
	public E remove(int arg0) {
		if (isReadOnly) throw new UnsupportedOperationException();
		return super.remove(arg0);
	}

	@Override
	public boolean remove(Object arg0) {
		if (isReadOnly) throw new UnsupportedOperationException();
		return super.remove(arg0);
	}

	@Override
	protected void removeRange(int arg0, int arg1) {
		if (isReadOnly) throw new UnsupportedOperationException();
		super.removeRange(arg0, arg1);
	}

	@Override
	@SuppressWarnings("unchecked")
	public EList clone() {
		EList cloned = new EList();
		for (Object obj : this) {
			cloned.add(InternalEObject.cloneIfNeeded(obj));
		}
		return cloned;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('[');
		for (int i=0; i<this.size(); i++) {
			Object obj = this.get(i);
			if (obj instanceof EObject) {
				buffer.append(((EObject)obj).toStringHeader());
			}
			else {
				buffer.append(obj.toString());
			}
			if (i < this.size()-1) {
				buffer.append(", ");
			}
		}
		buffer.append(']');
		return buffer.toString();
	}

}
