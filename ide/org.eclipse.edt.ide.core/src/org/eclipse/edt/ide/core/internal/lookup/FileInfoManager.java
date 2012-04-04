/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup;


/**
 * @author svihovec
 * 
 * The FileInfoManager maintains an LRU cache of recently created/loaded FileInfos.  This LRU cache is cleared at the start of each build by a resource change listener, 
 * and it is assumed that all new FileInfos are created and saved before saved versions of the file infos are requested.
 *
 */
public class FileInfoManager extends AbstractFileInfoManager {

	protected static final String SAVED_FILE_INFO_FOLDER = "fileinfo"; //$NON-NLS-1$
	protected static final String SAVED_FILE_INFO_FILE_EXTENSION = "fi"; //$NON-NLS-1$
	
	private static FileInfoManager INSTANCE = new FileInfoManager();
	 
	private FileInfoManager(){
		super(SAVED_FILE_INFO_FOLDER, SAVED_FILE_INFO_FILE_EXTENSION);
	}
	
    public static FileInfoManager getInstance() {
        return INSTANCE;
    }
}
