/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlGetByKeyStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.utils.SQL;

public class SqlGetByKeyStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlGetByKeyStatement stmt, Context ctx, TabbedWriter out) {
		boolean hasPreparedStmt = stmt.getPreparedStatement() != null;
		boolean hasDataSource = stmt.getDataSource() != null;
		boolean isResultSet = hasDataSource && SQL.isSQLResultSet(stmt.getDataSource().getType());
		
		String var_resultSet = ctx.nextTempName();
		if (hasPreparedStmt) {
			out.println("try {");
			int i = 1;
			String prepareStatementName = getExprString(stmt.getPreparedStatement(), ctx);
			if(stmt.getUsingExpressions() != null && stmt.getUsingExpressions().size() > 0 ){
				for (Expression uexpr : stmt.getUsingExpressions()) {
					genSetColumnValue(stmt, uexpr, prepareStatementName, i, ctx, out);
					i++;
				}
			}
			out.print(class_ResultSet + " " + var_resultSet + " = " + prepareStatementName);
			out.println(".executeQuery();");
		}
		else if (!isResultSet) {
			if (stmt.getSqlString() != null || !"".equals(stmt.getSqlString())) {
				genSqlStatementSetup(stmt, ctx, out);
			}
			// Handle the default case of no USING clause on default SQL with
			// the values of the target default key fields
			if (!hasPreparedStmt && stmt.getUsingExpressions().isEmpty() && !stmt.hasExplicitSql()) {
				Type type = stmt.getTarget().getType();
				if (type instanceof EGLClass && !SQL.isWrappedSQLType((EGLClass)type)) {
					// The assumption here is that the order of the SQL compare expressions
					// in the default WHERE clause derived by the order of the key fields in the type
					int i = 1;
					String varName = getExprString(stmt.getTarget(), ctx);
					for (Field f : ((EGLClass)type).getFields()) {
						if (SQL.isKeyField(f) && SQL.isMappedSQLType((EGLClass)f.getType().getClassifier())) {
							// TODO need a generalized way to generate the appropriate
							// accessor without assuming the field value to accessed is public
							// and is not an EGLProperty or Property field
							genSetColumnValue(f, var_statement, varName, i, ctx, out);
							i++;
						}
					}
				}
			}
			out.print(class_ResultSet + " " + var_resultSet + " = ");
			out.print(var_statement);
			out.println(".executeQuery();");

		}
		else {
			out.println("try {");
			// DataSource is a ResultSet already which is why there is no sql string
			out.print(class_ResultSet + " " + var_resultSet);
			out.print(" = ");
			ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
			out.println(".getResultSet();");
		}

		genPopulateFromResultSet(stmt, ctx, out, var_resultSet, isResultSet);
		genSqlStatementEnd(stmt, ctx, out);
	}
	
	public void genPopulateFromResultSet(SqlGetByKeyStatement stmt, Context ctx, TabbedWriter out, String var_resultSet, boolean isResultSet ) {
		if (stmt.getTargets().size() == 1) {
			Expression target = stmt.getTarget();
			boolean targetIsList = target.getType() instanceof ArrayType;
			EGLClass targetType;
			if (targetIsList) {
				ctx.invoke(genRuntimeTypeName, target.getType(), ctx, out, TypeNameKind.JavaObject);
				out.print(" ezeList = ");
				ctx.invoke(genInstantiation, target.getType(), ctx, out);
				out.println(";");
				// Assume target type is an EGLClass as other Classifiers would be filtered out by front end validation
				targetType = (EGLClass)((ArrayType)target.getType()).getElementType().getClassifier();
				out.println("while(" + var_resultSet + ".next()) {");
				ctx.invoke(genRuntimeTypeName, targetType, ctx, out);
				out.print(' ');
				String targetVarName = ctx.nextTempName();
				out.print(targetVarName + " = ");
				// Create a new instance
				if (ctx.mapsToPrimitiveType(targetType)) {
					ctx.invoke(genDefaultValue, targetType, ctx, out);
				}
				else {
					ctx.invoke(genInstantiation, targetType, ctx, out);
				}
				out.println(';');
				genGetSingleRowFromResultSet(targetType, targetVarName, var_resultSet, ctx, out);
				out.print("ezeList.add(");
				out.print(targetVarName);
				out.println(");");
				// Generate the adding of the result to the list;
				out.println('}');
				
				Assignment assignment = factory.createAssignment();
				if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					assignment.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));

				Field field = factory.createField();
				field.setName("ezeList");
				field.setType(target.getType());
				field.setIsNullable(target.isNullable());
				MemberName rhs = factory.createMemberName();
				if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					rhs.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
				rhs.setMember(field);
				rhs.setId(field.getName());

				assignment.setLHS((LHSExpr)target);
				assignment.setRHS(rhs);
				ctx.invoke(genExpression, assignment, ctx, out);
				out.println(";");
			}
			else {
				targetType = (EGLClass)target.getType().getClassifier();
				if(!isResultSet)
					out.println("if(" + var_resultSet + ".next()) {");
				genGetSingleRowFromResultSet(stmt.getTarget(), var_resultSet, ctx, out);
				if(!isResultSet)
					out.println('}');
				
			}
		}
		else {
			if (!isResultSet)
				out.println("if(" + var_resultSet + ".next()) {");
			genGetSingleRowFromResultSet(stmt.getTargets(), var_resultSet, ctx, out);
			if (!isResultSet)
				out.println('}');
		}
		if (!isResultSet)
			out.println(var_resultSet + ".close();");
	}
		
}
