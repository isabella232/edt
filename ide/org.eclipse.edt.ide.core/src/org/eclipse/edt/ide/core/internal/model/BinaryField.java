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
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IProperty;

public class BinaryField extends BinaryMember implements IField {

	protected BinaryField(IEGLElement parent, String name) {
		super(FIELD, parent, name);
	}

	public IProperty[] getProperties(String key) throws EGLModelException {
		IEGLElement[] children = getChildren();
		for(IEGLElement child: children){
			if(child instanceof BinaryPropertyBlock){
				if(((BinaryPropertyBlock)child).getElementName().equals(key))
					return ((BinaryPropertyBlock)child).getProperties();
			}
		}
		return null;
	}

	public IProperty[] getPropertiesForIndex(String key, int index) throws EGLModelException {
		return null;
	}

	public String getTypeSignature() throws EGLModelException {
		SourceFieldElementInfo info = (SourceFieldElementInfo) getElementInfo();
		return info.getTypeSignature();
	}

	public boolean hasOccurs() throws EGLModelException {
		return false;
	}

	public String getTypeName() throws EGLModelException {
		SourceFieldElementInfo info = (SourceFieldElementInfo) getElementInfo();
		return String.valueOf(info.getTypeName());
	}
	
	public String getTypeDeclaredPackage() throws EGLModelException{
		SourceFieldElementInfo info = (SourceFieldElementInfo) getElementInfo();
		if(info.getTypeDeclaredPackage() != null){
			return String.valueOf(info.getTypeDeclaredPackage());
		}
		return null;
	}

}
