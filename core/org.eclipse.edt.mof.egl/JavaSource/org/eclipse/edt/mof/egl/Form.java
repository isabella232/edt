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
package org.eclipse.edt.mof.egl;

import java.util.List;

public interface Form extends Record {
	FormGroup getContainer();
	
	void setContainer(FormGroup value);
	
	List<FormField> getFormFields();
	
	
	public List<ConstantFormField> getConstantFields();
	
	public List<VariableFormField> getVariableFields();
}
