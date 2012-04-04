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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


public class RUIPropertiesLibraryValidator implements IAnnotationValidationRule {
	
		
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {

		//validate that this library only contains fields of type String
		
		DefaultASTVisitor visitor = new DefaultASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.Library library) {
				return true;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.NestedFunction nestedFunction) {
				problemRequestor.acceptProblem(nestedFunction, IProblemRequestor.ONLY_STRING_FIELDS_ALLOWED, IMarker.SEVERITY_ERROR, new String[] {});
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.UseStatement useStatement) {
				problemRequestor.acceptProblem(useStatement, IProblemRequestor.ONLY_STRING_FIELDS_ALLOWED, IMarker.SEVERITY_ERROR, new String[] {});
				return false;
			}
			public boolean visit(ClassDataDeclaration classDataDeclaration) {
				if (!isValidInRUIPropertiesLibrary(classDataDeclaration)) {
					problemRequestor.acceptProblem(classDataDeclaration, IProblemRequestor.ONLY_STRING_FIELDS_ALLOWED, IMarker.SEVERITY_ERROR, new String[] {});
				}
				return false;
			}
			
		};
		target.accept(visitor);
		
	}
	
	private boolean isValidInRUIPropertiesLibrary(ClassDataDeclaration classDataDeclaration) {		
		if (classDataDeclaration.isConstant() || classDataDeclaration.isPrivate()) {
			return false;
		}
		
		ITypeBinding type = classDataDeclaration.getType().resolveTypeBinding();
		
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		
		if (type == PrimitiveTypeBinding.getInstance(Primitive.STRING)) {
			return true;
		}
		
		return false;
	}
}
