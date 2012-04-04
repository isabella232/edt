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


public class PCBKind extends Enumeration{
    public final static PCBKind INSTANCE = new PCBKind();
	public final static int TYPE_CONSTANT = PCBKIND;

	public final static int TP_CONSTANT = 1;
	public final static int DB_CONSTANT = 2;
	public final static int GSAM_CONSTANT = 3;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGLX_DLI, InternUtil.internCaseSensitive("PCBKind"), PCBKIND);
	public final static SystemEnumerationDataBinding TP = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("TP"), null, TYPE, TP_CONSTANT);
	public final static SystemEnumerationDataBinding DB = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("DB"), null, TYPE, DB_CONSTANT);
	public final static SystemEnumerationDataBinding GSAM = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("GSAM"), null, TYPE, GSAM_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(TP);
		TYPE.addEnumeration(DB);
		TYPE.addEnumeration(GSAM);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}
