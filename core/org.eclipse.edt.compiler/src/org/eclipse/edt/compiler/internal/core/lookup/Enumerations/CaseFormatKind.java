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
public class CaseFormatKind extends Enumeration{
    public final static CaseFormatKind INSTANCE = new CaseFormatKind();
    public final static int TYPE_CONSTANT = CASEFORMATKIND;
    public final static int DEFAULTCASE_CONSTANT = 1;
    public final static int LOWER_CONSTANT = 2;
    public final static int UPPER_CONSTANT = 3;
    
    public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("CaseFormatKind"), CASEFORMATKIND);
    public final static SystemEnumerationDataBinding DEFAULTCASE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("defaultCase"), null, TYPE, DEFAULTCASE_CONSTANT);
    public final static SystemEnumerationDataBinding LOWER = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("lower"), null, TYPE, LOWER_CONSTANT);
    public final static SystemEnumerationDataBinding UPPER = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("upper"), null, TYPE, UPPER_CONSTANT);
    
    static {
    	TYPE.setValid(true);
    	TYPE.addEnumeration(DEFAULTCASE);
    	TYPE.addEnumeration(LOWER);
    	TYPE.addEnumeration(UPPER);
    };
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return true;
    }
}
