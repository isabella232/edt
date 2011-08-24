package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.sql.SqlHostVariableToken;

public class SqlHostVariableTokenTemplate extends SqlTokenTemplate {

	public void genToken(SqlHostVariableToken token, Context ctx, TabbedWriter out) {
		Field field = (Field)((Name)token.getHostVarExpression()).getNamedElement();
		Integer index = (Integer)ctx.getAttribute(field, Attr_hostVariableIndex);
		out.print('?');
		out.print(index);
	}

}
