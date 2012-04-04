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


public class UITypeKind extends Enumeration{
    public final static UITypeKind INSTANCE = new UITypeKind();
	public final static int TYPE_CONSTANT = UITYPEKIND;

	public final static int UIFORM_CONSTANT = 1;
	public final static int HIDDEN_CONSTANT = 2;
	public final static int INPUT_CONSTANT = 3;
	public final static int INPUTOUTPUT_CONSTANT = 4;
	public final static int NONE_CONSTANT = 5;
	public final static int OUTPUT_CONSTANT = 6;
	public final static int PROGRAMLINK_CONSTANT = 7;
	public final static int SUBMIT_CONSTANT = 8;
	public final static int SUBMITBYPASS_CONSTANT = 9;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("UITypeKind"), UITYPEKIND);
	public final static SystemEnumerationDataBinding UIFORM = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("uiForm"), null, TYPE, UIFORM_CONSTANT);
	public final static SystemEnumerationDataBinding HIDDEN = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("hidden"), null, TYPE, HIDDEN_CONSTANT);
	public final static SystemEnumerationDataBinding INPUT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("input"), null, TYPE, INPUT_CONSTANT);
	public final static SystemEnumerationDataBinding INPUTOUTPUT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("inputOutput"), null, TYPE, INPUTOUTPUT_CONSTANT);
	public final static SystemEnumerationDataBinding NONE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("none"), null, TYPE, NONE_CONSTANT);
	public final static SystemEnumerationDataBinding OUTPUT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("output"), null, TYPE, OUTPUT_CONSTANT);
	public final static SystemEnumerationDataBinding PROGRAMLINK = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("programLink"), null, TYPE, PROGRAMLINK_CONSTANT);
	public final static SystemEnumerationDataBinding SUBMIT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("submit"), null, TYPE, SUBMIT_CONSTANT);
	public final static SystemEnumerationDataBinding SUBMITBYPASS = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("submitBypass"), null, TYPE, SUBMITBYPASS_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(UIFORM);
		TYPE.addEnumeration(HIDDEN);
		TYPE.addEnumeration(INPUT);
		TYPE.addEnumeration(INPUTOUTPUT);
		TYPE.addEnumeration(NONE);
		TYPE.addEnumeration(OUTPUT);
		TYPE.addEnumeration(PROGRAMLINK);
		TYPE.addEnumeration(SUBMIT);
		TYPE.addEnumeration(SUBMITBYPASS);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}
