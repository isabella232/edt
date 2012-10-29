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
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.List;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;


/**
 * @author Harmon
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AnnotationLeftHandScope extends Scope {

    Scope scopeToUseWhenResolving;

    public Element getElementBeingAnnotated() {
        return elementBeingAnnotated;
    }

    public Element getElementToHoldAnnotation() {
        return elementToHoldAnnotation;
    }

    Element elementBeingAnnotated;

    Element elementToHoldAnnotation;

    Type typeOfElementBeingAnnotated;
	
    /**
     * @param parentScope
     */
    public AnnotationLeftHandScope(Scope parentScope, Element elementBeingAnnotated, Type typeOfElementBeingAnnotated,
            Element elementToHoldAnnotation) {
        super(parentScope);
        this.scopeToUseWhenResolving = parentScope;
        this.elementBeingAnnotated = elementBeingAnnotated;
        this.elementToHoldAnnotation = elementToHoldAnnotation;
        this.typeOfElementBeingAnnotated = typeOfElementBeingAnnotated;
    }

    public List<Member> findMember(String simpleName) {
    	return null;
    }

    public List<Type> findType(String simpleName) {
    	/*
    	 * There should not be a reason to locate a part when binding the left hand side
    	 * of an annotation assignment. Returning NOT_FOUND_BINDING, so in strange cases
    	 * like:
    	 * Library lib ... end
    	 * Record rec {lib = 10} ... end
    	 * we issue error "lib cannot be resolved" instead of "types lib and int are not
    	 * assignment compatible"
    	 */
    	return null;
    }

    public boolean isAnnotationLeftHandScope() {
        return true;
    }
    

    public Scope getScopeToUseWhenResolving() {
        return scopeToUseWhenResolving;
    }

    public void setScopeToUseWhenResolving(Scope scopeToUseWhenResolving) {
        this.scopeToUseWhenResolving = scopeToUseWhenResolving;
    }

	@Override
	public IPackageBinding findPackage(String simpleName) {
		return getScopeToUseWhenResolving().findPackage(simpleName);
	}

    public AnnotationLeftHandScope getTopLevelAnnotationLeftHandScope() {

        if (getParentScope().isAnnotationLeftHandScope()) {
            return ((AnnotationLeftHandScope) getParentScope()).getTopLevelAnnotationLeftHandScope();
        }
        return this;

    }
	
}
