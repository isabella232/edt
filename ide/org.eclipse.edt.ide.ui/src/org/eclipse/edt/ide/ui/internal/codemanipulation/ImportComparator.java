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
package org.eclipse.edt.ide.ui.internal.codemanipulation;

import java.util.Comparator;

import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

import com.ibm.icu.util.StringTokenizer;

public class ImportComparator implements Comparator {

	String[] order;
	
	public ImportComparator() {
		super();
		
		//get the package ordering group from preferences
		IPreferenceStore store = EDTUIPreferenceConstants.getPreferenceStore();
		String orderString = store.getString(EDTUIPreferenceConstants.ORGIMPORTS_IMPORTORDER);
		
		order = unpackList(orderString, ";"); //$NON-NLS-1$
	}

	//o1 and o2 are String object, each represents the package value
	//i.e. import pkg1.pkg2.partA;   o1 would be "pkg1.pkg2"
	public int compare(Object o1, Object o2) {
		String s1 = (String)o1;
		String s2 = (String)o2;
		
		//each one in the order list is a group
		int g1 = getGroupNumber(s1);
		int g2 = getGroupNumber(s2);
		
		if(g1 == g2)
			return s1.compareTo(s2);
		else
			return g1-g2;		//positive number if o1 > o2		
	}
	
	private int getGroupNumber(String s)
	{
		int len = order.length;
		int grpNum = -1;		
		for(int i=0; i<len; i++)
		{
			// If a match is found for the first time, save the matching index.
			// Otherwise, save the index of the match with the longer length.
			if(s.startsWith(order[i])) { //$NON-NLS-1$
				if(grpNum == -1) {
					grpNum = i;
				} else if (order[i].length() > order[grpNum].length()) {
					grpNum = i;
				}
			}
		}
		// If no match found, it will be at the end.
		if (grpNum == -1) { 
			grpNum = len;  
		}
		return grpNum;	
	}

	private static String[] unpackList(String str, String separator) {
		StringTokenizer tok= new StringTokenizer(str, separator); //$NON-NLS-1$
		int nTokens= tok.countTokens();
		String[] res= new String[nTokens];
		for (int i= 0; i < nTokens; i++) {
			res[i]= tok.nextToken().trim();
		}
		return res;
	}
	
}
