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

import java.lang.reflect.Method;
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
		return object instanceof EList && ((EList)object).ezeUnbox() != null && ((EList)object).signature.equals( signature );
	}

	public static List ezeCast( Object value, String signature, Object... args ) throws TypeCastException
	{
		if ( value instanceof EList )
		{
			if ( ((EList)value).signature.equals( signature ) )
			{
				return copy( (List)((EList)value).ezeUnbox(), signature );
			}
			else
			{
				value = ((EList)value).ezeUnbox();
			}
		}
		
		if ( value == null )
		{
			return null;
		}
		else if ( value instanceof List )
		{
			List list = (List)value;
			if ( signatureMatches( list, signature ) )
			{
				return copy( list, signature );
			}
			else if ( args.length > 0 && args[ 0 ] == EAny.class )
			{
				return copyAndBoxElements( list, signature );
			}
			else if ( signature.startsWith( "eglx.lang.EList<" ) )
			{
				return copyAndCastElements( list, signature, args );
			}
		}
		
		TypeCastException tcx = new TypeCastException();
		tcx.castToName = signature;
		Object unboxed = value instanceof eglx.lang.EAny ? ((eglx.lang.EAny)value).ezeUnbox() : value;
		tcx.actualTypeName = unboxed.getClass().getName();
		throw tcx.fillInMessage( Message.CONVERSION_ERROR, value, tcx.actualTypeName,
				tcx.castToName );
	}
	
	private static boolean signatureMatches( List list, String signature )
	{
		if ( signature.startsWith( "eglx.lang.EList<" ) )
		{
			if ( list.isEmpty() )
			{
				return true;
			}
			else
			{
				String elementSig = signature.substring( 16, signature.length() - 1 );
				return signatureMatches( list.get( 0 ), elementSig );
			}
		}
		
		return false;
	}
	
	private static boolean signatureMatches( Object obj, String signature )
	{
		if ( obj == null )
		{
			// TODO When the object is null, we don't know its type.  Assume it's a match. 
			return true;
		}
		else if ( obj instanceof eglx.lang.EAny && signature.equals( "eglx.lang.EAny" ) )
		{
			return true;
		}
		else if ( obj instanceof List )
		{
			return signatureMatches( (List)obj, signature );
		}
		else if ( obj instanceof Integer )
		{
			return signature.equals( "eglx.lang.EInt" );
		}
		else if ( obj instanceof Long )
		{
			return signature.equals( "eglx.lang.EBigint" );
		}
		else if ( obj instanceof Short )
		{
			return signature.equals( "eglx.lang.ESmallint" );
		}
		else if ( obj instanceof Double )
		{
			return signature.equals( "eglx.lang.EFloat" );
		}
		else if ( obj instanceof Float )
		{
			return signature.equals( "eglx.lang.ESmallfloat" );
		}
		else if ( obj instanceof BigDecimal )
		{
			if ( !signature.startsWith( "eglx.lang.EDecimal" ) )
			{
				return false;
			}

			// If the signature includes precision and decimals, check them.
			if ( signature.length() == 18 )
			{
				return true;
			}
			else
			{
				//TODO The BigDecimal may not have the actual characteristics of the type.
				// A BigDecimal with precision=4 and scale=0 might be used for a decimal(10,5).
				int precision = ((BigDecimal)obj).precision();
				int decimals = ((BigDecimal)obj).scale();
				
				// Extract the precision and decimals from the signature string.
				// It's eglx.lang.EDecimal(x:y), never just eglx.lang.EDecimal(x).
				int precStart = 19;
				int precEnd = signature.indexOf( ':' );
				int decStart = precEnd + 1;
				int decEnd = signature.length() - 1;
				int sigPrecision = Integer.parseInt( signature.substring( precStart, precEnd ) );
				int sigDecimals = Integer.parseInt( signature.substring( decStart, decEnd ) );
				
				return sigPrecision >= precision && sigDecimals >= decimals;
			}
		}
		else if ( obj instanceof Boolean )
		{
			return signature.equals( "eglx.lang.EBoolean" );
		}
		else if ( obj instanceof byte[] )
		{
			if ( !signature.startsWith( "eglx.lang.EBytes" ) )
			{
				return false;
			}

			// If there's a length in the signature, check it.
			if ( signature.length() == 16 )
			{
				return true;
			}
			else
			{
				if ( obj instanceof EBytes )
				{
					obj = ((EBytes)obj).ezeUnbox();
				}

				int closeParenIndex = 18;
				while ( signature.charAt( closeParenIndex ) != ')' )
				{
					closeParenIndex++;
				}				
				int length = Integer.parseInt( signature.substring( 17, closeParenIndex ) );
				return length == ((byte[])obj).length;
			}
		}
		else if ( obj instanceof String )
		{
			if ( !signature.startsWith( "eglx.lang.EString" ) )
			{
				return false;
			}

			// If there's a length in the signature, check it.
			if ( signature.length() == 17 )
			{
				return true;
			}
			else
			{
				int closeParenIndex = 19;
				while ( signature.charAt( closeParenIndex ) != ')' )
				{
					closeParenIndex++;
				}				
				int length = Integer.parseInt( signature.substring( 18, closeParenIndex ) );
				return length >= ((String)obj).length();
			}
		}
		else if ( obj instanceof EDictionary )
		{
			return signature.equals( "eglx.lang.EDictionary" );
		}
		else if ( obj instanceof Delegate )
		{
			return signature.equals( ((Delegate)obj).getSignature() );
		}
		else if ( obj instanceof Calendar )
		{
			//TODO When obj is a Calendar, we can't tell if it's a date, time, or timestamp.
			return signature.equals( "eglx.lang.EDate" ) || signature.equals( "eglx.lang.ETime" ) || signature.startsWith( "eglx.lang.ETimestamp" );
		}

		return signature.equals( obj.getClass().getName() );
	}
	
	private static <T> List<T> copy( List<T> list, String signature )
	{
		try
		{
			List<T> copy = new ArrayList<T>( list.size() );
			for ( T element : list )
			{
				if ( element instanceof List )
				{
					String elementSig = signature.substring( 16, signature.length() - 1 );
					copy.add( (T)copy( (List)element, elementSig ) );
				}
				else if ( element instanceof Cloneable )
				{
					// Our runtime and generated classes are Cloneable.
					copy.add( (T)element.getClass().getMethod( "clone" ).invoke( element ) );
				}
				else
				{
					// This works for null, Strings, Numbers, etc.
					copy.add( element );
				}
			}
			return copy;
		}
		catch ( Exception ex )
		{
			TypeCastException tcx = new TypeCastException();
			tcx.castToName = signature;
			tcx.actualTypeName = list.getClass().getName();
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, list, tcx.actualTypeName,
					tcx.castToName );
		}
	}

	private static List copyAndBoxElements( List list, String signature )
	{
		try
		{
			List copy = new ArrayList( list.size() );
			for ( Object element : list )
			{
				Object box = null;
				if ( element instanceof List )
				{
					String elementSig = signature.substring( 16, signature.length() - 1 );
					box = boxElement( copy( (List)element, elementSig ) );
				}
				else if ( element instanceof Cloneable )
				{
					// Our runtime and generated classes are Cloneable.
					box = boxElement( element.getClass().getMethod( "clone" ).invoke( element ) );
				}
				else
				{
					// This works for null, Strings, Numbers, etc.
					box = boxElement( element );
				}
				copy.add( box );
			}
			return copy;
		}
		catch ( Exception ex )
		{
			TypeCastException tcx = new TypeCastException();
			tcx.castToName = signature;
			tcx.actualTypeName = list.getClass().getName();
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, list, tcx.actualTypeName,
					tcx.castToName );
		}
	}
	
	private static Object boxElement( Object obj )
	{
		if ( obj instanceof String )
		{
			return EString.ezeBox( (String)obj );
		}
		else if ( obj instanceof Integer )
		{
			return EInt.ezeBox( (Integer)obj );
		}
		else if ( obj instanceof Long )
		{
			return EBigint.ezeBox( (Long)obj );
		}
		else if ( obj instanceof Short )
		{
			return ESmallint.ezeBox( (Short)obj );
		}
		else if ( obj instanceof Double )
		{
			return EFloat.ezeBox( (Double)obj );
		}
		else if ( obj instanceof Float )
		{
			return ESmallfloat.ezeBox( (Float)obj );
		}
		else if ( obj instanceof BigDecimal )
		{
			return EDecimal.ezeBox( (BigDecimal)obj );
		}
		else if ( obj instanceof Boolean )
		{
			return EBoolean.ezeBox( (Boolean)obj );
		}
		else if ( obj instanceof byte[] )
		{
			return EBytes.ezeBox( (byte[])obj );
		}
		else if ( obj instanceof List )
		{
			return EList.ezeBox( (List)obj, "eglx.lang.EList<eglx.lang.EAny>" );
		}
		else if ( obj instanceof Calendar )
		{
			//TODO When obj is a Calendar, we can't tell if it's a date, time, or timestamp.
			return EDate.ezeBox( (Calendar)obj );
		}
		else
		{
			return obj;
		}
	}
	
	private static List copyAndCastElements( List list, String signature, Object... args )
	{
		if ( args.length > 0 && args[ 0 ] instanceof Class )
		{
			// The first value in the varargs array is a Class for the type we're casting
			// to (EBoolean, EInt, etc.).  The rest of the varargs array depends on the type.
			Class toClass = (Class)args[ 0 ];

			List result = new ArrayList( list.size() );			
			try
			{
				// We're going to call an ezeCast method to convert each element.
				// There are four varieties: no additional args, one Integer arg, two Integer 
				// args, or (for casting to EList) a String and some number of Objects.
				if ( args.length == 1 )
				{
					Method castMethod = toClass.getDeclaredMethod( "ezeCast", Object.class );
					for ( Object element : list )
					{
						result.add( castMethod.invoke( null, element ) );
					}
					return result;
				}
				else if ( args.length == 2 && args[ 1 ] instanceof Integer )
				{
					Method castMethod = toClass.getDeclaredMethod( "ezeCast", Object.class, Integer[].class );
					Integer[] castArg = new Integer[ 1 ];
					castArg[ 0 ] = (Integer)args[ 1 ];
					for ( Object element : list )
					{
						result.add( castMethod.invoke( null, element, castArg ) );
					}
					return result;
				}
				else if ( args.length == 3 && args[ 1 ] instanceof Integer && args[ 2 ] instanceof Integer )
				{
					Method castMethod = toClass.getDeclaredMethod( "ezeCast", Object.class, Integer[].class );
					Integer[] castArgs = new Integer[ 2 ];
					castArgs[ 0 ] = (Integer)args[ 1 ];
					castArgs[ 1 ] = (Integer)args[ 2 ];
					for ( Object element : list )
					{
						result.add( castMethod.invoke( null, element, castArgs ) );
					}
					return result;
				}
				else if ( args.length > 1 && toClass == EList.class )
				{
					String elementSig = (String)args[ 1 ];
					Object[] castArgs = new Object[ args.length - 2 ];
					System.arraycopy( args, 2, castArgs, 0, castArgs.length );
					for ( Object element : list )
					{
						result.add( ezeCast( element, elementSig, castArgs ) );
					}
					return result;
				}
			}
			catch ( Exception ex )
			{
				// Ignore it and throw an exception below.
			}
		}
		
		TypeCastException tcx = new TypeCastException();
		tcx.castToName = signature;
		tcx.actualTypeName = list.getClass().getName();
		throw tcx.fillInMessage( Message.CONVERSION_ERROR, list, tcx.actualTypeName,
				tcx.castToName );
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
