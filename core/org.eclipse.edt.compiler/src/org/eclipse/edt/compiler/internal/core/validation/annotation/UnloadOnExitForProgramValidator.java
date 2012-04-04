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
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;



/**
 * @author Dave Murray
 */
public class UnloadOnExitForProgramValidator implements IValueValidationRule {
	
	public void validate(final Node errorNode, Node target, IAnnotationBinding annotationBinding, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (target instanceof Program){
			Program program = (Program)target;
			if (!program.isCallable()) {
				problemRequestor.acceptProblem(errorNode,
						IProblemRequestor.UNLOADONEXIT_NOT_VALID_IN_MAIN,
						new String[] {});
			}
		}

	
	}
	
}
