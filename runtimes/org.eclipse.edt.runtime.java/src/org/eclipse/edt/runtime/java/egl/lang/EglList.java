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

import java.math.BigDecimal;
import java.util.*;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Delegate;
import org.eclipse.edt.javart.messages.Message;

import egl.lang.InvalidArgumentException;
import egl.lang.InvalidIndexException;
import eglx.java.JavaObjectException;

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
	 * Creates a list of the specified size, with elements initialized using 
	 * the factory.
	 */
	public EglList(int initialSize, ListElementFactory<? extends T> factory) {
		list = new ArrayList<T>(initialSize);
		for (int i = 0; i < initialSize; i++) {
			list.add( factory.newElement() );
		}
	}

	// the following group of methods implements the edt version, which means that the indexes are relative to 1
	@Override
	public EglList<T> appendAll(List<? extends T> c) {
		list.addAll(c);
		return this;
	}

	@Override
	public EglList<T> appendElement(T element) {
		list.add(element);
		return this;
	}

	@Override
	public T getElement(int index) {
		if (index < 1 || index > list.size())
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = index;
			throw ex.fillInMessage( Message.LIST_INDEX_OUT_OF_BOUNDS, index, list.size() );
		}
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
		if (index < 1 || index - 1 > list.size())
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = index;
			throw ex.fillInMessage( Message.LIST_INDEX_OUT_OF_BOUNDS, index, list.size() );
		}
		return list.subList(index - 1, list.size()).indexOf(o) + 1 + index - 1;
	}

	@Override
	public void insertElement(T element, int index) {
		if (index == 0)
			index = 1;
		if (index < 1 || index - 1 > list.size())
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = index;
			throw ex.fillInMessage( Message.LIST_INDEX_OUT_OF_BOUNDS, index, list.size() );
		}
		list.add(index - 1, element);
	}

	@Override
	public void removeAll() {
		list.clear();
	}

	@Override
	public void removeElement(int index) {
		if (index < 1 || index > list.size())
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = index;
			throw ex.fillInMessage( Message.LIST_INDEX_OUT_OF_BOUNDS, index, list.size() );
		}
		list.remove(index - 1);
	}
	
	@Override
	public void resize( int size, ListElementFactory<? extends T> factory ) 
	{
		if ( size == 0 )
		{
			list.clear();
		}
		else if ( size > list.size() )
		{
			// Add elements to the end of the list.
			if ( factory == null )
			{
				// The elements are nullable, so add nulls.
				for ( int needed = size - list.size(); needed > 0; needed-- )
				{
					list.add( null );
				}
			}
			else
			{
				// The elements aren't nullable, so add new instances using the factory.
				for ( int needed = size - list.size(); needed > 0; needed-- )
				{
					list.add( factory.newElement() );
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
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage( Message.NEGATIVE_SIZE, size );
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
		if (index < 1 || index > list.size())
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = index;
			throw ex.fillInMessage( Message.LIST_INDEX_OUT_OF_BOUNDS, index, list.size() );
		}
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

	
	/**
	 * A type of factory for making elements of Lists.  New elements are constructed
	 * by calling newInstance on the Class.
	 */
	public static class ElementFactory<EltT> implements ListElementFactory<EltT>
	{
		Class<EltT> elementClass;

		public ElementFactory( Class<EltT> elementClass )
		{
			this.elementClass = elementClass;
		}

		public EltT newElement()
		{
			try
			{
				return elementClass.newInstance();
			}
			catch ( Exception ex )
			{
				JavaObjectException jox = new JavaObjectException();
				jox.exceptionType = ex.getClass().getName();
				throw jox.fillInMessage( Message.CAUGHT_JAVA_EXCEPTION, ex );
			}
		}
	}

	/**
	 * A factory for making elements of int Lists.
	 */
	public static final ListElementFactory<Integer> IntFactory = 
			new ListElementFactory<Integer>() 
			{
				public Integer newElement()
				{
					return 0;
				}
			};
			
	/**
	 * A factory for making elements of bigint Lists.
	 */
	public static final ListElementFactory<Long> BigintFactory = 
			new ListElementFactory<Long>() 
			{
				public Long newElement()
				{
					return 0L;
				}
			};
			
	/**
	 * A factory for making elements of smallint Lists.
	 */
	public static final ListElementFactory<Short> SmallintFactory = 
			new ListElementFactory<Short>() 
			{
				public Short newElement()
				{
					return 0;
				}
			};
			
	/**
	 * A factory for making elements of boolean Lists.
	 */
	public static final ListElementFactory<Boolean> BooleanFactory = 
			new ListElementFactory<Boolean>() 
			{
				public Boolean newElement()
				{
					return Boolean.FALSE;
				}
			};
			
	/**
	 * A factory for making elements of string Lists.
	 */
	public static final ListElementFactory<String> StringFactory = 
			new ListElementFactory<String>() 
			{
				public String newElement()
				{
					return Constants.EMPTY_STRING;
				}
			};
			
	/**
	 * A factory for making elements of float Lists.
	 */
	public static final ListElementFactory<Double> FloatFactory = 
			new ListElementFactory<Double>() 
			{
				public Double newElement()
				{
					return 0.0;
				}
			};
			
	/**
	 * A factory for making elements of smallfloat Lists.
	 */
	public static final ListElementFactory<Float> SmallfloatFactory = 
			new ListElementFactory<Float>() 
			{
				public Float newElement()
				{
					return 0f;
				}
			};
			
	/**
	 * A factory for making elements of decimal Lists.
	 */
	public static final ListElementFactory<BigDecimal> DecimalFactory = 
			new ListElementFactory<BigDecimal>() 
			{
				public BigDecimal newElement()
				{
					return BigDecimal.ZERO;
				}
			};
			
	/**
	 * A factory for making elements of date Lists.
	 */
	public static final ListElementFactory<Calendar> DateFactory = 
			new ListElementFactory<Calendar>() 
			{
				public Calendar newElement()
				{
					return EDate.defaultValue();
				}
			};
			
	/**
	 * A type of factory for making elements of timestamp Lists.
	 */
	public static class TimestampFactory implements ListElementFactory<Calendar>
	{
		int startCode;
		int endCode;
		
		public TimestampFactory( int startCode, int endCode )
		{
			this.startCode = startCode;
			this.endCode = endCode;
		}
		
		public Calendar newElement()
		{
			return ETimestamp.defaultValue( startCode, endCode );
		}
	}
	
	/**
	 * A type of factory for making elements of multi-dimensional Lists.
	 */
	public static class ListFactory<EltT> implements ListElementFactory<EglList<EltT>>
	{
		int size;
		ListElementFactory<EltT> subFactory;

		public ListFactory( int size, ListElementFactory<EltT> subFactory )
		{
			this.size = size;
			this.subFactory = subFactory;
		}

		public EglList<EltT> newElement()
		{
			if ( subFactory != null )
			{
				return new EglList<EltT>( size, subFactory );
			}
			else
			{
				return new EglList<EltT>( size );
			}
		}
	}
}
