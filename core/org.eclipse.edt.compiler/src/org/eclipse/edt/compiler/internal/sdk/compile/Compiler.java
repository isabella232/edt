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
package org.eclipse.edt.compiler.internal.sdk.compile;

import org.eclipse.edt.compiler.internal.sdk.utils.Util;

/**
 * @author svihovec
 *
 */
public class Compiler extends org.eclipse.edt.compiler.internal.core.builder.Compiler {

    private static final Compiler INSTANCE = new Compiler();
    
    private Compiler(){}
    
    public static Compiler getInstance(){
        return INSTANCE;
    }
    
    protected void logPartBinderException(RuntimeException e) {
        Util.log("Part Binder Failure", e);  //$NON-NLS-1$
    }

    protected void logValidationException(RuntimeException e) {
        Util.log("Part Validation Failure", e);  //$NON-NLS-1$
    }
}
