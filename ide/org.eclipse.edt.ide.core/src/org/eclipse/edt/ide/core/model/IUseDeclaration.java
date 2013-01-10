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
 * Represents a field declared in a type.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IUseDeclaration extends IMember {
/**
 * Returns the type signature of this field.
 *
 * @see Signature
 * @return the type signature of this field.
 * @exception EGLModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource
 */
String getTypeSignature() throws EGLModelException;

/**
 * Returns the set of properties defined for this member
 * @param key
 * @return
 * @throws EGLModelException
 */
IProperty[] getProperties(String key) throws EGLModelException;

/**
 * If the field is an array field, return properties set on specific index elements 
 * @param key
 * @param index
 * @return
 * @throws EGLModelException
 */
IProperty[] getPropertiesForIndex(String key, int index) throws EGLModelException;
}
