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
package org.eclipse.edt.compiler.core.ast;

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * Record AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class Record extends Part{

	private Name partSubTypeOpt;

	public Record(Boolean privateAccessModifierOpt, SimpleName name, Name partSubTypeOpt, List structureContents, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, structureContents, startOffset, endOffset);
		
		if(partSubTypeOpt != null) {
			this.partSubTypeOpt = partSubTypeOpt;
			partSubTypeOpt.setParent(this);
		}
	}
	
	@Override
	public boolean hasSubType() {
		return partSubTypeOpt != null;
	}
	
	@Override
	public Name getSubType() {
		return partSubTypeOpt;
	}
	
	public List getStructureContents() {
		return contents;
	}
	
	public boolean isAnnotationType() {
		if(partSubTypeOpt != null && partSubTypeOpt.isSimpleName()) {
			String subTypeName = partSubTypeOpt.getIdentifier();
			return (subTypeName.equalsIgnoreCase("annotation"));
		}
		return false;
	}

	public boolean isStereotypeType() {
		if(partSubTypeOpt != null && partSubTypeOpt.isSimpleName()) {
			String subTypeName = partSubTypeOpt.getIdentifier();
			return (subTypeName.equalsIgnoreCase("stereotype"));
		}
		return false;
	}
	
	@Override
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			if(partSubTypeOpt != null) partSubTypeOpt.accept(visitor);
			acceptChildren(visitor, contents);
		}
		visitor.endVisit(this);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Name newPartSubTypeOpt = partSubTypeOpt != null ? (Name)partSubTypeOpt.clone() : null;
		
		return new Record(new Boolean(isPrivate), (SimpleName)name.clone(), newPartSubTypeOpt, cloneContents(), getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String getPartTypeName() {
		return IEGLConstants.KEYWORD_RECORD;
	}
	
	@Override
	public int getPartType() {
		return RECORD;
	}
}
