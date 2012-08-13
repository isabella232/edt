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
package org.eclipse.edt.mof.eglx.services;

import org.eclipse.edt.compiler.ASTValidator;
import org.eclipse.edt.compiler.ICompilerExtension;
import org.eclipse.edt.compiler.SystemEnvironmentUtil;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.eglx.services.gen.ServiceElementGenerator;
import org.eclipse.edt.mof.eglx.services.gen.ServicesCallStatement;
import org.eclipse.edt.mof.eglx.services.validation.EglServiceProxyFunctionValidator;
import org.eclipse.edt.mof.eglx.services.validation.RestServiceProxyFunctionValidator;
import org.eclipse.edt.mof.eglx.services.validation.ServicesCallStatementValidator;

public class ServicesExtension implements ICompilerExtension {
	
	@Override
	public String[] getSystemEnvironmentPaths() {
		return new String[]{SystemEnvironmentUtil.getSystemLibraryPath(ServicesCallStatement.class, "egllib")};
	}
	
	@Override
	public Class<?>[] getExtendedTypes() {
		return new Class[]{CallStatement.class, NestedFunction.class};
	}
	
	@Override
	public ElementGenerator getElementGeneratorFor(Node node) {
		if (shouldExtend(node)) {
			return new ServiceElementGenerator();
		}
		return null;
	}
	
	@Override
	public ASTValidator getValidatorFor(Node node) {
		// Call statement can be extended.
		if (shouldExtend(node)) {
			if (node instanceof CallStatement) {
				return new ServicesCallStatementValidator();
			}
			else if (node instanceof NestedFunction &&
					((NestedFunction)node).getName().resolveMember() instanceof Function) {
				if(((NestedFunction)node).getName().resolveMember().getAnnotation("eglx.rest.EglService") != null){
					return new EglServiceProxyFunctionValidator();
				}
				else if(((NestedFunction)node).getName().resolveMember().getAnnotation("eglx.rest.Rest") != null){
					return new RestServiceProxyFunctionValidator();
				}
			}
		}
		return null;
	}
	
	private boolean shouldExtend(Node node) {
		if(node instanceof CallStatement){
			if(((CallStatement)node).getUsing() != null){
				return Utils.isIHTTP(((CallStatement)node).getUsing().resolveType());
			}
			else{
				Expression exp = ((CallStatement)node).getInvocationTarget();
				if(exp instanceof QualifiedName){
					Object element = ((QualifiedName)exp).getQualifier().resolveElement();
					return (element instanceof Member &&
							((((Member)element).getAnnotation("eglx.rest.Rest") != null ||
									((Member)element).getAnnotation("eglx.rest.EGLService") != null)) ||
							element instanceof Service);
				}
				
			}
		}
		else if (node instanceof NestedFunction) {
			return ((NestedFunction)node).getName().resolveMember() instanceof Function && 
					(((NestedFunction)node).getName().resolveMember().getAnnotation("eglx.rest.EglService") != null ||
							((NestedFunction)node).getName().resolveMember().getAnnotation("eglx.rest.Rest") != null );
		}
		return false;
	}
}
