/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model;

import java.util.ArrayList;

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IProperty;
import org.eclipse.edt.ide.core.model.IPropertyContainer;


public class BinaryPropertyBlock extends BinaryMember implements IPropertyContainer {

	public BinaryPropertyBlock(IMember parent, String name) {
		super(PROPERTY_BLOCK, parent, name);
	}

	public IProperty[] getProperties() throws EGLModelException {
		ArrayList list = getChildrenOfType(PROPERTY);
		IProperty[] array= new IProperty[list.size()];
		list.toArray(array);
		return array;
	}

	public IPropertyContainer getPropertiesForIndex(String key, int index) throws EGLModelException {
		return null;
	}

	public IProperty getProperty(String key) throws EGLModelException {
		return null;
	}

}
