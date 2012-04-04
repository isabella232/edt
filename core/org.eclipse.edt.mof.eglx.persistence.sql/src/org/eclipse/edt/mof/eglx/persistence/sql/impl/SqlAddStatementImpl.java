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
import org.eclipse.edt.mof.eglx.persistence.sql.SqlAddStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.utils.SQL;

public class SqlAddStatementImpl extends SqlIOStatementImpl implements SqlAddStatement {

	@Override
	public String getSqlString() {
		String sql = super.getSqlString();
		if (sql == null || "".equals(sql)) {
			sql = generateDefaultSqlString();
			setSqlString(sql);
		}
		return sql;
	}
	
	// TODO This is a simplified mapping of one type to one table only - handle multiple tables
	private String generateDefaultSqlString() {
		String sql = null;
		Expression target = getTargets().get(0);
		boolean targetIsList = target.getType() instanceof ArrayType;
		EGLClass targetType;
		if (targetIsList) {
			targetType = (EGLClass)((ArrayType)target.getType()).getElementType().getClassifier();
		}
		else {
			targetType = (EGLClass)target.getType().getClassifier();
		}
		sql = "INSERT INTO ";
		sql += SQL.getTableName(targetType);
		sql += "(";
		boolean doComma = false;
		int fieldNum = 0;
		for (Field f : targetType.getFields()) {
			// Do not INSERT list fields which represent associations
			if (SQL.isInsertable(f)) {
				fieldNum++;
				if (doComma) sql += ", ";
				sql += SQL.getColumnName(f);
				if (!doComma) doComma = true;
			}
		}
		sql += ") VALUES (";
		for (int i=0; i<fieldNum; i++) {
			if (i>0) sql += ", ";
			sql += "?";
		}
		sql += ")";
		return sql;
	}	
}
