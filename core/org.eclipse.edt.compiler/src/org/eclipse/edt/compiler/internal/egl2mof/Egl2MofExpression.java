/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DataTableBinding;
import org.eclipse.edt.compiler.binding.DelegateBinding;
import org.eclipse.edt.compiler.binding.DynamicDataBinding;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FormGroupBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.LibraryDataBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.ProgramBinding;
import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
import org.eclipse.edt.compiler.binding.annotationType.EGLSystemConstantAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.LikeMatchesExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.internal.core.lookup.System.ISystemLibrary;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.ArrayLiteral;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.BooleanLiteral;
import org.eclipse.edt.mof.egl.CharLiteral;
import org.eclipse.edt.mof.egl.DBCharLiteral;
import org.eclipse.edt.mof.egl.DecimalLiteral;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.DelegateInvocation;
import org.eclipse.edt.mof.egl.DynamicAccess;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ExpressionStatement;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FloatingPointLiteral;
import org.eclipse.edt.mof.egl.FunctionInvocation;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.FunctionPartInvocation;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.HexLiteral;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.InvalidName;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IsAExpression;
import org.eclipse.edt.mof.egl.IsNotExpression;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.MBCharLiteral;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MultiOperandExpression;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.NumericLiteral;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.SetValuesExpression;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.StringLiteral;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.SubstringAccess;
import org.eclipse.edt.mof.egl.TernaryExpression;
import org.eclipse.edt.mof.egl.ThisExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.InternUtil;
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
		Type eType = (Type)mofTypeFor(expr.resolveTypeBinding());
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
			ITypeBinding exprType = indexExpr.resolveTypeBinding();
			if (!Binding.isValidBinding(exprType)) {
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
					ax.setIndex(IRUtils.createAsExpression(index, mofPrimitiveFor(Primitive.INT)));
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
		Iterator i = node.getExpressions().iterator();
		while (i.hasNext()) {
			org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
			expr.accept(this);
			entries.add((Expression)stack.pop());
		}
		ArrayLiteral lit = factory.createArrayLiteral();
		lit.getEntries().addAll(entries);

		setElementInformation(node, lit);
		stack.push(lit);
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
	public boolean visit(org.eclipse.edt.compiler.core.ast.BooleanLiteral literal) {
		BooleanLiteral lit = factory.createBooleanLiteral();
		lit.setValue(literal.getValue());
		setElementInformation(literal, lit);
		stack.push(lit);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.CharLiteral literal) {
		CharLiteral lit = factory.createCharLiteral();
		lit.setValue(literal.getValue());
		lit.setIsHex(literal.isHex());
		setElementInformation(literal, lit);
		stack.push(lit);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.DBCharLiteral literal) {
		DBCharLiteral lit = factory.createDBCharLiteral();
		lit.setValue(literal.getValue());
		lit.setIsHex(literal.isHex());
		setElementInformation(literal, lit);
		stack.push(lit);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.MBCharLiteral literal) {
		MBCharLiteral lit = factory.createMBCharLiteral();
		lit.setValue(literal.getValue());
		lit.setIsHex(literal.isHex());
		setElementInformation(literal, lit);
		stack.push(lit);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.FloatLiteral literal) {
		FloatingPointLiteral lit = factory.createFloatingPointLiteral();
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
		Type type = (Type)mofTypeFor(fieldAccess.getPrimary().resolveTypeBinding());
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
		
 		ITypeBinding typeBinding = node.getTarget().resolveTypeBinding();
		List<FunctionParameterBinding> functionParmBindings;
		if (typeBinding instanceof DelegateBinding) {
			
			functionParmBindings =  ((DelegateBinding)typeBinding).getParemeters();
			fi = factory.createDelegateInvocation();
			fi.setId(typeBinding.getCaseSensitiveName());
			node.getTarget().accept(this);
			((DelegateInvocation)fi).setExpression((Expression)stack.pop());
		}
		else {
						
			ITypeBinding targetBinding = node.getTarget().resolveTypeBinding();
			IFunctionBinding functionBinding = null;
			IPartBinding declarer = null;
			if (Binding.isValidBinding(targetBinding) && (targetBinding instanceof IFunctionBinding)) {
				functionBinding = (IFunctionBinding) targetBinding;
				functionParmBindings =  functionBinding.getParameters();
				declarer = functionBinding.getDeclarer();
			}
			else {
				functionParmBindings =  new ArrayList();
				
			}
			
			if (functionBinding instanceof TopLevelFunctionBinding) {
				fi = factory.createFunctionPartInvocation();
				String packageName = concatWithSeparator(((TopLevelFunctionBinding)functionBinding).getPackageName(), ".");
				((FunctionPartInvocation)fi).setPackageName(packageName);
				fi.setId(functionBinding.getCaseSensitiveName());			
			}
			else {
				boolean isStatic = Binding.isValidBinding(functionBinding) && (functionBinding.isStatic() || declarer instanceof LibraryBinding);
				if ((node.getTarget() instanceof SimpleName || node.getTarget() instanceof org.eclipse.edt.compiler.core.ast.ThisExpression) && !isStatic) {
					if (functionBinding == null || isSuperTypeMember(functionBinding)) {
						// Qualify with this to get QualifiedFunctionInvocation which will do dynamic lookup
						fi = factory.createQualifiedFunctionInvocation();
						fi.setId(node.getTarget().getCanonicalString());
						ThisExpression thisExpr = factory.createThisExpression();
						thisExpr.setThisObject(getCurrentFunctionMember().getContainer());
						((QualifiedFunctionInvocation)fi).setQualifier(thisExpr);
					}
					else {
						
						Member mbr = (Member)getEObjectFor(functionBinding);
						String id=null;
						if (mbr instanceof FunctionMember) {
							id =  ((FunctionMember)mbr).getName();
						}
						else {
							if(Binding.isValidBinding(functionBinding)) {
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
								id =  ((FunctionMember)mbr).getName();
							}
							else {
								if(Binding.isValidBinding(functionBinding)) {
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
							partName.setPackageName(concatWithSeparator(declarer.getPackageName(), "."));
							setElementInformation(node.getTarget(), partName);
							((QualifiedFunctionInvocation)fi).setQualifier(partName);
						}
					}
					else {
						if (node.getTarget() instanceof FieldAccess) {
							FieldAccess fa = (FieldAccess) node.getTarget();
							if (fa.getPrimary() instanceof org.eclipse.edt.compiler.core.ast.ThisExpression) {
								
								if (functionBinding == null || isSuperTypeMember(functionBinding)) {
									// Qualify with this to get QualifiedFunctionInvocation which will do dynamic lookup
									fi = factory.createQualifiedFunctionInvocation();
									fi.setId(fa.getCaseSensitiveID());
									ThisExpression thisExpr = factory.createThisExpression();
									thisExpr.setThisObject(getCurrentFunctionMember().getContainer());
									((QualifiedFunctionInvocation)fi).setQualifier(thisExpr);
								}
								else {		
									
									Member mbr = (Member)getEObjectFor(functionBinding);
									String id=null;
									if (mbr instanceof FunctionMember) {
										id =  ((FunctionMember)mbr).getName();
									}
									else {
										if(Binding.isValidBinding(functionBinding)) {
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
								fi.setId(fa.getID());
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
		}
		
		int index = 0;
		for (Iterator iter = node.getArguments().iterator(); iter.hasNext();) {
			org.eclipse.edt.compiler.core.ast.Expression argExpr = (org.eclipse.edt.compiler.core.ast.Expression) iter.next();
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
			FunctionParameterBinding parm = null;
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
	public boolean visit(org.eclipse.edt.compiler.core.ast.HexLiteral hexLiteral) {
		HexLiteral lit = factory.createHexLiteral();
		lit.setValue(hexLiteral.getValue());
		setElementInformation(hexLiteral, lit);
		stack.push(lit);
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
		Type type = (Type)mofTypeFor(expr.getType().resolveTypeBinding());
		isaExpr.setEType(type);
		return false;
	}

	@Override
	public boolean visit(ParenthesizedExpression parenExpr) {
		return true;
	}
	
	private Name createNameForPart(IPartBinding binding) {
		if (binding instanceof TopLevelFunctionBinding) {
			return factory.createTopLevelFunctionName();
		}
		else if (binding instanceof LibraryBinding 
				|| binding instanceof DataTableBinding 
				|| binding instanceof ExternalTypeBinding
				|| binding instanceof EnumerationTypeBinding
				|| binding instanceof FormBinding
				|| binding instanceof ProgramBinding) {
			// Is only a proper reference to a part if given part type is allowed to have
			// field references to the part itself (static reference) as opposed to field
			// of an instance.  SimpleName AST values may be referencing a Part in the case
			// where the reference is to a global variable outside the scope of a TopLevelFunction
			// that is being compiled independently
			PartName name = factory.createPartName();
			String packageName;
			if (binding instanceof FormBinding) {
				FormGroupBinding fg = ((FormBinding)binding).getEnclosingFormGroup();
				packageName = concatWithSeparator(fg == null ? binding.getPackageName() : fg.getPackageName(), ".");
			}
			else {
				packageName = concatWithSeparator(((ITypeBinding)binding).getPackageName(), ".");
				//TODO - remove this temporary hardcoded remapping of "egl.core" system parts when the front
				// end references to system parts are handled through a proper System Scope configuration
				// that will bind to the configured part
				if (packageName.equals("egl.core")) {
					packageName = "egl.lang";
				}
			}

			name.setPackageName(packageName);
			name.setId(binding.getCaseSensitiveName());
			return name;
		}
		else {
			return factory.createDanglingReference();
		}
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.SimpleName node) {
		IBinding binding = node.resolveBinding();
		// Since there was no binding this is an invalid name
		if (!Binding.isValidBinding(binding)) {
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
		ITypeBinding part = null;
		if (binding instanceof IDataBinding && ((IDataBinding)binding).isStaticPartDataBinding()) {
			part = ((IDataBinding)binding).getType();
		}
		else if (binding instanceof IPartBinding) {
			part = (IPartBinding)binding;
		}
		if (part != null) {
			name = createNameForPart((IPartBinding)part);
		}
		else {
			name = factory.createMemberName();
		}
			
		if (part instanceof FormBinding) {
			FormGroupBinding fg = ((FormBinding) part).getEnclosingFormGroup();
			String id;
			if (fg == null) {
				id = part.getCaseSensitiveName();
			}
			else {
				id = fg.getCaseSensitiveName() + Type.NestedPartDelimiter + part.getCaseSensitiveName();
			}
			name.setId(id);
		}
		else {
			if (binding instanceof DynamicDataBinding) {
				name.setId(node.getCaseSensitiveIdentifier());
			}
			else {
				name.setId(binding.getCaseSensitiveName());
			}
		}
		
		IDataBinding qualifier = (IDataBinding) node.getAttribute(org.eclipse.edt.compiler.core.ast.Name.IMPLICIT_QUALIFIER_DATA_BINDING);
		
		if (qualifier == null && binding instanceof EnumerationDataBinding) {
			name = (Name)addQualifier(createNameForPart((IPartBinding)((EnumerationDataBinding)binding).getDeclaringPart()), name);
		}
		
		if (qualifier != null) {
			
			if (qualifier instanceof LibraryDataBinding ) {
				name = (Name)addQualifier(createNameForPart((IPartBinding)qualifier.getType()), name);
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
		EObject objType = mofTypeFor(name.getQualifier().resolveTypeBinding());
		Type type = null;
		if (objType instanceof Type) {
			type = (Type)mofTypeFor(name.getQualifier().resolveTypeBinding());
		}
		if (TypeUtils.isDynamicType(type)) {
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
			
			IBinding qualBinding = name.getQualifier().resolveBinding();
			if (qualBinding instanceof PartBinding) {
				nameExpr.setQualifier(createNameForPart((IPartBinding)qualBinding));
			}
			else {
				name.getQualifier().accept(this);
				nameExpr.setQualifier((Expression)stack.pop());
			}
		}
		return false;
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
	@SuppressWarnings("unchecked")
	public boolean visit(org.eclipse.edt.compiler.core.ast.NewExpression newExpression) {
		NewExpression expr = factory.createNewExpression();
		ITypeBinding type = newExpression.resolveTypeBinding();
		if (!Binding.isValidBinding(type)) {
			stack.push(expr);
			return false;
		}
		
		EObject obj = mofTypeFor(type);
		if (!(obj instanceof Type)) {
			stack.push(expr);
			return false;
		}
		
		Type mofType = (Type)obj;
		expr.setId(mofType.getTypeSignature());
		setElementInformation(newExpression, expr);
		for (Node node : (List<Node>)newExpression.getArguments()) {
			node.accept(this);
			expr.getArguments().add((Expression)stack.pop());
		}
		
		if (type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
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
		if (type.isNullableType()) {
			processNewArray(type.getBaseType(), expr);
		}
		
		if (type.isArrayType()) {
			ArrayType arrType = ((ArrayType)type);
			processNewArray(arrType.getElementType(), expr);
			
			if (arrType.hasInitialSize()) {
				arrType.getInitialSize().accept(this);
				expr.getArguments().add((Expression)stack.pop());
			}
			else {
				IntegerLiteral lit = factory.createIntegerLiteral();
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
		if (Binding.isValidBinding(setValuesExpression.getExpression().resolveTypeBinding())) {
			EObject obj = mofTypeFor(setValuesExpression.getExpression().resolveTypeBinding());
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
		int arrayIndex = 0;
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
			localRef.setId(decl.getName());
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
				((MemberName)ref).setId(decl.getName());
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
			else if (setexpr instanceof SetValuesExpression) {
				SetValuesExpression ex = (SetValuesExpression)setexpr;
				//move the statements from the setValues block into this block
				for (Statement stmt : ex.getSettings().getStatements()) {
					block.getStatements().add(stmt);
				}
			}
			else {
				
				arrayIndex++;
				IntegerLiteral indexExpr = factory.createIntegerLiteral();
				indexExpr.setValue(String.valueOf(arrayIndex));
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
	public boolean visit(org.eclipse.edt.compiler.core.ast.ThisExpression thisExpression) {
		ThisExpression expr = factory.createThisExpression();
		IBinding binding = thisExpression.resolveDataBinding();
		if (binding == null) {
			binding = thisExpression.resolveTypeBinding();
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
		boolean isBang = unaryExpression.getOperator() == org.eclipse.edt.compiler.core.ast.UnaryExpression.Operator.BANG;

		if (subExpr instanceof NumericLiteral && !isBang) {
			if (unaryExpression.getOperator() == org.eclipse.edt.compiler.core.ast.UnaryExpression.Operator.MINUS) {
				((NumericLiteral)subExpr).setIsNegated(true);
			}
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

	private boolean isAny(ITypeBinding type) {
		if (Binding.isValidBinding(type) && type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
			PrimitiveTypeBinding primType = (PrimitiveTypeBinding) type;
			if (primType.getPrimitive() == Primitive.ANY) {
				return true;
			}
		}
		return false;
	}

	private boolean isFormBinding (ITypeBinding type) {
		return (Binding.isValidBinding(type) && ITypeBinding.FORM_BINDING == type.getKind());
	}

	private boolean isFormFieldBinding(IBinding binding) {
		if (!Binding.isValidBinding(binding)) {
			return false;
		}
		if (!binding.isDataBinding()) {
			return false;
		}
		if (((IDataBinding) binding).getKind() == IDataBinding.FORM_FIELD) {
			return true;
		}
		return false;
	}

	private boolean isNumeric(ITypeBinding type) {
		if (Binding.isValidBinding(type) && type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
			PrimitiveTypeBinding primType = (PrimitiveTypeBinding) type;
			return Primitive.isNumericType(primType.getPrimitive());
		}
		return false;
	}

	private boolean isPartBinding(IBinding binding) {
	
		if (binding.isFunctionBinding()) {
			return false;
		}
	
		if (binding.isTypeBinding()) {
			return true;
		}
	
		if (binding.isDataBinding()) {
			IDataBinding dataBinding = (IDataBinding) binding;
			if (dataBinding.getKind() == IDataBinding.DATATABLE_BINDING || dataBinding.getKind() == IDataBinding.PROGRAM_BINDING
					|| dataBinding.getKind() == IDataBinding.LIBRARY_BINDING
					|| dataBinding.getKind() == IDataBinding.ENUMERATIONTYPE_BINDING || dataBinding.getKind() == IDataBinding.FORM_BINDING
					|| dataBinding.getKind() == IDataBinding.EXTERNALTYPE_BINDING) {
				return true;
			}
			return false;
		}
		return true;
	}

	private boolean isResourceAssociationBinding(IBinding binding) {
		return Binding.isValidBinding(binding) && binding.isDataBinding()
				&& (((IDataBinding) binding).getKind() == IDataBinding.SYSTEM_VARIABLE_BINDING)
				&& IEGLConstants.SYSTEM_WORD_RESOURCEASSOCIATION.equalsIgnoreCase(binding.getName());
	}

	private boolean isSizeFunction(IDataBinding binding) {
		if (!Binding.isValidBinding(binding)) {
			return false;
		}
		IAnnotationBinding ann = binding.getAnnotation(EGLSystemConstantAnnotationTypeBinding.getInstance());
		if (ann == null) {
			return false;
		}
		if (ann.getValue() instanceof Integer) {
			int value = ((Integer) ann.getValue()).intValue();
			return value == ISystemLibrary.Size_func;
		}
		return false;
	}

	private boolean isStructureItemBinding(IBinding binding) {
		if (!Binding.isValidBinding(binding)) {
			return false;
		}
		if (!binding.isDataBinding()) {
			return false;
		}
		if (((IDataBinding) binding).getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
			return true;
		}
		return false;
	}

	private boolean isSuperTypeMember(IBinding binding) {
		if (binding == null || binding == IBinding.NOT_FOUND_BINDING)
			return false;
		
		StructPart part = null;
		if (binding.isDataBinding()) {
			part = (StructPart)mofTypeFor(((IDataBinding)binding).getDeclaringPart());
		}
		else if (binding.isFunctionBinding()) {
			part = (StructPart)mofTypeFor(((IFunctionBinding)binding).getDeclarer());
		}

		StructPart current = (StructPart)currentPart;
		return  part != null && !current.equals(part) && current.isSubtypeOf(part);
	}

	private boolean isWithPatternFunction(IDataBinding binding) {
		if (!Binding.isValidBinding(binding)) {
			return false;
		}
		IAnnotationBinding ann = binding.getAnnotation(EGLSystemConstantAnnotationTypeBinding.getInstance());
		if (ann == null) {
			return false;
		}
		if (ann.getValue() instanceof Integer) {
			int value = ((Integer) ann.getValue()).intValue();
			return (value == ISystemLibrary.TimeStampValueWithPattern_func || value == ISystemLibrary.IntervalValueWithPattern_func);
		}
		return false;
	}

	
}
