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

import org.eclipse.edt.compiler.binding.IDataBinding;

/**
 * AnnotationExpression AST node type.
 * 
 * @author Albert Ho
 * @author David Murray
 */
public class AnnotationExpression extends Expression {

    private Name name;

    public AnnotationExpression(Name name, int startOffset, int endOffset) {
        super(startOffset, endOffset);

        this.name = name;
        name.setParent(this);
    }

    public Name getName() {
        return name;
    }

    public String getCanonicalString() {
  		return getName().getCanonicalString();
    }
    
    public void accept(IASTVisitor visitor) {
        boolean visitChildren = visitor.visit(this);
        if (visitChildren) {
            name.accept(visitor);
        }
        visitor.endVisit(this);
    }

    public IDataBinding resolveDataBinding() {

        IDataBinding result = super.resolveDataBinding();
        if (result == null) {
            result = getName().resolveDataBinding();
        }
        return result;
    }

    protected Object clone() throws CloneNotSupportedException {
        return new AnnotationExpression((Name) name.clone(), getOffset(), getOffset() + getLength());
    }
}
