package org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation;

import java.util.Map;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class SQLResultSetControlValidator  implements IAnnotationValidationRule{

	@Override
	public void validate(Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (Binding.isValidBinding(targetTypeBinding)) {
			if (targetTypeBinding.getKind() != ITypeBinding.EXTERNALTYPE_BINDING ||
					!(InternUtil.intern(new String[] {"eglx", "persistence", "sql"}) == targetTypeBinding.getPackageName() &&
							InternUtil.intern("SQLResultSet").equals(targetTypeBinding.getName()))) {
				problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.SQLRESULTSET_ANNOTATION_TYPE_ERROR,
						new String[] {});
			}
		}
	}

}
