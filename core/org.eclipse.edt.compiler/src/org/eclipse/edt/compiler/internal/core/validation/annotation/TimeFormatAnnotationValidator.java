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
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;



/**
 * @author 
 */
public class TimeFormatAnnotationValidator extends AnnotationValidationAnnotationTypeBinding {
	
	protected IAnnotationTypeBinding annotationType;
	protected String canonicalAnnotationName;
	
	public TimeFormatAnnotationValidator() {
		super(InternUtil.internCaseSensitive("timeformat"));
	}
	
	public void validate(final Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		if (targetTypeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			Primitive prim = ((PrimitiveTypeBinding)targetTypeBinding).getPrimitive();
			if (prim == Primitive.DBCHAR ||
				prim == Primitive.MBCHAR ||
				prim == Primitive.HEX ||
				prim == Primitive.UNICODE ||
				prim == Primitive.CLOB ||
				prim == Primitive.BLOB || 
				prim == Primitive.MONTHSPAN_INTERVAL ||
				prim == Primitive.SECONDSPAN_INTERVAL ||
				prim == Primitive.DATE ||
				prim == Primitive.TIMESTAMP ||
				prim == Primitive.BOOLEAN){
				problemRequestor.acceptProblem(errorNode,
						IProblemRequestor.PROPERTY_DATEFORMAT_INVALID_PRIMITIVE_TYPE,
						new String[]{IEGLConstants.PROPERTY_TIMEFORMAT,
						prim.getName()});
			}else if (((PrimitiveTypeBinding)targetTypeBinding).getDecimals() > 0){
				problemRequestor.acceptProblem(errorNode,
						IProblemRequestor.PROPERTY_DATEFORMAT_INVALID_DECIMALS,
						new String[]{IEGLConstants.PROPERTY_TIMEFORMAT});
			
			}
		}
		
	}


}
