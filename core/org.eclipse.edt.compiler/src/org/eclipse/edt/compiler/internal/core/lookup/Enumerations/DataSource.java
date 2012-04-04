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
public class DataSource extends Enumeration{
    public final static DataSource INSTANCE = new DataSource();

	public final static int TYPE_CONSTANT = DATASOURCE;
	public final static int DATABASECONNECTION_CONSTANT = 1;
	public final static int REPORTDATA_CONSTANT = 2;
	public final static int SQLSTATEMENT_CONSTANT = 3;
	
	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_REPORTS_JASPER, InternUtil.internCaseSensitive("DataSource"), DATASOURCE);
	public final static SystemEnumerationDataBinding DATABASECONNECTION = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("databaseConnection"), null, TYPE, DATABASECONNECTION_CONSTANT);
	public final static SystemEnumerationDataBinding REPORTDATA = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("reportData"), null, TYPE, REPORTDATA_CONSTANT);
	public final static SystemEnumerationDataBinding SQLSTATEMENT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("sqlStatement"), null, TYPE, SQLSTATEMENT_CONSTANT);
	
	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(DATABASECONNECTION);
		TYPE.addEnumeration(REPORTDATA);
		TYPE.addEnumeration(SQLSTATEMENT);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return true;
    }
}
