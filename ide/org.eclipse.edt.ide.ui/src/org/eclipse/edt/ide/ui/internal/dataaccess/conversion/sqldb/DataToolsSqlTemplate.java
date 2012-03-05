/*******************************************************************************
 * Copyright 漏 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb;

import java.util.Locale;

import org.eclipse.edt.compiler.core.EGLKeywordHandler;
import org.eclipse.edt.ide.ui.internal.externaltype.conversion.javatype.JavaTypeConstants;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.eclipse.edt.mof.codegen.api.AbstractTemplate;

public class DataToolsSqlTemplate extends AbstractTemplate implements DataToolsSqlTemplateConstants{
	
	public static final String genTable = "genTable";
	public static final String genColumn = "genColumn";


	protected String getAliasName(String itemName) {
		String alias = null;
		
		if(EGLKeywordHandler.getKeywordHashSet().contains(itemName.toLowerCase(Locale.ENGLISH))) {
			alias = JavaTypeConstants.UNDERSTORE_PREFIX + itemName;
		} else {
			alias = CoreUtility.getCamelCaseString(itemName);
		}
		
		return alias;
	}
}
