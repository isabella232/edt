/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.search;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.search.IPartNameRequestor;

public class PartInfoRequestor implements IPartNameRequestor {
	
	private Collection fPartsFound;
	private PartInfoFactory fFactory;
	
	/**
	 * Constructs the PartRefRequestor
	 * @param typesFound Will collect all PartRef's found
	 */
	public PartInfoRequestor(Collection typesFound) {
		Assert.isNotNull(typesFound);
		fPartsFound= typesFound;
		fFactory= new PartInfoFactory();
	}

	/* non java-doc
	 * @see IPartNameRequestor#acceptInterface
	 */
	public void acceptPart(char[] packageName, char[] typeName, char partType, char[][] enclosingPartNames,String path, IPath projectPath) {
		fPartsFound.add(fFactory.create(packageName, typeName, enclosingPartNames, partType, path, projectPath));
	}

	
}
