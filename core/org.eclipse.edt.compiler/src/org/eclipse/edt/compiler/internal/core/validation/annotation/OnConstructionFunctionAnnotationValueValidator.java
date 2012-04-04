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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;


/**
 * @author svihovec
 *
 */
public class OnConstructionFunctionAnnotationValueValidator implements IValueValidationRule {

	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (annotationBinding.getValue() != null && annotationBinding.getValue() != IBinding.NOT_FOUND_BINDING) {
			if (annotationBinding.getValue() instanceof FunctionBinding){
				FunctionBinding value = (FunctionBinding)annotationBinding.getValue();
				boolean parameterTypesValid = true;
				String invalidParameterName = null;
				ITypeBinding invalidParameterType = null;
				
				for(Iterator iter = value.getParameters().iterator(); iter.hasNext() && parameterTypesValid;) {
					IDataBinding parm = (IDataBinding) iter.next();
					ITypeBinding type = parm.getType();
					if(type != null) {
						if(!parameterTypeIsValid(type)) {
							parameterTypesValid = false;
							invalidParameterName = parm.getCaseSensitiveName();
							invalidParameterType = type;
						}
					}
				}
				
				if(!parameterTypesValid) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.ONPAGELOADFUNCTION_PARAMETER_HAS_INVALID_TYPE,
						new String[] {
							StatementValidator.getShortTypeString(invalidParameterType),
							invalidParameterName,
							value.getName()
						});
				}
				
				IPartBinding valueDeclarer = value.getDeclarer();
				if(valueDeclarer != null && annotationBinding.getDeclaringPart() != valueDeclarer) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.LIBRARY_FUNCTION_NOT_ALLOWED_FOR_PROPERTY,
						new String[] {
							IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION,
							annotationBinding.getDeclaringPart().getCaseSensitiveName()
						});
				}
			}	
		}
	}
	
	private Set validPrimitives = new HashSet(Arrays.asList(new Primitive[] {
		Primitive.BIN,
		Primitive.INT,
		Primitive.SMALLINT,
		Primitive.BIGINT,
		Primitive.BOOLEAN,
		Primitive.CHAR,
		Primitive.DATE,
		Primitive.DBCHAR,
		Primitive.DECIMAL,
		Primitive.FLOAT,
		Primitive.HEX,
		Primitive.INTERVAL,
		Primitive.MBCHAR,
		Primitive.MONEY,
		Primitive.NUM,
		Primitive.NUMC,
		Primitive.PACF,
		Primitive.SMALLFLOAT,
		Primitive.STRING,
		Primitive.TIME,
		Primitive.TIMESTAMP,
		Primitive.UNICODE
	}));

	private boolean parameterTypeIsValid(ITypeBinding type) {
		if(type == null || IBinding.NOT_FOUND_BINDING == type) {
			return true;
		}
		switch(type.getKind()) {
			case ITypeBinding.ARRAY_TYPE_BINDING:
				return parameterTypeIsValid(type.getBaseType());
			case ITypeBinding.PRIMITIVE_TYPE_BINDING:
				return validPrimitives.contains(((PrimitiveTypeBinding) type).getPrimitive());
			case ITypeBinding.FIXED_RECORD_BINDING:
				return true;
			case ITypeBinding.FLEXIBLE_RECORD_BINDING:
				boolean typeValid = true;
				IDataBinding[] fields = ((FlexibleRecordBinding) type).getFields();
				for(int i = 0; i < fields.length && typeValid; i++) {
					typeValid = parameterTypeIsValid(fields[i].getType());
				}
				return typeValid;
			default:
				return false;
		}
	}
}
