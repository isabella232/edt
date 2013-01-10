/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.builder;

/**
 * @author svihovec
 * 
 * This exception is provided because we cannot use the Eclipse OperationCancelledException in our EDT.Core plugin.
 *
 */
public class CancelledException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}
