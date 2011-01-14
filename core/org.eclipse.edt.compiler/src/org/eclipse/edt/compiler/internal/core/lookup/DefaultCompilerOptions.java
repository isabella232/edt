/*******************************************************************************
 * Copyright © 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.lookup;

/**
 * @author svihovec
 *
 */
public class DefaultCompilerOptions implements ICompilerOptions{
    
    private static ICompilerOptions INSTANCE = new DefaultCompilerOptions();
    
    private DefaultCompilerOptions() {}
    
    public static ICompilerOptions getInstance() {
        return INSTANCE;
    }

    public boolean isVAGCompatible() {
        return false;
    }

	public boolean isAliasJSFNames() {
		return true;
	}
}
