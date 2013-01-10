/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.document.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.BooleanLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;

public class PropertyValueVisitor  extends DefaultASTVisitor{
	
	protected String propertyType;
	protected boolean propertyTypeMatch = true;
	protected ArrayList result = new ArrayList();
	
	public PropertyValueVisitor(String propertyType){
		this.propertyType = propertyType;
	}
	
	public ArrayList getResult(){
		return result;
	}
	
	public void processExpression(Expression expression){
		doProcessExpression(expression, result, propertyType);
	}
	
	/**
	 * If the property typeWe are looking for a literal value that matches the property type that is passed in.
	 * If the property type is a choice, all
	 */
	private void doProcessExpression(Expression expression, final List values, final String thePropertyType){
		
		if(IVEConstants.CHOICE_TYPE.equals(thePropertyType)){
			// All choice values are editable (propertyTypeMatch = true)
			// Return the value as found in the source
			values.add(expression.getCanonicalString());
		}else{
			// We are looking for a specific property type
			final boolean[] foundLiteral = {false};
			expression.accept(new DefaultASTVisitor(){	
				public boolean visit(StringLiteral stringLiteral){
					if(IVEConstants.STRING_TYPE.equals(thePropertyType) || IVEConstants.COLOR_TYPE.equals(thePropertyType)) {
						// String literals are treated the same for String and Color property types
						// Do not put quotes around the value
						values.add(stringLiteral.getValue());
					}else{
						// A string lteral is not valid for the current property type.
						// Return the value as it appears in the source, including quotes
						propertyTypeMatch = false;
						values.add(stringLiteral.getCanonicalString());
					}
					foundLiteral[0] = true;
					return false;
				}
				
				public boolean visit(BooleanLiteral booleanLiteral){
					if(!IVEConstants.BOOLEAN_TYPE.equals(thePropertyType)){
						propertyTypeMatch = false;
					}
					// Return the value as it appears in the source
					values.add(String.valueOf(booleanLiteral.booleanValue().booleanValue()));
					foundLiteral[0] = true;
					return false;
				}
				
				public boolean visit(IntegerLiteral integerLiteral){
					if(!IVEConstants.INTEGER_TYPE.equals(thePropertyType)){
						propertyTypeMatch = false;
					}
					// Return the value as it appears in the source
					values.add(integerLiteral.getValue());
					foundLiteral[0] = true;
					return false;
				}
				
				public boolean visit(ArrayLiteral arrayLiteral) {
					if(IVEConstants.STRING_ARRAY_TYPE.equals(thePropertyType)){
						List expressions = arrayLiteral.getExpressions();
						for (Iterator iterator = expressions.iterator(); iterator.hasNext();) {
							Expression expression = (Expression) iterator.next();
							doProcessExpression(expression, values, IVEConstants.STRING_TYPE);
						}
					}else{
						StringBuffer value = new StringBuffer();
						List expressions = arrayLiteral.getExpressions();
						List theValues = new ArrayList();
						for (Iterator iterator = expressions.iterator(); iterator.hasNext();) {
							Expression expression = (Expression) iterator.next();
							// Assume a string property type
							doProcessExpression(expression, theValues, IVEConstants.STRING_TYPE);
						}
						value.append("[");
						for (Iterator iterator = theValues.iterator(); iterator.hasNext();) {
							String theValue = (String) iterator.next();
							value.append(theValue);
							if(iterator.hasNext()){
								value.append(",");
							}
						}
						value.append("]");
						propertyTypeMatch = false;
						values.add(value.toString());
					}
					foundLiteral[0] = true;
					return false;
				}	
				
				public boolean visit(UnaryExpression unaryExpression) {
					// consume the unary expression, and keep processing the right hand side of the expression
					// as the actual value
					StringBuffer value = new StringBuffer();
					List theValues = new ArrayList();
					doProcessExpression(unaryExpression.getExpression(), theValues, thePropertyType);	
					value.append(unaryExpression.getOperator());
					for (Iterator iterator = theValues.iterator(); iterator.hasNext();) {
						String theValue = (String) iterator.next();
						value.append(theValue);
					}
					values.add(value.toString());
					foundLiteral[0] = true;
					return false;
				}
				
				public boolean visit(ParenthesizedExpression parenthesizedExpression) {
					StringBuffer value = new StringBuffer();
					List theValues = new ArrayList();
					// only support parenthesized integer expressions
					doProcessExpression(parenthesizedExpression.getExpression(), theValues, IVEConstants.INTEGER_TYPE);
					
					value.append("(");
					for (Iterator iterator = theValues.iterator(); iterator.hasNext();) {
						String theValue = (String) iterator.next();
						value.append(theValue);
					}
					value.append(")");		
					
					values.add(value.toString());
					if(IVEConstants.INTEGER_TYPE.equals(propertyType)){
						foundLiteral[0] = true;
					}else{
						foundLiteral[0] = false;
					}
					return false;
				}
			});
			
			// If we didn't find a literal, remember the actual value of the expression
			if(foundLiteral[0] == false){
				values.add(expression.getCanonicalString());
				propertyTypeMatch = false;
			}
		}
	}
}
