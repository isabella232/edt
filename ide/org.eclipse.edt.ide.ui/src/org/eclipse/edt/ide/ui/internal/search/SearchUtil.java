/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.search;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.core.internal.model.util.EGLModelUtil;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.search.ui.ISearchResultViewEntry;
import org.eclipse.ui.IWorkingSet;

/**
 * This class contains some utility methods for EGL Search.
 */
public class SearchUtil extends EGLModelUtil {

	// LRU working sets
	public static int LRU_WORKINGSET_LIST_SIZE= 3;
//	private static LRUWorkingSetsList fgLRUWorkingSets;

	// Settings store
//	private static final String DIALOG_SETTINGS_KEY= "EGLElementSearchActions"; //$NON-NLS-1$
//	private static final String STORE_LRU_WORKING_SET_NAMES= "lastUsedWorkingSetNames"; //$NON-NLS-1$
	
//	private static IDialogSettings fgSettingsStore;


	public static IEGLElement getEGLElement(IMarker marker) {
		if (marker == null || !marker.exists())
			return null;
		try {
			String handleId= (String)marker.getAttribute(IEGLSearchUIConstants.ATT_GE_HANDLE_ID);
			IEGLElement egle = EGLCore.create(handleId);
			if (egle == null)
				return null;

			if (!marker.getAttribute(IEGLSearchUIConstants.ATT_IS_WORKING_COPY, false)) {
				if (egle != null && egle.exists())
					return egle;
			}

			IEGLFile cu= findEGLFile(egle);
			if (cu == null || !cu.exists()) {
				cu= (IEGLFile)EGLCore.create(marker.getResource());
			}

			// Find working copy element
			IWorkingCopy[] workingCopies= EGLUI.getSharedWorkingCopiesOnEGLPath();
			int i= 0;
			while (i < workingCopies.length) {
				if (workingCopies[i].getOriginalElement().equals(cu)) {
					egle= findInWorkingCopy(workingCopies[i], egle, true);
					break;
				}
				i++;
			}
			if (egle != null && !egle.exists()) {
				IEGLElement[] eglElements= cu.findElements(egle);
				if (eglElements == null || eglElements.length == 0)
				egle= cu.getElementAt(marker.getAttribute(IMarker.CHAR_START, 0));
				else
				egle= eglElements[0];
			}
			return egle;
		} catch (CoreException ex) {
			EGLLogger.log(EGLSearchMessages.SearchErrorCreateEGLElementTitle, EGLSearchMessages.SearchErrorCreateEGLElementMessage);
			return null;
		}
	}

	public static IEGLElement getEGLElement(Object entry) {
		if (entry != null && isISearchResultViewEntry(entry))
			return getEGLElement((ISearchResultViewEntry)entry);
		return null;
	}

	public static IResource getResource(Object entry) {
		if (entry != null && isISearchResultViewEntry(entry))
			return ((ISearchResultViewEntry)entry).getResource();
		return null;
	}

	public static IEGLElement getEGLElement(ISearchResultViewEntry entry) {
		if (entry != null)
			return getEGLElement(entry.getSelectedMarker());
		return null;
	}

	public static boolean isSearchPlugInActivated() {
		return Platform.getPluginRegistry().getPluginDescriptor("org.eclipse.search").isPluginActivated(); //$NON-NLS-1$
	}

	public static boolean isISearchResultViewEntry(Object object) {
		return object != null && isSearchPlugInActivated() && (object instanceof ISearchResultViewEntry);
	}
	
	/** 
	 * Returns the working copy of the given java element.
	 * @param eglElement the eglElement for which the working copyshould be found
	 * @param reconcile indicates whether the working copy must be reconcile prior to searching it
	 * @return the working copy of the given element or <code>null</code> if none
	 */	
	private static IEGLElement findInWorkingCopy(IWorkingCopy workingCopy, IEGLElement element, boolean reconcile) throws EGLModelException {
		if (workingCopy != null) {
			if (reconcile) {
				synchronized (workingCopy) {
					workingCopy.reconcile();
					return SearchUtil.findInEGLFile((IEGLFile)workingCopy, element);
				}
			} else {
				return SearchUtil.findInEGLFile((IEGLFile)workingCopy, element);
			}
		}
		return null;
	}

	/**
	 * Returns the compilation unit for the given java element.
	 * 
	 * @param	element the java element whose compilation unit is searched for
	 * @return	the compilation unit of the given java element
	 */
	static IEGLFile findEGLFile(IEGLElement element) {
		if (element == null)
			return null;

		if (element.getElementType() == IEGLElement.EGL_FILE)
			return (IEGLFile)element;
			
		if (element instanceof IMember)
			return ((IMember)element).getEGLFile();

		return findEGLFile(element.getParent());
	}

	/**
	 * @param workingSets
	 * @return
	 */
	public static Object toString(IWorkingSet[] workingSets) {
		if( workingSets != null & workingSets.length > 0 ){
			String string = new String();
			for( int i = 0; i < workingSets.length; i++ ){
				if( i > 0 )
					string += ", ";  //$NON-NLS-1$
				string += workingSets[i].getName();
			}
			
			return string;
		}
		
		return null;
	}


//	public static String toString(IWorkingSet[] workingSets) {
//		Arrays.sort(workingSets, new WorkingSetComparator());
//		String result= ""; //$NON-NLS-1$
//		if (workingSets != null && workingSets.length > 0) {
//			boolean firstFound= false;
//			for (int i= 0; i < workingSets.length; i++) {
//				String workingSetName= workingSets[i].getName();
//				if (firstFound)
//					result= SearchMessages.getFormattedString("SearchUtil.workingSetConcatenation", new String[] {result, workingSetName}); //$NON-NLS-1$
//				else {
//					result= workingSetName;
//					firstFound= true;
//				}
//			}
//		}
//		return result;
//	}
//
//	// ---------- LRU working set handling ----------
//
//	/**
//	 * Updates the LRU list of working sets.
//	 * 
//	 * @param workingSets	the workings sets to be added to the LRU list
//	 */
//	public static void updateLRUWorkingSets(IWorkingSet[] workingSets) {
//		if (workingSets == null || workingSets.length < 1)
//			return;
//		
//		getLRUWorkingSets().add(workingSets);
//		saveState();
//	}
//
//	private static void saveState() {
//		IWorkingSet[] workingSets;
//		Iterator iter= fgLRUWorkingSets.iterator();
//		int i= 0;
//		while (iter.hasNext()) {
//			workingSets= (IWorkingSet[])iter.next();
//			String[] names= new String[workingSets.length];
//			for (int j= 0; j < workingSets.length; j++)
//				names[j]= workingSets[j].getName();
//			fgSettingsStore.put(STORE_LRU_WORKING_SET_NAMES + i, names);
//			i++;
//		}
//	}
//
//	public static LRUWorkingSetsList getLRUWorkingSets() {
//		if (fgLRUWorkingSets == null) {
//			restoreState();
//		}
//		return fgLRUWorkingSets;
//	}
//
//	static void restoreState() {
//		fgLRUWorkingSets= new LRUWorkingSetsList(LRU_WORKINGSET_LIST_SIZE);
//		fgSettingsStore= EGLPlugin.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS_KEY);
//		if (fgSettingsStore == null)
//			fgSettingsStore= EGLPlugin.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS_KEY);
//		
//		boolean foundLRU= false;
//		for (int i= LRU_WORKINGSET_LIST_SIZE - 1; i >= 0; i--) {
//			String[] lruWorkingSetNames= fgSettingsStore.getArray(STORE_LRU_WORKING_SET_NAMES + i);
//			if (lruWorkingSetNames != null) {
//				Set workingSets= new HashSet(2);
//				for (int j= 0; j < lruWorkingSetNames.length; j++) {
//					IWorkingSet workingSet= PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSet(lruWorkingSetNames[j]);
//					if (workingSet != null) {
//						workingSets.add(workingSet);
//					}
//				}
//				foundLRU= true;
//				if (!workingSets.isEmpty())
//					fgLRUWorkingSets.add((IWorkingSet[])workingSets.toArray(new IWorkingSet[workingSets.size()]));
//			}
//		}
//		if (!foundLRU)
//			// try old preference format
//			restoreFromOldFormat();
//	}
//
//	private static void restoreFromOldFormat() {
//		fgLRUWorkingSets= new LRUWorkingSetsList(LRU_WORKINGSET_LIST_SIZE);
//		fgSettingsStore= EGLPlugin.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS_KEY);
//		if (fgSettingsStore == null)
//			fgSettingsStore= EGLPlugin.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS_KEY);
//
//		boolean foundLRU= false;
//		String[] lruWorkingSetNames= fgSettingsStore.getArray(STORE_LRU_WORKING_SET_NAMES);
//		if (lruWorkingSetNames != null) {
//			for (int i= lruWorkingSetNames.length - 1; i >= 0; i--) {
//				IWorkingSet workingSet= PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSet(lruWorkingSetNames[i]);
//				if (workingSet != null) {
//					foundLRU= true;
//					fgLRUWorkingSets.add(new IWorkingSet[]{workingSet});
//				}
//			}
//		}
//		if (foundLRU)
//			// save in new format
//			saveState();
//	}

}
