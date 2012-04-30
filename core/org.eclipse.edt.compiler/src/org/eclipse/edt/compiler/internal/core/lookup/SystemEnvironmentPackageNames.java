/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.lookup;

import org.eclipse.edt.mof.egl.utils.InternUtil;

/**
 * @author Harmon
 */
public  class SystemEnvironmentPackageNames {
    
    public static final String[] EGL_CORE = InternUtil.intern(new String[] {"egl", "core"});
    public static final String[] EGLX_LANG = InternUtil.intern(new String[] {"eglx", "lang"});
    public static final String[] EGL_UI_CONSOLE = InternUtil.intern(new String[] {"egl", "ui", "console"});
    public static final String[] EGL_REPORTS_JASPER = InternUtil.intern(new String[] {"egl", "reports", "jasper"});
    public static final String[] EGLX_DLI = InternUtil.intern(new String[] {"eglx", "dli"});
    public static final String[] EGL_CORE_REFLECT = InternUtil.intern(new String[] {"egl", "core", "reflect"});
    public static final String[] EGL_REPORT_BIRT = InternUtil.intern(new String[] {"egl", "report", "birt"});

    
    public static final String EGL_CORE_STRING = InternUtil.intern("egl.core");
    public static final String EGL_CORE_REFLECT_STRING = InternUtil.intern("egl.core.reflect");

}
