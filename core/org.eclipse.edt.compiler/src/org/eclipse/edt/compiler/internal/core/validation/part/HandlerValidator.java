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

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.FunctionParameter.AttrType;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;

public class HandlerValidator extends FunctionContainerValidator {
	
	IRPartBinding irBinding;
	org.eclipse.edt.mof.egl.Handler handlerBinding;
	Handler handler = null;
	
	public HandlerValidator(IProblemRequestor problemRequestor, IRPartBinding irBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, irBinding, compilerOptions);
		this.irBinding = irBinding;
		handlerBinding = (org.eclipse.edt.mof.egl.Handler)irBinding.getIrPart();
	}
	
	public boolean visit(Handler ahandler) {
		handler = ahandler;
		partNode = ahandler;
		EGLNameValidator.validate(handler.getName(), EGLNameValidator.HANDLER, problemRequestor, compilerOptions);
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(ahandler); //TODO
		validateHandler();
		
		checkInterfaceFunctionsOverriden();
		return true;
	}
	
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
		super.visit(classDataDeclaration);
		return false;
	}
	
	public boolean visit(NestedFunction nestedFunction) {
		super.visit(nestedFunction);
		return false;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		super.visit(settingsBlock);
		return false;
	}
	
	public boolean visit(UseStatement useStatement) {
		super.visit(useStatement);
		return false;
	}
	
	private void validateHandler() {
		DefaultASTVisitor visitor1 =  new DefaultASTVisitor() {
			public boolean visit(NestedFunction nestedFunction) {
				
				final String funcName = nestedFunction.getName().getCanonicalName();
				
				DefaultASTVisitor visitor2 = new DefaultASTVisitor() {
					public boolean visit(NestedFunction nestedFunction) {
						return true;
					}
					public boolean visit(FunctionParameter functionParameter) {				
						if (functionParameter.getAttrType() == AttrType.FIELD){
							problemRequestor.acceptProblem(functionParameter,
									IProblemRequestor.FUNCTION_PARAMETERS_DO_NOT_SUPPORT_NULLABLE_AND_FIELD,
									new String[] {
									functionParameter.getName().getCanonicalName(),
									funcName,
									handler.getName().getCanonicalName(),
									IEGLConstants.KEYWORD_FIELD});
						}
						return false;
					}

				};
				nestedFunction.accept(visitor2);
				return false;
			}
			public boolean visit(Handler handler) {
				return true;
			}
			
								
		};
		
		handler.accept(visitor1);
	}

	private void checkInterfaceFunctionsOverriden() {
		List<Function> declaredAndInheritedFunctions = null;
		for (Function interfaceFunc : getInterfaceFunctionList()) {
			boolean foundMatchingHandlerFunc = false;
			
			if (declaredAndInheritedFunctions == null) {
				declaredAndInheritedFunctions = getDeclaredAndInheritedFunctionList();
			}
			
			for (Function handlerFunc : declaredAndInheritedFunctions) {
				if(BindingUtil.functionSignituresAreIdentical(handlerFunc, interfaceFunc)) {
					foundMatchingHandlerFunc = true;
					break;
				}
			}
			
			if(!foundMatchingHandlerFunc) {
				problemRequestor.acceptProblem(
					handler.getName(),
					IProblemRequestor.INTERFACE_FUNCTION_MISSING,
					new String[] {
						handler.getName().getCanonicalName(),
						interfaceFunc.getCaseSensitiveName() + "(" + getTypeNamesList(interfaceFunc.getParameters()) + ")",
						((Interface)interfaceFunc.getContainer()).getCaseSensitiveName()
					});
			}
		}
	}
	
	private List<Function> getDeclaredAndInheritedFunctionList() {
		List<Function> retVal = new ArrayList();
		getInheritedFunctions(handlerBinding, retVal, new HashSet<StructPart>());
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
	
	private List<Function> getInterfaceFunctionList() {
		List<Function> retVal = new ArrayList();
		Set<Interface> seen = new HashSet<Interface>();
		for (Interface iface : handlerBinding.getInterfaces()) {
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

	private static String getTypeNamesList( List<org.eclipse.edt.mof.egl.FunctionParameter> types ) {
		StringBuffer sb = new StringBuffer();
		if( !types.isEmpty() ) {
			sb.append( " " );
		}
		for(Iterator<org.eclipse.edt.mof.egl.FunctionParameter> iter = types.iterator(); iter.hasNext(); ) {
			org.eclipse.edt.mof.egl.FunctionParameter nextParm = iter.next();
			Type nextType = nextParm.getType();
			if (nextType != null){
				sb.append( nextType.getTypeSignature() );
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
				if( iter.hasNext() ) {
					sb.append( ", " );
				}
				else {
					sb.append( " " );
				}
			}
		}
		return sb.toString();
	}
}
