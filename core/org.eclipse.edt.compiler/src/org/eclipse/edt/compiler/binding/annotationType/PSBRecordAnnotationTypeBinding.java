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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.MutuallyExclusiveAnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.validation.annotation.MustBeDLINameAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
class PSBRecordAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String caseSensitiveName = InternUtil.internCaseSensitive("PSBRecord");
    public static final String name = InternUtil.intern(caseSensitiveName);
    
    public PSBRecordAnnotationTypeBinding() {
        super(caseSensitiveName, new Object[] {
        	IEGLConstants.PROPERTY_DEFAULTPSBNAME,	PrimitiveTypeBinding.getInstance(Primitive.STRING)	
        });
    }

    private static PSBRecordAnnotationTypeBinding INSTANCE = new PSBRecordAnnotationTypeBinding();
    
	private static final List subPartTypeAnnotations = new ArrayList();
   	static{	
   		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(
   			PCBAnnotationTypeBinding.caseSensitiveName, new String[]{RedefinesAnnotationTypeBinding.caseSensitiveName}));
   	}
   
	private static final List psbRecordAnnotations = new ArrayList();
   	static{
   	}
   	
   	private static final ArrayList defaultPSBNameAnnotations = new ArrayList();
   	static{
   		defaultPSBNameAnnotations.add(MustBeDLINameAnnotationValidator.INSTANCE);
   	}
   	
   	private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_DEFAULTPSBNAME), defaultPSBNameAnnotations);
   	}
   	
    public static PSBRecordAnnotationTypeBinding getInstance() {
        return INSTANCE;
    }
    
    public boolean isApplicableFor(IBinding binding) {
        return binding.isTypeBinding() && (((ITypeBinding) binding).getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING);
   }

    private Object readResolve() {
        return INSTANCE;
    }
    
	public List getPartSubTypeAnnotations() {
		return subPartTypeAnnotations;
	}
	
	public List getFieldAnnotations(String member){
		return (List)fieldAnnotations.get(member);
	}
}
