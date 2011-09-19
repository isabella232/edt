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
package egl.lang;

import java.util.Collection;

import org.eclipse.edt.javart.Delegate;

public interface EglList<T> extends EglAny, java.util.List<T> {

	public EglList<T> appendAll(Collection<? extends T> collection);

	public EglList<T> appendElement(T element);

	public T getElement(int index);

	public int getSize();

	public int indexOfElement(T element);

	public int indexOfElement(T element, int index);

	public void insertElement(T element, int index);

	public void removeAll();

	public void removeElement(int index);

	public void resize(int size, Class<T> elementClass);

	public void setElement(T element, int index);

	public void sort(Delegate sortFunction);
}
