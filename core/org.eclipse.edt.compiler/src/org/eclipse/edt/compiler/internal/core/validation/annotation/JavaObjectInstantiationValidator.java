/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;

public class JavaObjectInstantiationValidator implements IInstantiationValidationRule {

	@Override
	public void validate(Node node, final Type type, Part declaringPart, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		
		if (type == null || declaringPart == null) {
			return;
		}
		
		node.accept(new DefaultASTVisitor() {
			public boolean visit(StructureItem structureItem) {
				if (!hasDefaultConstructor(type)) {
					errorNoDefaultConstructor(structureItem.getType());
				}
				return false;
			}
			
			public boolean visit(ClassDataDeclaration classDataDeclaration) {
				if (!hasDefaultConstructor(type)) {
					errorNoDefaultConstructor(classDataDeclaration.getType());
				}
				return false;
			}
			
			public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
				if (!hasDefaultConstructor(type)) {
					errorNoDefaultConstructor(functionDataDeclaration.getType());
				}
				return false;
			}
			
			public boolean visit(NewExpression newExpression) {
				if (newExpression.getType().resolveType() instanceof org.eclipse.edt.mof.egl.ArrayType) {
					ArrayType arrType = (ArrayType)newExpression.getType();
					if (arrType.hasInitialSize() && !BindingUtil.isZeroLiteral(arrType.getInitialSize())) {
						if (!hasDefaultConstructor(type)) {
							errorNoDefaultConstructor(arrType.getBaseType());
						}
					}
					
				}
				else {
					if(newExpression.getType().getBaseType() instanceof NameType) {
						NameType nameType = (NameType)newExpression.getType().getBaseType();
						if (!nameType.hasArguments() || nameType.getArguments().size() == 0) {
							if (!hasDefaultConstructor(type)) {
								errorNoDefaultConstructor(newExpression.getType());
							}
						}
					}
				}
				
				return false;
			}
			
			private void errorNoDefaultConstructor(Node errorNode) {
				problemRequestor.acceptProblem(
						errorNode, 
						IProblemRequestor.NO_DEFAULT_CONSTRUCTOR, 
						new String[] {type.getTypeSignature()});
			}
		});

	}

	
	private boolean hasDefaultConstructor(Type typeBinding) {
		if (!(typeBinding instanceof ExternalType)) {
			return false;
		}
		
		List<Constructor> list = ((ExternalType)typeBinding).getConstructors();
		if (list.size() == 0) {
			return true;
		}
		
		Iterator<Constructor> i = list.iterator();
		while (i.hasNext()) {
			Constructor constructor = i.next();
			if (constructor.getParameters().size() == 0) {
				return true;
			}
		}
		
		return false;
	}
}
