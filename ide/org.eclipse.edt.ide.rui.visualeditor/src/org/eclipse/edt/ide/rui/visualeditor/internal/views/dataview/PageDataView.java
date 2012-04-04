/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.views.ViewsPlugin;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.ContentOutline;

public class PageDataView extends ContentOutline{
	private DefaultPage defaultPage;
	
	protected IPage createDefaultPage(PageBook book) {
		defaultPage = new DefaultPage();
		initPage(defaultPage);
		defaultPage.createControl(book);
		return defaultPage;
    }
	
	public DefaultPage getDefaultPage(){
		return this.defaultPage;
	}
	
	protected PageRec doCreatePage(IWorkbenchPart part) {
		Object object = ViewsPlugin.getAdapter(part, IPageDataViewPage.class, false);
		if (object instanceof IPageDataViewPage) {
			IPageDataViewPage pageDataViewPage = (IPageDataViewPage) object;
			if (pageDataViewPage instanceof IPageBookViewPage) {
				initPage((IPageBookViewPage) pageDataViewPage);
			}
			pageDataViewPage.createControl(getPageBook());
			pageDataViewPage.setPageDataView(this);
			return new PageRec(part, pageDataViewPage);
		}
		return null;
	}

	protected void doDestroyPage(IWorkbenchPart part, PageRec rec) {
		IPageDataViewPage pageDataViewPage = (IPageDataViewPage) rec.page;
		pageDataViewPage.dispose();
		rec.dispose();
	}
}
