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

import java.util.List;

import org.eclipse.edt.javart.Delegate;

public interface EList<T> extends EAny, List<T> {

	public EList<T> appendAll(List<? extends T> list);

	public EList<T> appendElement(T element);

	public T getElement(int index);

	public int getSize();

	public int indexOfElement(T element);

	public int indexOfElement(T element, int index);

	public void insertElement(T element, int index);

	public void removeAll();

	public void removeElement(int index);

	public void resize(int size, ListElementFactory<? extends T> factory);

	public void setElement(T element, int index);

	public void sort(Delegate sortFunction);

	/**
	 * API of objects that can create elements for Lists.
	 */
	public interface ListElementFactory<EltT>
	{
		public EltT newElement();
	}
}
