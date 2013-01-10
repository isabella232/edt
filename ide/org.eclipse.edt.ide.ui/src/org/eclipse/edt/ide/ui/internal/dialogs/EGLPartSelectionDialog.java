/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.dialogs;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Shell;


public class EGLPartSelectionDialog extends PartSelectionDialog {

    IFile initeglFile;
    EGLFileConfiguration fConfig;
    /**
     * @param parent
     * @param context
     * @param elementKinds
     * @param subType
     * @param scope
     */
    public EGLPartSelectionDialog(Shell parent,
            IRunnableContext context, int elemKind, String subType, IFile eglFile, EGLFileConfiguration fileConfig) {
        this(parent, context, elemKind, subType, eglFile, SearchEngine.createWorkspaceScope(), fileConfig);
    }

    public EGLPartSelectionDialog(Shell parent,
            IRunnableContext context, int elemKind, String subType, IFile eglFile, IEGLSearchScope searchScope, EGLFileConfiguration fileConfig) {
        super(parent, context, elemKind, subType, searchScope);
        this.initeglFile = eglFile;
        this.fConfig = fileConfig;
    }
	
	/*
	 * @see Dialog#okPressed
	 */
	protected void okPressed() {
	    //update the file configuration
	    updateConfig();
	    super.okPressed();
	}
	
	   /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#open()
     */
    public int open() {
        setFilter("*"); //$NON-NLS-1$
        return super.open();
    }
    
	private void updateConfig(){
		Object ref= getLowerSelectedElement();
		if (ref instanceof PartInfo) {
		    PartInfo partinfo = (PartInfo) ref;
		    String srcFolder = partinfo.getPackageFragmentRootPath().toOSString();
		    String pkgName = partinfo.getPackageName();
		    String partName = partinfo.getPartName();
			
		    fConfig.setContainerName(srcFolder);
		    fConfig.setFPackage(pkgName);
		    fConfig.setFileName(partName);
		}
	}	
	
    
}
