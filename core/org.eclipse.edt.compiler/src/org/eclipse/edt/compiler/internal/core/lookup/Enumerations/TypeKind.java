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


public class TypeKind extends Enumeration{
	public final static TypeKind INSTANCE = new TypeKind();
	public final static int TYPE_CONSTANT = TYPEKIND;

	public final static int ANYTYPE_CONSTANT = 1;
	public final static int NUMERICTYPE_CONSTANT = 2;
	public final static int TEXTTYPE_CONSTANT = 3;
	public final static int DATETYPE_CONSTANT = 4;
	public final static int ARRAYTYPE_CONSTANT = 5;
	public final static int DICTIONARYTYPE_CONSTANT = 6;
	public final static int NULLABLETYPE_CONSTANT = 7;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("TypeKind"), TYPEKIND);
	public final static SystemEnumerationDataBinding ANYTYPE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("AnyType"), null, TYPE, ANYTYPE_CONSTANT);
	public final static SystemEnumerationDataBinding NUMERICTYPE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("NumericType"), null, TYPE, NUMERICTYPE_CONSTANT);
	public final static SystemEnumerationDataBinding TEXTTYPE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("TextType"), null, TYPE, TEXTTYPE_CONSTANT);
	public final static SystemEnumerationDataBinding DATETYPE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("DateType"), null, TYPE, DATETYPE_CONSTANT);
	public final static SystemEnumerationDataBinding ARRAYTYPE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("ArrayType"), null, TYPE, ARRAYTYPE_CONSTANT);
	public final static SystemEnumerationDataBinding DICTIONARYTYPE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("DictionaryType"), null, TYPE, DICTIONARYTYPE_CONSTANT);
	public final static SystemEnumerationDataBinding NULLABLETYPE = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("NullableType"), null, TYPE, NULLABLETYPE_CONSTANT);

	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(ANYTYPE);
		TYPE.addEnumeration(NUMERICTYPE);
		TYPE.addEnumeration(TEXTTYPE);
		TYPE.addEnumeration(DATETYPE);
		TYPE.addEnumeration(ARRAYTYPE);
		TYPE.addEnumeration(DICTIONARYTYPE);
		TYPE.addEnumeration(NULLABLETYPE);
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}
