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

import org.eclipse.datatools.connectivity.sqm.core.definition.DatabaseDefinition;
import org.eclipse.datatools.modelbase.sql.tables.Column;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLRetrieveUtility;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLStructureItem;

public class DataToolsSqlColumnTemplate extends org.eclipse.edt.ide.ui.internal.record.conversion.sqldb.DataToolsSqlColumnTemplate {

	public void genEntityRecordColumn(Column column, EglSourceContext ctx, StringBuilder recordsDef) {
		if (!validateColumn(column, ctx)) {
			return;
		} else {
			recordsDef.append("\n");
			recordsDef.append(getFieldDefinition(column, ctx, true));

			boolean isPartOfPK = column.isPartOfPrimaryKey();
			if (isPartOfPK) {
				EGLSQLStructureItem item = new EGLSQLStructureItem();
				DatabaseDefinition def = (DatabaseDefinition) ctx.get(DTO2EglSource.DATA_DEFINITION_OBJECT);
				EGLSQLRetrieveUtility.getInstance().populateStructureItem(def, column, item);

				String fieldName = getFieldName(column, item);
				ctx.appendVariableValue(SEARCH_METHOD_PARAM_DEF, fieldName + " " + getFieldType(column, item) + " in", ", ");
				ctx.appendVariableValue(SEARCH_METHOD_PARAM, fieldName, ", ");
				ctx.appendVariableValue(SEARCH_RECORD_KEY_ASSIGN, fieldName + "=" + fieldName, ", ");
			}
		}
	}

	public void genSearchRecordColumn(Column column, EglSourceContext ctx, StringBuilder recordsDef) {
		if (!validateColumn(column, ctx)) {
			return;
		} else {
			recordsDef.append("\n");
			recordsDef.append(getFieldDefinition(column, ctx, false));
		}
	}

}
