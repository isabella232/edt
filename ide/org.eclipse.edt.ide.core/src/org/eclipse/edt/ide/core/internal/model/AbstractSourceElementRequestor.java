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

/**
 * An adapter which implements the methods for handling
 * reference information from the parser.
 */
public abstract class AbstractSourceElementRequestor {
/**
 * Does nothing.
 */
public void acceptFieldReference(char[] fieldName, int sourcePosition) {}
/**
 * Does nothing.
 */
public void acceptFunctionReference(char[] methodName, int argCount, int sourcePosition) {}
/**
 * Does nothing.
 */
public void acceptPartReference(char[][] typeName, int sourceStart, int sourceEnd) {}
/**
 * Does nothing.
 */
public void acceptPartReference(char[] typeName, int sourcePosition) {}
/**
 * Does nothing.
 */
public void acceptUnknownReference(char[][] name, int sourceStart, int sourceEnd) {}
/**
 * Does nothing.
 */
public void acceptUnknownReference(char[] name, int sourcePosition) {}
}
