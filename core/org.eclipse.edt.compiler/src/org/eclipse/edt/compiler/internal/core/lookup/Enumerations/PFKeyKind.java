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

import java.util.Comparator;

import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationDataBinding;
import org.eclipse.edt.compiler.binding.SystemEnumerationTypeBinding;
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class PFKeyKind extends Enumeration{
    public final static PFKeyKind INSTANCE = new PFKeyKind();
	public final static int TYPE_CONSTANT = PFKEYKIND;

	public final static int PF1_CONSTANT = 1;
	public final static int PF2_CONSTANT = 2;
	public final static int PF3_CONSTANT = 3;
	public final static int PF4_CONSTANT = 4;
	public final static int PF5_CONSTANT = 5;
	public final static int PF6_CONSTANT = 6;
	public final static int PF7_CONSTANT = 7;
	public final static int PF8_CONSTANT = 8;
	public final static int PF9_CONSTANT = 9;
	public final static int PF10_CONSTANT = 10;
	public final static int PF11_CONSTANT = 11;
	public final static int PF12_CONSTANT = 12;
	public final static int PF13_CONSTANT = 13;
	public final static int PF14_CONSTANT = 14;
	public final static int PF15_CONSTANT = 15;
	public final static int PF16_CONSTANT = 16;
	public final static int PF17_CONSTANT = 17;
	public final static int PF18_CONSTANT = 18;
	public final static int PF19_CONSTANT = 19;
	public final static int PF20_CONSTANT = 20;
	public final static int PF21_CONSTANT = 21;
	public final static int PF22_CONSTANT = 22;
	public final static int PF23_CONSTANT = 23;
	public final static int PF24_CONSTANT = 24;

	public final static EnumerationTypeBinding TYPE = new SystemEnumerationTypeBinding(SystemEnvironmentPackageNames.EGL_CORE, InternUtil.internCaseSensitive("PFKeyKind"), PFKEYKIND);
	public final static SystemEnumerationDataBinding PF1 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF1"), null, TYPE, PF1_CONSTANT);
	public final static SystemEnumerationDataBinding PF2 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF2"), null, TYPE, PF2_CONSTANT);
	public final static SystemEnumerationDataBinding PF3 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF3"), null, TYPE, PF3_CONSTANT);
	public final static SystemEnumerationDataBinding PF4 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF4"), null, TYPE, PF4_CONSTANT);
	public final static SystemEnumerationDataBinding PF5 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF5"), null, TYPE, PF5_CONSTANT);
	public final static SystemEnumerationDataBinding PF6 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF6"), null, TYPE, PF6_CONSTANT);
	public final static SystemEnumerationDataBinding PF7 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF7"), null, TYPE, PF7_CONSTANT);
	public final static SystemEnumerationDataBinding PF8 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF8"), null, TYPE, PF8_CONSTANT);
	public final static SystemEnumerationDataBinding PF9 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF9"), null, TYPE, PF9_CONSTANT);
	public final static SystemEnumerationDataBinding PF10 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF10"), null, TYPE, PF10_CONSTANT);
	public final static SystemEnumerationDataBinding PF11 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF11"), null, TYPE, PF11_CONSTANT);
	public final static SystemEnumerationDataBinding PF12 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF12"), null, TYPE, PF12_CONSTANT);
	public final static SystemEnumerationDataBinding PF13 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF13"), null, TYPE, PF13_CONSTANT);
	public final static SystemEnumerationDataBinding PF14 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF14"), null, TYPE, PF14_CONSTANT);
	public final static SystemEnumerationDataBinding PF15 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF15"), null, TYPE, PF15_CONSTANT);
	public final static SystemEnumerationDataBinding PF16 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF16"), null, TYPE, PF16_CONSTANT);
	public final static SystemEnumerationDataBinding PF17 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF17"), null, TYPE, PF17_CONSTANT);
	public final static SystemEnumerationDataBinding PF18 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF18"), null, TYPE, PF18_CONSTANT);
	public final static SystemEnumerationDataBinding PF19 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF19"), null, TYPE, PF19_CONSTANT);
	public final static SystemEnumerationDataBinding PF20 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF20"), null, TYPE, PF20_CONSTANT);
	public final static SystemEnumerationDataBinding PF21 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF21"), null, TYPE, PF21_CONSTANT);
	public final static SystemEnumerationDataBinding PF22 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF22"), null, TYPE, PF22_CONSTANT);
	public final static SystemEnumerationDataBinding PF23 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF23"), null, TYPE, PF23_CONSTANT);
	public final static SystemEnumerationDataBinding PF24 = new SystemEnumerationDataBinding(InternUtil.internCaseSensitive("PF24"), null, TYPE, PF24_CONSTANT);
	
	static {
		TYPE.setValid(true);
		TYPE.addEnumeration(PF1);
		TYPE.addEnumeration(PF2);
		TYPE.addEnumeration(PF3);
		TYPE.addEnumeration(PF4);
		TYPE.addEnumeration(PF5);
		TYPE.addEnumeration(PF6);
		TYPE.addEnumeration(PF7);
		TYPE.addEnumeration(PF8);
		TYPE.addEnumeration(PF9);
		TYPE.addEnumeration(PF10);
		TYPE.addEnumeration(PF11);
		TYPE.addEnumeration(PF12);
		TYPE.addEnumeration(PF13);
		TYPE.addEnumeration(PF14);
		TYPE.addEnumeration(PF15);
		TYPE.addEnumeration(PF16);
		TYPE.addEnumeration(PF17);
		TYPE.addEnumeration(PF18);
		TYPE.addEnumeration(PF19);
		TYPE.addEnumeration(PF20);
		TYPE.addEnumeration(PF21);
		TYPE.addEnumeration(PF22);
		TYPE.addEnumeration(PF23);
		TYPE.addEnumeration(PF24);
		
		TYPE.setCommaListComparator(new Comparator() {
			public int compare(Object o1, Object o2) {
				String s1 = (String) o1;
				String s2 = (String) o2;
				int int1 = Integer.parseInt(s1.substring(2));
				int int2 = Integer.parseInt(s2.substring(2));
				return new Integer(int1).compareTo(new Integer(int2));
			}
		});
	};
	
    public EnumerationTypeBinding getType() {
        return TYPE;
    }
    
    public boolean isResolvable() {
        return false;
    }
}
