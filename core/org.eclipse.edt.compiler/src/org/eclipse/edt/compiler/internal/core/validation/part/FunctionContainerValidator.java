/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation.part;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.ClassDataDeclarationValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;


/**
 * @author Dave Murray
 */
public abstract class FunctionContainerValidator extends AbstractASTVisitor {
	
	protected IProblemRequestor problemRequestor;
	protected IPartBinding partBinding;
	protected Part partNode;
    protected ICompilerOptions compilerOptions;
	
	public FunctionContainerValidator(IProblemRequestor problemRequestor, IPartBinding partBinding, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.partBinding = partBinding;
		this.compilerOptions = compilerOptions;
	}
	
	@Override
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
		classDataDeclaration.accept(new ClassDataDeclarationValidator(problemRequestor, compilerOptions, partBinding));
		return false;
	}
	
	@Override
	public boolean visit(NestedFunction nestedFunction) {
		nestedFunction.accept(new FunctionValidator(problemRequestor, partBinding, compilerOptions));
		return false;
	}
	
	@Override
	public boolean visit(Constructor constructor) {
		constructor.accept(new FunctionValidator(problemRequestor, partBinding, compilerOptions));
		return false;
	}
	
	@Override
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
	
	@Override
	public boolean visit(UseStatement useStatement) {
		//TODO Validator blows up right now
//		useStatement.accept(new UseStatementValidator(partBinding,partNode.getName().getCanonicalName(),problemRequestor, compilerOptions));
		return false;
	}
	
	protected void checkImplements(List<Name> implementedInterfaces) {
		if (implementedInterfaces == null || implementedInterfaces.size() == 0) {
			return;
		}
		
		for (Name name : implementedInterfaces) {
			Type type = name.resolveType();
			if (type != null && !(type instanceof Interface)) {
				problemRequestor.acceptProblem(
						name,
						IProblemRequestor.PART_MUST_IMPLEMENT_AN_INTERFACE);
			}
		}
	}
	
	protected void checkInterfaceFunctionsOverriden(StructPart binding) {
		List<Function> declaredAndInheritedFunctions = null;
		for (Function interfaceFunc : getInterfaceFunctionList(binding)) {
			boolean foundMatchingHandlerFunc = false;
			
			if (declaredAndInheritedFunctions == null) {
				declaredAndInheritedFunctions = getDeclaredAndInheritedFunctionList(binding);
			}
			
			for (Function handlerFunc : declaredAndInheritedFunctions) {
				if(BindingUtil.functionSignituresAreIdentical(handlerFunc, interfaceFunc)) {
					foundMatchingHandlerFunc = true;
					break;
				}
			}
			
			if(!foundMatchingHandlerFunc) {
				problemRequestor.acceptProblem(
					partNode.getName(),
					IProblemRequestor.INTERFACE_FUNCTION_MISSING,
					new String[] {
						partNode.getName().getCanonicalName(),
						interfaceFunc.getCaseSensitiveName() + "(" + getTypeNamesList(interfaceFunc.getParameters()) + ")",
						((Interface)interfaceFunc.getContainer()).getCaseSensitiveName()
					});
			}
		}
	}
	
	private List<Function> getDeclaredAndInheritedFunctionList(StructPart binding) {
		List<Function> retVal = new ArrayList();
		getInheritedFunctions(binding, retVal, new HashSet<StructPart>());
		return retVal;
	}
	
	private void getInheritedFunctions(StructPart part, List<Function> funcs, Set<StructPart> seenParts) {
		if (seenParts.contains(part)) {
			return;
		}
		seenParts.add(part);
		
		for (Function function : part.getFunctions()) {
			if(function.getAccessKind() != AccessKind.ACC_PRIVATE) {
				funcs.add(function);
			}
		}
		for (StructPart superPart : part.getSuperTypes()) {
			if (!(superPart instanceof Interface)) {
				getInheritedFunctions(superPart, funcs, seenParts);
			}
		}
	}
	
	private List<Function> getInterfaceFunctionList(StructPart binding) {
		List<Function> retVal = new ArrayList();
		Set<Interface> seen = new HashSet<Interface>();
		for (Interface iface : binding.getInterfaces()) {
			getInterfaceFunctions(iface, retVal, seen);
		}
		return retVal;
	}
	
	private void getInterfaceFunctions(Interface iface, List<Function> funcs, Set<Interface> seenInterfaces) {
		if (seenInterfaces.contains(iface)) {
			return;
		}
		seenInterfaces.add(iface);
		
		for(Function function : iface.getFunctions()) {
			if(function.getAccessKind() != AccessKind.ACC_PRIVATE) {
				funcs.add(function);
			}
		}
		
		for (Interface iface2: iface.getInterfaces()) {
			getInterfaceFunctions(iface2, funcs, seenInterfaces);
		}
	}

	private String getTypeNamesList(List<org.eclipse.edt.mof.egl.FunctionParameter> types) {
		StringBuffer sb = new StringBuffer();
		if (!types.isEmpty()) {
			sb.append(" ");
		}
		for (Iterator<org.eclipse.edt.mof.egl.FunctionParameter> iter = types.iterator(); iter.hasNext();) {
			org.eclipse.edt.mof.egl.FunctionParameter nextParm = iter.next();
			Type nextType = nextParm.getType();
			if (nextType != null) {
				sb.append(nextType.getTypeSignature());
				switch (nextParm.getParameterKind()) {
					case PARM_IN:
						sb.append( " " + IEGLConstants.KEYWORD_IN );
						break;
					case PARM_OUT:
						sb.append( " " + IEGLConstants.KEYWORD_OUT );
						break;
					case PARM_INOUT:
						sb.append( " " + IEGLConstants.KEYWORD_INOUT );
						break;
				}
				if (iter.hasNext()) {
					sb.append(", ");
				}
				else {
					sb.append(" ");
				}
			}
		}
		return sb.toString();
	}
}
