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

import org.eclipse.edt.mof.egl.Annotation;

/**
 * AnnotationExpression AST node type.
 * 
 * @author Albert Ho
 * @author David Murray
 */
public class AnnotationExpression extends Expression {

    private Name name;
    private Annotation annotation;

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
    
    public Annotation resolveAnnotation() {
    	return annotation;
    }
    
    public Object resolveElement() {
		return annotation;
	}
    
    public void setAnnotation(Annotation annotation) {
    	this.annotation = annotation;
    }

    protected Object clone() throws CloneNotSupportedException {
        return new AnnotationExpression((Name) name.clone(), getOffset(), getOffset() + getLength());
    }
    
    @Override
    public String toString() {
    	return "@" + getCanonicalString();
    }
}
