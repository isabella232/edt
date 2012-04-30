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
package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.ElementKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class AnnotationAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("Annotation"); 
	public static final String name = InternUtil.intern(caseSensitiveName);
    

    private static AnnotationAnnotationTypeBinding INSTANCE = new AnnotationAnnotationTypeBinding();
    
    public AnnotationAnnotationTypeBinding() {
        super(caseSensitiveName, new Object[] {
        	IEGLConstants.PROPERTY_TARGETS, 		ArrayTypeBinding.getInstance(ElementKind.TYPE),
//        	IEGLConstants.PROPERTY_TARGETTYPES, 	ArrayTypeBinding.getInstance(TypeKind.TYPE),
        	IEGLConstants.PROPERTY_VALIDATIONCLASS,	PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	
        	"ValidationProxy",						PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	"IsFormFieldArrayProperty",				PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN)
        });
    }
    
    public static AnnotationAnnotationTypeBinding getInstance() {
        return INSTANCE;
    }
    
    public boolean isApplicableFor(IBinding binding) {
         return binding.isTypeBinding() && ((ITypeBinding) binding).getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING;
    }

    private Object readResolve() {
        return INSTANCE;
    }
    
    public boolean isSystemAnnotation() {
    	return true;
    }
}
