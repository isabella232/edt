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
package org.eclipse.edt.compiler.internal.core.lookup;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.AmbiguousDataBinding;
import org.eclipse.edt.compiler.binding.AmbiguousSystemLibraryFieldDataBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.DataTableBinding;
import org.eclipse.edt.compiler.binding.DynamicDataBinding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FixedStructureBinding;
import org.eclipse.edt.compiler.binding.FormGroupBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.PartFoundButNotAnnotationRecordAnnotationBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.ProgramBinding;
import org.eclipse.edt.compiler.binding.SettingsBlockAnnotationBindingsCompletor;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
import org.eclipse.edt.compiler.binding.TopLevelFunctionDataBinding;
import org.eclipse.edt.compiler.binding.VariableBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.CharLiteral;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DBCharLiteral;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.HexLiteral;
import org.eclipse.edt.compiler.core.ast.IASTVisitor;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.MBCharLiteral;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NullableType;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.PrimitiveType;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.utils.ExpressionParser;
import org.eclipse.edt.mof.egl.utils.InternUtil;

import com.ibm.icu.util.StringTokenizer;

/**
 * @author winghong
 */
public abstract class AbstractBinder extends AbstractASTVisitor {
    
    protected Scope currentScope;
    protected IDependencyRequestor dependencyRequestor;
    protected IPartBinding currentBinding;
    protected ICompilerOptions compilerOptions;
    protected boolean bindingCallTarget;
        
    public AbstractBinder(Scope currentScope, IPartBinding currentBinding, IDependencyRequestor dependencyRequestor, ICompilerOptions compilerOptions) {
        this.currentScope = currentScope;
        this.currentBinding = currentBinding;
        this.dependencyRequestor = dependencyRequestor;
        this.compilerOptions = compilerOptions;
    }

    public ITypeBinding bindType(Type type) throws ResolutionException {
        switch(type.getKind()) {
            case Type.PRIMITIVETYPE:
                PrimitiveType primitiveType = (PrimitiveType) type;
                return primitiveType.resolveTypeBinding();
            
            case Type.ARRAYTYPE:
                ArrayType arrayType = (ArrayType) type;
                ITypeBinding elementTypeBinding = bindType(arrayType.getElementType());
                ArrayTypeBinding arrayTypeBinding = ArrayTypeBinding.getInstance(elementTypeBinding);
                arrayType.setTypeBinding(arrayTypeBinding);
                return arrayTypeBinding;

            case Type.NAMETYPE:
                NameType nameType = (NameType) type;
                return bindTypeName(nameType.getName());   // No need to set on type as it is delegated
                
            case Type.NULLABLETYPE:
            	NullableType nullableType = (NullableType) type;
            	ITypeBinding baseType = bindType(nullableType.getBaseType());
            	return baseType.getNullableInstance();            	

            default: throw new RuntimeException("Shouldn't come into here");            
        }
    }
    
    public ITypeBinding bindTypeName(Name name) throws ResolutionException {
    	return bindTypeName(name, false);
    }

    public ITypeBinding bindTypeName(Name name, boolean mayHaveFormgroupQualifier) throws ResolutionException {
        ITypeBinding result = null;
        if(name.isSimpleName()) {
            result = currentScope.findType(name.getIdentifier());
            // SimpleName dependency recorded by scope - System.out.println("bindTypeName simple name: " + name.getIdentifier());
        }
        else {
        	QualifiedName qualifiedName = (QualifiedName) name;
        	if(mayHaveFormgroupQualifier) {
        		try {
        			ITypeBinding qualifierType = bindTypeName(qualifiedName.getQualifier());
        			if(ITypeBinding.FORMGROUP_BINDING == qualifierType.getKind()) {
        				result = ((FormGroupBinding) qualifierType).findForm(qualifiedName.getIdentifier());
        				if(IBinding.NOT_FOUND_BINDING == result) {
        					name.setBinding(IBinding.NOT_FOUND_BINDING);
        					throw new ResolutionException(name, IProblemRequestor.TYPE_CANNOT_BE_RESOLVED, new String[] {qualifiedName.getIdentifier()});
        				}
        			}
        		}
        		catch(ResolutionException e) {
        			qualifiedName.getQualifier().setBinding(null);
        		}
        	}
        	if(result == null) {
	            IPackageBinding packageBinding = bindPackageName(qualifiedName.getQualifier());
	        
	            result = packageBinding.resolveType(qualifiedName.getIdentifier());
	            
	            if(result != IBinding.NOT_FOUND_BINDING){
	                if(((IPartBinding)result).isPrivate() && result.getPackageName() != currentBinding.getPackageName()){
	                    result = IBinding.NOT_FOUND_BINDING;
	                }
	            }
        	}
        }

        if(result == IBinding.NOT_FOUND_BINDING) {
            name.setBinding(IBinding.NOT_FOUND_BINDING);
            dependencyRequestor.recordName(name);  //    System.out.println("bindTypeName failure: " + name.getCanonicalName());
            int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.TYPE_CANNOT_BE_RESOLVED, new String[] {name.getCanonicalName()});
        }
        else if(result == ITypeBinding.AMBIGUOUS_TYPE){
            name.setBinding(ITypeBinding.AMBIGUOUS_TYPE);
            dependencyRequestor.recordName(name);
            int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.TYPE_IS_AMBIGUOUS, new String[] {name.getCanonicalName()});
        }
        
        dependencyRequestor.recordTypeBinding(result);
        name.setBinding(result);
      //  System.out.println("Qualified name dependency: " + result.getPackageName());
        return result;
    }
    
    public IDataBinding bindExpressionName(Name name) throws ResolutionException {
    	return bindExpressionName(name, false);
    }
    
    public IDataBinding bindExpressionName(Name name, boolean isFunctionInvocation) throws ResolutionException {
        IDataBinding result = IBinding.NOT_FOUND_BINDING;
        if(name.isSimpleName()) {
            result = currentScope.findData(name.getIdentifier());
            
            if(IBinding.NOT_FOUND_BINDING == result) {
            	ITypeBinding type = currentScope.findType(name.getIdentifier());
            	if(IBinding.NOT_FOUND_BINDING != type) {
            		if(ITypeBinding.DATATABLE_BINDING == type.getKind()) {
            			result = ((DataTableBinding) type).getStaticDataTableDataBinding();
            		}
            		else if(ITypeBinding.LIBRARY_BINDING == type.getKind()) {
            			result = ((LibraryBinding) type).getStaticLibraryDataBinding();
            		}
            		else if(ITypeBinding.PROGRAM_BINDING == type.getKind()) {
            			result = ((ProgramBinding) type).getStaticProgramDataBinding();
            		}
            		else if(ITypeBinding.EXTERNALTYPE_BINDING == type.getKind()) {
            			result = ((ExternalTypeBinding) type).getStaticExternalTypeDataBinding();
            		}
            	}
            }
            else {
            	/*
            	 * This is a simple name resolution, so if we found a static library or external
            	 * type access, throw it out.
            	 */
            	switch(result.getKind()) {
	            	case IDataBinding.EXTERNALTYPE_BINDING:
	            	case IDataBinding.LIBRARY_BINDING:
	            		result = IBinding.NOT_FOUND_BINDING;
            	}
            }
        }
        else {
            QualifiedName qualifiedName = (QualifiedName) name;
            Name qualifier = qualifiedName.getQualifier();
            String identifier = qualifiedName.getIdentifier();
            
            IBinding binding = bindUnknownName(qualifier);
            	
            if(binding.isDataBinding()) {            	
                IDataBinding dataBinding = (IDataBinding) binding;
                if(dataBinding.isDataBindingWithImplicitQualifier()) {
                	qualifier.setAttributeOnName(Name.IMPLICIT_QUALIFIER_DATA_BINDING, dataBinding.getImplicitQualifier());
                	dataBinding = dataBinding.getWrappedDataBinding();
                }
                
                ITypeBinding typeBinding = dataBinding.getType();
                
                if(dataBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
                	result = ((StructureItemBinding) dataBinding).findData(identifier);
                }
                else {
                	if(!Binding.isValidBinding(typeBinding)) {
                		result = IBinding.NOT_FOUND_BINDING;                		
                	}
                	else {
                		switch(dataBinding.getKind()) {
                			case IDataBinding.EXTERNALTYPE_BINDING:
                				result = findStaticData(typeBinding, identifier);
                				break;
                			default:
                				result = findData(typeBinding, identifier);
                		}
                    	
                    	if(IBinding.NOT_FOUND_BINDING == result && typeBinding.isDynamicallyAccessible()) {
                    		result = new DynamicDataBinding(qualifiedName.getCaseSensitiveIdentifier(), dataBinding.getDeclaringPart());
                    	}
                    }
                }
            }
            else if(binding.isTypeBinding()) {
                ITypeBinding typeBinding = (ITypeBinding) binding;
                result = findStaticData(typeBinding, identifier);
                
                if(result == IBinding.NOT_FOUND_BINDING) {
                	try {
                		IBinding packageBinding = bindPackageName(qualifier);
                		binding = packageBinding;
                	}
                	catch(ResolutionException e) {
                	}
                }
            }
            if(binding.isPackageBinding()) {
                IPackageBinding packageBinding = (IPackageBinding) binding;
                if (identifier != null && identifier.length() > 0){
                	dependencyRequestor.recordSimpleName(identifier);
                }
                
                ITypeBinding resolvedType = packageBinding.resolveType(identifier);
                if(resolvedType != IBinding.NOT_FOUND_BINDING) {
                	if(resolvedType.getPackageName() != currentBinding.getPackageName() && ((IPartBinding)resolvedType).isPrivate()){
                		dependencyRequestor.recordName(new QualifiedName((Name)qualifier, name.getIdentifier(), qualifier.getOffset(), name.getOffset() + name.getLength()));
                        result = IBinding.NOT_FOUND_BINDING;
                	}
                	else {
                		switch(resolvedType.getKind()) {
	                		case ITypeBinding.FUNCTION_BINDING :
	                			TopLevelFunctionBinding functionBinding = (TopLevelFunctionBinding)resolvedType;
		                        result = functionBinding.getStaticTopLevelFunctionDataBinding();
		                        dependencyRequestor.recordTopLevelFunctionBinding(functionBinding);
		                        break;
	                		case ITypeBinding.DATATABLE_BINDING:
	                			result = ((DataTableBinding) resolvedType).getStaticDataTableDataBinding();
	                			break;
	                		case ITypeBinding.LIBRARY_BINDING:
	                			result = ((LibraryBinding) resolvedType).getStaticLibraryDataBinding();
	                			break;
	                		case ITypeBinding.PROGRAM_BINDING:
	                			result = ((ProgramBinding) resolvedType).getStaticProgramDataBinding();
	                			break;
	                		case ITypeBinding.EXTERNALTYPE_BINDING:
	                			result = ((ExternalTypeBinding) resolvedType).getStaticExternalTypeDataBinding();
	                			break;
	                		default:
	                			result = IBinding.NOT_FOUND_BINDING;
	                			dependencyRequestor.recordName(new QualifiedName((Name)qualifier, name.getIdentifier(), qualifier.getOffset(), name.getOffset() + name.getLength()));
	                	}
                    }
                }else{
                	dependencyRequestor.recordName(new QualifiedName((Name)qualifier, name.getIdentifier(), qualifier.getOffset(), name.getOffset() + name.getLength()));	                	
               	 	result = IBinding.NOT_FOUND_BINDING;
                }
        	}
        }
        
        if(result != IBinding.NOT_FOUND_BINDING && result.isDataBindingWithImplicitQualifier()) {
        	name.setAttributeOnName(Name.IMPLICIT_QUALIFIER_DATA_BINDING, result.getImplicitQualifier());
        	result = result.getWrappedDataBinding();
        }
        
        if(result != IBinding.NOT_FOUND_BINDING && result.getKind() == IDataBinding.OVERLOADED_FUNCTION_SET_BINDING) {
        	if(!isFunctionInvocation) {
        		result = AmbiguousDataBinding.getInstance();
        	}
        }
        
        if(result != IBinding.NOT_FOUND_BINDING && result.getKind() == IDataBinding.TOP_LEVEL_FUNCTION_BINDING) {
        	if (!currentScope.isReturningTopLevelFunctions()) {
        		result = IBinding.NOT_FOUND_BINDING;
        	}
        	else {
        		result = ((TopLevelFunctionDataBinding) result).getFunctionWithItemsNullableSignature(currentScope.I4GLItemsNullableIsEnabled());
        	}
        }
        
        if(result == IBinding.NOT_FOUND_BINDING){
        	name.setBinding(IBinding.NOT_FOUND_BINDING);
        	int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.VARIABLE_NOT_FOUND, new String[] {name.getCanonicalName()});
        }
        else if(result.getKind() == IDataBinding.AMBIGUOUS_BINDING) {
        	 name.setBinding(IBinding.NOT_FOUND_BINDING);
        	 int[] errorOffsets = getLastIdentifierOffsets(name);
        	 throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.VARIABLE_ACCESS_AMBIGUOUS, new String[] {name.getCanonicalName()});
        }
        else if (result == IAnnotationBinding.NOT_APPLICABLE_ANNOTATION_BINDING) {
        	name.setBinding(IBinding.NOT_FOUND_BINDING);
        	int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.ANNOTATION_NOT_APPLICABLE, new String[] {name.getCanonicalName()});
        }
        else if (result instanceof PartFoundButNotAnnotationRecordAnnotationBinding) {
        	name.setBinding(IBinding.NOT_FOUND_BINDING);
        	int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.NOT_AN_ANNOTATION, new String[] {result.getType().getPackageQualifiedName()});
        }
        else if(IDataBinding.AMBIGUOUSSYSTEMLIBRARYFIELD_BINDING == result.getKind()) {
        	name.setBinding(IBinding.NOT_FOUND_BINDING);
        	int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(
        		errorOffsets[0], errorOffsets[1],
        		IProblemRequestor.VARIBLE_NEEDS_SYSTEM_LIBRARY_QUALIFIER,
        		new String[] {
        			name.getCanonicalName(),
        			toCommaList((String[]) ((AmbiguousSystemLibraryFieldDataBinding) result).getAllowedQualifiers().toArray(new String[0]))});
        }
        
        name.setBinding(result);
        name.setTypeBinding(result.getType());
        if(result.getType() != null) {
        	dependencyRequestor.recordTypeBinding(result.getType());
        }
        
        return result;
    }
    
    private int[] getLastIdentifierOffsets(Name name) {
		if(name.isQualifiedName()) {
			String id = name.getIdentifier();
			int endOffset = name.getOffset()+name.getLength();
			return new int[] {endOffset-id.length(), endOffset};
		}
		return new int[] {name.getOffset(), name.getOffset() + name.getLength()};
	}

	private IDataBinding findStaticData(ITypeBinding typeBinding, String identifier) {
    	IDataBinding result;
    	if (typeBinding.getKind() == ITypeBinding.LIBRARY_BINDING || typeBinding.getKind() == ITypeBinding.DATATABLE_BINDING || typeBinding.getKind() == ITypeBinding.ENUMERATION_BINDING) {
        	if(typeBinding == currentBinding) {
        		//find public and private data
        		result = typeBinding.findData(identifier);
        	}
        	else {
        		//find public data only
        		result = typeBinding.findPublicData(identifier);
        	}
        }
    	else if (bindingCallTarget && typeBinding.getKind() == ITypeBinding.INTERFACE_BINDING || typeBinding.getKind() == ITypeBinding.SERVICE_BINDING) {
    		//Call target can reference non-static functions in a static way
    		result = typeBinding.findData(identifier);;
    	}
        else if(typeBinding.getKind() == ITypeBinding.INTERFACE_BINDING ||
        		typeBinding.getKind() == ITypeBinding.EXTERNALTYPE_BINDING) {
        	result = typeBinding.findData(identifier);
        	if(result != IBinding.NOT_FOUND_BINDING) {
        		if(IDataBinding.NESTED_FUNCTION_BINDING == result.getKind() && !((IFunctionBinding) result.getType()).isStatic()) {
        			result = IBinding.NOT_FOUND_BINDING;
        		}
        		else if(IDataBinding.CLASS_FIELD_BINDING == result.getKind() && !((ClassFieldBinding) result).isStatic()) {
        			result = IBinding.NOT_FOUND_BINDING;
        		}
        	}
        }
        else {
            result = IBinding.NOT_FOUND_BINDING;
        }
    	return result;
	}

//    public IFunctionBinding bindFunctionTarget(Expression target) throws ResolutionException {
//    	IFunctionBinding result;
//        if(qualifier == null) {
//            result = currentScope.findFunction(name.getIdentifier());
//            
//            if(result != IBinding.NOT_FOUND_BINDING && result.isFunctionBindingWithImplicitQualifier()) {
//            	FunctionBindingWithImplicitQualifier fBindingWithQualifier = (FunctionBindingWithImplicitQualifier) result;
//            	name.setAttribute(Name.IMPLICIT_QUALIFIER_DATA_BINDING, fBindingWithQualifier.getImplicitQualifier());
//            	result = fBindingWithQualifier.getWrappedFunctionBinding();
//            }
//        }
//        else {
//        	String identifier = name.getIdentifier();
//        	if(qualifier.isName()) {        		
//	            IBinding binding = bindUnknownName((Name) qualifier);
//	
//	            if(binding.isDataBinding()) {
//	                IDataBinding dataBinding = (IDataBinding) binding;
//	                ITypeBinding typeBinding = dataBinding.getType();
//	                
//	                result = typeBinding.findPublicFunction(identifier);
//	                qualifier.setTypeBinding(typeBinding);
//	            }
//	            else if(binding.isTypeBinding()) {
//	                ITypeBinding typeBinding = (ITypeBinding) binding;
//	                
//	                if(typeBinding.getKind() == ITypeBinding.LIBRARY_BINDING) {
//	                	if(typeBinding == currentBinding) {
//	                		result = typeBinding.findFunction(identifier);
//	                	}
//	                	else {
//	                		result = typeBinding.findPublicFunction(identifier);
//	                	}
//	                }
//	                else if(typeBinding.getKind() == ITypeBinding.INTERFACE_BINDING) {
//	                	result = typeBinding.findPublicFunction(identifier);
//	                	if(result != IBinding.NOT_FOUND_BINDING && !result.isStatic()) {
//	                		//TODO: This will report a 'cannot be resolved' error message.
//	                		//      Should a more specific,
//	                		//      'cannot access non-static function from static context'
//	                		//      message be reported instead?
//	                		result = IBinding.NOT_FOUND_BINDING;
//	                	}
//	                }
//	                else {
//	                	// Report error
//	                	result = IBinding.NOT_FOUND_BINDING;
//	                }
//	            }
//	            else {
//	                // Must be package binding
//	                IPackageBinding packageBinding = (IPackageBinding) binding;
//	                
//	                ITypeBinding resolvedType = packageBinding.resolveType(identifier);
//	                if(resolvedType != IBinding.NOT_FOUND_BINDING && resolvedType.getKind() == ITypeBinding.FUNCTION_BINDING){
//	                    if(resolvedType.getPackageName() == currentBinding.getPackageName() || !((IPartBinding)resolvedType).isPrivate()){
//	                        IFunctionBinding functionBinding = (IFunctionBinding)resolvedType;
//	                        result = functionBinding;
//	                        dependencyRequestor.recordTopLevelFunctionBinding(functionBinding);
//	                    }else{
//	                        result = IBinding.NOT_FOUND_BINDING;
//	                    }
//	                }else{
//	               	 	result = IBinding.NOT_FOUND_BINDING;
//	                }
//	            }
//        	}
//        	else {
//        		ITypeBinding typeBinding = qualifier.resolveTypeBinding();
//        		if(typeBinding == null) {
//        			result = IBinding.NOT_FOUND_BINDING;
//        		}
//        		else if(typeBinding.getKind() == ITypeBinding.LIBRARY_BINDING) {
//                	result = typeBinding.findFunction(identifier);
//                }
//                else if(typeBinding.getKind() == ITypeBinding.INTERFACE_BINDING) {
//                	result = typeBinding.findFunction(identifier);
//                	if(result != IBinding.NOT_FOUND_BINDING && !result.isStatic()) {
//                		//TODO: This will report a 'cannot be resolved' error message.
//                		//      Should a more specific,
//                		//      'cannot access non-static function from static context'
//                		//      message be reported instead?
//                		result = IBinding.NOT_FOUND_BINDING;
//                	}
//                }
//                else if(typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
//                	result = typeBinding.findFunction(identifier);
//                }
//                else {
//                	// Report error
//                	result = IBinding.NOT_FOUND_BINDING;
//                }
//        	}
//        }
//        
//        if(result == IBinding.NOT_FOUND_BINDING){
//            if(qualifier != null && qualifier.isName()){
//                dependencyRequestor.recordName(new QualifiedName((Name)qualifier, name.getIdentifier(), qualifier.getOffset(), name.getOffset() + name.getLength()));
//            }else{
//                dependencyRequestor.recordName(name);
//            }
//    		name.setBinding(IBinding.NOT_FOUND_BINDING);
//    		throw new ResolutionException(name, IProblemRequestor.FUNCTION_REFERENCE_CANNOT_BE_RESOLVED, new String[] {name.getCanonicalName()});
//    	}
//    	else if(result == AmbiguousFunctionBinding.getInstance()) {
//    	    if(qualifier != null && qualifier.isName()){
//                dependencyRequestor.recordName(new QualifiedName((Name)qualifier, name.getIdentifier(), qualifier.getOffset(), name.getOffset() + name.getLength()));
//            }else{
//                dependencyRequestor.recordName(name);
//            }name.setBinding(IBinding.NOT_FOUND_BINDING);
//        	throw new ResolutionException(name, IProblemRequestor.FUNCTION_REFERENCE_AMBIGUOUS, new String[] {name.getCanonicalName()});
//    	}
//    	 
//    	name.setBinding(result);
//        return result;
//    }
    
	public IPackageBinding bindPackageName(Name name) throws ResolutionException {
		IPackageBinding result;
        if(name.isSimpleName()) {
            result = currentScope.findPackage(name.getIdentifier());
            // Simple name dependency is recorded by scope - System.out.println("bindPackageName simpleName: " + name.getIdentifier());
        }
        else {
        	QualifiedName qualifiedName = (QualifiedName) name;
        	IPackageBinding packageBinding = bindPackageName(qualifiedName.getQualifier());
           
            result = packageBinding.resolvePackage(qualifiedName.getIdentifier());
        }
        
        if(result == IBinding.NOT_FOUND_BINDING){
        	dependencyRequestor.recordName(name);
        	//TODO: create and use message that says name can't be resolved to package
        	throw new ResolutionException(name, IProblemRequestor.VARIABLE_NOT_FOUND, new String[] {name.getCanonicalName()});
        }
        
        name.setBinding(result);
        dependencyRequestor.recordPackageBinding(result);
        
        return result;
    } 
        
    public IBinding bindUnknownName(Name name) throws ResolutionException {
        String identifier = name.getIdentifier();
        IBinding result = IBinding.NOT_FOUND_BINDING;
        
        if(name.isSimpleName()) {
            // Find variable first
            result = currentScope.findData(identifier);  
            
            if(result != IBinding.NOT_FOUND_BINDING) {
            	IDataBinding dataBinding = (IDataBinding) result;
             		
        		if(dataBinding.isDataBindingWithImplicitQualifier()) {
        			name.setAttributeOnName(Name.IMPLICIT_QUALIFIER_DATA_BINDING, dataBinding.getImplicitQualifier());
        			result = dataBinding.getWrappedDataBinding();
        		}
        		
        		ITypeBinding type = ((IDataBinding)result).getType();
        	    name.setTypeBinding(type);
        	    if(type != null) {
        	    	// We have an ambiguous data item binding, so we can't record a dependency on the type
        	    	dependencyRequestor.recordTypeBinding(type);
        	    }
        	}
            // Then find type
            if(result == IBinding.NOT_FOUND_BINDING) {            	
                result = currentScope.findType(identifier);
               // Simple name dependency is recorded by scope - System.out.println("bindUnknownName simpleName: " + identifier);
                if(result != IBinding.NOT_FOUND_BINDING) {
                	name.setTypeBinding((ITypeBinding) result);
                }
            }
            
            // Then find package
            if(result == IBinding.NOT_FOUND_BINDING) {
                result = currentScope.findPackage(identifier);
                // Simple name dependency is recorded by scope
            }
    	}
        else {
        	QualifiedName qualifiedName = (QualifiedName) name;
            Name qualifier = qualifiedName.getQualifier();
            
            IBinding binding = bindUnknownName(qualifier);
		            
            if(binding == IBinding.NOT_FOUND_BINDING) return IBinding.NOT_FOUND_BINDING;
        
            if(binding.isDataBinding()) {
                IDataBinding dataBinding = (IDataBinding) binding;
                if(dataBinding.isDataBindingWithImplicitQualifier()) {
                	qualifier.setAttributeOnName(Name.IMPLICIT_QUALIFIER_DATA_BINDING, dataBinding.getImplicitQualifier());
                	dataBinding = dataBinding.getWrappedDataBinding();
                }
               
                if(dataBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
                	result = ((StructureItemBinding) dataBinding).findData(identifier);
                }
                else {
                	ITypeBinding typeBinding = dataBinding.getType();
                	
                	if(typeBinding == null) {
                		result = IBinding.NOT_FOUND_BINDING;                		
                	}
                	else {                     
	                	if(typeBinding.isDynamicallyAccessible()) {
	                		result = new DynamicDataBinding(name.getCaseSensitiveIdentifier(), dataBinding.getDeclaringPart());
	                	}
	                	else {
	                		switch(dataBinding.getKind()) {
		            			case IDataBinding.EXTERNALTYPE_BINDING:
		            				result = findStaticData(typeBinding, identifier);
		            				break;
		            			default:
		            				result = findData(typeBinding, identifier);
		            		}
	                	}
                	}
                }
                
                if(result != IBinding.NOT_FOUND_BINDING && result.isDataBinding()){
                	IDataBinding resultDataBinding = (IDataBinding) result;
                	name.setTypeBinding(resultDataBinding.getType());
                	dependencyRequestor.recordTypeBinding(resultDataBinding.getType());
                }
            }
            else if(binding.isTypeBinding()) {
                ITypeBinding typeBinding = (ITypeBinding) binding;
         //     System.out.println("bindUnknownName typeBinding: " + typeBinding.getPackageName());
                result = findStaticData(typeBinding, identifier);
                
                if(IBinding.NOT_FOUND_BINDING == result) {
                	try {
                		IBinding packageBinding = bindPackageName(qualifier);
                		binding = packageBinding;
                	}
                	catch(ResolutionException e) {
                	}
                }
            }
            
            if(binding.isPackageBinding()) {
                IPackageBinding packageBinding = (IPackageBinding) binding;
                
                // Find type within the package first
                result = packageBinding.resolveType(identifier);
                
                if(result != IBinding.NOT_FOUND_BINDING){
	                if(((IPartBinding)result).isPrivate() && ((IPartBinding)result).getPackageName() != currentBinding.getPackageName()){
	                    result = IBinding.NOT_FOUND_BINDING;
	                }
	                else {
	                	name.setTypeBinding((ITypeBinding) result);
	                }
	            }
                
                // Then find package
                if(result == IBinding.NOT_FOUND_BINDING) {
                    result = packageBinding.resolvePackage(identifier);
                }
            }
        }
        
        if(result == IBinding.NOT_FOUND_BINDING){
        	name.setBinding(IBinding.NOT_FOUND_BINDING);
        	dependencyRequestor.recordName(name);
        	int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.VARIABLE_NOT_FOUND, new String[] {name.getCanonicalName()});
        }
        else if(result == ITypeBinding.AMBIGUOUS_TYPE){
        	name.setBinding(ITypeBinding.AMBIGUOUS_TYPE);
        	dependencyRequestor.recordName(name);
        	int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.TYPE_IS_AMBIGUOUS, new String[] {name.getCanonicalName()});
        }
        else if(result.isDataBinding() && IDataBinding.AMBIGUOUSSYSTEMLIBRARYFIELD_BINDING == ((IDataBinding) result).getKind()) {
        	name.setBinding(IBinding.NOT_FOUND_BINDING);
        	dependencyRequestor.recordName(name);
        	int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(
        		errorOffsets[0], errorOffsets[1],
        		IProblemRequestor.VARIBLE_NEEDS_SYSTEM_LIBRARY_QUALIFIER,
        		new String[] {
        			name.getCanonicalName(),
        			toCommaList((String[]) ((AmbiguousSystemLibraryFieldDataBinding) result).getAllowedQualifiers().toArray(new String[0]))});
        }
        
        name.setBinding(result);
        if(result.isDataBinding()) {
        	name.setTypeBinding(((IDataBinding) result).getType());
        }
        dependencyRequestor.recordBinding(result);
        return result;
    }
    
    public IBinding bindInvocationTarget(Expression invocationTarget, final boolean allowFuncs) throws ResolutionException {
    	final IBinding result = IBinding.NOT_FOUND_BINDING;
    	final IASTVisitor thisVisitor = this;
    	final ResolutionException[] ex = new ResolutionException[] {null};
    	invocationTarget.accept(new AbstractASTExpressionVisitor() {
    		public void endVisit(StringLiteral stringLiteral) {
    			bindFromString(stringLiteral, stringLiteral.getValue());
    		}
    		
    		private void bindFromString(Expression expr, String value) {
    			ITypeBinding result = IBinding.NOT_FOUND_BINDING;
    			Name name = new ExpressionParser(compilerOptions).parseAsName(value);
    			if(name != null) {
	    			try {
	    				result = bindTypeName(name);
				        if(IBinding.NOT_FOUND_BINDING != result && ITypeBinding.AMBIGUOUS_TYPE != result) {
				        	dependencyRequestor.recordTypeBinding(result);
				        }
	    			}
	    			catch(ResolutionException e) {
	    				//ignore
	    			}
    			}
    			expr.setTypeBinding(result);
			}

			public void endVisitExpression(Expression expression) {
    			expression.accept(thisVisitor);
    			
    			IDataBinding dBinding = expression.resolveDataBinding();
    			if(dBinding != null && IBinding.NOT_FOUND_BINDING != dBinding) {
    				if(IDataBinding.CLASS_FIELD_BINDING == dBinding.getKind() ||
    				   IDataBinding.LOCAL_VARIABLE_BINDING == dBinding.getKind()) {
    					VariableBinding variableBinding = (VariableBinding) dBinding;
						if((variableBinding).isConstant()) {
    						bindFromString(expression, variableBinding.getConstantValue().toString());
    					}
    				}
    			}
    			
    			ITypeBinding tBinding = expression.resolveTypeBinding();
    			if(tBinding != null && IBinding.NOT_FOUND_BINDING != tBinding) {
    				switch(tBinding.getKind()) {
    				case ITypeBinding.PROGRAM_BINDING:
    					break;
    				case ITypeBinding.FUNCTION_BINDING:
    					if (allowFuncs) {
    						break;
    					}
    				case ITypeBinding.PRIMITIVE_TYPE_BINDING:
    					switch(((PrimitiveTypeBinding) tBinding).getPrimitive().getType()) {
    					case Primitive.CHAR_PRIMITIVE:
    					case Primitive.UNICODE_PRIMITIVE:
    					case Primitive.STRING_PRIMITIVE:
    						break;
    					default:
    						ex[0] = new ResolutionException(expression,
								IProblemRequestor.ITEM_OR_CONSTANT_NOT_CHARACTER_TYPE,
								new String[] {expression.getCanonicalString()});
    					}
    					break;
    				default :
    					ex[0] = new ResolutionException(expression,
							IProblemRequestor.INVOCATION_TARGET_INVALID,
							new String[] {expression.getCanonicalString()});    						
    				}
    			}
    		}
    	});
    	if(ex[0] != null) {
    		throw ex[0];
    	}
    	return result;
    }
    
    public static String toCommaList(String[] strings) {
		StringBuffer result = new StringBuffer();
		for(int i = 0; i < strings.length; i++) {
			result.append(strings[i]);
			if(i != strings.length-1) {
				result.append(", ");
			}
		}
		return result.toString();
	}
    
    public static String toCommaList(Collection objects) {
		StringBuffer result = new StringBuffer();
		for(Iterator iter = objects.iterator(); iter.hasNext();) {
			result.append(iter.next());
			if(iter.hasNext()) {
				result.append(", ");
			}
		}
		return result.toString();
	}
    
    public static String toCommaList(ITypeBinding[] types) {
		StringBuffer result = new StringBuffer();
		for(int i = 0; i < types.length; i++) {
			result.append(types[i].getCaseSensitiveName());
			if(i != types.length-1) {
				result.append(", ");
			}
		}
		return result.toString();
	}

	protected IDataBinding findData(ITypeBinding type, String identifier) {
    	if(type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
    		ITypeBinding baseElementType = type.getBaseType();
    		if(baseElementType.getKind() == ITypeBinding.FIXED_RECORD_BINDING ||
    		   baseElementType.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
    			IDataBinding result = findData(baseElementType, identifier);
    			if(IBinding.NOT_FOUND_BINDING != result) {
    				return result;
    			}
    		}    		
    	}
    	IDataBinding result = type.findPublicData(identifier);
    	
    	//For references like <array var>.<item in array that's not at top level>
    	if(result == IBinding.NOT_FOUND_BINDING &&
    	   (ITypeBinding.FIXED_RECORD_BINDING == type.getKind() ||
    	   	ITypeBinding.DATATABLE_BINDING == type.getKind())) {
    		result = (IDataBinding) ((FixedStructureBinding) type).getSimpleNamesToDataBindingsMap().get(identifier);
    		if(result == null) {
    			result = IBinding.NOT_FOUND_BINDING;
    		}
    	}
    	
    	if(IBinding.NOT_FOUND_BINDING == result) {
    		IFunctionBinding fBinding = type.findPublicFunction(identifier);
    		if(IBinding.NOT_FOUND_BINDING != fBinding) {
    			result = new NestedFunctionBinding(fBinding.getCaseSensitiveName(), type.isPartBinding() ? (IPartBinding) type : null, fBinding);
    		}
    	}
    	
    	return result;
    }
	
	protected static Object getConstantValue(Expression expr, IProblemRequestor problemRequestor, boolean returnStringTypes) {
		return getConstantValue(expr, null, null, problemRequestor, returnStringTypes);
	}
      
    protected static Object getConstantValue(Expression expr, ITypeBinding constantType, String constantName, IProblemRequestor problemRequestor) {
    	return getConstantValue(expr, constantType, constantName, problemRequestor, false);
    }
    
    protected static Object getConstantValue(Expression expr, ITypeBinding constantType, String constantName, IProblemRequestor problemRequestor, boolean returnStringTypes) {
    	ConstantValueCreator constValueCreator = new ConstantValueCreator(constantType, constantName, problemRequestor, returnStringTypes);
		expr.accept(constValueCreator);
		if(constValueCreator.constantValue == null) {
			//TODO: have problemRequestor accept real error - non-literal expression in array literal
			problemRequestor.acceptProblem(expr, IProblemRequestor.CONSTANT_VALUE_MUST_BE_LITERAL);			
		}
		return constValueCreator.constantValue;
    }
    
    private static class ConstantValueCreator extends DefaultASTVisitor {
    	Object constantValue;
    	IProblemRequestor problemRequestor;
    	boolean isNegative = false;
		private String constantName;
		private ITypeBinding constantType;
		private boolean returnStringTypes;
    	
		public ConstantValueCreator(ITypeBinding constantType, String constantName, IProblemRequestor problemRequestor, boolean returnStringTypes) {
			this.constantType = constantType;
			this.constantName = constantName;
			this.problemRequestor = problemRequestor;
			this.returnStringTypes = returnStringTypes;
		}
    	
    	public boolean visit(IntegerLiteral integerLiteral) {
    		String str = integerLiteral.getValue();
    		if(isNegative) {
    			str = "-" + str;
    		}
    		
   			if (integerLiteral.getLiteralKind() == LiteralExpression.BIGINT_LITERAL) {
   				try {
   					constantValue = new Long(str);
	   			} catch (NumberFormatException e) {
					problemRequestor.acceptProblem(integerLiteral, IProblemRequestor.BIGINT_LITERAL_OUT_OF_RANGE, new String[] { str });
					constantValue = null;
				}
   			}
   			else if (integerLiteral.getLiteralKind() == LiteralExpression.SMALLINT_LITERAL) {
   				try {
   					constantValue = new Short(str);
	   			} catch (NumberFormatException e) {
					problemRequestor.acceptProblem(integerLiteral, IProblemRequestor.SMALLINT_LITERAL_OUT_OF_RANGE, new String[] { str });
					constantValue = null;
				}
   			}
   			else {
   				try {
   					constantValue = new Integer(str);
	   			} catch (NumberFormatException e) {
					problemRequestor.acceptProblem(integerLiteral, IProblemRequestor.INTEGER_LITERAL_OUT_OF_RANGE, new String[] { str });
					constantValue = null;
				}
   			}
			
    		checkLengthAndDecimals(integerLiteral, integerLiteral.getValue());
			return false;
		}
    	
		public boolean visit(FloatLiteral floatLiteral) {
			String str = floatLiteral.getValue();
    		if(isNegative) {
    			str = "-" + str;
    		}
    		
    		if (floatLiteral.getLiteralKind() == LiteralExpression.SMALLFLOAT_LITERAL) {
    			constantValue = new Float(str);
    		}
    		else {
    			constantValue = new Double(str);
    		}
			return false;
		}
		
		public boolean visit(DecimalLiteral decimalLiteral) {
			String str = decimalLiteral.getValue();
    		if(isNegative) {
    			str = "-" + str;
    		}
			constantValue = new BigDecimal(str);
			checkLengthAndDecimals(decimalLiteral, decimalLiteral.getValue());
			return false;
		}
		
		public boolean visit(StringLiteral stringLiteral) {
			if (returnStringTypes) {
				constantValue = new SpecificTypedLiteral(SpecificTypedLiteral.StringLiteral, stringLiteral.getValue(), stringLiteral.isHex());
			}
			else {
				constantValue = stringLiteral.getValue();
			}
			return false;
		}
		
		public boolean visit(HexLiteral stringLiteral) {
			if (returnStringTypes) {
				constantValue = new SpecificTypedLiteral(SpecificTypedLiteral.HexLiteral, stringLiteral.getValue(), true);
			}
			else {
				constantValue = stringLiteral.getValue();
			}
			
			return false;
		}
		
		public boolean visit(CharLiteral stringLiteral) {
			if (returnStringTypes) {
				constantValue = new SpecificTypedLiteral(SpecificTypedLiteral.CharLiteral, stringLiteral.getValue(), stringLiteral.isHex());
			}
			else {
				constantValue = stringLiteral.getValue();
			}
			return false;
		}
		
		public boolean visit(DBCharLiteral stringLiteral) {
			if (returnStringTypes) {
				constantValue = new SpecificTypedLiteral(SpecificTypedLiteral.DBCharLiteral, stringLiteral.getValue(), stringLiteral.isHex());
			}
			else {
				constantValue = stringLiteral.getValue();
			}
			return false;
		}
		
		public boolean visit(MBCharLiteral stringLiteral) {
			if (returnStringTypes) {
				constantValue = new SpecificTypedLiteral(SpecificTypedLiteral.MBCharLiteral, stringLiteral.getValue(), stringLiteral.isHex());
			}
			else {
				constantValue = stringLiteral.getValue();
			}
			return false;
		}
		
		public boolean visit(org.eclipse.edt.compiler.core.ast.BooleanLiteral booleanLiteral) {
			constantValue = new Boolean(booleanLiteral.booleanValue().booleanValue());
			return false;
		}
		
		public boolean visit(org.eclipse.edt.compiler.core.ast.BytesLiteral bytesLiteral) {
			constantValue = bytesLiteral.getValue();
			return false;
		};
		
		private Expression getBaseExpression(Expression expr) {
			final Expression[] baseExpr = new Expression[] {expr};
			expr.accept(new DefaultASTVisitor(){
				public boolean visit(UnaryExpression unaryExpression) {
					baseExpr[0] = unaryExpression.getExpression();
					return true;
				}
			});
			return baseExpr[0];
		}

		
		public boolean visit(ArrayLiteral arrayLiteral) {
			List constantObjects = new ArrayList();
			Expression previousExpr = null;
			for(Iterator iter = arrayLiteral.getExpressions().iterator(); iter.hasNext();) {
				Expression nextExpr = (Expression) iter.next();
				if(previousExpr != null && !getBaseExpression(nextExpr).getClass().getName().equals(getBaseExpression(previousExpr).getClass().getName())) {
					problemRequestor.acceptProblem(nextExpr, IProblemRequestor.CONSTANT_VALUE_MIXED_TYPE_ARRAY);	
					constantValue = new Object[0];
					return false;
				}
				else {
					ConstantValueCreator constValueCreator = new ConstantValueCreator(constantType, constantName, problemRequestor, returnStringTypes);
					nextExpr.accept(constValueCreator);
					if(constValueCreator.constantValue == null) {
						problemRequestor.acceptProblem(nextExpr, IProblemRequestor.CONSTANT_VALUE_MUST_BE_LITERAL);			
						constantValue = new Object[0];
						return false;
					}
					else {
						constantObjects.add(constValueCreator.constantValue);
					}
				}
				previousExpr = nextExpr;
			}
			if(previousExpr == null) {
				constantValue = new Object[0];
			}
			else {
				//If here, previousExpr must be a literal expression
				LiteralExpression litExpr = (LiteralExpression) getBaseExpression(previousExpr);
				
				switch(litExpr.getLiteralKind()) {
					case LiteralExpression.INTEGER_LITERAL:
						constantValue = constantObjects.toArray(new Number[0]);
						break;
					case LiteralExpression.FLOAT_LITERAL:
						constantValue = constantObjects.toArray(new Float[0]);
						break;
					case LiteralExpression.DECIMAL_LITERAL:
						constantValue = constantObjects.toArray(new Number[0]);
						break;
					case LiteralExpression.STRING_LITERAL:
						if (returnStringTypes) {
							constantValue = constantObjects.toArray(new SpecificTypedLiteral[0]);							
						}
						else {
							constantValue = constantObjects.toArray(new String[0]);
						}
						break;
					case LiteralExpression.BOOLEAN_LITERAL:
						constantValue = constantObjects.toArray(new Boolean[0]);
						break;
					case LiteralExpression.BYTES_LITERAL:
						constantValue = constantObjects.toArray(new String[constantObjects.size()]);
						break;
					case LiteralExpression.ARRAY_LITERAL:
						int numElements = constantObjects.size(); 
						constantValue = Array.newInstance(constantObjects.get(0).getClass(), numElements);
						Object[] arrayForm = (Object[]) constantValue;
						for(int i = 0; i < numElements; i++) {
							arrayForm[i] = constantObjects.get(i);
						}
						break;
					default:
						constantValue = new Object[0];
				}
			}
			
			return false;
		}		

		public boolean visit(UnaryExpression unaryExpression) {
			if(unaryExpression.getOperator() == UnaryExpression.Operator.MINUS) {
				isNegative = !isNegative;
			}
			return true;
		}
		
		private void checkLengthAndDecimals(Node nodeForErrors, String value) {
			if(constantType != null && IBinding.NOT_FOUND_BINDING != constantType) {
				if(ITypeBinding.PRIMITIVE_TYPE_BINDING == constantType.getKind()) {
					PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) constantType;
					int length = primTypeBinding.getLength();
					int decimals = primTypeBinding.getDecimals();
					switch(primTypeBinding.getPrimitive().getType()) {
						case Primitive.DECIMAL_PRIMITIVE:
						case Primitive.BIN_PRIMITIVE:
						case Primitive.MONEY_PRIMITIVE:
						case Primitive.NUM_PRIMITIVE:
						case Primitive.NUMC_PRIMITIVE:
						case Primitive.PACF_PRIMITIVE:
							int nonDecimalLength = length-decimals;
							if(nonDecimalLength >= 0) {
								StringTokenizer st = new StringTokenizer(value, ".");
								int valueNonDecimalLength = st.nextToken().length();
								int valueDecimals = st.hasMoreTokens() ? st.nextToken().length() : 0;
								if(valueNonDecimalLength > nonDecimalLength && !primTypeBinding.isReference()) {
									problemRequestor.acceptProblem(
										nodeForErrors,
										IProblemRequestor.LENGTH_OF_NONDECIMAL_DIGITS_FOR_CONSTANT_TOO_LONG,
										new String[] {
											value,
											Integer.toString(valueNonDecimalLength),
											Integer.toString(nonDecimalLength),
											constantName
										});
								}
								if(valueDecimals > decimals && !primTypeBinding.isReference()) {
									problemRequestor.acceptProblem(
										nodeForErrors,
										IProblemRequestor.DECIMALS_OF_VALUE_FOR_CONSTANT_TOO_LONG,
										new String[] {
											value,
											Integer.toString(valueDecimals),
											Integer.toString(decimals),
											constantName
										});
								}
							}
					}
				}
			}
		}
    }
    
    /**
     * Have Expression AST accept an instance of this class and use
     * getOccursValue() to get the integer value for the occurs if the
     * expression is a literal integer expression. If the expression is
     * not a literal integer expression, a problem is reported to the
     * IProblemRequestor that is passed in and getOccursValue() returns -1.
     * 
     * This class should be used to set the occurs fields for structure item
     * and form field bindings.
     */
    protected static class OccursValueFinder extends AbstractASTExpressionVisitor {
    	private int occursValue = -1;
    	private boolean haveIssuedError = false;
    	private IProblemRequestor problemRequestor;
    	private String canonicalItemName;
    	
		public OccursValueFinder(IProblemRequestor problemRequestor, String canonicalItemName) {
			this.problemRequestor = problemRequestor;
			this.canonicalItemName = canonicalItemName;
		}
		
		public int getOccursValue() {
			return occursValue;
		}

		public boolean visit(IntegerLiteral integerLiteral) {
			try {
				occursValue = Integer.parseInt(integerLiteral.getValue());
			}
			catch(NumberFormatException e) {
				haveIssuedError = true;
				reportSizeOutOfRangeError(integerLiteral, integerLiteral.getCanonicalString());
			}
			return false;
		}
		
        public void endVisitExpression(Expression expression) {
			if(!haveIssuedError && occursValue < 1) {
				reportSizeNotIntegerError(expression, expression.getCanonicalString());
			}
		}
        
        protected void reportSizeNotIntegerError(Node errorNode, String canonicalExprString) {
        	problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.OCCURS_SIZE_NOT_POSITIVE_INTEGER,
				new String[] {canonicalExprString});
        }
        
        protected void reportSizeOutOfRangeError(Node errorNode, String canonicalExprString) {
        	problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.INVALID_OCCURS_VALUE,
				new String[] {canonicalExprString, canonicalItemName});
        }
    }

    protected static void bindNamesToNotFound(Node node) {
    	node.accept(new AbstractASTVisitor() {
    		public void endVisit(SimpleName simpleName) {
				simpleName.setBinding(IBinding.NOT_FOUND_BINDING);
			}
			public void endVisit(QualifiedName qualifiedName) {
				qualifiedName.setBinding(IBinding.NOT_FOUND_BINDING);
			}
    	});
    }
    

	public static boolean annotationIs(ITypeBinding annotationType, String[] packageName, String annotationName) {
		return annotationType != null && annotationType.getPackageName() == InternUtil.intern(packageName) && annotationType.getName() == InternUtil.intern(annotationName);
	}    
    
    public static boolean typeIs(ITypeBinding type, String[] packageName, String annotationName) {
		return type != null && type.getPackageName() == InternUtil.intern(packageName) && type.getName() == InternUtil.intern(annotationName);
	}
    
    public static boolean enumerationIs(EnumerationDataBinding enumDBinding, String[] enumerationTypePackageName, String enumerationTypeName, String enumeratedValueName) {
    	return enumDBinding.getType().getPackageName() == InternUtil.intern(enumerationTypePackageName) &&
               enumDBinding.getType().getName() == InternUtil.intern(enumerationTypeName) &&
               enumDBinding.getName() == InternUtil.intern(enumeratedValueName); 
    }
    
    public static boolean dataBindingIs(IDataBinding dataBinding, String[] enclosingTypePackageName, String enclosingTypeName, String dataBindingName) {
		return dataBinding != null &&
		       IBinding.NOT_FOUND_BINDING != dataBinding &&
		       dataBinding.getDeclaringPart() != null &&
		       dataBinding.getDeclaringPart().getPackageName() == InternUtil.intern(enclosingTypePackageName) &&
		       dataBinding.getDeclaringPart().getName() == InternUtil.intern(enclosingTypeName) &&
		       dataBinding.getName() == InternUtil.intern(dataBindingName);
	}
    
    public static boolean functionBindingIs(IFunctionBinding functionBinding, String[] enclosingTypePackageName, String enclosingTypeName, String dataBindingName) {
		return functionBinding != null &&
			   functionBinding.getDeclarer() != null &&
			   functionBinding.getDeclarer().getPackageName() == InternUtil.intern(enclosingTypePackageName) &&
			   functionBinding.getDeclarer().getName() == InternUtil.intern(enclosingTypeName) &&
			   functionBinding.getName() == InternUtil.intern(dataBindingName);
	}
    
    protected void processSettingsBlock(ClassDataDeclaration classDataDeclaration, IPartBinding functionContainerBinding, Scope functionContainerScope, IProblemRequestor problemRequestor) {
        
        Iterator i = classDataDeclaration.getNames().iterator();
        boolean annotationFoundUsingScope = false;
        while (i.hasNext()) {
            Name name = (Name) i.next();
            if (classDataDeclaration.hasSettingsBlock()) {
                if (name.resolveBinding() != null && name.resolveBinding() != IBinding.NOT_FOUND_BINDING) {
                    ClassFieldBinding fieldBinding = (ClassFieldBinding) name.resolveBinding();
                    Scope scope = new DataBindingScope(currentScope, fieldBinding);
                    AnnotationLeftHandScope annotationScope = new AnnotationLeftHandScope(scope, fieldBinding, fieldBinding.getType(),
                            fieldBinding, -1, functionContainerBinding);
                    annotationScope.setAnnotationFoundUsingThisScope(annotationFoundUsingScope);
                    Scope fcScope = functionContainerScope;
                    fcScope.startReturningTopLevelFunctions();
                    classDataDeclaration.getSettingsBlockOpt().accept(
                            new SettingsBlockAnnotationBindingsCompletor(fcScope, functionContainerBinding, annotationScope, dependencyRequestor,
                                    problemRequestor, compilerOptions));
                    annotationFoundUsingScope = annotationScope.isAnnotationFoundUsingThisScope();
                }
            }
        }
    }
    
    public boolean isNullable(ITypeBinding binding) {
    	if (!Binding.isValidBinding(binding)) {
    		return false;
    	}
    	if (binding.isNullable()) {
    		return true;
    	}
    	
    	if (binding.getKind() == ITypeBinding.DATAITEM_BINDING) {
    		DataItemBinding diBinding = (DataItemBinding) binding;
    		return isNullable(diBinding.getPrimitiveTypeBinding());
    	}
    	
    	if (binding != binding.getBaseType()) {
    		return isNullable(binding.getBaseType());
    	}
    	
    	return false;
    }
}
