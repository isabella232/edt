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
package org.eclipse.edt.ide.core.internal.errors;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class ErrorMarkerCollector {
	public static final ErrorMarkerCollector instance = new ErrorMarkerCollector();

	private ErrorMarkerCollector() {
		super();
	}

	private static class ErrorMarkerInfo {
		int startOffset;
		int endOffset;
		int line;
		String message;

		public ErrorMarkerInfo(int startOffset, int endOffset, int line, String message) {
			this.startOffset = startOffset;
			this.endOffset = endOffset;
			this.line = line;
			this.message = message;
		}
	}

	private ArrayList markerList;
	
	public int getNumErrors() {
		return markerList.size();
	}

	public void reset() {
		markerList = new ArrayList();
	}

	public void add(int startOffset, int endOffset, int line, String message) {
		markerList.add(new ErrorMarkerInfo(startOffset, endOffset, line, message));
	}

	public void createMarkers(IFile file) {
		try {
			if (markerList.size()>0)
	  		file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);

			for (Iterator iter = markerList.iterator(); iter.hasNext();) {
				ErrorMarkerInfo info = (ErrorMarkerInfo) iter.next();
				IMarker marker = file.createMarker(IMarker.PROBLEM);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute(IMarker.MESSAGE, info.message.trim());
				marker.setAttribute(IMarker.CHAR_START, info.startOffset);
				marker.setAttribute(IMarker.CHAR_END, info.endOffset);
				
				// Add a location so that we can sort the column by
				String OFFSET = "offset 0000";
				marker.setAttribute(IMarker.LOCATION, OFFSET.substring(0, 11 - Integer.toString(info.startOffset).length()) + info.startOffset);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
