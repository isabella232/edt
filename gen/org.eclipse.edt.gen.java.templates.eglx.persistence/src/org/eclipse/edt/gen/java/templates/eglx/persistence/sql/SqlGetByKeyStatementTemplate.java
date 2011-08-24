package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import java.io.StringWriter;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.sql.SqlClause;
import org.eclipse.edt.mof.egl.sql.SqlGetByKeyStatement;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class SqlGetByKeyStatementTemplate extends SqlIOStatementTemplate {

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
			genPopulateFromResultList(stmt, ctx, out, true);
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
			EGLClass targetType = (EGLClass)stmt.getTarget().getType().getClassifier();
			TabbedWriter temp = new TabbedWriter(new StringWriter());
			ctx.invoke(genExpression, stmt.getTarget(), ctx, temp);
			String targetVarName = temp.getCurrentLine();
			out.println("if (" + targetVarName + " == null) {");
			out.print(targetVarName);
			out.print(" = ");
			ctx.invoke(genInstantiation, targetType, ctx, out);
			out.println(";");
			out.println("}");
			genPopulateSingleResult(targetType, targetVarName, var_resultSet, ctx, out);
			genSqlStatementEnd(stmt, ctx, out);
		}
	}
	
	public void genPopulateFromResultList(SqlGetByKeyStatement stmt, Context ctx, TabbedWriter out, boolean doClose ) {
		Expression target = stmt.getTargets().size() > 0 ? stmt.getTargets().get(0) : null;
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
	
	public void genPopulateSingleResult(EGLClass targetType, String targetVarName, String resultSetName, Context ctx, TabbedWriter out) {
		out.println("for (int i=1; i<=ezeMetaData.getColumnCount(); i++) {");
		if (TypeUtils.isDynamicType(targetType)) {
			out.print(targetVarName + ".put(");
			out.print("ezeMetaData.getColumnName(i), ");
			out.println(var_resultSet + ".getObject(i));");
			out.println('}');
		}
		else {
			boolean doElse = false;
			for (Field field : targetType.getFields()) {
				if (!field.isStatic() && field.getAccessKind()!= AccessKind.ACC_PRIVATE && isPersistable(field, ctx)) {
					Annotation ann = field.getAnnotation("eglx.persistence.rdb.Column");
					String columnName = ann == null ? field.getName() : (String)ann.getValue("name");
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
	
	private void genSelectStatement(SqlGetByKeyStatement stmt, Context ctx, TabbedWriter out) {
		SqlClause select = stmt.getSelectClause();
		SqlClause from = stmt.getFromClause();
		SqlClause where = stmt.getWhereClause();
		SqlClause groupBy = stmt.getGroupByClause();
		SqlClause having = stmt.getHavingClause();
		SqlClause orderBy= stmt.getOrderByClause();
		SqlClause forUpdate = stmt.getForUpdateOfClause();
		
		// Generate the select clause - no need to worry about ROWID
		// as this is not a select for update
		ctx.invoke(genSelectClause, select, ctx, out, stmt, false);
		out.print( " + " );
		ctx.invoke(genClause, from, ctx, out, stmt);

		if( where != null )
		{
			out.print( " + " );
			ctx.invoke(genClause, where, ctx, out, stmt);
		}

		if( groupBy != null )
		{
			out.print( " + " );
			ctx.invoke(genClause, groupBy, ctx, out, stmt);
		}

		if( having != null )
		{
			out.print( " + " );
			ctx.invoke(genClause, having, ctx, out, stmt);
		}

		if( orderBy != null )
		{
			out.print( " + " );
			ctx.invoke(genClause, orderBy, ctx, out, stmt);
		}

		if( forUpdate != null )
		{
			out.print( " + " );
			ctx.invoke(genClause, forUpdate, ctx, out, stmt);
		}


	}
	
	public void genNamedNativeQuery(SqlGetByKeyStatement stmt, Context ctx, TabbedWriter out, String queryName) {
		out.print("@NamedNativeQuery(name=");
		out.print(quoted(queryName));
		out.print('"');
		genSelectStatement(stmt, ctx, out);
		out.print('"');
	}
}
