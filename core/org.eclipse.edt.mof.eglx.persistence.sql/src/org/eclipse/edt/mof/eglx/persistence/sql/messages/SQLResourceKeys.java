/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.persistence.sql.messages;

import java.util.ResourceBundle;



public class SQLResourceKeys {

	private static final String BUNDLE_FOR_KEYS = "org.eclipse.edt.mof.eglx.persistence.sql.messages.SQLValidationResources"; //$NON-NLS-1$
	private static ResourceBundle bundleForConstructedKeys = ResourceBundle.getBundle(BUNDLE_FOR_KEYS);

	public static ResourceBundle getResourceBundleForKeys() {
		return bundleForConstructedKeys;
	}
	
	public static final int SQL_EXPR_HAS_WRONG_TYPE = 8500;
    public static final int SQL_WITH_STMT_REQUIRED = 8501;
    public static final int SQL_FOR_TYPE_INVALID = 8502;
    public static final int SQL_FOR_NOT_ALLOWED = 8503;
    public static final int SQL_INTO_NOT_ALLOWED = 8504;
    public static final int SQL_TARGET_MUST_BE_DATA_EXPR_OR_COLUMNS = 8505;
    public static final int SQL_TARGET_MUST_BE_ENTITY_OR_COLUMNS = 8506;
    public static final int SQL_FOR_AND_TARGET_TYPES_MUST_MATCH = 8507;
    public static final int SQL_ENTITY_ASSOCIATIONS_NOT_SUPPORTED = 8508;
    public static final int SQL_NO_ID_IN_TARGET_TYPE = 8509;
    public static final int SQL_FOR_NOT_ALLOWED_WITH_DATA_SOURCE_TYPE = 8510;
    public static final int SQL_WITH_STMT_REQUIRED_FOR_DELETE = 8511;
    public static final int SQL_NO_WITH_FOR_USING = 8512;
    public static final int SQL_TARGET_NOT_DATA_EXPR = 8513;
    public static final int SQL_FOR_NOT_ALLOWED_WITHOUT_TARGET = 8514;
    public static final int SQL_SINGLE_TABLE_REQUIRED = 8515;
    public static final int SQL_DELETE_FOR_OR_WITH = 8516;
    public static final int SQL_NO_WITH_USING = 8517;
    public static final int SQL_STMT_REQUIRED_FOR_NON_SINGLE_TABLE = 8518;
    public static final int SQL_NULLABLE_TARGET_MISSING_DEFAULT_CONSTRUCTOR = 8519;
    public static final int SQLRESULTSET_ANNOTATION_TYPE_ERROR = 8520;
}
