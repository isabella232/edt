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


public interface IBinding  {
    
    /**
     * Get an case insensitive version of the Binding's name
     * @return the case insensitive name
     */
    String getName();
    
    /**
     * Get a case sensitive version of the Binding's name
     * @return the case sensitive name
     */
    String getCaseSensitiveName();
    
    boolean isPackageBinding();
    
}
