package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import java.io.StringWriter;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlGetByKeyStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.utils.SQL;

public class SqlGetByKeyStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlGetByKeyStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getSqlString() != null) {
			genSqlStatementSetup(stmt, ctx, out);
			if (stmt.getUsingExpressions()!= null) {
				int i = 1;
				for (Expression uexpr : stmt.getUsingExpressions()) {
					out.print(var_statement + ".setObject(" + i + ", ");
					ctx.invoke(genExpression, uexpr, ctx, out);
					out.println(");");
					i++;
				}
			}
			out.println(class_ResultSet + " " + var_resultSet + " = " + var_statement + ".executeQuery();");
			out.println("java.sql.ResultSetMetaData ezeMetaData = " + var_resultSet + ".getMetaData();");
			genPopulateFromResultSet(stmt, ctx, out, true);
			genSqlStatementEnd(stmt, ctx, out);
		}
		else {
			out.println("try {");
			// DataSource is a ResultSet already which is why there is no sql string
			out.print(class_ResultSet + " " + var_resultSet);
			out.print(" = ");
			ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
			out.println(".getResultSet();");
			out.println("java.sql.ResultSetMetaData ezeMetaData = " + var_resultSet + ".getMetaData();");
			genPopulateFromResultSet(stmt, ctx, out, false);
			genSqlStatementEnd(stmt, ctx, out);
		}
	}
	
	public void genPopulateFromResultSet(SqlGetByKeyStatement stmt, Context ctx, TabbedWriter out, boolean doClose ) {
		if (stmt.getTargets().size() == 1) {
			Expression target = stmt.getTarget();
			boolean targetIsList = target.getType() instanceof ArrayType;
			EGLClass targetType;
			if (targetIsList) {
				out.print(TypeUtils.Type_List + " ezeList = ");
				ctx.invoke(genInstantiation, target.getType().getClassifier(), ctx, out);
				out.println(";");
				// Assume target type is an EGLClass as other Classifiers would be filtered out by front end validation
				targetType = (EGLClass)((ArrayType)target.getType()).getElementType().getClassifier();
				out.println("while(" + var_resultSet + ".next()) {");
				ctx.invoke(genRuntimeTypeName, targetType, ctx, out, TypeNameKind.EGLImplementation);
				out.print(' ');
				String targetVarName = ctx.nextTempName();
				out.print(targetVarName);
				out.print(" = ");
				ctx.invoke(genInstantiation, targetType, ctx, out);
				out.println(";");
				genPopulateSingleResult(targetType, targetVarName, var_resultSet, ctx, out);
				out.print("ezeList.add(");
				out.print(targetVarName);
				out.println(");");
				// Generate the adding of the result to the list;
				out.println('}');
				ctx.invoke(genExpression, target, ctx, out);
				out.println(" = ezeList;");
			}
			else {
				targetType = (EGLClass)target.getType().getClassifier();
				out.println("if(" + var_resultSet + ".next()) {");
				TabbedWriter temp = new TabbedWriter(new StringWriter());
				ctx.invoke(genExpression, target, ctx, temp);
				String targetVarName = temp.getCurrentLine();
				out.println("if (" + targetVarName + " == null) {");
				out.print(targetVarName);
				out.print(" = ");
				ctx.invoke(genInstantiation, targetType, ctx, out);
				out.println(";");
				out.println("}");
				genPopulateSingleResult(targetType, targetVarName, var_resultSet, ctx, out);
				if (doClose)
					out.println(var_resultSet + ".close();");
				out.println('}');
				
			}
		}
		else {
			out.println("if(" + var_resultSet + ".next()) {");
			int i = 1;
			for (Expression target : stmt.getTargets()) {
				genSetTargetFromResultSet(target, var_resultSet, i, ctx, out);
				i++;
			}
			out.println('}');
		}
	}
	
	public void genSetTargetFromResultSet(Expression target, String var_resultSet, int columnIndex, Context ctx, TabbedWriter out) {
		ctx.invoke(genExpression, target, ctx, out);
		out.print(" = ");
		genGetColumnValue(target, columnIndex, ctx, out);
	}
	
	public void genGetColumnValue(Expression target, int columnIndex, Context ctx, TabbedWriter out) {
		out.print('(');
		ctx.invoke(genRuntimeTypeName, target.getType(), ctx, out, TypeNameKind.JavaImplementation);
		out.print(')');
		out.println(var_resultSet + ".getObject(" + columnIndex + ");");
	}

	public void genGetColumnValue(Expression target, String columnName, Context ctx, TabbedWriter out) {
		out.print('(');
		ctx.invoke(genRuntimeTypeName, target.getType(), ctx, out, TypeNameKind.JavaImplementation);
		out.print(')');
		out.println(var_resultSet + ".getObject(" + quoted(columnName) + ");");
	}

	public void genPopulateSingleResult(EGLClass targetType, String targetVarName, String resultSetName, Context ctx, TabbedWriter out) {
		out.println("for (int i=1; i<=ezeMetaData.getColumnCount(); i++) {");
		if (TypeUtils.isDynamicType(targetType)) {
			out.print(targetVarName + ".put(");
			out.print("ezeMetaData.getColumnName(i), ");
			out.println(var_resultSet + ".getObject(i));");
		}
		else {
			boolean doElse = false;
			for (Field field : targetType.getFields()) {
				if (!field.isStatic() && field.getAccessKind()!= AccessKind.ACC_PRIVATE && SQL.isPersistable(field)) {
					String columnName = SQL.getColumnName(field);
					if (doElse) out.print("else ");
					out.print("if (ezeMetaData.getColumnName(i).equalsIgnoreCase(");
					if (!doElse) doElse = true;
					out.print(quoted(columnName));
					out.println(")) {");					
					out.print(targetVarName + ".");
					ctx.invoke(genName, field, ctx, out);
					out.print(" = ");
					out.print('(');
					ctx.invoke(genRuntimeTypeName, field.getType(), ctx, out, TypeNameKind.JavaImplementation);
					out.print(')');
					out.println(var_resultSet + ".getObject(i);");
					out.println('}');
				}
			}
		}
		out.println("}");
	}
	
}
