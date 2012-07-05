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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.Delegate;
import org.eclipse.edt.javart.messages.Message;

import eglx.java.JavaObjectException;
import eglx.lang.AnyException;
import eglx.lang.InvalidArgumentException;
import eglx.lang.InvalidIndexException;
import eglx.lang.InvocationException;
import eglx.lang.TypeCastException;

public class EList<E> extends AnyBoxedObject<List<E>>
{
	/**
	 * The version ID used in serialization.
	 */
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	private String signature;

	/**
	 * Makes an EList.
	 */
	private EList( List<E> list, String signature )
	{
		super( list );
		this.signature = signature;
	}
	
	/**
	 * Creates a new empty list.
	 * 
	 * @param clazz  the type of elements, not used at run time, it's there to
	 *   satisfy the Java compiler's type-checking.
	 * @return a new list.
	 */
	public static <T> List<T> ezeNew( Class<? extends T> clazz )
	{
		return new ArrayList<T>();
	}
	
	/**
	 * Creates a new list of the specified size.  The elements will be created
	 * if the factory isn't null.
	 * 
	 * @param size     the number of elements in the new list.
	 * @param factory  a factory to make the list's elements, or null.
	 * @return a new list.
	 */
	public static <T> List<T> ezeNew( int size, ListElementFactory<? extends T> factory )
	{
		List<T> list = new ArrayList<T>( size );
		if ( factory != null )
		{
			for ( int i = 0; i < size; i++ )
			{
				list.add( factory.newElement() );
			}
		}
		else
		{
			for ( int i = 0; i < size; i++ )
			{
				list.add( null );
			}
		}
		return list;
	}
	
	/**
	 * Creates a new list containing the specified elements.
	 * 
	 * @param elements  the elements of the new list.
	 * @return a new list.
	 */
	public static <T> List<T> ezeNew( T... elements )
	{
		int size = elements == null ? 0 : elements.length;
		List<T> list = new ArrayList<T>( size );
		for ( int i = 0; i < size; i++ )
		{
			list.add( elements[ i ] );
		}
		return list;
	}
	
	/**
	 * Boxes a list.
	 */
	public static <T> EList<T> ezeBox( List<T> object, String signature )
	{
		return new EList( object, signature );
	}

	/**
	 * Tests if an object is a particular kind of list.
	 * 
	 * @param object     the object.
	 * @param signature  the signature of the list type.
	 * @return true if the object is the specified kind of list.
	 */
	public static boolean ezeIsa( Object object, String signature ) 
	{
		return object instanceof EList && ((EList)object).signature.equals( signature );
	}

	public static List ezeCast( Object value, String signature ) throws TypeCastException
	{
		if ( ezeIsa( value, signature ) )
		{
			return (List)((EList)value).ezeUnbox();
		}
		else
		{
			TypeCastException tcx = new TypeCastException();
			tcx.castToName = signature;
			Object unboxed = value instanceof eglx.lang.EAny ? ((eglx.lang.EAny)value).ezeUnbox() : value;
			tcx.actualTypeName = unboxed.getClass().getName();
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, value, tcx.actualTypeName,
					tcx.castToName );
		}
	}
	
	@Override
	public Object ezeGet( int index ) throws AnyException 
	{
		return EAny.asAny( ezeUnbox().get( index ) );
	}

	/**
	 * Adds an element to the end of a list.
	 *
	 * @param list     the list to modify.
	 * @param element  the new element.
	 * @return the list, after the new element has been added.
	 */
	public static <T> List<T> appendElement( List<T> list, T element )
	{
		list.add( element );
		return list;
	}
	
	/**
	 * Adds the elements of a list to the end of another list.
	 *
	 * @param list   the list to modify.
	 * @param other  the other list.
	 * @return the list, after the new elements have been added.
	 */
	public static <T> List<T> appendAll( List<T> list, List<? extends T> other )
	{
		list.addAll( other );
		return list;
	}

	/**
	 * Inserts an element into a list at a specified position.  Any elements
	 * at or beyond the specified index are shifted toward the end.
	 *
	 * The index may be any valid position within the list.  It may also be 
	 * zero, indicating that the new element goes at the front, or it may be one
	 * more than the current size, indicating that the new element goes at the 
	 * end (as if appendElement had been called). 
	 *
	 * @param list     the list to modify.
	 * @param element  the new element.
	 * @param index    where the element should be added.
	 * @throws InvalidIndexException  if the index is negative or greater than 1 plus the size.
	 */
	public static <T> void insertElement( List<T> list, T element, int index )
	{
		if ( index == 0 )
		{
			index = 1;
		}
		if ( index < 1 || index - 1 > list.size() )
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = index;
			throw ex.fillInMessage( Message.LIST_INDEX_OUT_OF_BOUNDS, index, list.size() );
		}
		list.add( index - 1, element );
	}
	
	/**
	 * Removes the element at a specified position from a list.  Any elements
	 * at or beyond the specified index are shifted toward the front.
	 *
	 * @param list   the list to modify.
	 * @param index  where the element should be removed.
	 * @throws InvalidIndexException  if the index isn't a valid subscript.
	 */
	public static void removeElement( List list, int index )
	{
		if ( index < 1 || index > list.size() )
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = index;
			throw ex.fillInMessage( Message.LIST_INDEX_OUT_OF_BOUNDS, index, list.size() );
		}
		list.remove( index - 1 );
	}
	
	/**
	 * Removes all elements from the list.
	 * 
	 * @param list   the list to modify.
	 */
	public static void removeAll( List list )
	{
		list.clear();
	}
	
	/**
	 * Sets the value of an element.
	 *
	 * @param list   the list to modify.
	 * @param value  the value for the specified element.
	 * @param index  the index of the element to be updated. 
	 * @throws InvalidIndexException  if the index isn't a valid subscript.
	 */
	public static <T> void setElement( List<T> list, T value, int index )
	{
		if ( index < 1 || index > list.size() )
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = index;
			throw ex.fillInMessage( Message.LIST_INDEX_OUT_OF_BOUNDS, index, list.size() );
		}
		list.set( index - 1, value );
	}

	/**
	 * Returns the current size of a list.
	 *
	 * @param list  the list.
	 * @return the current size of a list.
	 */
	public static int getSize( List list )
	{
		return list.size();
	}
	
	/**
	 * Sorts the elements of a list in order from smallest to largest.
	 *
	 * @param list          the list to sort.
	 * @param sortFunction  a function capable of comparing two elements.
	 * @throws InvocationException when the sort function can't be called.
	 */
	public static <T> void sort( List<T> list, final Delegate sortFunction )
	{
		Collections.sort(
				list, 
				new Comparator<T>()
				{
					public int compare( T obj1, T obj2 )
					{
						Object result = sortFunction.invoke( obj1, obj2 );
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

	/**
	 * Returns the index of the first ocurrance of the specified value within 
	 * a list.
	 * 
	 * @param list   the list.
	 * @param value  the value to find.
	 * @return the index of the value, or 0 if it was not found.
	 */
	public static <T> int indexOfElement( List<T> list, T value )
	{
		return list.indexOf( value ) + 1;
	}

	/**
	 * Returns the index of the first ocurrance of the specified value within 
	 * a list.  The search begins at the specified index and stops at the
	 * end of the list.
	 * 
	 * @param list   the list.
	 * @param value  the value to find.
	 * @param index  the index of the element to examine first. 
	 * @return the index of the value, or 0 if it was not found.
	 * @throws InvalidIndexException  if the index isn't a valid subscript.
	 */
	public static <T> int indexOfElement( List<T> list, T value, int index )
	{
		if ( index < 1 || index - 1 > list.size() )
		{
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = index;
			throw ex.fillInMessage( Message.LIST_INDEX_OUT_OF_BOUNDS, index, list.size() );
		}
		return list.subList( index - 1, list.size() ).indexOf( value ) + index;
	}

	/**
	 * Changes the size of a list.  If the new size is greater than the current
	 * size, new elements are added at the end of the list.  If the new size is
	 * smaller than the current size, elements at the end are removed. 
	 *
	 * @param list  the list to modify.
	 * @param size  the new size.
	 * @throws InvalidSizeException  if the specified size is negative.
	 * @return the resized list.
	 */
	public static <T> List<T> resize( List<T> list, int size, ListElementFactory<? extends T> factory )
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
		
		return list;
	}
	
	/**
	 * API of objects that can create elements for Lists.
	 */
	public interface ListElementFactory<EltT>
	{
		public EltT newElement();
	}

	/**
	 * A type of factory for making elements of Lists.  New elements are constructed
	 * by calling newInstance on the Class.
	 */
	public static class ElementFactory<EltT> implements ListElementFactory<EltT>
	{
		Class<? extends EltT> elementClass;

		public ElementFactory( Class<? extends EltT> elementClass )
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
				jox.initCause( ex );
				throw jox.fillInMessage( Message.CAUGHT_JAVA_EXCEPTION, ex );
			}
		}
	}

	/**
	 * A factory for making elements of int Lists.
	 */
	public static final ListElementFactory<byte[]> BytesFactory = 
			new ListElementFactory<byte[]>() 
			{
				public byte[] newElement()
				{
					return new byte[0];
				}
			};
			
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
					return "";
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
	 * A factory for making elements of date Lists.
	 */
	public static final ListElementFactory<Calendar> TimeFactory = 
			new ListElementFactory<Calendar>() 
			{
				public Calendar newElement()
				{
					return ETime.defaultValue();
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
	public static class ListFactory<EltT> implements ListElementFactory<List<EltT>>
	{
		int size;
		ListElementFactory<EltT> subFactory;

		public ListFactory( int size, ListElementFactory<EltT> subFactory )
		{
			this.size = size;
			this.subFactory = subFactory;
		}

		public List<EltT> newElement()
		{
			ArrayList<EltT> element = new ArrayList<EltT>( size + 10 );
			
			if ( subFactory != null )
			{
				for ( int i = 0; i < size; i++ )
				{
					element.add( subFactory.newElement() );
				}
			}
			else
			{
				for ( int i = 0; i < size; i++ )
				{
					element.add( null );
				}
			}
			
			return element;
		}
	}
}
