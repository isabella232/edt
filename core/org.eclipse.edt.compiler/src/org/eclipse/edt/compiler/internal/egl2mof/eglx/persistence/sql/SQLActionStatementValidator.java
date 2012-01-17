
package org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql;

import org.eclipse.edt.compiler.internal.core.validation.DefaultStatementValidator;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation.AddStatementValidator;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation.DeleteStatementValidator;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation.ExecuteStatementValidator;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation.ForEachStatementValidator;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation.GetByKeyStatementValidator;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation.GetByPositionStatementValidator;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation.OpenStatementValidator;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation.PrepareStatementValidator;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation.ReplaceStatementValidator;

public class SQLActionStatementValidator extends DefaultStatementValidator {

	public boolean visit(org.eclipse.edt.compiler.core.ast.AddStatement addStatement) {
		new AddStatementValidator(addStatement, problemRequestor, compilerOptions).validate();
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.CloseStatement closeStatement) {
		// nothing to validate.
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.DeleteStatement deleteStatement) {
		new DeleteStatementValidator(deleteStatement, problemRequestor, compilerOptions).validate();
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.ExecuteStatement executeStatement) {
		new ExecuteStatementValidator(executeStatement, problemRequestor, compilerOptions).validate();
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement foreachStatement) {
		new ForEachStatementValidator(foreachStatement, problemRequestor, compilerOptions).validate();
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByKeyStatement getStatement) {
		new GetByKeyStatementValidator(getStatement, problemRequestor, compilerOptions).validate();
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByPositionStatement getStatement) {
		new GetByPositionStatementValidator(getStatement, problemRequestor, compilerOptions).validate();
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.OpenStatement openStatement) {
		new OpenStatementValidator(openStatement, problemRequestor, compilerOptions).validate();
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.PrepareStatement prepareStatement) {
		new PrepareStatementValidator( prepareStatement, problemRequestor, compilerOptions ).validate();
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReplaceStatement replaceStatement) {
		new ReplaceStatementValidator(replaceStatement, problemRequestor, compilerOptions).validate();
		return false;
	}
}
