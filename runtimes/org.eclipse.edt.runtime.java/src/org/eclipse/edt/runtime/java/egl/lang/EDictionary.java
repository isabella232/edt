/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.edt.javart.JavartException;

/**
 * 
 */
public class EDictionary extends AnyObject implements egl.lang.EDictionary
{
	private static final long serialVersionUID = 70L;
	
	/** Value for Dictionary.order */
	public static final int NONE = 0;

	/** Value for Dictionary.order */
	public static final int INSERTION_ORDER = 1;

	/** Value for Dictionary.order */
	public static final int KEY_ORDER = 2;

	/**
	 * The map containing the dictionary values. It maps String keys to AnyRef
	 * objects, and iterates the values in the specified order (insertion, key
	 * or unspecified order).
	 */
	private Map<java.lang.String, Object> map;

	/** Whether the keys are case sensitive or not. */
	private boolean caseSensitive;

	/** The order that the dictionary will iterate its values in. */
	private int order;


	/**
	 * Constructor
	 * 
	 * @param name
	 * @param caseSensitive
	 * @param order
	 */
	public EDictionary() {
		this(false, 0);
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param caseSensitive
	 * @param order
	 */
	public EDictionary( boolean caseSensitive, int order )
	{
		this.caseSensitive = caseSensitive;
		this.order = order;

		createMap();
	}

	/**
	 * Copy constructor
	 * 
	 * @param other
	 * @throws JavartException 
	 */
	public EDictionary( EDictionary other ) throws JavartException
	{
		this( other.caseSensitive, other.order );
		
		insertAll( (EDictionary)other );
	}

	/**
	 * Return whether the dictionary key comparison is case sensitive
	 * 
	 * @return True if so
	 */
	public boolean caseSensitive()
	{
		return this.caseSensitive;
	}

	@Override
	public void setCaseSensitive(boolean isCaseSensitive) {
		this.caseSensitive = isCaseSensitive;
	}

	@Override
	public void setOrder(int orderConstant) {
		this.order = orderConstant;
		
	}

	/**
	 * Return the enumeration order for the dictionary
	 * 
	 * @return INSERTION_ORDER, KEY_ORDER, UNSPECIFIED_ORDER
	 */
	public int order()
	{
		return this.order;
	}
	
	/**
	 * Create the map data structure
	 */
	private void createMap()
	{
		switch ( this.order )
		{
			case INSERTION_ORDER:
				this.map = new LinkedHashMap<java.lang.String, Object>();
				break;

			case KEY_ORDER:
				this.map = new TreeMap<java.lang.String, Object>();
				break;

			default:
				this.map = new HashMap<java.lang.String, Object>();
				break;
		}
	}

	/**
	 * Clear the contents of the dictionary.
	 */
	public void clear()
	{
		this.map.clear();
	}

	/**
	 * Return whether the dictionary contains an entry with a specified key.
	 * 
	 * @param key
	 *            The key
	 * @return True if so
	 */
	public boolean containsKey( Object key )
	{
		if ( !this.caseSensitive && key instanceof EString )
		{
			key = ((java.lang.String)key).toLowerCase();
		}

		return this.map.containsKey( key );
	}

	/**
	 * Return whether the dictionary contains a specified value.
	 * 
	 * @param value
	 * @return True if so
	 */
	public boolean containsValue( Object value )
	{
		return this.map.containsValue( value );
	}

	/**
	 * Return the set of entries in the dictionary.
	 * 
	 * @return Set of Map.Entry objects
	 */
	public Set entrySet()
	{
		return this.map.entrySet();
	}

	/**
	 * Compare this dictionary to another object
	 * 
	 * @param obj
	 *            Object to compare
	 * @return Integer comparison
	 */
	public boolean equals( Object obj )
	{
		// equals must be implemented this way to meet the requirements of
		// the Map interface.
		return map.equals( obj );
	}

	/**
	 * Return the hashcode for this Dictionary Object.
	 * 
	 * @return Hashcode integer
	 */
	public int hashCode()
	{
		// hashCode must be implemented this way to meet the requirements of
		// the Map interface.
		return map.hashCode();
	}

	
	/**
	 * Retrieve a value for the specified key.
	 * 
	 * @param key
	 *            The key
	 * @return The value, or null if not found
	 */
	public Object get( Object key )
	{
		if ( !this.caseSensitive )
		{
			key = ((java.lang.String)key).toLowerCase();
		}

		return this.map.get( key );
	}

	/**
	 * Return whether the dictionary is empty.
	 * 
	 * @return True if so
	 */
	public boolean isEmpty()
	{
		return this.map.isEmpty();
	}

	/**
	 * Return a set of keys that exist in the dictionary. It will iterate itself
	 * in the preferred order.
	 * 
	 * @return The Set of Strings
	 */
	public Set keySet()
	{
		return this.map.keySet();
	}

	/**
	 * Insert a key/value in the dictionary. Not supported, use insert()
	 * instead.
	 */
	public Object put( EString key, Object value )
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Remove a specified key and its value from the dictionary. Not supported,
	 * use removeElement() instead.
	 */
	public Object remove( Object key )
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Return how many entries are in this dictionary.
	 * 
	 * @return The number of elements that exist
	 */
	public int size()
	{
		return this.map.size();
	}

	/**
	 * Return the values that exist in the dictionary. If a value occurs more
	 * than once, it will be duplicated in the returned collection. The returned
	 * collection will iterate itself in the preferred order.
	 * 
	 * @return A collection of AnyRef objects
	 */
	public Collection values()
	{
		return this.map.values();
	}

	/**
	 * Return the keys for the contents of the dictionary, in the preferred
	 * order.
	 * 
	 * @return The array of Strings
	 */
	public java.lang.String[] getKeyArray()
	{
		java.lang.String[] keyArray = new java.lang.String[ this.map.size() ];

		int ii = 0;
		for ( Iterator iter = this.map.keySet().iterator(); iter.hasNext(); ++ii )
		{
			keyArray[ ii ] = (java.lang.String)iter.next();
		}

		return keyArray;
	}

	/**
	 * Return the values for the contents of the dictionary, in the preferred
	 * order.
	 * 
	 * @return The array of AnyRefs
	 */
	public Object[] getValueArray()
	{
		Object[] valueArray = new Object[ this.map.size() ];

		int ii = 0;
		for ( Iterator iter = this.map.values().iterator(); iter.hasNext(); ++ii )
		{
			valueArray[ ii ] = iter.next();
		}

		return valueArray;
	}

	/**
	 * Add an entry to the dictionary
	 * 
	 * @param key
	 * @param value
	 */
	public void insert( java.lang.String key, Object value )
	{
		if ( !this.caseSensitive )
		{
			key = key.toLowerCase();
		}
		put(key, value);
	}

	/**
	 * Return whether a given key exists in the dictionary.
	 * 
	 * @param key
	 * @return True if so
	 */
	public boolean containsKey( java.lang.String key )
	{
		if ( !this.caseSensitive )
		{
			key = key.toLowerCase();
		}

		return this.map.containsKey( key );
	}

	/**
	 * Return a DynamicArray of the keys (StringItems) that exist in this
	 * dictionary in the preferred order.
	 * 
	 * @return The StringArray
	 * @throws JavartException
	 */
	public EList<java.lang.String> getKeys() throws JavartException
	{
		EList<java.lang.String> keys = new EList<java.lang.String>();
		for ( java.lang.String key : this.map.keySet() )
		{
			keys.add(key);
		}

		return keys;
	}

	/**
	 * Return a DynamicArray of the values that exist in this
	 * dictionary in the preferred order.
	 * 
	 * @return The ReferenceArray
	 * @throws JavartException
	 */
	public EList<Object> getValues() throws JavartException
	{
		EList<Object> vals = new EList<Object>();
		for ( Object value : this.map.values() )
		{
			vals.add(value);
		}

		return vals;
	}

	/**
	 * Insert all the entries another dictionary into this dictionary.
	 * 
	 * @param d
	 *            the source dictionary
	 * @throws JavartException
	 */
	public void insertAll( egl.lang.EDictionary d ) throws JavartException
	{
		// d.insertAll(d) is a NOOP
		if ( d == this )
		{
			return;
		}

		java.lang.String[] newKeys = d.getKeyArray();
		Object[] newValues = d.getValueArray();

		for ( int ii = 0; ii < newKeys.length; ++ii )
		{
			insert( newKeys[ ii ], newValues[ ii ] );
		}
	}

	/**
	 * Remove the entry with a specified key from the dictionary
	 * 
	 * @param key
	 *            the key for the entry to remove
	 * @throws AbnormalException
	 *             if no element with specified key exists
	 */
	@Override
	public void removeElement( java.lang.String key ) throws JavartException
	{
		if ( !this.caseSensitive )
		{
			key = key.toLowerCase();
		}

		// TODO: Why do we need this exception?
//		if ( !map.containsKey( key ) )
//		{
//			JavartUtil.throwRuntimeException( 
//					Message.DYNAMIC_ACCESS_FAILED, 
//					JavartUtil.errorMessage( 
//							program, 
//							Message.DYNAMIC_ACCESS_FAILED,
//							new Object[] { key, name() } ), 
//							program );
//		}

		this.map.remove( key );
	}

	/**
	 * Removes all the entries in the dictionary
	 */
	public void removeAll()
	{
		this.map.clear();
	}

	/**
	 * Returns a clone of this object.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		EDictionary theClone = (EDictionary)super.clone();
		
		// clone the map
		switch ( this.order )
		{
			case INSERTION_ORDER:
				theClone.map = new LinkedHashMap<java.lang.String, Object>( this.map.size() );
				break;

			case KEY_ORDER:
				theClone.map = new TreeMap<java.lang.String, Object>();
				break;

			default:
				theClone.map = new HashMap<java.lang.String, Object>( this.map.size() );
				break;
		}
		for ( Iterator<Map.Entry<java.lang.String, Object>> iter = this.map.entrySet().iterator(); iter.hasNext(); )
		{
			Map.Entry<java.lang.String, Object> entry = (Map.Entry<java.lang.String, Object>)iter.next();
			Object value = entry.getValue();
			if ( value instanceof AnyValue )
			{
				value = ((AnyValue)value).clone();
			}
			theClone.map.put( entry.getKey(), value );
		}
		
		return theClone;
	}

	/**
	 * Fetch a value from the dictionary. If it does not exist, a proxy is
	 * returned that will create the new entry if and when its value is updated.
	 * 
	 * @param key
	 * @return The AnyRef entry, or a DictionaryEntryMaker proxy object
	 */
	public Object lookup( java.lang.String key )
	{
		if ( !this.caseSensitive )
		{
			key = key.toLowerCase();
		}

		return this.map.get( key );

		// TODO Proxy reference object should not be necessary
//		if ( val != null )
//		{
//			return (AnyRef)val;
//		}
//
//		return new DictionaryEntryMaker( this, key, program );
	}

	@Override
	public Object put(java.lang.String key, Object value) {
		if ( !this.caseSensitive )
		{
			key = key.toLowerCase();
		}
		this.map.put(key, value);
		return value;
	}

	public egl.lang.AnyObject ezeGet(String name) throws JavartException {
		Object value = get(name);
		return value instanceof egl.lang.AnyObject ? (egl.lang.AnyObject)value : AnyObject.ezeBox(value);
	}

	@Override
	public void ezeSet(String name, Object value) {
//		if (value instanceof egl.lang.AnyObject) {
//			value = ((AnyObject)value).ezeUnbox();
//		}
		put(name, value);
		
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends Object> map) {
		this.map.putAll(map);
		
	}
}
