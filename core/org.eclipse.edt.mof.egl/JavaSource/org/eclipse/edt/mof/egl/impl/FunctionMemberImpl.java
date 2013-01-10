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
package org.eclipse.edt.mof.egl.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.Type;

public class FunctionMemberImpl extends MemberImpl implements FunctionMember {
	private static int Slot_parameters=0;
	private static int Slot_localDeclarations=1;
	private static int Slot_statementBlock=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + MemberImpl.totalSlots();
	}
	
	static {
		int offset = MemberImpl.totalSlots();
		Slot_parameters += offset;
		Slot_localDeclarations += offset;
		Slot_statementBlock += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<FunctionParameter> getParameters() {
		return (List<FunctionParameter>)slotGet(Slot_parameters);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Field> getLocalDeclarations() {
		return (List<Field>)slotGet(Slot_localDeclarations);
	}

	@Override
	public StatementBlock getStatementBlock() {
		return (StatementBlock)slotGet(Slot_statementBlock);
	}
	
	@Override
	public void setStatementBlock(StatementBlock value) {
		slotSet(Slot_statementBlock, value);
	}
	
	
	@Override
	public String getSignature() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getCaseSensitiveName());
		buffer.append('(');
		for (int i=0; i<getParameters().size(); i++) {
			buffer.append(getParameters().get(i).getType().getTypeSignature());
			if (i < getParameters().size()-1) {
				buffer.append(", ");
			}
		}
		buffer.append(')');
		return buffer.toString();
	}
	
	@Override
	public List<Statement> getStatements() {
		return getStatementBlock().getStatements();
	}
	
	@Override
	public void addStatement(Statement stmt) {
		getStatements().add(stmt);
	}
	
	@Override
	public void addStatements(List<Statement> stmts) {
		getStatements().addAll(stmts);
	}
	
	@Override
	public void addStatements(StatementBlock block) {
		getStatements().addAll(block.getStatements());
	}
	
	@Override
	public void addParameter(FunctionParameter parm) {
		addMember(parm);
	}

	@Override
	public void addMember(Member mbr) {
		if (mbr instanceof FunctionParameter) {
			getParameters().add((FunctionParameter)mbr);
		}	
		if (mbr instanceof Field) {
			getLocalDeclarations().add((Field)mbr);
		}
		mbr.setContainer(this);
		mbr.setAccessKind(AccessKind.ACC_PRIVATE);
	}

	@Override
	public List<Member> getMembers() {
		List<Member> list = new ArrayList<Member>();
		list.addAll(getParameters());
		return list;
	}

	@Override
	public List<Member> getAllMembers() {
		return getMembers();
	}

	@Override
	public Type getReturnType() {
		return getType();
	}
}
