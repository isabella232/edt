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

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.RequiredFieldInPartTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedFieldContentAnnotationValidationRule;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValueMustBeGreaterThanOrEqualToZeroForFormFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValueMustBeGreaterThanZeroForFormFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PositionForConsoleFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PositionRequiredForConsoleFieldUnlessSegmentsDefinedValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PrimitiveTypeNotAllowedInPartOfSubtypeValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.TwoElementArrayWhoseElementsAreGreaterThanZeroAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
class ConsoleFormAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String caseSensitiveName = InternUtil.internCaseSensitive("ConsoleForm");
    public static final String name = InternUtil.intern(caseSensitiveName);
    
    public ConsoleFormAnnotationTypeBinding() {
        super(caseSensitiveName, new Object[] {
        	IEGLConstants.PROPERTY_DELIMITERS,		PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_FORMSIZE,		ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.INT)),
        	IEGLConstants.PROPERTY_NAME,			PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_SHOWBRACKETS,	PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),        	
        },
        new Object[] {
        	IEGLConstants.PROPERTY_SHOWBRACKETS,	Boolean.YES
        });
    }

    private static ConsoleFormAnnotationTypeBinding INSTANCE = new ConsoleFormAnnotationTypeBinding();
    
    private static final List consoleFormAnnotations = new ArrayList();
   	static{
   		consoleFormAnnotations.add(new RequiredFieldInPartTypeAnnotationTypeBinding(
   			new String[]{IEGLConstants.PROPERTY_FORMSIZE},
   			caseSensitiveName));
   	}
   	
   	private static final ArrayList formSizeAnnotations = new ArrayList();
   	static{
   		formSizeAnnotations.add(new TwoElementArrayWhoseElementsAreGreaterThanZeroAnnotationValidator(
   			IEGLConstants.PROPERTY_FORMSIZE, IProblemRequestor.INVALID_PROPERTY_MUST_BE_TWO_POSITIVE_INTEGERS));
   	}
   	
   	private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_FORMSIZE), formSizeAnnotations);
   	}
   	
   	private static final List subPartTypeAnnotations = new ArrayList();
   	static{
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(PositionForConsoleFieldValidator.class));   		
   		subPartTypeAnnotations.add(new AnnotationValueMustBeGreaterThanZeroForFormFieldValidator(IEGLConstants.PROPERTY_COLUMNS, ColumnsAnnotationTypeBinding.getInstance()));
   		subPartTypeAnnotations.add(new AnnotationValueMustBeGreaterThanZeroForFormFieldValidator(IEGLConstants.PROPERTY_FIELDLEN, FieldLenAnnotationTypeBinding.getInstance()));
   		subPartTypeAnnotations.add(new AnnotationValueMustBeGreaterThanOrEqualToZeroForFormFieldValidator(IEGLConstants.PROPERTY_LINESBETWEENROWS, LinesBetweenRowsAnnotationTypeBinding.getInstance()));
   		subPartTypeAnnotations.add(new AnnotationValueMustBeGreaterThanZeroForFormFieldValidator(IEGLConstants.PROPERTY_SPACESBETWEENCOLUMNS, SpacesBetweenColumnsAnnotationTypeBinding.getInstance()));
   		subPartTypeAnnotations.add(new PrimitiveTypeNotAllowedInPartOfSubtypeValidator(Primitive.BOOLEAN, INSTANCE));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(PositionRequiredForConsoleFieldUnlessSegmentsDefinedValidator.class));
   	}
    
    public static ConsoleFormAnnotationTypeBinding getInstance() {
        return INSTANCE;
    }
    
    public boolean isApplicableFor(IBinding binding) {
        return binding.isTypeBinding() && (((ITypeBinding) binding).getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING || ((ITypeBinding) binding).getKind() == ITypeBinding.FIXED_RECORD_BINDING);
   }

    private Object readResolve() {
        return INSTANCE;
    }
    
    public List getFieldAnnotations(String field) {
		return (List)fieldAnnotations.get(field);
	}
    
	public List getAnnotations() {
		return consoleFormAnnotations;
	}
	
	public List getPartSubTypeAnnotations() {
		return subPartTypeAnnotations;
	}
}
