package org.eclipse.edt.compiler.binding;

import java.util.List;

import org.eclipse.edt.mof.egl.AnnotationType;

public interface IValidationProxy {
	/**
	 * Sets the annotation type that owns this proxy.
	 * 
	 * @param type The annotation type.
	 */
	void setType(AnnotationType type);
	
	/**
	 * @return the annotation type that owns this proxy.
	 */
	AnnotationType getType();
	
	/**
	 * @param field The name of a field on the annotation that corresponds to this proxy
	 * @return the validation rules to be run for the given annotation field
	 */
	List<ValueValidationRule> getFieldValidators(String field);
	
	/**
	 * @return the validation rules coming from a part's subtype to be run on fields, parameters, and return types within the part.
	 */
    List<FieldContentValidationRule> getPartSubTypeValidators();
    
    /**
     * @return the validation rules to be run on the part's subtype.
     */
    List<PartContentValidationRule> getPartTypeValidators();
    
    /**
     * @return the validation rules to be run on field accesses.
     */
    List<FieldAccessValidationRule> getFieldAccessValidators();
    
    /**
     * @return the validation rules to be run when instantiating a type.
     */
    List<InstantiationValidationRule> getInstantiationValidators();
    
    /**
     * @return the validation rules to be run when invoking a function or constructor.
     */
    List<InvocationValidationRule> getInvocationValidators();
    
    /**
     * @return the validation rules to be run on the annotation instance.
     */
    List<AnnotationValidationRule> getAnnotationValidators();
}
