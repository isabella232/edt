package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;

public class StereotypeTypeCompletor extends AnnotationTypeCompletor {

	public StereotypeTypeCompletor(Scope currentScope, IRPartBinding irBinding,
			IDependencyRequestor dependencyRequestor,
			IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super(currentScope, irBinding, dependencyRequestor, problemRequestor,
				compilerOptions);
	}

}
