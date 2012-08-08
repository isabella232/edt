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
 * Service AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class Service extends Part {

	private List implementsOpt;

	public Service(Boolean privateAccessModifierOpt, SimpleName name, List implementsOpt, List classContents, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, classContents, startOffset, endOffset);
		
		this.implementsOpt = setParent(implementsOpt);
	}
	
	/**
	 * @return A List of Name objects
	 */
	public List<Name> getImplementedInterfaces() {
		return implementsOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			acceptChildren(visitor, implementsOpt);
			acceptChildren(visitor, contents);			
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new Service(new Boolean(isPrivate), (SimpleName)name.clone(), cloneList(implementsOpt), cloneContents(), getOffset(), getOffset() + getLength());
	}
	
	public String getPartTypeName() {
		return  IEGLConstants.KEYWORD_SERVICE;
	}
	
	public int getPartType() {
		return SERVICE;
	}
	
	public boolean hasSubType() {
		return false;
	}

	public Name getSubType() {
		return null;
	}
	
	/**
	 * @deprecated There is no serviceReferences syntax in language anymore. This returns empty list
	 */
	public List getServiceReferences() {
		return Collections.EMPTY_LIST;
	}
}
