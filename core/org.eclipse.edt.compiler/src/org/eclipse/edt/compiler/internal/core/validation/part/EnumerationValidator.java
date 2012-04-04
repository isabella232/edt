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
package org.eclipse.edt.compiler.internal.core.validation.part;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author Dave Murray
 */
public class EnumerationValidator extends AbstractASTVisitor {
	
	protected IProblemRequestor problemRequestor;
	private ICompilerOptions compilerOptions;
	private List<Integer> alreadySeenValues;
	private boolean[] foundField = new boolean[1];
	
	public EnumerationValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(Enumeration enumeration) {
		alreadySeenValues = new ArrayList<Integer>();
		foundField = new boolean[1];
		
		return true;
	}
	
	public void endVisit(Enumeration enumeration) {
		if (!foundField[0]) {
			problemRequestor.acceptProblem(
					enumeration,
					IProblemRequestor.ENUMERATION_NO_FIELDS,
					new String[] {
							enumeration.getIdentifier()
					}
			);
		}
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.EnumerationField enumerationField) {
		
		foundField[0] = true;
		if (!validateValue(enumerationField)) {
			return false;
		}
		if (Binding.isValidBinding(enumerationField.getName().resolveDataBinding())) {
			EnumerationDataBinding enumBinding = (EnumerationDataBinding) enumerationField.getName().resolveDataBinding();
			Integer value = new Integer(enumBinding.geConstantValue());
			if (alreadySeenValues.contains(new Integer(enumBinding.geConstantValue()))) {
				Node node = enumerationField.getName();
				if (enumerationField.hasConstantValue()) {
					node = enumerationField.getConstantValue();
				}				
				problemRequestor.acceptProblem(
						node,
						IProblemRequestor.ENUMERATION_CONSTANT_DUPLICATE,
						new String[] {
							value.toString(),
							enumBinding.getDeclaringPart().getCaseSensitiveName()
						}
				);
			}
			else {
				alreadySeenValues.add(value);
			}
		}
		
		return false;
	}
	
	private boolean validateValue(org.eclipse.edt.compiler.core.ast.EnumerationField enumerationField) {
		if (enumerationField.hasConstantValue()) {
			Expression val = enumerationField.getConstantValue();
			while (val instanceof UnaryExpression) {
				val = ((UnaryExpression)val).getExpression();
			}
			if (val instanceof IntegerLiteral) {
				String value = ((IntegerLiteral)val).getValue();
				try {
					Integer.valueOf(value);
					return true;
				} catch (NumberFormatException e) {
				}				
			}
			problemRequestor.acceptProblem(
					enumerationField.getConstantValue(),
					IProblemRequestor.ENUMERATION_CONSTANT_INVALID,
					new String[] {
						enumerationField.getConstantValue().getCanonicalString()
					}
			);
			return false;
		}
		return true;
		
	}
}
