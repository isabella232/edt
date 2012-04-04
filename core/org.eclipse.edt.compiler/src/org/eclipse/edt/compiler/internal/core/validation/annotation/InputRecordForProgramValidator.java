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

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author 
 */
public class InputRecordForProgramValidator implements IValueValidationRule {

	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		if (target instanceof Program){
			Program program = (Program)target;
			if (program.getParameters().size()> 0){
				problemRequestor.acceptProblem(errorNode,
						IProblemRequestor.PROPERTY_CALLED_PROGRAM_INPUT_RECORD,
						new String[] {IEGLConstants.PROPERTY_INPUTRECORD});
			}
		}
//			if (StatementValidator.isValidBinding(containerBinding)){
//				IPartBinding part = containerBinding.getDeclaringPart();
//				if (part.getKind() == ITypeBinding.PROGRAM_BINDING)
//				{
//					ProgramBinding program = (ProgramBinding)part;
//					if (program.getParameters().size() > 0){
//						if (program.getAnnotation(BasicProgramAnnotationTypeBinding.getInstance()) != null ||
//							program.getAnnotation(TextUIProgramAnnotationTypeBinding.getInstance()) != null){
//							problemRequestor.acceptProblem(errorNode,
//									IProblemRequestor.PROPERTY_CALLED_PROGRAM_INPUT_RECORD,
//									new String[] {IEGLConstants.PROPERTY_INPUTRECORD});
//						}
//							
//						
//					}
//				}
//			}

	}
}
