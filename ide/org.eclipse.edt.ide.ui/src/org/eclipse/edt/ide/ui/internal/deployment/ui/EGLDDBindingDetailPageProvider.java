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

import org.eclipse.edt.ide.ui.internal.deployment.EGLBinding;
import org.eclipse.edt.ide.ui.internal.deployment.NativeBinding;
import org.eclipse.edt.ide.ui.internal.deployment.RestBinding;
import org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding;
import org.eclipse.edt.ide.ui.internal.deployment.WebBinding;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;

public class EGLDDBindingDetailPageProvider implements IDetailsPageProvider {

	public Object getPageKey(Object object) {
		return object;
	}

	public IDetailsPage getPage(Object key) {
		if(key instanceof EGLBinding)
			return new EGLBindingDetailPage();
		else if(key instanceof NativeBinding)
			return new NativeBindingDetailPage();
		else if(key instanceof WebBinding)
			return new WebBindingDetailPage();
		else if(key instanceof RestBinding)
			return new RestBindingDetailPage();
		else if(key instanceof SQLDatabaseBinding)
			return new SQLDatabaseBindingDetailPage();
		return null;
	}
	
}
