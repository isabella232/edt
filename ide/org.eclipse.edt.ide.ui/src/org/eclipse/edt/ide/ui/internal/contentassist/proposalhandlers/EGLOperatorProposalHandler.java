/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.BinaryExpression.Operator;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.text.ITextViewer;

public class EGLOperatorProposalHandler extends EGLAbstractProposalHandler {

	public EGLOperatorProposalHandler(ITextViewer viewer, int documentOffset, String prefix) {
		super(viewer, documentOffset, prefix);
	}

	public List getProposals() {
		String[] strings = new String[] {
				Operator.TIMES.toString(),
				Operator.TIMESTIMES.toString(),
				Operator.DIVIDE.toString(),
				Operator.MODULO.toString(),
				Operator.PLUS.toString(),
				Operator.MINUS.toString(),
				Operator.LESS.toString(),
				Operator.GREATER.toString(),
				Operator.LESS_EQUALS.toString(),
				Operator.GREATER_EQUALS.toString(),
				Operator.EQUALS.toString(),
				Operator.NOT_EQUALS.toString(),
				Operator.OR.toString(),
				Operator.AND.toString(),
				Operator.CONCAT.toString(),
				Operator.NULLCONCAT.toString(),
				Operator.BITAND.toString(),
				Operator.BITOR.toString(),
				Operator.XOR.toString(),
				Operator.LEFT_SHIFT.toString(),
				Operator.RIGHT_SHIFT_ARITHMETIC.toString(),
				Operator.RIGHT_SHIFT_LOGICAL.toString(),
				IEGLConstants.KEYWORD_AND,
				IEGLConstants.KEYWORD_OR,				
				IEGLConstants.KEYWORD_ISA
		};
		return getProposals(strings, UINlsStrings.CAProposal_Operator);
	}

}
