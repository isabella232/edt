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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

public class EList<T> extends AnyObject implements egl.lang.EList<T> {
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = 70L;
	
	private java.util.List<T> list;
	private int maxSize;
	
	public EList() {
		list = new ArrayList<T>();
	}
	
	public EList(int initialSize) {
		list = new ArrayList<T>(initialSize);
		for(int i = 0; i<initialSize; i++) {
			list.add(null);
		}
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	public void setMaxSize(int max) {
		maxSize = max;
	}
	
	public void appendElement(T element) {
		list.add(element);
	}
	
	public void removeElement(T element) {
		list.remove(element);
	}
	
	@Override
	public T get(int index) {
		return list.get(index);
	}
	
	@Override
	public T set(int index, T element) {
		return list.set(index, element);
	}

	@Override
	public boolean add(T e) {
		return list.add(e);
	}

	@Override
	public void add(int index, T element) {
		list.add(index, element);
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
		return remove(index);
	}

	@Override
	public boolean remove(Object o) {
		return remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return retainAll(c);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public java.util.List<T> subList(int fromIndex, int toIndex) {
		return subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}
	
	@Override
	public T getElement(int index) {
		return get(index-1);
	}
	
	@Override
	public void setElement(T element, int index) {
		set(index-1, element);
	}

	public EList<T> ezeSet(int index, T element) {
		setElement(element, index);
		return this;
	}
	
}
