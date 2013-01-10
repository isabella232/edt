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
package org.eclipse.edt.ide.core.model;

/**
 * @author twilson
 * created	Aug 28, 2003
 */
public interface IPropertyContainer extends IEGLElement, ISourceReference, ISourceManipulation {

	public IProperty getProperty(String key) throws EGLModelException;
	public IProperty[] getProperties() throws EGLModelException;
	public IPropertyContainer getPropertiesForIndex(String key, int index) throws EGLModelException;
}
