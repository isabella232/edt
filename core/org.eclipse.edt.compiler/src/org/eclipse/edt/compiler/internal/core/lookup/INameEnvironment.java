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
package org.eclipse.edt.compiler.internal.core.lookup;

import org.eclipse.edt.compiler.binding.ITypeBinding;

/**
 * @author winghong
 */
public interface INameEnvironment {
    
    ITypeBinding resolvePart(String projectContext, String[] packageName, String partName);
    ITypeBinding getPart(String project, String[] packageName, String partName);
    ITypeBinding getNewPart(String project, String[] packageName, String partName, int type);
}
