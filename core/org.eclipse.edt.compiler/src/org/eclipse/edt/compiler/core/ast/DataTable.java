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
package org.eclipse.edt.compiler.core.ast;

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * DataTable AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class DataTable extends Part {

	private Name partSubTypeOpt;
	private List structureContents;	// List of Nodes

	public DataTable(Boolean privateAccessModifierOpt, SimpleName name, Name partSubTypeOpt, List structureContents, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, structureContents, startOffset, endOffset);
		
		if(partSubTypeOpt != null) {
			this.partSubTypeOpt = partSubTypeOpt;
			partSubTypeOpt.setParent(this);
		}
		this.structureContents = setParent(structureContents);
	}
	
	public boolean hasSubType() {
		return partSubTypeOpt != null;
	}
	
	public Name getSubType() {
		return partSubTypeOpt;
	}
	
	public List getStructureContents() {
		return structureContents;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			if(partSubTypeOpt != null) partSubTypeOpt.accept(visitor);
			acceptChildren(visitor, structureContents);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Name newPartSubTypeOpt = partSubTypeOpt != null ? (Name)partSubTypeOpt.clone() : null;
		
		return new DataTable(new Boolean(isPrivate), (SimpleName)name.clone(), newPartSubTypeOpt, cloneContents(), getOffset(), getOffset() + getLength());
	}
	
	public boolean isGeneratable(){
		return true;
	}

	public String getPartTypeName() {
		return  IEGLConstants.KEYWORD_DATATABLE;
	}
	
	public int getPartType() {
		return DATATABLE;
	}
}
