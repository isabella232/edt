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

import java.text.MessageFormat;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.search.IEGLSearchResultCollector;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.dialogs.OptionalMessageDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.search.ui.ISearchResultView;
import org.eclipse.search.ui.SearchUI;
import org.eclipse.swt.widgets.Shell;


public class EGLSearchResultCollector implements IEGLSearchResultCollector {

	private static final String MATCH= EGLSearchMessages.SearchResultCollectorMatch;
	private static final String MATCHES= EGLSearchMessages.SearchResultCollectorMatches;
	private static final String DONE= EGLSearchMessages.SearchResultCollectorDone;
	private static final Boolean POTENTIAL_MATCH_VALUE= new Boolean(true);
	private static final String POTENTIAL_MATCH_DIALOG_ID= "Search.PotentialMatchDialog"; //$NON-NLS-1$
	
	private IProgressMonitor fMonitor;
	private ISearchResultView fView;
	private int fMatchCount= 0;
	private int fPotentialMatchCount= 0;
	private long fLastUpdateTime;
	private Integer[] fMessageFormatArgs= new Integer[1];
	
	public EGLSearchResultCollector() {
		// This ensures that the image class is loaded correctly
		EDTUIPlugin.getDefault().getImageRegistry();
	}

	/**
	 * @see IEGLSearchResultCollector#aboutToStart().
	 */
	public void aboutToStart() {
	}
	
	/**
	 * @see IEGLSearchResultCollector#accept
	 */
	public void accept(IResource resource, int start, int end, IEGLElement enclosingElement, int accuracy) throws CoreException {
		if (accuracy == POTENTIAL_MATCH && SearchUI.arePotentialMatchesIgnored())
			return;

//TODO search		
//		IMarker marker= resource.createMarker(SearchUI.SEARCH_MARKER);
//		HashMap attributes;
//		Object groupKey= enclosingElement;
//		attributes= new HashMap(7);
//		if (accuracy == POTENTIAL_MATCH) {
//			fPotentialMatchCount++;
//			attributes.put(SearchUI.POTENTIAL_MATCH, POTENTIAL_MATCH_VALUE);
//			if (groupKey == null)
//				groupKey= "?:null"; //$NON-NLS-1$
//			else
//				groupKey= "?:" + enclosingElement.getHandleIdentifier(); //$NON-NLS-1$
//		}			
//		IEGLFile cu= SearchUtil.findEGLFile(enclosingElement);
//		if (cu != null && cu.isWorkingCopy())
//			attributes.put(IEGLSearchUIConstants.ATT_IS_WORKING_COPY, new Boolean(true)); //$NON-NLS-1$
//			
//		EGLCore.addEGLElementMarkerAttributes(attributes, enclosingElement);
//		attributes.put(IEGLSearchUIConstants.ATT_GE_HANDLE_ID, enclosingElement.getHandleIdentifier());
//		attributes.put(IMarker.CHAR_START, new Integer(Math.max(start, 0)));
//		attributes.put(IMarker.CHAR_END, new Integer(Math.max(end, 0)));
//		attributes.put(IWorkbenchPage.EDITOR_ID_ATTR, EGLUI.ID_CU_EDITOR);
//		marker.setAttributes(attributes);
//
//		fView.addMatch(enclosingElement.getElementName(), groupKey, resource, marker);
//		fMatchCount++;
//		if (!getProgressMonitor().isCanceled() && System.currentTimeMillis() - fLastUpdateTime > 1000) {
//			getProgressMonitor().subTask(getFormattedMatchesString(fMatchCount));
//			fLastUpdateTime= System.currentTimeMillis();
//		}
	}
	
	/**
	 * @see IEGLSearchResultCollector#done().
	 */
	public void done() {
		if (!getProgressMonitor().isCanceled()) {
			String matchesString= getFormattedMatchesString(fMatchCount);
			getProgressMonitor().setTaskName(MessageFormat.format(DONE, new String[]{matchesString}));
		}

		if (fView != null) {
			if (fPotentialMatchCount > 0)
				explainPotentialMatch(fPotentialMatchCount);
			fView.searchFinished();
		}

		// Cut no longer unused references because the collector might be re-used
		fView= null;
		fMonitor= null;
	}

	private void explainPotentialMatch(final int potentialMatchCount) {
		// Make sure we are doing it in the right thread.
		final Shell shell= fView.getSite().getShell();
		final String title;
		if (potentialMatchCount == 1)
			title= new String(EGLSearchMessages.SearchPotentialMatchDialogTitleFoundPotentialMatch);
		else
			title= new String(EGLSearchMessages.bind(EGLSearchMessages.SearchPotentialMatchDialogTitleFoundPotentialMatch, "" + potentialMatchCount)); //$NON-NLS-1$ //$NON-NLS-2$
		
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				OptionalMessageDialog.open(
					POTENTIAL_MATCH_DIALOG_ID, //$NON-NLS-1$,
					shell,
					title,
					null,
					EGLSearchMessages.SearchPotentialMatchDialogMessage,
					OptionalMessageDialog.INFORMATION,
					new String[] { IDialogConstants.OK_LABEL },
					0);
			}
		});
	}
	
	/*
	 * @see IEGLSearchResultCollector#getProgressMonitor().
	 */
	public IProgressMonitor getProgressMonitor() {
		return fMonitor;
	};
	
	void setProgressMonitor(IProgressMonitor pm) {
		fMonitor= pm;
	}	
	
	private String getFormattedMatchesString(int count) {
		if (fMatchCount == 1)
			return MATCH;
		fMessageFormatArgs[0]= new Integer(count);
		return MessageFormat.format(MATCHES, fMessageFormatArgs);

	}

	public void accept(IEGLElement element, int start, int end,
			IResource resource, int accuracy) throws CoreException {
		// TODO Auto-generated method stub
		
	}
}
