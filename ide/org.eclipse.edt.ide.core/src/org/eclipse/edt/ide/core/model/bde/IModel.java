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

import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * A generic model. Classes that implement this interface are expected to be
 * able to:
 * <ul>
 * <li>Load from an input stream
 * <li>Reload (reset, load, fire 'world change')
 * <li>Dispose (clear all the data and reset)
 * <li>Be associated with a resource (optional)
 * </ul>
 * If a model is not created from a workspace resource file, its underlying
 * resource will be <samp>null </samp>.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 2.0
 */
public interface IModel {
	/**
	 * Returns a workspace resource that this model is created from. Load/reload
	 * operations are not directly connected with the resource (although they
	 * can be). In some cases, models will load from a buffer (an editor
	 * document) rather than a resource. However, the buffer will eventually be
	 * synced up with this resource.
	 * <p>
	 * With the caveat of stepped loading, all other properties of the
	 * underlying resource could be used directly (path, project etc.).
	 * 
	 * @return a workspace resource (file) that this model is associated with,
	 *         or <samp>null </samp> if the model is not created from a
	 *         resource.
	 */
	public IResource getUnderlyingResource();

	/**
	 * Tests if this model is loaded and can be used.
	 * 
	 * @return <code>true</code> if the model has been loaded
	 */
	boolean isLoaded();
	/**
	 * Loads the model directly from an underlying resource. This method does
	 * nothing if this model has no underlying resource or if there is a buffer
	 * stage between the model and the resource.
	 * 
	 * @throws CoreException
	 *             if errors are encountered during the loading.
	 */
	public void load() throws CoreException;

	/**
	 * Loads the model from the provided input stream. This method throws a
	 * CoreException if errors are encountered during the loading. Upon
	 * succesful load, 'isLoaded()' should return <samp>true </samp>.
	 * 
	 * @param source
	 *            an input stream that should be parsed to load the model
	 * @param outOfSync
	 *            if true, time stamp will not be updated to maintain
	 *            out-of-sync state of the model.
	 * @throws CoreException
	 *             if errors are encountered during the loading.
	 */
	public void load(InputStream source, boolean outOfSync) throws CoreException;
}
