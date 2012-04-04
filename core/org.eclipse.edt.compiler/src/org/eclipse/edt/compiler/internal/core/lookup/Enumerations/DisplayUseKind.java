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


public class DisplayUseKind extends Enumeration{
    public final static DisplayUseKind INSTANCE = new DisplayUseKind();
	public final static int TYPE_CONSTANT = DISPLAYUSEKIND;

	public final static int BUTTON_CONSTANT = 1;
	public final static int HYPERLINK_CONSTANT = 2;
	public final static int INPUT_CONSTANT = 3;
	public final static int OUTPUT_CONSTANT = 4;
	public final static int SECRET_CONSTANT = 5;
	public final static int TABLE_CONSTANT = 6;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("DisplayUseKind"), DISPLAYUSEKIND);
	public final static SystemEnumerationDataBinding BUTTON = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("button"), null, TYPE, BUTTON_CONSTANT);
	public final static SystemEnumerationDataBinding HYPERLINK = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("hyperlink"), null, TYPE, HYPERLINK_CONSTANT);
	public final static SystemEnumerationDataBinding INPUT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("input"), null, TYPE, INPUT_CONSTANT);
	public final static SystemEnumerationDataBinding OUTPUT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("output"), null, TYPE, OUTPUT_CONSTANT);
	public final static SystemEnumerationDataBinding SECRET = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("secret"), null, TYPE, SECRET_CONSTANT);
	public final static SystemEnumerationDataBinding TABLE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("table"), null, TYPE, TABLE_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(BUTTON);
		TYPE.addEnumeration(HYPERLINK);
		TYPE.addEnumeration(INPUT);
		TYPE.addEnumeration(OUTPUT);
		TYPE.addEnumeration(SECRET);
		TYPE.addEnumeration(TABLE);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}
