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
package org.eclipse.edt.ide.core.internal.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.lookup.FileInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;

import com.ibm.icu.text.NumberFormat;

public abstract class AbstractMarkerProblemRequestor extends DefaultProblemRequestor {

	public static final String PROBLEM = EDTCoreIDEPlugin.PLUGIN_ID + ".problem"; //$NON-NLS-1$
	public static final String BUILD_PROBLEM = EDTCoreIDEPlugin.PLUGIN_ID + ".buildProblem"; //$NON-NLS-1$
	public static final String CONTEXT_SPECIFIC_PROBLEM = EDTCoreIDEPlugin.PLUGIN_ID + ".contextSpecificProblem"; //$NON-NLS-1$
	public static final String FILE_PROBLEM = EDTCoreIDEPlugin.PLUGIN_ID + ".fileProblem"; //$NON-NLS-1$
    public static final String PARSER_PROBLEM = EDTCoreIDEPlugin.PLUGIN_ID + ".syntaxProblem"; //$NON-NLS-1$
    public static final String UNSUPPORTED_SYNTAX_PROBLEM = EDTCoreIDEPlugin.PLUGIN_ID + ".unsupportedSyntaxProblem"; //$NON-NLS-1$
		
	public static final String PART_NAME = "partName"; 		//$NON-NLS-1$
	public static final String EGL_PROBLEM = "eglProblem"; 	//$NON-NLS-1$
	protected IFile file;
    private String errorMsgCode;
    
    public AbstractMarkerProblemRequestor(IFile file, String errorMsgCode) {
        super();
        this.file = file;
        this.errorMsgCode = errorMsgCode;
    }

	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) {
 		if (severity == IMarker.SEVERITY_ERROR) {
 			setHasError(true);
 		}
    	try {
			createMarker(startOffset, endOffset, severity, problemKind, inserts);
		} catch (CoreException e) {
			throw new BuildException(e);
		}
    }
    
    protected IMarker createMarker(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) throws CoreException {
    	return createMarker(startOffset, endOffset, getLineNumberOfOffset(startOffset), severity, problemKind, inserts);
    }

	protected IMarker createMarker(int startOffset, int endOffset, int lineNumber, int severity, int problemKind, String[] inserts) throws CoreException {
		NumberFormat numberFormat = NumberFormat.getIntegerInstance();
		numberFormat.setMaximumFractionDigits(0);
		numberFormat.setMinimumIntegerDigits(3);
		
		IMarker marker = file.createMarker(getMarkerType(problemKind));
		if(problemKind != -1) {
            marker.setAttribute(IMarker.MESSAGE, getErrorMessageText(problemKind, startOffset, lineNumber, severity, getMessageFromBundle(problemKind, inserts)));
        }
        else {
            marker.setAttribute(IMarker.MESSAGE, getErrorMessageText(problemKind, startOffset, lineNumber, severity, inserts[0]));
        }
	    marker.setAttribute(IMarker.SEVERITY, translateSeverity(severity));
	    marker.setAttribute(IMarker.CHAR_START, startOffset);
	    marker.setAttribute(IMarker.CHAR_END, endOffset);
	    
	    marker.setAttribute(IMarker.LINE_NUMBER, lineNumber+1);
	    marker.setAttribute(EGL_PROBLEM, problemKind);
	    
		return marker;
	}
	
	private int translateSeverity(int severity) {
        switch(severity){
        	case org.eclipse.edt.compiler.internal.core.builder.IMarker.SEVERITY_ERROR: return IMarker.SEVERITY_ERROR;
        	case org.eclipse.edt.compiler.internal.core.builder.IMarker.SEVERITY_WARNING: return IMarker.SEVERITY_WARNING;
        	case org.eclipse.edt.compiler.internal.core.builder.IMarker.SEVERITY_INFO: return IMarker.SEVERITY_INFO;
        	default: return IMarker.SEVERITY_ERROR;
        }
    }
	
	private String getErrorMessageText(int problemKind, int startOffset, int lineNumber, int severity, String msgText) {
		StringBuffer result = new StringBuffer();
		result.append(getMessagePrefix(problemKind, startOffset, lineNumber, severity));
		result.append(" ");
		result.append(msgText);
		return result.toString();
	}
	
	private String getMessagePrefix(int problemKind, int startOffset, int lineNumber, int severity) {
		StringBuffer prefix = new StringBuffer();
		prefix.append("IWN.");
		prefix.append(errorMsgCode);
		if(problemKind != -1) {
			prefix.append(".");
			prefix.append(Integer.toString(problemKind));
		}
		if(IMarker.SEVERITY_ERROR == severity) {
			prefix.append(".e");
		}
		else if(IMarker.SEVERITY_WARNING == severity) {
			prefix.append(".w");
		}
		else if(IMarker.SEVERITY_INFO == severity) {
			prefix.append(".i");
		}
		prefix.append(" ");
		
		prefix.append(Integer.toString(lineNumber+1));
		
		prefix.append("/");
		
		int offsetOnLine = startOffset - getLineOffset(lineNumber);
		
		prefix.append(Integer.toString(offsetOnLine+1));
		
		return prefix.toString();
	}
	
	protected int getLineOffset(int lineNumber){
		IFileInfo fileInfo = FileInfoManager.getInstance().getFileInfo(file.getProject(), file.getProjectRelativePath());
		
		return fileInfo.getOffsetForLine(lineNumber);
	}
	
	protected int getLineNumberOfOffset(int offset) {
		
		IFileInfo fileInfo = FileInfoManager.getInstance().getFileInfo(file.getProject(), file.getProjectRelativePath());
		
		return fileInfo.getLineNumberForOffset(offset);
	}

	protected abstract String getMarkerType(int problemKind);
	/**
	 * @return A list of all of the marker types that this class could concievably create.
	 */
	protected abstract String[] getMarkerTypes();
	
	protected void removeMarkers(){
		
		try{
			String[] markerTypes = getMarkerTypes();
			for(int i = 0; i < markerTypes.length; i++) {
				IMarker[] markers = file.findMarkers(markerTypes[i], false, IResource.DEPTH_INFINITE);
				for (int j = 0; j < markers.length; j++){
					IMarker marker = markers[j];
					if(shouldRemoveMarker(marker)){
						marker.delete();
					}
				}
			}
		}catch(CoreException e){
			throw new BuildException(e);
		}
	}
	
	protected abstract boolean shouldRemoveMarker(IMarker marker);
}
