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

import java.util.*;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Delegate;

public class EglList<T> extends EglAny implements egl.lang.EglList<T> {
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	private java.util.List<T> list;
	private int maxSize;

	public EglList() {
		list = new ArrayList<T>();
	}

	public EglList(int initialSize) {
		list = new ArrayList<T>(initialSize);
		for (int i = 0; i < initialSize; i++) {
			list.add(null);
		}
	}

	// the following group of methods implements the edt version, which means that the indexes are relative to 1
	@Override
	public void appendAll(Collection<? extends T> c) {
		if (maxSize != 0 && c.size() + list.size() > maxSize)
			throw new ArraySizeException();
		list.addAll(c);
	}

	@Override
	public void appendElement(T element) {
		list.add(element);
	}

	@Override
	public T getElement(int index) {
		if (index - 1 < 0 || index - 1 >= list.size())
			throw new InvalidIndexException();
		return get(index - 1);
	}

	@Override
	public int getMaxSize() {
		return maxSize;
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public int indexOfElement(Object o) {
		return indexOfElement(o, 1);
	}

	@Override
	public int indexOfElement(Object o, int index) {
		if (index - 1 < 0 || index - 1 >= list.size())
			throw new InvalidIndexException();
		return list.subList(index - 1, list.size()).indexOf(o) + 1 + index - 1;
	}

	@Override
	public void insertElement(T element, int index) {
		if (maxSize != 0 && list.size() >= maxSize)
			throw new ArraySizeException();
		if (index == 0)
			index = 1;
		if (index - 1 < 0 || index - 1 > list.size())
			throw new InvalidIndexException();
		list.add(index - 1, element);
	}

	@Override
	public void removeAll() {
		list.clear();
	}

	@Override
	public void removeElement(int index) {
		if (index - 1 < 0 || index - 1 >= list.size())
			throw new InvalidIndexException();
		list.remove(index - 1);
	}

	@Override
	public void resize(int max) {
		if (max > maxSize || max < 0)
			throw new ArraySizeException();
		while (list.size() > max) {
			list.remove(list.size());
		}
	}

	@Override
	public void sort( final Delegate sortFunction )
	{
		Collections.sort(
				list, 
				new Comparator<T>()
				{
					public int compare( T obj1, T obj2 )
					{
						egl.lang.EglAny arg1 = obj1 instanceof egl.lang.EglAny ? (egl.lang.EglAny)obj1 : EglAny.ezeBox( obj1 );
						egl.lang.EglAny arg2 = obj2 instanceof egl.lang.EglAny ? (egl.lang.EglAny)obj2 : EglAny.ezeBox( obj2 );
						
						Object result = sortFunction.invoke( arg1, arg2 );
						if ( result instanceof Integer )
						{
							return (Integer)result;
						}
						else if ( result instanceof EInt )
						{
							return ((EInt)result).ezeUnbox();
						}
						else
						{
							return 0; //TODO throw something?
						}
					}
				} 
			);
	}

	@Override
	public void setElement(T element, int index) {
		if (index - 1 < 0 || index - 1 >= list.size())
			throw new InvalidIndexException();
		set(index - 1, element);
	}

	@Override
	public void setMaxSize(int max) {
		if (maxSize != 0 && max < maxSize)
			throw new ArraySizeException();
		maxSize = max;
	}

	// the following group of methods implements the java version, which means that the indexes are relative to 0
	@Override
	public void add(int index, T element) {
		list.add(index, element);
	}

	@Override
	public boolean add(T e) {
		return list.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return list.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return list.addAll(index, c);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public EglList<T> ezeSet(int index, T element) {
		setElement(element, index);
		return this;
	}

	@Override
	public T get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public T remove(int index) {
		return list.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public T set(int index, T element) {
		return list.set(index, element);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public java.util.List<T> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

}
