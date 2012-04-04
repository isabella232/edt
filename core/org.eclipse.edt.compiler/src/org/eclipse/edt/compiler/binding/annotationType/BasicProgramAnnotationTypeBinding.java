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
import org.eclipse.edt.compiler.binding.PartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AliasForProgramValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.InputRecordForProgramValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.MsgTablePrefixAnnotationValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.UnloadOnExitForProgramValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
class BasicProgramAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String caseSensitiveName = InternUtil.internCaseSensitive("BasicProgram");
    public static final String name = InternUtil.intern(caseSensitiveName);

    private static BasicProgramAnnotationTypeBinding INSTANCE = new BasicProgramAnnotationTypeBinding();
    
    private static final ArrayList msgTablePrefixAnnotations = new ArrayList();
   	static{
   		msgTablePrefixAnnotations.add(new UserDefinedValueValidationRule(MsgTablePrefixAnnotationValueValidator.class));
   	}
   	
 	private static final ArrayList inputrecordAnnotations = new ArrayList();
   	static{
   		inputrecordAnnotations.add(new UserDefinedValueValidationRule(InputRecordForProgramValidator.class));
   	}

 	private static final ArrayList unloadOnExitAnnotations = new ArrayList();
   	static{
   		unloadOnExitAnnotations.add(new UserDefinedValueValidationRule(UnloadOnExitForProgramValidator.class));
   	}

   	
   	private static final ArrayList aliasAnnotations = new ArrayList();
   	static{
   		aliasAnnotations.add(new UserDefinedValueValidationRule(AliasForProgramValidator.class));
   	}
   	
    private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_MSGTABLEPREFIX), msgTablePrefixAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_INPUTRECORD), inputrecordAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_ALIAS), aliasAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_UNLOADONEXIT), unloadOnExitAnnotations);
   	}
   	
    public BasicProgramAnnotationTypeBinding() {
        super(caseSensitiveName, new Object[] {
        	IEGLConstants.PROPERTY_INPUTRECORD,		SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_MSGTABLEPREFIX,	PrimitiveTypeBinding.getInstance(Primitive.STRING)	
        });
    }
    
    public static BasicProgramAnnotationTypeBinding getInstance() {
        return INSTANCE;
    }

    public boolean isApplicableFor(IBinding binding) {
        return binding.isTypeBinding() && (((ITypeBinding) binding).getKind() == ITypeBinding.PROGRAM_BINDING);
   }

    private Object readResolve() {
        return INSTANCE;
    }

	public List getFieldAnnotations(String field) {
		return (List)fieldAnnotations.get(field);
	}
}
