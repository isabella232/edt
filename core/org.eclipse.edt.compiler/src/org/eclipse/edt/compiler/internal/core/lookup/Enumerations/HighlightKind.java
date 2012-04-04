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
public class HighlightKind extends Enumeration{
    public final static HighlightKind INSTANCE = new HighlightKind();

	public final static int TYPE_CONSTANT = HIGHLIGHTKIND;
    public final static int BLINK_CONSTANT = 1;    
    public final static int NOHIGHLIGHT_CONSTANT = 2;
    public final static int REVERSE_CONSTANT = 3;
    public final static int UNDERLINE_CONSTANT = 4;
    public final static int DEFAULTHIGHLIGHT_CONSTANT = 5;
    
    public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("HighlightKind"), HIGHLIGHTKIND);
    public final static SystemEnumerationDataBinding BLINK = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("blink"), null, TYPE, BLINK_CONSTANT);
    public final static SystemEnumerationDataBinding NOHIGHLIGHT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("noHighlight"), null, TYPE, NOHIGHLIGHT_CONSTANT);
    public final static SystemEnumerationDataBinding REVERSE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("reverse"), null, TYPE, REVERSE_CONSTANT);
    public final static SystemEnumerationDataBinding UNDERLINE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("underline"), null, TYPE, UNDERLINE_CONSTANT);
    public final static SystemEnumerationDataBinding DEFAULTHIGHLIGHT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("defaultHighlight"), null, TYPE, DEFAULTHIGHLIGHT_CONSTANT);
    
    
    static {
    	TYPE.setValid(true);
        TYPE.addEnumeration(BLINK);
     	TYPE.addEnumeration(NOHIGHLIGHT);
     	TYPE.addEnumeration(NOHIGHLIGHT);
     	TYPE.addEnumeration(REVERSE);
     	TYPE.addEnumeration(UNDERLINE);
     	TYPE.addEnumeration(DEFAULTHIGHLIGHT);
     };
 	
     public EnumerationTypeBinding getType() {
         return TYPE;
     }
     
     public boolean isResolvable() {
         return true;
     }
}
