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

import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * QualifiedName AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class QualifiedName extends Name {

	private Name qualifier;

	public QualifiedName(Name qualifier, String ID, int startOffset, int endOffset) {
		super(ID, startOffset, endOffset);
		
		this.qualifier = qualifier;
		qualifier.setParent(this);
	}
	
	public boolean isQualifiedName() {
		return true;
	}
	
	public Name getQualifier() {
		return qualifier;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			qualifier.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
    public String getCanonicalName() {
        return getCanonicalNameBuffer().toString();
    }
    
    protected StringBuffer getCanonicalNameBuffer() {
        StringBuffer buffer = qualifier.getCanonicalNameBuffer();
        buffer.append('.');
        buffer.append(identifier);
        return buffer;
    }

    public String[] getNameComponents() {
        List nameComponentsList = getNameComponentsList();
        return InternUtil.intern((String[]) nameComponentsList.toArray(new String[nameComponentsList.size()]));
    }

    protected List getNameComponentsList() {
        List nameComponentsList = qualifier.getNameComponentsList();
        nameComponentsList.add(identifier);
        return nameComponentsList;
    }
    
    protected Object clone() throws CloneNotSupportedException {
		return new QualifiedName((Name)qualifier.clone(), identifier, getOffset(), getOffset() + getLength());
	}
    
    public void copyBindingsTo(Name anotherName) {
    	anotherName.setBinding(binding);
    	anotherName.setTypeBinding(resolveTypeBinding());
    	qualifier.copyBindingsTo(((QualifiedName) anotherName).getQualifier());
    }
}
