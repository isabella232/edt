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

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.IASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;


public class TypeNameUtility {
	public static String getName(Node node) {
		
		final String[] name = new String[1];
		IASTVisitor visitor = new AbstractASTPartVisitor() {

			public void visitPart(Part part) {
				name[0] = part.getName().getCanonicalName();
				
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.ClassDataDeclaration classDataDeclaration) {
				name[0] = classDataDeclaration.getType().getCanonicalName();
				return false;
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration functionDataDeclaration) {
				name[0] = functionDataDeclaration.getType().getCanonicalName();
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.StructureItem structureItem) {
				if (structureItem.hasType()) {
					name[0] = structureItem.getType().getCanonicalName();
				}
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.NameType nameType) {
				name[0] = nameType.getCanonicalName();
				return false;
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.PrimitiveType primitiveType) {
				name[0] = primitiveType.toString();
				return false;
			}
		
		};
		
		if (node != null) {
			node.accept(visitor);
		}
		
		if (name[0] == null) {
			Part part = getEnclosingPart(node);
			if (part != null) {
				return part.getName().getCanonicalName();
			}
			else {
				return "";
			}
		}
		return name[0];
		
	}
	
	public static ITypeBinding getType(Node node) {
		
		final ITypeBinding[] type = new ITypeBinding[1];
		IASTVisitor visitor = new AbstractASTPartVisitor() {

			public void visitPart(Part part) {
				type[0] = (ITypeBinding)part.getName().resolveBinding();
				
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.ClassDataDeclaration classDataDeclaration) {
				type[0] = classDataDeclaration.getType().resolveTypeBinding();
				return false;
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration functionDataDeclaration) {
				type[0] = functionDataDeclaration.getType().resolveTypeBinding();
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.StructureItem structureItem) {
				if (structureItem.hasType()) {
					type[0] = structureItem.getType().resolveTypeBinding();
				}
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.NameType nameType) {
				type[0] = nameType.resolveTypeBinding();
				return false;
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.PrimitiveType primitiveType) {
				type[0] = primitiveType.resolveTypeBinding();
				return false;
			}
		
		};
		
		node.accept(visitor);
		
		return type[0];
		
	}

	
	private static Part getEnclosingPart(Node node) {
		if(node == null || node instanceof Part) {
			return (Part) node;			
		}
		return getEnclosingPart(node.getParent());
	}


}
