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
package org.eclipse.edt.mof.eglx.jtopen.messages;

import java.util.ResourceBundle;



/**
 * @author winghong
 */
public class IBMiResourceKeys{

	private static final String BUNDLE_FOR_KEYS= "org.eclipse.edt.mof.eglx.jtopen.messages.IBMiValidationResources"; //$NON-NLS-1$
	private static ResourceBundle bundleForConstructedKeys= ResourceBundle.getBundle(BUNDLE_FOR_KEYS);

	public static ResourceBundle getResourceBundleForKeys() {
		return bundleForConstructedKeys;
	}
	
    
    public static final int AS400_ANNOTATION_TYPE_MISMATCH = 5700;
    public static final int WRONG_NUMBER_OF_PARAMETER_ANNOTATIONS = 5701;
    public static final int PARAMETER_ANNOTATION_INVALID = 5702;
    public static final int IBMIPROGRAM_CONTAINER_INVALID = 5703;
    public static final int IBMIPROGRAM_CANNOT_HAVE_STMTS = 5704;
    public static final int IBMIPROGRAM_ONLY_SERVICE_CAN_RETURN = 5705;
    public static final int IBMIPROGRAM_CAN_ONLY_RETURN_INT = 5706;
    public static final int AS400_BAD_LENGTH = 5707;
    public static final int AS400_NEGATIVE_DECIMAL = 5708;
    public static final int AS400_BAD_DECIMAL = 5709;
    public static final int AS400_PROPERTY_REQUIRED = 5710;
    public static final int AS400_PROPERTY_NOT_ALLOWED = 5711;
    public static final int ELEMENTTYPE_ANNOTATION_INVALID = 5712;
    public static final int RETURN_COUNT_VAR_MUST_BE_INT_COMPAT = 5713;
    public static final int RETURN_COUNT_VAR_DEFINED_IN_WRONG_PLACE = 5714;
    public static final int PROGRAM_PARAMETER_ANNOTATION_REQUIRED = 5715;
    public static final int AS400_ANNOTATION_NULLABLE_TYPE_INVALID = 5716;
    public static final int IBMIPROGRAM_PARM_TYPE_INVALID = 5717;
    public static final int IBMIPROGRAM_NULLABLE_PARM_INVALID = 5718;
    public static final int IBMIPROGRAM_ARRAY_NULLABLE_PARM_INVALID = 5719;
    public static final int IBMIPROGRAM_PARM_STRUCT_TYPE_INVALID = 5720;
    public static final int IBMIPROGRAM_NULLABLE_PARM_STRUCT_INVALID = 5721;
    public static final int IBMIPROGRAM_ARRAY_NULLABLE_PARM_STRUCT_INVALID = 5722;
    public static final int IBMIPROGRAM_PARM_STRUCT_REQUIRES_AS400 = 5723;
    public static final int IBMIPROGRAM_CALLBACK_OR_RETURNS_REQUIRED = 5724;
    public static final int IBMIPROGRAM_RETURNS_NOT_ALLOWED = 5725;
    public static final int IBMIPROGRAM_RETURNS_NOT_COMPAT_WITH_FUNCTION = 5726;
    public static final int IBMIPROGRAM_USING_HAS_WRONG_TYPE = 5727;
    public static final int IBMIPROGRAM_MUST_BE_SPECIFIED = 5728;
    public static final int IBMIPROGRAM_CALLBACK_NOT_SUPPORTED = 5729;
    
}
