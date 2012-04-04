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

import org.eclipse.edt.compiler.core.Boolean;

/**
 * @author Harmon
 */
public class UsedTypeBinding extends Binding {
    ITypeBinding type;
    java.lang.Boolean helpGroup;

    public UsedTypeBinding(ITypeBinding type) {
        super((String)null);
        this.type = type;
    }
    
    public boolean isUsedTypeBinding() {
        return true;
    }
    
    public ITypeBinding getType() {
        return type;
    }
    
    public boolean isHelpGroup() {
        if (helpGroup == null) {
	        boolean help = false;
	        IAnnotationBinding annotation = getAnnotation(new String[] {"egl", "ui", "text"}, "HelpGroup");
	        if (annotation != null) {
	            help = annotation.getValue() == Boolean.YES;
	        }
	        helpGroup = new java.lang.Boolean(help);
        }
        return helpGroup.booleanValue();
    }
}
