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
package org.eclipse.edt.compiler.binding;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.BooleanLiteral;
import org.eclipse.edt.compiler.core.ast.BytesLiteral;
import org.eclipse.edt.compiler.core.ast.CharLiteral;
import org.eclipse.edt.compiler.core.ast.DBCharLiteral;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.HexLiteral;
import org.eclipse.edt.compiler.core.ast.IASTVisitor;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.MBCharLiteral;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NullLiteral;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.SQLLiteral;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.SuperExpression;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.TypeLiteralExpression;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationRightHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.DataBindingScope;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.NullScope;
import org.eclipse.edt.compiler.internal.core.lookup.ProgramScope;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.compiler.internal.core.lookup.SystemScope;
import org.eclipse.edt.compiler.internal.core.lookup.TypeBindingScope;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.utils.ExpressionParser;
import org.eclipse.edt.compiler.internal.core.utils.TypeParser;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class SettingsBlockAnnotationBindingsCompletor extends DefaultBinder {

	private AnnotationLeftHandScope annotationLeftHandScope;

	private IPartBinding partBinding;

	private class ExpressionVisitor extends AbstractASTExpressionVisitor {
		int index = -1;

		int integerValue = -1;

		public boolean visit(AnnotationExpression annotationExpression) {
			// When resolving an annotation expression, use only the
			// annotationLeftHandScope(s) and the system scope. So, find
			// the top leftHandScope and set it's parent scope to the nullScope
			
			AnnotationLeftHandScope topAnnotationScope = annotationLeftHandScope.getTopLevelAnnotationLeftHandScope();
			Scope saveScope = topAnnotationScope.getParentScope();
			topAnnotationScope.setParentScope(NullScope.INSTANCE);

			LeftHandCompletor completor = new LeftHandCompletor(annotationLeftHandScope, partBinding, dependencyRequestor,
					problemRequestor, compilerOptions);
			annotationExpression.accept(completor);

			// Now, restore the original scope chain
			topAnnotationScope.setParentScope(saveScope);
			return false;
		}

		public boolean visitExpression(Expression expression) {
			// When resolving a non-annotation expression, do not allow
			// resolution to annotation record parts
			
			annotationLeftHandScope.setResolveToAnnotations(false);
			
			LeftHandCompletor completor = new LeftHandCompletor(annotationLeftHandScope, partBinding, dependencyRequestor,
					problemRequestor, compilerOptions);
			expression.accept(completor);
			
			annotationLeftHandScope.setResolveToAnnotations(true);
			return false;
		}

		public boolean visit(ArrayAccess arrayAccess) {
			LeftHandCompletor completor = new LeftHandCompletor(annotationLeftHandScope, partBinding, dependencyRequestor,
					problemRequestor, compilerOptions);
			arrayAccess.accept(completor);
			ExpressionVisitor visitor = new ExpressionVisitor();
			arrayAccess.getArray().accept(visitor);
			Expression expr = (Expression) arrayAccess.getIndices().get(0);
			final int[] value = new int[1];
			expr.accept(new AbstractASTExpressionVisitor() {
				public void endVisit(IntegerLiteral integerLiteral) {
					value[0] = Integer.parseInt(integerLiteral.getValue());
				}
			});
			index = value[0];
			return false;
		}

		public boolean visit(IntegerLiteral integerLiteral) {
			integerValue = Integer.parseInt(integerLiteral.getValue());
			return false;
		}

		public int getIndex() {
			return index;
		}

	}

	private static class LeftHandCompletor extends DefaultBinder {

		IAnnotationBinding annotation;

		AnnotationLeftHandScope leftHandScope;

		boolean notApplicable;

		private IPartBinding partBinding;

		private boolean withinAnnotationExpression = false;;

		public LeftHandCompletor(AnnotationLeftHandScope currentScope, IPartBinding partBinding, IDependencyRequestor dependencyRequestor,
				IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			super(currentScope, partBinding, dependencyRequestor, problemRequestor, compilerOptions);
			leftHandScope = currentScope;
			this.partBinding = partBinding;
		}

		public AnnotationLeftHandScope getAnnotationLeftHandScope() {
			return (AnnotationLeftHandScope) currentScope;
		}

		public void endVisit(ArrayAccess arrayAccess) {
			IProblemRequestor pRequestor = problemRequestor;
			problemRequestor = NullProblemRequestor.getInstance();
			super.endVisit(arrayAccess);
			problemRequestor = pRequestor;
		}

		public void endVisit(SimpleName simpleName) {
			IBinding binding = simpleName.resolveBinding();
			if (binding != IBinding.NOT_FOUND_BINDING && binding.isAnnotationBinding()) {
				annotation = (IAnnotationBinding) binding;
			} else {
				annotation = getAnnotationLeftHandScope().getNotApplicableBinding();
				notApplicable = annotation != null;
			}
		}
		

		public boolean visit(SettingsBlock settingsBlock) {
			return false;
		}

		public boolean visit(QualifiedName qualifiedName) {
			if (withinAnnotationExpression) {
				try {
					ITypeBinding type = bindTypeName(qualifiedName);

					if (IBinding.NOT_FOUND_BINDING != type && type.isPartBinding() && !((IPartBinding) type).isValid()) {
						type = ((IPartBinding) type).realize();
					}
					if (IBinding.NOT_FOUND_BINDING != type && type.getAnnotation(AnnotationAnnotationTypeBinding.getInstance()) != null) {
						AnnotationTypeBindingImpl annotationTypeBindingImpl = new AnnotationTypeBindingImpl((FlexibleRecordBinding) type, partBinding);
						IAnnotationBinding annotationBinding = new AnnotationBinding(qualifiedName.getCaseSensitiveIdentifier(), partBinding,
								annotationTypeBindingImpl);
						if (!annotationTypeBindingImpl.isApplicableFor(leftHandScope.getBindingBeingAnnotated())) {
							Scope parentScope = leftHandScope.getParentScope();
							if(parentScope instanceof AnnotationLeftHandScope &&
							   annotationTypeBindingImpl.isApplicableFor(((AnnotationLeftHandScope) parentScope).getBindingBeingAnnotated())) {
								qualifiedName.setBinding(annotationBinding);
								qualifiedName.setTypeBinding(annotationTypeBindingImpl);
							}
							else {
								getAnnotationLeftHandScope().setNotApplicableBinding(annotationBinding);
								qualifiedName.setBinding(IBinding.NOT_FOUND_BINDING);
								problemRequestor.acceptProblem(qualifiedName, IProblemRequestor.ANNOTATION_NOT_APPLICABLE,
										new String[] { annotationBinding.getCaseSensitiveName() });
							}
						} else {
							qualifiedName.setBinding(annotationBinding);
							qualifiedName.setTypeBinding(annotationTypeBindingImpl);
						}
					}
				} catch (ResolutionException e) {
					qualifiedName.setBinding(IBinding.NOT_FOUND_BINDING);
					problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e
							.getInserts());
				}
				return false;
			} else {
				return super.visit(qualifiedName);
			}
		}

		public void endVisit(QualifiedName qualifiedName) {
			IBinding binding = qualifiedName.resolveBinding();
			if (binding != IBinding.NOT_FOUND_BINDING && binding.isAnnotationBinding()) {
				annotation = (IAnnotationBinding) binding;
			} else {
				annotation = getAnnotationLeftHandScope().getNotApplicableBinding();
				notApplicable = annotation != null;
			}
		}

		public IAnnotationBinding getAnnotation() {
			return annotation;
		}

		public void setAnnotation(IAnnotationBinding annotation) {
			this.annotation = annotation;
		}
		
		public boolean visit(SuperExpression superExpression) {
			AnnotationLeftHandScope myScope = getAnnotationLeftHandScope().getTopLevelAnnotationLeftHandScope();
			if (myScope.getBindingBeingAnnotated().isDataBinding()) {
				ITypeBinding typeBinding = myScope.getTypeOfBindingBeingAnnotated();
				
				if (typeBinding instanceof PartBinding) {
					typeBinding = ((PartBinding)typeBinding).getDefaultSuperType();
					if (Binding.isValidBinding(typeBinding)) {
						superExpression.setTypeBinding(typeBinding);
						return false;
					}
				}
			}

			if (myScope.getBindingBeingAnnotated().isTypeBinding()) {
				ITypeBinding typeBinding = (ITypeBinding) myScope.getBindingBeingAnnotated();
				if (typeBinding instanceof PartBinding) {
					typeBinding = ((PartBinding)typeBinding).getDefaultSuperType();
					if (Binding.isValidBinding(typeBinding)) {
						superExpression.setTypeBinding(typeBinding);
						return false;
					}
				}
			}

			superExpression.setDataBinding(IBinding.NOT_FOUND_BINDING);
			superExpression.setTypeBinding(IBinding.NOT_FOUND_BINDING);
			
			return false;
		}

		public boolean visit(ThisExpression thisExpression) {
			AnnotationLeftHandScope myScope = getAnnotationLeftHandScope().getTopLevelAnnotationLeftHandScope();
			if (myScope.getBindingBeingAnnotated().isDataBinding()) {
				IDataBinding dataBinding = (IDataBinding) myScope.getBindingBeingAnnotated();
				thisExpression.setDataBinding(dataBinding);
				thisExpression.setTypeBinding(myScope.getTypeOfBindingBeingAnnotated());
				return false;
			}

			if (myScope.getBindingBeingAnnotated().isTypeBinding()) {
				ITypeBinding typeBinding = (ITypeBinding) myScope.getBindingBeingAnnotated();
				thisExpression.setTypeBinding(typeBinding);
				return false;
			}

			thisExpression.setDataBinding(IBinding.NOT_FOUND_BINDING);
			thisExpression.setTypeBinding(IBinding.NOT_FOUND_BINDING);
			return false;
		}

		public boolean isNotApplicable() {
			return notApplicable;
		}

		public IDataBinding bindExpressionName(Name name) throws ResolutionException {
			try {
				return super.bindExpressionName(name);
			} catch (ResolutionException e) {
				if (IProblemRequestor.ANNOTATION_NOT_APPLICABLE == e.getProblemKind()) {
					IBinding bindingBeingAnnotated = leftHandScope.getBindingBeingAnnotated();
					if (bindingBeingAnnotated.isDataBinding()) {
						ITypeBinding type = ((IDataBinding) bindingBeingAnnotated).getType();
						if (type != null) {
							if (ITypeBinding.FLEXIBLE_RECORD_BINDING == type.getKind()) {
								throw new ResolutionException(e.getStartOffset(), e.getEndOffset(),
										IProblemRequestor.CANNOT_OVERRIDE_FLEX_RECORD_PROPERTIES, new String[0]);
							}
						}
					}
				}
				throw e;
			}
		}

		public boolean visit(AnnotationExpression expr) {
			leftHandScope.setWithinAnnotationExpression(true);
			withinAnnotationExpression = true;
			return true;
		}

		public void endVisit(AnnotationExpression expr) {
			leftHandScope.setWithinAnnotationExpression(false);
			withinAnnotationExpression = false;
			
			expr.setTypeBinding(expr.getName().resolveTypeBinding());
		}
	}

	private static class RightHandCompletor extends DefaultBinder {

		Object value;

		boolean isNegative;

		IAnnotationBinding annotation;

		private IPartBinding partBinding;

		private boolean annotationIsResolvable;

		private boolean annotationTypeIsTypeRef;

		private boolean canMixTypesInArrayLiterals;

		boolean valueIsInvalid = false;

		private class ExpressionVisitor extends AbstractASTExpressionVisitor {

			private IPartBinding partBinding;

			public ExpressionVisitor(IPartBinding partBinding) {
				this.partBinding = partBinding;
			}

			public boolean visitExpression(Expression expression) {
				return false;
			}

			public boolean visit(AnnotationExpression annotationExpression) {
				AnnotationLeftHandScope newScope = new AnnotationLeftHandScope(currentScope, annotation, null, null, -1, partBinding);
				LeftHandCompletor completor = new LeftHandCompletor(newScope, partBinding, dependencyRequestor, problemRequestor,
						compilerOptions);
				annotationExpression.accept(completor);
				return false;
			}
		}

		public RightHandCompletor(Scope currentScope, IPartBinding partBinding, IDependencyRequestor dependencyRequestor,
				IProblemRequestor problemRequestor, IAnnotationBinding annotation, ICompilerOptions compilerOptions) {
			super(currentScope, partBinding, dependencyRequestor, problemRequestor, compilerOptions);
			this.annotation = annotation;
			this.partBinding = partBinding;
			IAnnotationTypeBinding annotationType = (IAnnotationTypeBinding) annotation.getType();
			this.annotationIsResolvable = annotationTypeIsResolvable(annotationType);
			if (annotationIsResolvable) {
				annotationTypeIsTypeRef = ((IAnnotationTypeBinding) annotation.getType()).getSingleValueType().getBaseType().getName() == InternUtil
						.intern("TYPEREF");
				canMixTypesInArrayLiterals = annotationTypeIsTypeRef;
			}
			canMixTypesInArrayLiterals = canMixTypesInArrayLiterals || annotationTypeIsAny(annotationType);
		}

		private boolean annotationTypeIsResolvable(IAnnotationTypeBinding aTypeBinding) {
			if (aTypeBinding.hasSingleValue()) {
				ITypeBinding singleValueType = aTypeBinding.getSingleValueType().getBaseType();
				IPartBinding matchingSystemPart = (IPartBinding) SystemPartManager.findType(singleValueType.getName());
				if (matchingSystemPart == singleValueType) {
					return SystemEnvironmentPackageNames.EGL_CORE_REFLECT == matchingSystemPart.getPackageName()
							&& matchingSystemPart != SystemPartManager.SQLSTRING_BINDING;
				}
			}
			return false;
		}

		private boolean annotationTypeIsAny(IAnnotationTypeBinding aTypeBinding) {
			if (aTypeBinding.hasSingleValue()) {
				ITypeBinding singleValueType = aTypeBinding.getSingleValueType().getBaseType();
				return (Binding.isValidBinding(singleValueType) && singleValueType.getNonNullableInstance() == PrimitiveTypeBinding.getInstance(Primitive.ANY));
			}
			return false;
		}
		
		protected void handleNameResolutionException(ResolutionException e) {
			valueIsInvalid = true;
			IAnnotationTypeBinding annotationType = (IAnnotationTypeBinding) annotation.getType();
			if (annotationType.hasSingleValue()) {
				ITypeBinding singleValueType = annotationType.getSingleValueType();
				if (ITypeBinding.ENUMERATION_BINDING == singleValueType.getKind()) {
					problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(),
							IProblemRequestor.PROPERTY_REQUIRES_SPECIFIC_VALUE, true, new String[] { annotation.getCaseSensitiveName(),
									((EnumerationTypeBinding) annotationType.getSingleValueType()).getEnumerationsAsCommaList() });
					return;
				} else if (PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN) == singleValueType) {
					problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(),
							IProblemRequestor.PROPERTY_REQUIRES_SPECIFIC_VALUE, true, new String[] { annotation.getCaseSensitiveName(),
									IEGLConstants.KEYWORD_YES + ", " + IEGLConstants.KEYWORD_NO });
					return;
				} else if (PrimitiveTypeBinding.getInstance(Primitive.STRING) == singleValueType &&
						   !annotationType.takesExpressionInOpenUIStatement()) {
					problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(),
							IProblemRequestor.PROPERTY_REQUIRES_STRING_VALUE, true, new String[] { annotation.getCaseSensitiveName()});
					return;
				}
			}
			super.handleNameResolutionException(e);
		}

		public boolean visit(SimpleName simpleName) {
			IProblemRequestor oldPRequestor = problemRequestor;
			if (annotationIsResolvable) {
				if (annotationTypeIsTypeRef) {
					try {
						bindTypeName(simpleName);
					} catch (ResolutionException e) {
						oldPRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e
								.getInserts());
					}
				} else {
					simpleName.setBinding(IBinding.NOT_FOUND_BINDING);
				}
				return false;
			}
			boolean result = super.visit(simpleName);
			problemRequestor = oldPRequestor;
			return result;
		}

		public boolean visit(QualifiedName qualifiedName) {
			IProblemRequestor oldPRequestor = problemRequestor;
			if (annotationIsResolvable) {
				if (annotationTypeIsTypeRef) {
					try {
						bindTypeName(qualifiedName);
					} catch (ResolutionException e) {
						oldPRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e
								.getInserts());
					}
				} else {
					qualifiedName.setBinding(IBinding.NOT_FOUND_BINDING);
				}
				return false;
			}
			boolean result = super.visit(qualifiedName);
			problemRequestor = oldPRequestor;
			return result;
		}

		public void endVisit(SimpleName simpleName) {
			IBinding binding = simpleName.resolveBinding();
			value = IBinding.NOT_FOUND_BINDING == binding && annotationIsResolvable ? (Object) simpleName.getCanonicalName() : binding;
		}

		public void endVisit(QualifiedName qualifiedName) {
			IBinding binding = qualifiedName.resolveBinding();
			value = IBinding.NOT_FOUND_BINDING == binding && annotationIsResolvable ? (Object) qualifiedName.getCanonicalName() : binding;
		}

		public void endVisit(ArrayAccess arrayAccess) {
			value = arrayAccess.resolveDataBinding();
		}

		public boolean visit(IntegerLiteral integerLiteral) {
			String str = integerLiteral.getValue();
			if (isNegative) {
				str = "-" + str;
			}
			
			if (integerLiteral.getLiteralKind() == LiteralExpression.BIGINT_LITERAL) {
				try {
					value = new Long(str);
				} catch (NumberFormatException e) {
					problemRequestor.acceptProblem(integerLiteral, IProblemRequestor.BIGINT_LITERAL_OUT_OF_RANGE, new String[] { str });
					value = null;
				}
			}
			else if (integerLiteral.getLiteralKind() == LiteralExpression.SMALLINT_LITERAL) {
				try {
					value = new Short(str);
				} catch (NumberFormatException e) {
					problemRequestor.acceptProblem(integerLiteral, IProblemRequestor.SMALLINT_LITERAL_OUT_OF_RANGE, new String[] { str });
					value = null;
				}
			}
			else {
				try {
					value = new Integer(str);
				} catch (NumberFormatException e) {
					problemRequestor.acceptProblem(integerLiteral, IProblemRequestor.INTEGER_LITERAL_OUT_OF_RANGE, new String[] { str });
					value = null;
				}
			}
			
			return false;
		}

		public boolean visit(FloatLiteral floatLiteral) {
			String str = floatLiteral.getValue();
			if (isNegative) {
				str = "-" + str;
			}
			value = new Double(str);
			return false;
		}

		public boolean visit(DecimalLiteral decimalLiteral) {
			String str = decimalLiteral.getValue();
			if (isNegative) {
				str = "-" + str;
			}
			value = new Float(str);
			return false;
		}

		public boolean visit(StringLiteral stringLiteral) {
			value = stringLiteral.getValue();
			return false;
		}
		
		private static class BinaryExpressionVisitor extends AbstractASTExpressionVisitor {
			private StringBuffer concatenatedString;
			private IProblemRequestor problemRequestor;
			private boolean exprValid = true;
			
			public BinaryExpressionVisitor(IProblemRequestor problemRequestor) {
				concatenatedString = new StringBuffer();
				this.problemRequestor = problemRequestor;
			}
			
			public String getConcatenatedString() {
				return concatenatedString.toString();
			}
			
			public boolean expressionIsValid() {
				return exprValid;
			}
			
			public boolean visit(BinaryExpression binaryExpression) {
				if(binaryExpression.getOperator() == BinaryExpression.Operator.PLUS || 
				   binaryExpression.getOperator() == BinaryExpression.Operator.CONCAT) {
					
					BinaryExpressionVisitor operand1Visitor = new BinaryExpressionVisitor(problemRequestor);
					binaryExpression.getFirstExpression().accept(operand1Visitor);
					concatenatedString.append(operand1Visitor.getConcatenatedString());
					
					BinaryExpressionVisitor operand2Visitor = new BinaryExpressionVisitor(problemRequestor);
					binaryExpression.getSecondExpression().accept(operand2Visitor);
					concatenatedString.append(operand2Visitor.getConcatenatedString());
					
					if(!operand1Visitor.exprValid || !operand2Visitor.exprValid) {
						problemRequestor.acceptProblem(binaryExpression, IProblemRequestor.INVALID_EXPRESSION_DATA_ACCESS_OR_STRING_LITERAL);
					}
				}
				else {
					exprValid = false;
				}
				return false;
			}
			
			public boolean visit(StringLiteral stringLiteral) {
				if (stringLiteral.isHex()) {
					concatenatedString.append(convertUnicodeHexToString(stringLiteral.getValue()));
				}
				else {
					concatenatedString.append(stringLiteral.getValue());
				}
				return false;
			}
			
			private String convertUnicodeHexToString(String str) {
				try {
					int charLen = (str.length()) / 4;
					if (charLen * 4 != str.length()) {
						charLen = charLen + 1;
					}
					
					char[] chars = new char[charLen];
					
					for (int i = 0; i < str.length(); i = i + 4) {
						String nextCharString;
						if (i + 4 < str.length()) {
							nextCharString = str.substring(i, i + 4);
						}
						else {
							nextCharString = str.substring(i);
						}
						
						char myUnicodeCharacter = (char) Integer.parseInt(nextCharString, 16);
						chars[i/4] = myUnicodeCharacter;
					}
					return new String(chars);
				} catch (NumberFormatException e) {
					return str;
				}

			}
			
			public boolean visitExpression(Expression expression) {
				exprValid = false;
				return false;
			}			
		}
		
		public void endVisit(BinaryExpression binaryExpression) {
			super.endVisit(binaryExpression);
			
			BinaryExpressionVisitor subVisitor = new BinaryExpressionVisitor(problemRequestor);
			binaryExpression.accept(subVisitor);
			
			if(!subVisitor.exprValid) {
				problemRequestor.acceptProblem(binaryExpression, IProblemRequestor.INVALID_EXPRESSION_DATA_ACCESS_OR_STRING_LITERAL);
			}
			else {
				value = subVisitor.getConcatenatedString();
			}
		}

		public boolean visit(HexLiteral stringLiteral) {
			value = stringLiteral.getValue();
			return false;
		}

		public boolean visit(CharLiteral stringLiteral) {
			value = stringLiteral.getValue();
			return false;
		}

		public boolean visit(DBCharLiteral stringLiteral) {
			value = stringLiteral.getValue();
			return false;
		}

		public boolean visit(MBCharLiteral stringLiteral) {
			value = stringLiteral.getValue();
			return false;
		}

		public boolean visit(BooleanLiteral booleanLiteral) {
			value = booleanLiteral.booleanValue();
			return false;
		}
		
		public boolean visit(BytesLiteral bytesLiteral) {
			value = bytesLiteral.getValue();
			return false;
		}

		public boolean visit(SQLLiteral sQLLiteral) {
			value = sQLLiteral.getValue();
			return false;
		}

		public boolean visit(TypeLiteralExpression typeLiteralExpression) {
			super.visit(typeLiteralExpression);
			value = typeLiteralExpression.getType().resolveTypeBinding();
			return false;
		}

		public boolean visit(SetValuesExpression setValuesExpression) {
			Expression expr = setValuesExpression.getExpression();
			expr.accept(new ExpressionVisitor(partBinding));
			IDataBinding dataBinding = expr.resolveDataBinding();
			if (dataBinding != null && dataBinding != IBinding.NOT_FOUND_BINDING && dataBinding.isAnnotationBinding()) {
				value = dataBinding;
				
				Scope sysScope = SettingsBlockAnnotationBindingsCompletor.getSystemScope(currentScope);
				if (sysScope == null) {
					sysScope = NullScope.INSTANCE;
				}
				AnnotationLeftHandScope newScope = new AnnotationLeftHandScope(sysScope, dataBinding, dataBinding.getType(),
						dataBinding, -1, partBinding);
				SettingsBlockAnnotationBindingsCompletor completor = new SettingsBlockAnnotationBindingsCompletor(currentScope,
						partBinding, newScope, dependencyRequestor, problemRequestor, compilerOptions);
				setValuesExpression.getSettingsBlock().accept(completor);

				expr.setTypeBinding(dataBinding.getType());
			}
			return false;
		}
		
		
		protected RightHandCompletor getNewInstance(Scope currentScope, IPartBinding partBinding, IDependencyRequestor dependencyRequestor,
				IProblemRequestor problemRequestor, IAnnotationBinding annotation) {
			return new RightHandCompletor(currentScope, partBinding, dependencyRequestor, problemRequestor, annotation, compilerOptions);
		}

		protected boolean canMixTypesInArrayLiterals() {
			return canMixTypesInArrayLiterals;
		}

		protected boolean validateValueWhenSetOnAnnotation() {
			return true;
		}

		public boolean visit(ArrayLiteral arrayLiteral) {
			List entries = new ArrayList();
			Iterator i = arrayLiteral.getExpressions().iterator();
			while (i.hasNext()) {
				Expression expr = (Expression) i.next();
				RightHandCompletor analyzer = getNewInstance(currentScope, partBinding, dependencyRequestor, problemRequestor, annotation);
				expr.accept(analyzer);
				entries.add(analyzer.getValue());
				if (analyzer.valueIsInvalid) {
					valueIsInvalid = true;
				}
			}
			if (entries.size() == 0) {
				value = new Object[0];
				return false;
			}

			i = entries.iterator();
			Object first = null;
			boolean hasMixedTypes = false;
			while (i.hasNext() && !hasMixedTypes) {
				Object entry = i.next();
				if (entry == IBinding.NOT_FOUND_BINDING) {
					value = new Object[] { IBinding.NOT_FOUND_BINDING };
					return false;
				}
				if (first == null) {
					first = entry;
				} else {
					if (entry != null && entry.getClass() != first.getClass()) {
						hasMixedTypes = true;
					}
				}
			}
			
			if (hasMixedTypes) {
				if (canMixTypesInArrayLiterals()) {
					value = entries.toArray(new Object[0]);
					return false;
				} else {
					problemRequestor.acceptProblem(arrayLiteral, IProblemRequestor.MIXED_LITERAL_TYPES_IN_ARRAY_LITERAL,
							new String[] { arrayLiteral.getCanonicalString() });
					value = null;
					return false;
				}
			}

			value = entries.toArray((Object[]) Array.newInstance(first == null ? Object.class : first.getClass(), entries.size()));
			return false;
		}

		public boolean visit(UnaryExpression unaryExpression) {
			if (unaryExpression.getOperator() == UnaryExpression.Operator.MINUS) {
				isNegative = !isNegative;
			}
			return true;
		}
		
		public boolean visit(FunctionInvocation functionInvocation) {
			super.visit(functionInvocation);
			value = functionInvocation.getTarget().resolveDataBinding();
			return false;
		}

		public void endVisit(FunctionInvocation functionInvocation) {
			super.endVisit(functionInvocation);
			ITypeBinding binding = functionInvocation.getTarget().resolveTypeBinding();
			if (binding != IBinding.NOT_FOUND_BINDING && binding != null && binding != AmbiguousFunctionBinding.getInstance()
					&& binding.isFunctionBinding()) {
				FunctionBinding functionBinding = (FunctionBinding) binding;
				ITypeBinding returnType = functionBinding.getReturnType();
				if (returnType == null) {
					problemRequestor.acceptProblem(functionInvocation, IProblemRequestor.FUNCTION_MUST_RETURN_TYPE,
							new String[] { functionBinding.getCaseSensitiveName() });
				} else {
					functionInvocation.setTypeBinding(returnType);
					dependencyRequestor.recordTypeBinding(returnType);
				}
			}
		}

		public Object getValue() {
			return value;
		}
	}

	private abstract static class DefaultValidValuesElement implements IValidValuesElement {
		public boolean isString() {
			return false;
		}

		public boolean isInt() {
			return false;
		}

		public boolean isFloat() {
			return false;
		}

		public boolean isRange() {
			return false;
		}

		public String getStringValue() {
			return null;
		}

		public long getLongValue() {
			return 0;
		}

		public double getFloatValue() {
			return 0;
		}

		public IValidValuesElement[] getRangeElements() {
			return null;
		}
	}

	public static class IntegerValidValuesElement extends DefaultValidValuesElement {
		
		long value;

		public IntegerValidValuesElement(long value) {
			this.value = value;
		}

		public boolean isInt() {
			return true;
		}

		public long getLongValue() {
			return value;
		}
	}

	public static class FloatValidValuesElement extends DefaultValidValuesElement {
		double value;

		public FloatValidValuesElement(double value) {
			this.value = value;
		}

		public boolean isFloat() {
			return true;
		}

		public double getFloatValue() {
			return value;
		}
	}

	public static class StringValidValuesElement extends DefaultValidValuesElement {
		String value;

		public StringValidValuesElement(String value) {
			this.value = value;
		}

		public boolean isString() {
			return true;
		}

		public String getStringValue() {
			return value;
		}
	}

	public static class RangeValidValuesElement extends DefaultValidValuesElement {
		IValidValuesElement[] value;

		public RangeValidValuesElement(IValidValuesElement[] value) {
			this.value = value;
		}

		public boolean isRange() {
			return true;
		}

		public IValidValuesElement[] getRangeElements() {
			return value;
		}
	}

	private abstract static class ValidValuesElementGatherer extends DefaultASTVisitor {
		boolean isNegative = false;

		public boolean visit(UnaryExpression unaryExpression) {
			if (unaryExpression.getOperator() == UnaryExpression.Operator.MINUS) {
				isNegative = !isNegative;
			}
			return true;
		}

		public boolean visit(IntegerLiteral integerLiteral) {
			String str = integerLiteral.getValue();
			if (isNegative) {
				str = "-" + str;
			}
			long i;
			try {
				i = Long.parseLong(str);
			} catch (NumberFormatException e) {
				i = isNegative ? Integer.MIN_VALUE : Integer.MAX_VALUE;
			}
			newValidValuesElement(new IntegerValidValuesElement(i));
			return false;
		}

		public boolean visit(FloatLiteral floatLiteral) {
			String str = floatLiteral.getValue();
			if (isNegative) {
				str = "-" + str;
			}
			newValidValuesElement(new FloatValidValuesElement(Double.parseDouble(str)));
			return false;
		}

		public boolean visit(DecimalLiteral decimalLiteral) {
			String str = decimalLiteral.getValue();
			if (isNegative) {
				str = "-" + str;
			}
			newValidValuesElement(new FloatValidValuesElement(Double.parseDouble(str)));
			return false;
		}

		public boolean visit(StringLiteral stringLiteral) {
			newValidValuesElement(new StringValidValuesElement(stringLiteral.getValue()));
			return false;
		}

		public boolean visit(HexLiteral stringLiteral) {
			newValidValuesElement(new StringValidValuesElement(stringLiteral.getValue()));
			return false;
		}

		public boolean visit(CharLiteral stringLiteral) {
			newValidValuesElement(new StringValidValuesElement(stringLiteral.getValue()));
			return false;
		}

		public boolean visit(DBCharLiteral stringLiteral) {
			newValidValuesElement(new StringValidValuesElement(stringLiteral.getValue()));
			return false;
		}

		public boolean visit(MBCharLiteral stringLiteral) {
			newValidValuesElement(new StringValidValuesElement(stringLiteral.getValue()));
			return false;
		}

		public abstract void newValidValuesElement(IValidValuesElement elem);
	}

	private static class ValidValuesRightHandCompletor extends RightHandCompletor {
		List validValuesElements;

		public ValidValuesRightHandCompletor(Scope currentScope, IPartBinding partBinding, IDependencyRequestor dependencyRequestor,
				IProblemRequestor problemRequestor, IAnnotationBinding annotation, ICompilerOptions compilerOptions) {
			super(currentScope, partBinding, dependencyRequestor, problemRequestor, annotation, compilerOptions);
		}

		public boolean visit(ArrayLiteral arrayLiteral) {
			validValuesElements = new ArrayList();
			Iterator i = arrayLiteral.getExpressions().iterator();
			while (i.hasNext()) {
				Expression expr = (Expression) i.next();
				expr.accept(new ValidValuesElementGatherer() {
					public boolean visit(ArrayLiteral arrayLiteral) {
						final List rangeElements = new ArrayList();
						for (Iterator iter = arrayLiteral.getExpressions().iterator(); iter.hasNext();) {
							ValidValuesElementGatherer gatherer = new ValidValuesElementGatherer() {
								public void newValidValuesElement(IValidValuesElement elem) {
									rangeElements.add(elem);
								}
							};
							((Expression) iter.next()).accept(gatherer);
						}
						if (rangeElements.size() == 2) {
							newValidValuesElement(new RangeValidValuesElement((IValidValuesElement[]) rangeElements
									.toArray(new IValidValuesElement[0])));
						} else {
							problemRequestor.acceptProblem(arrayLiteral, IProblemRequestor.PROPERTY_VALIDVALUES_INVALID_RANGE_VALUE);
						}
						return false;
					}

					public void newValidValuesElement(IValidValuesElement elem) {
						validValuesElements.add(elem);
					}
				});
			}
			value = (IValidValuesElement[]) validValuesElements.toArray(new IValidValuesElement[0]);
			return false;
		}
		
		protected boolean canMixTypesInArrayLiterals() {
			return true;
		}
	}

	private static class ContentsRightHandCompletor extends RightHandCompletor {

		public ContentsRightHandCompletor(Scope currentScope, IPartBinding partBinding, IDependencyRequestor dependencyRequestor,
				IProblemRequestor problemRequestor, IAnnotationBinding annotation, ICompilerOptions compilerOptions) {
			super(currentScope, partBinding, dependencyRequestor, problemRequestor, annotation, compilerOptions);
		}

		protected RightHandCompletor getNewInstance(Scope currentScope, IPartBinding partBinding, IDependencyRequestor dependencyRequestor,
				IProblemRequestor problemRequestor, IAnnotationBinding annotation) {
			return new ContentsRightHandCompletor(currentScope, partBinding, dependencyRequestor, problemRequestor, annotation,
					compilerOptions);
		}

		protected boolean canMixTypesInArrayLiterals() {
			return true;
		}

		protected boolean validateValueWhenSetOnAnnotation() {
			return false;
		}

		public boolean visit(IntegerLiteral integerLiteral) {
			String str = integerLiteral.getValue();
			if (isNegative) {
				str = "-" + str;
			}
			
			if (integerLiteral.getLiteralKind() == LiteralExpression.BIGINT_LITERAL) {
				try {
					value = new Long(str);
				} catch (NumberFormatException e) {
					problemRequestor.acceptProblem(integerLiteral, IProblemRequestor.BIGINT_LITERAL_OUT_OF_RANGE, new String[] { str });
					value = null;
				}
			}
			else if (integerLiteral.getLiteralKind() == LiteralExpression.SMALLINT_LITERAL) {
				try {
					value = new Short(str);
				} catch (NumberFormatException e) {
					problemRequestor.acceptProblem(integerLiteral, IProblemRequestor.SMALLINT_LITERAL_OUT_OF_RANGE, new String[] { str });
					value = null;
				}
			}
			else {
				try {
					value = new Integer(str);
				} catch (NumberFormatException e) {
					problemRequestor.acceptProblem(integerLiteral, IProblemRequestor.INTEGER_LITERAL_OUT_OF_RANGE, new String[] { str });
					value = null;
				}
			}
			
			return false;
		}

		public boolean visit(DecimalLiteral decimalLiteral) {
			String str = decimalLiteral.getValue();
			if (isNegative) {
				str = "-" + str;
			}
			value = new BigDecimal(str);
			return false;
		}

		public void endVisit(IntegerLiteral integerLiteral) {
			integerLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.INT));
		}

		public void endVisit(FloatLiteral floatLiteral) {
			floatLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.FLOAT));
		}

		public void endVisit(BooleanLiteral booleanLiteral) {
			booleanLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN));
		}

		public void endVisit(DecimalLiteral decimalLiteral) {
			decimalLiteral.setTypeBinding(PrimitiveTypeBinding.getInstance(Primitive.FLOAT));
		}
	}

	private static SystemScope getSystemScope(Scope scope) {
		if (scope == null) {
			return null;
		}
		if (scope.isSystemScope()) {
			return (SystemScope)scope;
		}
		
		return getSystemScope(scope.getParentScope());
	}

	public SettingsBlockAnnotationBindingsCompletor(Scope currentScope, IPartBinding partBinding,
			AnnotationLeftHandScope annotationLeftHandScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor,
			ICompilerOptions compilerOptions) {
		super(currentScope, partBinding, dependencyRequestor, problemRequestor, compilerOptions);
		this.partBinding = partBinding;
		this.annotationLeftHandScope = annotationLeftHandScope;
	}

	public boolean visit(SettingsBlock settingsBlock) {
		settingsBlock.accept(new AbstractASTExpressionVisitor() {

			public boolean visit(Assignment assignment) {
				LeftHandCompletor leftHandCompletor = new LeftHandCompletor(annotationLeftHandScope, partBinding, dependencyRequestor,
						problemRequestor, compilerOptions);

				assignment.getLeftHandSide().accept(leftHandCompletor);

				IAnnotationBinding annotation = leftHandCompletor.getAnnotation();
				if (annotation != null) {
					AnnotationRightHandScope rightHandScope = new AnnotationRightHandScope(currentScope, annotation
							.getAnnotationType());
					RightHandCompletor rightHandCompletor = getRightHandCompletor(rightHandScope, leftHandCompletor);
					assignment.getRightHandSide().accept(rightHandCompletor);

					if (leftHandCompletor.getAnnotationLeftHandScope().getBindingBeingAnnotated().isOpenUIStatementBinding()
							&& ((IAnnotationTypeBinding) annotation.getType()).takesExpressionInOpenUIStatement()) {
						annotation.setValue(assignment.getRightHandSide(), problemRequestor,
								assignment.getRightHandSide(), compilerOptions, false);
					} else {
						annotation.setValue(rightHandCompletor.getValue(), problemRequestor,
								assignment.getRightHandSide(), compilerOptions, rightHandCompletor.validateValueWhenSetOnAnnotation());
					}

					if (!leftHandCompletor.isNotApplicable() && !rightHandCompletor.valueIsInvalid
							&& annotation.getValue() != null
							&& annotation.getValue() != IBinding.NOT_FOUND_BINDING) {
						assignment.setBinding(annotation);
						storeAnnotationInBinding((AnnotationBinding) annotation);
					}
					if (annotationLeftHandScope.getNotApplicableBinding() == null) {
						IProblemRequestor origPRequestor = problemRequestor;
						problemRequestor = new DataItemPropertiesProblemsProblemRequestor(origPRequestor, leftHandCompletor.leftHandScope.getBindingBeingAnnotated(), annotation.getAnnotationType());
						
						processResolvableProperties(annotation, assignment.getRightHandSide());
						
						problemRequestor = origPRequestor;
					}
				}
				annotationLeftHandScope.resetNotApplicableBinding();
				return false;
			}

			public boolean visit(AnnotationExpression annotationExpression) {
				ExpressionVisitor expressionVisitor = new ExpressionVisitor();
				annotationExpression.accept(expressionVisitor);
				IDataBinding expressionDataBinding = annotationExpression.resolveDataBinding();
				if (Binding.isValidBinding(expressionDataBinding)) {
					if (expressionDataBinding.isAnnotationBinding()) {
						AnnotationBinding annotationBinding = (AnnotationBinding) expressionDataBinding;
						storeAnnotationInBinding(annotationBinding);
					}
					else {
						
						problemRequestor.acceptProblem(annotationExpression, IProblemRequestor.NOT_AN_ANNOTATION,
								new String[] { annotationExpression.getCanonicalString() });
					}
				}
				return false;
			}

			public boolean visit(SetValuesExpression setValuesExpression) {
				ExpressionVisitor expressionVisitor = new ExpressionVisitor();
				setValuesExpression.getExpression().accept(expressionVisitor);
				IDataBinding expressionDataBinding = setValuesExpression.getExpression().resolveDataBinding();
				if (expressionDataBinding != null && expressionDataBinding != IBinding.NOT_FOUND_BINDING
						&& expressionDataBinding.isAnnotationBinding()) {
					AnnotationBinding annotationBinding = (AnnotationBinding) expressionDataBinding;
					storeAnnotationInBinding(annotationBinding);
					AnnotationLeftHandScope newScope = new AnnotationLeftHandScope(annotationLeftHandScope, expressionDataBinding,
							expressionDataBinding.getType(), expressionDataBinding, -1, partBinding);
					
					Scope sysScope = SettingsBlockAnnotationBindingsCompletor.getSystemScope(newScope);
					if (sysScope == null) {
						sysScope =  NullScope.INSTANCE;
					}
					newScope.setScopeToUseWhenResolving(sysScope);
					SettingsBlockAnnotationBindingsCompletor completor = new SettingsBlockAnnotationBindingsCompletor(currentScope,
							partBinding, newScope, dependencyRequestor, problemRequestor, compilerOptions);
					setValuesExpression.getSettingsBlock().accept(completor);
					return false;
				}

				if (expressionDataBinding != null && expressionDataBinding != IBinding.NOT_FOUND_BINDING) {
					final AnnotationLeftHandScope newScope = createNewScope(expressionDataBinding, setValuesExpression.getExpression()
							.resolveTypeBinding(), annotationLeftHandScope.getTopLevelAnnotationLeftHandScope(), expressionVisitor
							.getIndex());
					if(annotationLeftHandScope.isWithinNewExpression()) {
						newScope.setResolveToAnnotations(false);
						newScope.setWithinNewExpression(true);
					}
					SettingsBlockAnnotationBindingsCompletor completor = new SettingsBlockAnnotationBindingsCompletor(currentScope,
							partBinding, newScope, dependencyRequestor, problemRequestor, compilerOptions);
					if (newScope.getBindingToHoldAnnotation().isDataBinding()) {
						final IDataBinding[][] pathArr = new IDataBinding[1][];
						pathArr[0] = newScope.getPath();
						setValuesExpression.getExpression().accept(new AbstractASTExpressionVisitor() {
							public boolean visitExpression(Expression expression) {
								return true;
							}

							public boolean visit(ArrayAccess arrayAccess) {
								arrayAccess.getArray().accept(this);
								return false;
							}

							public void endVisitExpression(Expression expression) {
								IDataBinding dataBinding = expression.resolveDataBinding();
								if (dataBinding != null && dataBinding != IBinding.NOT_FOUND_BINDING) {
									pathArr[0] = addElement(dataBinding, pathArr[0], (IDataBinding) newScope.getBindingToHoldAnnotation());
								}
							}
						});

						newScope.setPath(pathArr[0]);
					}
					setValuesExpression.getSettingsBlock().accept(completor);
					return false;
				}

				return false;
			}

			public boolean visitExpression(Expression expression) {
				// This method is invoked if the settings block contains a node
				// other
				// than an assignment or set values expression. Such a node is
				// only
				// allowed in a settings block on a declaration of an array.
				IBinding bindingBeingAnnotated = annotationLeftHandScope.getBindingBeingAnnotated();
				if (bindingBeingAnnotated != null && bindingBeingAnnotated != IBinding.NOT_FOUND_BINDING) {
					if (bindingBeingAnnotated.isDataBinding()) {
						bindingBeingAnnotated = ((IDataBinding) bindingBeingAnnotated).getType();
					}
					if(bindingBeingAnnotated != null) {
						boolean isArray = bindingBeingAnnotated.isTypeBinding()
								&& ITypeBinding.ARRAY_TYPE_BINDING == ((ITypeBinding) bindingBeingAnnotated).getKind();
						if (!isArray) {
							if (bindingBeingAnnotated.isTypeBinding()
									&& ITypeBinding.ANNOTATION_BINDING == ((ITypeBinding) bindingBeingAnnotated).getKind()
									&& ((IAnnotationTypeBinding) bindingBeingAnnotated).hasSingleValue()) {
								LeftHandCompletor leftHandCompletor = new LeftHandCompletor(annotationLeftHandScope, partBinding,
										dependencyRequestor, problemRequestor, compilerOptions);
								String newFieldName = (String) ((IAnnotationTypeBinding) bindingBeingAnnotated).getFieldNames().iterator()
										.next();
								IAnnotationBinding newField = (IAnnotationBinding) ((IAnnotationTypeBinding) bindingBeingAnnotated)
										.findData(newFieldName);
								leftHandCompletor.setAnnotation(newField);
								AnnotationRightHandScope rightHandScope = new AnnotationRightHandScope(currentScope,
										((IAnnotationTypeBinding) bindingBeingAnnotated));
								RightHandCompletor rightHandCompletor = getRightHandCompletor(rightHandScope, leftHandCompletor);
								expression.accept(rightHandCompletor);
	
								leftHandCompletor.getAnnotation().setValue(rightHandCompletor.getValue(), problemRequestor, expression,
										compilerOptions);
	
								if (!leftHandCompletor.isNotApplicable() && leftHandCompletor.getAnnotation().getValue() != null
										&& leftHandCompletor.getAnnotation().getValue() != IBinding.NOT_FOUND_BINDING) {
									((IAnnotationBinding) annotationLeftHandScope.getBindingBeingAnnotated()).addField(newField);
								}
								if (annotationLeftHandScope.getNotApplicableBinding() == null) {
									processResolvableProperties(leftHandCompletor.getAnnotation(), expression);
								}
							} else {
								problemRequestor.acceptProblem(expression, IProblemRequestor.POSITIONAL_PROPERTY_NOT_VALID_FOR,
										new String[] { bindingBeingAnnotated.getCaseSensitiveName() });
							}
						}
					}
				}

				return false;
			}
		});

		return false;
	}

	private AnnotationLeftHandScope createNewScope(IDataBinding dataBinding, ITypeBinding typeBinding, AnnotationLeftHandScope oldScope,
			int index) {
		IBinding bindingToHoldAnnotation = oldScope.getBindingToHoldAnnotation();
		// If the binding is in the part that contains the settingsBlock, we can
		// just put the annotation
		// right on the binding
		if (bindingToHoldAnnotation != null && shouldPutAnnotationOnDataBinding(dataBinding, bindingToHoldAnnotation)) {
			bindingToHoldAnnotation = dataBinding;
		}
		Scope dbScope = new DataBindingScope(currentScope, dataBinding);
		AnnotationLeftHandScope scope = new AnnotationLeftHandScope(dbScope, dataBinding, typeBinding, bindingToHoldAnnotation, index,
				partBinding);
		scope.setPath(oldScope.getPath());
		return scope;
	}

	private boolean shouldPutAnnotationOnDataBinding(IDataBinding dataBinding, IBinding originalBinding) {
		if (originalBinding == dataBinding.getDeclaringPart()) {
			return true;
		}

		if (originalBinding.isTypeBinding() && ((ITypeBinding) originalBinding).getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
			return true;
		}

		if (originalBinding.isDataBinding() && ((IDataBinding) originalBinding).getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
			return true;
		}
		return false;
	}

	private boolean isTuiForm(IAnnotationBinding ann) {
		return (ann.getDeclaringPart() != null && ann.getDeclaringPart().getKind() == ITypeBinding.FORM_BINDING);
	}
		
	private RightHandCompletor getRightHandCompletor(Scope rightHandScope, LeftHandCompletor leftHandCompletor) {
		IAnnotationBinding annotationBinding;
		IAnnotationTypeBinding annotationTypeBinding;
		annotationBinding = leftHandCompletor.getAnnotation();
		annotationTypeBinding = annotationBinding.getAnnotationType();

		if (leftHandCompletor.isNotApplicable()) {
			return new RightHandCompletor(rightHandScope, partBinding, dependencyRequestor, problemRequestor, annotationBinding,
					compilerOptions);
		}
		if (annotationIs(annotationTypeBinding, new String[] { "egl", "ui" }, "validValues")) {
			return new ValidValuesRightHandCompletor(rightHandScope, partBinding, dependencyRequestor, problemRequestor, annotationBinding,
					compilerOptions);
		}
		IAnnotationTypeBinding enclosingAnnotationType = annotationBinding.getEnclosingAnnotationType();
		if (enclosingAnnotationType != null && enclosingAnnotationType.isPartSubType()
				&& InternUtil.intern("contents") == annotationBinding.getName()) {
			return new ContentsRightHandCompletor(rightHandScope, partBinding, dependencyRequestor, problemRequestor, annotationBinding,
					compilerOptions);
		}

		return new RightHandCompletor(rightHandScope, partBinding, dependencyRequestor, problemRequestor, annotationBinding,
				compilerOptions);
	}

	private void processResolvableProperties(IAnnotationBinding annotation, Expression valueExpr) {
		if (annotation.getValue() == null) {
			// must have been an invalid value
			return;
		}
		
		IAnnotationTypeBinding annotationTypeBinding;
		
		if (annotation.isAnnotationField()) {
			annotationTypeBinding = annotation.getEnclosingAnnotationType();
			if (annotationIs(annotationTypeBinding, new String[] { "egl", "ui", "webTransaction" }, "programLinkData")) {
				
				if (!inRecord(valueExpr)) {
					if (annotation.getName() == InternUtil.intern(IEGLConstants.PROPERTY_PROGRAMNAME)) {
						annotation.setValue(resolveProgramName(valueExpr), null, null, compilerOptions, false);
						return;
					}
					if (annotation.getName() == InternUtil.intern(IEGLConstants.PROPERTY_UIRECORDNAME)) {
						annotation.setValue(resolveUIRecordName(valueExpr), null, null, compilerOptions, false);
						return;
					}
				}
			}
			
		}
		
		annotationTypeBinding = annotation.getAnnotationType();
		if (annotationIs(annotationTypeBinding, new String[] { "egl", "ui" }, "validatorDataTable")) {
			annotation.setValue(resolveValidatorDataTable(valueExpr), null, null, compilerOptions, false);
			return;
		}

		if (annotationIs(annotationTypeBinding, new String[] { "egl", "ui", "console" }, "dataType")) {
			annotation.setValue(resolveDataType(valueExpr), null, null, compilerOptions, false);
			return;
		}

		if (annotationIs(annotationTypeBinding, new String[] { "egl", "ui" }, "validatorFunction")) {
			boolean mustFind = !isTuiForm(annotation);
			annotation.setValue(resolveFunctionRef(valueExpr, false, false, IEGLConstants.PROPERTY_VALIDATORFUNCTION, mustFind), null, null, compilerOptions, false);
			return;
		}

		if (annotationIs(annotationTypeBinding, new String[] { "egl", "ui" }, "numElementsItem")) {
			annotation.setValue(resolveNumElementsItem(valueExpr), null, null, compilerOptions, false);
			return;
		}

		if (annotationIs(annotationTypeBinding, new String[] { "egl", "ui", "text" }, "msgField")) {
			annotation.setValue(resolveMsgField(valueExpr), null, null, compilerOptions, false);
			return;
		}
		
		if (annotationIs(annotationTypeBinding, new String[] { "egl", "ui"}, "ValidationPropertiesLibrary")) {
			annotation.setValue(resolveRuiPropertiesLib(valueExpr, "ValidationPropertiesLibrary"), null, null, compilerOptions, false);
			return;
		}


		String annotationName = annotation.getName();
		IAnnotationTypeBinding enclosingAnnotationType = annotation.getEnclosingAnnotationType();

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "ui", "text" }, "textForm")
				|| annotationIs(enclosingAnnotationType, new String[] { "egl", "ui", "text" }, "printForm")
				|| annotationIs(enclosingAnnotationType, new String[] { "egl", "ui", "jsf" }, "JSFHandler")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORFUNCTION) == annotationName) {
				annotation.setValue(resolveFunctionRef(valueExpr, false, false, IEGLConstants.PROPERTY_VALIDATORFUNCTION, true), null, null, compilerOptions, false);
				return;
			}
		}				

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "io", "dli" }, "dli")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_PSB) == annotationName) {
				annotation.setValue(resolvePSB(valueExpr), null, null, compilerOptions, false);
				return;
			}

			if (InternUtil.intern(IEGLConstants.PROPERTY_PSBPARM) == annotationName) {
				annotation.setValue(resolvePSBParm(valueExpr), null, null, compilerOptions, false);
				return;
			}

			if (InternUtil.intern(IEGLConstants.PROPERTY_PCBPARMS) == annotationName) {
				annotation.setValue(resolvePCBParms(valueExpr), null, null, compilerOptions, false);
				return;
			}
		}

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "ui", "text" }, "textUIProgram")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_INPUTFORM) == annotationName) {
				annotation.setValue(resolveInputForm(valueExpr), null, null, compilerOptions, false);
				return;
			}
		}

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "ui", "webTransaction" }, "vgwebtransaction")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_INPUTUIRECORD) == annotationName) {
				annotation.setValue(resolveInputUIRecord(valueExpr), null, null, compilerOptions, false);
				return;
			}
		}

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "core" }, "BasicProgram")
				|| annotationIs(enclosingAnnotationType, new String[] { "egl", "ui", "text" }, "TextUIProgram")
				|| annotationIs(enclosingAnnotationType, new String[] { "egl", "ui", "webTransaction" }, "VGWebTransaction")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_INPUTRECORD) == annotationName) {
				annotation.setValue(resolveInputRecord(valueExpr), null, null, compilerOptions, false);
				return;
			}
		}

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "ui", "gateway" }, "UIProgram")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_INPUTRECORD) == annotationName) {
				annotation.setValue(resolveInputRecord(valueExpr), null, null, compilerOptions, false);
				return;
			}
			if (InternUtil.intern(IEGLConstants.PROPERTY_INPUTUIRECORD) == annotationName) {
				annotation.setValue(resolveUIProgramInputUiRecord(valueExpr), null, null, compilerOptions, false);
				return;
			}
		}
		
		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "io", "dli" }, "relationship")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_PARENTRECORD) == annotationName
					|| InternUtil.intern(IEGLConstants.PROPERTY_SEGMENTRECORD) == annotationName) {
				annotation.setValue(resolveSegmentRecordOrParentRecord(valueExpr, annotationTypeBinding, annotationName), null, null,
						compilerOptions, false);
				return;
			}
		}

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "ui", "jsf" }, "JSFHandler")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION) == annotationName) {
				annotation.setValue(resolveFunctionRef(valueExpr, true, true, IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION, true), null, null, compilerOptions, false);
				return;
			}
		}

		if (annotationIs(enclosingAnnotationType, new String[] { "eglx", "lang" }, "EGLProperty")) {
			
			if (shouldResolveEGLPropertyValues()) {
				if (InternUtil.intern(IEGLConstants.PROPERTY_GETMETHOD) == annotationName) {
					Object result = resolveFunctionRef(valueExpr, false, false, IEGLConstants.PROPERTY_GETMETHOD, true);
					if (result instanceof IBinding && Binding.isValidBinding((IBinding)result)) {
						annotation.setValue(result, null, null, compilerOptions, false);
					}
					return;
				}
				if (InternUtil.intern(IEGLConstants.PROPERTY_SETMETHOD) == annotationName) {
					Object result = resolveFunctionRef(valueExpr, false, false, IEGLConstants.PROPERTY_SETMETHOD, true);
					if (result instanceof IBinding && Binding.isValidBinding((IBinding)result)) {
						annotation.setValue(result, null, null, compilerOptions, false);
					}
					return;
				}
			}
		}

		
		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "ui", "jsf" }, "JSFHandler")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_VALIDATIONBYPASSFUNCTIONS) == annotationName) {
				annotation.setValue(resolveValidationBypassFunctions(valueExpr), null, null, compilerOptions, false);
				return;
			}
			if (InternUtil.intern(IEGLConstants.PROPERTY_VIEWROOTVAR) == annotationName) {
				annotation.setValue(resolveViewRootVar(valueExpr), null, null, compilerOptions, false);
				return;
			}
		}

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "io", "file" }, "IndexedRecord")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_KEYITEM) == annotationName) {
				annotation.setValue(resolveKeyItem(valueExpr), null, null, compilerOptions, false);
				return;
			}
		}

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "io", "file" }, "RelativeRecord")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_RECORDNUMITEM) == annotationName) {
				annotation.setValue(resolveRecordNumItem(valueExpr), null, null, compilerOptions, false);
				return;
			}
		}

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "io", "file" }, "IndexedRecord")
				|| annotationIs(enclosingAnnotationType, new String[] { "egl", "io", "mq" }, "MQRecord")
				|| annotationIs(enclosingAnnotationType, new String[] { "egl", "io", "file" }, "SerialRecord")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_NUMELEMENTSITEM) == annotationName) {
				annotation.setValue(resolveNumElementsItem(valueExpr), null, null, compilerOptions, false);
				return;
			}

			if (InternUtil.intern(IEGLConstants.PROPERTY_LENGTHITEM) == annotationName) {
				annotation.setValue(resolveLengthItem(valueExpr), null, null, compilerOptions, false);
				return;
			}
		}

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "io", "dli" }, "DLISegment")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_KEYITEM) == annotationName) {
				annotation.setValue(resolveKeyItem(valueExpr), null, null, compilerOptions, false);
				return;
			}

			if (InternUtil.intern(IEGLConstants.PROPERTY_LENGTHITEM) == annotationName) {
				annotation.setValue(resolveLengthItem(valueExpr), null, null, compilerOptions, false);
				return;
			}
		}

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "ui", "webTransaction" }, "VGUIRecord")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_COMMANDVALUEITEM) == annotationName) {
				annotation.setValue(resolveCommandValueItem(valueExpr), null, null, compilerOptions, false);
				return;
			}

			if (InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORFUNCTION) == annotationName) {
				annotation.setValue(resolveFunctionRef(valueExpr, false, false, IEGLConstants.PROPERTY_VALIDATORFUNCTION, true), null, null, compilerOptions, false);
				return;
			}
		}

		if (annotationIs(enclosingAnnotationType, new String[] { "egl", "io", "sql" }, "SQLRecord")) {
			if (InternUtil.intern(IEGLConstants.PROPERTY_KEYITEMS) == annotationName) {
				annotation.setValue(resolveKeyItems(valueExpr), null, null, compilerOptions, false);
				return;
			}
		}

		if (ArrayTypeBinding.getInstance(SystemPartManager.RECORDREF_BINDING) == annotationTypeBinding.getSingleValueType()) {
			annotation.setValue(resolveRecordList(valueExpr), null, null, compilerOptions, false);
			return;
		}
		
		if(annotationTypeBinding.hasSingleValue()) {
			if(SystemPartManager.FUNCTIONREF_BINDING == annotationTypeBinding.getSingleValueType()) {
				annotation.setValue(resolveFunctionRef(valueExpr, true, true, annotationTypeBinding.getCaseSensitiveName(), true), null, null, compilerOptions, false);
				return;
			}
			
			if(SystemPartManager.FUNCTIONMEMBERREF_BINDING == annotationTypeBinding.getSingleValueType()) {
				annotation.setValue(resolveFunctionRef(valueExpr, false, false, annotationTypeBinding.getCaseSensitiveName(), true), null, null, compilerOptions, false);
				return;
			}
			
			if(SystemPartManager.FIELDREF_BINDING == annotationTypeBinding.getSingleValueType()) {
				annotation.setValue(resolveFieldRef(valueExpr, annotationTypeBinding.getCaseSensitiveName()), null, null, compilerOptions, false);
				return;
			}
			
			if(SystemPartManager.FIELDINTARGETREF_BINDING == annotationTypeBinding.getSingleValueType()) {
				annotation.setValue(resolveFieldInTargetRef(valueExpr), null, null, compilerOptions, false);
				return;
			}
		}
	}
	
	private boolean shouldResolveEGLPropertyValues() {
		if (annotationLeftHandScope == null || annotationLeftHandScope.getParentScope() == null || !annotationLeftHandScope.getParentScope().isAnnotationLeftHandScope()) {
			return true;
		}
	
		AnnotationLeftHandScope superscope = (AnnotationLeftHandScope)annotationLeftHandScope.getParentScope();
		if (Binding.isValidBinding(superscope.getBindingBeingAnnotated()) && superscope.getBindingBeingAnnotated().isDataBinding()) {
			IDataBinding db = (IDataBinding) superscope.getBindingBeingAnnotated();
			if (db.isAnnotationBinding() && db.getName() == InternUtil.intern(IEGLConstants.PROPERTY_FIELDDECLARATION)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean inRecord(Node node) {
		if (node == null) {
			return false;
		}
		final boolean[] isRec = new boolean[1];
		
		IASTVisitor visitor = new DefaultASTVisitor() {
			public boolean visit(Record record) {
				isRec[0] = true;
				return false;
			}
		};
		node.accept(visitor);
		
		if (isRec[0]) {
			return true;
		}
		
		return inRecord(node.getParent());
	}

	private Object resolveRecordList(final Expression valueExpr) {
		final List result = new ArrayList();
		valueExpr.accept(new DefaultASTVisitor() {
			public boolean visit(ArrayLiteral arrayLiteral) {
				return true;
			}

			public boolean visit(SimpleName simpleName) {
				return visitName(simpleName);
			}

			public boolean visit(QualifiedName qualifiedName) {
				return visitName(qualifiedName);
			}

			public boolean visitName(final Name name) {
				try {
					ITypeBinding type = bindTypeName(name);
					if (type.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
						if (!type.isValid()) {
							type = ((IPartBinding) type).realize();
						}
						result.add(type);
					} else {
						// TODO: issue error
					}
				} catch (ResolutionException e) {
					problemRequestor.acceptProblem(name, e.getProblemKind(), IMarker.SEVERITY_ERROR, e.getInserts());
				}
				return false;
			}
		});

		return (FlexibleRecordBinding[]) result.toArray(new FlexibleRecordBinding[0]);
	}

	private Object resolveProgramName(Expression expr) {
		
		StringLiteral lit = (StringLiteral) expr;
		
		String nameString = lit.getValue();
		
		Name name = new ExpressionParser(compilerOptions).parseAsName(nameString);
		if (name == null) {
			return nameString;
		}
		
		try {
			ITypeBinding type = bindTypeName(name);
			if (type.getKind() == ITypeBinding.PROGRAM_BINDING) {
				return type;
			}
		} catch (ResolutionException e) {}
		return nameString;
	}

	private Object resolveUIRecordName(Expression expr) {
		
		StringLiteral lit = (StringLiteral) expr;
		
		String nameString = lit.getValue();
		
		Name name = new ExpressionParser(compilerOptions).parseAsName(nameString);
		if (name == null) {
			return nameString;
		}
		
		try {
			ITypeBinding type = bindTypeName(name);
			if (type.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
				return type;
			}
		} catch (ResolutionException e) {}
		return nameString;
	}

	private IBinding resolveValidatorDataTable(Expression expr) {
		Name name = (Name) expr;
		String tableName = name.getCanonicalName();

		try {
			ITypeBinding type = bindTypeName(name);
			if (type.getKind() == ITypeBinding.DATATABLE_BINDING) {
				DataTableBinding table = (DataTableBinding) type;
				if (isValidAsValidatorTable(table)) {
					return table;
				} else {
					problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_INVALID_VALIDATORTABLE_TABLE_TYPE,
							IMarker.SEVERITY_ERROR, new String[] { tableName, IEGLConstants.PROPERTY_VALIDATORDATATABLE });
				}
			} else {
				problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERPTY_MUST_RESOLVE_TO_DATATABLE, IMarker.SEVERITY_ERROR,
						new String[] { tableName, IEGLConstants.PROPERTY_VALIDATORDATATABLE });
			}
			return IBinding.NOT_FOUND_BINDING;
		} catch (ResolutionException e) {
			problemRequestor.acceptProblem(expr, e.getProblemKind(), IMarker.SEVERITY_ERROR, e.getInserts());
			return IBinding.NOT_FOUND_BINDING;
		}
	}

	private boolean isValidAsValidatorTable(DataTableBinding table) {
		if (!table.isValid) {
			table = (DataTableBinding) table.realize();
		}
		IPartSubTypeAnnotationTypeBinding subType = table.getSubType();
		if (subType == null) {
			return false;
		}
		if (!annotationIs(subType, new String[] { "egl", "core" }, "MatchValidTable")
				&& !annotationIs(subType, new String[] { "egl", "core" }, "MatchInvalidTable")
				&& !annotationIs(subType, new String[] { "egl", "core" }, "RangeChkTable")) {
			return false;
		}

		return true;
	}
	
	private Object resolveFieldRef(final Expression expr, final String annotationName) {
		final Object[] result = new Object[] { null };
		expr.accept(new AbstractASTExpressionVisitor() {
			public boolean visitName(Name name) {
				Name valueName = null;
				try {
					valueName = new ExpressionParser(compilerOptions).parseAsName(expr.getCanonicalString());
					bindExpressionName(valueName);					
					result[0] = valueName;					
				} catch (ResolutionException e) {
					result[0] = null;
				} finally {
					if(valueName != null) {
						valueName.copyBindingsTo(name);
					}
				}
				return false;
			}
		});
		if (result[0] == null){//IBinding.NOT_FOUND_BINDING) {
			problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_MUST_RESOLVE_TO_DATA_DECLARATION,
					IMarker.SEVERITY_ERROR, new String[] { annotationName });
		}
		return result[0];

	}
	
	private boolean containsField(ITypeBinding type, IDataBinding dataBinding) {
		
		if (type == null || dataBinding == null) {
			return false;
		}
			
		if (type.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
			FlexibleRecordBinding rec = (FlexibleRecordBinding) type;
			return rec.getDeclaredFields().contains(dataBinding);
		}

		if (dataBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
			StructureItemBinding si =  (StructureItemBinding) dataBinding;
			return si.getEnclosingStructureBinding() == type;
		}

		return false;
	}
	
	private Object resolveFieldInTargetRef(final Expression expr) {
		final Object[] value = new Object[] {null};

		IBinding binding = annotationLeftHandScope.getTopLevelAnnotationLeftHandScope().getBindingBeingAnnotated();
		if (!binding.isDataBinding() && !binding.isTypeBinding() && !binding.isUsedTypeBinding()) {
			return null;
		}
		
		IDataBinding targetDataBinding = null;
		ITypeBinding typeBinding;
		Scope saveScope = currentScope;
		if (binding.isTypeBinding()) {
			typeBinding = (ITypeBinding) binding;
			currentScope = new TypeBindingScope(NullScope.INSTANCE, typeBinding, null);
		} else if(binding.isDataBinding()) {
			targetDataBinding = (IDataBinding) binding;
			currentScope = new DataBindingScope(NullScope.INSTANCE, targetDataBinding, true);
			typeBinding = targetDataBinding.getType();
		} else {
			UsedTypeBinding usedBinding = (UsedTypeBinding) binding;
			typeBinding = usedBinding.getType();
			currentScope = new TypeBindingScope(NullScope.INSTANCE, typeBinding, null, true);
		}
		
		final ITypeBinding fTypeBinding = typeBinding;
		expr.accept(new AbstractASTExpressionVisitor() {
			public boolean visitName(Name name) {
				Name valueName = null;
				try {
					valueName = new ExpressionParser(compilerOptions).parseAsName(expr.getCanonicalString());
					IDataBinding dBinding = bindExpressionName(valueName);
					if(dBinding != null && IBinding.NOT_FOUND_BINDING != dBinding) {
						ITypeBinding baseTargetType = fTypeBinding.getBaseType();
						if(!baseTargetType.isPartBinding() || dBinding.getDeclaringPart() == baseTargetType || containsField((IPartBinding)baseTargetType, dBinding)) {
							valueName.setBinding(dBinding);							
							value[0] = valueName;
						}
					}
				} catch (ResolutionException e) {					
				} finally {
					if(valueName != null) {
						valueName.copyBindingsTo(name);
					}
				}
				return false;
			}
		});
		
		if(value[0] == null) {
			problemRequestor.acceptProblem(expr, IProblemRequestor.VARIABLE_NOT_FOUND, IMarker.SEVERITY_ERROR,
				new String[] { expr.getCanonicalString() });
		}
		else if(targetDataBinding != null) {
			((Name) value[0]).setAttribute(Name.IMPLICIT_QUALIFIER_DATA_BINDING, targetDataBinding);
		}

		currentScope = saveScope;
		return value[0];
	}

	private Object resolveFunctionRef(Expression expr, boolean allowTopLevelFunctions, boolean allowLibraryFunctions, String annotationName, boolean errorIfNotFound) {
		Name name = (Name) expr;

		final Name[] qualifier = new Name[1];
		final SimpleName[] simpName = new SimpleName[1];
		name.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(SimpleName simpleName) {
				simpName[0] = simpleName;
				return false;
			}

			public boolean visit(QualifiedName qualifiedName) {
				simpName[0] = new SimpleName(qualifiedName.getIdentifier(), 0, 0);
				qualifier[0] = qualifiedName.getQualifier();
				return false;
			}
		});

		Object result;
		if(!allowTopLevelFunctions) {
			dependencyRequestor.stopRecordingTopLevelFunctionBindings();
		}
		try {
			IDataBinding resultDBinding = bindExpressionName(name, true);
			if (IDataBinding.OVERLOADED_FUNCTION_SET_BINDING == resultDBinding.getKind()) {
				resultDBinding = (IDataBinding) ((OverloadedFunctionSet) resultDBinding).getNestedFunctionBindings().get(0);
			}
			if (resultDBinding.getType() != null && resultDBinding.getType().isFunctionBinding()) {
				if(!allowLibraryFunctions && ITypeBinding.LIBRARY_BINDING == resultDBinding.getDeclaringPart().getKind()) {					
					result = IBinding.NOT_FOUND_BINDING;
					problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_MUST_NOT_RESOLVE_TO_LIBRARY_FUNCTION, IMarker.SEVERITY_ERROR, new String[] {annotationName, name.getCanonicalName()});
				}
				else {
					result = (IFunctionBinding) resultDBinding.getType();
				}
			} else {
				result = IBinding.NOT_FOUND_BINDING;
				problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_MUST_RESOLVE_TO_FUNCTION, IMarker.SEVERITY_ERROR, new String[] {annotationName, name.getCanonicalName()});
			}
		} catch (ResolutionException e) {
			if (IProblemRequestor.VARIABLE_NOT_FOUND == e.getProblemKind()) {
				
				if (errorIfNotFound) {
					problemRequestor.acceptProblem(expr, IProblemRequestor.FUNCTION_REFERENCE_CANNOT_BE_RESOLVED, IMarker.SEVERITY_ERROR, e
							.getInserts());
					result = IBinding.NOT_FOUND_BINDING;
				}
				else {
					result = name.getCanonicalName();
				}
			} else if (IProblemRequestor.VARIABLE_ACCESS_AMBIGUOUS == e.getProblemKind()) {
				problemRequestor
						.acceptProblem(expr, IProblemRequestor.FUNCTION_REFERENCE_AMBIGUOUS, IMarker.SEVERITY_ERROR, e.getInserts());
				result = IBinding.NOT_FOUND_BINDING;
			} else {
				problemRequestor.acceptProblem(expr, e.getProblemKind(), IMarker.SEVERITY_ERROR, e.getInserts());
				result = IBinding.NOT_FOUND_BINDING;
			}
		}
		if(!allowTopLevelFunctions) {
			dependencyRequestor.startRecordingTopLevelFunctionBindings();
		}
		return result;
	}
	
	private IBinding resolveSegmentRecordOrParentRecord(Expression expr, IAnnotationTypeBinding annotationType, String propertyName) {
		Name name = (Name) expr;
		String recordName = name.getCanonicalName();

		try {
			ITypeBinding type = bindTypeName(name);
			if (type.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
				FixedRecordBinding record = (FixedRecordBinding) type;
				if (isDliSegmentRecord(record)) {
					return record;
				}
			}
			problemRequestor.acceptProblem(expr, IProblemRequestor.PART_IN_PROPERTY_WRONG_RECORD_TYPE, IMarker.SEVERITY_ERROR,
					new String[] { recordName, propertyName, "DLISegment" });
			return IBinding.NOT_FOUND_BINDING;
		} catch (ResolutionException e) {
			problemRequestor.acceptProblem(expr, e.getProblemKind(), IMarker.SEVERITY_ERROR, e.getInserts());
			return IBinding.NOT_FOUND_BINDING;
		}
	}

	private IBinding resolveRuiPropertiesLib(Expression expr, String propertyName) {
		Name name = (Name) expr;
		String partName = name.getCanonicalName();

		try {
			ITypeBinding type = bindTypeName(name);
			if (type.getKind() == ITypeBinding.LIBRARY_BINDING) {
				LibraryBinding lib = (LibraryBinding) type;
				if (isRuiPropertiesLib(lib)) {
					return lib;
				}
			}
			problemRequestor.acceptProblem(expr, IProblemRequestor.PART_IN_PROPERTY_WRONG_LIBRARY_TYPE, IMarker.SEVERITY_ERROR,
					new String[] { partName, propertyName, "RUIPropertiesLibrary" });
			return IBinding.NOT_FOUND_BINDING;
		} catch (ResolutionException e) {
			problemRequestor.acceptProblem(expr, e.getProblemKind(), IMarker.SEVERITY_ERROR, e.getInserts());
			return IBinding.NOT_FOUND_BINDING;
		}
	}
	
	private boolean isRuiPropertiesLib(LibraryBinding lib) {
		if (!lib.isValid()) {
			lib = (LibraryBinding) lib.realize();
		}
		IPartSubTypeAnnotationTypeBinding subType = lib.getSubType();
		if (subType == null) {
			return false;
		}
		if (!annotationIs(subType, new String[] { "egl", "ui", "rui" }, "RUIPropertiesLibrary")) {
			return false;
		}

		return true;
	}

	private boolean isDliSegmentRecord(FixedRecordBinding record) {
		if (!record.isValid()) {
			record = (FixedRecordBinding) record.realize();
		}
		IPartSubTypeAnnotationTypeBinding subType = record.getSubType();
		if (subType == null) {
			return false;
		}
		if (!annotationIs(subType, new String[] { "egl", "io", "dli" }, "DLISegment")) {
			return false;
		}

		return true;
	}

	private Object resolveLengthItem(Expression expr) {
		IBinding binding = annotationLeftHandScope.getTopLevelAnnotationLeftHandScope().getBindingBeingAnnotated();
		String itemName = ((Name) expr).getCanonicalName();
		if (!binding.isTypeBinding()) {
			return itemName;
		}

		Name name = (Name) expr;
		Scope saveScope = currentScope;
		currentScope = new TypeBindingScope(NullScope.INSTANCE, (ITypeBinding) binding, null);

		Object result = itemName;
		try {
			bindExpressionName(name);
			// TODO validate the attributes of the length item
			result = name.resolveDataBinding();
		} catch (ResolutionException e) {
			// cannot signal an error here...need to try to resolve in the
			// functionContainer scope
		}
		currentScope = saveScope;
		return result;
	}

	private Object resolveKeyItem(Expression expr) {
		IBinding binding = annotationLeftHandScope.getTopLevelAnnotationLeftHandScope().getBindingBeingAnnotated();
		String keyName = ((Name) expr).getCanonicalName();
		if (!binding.isTypeBinding()) {
			return keyName;
		}
		Name name = (Name) expr;

		ITypeBinding typeBinding;
		Scope saveScope = currentScope;
		typeBinding = (ITypeBinding) binding;
		currentScope = new TypeBindingScope(NullScope.INSTANCE, typeBinding, null);
		Object result = keyName;
		try {
			bindExpressionName(name);
			// TODO validate the attributes of the key item
			result = name.resolveDataBinding();
		} catch (ResolutionException e) {
			if (binding.getAnnotation(InternUtil.intern(new String[] { "egl", "io", "file" }), InternUtil.intern("IndexedRecord")) != null) {
				problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_KEY_ITEM_MUST_BE_IN_INDEXED_RECORD, IMarker.SEVERITY_ERROR,
						new String[] { keyName, typeBinding.getName() });
				result = IBinding.NOT_FOUND_BINDING;
			} else {
				if (binding.getAnnotation(new String[] { "egl", "io", "dli" }, "DLISegment") != null) {
					problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_KEY_ITEM_MUST_BE_IN_DLISEGMENT, IMarker.SEVERITY_ERROR,
							new String[] { keyName, typeBinding.getName() });
					result = IBinding.NOT_FOUND_BINDING;
				}
			}
		}
		currentScope = saveScope;
		return result;
	}

	private Object resolveRecordNumItem(Expression expr) {
		IBinding binding = annotationLeftHandScope.getTopLevelAnnotationLeftHandScope().getBindingBeingAnnotated();
		if (!(expr instanceof StringLiteral)) {
			return null;
		}

		String keyName = ((StringLiteral) expr).getValue();
		if (!binding.isTypeBinding()) {
			return keyName;
		}
		Name name = new ExpressionParser(compilerOptions).parseAsName(keyName);
		if (name == null) {
			return IBinding.NOT_FOUND_BINDING;
		}

		ITypeBinding typeBinding;
		Scope saveScope = currentScope;
		typeBinding = (ITypeBinding) binding;
		currentScope = new TypeBindingScope(NullScope.INSTANCE, typeBinding, null);
		Object result = keyName;
		try {
			bindExpressionName(name);
			// TODO validate the attributes of the key item
			result = name.resolveDataBinding();
		} catch (ResolutionException e) {
		}
		currentScope = saveScope;
		return result;
	}

	private Object resolveCommandValueItem(Expression expr) {
		IBinding binding = annotationLeftHandScope.getTopLevelAnnotationLeftHandScope().getBindingBeingAnnotated();

		Name name = (Name) expr;
		String keyName = name.getCanonicalName();
		if (!binding.isDataBinding() && !binding.isTypeBinding()) {
			return keyName;
		}

		ITypeBinding typeBinding;
		Scope saveScope = currentScope;
		if (binding.isTypeBinding()) {
			typeBinding = (ITypeBinding) binding;
			currentScope = new TypeBindingScope(NullScope.INSTANCE, typeBinding, null);
		} else {
			IDataBinding dataBinding = (IDataBinding) binding;
			currentScope = new DataBindingScope(NullScope.INSTANCE, dataBinding, true);
			typeBinding = dataBinding.getType();
		}
		Object result = keyName;
		try {
			bindExpressionName(name);
			IDataBinding dBinding = name.resolveDataBinding();
			result = dBinding;

			if (typeBinding.getAnnotation(new String[] { "egl", "ui", "webTransaction" }, "VGUIRecord") != null) {
				if (name.getIdentifier() == typeBinding.getName()) {
					problemRequestor.acceptProblem(expr, IProblemRequestor.COMMAND_VALUE_ITEM_PROPERTY_VALUE_CANNOT_EQUAL_RECORD_NAME);
				}

				boolean typeIsValid = false;
				ITypeBinding tBinding = dBinding.getType();
				if (tBinding != null) {
					if (ITypeBinding.PRIMITIVE_TYPE_BINDING == tBinding.getKind()) {
						Primitive prim = ((PrimitiveTypeBinding) tBinding).getPrimitive();
						typeIsValid = prim == Primitive.CHAR || prim == Primitive.DBCHAR || prim == Primitive.MBCHAR
								|| prim == Primitive.UNICODE;
					}
				}
				if (!typeIsValid) {
					problemRequestor.acceptProblem(expr, IProblemRequestor.COMMAND_VALUE_ITEM_ITEM_WRONG_TYPE);
				}
			}
		} catch (ResolutionException e) {
			problemRequestor.acceptProblem(expr, IProblemRequestor.ITEM_REFERENCED_BY_COMMAND_VALUE_ITEM_MUST_BE_IN_RECORD, new String[] {
					keyName, typeBinding.getCaseSensitiveName() });
			result = IBinding.NOT_FOUND_BINDING;
		}
		currentScope = saveScope;
		return result;
	}

	private Object resolveNumElementsItem(Expression expr) {
		IBinding binding = annotationLeftHandScope.getTopLevelAnnotationLeftHandScope().getBindingBeingAnnotated();
		String numElementsName = ((Name) expr).getCanonicalName();
		if (!binding.isDataBinding() && !binding.isTypeBinding()) {
			return numElementsName;
		}

		ITypeBinding typeBinding;
		if (binding.isTypeBinding()) {
			typeBinding = (ITypeBinding) binding;
		} else {
			IDataBinding dataBinding = (IDataBinding) binding;
			typeBinding = dataBinding.getType().getBaseType();
		}
		if (typeBinding.getKind() != ITypeBinding.FIXED_RECORD_BINDING) {
			return numElementsName;
		}

		Name name = (Name) expr;

		Scope saveScope = currentScope;
		currentScope = new TypeBindingScope(NullScope.INSTANCE, typeBinding, null);

		Object result = numElementsName;
		try {
			bindExpressionName(name);

			result = name.resolveDataBinding();
		} catch (ResolutionException e) {
			problemRequestor.acceptProblem(expr, IProblemRequestor.ITEM_REFERENCED_BY_NUM_ELEMENTS_MUST_BE_IN_RECORD,
					IMarker.SEVERITY_ERROR, new String[] { numElementsName, typeBinding.getName() });
			result = IBinding.NOT_FOUND_BINDING;
		}
		currentScope = saveScope;
		return result;
	}

	private Object resolvePSB(Expression expr) {
		Name name = (Name) expr;
		String psbName = name.getCanonicalName();

		try {
			bindExpressionName(name);
			IDataBinding dataBinding = name.resolveDataBinding();
			if (dataBinding != null && IBinding.NOT_FOUND_BINDING != dataBinding) {
				ITypeBinding typebinding = dataBinding.getType();
				if (typebinding != null && IBinding.NOT_FOUND_BINDING != typebinding) {
					if (typebinding.getAnnotation(new String[] { "egl", "io", "dli" }, "PSBRecord") == null) {
						problemRequestor.acceptProblem(expr, IProblemRequestor.PSB_PROPERTY_VALUE_NOT_PSB_RECORD, IMarker.SEVERITY_ERROR,
								new String[] { psbName });
					}
				}

			}
			return dataBinding;
		} catch (ResolutionException e) {
			problemRequestor.acceptProblem(expr, IProblemRequestor.PSB_PROPERTY_VALUE_NOT_PSB_RECORD, IMarker.SEVERITY_ERROR,
					new String[] { psbName });
			return IBinding.NOT_FOUND_BINDING;
		}
	}

	private Object resolveInputRecord(final Expression expr) {
		final IDataBinding[] result = new IDataBinding[] { null };
		expr.accept(new AbstractASTExpressionVisitor() {
			public boolean visitName(Name name) {
				try {
					bindExpressionName(name);
					result[0] = name.resolveDataBinding();
					if (result[0].getKind() != IDataBinding.CLASS_FIELD_BINDING) {
						result[0] = IBinding.NOT_FOUND_BINDING;
					}
					else {
						if (result[0].getAnnotation(InternUtil.intern(new String[] { "egl", "core" }), InternUtil.intern("BasicRecord")) == null) {
							problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_MUST_RESOLVE_TO_DATA_DECLARATION_OF_RECORD,
									IMarker.SEVERITY_ERROR, new String[] { IEGLConstants.PROPERTY_INPUTRECORD });
						}
					}
				} catch (ResolutionException e) {
					result[0] = IBinding.NOT_FOUND_BINDING;
				}
				if (result[0] == IBinding.NOT_FOUND_BINDING) {
					problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_MUST_RESOLVE_TO_DATA_DECLARATION,
							IMarker.SEVERITY_ERROR, new String[] { IEGLConstants.PROPERTY_INPUTRECORD });
				}
				return false;
			}
		});
		return result[0];

	}

	private Object resolveUIProgramInputUiRecord(final Expression expr) {
		final IDataBinding[] result = new IDataBinding[] { null };
		expr.accept(new AbstractASTExpressionVisitor() {
			public boolean visitName(Name name) {
				try {
					bindExpressionName(name);
					result[0] = name.resolveDataBinding();
					if (result[0].getKind() != IDataBinding.CLASS_FIELD_BINDING) {
						result[0] = IBinding.NOT_FOUND_BINDING;
					}
					if ((Binding.isValidBinding(result[0].getType()) && result[0].getType().getKind() != ITypeBinding.FLEXIBLE_RECORD_BINDING )  
							|| result[0].getAnnotation(InternUtil.intern(new String[] { "egl", "core" }), InternUtil.intern("BasicRecord")) == null) {
						problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_MUST_RESOLVE_TO_DATA_DECLARATION_OF_FLEX_RECORD,
								IMarker.SEVERITY_ERROR, new String[] { IEGLConstants.PROPERTY_INPUTRECORD });
					}
				} catch (ResolutionException e) {
					result[0] = IBinding.NOT_FOUND_BINDING;
				}
				if (result[0] == IBinding.NOT_FOUND_BINDING) {
					problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_MUST_RESOLVE_TO_DATA_DECLARATION,
							IMarker.SEVERITY_ERROR, new String[] { IEGLConstants.PROPERTY_INPUTRECORD });
				}
				return false;
			}
		});
		return result[0];

	}

	private Object resolveInputUIRecord(Expression expr) {
		Name name = (Name) expr;

		IDataBinding result = null;
		try {
			bindExpressionName(name);
			result = name.resolveDataBinding();
			if (result.getKind() != IDataBinding.CLASS_FIELD_BINDING) {
				result = IBinding.NOT_FOUND_BINDING;
			}
			if (result.getAnnotation(new String[] { "egl", "ui", "webTransaction" }, "VGUIRecord") == null) {
				problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_MUST_RESOLVE_TO_DATA_DECLARATION_OF_UI_RECORD,
						IMarker.SEVERITY_ERROR, new String[] { IEGLConstants.PROPERTY_INPUTUIRECORD });
			}
		} catch (ResolutionException e) {
			result = IBinding.NOT_FOUND_BINDING;
		}
		if (result == IBinding.NOT_FOUND_BINDING) {
			problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_MUST_RESOLVE_TO_DATA_DECLARATION, IMarker.SEVERITY_ERROR,
					new String[] { IEGLConstants.PROPERTY_INPUTUIRECORD });
		}
		return result;

	}

	private Object resolveInputForm(Expression expr) {
		Name name = (Name) expr;
		String inputFormName = name.getCanonicalName();

		IDataBinding result = null;
		try {
			bindExpressionName(name);
			result = name.resolveDataBinding();
			if (result.getKind() != IDataBinding.FORM_BINDING) {
				result = IBinding.NOT_FOUND_BINDING;
			}
		} catch (ResolutionException e) {
			result = IBinding.NOT_FOUND_BINDING;
		}
		if (result == IBinding.NOT_FOUND_BINDING) {
			problemRequestor.acceptProblem(expr, IProblemRequestor.PROPERTY_NO_FORMGROUPS_IN_PROGRAM_WITH_INPUTFORM,
					IMarker.SEVERITY_ERROR, new String[] { inputFormName });
		}
		return result;

	}

	private Object resolveViewRootVar(Expression expr) {
		Name name = (Name) expr;
		String viewName = name.getCanonicalName();

		IDataBinding result = null;
		try {
			bindExpressionName(name);
			result = name.resolveDataBinding();
			if (result.getKind() != IDataBinding.CLASS_FIELD_BINDING) {
				result = IBinding.NOT_FOUND_BINDING;
			}
		} catch (ResolutionException e) {
			result = IBinding.NOT_FOUND_BINDING;
		}
		if (result == IBinding.NOT_FOUND_BINDING) {
			problemRequestor
					.acceptProblem(expr, IProblemRequestor.VIEWROOTVAR_NOT_FOUND, IMarker.SEVERITY_ERROR, new String[] { viewName });
		}
		return result;

	}

	private Object resolveMsgField(Expression expr) {
		IBinding binding = annotationLeftHandScope.getTopLevelAnnotationLeftHandScope().getBindingBeingAnnotated();
		if (!binding.isTypeBinding()) {
			return null;
		}

		ITypeBinding typeBinding;
		Scope saveScope = currentScope;
		typeBinding = (ITypeBinding) binding;
		currentScope = new TypeBindingScope(NullScope.INSTANCE, typeBinding, null);

		Name name = (Name) expr;

		IDataBinding result = null;
		try {
			bindExpressionName(name);
			result = name.resolveDataBinding();
			if (result.getKind() != IDataBinding.FORM_FIELD) {
				result = IBinding.NOT_FOUND_BINDING;
			}
		} catch (ResolutionException e) {
			problemRequestor.acceptProblem(expr, e.getProblemKind(), IMarker.SEVERITY_ERROR, e.getInserts());
			return IBinding.NOT_FOUND_BINDING;
		} finally {
			currentScope = saveScope;
		}

		return result;

	}

	private Object resolveDataType(Expression expr) {
		String dataTypeName = ((StringLiteral) expr).getValue();

		boolean typeIsValid = false;
		ITypeBinding result;

		Type type = new TypeParser(compilerOptions).parseAsType(dataTypeName);
		if (type == null) {
			result = IBinding.NOT_FOUND_BINDING;
		} else {
			try {
				result = bindType(type);
				if (ITypeBinding.PRIMITIVE_TYPE_BINDING == result.getKind()) {
					typeIsValid = true;
				}
				else {
					if (ITypeBinding.DATAITEM_BINDING == result.getKind()) {
						typeIsValid = true;
						result =  ((DataItemBinding)result).getPrimitiveTypeBinding();
						if (result == null) {
							typeIsValid = false;
						}
					}
				}
			} catch (ResolutionException e) {
				result = IBinding.NOT_FOUND_BINDING;
			}
		}

		if (!typeIsValid) {
			problemRequestor.acceptProblem(expr, IProblemRequestor.INVALID_DATATYPE_PROPERTY, new String[] { dataTypeName });
		}

		return result;
	}

	private Object resolvePSBParm(Expression expr) {
		Name name = (Name) expr;
		String psbParmName = name.getCanonicalName();

		IDataBinding result = null;
		try {
			bindExpressionName(name);
			result = name.resolveDataBinding();
			if (result.getKind() != IDataBinding.PROGRAM_PARAMETER_BINDING) {
				result = IBinding.NOT_FOUND_BINDING;
			}
		} catch (ResolutionException e) {
			result = IBinding.NOT_FOUND_BINDING;
		}
		if (result == IBinding.NOT_FOUND_BINDING) {
			IPartBinding functionContainerBinding = currentScope.getPartBinding();
			if (ITypeBinding.PROGRAM_BINDING == functionContainerBinding.getKind()) {
				problemRequestor.acceptProblem(expr, IProblemRequestor.DLI_PSBPARM_NOT_PROGRAM_PARAMETER, new String[] { psbParmName,
						functionContainerBinding.getCaseSensitiveName() });
			} else if (ITypeBinding.LIBRARY_BINDING == functionContainerBinding.getKind()) {
				problemRequestor.acceptProblem(expr, IProblemRequestor.UNSUPPORTED_PROPERTY, new String[] { IEGLConstants.PROPERTY_PSBPARM,
						IEGLConstants.KEYWORD_LIBRARY });
			}
		}
		return result;

	}

	private ProgramScope getProgramScope() {
		Scope cur = currentScope;
		while (cur != null) {
			if (cur.isProgramScope()) {
				return (ProgramScope) cur;
			}
			cur = cur.getParentScope();
		}
		return null;
	}

	private Object resolvePCBParms(Expression expr) {
		final List pcbParmsValue = new ArrayList();

		final IPartBinding functionContainerBinding = currentScope.getPartBinding();
		if (ITypeBinding.LIBRARY_BINDING == functionContainerBinding.getKind()) {
			problemRequestor.acceptProblem(expr, IProblemRequestor.UNSUPPORTED_PROPERTY, new String[] { IEGLConstants.PROPERTY_PCBPARMS,
					IEGLConstants.KEYWORD_LIBRARY });
			return new IDataBinding[0];
		}

		expr.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(ArrayLiteral arrayLiteral) {
				return true;
			}
			
			public boolean visit(NullLiteral nilLiteral) {
				pcbParmsValue.add(null);
				return false;
			}

			public boolean visitName(Name name) {
				String pcbParmName = name.getCanonicalName();

				IDataBinding result = null;
				try {
					bindExpressionName(name);
					result = name.resolveDataBinding();
					if (result.getKind() != IDataBinding.PROGRAM_PARAMETER_BINDING) {
						result = IBinding.NOT_FOUND_BINDING;
					}
				} catch (ResolutionException e) {
					result = IBinding.NOT_FOUND_BINDING;
				}
				pcbParmsValue.add(result);
				if (result == IBinding.NOT_FOUND_BINDING) {
					problemRequestor.acceptProblem(name, IProblemRequestor.DLI_PCB_IS_NOT_DECLARED_IN_PROGRAM_PARM_LIST, new String[] {
							pcbParmName, functionContainerBinding.getCaseSensitiveName() });
				}
				return false;
			}
		});

		return (IDataBinding[]) pcbParmsValue.toArray(new IDataBinding[pcbParmsValue.size()]);

	}

	private Object resolveKeyItems(Expression expr) {
		final List keyItemsValue = new ArrayList();

		IBinding binding = annotationLeftHandScope.getTopLevelAnnotationLeftHandScope().getBindingBeingAnnotated();
		if (!binding.isDataBinding() && !binding.isTypeBinding()) {
			return null;
		}

		ITypeBinding typeBinding;
		Scope saveScope = currentScope;
		if (binding.isTypeBinding()) {
			typeBinding = (ITypeBinding) binding;
			currentScope = new TypeBindingScope(NullScope.INSTANCE, typeBinding, null);
		} else {
			IDataBinding dataBinding = (IDataBinding) binding;
			currentScope = new DataBindingScope(NullScope.INSTANCE, dataBinding, true);
			typeBinding = dataBinding.getType();
		}
		final String typeName = typeBinding.getName();

		expr.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(ArrayLiteral arrayLiteral) {
				return true;
			}

			public boolean visit(SimpleName name) {
				String keyItemName = name.getIdentifier();

				try {
					bindExpressionName(name);
					keyItemsValue.add(name.resolveDataBinding());
				} catch (ResolutionException e) {
					problemRequestor.acceptProblem(name, IProblemRequestor.PROPERTY_KEY_ITEM_MUST_BE_IN_SQL_RECORD, IMarker.SEVERITY_ERROR,
							new String[] { keyItemName, typeName });
					keyItemsValue.add(IBinding.NOT_FOUND_BINDING);
				}
				return false;
			}
		});

		currentScope = saveScope;
		return (IDataBinding[]) keyItemsValue.toArray(new IDataBinding[keyItemsValue.size()]);

	}

	private Object resolveValidationBypassFunctions(Expression expr) {
		final List bypassFunctionsValue = new ArrayList();

		expr.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(ArrayLiteral arrayLiteral) {
				return true;
			}

			public boolean visitName(Name name) {
				String bypassName = name.getCanonicalName();

				final Name[] qualifier = new Name[1];
				final SimpleName[] simpName = new SimpleName[1];
				name.accept(new AbstractASTExpressionVisitor() {
					public boolean visit(SimpleName simpleName) {
						simpName[0] = simpleName;
						return false;
					}

					public boolean visit(QualifiedName qualifiedName) {
						simpName[0] = new SimpleName(qualifiedName.getIdentifier(), 0, 0);
						qualifier[0] = qualifiedName.getQualifier();
						return false;
					}
				});

				currentScope.stopReturningTopLevelFunctions();
				try {
					IDataBinding resultDBinding = bindExpressionName(name);
					if (resultDBinding.getType() != null && resultDBinding.getType().isFunctionBinding()) {
						bypassFunctionsValue.add((IFunctionBinding) resultDBinding.getType());
					}
				} catch (ResolutionException e) {
					problemRequestor.acceptProblem(name, e.getProblemKind(), IMarker.SEVERITY_ERROR, e.getInserts());
					bypassFunctionsValue.add(IBinding.NOT_FOUND_BINDING);
				}
				currentScope.startReturningTopLevelFunctions();
				return false;
			}
		});

		return (IFunctionBinding[]) bypassFunctionsValue.toArray(new IFunctionBinding[bypassFunctionsValue.size()]);

	}

	private void storeAnnotationInBinding(AnnotationBinding annotationBinding) {
		AnnotationLeftHandScope scopeThatFoundAnnotation = getScopeThatFoundAnnotation(annotationLeftHandScope);
		if (scopeThatFoundAnnotation == null) {
			scopeThatFoundAnnotation = annotationLeftHandScope;
		}

		IBinding bindingToHoldAnnotation = scopeThatFoundAnnotation.getBindingToHoldAnnotation();
		if(annotationBinding.isAnnotationField() && bindingToHoldAnnotation instanceof IPartBinding) {
			bindingToHoldAnnotation = bindingToHoldAnnotation.getAnnotation(((IPartBinding) bindingToHoldAnnotation).getSubType());
		}
		IBinding bindingBeingAnnotated = scopeThatFoundAnnotation.getBindingBeingAnnotated();
		
		IDataBinding[] path = scopeThatFoundAnnotation.getPath();
		if (bindingToHoldAnnotation != null) {
			if (bindingToHoldAnnotation == bindingBeingAnnotated || !bindingToHoldAnnotation.isDataBinding()
					|| !bindingBeingAnnotated.isDataBinding()) {
				bindingToHoldAnnotation.addAnnotation(annotationBinding);
			} else {
				if (path == null || path.length == 0) {
					((IDataBinding) bindingToHoldAnnotation).addAnnotation(annotationBinding,
							new IDataBinding[] { (IDataBinding) bindingBeingAnnotated });
				} else {
					((IDataBinding) bindingToHoldAnnotation).addAnnotation(annotationBinding, path);
				}
			}
		}

	}

	private AnnotationLeftHandScope getScopeThatFoundAnnotation(Scope scope) {
		if (scope == null || !scope.isAnnotationLeftHandScope()) {
			return null;
		}

		if (((AnnotationLeftHandScope) scope).isAnnotationFoundUsingThisScope()) {
			return (AnnotationLeftHandScope) scope;
		}

		return getScopeThatFoundAnnotation(scope.getParentScope());
	}

}
