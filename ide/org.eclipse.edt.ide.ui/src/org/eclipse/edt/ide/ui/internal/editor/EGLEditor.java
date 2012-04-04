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
package org.eclipse.edt.ide.ui.internal.editor;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import java_cup.runtime.Symbol;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NodeTypes;
import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.ide.core.internal.model.document.EGLDocument;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.bidi.IBIDIProvider;
import org.eclipse.edt.ide.ui.editor.IEGLEditor;
import org.eclipse.edt.ide.ui.editor.IFoldingStructureProvider;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.outline.ImportGroup;
import org.eclipse.edt.ide.ui.internal.outline.OutlineAdapterFactory;
import org.eclipse.edt.ide.ui.internal.outline.OutlineLabelProvider;
import org.eclipse.edt.ide.ui.internal.outline.OutlinePage;
import org.eclipse.edt.ide.ui.internal.preferences.ContentAssistPreference;
import org.eclipse.edt.ide.ui.internal.results.views.AbstractResultsViewPart;
import org.eclipse.help.IContextProvider;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class EGLEditor extends TextEditor implements IEGLEditor {
	/**
	 * ID of the action to toggle the style of the presentation.
	 */
	public static final String TOGGLE_PRESENTATION= "togglePresentation"; //$NON-NLS-1$
	public static final String FOLDING_TOGGLE_ID = "FoldingToggle"; //$NON-NLS-1$
	public static final String FOLDING_EXPANDALL_ID = "FoldingExpandAll"; //$NON-NLS-1$
	public static final String FOLDING_EXPAND_ID = "FoldingExpand"; //$NON-NLS-1$	
	public static final String FOLDING_COLLAPSE_ID = "FoldingCollapse"; //$NON-NLS-1$
	public static final String RENAME_ID = "org.eclipse.jdt.ui.actions.Rename"; //$NON-NLS-1$
	public static final String MOVE_ID = "org.eclipse.jdt.ui.actions.Move"; //$NON-NLS-1$

	/** Preference key for compiler task tags */
	private static final String COMPILER_TASK_TAGS = JavaCore.COMPILER_TASK_TAGS;

	private OutlinePage fOutlinePage;
	protected String fOutlinerContextMenuID;
	private boolean disableRulerArea;
	
	// HashMaps to contain nodes that have errors or warnings.  This is initialized
	// when the editor is opened and then is updated each time the markers change (which
	// happens on save).  These hashMaps are used by the outline adapters to put the
	// appropriate red X's on icons in the outline view.
	protected HashMap nodesWithSavedErrors;
	protected HashMap nodesWithSavedWarnings;

	AbstractResultsViewPart sqlErrorView;	 
	protected TextTools tools;

	/** The preference property change listener */
	private IPropertyChangeListener fPropertyChangeListener;

	protected ISelectionChangedListener fSelectionChangedListener = new SelectionChangedListener();
	private int fIgnoreOutlinePageSelection = 0;
	
	private OutlineAdapterFactory factory;
	
	private ProjectionSupport fProjectionSupport;
	private IFoldingStructureProvider fFoldingStructureProvider;
	private IBIDIProvider fBIDIProvider;
	
	private ToggleFoldingRunner fFoldingRunner;
	
	/** The bracket inserter. */
	private BracketInserter fBracketInserter= new BracketInserter();
	
	class AdaptedSourceViewer extends EGLSourceViewer  {
		public AdaptedSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles) {
			super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
		}

		public IContentAssistant getContentAssistant() {
			return fContentAssistant;
		}
	}
		
	class SelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
		    if(primGetOutlinePage() != null) {
		        Control control = primGetOutlinePage().getControl();
		        if(control != null && !control.isDisposed() && control.isFocusControl()) {
					doSelectionChanged(event);
		        }
		    }
		}
	};
	
	/**
	 * Runner that will toggle folding either instantly (if the editor is
	 * visible) or the next time it becomes visible. If a runner is started when
	 * there is already one registered, the registered one is canceled as
	 * toggling folding twice is a no-op.
	 * <p>
	 * The access to the fFoldingRunner field is not thread-safe, it is assumed
	 * that <code>runWhenNextVisible</code> is only called from the UI thread.
	 * </p>
	 *
	 * @since 3.1
	 */
	private final class ToggleFoldingRunner implements IPartListener2 {
		/**
		 * The workbench page we registered the part listener with, or
		 * <code>null</code>.
		 */
		private IWorkbenchPage fPage;

		/**
		 * Does the actual toggling of projection.
		 */
		private void toggleFolding() {
			ISourceViewer sourceViewer= getSourceViewer();
			if (sourceViewer instanceof ProjectionViewer) {
				ProjectionViewer pv= (ProjectionViewer) sourceViewer;
				if (pv.isProjectionMode() != isFoldingEnabled()) {
					if (pv.canDoOperation(ProjectionViewer.TOGGLE))
						pv.doOperation(ProjectionViewer.TOGGLE);
				}
			}
		}

		/**
		 * Makes sure that the editor's folding state is correct the next time
		 * it becomes visible. If it already is visible, it toggles the folding
		 * state. If not, it either registers a part listener to toggle folding
		 * when the editor becomes visible, or cancels an already registered
		 * runner.
		 */
		public void runWhenNextVisible() {
			// if there is one already: toggling twice is the identity
			if (fFoldingRunner != null) {
				fFoldingRunner.cancel();
				return;
			}
			
			IWorkbenchPartSite site= getSite();
			
			// The visual editor (EvEditor) is a multi page editor
			// Always do the toggle for the visual editor
			if (site != null && site instanceof MultiPageEditorSite == false ) {
				IWorkbenchPage page= site.getPage();
				
				// This visibility check returns false for the visual
				// editor when the Source page (EGLEditor) is visible
				// because the Source page does not have its own pane
				if (!page.isPartVisible(EGLEditor.this)) {
					// if we're not visible - defer until visible
					fPage= page;
					fFoldingRunner= this;
					page.addPartListener(this);
					return;
				}
			}
			// we're visible - run now
			toggleFolding();
		}

		/**
		 * Remove the listener and clear the field.
		 */
		private void cancel() {
			if (fPage != null) {
				fPage.removePartListener(this);
				fPage= null;
			}
			if (fFoldingRunner == this)
				fFoldingRunner= null;
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partVisible(IWorkbenchPartReference partRef) {
			if (EGLEditor.this.equals(partRef.getPart(false))) {
				cancel();
				toggleFolding();
			}
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partClosed(IWorkbenchPartReference partRef) {
			if (EGLEditor.this.equals(partRef.getPart(false))) {
				cancel();
			}
		}

		public void partActivated(IWorkbenchPartReference partRef) {}
		public void partBroughtToTop(IWorkbenchPartReference partRef) {}
		public void partDeactivated(IWorkbenchPartReference partRef) {}
		public void partOpened(IWorkbenchPartReference partRef) {}
		public void partHidden(IWorkbenchPartReference partRef) {}
		public void partInputChanged(IWorkbenchPartReference partRef) {}
	}
	
	/** Listener to annotation model changes that updates the error tick in the tab image */
	private EditorErrorTickUpdater fEGLEditorErrorTickUpdater;
	
	/**
	 * Handles a property change event describing a change
	 * of the java core's preferences and updates the preference
	 * related editor properties.
	 * 
	 * @param event the property change event
	 */
	protected void handlePreferencePropertyChanged(PropertyChangeEvent event) {
		ISourceViewer sourceViewer = getSourceViewer();
		if (COMPILER_TASK_TAGS.equals(event.getProperty())) {
			if (sourceViewer != null
				&& affectsTextPresentation(
					new PropertyChangeEvent(event.getSource(), event.getProperty(), event.getOldValue(), event.getNewValue())))
				sourceViewer.invalidateTextPresentation();
		}
		if (PreferenceConstants.EDITOR_SHOW_SEGMENTS.equals(event.getProperty())) {
			if ((sourceViewer != null)
				&& affectsTextPresentation(
					new PropertyChangeEvent(event.getSource(), event.getProperty(), event.getOldValue(), event.getNewValue()))) {
				sourceViewer.invalidateTextPresentation();
			}
		}
	}

	public EGLEditor() {
		this(false);
	}
	
	//The ruler is disabled when this editor is used within the Quick Edit view of Seoul to
	//prevent various problems which may need to get fix in a later release
	public EGLEditor(boolean disableRulerArea) {
		super();
		this.disableRulerArea = disableRulerArea;

		if (tools == null)
			tools = new TextTools(EDTUIPlugin.getDefault().getPreferenceStore());

		initializeKeyBindingScopes();
		
		fEGLEditorErrorTickUpdater= new EditorErrorTickUpdater(this);	
	}
	
	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#initializeEditor()
	 */
	protected void initializeEditor() {

		DocumentProvider documentProvider = (DocumentProvider) EGLUI.getDocumentProvider();
		setDocumentProvider(documentProvider);

		setRangeIndicator(new DefaultRangeIndicator());

		setEditorContextMenuId("#EGLEditorContext"); //$NON-NLS-1$
		setRulerContextMenuId("#EGLRulerContext"); //$NON-NLS-1$
		setOutlinerContextMenuId("#EGLOutlinerContext"); //$NON-NLS-1$

		// ensure that the TextTools adds its listener before this editor adds its listener
		if (tools == null)
			tools = new TextTools(EDTUIPlugin.getDefault().getPreferenceStore());

		// adds PropertyChangeListener
		setPreferenceStore(createCombinedPreferenceStore());
		
		setSourceViewerConfiguration(new EGLSourceViewerConfiguration(tools, this));
		
		// Instantiate this factory here and cache the value since it is
		// used by the outline adapters for the outline view.
		factory = new OutlineAdapterFactory( null, this);
		
	}

    protected boolean isEditorInputIncludedInContextMenu() {
    	return true;
    }
    
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		fAnnotationAccess= getAnnotationAccess();
		fOverviewRuler= createOverviewRuler(getSharedColors());

		//@bd1a Start
		if(Locale.getDefault().toString().toLowerCase().indexOf("ar") != -1) {
			styles |= SWT.LEFT_TO_RIGHT; 
		}
		//@bd1a End
		
		AdaptedSourceViewer sourceViewer = new AdaptedSourceViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(sourceViewer.getTextWidget(), IUIHelpConstants.EGL_EDITOR);
				

		/*
		 * This is a performance optimization to reduce the computation of
		 * the text presentation triggered by {@link #setVisibleDocument(IDocument)}
		 */
		if (sourceViewer != null && isFoldingEnabled())
			sourceViewer.prepareDelayedProjection();
		
		ProjectionViewer projectionViewer= (ProjectionViewer) sourceViewer; 
        createFoldingSupport(projectionViewer);
 //       if (isFoldingEnabled()) {
 //       	projectionViewer.doOperation(ProjectionViewer.TOGGLE);
 //       }			
        
        fFoldingStructureProvider = EDTUIPlugin.getDefault().getFoldingStructureProviderRegistry().getCurrentFoldingProvider();
        //fFoldingStructureProvider = new FoldingStructureProvider();
		if (fFoldingStructureProvider != null) {
			fFoldingStructureProvider.install(this, projectionViewer);
		}
		
	
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(sourceViewer);
		
		return sourceViewer;
	}

	public void doSelectionChanged(SelectionChangedEvent event) {
		int offset;
		int nodeLength;
		StructuredSelection selection = (StructuredSelection) event.getSelection();

		if (selection == null || selection.isEmpty())
			return;

		IEGLDocument document = (IEGLDocument) getDocumentProvider().getDocument(getEditorInput());

		if (document == null)
			return;

		// Use the 1st for selection (know it's not empty because of
		// test above) 
		Object selectedElement = selection.getFirstElement();
		if (selectedElement == null)
			return;

		if (selectedElement instanceof ImportGroup) {
			offset = ((ImportGroup) selectedElement).getOffset();
			nodeLength = ((ImportGroup) selectedElement).getLength();
		} else if (selectedElement instanceof ImportDeclaration || selectedElement instanceof PackageDeclaration) {
			offset = ((Node) selectedElement).getOffset();
			nodeLength = ((Node) selectedElement).getLength();
		} else {
			offset = ((Node) selectedElement).getOffset();
			nodeLength = ((Node) selectedElement).getLength();
		}

		try {
			editingScriptStarted();
			setSelection(offset, nodeLength);
			TreeViewer tv = (TreeViewer) event.getSource();
			ILabelProvider provider = (ILabelProvider) tv.getLabelProvider();
			IRegion region = ((OutlineLabelProvider) provider).getHighlightRange(selectedElement);
			if (region.getLength() != 0) {
				getSourceViewer().revealRange(region.getOffset(), region.getLength());
				getSourceViewer().setSelectedRange(region.getOffset(), region.getLength());
			}
		} finally {
			editingScriptEnded();
		}
	}

	/*
	 * @see AbstractTextEditor#doSetInput
	 */
	protected void doSetInput(IEditorInput input) throws CoreException {
		ISourceViewer sourceViewer = getSourceViewer();
		EGLSourceViewer eglSourceViewer = null;
		if(sourceViewer instanceof EGLSourceViewer)
			eglSourceViewer = (EGLSourceViewer)sourceViewer;
		
		IPreferenceStore store = getPreferenceStore();
		if(eglSourceViewer != null && isFoldingEnabled() && (store == null || !store.getBoolean(EDTUIPreferenceConstants.EDITOR_SHOW_SEGMENTS)))
			eglSourceViewer.prepareDelayedProjection();
		
		super.doSetInput(input);
		
		if (eglSourceViewer != null && eglSourceViewer.getReconciler() == null) {
			IReconciler reconciler = getSourceViewerConfiguration().getReconciler(eglSourceViewer);
			if (reconciler != null) {
				reconciler.install(sourceViewer);
			}
		}
		
		if (fEncodingSupport != null)
			fEncodingSupport.reset();
			
		// Initialize the error and warning hashmaps so the outline icons
		// are correct.  Must be done after the input is initialized. 	
		EditorUtility.populateNodeErrorWarningHashMaps(this);
					
		setOutlinePageInput(fOutlinePage, input);		
	}

	/*
	 * @see JavaEditor#setOutlinePageInput(JavaOutlinePage, IEditorInput)
	 */
	protected void setOutlinePageInput(OutlinePage page, IEditorInput input) {
		if (page != null) {
			DocumentProvider provider = (DocumentProvider) getDocumentProvider();
			page.setInput((IEGLDocument)provider.getDocument(input));
		}
	}

	/**
	 * Creates the outline page used with this editor.
	 */
	protected OutlinePage createOutlinePage() {
		IDocument doc = getDocumentProvider().getDocument(getEditorInput());
		if(doc instanceof IEGLDocument){
			OutlinePage page =
				new OutlinePage((IEGLDocument)doc, fOutlinerContextMenuID, this);
	
			page.addSelectionChangedListener(fSelectionChangedListener);
			setOutlinePageInput(page, getEditorInput());
	
			fOutlinePage = page;
			return page;
		}
		return null;
	}

	/**
	 * Informs the editor that its outliner has been closed.
	 */
	public void outlinePageClosed() {
		if (fOutlinePage != null) {
			fOutlinePage.removeSelectionChangedListener(fSelectionChangedListener);
			fOutlinePage = null;
			resetHighlightRange();
		}
	}

	public int getLineAtOffset(int offset) {
		try {
			EGLDocument document = (EGLDocument) getViewer().getDocument();
			return document.getLineOfOffset(offset);
		} catch (BadLocationException e) {
			e.printStackTrace();
			EGLLogger.log(this, e);
		}
		return 0;
	}

	public synchronized void editingScriptStarted() {
		++fIgnoreOutlinePageSelection;
	}

	public synchronized void editingScriptEnded() {
		--fIgnoreOutlinePageSelection;
	}
	
	private boolean isFoldingEnabled() {
		IPreferenceStore store= getPreferenceStore();
		return store.getBoolean(EDTUIPreferenceConstants.EDITOR_FOLDING_ENABLED);
	}	

	public synchronized boolean isEditingScriptRunning() {
		return (fIgnoreOutlinePageSelection > 0);
	}

	public void setSelection(int offset, int nodeLength) {

		StyledText textWidget = null;

		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer != null)
			textWidget = sourceViewer.getTextWidget();

		if (textWidget == null)
			return;

		try {
			if (offset < 0 || nodeLength < 0)
				return;

			textWidget.setRedraw(false);

			//TODO Do we need to pass in moveCursor value?
			setHighlightRange(offset, nodeLength, true);
			//		sourceViewer.revealRange(offset, nodeLength);
			//		sourceViewer.setSelectedRange(offset, nodeLength);
		} catch (IllegalArgumentException x) {
		} finally {
			if (textWidget != null)
				textWidget.setRedraw(true);
		}

	}
		public void dispose() {
			
		ISourceViewer sourceViewer= getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension)
			((ITextViewerExtension) sourceViewer).removeVerifyKeyListener(fBracketInserter);
			
		if (sqlErrorView != null) {
			sqlErrorView.closeMyViewerIfNecessary(this);
		}
        		
 		if (tools != null) {
			tools.dispose();
			tools = null;
		}
		
		if(fFoldingStructureProvider != null)
		{
			fFoldingStructureProvider.uninstall();
			fFoldingStructureProvider = null;
		}
		
		if(fBIDIProvider != null)
		{
			fBIDIProvider.uninstall();
			fBIDIProvider = null;
		}
		
		//((ProjectionViewer)getViewer()).removeProjectionListener(this);
		if (fProjectionSupport != null) {
			fProjectionSupport.dispose();
			fProjectionSupport= null;
		}		

		if (fPropertyChangeListener != null) {
			IPreferenceStore pref = getPreferenceStore();

			if (pref != null)
				pref.removePropertyChangeListener(fPropertyChangeListener);

			fPropertyChangeListener = null;
		}
		
		if (fEGLEditorErrorTickUpdater != null) {
			fEGLEditorErrorTickUpdater.dispose();
			fEGLEditorErrorTickUpdater= null;
		}
		super.dispose();
	}

	/**
	 * Returns the outline page
	 */
	public OutlinePage getOutlinePage() {
		if (fOutlinePage == null) {
			return createOutlinePage();
		}
		return fOutlinePage;
	}
	
	private IPreferenceStore createCombinedPreferenceStore() {
		List stores= new ArrayList(3);

		stores.add(EDTUIPlugin.getDefault().getPreferenceStore());
		stores.add(EditorsUI.getPreferenceStore());
		stores.add(PlatformUI.getPreferenceStore());

		return new ChainedPreferenceStore((IPreferenceStore[]) stores.toArray(new IPreferenceStore[stores.size()]));
	}
	
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "org.eclipse.edt.ide.ui.eglEditorScope"}); //$NON-NLS-1$
	}
	
	/**
	 * Sets the outliner's context menu ID.
	 */
	protected void setOutlinerContextMenuId(String menuId) {
		fOutlinerContextMenuID = menuId;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class required) {
		if (required == IEGLEditor.class || required == EGLEditor.class) {
			return this;
		}
		if (IContentOutlinePage.class.equals(required)) {
			return getOutlinePage();
		}
		if(required == IShowInTargetList.class){
			return new IShowInTargetList(){
				public String[] getShowInTargetIds() {
					return new String[]{IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.ID_OUTLINE, IPageLayout.ID_RES_NAV };
				}				
			};
		}
		
		if(required == IShowInSource.class){
			IEditorInput editorInput = getEditorInput();
			if(editorInput instanceof IFileEditorInput){
				IResource resource = ((IFileEditorInput)getEditorInput()).getFile();
				IEGLElement element = EGLCore.create(resource);
				if(element != null){
					final StructuredSelection selection = new StructuredSelection(element);
					return new IShowInSource(){
						public ShowInContext getShowInContext() {
							return new ShowInContext(getEditorInput(), selection);
						}					
					};
				}
			}
		}
		
        if (fProjectionSupport != null) { 
        	Object adapter= fProjectionSupport.getAdapter(getSourceViewer(), required); 
        	if (adapter != null) {
            	return adapter;
            }
        }

		if (required == IContextProvider.class)
			return EGLUIHelp.getHelpContextProvider(this, IUIHelpConstants.EGL_EDITOR);
		
		return super.getAdapter(required);
	}
	
	public StyledText getSourceViewerStyledText() {
		return getSourceViewer().getTextWidget();
	}

	public AbstractResultsViewPart getSqlErrorView() {
		return sqlErrorView;
	}
    
	public ISourceViewer getViewer() {
		return getSourceViewer();
	}

	public void setSqlErrorView(AbstractResultsViewPart sqlErrorView) {
		this.sqlErrorView = sqlErrorView;
	}
    
	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#doSetSelection(ISelection)
	 */
	protected void doSetSelection(ISelection selection) {
		super.doSetSelection(selection);
		synchronizeOutlinePageSelection();
	}

	/**
	 * Synchronizes the outliner selection with the actual cursor
	 * position in the editor.
	 */
	public void synchronizeOutlinePageSelection() {

		if (isEditingScriptRunning())
			return;

		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer == null || fOutlinePage == null)
			return;

		StyledText styledText = sourceViewer.getTextWidget();
		if (styledText == null)
			return;

		// get the node at the current cursor location
		int offset = getSourceViewer().getTextWidget().getSelection().x;
		IEGLDocument document = (IEGLDocument) getDocumentProvider().getDocument(getEditorInput());
		if (offset > 0) {
			offset--;
		}
		Node node = document.getNewModelNodeAtOffset(offset);
				
		factory = new OutlineAdapterFactory( null, this);
		while(node != null && !factory.isDisplayableElement(node)) {
			node = node.getParent();
		}
		
		if( node == null ) return;
				
		int nodeEndOffset = node.getOffset() + node.getLength();
		if( offset+1 > nodeEndOffset ) {
			do {
				node = node.getParent();
			}
			while( node != null && !factory.isDisplayableElement(node) );
		}
		
		fOutlinePage.removeSelectionChangedListener(fSelectionChangedListener);
		fOutlinePage.select( node );
		fOutlinePage.addSelectionChangedListener(fSelectionChangedListener);
	}

	/*
	 * @see AbstractTextEditor#handlePreferenceStoreChanged(PropertyChangeEvent)
	 */
	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {

		try {

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer == null)
				return;

			String property = event.getProperty();

			if (EDTUIPreferenceConstants.EDITOR_TAB_WIDTH.equals(property)) {
				Object value = event.getNewValue();
				if (value instanceof Integer) {
					sourceViewer.getTextWidget().setTabs(((Integer) value).intValue());
				} else if (value instanceof String) {
					sourceViewer.getTextWidget().setTabs(Integer.parseInt((String) value));
				}
				return;
			}
			
			if(EDTUIPreferenceConstants.EDITOR_FOLDING_PROVIDER.equals(property))
			{
				if(sourceViewer instanceof ProjectionViewer)
				{
					ProjectionViewer projectionViewer= (ProjectionViewer) sourceViewer;
					if(fFoldingStructureProvider != null)
						fFoldingStructureProvider.uninstall();
					//either freshly enabled or provider changed
					fFoldingStructureProvider = EDTUIPlugin.getDefault().getFoldingStructureProviderRegistry().getCurrentFoldingProvider();
					//fFoldingStructureProvider = new FoldingStructureProvider();
					if(fFoldingStructureProvider != null)
						fFoldingStructureProvider.install(this, projectionViewer);
				}
			}
			
			if (EDTUIPreferenceConstants.EDITOR_FOLDING_ENABLED.equals(property)) {
				if (sourceViewer instanceof ProjectionViewer) {
					new ToggleFoldingRunner().runWhenNextVisible();
				}
				return;
			}
			
			if (EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION.equals(property)) {
				IContentAssistant c = ((AdaptedSourceViewer)sourceViewer).getContentAssistant();
				if (c instanceof ContentAssistant) {
					ContentAssistPreference.changeConfiguration((ContentAssistant) c, getPreferenceStore(), event);
				}
				return;
			}
			
			if (EDTUIPreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY.equals(property)) {
				IContentAssistant c = ((AdaptedSourceViewer)sourceViewer).getContentAssistant();
				if (c instanceof ContentAssistant) {
					ContentAssistPreference.changeConfiguration((ContentAssistant) c, getPreferenceStore(), event);
				}
				return;
			}
			
		} finally {
			super.handlePreferenceStoreChanged(event);
		}
	}

	/*
	 * @see AbstractTextEditor#affectsTextPresentation(PropertyChangeEvent)
	 */
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return tools.affectsBehavior(event);
	}
	
	/*
	 * Update the icon used on the editor's tab
	 */
	public void updatedTitleImage(Image image) {
		setTitleImage(image);
	}
	
	public void setNodesWithSavedErrors(HashMap hashMap) {
		nodesWithSavedErrors = hashMap;
	}
	public HashMap getNodesWithSavedErrors() {
		return nodesWithSavedErrors;
	}
	public void setNodesWithSavedWarnings(HashMap hashMap) {
		nodesWithSavedWarnings = hashMap;
	}
	public HashMap getNodesWithSavedWarnings() {
		return nodesWithSavedWarnings;
	}
	
	public OutlineAdapterFactory getOutlineAdapterFactory(){
		return factory;
	}
	public OutlinePage primGetOutlinePage() {
		return fOutlinePage;
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 * 
	 * the ruler is disabled when this editor is used within the Quick Edit view of Seoul
	 */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getVerticalRuler().getControl().setEnabled(!disableRulerArea);		
		
		fBracketInserter.setCloseBracketsEnabled(true);		//always true for now, it could be read from egl editor preference		
		fBracketInserter.setCloseStringsEnabled(true);		//if we decide to add this as preference in the future

		ISourceViewer sourceViewer= getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension)
			((ITextViewerExtension) sourceViewer).prependVerifyKeyListener(fBracketInserter);		
		
	}
	
	private void createFoldingSupport(ProjectionViewer projectionViewer) {
		fProjectionSupport= new ProjectionSupport(projectionViewer, getAnnotationAccess(), getSharedColors());
		fProjectionSupport.addSummarizableAnnotationType(EGLMarkerAnnotation.ERROR_ANNOTATION_TYPE); //$NON-NLS-1$
		fProjectionSupport.addSummarizableAnnotationType(EGLMarkerAnnotation.WARNING_ANNOTATION_TYPE); //$NON-NLS-1$		
    	fProjectionSupport.setHoverControlCreator(new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell shell) {
				return new SourceViewerInformationControl(shell);
			}
		});
        fProjectionSupport.install();        
	}

	protected void performRevert() {
		ProjectionViewer projectionViewer= (ProjectionViewer) getSourceViewer();
		projectionViewer.setRedraw(false);
		try {

			boolean projectionMode= projectionViewer.isProjectionMode();
			if (projectionMode) {
				projectionViewer.disableProjection();
				if(fFoldingStructureProvider != null)
					fFoldingStructureProvider.uninstall();
			}

			super.performRevert();
			if(projectionMode){
				if(fFoldingStructureProvider != null)
				fFoldingStructureProvider.install(this, projectionViewer);
				projectionViewer.enableProjection();
			}
		} finally {
			projectionViewer.setRedraw(true);
		}
	}
	
	/**
	 * Position updater that takes any changes at the borders of a position to not belong to the position.
	 *
	 * @since 3.0
	 */
	private static class ExclusivePositionUpdater implements IPositionUpdater {

		/** The position category. */
		private final String fCategory;

		/**
		 * Creates a new updater for the given <code>category</code>.
		 *
		 * @param category the new category.
		 */
		public ExclusivePositionUpdater(String category) {
			fCategory= category;
		}

		/*
		 * @see org.eclipse.jface.text.IPositionUpdater#update(org.eclipse.jface.text.DocumentEvent)
		 */
		public void update(DocumentEvent event) {

			int eventOffset= event.getOffset();
			int eventOldLength= event.getLength();
			int eventNewLength= event.getText() == null ? 0 : event.getText().length();
			int deltaLength= eventNewLength - eventOldLength;

			try {
				Position[] positions= event.getDocument().getPositions(fCategory);

				for (int i= 0; i != positions.length; i++) {

					Position position= positions[i];

					if (position.isDeleted())
						continue;

					int offset= position.getOffset();
					int length= position.getLength();
					int end= offset + length;

					if (offset >= eventOffset + eventOldLength)
						// position comes
						// after change - shift
						position.setOffset(offset + deltaLength);
					else if (end <= eventOffset) {
						// position comes way before change -
						// leave alone
					} else if (offset <= eventOffset && end >= eventOffset + eventOldLength) {
						// event completely internal to the position - adjust length
						position.setLength(length + deltaLength);
					} else if (offset < eventOffset) {
						// event extends over end of position - adjust length
						int newEnd= eventOffset;
						position.setLength(newEnd - offset);
					} else if (end > eventOffset + eventOldLength) {
						// event extends from before position into it - adjust offset
						// and length
						// offset becomes end of event, length adjusted accordingly
						int newOffset= eventOffset + eventNewLength;
						position.setOffset(newOffset);
						position.setLength(end - newOffset);
					} else {
						// event consumes the position - delete it
						position.delete();
					}
				}
			} catch (BadPositionCategoryException e) {
				// ignore and return
			}
		}

		/**
		 * Returns the position category.
		 *
		 * @return the position category
		 */
		public String getCategory() {
			return fCategory;
		}

	}
	
	private static class BracketLevel {
		int fOffset;
		int fLength;
		LinkedModeUI fUI;
		Position fFirstPosition;
		Position fSecondPosition;
	}
	
	private class ExitPolicy implements IExitPolicy {

		final char fExitCharacter;
		final char fEscapeCharacter;
		final Stack fStack;
		final int fSize;

		public ExitPolicy(char exitCharacter, char escapeCharacter, Stack stack) {
			fExitCharacter= exitCharacter;
			fEscapeCharacter= escapeCharacter;
			fStack= stack;
			fSize= fStack.size();
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.link.LinkedPositionUI.ExitPolicy#doExit(org.eclipse.jdt.internal.ui.text.link.LinkedPositionManager, org.eclipse.swt.events.VerifyEvent, int, int)
		 */
		public ExitFlags doExit(LinkedModeModel model, VerifyEvent event, int offset, int length) {

			if (fSize == fStack.size() && !isMasked(offset)) {
				if (event.character == fExitCharacter) {
					BracketLevel level= (BracketLevel) fStack.peek();
					if (level.fFirstPosition.offset > offset || level.fSecondPosition.offset < offset)
						return null;
					if (level.fSecondPosition.offset == offset && length == 0)
						// don't enter the character if if its the closing peer
						return new ExitFlags(ILinkedModeListener.UPDATE_CARET, false);
				}
				// when entering an anonymous class between the parenthesis', we don't want
				// to jump after the closing parenthesis when return is pressed
				if (event.character == SWT.CR && offset > 0) {
					IDocument document= getSourceViewer().getDocument();
					try {
						if (document.getChar(offset - 1) == '{')
							return new ExitFlags(ILinkedModeListener.EXIT_ALL, true);
					} catch (BadLocationException e) {
					}
				}
			}
			return null;
		}

		private boolean isMasked(int offset) {
			IDocument document= getSourceViewer().getDocument();
			try {
				return fEscapeCharacter == document.getChar(offset - 1);
			} catch (BadLocationException e) {
			}
			return false;
		}
	}
	
	private class BracketInserter implements VerifyKeyListener, ILinkedModeListener {

		private boolean fCloseBrackets= true;
		private boolean fCloseStrings= true;
		private final String CATEGORY= toString();
		private IPositionUpdater fUpdater= new ExclusivePositionUpdater(CATEGORY);
		private Stack fBracketLevelStack= new Stack();

		public void setCloseBracketsEnabled(boolean enabled) {
			fCloseBrackets= enabled;
		}

		public void setCloseStringsEnabled(boolean enabled) {
			fCloseStrings= enabled;
		}

		/*
		 * @see org.eclipse.swt.custom.VerifyKeyListener#verifyKey(org.eclipse.swt.events.VerifyEvent)
		 */
		public void verifyKey(VerifyEvent event) {

			// early pruning to slow down normal typing as little as possible
			if (!event.doit || getInsertMode() != SMART_INSERT)
				return;
			switch (event.character) {
				case '(':
				case '{':
				case '[':
				case '\'':
				case '\"':
					break;
				default:
					return;
			}

			final ISourceViewer sourceViewer= getSourceViewer();
			IDocument document= sourceViewer.getDocument();

			final Point selection= sourceViewer.getSelectedRange();
			final int offset= selection.x;
			final int length= selection.y;

			try {
				IRegion startLine= document.getLineInformationOfOffset(offset);
				IRegion endLine= document.getLineInformationOfOffset(offset + length);

				//only scan the selection part
				int scanOffset = startLine.getOffset();
				String leftOfSelection = document.get(scanOffset, offset-scanOffset);
				String rightOfSelection = document.get(offset, endLine.getOffset()+endLine.getLength()-offset-length);
				
				Symbol prevToken = new Symbol(NodeTypes.EOF);
				Symbol nextToken = new Symbol(NodeTypes.EOF);
				java_cup.runtime.Scanner leftScanner = null, rightScanner = null;
				
				leftScanner = new Lexer(new StringReader(leftOfSelection));
				//find the last token in the leftScanner, which is the previous token of the cursor offset
				Symbol tmp = leftScanner.next_token();
				while(tmp.sym != NodeTypes.EOF)
				{
					prevToken = tmp;
					tmp = leftScanner.next_token();
				}
				String previous= prevToken.sym == NodeTypes.EOF ? null : document.get(prevToken.left+scanOffset, offset-scanOffset-prevToken.left).trim();				

				rightScanner = new Lexer(new StringReader(rightOfSelection));
				//find the 1st token in the rightScanner, which is the next token of the cursor offset				
				nextToken = rightScanner.next_token();								
				//String next= nextToken.sym == NodeTypes.EOF ? null : document.get(offset+length+nextToken.left, nextToken.right-nextToken.left).trim();
				String next= (nextToken.sym == NodeTypes.EOF  || nextToken.sym == NodeTypes.END || nextToken.sym == NodeTypes.LINE_COMMENT)? null : document.get(offset+length+nextToken.left, nextToken.right-nextToken.left).trim();
				switch (event.character) {
					case '(':
						if (!fCloseBrackets
								|| nextToken.sym == NodeTypes.LPAREN
								|| nextToken.sym == NodeTypes.ID
								|| next != null && next.length() > 1)
							return;
						break;
					case '{':
						if(!fCloseBrackets
								|| nextToken.sym == NodeTypes.LCURLY
								|| nextToken.sym == NodeTypes.ID
								|| next != null && next.length() > 1)
							return;
						break;
					case '[':
						if (!fCloseBrackets
								|| nextToken.sym == NodeTypes.ID
								|| next != null && next.length() > 1)
							return;
						break;

					case '\'':
					case '"':
						if (!fCloseStrings
								|| nextToken.sym == NodeTypes.ID
								|| prevToken.sym == NodeTypes.ID
								|| next != null && next.length() > 1
								|| previous != null && previous.length() > 1
								|| previous != null && previous.length()==1 && previous.charAt(0)==event.character)
							return;
						break;

					default:
						return;
				}

				ITypedRegion partition= TextUtilities.getPartition(document, IPartitions.EGL_PARTITIONING, offset, true);
				if (!IDocument.DEFAULT_CONTENT_TYPE.equals(partition.getType()))
					return;

				if (!validateEditorInputState())
					return;

				final char character= event.character;
				final char closingCharacter= getPeerCharacter(character);
				final StringBuffer buffer= new StringBuffer();
				buffer.append(character);
				buffer.append(closingCharacter);

				document.replace(offset, length, buffer.toString());


				BracketLevel level= new BracketLevel();
				fBracketLevelStack.push(level);

				LinkedPositionGroup group= new LinkedPositionGroup();
				group.addPosition(new LinkedPosition(document, offset + 1, 0, LinkedPositionGroup.NO_STOP));

				LinkedModeModel model= new LinkedModeModel();
				model.addLinkingListener(this);
				model.addGroup(group);
				model.forceInstall();

				level.fOffset= offset;
				level.fLength= 2;

				// set up position tracking for our magic peers
				if (fBracketLevelStack.size() == 1) {
					document.addPositionCategory(CATEGORY);
					document.addPositionUpdater(fUpdater);
				}
				level.fFirstPosition= new Position(offset, 1);
				level.fSecondPosition= new Position(offset + 1, 1);
				document.addPosition(CATEGORY, level.fFirstPosition);
				document.addPosition(CATEGORY, level.fSecondPosition);

				level.fUI= new EditorLinkedModeUI(model, sourceViewer);
				level.fUI.setSimpleMode(true);
				level.fUI.setExitPolicy(new ExitPolicy(closingCharacter, getEscapeCharacter(closingCharacter), fBracketLevelStack));
				level.fUI.setExitPosition(sourceViewer, offset + 2, 0, Integer.MAX_VALUE);
				level.fUI.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
				level.fUI.enter();


				IRegion newSelection= level.fUI.getSelectedRegion();
				sourceViewer.setSelectedRange(newSelection.getOffset(), newSelection.getLength());

				event.doit= false;

			} catch (BadLocationException e) {
				e.printStackTrace();
				EGLLogger.log(this, e);
			} catch (BadPositionCategoryException e) {
				e.printStackTrace();
				EGLLogger.log(this, e);
			} catch (IOException e) {
				e.printStackTrace();
				EGLLogger.log(this, e);
			} catch (Exception e) {				
				e.printStackTrace();
				EGLLogger.log(this, e);
			}
		}

		/*
		 * @see org.eclipse.jface.text.link.ILinkedModeListener#left(org.eclipse.jface.text.link.LinkedModeModel, int)
		 */
		public void left(LinkedModeModel environment, int flags) {

			final BracketLevel level= (BracketLevel) fBracketLevelStack.pop();

			if (flags != ILinkedModeListener.EXTERNAL_MODIFICATION)
				return;

			// remove brackets
			final ISourceViewer sourceViewer= getSourceViewer();
			final IDocument document= sourceViewer.getDocument();
			if (document instanceof IDocumentExtension) {
				IDocumentExtension extension= (IDocumentExtension) document;
				extension.registerPostNotificationReplace(null, new IDocumentExtension.IReplace() {

					public void perform(IDocument d, IDocumentListener owner) {
						if ((level.fFirstPosition.isDeleted || level.fFirstPosition.length == 0)
								&& !level.fSecondPosition.isDeleted
								&& level.fSecondPosition.offset == level.fFirstPosition.offset)
						{
							try {
								document.replace(level.fSecondPosition.offset,
												 level.fSecondPosition.length,
												 null);
							} catch (BadLocationException e) {
								e.printStackTrace();
								EGLLogger.log(this, e);
							}
						}

						if (fBracketLevelStack.size() == 0) {
							document.removePositionUpdater(fUpdater);
							try {
								document.removePositionCategory(CATEGORY);
							} catch (BadPositionCategoryException e) {
								e.printStackTrace();
								EGLLogger.log(this, e);
							}
						}
					}
				});
			}


		}

		/*
		 * @see org.eclipse.jface.text.link.ILinkedModeListener#suspend(org.eclipse.jface.text.link.LinkedModeModel)
		 */
		public void suspend(LinkedModeModel environment) {
		}

		/*
		 * @see org.eclipse.jface.text.link.ILinkedModeListener#resume(org.eclipse.jface.text.link.LinkedModeModel, int)
		 */
		public void resume(LinkedModeModel environment, int flags) {
		}
	}
	
	private static char getEscapeCharacter(char character) {
		switch (character) {
			case '"':
			case '\'':
				return '\\';
			default:
				return 0;
		}
	}	

	private static char getPeerCharacter(char character) {
		switch (character) {
			case '(':
				return ')';

			case ')':
				return '(';
				
			case '{':
				return '}';
				
			case '}':
				return '{';

			case '[':
				return ']';

			case ']':
				return '[';

			case '"':
				return character;

			case '\'':
				return character;

			default:
				throw new IllegalArgumentException();
		}
	}

	public boolean isPrefQuickDiffAlwaysOn(){
		if(disableRulerArea){
			return false;	
		}else{
			return super.isPrefQuickDiffAlwaysOn();
		}
	}
	
	public SourceViewerConfiguration getSourceViewerConfig() {
		return getSourceViewerConfiguration();
	}
	
	public IVerticalRuler getEditorVerticalRuler() {
		return super.getVerticalRuler();
	}
	
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		addAction(menu, ITextEditorActionConstants.GROUP_COPY, ITextEditorActionConstants.SELECT_ALL);
		addAction(menu, ITextEditorActionConstants.GROUP_FIND, ITextEditorActionConstants.FIND);
		addAction(menu, ITextEditorActionConstants.GROUP_UNDO, ITextEditorActionConstants.REDO);
		menu.addMenuListener( new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager menu) {
				try {
					IContributionItem item = menu.find( "org.eclipse.edt.ide.ui.file.sourceMenu" ); 
					if ( item != null ) {
						menu.remove( item );
					}
				} finally {
					menu.removeMenuListener( this );
				}
			}
			
		});
	}
	
	@Override
	protected String[] collectContextMenuPreferencePages() {
		return new String[] {
			"org.eclipse.edt.ide.ui.editorPreferences", //$NON-NLS-1$
			"org.eclipse.edt.ide.ui.contentAssistPreferences", //$NON-NLS-1$
			"org.eclipse.edt.ide.ui.foldingPreferences", //$NON-NLS-1$
			"org.eclipse.edt.ide.ui.formatterPreferences", //$NON-NLS-1$
			"org.eclipse.ui.editors.preferencePages.Spelling", //$NON-NLS-1$
			"org.eclipse.edt.ide.ui.organizeImportsPreferencePage", //$NON-NLS-1$
			"org.eclipse.edt.ide.ui.sourceStylesPreferences", //$NON-NLS-1$
			"org.eclipse.edt.ide.ui.templatePreferences", //$NON-NLS-1$
			"org.eclipse.edt.ide.ui.ContentAssistAdvancedPreferences", //$NON-NLS-1$
		};
		
	}

}
