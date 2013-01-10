/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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
import org.eclipse.edt.compiler.internal.EGLPropertyRule;

public class CapabilityFilterUtility {
	
	public static interface IPartBindingFilter {
		boolean partBindingPasses(IPartBinding partBinding);
	}
	
	public static interface IPropertyRuleFilter {
		boolean propertyRulePasses(EGLPropertyRule propertyRule);
	}
	
	public abstract static class PackageNameFilter implements IPartBindingFilter, IPropertyRuleFilter {
		private String[] packageNames;

		public PackageNameFilter(String packageName) {
			this(new String[] {packageName});
		}
		
		public PackageNameFilter(String[] packageNames) {
			this.packageNames = packageNames;
		}
		
		private boolean allowed(String packageName) {
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

		private boolean equals(String partPackageName, String packageName2) {
			if (partPackageName == packageName2) {
				return true;
			}

			if (partPackageName == null || packageName2 == null) {
				return false;
			}

			return partPackageName.equalsIgnoreCase(packageName2);
		}
	}
	
	private static IPartBindingFilter[] defaultPartBindingFilters = new IPartBindingFilter[] {
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
