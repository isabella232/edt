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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class ForeignLanguageTypeBinding extends TypeBinding {
	
	public static final int NO_KIND = 0;
	public static final int OBJID_KIND = 1;
	public static final int JAVA_KIND = 2;
	
	public static ForeignLanguageTypeBinding OBJIDJAVA = new ForeignLanguageTypeBinding("objId:java", OBJID_KIND);
	
	public static ForeignLanguageTypeBinding JAVACHAR = new ForeignLanguageTypeBinding("java:char", JAVA_KIND);
	public static ForeignLanguageTypeBinding JAVABIGDECIMAL = new ForeignLanguageTypeBinding("java:java.math.bigdecimal", JAVA_KIND);
	public static ForeignLanguageTypeBinding JAVABIGINTEGER = new ForeignLanguageTypeBinding("java:java.math.biginteger", JAVA_KIND);
	public static ForeignLanguageTypeBinding JAVABYTE = new ForeignLanguageTypeBinding("java:byte", JAVA_KIND);
	public static ForeignLanguageTypeBinding JAVADOUBLE = new ForeignLanguageTypeBinding("java:double", JAVA_KIND);
	public static ForeignLanguageTypeBinding JAVAFLOAT = new ForeignLanguageTypeBinding("java:float", JAVA_KIND);
	public static ForeignLanguageTypeBinding JAVASHORT = new ForeignLanguageTypeBinding("java:short", JAVA_KIND);
	public static ForeignLanguageTypeBinding JAVAINT = new ForeignLanguageTypeBinding("java:int", JAVA_KIND);
	public static ForeignLanguageTypeBinding JAVALONG = new ForeignLanguageTypeBinding("java:long", JAVA_KIND);
	public static ForeignLanguageTypeBinding JAVABOOLEAN = new ForeignLanguageTypeBinding("java:boolean", JAVA_KIND);
	
	public static ForeignLanguageTypeBinding NULL = new ForeignLanguageTypeBinding("null", NO_KIND);
	
	private static Map instances = new HashMap();
	static {
		instances.put(OBJIDJAVA.getName().toLowerCase(), OBJIDJAVA);
		
		instances.put(JAVACHAR.getName().toLowerCase(), JAVACHAR);
		instances.put(JAVABIGDECIMAL.getName().toLowerCase(), JAVABIGDECIMAL);
		instances.put(JAVABIGINTEGER.getName().toLowerCase(), JAVABIGINTEGER);
		instances.put(JAVABYTE.getName().toLowerCase(), JAVABYTE);
		instances.put(JAVADOUBLE.getName().toLowerCase(), JAVADOUBLE);
		instances.put(JAVAFLOAT.getName().toLowerCase(), JAVAFLOAT);
		instances.put(JAVASHORT.getName().toLowerCase(), JAVASHORT);
		instances.put(JAVAINT.getName().toLowerCase(), JAVAINT);
		instances.put(JAVALONG.getName().toLowerCase(), JAVALONG);
		instances.put(JAVABOOLEAN.getName().toLowerCase(), JAVABOOLEAN);
	}
	
	public static ForeignLanguageTypeBinding getInstance(String name) {
		return (ForeignLanguageTypeBinding) instances.get(name.toLowerCase());
	}
	
	private int kind;

	private ForeignLanguageTypeBinding(String identifier, int kind) {
		super(InternUtil.internCaseSensitive(identifier));
		this.kind = kind;
	}

	public int getKind() {
		return FOREIGNLANGUAGETYPE_BINDING;
	}
	
	public int getForeignLanguageKind() {
		return kind;
	}

	public ITypeBinding getBaseType() {
		return this;
	}
	
	public static Collection getSupportedCastStrings() {
		Set result = new TreeSet();
		for(Iterator iter = instances.values().iterator(); iter.hasNext();) {
			result.add(((IBinding) iter.next()).getCaseSensitiveName());
		}
		return result;
	}
	
	@Override
	public ITypeBinding primGetNullableInstance() {
		ForeignLanguageTypeBinding nullable = new ForeignLanguageTypeBinding(getCaseSensitiveName(), kind);
		nullable.setNullable(true);
		return nullable;
	}

}
