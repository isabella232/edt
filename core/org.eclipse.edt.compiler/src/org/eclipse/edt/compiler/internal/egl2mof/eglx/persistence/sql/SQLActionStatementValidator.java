package org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql;

import org.eclipse.edt.compiler.internal.core.validation.DefaultStatementValidator;

public class SQLActionStatementValidator extends DefaultStatementValidator {

	public boolean visit(org.eclipse.edt.compiler.core.ast.AddStatement addStatement) {
		//TODO validate the sql action statement
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.CloseStatement closeStatement) {
		//TODO validate the sql action statement
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.DeleteStatement deleteStatement) {
		//TODO validate the sql action statement
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.ExecuteStatement executeStatement) {
		//TODO validate the sql action statement
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement foreachStatement) {
		//TODO validate the sql action statement
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByKeyStatement getStatement) {
		//TODO validate the sql action statement
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.OpenStatement openStatement) {
		//TODO validate the sql action statement
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.PrepareStatement prepareStatement) {
		//TODO validate the sql action statement
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReplaceStatement replaceStatement) {
		//TODO validate the sql action statement
		return false;
	}
}
