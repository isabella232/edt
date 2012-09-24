package org.eclipse.edt.compiler.internal.core.lookup;

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;

public class StereotypeTypeBinder extends AnnotationTypeBinder {

	public StereotypeTypeBinder(IRPartBinding irBinding, Scope scope,
			IDependencyRequestor dependencyRequestor,
			IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super(irBinding, scope, dependencyRequestor, problemRequestor, compilerOptions);
	}

}
