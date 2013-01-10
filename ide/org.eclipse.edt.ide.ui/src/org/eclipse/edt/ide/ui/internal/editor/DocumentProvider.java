/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.internal.model.document.EGLDocument;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IBufferFactory;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IOpenable;
import org.eclipse.edt.ide.core.model.IProblemRequestor;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;


public class DocumentProvider extends FileDocumentProvider {

	/** The buffer factory */
	private IBufferFactory fBufferFactory= new BufferFactory();
	/** Indicates whether the save has been initialized by this provider */
	private boolean fIsAboutToSave= false;
	/** The save policy used by this provider */
	
	/** annotation model listener added to all created CU annotation models */
	private GlobalAnnotationModelListener fGlobalAnnotationModelListener;	
	
	// Jeff 1021 - Trying to make EGLEditorEnvironment not static
	private TextTools tools = new TextTools(EDTUIPlugin.getDefault().getPreferenceStore());
	
	/**
	 * Constructor for SampleDocumentProvider
	 */
	public DocumentProvider() {
		fGlobalAnnotationModelListener= new GlobalAnnotationModelListener();
	}

	/**
	 * Here for visibility issues only.
	 */
	protected class _FileSynchronizer extends FileSynchronizer {
		public _FileSynchronizer(IFileEditorInput fileEditorInput) {
			super(fileEditorInput);
		}
	};

	/**
	 * Creates <code>IBuffer</code>s based on documents.
	 */
	protected class BufferFactory implements IBufferFactory {
			
		private IDocument internalGetDocument(IFileEditorInput input) throws CoreException {
			IDocument document= getDocument(input);
			if (document != null)
				return document;
			return DocumentProvider.this.createDocument(input);
		}
			
		public IBuffer createBuffer(IOpenable owner) {
			if (owner instanceof IEGLFile) {
					
				IEGLFile unit= (IEGLFile) owner;
				IEGLFile original= (IEGLFile) unit.getOriginalElement();
				IResource resource= original.getResource();
				if (resource instanceof IFile) {
					IFileEditorInput providerKey= new FileEditorInput((IFile) resource);
						
					IDocument document= null;
					IStatus status= null;
						
					try {
						document= internalGetDocument(providerKey);
					} catch (CoreException x) {
						status= x.getStatus();
						document= new Document();
						// TODO do initializeDocument later
						// initializeDocument(document);
					}
						
					DocumentAdapter adapter= new DocumentAdapter(unit, document, new DefaultLineTracker(), DocumentProvider.this, providerKey);
					adapter.setStatus(status);
					
					// Link a name environment up to this document.  
					// We do this in the buffer creation instead of "CreateElementInfo" because "createElementInfo" is only called
					// when something is connected to a document, while a buffer is created any time someone wants to modify
					// a document (including from the model).
//JingNewModel					
//					try {
//						NameEnvironment nameEnvironment = new NameEnvironment(original.getEGLProject() );
//						NameLookup nameLookup = new NameLookup(original.getEGLProject());
//						nameEnvironment.setNameLookup(nameLookup);
//						nameLookup.setUnitsToLookInside(new IWorkingCopy[] {unit});
//						IProcessingUnit pu = new WorkingCopyProcessingUnit(unit, nameEnvironment);
//						((EGLDocument)adapter.getDocument()).setProcessingUnit(pu);
//					} catch (EGLModelException e) {
//						e.printStackTrace();
//					}	
					
					return adapter;
				}
						
			}
			return DocumentAdapter.NULL;
		}
	};
	/**
	 * Bundle of all required informations to allow working copy management. 
	 */
	protected class EGLFileInfo extends FileInfo {
			
		IEGLFile fCopy;
			
		public EGLFileInfo(IDocument document, IAnnotationModel model, _FileSynchronizer fileSynchronizer, IEGLFile copy) {
			super(document, model, fileSynchronizer);
			fCopy= copy;
		}
			
		public void setModificationStamp(long timeStamp) {
			fModificationStamp= timeStamp;
		}
	};
	protected static class GlobalAnnotationModelListener implements IAnnotationModelListener, IAnnotationModelListenerExtension {
			
		private ListenerList fListenerList;
			
		public GlobalAnnotationModelListener() {
			fListenerList= new ListenerList();
		}
			
		/**
		 * @see IAnnotationModelListener#modelChanged(IAnnotationModel)
		 */
		public void modelChanged(IAnnotationModel model) {
			Object[] listeners= fListenerList.getListeners();
			for (int i= 0; i < listeners.length; i++) {
				((IAnnotationModelListener) listeners[i]).modelChanged(model);
			}
		}

		/**
		 * @see IAnnotationModelListenerExtension#modelChanged(AnnotationModelEvent)
		 */
		public void modelChanged(AnnotationModelEvent event) {
			Object[] listeners= fListenerList.getListeners();
			for (int i= 0; i < listeners.length; i++) {
				Object curr= listeners[i];
				if (curr instanceof IAnnotationModelListenerExtension) {
					((IAnnotationModelListenerExtension) curr).modelChanged(event);
				}
			}
		}
			
		public void addListener(IAnnotationModelListener listener) {
			fListenerList.add(listener);
		}
			
		public void removeListener(IAnnotationModelListener listener) {
			fListenerList.remove(listener);
		}			
	};
	
	/*
	 * Whenever the document is created, set up our document partitioner
	 * so our documents are partitioned into SQL and the rest.
	 */	
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document= null;
		document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner= createDocumentPartitioner();
			document.setDocumentPartitioner(partitioner);
			partitioner.connect(document);
		}
		return document;
	}

	/*
	 * @see AbstractDocumentProvider#createElementInfo(Object)
	 */
	protected ElementInfo createElementInfo(Object element) throws CoreException {
		
		if ( !(element instanceof IFileEditorInput))
			return super.createElementInfo(element);
			
		IFileEditorInput input= (IFileEditorInput) element;
		IEGLFile original= createEGLFile(input.getFile());
		if (original != null) {
				
			try {
								
				try {
					refreshFile(input.getFile());
				} catch (CoreException x) {
					handleCoreException(x, "CoreException - DocumentProvider.createElementInfo()");	 //$NON-NLS-1$
				}
				
				IAnnotationModel m= createAnnotationModel(input);
				IProblemRequestor r= m instanceof IProblemRequestor ? (IProblemRequestor) m : null;
				IEGLFile c= (IEGLFile) original.getSharedWorkingCopy(getProgressMonitor(), fBufferFactory, r);
				
				DocumentAdapter a= null;
				try {
					a= (DocumentAdapter) c.getBuffer();
				} catch (ClassCastException x) {
					IStatus status= new Status(IStatus.ERROR, "org.eclipse.edt.ide.core", 3, "Shared working copy has wrong buffer", x); //$NON-NLS-1$ //$NON-NLS-2$
					throw new CoreException(status);
				}
				
				_FileSynchronizer f= new _FileSynchronizer(input); 
				f.install();
				
				EGLFileInfo info= new EGLFileInfo(a.getDocument(), m, f, c);
				info.setModificationStamp(computeModificationStamp(input.getFile()));
				info.fStatus= a.getStatus();
				info.fEncoding= getPersistedEncoding(input);
				/*
				if (r instanceof IProblemRequestorExtension) {
					IProblemRequestorExtension extension= (IProblemRequestorExtension) r;
					extension.setIsActive(isHandlingTemporaryProblems());
				}
				*/
				m.addAnnotationModelListener(fGlobalAnnotationModelListener);
		
				return info;
				
			} catch (EGLModelException x) {
				throw new CoreException(x.getStatus());
			}
		} else {		
			return super.createElementInfo(element);
		}
	}
	/**
	 * Adds a listener that reports changes from all compilation unit annotation models.
	 */
	public void addGlobalAnnotationModelListener(IAnnotationModelListener listener) {
		fGlobalAnnotationModelListener.addListener(listener);
	}

	/**
	 * Removes the listener.
	 */	
	public void removeGlobalAnnotationModelListener(IAnnotationModelListener listener) {
		fGlobalAnnotationModelListener.removeListener(listener);
	}
	
	/**
	 * Returns whether the given element is connected to this document provider.
	 * 
	 * @param element the element
	 * @return <code>true</code> if the element is connected, <code>false</code> otherwise
	 */
	boolean isConnected(Object element) {
		return getElementInfo(element) != null;	
	}
	/**
	 * Returns the underlying resource for the given element.
	 * 
	 * @param the element
	 * @return the underlying resource of the given element
	 */
	public IResource getUnderlyingResource(Object element) {
		if (element instanceof IFileEditorInput) {
			IFileEditorInput input= (IFileEditorInput) element;
			return input.getFile();
		}
		return null;
	}
	/**
	 * Creates a EGL file from the given file.
	 * 
	 * @param file the file from which to create the EGL file
	 */
	protected IEGLFile createEGLFile(IFile file) {
		Object element= EGLCore.create(file);
		if (element instanceof IEGLFile)
			return (IEGLFile) element;
		return null;
	}
	/*
	 * @see AbstractDocumentProvider#createAnnotationModel(Object)
	 */
	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
		if (element instanceof IFileEditorInput) {
			IFileEditorInput input = (IFileEditorInput) element;
			return new EGLMarkerAnnotationModel(input.getFile());
		}
		return super.createAnnotationModel(element);
	}

	/*
	 * This method returns a DefaultPartitioner that knows to use
	 * the class that partitions our documents into the two 
	 * separate partitions we honor - SQL and the rest. 
	 */
	public IDocumentPartitioner createDocumentPartitioner() {
		
		String[] types= new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			IPartitions.EGL_MULTI_LINE_COMMENT,
			IPartitions.EGL_SINGLE_LINE_COMMENT,
			IPartitions.SQL_CONTENT_TYPE,
			IPartitions.SQL_CONDITION_CONTENT_TYPE
		};

//		return new DefaultPartitioner(tools.getEGLPartitionScanner(), types);
		// BBL -- Switched back to use our version of the partitioner to avoid
		// performance problems and to fix a problem where partitioning of multi-line
		// comments wasn't being handled appropriately.  Also put back our code in
		// PartitionScanner so that we create a default partition for everything
		// except our SQL partition.  An OTI developer had suggested that we not
		// use the default partition, but since we had the problems listed above
		// we're going back to our patch.
		return new Partitioner(tools.getEGLPartitionScanner(), types);
	}
	
	/*
	 * @see AbstractDocumentProvider#disposeElementInfo(Object, ElementInfo)
	 */
	protected void disposeElementInfo(Object element, ElementInfo info) {
		
		if (info instanceof EGLFileInfo) {
			EGLFileInfo eglInfo= (EGLFileInfo) info;
			eglInfo.fCopy.destroy();
			eglInfo.fModel.removeAnnotationModelListener(fGlobalAnnotationModelListener);
		}
		
		super.disposeElementInfo(element, info);
	}
		
	/*
	 * @see AbstractDocumentProvider#doSaveDocument(IProgressMonitor, Object, IDocument, boolean)
	 */
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException {
				
		ElementInfo elementInfo= getElementInfo(element);		
		if (elementInfo instanceof EGLFileInfo) {
			EGLFileInfo info= (EGLFileInfo) elementInfo;
			
			// update structure, assumes lock on info.fCopy
			info.fCopy.reconcile();
			
			IEGLFile original= (IEGLFile) info.fCopy.getOriginalElement();
			IResource resource= original.getResource();
			
			if (resource == null) {
				// underlying resource has been deleted, just recreate file, ignore the rest
				super.doSaveDocument(monitor, element, document, overwrite);
				return;
			}
			
			if (resource != null && !overwrite)
				checkSynchronizationState(info.fModificationStamp, resource);
				
//			if (fSavePolicy != null)
//				fSavePolicy.preSave(info.fCopy);
			
			// inform about the upcoming content change
			fireElementStateChanging(element);	
			try {
				fIsAboutToSave= true;
				// commit working copy
				info.fCopy.commit(overwrite, monitor);
			} catch (CoreException x) {
				// inform about the failure
				fireElementStateChangeFailed(element);
				throw x;
			} catch (RuntimeException x) {
				// inform about the failure
				fireElementStateChangeFailed(element);
				throw x;
			} finally {
				fIsAboutToSave= false;
			}
			
			// If here, the dirty state of the editor will change to "not dirty".
			// Thus, the state changing flag will be reset.
			AbstractMarkerAnnotationModel model= (AbstractMarkerAnnotationModel) info.fModel;
			model.updateMarkers(info.fDocument);
			
			if (resource != null)
				info.setModificationStamp(computeModificationStamp(resource));
			
//			EGLTODO: Do we still need this code?	
//			if (fSavePolicy != null) {
//				IEGLFile unit= fSavePolicy.postSave(original);
//				if (unit != null) {
//					IResource r= unit.getResource();
//					IMarker[] markers= r.findMarkers(IMarker.MARKER, true, IResource.DEPTH_ZERO);
//					if (markers != null && markers.length > 0) {
//						for (int i= 0; i < markers.length; i++)
//							model.updateMarker(markers[i], info.fDocument, null);
//					}
//				}
//			}
//				
			
		} else {
			super.doSaveDocument(monitor, element, document, overwrite);
		}		
	}
	/*
	 * @see AbstractDocumentProvider#doResetDocument(Object)
	 */
	public void doResetDocument(Object element, IProgressMonitor monitor) throws CoreException {
		if (element == null)
			return;
			
		ElementInfo elementInfo= getElementInfo(element);		
		if (elementInfo instanceof EGLFileInfo) {
			EGLFileInfo info= (EGLFileInfo) elementInfo;
			
			IDocument document;
			IStatus status= null;
			
			try {
				
				IEGLFile original= (IEGLFile) info.fCopy.getOriginalElement();
				IResource resource= original.getResource();
				if (resource instanceof IFile) {
					
					IFile file= (IFile) resource;
					
					try {
						refreshFile(file, monitor);
					} catch (CoreException x) {
						handleCoreException(x, "CoreException - DocumentProvider.doResetDocument()");	 //$NON-NLS-1$
					}
					
					IFileEditorInput input= new FileEditorInput(file);
					document= super.createDocument(input);
					
				} else {
					document= new Document();
				}
					
			} catch (CoreException x) {
				document= new Document();
				status= x.getStatus();
			}
			
			fireElementContentAboutToBeReplaced(element);
			
			removeUnchangedElementListeners(element, info);
			info.fDocument.set(document.get());
			info.fCanBeSaved= false;
			info.fStatus= status;
			addUnchangedElementListeners(element, info);
			
			fireElementContentReplaced(element);
			fireElementDirtyStateChanged(element, false);
			
		} else {
			super.doResetDocument(element, monitor);
		}
	}
	
	/**
	 * Saves the content of the given document to the given element.
	 * This is only performed when this provider initiated the save.
	 * 
	 * @param monitor the progress monitor
	 * @param element the element to which to save
	 * @param document the document to save
	 * @param overwrite <code>true</code> if the save should be enforced
	 */
	public void saveDocumentContent(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException {
		
		if (!fIsAboutToSave)
			return;
		
		if (element instanceof IFileEditorInput) {
			IFileEditorInput input= (IFileEditorInput) element;
			try {
				String encoding= getEncoding(element);
				if (encoding == null)
					encoding= ResourcesPlugin.getEncoding();
				InputStream stream= new ByteArrayInputStream(document.get().getBytes(encoding));
				IFile file= input.getFile();
				file.setContents(stream, overwrite, true, monitor);
			} catch (IOException x)  {
				IStatus s= new Status(IStatus.ERROR, "org.eclipse.edt.ide.core", IStatus.OK, x.getMessage(), x); //$NON-NLS-1$
				throw new CoreException(s);
			}
		}
	}


	/**
	 * Returns the working copy this document provider maintains for the given
	 * element.
	 * 
	 * @param element the given element
	 * @return the working copy for the given element
	 */
	IEGLFile getWorkingCopy(IEditorInput element) {
		
		ElementInfo elementInfo= getElementInfo(element);		
		if (elementInfo instanceof EGLFileInfo) {
			EGLFileInfo info= (EGLFileInfo) elementInfo;
			return info.fCopy;
		}
		
		return null;
	}
	/**
	 * @return
	 */
	public IBufferFactory getBufferFactory() {
		return fBufferFactory;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createEmptyDocument()
	 */
	protected IDocument createEmptyDocument() {
		return new EGLDocument();
	}
	
	public boolean isSynchronized(Object element) {
		long modStamp = getModificationStamp(element);
		long synStamp = getSynchronizationStamp(element);
		return (modStamp == synStamp) && super.isSynchronized(element) ;
	}
}
