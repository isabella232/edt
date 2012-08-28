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
import org.eclipse.edt.compiler.internal.core.validation.ExpressionValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.FieldValidator;


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
	
	@Override
	public boolean visit(Record record) {
		recordNameNode = record.getName();
		EGLNameValidator.validate(recordNameNode, EGLNameValidator.RECORD, problemRequestor, compilerOptions);
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(record);
		record.accept(new ExpressionValidator(irBinding, problemRequestor, compilerOptions));
		
		return true;
	}
	
	@Override
	public boolean visit(StructureItem structureItem) {
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(structureItem);
		structureItem.accept(new FieldValidator(problemRequestor, compilerOptions, irBinding));
		
		//TODO StatementValidator has many errors
//		if (structureItem.hasType()) {
//			StatementValidator.validateDataDeclarationType(structureItem.getType(), problemRequestor, recordBinding);
//		}
		
		return false;
	}
	
	@Override
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
}
