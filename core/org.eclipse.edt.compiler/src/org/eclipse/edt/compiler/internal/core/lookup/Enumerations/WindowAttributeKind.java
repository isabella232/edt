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
public class WindowAttributeKind extends Enumeration{
    public final static WindowAttributeKind INSTANCE = new WindowAttributeKind();
    public final static int TYPE_CONSTANT = WINDOWATTRIBUTEKIND;

    public static final int COMMENT_LINE_CONSTANT = 1;
    public static final int ERROR_LINE_CONSTANT = 1;
    public static final int FORM_LINE_CONSTANT = 1;
    public static final int MENU_LINE_CONSTANT = 1;
    public static final int MESSAGE_LINE_CONSTANT = 1;
    public static final int PROMPT_LINE_CONSTANT = 1;
    public static final int COLOR_CONSTANT = 1;
    public static final int INTENSITY_CONSTANT = 1;
    public static final int HIGHLIGHT_CONSTANT = 1;
    
    public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_UI_CONSOLE, InternUtil.internCaseSensitive("WindowAttributeKind"), WINDOWATTRIBUTEKIND);
    public final static SystemEnumerationDataBinding COMMENT_LINE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("commentLine"), null, TYPE, COMMENT_LINE_CONSTANT);
    public final static SystemEnumerationDataBinding ERROR_LINE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("errorLine"), null, TYPE, ERROR_LINE_CONSTANT);
    public final static SystemEnumerationDataBinding FORM_LINE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("formLine"), null, TYPE, FORM_LINE_CONSTANT);
    public final static SystemEnumerationDataBinding MENU_LINE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("menuLine"), null, TYPE, MENU_LINE_CONSTANT);
    public final static SystemEnumerationDataBinding MESSAGE_LINE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("messageLine"), null, TYPE, MESSAGE_LINE_CONSTANT);
    public final static SystemEnumerationDataBinding PROMPT_LINE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("promptLine"), null, TYPE, PROMPT_LINE_CONSTANT);
    public final static SystemEnumerationDataBinding COLOR = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("color"), null, TYPE, COLOR_CONSTANT);
    public final static SystemEnumerationDataBinding INTENSITY = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("intensity"), null, TYPE, INTENSITY_CONSTANT);
    public final static SystemEnumerationDataBinding HIGHLIGHT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("highlight"), null, TYPE, HIGHLIGHT_CONSTANT);

    static {
        TYPE.setValid(true);
        TYPE.addEnumeration(COMMENT_LINE);
        TYPE.addEnumeration(ERROR_LINE);
        TYPE.addEnumeration(FORM_LINE);
        TYPE.addEnumeration(MENU_LINE);
        TYPE.addEnumeration(MESSAGE_LINE);
        TYPE.addEnumeration(PROMPT_LINE);
        TYPE.addEnumeration(COLOR);
        TYPE.addEnumeration(INTENSITY);
        TYPE.addEnumeration(HIGHLIGHT);
    };
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return true;
    }
}
