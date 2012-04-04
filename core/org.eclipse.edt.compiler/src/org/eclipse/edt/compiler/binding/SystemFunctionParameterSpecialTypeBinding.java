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
package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.mof.egl.utils.InternUtil;

/**
 * @author Dave Murray
 */
public class SystemFunctionParameterSpecialTypeBinding extends TypeBinding {
	
	public static SystemFunctionParameterSpecialTypeBinding ANYEGL = new SystemFunctionParameterSpecialTypeBinding("any EGL primitive");
	public static SystemFunctionParameterSpecialTypeBinding ANYEGLORASJAVA = new SystemFunctionParameterSpecialTypeBinding("any EGL primitive or asjava");
	public static SystemFunctionParameterSpecialTypeBinding ARRAYORTABLE = new SystemFunctionParameterSpecialTypeBinding("array or dataTable");
	public static SystemFunctionParameterSpecialTypeBinding ATTRIBUTE = new SystemFunctionParameterSpecialTypeBinding("J2EE function attribute");
	public static SystemFunctionParameterSpecialTypeBinding CONSOLEFORM = new SystemFunctionParameterSpecialTypeBinding("console form");
	public static SystemFunctionParameterSpecialTypeBinding IDENTIFIER = new SystemFunctionParameterSpecialTypeBinding("identifier");
	public static SystemFunctionParameterSpecialTypeBinding ITEMORRECORD = new SystemFunctionParameterSpecialTypeBinding("item or record");
	public static SystemFunctionParameterSpecialTypeBinding OBJIDTYPE = new SystemFunctionParameterSpecialTypeBinding("objIdType");
	public static SystemFunctionParameterSpecialTypeBinding OBJIDTYPEOPT = new SystemFunctionParameterSpecialTypeBinding("objIdTypeOpt");
	public static SystemFunctionParameterSpecialTypeBinding RECORD = new SystemFunctionParameterSpecialTypeBinding("record");
	public static SystemFunctionParameterSpecialTypeBinding FLEXIBLERECORD = new SystemFunctionParameterSpecialTypeBinding("flexible record");
	public static SystemFunctionParameterSpecialTypeBinding RECORDORDICTIONARY = new SystemFunctionParameterSpecialTypeBinding("record or dictionary");
	public static SystemFunctionParameterSpecialTypeBinding SERVICEORINTERFACE = new SystemFunctionParameterSpecialTypeBinding("service or interface");	
	public static SystemFunctionParameterSpecialTypeBinding TEXTFIELD = new SystemFunctionParameterSpecialTypeBinding("text field");
	public static SystemFunctionParameterSpecialTypeBinding VAGTEXT = new SystemFunctionParameterSpecialTypeBinding("vag text primitive");
	public static SystemFunctionParameterSpecialTypeBinding VAGTEXTORNUMERIC = new SystemFunctionParameterSpecialTypeBinding("vag text or numeric primitive");
	
	public static SystemFunctionParameterSpecialTypeBinding getType (String name) {
	    String iName = InternUtil.intern(name);
	    if (ANYEGL.getName() == iName) {
	        return ANYEGL;
	    }
	    if (ANYEGLORASJAVA.getName() == iName) {
	        return ANYEGLORASJAVA;
	    }
	    if (ARRAYORTABLE.getName() == iName) {
	        return ARRAYORTABLE;
	    }
	    if (ATTRIBUTE.getName() == iName) {
	        return ATTRIBUTE;
	    }
	    if (CONSOLEFORM.getName() == iName) {
	        return CONSOLEFORM;
	    }
	    if (IDENTIFIER.getName() == iName) {
	        return IDENTIFIER;
	    }
	    if (ITEMORRECORD.getName() == iName) {
	        return ITEMORRECORD;
	    }
	    if (OBJIDTYPE.getName() == iName) {
	        return OBJIDTYPE;
	    }
	    if (OBJIDTYPEOPT.getName() == iName) {
	        return OBJIDTYPEOPT;
	    }
	    if (RECORD.getName() == iName) {
	        return RECORD;
	    }
	    if (FLEXIBLERECORD.getName() == iName) {
	        return FLEXIBLERECORD;
	    }
	    if (RECORDORDICTIONARY.getName() == iName) {
	        return RECORDORDICTIONARY;
	    }
	    if (SERVICEORINTERFACE.getName() == iName) {
	        return SERVICEORINTERFACE;
	    }
	    if (TEXTFIELD.getName() == iName) {
	        return TEXTFIELD;
	    }
	    if (VAGTEXT.getName() == iName) {
	        return VAGTEXT;
	    }
	    if (VAGTEXTORNUMERIC.getName() == iName) {
	        return VAGTEXTORNUMERIC;
	    }
	    return null;
	}
	private SystemFunctionParameterSpecialTypeBinding(String name) {
		super(InternUtil.internCaseSensitive(name));
	}

	public int getKind() {
		return SPECIALSYSTEMFUNCTIONPARAMETER_BINDING;
	}

	public ITypeBinding getBaseType() {
		return this;
	}

	@Override
	public ITypeBinding primGetNullableInstance() {
		return this;
	}
}
