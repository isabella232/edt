/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.model.bde;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

/**
 * A model object that represents the content of a plug-in or
 * fragment manifest. This object contains data that is common
 * for both plug-ins and fragments.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IPluginBase extends IAdaptable {
	/**
	 * Returns a name of the plug-in provider.
	 *
	 * @return plug-in provider name
	 */
	String getProviderName();

	/**
	 * Returns this plug-in's version
	 * @return the version of the plug-in
	 */
	String getVersion();

	/**
	 * Sets the version of the plug-in.
	 * This method will throw a CoreException
	 * if the model is not editable.
	 *
	 * @param version the new plug-in version
	 */
	void setVersion(String version) throws CoreException;

	/**
	 * Returns a unique id of this object.
	 * @return the id of this object
	 */
	public String getId();

	/**
	 * Sets the id of this IIdentifiable to the provided value.
	 * This method will throw CoreException if
	 * object is not editable.
	 *
	 *@param id a new id of this object
	 */
	void setId(String id) throws CoreException;
	/**
	 * Returns the model that owns this object.
	 * @return the model instance
	 */
	IPluginModelBase getModel();

	/**
	 * Returns the name of this model object
	 *@return the object name
	 */
	String getName();

	/**
	 * Chances the name of this model object.
	 * This method may throw a CoreException
	 * if the model is not editable.
	 *
	 * @param name the new object name
	 */
	void setName(String name) throws CoreException;	
}
