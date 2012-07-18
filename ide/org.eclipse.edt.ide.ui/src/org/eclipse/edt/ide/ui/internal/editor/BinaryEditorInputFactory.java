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
package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.edt.ide.core.utils.BinaryReadOnlyFile;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class BinaryEditorInputFactory implements IElementFactory {

	private static final String ID_FACTORY = "org.eclipse.edt.ide.ui.BinaryEditorInputFactory"; //$NON-NLS-1$
	private static String TAG_PATH_EGLAR = "eglarpath";
	private static String TAG_PATH_SOURCE = "sourcepath";
	private static String TAG_PATH_IRNAME = "irname";
	private static String TAG_PATH_PRJ = "prjname";
	
	@Override
	public IAdaptable createElement(IMemento memento) {
		String eglarPath = memento.getString(TAG_PATH_EGLAR);
		String sourcePath = memento.getString(TAG_PATH_SOURCE);
		String irName = memento.getString(TAG_PATH_IRNAME);
		String prjName = memento.getString(TAG_PATH_PRJ);
		IProject prj = ResourcesPlugin.getWorkspace().getRoot().getProject(prjName);
		if(null != prj){
			return(new BinaryEditorInput(new BinaryReadOnlyFile(eglarPath, sourcePath, irName, prj, false)));
		}

		return null;
	}

	
    /**
     * Returns the element factory id for this class.
     * 
     * @return the element factory id
     */
    public static String getFactoryId() {
        return ID_FACTORY;
    }

    /**
     * Saves the state of the given file editor input into the given memento.
     *
     * @param memento the storage area for element state
     * @param input the file editor input
     */
    public static void saveState(IMemento memento, BinaryEditorInput input) {
    	BinaryReadOnlyFile file = input.getBinaryReadOnlyFile();
        memento.putString(TAG_PATH_EGLAR,file.getEglarPathStr());
        memento.putString(TAG_PATH_SOURCE,file.getEglarSourcePath());
        memento.putString(TAG_PATH_IRNAME,file.getIrName());
        memento.putString(TAG_PATH_PRJ, file.getProject().getName());
    }
}
