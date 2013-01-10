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
package org.eclipse.edt.ide.rui.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.internal.model.BinaryPart;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.internal.model.SourcePartElementInfo;
import org.eclipse.edt.ide.core.internal.model.SourcePropertyBlock;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfoRequestor;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.Signature;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.rui.document.utils.AssignmentLocator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class Util {

	public static final String RUIHANDLER = NameUtile.getAsName("RUIHandler"); //$NON-NLS-1$
	public static final String RUIWIDGET = NameUtile.getAsName("RUIWIdget"); //$NON-NLS-1$
	public static final String	WIDGET_ID_SEPARATOR	= "@@";
	
	public static boolean useCompression = true;
	public static String[] excludedCompression = new String[]{"com.ibm.egl.rui.dojo.runtime.local_1.6"};
	
	static {
		if("yes".equals(System.getProperty("EGL_RICH_UI_USE_COMPRESSION", "yes"))){
			useCompression = true;
		}else{
			useCompression = false;
		}
	}

	public static String combineWidgetId(String projectName, String packageName, String widgetType){
		return projectName + WIDGET_ID_SEPARATOR + packageName + WIDGET_ID_SEPARATOR + widgetType;
	}
	
	public static boolean isVESupportType(IFile file){
		return isVESupportType((IEGLFile)EGLCore.create(file), null);
	}
	
	public static boolean isVESupportType(IFile file, IEGLDocument currentDocument){
		return isVESupportType((IEGLFile)EGLCore.create(file), currentDocument);
	}
	
	public static boolean isRUIWidget(IFile file){
		return isRUIWidget((IEGLFile)EGLCore.create(file));
	}
	
	public static String getRUIWidgetId(IFile file){
		return getRUIWidgetId((IEGLFile)EGLCore.create(file));
	}
	
	public static boolean isVESupportType(IEGLFile file, IEGLDocument currentDocument){
		// Prevent NPE if this check is run on a non-EGL file.
		if (file == null) {
			return false;
		}
		
		try{
			IEGLFile sharedWorkingCopy = (IEGLFile)file.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			try{
				sharedWorkingCopy.reconcile(true, null);
				
				IPart part = sharedWorkingCopy.getPart(new Path(file.getUnderlyingResource().getName()).removeFileExtension().toString());
				
				if(part.exists() && ((SourcePart)part).isHandler()){
					SourcePartElementInfo partInfo = (SourcePartElementInfo) ((SourcePart)part).getElementInfo();
					if (partInfo.getSubTypeName() == null) {return false;}
					String typeName = NameUtile.getAsName(new String(partInfo.getSubTypeName()));
					if( NameUtile.equals(typeName, RUIHANDLER) ){
						return true;
					}
					if ( NameUtile.equals(typeName, RUIWIDGET) ) {
						IEGLElement[] children = partInfo.getChildren();
						SourcePropertyBlock spb = null;
						for ( int i = 0; i < children.length; i ++ ) {
							if ( children[i] instanceof SourcePropertyBlock && children[i].getElementName().equalsIgnoreCase( part.getElementName() )) {
								spb = (SourcePropertyBlock)children[i];
								IEGLElement[] propertyChildren = spb.getChildren();
								for ( int j = 0; j < propertyChildren.length; j ++ ) {
									if ( propertyChildren[j].getElementName().equalsIgnoreCase( "targetWidget" ) ) {
										return true;
									}
								}
							}
						}
						
						if ( currentDocument == null ) {
							return false;
						}

						if ( spb != null ) {
							final org.eclipse.edt.compiler.core.ast.File fileAST = currentDocument.getNewModelEGLFile();
							final Node perpertiesBlock = currentDocument.getNewModelNodeAtOffset(spb.getSourceRange().getOffset(), spb.getSourceRange().getLength());
							AssignmentLocator assignmentLocator = new AssignmentLocator(NameUtile.getAsName( "tagName" ));
							perpertiesBlock.accept(assignmentLocator);
							Assignment setting = assignmentLocator.getAssignment();
							if ( setting != null ) {
								return false;
							}
						}
					}
					return true;
				}
			}finally{
				sharedWorkingCopy.destroy();
			}
		}catch(EGLModelException e){
			return false;
		}
		
		return false;
	}
	
	public static boolean isRUIWidget(IEGLFile file){
		try{
			IEGLFile sharedWorkingCopy = (IEGLFile)file.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			try{
				sharedWorkingCopy.reconcile(true, null);
						
				IPart part = sharedWorkingCopy.getPart(new Path(file.getUnderlyingResource().getName()).removeFileExtension().toString());
				
				//TODO: Move this check to SourcePart::isRUIHandler
				if(part.exists() && ((SourcePart)part).isHandler()){
					SourcePartElementInfo partInfo = (SourcePartElementInfo) ((SourcePart)part).getElementInfo();
					if (partInfo.getSubTypeName() == null) {return false;}
					String typeName = NameUtile.getAsName(new String(partInfo.getSubTypeName()));
					return ( NameUtile.equals(typeName, RUIWIDGET) );
				}
			}finally{
				sharedWorkingCopy.destroy();
			}
		}catch(EGLModelException e){
			return false;
		}
		
		return false;
	}
	
	public static final String getRUIWidgetId(IEGLFile file){
		try{
			IEGLFile sharedWorkingCopy = (IEGLFile)file.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			try{
				sharedWorkingCopy.reconcile(true, null);
						
				IPart part = sharedWorkingCopy.getPart(new Path(file.getUnderlyingResource().getName()).removeFileExtension().toString());
				
				//TODO: Move this check to SourcePart::isRUIHandler
				if(part.exists() && ((SourcePart)part).isHandler()){
					String projectName = part.getEGLProject().getElementName();
					String packageName = part.getPackageFragment().getElementName();
					String widgetType = part.getElementName();
					String widgetId = combineWidgetId(projectName, packageName, widgetType);
					
					return widgetId;
				}
			}finally{
				sharedWorkingCopy.destroy();
			}
		}catch(EGLModelException e){
			return null;
		}
		
		return null;
	}

	
	/**
	 * Returns an array of .egl files that contain rui widgets in them contained within the passed project
	 * @param project
	 * @return
	 * @deprecated - This returns all ET's in the workspace, not just ones that extend ET Widget.  Do not use this if you 
	 * are not checking to see if the IR extends from ET Widget
	 */
	public static IPart[] searchForRUIWidgets(IProject project){
		Set result = new LinkedHashSet();
		try{
			ArrayList partsList = new ArrayList();			
			IWorkspaceRoot workspaceroot = ResourcesPlugin.getWorkspace().getRoot();
			
			IEGLProject eglProject = EGLCore.create(workspaceroot.getProject(project.getName()));
			
			IEGLSearchScope searchScope = org.eclipse.edt.ide.core.search.SearchEngine.createEGLSearchScope( new IEGLProject[]{eglProject}, true);
			PartInfoRequestor searchResult = new PartInfoRequestor(partsList);
			new org.eclipse.edt.ide.core.search.SearchEngine().searchAllPartNames(ResourcesPlugin.getWorkspace(), null, null,
					IIndexConstants.PATTERN_MATCH,
					IEGLSearchConstants.CASE_INSENSITIVE,
					IEGLSearchConstants.HANDLER | IEGLSearchConstants.EXTERNALTYPE, searchScope, searchResult,
					IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
	
			for (Iterator iter = partsList.iterator(); iter.hasNext();) {
				PartInfo partinfo = (PartInfo) iter.next();
				IPart part = (IPart) partinfo.resolvePart(searchScope);
				if (part != null && part.getEGLProject().getProject() == eglProject.getProject() && isRUIWidgetOrExternalType(part)) {
					result.add(part);
				}
			}
		}catch(EGLModelException e){
			
		}
		return (IPart[])result.toArray(new IPart[result.size()]);
	}
	
	private static boolean isRUIWidgetOrExternalType(IPart part) {
		if(part instanceof SourcePart) {
			if ( ((SourcePart)part).isHandler() && part.getSubTypeSignature() != null
					&& NameUtile.equals(RUIWIDGET, NameUtile.getAsName((Signature.toString(part.getSubTypeSignature())))) ) {
				return true;
			}
			return ((SourcePart)part).isExternalType();
		} else if(part instanceof BinaryPart) {
			if ( ((BinaryPart)part).isHandler() && part.getSubTypeSignature() != null
					&& NameUtile.equals(RUIWIDGET, NameUtile.getAsName((Signature.toString(part.getSubTypeSignature())))) ) {
				return true;
			}
			return ((BinaryPart)part).isExternalType();
		}
		return false;
	}
	
	/**
	 * Returns <code>true</code> if the passed project has a RUi nature applied to it
	 * 
	 * @param project The IProject to check
	 * @return
	 */
	public static boolean isRUIProject(IProject project) {
		return EGLProject.hasRUINature(project);
	}
	
	/**
	 * Returns <code>true</code> if the passed project has a CE nature applied to it
	 * 
	 * @param project The IProject to check
	 * @return
	 */
	public static boolean isCEProject(IProject project) {
		return EGLProject.hasCENature(project);
	}
    
	/**
	 * Try desperately to return a valid Shell (pulled out of thin air...). This
	 * is probably totally bogus.
	 * 
	 * @return Shell
	 */
	public static final Shell getShell() {

		Shell shell = null;

		Display display = getDisplay();
		if (display != null)
			shell = display.getActiveShell();

		if (shell == null) {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (workbenchWindow != null)
				shell = workbenchWindow.getShell();
		}

		return shell;
	}
	
	/**
	 * Try desperately to return a valid Display. Return null if we fail.
	 * 
	 * @return Display
	 */
	public static final Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}
	
	public static String[] combineArray( String[] array1, String[] array2 ) {
		String[] result = new String[ array1.length + array2.length ];
		System.arraycopy( array1, 0, result, 0, array1.length );
		System.arraycopy( array2, 0, result, array1.length, array2.length );
		
		return result;
	}
	
	private static boolean isExcludedCompression( String fileName ) {
		for ( int i = 0; i < excludedCompression.length; i ++ ) {
			if ( fileName.indexOf( excludedCompression[i]) > 0 ) {
				return true;
			}
		}
		return false;
	}
}
