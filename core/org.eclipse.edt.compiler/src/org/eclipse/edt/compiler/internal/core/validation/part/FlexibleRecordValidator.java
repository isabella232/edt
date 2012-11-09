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

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;


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
		
		TypeValidator.validateTypeDeclaration(structureItem.getType(), irBinding, problemRequestor);
		
		Type typeBinding = structureItem.getType().resolveType();
		if (typeBinding instanceof org.eclipse.edt.mof.egl.Record && containsReferenceTo((org.eclipse.edt.mof.egl.Record)typeBinding, recordBinding, new ArrayList<org.eclipse.edt.mof.egl.Record>())) {
			problemRequestor.acceptProblem(
					structureItem.getType(),
					IProblemRequestor.RECURSIVE_LOOP_STARTED_WITHIN_FLEXIBLE_RECORD_BY_TYPEDEF,
					new String[]{typeBinding.getTypeSignature()});
		}
		
		return false;
	}
	
	@Override
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
	
	private boolean containsReferenceTo(org.eclipse.edt.mof.egl.Record mainRecord, org.eclipse.edt.mof.egl.Record recordToLookFor, List<org.eclipse.edt.mof.egl.Record> processedRecords) {
		for (Field f : mainRecord.getFields()) {
			Type t = f.getType();
			if (t instanceof org.eclipse.edt.mof.egl.Record) {
				org.eclipse.edt.mof.egl.Record currentRecord = (org.eclipse.edt.mof.egl.Record)BindingUtil.realize((org.eclipse.edt.mof.egl.Record)t);
	            if (primContainsReferenceTo(recordToLookFor, processedRecords, currentRecord)) {
	                return true;
	            }
			}
        }
        return false;
	}
	
	private boolean primContainsReferenceTo(org.eclipse.edt.mof.egl.Record recordToLookFor, List<org.eclipse.edt.mof.egl.Record> processedRecords, org.eclipse.edt.mof.egl.Record currentRecord) {
        if (currentRecord.equals(recordToLookFor)) {
            return true;
        }
        if (!processedRecords.contains(currentRecord)) {
            processedRecords.add(currentRecord);
            if (containsReferenceTo((org.eclipse.edt.mof.egl.Record)currentRecord, recordToLookFor, processedRecords )) {
                return true;
            }
        }
        return false;
    }
}
