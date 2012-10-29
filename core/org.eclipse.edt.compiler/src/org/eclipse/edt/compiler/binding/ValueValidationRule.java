package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.Annotation;

public abstract class ValueValidationRule extends AbstractValidationRule {
	
	public ValueValidationRule(String caseSensitiveInternedName) {
		super(caseSensitiveInternedName);
	}

	public abstract void validate(Node errorNode, Node target, Annotation annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions);
}
