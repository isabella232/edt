/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.core.ast.ISyntaxErrorRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IProblemRequestorFactory;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.model.document.IEGLModelChangeListener;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

public class ProblemReconciler implements IReconciler {

	private class BackgroundThread extends Thread {

		private boolean fCanceled = false;
		private boolean fReset = false;
		private boolean fIsDirty = false;

		/**
		 * Creates a new background thread. The thread
		 * runs with minimal priority.
		 *
		 * @param name - the thread's name
		 */
		public BackgroundThread(String name) {
			super(name);
			setPriority(Thread.MIN_PRIORITY);
			setDaemon(true);
		}

		public synchronized boolean isDirty() {
			return fIsDirty;
		}
		
		public void cancel() {
			fCanceled = true;
		}

		/**
		 * Reset the background thread as the text viewer has been changed,
		 */
		public void reset() {

			synchronized (this) {
				fIsDirty = true;
				fReset = true;
			}
		}

		public void run() {
	
			while (!fCanceled) {
				
				synchronized (this) {
					try {
						this.wait(fDelay);
					} catch (InterruptedException x) {
					}
				}
				
				if (fCanceled)
					break;

				if (!isDirty())
					continue;

				synchronized (this) {
					if (fReset) {
						fReset = false;
						continue;
					}
				}

				fStrategy.reconcile();

				synchronized (this) {
					// Someone may have set fReset while fStrategy.reconcile() was running.  In that case,
					// we want to run the reconciler again because the document is truly still dirty.
					if(!fReset){
						fIsDirty = false;
					}
				}
			}
		}
	}
	
	private class Listener implements ITextInputListener, IEGLModelChangeListener {
		
		public void modelChanged() {
			startReconciling(false);
		}

		public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
			if (oldInput == fDocument) {
				if (fDocument != null)
					fDocument.removeModelChangeListener(this);

				fDocument = null;
			}
		}

		public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
			if(newInput instanceof IEGLDocument)
				fDocument = (IEGLDocument) newInput;
			if (fDocument == null)
				return;

			fDocument.addModelChangeListener(this);
		}	
	}
	
	/**
	 * Internal part listener for activating the reconciler.
	 */
	private class PartListener implements IPartListener {

		public void partActivated(IWorkbenchPart part) {
			if (part == fEditor) {
				forceReconciling(true);
				setEditorActive(true);
			}
		}

		public void partBroughtToTop(IWorkbenchPart part) {}

		public void partClosed(IWorkbenchPart part) {}

		public void partDeactivated(IWorkbenchPart part) {
			setEditorActive(false);
		}
		
		public void partOpened(IWorkbenchPart part) {}
	}
	
	private class ResourceChangeListener implements IResourceChangeListener {

		private IResource getResource() {
			IEditorInput input = fEditor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput fileInput = (IFileEditorInput) input;
				return fileInput.getFile();
			}
			return null;
		}

		public void resourceChanged(IResourceChangeEvent e) {
			IResourceDelta delta = e.getDelta();
			IResource resource = getResource();
			if (delta != null && resource != null) {
				IResourceDelta child = delta.findMember(resource.getFullPath());
				if (child != null) {
					IMarkerDelta[] deltas = child.getMarkerDeltas();
					if (deltas.length > 0) {
						forceReconciling(false);
					}
				}
			}
		}
	}
	
	private class EGLPropertyChangeListener implements IPropertyChangeListener {

		public void propertyChange(PropertyChangeEvent event) {
			if (EDTUIPreferenceConstants.EDITOR_HANDLE_DYNAMIC_PROBLEMS.equals(event.getProperty())) {
				boolean enable = fModel.shouldEnableDynamicProblems();
				fModel.setIsHandlingDynamicProblems(enable);
				if (enable == true && isEditorActive()) {
					forceReconciling(false);
				}
			}
		}
	}
	
	private class ReconcilerProblemRequestorFactory implements IProblemRequestorFactory {

		private AccumulatingSyntaxProblemRequestor syntaxErrorRequestor;
		private AccumulatingDynamicProblemRequestor problemRequestor;
		private AccumulatingTopLevelProblemRequestor topLevelProblemRequestor;
		private IFile file;

		private ReconcilerProblemRequestorFactory(IFile file) {
			this.file = file;
			try {
				syntaxErrorRequestor = new AccumulatingSyntaxProblemRequestor(Util.getFileContents(file));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
			problemRequestor = new AccumulatingDynamicProblemRequestor();
			topLevelProblemRequestor = new AccumulatingTopLevelProblemRequestor(problemRequestor);
		}

		public IProblemRequestor getContainerContextTopLevelProblemRequestor(IFile file, String functionPartName,
				String containerContextName, IPath containerContextPath, boolean containerContextDependent) {
			if (!this.file.equals(file)) {
				return NullProblemRequestor.getInstance();
			}
			topLevelProblemRequestor.setContainerContextDependent(containerContextDependent);
			topLevelProblemRequestor.setReportContextErrors(true);
			((AccumulatingDynamicProblemRequestor) topLevelProblemRequestor.getRequestor()).setContainerContextName(containerContextName);
			return topLevelProblemRequestor;
		}

		public IProblemRequestor getGenericTopLevelFunctionProblemRequestor(IFile file, String partName, boolean containerContextDependent) {
			topLevelProblemRequestor.setContainerContextDependent(containerContextDependent);
			topLevelProblemRequestor.setReportContextErrors(false);
			return topLevelProblemRequestor;
		}

		public IProblemRequestor getProblemRequestor(IFile file, String partName) {
			problemRequestor.setContainerContextName(partName);
			return problemRequestor;
		}

		public ISyntaxErrorRequestor getSyntaxErrorRequestor(IFile file) {
			if (this.file.equals(file)) {
				return syntaxErrorRequestor;
			}
			return new org.eclipse.edt.compiler.core.ast.NullProblemRequestor();
		}

		public IProblemRequestor getFileProblemRequestor(IFile file) {
			if (this.file.equals(file)) {
				return problemRequestor;
			}
			return NullProblemRequestor.getInstance();
		}

	}
	
	private class EGLReconcilingStrategy  {

		protected void reconcile() {
			if (fDocument != null && fModel != null && fModel.isHandlingDynamicProblems()) {
				IFile file = ((IFileEditorInput) fEditor.getEditorInput()).getFile();
				IProject proj = file.getProject();
				IWorkingCopy[] currRegedWCs = EGLCore.getSharedWorkingCopies(EGLUI.getBufferFactory());
				IEGLElement eglFile = EGLCore.create(file);
				if (eglFile instanceof IEGLFile) {
					String pkgName;
					IPackageFragment packageFragment = (IPackageFragment) eglFile.getAncestor(IEGLElement.PACKAGE_FRAGMENT);
					if (packageFragment.isDefaultPackage()) {
						pkgName = "";
					} else {
						pkgName = packageFragment.getElementName();
					}
					ReconcilerProblemRequestorFactory problemFactory = new ReconcilerProblemRequestorFactory(file);
					
					IDocument document = fProvider.getDocument(fEditor.getEditorInput());
					if(document != null){
						WorkingCopyCompiler.getInstance().compileAllParts(proj, pkgName, file, currRegedWCs,
								new IWorkingCopyCompileRequestor() {
									public void acceptResult(WorkingCopyCompilationResult result) {}
								}, 
								problemFactory);
						
						List reportedProblems = new ArrayList();
						List errors = problemFactory.problemRequestor.getProblems();
						List syntaxErrors = problemFactory.syntaxErrorRequestor.getProblems();
						
						//create EGLProblemMarker objects from errors
						String filePath = file.getFullPath().toOSString();
						IEditorInput editorInput = fEditor.getEditorInput();
						if(editorInput == null){
							System.out.println("Null editor input");
						}
						buildProblemList(document, filePath, reportedProblems, syntaxErrors, "SYN");
						buildProblemList(document, filePath, reportedProblems, errors, "VAL");
												
						fModel.reportProblems(reportedProblems);
					}
				}
			}
		}

		private void buildProblemList(IDocument document, String filePath, List reportedProblems, List errors, String errorMsg) {
			for (Iterator iterator = errors.iterator(); iterator.hasNext();) {
				Problem problem = (Problem) iterator.next();
				reportedProblems.add(new ReportedProblem(document, filePath, errorMsg, problem));
			}
		}
	}
	
	private IEGLDocument fDocument;
	private Listener fModelListener;
	private IPropertyChangeListener fPropertyListener;
	private ITextViewer fViewer;
	private ITextEditor fEditor;
	private IPartListener fPartListener;
	private IResourceChangeListener fResourceChangeListener;
	private BackgroundThread fThread;
	private int fDelay = 500;
	private DocumentProvider fProvider;
	private EGLMarkerAnnotationModel fModel;
	private EGLReconcilingStrategy fStrategy = new EGLReconcilingStrategy();
	private boolean fIsEditorActive;
	
	public ProblemReconciler(ITextEditor editor) {
		fEditor = editor;
		fProvider = (DocumentProvider) fEditor.getDocumentProvider();
		fModel = (EGLMarkerAnnotationModel) fProvider.getAnnotationModel(fEditor.getEditorInput());
		if (fModel != null) {
			boolean enable = fModel.shouldEnableDynamicProblems();
			fModel.setIsHandlingDynamicProblems(enable);
		}
	}
	
	protected void forceReconciling(boolean forceReset) {
		if (fDocument != null) {
			startReconciling(forceReset);
		}
	}

	protected synchronized void startReconciling(boolean forceReset) {
		if (fThread == null)
			return;

		if (!fThread.isAlive()) {
			try {
				fThread.start();
				if (forceReset) {
					fThread.reset();
				}
			} catch (IllegalThreadStateException e) {
			}
		} else {
			fThread.reset();
		}
	}
	
	public void install(ITextViewer textViewer) {
		fViewer = textViewer;
		
		synchronized (this) {
			if (fThread != null)
				return;
			fThread = new BackgroundThread(getClass().getName());
		}
		
		fPartListener = new PartListener();
		IWorkbenchPartSite site = fEditor.getSite();
		IWorkbenchWindow window = site.getWorkbenchWindow();
		window.getPartService().addPartListener(fPartListener);
		
		fModelListener = new Listener();
		fViewer.addTextInputListener(fModelListener);
		
		fResourceChangeListener = new ResourceChangeListener();
		IWorkspace workspace = EDTUIPlugin.getWorkspace();
		workspace.addResourceChangeListener(fResourceChangeListener);
		
		fPropertyListener = new EGLPropertyChangeListener();
		EDTUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(fPropertyListener);
	}

	public void uninstall() {
		IWorkbenchPartSite site = fEditor.getSite();
		IWorkbenchWindow window = site.getWorkbenchWindow();
		window.getPartService().removePartListener(fPartListener);
		fPartListener = null;
		
		fViewer.removeTextInputListener(fModelListener);
		fModelListener = null;
		
		IWorkspace workspace = EDTUIPlugin.getWorkspace();
		workspace.removeResourceChangeListener(fResourceChangeListener);
		fResourceChangeListener = null;
		
		EDTUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(fPropertyListener);
		fPropertyListener = null;
		
		synchronized (this) {
			BackgroundThread bt = fThread;
			fThread = null;
			bt.cancel();
        }
	}
	
	private synchronized boolean isEditorActive() {
		return fIsEditorActive;
	}

	
	private synchronized void setEditorActive(boolean state) {
		fIsEditorActive = state;
	}
	

	public IReconcilingStrategy getReconcilingStrategy(String contentType) {
		return null;
	}

}
