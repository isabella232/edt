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
package org.eclipse.edt.ide.ui.templates.parts;

public class Record extends Part {
	private static String eol = System.getProperty("line.separator", "\n");
	
	public String getPartKind() {
		return "Record";
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		
		buff.append(getHeader());
		buff.append(eol);
		
		Field[] fields = getFields();
		
		for (int i = 0; i < fields.length; i++) {
			if (i > 0) {
				buff.append(eol);
			}
			buff.append("\t" + fields[i].toString() + ";");
		}
		
		buff.append(eol+"end");
		
		return buff.toString();
	}
}
