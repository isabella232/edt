/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package  org.eclipse.edt.mof.egl.utils;

import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.MultiOperandExpression;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Type;

public class ArithmeticTypeAnalyzer {
	
	private static final int maxLen = 32;
	
//
//	 i
//	    The number of integer places carried for an intermediate result.
//
//	d
//	    The number of decimal places carried for an intermediate result. 
//
//	dmax
//	    In a particular statement, the the maximum number of decimal places defined for any operand, except divisors or exponents
//
//	 op1
//	    The first operand in a generated arithmetic statement (in division, the divisor).
//
//	op2
//	    The second operand in a generated arithmetic statement (in division, the dividend).
//
//	i1, i2
//	    The number of integer places in op1 and op2, respectively.
//
//	d1, d2
//	    The number of decimal places in op1 and op2, respectively.
//
//	maxLen
//	    The maximum length of a decimal item supported for any given target system
//
//
//	The following table shows the precision theoretically possible as the result of addition, subtraction, multiplication, or division. 
//		
//	 Operation    	 Integer places               				Decimal places     
//	        
//	 + or -				(i1 or i2) + 1, whichever is greater		d1 or d2, whichever is greater                    
//	 *            	 	i1 + i2                   		   			d1 + d2                    
//	 / 					i2 + d1										(d2 - d1) or dmax whichever is greater       
//
//
//
//	Value of i  + d			Value of d		 Value of i +  dmax		Number of places carried for ir  
//
//	<=maxLen				any value			any value			i integer and d decimal places
//
//	>maxLen					<= dmax				any value			maxLen-d integer and d decimal places
//
//	>maxLen					> dmax				<= maxLen			i integer and maxLen-i decimal places
//
//	>maxLen					> dmax				> maxLen			maxLen-dmax integer and dmax decimal places
//
//
//
//
//	 Exponentiation is represented by the expression op1 ** op2. Based on the characteristics of op2, exponentiation of fixed-point numbers is handled in one of 2 ways:
//
//	  * When op2 is an integral literal or constant, the value d is computed as
//			d = d1 * |op2|
//		and the value i is computed as:
//			i = i * |op2|	
//
//		if (i + d <= maxLen) then i integer places and d decimal places, otherwise:
//			maxLen - dmax integer places, dmax decimal places
//
//	    * When op2 is a variable, maxLen - dmax integer places, dmax decimal places
//
//
//
//	Modulo is expressed as op1 % op2
//	  If op1 has no decimal places, then the type of op2 is returned
//
//	  i = i1,i2 integer places whichever is smaller and d = d1,d2 decimal places, whichever is greater
//		if (i + d <= maxLen) then i integer places and d decimal places, otherwise:
//		maxLen - dmax integer places, dmax decimal places
//	
	
	
	private static boolean needsComputedType(BinaryExpression exp, Type LHSType, Type RHSType) {
		
		if (exp.getType() != IRUtils.getEGLPrimitiveType(MofConversion.Type_Decimal)) {
			return false;
		}
		
		if (!isArithmeticOperator(exp.getOperator())) {
			return false;
		}
		
		if (exp.getLHS() == null || !TypeUtils.isNumericType(LHSType) || TypeUtils.isReferenceType(LHSType) || getLength(LHSType) == 0) {
			return false;
		}
		
		if (exp.getRHS() == null || !TypeUtils.isNumericType(RHSType) || TypeUtils.isReferenceType(RHSType) || getLength(RHSType) == 0) {
			return false;
		}		
		
		return true;
	}
	
	private static boolean isArithmeticOperator(String o) {
		  
		if (o == null) {
			return false;
		}
		
		//PLUS, MINUS, DIVIDE, TIMES, TIMESTIMES, MODULO 		
		return 
			o.equals(MultiOperandExpression.Op_PLUS) ||
			o.equals(MultiOperandExpression.Op_MINUS) ||
			o.equals(MultiOperandExpression.Op_DIVIDE) ||
			o.equals(MultiOperandExpression.Op_MULTIPLY) ||
			o.equals(MultiOperandExpression.Op_POWER) ||
			o.equals(MultiOperandExpression.Op_MODULO);
	}
	
	
	public static Type getType(Expression exp) {
		
		if (!(exp instanceof BinaryExpression)) {
			return exp.getType();
		}
		
		
		BinaryExpression binExp = (BinaryExpression) exp;
		Expression exp1, exp2;		
		String operator = binExp.getOperator();

		
		if (operator.equals(MultiOperandExpression.Op_DIVIDE)) {
			exp1 = binExp.getRHS();
			exp2 = binExp.getLHS();			
		}
		else {
			exp1 = binExp.getLHS();
			exp2 = binExp.getRHS();			
		}						
		
		Type op1 = getType(exp1);
		Type op2 = getType(exp2);

		if (op1 == null || op2 == null) {
			return exp.getType();
		}

		if (!needsComputedType(binExp, op1, op2)) {
			return exp.getType();
		}
		
						
		int dmax = getDmax(op1, op2, operator);

		int i1 = getIntDigits(exp1, op1);
		int i2 = getIntDigits(exp2, op2);

		int d1 = getDecimals(op1);
		int d2 = getDecimals(op2);

		
//	MODULO
//		  If op1 has no decimal places, then the type of op2 is returned
//
//			  i = i1,i2 integer places whichever is smaller and d = d1,d2 decimal places, whichever is greater
//				if (i + d <= maxLen) then i integer places and d decimal places, otherwise:
//				maxLen - dmax integer places, dmax decimal places
//			
		if (operator.equals(MultiOperandExpression.Op_MODULO)) {

			if (d1 == 0) {
				return op2;
			}
			
			int i = min(i1, i2);
			int d = dmax;
			if (i + d <= maxLen) {
				return IRUtils.getEGLPrimitiveType(MofConversion.Type_Decimal, i+d, d);
			}	
			else {
				return IRUtils.getEGLPrimitiveType(MofConversion.Type_Decimal, maxLen, dmax);
			}
		}
		
		
// EXPONENTIATION
//		 Exponentiation is represented by the expression op1 ** op2. Based on the characteristics of op2, exponentiation of fixed-point numbers is handled in one of 2 ways:
//
//			  * When op2 is an integral literal or constant, the value d is computed as
//					d = d1 * |op2|
//				and the value i is computed as:
//					i = i1 * |op2|	
//
//				if (i + d <= maxLen) then i integer places and d decimal places, otherwise:
//					maxLen - dmax integer places, dmax decimal places
//
//			    * When op2 is a variable, maxLen - dmax integer places, dmax decimal places

			
		if (operator.equals(MultiOperandExpression.Op_POWER)) {
			IntegerLiteral lit = null;

			if (binExp.getRHS() instanceof IntegerLiteral) {
				lit = (IntegerLiteral) binExp.getRHS();
			}
			else {
				if (binExp.getRHS() instanceof Name && ((Name)binExp.getRHS()).getNamedElement() instanceof ConstantField) {
					ConstantField constant = (ConstantField) ((Name)binExp.getRHS()).getNamedElement();
					if (constant.getValue() instanceof IntegerLiteral) {
						lit = (IntegerLiteral) constant.getValue();
					}
				}
			}

			if (lit != null) {
				int value = Integer.parseInt(lit.getUnsignedValue());
				int d, i;
				i = i1 * value;
				d = d1 * value;
				
				if (i + d <= maxLen) {
					return IRUtils.getEGLPrimitiveType(MofConversion.Type_Decimal, i+d, d);
				}	
				else {
					return IRUtils.getEGLPrimitiveType(MofConversion.Type_Decimal, maxLen, dmax);
				}
			}
			else {
				return IRUtils.getEGLPrimitiveType(MofConversion.Type_Decimal, maxLen, dmax);
			}
		}

		
//		The following table shows the precision theoretically possible as the result of addition, subtraction, multiplication, or division. 
//		
//	 Operation    	 Integer places               		Decimal places     
//	        
//	 + or -		(i1 or i2) + 1, whichever is greater	d1 or d2, whichever is greater                    
//	 *            	 i1 + i2                   		   	d1 + d2                    
//	 / 		i2 + d1					(d2 - d1) or dmax whichever is greater       
//
		int d;
		int i;
		if (operator.equals(MultiOperandExpression.Op_PLUS) || operator.equals(MultiOperandExpression.Op_MINUS)) {
			i = max(i1, i2) + 1;
			d = max(d1, d2);
			
		}
		else{
			if (operator.equals(MultiOperandExpression.Op_MULTIPLY)) {
				i = i1 + i2;
				d = d1 + d2;
			}
			else {
				//must be divide
				i = i2 + d1;
				d = max((d2 - d1), dmax);
			}
		}
		
//		Value of i  + d			Value of d		 Value of i +  dmax		Number of places carried for ir  
//
//			<=maxLen			any value		any value			i integer and d decimal places
//
//			>maxLen			<= dmax		any value			maxLen-d integer and d decimal places
//
//			>maxLen			> dmax			<= maxLen			i integer and maxLen-i decimal places
//
//			>maxLen			> dmax			> maxLen			malLen-dmax integer and dmax decimal places
		
		if (i + d <= maxLen) {
			return IRUtils.getEGLPrimitiveType(MofConversion.Type_Decimal, i+d, d);
		}
		else {
			if (d <= dmax) {
				return IRUtils.getEGLPrimitiveType(MofConversion.Type_Decimal, maxLen, d);
			}
			else {
				if (i + dmax <= maxLen) {
					return IRUtils.getEGLPrimitiveType(MofConversion.Type_Decimal, maxLen, maxLen-i);
				}
				else {
					return IRUtils.getEGLPrimitiveType(MofConversion.Type_Decimal, maxLen, dmax);
				}
			}
		}
	}
	
	private static int getDmax(Type op1, Type op2, String operator) {
		
		// For divide, do not look at the divisor
		if (operator.equals(MultiOperandExpression.Op_DIVIDE)) {
			return getDecimals(op2);
		}
		
		// For exponentiation, do not look at the exponent.
		if (operator.equals(MultiOperandExpression.Op_POWER)) {
			return getDecimals(op1);
		}
		
		
		return max(getDecimals(op1), getDecimals(op2));		
	}
	
	private static int max(int i1, int i2) {
		if (i1 > i2) {
			return i1;
		}
		
		return i2;
	}

	private static int min(int i1, int i2) {
		if (i1 > i2) {
			return i2;
		}
		
		return i1;
	}

	private static int getIntDigits(Expression expr, Type type) {
		if (expr instanceof IntegerLiteral) {
			IntegerLiteral lit = (IntegerLiteral) expr;
			return lit.getUnsignedValue().length();
		}
		else {
			return getLength(type) - getDecimals(type);
		}
	}
	
	private static int getLength(Type type) {
		
		String key = type.getClassifier().getMofSerializationKey();
		
		if (key.equalsIgnoreCase(MofConversion.Type_EGLInt) || key.equalsIgnoreCase(MofConversion.Type_EGLInt)) {
			return 9;
		}
		
		if (key.equalsIgnoreCase(MofConversion.Type_EGLSmallint)) {
			return 4;
		}

		if (key.equalsIgnoreCase(MofConversion.Type_EGLBigint)) {
			return 18;
		}
		
		if (type instanceof FixedPrecisionType) {
			return ((FixedPrecisionType)type).getLength();
		}
		
		return 0;
	}
	
	private static int getDecimals(Type type) {
		
		if (type instanceof FixedPrecisionType) {
			return ((FixedPrecisionType)type).getDecimals();
		}
		
		return 0;
	}

}
