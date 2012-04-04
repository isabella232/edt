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
 * @author Harmon
 */
public class WhitespaceKind extends Enumeration{
    public final static WhitespaceKind INSTANCE = new WhitespaceKind();
    public final static int TYPE_CONSTANT = WHITESPACEKIND;

    public final static int PRESERVEWHITESPACE_CONSTANT = 1;
    public final static int REPLACEWHITESPACE_CONSTANT = 2;
    public final static int COLLAPSEWHITESPACE_CONSTANT = 3;
    
    public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("WhitespaceKind"), WHITESPACEKIND);
    public final static SystemEnumerationDataBinding PRESERVEWHITESPACE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("preserveWhitespace"), null, TYPE, PRESERVEWHITESPACE_CONSTANT);
    public final static SystemEnumerationDataBinding REPLACEWHITESPACE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("replaceWhitespace"), null, TYPE, REPLACEWHITESPACE_CONSTANT);
    public final static SystemEnumerationDataBinding COLLAPSEWHITESPACE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("collapseWhitespace"), null, TYPE, COLLAPSEWHITESPACE_CONSTANT);

    static {
        TYPE.setValid(true);
        TYPE.addEnumeration(PRESERVEWHITESPACE);
        TYPE.addEnumeration(REPLACEWHITESPACE);
        TYPE.addEnumeration(COLLAPSEWHITESPACE);
    };
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}
