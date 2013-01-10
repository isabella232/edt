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
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IProperty;

/**
 * @author twilson
 * created	Aug 29, 2003
 */
public class SourcePropertyBlockElementInfo extends MemberElementInfo {
	protected static final IProperty[] NO_PROPERTIES = new IProperty[0];

	public IProperty[] getPropertiesForIndex(String key, int index) {
		return NO_PROPERTIES;
	}
	public IProperty[] getProperties() {
		int length = fChildren.length;
		if (length == 0) return NO_PROPERTIES;
		IProperty[] props = new IProperty[length];
		int propIndex = 0;
		for (int i = 0; i < length; i++) {
			IEGLElement child = fChildren[i];
			if (child instanceof SourceProperty) {
				try {
					IProperty property = (IProperty)((SourceProperty)child).getElementInfo();
					props[propIndex++] = property;
				} catch (EGLModelException e) {
				}
			}
		}
		if (propIndex == 0) return NO_PROPERTIES;
		System.arraycopy(props, 0, props = new IProperty[propIndex], 0, propIndex);
		return props;
	}
}
