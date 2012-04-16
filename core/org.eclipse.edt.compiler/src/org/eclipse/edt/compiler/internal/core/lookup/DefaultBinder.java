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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.edt.compiler.binding.AmbiguousDataBinding;
import org.eclipse.edt.compiler.binding.AmbiguousFunctionBinding;
import org.eclipse.edt.compiler.binding.AnnotationBinding;
import org.eclipse.edt.compiler.binding.AnnotationFieldBinding;
import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ArrayDictionaryBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ConstructorBinding;
import org.eclipse.edt.compiler.binding.DataBinding;
import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.DelegateBinding;
import org.eclipse.edt.compiler.binding.DictionaryBinding;
import org.eclipse.edt.compiler.binding.DynamicDataBinding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.ForeignLanguageTypeBinding;
import org.eclipse.edt.compiler.binding.FormFieldBinding;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IPartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.binding.IsNotStateBinding;
import org.eclipse.edt.compiler.binding.LocalVariableBinding;
import org.eclipse.edt.compiler.binding.MultiplyOccuringItemTypeBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.NilBinding;
import org.eclipse.edt.compiler.binding.OverloadedFunctionSet;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.ProgramBinding;
import org.eclipse.edt.compiler.binding.SettingsBlockAnnotationBindingsCompletor;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.binding.VariableBinding;
import org.eclipse.edt.compiler.binding.VariableFormFieldBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.binding.annotationType.EGLNotInCurrentReleaseAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.AsExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.Assignment.Operator;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.BooleanLiteral;
import org.eclipse.edt.compiler.core.ast.CharLiteral;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.DBCharLiteral;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.ForwardStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.FunctionInvocationStatement;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.HexLiteral;
import org.eclipse.edt.compiler.core.ast.InExpression;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.IsAExpression;
import org.eclipse.edt.compiler.core.ast.IsNotExpression;
import org.eclipse.edt.compiler.core.ast.LikeMatchesExpression;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.MBCharLiteral;
import org.eclipse.edt.compiler.core.ast.MoveModifier;
import org.eclipse.edt.compiler.core.ast.MoveStatement;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NullLiteral;
import org.eclipse.edt.compiler.core.ast.NullableType;
import org.eclipse.edt.compiler.core.ast.ObjectExpression;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.PrimitiveType;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.ProgramParameter;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.ReturningToNameClause;
import org.eclipse.edt.compiler.core.ast.ReturnsDeclaration;
import org.eclipse.edt.compiler.core.ast.SQLLiteral;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.core.ast.TernaryExpression;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.TypeLiteralExpression;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.core.ast.UsingPCBClause;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.utils.ExpressionParser;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.annotation.LengthItemForSerialMessageOrIndexedRecordValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.RecordNumItemValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.AssignmentStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.type.PrimitiveTypeValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;



/**
 * @author winghong
 */
public abstract class DefaultBinder extends AbstractBinder {
    
    protected IProblemRequestor problemRequestor;
    
    private List IOObjectsInCurrentStatement = Collections.EMPTY_LIST;
    private boolean visitingIntoClause;    
   
    private List FormGroupsInProgram = Collections.EMPTY_LIST;

	private boolean bindingFunctionInvocationTarget;

	private boolean dotOperatorAllowedForArrays = false;
    
    public DefaultBinder(Scope currentScope, IPartBinding currentBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, currentBinding, dependencyRequestor, compilerOptions);
        this.problemRequestor = problemRequestor;
        this.compilerOptions = compilerOptions;
    }

    /**
     * This method binds names that are expression names
     * Clients must be careful to prevent this method from being called on names that are not expression names
     * e.g. package names, types names
     * Usually the owner of these names have their own visitor and that visit method should return false 
     * to prevent this method from being called
     */
    public boolean visit(QualifiedName qualifiedName) {
    	if(qualifiedName.resolveBinding() == null) {
	        try {        	
	            bindExpressionName(qualifiedName, bindingFunctionInvocationTarget);
	            
	            IBinding qualifierBinding = qualifiedName.getQualifier().resolveBinding();
	            if(IBinding.NOT_FOUND_BINDING != qualifierBinding && qualifierBinding != null) {
	            	if(qualifierBinding.isTypeBinding() &&
	            	   ITypeBinding.LIBRARY_BINDING == ((ITypeBinding) qualifierBinding).getKind() &&
	            	   qualifierBinding.getAnnotation(EGLNotInCurrentReleaseAnnotationTypeBinding.getInstance()) != null) {
	            		problemRequestor.acceptProblem(
	            			qualifiedName.getQualifier(),
	            			IProblemRequestor.SYSTEM_PART_NOT_SUPPORTED,
	            			new String[] {
	            				qualifierBinding.getCaseSensitiveName()
	            			});	            	
	            	}
	            }
	        } catch (ResolutionException e) {
//	        	if(qualifiedName.getQualifier().resolveBinding() == IBinding.NOT_FOUND_BINDING) {
	        		qualifiedName.setBinding(IBinding.NOT_FOUND_BINDING);
//	        	}
	        	problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
	        }
    	}
        return false;
    }
    
    protected void handleNameResolutionException(ResolutionException e) {
    	problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    }

    /**
     * This methods binds names that are expression names
     * See method above for more info
     */
    public boolean visit(SimpleName simpleName) {
    	if(bindingFunctionInvocationTarget && simpleName.getIdentifier().toLowerCase().startsWith("eze")) {
    		//Don't issue error for invocations of functions whose name begins with eze
    		simpleName.setBinding(IBinding.NOT_FOUND_BINDING);
    		return false;
    	}
    	
        // In some cases (particular for names of part declarations), the simple name has already been bound
        if(simpleName.resolveBinding() == null) {
        	if(visitingIntoClause) {
        		bindNameFromSQLRecordArrayBindingsInIOObjects(simpleName);
        	}
        	if(simpleName.resolveBinding() == null ) {
	            try {
	            	bindExpressionName(simpleName, bindingFunctionInvocationTarget);	            	
	            } catch (ResolutionException e) {
                    handleNameResolutionException(e);
                }
                return false;
        	}        	
        }
        return false;
    }
    
    public void endVisit(Program program){
    	Iterator iter = FormGroupsInProgram.iterator();
    	boolean iterHelpGroup = false;
    	
    	while(iter.hasNext()){
    		UseStatement useStatement = (UseStatement)iter.next();
    		if (useStatement.getUsedTypeBinding().isHelpGroup()){
    			if (iterHelpGroup){
    				problemRequestor.acceptProblem((Node)useStatement.getNames().get(0),
    	    				IProblemRequestor.PROGRAM_USE_STATEMENT_TOO_MANY_HELP_GROUP_PROPERTIES);
    			}else iterHelpGroup = true;
    		}
    		
    	}
    	
    	FormGroupsInProgram = Collections.EMPTY_LIST;
    }
    
    public void endVisit(UseStatement useStatement){
    	if(FormGroupsInProgram == Collections.EMPTY_LIST){
    		FormGroupsInProgram = new ArrayList();
    	}
    	FormGroupsInProgram.add(useStatement);
    	
    }
    
	public boolean visit(FunctionInvocation functionInvocation) {
		bindingFunctionInvocationTarget = true;
		functionInvocation.getTarget().accept(this);
		bindingFunctionInvocationTarget = false;
		
		ITypeBinding tBinding = functionInvocation.getTarget().resolveTypeBinding();
		if(IBinding.NOT_FOUND_BINDING != tBinding && tBinding != null) {
			if(tBinding.isFunctionBinding()) {
				IFunctionBinding fBinding = (IFunctionBinding) tBinding;
			}
			else if(ITypeBinding.DELEGATE_BINDING != tBinding.getKind()){
				if (functionInvocation.getTarget() instanceof ThisExpression) {
					OverloadedFunctionSet funcSet = getConstructors(tBinding);
					if (funcSet == null || funcSet.getNestedFunctionBindings().isEmpty()) {
						problemRequestor.acceptProblem(
								functionInvocation.getTarget(),
								IProblemRequestor.MATCHING_CONSTRUCTOR_CANNOT_BE_FOUND,
								new String[]{tBinding.getName()});
					}
					else {
						functionInvocation.getTarget().setDataBinding(funcSet);
					}
				}
				else {
					problemRequestor.acceptProblem(
						functionInvocation.getTarget(),
						IProblemRequestor.FUNCTION_INVOCATION_TARGET_NOT_FUNCTION_OR_DELEGATE);
				}
			}
		}
        for(Iterator iter = functionInvocation.getArguments().iterator(); iter.hasNext();) {
        	((Node) iter.next()).accept(this);
        }
		
        return false;
	}
	
	public boolean visit(ThisExpression thisExpression) {
	    if (thisExpression.resolveTypeBinding() !=  null) {
	        return false;
	    }
		Scope scopeForThis = currentScope.getScopeForKeywordThis();
		if(scopeForThis instanceof FunctionContainerScope) {
			thisExpression.setTypeBinding(((FunctionContainerScope) scopeForThis).getPartBinding());
		}
		else {
			thisExpression.setTypeBinding(IBinding.NOT_FOUND_BINDING);
			problemRequestor.acceptProblem(thisExpression, IProblemRequestor.VARIABLE_NOT_FOUND, new String[] {thisExpression.getCanonicalString()});
		}
		return false;
	}
	
	public boolean visit(TypeLiteralExpression typeLiteralExpression) {
		try {
			bindType(typeLiteralExpression.getType());
		}
		catch(ResolutionException e) {
			//Shouldn't happen since only primitive types can be used in this expression
		}
		return false;
	}

	public boolean visit(ArrayAccess arrayAccess) {
		
		if (arrayAccess.getIndices().size() > 1) {
			problemRequestor.acceptProblem(
					arrayAccess,
					IProblemRequestor.MULTI_INDICES_NOT_SUPPORTED,
					new String[]{arrayAccess.getCanonicalString()});

		}
		
		return true;
	}
	
    public void endVisit(ArrayAccess arrayAccess) {
        if (arrayAccess.resolveTypeBinding() != null) {
            return;
        }
    	ITypeBinding type = arrayAccess.getArray().resolveTypeBinding();
    	if(type != null && type != IBinding.NOT_FOUND_BINDING) {
    		if(arrayAccess.getIndices().size() == 1) {
	    		ITypeBinding indexType = ((Expression) arrayAccess.getIndices().get(0)).resolveTypeBinding();
	    		if(indexType != null && indexType.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING
	    				             && Primitive.isStringType(((PrimitiveTypeBinding) indexType).getPrimitive())) {
	    			if(!type.isDynamicallyAccessible() &&
	    			   type.getKind() != ITypeBinding.FLEXIBLE_RECORD_BINDING &&
					   type.getKind() != ITypeBinding.EXTERNALTYPE_BINDING) {
	    				problemRequestor.acceptProblem(
	    					arrayAccess.getArray(),
							IProblemRequestor.NON_DYNAMIC_ACCESS_ACCESSED_DYNAMICALLY,
							new String[]{arrayAccess.getArray().getCanonicalString()});
	    				
	    			}
	    			arrayAccess.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.ANY).getNullableInstance());
	    			return;
	    		}
    		}
    		
    		int[] dimensions = getDimensions(arrayAccess.getArray());
    		int currentSubscriptNum = 0;
    		for(Iterator iter = arrayAccess.getIndices().iterator(); iter.hasNext();) {
    			final Expression nextIndex = (Expression) iter.next();
    			ITypeBinding tBinding = nextIndex.resolveTypeBinding();
    			if(tBinding != null) {
    				boolean typeIsValid = false;
    				if(tBinding.isDynamic()) {
    					typeIsValid = true;
    				}
    				else if(ITypeBinding.PRIMITIVE_TYPE_BINDING == tBinding.getKind()) {
    					PrimitiveTypeBinding pTypeBinding = (PrimitiveTypeBinding) tBinding;
    					if(Primitive.isNumericType(pTypeBinding.getPrimitive()) && pTypeBinding.getDecimals() == 0) {
    						typeIsValid = true;
    					}
    				}
    				if(!typeIsValid) {
    					problemRequestor.acceptProblem(
    						nextIndex,
							IProblemRequestor.SUBSCRIPT_MUST_BE_INTEGER_ITEM,
							new String[] {nextIndex.getCanonicalString(), arrayAccess.getCanonicalString()});
    				}
    			}
    			
    			if(currentSubscriptNum < dimensions.length) {
    				int currentDimension = dimensions[currentSubscriptNum];
    				int currentSubscript = getInt(nextIndex);
    				if(currentSubscript != -1) {
	    				if(currentSubscript > currentDimension) {
	    					problemRequestor.acceptProblem(
	    						nextIndex,
								IProblemRequestor.SUBSCRIPT_OUT_OF_RANGE,
								new String[] {Integer.toString(currentSubscript), arrayAccess.getCanonicalString()});
	    				}
    				}
    			}
    			else if(!type.isDynamic()){
    				if(dimensions.length == 0) {
    					problemRequestor.acceptProblem(
	    					arrayAccess.getArray(),
							IProblemRequestor.NON_ARRAY_ACCESS_SUBSCRIPTED,
							new String[] {
	    						arrayAccess.getArray().getCanonicalString()
	    					});
    				}
    				else {
	    				problemRequestor.acceptProblem(
	    					arrayAccess.getArray(),
							IProblemRequestor.TOO_MANY_SUBSCRIPTS,
							new String[] {
	    						arrayAccess.getCanonicalString()
	    					});
    				}
    				break;
    			}
    			currentSubscriptNum += 1;
    		}    		

    		if(type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
	    		ArrayTypeBinding arrayTypeBinding = (ArrayTypeBinding) type; 
	    		for(int i = 0; i < arrayAccess.getIndices().size()-1; i++) {
	    			ITypeBinding elementType = arrayTypeBinding.getElementType();
	    			if(elementType.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
	    				arrayTypeBinding = ((ArrayTypeBinding) elementType);
	    			}
	    		}
	    		arrayAccess.setTypeBinding(arrayTypeBinding.getElementType());
	    	}
	    	else if(type.isDynamic()) {
	    		arrayAccess.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.ANY));
	    	}
	    	else if(ITypeBinding.ARRAYDICTIONARY_BINDING == type.getKind()) {
	    		arrayAccess.setTypeBinding(DictionaryBinding.INSTANCE);
	    	}
	    	else {
	    		IDataBinding dBinding = arrayAccess.getArray().resolveDataBinding();
	    		if(dBinding != null && dBinding != IBinding.NOT_FOUND_BINDING) {
	    			if(IDataBinding.STRUCTURE_ITEM_BINDING == dBinding.getKind()) {
	    				arrayAccess.setDataBinding(dBinding);
	    				arrayAccess.setTypeBinding(dBinding.getType());
	    				
	    				if(((StructureItemBinding) dBinding).isMultiplyOccuring()) {
		    				if(numSubscripts(arrayAccess) < dimensions.length) {
	    						arrayAccess.setTypeBinding(new MultiplyOccuringItemTypeBinding(dBinding.getType()));
		    				}
	    				}
	    			}
	    			else if(IDataBinding.FORM_FIELD == dBinding.getKind() &&
	    					((FormFieldBinding) dBinding).isMultiplyOccuring()) {
	    				arrayAccess.setDataBinding(dBinding);
	    				arrayAccess.setTypeBinding(dBinding.getType());
	    			}
	    			else {
//	    				problemRequestor.acceptProblem(
//	    					arrayAccess.getArray(),
//							IProblemRequestor.NON_ARRAY_ACCESS_SUBSCRIPTED,
//							new String[] {arrayAccess.getArray().getCanonicalString()});
	    			}
	    		}
	    	}    		
    	}    	
    }
    
    private static class DimensionGatherer extends AbstractASTExpressionVisitor {
    	List dimensions = new ArrayList();
    	boolean haveProcessedStructureItem = false;
    	
    	int[] getDimensions(Expression expr) {
    		expr.accept(this);
    		int[] result = new int[dimensions.size()];
    		int i = 0;
    		for(Iterator iter = dimensions.iterator(); iter.hasNext();) {
    			result[i++] = ((Integer) iter.next()).intValue();
    		}
    		
    		//System.out.println("For expression: " + expr.getCanonicalString() + ", dimensions are " + toString(result));
    		
    		return result;
    	}
    	
//    	private String toString(int[] ary) {
//    		StringBuffer sb = new StringBuffer();
//    		sb.append("[");
//    		for(int i = 0; i < ary.length; i++) {
//    			sb.append(Integer.toString(ary[i]));
//    			if(i != ary.length-1) {
//    				sb.append(",");
//    			}
//    		}
//    		sb.append("]");
//    		return sb.toString();
//    	}
    	
    	private List getDimensions(StructureItemBinding itemBinding) {
    		List result = new ArrayList();
    		StructureItemBinding parentItem = itemBinding.getParentItem();
    		if(parentItem != null) {
    			result.addAll(getDimensions(parentItem));
    		}
    		else if(ITypeBinding.DATATABLE_BINDING == itemBinding.getEnclosingStructureBinding().getKind()) {
    			if(!itemBinding.hasOccurs()) {
    				result.add(new Integer(Integer.MAX_VALUE));
    			}
    		}
    		
    		if(itemBinding.hasOccurs()) {
    			result.add(new Integer(itemBinding.getOccurs()));
    		}    		
    		return result;
    	}
    	
    	private void processBinding(IDataBinding dBinding) {
    		if(IDataBinding.STRUCTURE_ITEM_BINDING == dBinding.getKind()) {
				if(!haveProcessedStructureItem) {
					haveProcessedStructureItem = true;
					dimensions.addAll(0, getDimensions((StructureItemBinding) dBinding));
				}
			}
			else if(IDataBinding.FORM_FIELD == dBinding.getKind()) {
				FormFieldBinding fieldBinding = (FormFieldBinding) dBinding;
				if(fieldBinding.isMultiplyOccuring()) {
					dimensions.add(0, new Integer(fieldBinding.getOccurs()));
				}
			}
			else if(IDataBinding.LOCAL_VARIABLE_BINDING == dBinding.getKind() ||
					IDataBinding.CLASS_FIELD_BINDING == dBinding.getKind() ||
					IDataBinding.FUNCTION_PARAMETER_BINDING == dBinding.getKind() ||
					IDataBinding.PROGRAM_PARAMETER_BINDING == dBinding.getKind() ||
					IDataBinding.FLEXIBLE_RECORD_FIELD == dBinding.getKind() ||
					IDataBinding.SYSTEM_VARIABLE_BINDING == dBinding.getKind()) {
				ITypeBinding tBinding = dBinding.getType();
				processBinding(tBinding);
			}
    	}
    	
    	private void processBinding(ITypeBinding tBinding) {
    		if(tBinding != null) {
    			while(ITypeBinding.ARRAY_TYPE_BINDING == tBinding.getKind()) {
					dimensions.add(0, new Integer(Integer.MAX_VALUE));
					tBinding = ((ArrayTypeBinding) tBinding).getElementType();
				}
				if(ArrayDictionaryBinding.INSTANCE == tBinding) {
					dimensions.add(0, new Integer(Integer.MAX_VALUE));
				}
    		}
    	}
    	
		public boolean visitName(Name name) {
			IDataBinding dBinding = name.resolveDataBinding();
			if(dBinding != null && dBinding != IBinding.NOT_FOUND_BINDING) {
				processBinding(dBinding);
				
				if(IDataBinding.STRUCTURE_ITEM_BINDING == dBinding.getKind()) {
					IDataBinding implicitQualifier = (IDataBinding) name.getAttribute(Name.IMPLICIT_QUALIFIER_DATA_BINDING);
					if(implicitQualifier != null) {
						processBinding(implicitQualifier);
					}
					
					/*
					 * Special code to handle the following case:
					 * 
					 * record rec1 type BasicRecord
					 *   10 id int;
					 *   10 name CHAR(20);
					 * end
					 * 
					 * Record rec2 type BasicRecord
					 *   10 r2 rec1[2] {this[1] {id = 2, name = "William"}, this[2] {id = 3, name = "Emily"}};
					 * end
					 */			
					if(name.isSimpleName()) {
						Node parent = name.getParent();
						if(parent instanceof Assignment && name == ((Assignment) parent).getLeftHandSide()) {
							parent = parent.getParent();
							if(parent instanceof SettingsBlock) {
								parent = parent.getParent();
								if(parent instanceof SetValuesExpression) {
									Expression target = ((SetValuesExpression) parent).getExpression();
									if(target instanceof ArrayAccess) {
										processIndices(((ArrayAccess) target).getIndices());
									}
								}
							}
						}
					}
				}
			}
			return true;
		}
		
		private void processIndices(List indices) {
			for(Iterator iter = indices.iterator(); iter.hasNext();) {
				ITypeBinding indexType = ((Expression) iter.next()).resolveTypeBinding();
				if(indexType == null || ITypeBinding.PRIMITIVE_TYPE_BINDING != indexType.getKind() ||
				   !Primitive.isStringType(((PrimitiveTypeBinding) indexType).getPrimitive())) {
					if(!dimensions.isEmpty()) {
						dimensions.remove(0);
					}
				}
			}
		}

		public boolean visitExpression(Expression expression) {
			ITypeBinding tBinding = expression.resolveTypeBinding();
			if(tBinding != null) {
				processBinding(tBinding);
			}
			return false;
		}
		
		public boolean visit(ParenthesizedExpression parenthesizedExpression) {
			return true;
		}
		
		public boolean visit(FieldAccess fieldAccess) {
			IDataBinding dBinding = fieldAccess.resolveDataBinding();
			if(dBinding != null && dBinding != IBinding.NOT_FOUND_BINDING) {
				processBinding(dBinding);
			}
			return true;
		}
		
		public boolean visit(ThisExpression thisExpression) {
			processBinding(thisExpression.resolveTypeBinding());
			return false;
		}
		
		public boolean visit(ArrayAccess arrayAccess) {
			arrayAccess.getArray().accept(this);
			processIndices(arrayAccess.getIndices());
			return false;
		}    	
    }
    
    private int[] getDimensions(Expression expr) {
    	return new DimensionGatherer().getDimensions(expr);
    }
    
    private static class SubscriptsGatherer extends AbstractASTVisitor {
    	List subscripts = new ArrayList();
    	
    	List getSubscripts(Expression expr) {
    		expr.accept(this);
    		return subscripts;
    	}

		public boolean visit(ArrayAccess arrayAccess) {
			arrayAccess.getArray().accept(this);
			for(Iterator iter = arrayAccess.getIndices().iterator(); iter.hasNext();) {
				Expression nextExpr = (Expression) iter.next();
				ITypeBinding indexType = nextExpr.resolveTypeBinding();
				if(indexType == null || ITypeBinding.PRIMITIVE_TYPE_BINDING != indexType.getKind() ||
				   !Primitive.isStringType(((PrimitiveTypeBinding) indexType).getPrimitive())) {
					subscripts.add(nextExpr);
				}
			}
			return false;
		}    	
    }
    
    private int numSubscripts(Expression expr) {
    	return new SubscriptsGatherer().getSubscripts(expr).size();
    }
    
    private static class IntGetter extends DefaultASTVisitor {
    	int intValue = -1;
    	boolean isNegative = false;
    	
    	int getInt(Expression expr) {
    		expr.accept(this);
    		return intValue;
    	}
    	
    	public boolean visit(UnaryExpression unaryExpression) {
    		if(unaryExpression.getOperator() == UnaryExpression.Operator.MINUS) {
    			isNegative = !isNegative;
    		}
			return true;
		}
    	
		public boolean visit(IntegerLiteral integerLiteral) {
			intValue = Integer.parseInt(integerLiteral.getValue());
			if(isNegative) {
				intValue *= -1;
			}
			return false;
		}
    	
    	public boolean visit(ParenthesizedExpression parenthesizedExpression) {
			return true;
		}
    }
    
    /**
     * Returns the appropriate integer if expr is a literal integer expression,
     * -1 otherwise.
     */
    private int getInt(Expression expr) {
    	return new IntGetter().getInt(expr);
    }
    
	public void endVisit(SubstringAccess substringAccess) {
		ITypeBinding type = substringAccess.getPrimary().resolveTypeBinding();
    	if(type != null) {
    		boolean typeIsValid = false;
    		
    		if(type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
    			Primitive prim = ((PrimitiveTypeBinding) type).getPrimitive();
    			typeIsValid = Primitive.isStringType(prim);
    			if(typeIsValid) {
    				boolean typeIsNullable = type.isNullable();
    				type = PrimitiveTypeBinding.getInstance(prim, getSubstringLength(substringAccess.getExpr(), substringAccess.getExpr2()));
    				if(typeIsNullable) {
    					type = type.getNullableInstance();
    				}
    			}
	    	}
	    	
    		if(typeIsValid) {    			
    			substringAccess.setTypeBinding(type);
    		}
    		else {
    			problemRequestor.acceptProblem(
    				substringAccess.getPrimary(),
					IProblemRequestor.SUBSTRING_TARGET_NOT_STRING,
					new String[] {substringAccess.getPrimary().getCanonicalString()});
    		}
    	}
    	checkSubstringIndex(substringAccess.getExpr(), substringAccess);
    	checkSubstringIndex(substringAccess.getExpr2(), substringAccess);
	}
	
	private static class IntFromExpressionGetter extends DefaultASTVisitor {
		int result = -1;
		boolean isNegative = false;
		
		int getInt(Expression expr) {
			IDataBinding dBinding = expr.resolveDataBinding();
			if(dBinding != null) {
				if(dBinding != IBinding.NOT_FOUND_BINDING) {
					if(IDataBinding.CLASS_FIELD_BINDING == dBinding.getKind() ||
					   IDataBinding.LOCAL_VARIABLE_BINDING == dBinding.getKind()) {
						VariableBinding varBinding = (VariableBinding) dBinding;
						if(varBinding.isConstant()) {
							Object constValue = varBinding.getConstantValue();
							if(constValue instanceof Integer) {
								result = ((Integer) constValue).intValue();
							}
						}
					}
				}
				return result;
			}
			else {
				expr.accept(this);
				if(isNegative && result != -1) {
					result = -1 * result;
				}
			}
			return result;
		}
		
		public boolean visit(ParenthesizedExpression parenthesizedExpression) {
			return true;
		}
		
		public boolean visit(UnaryExpression unaryExpression) {
			if(unaryExpression.getOperator() == UnaryExpression.Operator.MINUS) {
				isNegative = !isNegative;
			}
			return true;
		}
		
		public boolean visit(IntegerLiteral integerLiteral) {
			result = Integer.parseInt(integerLiteral.getValue());
			return false;
		}
	}
	
	/**
	 * Gets integer values for both expressions and returns the second integer minus
	 * the first plus one. Returns -1 if an integer value cannot be determined for
	 * either expressions (-1 meaning that the length is indeterminate).
	 */
	private int getSubstringLength(Expression firstExpr, Expression secondExpr) {		
		int intFromSecondExpr = new IntFromExpressionGetter().getInt(secondExpr);
		if(intFromSecondExpr == -1) {
			return -1;
		}
		
		int intFromFirstExpr = new IntFromExpressionGetter().getInt(firstExpr);
		if(intFromFirstExpr == -1) {
			return -1;
		}
		
		return intFromSecondExpr - intFromFirstExpr + 1;
	}
	
	private void checkSubstringIndex(final Expression index, SubstringAccess parentAccess) {
		ITypeBinding tBinding = index.resolveTypeBinding();
		if(tBinding != null) {
			boolean typeIsValid = false;
			if(tBinding.isDynamic()) {
				typeIsValid = true;
			}
			else if(ITypeBinding.PRIMITIVE_TYPE_BINDING == tBinding.getKind()) {
				PrimitiveTypeBinding pTypeBinding = (PrimitiveTypeBinding) tBinding;
				if(Primitive.isNumericType(pTypeBinding.getPrimitive()) &&
				   pTypeBinding.getDecimals() == 0) {
					typeIsValid = true;
				}
			}			
			if(!typeIsValid) {
				problemRequestor.acceptProblem(
					index,
					IProblemRequestor.SUBSTRING_INDEX_NOT_INTEGER,
					new String[] {index.getCanonicalString(), parentAccess.getCanonicalString()});
			}
		}
	}

     public void endVisit(final FieldAccess fieldAccess) {
        if (fieldAccess.resolveTypeBinding() != null) {
            return;
        }
        
        IDataBinding expressionData = fieldAccess.getPrimary().resolveDataBinding();
    	ITypeBinding expressionType = fieldAccess.getPrimary().resolveTypeBinding();
        
        IDataBinding dataBinding = null;
        
    	if(expressionData == IBinding.NOT_FOUND_BINDING) {
    		dataBinding = null;
    	}
    	else if(expressionData != null && expressionData.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
    		dataBinding = ((StructureItemBinding) expressionData).findData(fieldAccess.getID());
    	}
    	else if(expressionType != null){
    		if(expressionType == IBinding.NOT_FOUND_BINDING) {
    			dataBinding = null;
    		}
    		else {
    			if(fieldAccess.getParent() instanceof FunctionInvocation && ITypeBinding.DICTIONARY_BINDING == expressionType.getKind()) {
    				IFunctionBinding findFunction = expressionType.findFunction(fieldAccess.getID());
    				if(IBinding.NOT_FOUND_BINDING != findFunction) {
    					dataBinding = new NestedFunctionBinding(InternUtil.internCaseSensitive(fieldAccess.getCanonicalString()), null, findFunction);
    				}
    			}
    			
    			if(dataBinding == null) {
	        		if(expressionType.isDynamicallyAccessible()) {
	        			dataBinding = new DynamicDataBinding(fieldAccess.getCaseSensitiveID(), null);
	        		}
	        		else {
	        			if(fieldAccess.getPrimary() instanceof ThisExpression) {
	        				//To get priveate as well as public fields
	        				dataBinding = expressionType.findData(fieldAccess.getID());
	        			}
	        			else {
	        				dataBinding = findData(expressionType, fieldAccess.getID());
	        			}
	        		}
    			}
    		}
    	}
        
        //if(dataBinding == null) dataBinding = IBinding.NOT_FOUND_BINDING;
        if(dataBinding == null) return;
        	
        if(dataBinding == IBinding.NOT_FOUND_BINDING) {
        	int endOffset = fieldAccess.getOffset() + fieldAccess.getLength();
        	problemRequestor.acceptProblem(
        		endOffset - fieldAccess.getID().length(),
        		endOffset,
				IProblemRequestor.VARIABLE_NOT_FOUND,
				new String[] {fieldAccess.getCanonicalString()});
        }
        else {
        	if(dataBinding.isDataBindingWithImplicitQualifier()) {
        		fieldAccess.setAttributeOnName(Name.IMPLICIT_QUALIFIER_DATA_BINDING, dataBinding.getImplicitQualifier());
        		dataBinding = dataBinding.getWrappedDataBinding();
        	}
        	fieldAccess.setDataBinding(dataBinding);
        	if(dataBinding != null && dataBinding != IBinding.NOT_FOUND_BINDING) {
	        	ITypeBinding type = dataBinding.getType();
				fieldAccess.setTypeBinding(type);
	        	
	        	if(dataBinding.getKind() == IDataBinding.AMBIGUOUS_BINDING) {
	        	    fieldAccess.setDataBinding(IBinding.NOT_FOUND_BINDING);
	        		problemRequestor.acceptProblem(
	        			fieldAccess,
						IProblemRequestor.VARIABLE_ACCESS_AMBIGUOUS,
						new String[] {fieldAccess.getCanonicalString()});
	        	}else if(type != null) {	        		
	        		dependencyRequestor.recordTypeBinding(type);		        	
	        	}
        	}
        }        
        
//        if(thisScope != null) {
//        	thisScope.setParentScope(lastParentOfThisScope);
//        	thisScope = null;
//        	lastParentOfThisScope = null;
//        }
        
        if (expressionType != null && expressionType != IBinding.NOT_FOUND_BINDING) {
            if (expressionType.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
            	if(ITypeBinding.FIXED_RECORD_BINDING != expressionType.getBaseType().getKind() &&
            	   fieldAccess.resolveTypeBinding() != null &&
				   !fieldAccess.resolveTypeBinding().isFunctionBinding()) {
            		if(dotOperatorAllowedForArrays) {
						ITypeBinding currentType = fieldAccess.resolveTypeBinding();
						if(Binding.isValidBinding(currentType)) {
							fieldAccess.setTypeBinding(ArrayTypeBinding.getInstance(currentType));
						}
					}
					else {
	            		problemRequestor.acceptProblem(
							fieldAccess.getPrimary(),
							IProblemRequestor.ARRAY_ACCESS_NOT_SUBSCRIPTED,
							new String[] {fieldAccess.getPrimary().getCanonicalString()});
					}
            	}
            }
        }
        
        new CheckNoDynamicAccessVisitor().check(fieldAccess.getPrimary(), fieldAccess);
        
        IDataBinding fieldAccessDB = fieldAccess.resolveDataBinding();
        if(fieldAccessDB != null && fieldAccessDB != IBinding.NOT_FOUND_BINDING) {
        	if(isMultiplyOccuringItem(fieldAccessDB)) {
        		int[] dimensions = getDimensions(fieldAccess);
				if(dimensions.length != 0) {
       				fieldAccess.setTypeBinding(new MultiplyOccuringItemTypeBinding(fieldAccessDB.getType()));
        		}
        	}
        }
    }
    
    private class CheckNoDynamicAccessVisitor extends AbstractASTVisitor {
    	boolean dynamicAccessUsed = false;
    	
    	void check(Expression expr, Node nodeToHangErrorOn) {
    		expr.accept(this);
    		if(dynamicAccessUsed) {
    			problemRequestor.acceptProblem(nodeToHangErrorOn, IProblemRequestor.DOT_ACCESS_USED_AFTER_DYNAMIC);
    		}
    	}
    	
    	public boolean visit(ArrayAccess arrayAccess) {
    		if(arrayAccess.getIndices().size() == 1) {
	    		ITypeBinding indexType = ((Expression) arrayAccess.getIndices().get(0)).resolveTypeBinding();
	    		if(indexType != null && indexType.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING
	    				             && Primitive.isStringType(((PrimitiveTypeBinding) indexType).getPrimitive())) {
	    			dynamicAccessUsed = true;
	    		}
    		}
			return true;
		}
    }
    
    public void endVisit(SimpleName simpleName) {
		endVisitName(simpleName);
		
		IDataBinding qualifier = (IDataBinding) simpleName.getAttribute(org.eclipse.edt.compiler.core.ast.Name.IMPLICIT_QUALIFIER_DATA_BINDING);
		if (Binding.isValidBinding(qualifier) && Binding.isValidBinding(qualifier.getType())) {
			ITypeBinding qualifierTypeBinding = qualifier.getType();
			
			if(ITypeBinding.ARRAY_TYPE_BINDING == qualifierTypeBinding.getKind()) {
				if(ITypeBinding.FIXED_RECORD_BINDING == qualifierTypeBinding.getBaseType().getKind()) {
					if(simpleName.resolveTypeBinding() == null ||
						!simpleName.resolveTypeBinding().isFunctionBinding()) {
						simpleName.setTypeBinding(new MultiplyOccuringItemTypeBinding(simpleName.resolveTypeBinding()));
					}
				}
			}

		}	
	}
    
    public void endVisit(QualifiedName qualifiedName) {
    	endVisitName(qualifiedName);
    	
//		IDataBinding qualifierDataBinding = qualifiedName.getQualifier().resolveDataBinding();		
//		if(qualifierDataBinding != null && qualifierDataBinding != IBinding.NOT_FOUND_BINDING) {
//			ITypeBinding qualifierTypeBinding = qualifierDataBinding.getType();
    		ITypeBinding qualifierTypeBinding = findArrayInQualifiers(qualifiedName);
			if(qualifierTypeBinding != null) {
				if(ITypeBinding.ARRAY_TYPE_BINDING == qualifierTypeBinding.getKind()) {
					if(ITypeBinding.FIXED_RECORD_BINDING == qualifierTypeBinding.getBaseType().getKind()) {
						if(qualifiedName.resolveTypeBinding() == null ||
							!qualifiedName.resolveTypeBinding().isFunctionBinding()) {
							qualifiedName.setTypeBinding(new MultiplyOccuringItemTypeBinding(qualifiedName.resolveTypeBinding()));
						}
					}
					else if(qualifiedName.resolveTypeBinding() != null &&
							!qualifiedName.resolveTypeBinding().isFunctionBinding()) {
						
						if(dotOperatorAllowedForArrays) {
							ITypeBinding currentType = qualifiedName.resolveTypeBinding();
							if(Binding.isValidBinding(currentType)) {
								qualifiedName.setTypeBinding(ArrayTypeBinding.getInstance(currentType));
							}
						}
						else {
							problemRequestor.acceptProblem(
								qualifiedName.getQualifier(),
								IProblemRequestor.ARRAY_ACCESS_NOT_SUBSCRIPTED,
								new String[] {qualifiedName.getQualifier().getCanonicalName()});
						}
					}
				}
			}
//		}	
	}
    
    private ITypeBinding findArrayInQualifiers(QualifiedName qName) {
    	Name qualifier = qName.getQualifier();
    	ITypeBinding qualifierType = qualifier.resolveTypeBinding();
    	if(Binding.isValidBinding(qualifierType) && ITypeBinding.ARRAY_TYPE_BINDING == qualifierType.getKind()) {
    		return qualifierType;
    	}
    	if(qualifier.isQualifiedName()) {
    		return findArrayInQualifiers((QualifiedName) qualifier);
    	}
    	return null;
    }
    
    private void endVisitName(Name name) {
    	IDataBinding dBinding = name.resolveDataBinding();
    	if(dBinding != null && dBinding != IBinding.NOT_FOUND_BINDING) {
    		if(isMultiplyOccuringItem(dBinding)) {
    			int[] dimensions = getDimensions(name);
				if(dimensions.length != 0) {
       				name.setTypeBinding(new MultiplyOccuringItemTypeBinding(dBinding.getType()));
        		}
    		}
    	}
    }
    
    private boolean isMultiplyOccuringItem(IDataBinding dBinding) {
    	switch(dBinding.getKind()) {
    		case IDataBinding.STRUCTURE_ITEM_BINDING:
    			return ((StructureItemBinding) dBinding).isMultiplyOccuring();
    		case IDataBinding.FORM_FIELD:
    			return ((FormFieldBinding) dBinding).isMultiplyOccuring();
    	}
    	return false;
    }
    
    private void bindNameFromSQLRecordArrayBindingsInIOObjects(SimpleName simpleName) {
    	for(Iterator iter = IOObjectsInCurrentStatement.iterator(); iter.hasNext();) {
    		IDataBinding nextDataBinding = ((Expression) iter.next()).resolveDataBinding();
    		if(nextDataBinding != null && nextDataBinding != IBinding.NOT_FOUND_BINDING) {
    			ITypeBinding nextTypeBinding = nextDataBinding.getType();
    			if(nextTypeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
    				ITypeBinding baseElementType = ((ArrayTypeBinding) nextTypeBinding).getBaseType();
    				if(baseElementType.getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord") != null) {    				
		    			IDataBinding dataBindingFromIOObject = (IDataBinding) baseElementType.getSimpleNamesToDataBindingsMap().get(simpleName.getIdentifier());
		    			if(dataBindingFromIOObject != null) {
		    				if(simpleName.resolveBinding() == null) {
		    					simpleName.setBinding(dataBindingFromIOObject);
		    					simpleName.setTypeBinding(dataBindingFromIOObject.getType());
		    					simpleName.setAttribute(Name.IMPLICIT_QUALIFIER_DATA_BINDING, nextDataBinding);
		    				}
		    				else {
		    					simpleName.setBinding(AmbiguousDataBinding.getInstance());
		    				}
		    			}
    				}
    			}
    		}
    	}
    }
    
    protected IDataBinding[] getPCBParams(){
		IAnnotationBinding dliBinding = getDLIAnnotation();
		if (dliBinding != null){
			IAnnotationBinding pcbparamsBinding = (IAnnotationBinding) dliBinding.findData(InternUtil.intern(IEGLConstants.PROPERTY_PCBPARMS));
			if (pcbparamsBinding != IBinding.NOT_FOUND_BINDING){
				IDataBinding[] dataBindings = (IDataBinding[])pcbparamsBinding.getValue();
				return dataBindings;
			}
		}

		return null;
    }
    
    protected IDataBinding getPSB(){
		IAnnotationBinding dliBinding = getDLIAnnotation();
		if (dliBinding != null){
			IAnnotationBinding psbBinding = (IAnnotationBinding) dliBinding.findData(InternUtil.intern(IEGLConstants.PROPERTY_PSB));
			if (psbBinding != IBinding.NOT_FOUND_BINDING){
				IDataBinding dataBindings = (IDataBinding)psbBinding.getValue();
				return dataBindings;
			}
		}

		return null;
    }
    
    protected IAnnotationBinding getDLIAnnotation() {
    	IPartBinding functionContainerBinding = currentScope.getPartBinding();
		if(functionContainerBinding != null ){
			return functionContainerBinding.getAnnotation(new String[] {"egl", "io", "dli"}, "DLI");
		}
		return null;
    }
    
    
	public boolean visit(UsingPCBClause usingPCBClause) {
		final Expression expr = usingPCBClause.getPCB();
		final AbstractASTVisitor thisobject = this;
		expr.accept(new AbstractASTVisitor(){
			boolean bFound = false;
			public boolean visit (QualifiedName qualifiedName){
				qualifiedName.getQualifier().accept(thisobject);
				IDataBinding qDataBinding = qualifiedName.getQualifier().resolveDataBinding(); 
				if (StatementValidator.isValidBinding(qDataBinding)){
					String name = qualifiedName.getIdentifier();
					IDataBinding dataBinding = 	getPSB();
					if (StatementValidator.isValidBinding(dataBinding) && qDataBinding == dataBinding){
						ITypeBinding typeBinding = dataBinding.getType();
						if (StatementValidator.isValidBinding(typeBinding)){
							if (typeBinding.getAnnotation(new String[] {"egl", "io", "dli"}, "PSBRecord")!= null){
								if (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING){
									List fields = ((FlexibleRecordBinding)typeBinding).getDeclaredFields();
									Iterator iter = fields.iterator();
									while(iter.hasNext()){
										FlexibleRecordFieldBinding field = (FlexibleRecordFieldBinding)iter.next();
										if (field.getName() == name){
											qualifiedName.setBinding(field);
											qualifiedName.setTypeBinding(field.getType());
											dependencyRequestor.recordTypeBinding(field.getType());
											bFound = true;
										}
									}
								}
							}
						}
					}
				}
				
				if (!bFound){
					qualifiedName.setBinding(IBinding.NOT_FOUND_BINDING);
					qualifiedName.setTypeBinding(IBinding.NOT_FOUND_BINDING);
					doError();
				}
				return false;
			}
			public boolean visit (SimpleName simpleName){
				final IDataBinding[] dataBindings = getPCBParams();
				if (dataBindings != null){
					for (int i = 0; i < dataBindings.length; i++){
						IDataBinding binding = dataBindings[i];
						if (StatementValidator.isValidBinding(binding)){
							if (InternUtil.intern(simpleName.getCanonicalString()) == binding.getName()){
								simpleName.setBinding(binding);
								bFound = true;
								break;
							}
						}
					}
				}
				if (!bFound){
					simpleName.setBinding(IBinding.NOT_FOUND_BINDING);
					doError();
				}

				return false;
			}
			
			public void doError(){			    
				IPartBinding functionContainerBinding = currentScope.getPartBinding();
				if (functionContainerBinding != null &&
				        (functionContainerBinding.getKind() == ITypeBinding.LIBRARY_BINDING ||
		                functionContainerBinding.getKind() == ITypeBinding.PROGRAM_BINDING)){
						problemRequestor.acceptProblem(expr,
								IProblemRequestor.USINGPCB_ITEM_NOT_IN_PROGRAM_PSB,
								new String[] {expr.getCanonicalString()});
					}
				}

		});
		return false;
	}

	
	public boolean visit(OpenStatement openStatement) {
		IOObjectsInCurrentStatement = openStatement.getIOObjects();
		return true;
	}
	
	public void endVisit(OpenStatement openStatement) {
		IOObjectsInCurrentStatement = Collections.EMPTY_LIST;
	}
	
	public boolean visit(GetByKeyStatement openStatement) {
		IOObjectsInCurrentStatement = openStatement.getIOObjects();
		return true;
	}
	
	public void endVisit(final GetByKeyStatement getByKeyStatement) {
		IOObjectsInCurrentStatement = Collections.EMPTY_LIST;
		validateIOStatementInDLIProgram(getByKeyStatement);
	}
	
	public void endVisit(final DeleteStatement getByKeyStatement) {
		IOObjectsInCurrentStatement = Collections.EMPTY_LIST;
		validateIOStatementInDLIProgram(getByKeyStatement);
	}
	
	public boolean visit(GetByPositionStatement openStatement) {
		IOObjectsInCurrentStatement = openStatement.getIOObjects();
		return true;
	}
	
	public void endVisit(final GetByPositionStatement openStatement) {
		IOObjectsInCurrentStatement = Collections.EMPTY_LIST;
		validateIOStatementInDLIProgram(openStatement);
	}
	
	public void endVisit(final AddStatement openStatement) {
		validateIOStatementInDLIProgram(openStatement);
	}
	
	public void endVisit(final ReplaceStatement openStatement) {
		validateIOStatementInDLIProgram(openStatement);
	}
	
	public boolean visit(ExecuteStatement openStatement) {
		IOObjectsInCurrentStatement = openStatement.getIOObjects();
		return true;
	}
	
	public void endVisit(ExecuteStatement openStatement) {
		IOObjectsInCurrentStatement = Collections.EMPTY_LIST;
	}
	
	public boolean visit(IntoClause intoClause) {
		visitingIntoClause = true;
		return true;		
	}
	
	public void endVisit(IntoClause intoClause) {
		visitingIntoClause = false;
	}
	
	public boolean visit(IsAExpression isAExpression) {
		isAExpression.getExpression().accept(this);
		try {
			Type typeAST = isAExpression.getType();
			ITypeBinding type = bindType(typeAST);
			checkTypeForIsaOrAs(typeAST, type);			
		}
		catch(ResolutionException e) {
			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
		}
		return false;
	}
	
	public boolean visit(AsExpression asExpression) {
		asExpression.getExpression().accept(this);
		try {
			if(asExpression.hasType()) {
				Type typeAST = asExpression.getType();
				ITypeBinding type = bindType(typeAST);
				checkTypeForIsaOrAs(typeAST, type);
			}
		}
		catch(ResolutionException e) {
			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
		}
		return false;
	}
	
	private void checkTypeForIsaOrAs(Type typeAST, ITypeBinding type) {
		if(type.isPartBinding() && !((IPartBinding) type).isDeclarablePart()) {
			problemRequestor.acceptProblem(
				typeAST,
				IProblemRequestor.ISA_TARGET_NOT_ITEM_OR_PART,
				new String[] {typeAST.getCanonicalName()});
		}
		else {
			Type baseType = typeAST.getBaseType();
			if(baseType.isPrimitiveType()) {
				PrimitiveTypeValidator.validate((PrimitiveType) baseType, problemRequestor, compilerOptions); 
			}
			
			if (typeAST.isNullableType()) {				
				problemRequestor.acceptProblem(
						typeAST,
						IProblemRequestor.NULLABLE_INVALID_IN_ISA_AS_OR_NEW);
			}
						
			Type tempTypeAST = typeAST;
			while(tempTypeAST.isArrayType()) {
				if(((ArrayType) tempTypeAST).hasInitialSize()) {
					problemRequestor.acceptProblem(
						((ArrayType) tempTypeAST).getInitialSize(),
						IProblemRequestor.ARRAY_SIZE_NOT_ALLOWED_IN_ISA_OR_AS);
				}
				
				tempTypeAST = ((ArrayType) tempTypeAST).getElementType();
				if (tempTypeAST.isNullableType()) {
					tempTypeAST = ((NullableType)tempTypeAST).getBaseType();
				}
			}
		}
	}
	
	public boolean visit(InExpression inExpression) {
		inExpression.getFirstExpression().accept(this);
		
		dotOperatorAllowedForArrays = true;
		inExpression.getSecondExpression().accept(this);
		dotOperatorAllowedForArrays = false;
		
		if(inExpression.hasFromExpression()) {
			inExpression.getFromExpression().accept(this);
		}
		return false;
	}
	
	public void endVisit(LikeMatchesExpression likeMatchesExpression) {
		likeMatchesExpression.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN));
		
		Expression operand1 = likeMatchesExpression.getFirstExpression();
		Expression operand2 = likeMatchesExpression.getSecondExpression();
		ITypeBinding type1 = operand1.resolveTypeBinding();
		ITypeBinding type2 = operand2.resolveTypeBinding();
		
		checkTypeForLikeMatchesOperand(type1, operand1);
		checkTypeForLikeMatchesOperand(type2, operand2);
		
		if(likeMatchesExpression.hasEscapeString()) {
			String escapeCharacter = likeMatchesExpression.getEscapeString();
			escapeCharacter = escapeCharacter.replaceFirst("^\\\"", "");
			escapeCharacter = escapeCharacter.replaceFirst("\\\"$", "");
			if(escapeCharacter.length() > 1 || escapeCharacter.getBytes().length > 1) {
				problemRequestor.acceptProblem(
					likeMatchesExpression,
					IProblemRequestor.COND_INVALID_ESCAPE_CHARACTER,
					new String[] {escapeCharacter});
			}
		}
	}
	
	private void checkTypeForLikeMatchesOperand(ITypeBinding type, Expression operandNode) {
		if(type != null) {
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING != type.getKind()) {
				problemRequestor.acceptProblem(
					operandNode,
					IProblemRequestor.COND_OPERAND_MUST_BE_STRING,
					new String[] {operandNode.getCanonicalString()});
			}
			else if(!type.isDynamic()) {
				Primitive prim = ((PrimitiveTypeBinding) type).getPrimitive();
				if(!Primitive.isStringType(prim)) {
					problemRequestor.acceptProblem(
						operandNode,
						IProblemRequestor.COND_OPERAND_MUST_BE_STRING,
						new String[] {operandNode.getCanonicalString()});
				}
				else {
					if(Primitive.HEX == prim || Primitive.DBCHAR == prim) {
						problemRequestor.acceptProblem(
							operandNode,
							IProblemRequestor.COND_OPERAND_CANNOT_BE_HEX_OR_DBCHAR,
							new String[] {operandNode.getCanonicalString()});
					}
				}
			}
		}
	}
	
	public boolean visit(NewExpression newExpression) {
		ITypeBinding type = null;
		try {
		    if (newExpression.resolveTypeBinding() == null) {
				type = convertDataItemToPrimitive(bindType(newExpression.getType()));
		        newExpression.setTypeBinding(type);	
		    }
		}
		catch(ResolutionException e) {
			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
			newExpression.setTypeBinding(IBinding.NOT_FOUND_BINDING);
		}
		
		if (newExpression.getType().isNullableType()) {
			problemRequestor.acceptProblem(
					newExpression.getType(),
					IProblemRequestor.NULLABLE_INVALID_IN_ISA_AS_OR_NEW);
		}
		
		if(newExpression.hasSettingsBlock()) {
		    type = newExpression.resolveTypeBinding();
		    if (type != IBinding.NOT_FOUND_BINDING) {
		        IDataBinding dataBinding = new LocalVariableBinding(type.getCaseSensitiveName(), null, type);
		        newExpression.setDataBindingForAnnotations(dataBinding);
		        Scope typeScope = new TypeBindingScope(NullScope.INSTANCE, type, null);
			    AnnotationLeftHandScope lhScope = new AnnotationLeftHandScope(typeScope, type, type, dataBinding, -1, currentBinding);
			    lhScope.setWithinNewExpression(true);
			    SettingsBlockAnnotationBindingsCompletor completor = new SettingsBlockAnnotationBindingsCompletor(currentScope, currentBinding, lhScope, dependencyRequestor, problemRequestor, compilerOptions);
				newExpression.getSettingsBlock().accept(completor); // bind the annotations
				for(Iterator iter = newExpression.getSettingsBlock().getSettings().iterator(); iter.hasNext();) {
					((Node) iter.next()).accept(this);		//bind the right hand side of the assignments that are not for annotations
				}
		    }
		}
		
		if(type != null && type != IBinding.NOT_FOUND_BINDING) {
			if(type.isReference()) {
				if(ITypeBinding.SERVICE_BINDING == type.getKind() ||
				   ITypeBinding.INTERFACE_BINDING == type.getKind()) {
					//Don't want to issue error for built-in interface types
					//like window, consoleField, etc...
					if(type != SystemPartManager.findType(type.getName())) {
						problemRequestor.acceptProblem(
							newExpression.getType(),
							IProblemRequestor.NEW_NOT_SUPPORTED_FOR_SERVICE_OR_INTERFACE);
					}
				}
			}
			else if(type.isPartBinding() && !((IPartBinding) type).isDeclarablePart()) {
				problemRequestor.acceptProblem(
					newExpression.getType(),
					IProblemRequestor.INVALID_NEW_OPERATION_ON_TYPE,
					new String[] {newExpression.getType().getCanonicalName()});
			}
		}
		
		if(newExpression.hasArgumentList()) {
			for(Iterator iter = newExpression.getArguments().iterator(); iter.hasNext();) {
				((Node) iter.next()).accept(this);
			}
		}
		
		return false;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		Node parent = settingsBlock.getParent();
		
		final boolean[] invalid = new boolean[1];
		parent.accept(new DefaultASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.IfStatement ifStatement) {
				invalid[0] = true;
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.ElseBlock elseBlock) {
				invalid[0] = true;
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.WhileStatement whileStatement) {
				invalid[0] = true;
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.WhenClause whenClause) {
				invalid[0] = true;
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.TryStatement tryStatement) {
				invalid[0] = true;
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.OnExceptionBlock onExceptionBlock) {
				invalid[0] = true;
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.ForStatement forStatement) {
				invalid[0] = true;
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement forEachStatement) {
				invalid[0] = true;
				return false;
			}			
		});
		if (invalid[0]) {
			problemRequestor.acceptProblem(
					settingsBlock,
					IProblemRequestor.SETTINGS_BLOCK_NOT_ALLOWED,
					new String[] {});

		}
		
		return super.visit(settingsBlock);
	}
	
	private ITypeBinding convertDataItemToPrimitive(ITypeBinding type) {
		if(type != null && IBinding.NOT_FOUND_BINDING != type) {
			switch(type.getKind()) {
			case ITypeBinding.DATAITEM_BINDING:
				return ((DataItemBinding) type).getPrimitiveTypeBinding();
			case ITypeBinding.ARRAY_TYPE_BINDING:
				return ArrayTypeBinding.getInstance(convertDataItemToPrimitive(((ArrayTypeBinding) type).getElementType()));
			}
		}
		return type;
	}

	public void endVisit(NewExpression newExpression) {
		ITypeBinding targetType = newExpression.getType().resolveTypeBinding();
		if(targetType != null) {
			if(typeIs(targetType, new String[] {"egl", "ui", "console"}, "Menu")) {
				final boolean[] hasLabelKeyOrLabelText = new boolean[] {false};
				if(newExpression.hasSettingsBlock()) {
					for(Iterator iter = newExpression.getSettingsBlock().getSettings().iterator(); !hasLabelKeyOrLabelText[0] && iter.hasNext();)
						((Node) iter.next()).accept(new DefaultASTVisitor() {
						public boolean visit(Assignment assignment) {
							assignment.getLeftHandSide().accept(new AbstractASTExpressionVisitor() {
								public boolean visitName(Name name) {
									String identifier = name.getIdentifier();
									if(InternUtil.intern("labelKey") == identifier ||
									   InternUtil.intern("labelText") == identifier) {
										hasLabelKeyOrLabelText[0] = true;
									}								
									return false;
								}
							});
							return false;
						}
					});
				}
				
				if(!hasLabelKeyOrLabelText[0]) {
					problemRequestor.acceptProblem(
						newExpression.getType(),
						IProblemRequestor.MENU_DECLARATION_REQUIRES_LABELKEY_OR_LABELTEXT,
						new String[] {"labelText"});
				}
			}
			
			if(newExpression.hasArgumentList()) {
				OverloadedFunctionSet constructors = getConstructors(targetType);				
				Type type = newExpression.getType();
				if(type.isNameType()) {
					((NameType) type).getName().setAttribute(Name.OVERLOADED_FUNCTION_SET, constructors);
				}
				
				FunctionResolver resolver = new FunctionResolver(compilerOptions);
				IDataBinding matchingConstructor = resolver.findMatchingFunction(constructors, getArgumentTypes(newExpression.getArguments()), false);
				
				if(IBinding.NOT_FOUND_BINDING == matchingConstructor || AmbiguousDataBinding.getInstance() == matchingConstructor) {
					if (newExpression.getArguments().size() > 0) {
						problemRequestor.acceptProblem(
							newExpression.getType(),
							IProblemRequestor.MATCHING_CONSTRUCTOR_CANNOT_BE_FOUND,
							new String[] {
								targetType.getCaseSensitiveName()
							});
						newExpression.setConstructorBinding(IBinding.NOT_FOUND_BINDING);
					}
				}
				else {
					newExpression.setConstructorBinding(matchingConstructor);
					
					if (Binding.isValidBinding(matchingConstructor) && ((ConstructorBinding)matchingConstructor).isPrivate()) {
						if (currentScope.getPartBinding() != ((ConstructorBinding)matchingConstructor).getDeclaringPart()) {
							problemRequestor.acceptProblem(type,
									IProblemRequestor.PRIVATE_CONSTRUCTOR,
								new String[] {type.getCanonicalName()});
						}
					}					
				}
			}
			else {
				ITypeBinding tBinding = newExpression.getType().resolveTypeBinding();
				if (Binding.isValidBinding(tBinding) && tBinding.isReference() && !tBinding.isInstantiable() && !tBinding.isNullable() && currentScope.getPartBinding() != tBinding) {
					problemRequestor.acceptProblem(newExpression.getType(),
							IProblemRequestor.TYPE_NOT_INSTANTIABLE_2,
						new String[] {newExpression.getType().getCanonicalName()});
					newExpression.setConstructorBinding(IBinding.NOT_FOUND_BINDING);
				}
				else {
					newExpression.setConstructorBinding(getDefaultConstructor(targetType));
				}
			}
		}
		
		if(newExpression.getType().isArrayType()) {
			
			//prevent the expression:  new any[10]
			//you cannot create an array of non-instantiable types
			ArrayType arrType = (ArrayType)newExpression.getType();
			if (arrType.hasInitialSize() && !isZeroLiteral(arrType.getInitialSize())) {
				ITypeBinding tBinding = arrType.getElementType().resolveTypeBinding();
				if (Binding.isValidBinding(tBinding) && Binding.isValidBinding(tBinding.getBaseType()) && tBinding.getBaseType().isReference() && !tBinding.getBaseType().isInstantiable() && !tBinding.getBaseType().isNullable() && currentScope.getPartBinding() != tBinding.getBaseType()) {
					problemRequestor.acceptProblem(arrType.getBaseType(),
							IProblemRequestor.TYPE_NOT_INSTANTIABLE_2,
						new String[] {tBinding.getBaseType().getCaseSensitiveName()});
				}
			}
	
			final boolean[] hasInitialSize = new boolean[1];
			newExpression.getType().accept(new AbstractASTVisitor() {
				public boolean visit(ArrayType arrayType) {
					if(arrayType.hasInitialSize()) {
						final boolean sizeIsValid[] = new boolean[] {false};
						arrayType.getInitialSize().accept(new DefaultASTVisitor() {
							public boolean visit(IntegerLiteral integerLiteral) {
								sizeIsValid[0] = true;
								return false;
							}
						});
						
						if (sizeIsValid[0]) {
							hasInitialSize[0] = true;
						}
						else {
							problemRequestor.acceptProblem(
								arrayType.getInitialSize(),
								IProblemRequestor.ARRAY_SIZE_LESS_THAN_ZERO,
								new String[] {arrayType.getInitialSize().getCanonicalString()});
						}
					}
					return true;
				}
			});
			
			if (hasInitialSize[0] && newExpression.hasSettingsBlock()) {
				//Disallow a new expression for an array that specifies an initial size and specifies entries in a settings block:
				//new int[5] {1,2,3}
				final Node[] errorNode = new Node[1];
				newExpression.getSettingsBlock().accept(new AbstractASTExpressionVisitor() {
		    		public boolean visit(Assignment assignment) {
		    			return false;
		    		}
		    		
		    		public boolean visit(AnnotationExpression annotationExpression) {
		    			return false;
		    		}
		    		
		    		public boolean visit(SetValuesExpression setValuesExpression) {
						return false; 			
		    		}
		    		
		    		public boolean visitExpression(Expression expression) {
		    			if (errorNode[0] != null) {
		    				return false;
		    			}
		    			errorNode[0] = expression;
		    			return false;
		    		}
		    		
		    	});
		    	if (errorNode[0] != null) {
		    		problemRequestor.acceptProblem(errorNode[0],IProblemRequestor.POSITIONAL_PROPERTY_NOT_ALLOWED_WITH_INITIAL_SIZE, IMarker.SEVERITY_ERROR, new String[] {});
		    	}
			}
		}
		
		if(newExpression.hasSettingsBlock()) {
			newExpression.getSettingsBlock().accept(new DefaultASTVisitor() {
				public boolean visit(SettingsBlock settingsBlock) {
					return true;
				}
				
				public boolean visit(Assignment assignment) {
					validateAssignment(assignment, true);
					return false;
				}
			});
		}
	}
	
	public static boolean isZeroLiteral(Expression expr) {
		
		final boolean[] isZero = new boolean[1];
		expr.accept(new DefaultASTVisitor(){
			public boolean visit(IntegerLiteral integerLiteral) {
				isZero[0] = Integer.valueOf(integerLiteral.getValue()) == 0;
				return false;
			}
		});
		return isZero[0];
	}
	
	private List getArgumentTypes(List functionArguments) {
		List list = new ArrayList();
		Iterator i = functionArguments.iterator();
		while (i.hasNext()) {
			list.add(((Expression) i.next()).resolveTypeBinding());
		}
		return list;
	}
	
	private boolean[] getArgumentIsLiteralArray(List arguments) {
		boolean[] result = new boolean[arguments.size()];
		for(int i = 0; i < arguments.size(); i++) {
			result[i] = arguments.get(i) instanceof LiteralExpression;
		}
		return result;
	}

	public static ConstructorBinding getDefaultConstructor(ITypeBinding targetType) {
		OverloadedFunctionSet set = getConstructors(targetType);
		Iterator i = set.getNestedFunctionBindings().iterator();
		while (i.hasNext()) {
			ConstructorBinding binding = (ConstructorBinding)i.next();
			if (binding.getParameters().size() == 0) {
				return binding;
			}
		}
		return null;
	}
	
	
	private static OverloadedFunctionSet getConstructors(ITypeBinding targetType) {
		OverloadedFunctionSet constructors = new OverloadedFunctionSet();
		if (!Binding.isValidBinding(targetType)) {
			return constructors;
		}
		constructors.setName(InternUtil.internCaseSensitive(IEGLConstants.KEYWORD_CONSTRUCTOR));
		if(ITypeBinding.EXTERNALTYPE_BINDING == targetType.getKind()) {
			for(Iterator iter = ((ExternalTypeBinding) targetType).getConstructors().iterator(); iter.hasNext();) {
				constructors.addNestedFunctionBinding((IDataBinding) iter.next());
			}
		}
		else {
			if(ITypeBinding.HANDLER_BINDING == targetType.getKind()) {
				for(Iterator iter = ((HandlerBinding) targetType).getConstructors().iterator(); iter.hasNext();) {
					constructors.addNestedFunctionBinding((IDataBinding) iter.next());
				}
			}
			else {
				if(ITypeBinding.DICTIONARY_BINDING == targetType.getKind()) {
					for(Iterator iter = ((DictionaryBinding) targetType).getConstructors().iterator(); iter.hasNext();) {
						constructors.addNestedFunctionBinding((IDataBinding) iter.next());   
					}
				}
			}
		}
		return constructors;		
	}

	public boolean visit(CloseStatement closeStatement) {
		//The target might be a resultSetID, so don't issue error
		IProblemRequestor pRequestor = problemRequestor;
		problemRequestor = NullProblemRequestor.getInstance();
		closeStatement.getExpr().accept(this);
		problemRequestor = pRequestor;		
		return false;
	}
	

	public boolean visit(final IsNotExpression isNotExpression) {
		
		problemRequestor.acceptProblem(
				isNotExpression,
				IProblemRequestor.IS_NOT_UNSUPPORTED,
				new String[] {});

		
		isNotExpression.getFirstExpression().accept(this);
		final IDataBinding leftExpressionDataBinding = isNotExpression.getFirstExpression().resolveDataBinding();
		
		isNotExpression.getSecondExpression().accept(new AbstractASTExpressionVisitor() {
			boolean stateIsValid = false;
			public boolean visit(SimpleName simpleName) {
				IDataBinding dBinding = IsNotStateBinding.toIsNotStateBinding(simpleName.getIdentifier());
				if(dBinding != null) {
					simpleName.setBinding(dBinding);
					stateIsValid = true;
				}
				else {
					simpleName.setBinding(IBinding.NOT_FOUND_BINDING);
				}
				return false;
			}
			public boolean visit(QualifiedName qualifiedName) {
				qualifiedName.setBinding(IBinding.NOT_FOUND_BINDING);
				return false;
			}
			public void endVisitExpression(Expression expression) {
				if(!stateIsValid) {
					//More specific error message when lhs is sysVar.systemType will be
					//issued later (in endVisit(IsNotExpression))
					if(dataBindingIs(leftExpressionDataBinding, new String[] {"egl", "core"}, "SysVar", "SYSTEMTYPE") && currentScope.getPartBinding() != null) {
					}
					else {
						problemRequestor.acceptProblem(
							expression,
							IProblemRequestor.INVALID_NONARITHMETIC_COND_RIGHT_SIDE,
							new String[] {expression.getCanonicalString()});
					}
				}
			}
		});
		return false;
	}
	
	public boolean visit(ReturningToNameClause returningToNameClause) {
		try {
			bindTypeName(returningToNameClause.getName());
		}
		catch(ResolutionException e) {
			//The returning to name is allowed to not resolve.
		}

		return false;	
	}
	
	
	public boolean visit(final ForwardStatement forwardStatement) {
		if (forwardStatement.hasForwardTarget()){
			final Expression expr = forwardStatement.getForwardTarget();
			if(forwardStatement.isForwardToURL()) {
				expr.accept(this);
			}
			else {
				final DefaultBinder thisDefaultBinder = this;
				expr.accept(new AbstractASTExpressionVisitor(){				
					public boolean visit(StringLiteral stringLiteral) {
						String pgmname = InternUtil.intern(stringLiteral.getValue());
						ITypeBinding result = currentScope.findType(pgmname);
				        if(result == IBinding.NOT_FOUND_BINDING) {
				        	expr.setTypeBinding(IBinding.NOT_FOUND_BINDING);
				        }
				        else if(result == ITypeBinding.AMBIGUOUS_TYPE){
				            expr.setTypeBinding(ITypeBinding.AMBIGUOUS_TYPE);
				        }
				        else {			        
				        	dependencyRequestor.recordTypeBinding(result);
				        }
				        expr.setTypeBinding(result);
						return false;
					}
					
					public boolean visitExpression(Expression expr) {
						if(expr.isName()) {
							try {
								bindExpressionName((Name) expr);
							} catch (ResolutionException e) {
								problemRequestor.acceptProblem(
									expr,
									IProblemRequestor.INVALID_FORWARD_TARGET);
								return false;
							}
						}
						else {
							expr.accept(thisDefaultBinder);
						}
						
						ITypeBinding tBinding = expr.resolveTypeBinding();
						if(Binding.isValidBinding(tBinding)) {
							if(!TypeCompatibilityUtil.isMoveCompatible(PrimitiveTypeBinding.getInstance(Primitive.STRING), tBinding, expr, compilerOptions)) {
								problemRequestor.acceptProblem(
									expr,
									IProblemRequestor.INVALID_FORWARD_TARGET);
							}
						}
						
						return false;
					}
				});
			}
		}
	
		for(Iterator iter = forwardStatement.getArguments().iterator(); iter.hasNext();) {
			((Node) iter.next()).accept(this);
		}
		for(Iterator iter = forwardStatement.getForwardOptions().iterator(); iter.hasNext();) {
			((Node) iter.next()).accept(this);
		}
		return false;
	}

	/*
	 * Type inference logic -- code which sets type binding for Expression ASTs
	 */
	
	public void endVisit(ParenthesizedExpression parenthesizedExpression) {
		parenthesizedExpression.setTypeBinding(parenthesizedExpression.getExpression().resolveTypeBinding());
	}
	
	public void endVisit(UnaryExpression unaryExpression) {
		Expression operand = unaryExpression.getExpression();
		ITypeBinding operandType = operand.resolveTypeBinding();
		
		if(operandType != null) {
			UnaryExpression.Operator operator = unaryExpression.getOperator();
			
			if(operator == UnaryExpression.Operator.PLUS || operator == UnaryExpression.Operator.MINUS) {
				//Numeric unary expression
				unaryExpression.setTypeBinding(inferTypeForNumericOperand(operand, problemRequestor));
			}
			else if(operator == UnaryExpression.Operator.NEGATE) {
				//Bitwise unary expression
				unaryExpression.setTypeBinding(inferTypeForBitwiseOperand(operand, problemRequestor));
			}
			else {
				//Boolean unary expression (operator is Operator.BANG)
				unaryExpression.setTypeBinding(inferTypeForBooleanOperand(operand, problemRequestor));
			}
		}
	}
	
	public void endVisit(BinaryExpression binaryExpression) {
		Expression operand1 = binaryExpression.getFirstExpression();
		Expression operand2 = binaryExpression.getSecondExpression();
		ITypeBinding type1 = operand1.resolveTypeBinding();
		ITypeBinding type2 = operand2.resolveTypeBinding();
		BinaryExpression.Operator operator = binaryExpression.getOperator();
		
		if(type1 != null) {
			if((operator == BinaryExpression.Operator.CONCAT) &&
				(ITypeBinding.ARRAY_TYPE_BINDING == type1.getKind() ||
				 type2 != null && ITypeBinding.ARRAY_TYPE_BINDING == type2.getKind())) {
				//Array concatentation
				if(ITypeBinding.ARRAY_TYPE_BINDING == type1.getKind()) {
					binaryExpression.setTypeBinding(type1);
				}
				else if(type2 != null && ITypeBinding.ARRAY_TYPE_BINDING == type2.getKind()) {
					binaryExpression.setTypeBinding(type2);
				}
			}
			else if ((isStringType(type1) || isStringType(type2)) &&
					(operator == BinaryExpression.Operator.PLUS || 
					   operator == BinaryExpression.Operator.CONCAT ||
					   operator == BinaryExpression.Operator.NULLCONCAT	)) {
				//this is string concatenation , so this is allowed
				binaryExpression.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.STRING));
			}
			else if(operator == BinaryExpression.Operator.PLUS && (isStringType(type1)) ||
			   operator == BinaryExpression.Operator.CONCAT ||
			   operator == BinaryExpression.Operator.NULLCONCAT) {
				//String concatenation expression
				if(type2 != null) {
					if(type2.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING && !type2.isDynamic()) {
						problemRequestor.acceptProblem(
							operand2,
							IProblemRequestor.ELEMENT_NOT_VALID_IN_EXPRESSION,
							new String[] {operand2.getCanonicalString()});
					}
					else if(!type2.isDynamic()) {
						if(	!(type1 instanceof PrimitiveTypeBinding) ||
							!(type2 instanceof PrimitiveTypeBinding) ||
							!TypeCompatibilityUtil.isMoveCompatible(
							(PrimitiveTypeBinding) type1,
							(PrimitiveTypeBinding) type2) &&
							Primitive.BOOLEAN != ((PrimitiveTypeBinding) type2).getPrimitive()) {
								problemRequestor.acceptProblem(
									binaryExpression,
									IProblemRequestor.EXPRESSIONS_INCOMPATIBLE,
									new String[] {operand1.getCanonicalString(), operand2.getCanonicalString()});
						}
					}
					if ((operator == BinaryExpression.Operator.CONCAT ||
								operator == BinaryExpression.Operator.NULLCONCAT)) {
						if (Binding.isValidBinding(type1) && !isStringType(type1)) {
							problemRequestor.acceptProblem(
									operand1,
									IProblemRequestor.ELEMENT_NOT_VALID_IN_EXPRESSION,
									new String[] {operand1.getCanonicalString()});
						}
					}
				}
				else {
					operand2.accept(new AbstractASTExpressionVisitor() {
						public boolean visit(ParenthesizedExpression parenthesizedExpression) {
							return true;
						}
						public void endVisit(SQLLiteral sQLLiteral) {
							problemRequestor.acceptProblem(
								sQLLiteral,
								IProblemRequestor.ELEMENT_NOT_VALID_IN_EXPRESSION,
								new String[] {sQLLiteral.getCanonicalString()});
						}
					});
				}
				
				int length1 = Binding.isValidBinding(type1) ? getFixedLength(type1) : -1;
				int length2 = Binding.isValidBinding(type2) ? getFixedLength(type2) : -1;
				if(length1 != -1 && length2 != -1) {
					binaryExpression.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.STRING, length1 + length2));
				}
				else {
					binaryExpression.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.STRING));
				}
			}
			else if(operator == BinaryExpression.Operator.DIVIDE ||
					operator == BinaryExpression.Operator.MINUS ||
					operator == BinaryExpression.Operator.MODULO ||
					operator == BinaryExpression.Operator.PLUS ||
					operator == BinaryExpression.Operator.TIMES ||
					operator == BinaryExpression.Operator.TIMESTIMES){
				ITypeBinding result = validateArithmeticOperation(operand1, operand2, operator, binaryExpression);
				if (result != null) {
					binaryExpression.setTypeBinding(result);
				}
			}
			else if(operator == BinaryExpression.Operator.BITAND ||
					operator == BinaryExpression.Operator.BITOR ||
					operator == BinaryExpression.Operator.XOR ||
					operator == BinaryExpression.Operator.LEFT_SHIFT ||
					operator == BinaryExpression.Operator.RIGHT_SHIFT_ARITHMETIC ||
					operator == BinaryExpression.Operator.RIGHT_SHIFT_LOGICAL) {
				type1 = inferTypeForBitwiseOperand(operand1, problemRequestor);
				type2 = type2 == null ? null : inferTypeForBitwiseOperand(operand2, problemRequestor);
				binaryExpression.setTypeBinding(type2 == null ? type1 : getWiderType(type1, type2));
			}
			else {
				//Boolean expression -- setting type is easy, afterwards need
				//                      to validate that operand types are good
				binaryExpression.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN));
				
				if(operator == BinaryExpression.Operator.AND ||
				   operator == BinaryExpression.Operator.OR) {
					inferTypeForBooleanOperand(operand1, problemRequestor);
					if(type2 != null) inferTypeForBooleanOperand(operand2, problemRequestor);
				}
				else {
					final boolean[] shouldContinue = new boolean[] {true};
					
					class NoNewOrSetValuesExpressionChecker extends AbstractASTExpressionVisitor {
						boolean isNewOrSetValuesExpression = false;
						
						public boolean visitExpression(Expression expression) {
							return false;
						}
						
						public boolean visit(NewExpression newExpression) {
							isNewOrSetValuesExpression = true;
							return false;
						}
						
						public boolean visit(SetValuesExpression setValuesExpression) {
							isNewOrSetValuesExpression = true;
							return false;
						}
						
						public void endVisitExpression(Expression expression) {
							if(isNewOrSetValuesExpression) {
								problemRequestor.acceptProblem(expression, IProblemRequestor.SET_VALUES_BLOCK_OR_CONSTRUCTOR_NOT_VALID_AS_CONDITIONAL_OPERAND);
								shouldContinue[0] = false;
							}
						}
					}
					
					operand1.accept(new NoNewOrSetValuesExpressionChecker());
					operand2.accept(new NoNewOrSetValuesExpressionChecker());
					
					if(!shouldContinue[0]) return;
					
					if((type1.isReference() && !type1.isDynamic() && !isUnparameterizedPrimitive(type1)|| NilBinding.INSTANCE == type1) ||
					   type2 != null && (type2.isReference() && !type2.isDynamic() && !isUnparameterizedPrimitive(type2) || NilBinding.INSTANCE == type2)) {
						if(operator != BinaryExpression.Operator.EQUALS &&
						   operator != BinaryExpression.Operator.NOT_EQUALS) {
							//do not show error for number or decimal
							if (!isNumberOrDecimal(type1) || !isNumberOrDecimal(type2)) {
								problemRequestor.acceptProblem(
									binaryExpression,
									IProblemRequestor.TYPE_INVALID_REF_TYPE_COMPARISON);
								return;
							}
						}
					}
					
					if(ITypeBinding.FIXED_RECORD_BINDING == type1.getKind() ||
					   ITypeBinding.FLEXIBLE_RECORD_BINDING == type1.getKind()) {
						if(NilBinding.INSTANCE != type2) {
							problemRequestor.acceptProblem(
								operand1, IProblemRequestor.FIXED_RECORDS_NOT_ALLOWED_IN_COMPARISONS);
							shouldContinue[0] = false;
						}
					}
					

					
					if(	operator == BinaryExpression.Operator.GREATER ||
							operator == BinaryExpression.Operator.GREATER_EQUALS ||							
						operator == BinaryExpression.Operator.LESS ||							
						operator == BinaryExpression.Operator.LESS_EQUALS) {
						if (ITypeBinding.PRIMITIVE_TYPE_BINDING == type1.getKind()&&
						   ((PrimitiveTypeBinding)type1).getPrimitive() == Primitive.BOOLEAN) {
							problemRequestor.acceptProblem(
									operand1,
									IProblemRequestor.TYPE_INCOMPATIBLE_ARITHMETIC_COMPARISON,
									new String[] {operand1.getCanonicalString(), operand2.getCanonicalString()});
							shouldContinue[0] = false;
						}
						else {
							if (Binding.isValidBinding(type2) &&
								ITypeBinding.PRIMITIVE_TYPE_BINDING == type2.getKind() &&
								((PrimitiveTypeBinding)type2).getPrimitive() == Primitive.BOOLEAN) {
								problemRequestor.acceptProblem(
										operand2,
										IProblemRequestor.TYPE_INCOMPATIBLE_ARITHMETIC_COMPARISON,
										new String[] {operand2.getCanonicalString(), operand1.getCanonicalString()});
								shouldContinue[0] = false;
							}
						}
					}
					
					
					
					
					if(!shouldContinue[0]) return;
					
					if(type1.getAnnotation(new String[] {"egl", "io", "dli"}, "PSBRecord") != null &&
					   type1 == type2) {
						problemRequestor.acceptProblem(
							operand1,
							IProblemRequestor.TYPE_INCOMPATIBLE_ARITHMETIC_COMPARISON,
							new String[] {operand1.getCanonicalString(), operand2.getCanonicalString()});
						return;
					}
					
					if(type2 != null) {
						if(!(TypeCompatibilityUtil.isMoveCompatible(type1, type2, operand2, compilerOptions) ||
					         TypeCompatibilityUtil.isMoveCompatible(type2, type1, operand1, compilerOptions))) {
							problemRequestor.acceptProblem(
								operand1,
								IProblemRequestor.TYPE_INCOMPATIBLE_ARITHMETIC_COMPARISON,
								new String[] {operand1.getCanonicalString(), operand2.getCanonicalString()});
							return;
						}
						else {
							if((type1.isDynamic() || type2.isDynamic()) &&
									operator != BinaryExpression.Operator.EQUALS &&
									operator != BinaryExpression.Operator.NOT_EQUALS) {
								problemRequestor.acceptProblem(
									operand1,
									IProblemRequestor.TYPE_INCOMPATIBLE_ARITHMETIC_COMPARISON,
									new String[] {operand1.getCanonicalString(), operand2.getCanonicalString()});
								return;
							}
							else {}
						}												
					}					
				}
			}
		}
	}
	
	public void endVisit(TernaryExpression ternaryExpression) {
		Expression firstExpr = ternaryExpression.getFirstExpr();
		ITypeBinding type1 = firstExpr.resolveTypeBinding();
		
		if (type1 != null && type1.isValid()) {
			boolean validCondition =
					PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN) == type1 ||
					PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN).getNullableInstance() == type1;
			if (!validCondition) {
				problemRequestor.acceptProblem(
					firstExpr,
					IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
					new String[] {
						StatementValidator.getShortTypeString(type1),
						IEGLConstants.KEYWORD_BOOLEAN,
						firstExpr.getCanonicalString()
					});
			}
		}
		
		// Set the type of the expression to ANY. We might want to make sure the types are compatible in the future.
		ternaryExpression.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.ANY));
	}
	
	private boolean isUnparameterizedPrimitive(ITypeBinding type) {
		if (Binding.isValidBinding(type) && type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING)  {
			return ((PrimitiveTypeBinding)type).isUnparemeterizedReference();
		}
		return false;
	}
	
	
	private boolean isNumberOrDecimal(ITypeBinding type) {
		if (Binding.isValidBinding(type)) {
			return 
				type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING && 
				(((PrimitiveTypeBinding)type).getPrimitive() == Primitive.NUMBER || 
						((PrimitiveTypeBinding)type).getPrimitive() == Primitive.DECIMAL);
		}
		return false;
		
	}
	
	private ITypeBinding validateArithmeticOperation(Expression operand1, Expression operand2, BinaryExpression.Operator operator, Node node) {	
		//Numeric expression				
		ITypeBinding type1 = inferTypeForNumericOperand(operand1, problemRequestor);
		ITypeBinding type2 = operand2.resolveTypeBinding();
		type2 = type2 == null ? null : inferTypeForNumericOperand(operand2, problemRequestor);
		ITypeBinding result = null;
		
		if(type1 != null && type2 != null) {
			if(isDateTimeType(type1) || isDateTimeType(type2)) {
				//Date/Time numeric expression
				Primitive inferredPrim = TypeCompatibilityUtil.getDateTimeArithmeticResult(type1, type2, operator);						
				
				if(inferredPrim == null) {
					problemRequestor.acceptProblem(
						node,
						IProblemRequestor.OPERANDS_NOT_VALID_WITH_OPERATOR,
						new String[] {operand1.getCanonicalString(), operand2.getCanonicalString(), operator.toString()});
				}
				else {
					ITypeBinding primTypeBinding;
					if(Primitive.SECONDSPAN_INTERVAL == inferredPrim) {
						primTypeBinding = PrimitiveTypeBinding.getInstance(inferredPrim, "ddmmssffffff");
					}
					else if(Primitive.MONTHSPAN_INTERVAL == inferredPrim) {
						primTypeBinding = PrimitiveTypeBinding.getInstance(inferredPrim, "yyyyMM");
					}
					else {
						primTypeBinding = PrimitiveTypeBinding.getInstance(inferredPrim);
					}
					result = primTypeBinding;
				}
			}
			else {
				//Numeric numeric expression
				if(!type1.isDynamic() && !type2.isDynamic()) {
					result = getWiderType(type1, type2);
					
					if(!TypeCompatibilityUtil.isMoveCompatible(
						(PrimitiveTypeBinding) type1,
						(PrimitiveTypeBinding) type2)) {
						problemRequestor.acceptProblem(
							node,
							IProblemRequestor.EXPRESSIONS_INCOMPATIBLE,
							new String[] {operand1.getCanonicalString(), operand2.getCanonicalString()});
					}
				}
				else {
					result = PrimitiveTypeBinding.getInstance(Primitive.ANY);
				}
			}
		}
		
		return result;
	}
	
	private int getFixedLength(ITypeBinding type1) {
		switch(type1.getKind()) {
			case ITypeBinding.FIXED_RECORD_BINDING:
				return ((FixedRecordBinding) type1).getSizeInBytes();
			case ITypeBinding.PRIMITIVE_TYPE_BINDING:
				int bytes = ((PrimitiveTypeBinding) type1).getBytes();
				return bytes == 0 ? -1 : bytes;
		}
		return -1;
	}

	private ITypeBinding getWiderType(ITypeBinding type1, ITypeBinding type2) {
		return TypeCompatibilityUtil.valueWideningDistance(type1, type2, compilerOptions) > 0 ? type2 : type1;
	}

	public void endVisit(Assignment assignment) {
		Expression rightHandSide = assignment.getRightHandSide();
		ITypeBinding rhType = rightHandSide.resolveTypeBinding();

		Expression leftHandSide = assignment.getLeftHandSide();
		ITypeBinding lhType = leftHandSide.resolveTypeBinding();
		
		if(Binding.isValidBinding(rhType) && Binding.isValidBinding(lhType)) {
			Operator operator = assignment.getOperator();
			if(Assignment.Operator.PLUS == operator) {
				//let this fall through to validateArithemeticOperation 
			}
			else if(Assignment.Operator.MINUS == operator ||			   
			   Assignment.Operator.TIMES== operator ||
			   Assignment.Operator.DIVIDE == operator ||
			   Assignment.Operator.MODULO == operator ||
			   Assignment.Operator.TIMESTIMES == operator) {
				//Handled later by calling validateArithmeticOperation
				//inferTypeForNumericOperand(rightHandSide, problemRequestor);
			}
			else if(Assignment.Operator.OR == operator ||
					Assignment.Operator.AND == operator ||
					Assignment.Operator.XOR == operator ||
					Assignment.Operator.LEFT_SHIFT == operator ||
					Assignment.Operator.RIGHT_SHIFT_ARITHMETIC == operator ||
					Assignment.Operator.RIGHT_SHIFT_LOGICAL == operator) {
				inferTypeForBitwiseOperand(leftHandSide, problemRequestor);
				inferTypeForBitwiseOperand(rightHandSide, problemRequestor);
			}
			else if (Assignment.Operator.CONCAT == operator) {
				
				if (!isStringType(lhType) && !isArrayType(lhType)) {
					problemRequestor.acceptProblem(
							leftHandSide,
							IProblemRequestor.ELEMENT_NOT_VALID_IN_EXPRESSION,
							new String[] {leftHandSide.getCanonicalString()});
				}
			}
			else if (Assignment.Operator.NULLCONCAT == operator) {
				
				if (!isStringType(lhType)) {
					problemRequestor.acceptProblem(
							leftHandSide,
							IProblemRequestor.ELEMENT_NOT_VALID_IN_EXPRESSION,
							new String[] {leftHandSide.getCanonicalString()});
				}
			}
					
			if(isArithmeticAssignment(assignment)){
				validateArithmeticOperation(assignment.getLeftHandSide(), assignment.getRightHandSide(), getBinaryOperator(operator), assignment);								
			}
		}		
		
	}
	
	public static boolean isArithmeticAssignment(Assignment assignment) {
		
		if (assignment == null) {
			return false;
		}
		
		Operator operator = assignment.getOperator();
		BinaryExpression.Operator binOperator = getBinaryOperator(operator);

		Expression rightHandSide = assignment.getRightHandSide();
		ITypeBinding rhType = rightHandSide.resolveTypeBinding();

		Expression leftHandSide = assignment.getLeftHandSide();
		ITypeBinding lhType = leftHandSide.resolveTypeBinding();
		
		
		//The next 2 if statements are to short circuit the arithmetic operator checking
		if((binOperator == BinaryExpression.Operator.CONCAT ) &&
				((Binding.isValidBinding(lhType) && ITypeBinding.ARRAY_TYPE_BINDING == lhType.getKind()) ||
				 (Binding.isValidBinding(rhType) && ITypeBinding.ARRAY_TYPE_BINDING == rhType.getKind()))) {
				return false;
		}
		
		if(binOperator == BinaryExpression.Operator.PLUS && (isStringType(lhType) || isStringType(rhType)) ||
				binOperator == BinaryExpression.Operator.CONCAT ||
				binOperator == BinaryExpression.Operator.NULLCONCAT) {
			return false;
			
		}
	
	
		if(operator == Operator.DIVIDE ||
				operator == Operator.MINUS ||
				operator == Operator.MODULO ||
				operator == Operator.PLUS ||
				operator == Operator.TIMES ||
				operator == Operator.TIMESTIMES){
			return true;								
		}
		return false;
	}		

	private static BinaryExpression.Operator getBinaryOperator(Operator operator) {
		if (operator == Assignment.Operator.AND) {
			return BinaryExpression.Operator.AND;
		}

		if (operator == Assignment.Operator.CONCAT) {
			return BinaryExpression.Operator.CONCAT;
		}
		
		if (operator == Assignment.Operator.DIVIDE) {
			return BinaryExpression.Operator.DIVIDE;
		}
		
		if (operator == Assignment.Operator.MINUS) {
			return BinaryExpression.Operator.MINUS;
		}
		
		if (operator == Assignment.Operator.MODULO) {
			return BinaryExpression.Operator.MODULO;
		}
		
		if (operator == Assignment.Operator.NULLCONCAT) {
			return BinaryExpression.Operator.NULLCONCAT;
		}
		
		if (operator == Assignment.Operator.OR) {
			return BinaryExpression.Operator.OR;
		}
		
		if (operator == Assignment.Operator.PLUS) {
			return BinaryExpression.Operator.PLUS;
		}

		if (operator == Assignment.Operator.TIMES) {
			return BinaryExpression.Operator.TIMES;
		}

		if (operator == Assignment.Operator.TIMESTIMES) {
			return BinaryExpression.Operator.TIMESTIMES;
		}

		if (operator == Assignment.Operator.XOR) {
			return BinaryExpression.Operator.XOR;
		}
		
		if (operator == Assignment.Operator.LEFT_SHIFT) {
			return BinaryExpression.Operator.LEFT_SHIFT;
		}
		
		if (operator == Assignment.Operator.RIGHT_SHIFT_ARITHMETIC) {
			return BinaryExpression.Operator.RIGHT_SHIFT_ARITHMETIC;
		}
		
		if (operator == Assignment.Operator.RIGHT_SHIFT_LOGICAL) {
			return BinaryExpression.Operator.RIGHT_SHIFT_LOGICAL;
		}
		
		return null;
		
	}
		
	private static class QualifierBindingGatherer extends DefaultASTVisitor {
		IDataBinding qualifierDBinding;
		ITypeBinding qualifierTBinding;
		
		private boolean returnTopLevelQualifier;
		
		public QualifierBindingGatherer(boolean returnTopLevelQualifier) {
			this.returnTopLevelQualifier = returnTopLevelQualifier;
		}
		
		public boolean visit(QualifiedName qualifiedName) {
			Expression expr = qualifiedName.getQualifier();
			qualifierDBinding = expr.resolveDataBinding();
			qualifierTBinding = expr.resolveTypeBinding();
			return returnTopLevelQualifier;
		}
		
		public boolean visit(ArrayAccess arrayAccess) {
			arrayAccess.getArray().accept(this);
			return false;
		}
	}
	
	public void endVisit(InExpression inExpression) {
		inExpression.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN));
		
		Expression operand1 = inExpression.getFirstExpression();
		Expression operand2 = inExpression.getSecondExpression();
		ITypeBinding type1 = operand1.resolveTypeBinding();
		ITypeBinding type2 = operand2.resolveTypeBinding();
		
		if(type1 != null && ITypeBinding.PRIMITIVE_TYPE_BINDING != type1.getKind()) {
			problemRequestor.acceptProblem(
				operand1,
				IProblemRequestor.IN_CONDITIONAL_LEFT_OPERAND_INVALID,
				new String[] {operand1.getCanonicalString()});
			return;
		}
		
		if(type1 != null && type2 != null) {
			boolean rightSideValidForInFrom = true;
			if(ITypeBinding.ARRAY_TYPE_BINDING == type2.getKind()) {
				type2 = ((ArrayTypeBinding) type2).getElementType();
			}
			else if(ITypeBinding.MULTIPLY_OCCURING_ITEM == type2.getKind()) {
				type2 = ((MultiplyOccuringItemTypeBinding) type2).getBaseType();
			}
			else {
				IDataBinding rightSideDBinding = operand2.resolveDataBinding();				
				if(Binding.isValidBinding(rightSideDBinding)) {
					if(IDataBinding.STRUCTURE_ITEM_BINDING == rightSideDBinding.getKind()) {
						if(ITypeBinding.DATATABLE_BINDING != rightSideDBinding.getDeclaringPart().getKind()) {
							QualifierBindingGatherer qb = new QualifierBindingGatherer(true);
							operand2.accept(qb);
							ITypeBinding qualifierType2 = qb.qualifierTBinding;
							
							if(Binding.isValidBinding(qualifierType2)) {
								rightSideValidForInFrom = ITypeBinding.ARRAY_TYPE_BINDING == qualifierType2.getKind() &&
								                 ITypeBinding.FIXED_RECORD_BINDING == ((ArrayTypeBinding) qualifierType2).getElementType().getKind();
							}
						}
					}
					else if(IDataBinding.FORM_FIELD == rightSideDBinding.getKind()) {
						rightSideValidForInFrom = rightSideDBinding instanceof VariableFormFieldBinding &&
						                 ((VariableFormFieldBinding) rightSideDBinding).isMultiplyOccuring();
					}
					else {
						rightSideValidForInFrom = false;
					}
				}
			}
			
			if(!rightSideValidForInFrom && inExpression.hasFromExpression()) {
				problemRequestor.acceptProblem(
						operand2,
						IProblemRequestor.IN_CONDITIONAL_RIGHT_OPERAND_INVALID,
						new String[] {operand2.getCanonicalString()});
			}
			else {
				if(ITypeBinding.PRIMITIVE_TYPE_BINDING != type2.getKind() || !TypeCompatibilityUtil.isMoveCompatible(type2, type1, null, compilerOptions)) {
					problemRequestor.acceptProblem(
						operand1,
						IProblemRequestor.TYPE_INCOMPATIBLE_ARITHMETIC_COMPARISON,
						new String[] { operand1.getCanonicalString(), operand2.getCanonicalString()});
				}
			}
		}
		
		if(inExpression.hasFromExpression()) {
			Expression fromExpression = inExpression.getFromExpression();
			ITypeBinding fromType = fromExpression.resolveTypeBinding();
			if(Binding.isValidBinding(fromType)) {
				boolean exprIsValid = false;
				
				if(ITypeBinding.PRIMITIVE_TYPE_BINDING == fromType.getKind()) {
					PrimitiveTypeBinding primTBinding = (PrimitiveTypeBinding) fromType;
					if(Primitive.isNumericType(primTBinding.getPrimitive()) &&
					   primTBinding.getDecimals() == 0) {
						exprIsValid = true;
					}
				}
				
				if(!exprIsValid) {
					problemRequestor.acceptProblem(
						fromExpression,
						IProblemRequestor.IN_FROM_EXPRESSION_NOT_INTEGER,
						new String[] {fromExpression.getCanonicalString()});
				}
			}
		}
	}
	
	
	/**
	 * @precondition operand.resolveTypeBinding() != null
	 */
	public static ITypeBinding inferTypeForNumericOperand(Expression operand, IProblemRequestor problemRequestor) {
		ITypeBinding operandType = operand.resolveTypeBinding();
		if(operandType.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
			PrimitiveTypeBinding operandPrimType = (PrimitiveTypeBinding) operandType;
			if(TypeCompatibilityUtil.isMoveCompatible(
				PrimitiveTypeBinding.getInstance(Primitive.INT),
				operandPrimType)) {
				return operandType;	
			}
			else {
				problemRequestor.acceptProblem(
					operand,
					IProblemRequestor.TYPE_NOT_VALID_IN_NUMERIC_EXPRESSION,
					new String[] {operandPrimType.getPrimitive().getName(), operand.getCanonicalString()});
			}
		}
		else {
			problemRequestor.acceptProblem(
				operand,
				IProblemRequestor.ELEMENT_NOT_VALID_IN_EXPRESSION,
				new String[] {operand.getCanonicalString()});
		}
		return null;
	}
	
	/**
	 * @precondition operand.resolveTypeBinding() != null
	 */
	public static ITypeBinding inferTypeForBooleanOperand(Expression operand, IProblemRequestor problemRequestor) {
		ITypeBinding operandType = operand.resolveTypeBinding();
		if(operandType.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
			Primitive operandPrim = ((PrimitiveTypeBinding) operandType).getPrimitive();
			if(operandPrim != Primitive.BOOLEAN ) {
				problemRequestor.acceptProblem(
					operand,
					IProblemRequestor.TYPE_NOT_VALID_IN_BOOLEAN_EXPRESSION,
					new String[] {operandPrim.getName(), operand.getCanonicalString()});
			}
		}
		else {
			problemRequestor.acceptProblem(
				operand,
				IProblemRequestor.ELEMENT_NOT_VALID_IN_EXPRESSION,
				new String[] {operand.getCanonicalString()});
		}
		return PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN);
	}
	
	/**
	 * @precondition operand.resolveTypeBinding() != null
	 */
	public static ITypeBinding inferTypeForBitwiseOperand(Expression operand, IProblemRequestor problemRequestor) {
		ITypeBinding operandType = operand.resolveTypeBinding();
		if(operandType.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
			PrimitiveTypeBinding operandPrimType = (PrimitiveTypeBinding) operandType;
			if(Primitive.INT == operandPrimType.getPrimitive()
					|| Primitive.SMALLINT == operandPrimType.getPrimitive()) {
				return PrimitiveTypeBinding.getInstance(Primitive.INT);	
			}
			else {
				problemRequestor.acceptProblem(
					operand,
					IProblemRequestor.TYPE_NOT_VALID_IN_BITWISE_EXPRESSION,
					new String[] {operandPrimType.toString(), operand.getCanonicalString()});
			}
		}
		else {
			problemRequestor.acceptProblem(
				operand,
				IProblemRequestor.ELEMENT_NOT_VALID_IN_EXPRESSION,
				new String[] {operand.getCanonicalString()});
		}
		return null;
	}
	
	private static Set invalidIsNotStatesForDLISegments = new HashSet();
    static {
    	invalidIsNotStatesForDLISegments.add(IsNotStateBinding.FILENOTFOUND);
    	invalidIsNotStatesForDLISegments.add(IsNotStateBinding.DEADLOCK);
    	invalidIsNotStatesForDLISegments.add(IsNotStateBinding.INVALIDFORMAT);
    	invalidIsNotStatesForDLISegments.add(IsNotStateBinding.FILENOTAVAILABLE);
    	invalidIsNotStatesForDLISegments.add(IsNotStateBinding.FULL);
    }
    
	public void endVisit(IsAExpression isAExpression) {
		isAExpression.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN));
	}
	
	private static List VALID_STRING_CASTS = Arrays.asList( new ForeignLanguageTypeBinding[] {
		ForeignLanguageTypeBinding.OBJIDJAVA, ForeignLanguageTypeBinding.JAVACHAR
    } );
    
    private static List VALID_NUMERIC_CASTS = Arrays.asList( new ForeignLanguageTypeBinding[] {
    	ForeignLanguageTypeBinding.JAVASHORT,  ForeignLanguageTypeBinding.JAVAINT,     ForeignLanguageTypeBinding.JAVALONG,       ForeignLanguageTypeBinding.JAVAFLOAT,
    	ForeignLanguageTypeBinding.JAVADOUBLE, ForeignLanguageTypeBinding.JAVABOOLEAN, ForeignLanguageTypeBinding.JAVABIGINTEGER, ForeignLanguageTypeBinding.JAVABIGDECIMAL,
		ForeignLanguageTypeBinding.JAVABYTE
    } );
	
	public void endVisit(final AsExpression asExpression) {
		if(asExpression.hasType()) {
			ITypeBinding fromType = asExpression.getExpression().resolveTypeBinding();
			ITypeBinding toType = asExpression.getType().resolveTypeBinding();
			if(fromType != null && fromType.isValid() && toType != null && toType.isValid()) {
				if(fromType.isDynamic() ||
				   fromType.getBaseType().isDynamic() && ITypeBinding.ARRAY_TYPE_BINDING == toType.getKind() ||
				   TypeCompatibilityUtil.typesOrElementTypesMoveCompatible(toType, fromType, compilerOptions) ||
				   TypeCompatibilityUtil.areCompatibleExceptions(fromType, toType, compilerOptions)) {
					asExpression.setTypeBinding(convertDataItemToPrimitive(toType));
				}
				else {
					problemRequestor.acceptProblem(
						asExpression,
						IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
						new String[] {
							StatementValidator.getTypeString(fromType),
							StatementValidator.getTypeString(toType),
							asExpression.getCanonicalString()
						});
				}
			}
		}
		else {
			asExpression.getExpression().accept(new AbstractASTExpressionVisitor() {
				public boolean visit(NullLiteral nilLiteral) {
					asExpression.getStringLiteral().setTypeBinding(ForeignLanguageTypeBinding.NULL);
					asExpression.setTypeBinding(ForeignLanguageTypeBinding.NULL);
					return false;
				}
				
				public boolean visitExpression(Expression expression) {
					ForeignLanguageTypeBinding foreignLanguageType = ForeignLanguageTypeBinding.getInstance(((StringLiteral) asExpression.getStringLiteral()).getValue());
					if(foreignLanguageType == null) {
						problemRequestor.acceptProblem(
							asExpression.getStringLiteral(),
							IProblemRequestor.JAVA_CAST_ARGUMENT_REQUIRED,
							new String[] {
								toCommaList(ForeignLanguageTypeBinding.getSupportedCastStrings())
							}
						);
					}
					else {
						asExpression.getStringLiteral().setTypeBinding(foreignLanguageType);
						asExpression.setTypeBinding(foreignLanguageType);
						
						ITypeBinding argType = asExpression.getExpression().resolveTypeBinding();
						if(argType != null && IBinding.NOT_FOUND_BINDING != argType) {
							boolean isString = false;
							boolean isNumeric = false;
							boolean isBoolean = false;
							if(ITypeBinding.PRIMITIVE_TYPE_BINDING == argType.getKind()) {
								Primitive prim = ((PrimitiveTypeBinding) argType).getPrimitive();
								isString = Primitive.isStringType(prim);
								isNumeric = Primitive.isNumericType(prim);
								isBoolean = Primitive.BOOLEAN == prim;
							}
							
							if(isBoolean && ForeignLanguageTypeBinding.JAVABOOLEAN == foreignLanguageType) {	    					
							}
							else if( VALID_STRING_CASTS.contains(foreignLanguageType) && !isString ){
								problemRequestor.acceptProblem(
						    		asExpression.getExpression(),
									IProblemRequestor.JAVA_CAST_CHARACTER_TYPE_NEEDED,
									new String[] { foreignLanguageType.getCaseSensitiveName(), foreignLanguageType.getCaseSensitiveName() });
								asExpression.setTypeBinding(null);
							}
							else if( VALID_NUMERIC_CASTS.contains(foreignLanguageType) && !isNumeric ) {
								problemRequestor.acceptProblem(
									asExpression.getExpression(),
									IProblemRequestor.JAVA_CAST_NUMERIC_TYPE_NEEDED,
									new String[] { foreignLanguageType.getCaseSensitiveName(), foreignLanguageType.getCaseSensitiveName() });
								asExpression.setTypeBinding(null);
							}
						}
					}
					return false;
				}

				private String toCommaList(Collection supportedCastStrings) {
					StringBuffer sb = new StringBuffer();
					for(Iterator iter = supportedCastStrings.iterator(); iter.hasNext();) {
						sb.append((String) iter.next());
						if(iter.hasNext()) {
							sb.append(", ");
						}
					}
					return sb.toString();
				}
			});
		}
	}
	
	public void endVisit(final IsNotExpression isNotExpression) {
		isNotExpression.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN));
		
		isNotExpression.getSecondExpression().accept(new AbstractASTExpressionVisitor() {
			public boolean visit(SimpleName simpleName) {
				final boolean[] shouldContinue = new boolean[] {true};
				final IBinding binding = simpleName.resolveBinding();
				
				isNotExpression.getFirstExpression().accept(new AbstractASTExpressionVisitor() {
					public void endVisitName(Name name) {
						IBinding nameBinding = name.resolveBinding();
						if(nameBinding != IBinding.NOT_FOUND_BINDING &&
						   nameBinding.isDataBinding()) {
							IDataBinding dataBinding = (IDataBinding) nameBinding;
							if(dataBindingIs(dataBinding, new String[] {"egl", "core"}, "SysVar", "SYSTEMTYPE")) {
								if(binding == IBinding.NOT_FOUND_BINDING ||
								   ((IsNotStateBinding) binding).getIsNotStateKind() != IsNotStateBinding.SYSTEM_TYPE) {
									shouldContinue[0] = false;
									problemRequestor.acceptProblem(
										isNotExpression.getSecondExpression(),
										IProblemRequestor.INVALID_SYSTEM_TYPE_VALUE,
										new String[] {isNotExpression.getSecondExpression().getCanonicalString()});
								}
							}
							else if(dataBindingIs(dataBinding, new String[] {"egl", "ui", "text"}, "ConverseVar", "EVENTKEY")) {
								if(binding == IBinding.NOT_FOUND_BINDING ||
								   ((IsNotStateBinding) binding).getIsNotStateKind() != IsNotStateBinding.EVENT_KEY) {
									shouldContinue[0] = false;
									problemRequestor.acceptProblem(
										isNotExpression.getSecondExpression(),
										IProblemRequestor.INVALID_EVENT_KEY_VALUE,
										new String[] {isNotExpression.getSecondExpression().getCanonicalString()});
								}
							}
						}
					}
				});
				
				if(!shouldContinue[0]) return false;
				
				if(binding != IBinding.NOT_FOUND_BINDING) {
					final IsNotStateBinding stateBinding = (IsNotStateBinding) binding;
					
					isNotExpression.getFirstExpression().accept(new AbstractASTExpressionVisitor() {
						public boolean visit(ParenthesizedExpression parenthesizedExpression) {
							return true;
						}
						public void endVisit(FunctionInvocation functionInvocation) {
							shouldContinue[0] = false;
							problemRequestor.acceptProblem(
								functionInvocation,
								IProblemRequestor.FUNCTION_INVOCATION_USED_IN_ISNOT_WITHOUT_NULL);
						}
					});
					
					if(!shouldContinue[0]) return false;
					
					if(invalidIsNotStatesForDLISegments.contains(stateBinding)) {
						isNotExpression.getFirstExpression().accept(new AbstractASTExpressionVisitor() {
							public boolean visit(ParenthesizedExpression parenthesizedExpression) {
								return true;
							}
							public void endVisitName(Name name) {
								IBinding binding = name.resolveBinding();
								if(binding != IBinding.NOT_FOUND_BINDING) {
									if(binding.getAnnotation(new String[] {"egl", "io", "dli"}, "DLISegment") != null) {
										shouldContinue[0] = false;
										problemRequestor.acceptProblem(
											isNotExpression.getSecondExpression(),
											IProblemRequestor.ISNOT_STATE_NOT_VALID_FOR_DLISEGMENT,
											new String[] {stateBinding.getCaseSensitiveName()});
									}
								}
							}
						});
					}
					
					if(!shouldContinue[0]) return false;
					
					isNotExpression.getFirstExpression().accept(new AbstractASTExpressionVisitor() {
						public boolean visit(ParenthesizedExpression parenthesizedExpression) {
							return true;
						}
						public void endVisit(SimpleName simpleName) {}
						public void endVisit(QualifiedName qualifiedName) {}
						public void endVisit(ArrayAccess arrayAccess) {}
						public void endVisit(FieldAccess fieldAccess) {}
						public void endVisit(SubstringAccess substringAccess) {}
						public void endVisit(FunctionInvocation functionInvocation) {}
						public void endVisit(ParenthesizedExpression parenthesizedExpression) {}
						
						public void endVisitExpression(Expression expression) {
							shouldContinue[0] = false;
							problemRequestor.acceptProblem(
								expression,
								IProblemRequestor.INVALID_EXPRESSION_IN_ISNOT);
						}
					});
					
					if(!shouldContinue[0]) return false;
					
					switch(stateBinding.getIsNotStateKind()) {
						case IsNotStateBinding.EVENT_KEY :
							isNotExpression.getFirstExpression().accept(new SystemVariableOnlyChecker(new String[] {"egl", "ui", "text"}, "ConverseVar", "EVENTKEY", problemRequestor, stateBinding, IProblemRequestor.INVALID_EVENT_KEY_USE));
							break;
						case IsNotStateBinding.SYSTEM_TYPE :
							isNotExpression.getFirstExpression().accept(new SystemVariableOnlyChecker(new String[] {"egl", "core"}, "SysVar", "SYSTEMTYPE", problemRequestor, stateBinding, IProblemRequestor.INVALID_SYSTEM_TYPE_USE));
							break;
						case IsNotStateBinding.IO_ERROR :
							isNotExpression.getFirstExpression().accept(new IsNotBindingChecker(problemRequestor, IProblemRequestor.COND_OPERAND_MUST_BE_RECORD) {
								boolean isValid(IBinding binding) {
									if(binding.isDataBinding()) {
										binding = ((IDataBinding) binding).getType();
									}
									if(binding.isTypeBinding()) {
										ITypeBinding typeBinding = (ITypeBinding) binding;
										if(ITypeBinding.ARRAY_TYPE_BINDING == typeBinding.getKind()) {
											ITypeBinding elementType = ((ArrayTypeBinding) typeBinding).getElementType(); 
											if(elementType.getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord") != null ||
												elementType.getAnnotation(new String[] {"egl", "io", "file"}, "CSVRecord") != null ||
											   elementType.getAnnotation(new String[] {"egl", "io", "dli"}, "DLISegment") != null) {
												return true;
											}
											return false;
										}
									}
									if(binding.getAnnotation(new String[] {"egl", "io", "file"}, "SerialRecord") != null ||
									   binding.getAnnotation(new String[] {"egl", "io", "file"}, "RelativeRecord") != null ||
									   binding.getAnnotation(new String[] {"egl", "io", "mq"}, "MQRecord") != null ||
							           binding.getAnnotation(new String[] {"egl", "io", "file"}, "IndexedRecord") != null ||
							           binding.getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord") != null ||
							           binding.getAnnotation(new String[] {"egl", "io", "file"}, "CSVRecord") != null ||
							           binding.getAnnotation(new String[] {"egl", "io", "dli"}, "DLISegment") != null) {
										return true;
									}																																					
									return false;
								}
							});
							break;
						case IsNotStateBinding.TEXTFIELD :
							isNotExpression.getFirstExpression().accept(new IsNotBindingChecker(problemRequestor, IProblemRequestor.COND_OPERAND_MUST_BE_TEXTFORM_FIELD) {
								boolean isValid(IBinding binding) {
									if(binding.isDataBinding()) {
										IDataBinding dataBinding = (IDataBinding) binding;
										if(IDataBinding.FUNCTION_PARAMETER_BINDING == dataBinding.getKind()) {
											return ((FunctionParameterBinding) dataBinding).isField();
										}
										if(IDataBinding.FORM_FIELD == dataBinding.getKind()) {
											return dataBinding.getDeclaringPart().getAnnotation(new String[] {"egl", "ui", "text"}, "TextForm") != null;
										}
									}
									return false;
								}
							});
							break;
						case IsNotStateBinding.TEXTFIELD_OR_STRING :
							isNotExpression.getFirstExpression().accept(new IsNotBindingChecker(problemRequestor, IProblemRequestor.INVALID_TYPE_BLANKS) {
								boolean isValid(IBinding binding) {
									if(binding.isDataBinding()) {
										IDataBinding dataBinding = (IDataBinding) binding;
										if(IDataBinding.FUNCTION_PARAMETER_BINDING == dataBinding.getKind()) {
											if(((FunctionParameterBinding) dataBinding).isField()) {
												return true;
											}
										}
										if(IDataBinding.FORM_FIELD == dataBinding.getKind()) {
											return dataBinding.getDeclaringPart().getAnnotation(new String[] {"egl", "ui", "text"}, "TextForm") != null;
										}
										if(IDataBinding.LOCAL_VARIABLE_BINDING == dataBinding.getKind() ||
										   IDataBinding.CLASS_FIELD_BINDING == dataBinding.getKind()) {
											if(((VariableBinding) dataBinding).isConstant()) {
												return false;
											}
										}
										binding = dataBinding.getType();
									}
									if(binding.isTypeBinding()) {
										ITypeBinding typeBinding = (ITypeBinding) binding;
										return ITypeBinding.PRIMITIVE_TYPE_BINDING == typeBinding.getKind() &&
									           Primitive.isStringType(((PrimitiveTypeBinding) typeBinding).getPrimitive());
									}
									return false;
								}
							});
							break;
						case IsNotStateBinding.VALID_FOR_NUMERIC:
							isNotExpression.getFirstExpression().accept(new IsNotBindingChecker(problemRequestor, IProblemRequestor.INVALID_TYPE_ISNUMERIC) {
								boolean isValid(IBinding binding) {
									if(binding.isDataBinding()) {
										IDataBinding dataBinding = (IDataBinding) binding;
										if(IDataBinding.LOCAL_VARIABLE_BINDING == dataBinding.getKind() ||
										   IDataBinding.CLASS_FIELD_BINDING == dataBinding.getKind()) {
											if(((VariableBinding) dataBinding).isConstant()) {
												return false;
											}
										}
										binding = dataBinding.getType();
									}
									if(binding.isTypeBinding()) {
										ITypeBinding typeBinding = (ITypeBinding) binding;
										if(ITypeBinding.PRIMITIVE_TYPE_BINDING == typeBinding.getKind()) {
											Primitive prim = ((PrimitiveTypeBinding) typeBinding).getPrimitive();
											return prim == Primitive.CHAR	|| prim == Primitive.MBCHAR ||
											       prim == Primitive.STRING	|| prim == Primitive.UNICODE;
										}
									}
									return false;
								}
							});
							break;
						case IsNotStateBinding.MODIFIED_KIND :
							isNotExpression.getFirstExpression().accept(new IsNotBindingChecker(problemRequestor, IProblemRequestor.COND_OPERAND_INVALID_MODIFIED) {
								boolean isValid(IBinding binding) {
									if(binding.isDataBinding()) {
										IDataBinding dataBinding = (IDataBinding) binding;
										if(IDataBinding.FUNCTION_PARAMETER_BINDING == dataBinding.getKind()) {
											return ((FunctionParameterBinding) dataBinding).isField();
										}
										if(IDataBinding.FORM_FIELD == dataBinding.getKind()) {
											return dataBinding.getDeclaringPart().getAnnotation(new String[] {"egl", "ui", "text"}, "TextForm") != null;
										}
										if(IDataBinding.STRUCTURE_ITEM_BINDING == dataBinding.getKind()) {
											return dataBinding.getDeclaringPart().getAnnotation(new String[] {"egl", "ui", "webTransaction"}, "VGUIRecord") != null;
										}
										binding = dataBinding.getType();										
									}
									if(binding.isTypeBinding()) {
										ITypeBinding typeBinding = (ITypeBinding) binding;
										if(typeBinding.getAnnotation(new String[] {"egl", "ui", "text"}, "TextForm") != null ||
										   typeBinding.getAnnotation(new String[] {"egl", "ui", "webTransaction"}, "VGUIRecord") != null) {
											return true;
										}										
									}
									return false;
								}
							});
							break;
						case IsNotStateBinding.TRUNC_KIND :
							final boolean[] isValid = new boolean[] {true};
							isNotExpression.getFirstExpression().accept(new DefaultASTVisitor() {
								public boolean visit(ParenthesizedExpression parenthesizedExpression) {
									return true;
								}
								
								public boolean visit(SubstringAccess substringAccess) {
									problemRequestor.acceptProblem(
										substringAccess,
										IProblemRequestor.SUBSTRING_USED_IN_ISNOT_WITH_NULL_OR_TRUNC);
									isValid[0] = false;
									return false;
								}
							});
							if(isValid[0]) {
								isNotExpression.getFirstExpression().accept(new IsNotBindingChecker(problemRequestor, IProblemRequestor.TRUNC_OPERAND_INVALID_MODIFIED) {
									boolean isValid(IBinding binding) {
										if(binding.isDataBinding()) {
											IDataBinding dataBinding = (IDataBinding) binding;
											if(IDataBinding.STRUCTURE_ITEM_BINDING == dataBinding.getKind() ||
											   IDataBinding.FLEXIBLE_RECORD_FIELD == dataBinding.getKind()) {
												return dataBinding.getDeclaringPart().getAnnotation(new String[] {"egl", "io", "sql"}, "SQLRecord") != null;
											}
											if(IDataBinding.FUNCTION_PARAMETER_BINDING == dataBinding.getKind()) {
												return ((FunctionParameterBinding) dataBinding).isSQLNullable();
											}
										}										
										return false;
									}
								});							
							}
							break;
					}
				}
				
				return false;
			}
		});
	}
	
	private static class SystemVariableOnlyChecker extends AbstractASTExpressionVisitor {
		String[] systemLibraryPackage;
		String systemLibraryName;
		String systemVariableName;		
		IProblemRequestor problemRequestor;
		IsNotStateBinding stateBinding;
		int problemKind;
		
		SystemVariableOnlyChecker(String[] systemLibraryPackage, String systemLibraryName, String systemVariableName, IProblemRequestor problemRequestor, IsNotStateBinding stateBinding, int problemKind) {
			this.systemLibraryPackage = InternUtil.intern(systemLibraryPackage);
			this.systemLibraryName = InternUtil.intern(systemLibraryName);
			this.systemVariableName = InternUtil.intern(systemVariableName);
			this.problemRequestor = problemRequestor;
			this.stateBinding = stateBinding;
			this.problemKind = problemKind;
		}
		
		public boolean visit(ParenthesizedExpression parenthesizedExpression) {
			return true;
		}
		public void endVisit(FunctionInvocation functionInvocation) {
			// Error already issued...
		}
		public void endVisitName(Name name) {
			IBinding binding = name.resolveBinding();
			boolean isValid = false;
			if(binding == IBinding.NOT_FOUND_BINDING) {
				//error already issued
				isValid = true;
			}
			else if(binding.isDataBinding()) {
			   	IPartBinding declaringPart = ((IDataBinding) binding).getDeclaringPart();
			   	if(declaringPart != null) {
			   		isValid = declaringPart.getPackageName() == systemLibraryPackage &&
			   		          declaringPart.getName() == systemLibraryName &&
			   		          binding.getName() == systemVariableName;
			   	}
			}
			
			if(!isValid) {
				endVisitExpression(name);
			}
		}
		public void endVisitExpression(Expression expression) {
			problemRequestor.acceptProblem(
				expression,
				problemKind,
				new String[] {
					stateBinding.getName().toLowerCase(),
					stateBinding.getName().toLowerCase()
				}
			);
		}					
	}
	
	private abstract static class IsNotBindingChecker extends AbstractASTExpressionVisitor {		
		IProblemRequestor problemRequestor;
		int problemKind;
		boolean bindingIsValid = false;
		
		IsNotBindingChecker(IProblemRequestor problemRequestor, int problemKind) {
			this.problemRequestor = problemRequestor;
			this.problemKind = problemKind;
		}
		
		public boolean visit(ParenthesizedExpression parenthesizedExpression) {
			return true;
		}
		
		public boolean visitExpression(Expression expression) {
			if(expression.resolveTypeBinding() != null) {
				bindingIsValid = isValid(expression.resolveTypeBinding());
			}
			return false;
		}

		public boolean visitName(Name name) {
			IBinding binding = name.resolveBinding();
			if(binding == IBinding.NOT_FOUND_BINDING) {
				bindingIsValid = true;
			}
			else {
				bindingIsValid = isValid(binding);
			}
			return false;
		}
		
		public boolean visit(ArrayAccess arrayAccess) {
			IBinding binding = arrayAccess.resolveDataBinding();
			if(binding == null || binding == IBinding.NOT_FOUND_BINDING) {
				bindingIsValid = true;
			}
			else {
				IDataBinding dBinding = (IDataBinding) binding;
				ITypeBinding tBinding = dBinding.getType();
				if(tBinding != null && ITypeBinding.ARRAY_TYPE_BINDING == tBinding.getKind()) {
					binding = arrayAccess.resolveTypeBinding();
				}
				if(binding != null) {
					bindingIsValid = isValid(binding);
				}
			}
			return false;
		}
		
		public void endVisitExpression(Expression expression) {
			if(!bindingIsValid) {
				problemRequestor.acceptProblem(expression, problemKind, new String[] {expression.getCanonicalString()});
			}
		}
		
		abstract boolean isValid(IBinding binding);
	}
	
	private static boolean isStringType(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		return type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING &&
		       Primitive.isStringType(((PrimitiveTypeBinding) type).getPrimitive());
	}

	private static boolean isArrayType(ITypeBinding type) {
		return type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING;
	}

	private static boolean isDateTimeType(ITypeBinding type) {
		return type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING &&
		       Primitive.isDateTimeType(((PrimitiveTypeBinding) type).getPrimitive());
	}
	
	public void endVisit(IntegerLiteral integerLiteral) {
		if(integerLiteral.resolveTypeBinding() == null){
			integerLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.INT));
			
			String strVal = integerLiteral.getValue();
			if(strVal.length() > 32) {
				problemRequestor.acceptProblem(
					integerLiteral,
					IProblemRequestor.INTEGER_LITERAL_OUT_OF_RANGE,
					new String[] {strVal});
			}
		}
	}
	
	public void endVisit(DecimalLiteral decimalLiteral) {
		if(decimalLiteral.resolveTypeBinding() == null){
			decimalLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(
					Primitive.DECIMAL,
					getLengthFromLiteral(decimalLiteral),
					getDecimalsFromLiteral(decimalLiteral)));	
			
			String strVal = decimalLiteral.getValue();
			
			if( strVal.length() > (strVal.indexOf( '.' ) == -1 ? 32 : 33) ) {
				problemRequestor.acceptProblem(
					decimalLiteral,
					IProblemRequestor.DECIMAL_LITERAL_OUT_OF_RANGE,
					new String[] {strVal});
			}
		}
	}
	
	public void endVisit(FloatLiteral floatLiteral) {
		if(floatLiteral.resolveTypeBinding() == null){
			try {
				floatLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(
						Primitive.FLOAT));
				
				String strVal = floatLiteral.getValue();
				
				if(Double.isInfinite(Double.parseDouble(strVal))) {
					problemRequestor.acceptProblem(
						floatLiteral,
						IProblemRequestor.FLOATING_POINT_LITERAL_OUT_OF_RANGE,
						new String[] { strVal });
				}
			}
			catch( NumberFormatException e ) {
				// should be syntax error, so ignore			
			}
		}
	}
	
	public void endVisit(BooleanLiteral booleanLiteral) {
		booleanLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN));
	}
	
	public void endVisit(StringLiteral stringLiteral) {
		if(stringLiteral.resolveTypeBinding() == null){
			String value = stringLiteral.getValue();
			
			boolean textLiteralDefaultIsString = true;
			Primitive prim = null;
			int length = value.length();
			
			IPartBinding functionContainerBinding = currentScope.getPartBinding();
			if(functionContainerBinding != null ){
				IAnnotationBinding aBinding = functionContainerBinding.getAnnotation(new String[] {"egl", "core"}, "TextLiteralDefaultIsString");
				if(aBinding != null) {
					textLiteralDefaultIsString = Boolean.YES == aBinding.getValue();
				}
			}
			
			if(textLiteralDefaultIsString) {
				prim = Primitive.STRING;
			}
			else {
				byte[] bytes = value.getBytes();				
				
				if (bytes.length == length) {
					prim = Primitive.CHAR;
				}
				else if (bytes.length == length * 2) {
					prim = Primitive.DBCHARLIT;
				}
				else {
					prim = Primitive.MBCHAR;
				}
			}
			
			stringLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(prim, length));
			
			if(stringLiteral.isHex()) {
				if(stringLiteral.getValue().length() % 4 != 0) {
					problemRequestor.acceptProblem(stringLiteral, IProblemRequestor.HEX_LITERAL_LENGTH_MUST_BE_MULTIPLE_OF_FOUR, new String[] {stringLiteral.getValue()});
				}
			}
		}
	}
	
	public void endVisit(HexLiteral hexLiteral) {
		if(hexLiteral.resolveTypeBinding() == null) {
			hexLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.HEX, hexLiteral.getValue().length()));
			
			if(hexLiteral.getValue().length() % 2 != 0) {
				problemRequestor.acceptProblem(hexLiteral, IProblemRequestor.HEX_LITERAL_LENGTH_MUST_BE_EVEN, new String[] {hexLiteral.getValue()});
			}
		}
	}
	
	public void endVisit(CharLiteral charLiteral) {
		if(charLiteral.resolveTypeBinding() == null) {
			charLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.CHAR, charLiteral.getValue().length()));
			
			if(charLiteral.isHex()) {
				if(charLiteral.getValue().length() % 2 != 0) {
					problemRequestor.acceptProblem(charLiteral, IProblemRequestor.HEX_LITERAL_LENGTH_MUST_BE_EVEN, new String[] {charLiteral.getValue()});
				}
			}
		}
	}
	
	public void endVisit(DBCharLiteral dbcharLiteral) {
		if(dbcharLiteral.resolveTypeBinding() == null) {
			dbcharLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.DBCHARLIT, dbcharLiteral.getValue().length()));
			
			if(dbcharLiteral.isHex()) {
				if(dbcharLiteral.getValue().length() % 4 != 0) {
					problemRequestor.acceptProblem(dbcharLiteral, IProblemRequestor.HEX_LITERAL_LENGTH_MUST_BE_MULTIPLE_OF_FOUR, new String[] {dbcharLiteral.getValue()});
				}
			}
		}
	}
	
	public void endVisit(MBCharLiteral mbcharLiteral) {
		if(mbcharLiteral.resolveTypeBinding() == null) {
			mbcharLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.MBCHAR, mbcharLiteral.getValue().length()));
			
			if(mbcharLiteral.isHex()) {
				if(mbcharLiteral.getValue().length() % 2 != 0) {
					problemRequestor.acceptProblem(mbcharLiteral, IProblemRequestor.HEX_LITERAL_LENGTH_MUST_BE_EVEN, new String[] {mbcharLiteral.getValue()});
				}
			}
		}
	}
	
	public void endVisit(SQLLiteral sqlLiteral) {
		sqlLiteral.setTypeBinding(SystemPartManager.SQLSTRING_BINDING);
	}
	
	public void endVisit(TypeLiteralExpression typeLiteralExpression) {
		typeLiteralExpression.setTypeBinding(SystemPartManager.TYPEREF_BINDING);
	}
	
	private static int getLengthFromLiteral(LiteralExpression literalExpr) {
		return literalExpr.getValue().length();
	}
	
	private static int getDecimalsFromLiteral(LiteralExpression literalExpr) {
		if (literalExpr.getLiteralKind() == LiteralExpression.DECIMAL_LITERAL) {			
			return getLengthFromLiteral(literalExpr) - literalExpr.getValue().indexOf('.') + 1;
		}
		else if (literalExpr.getLiteralKind() == LiteralExpression.FLOAT_LITERAL) {			
			String literal = literalExpr.getValue();
			int length = literal.indexOf('e') +1;
			if ( length < 1 ) { // must be upper case E then
				length = literal.indexOf('E') +1;
			}
			//really length - 1 to discount the E 
			length = length - 1;
			return length - (literal.indexOf('.') + 1);
		}
		return 0;
	}
	
	protected boolean canMixTypesInArrayLiterals() {
		return true;
	}
	
	public void endVisit(ObjectExpression objExpr) {
		if (objExpr.resolveTypeBinding() == null) {
			objExpr.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.ANY));
		}
	}
	
	public void endVisit(ArrayLiteral arrayLiteral) {
		if(arrayLiteral.resolveTypeBinding() == null){
			List expressions = arrayLiteral.getExpressions();
			if(expressions.isEmpty()) {
				arrayLiteral.setTypeBinding(ArrayTypeBinding.getNonReferenceInstance(PrimitiveTypeBinding.getInstance(Primitive.ANY)));
			}
			else {
				Expression firstElementExpr = (Expression) expressions.get(0);
				ITypeBinding firstElementType = stripPrimitiveLength((firstElementExpr).resolveTypeBinding());
				if(firstElementType instanceof AnnotationTypeBindingImpl) {
					firstElementType = ((AnnotationTypeBindingImpl) firstElementType).getAnnotationRecord();
				}
				if(firstElementType == null && firstElementExpr instanceof Name && IBinding.NOT_FOUND_BINDING == ((Name) firstElementExpr).resolveBinding())  {
					return;
				}
				if(firstElementType != null && firstElementType != IBinding.NOT_FOUND_BINDING) {
					boolean allTypesSame = true;
					Iterator iter = expressions.iterator();
					iter.next();
					while(iter.hasNext() && allTypesSame) {
						Expression nextExpr = (Expression) iter.next();
						ITypeBinding nextType = stripPrimitiveLength((nextExpr).resolveTypeBinding());
						if(nextType instanceof AnnotationTypeBindingImpl) {
							nextType = ((AnnotationTypeBindingImpl) nextType).getAnnotationRecord();
						}
						if(nextType != null && IBinding.NOT_FOUND_BINDING != nextType) {
							if(nextType != firstElementType) {
								ITypeBinding commonExternalTypeSuperType = getCommonExtendedTypeSupertype(firstElementType, nextType, compilerOptions);
								if(commonExternalTypeSuperType != null) {
									firstElementType = commonExternalTypeSuperType;
								}
								else {
									allTypesSame = false;
									
									if(!TypeCompatibilityUtil.isMoveCompatible(firstElementType, nextType, nextExpr, compilerOptions)) {
										if(!canMixTypesInArrayLiterals()) {
											problemRequestor.acceptProblem(
												nextExpr,
												IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
												new String[] {
													StatementValidator.getShortTypeString(firstElementType),
													StatementValidator.getShortTypeString(nextType),
													"[" + firstElementExpr.getCanonicalString() + ", ..., " + nextExpr.getCanonicalString() + "]"
												}
											);
										}
									}
								}
							}
						}
					}
					if(allTypesSame) {
						arrayLiteral.setTypeBinding(ArrayTypeBinding.getNonReferenceInstance(firstElementType));
					}
				}
			}
			
			if(arrayLiteral.resolveTypeBinding() == null){
				arrayLiteral.setTypeBinding(ArrayTypeBinding.getNonReferenceInstance(PrimitiveTypeBinding.getInstance(Primitive.ANY)));
			}
		}
	}

	private ITypeBinding getCommonExtendedTypeSupertype (ITypeBinding type1, ITypeBinding type2, ICompilerOptions compilerOptions) {
		if(ITypeBinding.EXTERNALTYPE_BINDING == type1.getKind() && ITypeBinding.EXTERNALTYPE_BINDING == type2.getKind()) {
			List extendedTypes1 = ((ExternalTypeBinding) type1).getExtendedTypes();
			List extendedTypes2 = ((ExternalTypeBinding) type2).getExtendedTypes();
			if(extendedTypes1.contains(type2)) {
				return type2;
			}
			if(extendedTypes2.contains(type1)) {
				return type1;
			}
			for(Iterator iter = extendedTypes1.iterator(); iter.hasNext();) {
				ITypeBinding next = (ITypeBinding) iter.next();
				if(extendedTypes2.contains(next)) {
					return next;
				}
			}
		}

		if(ITypeBinding.INTERFACE_BINDING == type1.getKind() && ITypeBinding.INTERFACE_BINDING == type2.getKind()) {
			List extendedTypes1 = ((InterfaceBinding) type1).getExtendedTypes();
			List extendedTypes2 = ((InterfaceBinding) type2).getExtendedTypes();
			if(extendedTypes1.contains(type2)) {
				return type2;
			}
			if(extendedTypes2.contains(type1)) {
				return type1;
			}
			for(Iterator iter = extendedTypes1.iterator(); iter.hasNext();) {
				ITypeBinding next = (ITypeBinding) iter.next();
				if(extendedTypes2.contains(next)) {
					return next;
				}
			}
		}

		return null;
	}

	private ITypeBinding stripPrimitiveLength(ITypeBinding binding) {
		if(binding == null || binding == IBinding.NOT_FOUND_BINDING) {
			return binding;
		}
		switch(binding.getKind()) {
		case ITypeBinding.ARRAY_TYPE_BINDING:
			return ArrayTypeBinding.getInstance(stripPrimitiveLength(((ArrayTypeBinding) binding).getElementType()));
		case ITypeBinding.PRIMITIVE_TYPE_BINDING:
			return PrimitiveTypeBinding.getInstance(((PrimitiveTypeBinding) binding).getPrimitive());
		}
		return binding;
	}

	public void endVisit(NullLiteral nilLiteral) {
		nilLiteral.setTypeBinding(NilBinding.INSTANCE);
	}
	
	public void endVisit(AnnotationExpression annotationExpression) {
		annotationExpression.setTypeBinding(annotationExpression.getName().resolveTypeBinding());
	}
	
    protected static class SetValuesExpressionCompletor extends DefaultBinder {

        TypeBindingScope leftHandScope;

        public SetValuesExpressionCompletor(Scope currentScope, IPartBinding currentBinding, TypeBindingScope leftHandScope,
                IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
            super(currentScope, currentBinding, dependencyRequestor, problemRequestor, compilerOptions);
            this.leftHandScope = leftHandScope;
        }

        public boolean visit(SettingsBlock settingsBlock) {
            return true;
        }

        public boolean visit(Assignment assignment) {
            Scope saved = currentScope;
            currentScope = leftHandScope;
            assignment.getLeftHandSide().accept(this);
            currentScope = saved;
            assignment.getRightHandSide().accept(this);
            return false;
        }

        public boolean visit(SetValuesExpression setValuesExpression) {
            Scope saved = currentScope;
            currentScope = leftHandScope;
            setValuesExpression.getExpression().accept(this);
            currentScope = saved;
            final ITypeBinding typeBinding = setValuesExpression.getExpression().resolveTypeBinding();
            if (Binding.isValidBinding(typeBinding)) {
                TypeBindingScope newScope = new TypeBindingScope(NullScope.INSTANCE, typeBinding, setValuesExpression.getExpression()
                        .resolveDataBinding());
                setValuesExpression.getSettingsBlock().accept(
                        new SetValuesExpressionCompletor(currentScope, currentBinding, newScope, dependencyRequestor, problemRequestor, compilerOptions));
            } else {
            	bindNamesToNotFound(setValuesExpression.getSettingsBlock());
            }
            return false;
        }

        public boolean visit(ThisExpression thisExpression) {
            thisExpression.setTypeBinding(leftHandScope.getTypeBinding());
            return false;
        }
    }
	
	public boolean visit(SetValuesExpression setValuesExpression) {
		if(setValuesExpression.getExpression().resolveDataBinding() == IBinding.NOT_FOUND_BINDING) {
			//To prevent cascading errors, stop traversing if this is a complex annotation
			//that is not applicable.
			return false;
		}
		
        setValuesExpression.getExpression().accept(this);
        final ITypeBinding typeBinding = setValuesExpression.getExpression().resolveTypeBinding();
        if (typeBinding != null) {
            TypeBindingScope newScope = new TypeBindingScope(NullScope.INSTANCE, typeBinding, setValuesExpression.getExpression()
                    .resolveDataBinding());
            setValuesExpression.getSettingsBlock().accept(
                    new SetValuesExpressionCompletor(currentScope, currentBinding, newScope, dependencyRequestor, problemRequestor, compilerOptions));
        } else if (setValuesExpression.getExpression().resolveDataBinding() != IBinding.NOT_FOUND_BINDING) {
            setValuesExpression.getSettingsBlock().accept(this);
        }
        
		return true;
	}
	
	// Will be null if not visiting a function invocation statement
	FunctionInvocation functionInvocationFromFunctionInvocationStatement;
	
	public boolean visit(FunctionInvocationStatement functionInvocationStatement) {
		functionInvocationFromFunctionInvocationStatement = functionInvocationStatement.getFunctionInvocation();
		return true;
	}
	
	public void endVisit(FunctionInvocationStatement functionInvocationStatement) {
		functionInvocationFromFunctionInvocationStatement = null;
	}
	
	public void endVisit(FunctionInvocation functionInvocation) {
		IDataBinding fInvocationDBinding = functionInvocation.getTarget().resolveDataBinding();
		if(IBinding.NOT_FOUND_BINDING != fInvocationDBinding && fInvocationDBinding != null &&
		   IDataBinding.OVERLOADED_FUNCTION_SET_BINDING == fInvocationDBinding.getKind()) {

			FunctionResolver resolver = new FunctionResolver(compilerOptions);			
			OverloadedFunctionSet overloadedFunctionSet = (OverloadedFunctionSet) fInvocationDBinding;
			boolean isConstructor = functionInvocation.getTarget() instanceof ThisExpression;
			IDataBinding matchingFunction = resolver.findMatchingFunction(overloadedFunctionSet, getArgumentTypes(functionInvocation.getArguments()), getArgumentIsLiteralArray(functionInvocation.getArguments()), !isConstructor);
			
			if (isConstructor) {
				if(IBinding.NOT_FOUND_BINDING == matchingFunction || AmbiguousDataBinding.getInstance() == matchingFunction) {
					problemRequestor.acceptProblem(
						functionInvocation.getTarget(),
						IProblemRequestor.MATCHING_CONSTRUCTOR_CANNOT_BE_FOUND,
						new String[] {
							functionInvocation.getTarget().resolveTypeBinding().getCaseSensitiveName()
						});
	        		functionInvocation.getTarget().setDataBinding(IBinding.NOT_FOUND_BINDING);
	        		return;
				}
			}
			
			functionInvocation.getTarget().setAttributeOnName(Name.OVERLOADED_FUNCTION_SET, overloadedFunctionSet);
        	
        	if(AmbiguousDataBinding.getInstance() == matchingFunction) {
        		/*
        		 * If the type for at least one argument is null, an error message has already been issued.
        		 * Let the user take care of that one before putting out an error for multiple matching
        		 * overloaded functions. 
        		 */
        		boolean hasNullArgument = false;
        		for(Iterator iter = functionInvocation.getArguments().iterator(); iter.hasNext() && !hasNullArgument;) {
        			if(((Expression) iter.next()).resolveTypeBinding() == null) {
        				hasNullArgument = true;
        			}
        		}
        		if(!hasNullArgument) {
	        		problemRequestor.acceptProblem(
	        			functionInvocation.getTarget(),
	        			IProblemRequestor.MULTIPLE_OVERLOADED_FUNCTIONS_MATCH_ARGUMENTS,
	        			new String[] {
	        				fInvocationDBinding.getCaseSensitiveName()
	        			});
        		}
        		if(functionInvocation.getTarget().isName()) {
        			((Name) functionInvocation.getTarget()).setBinding(IBinding.NOT_FOUND_BINDING);
        		}
        		functionInvocation.getTarget().setDataBinding(IBinding.NOT_FOUND_BINDING);
        		functionInvocation.getTarget().setTypeBinding(null);
        	}
        	else {
        		if(functionInvocation.getTarget().isName()) {
        			((Name) functionInvocation.getTarget()).setBinding(matchingFunction);
        		}
        		functionInvocation.getTarget().setDataBinding(matchingFunction);
        		functionInvocation.getTarget().setTypeBinding(matchingFunction.getType());
        	}
        }
		
		ITypeBinding binding = functionInvocation.getTarget().resolveTypeBinding();
		if(binding != IBinding.NOT_FOUND_BINDING &&
		   binding != null &&
		   binding != AmbiguousFunctionBinding.getInstance() &&
		   (binding.isFunctionBinding() || ITypeBinding.DELEGATE_BINDING == binding.getKind())) {
			FunctionBinding functionBinding = binding.isFunctionBinding() ?
				(FunctionBinding) binding :
				createFunctionBindingFromDelegate((DelegateBinding) binding);
			ITypeBinding returnType = functionBinding.getReturnType();
			if(returnType == null) {
				// Error - function must return a type (unless this function
				// invocation is the invocation for a function invocation
				// statement, in which case it does not have to return a type)
				if(functionInvocation != functionInvocationFromFunctionInvocationStatement) {
					problemRequestor.acceptProblem(
						functionInvocation,
						IProblemRequestor.FUNCTION_MUST_RETURN_TYPE,
						new String[] {functionBinding.getName()});
				}
			}
			else {
				functionInvocation.setTypeBinding(returnType);
				dependencyRequestor.recordTypeBinding(returnType);
			}
			
			IPartBinding functionContainerBinding = currentScope.getPartBinding();
			
			checkSystemFunctionUsedInCorrectLocation(functionBinding, functionInvocation, functionContainerBinding);				
			
			functionInvocation.accept(new FunctionArgumentValidator(functionBinding, functionContainerBinding, problemRequestor, compilerOptions));
		}
	}
	
	private FunctionBinding createFunctionBindingFromDelegate(DelegateBinding binding) {
		FunctionBinding result = new FunctionBinding(binding.getCaseSensitiveName(), binding);
		result.setPrivate(binding.isPrivate());
		result.setReturnType(binding.getReturnType());
		result.setReturnTypeIsSqlNullable(binding.returnTypeIsSqlNullable());
		for(Iterator iter = binding.getParemeters().iterator(); iter.hasNext();) {
			result.addParameter((FunctionParameterBinding) iter.next());
		}
		return result;
	}
	
	private static final Set systemFunctionsOnlyAllowedInPageHandlers = new HashSet();
	static {
		systemFunctionsOnlyAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "java"}, "J2EELib", "CLEARREQUESTATTR"));
		systemFunctionsOnlyAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "java"}, "J2EELib", "CLEARSESSIONATTR"));
		systemFunctionsOnlyAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "java"}, "J2EELib", "GETREQUESTATTR"));
		systemFunctionsOnlyAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "java"}, "J2EELib", "GETSESSIONATTR"));
		systemFunctionsOnlyAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "java"}, "J2EELib", "SETREQUESTATTR"));
		systemFunctionsOnlyAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "java"}, "J2EELib", "SETSESSIONATTR"));
	}
	
	private static final Set systemFunctionsNotAllowedInPageHandlers = new HashSet();
	static {
		systemFunctionsNotAllowedInPageHandlers.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "ui", "console"}, "ConsoleLib", "DISPLAYLINEMODE"));
	}
	
	private static final Set systemFunctionsOnlyAllowedInProgramsWithDLIAnnotation = new HashSet();
	static {
		systemFunctionsOnlyAllowedInProgramsWithDLIAnnotation.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "io", "dli"}, "DLILib", "AIBTDLI"));
		systemFunctionsOnlyAllowedInProgramsWithDLIAnnotation.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "io", "dli"}, "DLILib", "EGLTDLI"));
		systemFunctionsOnlyAllowedInProgramsWithDLIAnnotation.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "vg"}, "VGLib", "VGTDLI"));
	}
	
	private static final Set systemFunctionsNotAllowedInServices = new HashSet();
	static {
		systemFunctionsNotAllowedInServices.add(new FunctionArgumentValidator.FunctionIdentifier(new String[] {"egl", "core"}, "SysLib", "setError"));		
	}
	
	private void checkSystemFunctionUsedInCorrectLocation(IFunctionBinding functionBinding, FunctionInvocation functionInvocation, IPartBinding functionContainerBinding) {
		FunctionArgumentValidator.FunctionIdentifier fIdentifier = new FunctionArgumentValidator.FunctionIdentifier(functionBinding);
		if(functionContainerBinding == null || functionContainerBinding.getAnnotation(new String[] {"egl", "ui", "jsf"}, "JSFHandler") == null) {
			if (AbstractBinder.typeIs(functionBinding.getDeclarer(),new String[] {"egl", "java"}, "PortalLib"))	{
				problemRequestor.acceptProblem(
						functionInvocation.getTarget(),
						IProblemRequestor.INVALID_PAGEHANDLER_SYSTEM_FUNCTION_USAGE,
						new String[] {functionBinding.getCaseSensitiveName()});
				return;
			}
		}
		
		if(systemFunctionsNotAllowedInPageHandlers.contains(fIdentifier)) {
			if(functionContainerBinding != null && functionContainerBinding.getAnnotation(new String[] {"egl", "ui", "jsf"}, "JSFHandler") != null) {
				problemRequestor.acceptProblem(
					functionInvocation.getTarget(),
					IProblemRequestor.SYSTEM_FUNCTION_NOT_ALLOWED_IN_PAGEHANDLER,
					new String[] {functionBinding.getCaseSensitiveName()});
			}
		}
		else if(systemFunctionsOnlyAllowedInProgramsWithDLIAnnotation.contains(fIdentifier)) {
			IAnnotationBinding dliAnnotation = null;

			if(functionContainerBinding != null) {
				dliAnnotation = functionContainerBinding.getAnnotation(new String[] {"egl", "io", "dli"}, "DLI");
			}
			
			if(dliAnnotation == null) {
				problemRequestor.acceptProblem(
					functionInvocation.getTarget(),
					IProblemRequestor.DLI_IO_ONLY_ALLOWED_IN_PROGRAM_WITH_DLI);
			}
			else {
				IAnnotationBinding callInterfaceAnnotation = (IAnnotationBinding) dliAnnotation.findData(InternUtil.intern(IEGLConstants.PROPERTY_CALLINTERFACE));
				if(callInterfaceAnnotation == IBinding.NOT_FOUND_BINDING || InternUtil.intern("AIBTDLI") == ((EnumerationDataBinding) callInterfaceAnnotation.getValue()).getName()) {
					if(!functionBindingIs(functionBinding, new String[] {"egl", "io", "dli"}, "DLILib", "AIBTDLI")) {
						problemRequestor.acceptProblem(
							functionInvocation.getTarget(),
							IProblemRequestor.DLI_SYSTEM_FUNCTION_NOT_ALLOWED_WITH_AIBTDLI_INTERFACE,
							new String[] {functionBinding.getCaseSensitiveName()});
					}
				}
			}
		}
		else if(systemFunctionsNotAllowedInServices.contains(fIdentifier)) {
			if(functionContainerBinding != null && ITypeBinding.SERVICE_BINDING == functionContainerBinding.getKind()) {
				problemRequestor.acceptProblem(
					functionInvocation.getTarget(),
					IProblemRequestor.SYSTEM_FUNCTION_NOT_ALLOWED_IN_SERVICE,
					new String[] {functionBinding.getCaseSensitiveName()});
			}
		}
	}
	
	public void endVisit(MoveStatement moveStatement) {
		//When arrays or multiply occuring items are used as targets of the
		//move statement with either the 'for <index>' or 'for all' modifiers
		//and an array index is specified, the array index is providing a
		//"starting index" for the move -- the target expression is not a
		//normal array access, in other words. If these conditions are met,
		//the following code sets the type binding for the array access to
		//the 'array' part of the array access.
		MoveModifier modifier = moveStatement.getMoveModifierOpt();
		if(modifier != null) {
			if(modifier.isFor() || modifier.isForAll()) {
				moveStatement.getTarget().accept(new DefaultASTVisitor() {
					public boolean visit(ParenthesizedExpression parenthesizedExpression) {
						return true;
					}
					public boolean visit(ArrayAccess arrayAccess) {
						List indices = arrayAccess.getIndices();
						if(indices.size() == 1) {
							ITypeBinding indexType = ((Expression) indices.get(0)).resolveTypeBinding();
							if(indexType != null && ITypeBinding.PRIMITIVE_TYPE_BINDING == indexType.getKind()) {
								if(Primitive.isNumericType(((PrimitiveTypeBinding) indexType).getPrimitive())) {
									ITypeBinding arrayType = arrayAccess.getArray().resolveTypeBinding();
									if(arrayType != null) {
										arrayAccess.setTypeBinding(arrayType);
									}
								}
							}
						}
						return false;
					}
				});
			}
		}
	}
	
	public void endVisit(final CloseStatement closeStatement) {
		closeStatement.accept(new AbstractASTExpressionVisitor(){
			public boolean visitExpression(final Expression expression) {
				ITypeBinding typeBinding = expression.resolveTypeBinding();
				if (StatementValidator.isValidBinding(typeBinding)){

					if (typeBinding.getAnnotation(new String[] {"egl", "ui", "text"}, "PrintForm") != null){
						Scope scope = currentScope;
						while (scope != null){
							if (scope.isProgramScope()){
								break;
							}
							scope = scope.getParentScope();
						}
						
						if (scope != null){
							ProgramBinding binding = ((ProgramScope)scope).getProgramBinding();
							if (binding.getAnnotation(new String[] {"egl", "ui", "webTransaction"}, "VGWebTransaction") != null){
								problemRequestor.acceptProblem(
										expression ,
										IProblemRequestor.CLOSE_PRINTFORM_NOT_ALLOWED_IN_WEB_TRANSACTION);
							}
							
						}
					}
				}
				return false;
			}
		});
	}

	public void endVisit(SetValuesExpression setValuesExpression) {		
		setValuesExpression.setTypeBinding(setValuesExpression.getExpression().resolveTypeBinding());
		
		setValuesExpression.getSettingsBlock().accept(new DefaultASTVisitor() {
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}
			
			public boolean visit(Assignment assignment) {
				validateAssignment(assignment, false);
				return false;
			}
		});
		
		ITypeBinding type = setValuesExpression.getExpression().resolveTypeBinding();
		if (Binding.isValidBinding(type) && (type.getKind() == ITypeBinding.FUNCTION_BINDING || type.getKind() == ITypeBinding.DELEGATE_BINDING)) {
			problemRequestor.acceptProblem(
					setValuesExpression.getSettingsBlock(),
					IProblemRequestor.SETTINGS_BLOCK_NOT_ALLOWED,
					new String[] {});
		}
	}
	
	private void validateAssignment(final Assignment assignment, final boolean canAssignToReadOnlyFields) {
		if(assignment.resolveBinding() == null) {
			Expression leftHandSide = assignment.getLeftHandSide();
			ITypeBinding lhType = leftHandSide.resolveTypeBinding();
			IDataBinding lhDBinding = leftHandSide.resolveDataBinding();
			
			Expression rightHandSide = assignment.getRightHandSide();
			ITypeBinding rhType = rightHandSide.resolveTypeBinding();
			IDataBinding rhDBinding = rightHandSide.resolveDataBinding();
			
			if(StatementValidator.isValidBinding(lhType) &&
			   StatementValidator.isValidBinding(rhType) &&
			   ITypeBinding.ANNOTATION_BINDING != lhType.getKind()) {
				new AssignmentStatementValidator(problemRequestor, 	compilerOptions, null).validateAssignment(
						assignment.getOperator(), 
						assignment.getLeftHandSide(), 
						assignment.getRightHandSide(), 
						assignment.getLeftHandSide().resolveTypeBinding(), 
						assignment.getRightHandSide().resolveTypeBinding(), 
						assignment.getLeftHandSide().resolveDataBinding(), 
						assignment.getRightHandSide().resolveDataBinding(), 
						false, 
						DefaultBinder.isArithmeticAssignment(assignment));
			}
		}
	}
	
	protected Object resolveSelectedIndexItem(Node node, Object value, IDataBinding varBinding, IDataBinding[] path) {
	    if (!(value instanceof String)) {
	        return value;
	    }

	    IDataBinding dataBinding = getDataBindingForAnnotation(varBinding, path);
	    if (dataBinding.getKind() != IDataBinding.STRUCTURE_ITEM_BINDING) {
	        return value;
	    }
	    
	    StructureItemBinding structItem = (StructureItemBinding) dataBinding;
	    String selectedIndexName = (String) value;

	    Name name = new ExpressionParser(compilerOptions).parseAsName(selectedIndexName);
	    if (name == null) {
	        return IBinding.NOT_FOUND_BINDING;
	    }
	    Object result;
	    Scope saveScope = currentScope;
	    currentScope = new TypeBindingScope(NullScope.INSTANCE, structItem.getEnclosingStructureBinding(), null);
	    try {
	        bindExpressionName(name);
	        result = name;
	        //TODO validate the attributes of the selectedIndex item
	    } catch (ResolutionException e) {
	        problemRequestor.acceptProblem(node, IProblemRequestor.SELECTEDINDEXITEM_MUST_BE_IN_RECORD, IMarker.SEVERITY_ERROR, new String[] {selectedIndexName, structItem.getName(), structItem.getEnclosingStructureBinding().getName()});
	        result = IBinding.NOT_FOUND_BINDING;
	    }
	    currentScope = saveScope;
	    return result;
	}
	
	protected Object resolveValueRef(Node node, Object value, IDataBinding varBinding, IDataBinding[] path) {
		if (!(value instanceof String)) {
	        return value;
	    }
		
		IPartBinding partBinding = varBinding.getDeclaringPart();
		if(partBinding == null || ITypeBinding.FIXED_RECORD_BINDING != partBinding.getKind()) {
			return value;
		}

	    String selectedIndexName = (String) value;
	    Name name = new ExpressionParser(compilerOptions).parseAsName(selectedIndexName);
	    if (name == null) {
	        return IBinding.NOT_FOUND_BINDING;
	    }
	    
	    Object result = ((FixedRecordBinding) partBinding).getSimpleNamesToDataBindingsMap().get(name.getIdentifier());
	    
	    if(result == null) {
	    	result = IBinding.NOT_FOUND_BINDING;
	        problemRequestor.acceptProblem(
	        	node,
				IProblemRequestor.VALUEREF_NOT_RESOLVED,
				new String[] {
	        		name.getCanonicalName()
	        	});
	    }

	    return result;
	}
	
    protected Object resolveNumElementsItem(Node node, Object value, IDataBinding varBinding, IDataBinding[] path) {

        if (!(value instanceof String)) {
            return value;
        }

        IDataBinding dataBinding = getDataBindingForAnnotation(varBinding, path);
        if (dataBinding.getKind() != IDataBinding.STRUCTURE_ITEM_BINDING) {
            return value;
        }
        
        StructureItemBinding structItem = (StructureItemBinding) dataBinding;
        String numElementsName = (String) value;

        Name name = new ExpressionParser(compilerOptions).parseAsName(numElementsName);
        if (name == null) {
            return IBinding.NOT_FOUND_BINDING;
        }
        Object result;
        Scope saveScope = currentScope;
        currentScope = new TypeBindingScope(NullScope.INSTANCE, structItem.getEnclosingStructureBinding(), null);
        try {
            IDataBinding dBinding = bindExpressionName(name);
            result = name;
            
            if(dBinding != IBinding.NOT_FOUND_BINDING) {
            	ITypeBinding tBinding = dBinding.getType();
            	if(tBinding != null) {
            		boolean typeIsValid = false;
            		if(ITypeBinding.PRIMITIVE_TYPE_BINDING == tBinding.getKind()) {
            			PrimitiveTypeBinding primTBinding = (PrimitiveTypeBinding) tBinding;
            			typeIsValid = Primitive.isNumericType(primTBinding.getPrimitive()) &&
            					      primTBinding.getDecimals() == 0;
            		}
            		if(!typeIsValid) {
            			problemRequestor.acceptProblem(
            				node,
							IProblemRequestor.NUMELEMENTSITEM_MUST_BE_NUMERIC,
							new String[] {
        	            		numElementsName,
        						structItem.getName(),
        						structItem.getEnclosingStructureBinding().getCaseSensitiveName()});
            		}
            	}
            }
        } catch (ResolutionException e) {
            problemRequestor.acceptProblem(
            	node,
				IProblemRequestor.NUMELEMENTSITEM_MUST_BE_IN_RECORD,
				new String[] {
            		numElementsName,
					structItem.getName(),
					structItem.getEnclosingStructureBinding().getCaseSensitiveName()});
            result = IBinding.NOT_FOUND_BINDING;
        }
        currentScope = saveScope;
        return result;
    }


    protected IDataBinding getDataBindingForAnnotation(IDataBinding dataBinding, IDataBinding[] path) {
        if (path == null || path.length == 0) {
            return dataBinding;
        }
        return path[path.length - 1];
    }
    
    protected void processResolvableProperties(SettingsBlock block, final IDataBinding dataBinding, final IDataBinding[] path) {
    	processResolvableProperties(block, dataBinding, path, null);
    }

    protected void processResolvableProperties(SettingsBlock block, final IDataBinding dataBinding, final IDataBinding[] path, final IAnnotationBinding currentAnnotation) {
        block.accept(new AbstractASTExpressionVisitor() {
            public boolean visit(Assignment assignment) {
                IAnnotationBinding annotation = assignment.resolveBinding();
                if (annotation != null && dataBinding != null) {
                    processResolvableProperty(getAnnotationType(annotation), annotation.getValue(), dataBinding, path, assignment
                            .getRightHandSide());
                }
                return false;
            }

            private IAnnotationTypeBinding getAnnotationType(IAnnotationBinding annotation) {
            	if(annotation.isAnnotationField()) {
            		IAnnotationTypeBinding enclosingType = ((AnnotationFieldBinding) annotation).getEnclosingAnnotationType();
            		if(enclosingType.hasSingleValue()) {
            			return enclosingType;
            		}
            	}
            	return annotation.getAnnotationType();
			}

			public boolean visit(SetValuesExpression setValuesExpression) {
                IDataBinding exprData = setValuesExpression.getExpression().resolveDataBinding();
                if (exprData != null && exprData != IBinding.NOT_FOUND_BINDING) {
                    if (exprData.getKind() == IDataBinding.ANNOTATION_BINDING) {
                        processResolvableProperties(setValuesExpression.getSettingsBlock(), dataBinding, path, (IAnnotationBinding) exprData);
                    } else {
                        
                       //Add elements to the path based on the databindings specified in the setValues expression
    	                final IDataBinding[][] pathArr = new IDataBinding[1][];
    	                pathArr[0] = path;   	                        
    	                setValuesExpression.getExpression().accept(new AbstractASTExpressionVisitor() {
                            public boolean visitExpression(Expression expression) {
                                return true;
                            }
                            public boolean visit(ArrayAccess arrayAccess) {
                                arrayAccess.getArray().accept(this);
                                return false;
                            }
                            public void endVisitExpression(Expression expression) {
                                IDataBinding exprBinding = expression.resolveDataBinding();
                                if (dataBinding != null && dataBinding != IBinding.NOT_FOUND_BINDING) {
                                    pathArr[0] = addElement(exprBinding, pathArr[0], dataBinding );
                                }
                            }
	                    });

                        
                        if (dataBinding == null) {
                            processResolvableProperties(setValuesExpression.getSettingsBlock(), exprData, pathArr[0]);
                        }
                        else {
                        processResolvableProperties(setValuesExpression.getSettingsBlock(), dataBinding, pathArr[0]);
                        }
                  }
                }
                return false;
            }
			
			public boolean visitExpression(Expression expression) {
				if(currentAnnotation != null && !currentAnnotation.isAnnotationField() && currentAnnotation.getAnnotationType().hasSingleValue()) {
					processResolvableProperty(currentAnnotation.getAnnotationType(), currentAnnotation.getValue(), dataBinding, path, expression);
				}
				return false;
			}
        });
    }

    protected IDataBinding[] addElement(IDataBinding newElement, IDataBinding[] array, IDataBinding dataBinding) {
        if (array == null) {
            if (newElement == dataBinding) {
                return null;
            }
            return new IDataBinding[] { newElement };
        }
        if (newElement == array[array.length - 1]) {
            return array;
        }
        if (newElement.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING
                && array[array.length - 1].getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
            array[array.length - 1] = newElement;
            return array;
        }

        IDataBinding[] newArray = new IDataBinding[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = newElement;
        return newArray;
    }

    protected Object resolveRedefines(Node node, Object value, IDataBinding varBinding, IDataBinding[] path) {

        if (!(value instanceof String)) {
            return value;
        }

        String redefinesName = (String) value;

        Name name = new ExpressionParser(compilerOptions).parseAsName(redefinesName);
        if (name == null) {
            problemRequestor.acceptProblem(node, IProblemRequestor.REDEFINES_MUST_BE_DECLARATION, IMarker.SEVERITY_ERROR, new String[] {IEGLConstants.PROPERTY_REDEFINES});
            return IBinding.NOT_FOUND_BINDING;
        }
        Object result;
        try {
            bindExpressionName(name);
            result = name.resolveDataBinding();
            //TODO validate the attributes of the redefines record
        } catch (ResolutionException e) {
            problemRequestor.acceptProblem(node, IProblemRequestor.REDEFINES_MUST_BE_DECLARATION, IMarker.SEVERITY_ERROR, new String[] {IEGLConstants.PROPERTY_REDEFINES});
            result = IBinding.NOT_FOUND_BINDING;
        }
        return result;
    }
    
    protected Object resolveFunctionMemberRef(Node node, IAnnotationBinding aBinding, Object value, IDataBinding varBinding, IDataBinding[] path) {

        if (!(value instanceof String)) {
            return value;
        }

        String functionName = (String) value;

        Name name = new ExpressionParser(compilerOptions).parseAsName(functionName);
        if (name == null) {
            return IBinding.NOT_FOUND_BINDING;
        }
        Object result = null;
        boolean resultValid = true;
        try {
            bindExpressionName(name);
            IDataBinding dataBinding = name.resolveDataBinding();
			result = dataBinding;
            resultValid = IDataBinding.NESTED_FUNCTION_BINDING == dataBinding.getKind();
        } catch (ResolutionException e) {
        	resultValid = false;
        }
        
        if(!resultValid) {
	        problemRequestor.acceptProblem(
            	node,
				IProblemRequestor.PROPERTY_DOESNT_RESOLVE,
				new String[] {
            		functionName,
					aBinding.getCaseSensitiveName(),
					varBinding.getCaseSensitiveName()
            	});
	        result = IBinding.NOT_FOUND_BINDING;
        }
        
        return result;
    }
    
    protected Object resolveBinding(final Node node, Object value, IDataBinding varBinding, IDataBinding[] path) {

        if (!(value instanceof String)) {
            return value;
        }

        String bindingName = (String) value;
        boolean resultIsValid = false;
        IDataBinding result;

        Expression expr = new ExpressionParser(compilerOptions).parseAsLvalue(bindingName);
        if (expr == null) {
            result = IBinding.NOT_FOUND_BINDING;
        }
        else {
        	final IProblemRequestor oldProblemRequestor = problemRequestor;
        	problemRequestor = new DefaultProblemRequestor() {
				public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) {
			 		if (severity == IMarker.SEVERITY_ERROR) {
			 			setHasError(true);
			 		}
					oldProblemRequestor.acceptProblem(node, problemKind, severity, inserts);
				}    
				public boolean shouldReportProblem(int problemKind) {
					return oldProblemRequestor.shouldReportProblem(problemKind);
				}

        	};
	        expr.accept(this);
	        problemRequestor = oldProblemRequestor;
	        
	        resultIsValid = true;
	        
	        result = expr.resolveDataBinding();
	        if(result == null) {
	        	result = IBinding.NOT_FOUND_BINDING;
	        }	        
        }
        
        if(!resultIsValid) {
        	problemRequestor.acceptProblem(
        		node,
				IProblemRequestor.VARIABLE_NOT_FOUND,
				new String[] {value.toString()});
        }
        
        return result;
    }

    protected boolean isUIRecordItem(IDataBinding dataBinding, IDataBinding[] path) {
        if (path == null || path.length < 2) {
            return dataBinding.getAnnotation(new String[] {"egl", "ui", "webTransaction"}, "VGUIRecord") != null;
        }
        IDataBinding[] newPath = new IDataBinding[path.length - 1];
        System.arraycopy(path, 0, newPath, 0, newPath.length);
        return dataBinding.getAnnotationFor(new String[] {"egl", "ui", "webTransaction"}, "VGUIRecord", newPath) != null;
    }

	public void endVisit(ClassDataDeclaration classDataDeclaration) {
		Type type = classDataDeclaration.getType();
		if (type != null){
			while (type.isArrayType()){
				type = ((ArrayType)type).getElementType();
			}
			if (type.isPrimitiveType()){
				PrimitiveTypeValidator.validate((PrimitiveType)type,problemRequestor,compilerOptions);
			}
		}
		
		if (classDataDeclaration.hasInitializer()) {
			if (classDataDeclaration.hasSettingsBlock()) {
				issueErrorForInitialization(classDataDeclaration.getSettingsBlockOpt(), ((Name)classDataDeclaration.getNames().get(0)).getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED);
			}
		}
		else {
			ITypeBinding tBinding = classDataDeclaration.getType().resolveTypeBinding();
			//Non-nullable reference types must be instantiable, because they are initialized with the default constructor
			if (Binding.isValidBinding(tBinding) && !tBinding.isNullable() && tBinding.isReference() && !tBinding.isInstantiable() && currentScope.getPartBinding() != tBinding) {
				//Don't need to throw error if the field is in an ExternalType
				if (Binding.isValidBinding(currentScope.getPartBinding()) && currentScope.getPartBinding().getKind() != ITypeBinding.EXTERNALTYPE_BINDING) {
					problemRequestor.acceptProblem(type,
							IProblemRequestor.TYPE_NOT_INSTANTIABLE,
						new String[] {type.getCanonicalName()});
				}
			}
			
			//nullable types cannot specify a settings block that contains settings for data in the field
			if (classDataDeclaration.hasSettingsBlock() && Binding.isValidBinding(tBinding) && tBinding.isNullable()) {
				issueErrorForInitialization(classDataDeclaration.getSettingsBlockOpt(), ((Name)classDataDeclaration.getNames().get(0)).getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED_NULL);
			}
		}
		
		IPartBinding partBinding = currentScope.getPartBinding();
		if (StatementValidator.isValidBinding(partBinding) && partBinding.getKind() == ITypeBinding.LIBRARY_BINDING){
			if (partBinding.getAnnotation(new String[] {"egl", "core"}, "NativeLibrary") != null){
				boolean error = false;
				if (type.isPrimitiveType()){
					error = true;
				}else if (type.isNameType()){
					Name name = ((NameType)type).getName();
					IBinding binding = name.resolveBinding();
					ITypeBinding typeBinding = null;
					if(IBinding.NOT_FOUND_BINDING != binding) {
						if (binding.isDataBinding()){
							error = true;
						}else if (binding.isTypeBinding()){
							typeBinding = (ITypeBinding)binding;
							if (typeBinding.getKind() != ITypeBinding.SERVICE_BINDING &&
									typeBinding.getKind() != ITypeBinding.INTERFACE_BINDING){
								error = true;
							}
						}
					}
				}
				if (error){				
					String name = ((Expression)classDataDeclaration.getNames().get(0)).getCanonicalString();
					problemRequestor.acceptProblem(classDataDeclaration,
							classDataDeclaration.isConstant()? IProblemRequestor.NATIVE_LIBRARYS_DO_NOT_SUPPORT_CONSTANT_DECLARATIONS : IProblemRequestor.NATIVE_LIBRARYS_DO_NOT_SUPPORT_DECLARATIONS,
						new String[] {name,partBinding.getCaseSensitiveName()});
				}
			}
		}
		if (classDataDeclaration.hasSettingsBlock()) {
			IDataBinding dbinding = ((Expression)classDataDeclaration.getNames().get(0)).resolveDataBinding();
			if (Binding.isValidBinding(dbinding)) {
				issueErrorForPropertyOverrides(dbinding, classDataDeclaration.getSettingsBlockOpt());
			}
		}
	}
	
	public void endVisit(final FunctionDataDeclaration functionDataDeclaration) {
		Type type = functionDataDeclaration.getType();
		if (type != null){	
			if (type.isNameType()){
				IBinding binding = ((NameType)type).getName().resolveBinding();
				if (StatementValidator.isValidBinding(binding) && binding.isTypeBinding() && ((ITypeBinding)binding).getKind() == ITypeBinding.ARRAYDICTIONARY_BINDING){
					if (functionDataDeclaration.hasSettingsBlock()){
						functionDataDeclaration.getSettingsBlockOpt().accept(new AbstractASTExpressionVisitor(){
							int initSize = -1;
						    public boolean visit(NewExpression newExpression) {
						    	if (newExpression.getType().isArrayType()){
						    		Expression initsizeexpr = ((ArrayType)newExpression.getType()).getInitialSize();
						    		if (initsizeexpr != null){
						    			initsizeexpr.accept(new AbstractASTExpressionVisitor(){
						    				 public boolean visit(IntegerLiteral integerLiteral) {
						    				 	int size = Integer.valueOf(integerLiteral.getValue()).intValue();
						    				 	if (initSize != -1 && initSize != size){
						    				 		problemRequestor.acceptProblem(functionDataDeclaration,
						    				 				IProblemRequestor.ARRAY_DICTIONARY_HAS_INVALID_COLUMNS);
						    				 	}
						    				 	
						    				 	initSize = size;
						    				 	
						    				 	return false;
						    				 }
						    			});
						    		}
						    		
						    	}
						        return false;
						    }
						});
					}
				}
			}
			while (type.isArrayType()){
				type = ((ArrayType)type).getElementType();
			}
			if (type.isPrimitiveType()){
				PrimitiveTypeValidator.validate((PrimitiveType)type,problemRequestor,compilerOptions);
			}

			if (functionDataDeclaration.hasInitializer()) {
				if (functionDataDeclaration.hasSettingsBlock()) {
					issueErrorForInitialization(functionDataDeclaration.getSettingsBlockOpt(), ((Name)functionDataDeclaration.getNames().get(0)).getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED);
				}
			}
			else {
				ITypeBinding tBinding = functionDataDeclaration.getType().resolveTypeBinding();
				//Non-nullable reference types must be instantiable, because they are initialized with the default constructor
				if (Binding.isValidBinding(tBinding) && !tBinding.isNullable() && tBinding.isReference() && !tBinding.isInstantiable() && currentScope.getPartBinding() != tBinding) {
					problemRequestor.acceptProblem(type,
							IProblemRequestor.TYPE_NOT_INSTANTIABLE,
						new String[] {type.getCanonicalName()});
				}
				//nullable types cannot specify a settings block that contains settings for data in the field
				if (functionDataDeclaration.hasSettingsBlock() && Binding.isValidBinding(tBinding) && tBinding.isNullable()) {
					issueErrorForInitialization(functionDataDeclaration.getSettingsBlockOpt(), ((Name)functionDataDeclaration.getNames().get(0)).getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED_NULL);
				}
			}

		}
		if (functionDataDeclaration.hasSettingsBlock()) {
			IDataBinding dbinding = ((Expression)functionDataDeclaration.getNames().get(0)).resolveDataBinding();
			if (Binding.isValidBinding(dbinding)) {
				issueErrorForPropertyOverrides(dbinding, functionDataDeclaration.getSettingsBlockOpt());
			}
		}

	}
	
	public void endVisit(FunctionParameter functionParameter) {
		Type type = functionParameter.getType();
		if (type != null){		
			if (type.isArrayType()){
				String name = functionParameter.getName().getCanonicalName();
				StatementValidator.validateRecordParamDimensions(type,problemRequestor,functionParameter,name);
				type = ((ArrayType)type).getElementType();
			}
			if (type.isPrimitiveType()){
				PrimitiveTypeValidator.validateParamInFunction((PrimitiveType)type,problemRequestor,compilerOptions);
			}
		}
	}
	
	public void endVisit(ProgramParameter programParameter) {
		Type type = programParameter.getType();
		if (type != null){		
			if (type.isArrayType()){
				String name = programParameter.getName().getCanonicalName();
				StatementValidator.validateRecordParamDimensions(type,problemRequestor,programParameter,name);				
			}
			
			Type baseType = type.getBaseType();
			if (baseType.isPrimitiveType()){
				PrimitiveTypeValidator.validate((PrimitiveType)baseType,problemRequestor,compilerOptions);
			}
			
			if (type.isNameType()){
				if (InternUtil.intern(((NameType)type).getName().getCanonicalName()) == InternUtil.intern(IEGLConstants.DB_PCBRECORD_STRING)){
					if (this.getDLIAnnotation() == null){
						problemRequestor.acceptProblem(programParameter,
								IProblemRequestor.DLI_PCB_PARAMETER_REQUIRES_DLI_PROPERTY_ON_PROGRAM);
					}
				}
			}
		}
	}
	
	public void endVisit(StructureItem structureItem) {
		Type type = structureItem.getType();
		if (type != null){
			Type baseType = type.getBaseType();
			if (baseType.isPrimitiveType()){
				PrimitiveTypeValidator.validate((PrimitiveType)baseType,problemRequestor,compilerOptions);
			}

			if (structureItem.hasInitializer()) {
				if (structureItem.hasSettingsBlock()) {
					if (structureItem.getName() != null) {
						issueErrorForInitialization(structureItem.getSettingsBlock(), structureItem.getName().getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED);
					}
				}
			}
			if (!structureItem.hasInitializer()) {
				ITypeBinding tBinding = type.resolveTypeBinding();
				//Non-nullable reference types must be instantiable, because they are initialized with the default constructor
				if (Binding.isValidBinding(tBinding) && !tBinding.isNullable() && tBinding.isReference() && !tBinding.isInstantiable() && currentScope.getPartBinding() != tBinding) {
					problemRequestor.acceptProblem(type,
							IProblemRequestor.TYPE_NOT_INSTANTIABLE,
						new String[] {type.getCanonicalName()});
				}
				//nullable types cannot specify a settings block that contains settings for data in the field
				if (structureItem.hasSettingsBlock() && Binding.isValidBinding(tBinding) && tBinding.isNullable() && structureItem.getName() != null) {
					issueErrorForInitialization(structureItem.getSettingsBlock(), structureItem.getName().getCanonicalName(), IProblemRequestor.SETTING_NOT_ALLOWED_NULL);
				}
			}
		}

		if (structureItem.hasSettingsBlock() && structureItem.getName() != null) {
			IDataBinding dbinding = structureItem.getName().resolveDataBinding();
			if (Binding.isValidBinding(dbinding)) {
				issueErrorForPropertyOverrides(dbinding, structureItem.getSettingsBlock());
			}
		}
	}	
	
	public void endVisit(ReturnsDeclaration returnStatement) {
		Type type = returnStatement.getType();
		if (type != null){		
			Type baseType = type.getBaseType();
			if (baseType.isPrimitiveType()){
				PrimitiveTypeValidator.validate((PrimitiveType)baseType,problemRequestor,compilerOptions);
			}
		}
	}	
	
	
	protected void validateIOStatementInDLIProgram(final Node node) {
		node.accept(new AbstractASTExpressionVisitor() {
			boolean processed = false;

			public boolean visitExpression(final Expression expression) {
				if (processed) {
					return false;
				}

				processed = true;
				ITypeBinding typeBinding = expression.resolveTypeBinding();
				if (StatementValidator.isValidBinding(typeBinding)) {
					if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
						typeBinding = typeBinding.getBaseType();
					}

					if (typeBinding.getAnnotation(new String[] { "egl", "io", "dli" }, "DLISegment") != null) {
						IPartBinding partBinding = currentScope.getPartBinding();
						if (StatementValidator.isValidBinding(partBinding)) {
							if (partBinding.getKind() != ITypeBinding.PROGRAM_BINDING
									&& partBinding.getKind() != ITypeBinding.LIBRARY_BINDING) {
								problemRequestor.acceptProblem(node, IProblemRequestor.DLI_ONLY_VALID_IN_PROGRAM);
							} else if (partBinding.getAnnotation(new String[] { "egl", "io", "dli" }, "DLI") == null) {
								problemRequestor.acceptProblem(node, IProblemRequestor.DLI_PROGRAM_MUST_HAVE_DLI);
							}
						}
					}
				}

				return false;
			}

		});
	}
	
    protected void processResolvableProperties(Name name) {
        
        IDataBinding dataBinding = name.resolveDataBinding();
        if (dataBinding == null || dataBinding == IBinding.NOT_FOUND_BINDING) {
            return;
        }
        processResolvablePropertiesAndFields(dataBinding, null, name);
    }
    
    private void processResolvablePropertiesAndFields(IDataBinding varBinding, IDataBinding[] path, Node node) {
        processResolvableProperties(varBinding, path, node);
        IDataBinding dataBinding = getDataBindingForAnnotation(varBinding, path);
        if (dataBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
            StructureItemBinding structItem = (StructureItemBinding) dataBinding;
            Iterator i = structItem.getChildren().iterator();
            while (i.hasNext()) {
                StructureItemBinding curItem = (StructureItemBinding)i.next();
                IDataBinding[] newPath = addElement(curItem, path, varBinding);
                processResolvablePropertiesAndFields(varBinding, newPath, node);
            }
            return;
        }
        
        if (dataBinding.getType() == null || dataBinding.getType() == IBinding.NOT_FOUND_BINDING ||dataBinding.getType().getBaseType() == null || dataBinding.getType().getBaseType() == IBinding.NOT_FOUND_BINDING) {
            return;
        }
            
        ITypeBinding typeBinding = dataBinding.getType().getBaseType();
        if (typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
            FlexibleRecordBinding flexRec = (FlexibleRecordBinding) typeBinding;
            Iterator i = flexRec.getDeclaredFields().iterator();
            while (i.hasNext()) {
                FlexibleRecordFieldBinding field = (FlexibleRecordFieldBinding)i.next();
                IDataBinding[] newPath = addElement(field, path, varBinding);
                processResolvablePropertiesAndFields(varBinding, newPath, node);
                
            }
            return;
        }
        if (typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
            FixedRecordBinding fixedRec = (FixedRecordBinding) typeBinding;
            Iterator i = fixedRec.getStructureItems().iterator();
            while (i.hasNext()) {
                StructureItemBinding curItem = (StructureItemBinding)i.next();
                IDataBinding[] newPath = addElement(curItem, path, varBinding);
                processResolvablePropertiesAndFields(varBinding, newPath, node);
            }
            return;
        }
    }

    private void processResolvableProperties(IDataBinding dataBinding, IDataBinding[] path, Node node) {
        IDataBinding bindingBeingAnnotated = getDataBindingForAnnotation(dataBinding, path);
        
        ITypeBinding typeOfBindingBeingAnnotated = bindingBeingAnnotated.getType();
        if(typeOfBindingBeingAnnotated != null && typeOfBindingBeingAnnotated.isPartBinding()) {
        	IPartSubTypeAnnotationTypeBinding subType = ((IPartBinding) typeOfBindingBeingAnnotated).getSubType();
        	if(subType != null) {
        		List newFields = new ArrayList();
        		IAnnotationBinding subTypeAnnotationBinding = bindingBeingAnnotated.getAnnotation(subType);
        		
        		IAnnotationBinding annotation = getField(subTypeAnnotationBinding, IEGLConstants.PROPERTY_LENGTHITEM);
                if (annotation != null) {
                    Object newValue = resolveLengthItem(node, annotation.getValue(), dataBinding, path, true);
                    if (newValue != annotation.getValue()) {
                        AnnotationBinding newAnnotation = new AnnotationFieldBinding(InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_LENGTHITEM), null, getInternalAnnotationType(IEGLConstants.PROPERTY_LENGTHITEM), subType);
                        newAnnotation.setValue(newValue, null, null, compilerOptions, false);
                        newFields.add(newAnnotation);
                    }
                }
                
                annotation = getField(subTypeAnnotationBinding, IEGLConstants.PROPERTY_PUTOPTIONSRECORD);
                if (annotation != null) {
                    Object newValue = resolveMQRecordProperty(node, annotation.getValue(), dataBinding, path, IEGLConstants.PROPERTY_PUTOPTIONSRECORD);
                    if (newValue != annotation.getValue()) {
                    	AnnotationBinding newAnnotation = new AnnotationFieldBinding(InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_PUTOPTIONSRECORD), null, getInternalAnnotationType(IEGLConstants.PROPERTY_PUTOPTIONSRECORD), subType);
                        newAnnotation.setValue(newValue, null, null, compilerOptions, false);
                        newFields.add(newAnnotation);
                    }
                }
                
                annotation = getField(subTypeAnnotationBinding, IEGLConstants.PROPERTY_OPENOPTIONSRECORD);
                if (annotation != null) {
                    Object newValue = resolveMQRecordProperty(node, annotation.getValue(), dataBinding, path, IEGLConstants.PROPERTY_OPENOPTIONSRECORD);
                    if (newValue != annotation.getValue()) {
                    	AnnotationBinding newAnnotation = new AnnotationFieldBinding(InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_OPENOPTIONSRECORD), null, getInternalAnnotationType(IEGLConstants.PROPERTY_OPENOPTIONSRECORD), subType);
                        newAnnotation.setValue(newValue, null, null, compilerOptions, false);
                        newFields.add(newAnnotation);
                    }
                }
                
                annotation = getField(subTypeAnnotationBinding, IEGLConstants.PROPERTY_GETOPTIONSRECORD);
                if (annotation != null) {
                    Object newValue = resolveMQRecordProperty(node, annotation.getValue(), dataBinding, path, IEGLConstants.PROPERTY_GETOPTIONSRECORD);
                    if (newValue != annotation.getValue()) {
                    	AnnotationBinding newAnnotation = new AnnotationFieldBinding(InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_GETOPTIONSRECORD), null, getInternalAnnotationType(IEGLConstants.PROPERTY_GETOPTIONSRECORD), subType);
                        newAnnotation.setValue(newValue, null, null, compilerOptions, false);
                        newFields.add(newAnnotation);
                    }
                }
                
                annotation = getField(subTypeAnnotationBinding, IEGLConstants.PROPERTY_MSGDESCRIPTORRECORD);
                if (annotation != null) {
                    Object newValue = resolveMQRecordProperty(node, annotation.getValue(), dataBinding, path, IEGLConstants.PROPERTY_MSGDESCRIPTORRECORD);
                    if (newValue != annotation.getValue()) {
                    	AnnotationBinding newAnnotation = new AnnotationFieldBinding(InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_MSGDESCRIPTORRECORD), null, getInternalAnnotationType(IEGLConstants.PROPERTY_MSGDESCRIPTORRECORD), subType);
                        newAnnotation.setValue(newValue, null, null, compilerOptions, false);
                        newFields.add(newAnnotation);
                    }
                }
                
                annotation = getField(subTypeAnnotationBinding, IEGLConstants.PROPERTY_QUEUEDESCRIPTORRECORD);
                if (annotation != null) {
                    Object newValue = resolveMQRecordProperty(node, annotation.getValue(), dataBinding, path, IEGLConstants.PROPERTY_QUEUEDESCRIPTORRECORD);
                    if (newValue != annotation.getValue()) {
                    	AnnotationBinding newAnnotation = new AnnotationFieldBinding(InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_QUEUEDESCRIPTORRECORD), null, getInternalAnnotationType(IEGLConstants.PROPERTY_QUEUEDESCRIPTORRECORD), subType);
                        newAnnotation.setValue(newValue, null, null, compilerOptions, false);
                        newFields.add(newAnnotation);
                    }
                }
                
                annotation = getField(subTypeAnnotationBinding, IEGLConstants.PROPERTY_NUMELEMENTSITEM);
                if (annotation != null) {
                    Object newValue = resolveNumElementsItem(node, annotation.getValue(), dataBinding, path);
                    if (newValue != annotation.getValue()) {
                    	AnnotationBinding newAnnotation = new AnnotationFieldBinding(InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_NUMELEMENTSITEM), null, getInternalAnnotationType(IEGLConstants.PROPERTY_NUMELEMENTSITEM), subType);
                        newAnnotation.setValue(newValue, null, null, compilerOptions, false);
                        newFields.add(newAnnotation);
                    }
                }
                
                annotation = getField(subTypeAnnotationBinding, IEGLConstants.PROPERTY_KEYITEM);
                if (annotation != null) {
                    Object newValue = resolveKeyItem(node, annotation.getValue(), dataBinding, path);
                    if (newValue != annotation.getValue()) {
                    	AnnotationBinding newAnnotation = new AnnotationFieldBinding(InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_KEYITEM), null, getInternalAnnotationType(IEGLConstants.PROPERTY_KEYITEM), subType);
                        newAnnotation.setValue(newValue, null, null, compilerOptions, false);
                        newFields.add(newAnnotation);
                    }
                }
                
                annotation = getField(subTypeAnnotationBinding, IEGLConstants.PROPERTY_RECORDNUMITEM);
                if (annotation != null) {
                    Object newValue = resolveRecordNumItem(node, annotation.getValue(), dataBinding, path, true);
                    if (newValue != annotation.getValue()) {
                    	AnnotationBinding newAnnotation = new AnnotationFieldBinding(InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_RECORDNUMITEM), null, getInternalAnnotationType(IEGLConstants.PROPERTY_RECORDNUMITEM), subType);
                        newAnnotation.setValue(newValue, null, null, compilerOptions, false);
                        newFields.add(newAnnotation);
                    }
                }
                
                if(!newFields.isEmpty()) {
                	IAnnotationBinding newSubtypeAnnotation = new AnnotationBinding(subTypeAnnotationBinding.getCaseSensitiveName(), bindingBeingAnnotated.getDeclaringPart(), subType);
                	for(Iterator iter = subTypeAnnotationBinding.getAnnotationFields().iterator(); iter.hasNext();) {
                		newSubtypeAnnotation.addField((IAnnotationBinding) iter.next());
                	}
                	for(Iterator iter = newFields.iterator(); iter.hasNext();) {
                		newSubtypeAnnotation.addField((IAnnotationBinding) iter.next());
                	}
                	bindingBeingAnnotated.addAnnotation(newSubtypeAnnotation);
                }
        	}
        }
        
        if (isPageHandler()) {
            IAnnotationBinding annotation = dataBinding.getAnnotationFor(new String[] {"egl", "ui"}, "NumElementsItem", path);
            if (annotation != null) {
                Object newValue = resolveNumElementsItem(node, annotation.getValue(), dataBinding, path);
                if (newValue != annotation.getValue()) {
                    AnnotationBinding newAnnotation = new AnnotationBinding(InternUtil.internCaseSensitive("NumElementsItem"), annotation.getDeclaringPart(), annotation.getType());
                    newAnnotation.setValue(newValue, null, null, compilerOptions, false);
                    dataBinding.addAnnotation(newAnnotation, path);
                }
            }
        }

        IAnnotationBinding annotation = dataBinding.getAnnotationFor(new String[] {"egl", "ui", "jsf"}, "SelectFromListItem", path);
        if (annotation != null) {
            Object newValue = resolveSelectFromListItem(node, annotation.getValue(), dataBinding, path);
            if (newValue != annotation.getValue()) {
            	AnnotationBinding newAnnotation = new AnnotationBinding(InternUtil.internCaseSensitive("SelectFromListItem"), null, annotation.getType());
                newAnnotation.setValue(newValue, null, null, compilerOptions, false);
                dataBinding.addAnnotation(newAnnotation, path);
            }
        }
                
        annotation = dataBinding.getAnnotationFor(new String[] {"egl", "core"}, "Redefines", path);
        if (annotation != null) {
            Object newValue = resolveRedefines(node, annotation.getValue(), dataBinding, path);
            if (newValue != annotation.getValue()) {
            	AnnotationBinding newAnnotation = new AnnotationBinding(InternUtil.internCaseSensitive("Redefines"), null, annotation.getType());
                newAnnotation.setValue(newValue, null, null, compilerOptions, false);
                dataBinding.addAnnotation(newAnnotation, path);
            }
        }
        
        List annotationsFor = dataBinding.getAnnotationsFor(path);
        List newAnnotations = new ArrayList();
        for(Iterator iter = annotationsFor.iterator(); iter.hasNext();) {
        	annotation = (IAnnotationBinding) iter.next();
	        IAnnotationTypeBinding annotationType = (IAnnotationTypeBinding) annotation.getType();
	        if(annotationType.hasSingleValue()) {
	        	if(SystemPartManager.FUNCTIONMEMBERREF_BINDING == annotationType.getSingleValueType()) {
	        		Object newValue = resolveFunctionMemberRef(node, annotation, annotation.getValue(), dataBinding, path);
	                if (newValue != annotation.getValue()) {
	                	AnnotationBinding newAnnotation = new AnnotationBinding(InternUtil.internCaseSensitive("Redefines"), null, annotation.getType());
	                    newAnnotation.setValue(newValue, null, null, compilerOptions, false);
	                    newAnnotations.add(newAnnotation);	                    
	                }
	        	}
	        }
        }
        for(Iterator iter = newAnnotations.iterator(); iter.hasNext();) {
        	dataBinding.addAnnotation((IAnnotationBinding) iter.next(), path);
        }
    }
    
    private ITypeBinding getInternalAnnotationType(String name) {
		return new AnnotationTypeBinding(InternUtil.internCaseSensitive(name), SystemPartManager.INTERNALREF_BINDING) {
			public ITypeBinding getSingleValueType() {
				return SystemPartManager.INTERNALREF_BINDING;
			}

			public boolean isApplicableFor(IBinding binding) {
				return true;
			}
		};
	}

	private Object resolveLengthItem(Node node, Object value, IDataBinding varBinding, IDataBinding[] path, boolean validateValue) {

        if (!(value instanceof String)) {
            return value;
        }
        String itemName = (String) value;

        Name name = new ExpressionParser(compilerOptions).parseAsName(itemName);
        if (name == null) {
            return IBinding.NOT_FOUND_BINDING;
        }
        Object result;
        currentScope = new DataBindingScope(currentScope, getDataBindingForAnnotation(varBinding, path), true);
        try {
            bindExpressionName(name);
            
            if (validateValue) {
				AnnotationBinding annBinding = new AnnotationFieldBinding(
						InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_LENGTHITEM), null,
						getInternalAnnotationType(IEGLConstants.PROPERTY_LENGTHITEM), null);
				annBinding.setValue(name, null, null, compilerOptions, false);
				new LengthItemForSerialMessageOrIndexedRecordValidator()
						.validate(node, null, annBinding, problemRequestor, compilerOptions);
			}
            
            result = name;
            //TODO validate the attributes of the length item
        } catch (ResolutionException e) {
        	if(IProblemRequestor.VARIABLE_NOT_FOUND == e.getProblemKind()) {
        		problemRequestor.acceptProblem(
        			node,
					IProblemRequestor.VARIABLE_NOT_FOUND,
					new String[] {
        				IEGLConstants.PROPERTY_LENGTHITEM + " - \"" + itemName + "\""
        			});
        	}
        	else {
        		problemRequestor.acceptProblem(node, e.getProblemKind(), IMarker.SEVERITY_ERROR, e.getInserts());
        	}
            result = IBinding.NOT_FOUND_BINDING;
        }
        currentScope = currentScope.getParentScope();
        return result;
    }

    private Object resolveKeyItem(Node node, Object value, IDataBinding varBinding, IDataBinding[] path) {

        if (!(value instanceof String)) {
            return value;
        }

        IDataBinding dataBinding = getDataBindingForAnnotation(varBinding, path);
        String keyName = (String) value;

        Name name = new ExpressionParser(compilerOptions).parseAsName(keyName);
        if (name == null) {
            return IBinding.NOT_FOUND_BINDING;
        }
        Object result;
        boolean isIndexed = varBinding.getAnnotationFor(new String[] {"egl", "io", "file"}, "IndexedRecord", path) != null;
        boolean isDliSeg = varBinding.getAnnotationFor(new String[] {"egl", "io", "dli"}, "DLISegment", path) != null;
        Scope saveScope = currentScope;
        currentScope = new DataBindingScope(NullScope.INSTANCE, dataBinding, true);
        
        try {
            bindExpressionName(name);
            result = name;
            //TODO validate the attributes of the key item
        } catch (ResolutionException e) {
            if (isIndexed) {
                problemRequestor.acceptProblem(node, IProblemRequestor.PROPERTY_KEY_ITEM_MUST_BE_IN_INDEXED_RECORD, IMarker.SEVERITY_ERROR,
                        new String[] { keyName, dataBinding.getType().getBaseType().getName() });
            } else {
            	problemRequestor.acceptProblem(node, IProblemRequestor.PROPERTY_KEY_ITEM_MUST_BE_IN_DLISEGMENT, IMarker.SEVERITY_ERROR,
                        new String[] { keyName, dataBinding.getType().getBaseType().getName() });
            }
            result = IBinding.NOT_FOUND_BINDING;
        }
        currentScope = saveScope;
        return result;
    }
    
    private Object resolveRecordNumItem(Node node, Object value, IDataBinding varBinding, IDataBinding[] path, boolean validateValue) {

        if (!(value instanceof String)) {
            return value;
        }

        IDataBinding dataBinding = getDataBindingForAnnotation(varBinding, path);
        String keyName = (String) value;

        Name name = new ExpressionParser(compilerOptions).parseAsName(keyName);
        if (name == null) {
            return IBinding.NOT_FOUND_BINDING;
        }
        Object result;
        Scope saveScope = currentScope;
        currentScope = new DataBindingScope(currentScope, dataBinding, true);
        try {
            bindExpressionName(name);

            if (validateValue) {
				AnnotationBinding annBinding = new AnnotationFieldBinding(InternUtil
						.internCaseSensitive(IEGLConstants.PROPERTY_RECORDNUMITEM), null,
						getInternalAnnotationType(IEGLConstants.PROPERTY_LENGTHITEM), null);
				annBinding.setValue(name, null, null, compilerOptions, false);
				new RecordNumItemValidator().validate(node, null, annBinding, problemRequestor, compilerOptions);
			}
            
            result = name;
            //TODO validate the attributes of the key item
        } catch (ResolutionException e) {
            problemRequestor.acceptProblem(node, e.getProblemKind(), IMarker.SEVERITY_ERROR, e.getInserts());
            result = IBinding.NOT_FOUND_BINDING;
        }
        currentScope = saveScope;
        return result;
    }
    
    private Object resolveSelectFromListItem(Node node, Object value, IDataBinding varBinding, IDataBinding[] path) {

        if (!(value instanceof String)) {
            return value;
        }

        String selectFromListName = (String) value;

        Name name = new ExpressionParser(compilerOptions).parseAsName(selectFromListName);
        if (name == null) {
            return IBinding.NOT_FOUND_BINDING;
        }
        Object result;
        try {
            IDataBinding db = bindExpressionName(name);
            result = name;
            if(db != IBinding.NOT_FOUND_BINDING) {
            	boolean valid = false;
            	if(isArray(db)) {
            		valid = ITypeBinding.FLEXIBLE_RECORD_BINDING != db.getDeclaringPart().getKind() &&
            		        ITypeBinding.PRIMITIVE_TYPE_BINDING == db.getType().getBaseType().getKind();
            	}
            	if(!valid) {
            		problemRequestor.acceptProblem(node, IProblemRequestor.SELECTFROMLIST_MUST_BE_ARRAY, new String[] {value.toString()});
            	}
            }
        } catch (ResolutionException e) {
        	if(e.getProblemKind() == IProblemRequestor.VARIABLE_ACCESS_AMBIGUOUS) {
        		problemRequestor.acceptProblem(node, IProblemRequestor.INVALID_PAGEHANDLER_PROPERTY_VALUE_IS_AMBIGUOUS, IMarker.SEVERITY_ERROR, new String[] {IEGLConstants.PROPERTY_SELECTFROMLISTITEM, selectFromListName, getDataBindingForAnnotation(varBinding, path).getName()});
        	}
        	else {
        		problemRequestor.acceptProblem(node, IProblemRequestor.INVALID_PAGEHANDLER_PROPERTY_VALUE_CANNOT_RESOLVE, IMarker.SEVERITY_ERROR, new String[] {IEGLConstants.PROPERTY_SELECTFROMLISTITEM, selectFromListName, getDataBindingForAnnotation(varBinding, path).getName()});
        	}
            result = IBinding.NOT_FOUND_BINDING;
        }
        return result;
    }
    
    private boolean isArray(IDataBinding db) {
    	if(IDataBinding.STRUCTURE_ITEM_BINDING == db.getKind()) {
    		StructureItemBinding sItemBinding = (StructureItemBinding) db;
    		if(ITypeBinding.DATATABLE_BINDING == sItemBinding.getEnclosingStructureBinding().getKind() ||
    		   sItemBinding.isMultiplyOccuring()) {
    			return true;
    		}
    	}
    	ITypeBinding tBinding = db.getType();
    	return tBinding == null || ITypeBinding.ARRAY_TYPE_BINDING == tBinding.getKind();
    }

    private Object resolveMQRecordProperty(Node node, Object value, IDataBinding varBinding, IDataBinding[] path, String annotationType) {

        if (!(value instanceof String)) {
            return value;
        }

        String propName = (String) value;

        Name name = new ExpressionParser(compilerOptions).parseAsName(propName);
        if (name == null) {
            return IBinding.NOT_FOUND_BINDING;
        }
        Object result;
        try {
            bindExpressionName(name);
            result = name;
            if (!isBasicRecordReferece(name.resolveDataBinding())) {
                problemRequestor.acceptProblem(node, IProblemRequestor.PROPERTY_MUST_BE_BASIC_RECORD, IMarker.SEVERITY_ERROR, new String[] {propName, annotationType});
            }
        } catch (ResolutionException e) {
        	if(IProblemRequestor.VARIABLE_NOT_FOUND == e.getProblemKind()) {
	            problemRequestor.acceptProblem(
	            	node,
					IProblemRequestor.PROPERTY_DOESNT_RESOLVE,
					new String[] {
						propName,
						annotationType,
						varBinding.getName()
	            	});
        	}
        	else if(IProblemRequestor.VARIABLE_ACCESS_AMBIGUOUS == e.getProblemKind()) {
        		problemRequestor.acceptProblem(
	            	node,
					IProblemRequestor.PROPERTY_AMBIGUOUS,
					new String[] {
						propName,
						annotationType,
						varBinding.getName()
	            	});
        	}
        	else {
        		problemRequestor.acceptProblem(
	            	node,
					e.getProblemKind(),
					IMarker.SEVERITY_ERROR,
					e.getInserts());
        	}
            result = IBinding.NOT_FOUND_BINDING;
        }
        return result;
    }
    
    protected void processResolvableProperty(IAnnotationTypeBinding annotationType, Object value, IDataBinding dataBinding,
            IDataBinding[] path, Expression expr) {
        if (value == null) {
            return;
        }

        IAnnotationBinding annotation = dataBinding.getAnnotationFor(annotationType, path);
        if (annotation == null) {
        	if(dataBinding.getType() != null && dataBinding.getType().isPartBinding()) {        		
        		IPartSubTypeAnnotationTypeBinding subType = ((IPartBinding) dataBinding.getType()).getSubType();
        		if(subType != null) {
					annotation = (IAnnotationBinding) dataBinding.getAnnotation(subType).findData(annotationType.getName());
	        		if(IBinding.NOT_FOUND_BINDING == annotation) {
	        			return;
	        		}
        		}
        		else {
        			return;
        		}
        	}
        	else {        	
        		return;
        	}
        }
        
        if (annotationIs(annotationType, new String[] {"egl", "ui", "jsf"}, "SelectFromListItem")) {
            annotation.setValue(resolveSelectFromListItem(expr, value, dataBinding, path), null, null, compilerOptions, false);
            return;
        }

        if (annotationIs(annotationType, new String[] {"egl", "ui"}, "SelectedIndexItem")) {
            if (isUIRecordItem(dataBinding, path)) {
                annotation.setValue(resolveSelectedIndexItem(expr, value, dataBinding, path), null, null, compilerOptions, false);
            }
            return;
        }

        if (annotationIs(annotationType, new String[] {"egl", "core"}, "redefines")) {
            annotation.setValue(resolveRedefines(expr, value, dataBinding, path), null, null, compilerOptions, false);
            return;
        }
        
        if (annotationIs(annotationType, new String[] {"egl", "ui"}, "NumElementsItem")) {
            if (isUIRecordItem(dataBinding, path) || isPageHandler()) {
                annotation.setValue(resolveNumElementsItem(expr, value, dataBinding, path), null, null, compilerOptions, false);
            }
            return;
        }
        
        String annotationName = annotation.getName();
        String caseSensitiveAnnotationName = annotation.getCaseSensitiveName();
        IAnnotationTypeBinding enclosingAnnotationType = annotation.getEnclosingAnnotationType();
        
        if(annotationIs(enclosingAnnotationType, new String[] {"egl", "io", "mq"}, "MQRecord")) {        	
        	if(InternUtil.intern(IEGLConstants.PROPERTY_PUTOPTIONSRECORD) == annotationName ||
        	   InternUtil.intern(IEGLConstants.PROPERTY_OPENOPTIONSRECORD) == annotationName ||
        	   InternUtil.intern(IEGLConstants.PROPERTY_GETOPTIONSRECORD) == annotationName ||
        	   InternUtil.intern(IEGLConstants.PROPERTY_MSGDESCRIPTORRECORD) == annotationName ||
        	   InternUtil.intern(IEGLConstants.PROPERTY_QUEUEDESCRIPTORRECORD) == annotationName) {
                annotation.setValue(resolveMQRecordProperty(expr, value, dataBinding, path, caseSensitiveAnnotationName), null, null, compilerOptions, false);
                return;
            }
        }
        
        if(annotationIs(enclosingAnnotationType, new String[] {"egl", "io", "file"}, "RelativeRecord")) {
        	annotation.setValue(resolveRecordNumItem(expr, value, dataBinding, path, false), null, null, compilerOptions, false);
        }
        
        if(annotationIs(enclosingAnnotationType, new String[] {"egl", "io", "file"}, "IndexedRecord") ||
           annotationIs(enclosingAnnotationType, new String[] {"egl", "io", "mq"}, "MQRecord") ||
           annotationIs(enclosingAnnotationType, new String[] {"egl", "io", "file"}, "SerialRecord") ||
           annotationIs(enclosingAnnotationType, new String[] {"egl", "io", "dli"}, "DLISegment")) {
         	if(InternUtil.intern(IEGLConstants.PROPERTY_NUMELEMENTSITEM) == annotationName) {
          		annotation.setValue(resolveNumElementsItem(expr, value, dataBinding, path), null, null, compilerOptions, false);
          		return;
          	}
          	
          	if(InternUtil.intern(IEGLConstants.PROPERTY_LENGTHITEM) == annotationName) {
          		annotation.setValue(resolveLengthItem(expr, value, dataBinding, path, false), null, null, compilerOptions, false);
          		return;
          	}
         }
        
        if(annotationType.hasSingleValue()) {
        	if(SystemPartManager.FUNCTIONMEMBERREF_BINDING == annotationType.getSingleValueType()) {
        		annotation.setValue(resolveFunctionMemberRef(expr, annotation, value, dataBinding, path), null, null, compilerOptions, false);
          		return;
        	}
        }
    }

    protected boolean isPageHandler() {
        //Override this in the Binder for pageHandler
        return false;
    }
    
    private boolean isBasicRecordReferece(IDataBinding dataBinding) {
    	if (dataBinding.getType().getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
    		return false;
    	}
        return dataBinding.getAnnotation(new String[] {"egl", "core"}, "BasicRecord") != null;
    }
    
    private IAnnotationBinding getField(IAnnotationBinding aBinding, String fieldName) {
    	if (!Binding.isValidBinding(aBinding)) {
    		return null;
    	}
    	
		IDataBinding fieldBinding = aBinding.findData(fieldName);
		return IBinding.NOT_FOUND_BINDING == fieldBinding ? null : (IAnnotationBinding) fieldBinding;
	}
    
    
    private void issueErrorForPropertyOverrides(IDataBinding fieldBinding, SettingsBlock settings) {
    	if (!(fieldBinding instanceof DataBinding) || ((DataBinding)fieldBinding).getPropertyOverrides().isEmpty()) {
    		return;
    	}
    	
    	final Stack<IDataBinding> stack = new Stack();
    	stack.push(fieldBinding);
		final Node[] errorNode = new Node[1];
   	
    	settings.accept(new AbstractASTExpressionVisitor() {
    		public boolean visit(Assignment assignment) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			
    			//if this is an annotation assignment
    			if (Binding.isValidBinding(assignment.resolveBinding())) {
    				if (stack.size() > 1) {
    					errorNode[0] = assignment;
    				}
    			}
    			return false;
    		}
    		
    		public boolean visit(AnnotationExpression annotationExpression) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			
				if (stack.size() > 1) {
					errorNode[0] = annotationExpression;
				}
				return false;
    		}
    		
    		public boolean visit(SetValuesExpression setValuesExpression) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			
    			IDataBinding dBinding = setValuesExpression.getExpression().resolveDataBinding();
				if (Binding.isValidBinding(dBinding)) {					
					if (dBinding.getKind() == IDataBinding.ANNOTATION_BINDING) {
						if (stack.size() > 1) {
							errorNode[0] = setValuesExpression.getExpression();
						}
					}
					else {
						stack.push(dBinding);
						setValuesExpression.getSettingsBlock().accept(this);
					}
				}
				return false; 			
    		}
    		
    		public void endVisit(SetValuesExpression setValuesExpression) {
    			IDataBinding dBinding = setValuesExpression.getExpression().resolveDataBinding();
				if (Binding.isValidBinding(dBinding)) {					
					if (dBinding.getKind() == IDataBinding.ANNOTATION_BINDING) {}
					else {
						stack.pop();
					}
				}
    		}
    		
    		public boolean visitExpression(Expression expression) {
    			return false;
    		}
    		
    	});
		problemRequestor.acceptProblem(errorNode[0], IProblemRequestor.PROPERTY_OVERRIDES_NOT_SUPPORTED, IMarker.SEVERITY_ERROR, new String[] {fieldBinding.getCaseSensitiveName()});
    }
    
    private void issueErrorForInitialization(SettingsBlock settings, final String fieldName, int errorNo) {
		final Node[] errorNode = new Node[1];
    	settings.accept(new AbstractASTExpressionVisitor() {
    		public boolean visit(Assignment assignment) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			
    			//check if it was an annotation type assignment
    			if (assignment.resolveBinding() == null) {
    				IDataBinding dBinding = assignment.getLeftHandSide().resolveDataBinding();
    				if (Binding.isValidBinding(dBinding) && (dBinding.getKind() != IDataBinding.ANNOTATION_BINDING)) {
    	        		errorNode[0] = assignment;
    				}
    			}
    			return false;
    		}
    		
    		public boolean visit(AnnotationExpression annotationExpression) {
    			return false;
    		}
    		
    		public boolean visit(SetValuesExpression setValuesExpression) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			IDataBinding dBinding = setValuesExpression.getExpression().resolveDataBinding();
				if (Binding.isValidBinding(dBinding) && (dBinding.getKind() != IDataBinding.ANNOTATION_BINDING)) {
					setValuesExpression.getSettingsBlock().accept(this);
				}
				return false; 			
    		}
    		
    		public boolean visitExpression(Expression expression) {
    			if (errorNode[0] != null) {
    				return false;
    			}
    			errorNode[0] = expression;
    			return false;
    		}
    		
    	});
    	if (errorNode[0] != null) {
    		problemRequestor.acceptProblem(errorNode[0], errorNo, IMarker.SEVERITY_ERROR, new String[] {fieldName});
    	}
    }
 }
