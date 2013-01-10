/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.dependency;
/**
 * @author svihovec
 *
 */
public interface IDependencyGraphValue extends ISerializable {

	public static final int FUNCTION = 0;
	public static final int PART = 1;
	public static final int QUALIFIED_NAME = 2;
	public static final int SIMPLE_NAME = 3;

	int getKind();
	
	int getNormalizedHashCode();
	
}
