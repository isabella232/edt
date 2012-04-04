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
package org.eclipse.edt.compiler.internal;

import java.util.HashMap;

/**
 * @author svihovec
 *
 */
public class EGLSysLib extends EGLLib{

    private static final EGLSysLib INSTANCE = new EGLSysLib();
    private static final String LIBRARY_NAME = IEGLConstants.KEYWORD_SYSLIB;
    
    public static final EGLLibConstant NO_OUTLINE = new EGLLibConstant(IEGLConstants.MNEMONIC_NOOUTLINE);
    public static final EGLLibConstant BOX = new EGLLibConstant(IEGLConstants.MNEMONIC_BOX);
   
    private static final HashMap sysLibConstants = new HashMap();
    
    static{
        sysLibConstants.put(NO_OUTLINE.getName().toUpperCase().toLowerCase(), NO_OUTLINE);
        sysLibConstants.put(BOX.getName().toUpperCase().toLowerCase(), BOX);
    }
    
    private EGLSysLib(){}
    
    public static EGLSysLib getInstance(){
        return INSTANCE;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.EGLLib#getConstant(java.lang.String)
     */
    public EGLLibConstant getConstant(String name) {
       return (EGLLibConstant)sysLibConstants.get(name.toUpperCase().toLowerCase());
    }
    
    public EGLLibConstant resolve(String resolveString){
        return resolve(LIBRARY_NAME, resolveString);
    }
}
