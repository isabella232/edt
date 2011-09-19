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

import egl.lang.InvalidArgumentException;
import egl.lang.InvalidIndexException;

public class EglList<T> extends EglAny implements egl.lang.EglList<T> {
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	private java.util.List<T> list;

	/**
	 * Creates an empty list.
	 */
	public EglList() {
		list = new ArrayList<T>();
	}

	/**
	 * Creates a list of the specified size, with null elements.
	 */
	public EglList(int initialSize) {
		list = new ArrayList<T>(initialSize);
		for (int i = 0; i < initialSize; i++) {
			list.add(null);
		}
	}

	/**
	 * Creates a list of the specified size, with elements initialized by using
	 * the default constructor of the specified Class.
	 */
	public EglList(int initialSize, Class<T> elementClass) {
		list = new ArrayList<T>(initialSize);
		for (int i = 0; i < initialSize; i++) {
			try
			{
				list.add( elementClass.newInstance() );
			}
			catch ( Exception ex )
			{
				throw new InvalidArgumentException();
			}
		}
	}

	// the following group of methods implements the edt version, which means that the indexes are relative to 1
	@Override
	public Collection<? extends T> appendAll(Collection<? extends T> c) {
		list.addAll(c);
		return list;
	}

	@Override
	public Collection<? extends T> appendElement(T element) {
		list.add(element);
		return list;
	}

	@Override
	public T getElement(int index) {
		if (index - 1 < 0 || index - 1 >= list.size())
			throw new InvalidIndexException();
		return get(index - 1);
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
		if (index - 1 < 0 || index - 1 > list.size())
			throw new InvalidIndexException();
		return list.subList(index - 1, list.size()).indexOf(o) + 1 + index - 1;
	}

	@Override
	public void insertElement(T element, int index) {
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
	public void resize( int size, Class<T> elementClass ) 
	{
		if ( size == 0 )
		{
			list.clear();
		}
		else if ( size > list.size() )
		{
			// Add elements to the end of the list.
			if ( elementClass == null )
			{
				// The elements are nullable, so add nulls.
				for ( int needed = size - list.size(); needed > 0; needed-- )
				{
					list.add( null );
				}
			}
			else
			{
				// The elements aren't nullable, so add new instances using the
				// class's default constructor.
				for ( int needed = size - list.size(); needed > 0; needed-- )
				{
					try
					{
						list.add( elementClass.newInstance() );
					}
					catch ( Exception ex )
					{
						throw new InvalidArgumentException();
					}
				}
			}
		}
		else if ( size > 0 )
		{
			// Remove elements from the end of the list.
			for ( int index = list.size() - 1; index >= size; index-- )
			{
				list.remove( index );
			}
		}
		else
		{
			throw new InvalidArgumentException();
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
							return 0; // This shouldn't happen.
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
