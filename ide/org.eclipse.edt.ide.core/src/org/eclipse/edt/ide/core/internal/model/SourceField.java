/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IProperty;
import org.eclipse.edt.ide.core.model.IPropertyContainer;
import org.eclipse.edt.ide.core.model.Signature;

/**
 * @see IField
 */

public class SourceField extends Member implements IField {
	protected static final IProperty[] fgEmptyProperties = new IProperty[0];

	/**
	 * Constructs a handle to the field with the given name in the specified
	 * type.
	 */
	protected SourceField(IPart parent, String name) {
		super(FIELD, parent, name);
	}

	/**
	 * @see EGLElement#getHandleMemento()
	 */
	protected char getHandleMementoDelimiter() {
		return EGLElement.EGLM_FIELD;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.etools.egl.internal.model.core.IMember#getProperties(java.lang
	 * .String)
	 */
	public IProperty[] getProperties(String key) throws EGLModelException {
		ArrayList list = getChildrenOfType(PROPERTY_BLOCK);
		if (list.isEmpty())
			return fgEmptyProperties;
		else
			return ((IPropertyContainer) list.get(0)).getProperties();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.etools.egl.internal.model.core.IField#getPropertiesForIndex(java
	 * .lang.String, int)
	 */
	public IProperty[] getPropertiesForIndex(String key, int index)
			throws EGLModelException {
		ArrayList list = getChildrenOfType(PROPERTY_BLOCK);
		if (list.isEmpty())
			return fgEmptyProperties;
		else {
			IPropertyContainer props = ((IPropertyContainer) list.get(0))
					.getPropertiesForIndex(key, index);
			if (props == null)
				return fgEmptyProperties;
			else
				return props.getProperties();
		}
	}

	/**
	 * @see IField
	 */
	public String getTypeSignature() throws EGLModelException {
		SourceFieldElementInfo info = (SourceFieldElementInfo) getElementInfo();
		return info.getTypeSignature();
	}

	/**
	 * @private Debugging purposes
	 */
	protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
		buffer.append(this.tabString(tab));
		if (info == null) {
			buffer.append(getElementName());
			buffer.append(" (not open)"); //$NON-NLS-1$
		} else if (info == NO_INFO) {
			buffer.append(getElementName());
		} else {
			try {
				/**
				 * In the EGL language you can have a '*' item which is a filler
				 * and it is possible that there is no data type for a filler.
				 * In this case we no not want to output anything for the type
				 * signature. This fix stops an illegalArgumentException from
				 * being thrown. This was getting thrown because our model code
				 * is based on the java language model and in java all variables
				 * must have a type.
				 */
				if (((SourceFieldElementInfo) getElementInfo()).getTypeName().length > 0) {
					buffer.append(Signature.toString(this.getTypeSignature()));
				}
				buffer.append(" "); //$NON-NLS-1$
				buffer.append(this.getElementName());
			} catch (EGLModelException e) {
				buffer.append("<EGLModelException in toString of " + getElementName()); //$NON-NLS-1$
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.egl.internal.model.core.IField#hasOccurs()
	 */
	public boolean hasOccurs() throws EGLModelException {
		return ((SourceFieldElementInfo) getElementInfo()).hasOccurs();
	}

	public String getTypeName() throws EGLModelException {
		SourceFieldElementInfo info = (SourceFieldElementInfo) getElementInfo();
		return info.getTypeName().toString();
	}

	public String getTypeDeclaredPackage() throws EGLModelException {
		SourceFieldElementInfo info = (SourceFieldElementInfo) getElementInfo();
		if (info.getTypeDeclaredPackage() != null) {
			return info.getTypeDeclaredPackage().toString();
		}
		return null;
	}

}
