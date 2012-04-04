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
package org.eclipse.edt.compiler.binding;

/**
 * @author Harmon
 */
public class AnnotationBindingForElement extends AnnotationBinding {
    private int index;

    /**
     * @param simpleName
     * @param declarer
     * @param typeBinding
     */
    public AnnotationBindingForElement(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding, int index) {
        super(caseSensitiveInternedName, declarer, typeBinding);
        this.index = index;
    }

    public static void main(String[] args) {
    }
    public int getIndex() {
        return index;
    }
    public boolean isForElement() {
        return true;
    }
}
