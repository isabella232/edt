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
package org.eclipse.edt.mof.eglx.rui.messages;

import java.util.ResourceBundle;

public class RUIResourceKeys {
	private static final String BUNDLE_FOR_KEYS = "org.eclipse.edt.mof.eglx.rui.messages.RUIValidationResources"; //$NON-NLS-1$
	private static ResourceBundle bundleForConstructedKeys= ResourceBundle.getBundle(BUNDLE_FOR_KEYS);

	public static ResourceBundle getResourceBundleForKeys() {
		return bundleForConstructedKeys;
	}
	
	public static final int ONLY_STRING_FIELDS_ALLOWED = 2066;
	public static final int PROPERTY_EXCEEDS_ALLOWED_LENGTH = 3101;
	public static final int PROPERTY_REQUIRES_NONDECIMAL_DIGITS = 3123;
	public static final int INVALID_PROPERTY_VALUE_FOR_ITEM_TYPE = 3141;
	public static final int PROPERTY_INVALID_CHARACTER_IN_DATEFORMAT = 3171;
	public static final int PROPERTY_INVALID_FOR_TYPE = 3174;
	public static final int PROPERTY_INVALID_FOR_DECIMALS = 3175;
	public static final int PROPERTY_MINIMUM_INPUT_MUST_BE_GREATER_THAN_ZERO= 3181;
	public static final int VALIDATOR_FUNCTION_HAS_PARAMETERS = 3268;
	public static final int VALIDATION_PROPERTIES_LIBRARY_WRONG_TYPE = 3276;
	public static final int PROPERTIESFILE_NAME_CANNOT_CONTAIN_DASH = 3384;
	public static final int INVALID_CURRENCY_SYMBOL_PROPERTY_VALUE = 5352;
	public static final int INVALID_FILLCHARACTER_PROPERTY_VALUE = 5353;
}
