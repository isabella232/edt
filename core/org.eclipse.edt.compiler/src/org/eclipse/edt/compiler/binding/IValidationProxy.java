package org.eclipse.edt.compiler.binding;

import java.util.List;

import org.eclipse.edt.mof.egl.AnnotationType;

public interface IValidationProxy {
	void setType(AnnotationType type);
	AnnotationType getType();
	
	List<ValueValidationRule> getFieldValidators(String field);
	
	List<ValueValidationRule> getValueValidators();
    
    List<FieldContentValidationRule> getPartSubTypeValidators();
    
    List<PartContentValidationRule> getPartTypeValidators();
    
    List<FieldAccessValidationRule> getFieldAccessAnnotations();
    
    List<InstantiationValidationRule> getInstantiationValidators();
    
    List<InvocationValidationRule> getInvocationValidators();
    
    List<AnnotationValidationRule> getAnnotationValidators();
}
