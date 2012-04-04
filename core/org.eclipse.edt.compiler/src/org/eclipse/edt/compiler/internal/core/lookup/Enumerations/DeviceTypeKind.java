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


public class DeviceTypeKind extends Enumeration{
    public final static DeviceTypeKind INSTANCE = new DeviceTypeKind();
	public final static int TYPE_CONSTANT = DEVICETYPEKIND;

	public final static int SINGLEBYTE_CONSTANT = 1;
	public final static int DOUBLEBYTE_CONSTANT = 2;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("DeviceTypeKind"), DEVICETYPEKIND);
	public final static SystemEnumerationDataBinding SINGLEBYTE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("singleByte"), null, TYPE, SINGLEBYTE_CONSTANT);
	public final static SystemEnumerationDataBinding DOUBLEBYTE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("doubleByte"), null, TYPE, DOUBLEBYTE_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(SINGLEBYTE);
		TYPE.addEnumeration(DOUBLEBYTE);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}
