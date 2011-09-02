package org.eclipse.edt.compiler.internal.core.validation;

import org.eclipse.edt.compiler.StatementValidator;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

public abstract class AbstractStatementValidator extends AbstractASTVisitor implements StatementValidator {
	
	IProblemRequestor problemRequestor;
	ICompilerOptions compilerOptions;
	
	@Override
	public void validateStatement(Statement stmt, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
		stmt.accept(this);
	}

}
