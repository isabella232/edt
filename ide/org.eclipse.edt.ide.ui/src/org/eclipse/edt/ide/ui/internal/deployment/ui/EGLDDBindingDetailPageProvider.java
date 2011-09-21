/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

public class EGLDDBindingDetailPageProvider implements IDetailsPageProvider {

	public Object getPageKey(Object object) {
		return object;
	}

	public IDetailsPage getPage(Object key) {
		if (key instanceof Binding) {
			String type = ((Binding)key).getType();
			if (org.eclipse.edt.javart.resources.egldd.Binding.BINDING_SERVICE_REST.equals(type)) {
				return new RestBindingDetailPage();
			}
			else if (org.eclipse.edt.javart.resources.egldd.Binding.BINDING_DB_SQL.equals(type)) {
				return new SQLDatabaseBindingDetailPage();
			}
		}
		
		return null;
	}
	
}
