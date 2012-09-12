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

import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public class RUIPropertiesLibraryValidator implements IAnnotationValidationRule {
	
	@Override
	public void validate(Node errorNode, Node target, Element targetElement, Map<String, Object> allAnnotationsAndFields, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {

		//validate that this library only contains fields of type String
		
		DefaultASTVisitor visitor = new DefaultASTVisitor() {
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.Library library) {
				return true;
			}
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.NestedFunction nestedFunction) {
				problemRequestor.acceptProblem(nestedFunction, IProblemRequestor.ONLY_STRING_FIELDS_ALLOWED, IMarker.SEVERITY_ERROR, new String[] {});
				return false;
			}
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.UseStatement useStatement) {
				problemRequestor.acceptProblem(useStatement, IProblemRequestor.ONLY_STRING_FIELDS_ALLOWED, IMarker.SEVERITY_ERROR, new String[] {});
				return false;
			}
			@Override
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
		
		Type type = classDataDeclaration.getType().resolveType();
		
		if (type == null) {
			return false;
		}
		
		if (TypeUtils.Type_STRING.equals(type.getClassifier())) {
			return true;
		}
		
		return false;
	}
}
