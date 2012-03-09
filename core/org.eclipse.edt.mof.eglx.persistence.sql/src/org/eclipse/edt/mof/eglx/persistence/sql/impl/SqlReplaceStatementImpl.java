/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
import org.eclipse.edt.mof.eglx.persistence.sql.SqlReplaceStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.utils.SQL;

public class SqlReplaceStatementImpl extends SqlIOStatementImpl implements SqlReplaceStatement {
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
		if (SQL.isSQLResultSet(getDataSource().getType())) return null;
		
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
		sql = "UPDATE ";
		sql += SQL.getTableName(targetType);
		sql += " SET ";
		boolean doComma = false;
		for (Field f : targetType.getFields()) {
			if (SQL.hasUpdateableAnnotation(f) || (!SQL.isKeyField(f) && SQL.isUpdateable(f))) {
				if (doComma) sql += ", ";
				sql += SQL.getColumnName(f);
				sql += " = ?";
				if (!doComma) doComma = true;
			}
		}
		sql += " WHERE ";
		boolean addAND = false;
		for (Field f : targetType.getFields()) {
			if (SQL.isKeyField(f)) {
				if(addAND){
					sql += " AND ";
				}
				addAND = true;
				sql += SQL.getColumnName(f);
				sql += " = ?";
			}
		}
		return sql;
	}	
}
