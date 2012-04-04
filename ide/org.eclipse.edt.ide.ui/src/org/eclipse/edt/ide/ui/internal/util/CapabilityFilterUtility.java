/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.compiler.internal.EGLPropertyRule;

public class CapabilityFilterUtility {
	static final String[] EGLCORE = new String[] {"egl", "core"};
	static final String[] EGLJAVA = new String[] {"egl", "java"};
	static final String[] EGLUIJASPER = new String[] {"egl", "ui", "jasper"};
	static final String[] EGLUIJSF = new String[] {"egl", "ui", "jsf"};
	static final String[] EGLUITEXT = new String[] {"egl", "ui", "text"};
	static final String[] EGLUICONSOLE = new String[] {"egl", "ui", "console"};
	static final String[] EGLUIWEBTRANSACTION = new String[] {"egl", "ui", "webTransaction"};
	static final String[] EGLBIRT = new String[] {"egl", "report", "birt"};
	static final String[] EGLIOSQL = new String[] {"egl", "io", "sql"};
	static final String[] EGLIOFILE = new String[] {"egl", "io", "file"};
	static final String[] EGLIOMQ = new String[] {"egl", "io", "mq"};
	static final String[] EGLIODLI = new String[] {"egl", "io", "dli"};
	static final String[] EGLUIRUI = new String[] {"egl", "ui", "rui"};
	
	public static interface IPartBindingFilter {
		boolean partBindingPasses(IPartBinding partBinding);
	}
	
	public static interface IPropertyRuleFilter {
		boolean propertyRulePasses(EGLPropertyRule propertyRule);
	}
	
	public abstract static class PackageNameFilter implements IPartBindingFilter, IPropertyRuleFilter {
		private String[][] packageNames;

		public PackageNameFilter(String[] packageName) {
			this(new String[][] {packageName});
		}
		
		public PackageNameFilter(String[][] packageNames) {
			this.packageNames = packageNames;
		}
		
		private boolean allowed(String[] packageName) {
			boolean passes = true;
			for(int i = 0; i < packageNames.length && passes; i++) {
				if(equals(packageName, packageNames[i])) {
					passes = isAllowed();
				}
			}
			return passes;
		}
		
		public boolean partBindingPasses(IPartBinding partBinding) {
			return allowed(partBinding.getPackageName());
		}
		
		public boolean propertyRulePasses(EGLPropertyRule propertyRule) {
			return allowed(propertyRule.getPackageName());
		}

		protected abstract boolean isAllowed();

		private boolean equals(String[] partPackageName, String[] packageName2) {
			if(partPackageName == null || partPackageName.length != packageName2.length) {
				return false;
			}
			for(int i = 0; i < partPackageName.length; i++) {
				if(!partPackageName[i].equalsIgnoreCase(packageName2[i])) {
					return false;
				}
			}
			return true;
		}
	}
	
	private static IPartBindingFilter[] defaultPartBindingFilters = new IPartBindingFilter[] {
		new PackageNameFilter(new String[][] {EGLJAVA, EGLUIJSF}) { protected boolean isAllowed() { return EGLBasePlugin.isJSF(); } },
		new PackageNameFilter(EGLIODLI) { protected boolean isAllowed() { return EGLBasePlugin.isDLI(); } },
		new PackageNameFilter(EGLUICONSOLE) { protected boolean isAllowed() { return EGLBasePlugin.isCUI(); } },
		new PackageNameFilter(EGLUIJASPER) { protected boolean isAllowed() { return EGLBasePlugin.isReports(); } },
		new PackageNameFilter(EGLUITEXT) { protected boolean isAllowed() { return EGLBasePlugin.isTUI(); } },
		new PackageNameFilter(EGLUIWEBTRANSACTION) { protected boolean isAllowed() { return EGLBasePlugin.isVGUI(); } },
		new PackageNameFilter(EGLBIRT) { protected boolean isAllowed() { return EGLBasePlugin.isBIRT(); } },
		new PackageNameFilter(EGLIOMQ) { protected boolean isAllowed() { return EGLBasePlugin.isMQ(); } },
		new PackageNameFilter(EGLUIRUI) { protected boolean isAllowed() { return EGLBasePlugin.isRUI(); } },
	};
	
	public static Collection filterParts(Collection partBindings) {
		return filterParts(partBindings, new IPartBindingFilter[0]);
	}
	
	public static Collection filterParts(Collection partBindings, IPartBindingFilter[] additionalFilters) {
		Collection result = new ArrayList();
		for(Iterator iter = partBindings.iterator(); iter.hasNext();) {
			IPartBinding next = (IPartBinding) iter.next();
			boolean passes = true;
			for(int i = 0; i < defaultPartBindingFilters.length && passes; i++) {
				passes = defaultPartBindingFilters[i].partBindingPasses(next);
			}
			for(int i = 0; i < additionalFilters.length && passes; i++) {
				passes = additionalFilters[i].partBindingPasses(next);
			}
			if(passes) {
				result.add(next);
			}
		}
		return result;
	}
	
	private static IPropertyRuleFilter[] defaultPropertyRuleFilters = new IPropertyRuleFilter[] {
		new PackageNameFilter(new String[][] {EGLJAVA, EGLUIJSF}) { protected boolean isAllowed() { return EGLBasePlugin.isJSF(); } },
		new PackageNameFilter(EGLIODLI) { protected boolean isAllowed() { return EGLBasePlugin.isDLI(); } },
		new PackageNameFilter(EGLUICONSOLE) { protected boolean isAllowed() { return EGLBasePlugin.isCUI(); } },
		new PackageNameFilter(EGLUIJASPER) { protected boolean isAllowed() { return EGLBasePlugin.isReports(); } },
		new PackageNameFilter(EGLUITEXT) { protected boolean isAllowed() { return EGLBasePlugin.isTUI(); } },
		new PackageNameFilter(EGLUIWEBTRANSACTION) { protected boolean isAllowed() { return EGLBasePlugin.isVGUI(); } },
		new PackageNameFilter(EGLBIRT) { protected boolean isAllowed() { return EGLBasePlugin.isBIRT(); } },
		new PackageNameFilter(EGLIOMQ) { protected boolean isAllowed() { return EGLBasePlugin.isMQ(); } },
		new PackageNameFilter(EGLUIRUI) { protected boolean isAllowed() { return EGLBasePlugin.isRUI(); } },
	};
	
	public static Collection filterPropertyRules(Collection propertyRules) {
		return filterPropertyRules(propertyRules, new IPropertyRuleFilter[0]);
	}
	
	public static Collection filterPropertyRules(Collection propertyRules, IPropertyRuleFilter[] additionalFilters) {
		Collection result = new ArrayList();
		if (propertyRules !=  null) {
			for(Iterator iter = propertyRules.iterator(); iter.hasNext();) {
				EGLPropertyRule next = (EGLPropertyRule) iter.next();
				boolean passes = true;
				for(int i = 0; i < defaultPropertyRuleFilters.length && passes; i++) {
					passes = defaultPropertyRuleFilters[i].propertyRulePasses(next);
				}
				for(int i = 0; i < additionalFilters.length && passes; i++) {
					passes = additionalFilters[i].propertyRulePasses(next);
				}
				if(passes) {
					result.add(next);
				}
			}
		}
		return result;
	}
}
