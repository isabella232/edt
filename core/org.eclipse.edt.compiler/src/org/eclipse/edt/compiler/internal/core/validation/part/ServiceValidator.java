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
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Service;
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
import org.eclipse.edt.mof.utils.NameUtile;


public class ServiceValidator extends FunctionContainerValidator {
	IRPartBinding irBinding;
	org.eclipse.edt.mof.egl.Service serviceBinding;
	protected Service service = null;
	
	public ServiceValidator(IProblemRequestor problemRequestor, IRPartBinding irBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, irBinding, compilerOptions);
		this.irBinding = irBinding;
		serviceBinding = (org.eclipse.edt.mof.egl.Service)irBinding.getIrPart();
	}
	
	public boolean visit(Service aservice) {
		//TODO validate wsdl properties
		this.service = aservice;
		partNode = aservice;
		EGLNameValidator.validate(service.getName(), EGLNameValidator.PART, problemRequestor, compilerOptions);
		checkInterfaceFunctionsOverriden();
		
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(aservice); TODO

		return true;
	}
	
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
		super.visit(classDataDeclaration);
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
	
	public boolean visit(NestedFunction nestedFunction) {
		super.visit(nestedFunction);
//		ServiceInterfaceValidatorUtil.validateParametersAndReturn(nestedFunction,true,problemRequestor); TODO
		
		if (NameUtile.equals(nestedFunction.getName().getCanonicalName(), IEGLConstants.MNEMONIC_MAIN)){
			problemRequestor.acceptProblem(service.getName(),
					IProblemRequestor.LIBRARY_NO_MAIN_FUNCTION_ALLOWED,
					new String[] {serviceBinding.getCaseSensitiveName()});
		}
		
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(nestedFunction); TODO
		
		return false;
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
					service.getName(),
					IProblemRequestor.INTERFACE_FUNCTION_MISSING,
					new String[] {
						service.getName().getCanonicalName(),
						interfaceFunc.getCaseSensitiveName() + "(" + getTypeNamesList(interfaceFunc.getParameters()) + ")",
						((Interface)interfaceFunc.getContainer()).getCaseSensitiveName()
					});
			}
		}
	}
	
	private List<Function> getDeclaredAndInheritedFunctionList() {
		List<Function> retVal = new ArrayList();
		getInheritedFunctions(serviceBinding, retVal, new HashSet<StructPart>());
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
		for (Interface iface : serviceBinding.getInterfaces()) {
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
