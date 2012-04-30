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
package org.eclipse.edt.compiler.core.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * Program AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class Program extends Part{

	private Name partSubTypeOpt;
	private List<ProgramParameter> programParametersOpt;
	private boolean isCallable;
	

	public Program(Boolean privateAccessModifierOpt, SimpleName name, Name partSubTypeOpt, List<ProgramParameter> programParametersOpt, List classContents, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, classContents, startOffset, endOffset);
		
		if(partSubTypeOpt != null) {
			this.partSubTypeOpt = partSubTypeOpt;
			partSubTypeOpt.setParent(this);
		}
		if(programParametersOpt == null) {
			this.programParametersOpt = Collections.EMPTY_LIST;
			isCallable = false;
		}
		else {
			this.programParametersOpt = setParent(programParametersOpt);
			isCallable = true;
		}
	}
	
	public boolean hasSubType() {
		return partSubTypeOpt != null;
	}
	
	public Name getSubType() {
		return partSubTypeOpt;
	}
	
	public boolean isCallable() {
	    return isCallable;
	}
	
	public List<ProgramParameter> getParameters() {
		return programParametersOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			if(partSubTypeOpt != null) partSubTypeOpt.accept(visitor);
			if(programParametersOpt != null) acceptChildren(visitor, programParametersOpt);
			acceptChildren(visitor, contents);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Name newPartSubTypeOpt = partSubTypeOpt != null ? (Name)partSubTypeOpt.clone() : null;
		
		ArrayList parmsOpt = null;
		if (isCallable()) {
			parmsOpt = cloneList(programParametersOpt);
		}
		return new Program(new Boolean(isPrivate), (SimpleName)name.clone(), newPartSubTypeOpt, parmsOpt, cloneContents(), getOffset(), getOffset() + getLength());
	}
	
	public boolean isGeneratable(){
		return true;
	}

	public String getPartTypeName() {
		return  IEGLConstants.KEYWORD_PROGRAM;
	}
	
	public int getPartType() {
		return PROGRAM;
	}
	
	/**
	 * @deprecated There is no serviceReferences syntax in language anymore. This returns empty list
	 */
	public List getServiceReferences() {
		return Collections.EMPTY_LIST;
	}

}
