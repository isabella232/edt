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


public class ParameterModifierKind extends Enumeration{
	public final static ParameterModifierKind INSTANCE = new ParameterModifierKind();
	public final static int TYPE_CONSTANT = PARAMETERMODFIERKIND;

	public final static int INMODIFIER_CONSTANT = 1;
	public final static int OUTMODIFIER_CONSTANT = 2;
	public final static int INOUTMODIFIER_CONSTANT = 3;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("ParameterModifierKind"), TYPE_CONSTANT);

	public final static SystemEnumerationDataBinding INMODIFIER = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("InModifier"), null, TYPE, INMODIFIER_CONSTANT);
	public final static SystemEnumerationDataBinding OUTMODIFIER = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("OutModifier"), null, TYPE, OUTMODIFIER_CONSTANT);
	public final static SystemEnumerationDataBinding INOUTMODIFIER = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("InoutModifier"), null, TYPE, INOUTMODIFIER_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(INMODIFIER);
		TYPE.addEnumeration(OUTMODIFIER);
		TYPE.addEnumeration(INOUTMODIFIER);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return true;
    }
}
