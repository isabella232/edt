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


public class DLICallInterfaceKind extends Enumeration{
    public final static DLICallInterfaceKind INSTANCE = new DLICallInterfaceKind();
	public final static int TYPE_CONSTANT = DLICALLINTERFACEKIND;

	public final static int AIBTDLI_CONSTANT = 1;
	public final static int CBLTDLI_CONSTANT = 2;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGLX_DLI, InternUtil.internCaseSensitive("DLICallInterfaceKind"), DLICALLINTERFACEKIND);
	public final static SystemEnumerationDataBinding AIBTDLI = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("AIBTDLI"), null, TYPE, AIBTDLI_CONSTANT);
	public final static SystemEnumerationDataBinding CBLTDLI = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("CBLTDLI"), null, TYPE, CBLTDLI_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(AIBTDLI);
		TYPE.addEnumeration(CBLTDLI);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}
