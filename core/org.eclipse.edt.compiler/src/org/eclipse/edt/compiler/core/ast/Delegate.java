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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * Delegate AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class Delegate extends Part {
	
	private List parameters;
	private ReturnsDeclaration returnsOpt;
	List settingsBlocks;

	public Delegate(Boolean privateAccessModifierOpt, SimpleName name, List functionParameters, ReturnsDeclaration returnsOpt,  List settingsBlocks, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, new ArrayList(), startOffset, endOffset);
		
		this.parameters = setParent(functionParameters);
		if(returnsOpt != null) {
			this.returnsOpt = returnsOpt;
			returnsOpt.setParent(this);
		}
		this.settingsBlocks = setParent( settingsBlocks );
	}
	
	public List getParameters() {
		return parameters;
	}
	
	public boolean hasReturnType() {
		return returnsOpt != null;
	}
	
	public Type getReturnType() {
		return returnsOpt.getType();
	}
	
	public ReturnsDeclaration getReturnDeclaration(){
		return returnsOpt;
	}
	
	public boolean returnTypeIsSqlNullable() {
		return returnsOpt.isSqlNullable();
	}
	
	public List getSettingsBlocks() {
		return settingsBlocks;
	}
		
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept( visitor );
			acceptChildren(visitor, parameters);
			if(returnsOpt != null) {
				returnsOpt.accept(visitor);
			}
			acceptChildren( visitor, settingsBlocks );
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new Delegate(new Boolean(isPrivate), (SimpleName)name.clone(), parameters, returnsOpt, cloneList(settingsBlocks), getOffset(), getOffset() + getLength());
	}

	public String getPartTypeName() {
		return IEGLConstants.KEYWORD_DELEGATE;
	}

	public int getPartType() {
		return DELEGATE;
	}
	
	public boolean hasSubType() {
		return false;
	}

	public Name getSubType() {
		return null;
	}
}
