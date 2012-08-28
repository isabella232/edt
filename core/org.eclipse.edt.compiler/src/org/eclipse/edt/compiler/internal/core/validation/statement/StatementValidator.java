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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CaseStatement;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.ContinueStatement;
import org.eclipse.edt.compiler.core.ast.ConverseStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.DisplayStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.ExitStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.ForwardStatement;
import org.eclipse.edt.compiler.core.ast.FreeSQLStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.GotoStatement;
import org.eclipse.edt.compiler.core.ast.IfStatement;
import org.eclipse.edt.compiler.core.ast.MoveStatement;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.OpenUIStatement;
import org.eclipse.edt.compiler.core.ast.PrepareStatement;
import org.eclipse.edt.compiler.core.ast.PrintStatement;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.ReturnStatement;
import org.eclipse.edt.compiler.core.ast.SetStatement;
import org.eclipse.edt.compiler.core.ast.ShowStatement;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.core.ast.ThrowStatement;
import org.eclipse.edt.compiler.core.ast.TransferStatement;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class StatementValidator {

	public static String getTypeString(org.eclipse.edt.mof.egl.Type binding) {
		StringBuilder result = new StringBuilder();
		if (binding.equals(TypeUtils.Type_ARRAYDICTIONARY)) {
			result.append(IEGLConstants.MIXED_ARRAYDICTIONARY_STRING);
		}
		else if (binding.equals(TypeUtils.Type_DICTIONARY)) {
			result.append(IEGLConstants.MIXED_DICTIONARY_STRING);
		}
		else if (binding instanceof org.eclipse.edt.mof.egl.ArrayType) {
			result.append(getShortTypeString(((org.eclipse.edt.mof.egl.ArrayType)binding).getElementType(), true));
			if (((org.eclipse.edt.mof.egl.ArrayType)binding).elementsNullable()) {
				result.append('?');
			}
			result.append("[]");
		}				
		else if (binding instanceof AnnotationType) {
			result.append('@');
			result.append(getUnqualifiedSignature(binding.getTypeSignature()));
		}				
		else {
			result.append(BindingUtil.getUnaliasedTypeName(binding));
		}
		
		return result.toString();
	}
	
	public static String getShortTypeString(org.eclipse.edt.mof.egl.Type binding) {
		return getShortTypeString(binding, false);
	}
	
	public static String getShortTypeString(org.eclipse.edt.mof.egl.Type binding, boolean includeLengthForPrimitives) {
		StringBuilder result;
		if (TypeUtils.isPrimitive(binding)) {
			if (includeLengthForPrimitives || binding.equals(TypeUtils.Type_MONTHSPANINTERVAL) || binding.equals(TypeUtils.Type_SECONDSPANINTERAL)) {
				result = new StringBuilder(getTypeString(binding));
			}
			else {
				result = new StringBuilder(BindingUtil.getUnaliasedTypeName(binding));
			}
		}
		else {
			result = new StringBuilder(getTypeString(binding));
		}
		
		return result.toString();
	}
	
	private static String getUnqualifiedSignature(String sig) {
		int lastDot = sig.lastIndexOf('.');
		if (lastDot == -1) {
			return sig;
		}
		return sig.substring(lastDot + 1);
	}
	
	public static void validatePrimitiveConstant(Type type,final IProblemRequestor problemRequestor){
		if (type.isArrayType()){
			type = ((ArrayType)type).getBaseType();
		}
		
		if (type.isNullableType()) {
			validatePrimitiveConstant(((NullableType)type).getBaseType(), problemRequestor);
		}
		
		if (type.isPrimitiveType()) {
			return;
		}
		
		if (type.isNameType()){
			Name name = ((NameType)type).getName();
			IBinding binding = name.resolveBinding();
			ITypeBinding typeBinding = null;
			if (isValidBinding(binding)){
				if (binding.isDataBinding()){
					typeBinding = ((IDataBinding)binding).getType();
				}else if (binding.isTypeBinding()){
					typeBinding = (ITypeBinding)binding;
					if (Binding.isValidBinding(typeBinding) && typeBinding.getKind() == ITypeBinding.DATAITEM_BINDING) {
						typeBinding = ((DataItemBinding)typeBinding).getPrimitiveTypeBinding();
					}
				}
			}
			if (StatementValidator.isValidBinding(typeBinding) && typeBinding.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING){
				problemRequestor.acceptProblem(type,
					IProblemRequestor.CONSTANT_DECL_MUST_BE_PRIMITIVE);
			}
		}
	}
	
	public static String getName(Statement statement) {
		final String[] result = new String[] {null};
		statement.accept(new DefaultASTVisitor() {
			public void endVisit(AddStatement addStatement) {
				result[0] = IEGLConstants.KEYWORD_ADD;
			}

			public void endVisit(CallStatement callStatement) {
				result[0] = IEGLConstants.KEYWORD_CALL;
			}

			public void endVisit(CaseStatement caseStatement) {
				result[0] = IEGLConstants.KEYWORD_CASE;
			}

			public void endVisit(CloseStatement closeStatement) {
				result[0] = IEGLConstants.KEYWORD_CLOSE;
			}

			public void endVisit(ContinueStatement continueStatement) {
				result[0] = IEGLConstants.KEYWORD_CONTINUE;
			}

			public void endVisit(ConverseStatement converseStatement) {
				result[0] = IEGLConstants.KEYWORD_CONVERSE;
			}
			
			public void endVisit(DeleteStatement deleteStatement) {
				result[0] = IEGLConstants.KEYWORD_DELETE;
			}

			public void endVisit(DisplayStatement displayStatement) {
				result[0] = IEGLConstants.KEYWORD_DISPLAY;
			}

			public void endVisit(ExecuteStatement executeStatement) {
				result[0] = IEGLConstants.KEYWORD_EXECUTE;
			}

			public void endVisit(ExitStatement exitStatement) {
				result[0] = IEGLConstants.KEYWORD_EXIT;
			}

			public void endVisit(ForEachStatement forEachStatement) {
				result[0] = IEGLConstants.KEYWORD_FOREACH;
			}

			public void endVisit(ForStatement forStatement) {
				result[0] = IEGLConstants.KEYWORD_FOR;
			}

			public void endVisit(ForwardStatement forwardStatement) {
				result[0] = IEGLConstants.KEYWORD_FORWARD;
			}

			public void endVisit(FreeSQLStatement freeSQLStatement) {
				result[0] = IEGLConstants.KEYWORD_FREESQL;
			}

			public void endVisit(GetByKeyStatement getByKeyStatement) {
				result[0] = IEGLConstants.KEYWORD_GET;
			}

			public void endVisit(GetByPositionStatement getByPositionStatement) {
				result[0] = IEGLConstants.KEYWORD_GET;
			}

			public void endVisit(GotoStatement gotoStatement) {
				result[0] = IEGLConstants.KEYWORD_GOTO;
			}
			
			public void endVisit(IfStatement ifStatement) {
				result[0] = IEGLConstants.KEYWORD_IF;
			}

			public void endVisit(MoveStatement moveStatement) {
				result[0] = IEGLConstants.KEYWORD_MOVE;
			}

			public void endVisit(OpenStatement openStatement) {
				result[0] = IEGLConstants.KEYWORD_OPEN;
			}

			public void endVisit(OpenUIStatement openUIStatement) {
				result[0] = IEGLConstants.KEYWORD_OPENUI;
			}

			public void endVisit(PrintStatement printStatement) {
				result[0] = IEGLConstants.KEYWORD_PRINT;
			}

			public void endVisit(ReplaceStatement replaceStatement) {
				result[0] = IEGLConstants.KEYWORD_REPLACE;
			}

			public void endVisit(ShowStatement showStatement) {
				result[0] = IEGLConstants.KEYWORD_SHOW;
			}
			
			public void endVisit(ThrowStatement throwStatement) {
				result[0] = IEGLConstants.KEYWORD_THROW;
			}

			public void endVisit(TransferStatement transferStatement) {
				result[0] = IEGLConstants.KEYWORD_TRANSFER;
			}

			public void endVisit(TryStatement tryStatement) {
				result[0] = IEGLConstants.KEYWORD_TRY;
			}

			public void endVisit(WhileStatement whileStatement) {
				result[0] = IEGLConstants.KEYWORD_WHILE;
			}
			
		    public void endVisit(ReturnStatement returnStatement) {
		        result[0] = IEGLConstants.KEYWORD_RETURN;
		    }
		    
		    public void endVisit(SetStatement setStatement) {
		        result[0] = IEGLConstants.KEYWORD_SET;
		    }
		    
		    public void endVisit(PrepareStatement prepareStatement) {
		        result[0] = IEGLConstants.KEYWORD_PREPARE;
		    }
		});
		
		if(result[0] == null) {
			throw new RuntimeException("Must define visit() for class " + statement.getClass() + " in StatementValidator::getName()");
		}
		
		return result[0];
	}
}
