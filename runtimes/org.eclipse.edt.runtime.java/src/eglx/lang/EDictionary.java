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
package eglx.lang;

import java.util.List;
import java.util.Map;


/**
 * 
 */
public interface EDictionary extends EAny, Map<String, Object> {
	/**
	 * Return whether the dictionary key comparison is case sensitive
	 * @return True if so
	 */
	public boolean getCaseSensitive();

	/**
	 * Return the enumeration order for the dictionary
	 * @return byKey, byInsertion, or none
	 */
	public OrderingKind getOrdering();

	/**
	 * Return the keys for the contents of the dictionary, in the preferred order.
	 * @return The array of Strings
	 */
	public String[] getKeyArray();

	/**
	 * Return the values for the contents of the dictionary, in the preferred order.
	 * @return The array of Objects
	 */
	public Object[] getValueArray();

	/**
	 * Insert all the entries another dictionary into this dictionary.
	 * @param d the source dictionary
	 * @throws AnyException
	 */
	public void insertAll(EDictionary d) throws AnyException;

	/**
	 * Remove the entry with a specified key from the dictionary
	 * @param key the key for the entry to remove
	 * @throws AbnormalException if no element with specified key exists
	 */
	public void removeElement(String key) throws AnyException;

	/**
	 * Removes all the entries in the dictionary
	 */
	public void removeAll();

	/**
	 * Tells if there's a value stored with the given key.
	 *
	 * @param key  the key.
	 * @return true if the key is in this dictionary.
	 */
	public boolean containsKey(String key);
	
	/**
	 * Returns a list containing all of the keys, in the order specified by
	 * the ordering annotation.
	 *
	 * @return the keys.
	 */
	public List<String> getKeys();
	
	/**
	 * Returns a list containing all of the values, in the order specified by
	 * the ordering annotation.
	 *
	 * @return the values.
	 */
	public List<Object> getValues();
	
	/**
	 * Returns the number of key-value pairs in this dictionary.
	 *
	 * @return the size.
	 */
	public int size();
}
