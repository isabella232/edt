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
package org.eclipse.edt.compiler.internal.core.lookup.Enumerations;

import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationDataBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationTypeBinding;
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class AlignKind extends Enumeration{
    public final static AlignKind INSTANCE = new AlignKind();
    public final static int TYPE_CONSTANT = ALIGNKIND;
    public final static int CENTER_CONSTANT = 1;    
    public final static int LEFT_CONSTANT = 2;
    public final static int RIGHT_CONSTANT = 3;
    public final static int NONE_CONSTANT = 4;
    
    public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("AlignKind"), ALIGNKIND);
    public final static SystemEnumerationDataBinding CENTER = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("center"), null, TYPE, CENTER_CONSTANT);
    public final static SystemEnumerationDataBinding LEFT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("left"), null, TYPE, LEFT_CONSTANT);
    public final static SystemEnumerationDataBinding RIGHT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("right"), null, TYPE, RIGHT_CONSTANT);
    public final static SystemEnumerationDataBinding NONE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("none"), null, TYPE, NONE_CONSTANT);
    
    
    static {
    	TYPE.setValid(true);
        TYPE.addEnumeration(CENTER);
     	TYPE.addEnumeration(LEFT);
     	TYPE.addEnumeration(RIGHT);
     	TYPE.addEnumeration(NONE);     		
    };
    
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return true;
    }

}
