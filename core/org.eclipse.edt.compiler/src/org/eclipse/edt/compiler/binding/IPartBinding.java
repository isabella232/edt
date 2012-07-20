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

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;

/**
 * @author winghong
 */
public interface IPartBinding extends ITypeBinding {
    
    ITypeBinding RECURSIVE_USE_BINDING = RecursiveUseErrorBinding.INSTANCE;

    IEnvironment getEnvironment();
    void setEnvironment(IEnvironment environment);
    
    IPartBinding realize();
    
    void clear();

    boolean isStructurallyEqual(IPartBinding anotherPartBinding);
    
    void setValid(boolean isValid);
    
    IPartSubTypeAnnotationTypeBinding getSubType();
    
    IAnnotationBinding getSubTypeAnnotationBinding();
    
    /**
     * Returns true iff this part is valid to be used as the type of a variable
     * declaration.
     */
    boolean isDeclarablePart();
    
    boolean isPrivate();
    void setPrivate(boolean bool);
    
    boolean isSystemPart();
    
    StaticPartDataBinding getStaticPartDataBinding();
}
