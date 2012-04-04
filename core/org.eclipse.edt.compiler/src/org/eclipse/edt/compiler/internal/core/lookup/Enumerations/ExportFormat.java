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
public class ExportFormat extends Enumeration{
    public final static ExportFormat INSTANCE = new ExportFormat();

	public final static int TYPE_CONSTANT = EXPORTFORMAT;
	public final static int CSV_CONSTANT = 1;
	public final static int HTML_CONSTANT = 2;
	public final static int PDF_CONSTANT = 3;
	public final static int TEXT_CONSTANT = 4;
	public final static int XML_CONSTANT = 5;
	
	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_REPORTS_JASPER, InternUtil.internCaseSensitive("ExportFormat"), EXPORTFORMAT);
	public final static SystemEnumerationDataBinding CSV = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("csv"), null, TYPE, CSV_CONSTANT);
	public final static SystemEnumerationDataBinding HTML = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("html"), null, TYPE, HTML_CONSTANT);
	public final static SystemEnumerationDataBinding PDF = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("pdf"), null, TYPE, PDF_CONSTANT);
	public final static SystemEnumerationDataBinding TEXT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("text"), null, TYPE, TEXT_CONSTANT);
	public final static SystemEnumerationDataBinding XML = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("xml"), null, TYPE, XML_CONSTANT);
	
	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(CSV);
		TYPE.addEnumeration(HTML);
		TYPE.addEnumeration(PDF);
		TYPE.addEnumeration(TEXT);
		TYPE.addEnumeration(XML);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return true;
    }
}
