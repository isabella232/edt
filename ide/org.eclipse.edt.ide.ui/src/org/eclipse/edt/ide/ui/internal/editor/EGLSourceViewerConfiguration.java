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

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLContentAssistProcessor;
import org.eclipse.edt.ide.ui.internal.formatting.FormattingStrategy;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.derived.HTMLTextPresenter;

import org.eclipse.edt.ide.ui.internal.quickfix.EGLCorrectionAssistant;
/**
 * Example configuration for an <code>SourceViewer</code> which shows Java code.
 */
public class EGLSourceViewerConfiguration extends TextSourceViewerConfiguration {
	private ITextEditor editor;
	private TextTools tools;

	public EGLSourceViewerConfiguration() {
		this(new TextTools(EDTUIPlugin.getDefault().getPreferenceStore()));
		editor = null;
	}
	public EGLSourceViewerConfiguration(TextTools textTools) {
		this(textTools, null);
	}
	public EGLSourceViewerConfiguration(TextTools textTools, ITextEditor editor) {
		super();
		tools = textTools;
		this.editor = editor;
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		// Our annotation code is called when you hover over the ruler area.
		// We display the source text that is in that line.
		// This algorithm is sufficient for both the default and SQL partitions.  
		return new AnnotationHover();
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		// Use unique auto indent strategies for the different types of partitions
		if (contentType.equals(IDocument.DEFAULT_CONTENT_TYPE))
			return new IAutoEditStrategy[] {new AutoIndentStrategy()};
		else
			return new IAutoEditStrategy[] {new SQLAutoIndentStrategy()};
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, 
				IPartitions.EGL_MULTI_LINE_COMMENT,
				IPartitions.EGL_SINGLE_LINE_COMMENT,
				IPartitions.SQL_CONTENT_TYPE, 
				IPartitions.SQL_CONDITION_CONTENT_TYPE};
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		//TODO Adjust content assist tips & link to preferences (for colors, timings, etc.)
		ContentAssistant assistant = new ContentAssistant();

		// Add completion processor for the default EGL partition
//		assistant.setContentAssistProcessor(new EGLContentAssistProcessor(editor, assistant, IDocument.DEFAULT_CONTENT_TYPE), IDocument.DEFAULT_CONTENT_TYPE);
//		assistant.setContentAssistProcessor(new EGLContentAssistProcessor(editor, assistant, IPartitions.EGL_MULTI_LINE_COMMENT), IPartitions.EGL_MULTI_LINE_COMMENT);
//		assistant.setContentAssistProcessor(new EGLContentAssistProcessor(editor, assistant, IPartitions.EGL_SINGLE_LINE_COMMENT), IPartitions.EGL_SINGLE_LINE_COMMENT);
//		assistant.setContentAssistProcessor(new EGLContentAssistProcessor(editor, assistant, IPartitions.SQL_CONTENT_TYPE), IPartitions.SQL_CONTENT_TYPE);
//		assistant.setContentAssistProcessor(new EGLContentAssistProcessor(editor, assistant, IPartitions.SQL_CONDITION_CONTENT_TYPE), IPartitions.SQL_CONDITION_CONTENT_TYPE);
		
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));

		// Set auto activation based on preference
		IPreferenceStore store = EDTUIPlugin.getDefault().getPreferenceStore();
		boolean enabled= store.getBoolean(EGLPreferenceConstants.CODEASSIST_AUTOACTIVATION);
		assistant.enableAutoActivation(enabled);
		
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
		
		assistant.enableAutoInsert(true);

		//assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		//assistant.setContextInformationPopupBackground(JavaEditorEnvironment.getJavaColorProvider().getColor(new RGB(150, 150, 0)));

		return assistant;
	}

	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final MultiPassContentFormatter formatter= new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IDocument.DEFAULT_CONTENT_TYPE);

		formatter.setMasterStrategy(new FormattingStrategy());;
//		formatter.setSlaveStrategy(strategy, IPartitions.DLI_CONTENT_TYPE);
//		formatter.setSlaveStrategy(strategy, IPartitions.SQL_CONTENT_TYPE);
//		formatter.setSlaveStrategy(strategy, IPartitions.SQL_CONDITION_CONTENT_TYPE);

		return formatter;
	}
	
	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		// Use the same values for both partitions
		return new ScriptDoubleClickSelector();
	}			

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
		// Use the same values for both partitions
		return new String[] { "\t", "    " }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/* 
	 * This method creates a presentation reconciler which has separate
	 * damage repairers for the three separate partitions of our documents - 
	 * SQL, DLI and the rest.  These damage repairers know to use the right
	 * code scanners for the right partitions so the color coding is
	 * correct. 
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		PresentationReconciler reconciler = new PresentationReconciler();

		//DefaultDamagerRepairer dr = new DefaultDamagerRepairer(EGLEditorEnvironment.getEGLCodeScanner());
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(tools.getEGLCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(tools.getEGLMultilineCommentScanner());
		reconciler.setDamager(dr, IPartitions.EGL_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, IPartitions.EGL_MULTI_LINE_COMMENT);
		
		dr = new DefaultDamagerRepairer(tools.getEGLSinglelineCommentScanner());
		reconciler.setDamager(dr, IPartitions.EGL_SINGLE_LINE_COMMENT);
		reconciler.setRepairer(dr, IPartitions.EGL_SINGLE_LINE_COMMENT);
		
		//dr = new DefaultDamagerRepairer(EGLEditorEnvironment.getEGLSQLCodeScanner());
		dr = new DefaultDamagerRepairer(tools.getEGLSQLCodeScanner());
		reconciler.setDamager(dr, IPartitions.SQL_CONTENT_TYPE);
		reconciler.setRepairer(dr, IPartitions.SQL_CONTENT_TYPE);
		reconciler.setRepairer(dr, IPartitions.SQL_CONDITION_CONTENT_TYPE);

		return reconciler;
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public int getTabWidth(ISourceViewer sourceViewer) {
		return 4;
	}

	/* (non-Javadoc)
	 * Method declared on SourceViewerConfiguration
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return new BestMatchHover( getEditor() );
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getUndoManager(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IUndoManager getUndoManager(ISourceViewer sourceViewer) {
		//TODO We are now using a hybrid default undo manager
		//return null; //new EGLUndoManager(25);
		
		// Use the same undo manager for both partitions
		// The former DefaultUndoManager has been deprecated
		// The new TextViewerUndoManager supports compound changes which the visual editor requires
		return new TextViewerUndoManager(25);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getDefaultPrefixes(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 * Use // as the primary prefix for all partitions.  There were too many problems
	 * to try to figure out what parts of what partitions were selected, etc. Add -- so that sql comments
	 * will get uncommented.
	 */
	public String[] getDefaultPrefixes(ISourceViewer sourceViewer, String contentType) {
		return new String[] { CodeConstants.EGL_SINGLE_LINE_COMMENT, CodeConstants.EGL_SQL_SINGLE_LINE_COMMENT, "" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/*
	 * @see SourceViewerConfiguration#getInformationControlCreator(ISourceViewer)
	 * @since 2.0
	 */
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, new HTMLTextPresenter(true));
				// return new HoverBrowserControl(parent);
			}
		};
	}

	/**
	 * Returns the information presenter control creator. The creator is a factory creating the
	 * presenter controls for the given source viewer. This implementation always returns a creator
	 * for <code>DefaultInformationControl</code> instances.
	 * 
	 * @param sourceViewer the source viewer to be configured by this configuration
	 * @return an information control creator
	 * @since 2.1
	 */
	public IInformationControlCreator getInformationPresenterControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle = SWT.RESIZE;
				int style = SWT.V_SCROLL | SWT.H_SCROLL;
				return new DefaultInformationControl(parent, shellStyle, style, new HTMLTextPresenter(false));
				// return new HoverBrowserControl(parent);
			}
		};
	}
	
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		final ITextEditor editor = getEditor();
		if (editor != null && editor.isEditable()) {
			ProblemReconciler reconciler = new ProblemReconciler(editor);
			return reconciler;
		}
		return null;
	}
	
	public ITextEditor getEditor() {
		return editor;
	}
	
	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getQuickAssistAssistant(org.eclipse.jface.text.source.ISourceViewer)
	 * @since 3.2
	 */
	public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) {
		if (getEditor() != null)
			return new EGLCorrectionAssistant(getEditor());
		return null;
	}

	/*
	 * @see SourceViewerConfiguration#getInformationPresenter(ISourceViewer)
	 * @since 2.0
	 */
//	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		//EGL30Migration - SED
//		InformationPresenter presenter = new InformationPresenter(getInformationPresenterControlCreator(sourceViewer));
//		IInformationProvider provider = new HTMLInformationProvider();
//		presenter.setInformationProvider(provider, IDocument.DEFAULT_CONTENT_TYPE);
//		return presenter;
//	}

}
