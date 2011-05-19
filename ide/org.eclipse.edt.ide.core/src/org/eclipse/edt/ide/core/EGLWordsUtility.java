/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.ISystemPackageBuildPathEntry;
import org.eclipse.edt.compiler.SystemEnvironment;
import org.eclipse.edt.compiler.core.EGLKeywordHandler;
import org.eclipse.edt.compiler.core.EGLSQLKeywordHandler;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;

public class EGLWordsUtility {
	
	//Returns a set of case insenstive interned strings that represent the name of all of the EGL system parts
	public static Set getEGLSystemPartNames() {
		EDTCoreIDEPlugin.getPlugin(); //Force the systemEnvironment to be initialized
		Set set = new HashSet();
		ISystemPackageBuildPathEntry[] entries = SystemEnvironment.getInstance().getSysPackages();
		for (int i = 0; i < entries.length; i++) {
			HashMap map = entries[i].getPartBindingsWithoutPackage();
			buildPartNames(map, set);
			map = entries[i].getPartNamesByPackage();
			Iterator iter = map.values().iterator();
			while (iter.hasNext()) {
				buildPartNames((Map)iter.next(), set);
			}
		}
		return set;
	}
	
	private static void buildPartNames(Map map, Set set) {
		Iterator i = map.keySet().iterator();
		while (i.hasNext()) {
			String name = (String) i.next();
			set.add(InternUtil.intern(name));
		}
	}
	
	//Returns a set of case insenstive interned strings that represent the name of all of the EGL reserved words
	public static Set getEGLKeyWords() {
		String[] keywords = EGLKeywordHandler.getKeywordNames();
		Set interned = new HashSet();
		for (int i = 0; i < keywords.length; i++) {
			interned.add(InternUtil.intern(keywords[i]));
		}
		return interned;
	}

	//Returns a set of case insenstive interned strings that represent the name of all of the SQL key words
	public static Set getSQLKeyWords() {
		String[] keywords = EGLSQLKeywordHandler.getSQLKeywordNames();
		Set interned = new HashSet();
		for (int i = 0; i < keywords.length; i++) {
			interned.add(InternUtil.intern(keywords[i]));
		}
		return interned;
	}

	//Returns a set of case insenstive interned strings that represent the name of all of the SQL clause key words
	public static Set getSQLClauseKeyWords() {
		String[] keywords = EGLSQLKeywordHandler.getSQLClauseKeywordNames();
		Set interned = new HashSet();
		for (int i = 0; i < keywords.length; i++) {
			interned.add(InternUtil.intern(keywords[i]));
		}
		return interned;
	}

}
