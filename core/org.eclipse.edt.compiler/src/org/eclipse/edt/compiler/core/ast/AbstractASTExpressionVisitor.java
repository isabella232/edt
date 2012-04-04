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
package org.eclipse.edt.compiler.core.ast;

/**
 * @author svihovec
 *  
 */
public abstract class AbstractASTExpressionVisitor extends AbstractASTVisitor {

	public boolean visitExpression(Expression expression) {
	    return false;
	}
	
	public void endVisitExpression(Expression expression) {
	    return;
	}
	
	public boolean visitName(Name name) {
	    return visitExpression(name);
	}
	
    public boolean visit(SimpleName simpleName) {
        return visitName(simpleName);
    }
    
    public boolean visit(QualifiedName qualifiedName) {
        return visitName(qualifiedName);
    }
    
    public boolean visit(AnnotationExpression annotationExpression) {
        return visitExpression(annotationExpression);
    }
    
    public boolean visit(ArrayAccess arrayAccess) {
        return visitExpression(arrayAccess);
    }
    
    public boolean visit(AsExpression asExpression) {
    	return visitExpression(asExpression);
    }
    
    public boolean visit(BinaryExpression binaryExpression) {
        return visitExpression(binaryExpression);
    }
    
    public boolean visit(FieldAccess fieldAccess) {
        return visitExpression(fieldAccess);
    }
    
    public boolean visit(FunctionInvocation functionInvocation) {
        return visitExpression(functionInvocation);
    }
    
    public boolean visit(InExpression inExpression) {
        return visitExpression(inExpression);
    }
    
    public boolean visit(IsAExpression isAExpression) {
        return visitExpression(isAExpression);
    }
    
    public boolean visit(IsNotExpression isNotExpression) {
        return visitExpression(isNotExpression);
    }
    
    public boolean visit(LikeMatchesExpression likeMatchesExpression) {
        return visitExpression(likeMatchesExpression);
    }
    
    public boolean visitLiteral(LiteralExpression literal) {
        return visitExpression(literal);
    }
    
    public boolean visit(ArrayLiteral arrayLiteral) {
        return visitLiteral(arrayLiteral);
    }

    public boolean visit(DecimalLiteral decimalLiteral) {
        return visitLiteral(decimalLiteral);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.core.ast.AbstractASTVisitor#visit(org.eclipse.edt.compiler.core.ast.FloatLiteral)
     */
    public boolean visit(FloatLiteral floatLiteral) {
        return visitLiteral(floatLiteral);
    }
    
     /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.core.ast.AbstractASTVisitor#visit(org.eclipse.edt.compiler.core.ast.IntegerLiteral)
     */
    public boolean visit(IntegerLiteral integerLiteral) {
        return visitLiteral(integerLiteral);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.core.ast.AbstractASTVisitor#visit(org.eclipse.edt.compiler.core.ast.NilLiteral)
     */
    public boolean visit(NullLiteral nilLiteral) {
        return visitLiteral(nilLiteral);
    }
    
    public boolean visit(ObjectExpression objExpr) {
    	return visitExpression(objExpr);
    }
    
    public boolean visit(SQLLiteral sQLLiteral) {
        return visitLiteral(sQLLiteral);
    }
    
    public boolean visit(StringLiteral stringLiteral) {
        return visitLiteral(stringLiteral);
    }
    
    public boolean visit(CharLiteral stringLiteral) {
        return visitLiteral(stringLiteral);
    }
    
    public boolean visit(DBCharLiteral stringLiteral) {
        return visitLiteral(stringLiteral);
    }
    
    public boolean visit(MBCharLiteral stringLiteral) {
        return visitLiteral(stringLiteral);
    }
    
    public boolean visit(HexLiteral stringLiteral) {
        return visitLiteral(stringLiteral);
    }
    
    public boolean visit(BooleanLiteral booleanLiteral) {
		return visitLiteral(booleanLiteral);
	}
    
    public boolean visit(NewExpression newExpression) {
        return visitExpression(newExpression);
    }
    
    public boolean visit(ParenthesizedExpression parenthesizedExpression) {
        return visitExpression(parenthesizedExpression);
    }
    
    public boolean visit(SetValuesExpression setValuesExpression) {
        return visitExpression(setValuesExpression);
    }
    
    public boolean visit(SubstringAccess substringAccess) {
        return visitExpression(substringAccess);
    }
    
    public boolean visit(ThisExpression thisExpression) {
        return visitExpression(thisExpression);
    }
    
    public boolean visit(UnaryExpression unaryExpression) {
        return visitExpression(unaryExpression);
    }
    
	public void endVisitName(Name name) {
	    endVisitExpression(name);
	}
	
    public void endVisit(SimpleName simpleName) {
        endVisitName(simpleName);
    }
    
    public void endVisit(QualifiedName qualifiedName) {
        endVisitName(qualifiedName);
    }
    
    public void endVisit(AnnotationExpression annotationExpression) {
        endVisitExpression(annotationExpression);
    }
    
    public void endVisit(ArrayAccess arrayAccess) {
        endVisitExpression(arrayAccess);
    }
    
    public void endVisit(AsExpression asExpression) {
        endVisitExpression(asExpression);
    }
    
    public void endVisit(BinaryExpression binaryExpression) {
        endVisitExpression(binaryExpression);
    }
    
    public void endVisit(FieldAccess fieldAccess) {
        endVisitExpression(fieldAccess);
    }
    
    public void endVisit(FunctionInvocation functionInvocation) {
        endVisitExpression(functionInvocation);
    }
    
    public void endVisit(InExpression inExpression) {
        endVisitExpression(inExpression);
    }
    
    public void endVisit(IsAExpression isAExpression) {
        endVisitExpression(isAExpression);
    }
    
    public void endVisit(IsNotExpression isNotExpression) {
        endVisitExpression(isNotExpression);
    }
    
    public void endVisit(LikeMatchesExpression likeMatchesExpression) {
        endVisitExpression(likeMatchesExpression);
    }
    
    public void endVisitLiteral(LiteralExpression literal) {
        endVisitExpression(literal);
    }
    
    public void endVisit(ArrayLiteral arrayLiteral) {
        endVisitLiteral(arrayLiteral);
    }

    public void endVisit(DecimalLiteral decimalLiteral) {
        endVisitLiteral(decimalLiteral);
    }
    
    public void endVisit(FloatLiteral floatLiteral) {
        endVisitLiteral(floatLiteral);
    }
    
    public void endVisit(IntegerLiteral integerLiteral) {
        endVisitLiteral(integerLiteral);
    }
    
    public void endVisit(NullLiteral nilLiteral) {
        endVisitLiteral(nilLiteral);
    }
    
    public void endVisit(SQLLiteral sQLLiteral) {
        endVisitLiteral(sQLLiteral);
    }
    
    public void endVisit(StringLiteral stringLiteral) {
        endVisitLiteral(stringLiteral);
    }
    
    public void endVisit(CharLiteral stringLiteral) {
        endVisitLiteral(stringLiteral);
    }
    
    public void endVisit(DBCharLiteral stringLiteral) {
        endVisitLiteral(stringLiteral);
    }
    
    public void endVisit(MBCharLiteral stringLiteral) {
        endVisitLiteral(stringLiteral);
    }
    
    public void endVisit(HexLiteral stringLiteral) {
        endVisitLiteral(stringLiteral);
    }
    
    public void endVisit(BooleanLiteral booleanLiteral) {
		endVisitLiteral(booleanLiteral);
	}
    
    public void endVisit(NewExpression newExpression) {
        endVisitExpression(newExpression);
    }
    
    public void endVisit(ParenthesizedExpression parenthesizedExpression) {
        endVisitExpression(parenthesizedExpression);
    }
    
    public void endVisit(SetValuesExpression setValuesExpression) {
        endVisitExpression(setValuesExpression);
    }
    
    public void endVisit(SubstringAccess substringAccess) {
        endVisitExpression(substringAccess);
    }
    
    public void endVisit(ThisExpression thisExpression) {
        endVisitExpression(thisExpression);
    }
    
    public void endVisit(UnaryExpression unaryExpression) {
        endVisitExpression(unaryExpression);
    }
    
    public void endVisit(ObjectExpression objExpr) {
    	endVisitExpression(objExpr);
    }
}
