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
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.ServiceBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class ServiceValidator extends FunctionContainerValidator {
	protected ServiceBinding serviceBinding = null;
	protected Service service = null;
	
	public ServiceValidator(IProblemRequestor problemRequestor, ServiceBinding partBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, partBinding, compilerOptions);
		serviceBinding = partBinding;
	}
	
	public boolean visit(Service aservice) {
		//TODO validate wsdl properties
		this.service = aservice;
		partNode = aservice;
		EGLNameValidator.validate(service.getName(), EGLNameValidator.PART, problemRequestor, compilerOptions);
		checkInterfaceFunctionsOverriden();
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(aservice);

		return true;
	}
	
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
		super.visit(classDataDeclaration);
		return false;
	}
	
	public boolean visit(NestedFunction nestedFunction) {
		super.visit(nestedFunction);
		ServiceInterfaceValidatorUtil.validateParametersAndReturn(nestedFunction,true,problemRequestor);
		
		if (InternUtil.intern(nestedFunction.getName().getCanonicalName()) == InternUtil.intern(IEGLConstants.MNEMONIC_MAIN)){
			problemRequestor.acceptProblem(service.getName(),
					IProblemRequestor.LIBRARY_NO_MAIN_FUNCTION_ALLOWED,
					new String[] {serviceBinding.getCaseSensitiveName()});
		}
		
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(nestedFunction);
		
		return false;
	}
	
	private void checkInterfaceFunctionsOverriden() {
		for(Iterator iter = getInterfaceFunctionList().iterator(); iter.hasNext();) {
			IFunctionBinding interfaceFunc = (IFunctionBinding) ((NestedFunctionBinding) iter.next()).getType();
			boolean foundMatchingServiceFunc = false;
			for(Iterator iter2 = serviceBinding.getDeclaredAndInheritedFunctions().iterator(); !foundMatchingServiceFunc && iter2.hasNext();) {
				IFunctionBinding serviceFunc = (IFunctionBinding) ((NestedFunctionBinding) iter2.next()).getType();
				if(TypeCompatibilityUtil.functionSignituresAreIdentical(serviceFunc, interfaceFunc, compilerOptions)) {
					foundMatchingServiceFunc = true;
				}				   
			}
			if(!foundMatchingServiceFunc) {
				problemRequestor.acceptProblem(
					service.getName(),
					IProblemRequestor.INTERFACE_FUNCTION_MISSING,
					new String[] {
						service.getName().getCanonicalName(),
						interfaceFunc.getCaseSensitiveName() + "(" + getTypeNamesList(interfaceFunc.getParameters()) + ")",
						interfaceFunc.getDeclarer().getCaseSensitiveName()
					});
			}
		}
	}
	
	private List getInterfaceFunctionList() {
		List retVal = new ArrayList();
		List interfaceList = serviceBinding.getImplementedInterfaces();
		for (int i = 0; i < interfaceList.size(); i++){
			InterfaceBinding interfaceBinding = (InterfaceBinding)interfaceList.get(i);
			for(Iterator iter = interfaceBinding.getDeclaredAndInheritedFunctions().iterator(); iter.hasNext();) {
				NestedFunctionBinding fBinding = (NestedFunctionBinding) iter.next();
				if(!fBinding.isPrivate()) {
					retVal.add(fBinding);
				}
			}
		}
		return retVal;
	}

	public boolean visit(SettingsBlock settingsBlock) {
		super.visit(settingsBlock);
		return false;
	}
	
	public boolean visit(UseStatement useStatement) {
		super.visit(useStatement);
		return false;
	}
	
	private static String getTypeNamesList( List types ) {
		StringBuffer sb = new StringBuffer();
		if( !types.isEmpty() ) {
			sb.append( " " );
		}
		for( Iterator iter = types.iterator(); iter.hasNext(); ) {
			FunctionParameterBinding nextParm = (FunctionParameterBinding) iter.next();
			ITypeBinding nextType = nextParm.getType();
			if (StatementValidator.isValidBinding(nextType)){
				sb.append( nextType.getCaseSensitiveName() );
				if( nextParm.isInput() ) {
					sb.append( " " + IEGLConstants.KEYWORD_IN );
				}
				else if( nextParm.isOutput() ) {
					sb.append( " " + IEGLConstants.KEYWORD_OUT );
				}
				else {
					sb.append( " " + IEGLConstants.KEYWORD_INOUT );
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
