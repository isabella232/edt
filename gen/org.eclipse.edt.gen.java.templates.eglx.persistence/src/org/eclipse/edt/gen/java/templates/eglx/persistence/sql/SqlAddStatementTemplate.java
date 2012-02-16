package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlAddStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.utils.SQL;

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
				if(SQL.isMappedSQLType(targetType)){
					genSetColumnValue(addStmt, addStmt.getTarget(), var_statement, 1, ctx, out);
				}
				else{
					TabbedWriter temp = ctx.getTabbedWriter();
					ctx.invoke(genExpression, addStmt.getTarget(), ctx, temp);
					genAddSingleValue(targetType, temp.getCurrentLine(), ctx, out);
				}
				out.println(var_statement + ".execute();");
			}
			
			genSqlStatementEnd(addStmt, ctx, out);
		}
		else {
			out.println(err_noSqlGenerated);
		}
	}

	private void genAddSingleValue(EGLClass type, String varName, Context ctx, TabbedWriter out) {		
		int i = 1;
		for (Field f : type.getFields()) {
			if (SQL.isInsertable(f) && SQL.isMappedSQLType((EGLClass)f.getType().getClassifier())) {
				genSetColumnValue(f, var_statement, varName, i, ctx, out);
				i++;
			}
		}
	}
}
