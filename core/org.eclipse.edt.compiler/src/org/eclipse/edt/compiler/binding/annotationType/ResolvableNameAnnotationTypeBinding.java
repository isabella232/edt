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

import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;


/**
 * @author Harmon
 */
public abstract class ResolvableNameAnnotationTypeBinding extends AnnotationTypeBinding {

    /**
     * @param name
     */
    public ResolvableNameAnnotationTypeBinding(String caseSensitiveInternedName) {
        super(caseSensitiveInternedName, SystemPartManager.INTERNALREF_BINDING);
    }

    public boolean isApplicableFor(IBinding binding) {
        return false;
    }
    
    public ITypeBinding getSingleValueType() {
    	return SystemPartManager.INTERNALREF_BINDING;
    }
}
