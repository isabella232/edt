package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.validation.annotation.AS400UnsignedBin1Validator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AbstractAS400ParameterAnnotaionValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class AS400UnsignedBin1AnnotationTypeBinding extends AS400ParmeterAnnotationTypeBinding {

	private static AS400UnsignedBin1AnnotationTypeBinding INSTANCE = new AS400UnsignedBin1AnnotationTypeBinding();

	public static final String caseSensitiveName = InternUtil.internCaseSensitive("AS400UnsignedBin1");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
   	private static final List validationAnns = new ArrayList();
   	static{
   		validationAnns.add(new UserDefinedAnnotationValidationRule(AS400UnsignedBin1Validator.class));
   	}

   	public AbstractAS400ParameterAnnotaionValidator getValidator() {
   		return new AS400UnsignedBin1Validator();
   	}
   	
	public AS400UnsignedBin1AnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static AS400UnsignedBin1AnnotationTypeBinding getInstance() {
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
