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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.Map;

import org.eclipse.edt.compiler.binding.FieldLenUtility;
import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class FieldLenForFormFieldValidator extends DefaultFieldContentAnnotationValidationRule {

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IAnnotationBinding annotationBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_FIELDLEN));
		if(annotationBinding != null && annotationBinding.getValue() != null) {
			
			if(containerBinding != null && IDataBinding.FORM_FIELD == containerBinding.getKind()) {
				FormFieldBinding fieldBinding = (FormFieldBinding) containerBinding;
				if(!fieldBinding.isConstant()) {
					try {
						int value = Integer.parseInt(annotationBinding.getValue().toString());
						if(!isValid(value, fieldBinding, new FieldLenUtility(fieldBinding, DefaultCompilerOptions.getInstance(), null).getDefaultFieldLen())) {
							problemRequestor.acceptProblem(
								errorNode,
								IProblemRequestor.INVALID_FORM_FIELD_FIELDLEN_PROPERTY_VALUE,
								new String[] {
									IEGLConstants.PROPERTY_FIELDLEN,
									canonicalContainerName
								});
						}
						else {
							if (!isValidForNumeric(value, fieldBinding)) {
								
								problemRequestor.acceptProblem(
										errorNode,
										IProblemRequestor.INVALID_NUMERIC_FORM_FIELD_FIELDLEN_PROPERTY_VALUE,
										IMarker.SEVERITY_WARNING,
										new String[] {
											IEGLConstants.PROPERTY_FIELDLEN,
											canonicalContainerName
										});
							}
						}
					}
					catch(NumberFormatException e) {
						//TODO: issue error?
					}
				}
			}
		}
	}
	
	private boolean isValidForNumeric(int value, FormFieldBinding formFieldBinding) {
		boolean result = true;
		ITypeBinding formFieldType = formFieldBinding.getType();
		if (formFieldType != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == formFieldType.getKind()) {
			PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) formFieldType; 
			Primitive prim = primTypeBinding.getPrimitive();
			
			if (Primitive.isNumericType(prim)) {
				result = value >= primTypeBinding.getLength();
			}
			
		}
		
		return result;
		
	}
	
	
	private boolean isValid(int value, FormFieldBinding formFieldBinding, int calculatedFieldLength)
	{
		boolean result = true;
		ITypeBinding formFieldType = formFieldBinding.getType();
		if (formFieldType != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == formFieldType.getKind()) {
			PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) formFieldType; 
			Primitive prim = primTypeBinding.getPrimitive();
			IAnnotationBinding format = null;
			IDataBinding dBinding;
			switch (prim.getType()) {				
				case Primitive.DATE_PRIMITIVE :
				    // Don't validate if we have no format specified
				    format = formFieldBinding.getAnnotation(new String[] {"egl", "ui"}, "DateFormat");
				    dBinding = format != null && format.getValue() instanceof IDataBinding ? (IDataBinding) format.getValue() : null;
					if(format != null && format.getValue() != null){
						//Don't validate if we have a default format
						if(AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "core"}, "StrLib", "DEFAULTDATEFORMAT")) {
							result = true;
						}
					    else{
					        result = calculatedFieldLength <= value;
					    }
					}else{
					    result = true;
					}
					break;
				case Primitive.TIME_PRIMITIVE :
				    // Don't validate if we have no format specified
					format = formFieldBinding.getAnnotation(new String[] {"egl", "ui"}, "TimeFormat");
					dBinding = format != null && format.getValue() instanceof IDataBinding ? (IDataBinding) format.getValue() : null;
					if(format != null && format.getValue() != null){
						//Don't validate if we have default format
						if(AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "core"}, "StrLib", "DEFAULTTIMEFORMAT")) {
							result = true;
						}					    
					    else{
					        result = calculatedFieldLength <= value;
					    }
					}else{
					    result = true;
					}
					break;
				case Primitive.TIMESTAMP_PRIMITIVE :
				    // Don't validate if we have no format specified
					format = formFieldBinding.getAnnotation(new String[] {"egl", "ui"}, "TimeStampFormat");
					dBinding = format != null && format.getValue() instanceof IDataBinding ? (IDataBinding) format.getValue() : null;
					if(format != null && format.getValue() != null){
						//Don't validate if we have default format
						if(AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "core"}, "StrLib", "DEFAULTTIMESTAMPFORMAT")) {
							result = true;
						}					    
					    else{
					        result = calculatedFieldLength <= value;
					    }
					}else{
					    result = true;
					}
					break;
				case Primitive.MONTHSPAN_INTERVAL_PRIMITIVE :
				case Primitive.SECONDSPAN_INTERVAL_PRIMITIVE :
				    result = calculatedFieldLength <= value;
					break;
				case Primitive.CHAR_PRIMITIVE :
				case Primitive.MBCHAR_PRIMITIVE :
					result = value == primTypeBinding.getLength();
					break;
				case Primitive.DBCHAR_PRIMITIVE :
					result = value == primTypeBinding.getBytes();
					break;
				default :
					result = (value >= 0);
			}
		}
		return result;
	}
}
