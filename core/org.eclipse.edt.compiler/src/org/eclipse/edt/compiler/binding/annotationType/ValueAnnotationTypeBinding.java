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

import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.CharLiteral;
import org.eclipse.edt.compiler.core.ast.DBCharLiteral;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.HexLiteral;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.MBCharLiteral;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class ValueAnnotationTypeBinding extends AnnotationTypeBinding {
    
    private static class LiteralChecker extends AbstractASTExpressionVisitor {
        boolean valid = false;

        public boolean isValid(Expression expression) {
            expression.accept(this);
            return valid;
        }
        
        public boolean visit(IntegerLiteral lit) {
            valid = true;
            return false;
        }

        public boolean visit(FloatLiteral lit) {
            valid = true;
            return false;
        }

        public boolean visit(DecimalLiteral lit) {
            valid = true;
            return false;
        }

        public boolean visit(StringLiteral lit) {
            valid = true;
            return false;
        }
        
        public boolean visit(HexLiteral lit) {
            valid = true;
            return false;
        }
        
        public boolean visit(CharLiteral lit) {
            valid = true;
            return false;
        }
        
        public boolean visit(DBCharLiteral lit) {
            valid = true;
            return false;
        }
        
        public boolean visit(MBCharLiteral lit) {
            valid = true;
            return false;
        }

        //arrays are not valid for property 'value'
//        public boolean visit(ArrayLiteral lit) {
//           valid = true;
//            return false;
//        }
        
        public boolean visit(BinaryExpression binaryExpression) {
			return binaryExpression.getOperator() == BinaryExpression.Operator.PLUS;
		}
        
        public boolean visit(UnaryExpression unaryExpression) {
			return unaryExpression.getOperator() == UnaryExpression.Operator.MINUS ||
			       unaryExpression.getOperator() == UnaryExpression.Operator.PLUS;
		}
    }
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("value");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static ValueAnnotationTypeBinding INSTANCE = new ValueAnnotationTypeBinding();
	
	private ValueAnnotationTypeBinding() {
		super(caseSensitiveName, PrimitiveTypeBinding.getInstance(Primitive.ANY));
	}
	
	public static ValueAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return takesPageItemAnnotations(binding) || takesFormFieldAnnotations(binding);
	}
	
	private Object readResolve() {
		return INSTANCE;
	}

	public boolean supportsElementOverride() {
        return true;
    }
	
	public ITypeBinding getSingleValueType() {
		return PrimitiveTypeBinding.getInstance(Primitive.ANY);
	}
}
