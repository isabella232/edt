/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedFieldAccessAnnotationValidationRule;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class RUIHandlerAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String caseSensitiveName = InternUtil.internCaseSensitive("RUIHandler");
    public static final String name = InternUtil.intern(caseSensitiveName);

    private static RUIHandlerAnnotationTypeBinding INSTANCE = new RUIHandlerAnnotationTypeBinding();
    
    private static final List annotations = new ArrayList();
   	   	    
    public RUIHandlerAnnotationTypeBinding() {
        super(caseSensitiveName);
    }
    
    public static RUIHandlerAnnotationTypeBinding getInstance() {
        return INSTANCE;
    }
    
    public boolean isApplicableFor(IBinding binding) {
        return binding.isTypeBinding() && (((ITypeBinding) binding).getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING || ((ITypeBinding) binding).getKind() == ITypeBinding.FIXED_RECORD_BINDING);
   }

    private Object readResolve() {
        return INSTANCE;
    }

	public List getAnnotations(){
		return annotations;
	}
}
