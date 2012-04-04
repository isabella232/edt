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
import org.eclipse.edt.compiler.binding.MutuallyExclusiveAnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedFieldContentAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValueMustBeGreaterThanOrEqualToZeroForFormFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValueMustBeGreaterThanZeroForFormFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.DateFormatForFormFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.FieldLenForFormFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.MinimumInputForFormFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.MsgKeyPropertyForFormFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PositionForFormFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PrimitiveTypeNotAllowedInPartOfSubtypeValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ProtectForFormFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ScreenSizesAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.TwoElementArrayWhoseElementsAreGreaterThanZeroAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidatorFunctionAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValueForFormFieldValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
class TextFormAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String caseSensitiveName = InternUtil.internCaseSensitive("TextForm");
    public static final String name = InternUtil.intern(caseSensitiveName);
    
    public TextFormAnnotationTypeBinding() {
        super(caseSensitiveName, new Object[] {
        	IEGLConstants.PROPERTY_HELPFORM,			PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_SCREENSIZES,			ArrayTypeBinding.getInstance(ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.INT))),
        });
    }

    private static TextFormAnnotationTypeBinding INSTANCE = new TextFormAnnotationTypeBinding();
    
    private static final ArrayList formSizeAnnotations = new ArrayList();
   	static{
   		formSizeAnnotations.add(new TwoElementArrayWhoseElementsAreGreaterThanZeroAnnotationValidator(
   			IEGLConstants.PROPERTY_FORMSIZE, IProblemRequestor.INVALID_FORM_SIZE_PROPERTY_VALUE));
   	}
   	
   	private static final ArrayList positionAnnotations = new ArrayList();
   	static{
   		positionAnnotations.add(new TwoElementArrayWhoseElementsAreGreaterThanZeroAnnotationValidator(
   			IEGLConstants.PROPERTY_POSITION, IProblemRequestor.INVALID_FORM_POSITION_PROPERTY_VALUE));
   	}
   	
   	private static final ArrayList screenSizesAnnotations = new ArrayList();
   	static{
   		screenSizesAnnotations.add(new UserDefinedValueValidationRule(ScreenSizesAnnotationValidator.class));
   	}
   	
   	private static final ArrayList validatorFunctionAnnotations = new ArrayList();
   	static{
   		validatorFunctionAnnotations.add(new UserDefinedValueValidationRule(ValidatorFunctionAnnotationValidator.class));
   	}
   	
    private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_FORMSIZE), formSizeAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_POSITION), positionAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_SCREENSIZES), screenSizesAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORFUNCTION), validatorFunctionAnnotations);
   	}
   	
   	private static final List subPartTypeAnnotations = new ArrayList();
   	static{
   		//Common to text and print forms:
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(DateFormatForFormFieldValidator.class));   		
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(FieldLenForFormFieldValidator.class));   		
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(PositionForFormFieldValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(ProtectForFormFieldValidator.class));   		
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(ValueForFormFieldValidator.class));
   		subPartTypeAnnotations.add(new AnnotationValueMustBeGreaterThanZeroForFormFieldValidator(IEGLConstants.PROPERTY_COLUMNS, ColumnsAnnotationTypeBinding.getInstance()));
   		subPartTypeAnnotations.add(new AnnotationValueMustBeGreaterThanZeroForFormFieldValidator(IEGLConstants.PROPERTY_SPACESBETWEENCOLUMNS, SpacesBetweenColumnsAnnotationTypeBinding.getInstance()));
   		subPartTypeAnnotations.add(new AnnotationValueMustBeGreaterThanOrEqualToZeroForFormFieldValidator(IEGLConstants.PROPERTY_LINESBETWEENROWS, LinesBetweenRowsAnnotationTypeBinding.getInstance()));
   		
   		//Text form rules:
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(MinimumInputForFormFieldValidator.class));
   		subPartTypeAnnotations.add(new MsgKeyPropertyForFormFieldValidator(InputRequiredMsgKeyAnnotationTypeBinding.getInstance(), IEGLConstants.PROPERTY_INPUTREQUIREDMSGKEY));
   		subPartTypeAnnotations.add(new MsgKeyPropertyForFormFieldValidator(MinimumInputMsgKeyAnnotationTypeBinding.getInstance(), IEGLConstants.PROPERTY_MINIMUMINPUTMSGKEY));
   		subPartTypeAnnotations.add(new MsgKeyPropertyForFormFieldValidator(TypeChkMsgKeyAnnotationTypeBinding.getInstance(), IEGLConstants.PROPERTY_TYPECHKMSGKEY));
   		subPartTypeAnnotations.add(new MsgKeyPropertyForFormFieldValidator(ValidatorDataTableMsgKeyAnnotationTypeBinding.getInstance(), IEGLConstants.PROPERTY_VALIDATORDATATABLEMSGKEY));
   		subPartTypeAnnotations.add(new MsgKeyPropertyForFormFieldValidator(ValidatorFunctionMsgKeyAnnotationTypeBinding.getInstance(), IEGLConstants.PROPERTY_VALIDATORFUNCTIONMSGKEY));
   		subPartTypeAnnotations.add(new MsgKeyPropertyForFormFieldValidator(ValidValuesMsgKeyAnnotationTypeBinding.getInstance(), IEGLConstants.PROPERTY_VALIDVALUESMSGKEY));
   		subPartTypeAnnotations.add(new AnnotationValueMustBeGreaterThanZeroForFormFieldValidator(IEGLConstants.PROPERTY_VALIDATIONORDER, ValidationOrderAnnotationTypeBinding.getInstance()));   		   		
  		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(IsBooleanAnnotationTypeBinding.caseSensitiveName,
				new String[]{
				NumericSeparatorAnnotationTypeBinding.name,
				CurrencyAnnotationTypeBinding.name,
				SignAnnotationTypeBinding.name,
				ZeroFormatAnnotationTypeBinding.name,
				ValidValuesAnnotationTypeBinding.name}));
  		subPartTypeAnnotations.add(new PrimitiveTypeNotAllowedInPartOfSubtypeValidator(Primitive.BOOLEAN, INSTANCE));
  	}
    
   	public static TextFormAnnotationTypeBinding getInstance() {
        return INSTANCE;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public boolean isApplicableFor(IBinding binding) {
        return binding.isTypeBinding() && (((ITypeBinding) binding).getKind() == ITypeBinding.FORM_BINDING);
   }
    
	public List getFieldAnnotations(String field) {
		return (List)fieldAnnotations.get(field);
	}
	
	public List getPartSubTypeAnnotations() {
		return subPartTypeAnnotations;
	}
}
