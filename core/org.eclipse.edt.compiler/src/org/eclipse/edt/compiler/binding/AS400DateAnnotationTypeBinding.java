package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.validation.annotation.AS400DateValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AbstractAS400ParameterAnnotaionValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class AS400DateAnnotationTypeBinding extends AS400ParmeterAnnotationTypeBinding {

	private static AS400DateAnnotationTypeBinding INSTANCE = new AS400DateAnnotationTypeBinding();

	public static final String caseSensitiveName = InternUtil.internCaseSensitive("AS400Date");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
   	private static final List validationAnns = new ArrayList();
   	static{
   		validationAnns.add(new UserDefinedAnnotationValidationRule(AS400DateValidator.class));
   	}

   	public AbstractAS400ParameterAnnotaionValidator getValidator() {
   		return new AS400DateValidator();
   	}
   	
	public AS400DateAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static AS400DateAnnotationTypeBinding getInstance() {
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