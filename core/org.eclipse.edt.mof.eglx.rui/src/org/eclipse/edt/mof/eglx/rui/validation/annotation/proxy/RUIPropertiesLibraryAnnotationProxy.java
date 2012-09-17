package org.eclipse.edt.mof.eglx.rui.validation.annotation.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AbstractValidationProxy;
import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.binding.ValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.eglx.rui.validation.annotation.PropertiesFileAnnotationValueValidator;
import org.eclipse.edt.mof.eglx.rui.validation.annotation.RUIPropertiesLibraryValidator;
import org.eclipse.edt.mof.utils.NameUtile;

public class RUIPropertiesLibraryAnnotationProxy extends AbstractValidationProxy {
	
	private static RUIPropertiesLibraryAnnotationProxy INSTANCE = new RUIPropertiesLibraryAnnotationProxy();
	
	private static final List<AnnotationValidationRule> annotationRules = new ArrayList();
   	static{
   		annotationRules.add(new UserDefinedAnnotationValidationRule(RUIPropertiesLibraryValidator.class));
   	}
   	
   	private static final List<ValueValidationRule> propertiesFileAnnotations = new ArrayList();
   	static{
   		propertiesFileAnnotations.add(new UserDefinedValueValidationRule(PropertiesFileAnnotationValueValidator.class));
   	}
   	
   	private static final Map<String, List<ValueValidationRule>> fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(NameUtile.getAsName(IEGLConstants.PROPERTY_PROPERTIESFILE), propertiesFileAnnotations);
   	}
	
	private RUIPropertiesLibraryAnnotationProxy() {
	}
	
	public static RUIPropertiesLibraryAnnotationProxy getInstance() {
		return INSTANCE;
	}
	
	@Override
	public List<AnnotationValidationRule> getAnnotationValidators() {
		return annotationRules;
	}
	
	@Override
	public List<ValueValidationRule> getFieldValidators(String field) {
		return fieldAnnotations.get(field);
	}
}
