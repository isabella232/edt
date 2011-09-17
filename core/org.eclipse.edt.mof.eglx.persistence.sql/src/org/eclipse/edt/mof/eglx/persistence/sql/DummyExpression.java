package org.eclipse.edt.mof.eglx.persistence.sql;

import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.Type;

public interface DummyExpression extends LHSExpr {	
	String getExpr();
	void setExpr(String expr);
	
	void setType(Type type);
}
