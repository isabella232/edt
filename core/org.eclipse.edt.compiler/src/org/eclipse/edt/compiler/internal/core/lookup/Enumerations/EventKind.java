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


public class EventKind extends Enumeration{
    public final static EventKind INSTANCE = new EventKind();
	public final static int TYPE_CONSTANT = EVENTKIND;

	public final static int AFTERDELETE_CONSTANT = 1;
	public final static int AFTERFIELD_CONSTANT = 2;
	public final static int AFTERINSERT_CONSTANT = 3;
	public final static int AFTEROPENUI_CONSTANT = 4;
	public final static int AFTERROW_CONSTANT = 5;
	public final static int BEFOREDELETE_CONSTANT = 6;
	public final static int BEFOREFIELD_CONSTANT = 7;
	public final static int BEFOREINSERT_CONSTANT = 8;
	public final static int BEFOREOPENUI_CONSTANT = 9;
	public final static int BEFOREROW_CONSTANT = 10;
	public final static int MENUACTION_CONSTANT = 11;
	public final static int ONKEY_CONSTANT = 12;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_UI_CONSOLE, InternUtil.internCaseSensitive("EventKind"), EVENTKIND);
	public final static SystemEnumerationDataBinding AFTERDELETE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("after_delete"), null, TYPE, AFTERDELETE_CONSTANT);
	public final static SystemEnumerationDataBinding AFTERFIELD = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("after_field"), null, TYPE, AFTERFIELD_CONSTANT);
	public final static SystemEnumerationDataBinding AFTERINSERT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("after_insert"), null, TYPE, AFTERINSERT_CONSTANT);
	public final static SystemEnumerationDataBinding AFTEROPENUI = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("after_openUI"), null, TYPE, AFTEROPENUI_CONSTANT);
	public final static SystemEnumerationDataBinding AFTERROW = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("after_row"), null, TYPE, AFTERROW_CONSTANT);
	public final static SystemEnumerationDataBinding BEFOREDELETE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("before_delete"), null, TYPE, BEFOREDELETE_CONSTANT);
	public final static SystemEnumerationDataBinding BEFOREFIELD = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("before_field"), null, TYPE, BEFOREFIELD_CONSTANT);
	public final static SystemEnumerationDataBinding BEFOREINSERT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("before_insert"), null, TYPE, BEFOREINSERT_CONSTANT);
	public final static SystemEnumerationDataBinding BEFOREOPENUI = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("before_openUI"), null, TYPE, BEFOREOPENUI_CONSTANT);
	public final static SystemEnumerationDataBinding BEFOREROW = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("before_row"), null, TYPE, BEFOREROW_CONSTANT);
	public final static SystemEnumerationDataBinding MENUACTION = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("menu_action"), null, TYPE, MENUACTION_CONSTANT);
	public final static SystemEnumerationDataBinding ONKEY = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("on_key"), null, TYPE, ONKEY_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(AFTERDELETE);
		TYPE.addEnumeration(AFTERFIELD);
		TYPE.addEnumeration(AFTERINSERT);
		TYPE.addEnumeration(AFTEROPENUI);
		TYPE.addEnumeration(AFTERROW);
		TYPE.addEnumeration(BEFOREDELETE);
		TYPE.addEnumeration(BEFOREFIELD);
		TYPE.addEnumeration(BEFOREINSERT);
		TYPE.addEnumeration(BEFOREOPENUI);
		TYPE.addEnumeration(BEFOREROW);
		TYPE.addEnumeration(MENUACTION);
		TYPE.addEnumeration(ONKEY);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return true;
    }
}
