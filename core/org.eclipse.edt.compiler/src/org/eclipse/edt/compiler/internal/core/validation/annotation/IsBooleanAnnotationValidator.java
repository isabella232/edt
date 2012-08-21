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

import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;




/**
 * @author 
 */
public class IsBooleanAnnotationValidator extends AnnotationValidationRule {
	
	protected IAnnotationTypeBinding annotationType;
	protected String canonicalAnnotationName;
	
	public IsBooleanAnnotationValidator() {
		super(InternUtil.internCaseSensitive("isboolean"));
	}
	
	private static final Primitive[] acceptedPrimitives = new Primitive[11];
	static{
		acceptedPrimitives[0]=(Primitive.CHAR);
		acceptedPrimitives[1]=(Primitive.BIN);
		acceptedPrimitives[2]=(Primitive.NUM);
		acceptedPrimitives[3]=(Primitive.NUMC);
		acceptedPrimitives[4]=(Primitive.DECIMAL);
		acceptedPrimitives[5]=(Primitive.PACF);
		acceptedPrimitives[6]=(Primitive.INT);
		acceptedPrimitives[7]=(Primitive.BIGINT);
		acceptedPrimitives[8]=(Primitive.SMALLINT);
		acceptedPrimitives[9]=(Primitive.FLOAT);
		acceptedPrimitives[10]=(Primitive.MONEY);
	}
	
	public void validate(final Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		if (targetTypeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			PrimitiveTypeBinding primitiveTypeBinding = (PrimitiveTypeBinding)targetTypeBinding;
			Primitive prim = (primitiveTypeBinding).getPrimitive();
			
			for (int i = 0; i < acceptedPrimitives.length; i++){
				if (prim == acceptedPrimitives[i]){
					if(primitiveTypeBinding.getLength() != 0 && primitiveTypeBinding.getDecimals() == primitiveTypeBinding.getLength()) {
						problemRequestor.acceptProblem(errorNode,
							IProblemRequestor.PROPERTY_REQUIRES_NONDECIMAL_DIGITS,
							new String[]{
								IEGLConstants.PROPERTY_ISBOOLEAN,
								StatementValidator.getTypeString(primitiveTypeBinding)
							}
						);;
						
					}
					return;
				}
			}
			
			problemRequestor.acceptProblem(errorNode,
						IProblemRequestor.PROPERTY_ONLY_VALID_FOR_PRIMITIVE_LIST,
						new String[]{IEGLConstants.PROPERTY_ISBOOLEAN,
						getCommaList()});
			
		}

	}

	private String getCommaList() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < acceptedPrimitives.length; i++){
			sb.append( acceptedPrimitives[i].getName() );
			if( i < acceptedPrimitives.length -1  ) {
				sb.append( ", " );
			}
		}
		return sb.toString();
	}
}
