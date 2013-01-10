/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.NullScope;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author winghong
 */
public abstract class FunctionContainerBindingCompletor extends AbstractBinder {

    private org.eclipse.edt.mof.egl.Part functionContainerBinding;

    protected IProblemRequestor problemRequestor;

    protected Set<String> definedDataNames = new HashSet<String>();

    protected Set<String> definedFunctionNames = new HashSet<String>();

    protected PartSubTypeAndAnnotationCollector partSubTypeAndAnnotationCollector;

    private Set<Type> usedTypes = new HashSet<Type>();

    private List<ClassDataDeclaration> dataDeclarations = new ArrayList<ClassDataDeclaration>();

	private FunctionContainerScope functionContainerScope;
	
	private IRPartBinding irBinding;

    public FunctionContainerBindingCompletor(IRPartBinding irBinding, Scope currentScope,
            IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, irBinding.getIrPart(), dependencyRequestor, compilerOptions);
        this.problemRequestor = problemRequestor;
        this.irBinding = irBinding;
        this.functionContainerBinding = irBinding.getIrPart();
    }

    public PartSubTypeAndAnnotationCollector getPartSubTypeAndAnnotationCollector() {
        if (partSubTypeAndAnnotationCollector == null) {
            partSubTypeAndAnnotationCollector = new PartSubTypeAndAnnotationCollector(functionContainerBinding, this, problemRequestor);
        }
        return partSubTypeAndAnnotationCollector;
    }

    void setDefaultSuperType() {
    	
		BindingUtil.setDefaultSupertype((StructPart)functionContainerBinding, getPartSubTypeAndAnnotationCollector().getStereotype(), getDefaultSuperType());
    }
    
    void endVisitFunctionContainer(Part part) {
    	irBinding.setValid(true);
    }
    
    StructPart getDefaultSuperType() {
    	StructPart eany = (StructPart)BindingUtil.getEAny();
    	if (irBinding.getIrPart() instanceof StructPart && !eany.equals(irBinding.getIrPart())) {
    		StructPart anyStruct = (StructPart)BindingUtil.findPart(NameUtile.getAsName(MofConversion.EGLX_lang_package), NameUtile.getAsName("AnyStruct"));
    		return anyStruct;
    	}
    	return eany;
    }

    public boolean visit(ClassDataDeclaration classDataDeclaration) {
        Type type = null;
        try {
            type = bindType(classDataDeclaration.getType());
        } catch (ResolutionException e) {
            problemRequestor
                    .acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
            if(classDataDeclaration.hasSettingsBlock()) {
            	setBindAttemptedForNames(classDataDeclaration.getSettingsBlockOpt());
            }
            for (Name name : classDataDeclaration.getNames()) {
            	name.setBindAttempted(true);
            }

            return false; // Do not create the class field bindings if the type
            // cannot be resolved
        }

        dataDeclarations.add(classDataDeclaration);
        boolean isConstantDeclaration = classDataDeclaration.isConstant();
        
        for (Name name : classDataDeclaration.getNames()) {
            String dataName = name.getIdentifier();
            
            Field field;
            if (isConstantDeclaration) {
            	field = IrFactory.INSTANCE.createConstantField();
            }
            else {
            	field = IrFactory.INSTANCE.createField();
            }

            if (classDataDeclaration.isPrivate() || membersPrivateByDefault()) {
            	field.setAccessKind(AccessKind.ACC_PRIVATE);
            }
                        
            field.setContainer((Container)functionContainerBinding);
            field.setType(type);
            field.setIsNullable(classDataDeclaration.isNullable());
            field.setName(name.getCaseSensitiveIdentifier());
            field.setIsStatic(classDataDeclaration.isStatic() || membersStaticByDefault());
            
            if (definedDataNames.contains(dataName) || definedFunctionNames.contains(dataName)) {
                problemRequestor.acceptProblem(name, IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] { name.getCanonicalName(),
                        functionContainerBinding.getName() });
            } else {
            	((LogicAndDataPart)functionContainerBinding).getFields().add(field);
                definedDataNames.add(dataName);
            }
            name.setMember(field);
            name.setType(type);
        }

        return false;
    }

    
    private void processDataDeclarationsSettingsBlocks() {
    	for (ClassDataDeclaration decl : dataDeclarations) {
            processSettingsBlock(decl, functionContainerBinding, getFunctionContainerScope(), problemRequestor);
    	}
    }
        
    public boolean visit(NestedFunction nestedFunction) {
        String name = nestedFunction.getName().getIdentifier();
        
        Function function = IrFactory.INSTANCE.createFunction();
        function.setName(nestedFunction.getName().getCaseSensitiveIdentifier());
        function.setContainer((Container)functionContainerBinding);
        
        FunctionBindingCompletor functionBindingCompletor = new FunctionBindingCompletor(functionContainerBinding, getFunctionContainerScope(),
                function, dependencyRequestor, problemRequestor, compilerOptions);
        nestedFunction.accept(functionBindingCompletor);
        
        nestedFunction.getName().setMember(function);
        function.setIsStatic(function.isStatic() || membersStaticByDefault());
        if (membersPrivateByDefault()) {
        	function.setAccessKind(AccessKind.ACC_PRIVATE);
        }
                
        if (definedDataNames.contains(name)) {
        	problemRequestor.acceptProblem(nestedFunction.getName(), IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] { nestedFunction.getName().getCanonicalName(), functionContainerBinding.getCaseSensitiveName() });
        } else if(definedFunctionNames.contains(name)) {
        	
        	for (Function func : ((StructPart)functionContainerBinding).getFunctions()) {
        		if(BindingUtil.functionSignituresAreIdentical(func, function, false, false)) {
	        			problemRequestor.acceptProblem(
	        				nestedFunction.getName(),
	        				IProblemRequestor.DUPLICATE_NAME_IN_FILE,
	        				new String[] {
	        					IEGLConstants.KEYWORD_FUNCTION,
	        					nestedFunction.getName().getCanonicalName()
	        				});
	        			return false;
        		}
        	}
        } else {
            definedFunctionNames.add(name);
        }        
        
        ((StructPart)functionContainerBinding).getFunctions().add(function);
        
        return false;
    }
    
    protected void processSettingsBlocks() {
        processDataDeclarationsSettingsBlocks();
        
        if (getPartSubTypeAndAnnotationCollector().getSettingsBlocks().size() > 0) {
        	FunctionContainerScope funcContScope = new FunctionContainerScope(NullScope.INSTANCE, functionContainerBinding);
            AnnotationLeftHandScope scope = new AnnotationLeftHandScope(funcContScope, functionContainerBinding, functionContainerBinding, functionContainerBinding);
            //If the part type is specified on the part (as opposed to with an
            // annotation),
            //then create an annotation scope to handle resolution of subtype
            // dependent properties
            if (!getPartSubTypeAndAnnotationCollector().isFoundSubTypeInSettingsBlock()) {
                if (getPartSubTypeAndAnnotationCollector().getStereotype() == null) {
                	StereotypeType stereoTypeType = getDefaultStereotypeType();
                    if (stereoTypeType != null) {                   	
                    	Stereotype stereotype = (Stereotype) stereoTypeType.newInstance();
                        functionContainerBinding.addAnnotation(stereotype);
                        scope = new AnnotationLeftHandScope(scope, stereotype, stereoTypeType, stereotype);
                    }
                } else {
                    scope = new AnnotationLeftHandScope(scope, getPartSubTypeAndAnnotationCollector().getStereotype(), (StereotypeType)getPartSubTypeAndAnnotationCollector().getStereotype().getEClass(),
                            getPartSubTypeAndAnnotationCollector().getStereotype());
                }
            }
            Scope fcScope = getFunctionContainerScope();
            SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(fcScope, functionContainerBinding, scope,
                    dependencyRequestor, problemRequestor, compilerOptions);
            for(SettingsBlock block : getPartSubTypeAndAnnotationCollector().getSettingsBlocks()) {
                block.accept(blockCompletor);
            }
        } else {
            if (getPartSubTypeAndAnnotationCollector().getStereotype() == null) {
            	StereotypeType stereoTypeType = getDefaultStereotypeType();
                if (stereoTypeType != null) {
                	Stereotype stereoType = (Stereotype) stereoTypeType.newInstance();
                    functionContainerBinding.addAnnotation(stereoType);
                }
            }
        }
    }

    protected abstract StereotypeType getDefaultStereotypeType();

    public boolean visit(UseStatement useStatement) {
        for (Name nextName : useStatement.getNames()) {
 
            Type typeBinding = null;
            try {
                typeBinding = bindTypeName(nextName); 
            } catch (ResolutionException e) {
                problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e
                        .getInserts());
                if (useStatement.hasSettingsBlock()) {
                	setBindAttemptedForNames(useStatement.getSettingsBlock());
                }
                continue;
            }

            if (usedTypes.contains(typeBinding)) {
                problemRequestor.acceptProblem(nextName, IProblemRequestor.DUPLICATE_USE_NAME, new String[] {
                        nextName.getCanonicalString(), functionContainerBinding.getCaseSensitiveName() });

            } else {
                usedTypes.add(typeBinding);
            }
            
            if(typeBinding instanceof org.eclipse.edt.mof.egl.Part) {
            	getFunctionContainerScope().addUsedPart((org.eclipse.edt.mof.egl.Part)typeBinding);
            }
        }
        return false;
    }
    
    protected FunctionContainerScope getFunctionContainerScope() {
    	if(functionContainerScope == null) {
    		functionContainerScope = new FunctionContainerScope(currentScope, functionContainerBinding);
    	}
    	return functionContainerScope;
    }
    
    protected boolean membersStaticByDefault() {
    	return false;
    }

    protected boolean membersPrivateByDefault() {
    	return false;
    }
    
    public boolean visit(Constructor constructor) {
    	final org.eclipse.edt.mof.egl.Constructor constructorBinding = IrFactory.INSTANCE.createConstructor();
    	constructorBinding.setName(NameUtile.getAsCaseSensitiveName(IEGLConstants.KEYWORD_CONSTRUCTOR));
    	constructorBinding.setType(functionContainerBinding);
    	constructorBinding.setContainer((Container)functionContainerBinding);
    	final Set<String> definedParameters = new HashSet<String>();
    	
    	constructor.setBinding(constructorBinding);
    	
    	if (constructor.isPrivate()) {
    		constructorBinding.setAccessKind(AccessKind.ACC_PRIVATE);
    	}
    	
    	constructor.accept(new AbstractASTVisitor() {
    		public boolean visit(FunctionParameter functionParameter) {
    			String parmName = functionParameter.getName().getIdentifier();
    	        org.eclipse.edt.compiler.core.ast.Type parmType = functionParameter.getType();        
    	        org.eclipse.edt.mof.egl.Type typeBinding = null;
    	        try {
    	            typeBinding = bindType(parmType);
    	        } catch (ResolutionException e) {
    	            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    	            return false;
    	        }
    	        
    	        org.eclipse.edt.mof.egl.FunctionParameter funcParmBinding = IrFactory.INSTANCE.createFunctionParameter();
    	        funcParmBinding.setName(functionParameter.getName().getCaseSensitiveIdentifier());
    	        functionParameter.getName().setMember(funcParmBinding);
    	        funcParmBinding.setType(typeBinding);
    	        funcParmBinding.setIsNullable(functionParameter.isNullable());   	        
    	        
    	        funcParmBinding.setIsConst(functionParameter.isParmConst());
    	        
    	        FunctionParameter.UseType useType = functionParameter.getUseType();
    	        if (useType == FunctionParameter.UseType.IN) {
    	            funcParmBinding.setParameterKind(ParameterKind.PARM_IN);
    	        } else if (useType == FunctionParameter.UseType.OUT) {
    	            funcParmBinding.setParameterKind(ParameterKind.PARM_OUT);
    	        } else if (useType == null && typeBinding != null && TypeUtils.isReferenceType(typeBinding)) {
    	        	funcParmBinding.setParameterKind(ParameterKind.PARM_IN);
    	        }
    	        else {
    	        	funcParmBinding.setParameterKind(ParameterKind.PARM_INOUT);
    	        }
 
    	        if (definedParameters.contains(parmName)) {
    	            problemRequestor.acceptProblem(functionParameter, IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] { functionParameter.getName().getCanonicalName(), IEGLConstants.KEYWORD_CONSTRUCTOR });
    	        } else {
    	            constructorBinding.addParameter(funcParmBinding);
    	            definedParameters.add(parmName);
    	        }
    	        
    	        return false;
    		}
    	});
    	
    	((StructPart)functionContainerBinding).getConstructors().add(constructorBinding);
    	
    	if (constructor.hasSettingsBlock()) {
            FunctionScope functionScope = new FunctionScope(NullScope.INSTANCE, constructorBinding);
            AnnotationLeftHandScope scope = new AnnotationLeftHandScope(functionScope, constructorBinding, null, constructorBinding);
            functionScope = new FunctionScope(currentScope, constructorBinding);
            SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(functionScope, functionContainerBinding, scope,
                    dependencyRequestor, problemRequestor, compilerOptions);
            constructor.getSettingsBlock().accept(blockCompletor);
    		
    	}
    	
    	return false;
    }

    
}
