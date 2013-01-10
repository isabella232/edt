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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.Label;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.BooleanLiteral;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.ForStatement;
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class ForStatementTemplate extends JavaTemplate {

	public void genStatementBody(ForStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getDeclarationExpression() != null) {
			out.println("{");
			ctx.invoke(genDeclarationExpression, stmt.getDeclarationExpression(), ctx, out);
		}
		// do we have a simple or complex for statement
		boolean hasSideEffects = (stmt.getCounterVariable() != null && (org.eclipse.edt.gen.CommonUtilities.hasSideEffects(stmt.getCounterVariable(), ctx) || stmt
			.getCounterVariable() instanceof ArrayAccess))
			|| (stmt.getFromExpression() != null && org.eclipse.edt.gen.CommonUtilities.hasSideEffects(stmt.getFromExpression(), ctx))
			|| (stmt.getToExpression() != null && org.eclipse.edt.gen.CommonUtilities.hasSideEffects(stmt.getToExpression(), ctx))
			|| (stmt.getDeltaExpression() != null && org.eclipse.edt.gen.CommonUtilities.hasSideEffects(stmt.getDeltaExpression(), ctx));
		if (hasSideEffects) {
			// we need to process this as a complex for statement
			// create the initial statement block
			StatementBlock statementBlockInit = factory.createStatementBlock();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				statementBlockInit.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			// we need to create an assignment statement for the initial expression
			AssignmentStatement assignmentStatementInit = factory.createAssignmentStatement();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentStatementInit.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatementInit.setContainer(statementBlockInit.getContainer());
			Assignment assignmentInit = factory.createAssignment();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentInit.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatementInit.setAssignment(assignmentInit);
			assignmentInit.setLHS((LHSExpr) stmt.getCounterVariable());
			if (stmt.getFromExpression() != null)
				assignmentInit.setRHS(stmt.getFromExpression());
			else {
				IntegerLiteral integerLiteralInit = factory.createIntegerLiteral();
				integerLiteralInit.setType(IRUtils.getEGLPrimitiveType(MofConversion.Type_Int));
				if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					integerLiteralInit.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
				integerLiteralInit.setValue("1");
				assignmentInit.setRHS(integerLiteralInit);
			}
			statementBlockInit.getStatements().add(assignmentStatementInit);
			ctx.invoke(genStatementNoBraces, statementBlockInit, ctx, out);
			out.println("while (true) {");
			// create the conditional statement block
			StatementBlock statementBlockCond = factory.createStatementBlock();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				statementBlockCond.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			// create a boolean flag
			String temporaryCond = ctx.nextTempName();
			LocalVariableDeclarationStatement localDeclarationCond = factory.createLocalVariableDeclarationStatement();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				localDeclarationCond.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			localDeclarationCond.setContainer(stmt.getContainer());
			DeclarationExpression declarationExpressionCond = factory.createDeclarationExpression();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				declarationExpressionCond.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			Field fieldCond = factory.createField();
			fieldCond.setName(temporaryCond);
			fieldCond.setType(TypeUtils.Type_BOOLEAN);
			// we need to create the member access
			MemberName nameExpressionCond = factory.createMemberName();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				nameExpressionCond.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			nameExpressionCond.setMember(fieldCond);
			nameExpressionCond.setId(fieldCond.getName());
			// add the field to the declaration expression
			declarationExpressionCond.getFields().add(fieldCond);
			// connect the declaration expression to the local declaration
			localDeclarationCond.setExpression(declarationExpressionCond);
			statementBlockCond.getStatements().add(localDeclarationCond);
			// we need to create an if statement for the conditional expression
			BinaryExpression binaryExpressionCond = factory.createBinaryExpression();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				binaryExpressionCond.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			binaryExpressionCond.setLHS(stmt.getCounterVariable());
			binaryExpressionCond.setRHS(stmt.getToExpression());
			if (stmt.isIncrement())
				binaryExpressionCond.setOperator("<=");
			else
				binaryExpressionCond.setOperator(">=");
			IfStatement ifStatementCond = factory.createIfStatement();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				ifStatementCond.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			ifStatementCond.setContainer(stmt.getContainer());
			ifStatementCond.setCondition(binaryExpressionCond);
			// create the true and false statement blocks
			StatementBlock trueStatementBlockCond = factory.createStatementBlock();
			trueStatementBlockCond.setContainer(ifStatementCond.getContainer());
			ifStatementCond.setTrueBranch(trueStatementBlockCond);
			statementBlockCond.getStatements().add(ifStatementCond);
			// create the boolean literal for true
			BooleanLiteral booleanLiteralCond = factory.createBooleanLiteral();
			booleanLiteralCond.setBooleanValue(true);
			// we need to create a boolean assignment statement
			AssignmentStatement assignmentStatementCond = factory.createAssignmentStatement();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentStatementCond.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatementCond.setContainer(trueStatementBlockCond.getContainer());
			Assignment assignmentCond = factory.createAssignment();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentCond.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatementCond.setAssignment(assignmentCond);
			assignmentCond.setLHS(nameExpressionCond);
			assignmentCond.setRHS(booleanLiteralCond);
			trueStatementBlockCond.getStatements().add(assignmentStatementCond);
			ctx.invoke(genStatementNoBraces, statementBlockCond, ctx, out);
			// now add the logic for the block execution
			out.println("if (!" + temporaryCond + ") break;");
			Label label = new Label(ctx, Label.LABEL_TYPE_FOR);
			label.setFlag(temporaryCond);
			ctx.pushLabelStack(label);
			if (ctx.getAttribute(stmt, org.eclipse.edt.gen.Constants.SubKey_statementNeedsLabel) != null
				&& ((Boolean) ctx.getAttribute(stmt, org.eclipse.edt.gen.Constants.SubKey_statementNeedsLabel)).booleanValue())
				out.print(label.getName() + ": ");
			out.print("do ");
			ctx.invoke(genStatement, stmt.getBody(), ctx, out);
			out.println("while (false);");
			// it is possible that an exit statement was used and it set this flag
			out.println("if (!" + temporaryCond + ") break;");
			// create the increment statement block
			StatementBlock statementBlockIncr = factory.createStatementBlock();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				statementBlockIncr.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			// we need to create a increment assignment statement
			AssignmentStatement assignmentStatementIncr = factory.createAssignmentStatement();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentStatementIncr.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatementIncr.setContainer(stmt.getContainer());
			Assignment assignmentIncr = factory.createAssignment();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentIncr.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatementIncr.setAssignment(assignmentIncr);
			if (stmt.getDeltaExpression() != null)
				assignmentIncr.setRHS(stmt.getDeltaExpression());
			else {
				IntegerLiteral integerLiteralIncr = factory.createIntegerLiteral();
				integerLiteralIncr.setType(IRUtils.getEGLPrimitiveType(MofConversion.Type_Int));
				if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					integerLiteralIncr.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
				integerLiteralIncr.setValue("1");
				assignmentIncr.setRHS(integerLiteralIncr);
			}
			if (stmt.isIncrement())
				assignmentIncr.setOperator("+=");
			else
				assignmentIncr.setOperator("-=");
			assignmentIncr.setLHS((LHSExpr) stmt.getCounterVariable());
			statementBlockIncr.getStatements().add(assignmentStatementIncr);
			ctx.invoke(genStatementNoBraces, statementBlockIncr, ctx, out);
			out.println("}");
		} else {
			// we need to process this as a simple for statement
			Label label = new Label(ctx, Label.LABEL_TYPE_FOR);
			ctx.pushLabelStack(label);
			if (ctx.getAttribute(stmt, org.eclipse.edt.gen.Constants.SubKey_statementNeedsLabel) != null
				&& ((Boolean) ctx.getAttribute(stmt, org.eclipse.edt.gen.Constants.SubKey_statementNeedsLabel)).booleanValue())
				out.print(label.getName() + ": ");
			out.print("for (");
			// we need to create an assignment statement for the initial expression
			Assignment assignmentInit = factory.createAssignment();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentInit.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentInit.setLHS((LHSExpr) stmt.getCounterVariable());
			if (stmt.getFromExpression() != null)
				assignmentInit.setRHS(stmt.getFromExpression());
			else {
				IntegerLiteral integerLiteralInit = factory.createIntegerLiteral();
				integerLiteralInit.setType(IRUtils.getEGLPrimitiveType(MofConversion.Type_Int));
				if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					integerLiteralInit.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
				integerLiteralInit.setValue("1");
				assignmentInit.setRHS(integerLiteralInit);
			}
			ctx.invoke(genExpression, assignmentInit, ctx, out);
			out.print("; ");
			// now generate the condition
			BinaryExpression binaryExpressionCond = factory.createBinaryExpression();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				binaryExpressionCond.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			binaryExpressionCond.setLHS(stmt.getCounterVariable());
			binaryExpressionCond.setRHS(stmt.getToExpression());
			if (stmt.isIncrement())
				binaryExpressionCond.setOperator("<=");
			else
				binaryExpressionCond.setOperator(">=");
			ctx.invoke(genExpression, binaryExpressionCond, ctx, out);
			out.print("; ");
			// now generate the increment
			BinaryExpression binaryExpressionIncr = factory.createBinaryExpression();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				binaryExpressionIncr.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			binaryExpressionIncr.setLHS(stmt.getCounterVariable());
			if (stmt.getDeltaExpression() != null)
				binaryExpressionIncr.setRHS(stmt.getDeltaExpression());
			else {
				IntegerLiteral integerLiteralIncr = factory.createIntegerLiteral();
				integerLiteralIncr.setType(IRUtils.getEGLPrimitiveType(MofConversion.Type_Int));
				if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					integerLiteralIncr.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
				integerLiteralIncr.setValue("1");
				binaryExpressionIncr.setRHS(integerLiteralIncr);
			}
			if (stmt.isIncrement())
				binaryExpressionIncr.setOperator("+");
			else
				binaryExpressionIncr.setOperator("-");
			Assignment assignmentIncr = factory.createAssignment();
			if (stmt.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentIncr.addAnnotation(stmt.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentIncr.setRHS(binaryExpressionIncr);
			assignmentIncr.setLHS((LHSExpr) stmt.getCounterVariable());
			ctx.invoke(genExpression, assignmentIncr, ctx, out);
			// finish the for statement
			out.print(") ");
			// now process the statement block
			ctx.invoke(genStatement, stmt.getBody(), ctx, out);
		}
		// if we had a declaration, clean up
		if (stmt.getDeclarationExpression() != null)
			out.println("}");
		// now remove the label from the stack
		ctx.popLabelStack();
	}

	public void genStatementEnd(ForStatement stmt, Context ctx, TabbedWriter out) {
		// we don't want a semi-colon
	}
}
