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
package org.eclipse.edt.mof.eglx.jtopen;

import org.eclipse.edt.compiler.ASTValidator;
import org.eclipse.edt.compiler.ICompilerExtension;
import org.eclipse.edt.compiler.SystemEnvironmentUtil;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.eglx.jtopen.gen.IBMiCallStatement;
import org.eclipse.edt.mof.eglx.jtopen.gen.IBMiElementGenerator;
import org.eclipse.edt.mof.eglx.jtopen.validation.IBMiFunctionValidator;
import org.eclipse.edt.mof.eglx.jtopen.validation.IBMiProgramCallStatementValidator;

public class IBMiExtension implements ICompilerExtension {
	
	@Override
	public String[] getSystemEnvironmentPaths() {
		return new String[]{SystemEnvironmentUtil.getSystemLibraryPath(IBMiCallStatement.class, "egllib")};
	}
	
	@Override
	public Class<?>[] getExtendedTypes() {
		return new Class[]{CallStatement.class, NestedFunction.class};
	}
	
	@Override
	public ElementGenerator getElementGeneratorFor(Node node) {
		if (shouldExtend(node)) {
			return new IBMiElementGenerator();
		}
		return null;
	}
	
	@Override
	public ASTValidator getValidatorFor(Node node) {
		// Call statement can be extended.
		if (shouldExtend(node)) {
			if (node instanceof CallStatement) {
				return new IBMiProgramCallStatementValidator();
			}
			else if (node instanceof NestedFunction) {
				return new IBMiFunctionValidator();
			}
		}
		return null;
	}
	
	private boolean shouldExtend(Node node) {
		if (node instanceof CallStatement) {
			CallStatement stmt = (CallStatement)node;
			if (stmt.getUsing() != null) {
				return Utils.isIBMiConnection(stmt.getUsing().resolveType());
			}
			else {
				if(((CallStatement)node).getInvocationTarget() != null &&
						((CallStatement)node).getInvocationTarget().resolveElement() instanceof Function){
					Function function = (Function)((CallStatement)node).getInvocationTarget().resolveElement();
					return !Utils.isFunctionServiceQualified(((CallStatement)node).getInvocationTarget(), function) &&
							function.getAnnotation("eglx.jtopen.annotations.IBMiProgram") != null;
				}
			}
		}
		else if (node instanceof NestedFunction) {
			return ((NestedFunction)node).getName().resolveMember() instanceof Function && ((NestedFunction)node).getName().resolveMember().getAnnotation("eglx.jtopen.annotations.IBMiProgram") != null;
		}
		
		return false;
	}
}
