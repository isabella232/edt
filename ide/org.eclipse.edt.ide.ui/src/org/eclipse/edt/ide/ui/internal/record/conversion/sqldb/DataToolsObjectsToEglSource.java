/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.record.conversion.sqldb;

import org.eclipse.edt.ide.ui.internal.record.conversion.AbstractObjectToEglSource;

public class DataToolsObjectsToEglSource extends AbstractObjectToEglSource {
	
	public static final String DATA_DEFINITION_OBJECT = "dataDefinition";
	public static final String TABLE_NAME_QUALIFIED = "tableNameQualified";

	@Override
	public String[] getTemplatePath() {
		return new String[] { "org.eclipse.edt.ide.ui.internal.record.conversion.sqldb.templates" };
	}

}
