package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import java.io.StringWriter;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.sql.SqlAddStatement;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class SqlAddStatementTemplate extends SqlIOStatementTemplate {

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
			else {
				targetType = (EGLClass)addStmt.getTarget().getType().getClassifier();
				TabbedWriter temp = new TabbedWriter(new StringWriter());
				ctx.invoke(genExpression, addStmt.getTarget(), ctx, temp);
				String targetVar = temp.getCurrentLine();
				genAddSingleValue(targetType, targetVar, ctx, out);
			}
			
			genSqlStatementEnd(addStmt, ctx, out);
		}
		else {
			out.println(err_noSqlGenerated);
		}
	}
	
	public void genAddSingleValue(EGLClass type, String varName, Context ctx, TabbedWriter out) {		
		int i = 1;
		for (Field f : type.getFields()) {
			if (isPersistable(f, ctx)) {
				out.print(var_statement);
				out.print(".setObject(" + i + ", ");
				out.print(varName);
				out.print('.');
				ctx.invoke(genName, f, ctx, out);
				out.println(");");
				i++;
			}
		}
		out.println(var_statement + ".execute();");

	}
}
