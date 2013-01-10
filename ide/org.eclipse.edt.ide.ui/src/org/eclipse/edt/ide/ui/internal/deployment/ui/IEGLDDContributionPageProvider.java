/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

public interface IEGLDDContributionPageProvider {
	public EGLDDBaseFormPage createPage(FormEditor editor);
	public Table createTable( Composite client, FormToolkit toolkit );
	public IContentProvider getTableContentProvider( IProject project );
	public ILabelProvider getTableLabelProvider();
	public String getOverviewTitle();
	public String getOverviewDescription();
	public String getTargetReferenceTitle();
	public String getTargetReferenceDescription();
	public String getDetailPageId();
	public String getHelpId();
}
