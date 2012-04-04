/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
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

public class BinaryProperty extends BinaryMember implements IProperty {
	
	protected BinaryProperty(IPropertyContainer parent, String name) {
		super(PROPERTY, parent, name);
	}

	public Object getValue() throws EGLModelException {
		SourcePropertyElementInfo info = (SourcePropertyElementInfo)getElementInfo();
		return info.getValue();
	}

	public int getValueType() throws EGLModelException {
		SourcePropertyElementInfo info = (SourcePropertyElementInfo)getElementInfo();
		return info.getValueType();
	}

}
