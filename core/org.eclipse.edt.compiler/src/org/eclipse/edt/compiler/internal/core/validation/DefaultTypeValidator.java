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
package org.eclipse.edt.compiler.internal.core.validation;

import org.eclipse.edt.compiler.TypeValidator;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.IntervalType;
import org.eclipse.edt.mof.egl.SequenceType;
import org.eclipse.edt.mof.egl.TimestampType;
import org.eclipse.edt.mof.egl.Type;

public class DefaultTypeValidator implements TypeValidator {
	
	@Override
	public void validateType(org.eclipse.edt.compiler.core.ast.Type type, Type typeBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (typeBinding instanceof FixedPrecisionType) {
			// Length and decimals must be non-negative, 0-32, and decimals must be <= length.
			int len = ((FixedPrecisionType)typeBinding).getLength();
			int decimals = ((FixedPrecisionType)typeBinding).getDecimals();
			
			if (len < 0 || len > 32) {
				problemRequestor.acceptProblem(type, IProblemRequestor.INVALID_LENGTH_FOR_PARAMETERIZED_TYPE,
						new String[] {Integer.toString(len), typeBinding.getClassifier().getTypeSignature(), "(1..32)"});
			}
			
			// Don't report too many errors at once
			if (decimals < 0 || decimals > 32) {
				problemRequestor.acceptProblem(type, IProblemRequestor.INVALID_DECIMALS,
						new String[] {Integer.toString(decimals), typeBinding.getClassifier().getTypeSignature(), "(1..32)"});
			}
			else if (decimals > len) {
				problemRequestor.acceptProblem(type, IProblemRequestor.DECIMALS_GREATER_THAN_LENGTH,
						new String[] {Integer.toString(decimals), typeBinding.getClassifier().getTypeSignature(), Integer.toString(len)});
			}
		}
		else if (typeBinding instanceof SequenceType) {		
			// Length must be non-negative
			int len = ((SequenceType)typeBinding).getLength();
			if (len < 0) {
				problemRequestor.acceptProblem(type, IProblemRequestor.NEGATIVE_LENGTH_INVALID,
						new String[] {Integer.toString(len), typeBinding.getClassifier().getTypeSignature()});
			}
		}
		else if (typeBinding instanceof TimestampType) {		
			//TODO
		}
		else if (typeBinding instanceof IntervalType) {		
			//TODO
		}
	}
}
