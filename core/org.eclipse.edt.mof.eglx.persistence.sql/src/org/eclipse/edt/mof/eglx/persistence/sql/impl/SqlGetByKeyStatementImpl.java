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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.persistence.sql.Utils;
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SqlGetByKeyStatement;

public class SqlGetByKeyStatementImpl extends SqlIOStatementImpl implements SqlGetByKeyStatement {
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
		if (getPreparedStatement() != null || Utils.isSQLResultSet(getDataSource().getType())) return null;
		
		StringBuilder sql = new StringBuilder();
		if (getTargets().size() == 1) {
			Expression target = getTargets().get(0);
			boolean targetIsList = target.getType() instanceof ArrayType;
			EGLClass targetType;
			if (targetIsList) {
				targetType = (EGLClass)((ArrayType)target.getType()).getElementType().getClassifier();
			}
			else {
				targetType = (EGLClass)target.getType().getClassifier();
			}
			if (!TypeUtils.isDynamicType(targetType)) {
				sql.append("SELECT ");
				List<Field> idFields = new ArrayList<Field>();
				boolean doComma = false;
				for (Field f : targetType.getFields()) {
					if (Utils.isKeyField(f)) idFields.add(f);
					if (Utils.isReadable(f)) {
						if (doComma) sql.append(", ");
						if(Utils.isTextType(f.getType().getClassifier())){
							sql.append("RTRIM(");
							sql.append(Utils.getColumnName(f));
							sql.append(")");
						}
						else{
							sql.append(Utils.getColumnName(f));
						}
						if (!doComma) doComma = true;
					}
				}
				sql.append(" FROM ");
				sql.append(Utils.getTableName(targetType));
				if (!targetIsList && !idFields.isEmpty()) {
					sql.append(" WHERE ");
					boolean doAnd = false;
					for (Field f: idFields) {
						if (doAnd) sql.append(" AND ");
						sql.append(Utils.getColumnName(f) + " = ?");
						if (!doAnd) doAnd = true;
					}
				}
			}
		}
		return sql.toString();
	}

}
