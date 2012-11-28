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

import org.eclipse.edt.compiler.ASTValidator;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.ClassDataDeclarationValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.UseStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;


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
		List<ASTValidator> validators = partBinding.getEnvironment().getCompiler().getValidatorsFor(nestedFunction);
    	if (validators != null && validators.size() > 0) {
    		for (ASTValidator validator : validators) {
    			validator.validate(nestedFunction, (IRPartBinding)partBinding, problemRequestor, compilerOptions);
    		}
    	}
    	
    	if (nestedFunction.isStatic()) {
    		nestedFunction.accept(new StaticReferenceChecker(problemRequestor));
    	}
    	
		return false;
	}
	
	@Override
	public boolean visit(Constructor constructor) {
		List<ASTValidator> validators = partBinding.getEnvironment().getCompiler().getValidatorsFor(constructor);
    	if (validators != null && validators.size() > 0) {
    		for (ASTValidator validator : validators) {
    			validator.validate(constructor, (IRPartBinding)partBinding, problemRequestor, compilerOptions);
    		}
    	}
		return false;
	}
	
	@Override
	public boolean visit(SettingsBlock settingsBlock) {
		return false;
	}
	
	@Override
	public boolean visit(UseStatement useStatement) {
		useStatement.accept(new UseStatementValidator(partBinding, partNode.getName().getCanonicalName(), problemRequestor, compilerOptions));
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
		if (!BindingUtil.isAbstract(binding)) {
			List<Function> declaredAndInheritedFunctions = null;
			for (Function interfaceFunc : getInterfaceFunctionList(binding)) {
				boolean foundMatchingFunc = false;
				
				if (declaredAndInheritedFunctions == null) {
					declaredAndInheritedFunctions = getDeclaredAndInheritedFunctionList(binding);
				}
				
				for (Function func : declaredAndInheritedFunctions) {
					if(BindingUtil.functionSignituresAreIdentical(func, interfaceFunc)) {
						foundMatchingFunc = true;
						break;
					}
				}
				
				if(!foundMatchingFunc) {
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
	}
	
	protected void checkAbstractFunctionsOverriden(StructPart binding) {
		if (!BindingUtil.isAbstract(binding)) {
			List<Function> declaredAndInheritedFunctions = null;
			for (Function abstractFunc : getAbstractFunctionList(binding)) {
				boolean foundMatchingFunc = false;
				
				if (declaredAndInheritedFunctions == null) {
					declaredAndInheritedFunctions = getDeclaredAndInheritedFunctionList(binding);
				}
				
				for (Function func : declaredAndInheritedFunctions) {
					if (BindingUtil.functionSignituresAreIdentical(func, abstractFunc)) {
						foundMatchingFunc = true;
						break;
					}
				}
				
				if (!foundMatchingFunc) {
					problemRequestor.acceptProblem(
						partNode.getName(),
						IProblemRequestor.ABSTRACT_FUNCTION_MISSING,
						new String[] {
							partNode.getName().getCanonicalName(),
							abstractFunc.getCaseSensitiveName() + "(" + getTypeNamesList(abstractFunc.getParameters()) + ")",
							((org.eclipse.edt.mof.egl.Part)abstractFunc.getContainer()).getCaseSensitiveName()
						});
				}
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
			if (!function.isAbstract() && function.getAccessKind() != AccessKind.ACC_PRIVATE) {
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
	
	private List<Function> getAbstractFunctionList(StructPart binding) {
		List<Function> retVal = new ArrayList();
		Set<StructPart> seen = new HashSet<StructPart>();
		for (StructPart superType : binding.getSuperTypes()) {
			if (!(superType instanceof Interface)) {
				getAbstractFunctions(superType, retVal, seen);
			}
		}
		return retVal;
	}
	
	private void getAbstractFunctions(StructPart structPart, List<Function> funcs, Set<StructPart> seenParts) {
		if (seenParts.contains(structPart)) {
			return;
		}
		seenParts.add(structPart);
		
		for (Function function : structPart.getFunctions()) {
			if (function.isAbstract()) {
				funcs.add(function);
			}
		}
		
		for (StructPart superType: structPart.getSuperTypes()) {
			getAbstractFunctions(superType, funcs, seenParts);
		}
	}

	public static String getTypeNamesList(List<org.eclipse.edt.mof.egl.FunctionParameter> types) {
		StringBuffer sb = new StringBuffer();
		if (!types.isEmpty()) {
			sb.append(" ");
		}
		for (Iterator<org.eclipse.edt.mof.egl.FunctionParameter> iter = types.iterator(); iter.hasNext();) {
			org.eclipse.edt.mof.egl.FunctionParameter nextParm = iter.next();
			Type nextType = nextParm.getType();
			if (nextType != null) {
				sb.append(BindingUtil.getTypeName(nextParm, nextType));
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
	
	public static class StaticReferenceChecker extends AbstractASTVisitor {
		
		IProblemRequestor problemRequestor;
		
		public StaticReferenceChecker(IProblemRequestor problemRequestor) {
			this.problemRequestor = problemRequestor;
		}
		
		@Override
		public boolean visit(SimpleName simpleName) {
			checkStatic(simpleName, simpleName);
			return false;
		}
		
		@Override
		public boolean visit(QualifiedName qualifiedName) {
			checkStatic(qualifiedName, qualifiedName);
			return false;
		}
		
		private void checkStatic(Node errorNode, Name name) {
			Object element = name.resolveElement();
			if (element instanceof Member) {
				if (!((Member)element).isStatic() && !(((Member)element).getContainer() instanceof FunctionMember)) {
					if (name.isSimpleName()) {
						if (element instanceof FunctionMember) {
							problemRequestor.acceptProblem(errorNode, IProblemRequestor.INVALID_REFERENCE_TO_NONSTATIC_FUNCTION,
									new String[]{name.getCaseSensitiveIdentifier() + "(" + FunctionContainerValidator.getTypeNamesList(((FunctionMember)element).getParameters()) + ")"});
						}
						else {
							problemRequestor.acceptProblem(errorNode, IProblemRequestor.INVALID_REFERENCE_TO_NONSTATIC_FIELD,
									new String[]{name.getCaseSensitiveIdentifier()});
						}
					}
					else {
						// visit the parent to check for static.
						checkStatic(errorNode, ((QualifiedName)name).getQualifier());
					}
				}
			}
		}
	}
	
	protected void checkImplicitConstructor(Part part) {
		class FoundContructor extends RuntimeException{private static final long serialVersionUID = 1L;}
		try {
			part.accept(new AbstractASTVisitor() {
				@Override
				public boolean visit(Constructor constructor) {
					throw new FoundContructor();
				}
				@Override
				public boolean visit(NestedFunction nestedFunction) {
					return false;
				}
				@Override
				public boolean visit(ClassDataDeclaration classDataDeclaration) {
					return false;
				}
			});
		}
		catch (FoundContructor e) {
			return;
		}
		
		// This type has an implicit default constructor. Make sure its parent type has a public default constructor so its implicit 'super()' is valid.
		Type type = part.getName().resolveType();
		if (type instanceof StructPart) {
			List<StructPart> superTypes = ((StructPart)type).getSuperTypes();
			if (superTypes != null && superTypes.size() > 0 && !TypeValidator.hasPublicDefaultConstructor(superTypes.get(0))) {
				problemRequestor.acceptProblem(part.getName(), IProblemRequestor.MUST_DEFINE_CONSTRUCTOR,
						new String[]{BindingUtil.getShortTypeString(superTypes.get(0), false) + "()"});
			}
		}
	}
}
