/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.IntervalType;

public class IntervalTypeTemplate extends JavaTemplate {

	public void genConstructorOptions(IntervalType type, Context ctx, TabbedWriter out) {
		// we need to skip over the 1st comma and space
		generateOptions(type, ctx, out, false);
	}

	public void genTypeDependentOptions(IntervalType type, Context ctx, TabbedWriter out) {
		out.print(", ");
		generateOptions(type, ctx, out, true);
	}

	protected void generateOptions(IntervalType type, Context ctx, TabbedWriter out, boolean needSeparator) {
		// default to month interval
		String pattern = "yyyymm";
		if (type.getPattern() != null && !type.getPattern().equalsIgnoreCase("null"))
			pattern = type.getPattern().toLowerCase();
		// we aren't sure whether this is a month or second interval, so we need to interogate the pattern to see. we look
		// for specific seconds interval pattern characters (ignore m, as it could be month)
		boolean monthInterval = true;
		int patternIndex = 0;
		String matches = "dhsf";
		for (int matchesIndex = 0; matchesIndex < matches.length(); matchesIndex++) {
			while (patternIndex < pattern.length() && pattern.charAt(patternIndex) == matches.charAt(matchesIndex)) {
				monthInterval = false;
				patternIndex++;
			}
		}
		if (monthInterval)
			matches = "ym";
		else
			matches = "dhmsf";
		// generate the dependent options
		patternIndex = 0;
		for (int matchesIndex = 0; matchesIndex < matches.length(); matchesIndex++) {
			int count = 0;
			while (patternIndex < pattern.length() && pattern.charAt(patternIndex) == matches.charAt(matchesIndex)) {
				count++;
				patternIndex++;
			}
			if (needSeparator)
				out.print(", ");
			needSeparator = true;
			out.print("" + count);
		}
	}
}
