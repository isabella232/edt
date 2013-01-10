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
package org.eclipse.edt.compiler.internal;

import java.util.HashMap;


/**
 * @author svihovec
 */
public class EGLServiceLib extends EGLLib {

    private static final EGLServiceLib INSTANCE = new EGLServiceLib();
    private static final String LIBRARY_NAME = IEGLConstants.KEYWORD_SERVICELIB;
    
    private static final HashMap serviceLibConstants = new HashMap();
    
    private EGLServiceLib(){}
    
    public static EGLServiceLib getInstance(){
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.EGLLib#getConstant(java.lang.String)
     */
    public EGLLibConstant getConstant(String name) {
       return (EGLLibConstant)serviceLibConstants.get(name.toUpperCase().toLowerCase());
    }
    
    public EGLLibConstant resolve(String resolveString){
        return resolve(LIBRARY_NAME, resolveString);
    }
}
