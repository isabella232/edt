package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.validation.annotation.AS400PackedDecimalValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AbstractAS400ParameterAnnotaionValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class AS400PackedDecimalAnnotationTypeBinding extends AS400ParmeterAnnotationTypeBinding {

	private static AS400PackedDecimalAnnotationTypeBinding INSTANCE = new AS400PackedDecimalAnnotationTypeBinding();

	public static final String caseSensitiveName = InternUtil.internCaseSensitive("AS400PackedDecimal");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
   	private static final List validationAnns = new ArrayList();
   	static{
   		validationAnns.add(new UserDefinedAnnotationValidationRule(AS400PackedDecimalValidator.class));
   	}

   	public AbstractAS400ParameterAnnotaionValidator getValidator() {
   		return new AS400PackedDecimalValidator();
   	}
   	
	public AS400PackedDecimalAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static AS400PackedDecimalAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}


	@Override
	public boolean isApplicableFor(IBinding binding) {
		return true;
	}
	
	public List getAnnotations(){
		return validationAnns;
	}

	

}
