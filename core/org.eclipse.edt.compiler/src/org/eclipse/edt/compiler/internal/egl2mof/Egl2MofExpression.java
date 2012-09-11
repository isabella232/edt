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
package org.eclipse.edt.compiler.internal.egl2mof;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.LikeMatchesExpression;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.ArrayLiteral;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.BooleanLiteral;
import org.eclipse.edt.mof.egl.BytesLiteral;
import org.eclipse.edt.mof.egl.ConstructorInvocation;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.DecimalLiteral;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.DelegateInvocation;
import org.eclipse.edt.mof.egl.DynamicAccess;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FloatingPointLiteral;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionInvocation;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IsAExpression;
import org.eclipse.edt.mof.egl.IsNotExpression;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MultiOperandExpression;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.NumericLiteral;
import org.eclipse.edt.mof.egl.ObjectExpression;
import org.eclipse.edt.mof.egl.ObjectExpressionEntry;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.SetValuesExpression;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.StringLiteral;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.SubstringAccess;
import org.eclipse.edt.mof.egl.SuperExpression;
import org.eclipse.edt.mof.egl.TernaryExpression;
import org.eclipse.edt.mof.egl.ThisExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.serialization.IEnvironment;


abstract class Egl2MofExpression extends Egl2MofStatement {
	
	private Stack<Expression> sveStack = new Stack<Expression>();
	private Stack<Type> sveTypeStack = new Stack<Type>();
	private Stack<LHSExpr> localStack = new Stack<LHSExpr>();
 
	Egl2MofExpression(IEnvironment env) {
		super(env);
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.AsExpression expr) {
		AsExpression asExpr = factory.createAsExpression();
		stack.push(asExpr);
		setElementInformation(expr, asExpr);
		expr.getExpression().accept(this);
		asExpr.setObjectExpr((Expression)stack.pop());
		Type eType = (Type)mofTypeFor(expr.resolveType());
		asExpr.setEType(eType);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ArrayAccess node) {
		node.getArray().accept(this);
		Expression arrayExpression = (Expression)stack.pop();

		Expression lastAC = null;
		for (int i = 0; i < node.getIndices().size(); i++) {
			org.eclipse.edt.compiler.core.ast.Expression indexExpr = (org.eclipse.edt.compiler.core.ast.Expression) node.getIndices().get(i);
			indexExpr.accept(this);
			Expression index = (Expression)stack.pop();
			Type exprType = indexExpr.resolveType();
			if (exprType == null) {
				ArrayAccess ax = factory.createArrayAccess();
				ax.setIndex(index);
				ax.setArray(arrayExpression);
				lastAC = ax;
			}
			else {
			
				boolean isDynamicAccess = TypeUtils.isTextType((Type)mofTypeFor(exprType));
				
				if (isDynamicAccess) {
					DynamicAccess ax = factory.createDynamicAccess();
					ax.setAccess(index);
					ax.setExpression(arrayExpression);
					lastAC = ax;
				} else {
					ArrayAccess ax = factory.createArrayAccess();
					ax.setIndex(IRUtils.createAsExpression(index, (Type)getMofSerializable(Type_EGLInt)));
					ax.setArray(arrayExpression);
					lastAC = ax;
				}
			}

		}
		setElementInformation(node, lastAC);
		stack.push(lastAC);
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.ArrayLiteral node) {
		List<Expression> entries = new ArrayList<Expression>();
		for(org.eclipse.edt.compiler.core.ast.Expression expr : node.getExpressions()) {
			expr.accept(this);
			entries.add((Expression)stack.pop());
		}
		ArrayLiteral lit = factory.createArrayLiteral();
		lit.getEntries().addAll(entries);

		setElementInformation(node, lit);
		stack.push(lit);
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.ObjectExpression node) {
		List<ObjectExpressionEntry> entries = new ArrayList<ObjectExpressionEntry>();
		for (org.eclipse.edt.compiler.core.ast.ObjectExpressionEntry entry : (List<org.eclipse.edt.compiler.core.ast.ObjectExpressionEntry>)node.getEntries()) {
			ObjectExpressionEntry entryIR = factory.createObjectExpressionEntry();
			entryIR.setId(entry.getId());
			entry.getExpression().accept(this);
			entryIR.setExpression((Expression) stack.pop());
			entries.add(entryIR);
		}
		
		ObjectExpression objExpr = factory.createObjectExpression();
		objExpr.getEntries().addAll(entries);
		setElementInformation(node,objExpr);
		stack.push(objExpr);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Assignment assignment) {
		Assignment expr = factory.createAssignment();
		expr.setOperator(assignment.getOperator().toString());
		setElementInformation(assignment, expr);
		stack.push(expr);
		assignment.getLeftHandSide().accept(this);
		expr.setLHS((LHSExpr)eStackPop());
		assignment.getRightHandSide().accept(this);
		expr.setRHS((Expression)eStackPop());
		
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.BinaryExpression binExpr) {
		BinaryExpression expr = factory.createBinaryExpression();
		setElementInformation(binExpr, expr);
		stack.push(expr);
		binExpr.getFirstExpression().accept(this);
		Expression arg1 = (Expression)eStackPop();
		binExpr.getSecondExpression().accept(this);
		Expression arg2 = (Expression)eStackPop();
		expr.setLHS(arg1);
		expr.setRHS(arg2);
		expr.setOperator(binExpr.getOperator().toString());
		
//		// Handle implicit cast operation if necessary
//		IRUtils.makeCompatible(expr);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.TernaryExpression ternaryExpr) {
		TernaryExpression expr = factory.createTernaryExpression();
		setElementInformation(ternaryExpr, expr);
		stack.push(expr);
		ternaryExpr.getFirstExpr().accept(this);
		Expression arg1 = (Expression)eStackPop();
		ternaryExpr.getSecondExpr().accept(this);
		Expression arg2 = (Expression)eStackPop();
		ternaryExpr.getThirdExpr().accept(this);
		Expression arg3 = (Expression)eStackPop();
		
		expr.setFirst(arg1);
		expr.setSecond(arg2);
		expr.setThird(arg3);
		expr.setOperator("?");
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.BooleanLiteral literal) {
		BooleanLiteral lit = factory.createBooleanLiteral();
		lit.setValue(literal.getValue());
		setElementInformation(literal, lit);
		stack.push(lit);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.BytesLiteral literal) {
		BytesLiteral lit = factory.createBytesLiteral();
		lit.setValue(literal.getValue());
		setElementInformation(literal, lit);
		stack.push(lit);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.FloatLiteral literal) {
		FloatingPointLiteral lit = factory.createFloatingPointLiteral();
		if (literal.getLiteralKind() == LiteralExpression.SMALLFLOAT_LITERAL) {
			lit.setType(IRUtils.getEGLPrimitiveType(Type_Smallfloat));
		}
		else {
			lit.setType(IRUtils.getEGLPrimitiveType(Type_Float));
		}
		lit.setValue(literal.getValue());
		setElementInformation(literal, lit);
		stack.push(lit);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.DecimalLiteral literal) {
		DecimalLiteral lit = factory.createDecimalLiteral();
		lit.setValue(literal.getValue());
		setElementInformation(literal, lit);
		stack.push(lit);
		return false;
	}


	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.FieldAccess fieldAccess) {
		Type type = (Type)mofTypeFor(fieldAccess.getPrimary().resolveType());
		if (TypeUtils.isDynamicType(type)) {
			DynamicAccess expr = factory.createDynamicAccess();
			setElementInformation(fieldAccess, expr);
			StringLiteral index = factory.createStringLiteral();
			index.setValue(fieldAccess.getCaseSensitiveID());
			expr.setAccess(index);
			stack.push(expr);
			fieldAccess.getPrimary().accept(this);
			expr.setExpression((Expression)stack.pop());
		}
		else {
			MemberAccess expr = factory.createMemberAccess();
			expr.setId(fieldAccess.getCaseSensitiveID());
			setElementInformation(fieldAccess, expr);
			stack.push(expr);
			fieldAccess.getPrimary().accept(this);
			expr.setQualifier((Expression)eStackPop());
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.FunctionInvocation node) {
		InvocationExpression fi;
		
 		Type typeBinding = node.getTarget().resolveType();
		List<FunctionParameter> functionParmBindings;
		if (typeBinding instanceof Delegate) {
			
			functionParmBindings =  ((Delegate)typeBinding).getParameters();
			fi = factory.createDelegateInvocation();
			fi.setId(((Delegate) typeBinding).getCaseSensitiveName());
			node.getTarget().accept(this);
			((DelegateInvocation)fi).setExpression((Expression)stack.pop());
		}
		else {
						
			Element targetBinding = (Element)node.getTarget().resolveElement();
			Function functionBinding = null;
			Part declarer = null;
			if (targetBinding instanceof Function) {
				functionBinding = (Function) targetBinding;
				functionParmBindings =  functionBinding.getParameters();
				declarer = (Part)functionBinding.getContainer();
			}
			else {
				functionParmBindings =  new ArrayList<FunctionParameter>();
				
			}
			
			boolean isStatic = functionBinding != null && (functionBinding.isStatic() || functionBinding.getContainer() instanceof Library);
			if (node.getTarget() instanceof org.eclipse.edt.compiler.core.ast.ThisExpression
					|| node.getTarget() instanceof org.eclipse.edt.compiler.core.ast.SuperExpression) {
				// Constructor invocation.
				fi = factory.createConstructorInvocation();
				fi.setId("constructor");
				
				Expression expr;
				
				if (node.getTarget() instanceof org.eclipse.edt.compiler.core.ast.ThisExpression) {
					expr = factory.createThisExpression();
					((ThisExpression)expr).setThisObject(getCurrentFunctionMember().getContainer());
				}
				else {
					expr = factory.createSuperExpression();
					((SuperExpression)expr).setThisObject(getCurrentFunctionMember().getContainer());
				}
				
				((ConstructorInvocation)fi).setExpression(expr);
			}
			else if (node.getTarget() instanceof SimpleName && !isStatic) {
				if (functionBinding == null || isSuperTypeMember(functionBinding)) {
					// Qualify with this to get QualifiedFunctionInvocation which will do dynamic lookup
					fi = factory.createQualifiedFunctionInvocation();
					fi.setId(node.getTarget().getCanonicalString());
					if (node.getTarget() instanceof org.eclipse.edt.compiler.core.ast.SuperExpression) {
						node.getTarget().accept(this);
						((QualifiedFunctionInvocation)fi).setQualifier((Expression)stack.pop());
					}
					else {
						ThisExpression thisExpr = factory.createThisExpression();
						thisExpr.setThisObject(getCurrentFunctionMember().getContainer());
						((QualifiedFunctionInvocation)fi).setQualifier(thisExpr);
					}
				}
				else {
					
					Member mbr = (Member)getEObjectFor(functionBinding);
					String id=null;
					if (mbr instanceof FunctionMember) {
						id =  ((FunctionMember)mbr).getCaseSensitiveName();
					}
					else {
						if(functionBinding != null) {
							id = functionBinding.getCaseSensitiveName();
						}
					}

					fi = factory.createFunctionInvocation();
					fi.setId(id);
					((FunctionInvocation)fi).setTarget(mbr);
				}
	
			}
			else {
				if (isStatic && node.getTarget() instanceof org.eclipse.edt.compiler.core.ast.Name) {
					
					if (mofTypeFor(declarer) == currentPart) {
						Member mbr = (Member)getEObjectFor(functionBinding);
						String id=null;
						if (mbr instanceof FunctionMember) {
							id =  ((FunctionMember)mbr).getCaseSensitiveName();
						}
						else {
							if(functionBinding != null) {
								id = functionBinding.getCaseSensitiveName();
							}
						}
						
						fi = factory.createFunctionInvocation();
						fi.setId(id);
						((FunctionInvocation)fi).setTarget(mbr);
					}
					else {
						fi = factory.createQualifiedFunctionInvocation();
						fi.setId(functionBinding.getCaseSensitiveName());
						PartName partName = factory.createPartName();
						partName.setId(declarer.getCaseSensitiveName());
						partName.setPackageName(declarer.getCaseSensitivePackageName());
						setElementInformation(node.getTarget(), partName);
						((QualifiedFunctionInvocation)fi).setQualifier(partName);
					}
				}
				else {
					if (node.getTarget() instanceof FieldAccess) {
						FieldAccess fa = (FieldAccess) node.getTarget();
						if (fa.getPrimary() instanceof org.eclipse.edt.compiler.core.ast.ThisExpression
								|| fa.getPrimary() instanceof org.eclipse.edt.compiler.core.ast.SuperExpression) {
							
							if (functionBinding == null || isSuperTypeMember(functionBinding)) {
								// Qualify with this to get QualifiedFunctionInvocation which will do dynamic lookup
								fi = factory.createQualifiedFunctionInvocation();
								fi.setId(functionBinding.getCaseSensitiveName());
								if (fa.getPrimary() instanceof org.eclipse.edt.compiler.core.ast.SuperExpression) {
									SuperExpression superExpr = factory.createSuperExpression();
									superExpr.setThisObject(getCurrentFunctionMember().getContainer());
									((QualifiedFunctionInvocation)fi).setQualifier(superExpr);
								}
								else {
									ThisExpression thisExpr = factory.createThisExpression();
									thisExpr.setThisObject(getCurrentFunctionMember().getContainer());
									((QualifiedFunctionInvocation)fi).setQualifier(thisExpr);
								}
							}
							else {		
								
								Member mbr = (Member)getEObjectFor(functionBinding);
								String id=null;
								if (mbr instanceof FunctionMember) {
									id =  ((FunctionMember)mbr).getCaseSensitiveName();
								}
								else {
									if(functionBinding != null) {
										id = functionBinding.getCaseSensitiveName();
									}
								}

								fi = factory.createFunctionInvocation();
								fi.setId(id);
								((FunctionInvocation)fi).setTarget(mbr);
							}
						}
						else {
							fi = factory.createQualifiedFunctionInvocation();
							fi.setId(functionBinding.getCaseSensitiveName());
							fa.getPrimary().accept(this);
							((QualifiedFunctionInvocation)fi).setQualifier((Expression)stack.pop());
						}					

					}
					else {
						if (node.getTarget() instanceof QualifiedName) {
							fi = factory.createQualifiedFunctionInvocation();
							QualifiedName name = (QualifiedName)node.getTarget();
							fi.setId(name.getCaseSensitiveIdentifier());
							name.getQualifier().accept(this);
							((QualifiedFunctionInvocation)fi).setQualifier((Expression)stack.pop());
						}
						else {
							//Catch error cases
							fi = factory.createFunctionInvocation();
							fi.setId(node.getTarget().getCanonicalString());

						}
					}
				}
			}
		}
		
		int index = 0;
		for (Iterator<org.eclipse.edt.compiler.core.ast.Expression> iter = node.getArguments().iterator(); iter.hasNext();) {
			org.eclipse.edt.compiler.core.ast.Expression argExpr = iter.next();
			argExpr.accept(this);
			Expression expr = (Expression)stack.pop();
			
			// TODO: find out why this is being done this way
//			if (isFormatFunction && index == 2 && expr instanceof org.eclipse.edt.mof.egl.api.StringLiteral) {
//				org.eclipse.edt.mof.egl.api.StringLiteral lit = (org.eclipse.edt.mof.egl.api.StringLiteral) expr;
//				lit.setValue(new TimeStampAndIntervalPatternFixer(lit.getValue()).toString());
//			}

			// if passing any type to anything other than an any, must change
			// the arg to an AsExpression...only need to do this if the parm is an input type...do not need
			// to massage the arguments for output parms
			FunctionParameter parm = null;
			if (functionParmBindings.size() > 0) {
				if (functionParmBindings.size() > index) {
					parm = functionParmBindings.get(index);					
				}
				else {
					parm = functionParmBindings.get(functionParmBindings.size() - 1);
				}
			}
			
			fi.getArguments().add(expr);
			index++;
		}
		setElementInformation(node, fi);
		stack.push(fi);
		return false;
	}
	

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.InExpression inExpression) {
		TernaryExpression inExpr = factory.createTernaryExpression();
		setElementInformation(inExpression, inExpr);
		stack.push(inExpr);
		inExpression.getFirstExpression().accept(this);
		inExpr.setFirst((Expression)stack.pop());
		inExpression.getSecondExpression().accept(this);
		inExpr.setSecond((Expression)stack.pop());
		inExpr.setOperator(MultiOperandExpression.Op_IN);
		if (inExpression.getFromExpression() != null) {
			inExpression.getFromExpression().accept(this);
			inExpr.setThird((Expression)stack.pop());			
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.IntegerLiteral literal) {
		IntegerLiteral lit = factory.createIntegerLiteral();
		switch (literal.getLiteralKind()) {
			case LiteralExpression.BIGINT_LITERAL:
				lit.setType(IRUtils.getEGLPrimitiveType(Type_Bigint));
				break;
			case LiteralExpression.SMALLINT_LITERAL:
				lit.setType(IRUtils.getEGLPrimitiveType(Type_Smallint));
				break;
			case LiteralExpression.INTEGER_LITERAL:
			default:
				lit.setType(IRUtils.getEGLPrimitiveType(Type_Int));
				break;
		}
		lit.setValue(literal.getValue());
		setElementInformation(literal, lit);
		stack.push(lit);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.IsAExpression expr) {
		IsAExpression isaExpr = factory.createIsAExpression();
		setElementInformation(expr, isaExpr);
		stack.push(isaExpr);
		expr.getExpression().accept(this);
		isaExpr.setObjectExpr((Expression)stack.pop());
		Type type = (Type)mofTypeFor(expr.getType().resolveType());
		isaExpr.setEType(type);
		return false;
	}

	@Override
	public boolean visit(ParenthesizedExpression parenExpr) {
		return true;
	}
	
	private Name createNameForPart(Type binding) {
		if (binding instanceof StructPart 
				|| binding instanceof ExternalType
				|| binding instanceof Enumeration
				|| binding instanceof Program
				|| binding instanceof Interface
				|| binding instanceof Service) {
			// Is only a proper reference to a part if given part type is allowed to have
			// field references to the part itself (static reference) as opposed to field
			// of an instance.  SimpleName AST values may be referencing a Part in the case
			// where the reference is to a global variable outside the scope of a TopLevelFunction
			// that is being compiled independently
			PartName name = factory.createPartName();
			String packageName;
			packageName = ((Part)binding).getCaseSensitivePackageName();
			//TODO - remove this temporary hardcoded remapping of "egl.core" system parts when the front
			// end references to system parts are handled through a proper System Scope configuration
			// that will bind to the configured part
			if (packageName.equals("egl.core")) {
				packageName = "egl.lang";
			}

			name.setPackageName(packageName);
			name.setId(((Part)binding).getCaseSensitiveName());
			return name;
		}
		else {
			return factory.createDanglingReference();
		}
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.SimpleName node) {
		Element binding = (Element)node.resolveElement(); 
		// Since there was no binding this is an invalid name
		if (binding == null) {
			Name invalid;
			if (currentPart instanceof FunctionPart) {
				invalid = factory.createDanglingReference();
			}
			else {
				invalid = factory.createInvalidName();
			}
			invalid.setId(node.getCanonicalName());
			setElementInformation(node, invalid);
			stack.push(invalid);
			return false;
		}
		
		Name name = null;
		if (binding instanceof Part) {
			name = createNameForPart((Part)binding);
			name.setId(((Part)binding).getCaseSensitiveName());
		}
		else if(binding instanceof Member){
			name = factory.createMemberName();
			if(((Member)binding).getContainer() == null && TypeUtils.isDynamicType(((Member) binding).getType())){
				name.setId(node.getCaseSensitiveIdentifier());
			}
			else{
				name.setId(((Member)binding).getCaseSensitiveName());
			}
		}
			
		Element qualifier = null;
		
		if(binding instanceof Member && 
				isInOtherLibrary(((Member)binding).getContainer())){
			qualifier = ((Member)binding).getContainer();
		}
		else if(binding instanceof EnumerationEntry){
			qualifier = ((Member)binding).getContainer();
		}
		
		if (qualifier != null) {
			
			if (qualifier instanceof Part) {
				name = (Name)addQualifier(createNameForPart((Part)qualifier), name);
			}
			else {
				Element context = (Element)getEObjectFor(qualifier);
				name = (Name)addQualifier(context, name);
			}
		}
		if (name instanceof MemberName) {
			if (isSuperTypeMember(binding)) {
				ThisExpression thisExpr = factory.createThisExpression();
				thisExpr.setThisObject((Part)currentPart);
				name = (Name)addQualifier(thisExpr, name);
			}
			else {
				EObject result = getEObjectFor(binding);
				if (result instanceof Member) {
					Member mbr = (Member)result;
					((MemberName)name).setMember(mbr);
				}
			}
		}
		setElementInformation(node, name);
		stack.push(name);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.QualifiedName name) {
		Object element = name.resolveElement();
		if (element instanceof Member &&
				((Member)element).getContainer() == null && 
				TypeUtils.isDynamicType(((Member)element).getType())) {
			DynamicAccess expr = factory.createDynamicAccess();
			setElementInformation(name, expr);
			StringLiteral index = factory.createStringLiteral();
			index.setValue(name.getCaseSensitiveIdentifier());
			expr.setAccess(index);
			stack.push(expr);
			name.getQualifier().accept(this);
			expr.setExpression((Expression)stack.pop());
		}
		else {
			MemberAccess nameExpr = factory.createMemberAccess();
			setElementInformation(name, nameExpr);
			stack.push(nameExpr);
			nameExpr.setId(name.getCaseSensitiveIdentifier());
			
			Element qualBinding = (Element)name.getQualifier().resolveElement();
			if (qualBinding instanceof Part && 
				!isInUse((Part)qualBinding) ||
				qualBinding instanceof Enumeration){
				nameExpr.setQualifier(createNameForPart((Part)qualBinding));
			}
			else {
				name.getQualifier().accept(this);
				nameExpr.setQualifier((Expression)stack.pop());
			}
		}
		return false;
	}

	private boolean isInUse(Type binding){
		return binding instanceof Library && 
				currentPart instanceof LogicAndDataPart &&
				((LogicAndDataPart)currentPart).getUsedParts().contains(mofTypeFor((Library)binding));
	}
	private boolean isInOtherLibrary(Container binding){
		return (binding instanceof Library && 
				!mofTypeFor((Library)binding).equals(currentPart));
	}
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.NullLiteral literal) {
		NullLiteral lit = factory.createNullLiteral();
		setElementInformation(literal, lit);
		stack.push(lit);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.StringLiteral literal) {
		StringLiteral lit = factory.createStringLiteral();
		lit.setValue(literal.getValue());
		lit.setIsHex(literal.isHex());
		setElementInformation(literal, lit);
		stack.push(lit);
		return false;
	}

	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.IsNotExpression isNotExpression) {
		IsNotExpression expr = factory.createIsNotExpression();
		setElementInformation(isNotExpression, expr);
		stack.push(expr);
		isNotExpression.getFirstExpression().accept(this);
		expr.setExpr((Expression)stack.pop());
		expr.setOperation(isNotExpression.getOperator().toString());
		SimpleName mnemonic = (SimpleName)isNotExpression.getSecondExpression();
		expr.setMnemonic(mnemonic.getIdentifier());
		return false;
	}

	@Override
	public boolean visit(LikeMatchesExpression likeMatchesExpression) {
		TernaryExpression expr = factory.createTernaryExpression();
		setElementInformation(likeMatchesExpression, expr);
		stack.push(expr);
		likeMatchesExpression.getFirstExpression().accept(this);
		expr.setFirst((Expression)stack.pop());
		likeMatchesExpression.getSecondExpression().accept(this);
		expr.setSecond((Expression)stack.pop());
		expr.setOperator(likeMatchesExpression.getOperator().toString());
		if (likeMatchesExpression.getEscapeString() != null) {
			StringLiteral lit = factory.createStringLiteral();
			lit.setValue(likeMatchesExpression.getEscapeString());
			setElementInformation(likeMatchesExpression, lit);
			expr.setThird(lit);			
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.NewExpression newExpression) {
		NewExpression expr = factory.createNewExpression();
		Type type = newExpression.resolveType();
		if (type == null) {
			stack.push(expr);
			return false;
		}
		
		EObject obj = mofTypeFor(type);
		if (!(obj instanceof Type)) {
			stack.push(expr);
			return false;
		}
		
		Type mofType = (Type)obj;
		expr.setId(type.getTypeSignature());
		setElementInformation(newExpression, expr);
		if(newExpression.getType() instanceof NameType){
			for(org.eclipse.edt.compiler.core.ast.Expression argument : ((NameType)newExpression.getType()).getArguments()){
				argument.accept(this);
				expr.getArguments().add((Expression)stack.pop());
			}
		}
		
		if (newExpression.getType().isArrayType()) {
			processNewArray(newExpression.getType(), expr);
		}
		if (newExpression.getSettingsBlock() != null && newExpression.getSettingsBlock().getSettings().size() > 0) {
			SetValuesExpression sve = processSettings(expr, newExpression, mofType, newExpression.getSettingsBlock());
			setElementInformation(newExpression, sve);
			stack.push(sve);
		}
		else {
			stack.push(expr);
		}
		return false;
	}
	
	private void processNewArray(org.eclipse.edt.compiler.core.ast.Type type, NewExpression expr) {
		
		if (type.isArrayType()) {
			ArrayType arrType = ((ArrayType)type);
			processNewArray(arrType.getElementType(), expr);
			
			if (arrType.hasInitialSize()) {
				arrType.getInitialSize().accept(this);
				expr.getArguments().add((Expression)stack.pop());
			}
			else {
				IntegerLiteral lit = factory.createIntegerLiteral();
				lit.setType(IRUtils.getEGLPrimitiveType(Type_Int));
				lit.setValue("0");
				setElementInformation(type, lit);
				expr.getArguments().add(lit);
			}
		}
	}


	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.SetValuesExpression setValuesExpression) {
		setValuesExpression.getExpression().accept(this);
		Type targetType = null;
		if (setValuesExpression.getExpression().resolveType() != null) {
			EObject obj = mofTypeFor(setValuesExpression.getExpression().resolveType());
			if (obj instanceof Type) {
				targetType = (Type) obj;
			}
		}
		SetValuesExpression sve = processSettings((Expression)stack.pop(), setValuesExpression.getExpression(), targetType, setValuesExpression.getSettingsBlock());
		setElementInformation(setValuesExpression, sve);
		stack.push(sve);
		return false;
	}
	
	private SetValuesExpression processSettings(Expression target, org.eclipse.edt.compiler.core.ast.Expression targetNode, Type targetType, SettingsBlock settings) {
		if (!localStack.isEmpty() && target instanceof LHSExpr) {
			
			if (!sveTypeStack.isEmpty() && TypeUtils.isDynamicType(sveTypeStack.peek())) {
				//If the previous type was a dynamic type (like dictionary), we need to create a dynamic
				//access to use
				DynamicAccess da = factory.createDynamicAccess();
				setElementInformation(settings, da);
				LHSExpr newLHS = setAccessForDynamicAccess(da, target);					
				da.setExpression(localStack.peek());
				target = newLHS;
			}
			else {
				target = addQualifier(localStack.peek(), (LHSExpr)target);
			}
		}
		sveStack.push(target);
		sveTypeStack.push(targetType);
		SetValuesExpression sve = factory.createSetValuesExpression();
		sve.setTarget(target);
		StatementBlock block = factory.createStatementBlock();
		setElementInformation(settings, block);
		
		// Can assume no annotations
		MemberName localRef = null;
		Field decl = null;
		if (!(target instanceof LHSExpr)) {
			LocalVariableDeclarationStatement local = factory.createLocalVariableDeclarationStatement();
			DeclarationExpression declExpr = factory.createDeclarationExpression();
			decl = factory.createField();	
			decl.setName("eze$SettingTarget" + sveStack.size());
			decl.setType(targetType);
			setElementInformation(targetNode, decl);
			declExpr.getFields().add(decl);
			local.setExpression(declExpr);
			setElementInformation(targetNode, local);
			Assignment assignExpr = factory.createAssignment();
			localRef = factory.createMemberName();
			localRef.setId(decl.getCaseSensitiveName());
			localRef.setMember(decl);
			// Push new reference to temp onto the stack
			localStack.push(localRef);
			assignExpr.setLHS(localRef);
			assignExpr.setRHS(target);
			StatementBlock initializer = factory.createStatementBlock();
			AssignmentStatement stmt = createAssignmentStatement(assignExpr);
			setElementInformation(targetNode, stmt);
			initializer.getStatements().add(stmt);
			setElementInformation(targetNode, initializer);
			decl.setInitializerStatements(initializer);
			decl.setContainer(initializer.getContainer());
			block.getStatements().add(local);
		}
		else {
			localStack.push((LHSExpr)target);
		}
		
		for (Node setting : (List<Node>)settings.getSettings()) {
			setting.accept(this);
			Expression setexpr = (Expression)stack.pop();
			LHSExpr ref = null;
			if (decl == null) {
				// No temp was created so use the actual target
				// but clone it because it should be a different
				// instance of the expression when used in another place
				ref = (LHSExpr)target;
			}
			else { 
				ref = factory.createMemberName();
				((MemberName)ref).setId(decl.getCaseSensitiveName());
				((MemberName)ref).setMember(decl);
			}
			if (setexpr instanceof Assignment) {
				
				
				Assignment assign = (Assignment)setexpr;
				
				if (TypeUtils.isDynamicType(targetType)) {
					DynamicAccess da = factory.createDynamicAccess();
					setElementInformation(setting, da);
					LHSExpr newLHS = setAccessForDynamicAccess(da, assign.getLHS());					
					da.setExpression(ref);
					assign.setLHS(newLHS);
				}
				else {
					LHSExpr lhs = addQualifier(ref, assign.getLHS());
					assign.setLHS(lhs);
				}
				AssignmentStatement stmt = createAssignmentStatement(assign);
				setElementInformation(setting, stmt);
				block.getStatements().add(stmt);
			}
			else if (setexpr instanceof SetValuesExpression && !(setting instanceof org.eclipse.edt.compiler.core.ast.NewExpression)) {
				SetValuesExpression ex = (SetValuesExpression)setexpr;
				//move the statements from the setValues block into this block
				for (Statement stmt : ex.getSettings().getStatements()) {
					block.getStatements().add(stmt);
				}
			}
			else {
				
				QualifiedFunctionInvocation func = factory.createQualifiedFunctionInvocation();
				setElementInformation(setting, func);
				func.setQualifier(ref);
				func.setId("appendElement");
				func.getArguments().add(setexpr);
				FunctionStatement stmt = factory.createFunctionStatement();
				stmt.setExpr(func);
				setElementInformation(setting, stmt);
				block.getStatements().add(stmt);
			}
		}
		sve.setSettings(block);
		sveStack.pop();
		sveTypeStack.pop();
		localStack.pop();
		return sve;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.SubstringAccess substringAccess) {
		SubstringAccess access = factory.createSubstringAccess();
		setElementInformation(substringAccess, access);
		stack.push(access);
		substringAccess.getExpr().accept(this);
		access.setStart((Expression)stack.pop());
		substringAccess.getExpr2().accept(this);
		access.setEnd((Expression)stack.pop());
		substringAccess.getPrimary().accept(this);	
		access.setStringExpression((Expression)stack.pop());
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.SuperExpression superExpression) {
		SuperExpression expr = factory.createSuperExpression();
		expr.setThisObject(getCurrentFunctionMember().getContainer());
		setElementInformation(superExpression, expr);
		stack.push(expr);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ThisExpression thisExpression) {
		ThisExpression expr = factory.createThisExpression();
		Element binding = (Element)thisExpression.resolveElement();
		if (binding == null) {
			binding = thisExpression.resolveType();
		}
		Element obj = (Element)getEObjectFor(binding);
		expr.setThisObject(obj);
		setElementInformation(thisExpression, expr);
		stack.push(expr);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.UnaryExpression unaryExpression) {
		unaryExpression.getExpression().accept(this);
		Expression subExpr = (Expression)stack.pop();
		//If the operator is a +, do not create a unary expression, just pass on the subexpr
		if (unaryExpression.getOperator() == org.eclipse.edt.compiler.core.ast.UnaryExpression.Operator.PLUS) {
			stack.push(subExpr);
			return false;
		}
		
		boolean isMinus = (unaryExpression.getOperator() == org.eclipse.edt.compiler.core.ast.UnaryExpression.Operator.MINUS);

		if (subExpr instanceof NumericLiteral && isMinus) {
			((NumericLiteral)subExpr).setIsNegated(!((NumericLiteral)subExpr).isNegated());
			stack.push(subExpr);
		}
		else {		
			UnaryExpression expr = factory.createUnaryExpression();
			setElementInformation(unaryExpression, expr);
			stack.push(expr);
			expr.setExpression(subExpr);
			expr.setOperator(unaryExpression.getOperator().toString());
		}
		return false;
	}

	private boolean isSuperTypeMember(Element binding) {
		if (binding == null)
			return false;
		
		StructPart part = null;
		if (binding instanceof Member && ((Member)binding).getContainer() instanceof StructPart) {
			part = (StructPart)mofTypeFor((Type)((Member)binding).getContainer());
		}
		else if (binding instanceof Member) {
			return isSuperTypeMember(((Member)binding).getContainer());
		}

		StructPart current = (StructPart)currentPart;
		return  part != null && !current.equals(part) && current.isSubtypeOf(part);
	}

}
