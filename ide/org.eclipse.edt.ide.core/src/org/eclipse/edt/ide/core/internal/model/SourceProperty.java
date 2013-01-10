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

import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IProperty;
import org.eclipse.edt.ide.core.model.IPropertyContainer;

/**
 * @author twilson
 * created	Aug 28, 2003
 */
public class SourceProperty extends Member implements IProperty {

	/**
	 * Constructs a handle to the field with the given name in the specified type. 
	 */
	protected SourceProperty(IPropertyContainer parent, String name) {
		super(PROPERTY, parent, name);
	}

	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.internal.model.core.IProperty#getValue()
	 */
	public Object getValue() throws EGLModelException {
		SourcePropertyElementInfo info = (SourcePropertyElementInfo)getElementInfo();
		return info.getValue();
	}

	public int getValueType() throws EGLModelException {
		SourcePropertyElementInfo info = (SourcePropertyElementInfo)getElementInfo();
		return info.getValueType();
	}
	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.internal.model.internal.core.EGLElement#getHandleMementoDelimiter()
	 */
	protected char getHandleMementoDelimiter() {
		return EGLM_PROPERTY;
	}

}
