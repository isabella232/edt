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

import org.eclipse.edt.compiler.ICompilerExtension;
import org.eclipse.edt.compiler.PartValidator;
import org.eclipse.edt.compiler.StatementValidator;
import org.eclipse.edt.compiler.SystemEnvironmentUtil;
import org.eclipse.edt.compiler.TypeValidator;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.eglx.jtopen.gen.IBMiCallStatement;
import org.eclipse.edt.mof.eglx.jtopen.gen.IBMiElementGenerator;
import org.eclipse.edt.mof.eglx.jtopen.validation.IBMiProgramCallStatementValidator;

public class IBMiExtension implements ICompilerExtension {
	
	@Override
	public String[] getSystemEnvironmentPaths() {
		return new String[]{SystemEnvironmentUtil.getSystemLibraryPath(IBMiCallStatement.class, "egllib")};
	}
	
	@Override
	public Class[] getExtendedTypes() {
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
	public PartValidator getValidatorFor(Part part) {
		// No special validators.
		return null;
	}
	
	@Override
	public StatementValidator getValidatorFor(Statement stmt) {
		// Call statement can be extended.
		if (shouldExtend(stmt)) {
			return new IBMiProgramCallStatementValidator();
		}
		return null;
	}
	
	@Override
	public TypeValidator getValidatorFor(Type type) {
		// No special validators.
		return null;
	}
	
	private boolean shouldExtend(Node node) {
		if (node instanceof CallStatement) {
			CallStatement stmt = (CallStatement)node;
			if (stmt.getUsing() != null) {
				return Utils.isIBMiConnection(stmt.getUsing().resolveType());
			}
			else {
				Expression exp = stmt.getInvocationTarget();
				Member binding = exp.resolveMember();
				//only service can have a Service type qualifier with no using clause
				return binding instanceof Function && 
						!isFunctionServiceQualified(exp, (Function)binding) &&
						binding.getAnnotation("eglx.jtopen.annotations.IBMiProgram") != null;
			}
		}
		else if (node instanceof NestedFunction) {
			NestedFunction func = (NestedFunction)node;
			return func.getName().resolveMember() instanceof Function && func.getName().resolveMember().getAnnotation("eglx.jtopen.annotations.IBMiProgram") != null;
		}
		
		return false;
	}
	
	private static boolean isFunctionServiceQualified(	Expression exp, Function function){
		return !(exp instanceof FieldAccess && ((FieldAccess)exp).getPrimary() instanceof ThisExpression) &&
				function.getContainer() instanceof Service;
	}
}
