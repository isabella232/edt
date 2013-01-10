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
package org.eclipse.edt.ide.core.internal.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IProperty;
import org.eclipse.edt.ide.core.model.IPropertyContainer;


/**
 * @author twilson
 * created	Aug 29, 2003
 */
public class SourcePropertyBlock extends Member implements IPropertyContainer {

	protected SourcePropertyBlock(IEGLElement parent, String name) {
		super(PROPERTY_BLOCK, parent, name);
	}
	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.internal.model.core.IPropertyContainer#getProperties(java.lang.String)
	 */
	public IProperty[] getProperties() throws EGLModelException {
		ArrayList list = getChildrenOfType(PROPERTY);
		IProperty[] array= new IProperty[list.size()];
		list.toArray(array);
		return array;
	}

	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.internal.model.core.IPropertyContainer#getPropertiesForIndex(java.lang.String, int)
	 */
	public IPropertyContainer getPropertiesForIndex(String name, int index)
		throws EGLModelException {
		// TODO Does not use the index yet.
		ArrayList list = getChildrenOfType(PROPERTY_BLOCK);
		IPropertyContainer block = null;
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			IPropertyContainer element = (IPropertyContainer) iter.next();
			if (element.getElementName().equals(name)) { 
				block = element;
				break;
			}
		}
		return block;
	}

	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.internal.model.core.IPropertyContainer#getProperty(java.lang.String)
	 */
	public IProperty getProperty(String key) throws EGLModelException {
		ArrayList list = getChildrenOfType(PROPERTY);
		IProperty property = null;
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			IProperty element = (IProperty) iter.next();
			if (element.getElementName().equalsIgnoreCase(key)) { 
				property = element;
				break;
			}
		}
		return property;
	}

}
