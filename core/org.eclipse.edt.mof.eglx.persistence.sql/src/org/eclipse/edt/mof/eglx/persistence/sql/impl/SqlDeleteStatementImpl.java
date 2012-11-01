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
package org.eclipse.edt.mof.eglx.persistence.sql.impl;

import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlDeleteStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.ext.Utils;

public class SqlDeleteStatementImpl extends SqlIOStatementImpl implements SqlDeleteStatement {

 	@Override
	public String getSqlString() {
		String sql = super.getSqlString();
		if (sql == null || "".equals(sql)) {
			sql = generateDefaultSqlString();
			setSqlString(sql);
		}
		return sql;
	}
	
	public String generateDefaultSqlString() {
		if (Utils.isSQLResultSet(getDataSource().getType())) return null;
		
		String sql = null;
		Expression target = getTargets().get(0);
		boolean targetIsList = target.getType().getClassifier().equals(TypeUtils.Type_LIST);
		EGLClass targetType;
		if (targetIsList) {
			targetType = (EGLClass)((ArrayType)target.getType()).getElementType().getClassifier();
		}
		else {
			targetType = (EGLClass)target.getType().getClassifier();
		}
		sql = "DELETE FROM ";
		sql += Utils.getTableName(targetType);
		sql += " WHERE ";
		boolean addAND = false;
		for (Field f : targetType.getFields()) {
			if (Utils.isKeyField(f)) {
				if(addAND){
					sql += " AND ";
				}
				addAND = true;
				sql += Utils.getColumnName(f);
				sql += " = ?";
			}
		}
		return sql;
	}
	
}
