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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.FixedPrecisionType;

public class FixedPrecisionTypeTemplate extends JavaScriptTemplate {

	public void genTypeDependentOptions(FixedPrecisionType type, Context ctx, TabbedWriter out) {
		out.print(", ");
		out.print(type.getDecimals());
		out.print(", ");
		out.print(decimalLimit(type.getDecimals(), type.getLength()));
	}

	/**
	 * Returns a value for the limit parameter to the convertToDecimal methods. The limit is the largest positive value that
	 * can be assigned to a variable of the given type.
	 */
	protected String decimalLimit(int decimals, int length) {
		if (length > 32) {
			String limit = "";
			for (int len = length; len > 0; len--) {
				limit += "9";
			}
			if (decimals > 0)
				limit = limit.substring(0, length - decimals) + '.' + limit.substring(length - decimals);
			return "new egl.javascript.BigDecimal(\"" + limit + "\")";
		} else {
			String limit = "egl.javascript.BigDecimal.prototype.NINES[" + (length - 1) + "]";
			if (decimals > 0)
				limit += ".movePointLeft(" + decimals + ")";
			return limit;
		}
	}
}
