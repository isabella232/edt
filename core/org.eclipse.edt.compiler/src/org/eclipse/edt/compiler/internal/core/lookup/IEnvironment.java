/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.util.PackageAndPartName;

/**
 * @author winghong
 */
public interface IEnvironment {

    IPartBinding getPartBinding(String packageName, String partName);
    IPartBinding getNewPartBinding(PackageAndPartName ppName, int kind);
    
    boolean hasPackage(String packageName);
    IPackageBinding getRootPackage();
    ICompiler getCompiler();
        
    

}
