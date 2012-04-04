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


public class RESTFormatKind extends Enumeration {

	public final static int NONE_CONSTANT = 1;
	public final static int XML_CONSTANT = 2;
	public final static int JSON_CONSTANT = 3;
	public final static int FORMDATA_CONSTANT = 4;
	
	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("restFormatKind"), RESTFORMATKIND);
    public final static SystemEnumerationDataBinding NONE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("none"), null, TYPE, NONE_CONSTANT);
    public final static SystemEnumerationDataBinding XML = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("xml"), null, TYPE, XML_CONSTANT);
    public final static SystemEnumerationDataBinding JSON = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("json"), null, TYPE, JSON_CONSTANT);
    public final static SystemEnumerationDataBinding FORMDATA = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("formdata"), null, TYPE, FORMDATA_CONSTANT);    
	
	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(NONE);
		TYPE.addEnumeration(XML);
		TYPE.addEnumeration(JSON);
		TYPE.addEnumeration(FORMDATA);
	}
	
	public EnumerationTypeBinding getType() {
		return TYPE;
	}

	public boolean isResolvable() {
		return true;
	}

}
