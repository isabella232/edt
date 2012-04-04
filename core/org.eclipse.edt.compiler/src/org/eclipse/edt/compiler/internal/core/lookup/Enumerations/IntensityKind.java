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
public class IntensityKind extends Enumeration{
    public final static IntensityKind INSTANCE = new IntensityKind();
    public final static int TYPE_CONSTANT = INTENSITYKIND;
    public final static int BOLD_CONSTANT = 1;
    public final static int DIM_CONSTANT = 2;
    public final static int INVISIBLE_CONSTANT = 3;
    public final static int NORMALINTENSITY_CONSTANT = 4;
    public final static int DEFAULTINTENSITY_CONSTANT = 5;
    
    public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("IntensityKind"), INTENSITYKIND);
    public final static SystemEnumerationDataBinding BOLD = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("bold"), null, TYPE, BOLD_CONSTANT);
    public final static SystemEnumerationDataBinding DIM = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("dim"), null, TYPE, DIM_CONSTANT);
    public final static SystemEnumerationDataBinding INVISIBLE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("invisible"), null, TYPE, INVISIBLE_CONSTANT);
    public final static SystemEnumerationDataBinding NORMALINTENSITY = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("normalIntensity"), null, TYPE, NORMALINTENSITY_CONSTANT);
    public final static SystemEnumerationDataBinding DEFAULTINTENSITY = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("defaultIntensity"), null, TYPE, DEFAULTINTENSITY_CONSTANT);
    
    
    static {
    	TYPE.setValid(true);
    	TYPE.addEnumeration(BOLD);
    	TYPE.addEnumeration(DIM);
    	TYPE.addEnumeration(INVISIBLE);
    	TYPE.addEnumeration(NORMALINTENSITY);
    	TYPE.addEnumeration(DEFAULTINTENSITY);
    }
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return true;
    }
}
