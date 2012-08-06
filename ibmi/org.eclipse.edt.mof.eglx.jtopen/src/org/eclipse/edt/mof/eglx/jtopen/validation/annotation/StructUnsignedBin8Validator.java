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
package org.eclipse.edt.mof.eglx.jtopen.validation.annotation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.Member;

public class StructUnsignedBin8Validator extends AbstractStructParameterAnnotationValidator implements IAnnotationValidationRule {
	
	@Override
	protected String getName() {
		return "StructUnsignedBin8";
	}
	
	@Override
	protected List<String> getSupportedTypes() {
		return new ArrayList<String>();
	}
	
	protected boolean isValidType(Member targetBinding) {
		if (targetBinding != null && targetBinding.getType() != null) {						
			return targetBinding.getType() instanceof FixedPrecisionType &&
					((FixedPrecisionType)targetBinding.getType()).getLength() == 20 &&
					((FixedPrecisionType)targetBinding.getType()).getDecimals() == 0;
		}
		else {
			return true;  //return true to avoid excess error messages
		}
	}


}
