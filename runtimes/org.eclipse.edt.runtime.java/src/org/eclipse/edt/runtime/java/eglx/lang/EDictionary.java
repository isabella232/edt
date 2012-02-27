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
package org.eclipse.edt.runtime.java.eglx.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.messages.Message;

import eglx.lang.AnyException;
import eglx.lang.DynamicAccessException;
import eglx.lang.OrderingKind;

/**
 * 
 */
public class EDictionary extends EAny implements eglx.lang.EDictionary {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * The map containing the dictionary values. It maps String keys to AnyRef objects, and iterates the values in the
	 * specified order (insertion, key or unspecified order).
	 */
	private Map<String, Object> map;

	/** Whether the keys are case sensitive or not. */
	private boolean caseSensitive;

	/** The order that the dictionary will iterate its values in. */
	private OrderingKind order;

	/**
	 * Constructor
	 */
	public EDictionary() {
		this(false, OrderingKind.none);
	}

	/**
	 * Constructor
	 */
	public EDictionary(boolean caseSensitive, OrderingKind order) {
		this.caseSensitive = caseSensitive;
		this.order = order;
		createMap();
	}

	/**
	 * Copy constructor
	 */
	public EDictionary(EDictionary other) throws AnyException {
		this(other.caseSensitive, other.order);
		insertAll((EDictionary) other);
	}

	/**
	 * Return whether the dictionary key comparison is case sensitive
	 */
	public boolean getCaseSensitive() {
		return this.caseSensitive;
	}

	/**
	 * Return the enumeration order for the dictionary
	 */
	public OrderingKind getOrdering() {
		return this.order;
	}

	/**
	 * Create the map data structure
	 */
	private void createMap() {
		switch (this.order) {
			case byInsertion:
				this.map = new LinkedHashMap<String, Object>();
				break;

			case byKey:
				this.map = new TreeMap<String, Object>();
				break;

			default:
				this.map = new HashMap<String, Object>();
				break;
		}
	}

	/**
	 * Clear the contents of the dictionary.
	 */
	public void clear() {
		this.map.clear();
	}

	/**
	 * Return whether the dictionary contains an entry with a specified key.
	 * @param key The key
	 * @return True if so
	 */
	public boolean containsKey(Object key) {
		if (key instanceof String) {
			return containsKey( (String)key );
		}

		return this.map.containsKey(key);
	}

	/**
	 * Return whether the dictionary contains a specified value.
	 */
	public boolean containsValue(Object value) {
		return this.map.containsValue(value);
	}

	/**
	 * Return the set of entries in the dictionary.
 */
	public Set entrySet() {
		return this.map.entrySet();
	}

	/**
	 * Compare this dictionary to another object
	 */
	public boolean equals(Object obj) {
		// equals must be implemented this way to meet the requirements of
		// the Map interface.
		return map.equals(obj);
	}

	/**
	 * Return the hashcode for this Dictionary Object.
	 */
	public int hashCode() {
		// hashCode must be implemented this way to meet the requirements of
		// the Map interface.
		return map.hashCode();
	}

	/**
	 * Retrieve a value for the specified key.
	 */
	public Object get(Object key) {
		if (!this.caseSensitive && key instanceof String) {
			key = ((String) key).toLowerCase();
		}

		return this.map.get(key);
	}

	/**
	 * Return whether the dictionary is empty.
	 */
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	/**
	 * Return a set of keys that exist in the dictionary. It will iterate itself in the preferred order.
	 */
	public Set keySet() {
		return this.map.keySet();
	}

	/**
	 * Remove a specified key and its value from the dictionary. Not supported, use removeElement() instead.
	 */
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return how many entries are in this dictionary.
	 */
	public int size() {
		return this.map.size();
	}

	/**
	 * Return the values that exist in the dictionary. If a value occurs more than once, it will be duplicated in the
	 * returned collection. The returned collection will iterate itself in the preferred order.
	 */
	public Collection values() {
		return this.map.values();
	}

	/**
	 * Return the keys for the contents of the dictionary, in the preferred order.
	 */
	public String[] getKeyArray() {
		String[] keyArray = new String[this.map.size()];

		int ii = 0;
		for (Iterator iter = this.map.keySet().iterator(); iter.hasNext(); ++ii) {
			keyArray[ii] = (String) iter.next();
		}

		return keyArray;
	}

	/**
	 * Return the values for the contents of the dictionary, in the preferred order.
	 */
	public Object[] getValueArray() {
		Object[] valueArray = new Object[this.map.size()];

		int ii = 0;
		for (Iterator iter = this.map.values().iterator(); iter.hasNext(); ++ii) {
			valueArray[ii] = iter.next();
		}

		return valueArray;
	}

	/**
	 * Add an entry to the dictionary
	 */
	public void insert(String key, Object value) {
		if (!this.caseSensitive) {
			key = key.toLowerCase();
		}
		map.put(key, value);
	}

	/**
	 * Return whether a given key exists in the dictionary.
	 */
	public boolean containsKey(String key) {
		if (!this.caseSensitive) {
			key = key.toLowerCase();
		}

		return this.map.containsKey(key);
	}

	/**
	 * Return a list of the keys (StringItems) that exist in this dictionary in the preferred order.
	 */
	public List<String> getKeys() throws AnyException {
		List<String> keys = new ArrayList<String>();
		for (String key : this.map.keySet()) {
			keys.add(key);
		}

		return keys;
	}

	/**
	 * Return a list of the values that exist in this dictionary in the preferred order.
	 */
	public List<Object> getValues() throws AnyException {
		List<Object> vals = new ArrayList<Object>();
		for (Object value : this.map.values()) {
			vals.add(value);
		}

		return vals;
	}

	/**
	 * Insert all the entries another dictionary into this dictionary.
	 */
	public void insertAll(eglx.lang.EDictionary d) throws AnyException {
		// d.insertAll(d) is a NOOP
		if (d == this) {
			return;
		}

		String[] newKeys = d.getKeyArray();
		Object[] newValues = d.getValueArray();

		for (int ii = 0; ii < newKeys.length; ++ii) {
			insert(newKeys[ii], newValues[ii]);
		}
	}

	/**
	 * Remove the entry with a specified key from the dictionary
	 */
	@Override
	public void removeElement(String key) throws AnyException {
		if (!this.caseSensitive) {
			key = key.toLowerCase();
		}

		int originalSize = map.size();
		this.map.remove(key);
		if ( originalSize == map.size() )
		{
			// Since the size is unchanged, there was no value with that key.
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = key;
			throw dax.fillInMessage( Message.DYNAMIC_ACCESS_FAILED, key, "dictionary" );
		}
	}

	/**
	 * Removes all the entries in the dictionary
	 */
	public void removeAll() {
		this.map.clear();
	}

	/**
	 * Returns a clone of this object.
	 */
	public Object clone() throws CloneNotSupportedException {
		EDictionary theClone = (EDictionary) super.clone();

		// clone the map
		switch (this.order) {
			case byInsertion:
				theClone.map = new LinkedHashMap<String, Object>(this.map.size());
				break;

			case byKey:
				theClone.map = new TreeMap<String, Object>();
				break;

			default:
				theClone.map = new HashMap<String, Object>(this.map.size());
				break;
		}
		for (Iterator<Map.Entry<String, Object>> iter = this.map.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iter.next();
			Object value = entry.getValue();
			if (value instanceof AnyValue) {
				value = ((AnyValue) value).clone();
			}
			theClone.map.put(entry.getKey(), value);
		}

		return theClone;
	}

	@Override
	public Object put(String key, Object value) {
		if (!this.caseSensitive) {
			key = key.toLowerCase();
		}
		this.map.put(key, value);
		return value;
	}

	public Object ezeGet(String name) throws AnyException {
		if (!this.caseSensitive) {
			name = name.toLowerCase();
		}

		Object value = map.get( name );
		if ( value == null && !map.containsKey( name ) )
		{
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = name;
			throw dax.fillInMessage( Message.DYNAMIC_ACCESS_FAILED, name, "dictionary" );
		}
		return value;
	}

	@Override
	public void ezeSet(String name, Object value) {
		put(name, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> map) {
		this.map.putAll(map);
	}
}
