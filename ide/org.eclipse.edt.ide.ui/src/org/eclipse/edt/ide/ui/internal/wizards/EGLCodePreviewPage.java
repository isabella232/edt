/*******************************************************************************
 * Copyright 漏 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.ui.editor.EGLCodeFormatterUtil;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.EGLPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.editor.DocumentProvider;
import org.eclipse.edt.ide.ui.internal.editor.EGLSourceViewerConfiguration;
import org.eclipse.edt.ide.ui.internal.editor.TextTools;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.SharedImages;

public class EGLCodePreviewPage extends WizardPage {
	private SourceViewer fPreviewViewer;
	// private IDocument document;
	private Label warningLabel;
	private Table messageList;
	private Combo fileSelector;
	private Hashtable<String, String> sourceFileContentTable;
	private String deafultSelectFile;
	private Hashtable<String, IDocument> formattedSourceFileContentTable = new Hashtable<String, IDocument>();
	private Composite messageComposite;
	private Composite codeViewerSelectorContainer;
	private Composite multiFileSelectorContainer;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public EGLCodePreviewPage(String pageName) {
		super(pageName);
	}

	public EGLCodePreviewPage(String pageName, String title, String description) {
		this(pageName);
		setTitle(title);
		setDescription(description);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, IUIHelpConstants.EGL_NEW_RECORD_SUMMARY_PAGE);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		codeViewerSelectorContainer = new Composite(container, SWT.NULL);

		GridLayout codeViewerSelectorContainerLayout = new GridLayout(2, false);
		codeViewerSelectorContainerLayout.marginWidth = 0;
		codeViewerSelectorContainer.setLayout(codeViewerSelectorContainerLayout);

		TextTools tools = new TextTools(new PreferenceStore());

		fPreviewViewer = new SourceViewer(container, null, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		fPreviewViewer.configure(new EGLSourceViewerConfiguration(tools, null));
		Font font = JFaceResources.getFont(EGLPreferenceConstants.EDITOR_TEXT_FONT);
		fPreviewViewer.getTextWidget().setFont(font);
		fPreviewViewer.setEditable(false);
		fPreviewViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		messageComposite = new HideableComposite(container, 0);
		layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		messageComposite.setLayout(layout);
		messageComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		warningLabel = new Label(messageComposite, SWT.HORIZONTAL);
		warningLabel.setText(NewWizardMessages.NewEGLFilesPreviewPage_warningLabel);
		warningLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		messageList = new Table(messageComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		messageList.setLinesVisible(false);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 80;
		messageList.setLayoutData(data);

		setControl(container);
	}

	private void clearCodeViewerSelectorContainer() {
		Control[] controls = codeViewerSelectorContainer.getChildren();
		for (int i = 0; i < controls.length; i++) {
			controls[i].dispose();
		}
	}

	private void addSingleFileInformationLabel(String filename) {
		clearCodeViewerSelectorContainer();
		Label singleFileInformationLabel = new Label(codeViewerSelectorContainer, SWT.HORIZONTAL);
		singleFileInformationLabel.setText(NewWizardMessages.NewEGLFilesPreviewFileName + filename);
	}

	private void addMultiFileSelectorContainer() {
		clearCodeViewerSelectorContainer();
		multiFileSelectorContainer = new Composite(codeViewerSelectorContainer, SWT.NULL);
		GridLayout fileSelectorContainerLayout = new GridLayout(2, false);
		fileSelectorContainerLayout.marginWidth = 0;
		multiFileSelectorContainer.setLayout(fileSelectorContainerLayout);

		Label fileSelectorLabel = new Label(multiFileSelectorContainer, SWT.HORIZONTAL);
		fileSelectorLabel.setText(NewWizardMessages.NewEGLFilesPreviewFileSelection);
		fileSelector = new Combo(multiFileSelectorContainer, SWT.READ_ONLY | SWT.BORDER);
		GridData fsgd = new GridData(GridData.FILL_HORIZONTAL);
		fsgd.widthHint = 250;
		fileSelector.setLayoutData(fsgd);

		fileSelector.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setContentByFileName(fileSelector.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				setContentByFileName(fileSelector.getText());
			}
		});
	}

	public void setsourceFileContentTable(Hashtable<String, String> sourceFileContentTable) {
		this.sourceFileContentTable = sourceFileContentTable;
		Set<String> fileNames = sourceFileContentTable.keySet();
		List<String> list = new ArrayList<String>(fileNames);
		java.util.Collections.sort(list);

		if (fileNames.size() < 1) {
			addSingleFileInformationLabel(NewWizardMessages.NewEGLFilesPreviewNoFile);
		} else if (fileNames.size() == 1) {
			String fileName = (String) fileNames.toArray()[0];
			addSingleFileInformationLabel(fileName);
		} else {
			addMultiFileSelectorContainer();
			fileSelector.removeAll();
			for (String fileName : list) {
				fileSelector.add(fileName);
			}
		}
		codeViewerSelectorContainer.layout(true);
	}
	
	public void formatSourceDocument(){
		setFormattedSourceFileTable();
		Set<String> fileNames = sourceFileContentTable.keySet();
		if (fileNames.size() == 1) {
			String fileName = (String) fileNames.toArray()[0];
			setContentByFileName(fileName);
		} else {
			if(deafultSelectFile != null){
				fileSelector.setText(deafultSelectFile);
			}else{
				fileSelector.select(0);
			}
			setContentByFileName(fileSelector.getText());
		}
	}

	private void setFormattedSourceFileTable( ){
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					formattedSourceFileContentTable.clear();
					Set<String> fileNames = sourceFileContentTable.keySet();
					monitor.beginTask(
							NewWizardMessages.NewEGLFilesPreviewStartFormattingCode,
							fileNames.size());
					for (String fileName : fileNames) {
						monitor.setTaskName(NewWizardMessages.bind(NewWizardMessages.NewEGLFilesPreviewFormattingCode, new String[]{fileName}));
						String code = sourceFileContentTable.get(fileName);
						formattedSourceFileContentTable.put(fileName, buildDocument(code));
						monitor.worked(1);
					}
				}
			});
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			EGLLogger.log(this, ex.toString());	
		} catch (Exception ex) {
			ex.printStackTrace();
			EGLLogger.log(this, ex.toString());	
		}
	}

	private IDocument buildDocument(String Str) {
		IDocument document = new Document("");
		IDocumentPartitioner partitioner = ((DocumentProvider) EGLUI.getDocumentProvider()).createDocumentPartitioner();
		document.setDocumentPartitioner(partitioner);
		partitioner.connect(document);

		document.set(Str != null ? Str : "");
		try {
			TextEdit edit = EGLCodeFormatterUtil.format(document, null);
			edit.apply(document);
		} catch (Exception ex) {
			ex.printStackTrace();
			EGLLogger.log(this, ex.toString());	
		}
		return document;
	}

	private void setContentByFileName(String fileName){
		fPreviewViewer.setDocument(formattedSourceFileContentTable.get(fileName));
	}

	public void setMessages(java.util.List<String> messages) {
		messageList.removeAll();

		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();

		if (messages != null && messages.size() > 0) {
			for (String message : messages) {
				TableItem item = new TableItem(messageList, 0);
				item.setImage(sharedImages.getImage(SharedImages.IMG_OBJS_WARN_TSK));
				item.setText(message);
			}
		}

		messageComposite.setVisible(messageList.getItemCount() > 0);

		if (messageList.getItemCount() == 0) {
			messageComposite.setVisible(false);
		} else {
			warningLabel.setText(NewWizardMessages.bind(NewWizardMessages.NewEGLFilesPreviewPage_warningLabel2,
					new String[] { Integer.toString(messageList.getItemCount()) }));
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

	public void setDeafultSelectFile(String deafultSelectFile) {
		this.deafultSelectFile = deafultSelectFile;
	}
	

}
