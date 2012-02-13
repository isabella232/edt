package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.validation.annotation.AS400UnsignedBin2Validator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AbstractAS400ParameterAnnotaionValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class AS400UnsignedBin2AnnotationTypeBinding extends AS400ParmeterAnnotationTypeBinding {

	private static AS400UnsignedBin2AnnotationTypeBinding INSTANCE = new AS400UnsignedBin2AnnotationTypeBinding();

	public static final String caseSensitiveName = InternUtil.internCaseSensitive("AS400UnsignedBin2");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
   	private static final List validationAnns = new ArrayList();
   	static{
   		validationAnns.add(new UserDefinedAnnotationValidationRule(AS400UnsignedBin2Validator.class));
   	}

   	public AbstractAS400ParameterAnnotaionValidator getValidator() {
   		return new AS400UnsignedBin2Validator();
   	}
   	
	public AS400UnsignedBin2AnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static AS400UnsignedBin2AnnotationTypeBinding getInstance() {
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
