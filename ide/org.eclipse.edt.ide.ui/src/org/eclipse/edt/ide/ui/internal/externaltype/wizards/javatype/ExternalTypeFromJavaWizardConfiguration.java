/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.externaltype.wizards.javatype;

import java.util.HashMap;
import java.util.Map;

public class ExternalTypeFromJavaWizardConfiguration {
	private Class<?> selectedClazz;
	private Map<Class<?>,JavaType> toBeGenerated;
	
	boolean isAllSuperTypesGenerated;
	boolean isAllReferencedTypesGenerated;
	
	public ExternalTypeFromJavaWizardConfiguration() {
		setDefaultValues();
	}
	
	protected void setDefaultValues() {
		selectedClazz = null;
		toBeGenerated = new HashMap<Class<?>,JavaType>(20);
		
		isAllSuperTypesGenerated = true;
		isAllReferencedTypesGenerated = true;
	}

	public Class<?> getSelectedClazz() {
		return selectedClazz;
	}

	public void setSelectedClazz(Class<?> selectedClazz) {
		this.selectedClazz = selectedClazz;
	}

	public Map<Class<?>, JavaType> getToBeGenerated() {
		return toBeGenerated;
	}

	public void setToBeGenerated(Map<Class<?>, JavaType> toBeGenerated) {
		this.toBeGenerated = toBeGenerated;
	}
	
	public boolean isAllSuperTypesGenerated() {
		return isAllSuperTypesGenerated;
	}

	public void setAllSuperTypesGenerated(boolean isAllSuperTypesGenerated) {
		this.isAllSuperTypesGenerated = isAllSuperTypesGenerated;
	}
	
	public boolean isAllReferencedTypesGenerated() {
		return isAllReferencedTypesGenerated;
	}

	public void setAllReferencedTypesGenerated(boolean isAllReferencedTypesGenerated) {
		this.isAllReferencedTypesGenerated = isAllReferencedTypesGenerated;
	}

}
