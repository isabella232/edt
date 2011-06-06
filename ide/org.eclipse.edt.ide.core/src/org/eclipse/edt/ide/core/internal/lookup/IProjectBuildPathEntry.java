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
package org.eclipse.edt.ide.core.internal.lookup;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.serialization.ObjectStore;

// TODO this interface is a candidate for removal once core refactoring is done. Due to the dependency chain
// we cannot put references to org.eclipse.edt.mof* code in IBuildPathEntry, but if we can after the
// refactoring then these methods should be moved and this interface deleted.
public interface IProjectBuildPathEntry extends IBuildPathEntry {
	/**
	 * Finds the given part name.
	 */
	public Part findPart(String[] packageName, String name) throws PartNotFoundException;
	
	/**
	 * @return the entry's object stores, never null.
	 */
	public ObjectStore[] getObjectStores();
	
	/**
	 * Adds a binding to the cache.
	 */
	public void addPartBindingToCache(IPartBinding partBinding);
}
