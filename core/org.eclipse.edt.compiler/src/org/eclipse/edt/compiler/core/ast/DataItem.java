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

import java.util.Collections;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * DataItem AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class DataItem extends Part {
	
	Type type;
	List settingsBlocks;

	public DataItem(Boolean privateAccessModifierOpt, SimpleName name, Type type, List settingsBlocks, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, Collections.EMPTY_LIST, startOffset, endOffset);
		
		this.type = type;
		type.setParent(this);
		this.settingsBlocks = setParent( settingsBlocks );
	}
	
	public Type getType() {
		return type;
	}
	
	public List getSettingsBlocks() {
		return settingsBlocks;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept( visitor );
			type.accept( visitor );
			acceptChildren( visitor, settingsBlocks );
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new DataItem(new Boolean(isPrivate), (SimpleName)name.clone(), (Type)type.clone(), cloneList(settingsBlocks), getOffset(), getOffset() + getLength());
	}

	public String getPartTypeName() {
		return IEGLConstants.KEYWORD_DATAITEM;
	}

	public int getPartType() {
		return DATAITEM;
	}

	public boolean hasSubType() {
		return false;
	}

	public Name getSubType() {
		return null;
	}	
}
