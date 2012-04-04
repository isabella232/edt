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
package org.eclipse.edt.ide.ui.internal.handlers;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.actions.WorkbenchRunnableAdapter;
import org.eclipse.edt.ide.ui.internal.codemanipulation.AddImportOperation;
import org.eclipse.edt.ide.ui.internal.codemanipulation.OrganizeImportsOperation;
import org.eclipse.edt.ide.ui.internal.codemanipulation.OrganizeImportsOperation.IChooseImportQuery;
import org.eclipse.edt.ide.ui.internal.codemanipulation.OrganizeImportsOperation.SyntaxErrorHelper;
import org.eclipse.edt.ide.ui.internal.dialogs.MultiElementListSelectionDialog;
import org.eclipse.edt.ide.ui.internal.dialogs.ProblemDialog;
import org.eclipse.edt.ide.ui.internal.util.PartInfoLabelProvider;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.progress.IProgressService;

public class AddImportHandler extends EGLHandler {
	
	/** <code>true</code> if the query dialog is showing. */
	private boolean fIsQueryShowing= false;
	
	static final class OrganizeImportError extends RuntimeException {
		private static final long serialVersionUID= 1L;
	}
 	
	public void run() {
		List eglFiles = getEGLFiles(fSelection);
    	int selCnt = eglFiles.size();
    	if(selCnt == 0)
    		return;
    	if(selCnt == 1)
    	{
    		IEGLFile eglFile = (IEGLFile)(eglFiles.get(0));
    		runOnSingle(eglFile);
    	}
    	else
    		runOnMultiple(eglFiles);
	}
	
	protected void runOnSingle(IEGLFile eglFile)
	{
		IEditingSupport helper= createViewerHelper();		
		//EGLOrganizeImportsOperation op = new EGLOrganizeImportsOperation(eglFile, needsave, createChooseImportQuery());
		
		OrganizeImportsOperation op = createOperation(eglFile);
		IProgressService progressService= PlatformUI.getWorkbench().getProgressService();
		IRunnableContext context= fSite.getWorkbenchWindow();
		if (context == null) {
			context= progressService;
		}
		try {
			registerHelper(helper);
			progressService.runInUI(context, new WorkbenchRunnableAdapter(op, op.getScheduleRule()), op.getScheduleRule());
			postRun(op);
		}catch (InvocationTargetException e) { 
			e.printStackTrace();			
		} catch (InterruptedException e) {			
		} finally {
			deregisterHelper(helper);
		}
		
	}
	
	protected void registerHelper(IEditingSupport helper) {
		if (fEditor == null)
			return;
		ISourceViewer viewer= fEditor.getViewer();
		if (viewer instanceof IEditingSupportRegistry) {
			IEditingSupportRegistry registry= (IEditingSupportRegistry) viewer;
			registry.register(helper);
		}
	}
	
	protected void postRun(OrganizeImportsOperation op) {
		if(op instanceof AddImportOperation)
		{
			AddImportOperation addOp = (AddImportOperation)op;
			IStatus status = addOp.getStatus();
			if(!status.isOK())
				setStatusBarMessage(status.getMessage());
		}
	}
	
	protected void setStatusBarMessage(String message) {
		IEditorActionBarContributor contributor= fEditor.getEditorSite().getActionBarContributor();
		if (contributor instanceof EditorActionBarContributor) {
			IStatusLineManager manager= ((EditorActionBarContributor) contributor).getActionBars().getStatusLineManager();
			manager.setMessage(message);
		}
	}
	
	private String getOrganizeSummary(OrganizeImportsOperation op) {
		int nImportsAdded= op.getNumberOfImportsAdded();
		int nImportsRemoved = op.getNumberOfImportsRemoved();

		String[] args = {String.valueOf(nImportsAdded), String.valueOf(nImportsRemoved)};
		return MessageFormat.format(UINlsStrings.OrganizeImportsAction_summary, args); 
	}

	protected void deregisterHelper(IEditingSupport helper) {
		if (fEditor == null)
			return;
		ISourceViewer viewer= fEditor.getViewer();
		if (viewer instanceof IEditingSupportRegistry) {
			IEditingSupportRegistry registry= (IEditingSupportRegistry) viewer;
			registry.unregister(helper);
		}
	}
	
	protected IEditingSupport createViewerHelper() {
		return new IEditingSupport() {
			public boolean isOriginator(DocumentEvent event, IRegion subjectRegion) {
				return true; // assume true, since we only register while we are active
			}
			public boolean ownsFocusShell() {
				return fIsQueryShowing;
			}
			
		};
	}
	
	protected OrganizeImportsOperation createOperation(IEGLFile eglFile)
	{
		ISelection selectedText = fEditor.getSelectionProvider().getSelection();
		IEGLDocument egldoc = (IEGLDocument)(fEditor.getDocumentProvider().getDocument(fEditor.getEditorInput()));
		if(selectedText instanceof ITextSelection && egldoc != null)
		{
			final ITextSelection textSelection= (ITextSelection) selectedText;
			int selOffset = textSelection.getOffset();
			int selLen = textSelection.getLength();
			return new AddImportOperation(eglFile, egldoc, selOffset, selLen, false, createChooseImportQuery());
		}
		else
			return null;
	}
	
	protected IChooseImportQuery createChooseImportQuery() {
		return new IChooseImportQuery() {
			public PartInfo[] chooseImports(Map /*<Name>, <List of <PartInfo> >*/mapOpenChoices) {
				int cnt = mapOpenChoices.size();
				
				ArrayList openChoices = new ArrayList(cnt);
				ArrayList regionList = new ArrayList(cnt);
				Set keys = mapOpenChoices.keySet();
				Iterator it = keys.iterator();
				while(it.hasNext())
				{
					Name unrevoledTypeName = (Name)(it.next());
					List partInfos = (List)(mapOpenChoices.get(unrevoledTypeName));
										
					Region region = new Region(unrevoledTypeName.getOffset(), unrevoledTypeName.getLength());
					regionList.add(region);
					
					//conver partInfos to array
					int choicesPerPartCnt = partInfos.size();
					PartInfo[] openChoice = (PartInfo[]) partInfos.toArray(new PartInfo[choicesPerPartCnt]);
					openChoices.add(openChoice);
				}
				
				PartInfo[][] chooseImportChoices = (PartInfo[][])openChoices.toArray(new PartInfo[openChoices.size()][]);
				IRegion[] regions = (IRegion[])regionList.toArray(new IRegion[regionList.size()]);
				return doChooseImports(chooseImportChoices, regions);
			}
		};
	}
	
	protected PartInfo[] doChooseImports(PartInfo[][] openChoices, final IRegion[] ranges) {
		// remember selection
		ISelection sel= fEditor != null ? fEditor.getSelectionProvider().getSelection() : null;
		PartInfo[] result= null;
		ILabelProvider labelProvider= new PartInfoLabelProvider(PartInfoLabelProvider.SHOW_FULLYQUALIFIED);
		
		MultiElementListSelectionDialog dialog= new MultiElementListSelectionDialog(fSite.getShell(), labelProvider) {
			protected void handleSelectionChanged() {
				super.handleSelectionChanged();
				// show choices in editor
				doListSelectionChanged(getCurrentPage(), ranges);
			}
		};
		fIsQueryShowing= true;
		dialog.setTitle(UINlsStrings.OrganizeImportsAction_selectiondialog_title); 
		dialog.setMessage(UINlsStrings.OrganizeImportsAction_selectiondialog_message); 
		dialog.setElements(openChoices);
		if (dialog.open() == Window.OK) {
			Object[] res= dialog.getResult();			
			result= new PartInfo[res.length];
			for (int i= 0; i < res.length; i++) {
				Object[] array= (Object[]) res[i];
				if (array.length > 0)
					result[i]= (PartInfo) array[0];
			}
		}
		// restore selection
		if (sel instanceof ITextSelection) {
			ITextSelection textSelection= (ITextSelection) sel;
			fEditor.selectAndReveal(textSelection.getOffset(), textSelection.getLength());
		}
		fIsQueryShowing= false;
		return result;
	}
	
	protected void doListSelectionChanged(int page, IRegion[] ranges) {
		if (fEditor != null && ranges != null && page >= 0 && page < ranges.length) {
			IRegion range= ranges[page];
			fEditor.selectAndReveal(range.getOffset(), range.getLength());
		}
	}
		
	private void runOnMultiple(final List eglFiles)
	{		
		String message= UINlsStrings.OrganizeImportsAction_status_description; //$NON-NLS-1$
		final MultiStatus status= new MultiStatus(EDTUIPlugin.PLUGIN_ID, IStatus.OK, message, null);
		
		IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		try {
			progressService.run(true, true, new WorkbenchRunnableAdapter(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor){
					doRunOnMultiple(eglFiles, status, monitor);
				}
			}));
			if(!status.isOK())
			{
				String title = UINlsStrings.OrganizeImportsAction_status_title;
				ProblemDialog.open(fSite.getShell(), title, null, status);
			}			
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			//cancel by user
		} //workspace lock
		
/*		ISchedulingRule rule = modifyRule(eglFiles);		
		
		String Jobtitle = EGLUINlsStrings.OrganizeImportsAction_op_description;
		WorkspaceJob job = new WorkspaceJob(Jobtitle) {
			
			 (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				doRunOnMultiple(eglFiles, status, monitor);
				if(isModal(this))
				{
					//the progress dialog is still open so just open the message
					showResults();
				}
				else
				{
					//setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
					//setProperty(IProgressConstants.ACTION_PROPERTY, getOrganizeUsedFormsCompeletedAction());
				}
				return status;
			}				
		};
					
		job.setRule(rule);
		job.setUser(true);
		job.schedule();
*/		
	}
	
	private void doRunOnMultiple(List eglfiles, MultiStatus status, IProgressMonitor monitor) throws OperationCanceledException
	{
		if(monitor == null)
			monitor = new NullProgressMonitor();
		
		monitor.setTaskName(UINlsStrings.OrganizeImportsAction_op_description);
		
		int size = eglfiles.size();
		monitor.beginTask("", size); //$NON-NLS-1$
		
		IChooseImportQuery query= new IChooseImportQuery() {
			public PartInfo[] chooseImports(Map mapOpenChoices) {
				throw new OrganizeImportError();
			}
		};
				
		try{
			for(int i=0; i<size; i++)
			{
				IEGLFile eglfile = (IEGLFile)(eglfiles.get(i));
				String fileLocation = eglfile.getPath().makeRelative().toString();
				if(testOnEGLPath(eglfile, fileLocation, status))
				{
					monitor.subTask(fileLocation);
					
					boolean needsave = ((Boolean)(fileNeedsSave.get(eglfile))).booleanValue();
					OrganizeImportsOperation op = new OrganizeImportsOperation(eglfile, needsave, query);
	/*				try 
					{
						op.run(new SubProgressMonitor(monitor, 1));
					} catch (CoreException e) {
						e.printStackTrace();
						String message = EGLUINlsStrings.OrganizeAction_error_unexpected;
						status.add(new Status(IStatus.ERROR, EGLUIPlugin.PLUGIN_ID, IStatus.ERROR, message, e));
					}
	*/
					runInSync(op, status, fileLocation, monitor);
					SyntaxErrorHelper syntaxErr = op.getSyntaxError();
					if(syntaxErr != null)
					{
						String msg = MessageFormat.format(UINlsStrings.OrganizeAction_error_syntax, new String[]{fileLocation});
						status.add(new Status(IStatus.INFO, EDTUIPlugin.PLUGIN_ID, IStatus.ERROR, msg, null));
					}  
					
					if(monitor.isCanceled())
						throw new OperationCanceledException();
				}
			}
		}
		finally
		{
			monitor.done();
		}
	}
	
	private boolean testOnEGLPath(IEGLElement eglelem, String fileLocation, MultiStatus status){
		IEGLProject eglProj = eglelem.getEGLProject();
		if(!eglProj.isOnEGLPath(eglelem))
		{
			String message = MessageFormat.format(UINlsStrings.OrganizeImportsAction_multi_error_notoncp, new String[]{fileLocation});
			status.add(new Status(IStatus.INFO, EDTUIPlugin.PLUGIN_ID, IStatus.ERROR, message, null));
			return false;
		}
		return true;
	}
	
	private void runInSync(final OrganizeImportsOperation op, final MultiStatus status, final String fileLocation, final IProgressMonitor monitor)
	{
		Runnable runnable = new Runnable() {
			public void run(){
				try
				{
					op.run(new SubProgressMonitor(monitor, 1));
				}
				catch (CoreException e) {
					e.printStackTrace();
					String message = UINlsStrings.OrganizeAction_error_unexpected;
					status.add(new Status(IStatus.ERROR, EDTUIPlugin.PLUGIN_ID, IStatus.ERROR, message, e));
				}
				catch (OrganizeImportError e){
					String[] args = {fileLocation};
					String message = MessageFormat.format(UINlsStrings.OrganizeImportsAction_multi_error_unresolvable, args);
					status.add(new Status(IStatus.INFO, EDTUIPlugin.PLUGIN_ID, IStatus.ERROR, message, e));
				}				
				catch (OperationCanceledException e)
				{
					//Canceled
					monitor.setCanceled(true);
				}
			}
		};
		fSite.getShell().getDisplay().syncExec(runnable);
	}	
	
	public void update() {
		// TODO Auto-generated method stub

	}
}
