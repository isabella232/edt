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

import org.eclipse.edt.ide.core.internal.model.bde.BinaryProjectDescription;

public interface IPluginModelBase extends IModel {

	/**
	 * Returns a location of the file that was used
	 * to create this model.  The location can be that 
	 * of a directory or that of a JAR file.
	 *
	 * @return a location of the external model, or
	 * <samp>null</samp> if the model is not created 
	 * from a resource or a file in the file system.
	 */
	String getInstallLocation();
	/**
	 * Creates and return a top-level plugin model object
	 *  
	 * @return a top-level model object representing a plug-in or a fragment.
	 */
	IPluginBase createPluginBase();
	/**
	 * Returns a top-level model object. Equivalent to
	 * calling <pre>getPluginBase(true)</pre>.
	 * 
	 * @return a top-level model object representing a plug-in or a fragment.
	 */
	IPluginBase getPluginBase();

	/**
	 * Returns a top-level model object.
	 * 
	 * @param createIfMissing if true, root model object will
	 * be created if not defined.
	 * 
	 * @return a top-level model object
	 */
	IPluginBase getPluginBase(boolean createIfMissing);

	/**
	 * Returns </samp>true</samp> if this model is currently enabled.
	 *
	 *@return true if the model is enabled
	 */
	boolean isEnabled();
	/**
	 * Sets the enable state of the model.
	 *
	 * @param enabled the new enable state
	 */
	void setEnabled(boolean enabled);
	/**
	 * Returns the bundle description of the plug-in
	 * in case the plug-in uses the new OSGi bundle layout. 
	 * 
	 * @return bundle description if this is an OSGi plug-in,
	 * or <code>null</code> if the plug-in is in a classic
	 * format.
	 * 
	 * @since 3.0
	 */
	BinaryProjectDescription getBundleDescription();

	/**
	 * Associates the bundle description of the plug-in
	 * with this model in case the plug-in uses the new
	 * OSGi bundle layout.
	 * 
	 * @param description bundle description to associate
	 * with this model
	 * 
	 * @since 3.0
	 */
	void setBundleDescription(BinaryProjectDescription description);
}
