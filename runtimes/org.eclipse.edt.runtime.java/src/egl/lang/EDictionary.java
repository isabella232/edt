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
package egl.lang;

import java.util.Map;

import org.eclipse.edt.runtime.java.egl.lang.EglList;

import eglx.lang.OrderingKind;

/**
 * 
 */
public interface EDictionary extends EglAny, Map<String, Object> {
	/**
	 * Return whether the dictionary key comparison is case sensitive
	 * @return True if so
	 */
	public boolean caseSensitive();

	public void setCaseSensitive(boolean isCaseSensitive);

	/**
	 * Return the enumeration order for the dictionary
	 * @return byKey, byInsertion, or none
	 */
	public OrderingKind order();

	public void setOrder(OrderingKind orderConstant);

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
	 * Returns an array containing all of the keys, in the order specified by
	 * the ordering annotation.
	 *
	 * @return the keys.
	 */
	public EglList<String> getKeys();
	
	/**
	 * Returns an array containing all of the values, in the order specified by
	 * the ordering annotation.
	 *
	 * @return the values.
	 */
	public EglList<Object> getValues();
}
