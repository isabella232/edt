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
package org.eclipse.edt.mof.eglx.services.messages;

import java.util.ResourceBundle;



/**
 * @author winghong
 */
public class ResourceKeys{

	private static final String BUNDLE_FOR_KEYS= "org.eclipse.edt.mof.eglx.services.messages.ValidationResources"; //$NON-NLS-1$
	private static ResourceBundle bundleForConstructedKeys= ResourceBundle.getBundle(BUNDLE_FOR_KEYS);

	public static ResourceBundle getResourceBundleForKeys() {
		return bundleForConstructedKeys;
	}
	
    
    public static final int FUNCTION_CALL_TARGET_MUST_BE_FUNCTION = 3390;
    public static final int FUNCTION_MUST_BE_DEFINED_IN_PART = 3391;
    public static final int FUNCTION_CALLBACK_FUNCTION_REQUIRED = 3393;
    public static final int FUNCTION_CALLBACK_MUST_BE_FUNCTION = 3394;
    public static final int FUNCTION_CANNOT_HAVE_RETURN_TYPE = 3395;
    public static final int FUNCTION_REQUIRES_N_PARMS = 3396;
    public static final int FUNCTION_MUST_HAVE_ALL_IN_PARMS = 3397;
    public static final int FUNCTION_PARM_MUST_HAVE_TYPE = 3398;
    public static final int FUNCTION_TYPE_NOT_COMPAT_WITH_PARM = 3399;
    public static final int INVALID_CONTAINER = 3400;
    
	public static final int XXXREST_ALL_PARMS_MUST_BE_IN = 3401;
	public static final int XXXREST_ONLY_1_RESOURCE_PARM = 3402;
	public static final int XXXREST_MUST_RETURN_RESOURCE = 3403;
	public static final int XXXREST_NON_RESOUCE_MUST_BE_STRING_COMPAT = 3404;
	public static final int XXXREST_UMATCHED_SUBS_VAR = 3405;
	public static final int XXXREST_RESOURCE_PARM_MUST_BE_RESOURCE = 3406;
	public static final int XXXREST_FORMAT_MUST_BE_NONE = 3407;
	public static final int XXXREST_RESPONSEFORMAT_NOT_SUPPORTD = 3408;
	public static final int XXXREST_PARM_TYPE_MUST_BE_FLAT_RECORD = 3409;
	
	public static final int XXXREST_NO_RESOURCE_PARM = 3411;
	public static final int XXXREST_NO_METHOD = 3412;
	public static final int SERVICE_CALL_USING_WRONG_TYPE = 3415;
	
}
