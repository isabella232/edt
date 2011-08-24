package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.StatementTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.sql.SqlClause;
import org.eclipse.edt.mof.egl.sql.SqlHostVariableToken;
import org.eclipse.edt.mof.egl.sql.SqlIOStatement;
import org.eclipse.edt.mof.egl.sql.SqlToken;

public abstract class SqlIOStatementTemplate extends StatementTemplate {
	public static final String sqlStmtKey = "org.eclipse.edt.gen.java.sql.stmtkey";
	public static final String genClause = "genClause";
	public static final String genSelectClause = "genSelectClause";
	public static final String var_connection = "ezeConn";
	public static final String var_datasource = "ds";
	public static final String var_resultSet = "ezeResult";
	public static final String var_statement = "ezeStatement";
	public static final String var_listElement = "ezeElement";
	public static final String expr_getConnection = "getConnection()";
	public static final String class_Statement = "java.sql.PreparedStatement";
	public static final String class_ResultSet = "java.sql.ResultSet";
	public static final String err_noSqlGenerated = "<no SQL generated>";
	
	public String quoted(String value) {
		StringBuilder out = new StringBuilder();
		out.append('"');
		out.append(value);
		out.append('"');
		return out.toString();
	}
	
	public String getSQLString(SqlIOStatement stmt) {
		StringBuilder out = new StringBuilder();
		for (SqlClause sql : stmt.getSqlClauses()) {
			if (sql != null) {
				for(SqlToken token : sql.getTokens()) {
					out.append(' ');
					out.append(token.getString());
				}
			}
		}
		return out.toString();
	}

	public void genStatementBody(Statement stmt, Context ctx, TabbedWriter out ) {
		// TODO Auto-generated method stub
		
	}

	public void genStatementEnd(TabbedWriter out) {
		// Do nothing as these statements are handled as sets of java code
		// that will manage their own blocks of code
	}

	public List<Expression> processHostVariables(SqlIOStatement stmt, Context ctx) {
		int index = 1;
		List<Expression> exprs = null;
		for(SqlClause clause : stmt.getSqlClauses()) {
			for(SqlToken token : clause.getTokens()) {
				if (token instanceof SqlHostVariableToken) {
					Member field = ((SqlHostVariableToken)token).getMember();
					if (exprs == null) exprs = new ArrayList<Expression>();
					if (ctx.getAttribute(field, SqlTokenTemplate.Attr_hostVariableIndex) == null) {
						ctx.putAttribute(field, SqlTokenTemplate.Attr_hostVariableIndex, index);
						exprs.add(((SqlHostVariableToken)token).getHostVarExpression());
						index++;
					}
				}
			}
		}
		return exprs;
	}
	
	public void genSetParameterType(Classifier type, Context ctx, TabbedWriter out) {
		//TODO to handle types that do not have predefined mapping to SQL
		out.print("setObject(");
	}
	
	public void genSqlStatementSetup(SqlIOStatement stmt, Context ctx, TabbedWriter out, String var_stmt) {
		out.println("try {");
		Integer stmtNumber = getNextStatementKey(ctx);
		String typeSignature = quoted(((Type)((Function)stmt.getContainer()).getContainer()).getTypeSignature());
		out.print(class_Statement + " " + var_stmt + " = (java.sql.PreparedStatement)");
		ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
		out.println(".getStatement(" + typeSignature + ", " + stmtNumber + ");");
		out.println("if (" + var_stmt + "== null) {");
		out.print("String queryStr = ");
		out.print(quoted(stmt.getSqlString()));
		out.println(';');
		out.println(var_stmt + " = " + var_datasource + '.' + expr_getConnection + ".prepareStatement(queryStr);");
		ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
		out.println(".registerStatement(" + typeSignature + ", " + var_stmt + ");");
		out.println("}");

	}
	
	public void genSqlStatementSetup(SqlIOStatement stmt, Context ctx, TabbedWriter out) {
		genSqlStatementSetup(stmt, ctx, out, var_statement);
	}

	
	public void genDefaultWhereClauseParameterSettings(EGLClass targetType, Expression targetExpr, String var_statement, int startParmIndex, Context ctx, TabbedWriter out) {
		int i = startParmIndex;
		for (Field f : targetType.getFields()) {
			if (isKeyField(f)) {
				out.print(var_statement);
				out.print(".setObject(" + i + ", ");
				if (targetType instanceof ArrayType) {
					out.print(var_listElement);
				}
				else {
					ctx.invoke(genExpression, targetExpr, ctx, out);
				}
				out.print('.');
				ctx.invoke(genName, f, ctx, out);
				out.println(");");
				i++;
			}
		}

	}

	
	public void genSqlStatementEnd(SqlIOStatement stmt, Context ctx, TabbedWriter out) {
		out.println("}");
		out.println("catch(java.sql.SQLException ex) {");
		out.println("throw new eglx.persistence.sql.SQLException(ex);");
		out.println('}');
	}
	
	public Integer getNextStatementKey(Context ctx) {
		Integer key = (Integer)ctx.get(sqlStmtKey);
		if (key == null) {
			key = 0;
		}
		else {
			key++;
		}
		ctx.put(sqlStmtKey, key);
		return key;
	}
	
	public String getCursorNameFromDataSource(Expression datasource, Context ctx) {
		Member ds = (Member)((Name)datasource).getNamedElement();
		return (String)ctx.getAttribute(ds, "eglx.persistence.sql.CursorName");
	}

	public EGLClass getTargetType(SqlIOStatement stmt) {
		Type type = stmt.getTarget().getType();
		if (type instanceof ArrayType) {
			return (EGLClass)((ArrayType)type).getElementType();
		}
		else {
			return (EGLClass)type;
		}
	}
	
	public String getTableName(EGLClass entity) {
		Annotation table = entity.getAnnotation("eglx.persistence.sql.Table");
		return table == null ? entity.getName() : (String)table.getValue();
	}
	
	public String getColumnName(Field field) {
		Annotation column = field.getAnnotation("eglx.persistence.sql.Column");
		return column == null ? field.getName() : (String)column.getValue();
	}

	public boolean isKeyField(Field field) {
		return field.getAnnotation("eglx.persistence.Id") != null;
	}
	
	public boolean isAutoGenKeyField(Field field) {
		return isKeyField(field) && field.getAnnotation("eglx.persistence.sql.GeneratedValue") != null;
	}

	public boolean isAssociationField(Field field, Context ctx) {
		Type type = field.getType();
		if (type instanceof ArrayType) return true;
		if (!ctx.mapsToPrimitiveType(type)) return true;
		return false;
	}
	
	public boolean isTransient(Field field) {
		return field.getAnnotation("eglx.persistence.Transient") != null;
	}
	
	public boolean isPersistable(Field field, Context ctx) {
		return !isAutoGenKeyField(field) 
			&& !isAssociationField(field, ctx)
			&& !isTransient(field);
	}
}
