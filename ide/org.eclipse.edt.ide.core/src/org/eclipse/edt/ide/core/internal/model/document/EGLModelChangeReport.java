/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author winghong
 */
public class EGLModelChangeReport {

	private List events;
	
	public void add(EGLModelChangeEvent event) {
		if(events == null) {
			events = new ArrayList();
		}
		
		events.add(event);
	}
	
	public int getSize() {
		return events == null ? 0 : events.size(); 
	}
	
	public Iterator iterator() {
		return events.iterator();
	}
	
	public String toString() {
		if(events == null) return "";
		
		StringBuffer buffer = new StringBuffer();
		for (Iterator iter = events.iterator(); iter.hasNext();) {
			buffer.append(iter.next().toString());
			buffer.append((char) Character.LINE_SEPARATOR);
		}

		return buffer.toString();
	}
}
