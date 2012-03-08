/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
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
							(InternUtil.intern("SQLResultSet").equals(targetTypeBinding.getName()) || InternUtil.intern("SQLStatement").equals(targetTypeBinding.getName())))) {
				problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.SQLRESULTSET_ANNOTATION_TYPE_ERROR,
						new String[] {});
			}
		}
	}

}
