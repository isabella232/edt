package org.eclipse.edt.compiler;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

public interface StatementValidator {
	Map<String, StatementValidator> Registry = new HashMap<String, StatementValidator>();
	void validateStatement(Statement stmt, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions);
}
