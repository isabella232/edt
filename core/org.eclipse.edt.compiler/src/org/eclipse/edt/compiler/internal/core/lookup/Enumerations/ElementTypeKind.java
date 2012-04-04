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


public class ElementTypeKind extends Enumeration{
    public final static ElementTypeKind INSTANCE = new ElementTypeKind();
	public final static int TYPE_CONSTANT = ELEMENTTYPEKIND;

	public final static int DETAILROW_CONSTANT = 1;
	public final static int HEADERROW_CONSTANT = 2;
	public final static int FOOTERROW_CONSTANT = 3;
	public final static int GROUPHEADERROW_CONSTANT = 4;
	public final static int GROUPFOOTERROW_CONSTANT = 5;
	public final static int LABEL_CONSTANT = 6;
	public final static int TEXT_CONSTANT = 7; 
	public final static int DATA_CONSTANT = 8; 
	public final static int GRID_CONSTANT = 9; 
	public final static int TABLE_CONSTANT = 10; 
	public final static int LIST_CONSTANT = 11; 
	
	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_REPORT_BIRT, InternUtil.internCaseSensitive("ElementTypeKind"), ELEMENTTYPEKIND);
	public final static SystemEnumerationDataBinding DETAILROW = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("detailRow"), null, TYPE, DETAILROW_CONSTANT);
	public final static SystemEnumerationDataBinding HEADERROW = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("headerRow"), null, TYPE, HEADERROW_CONSTANT);
	public final static SystemEnumerationDataBinding FOOTERROW = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("footerRow"), null, TYPE, FOOTERROW_CONSTANT);
	public final static SystemEnumerationDataBinding GROUPHEADERROW = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("groupHeaderRow"), null, TYPE, GROUPHEADERROW_CONSTANT);
	public final static SystemEnumerationDataBinding GROUPFOOTERROW = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("groupFooterRow"), null, TYPE, GROUPFOOTERROW_CONSTANT);
	public final static SystemEnumerationDataBinding LABEL = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("labelElement"), null, TYPE, LABEL_CONSTANT);
	public final static SystemEnumerationDataBinding TEXT = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("textElement"), null, TYPE, TEXT_CONSTANT);
	public final static SystemEnumerationDataBinding DATA = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("dataElement"), null, TYPE, DATA_CONSTANT);
	public final static SystemEnumerationDataBinding GRID = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("grid"), null, TYPE, GRID_CONSTANT);
	public final static SystemEnumerationDataBinding TABLE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("table"), null, TYPE, TABLE_CONSTANT);	
	public final static SystemEnumerationDataBinding LIST = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("list"), null, TYPE, LIST_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(DETAILROW);
		TYPE.addEnumeration(HEADERROW);
		TYPE.addEnumeration(FOOTERROW);
		TYPE.addEnumeration(GROUPHEADERROW);
		TYPE.addEnumeration(GROUPFOOTERROW);
		TYPE.addEnumeration(LABEL);
		TYPE.addEnumeration(TEXT);
		TYPE.addEnumeration(DATA);
		TYPE.addEnumeration(GRID);
		TYPE.addEnumeration(TABLE);
		TYPE.addEnumeration(LIST);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}
