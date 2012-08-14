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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.Type;

public class UseStatementValidator extends DefaultASTVisitor {
	
	private IPartBinding parent;
	private String canonicalParentName;
	private IProblemRequestor problemRequestor;
	
	public UseStatementValidator(IPartBinding binding, String canonicalParentName, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		parent = binding;
		this.problemRequestor = problemRequestor;
		this.canonicalParentName = canonicalParentName;
	}
	
	public boolean visit(final UseStatement useStatement) {
		if (parent == null || useStatement.getNames().size() == 0){
			return false;
		}
		
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(useStatement); TODO
		
		useStatement.getParent().accept(new AbstractASTPartVisitor() {
			@Override
			public boolean visit(Service service) {
				validateUseTypes(IEGLConstants.KEYWORD_SERVICE);
				return false;
			}
			
			@Override
			public boolean visit(Handler handler){
				validateUseTypes(IEGLConstants.KEYWORD_HANDLER);
				return false;
			}
			
			@Override
			public boolean visit(EGLClass eglClass) {
				validateUseTypes(IEGLConstants.KEYWORD_CLASS);
				return false;
			};
			
			@Override
			public boolean visit(Program program){
				validateUseTypes(IEGLConstants.KEYWORD_PROGRAM);
				return false;
			}
			
			@Override
			public boolean visit(Library library){
				validateUseTypes(IEGLConstants.KEYWORD_LIBRARY);
				return false;
			}
			
			private void validateUseTypes(String type) {
				for (Name name : useStatement.getNames()) {
					Type typeBinding = name.resolveType();
					if (typeBinding != null && !(typeBinding instanceof Enumeration) && !(typeBinding instanceof org.eclipse.edt.mof.egl.Library)) {
						problemRequestor.acceptProblem(name,
								IProblemRequestor.USE_STATEMENT_RESOLVES_TO_INVALID_TYPE,
								new String[] {name.getCanonicalName(),
								type,
								canonicalParentName});
					}
				}
			}
			
			@Override
			public void visitPart(Part part) {
			}
		});

		return false;
	}
}
