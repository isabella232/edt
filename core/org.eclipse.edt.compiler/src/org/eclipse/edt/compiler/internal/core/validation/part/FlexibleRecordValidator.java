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
package org.eclipse.edt.compiler.internal.core.validation.part;

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.FieldValidator;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Member;


public class FlexibleRecordValidator extends AbstractASTVisitor {
	
	protected IProblemRequestor problemRequestor;
	IRPartBinding irBinding;
	org.eclipse.edt.mof.egl.Record recordBinding;
	private Name recordNameNode;
    private ICompilerOptions compilerOptions;
	
	public FlexibleRecordValidator(IProblemRequestor problemRequestor, IRPartBinding irBinding, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
		this.irBinding = irBinding;
		this.recordBinding = (org.eclipse.edt.mof.egl.Record)irBinding.getIrPart();
	}
	
	public boolean visit(Record record) {
		recordNameNode = record.getName();
		EGLNameValidator.validate(recordNameNode, EGLNameValidator.RECORD, problemRequestor, compilerOptions);
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(record);		
		
		return true;
	}
	
	public boolean visit(StructureItem structureItem) {
		if (!structureItem.isEmbedded()) {
			Member binding = structureItem.resolveMember();
			if (binding instanceof Field) {
//				Field itemBinding = (Field) binding;
//				
//				if (structureItem.hasInitializer()) {
//					// TODO update to a new type compatibility implementation
//					Expression expression = structureItem.getInitializer();
//					ITypeBinding expressionTBinding = expression.resolveTypeBinding();
//					if (expressionTBinding != null && expressionTBinding != IBinding.NOT_FOUND_BINDING) {
//						if (!TypeCompatibilityUtil.isMoveCompatible(itemBinding.getType(), expressionTBinding, expression, compilerOptions) &&
//						   !expressionTBinding.isDynamic() &&
//						   !TypeCompatibilityUtil.areCompatibleExceptions(expressionTBinding, itemBinding.getType(), compilerOptions)) {
//							problemRequestor.acceptProblem(
//								expression,
//								IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
//								new String[] {
//									StatementValidator.getShortTypeString(itemBinding.getType()),
//									StatementValidator.getShortTypeString(expressionTBinding),										
//									structureItem.isFiller() ? "*" : structureItem.getName().getCanonicalName() + "=" + expression.getCanonicalString()
//								});
//						}
//					}
//				}
			}
		}
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(structureItem);
		structureItem.accept(new FieldValidator(problemRequestor, compilerOptions, irBinding));
		
		//TODO StatementValidator has many errors
//		if (structureItem.hasType()) {
//			StatementValidator.validateDataDeclarationType(structureItem.getType(), problemRequestor, recordBinding);
//		}
		
		return false;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
}
