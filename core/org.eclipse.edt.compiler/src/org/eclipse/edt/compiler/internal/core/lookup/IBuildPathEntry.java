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
package org.eclipse.edt.compiler.internal.core.lookup;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.serialization.ObjectStore;

public interface IBuildPathEntry {

	IPartBinding getPartBinding(String packageName,String partName);
	boolean hasPackage(String packageName);
	int hasPart(String packageName,String partName);
	IEnvironment getRealizingEnvironment();
	IPartBinding getCachedPartBinding(String packageName, String partName);
	public ObjectStore[] getObjectStores();
	public Part findPart(String packageName, String name) throws PartNotFoundException;

	
	boolean isZipFile();
	boolean isProject();
	String getID();
}
