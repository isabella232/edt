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

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.persistence.sql.Utils;
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SqlAddStatement;

public class SqlAddStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlAddStatement addStmt, Context ctx, TabbedWriter out) {
		if (addStmt.getSqlString() != null) {
			genSqlStatementSetup(addStmt, ctx, out);
			boolean targetIsList = addStmt.getTarget().getType().getClassifier().equals(TypeUtils.Type_LIST);
			EGLClass targetType = null;
			if (targetIsList) {
				targetType = (EGLClass)((ArrayType)addStmt.getTarget().getType()).getElementType().getClassifier();
				out.print("for (");
				ctx.invoke(genRuntimeTypeName, targetType, ctx, out, TypeNameKind.EGLImplementation);
				out.print(" " + var_listElement + " : ");
				ctx.invoke(genExpression, addStmt.getTarget(), ctx, out);
				out.println(") {");
				genAddSingleValue(targetType, var_listElement, ctx, out);
				out.println("}");
			}
			else if (addStmt.getTargets().size() > 1) {
				int i = 1;
				for(Expression target : addStmt.getTargets()){
					genSetColumnValue(addStmt, target, var_statement, i, ctx, out);
					i++;
				}
				out.println(var_statement + ".execute();");
			}
			else{
				targetType = (EGLClass)addStmt.getTarget().getType().getClassifier();
				if(Utils.isMappedSQLType(targetType)){
					genSetColumnValue(addStmt, addStmt.getTarget(), var_statement, 1, ctx, out);
				}
				else{
					TabbedWriter temp = ctx.getTabbedWriter();
					ctx.invoke(genExpression, addStmt.getTarget(), ctx, temp);
					genAddSingleValue(targetType, temp.getCurrentLine(), ctx, out);
				}
				out.println(var_statement + ".execute();");
			}
			genGetGeneratedColumns(addStmt, ctx, out);
			genSqlStatementEnd(addStmt, ctx, out);
		}
		else {
			out.println(err_noSqlGenerated);
		}
	}

	private void genGetGeneratedColumns(SqlAddStatement addStmt, Context ctx, TabbedWriter out) {
		if(hasGeneratedValues(addStmt)){
			int idx = 1;
			String generatedColumnsResultSet = ctx.nextTempName();
			//java.sql.ResultSet eze$Temp2 = ezeStatement.getGeneratedKeys();
			out.print("java.sql.ResultSet ");
			out.print(generatedColumnsResultSet);
			out.print(" = ");
			out.print(var_statement);
			out.println(".getGeneratedKeys();");
			//if (eze$Temp2 != null && eze$Temp2.next()) {
			out.print("if (");
			out.print(generatedColumnsResultSet);
			out.print(" != null && ");
			out.print(generatedColumnsResultSet);
			out.println(".next()) {");
			boolean targetIsList = addStmt.getTarget().getType().getClassifier().equals(TypeUtils.Type_LIST);
			EGLClass targetType = null;
			if (targetIsList) {
				//array are currently not supported by the add
			}
			else if (addStmt.getTargets().size() > 1) {
				//scalars are not supported to set the identity
			}
			else{
				targetType = (EGLClass)addStmt.getTarget().getType().getClassifier();
				if(Utils.isMappedSQLType(targetType)){
				}
				else{
					for(Field field : targetType.getFields()){
				    	if(field.getAnnotation(SqlActionStatementTemplate.AnnotationSQLGeneratedValue) != null){
				    		genSetTargetFromResultSet(addStmt.getTarget(), field, generatedColumnsResultSet, idx++, ctx, out);
				    	}
					}
				}
			}
			out.println("}");
		}
	}
	
	public void genStatementOptions(SqlAddStatement addStmt, Context ctx, TabbedWriter out, MemberName member) {
		if(hasGeneratedValues(addStmt)){
			out.print(", java.sql.Statement.RETURN_GENERATED_KEYS");
		}
	}

	private boolean hasGeneratedValues(SqlAddStatement addStmt){
		boolean targetIsList = addStmt.getTarget().getType().getClassifier().equals(TypeUtils.Type_LIST);
		EGLClass targetType = null;
		if (targetIsList) {
			//array are currently not supported by the add
		}
		else if (addStmt.getTargets().size() > 1) {
			//scalars are not supported to set the identity
		}
		else{
			targetType = (EGLClass)addStmt.getTarget().getType().getClassifier();
			if(Utils.isMappedSQLType(targetType)){
			}
			else{
				for(Field field : targetType.getFields()){
			    	if(field.getAnnotation(SqlActionStatementTemplate.AnnotationSQLGeneratedValue) != null){
			    		return true;
			    	}
				}
			}
		}
		return false;
	}
	
	private void genAddSingleValue(EGLClass type, String varName, Context ctx, TabbedWriter out) {		
		int i = 1;
		for (Field f : type.getFields()) {
			if (Utils.isInsertable(f) && Utils.isMappedSQLType((EGLClass)f.getType().getClassifier())) {
				genSetColumnValue(f, var_statement, varName, i, ctx, out);
				i++;
			}
		}
	}
}
