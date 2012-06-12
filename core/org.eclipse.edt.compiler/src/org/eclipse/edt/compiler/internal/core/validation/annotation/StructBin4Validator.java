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

import org.eclipse.edt.compiler.binding.StructBin4AnnotationTypeBinding;


public class StructBin4Validator extends StructIntValidator implements IAnnotationValidationRule {
	
	@Override
	protected String getName() {
		return StructBin4AnnotationTypeBinding.caseSensitiveName;
	}
	
	@Override
	protected String getInternedName() {
		return StructBin4AnnotationTypeBinding.name;
	}
}
