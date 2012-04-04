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
package org.eclipse.edt.compiler.binding.annotationType;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;


/**
 * @author Harmon
 */
public abstract class ResolvableNameArrayAnnotationTypeBinding extends StringArrayValueAnnotationTypeBinding {

    /**
     * @param name
     */
    public ResolvableNameArrayAnnotationTypeBinding(String caseSensitiveInternedName) {
        super(caseSensitiveInternedName);
    }

    public boolean isApplicableFor(IBinding binding) {
        return false;
    }
    
    public ITypeBinding getSingleValueType() {
    	return ArrayTypeBinding.getInstance(SystemPartManager.INTERNALREF_BINDING);
    }
}
