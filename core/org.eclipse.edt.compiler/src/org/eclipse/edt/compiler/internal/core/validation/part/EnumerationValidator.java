/*******************************************************************************
 * Copyright © 2005, 2010 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author Dave Murray
 */
public class EnumerationValidator extends AbstractASTVisitor {
	
	protected IProblemRequestor problemRequestor;
	private ICompilerOptions compilerOptions;
	
	public EnumerationValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(Enumeration enumeration) {
//		if(true) {
//			problemRequestor.acceptProblem(
//				enumeration.getName(),
//				IProblemRequestor.PART_OR_STATEMENT_NOT_SUPPORTED,
//				new String[] {
//					IEGLConstants.KEYWORD_ENUMERATION.toUpperCase()
//				});
//			return false;
//		}
		
		return true;
	}
}
