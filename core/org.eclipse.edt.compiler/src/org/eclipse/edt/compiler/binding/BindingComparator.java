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

import java.util.Comparator;


/**
 * @author winghong
 */
public class BindingComparator implements Comparator {
    
    private static BindingComparator INSTANCE = new BindingComparator();
    
    private BindingComparator() {
        super();
    }
    
    public static BindingComparator getInstance() {
        return INSTANCE;
    }

    public int compare(Object o1, Object o2) {
        Binding binding1= (Binding) o1;
        Binding binding2 = (Binding) o2;
        return binding1.getName().compareTo(binding2.getName());
    }

}
