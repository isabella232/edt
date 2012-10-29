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
package org.eclipse.edt.compiler.binding;


/**
 * @author winghong
 */
public interface IPackageBinding extends IBinding {
    
    String getPackageName();
    String getCaseSensitivePackageName();

    
    IPartBinding resolveType(String simpleName);
    IPackageBinding resolvePackage(String simpleName);

}
