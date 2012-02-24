package org.eclipse.edt.compiler.internal.core.validation.annotation;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;

public class EGLPropertyFieldAccessValidator extends
		PropertyFieldAccessValidator {
	
	protected IAnnotationBinding getAnnotation(IDataBinding binding) {
		return binding.getAnnotation(new String[] {"eglx", "lang"}, "EGLProperty");
	}


}
