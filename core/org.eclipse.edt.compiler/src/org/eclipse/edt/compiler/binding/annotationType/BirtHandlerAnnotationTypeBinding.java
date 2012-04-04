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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
public class BirtHandlerAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String caseSensitiveName = InternUtil.internCaseSensitive("BirtHandler");
    public static final String name = InternUtil.intern(caseSensitiveName);

    private static BirtHandlerAnnotationTypeBinding INSTANCE = new BirtHandlerAnnotationTypeBinding();
    
//	public static final SystemFunctionBinding GETDATACOLUMNBINDING = SystemLibrary.createSystemFunction(
//			IEGLConstants.SYSTEM_WORD_GETDATACOLUMNBINDING,
//			null,
//			PrimitiveTypeBinding.getInstance(Primitive.ANY),
//			new String[]		{"fieldName"},
//			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
//			new UseType[]		{UseType.IN},
//			ISystemLibrary.GetFieldValue_func
//		);
//	
//	public static List getBirtHandlerReportFunctions() {
//		return Arrays.asList(new SystemFunctionBinding[] {GETDATACOLUMNBINDING});
//	}
	
    public BirtHandlerAnnotationTypeBinding() {
        super(caseSensitiveName, new Object[0]);
    }
    
    public static BirtHandlerAnnotationTypeBinding getInstance() {
        return INSTANCE;
    }

    public boolean isApplicableFor(IBinding binding) {
        return binding.isTypeBinding() && (((ITypeBinding) binding).getKind() == ITypeBinding.HANDLER_BINDING);
   }

    private Object readResolve() {
        return INSTANCE;
    }
}
