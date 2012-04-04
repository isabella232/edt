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

import org.eclipse.edt.compiler.binding.ProgramBinding;

/**
 * @author winghong
 */
public class ProgramScope extends FunctionContainerScope {
    
    public ProgramScope(Scope parentScope, ProgramBinding programBinding) {
        super(parentScope, programBinding);
    }
    
    public boolean isProgramScope() {
        return true;
    }
    
    public ProgramBinding getProgramBinding() {
        return (ProgramBinding)functionContainerBinding;
    }
}
