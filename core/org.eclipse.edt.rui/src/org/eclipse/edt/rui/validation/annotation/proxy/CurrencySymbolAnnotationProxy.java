/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.rui.validation.annotation.proxy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.AbstractValidationProxy;
import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PropertyApplicableForNumericTypeOnlyAnnotationValidator;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.edt.rui.validation.annotation.CurrencySymbolValidator;

public class CurrencySymbolAnnotationProxy extends AbstractValidationProxy {
	public static final String caseSensitiveName = NameUtile.getAsCaseSensitiveName("currencySymbol");
	public static final String name = NameUtile.getAsName(caseSensitiveName);
	
	private static CurrencySymbolAnnotationProxy INSTANCE = new CurrencySymbolAnnotationProxy();
	
	private static final List<AnnotationValidationRule> annotations = new ArrayList();
	static{
		annotations.add(new PropertyApplicableForNumericTypeOnlyAnnotationValidator(IEGLConstants.PROPERTY_CURRENCYSYMBOL));
		annotations.add(new UserDefinedAnnotationValidationRule(CurrencySymbolValidator.class));
	}
	
	private CurrencySymbolAnnotationProxy() {
	}
	
	public static CurrencySymbolAnnotationProxy getInstance() {
		return INSTANCE;
	}
	
	@Override
	public List<AnnotationValidationRule> getAnnotationValidators() {
		return annotations;
	}
}
