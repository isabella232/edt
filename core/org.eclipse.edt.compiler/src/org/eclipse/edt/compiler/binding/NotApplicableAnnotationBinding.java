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

import org.eclipse.edt.mof.egl.utils.InternUtil;

/**
 * @author Harmon
 */
public class NotApplicableAnnotationBinding extends AnnotationBinding {
    
    private static final NotApplicableAnnotationBinding INSTANCE = new NotApplicableAnnotationBinding(InternUtil.internCaseSensitive(""), null, null);

    /**
     * @param simpleName
     * @param declarer
     * @param typeBinding
     */
    private NotApplicableAnnotationBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding) {
        super(caseSensitiveInternedName, declarer, typeBinding);
        // TODO Auto-generated constructor stub
    }
    
    public static NotApplicableAnnotationBinding getInstance() {
        return INSTANCE;
    }
    
    private Object readResolve() {
        return INSTANCE;
    }

}
