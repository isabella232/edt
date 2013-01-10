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
package org.eclipse.edt.ide.ui.wizards;

import com.ibm.icu.util.StringTokenizer;

public class PartTemplateException extends InterruptedException {
	
	private static final long serialVersionUID = 1L;

	public PartTemplateException(String tException){
		super(tException);
	}
	
	public PartTemplateException(String pType, String pDescription, String tException){
		super(pType + ":" + pDescription + ":" + tException); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @return
	 */
	public String getPartDescription() {		
		StringTokenizer st = new StringTokenizer(this.getMessage(), ":", false); //$NON-NLS-1$
		st.nextToken();
		return st.nextToken();		
	}

	/**
	 * @return
	 */
	public String getPartType() {
		StringTokenizer st = new StringTokenizer(this.getMessage(), ":", false); //$NON-NLS-1$
		if(st.hasMoreTokens())
			return st.nextToken();
		else
			return ""; //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public String getTemplateExcpetion() {
		StringTokenizer st = new StringTokenizer(this.getMessage(), ":", false); //$NON-NLS-1$
		st.nextToken();
		st.nextToken();
		return st.nextToken();	
	}

}
