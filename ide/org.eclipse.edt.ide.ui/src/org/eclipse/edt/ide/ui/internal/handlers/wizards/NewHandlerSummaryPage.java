/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.handlers.wizards;

import org.eclipse.edt.ide.ui.editor.EGLCodeFormatterUtil;
import org.eclipse.edt.ide.ui.internal.EGLPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.editor.DocumentProvider;
import org.eclipse.edt.ide.ui.internal.editor.EGLSourceViewerConfiguration;
import org.eclipse.edt.ide.ui.internal.editor.TextTools;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.SharedImages;

public class NewHandlerSummaryPage extends WizardPage {
	private SourceViewer fPreviewViewer;
	private IDocument document;
	private Label warningLabel;
	private Table messageList;
	private Composite messageComposite;
	private String content;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NewHandlerSummaryPage() {
		super(NewHandlerWizardMessages.NewHandlerSummaryPage_pageName);
		setTitle(NewHandlerWizardMessages.NewHandlerSummaryPage_pageTitle);
		setDescription(NewHandlerWizardMessages.NewHandlerSummaryPage_pageDescription);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		// TODO change help
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, IUIHelpConstants.EGL_NEW_HANDLER_SUMMARY_PAGE);
		
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		Label label = new Label(container, SWT.HORIZONTAL);
		label.setText(NewHandlerWizardMessages.NewHandlerSummaryPage_previewLabel);

		TextTools tools = new TextTools(new PreferenceStore());

		fPreviewViewer = new SourceViewer(container, null, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		fPreviewViewer.configure(new EGLSourceViewerConfiguration(tools, null));
		Font font = JFaceResources.getFont(EGLPreferenceConstants.EDITOR_TEXT_FONT);
		fPreviewViewer.getTextWidget().setFont(font);
		fPreviewViewer.setEditable(false);
		fPreviewViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		document = new Document("");		

		IDocumentPartitioner partitioner = ((DocumentProvider) EGLUI.getDocumentProvider()).createDocumentPartitioner();
		document.setDocumentPartitioner(partitioner);
		partitioner.connect(document);

		fPreviewViewer.setDocument(document);		

		messageComposite = new HideableComposite(container, 0);
		layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		messageComposite.setLayout(layout);
		messageComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		warningLabel = new Label(messageComposite, SWT.HORIZONTAL);
		warningLabel.setText(NewHandlerWizardMessages.NewHandlerSummaryPage_warningLabel);
		warningLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		messageList = new Table(messageComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		messageList.setLinesVisible(false);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 80;
		messageList.setLayoutData(data);

		setControl(container);	
		setContent(content);
	}

	public void setContent(String str) {
		content = str;
		if(document!=null){
			document.set(str != null ? str : "");
			try {
				TextEdit edit = EGLCodeFormatterUtil.format(document, null);
				edit.apply(document);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
			
	public void setMessages(java.util.List<String> messages) {
		messageList.removeAll();

		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		
		if(messages != null && messages.size() > 0) {
			for(String message : messages) {
				TableItem item = new TableItem(messageList, 0);
				item.setImage(sharedImages.getImage(SharedImages.IMG_OBJS_WARN_TSK));
				item.setText(message);
			}
		}

		messageComposite.setVisible(messageList.getItemCount() > 0);

		if (messageList.getItemCount() == 0) {
			messageComposite.setVisible(false);
		} else {
			warningLabel.setText(NewHandlerWizardMessages.bind(NewHandlerWizardMessages.NewHandlerSummaryPage_warningLabel2,new String[] {Integer.toString(messageList.getItemCount())}));
		}
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (messageComposite != null) {
			messageComposite.setVisible(messageList.getItemCount() > 0);
		}
	}

	private static class HideableComposite extends Composite {

		public HideableComposite(Composite parent, int style) {
			super(parent, style);
		}

		public Point computeSize(int wHint, int hHint, boolean changed) {
			if (isVisible()) {
				return super.computeSize(wHint, hHint, changed);
			} else {
				return new Point(0, 0);
			}
		}

		public void setVisible(boolean visible) {
			super.setVisible(visible);

			pack(true);
			getParent().layout(true);
		}		
	}
	public boolean isPageComplete(){
		return true;
	}
	
}
