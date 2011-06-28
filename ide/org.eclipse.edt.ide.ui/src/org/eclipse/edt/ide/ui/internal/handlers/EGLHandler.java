/*
 * Licensed Materials - Property of IBM
 *
 * Copyright IBM Corporation 2011. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA DP Schedule Contract with IBM Corp.
 */
package org.eclipse.edt.ide.ui.internal.handlers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.util.EditorUtility;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class EGLHandler extends AbstractHandler implements IHandler {

	protected IStructuredSelection fSelection;
	protected IWorkbenchSite fSite;	
	protected EGLEditor fEditor;
	
    //key is the IEGLFile, value is whether or not this file needs to be saved
    //if file is opened in the EGL editor, then it does not need to be saved
    //otherwise the file needs to be saved
    protected Hashtable fileNeedsSave = new Hashtable();
    
    abstract public void run();
    
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Initialize editor 
		IEditorPart editor = HandlerUtil.getActiveEditor( event );
		if( editor instanceof EGLEditor ) {
			fEditor = (EGLEditor)editor;
			if(editor != null)
			{	
				IEditorInput editorInput = editor.getEditorInput();
				if (editorInput instanceof IFileEditorInput) {
					IResource resource = ((IFileEditorInput) editorInput).getFile();
					IEGLElement element = EGLCore.create(resource);
					fSite = editor.getSite();
					fSelection = new StructuredSelection( element );
				}			
		    }			
		}			
		if( fSelection != null )
		{
			run();
		}
		return null;
	}
	
	protected List getEGLFiles(IStructuredSelection selection)
	{
	    List result = new ArrayList();
	    if(selection != null)
	    {
	        Iterator it = selection.iterator();
	        while(it.hasNext())
	        {
	        	try
				{
		            Object element = it.next();
		            if(element instanceof IEGLElement)
		            {
		                IEGLElement eglElem = (IEGLElement)element;
		                getEGLElements(eglElem, result);
		            }
		            else if(element instanceof IProject)
		            {
		            	 IEGLProject eglproj = EGLCore.create((IProject)element);
		            	 getEGLElements(eglproj, result);
		            }
		            else if(element instanceof IResource)
		            {
		            	IEGLElement eglResourceElem = EGLCore.create((IResource)element);
		            	getEGLElements(eglResourceElem, result);
		            }
				}
	        	catch(EGLModelException e)
				{
	        		EDTUIPlugin.log(e);
				}
	        }
	    }
	    return result;
	}
	
	private void getEGLElements(IEGLElement eglElem, List result) throws EGLModelException
	{
		if(eglElem != null)
		{
	        switch(eglElem.getElementType())
	        {
	        case IEGLElement.EGL_PROJECT:
	        	IPackageFragmentRoot[] pkgRoots = ((IEGLProject)eglElem).getPackageFragmentRoots();
	        	for(int i=0; i<pkgRoots.length; i++)
	        	{
	        		collectEGLFiles(pkgRoots[i], result);
	        	}		                	
	            break;
	        case IEGLElement.PACKAGE_FRAGMENT_ROOT:
	        	collectEGLFiles((IPackageFragmentRoot)eglElem, result);
	            break;
	        case IEGLElement.PACKAGE_FRAGMENT:
	        	collectEGLFiles((IPackageFragment)eglElem, result);
	            break;
	        case IEGLElement.EGL_FILE:
	            result.add(eglElem);
	        	fileNeedsSave.put(eglElem, new Boolean(needSave((IEGLFile)eglElem)));
	            break;
	        }
		}
	}
	
	protected void collectEGLFiles(IPackageFragment pkg, List result) throws EGLModelException
	{
    	IEGLFile[] eglfiles = pkg.getEGLFiles();
    	for(int i=0; i<eglfiles.length; i++)
    	{
    		result.add(eglfiles[i]);
    		fileNeedsSave.put(eglfiles[i], new Boolean(needSave(eglfiles[i])));
    	}
	}
	
	protected void collectEGLFiles(IPackageFragmentRoot pkgRoot, List result) throws EGLModelException
	{
		if(pkgRoot.getKind() == IPackageFragmentRoot.K_SOURCE)
		{
			IEGLElement[] children = pkgRoot.getChildren();
			for(int i=0; i<children.length; i++)
			{
				collectEGLFiles((IPackageFragment)children[i], result);
			}
		}
	}
	
	private boolean needSave(IEGLFile eglfile)
	{
		boolean needSave = false;
		IEditorPart editor = EditorUtility.isOpenInEditor(eglfile);
		if(editor == null)
		{
			needSave = true;
		}
		else if(editor instanceof EGLEditor)
			fEditor = (EGLEditor)editor;
		
		return needSave;
	}

}
