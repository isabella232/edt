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
public class LineWrapKind extends Enumeration{
    public final static LineWrapKind INSTANCE = new LineWrapKind();
	
    public final static int TYPE_CONSTANT = LINEWRAPKIND;
    public final static int COMPRESS_CONSTANT = 1;    
    public final static int WORD_CONSTANT = 2;
    public final static int CHARACTER_CONSTANT = 3;
    
    public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("LineWrapKind"), LINEWRAPKIND);
    public final static SystemEnumerationDataBinding COMPRESS = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("compress"), null, TYPE, COMPRESS_CONSTANT);
    public final static SystemEnumerationDataBinding WORD = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("word"), null, TYPE, WORD_CONSTANT);
    public final static SystemEnumerationDataBinding CHARACTER = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("character"), null, TYPE, CHARACTER_CONSTANT);
    
    
    static {
    	TYPE.setValid(true);
    	TYPE.addEnumeration(COMPRESS);
     	TYPE.addEnumeration(WORD);
     	TYPE.addEnumeration(CHARACTER);
     };
 	
     public EnumerationTypeBinding getType() {
         return TYPE;
     }
     
     public boolean isResolvable() {
         return true;
     }
}
