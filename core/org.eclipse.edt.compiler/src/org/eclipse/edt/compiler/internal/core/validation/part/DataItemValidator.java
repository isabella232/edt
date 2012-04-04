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

import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.PrimitiveType;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.type.PrimitiveTypeValidator;


/**
 * @author Dave Murray
 */
public class DataItemValidator extends AbstractASTVisitor {
	
	protected IProblemRequestor problemRequestor;
	private ICompilerOptions compilerOptions;
	
	public DataItemValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(DataItem dataItem) {
		EGLNameValidator.validate(dataItem.getName(), EGLNameValidator.DATAITEM, problemRequestor, compilerOptions);
		
		if(dataItem.getType().isPrimitiveType()) {
			PrimitiveTypeValidator.validate((PrimitiveType) dataItem.getType(), problemRequestor, compilerOptions);
		}
		return true;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
}
