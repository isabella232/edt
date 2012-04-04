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
package org.eclipse.edt.compiler.binding.annotationType;

import java.util.Iterator;

import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class ContentsAnnotationTypeBinding extends AnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("contents");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static ContentsAnnotationTypeBinding INSTANCE = new ContentsAnnotationTypeBinding();
	
	private ContentsAnnotationTypeBinding() {
		super(caseSensitiveName, PrimitiveTypeBinding.getInstance(Primitive.ANY));
	}
	
	public static ContentsAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return binding.isTypeBinding() && ((ITypeBinding) binding).getKind() == ITypeBinding.DATATABLE_BINDING;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public ITypeBinding getSingleValueType() {
		return PrimitiveTypeBinding.getInstance(Primitive.ANY);
	}
	
	private static class TwoDimensionArrayOfExpressionsChecker extends DefaultASTVisitor {
		boolean isTwoDimensionalArrayOfLiterals = false;

		void visitExpression(Expression expr) {
			expr.accept(this);
		}
		
		public boolean visit(ArrayLiteral arrayLiteral) {
			for(Iterator iter = arrayLiteral.getExpressions().iterator(); iter.hasNext();) {
				OneDimensionArrayOfExpressionsChecker checker = new OneDimensionArrayOfExpressionsChecker();
				checker.visitExpression((Expression) iter.next());
				if(!checker.isOneDimensionalArrayOfLiterals) {
					return false;
				}
			}
			isTwoDimensionalArrayOfLiterals = true;
			return false;
		}
	}
	
	private static class OneDimensionArrayOfExpressionsChecker extends DefaultASTVisitor {
		boolean isOneDimensionalArrayOfLiterals = false;
		
		void visitExpression(Expression expr) {
			expr.accept(this);
		}
		
		public boolean visit(ArrayLiteral arrayLiteral) {
			isOneDimensionalArrayOfLiterals = true;
			return false;
		}
	}
}
