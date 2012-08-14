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
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.StructPart;
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
				final Object[] part = new Object[1];
				final String[] functionID = new String[1];
				final Function[] function = new Function[1];
				exp.accept(new AbstractASTVisitor() {
					@Override
					public boolean visit(org.eclipse.edt.compiler.core.ast.ArrayAccess arrayAccess) {
						if(part[0] == null){
							arrayAccess.getArray().accept(this);
							while( part[0] instanceof ArrayType){
								part[0] = ((ArrayType)part[0]).getElementType();
							}
						}
						return false;
					};
					@Override
					public boolean visit(org.eclipse.edt.compiler.core.ast.FieldAccess fieldAccess) {
						if(functionID[0] == null){
							functionID[0] = fieldAccess.getID();
						}
						if(part[0] == null){
							fieldAccess.getPrimary().accept(this);
						}
						return false;
					};
					@Override
					public boolean visit(ThisExpression thisExpression) {
						if(thisExpression.resolveElement() instanceof Function){
							setFunction((Function)thisExpression.resolveElement());
						}
						if(part[0] == null){
							part[0] = thisExpression.resolveType();
						}
						return false;
					}
					@Override
					public boolean visit(SimpleName simpleName) {
						if(part[0] == null){
							part[0] = simpleName.resolveType();
						}
						return false;
					}
					@Override
					public boolean visit(QualifiedName qualifiedName) {
						if(qualifiedName.resolveElement() instanceof Function){
							setFunction((Function)qualifiedName.resolveElement());
						}
						else{
							if(functionID[0] == null){
								functionID[0] = qualifiedName.getIdentifier();
							}
							qualifiedName.getQualifier().accept(this);
						}
						return false;
					}
					@Override
					public boolean visit(FunctionInvocation functionInvocation) {
						if(part[0] == null && 
								functionInvocation.getTarget() != null &&
								functionInvocation.getTarget().resolveElement() instanceof Function){
							setFunction((Function)functionInvocation.getTarget().resolveElement());
						}
						return false;
					}
					private void setFunction(Function func){
						function[0] = func;
						part[0] = function[0].getContainer();
						functionID[0] = function[0].getId();
					}
				});
				if(part[0] instanceof Service){
					return true;
				}
				if(function[0] != null){
					return isServiceProxy(function[0]);
				}
				return part[0] instanceof StructPart && isServiceProxy(((StructPart)part[0]).getFunction(functionID[0]));
			}
		}
		else if (node instanceof NestedFunction) {
			return isServiceProxy(((NestedFunction)node).getName().resolveMember());
		}
		return false;
	}
	
	private static boolean isServiceProxy(Member member){
		return	member.getAnnotation("eglx.rest.Rest") != null ||
						member.getAnnotation("eglx.rest.EGLService") != null;
	}
}
