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

import org.eclipse.edt.compiler.binding.AnnotationValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;



/**
 * @author Dave Murray
 */
public class SQLVariableLenAnnotationValidator extends AnnotationValidationAnnotationTypeBinding {
	
	protected IAnnotationTypeBinding annotationType;
	protected String canonicalAnnotationName;
	
	public SQLVariableLenAnnotationValidator() {
		super(InternUtil.internCaseSensitive("SQLVariableLen"));
	}
	
	public void validate(final Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		IAnnotationBinding aBinding = (IAnnotationBinding) allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_SQLVARIABLELEN));
		if(Boolean.YES == aBinding.getValue()) {
			if (targetTypeBinding.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING ||
				(targetTypeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING && !StatementValidator.isStringCompatible(targetTypeBinding))){
				problemRequestor.acceptProblem(errorNode,
						IProblemRequestor.PROPERTY_STRING_PRIMITIVE_REQUIRED,
						new String[] {IEGLConstants.PROPERTY_SQLVARIABLELEN,
						targetTypeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING? ((PrimitiveTypeBinding)targetTypeBinding).getPrimitive().getName():""});
			}
		}
	}
}
