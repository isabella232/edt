/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.record.wizards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.edt.ide.ui.internal.record.conversion.IMessageHandler;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizard;
import org.eclipse.edt.ide.ui.templates.parts.Part;
import org.eclipse.edt.ide.ui.internal.record.NewRecordSummaryPage;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizard;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.record.RecordOperation;
import org.eclipse.edt.ide.ui.internal.record.RecordConfiguration;
import org.eclipse.edt.ide.ui.internal.record.conversion.PartsWrapper;

public abstract class AbstractRecordFromInputWizard extends TemplateWizard 
      implements IPageChangingListener, IMessageHandler {
	protected AbstractRecordFromStringInputPage inputPage;
	protected NewRecordSummaryPage summaryPage;
	protected IStructuredSelection selection;
	protected Part[] parts;

	protected List<String> messages = new ArrayList<String>();

	public AbstractRecordFromInputWizard() {
		super();

		setNeedsProgressMonitor(true);
		
		setDialogSettings(EDTUIPlugin.getDefault().getDialogSettings());
	}

	public void addPages() {
		inputPage = createInputPage();
		addPage(inputPage);

		List<String> urls = getSavedUrls();
		inputPage.setRecentUrls(urls.toArray(new String[urls.size()]));
		List<String> files = getSavedFilePaths();
		inputPage.setRecentFilePaths(files.toArray(new String[files.size()]));

		summaryPage = new NewRecordSummaryPage(selection);
		addPage(summaryPage);
	}

	/**
	 * Constructs the input (configuration) page for the wizard
	 * 
	 * @return
	 */
	protected abstract AbstractRecordFromStringInputPage createInputPage();

	/**
	 * Processes the supplied input (typically a String representing the input
	 * data). Returns true if the processing was successful.
	 * 
	 * This function will typically call setParts and setMessages
	 * 
	 * @param input
	 * @see #setParts(Part[])
	 * @see #setMessages(List)
	 * @return
	 */
	protected abstract boolean processInput(Object input);

	public boolean performFinish() {
		if (inputPage.isInputNeedsProcessing()) {
			Object input = inputPage.getInput();
			if (input == null) {
				inputPage.setPageComplete(false);				
			} else if (!processInput(input)) {
				inputPage.setPageComplete(false);
				inputPage.setErrorMessage(buildMessageString());
				return false;
			} else {
				inputPage.setInputNeedsProcessing(false);
			}
		}

		if (parts == null) {
			return false;
		} else {
			((NewRecordWizard) getParentWizard()).setContentObj(new PartsWrapper(parts));

			if (inputPage.isUrlSource()) {
				String url = inputPage.getUrl();
				updateSavedUrls(url);
			} else if (inputPage.isFileSource()) {
				String filePath = inputPage.getFilePath();
				updateSavedFilePaths(filePath);
			}

			return true;
		}
	}

	public void init(IWorkbench arg0, IStructuredSelection selection) {
		this.selection = selection;
	}

	public void setParts(Part[] parts) {
		this.parts = parts;

		// Create preview for Summary page
		RecordOperation op = new RecordOperation(new RecordConfiguration(), null, new PartsWrapper(parts));
		try {
			summaryPage.setContent(op.getFileContents());
			summaryPage.setMessages(getFilteredMessages());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setContainer(IWizardContainer wizardContainer) {
		super.setContainer(wizardContainer);

		if (wizardContainer != null) {
			((WizardDialog) wizardContainer).addPageChangingListener(this);
		}
	}

	public void handlePageChanging(PageChangingEvent event) {
		if (event.getCurrentPage() == inputPage && inputPage.isInputNeedsProcessing() && event.getTargetPage() == summaryPage) {
			Object input = inputPage.getInput();
			if (input == null) {
				inputPage.setPageComplete(false);
				event.doit = false;
			} else if (!processInput(input)) {
				event.doit = false;
				inputPage.setPageComplete(false);
				inputPage.setErrorMessage(buildMessageString());
			} else {
				inputPage.setInputNeedsProcessing(false);
			}
		}
	}

	public List<String> getMessages() {
		return this.messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public void addMessage(String message) {
		if (this.messages == null) {
			messages = new ArrayList<String>();
		}
		this.messages.add(message);
	}

	private List<String> getFilteredMessages() {
		List<String> ret = new ArrayList<String>();

		if (messages != null) {
			for (Object obj : messages) {
				if (!ret.contains(obj.toString())) {
					ret.add(obj.toString());
				}
			}
		}

		return ret;
	}

	private String buildMessageString() {
		String ret = "";

		if (messages != null) {
			for (Iterator<String> it = messages.iterator(); it.hasNext();) {
				ret += it.next() + "\n";
			}
		}

		return ret;
	}

	protected List<String> getSavedUrls() {
		List<String> savedUrls = new ArrayList<String>();

		IDialogSettings settings = getDialogSettings().getSection(getClass().getName());
		if (settings != null) {
			for (int i = 0; i < 10; i++) {
				String url = settings.get(getClass() + ".url" + "_" + i);
				if (url != null && !url.trim().equals("")) {
					savedUrls.add(url);
				}
			}
		}

		return savedUrls;
	}

	protected List<String> getSavedFilePaths() {
		List<String> savedFilePaths = new ArrayList<String>();

		IDialogSettings settings = getDialogSettings().getSection(getClass().getName());
		if (settings != null) {
			for (int i = 0; i < 10; i++) {
				String filePath = settings.get(getClass() + ".file" + "_" + i);
				if (filePath != null && !filePath.trim().equals("")) {
					savedFilePaths.add(filePath);
				}
			}
		}

		return savedFilePaths;
	}
	
	protected void updateSavedUrls(String url) {
		List<String> urls = getSavedUrls();
		if (urls.contains(url)) {
			urls.remove(url);
		}
		urls.add(0, url);

		IDialogSettings settings = getDialogSettings().getSection(getClass().getName());
		if (settings == null) {
			settings = getDialogSettings().addNewSection(getClass().getName());
		}
		for (int i = 0; i < 10; i++) {
			settings.put(getClass() + ".url" + "_" + i, (urls.size() > i ? urls.get(i).toString() : ""));
		}
	}
	
	protected void updateSavedFilePaths(String filePath) {
		List<String> filePaths = getSavedFilePaths();
		if (filePaths.contains(filePath)) {
			filePaths.remove(filePath);
		}
		filePaths.add(0, filePath);

		IDialogSettings settings = getDialogSettings().getSection(getClass().getName());
		if (settings == null) {
			settings = getDialogSettings().addNewSection(getClass().getName());
		}
		for (int i = 0; i < 10; i++) {
			settings.put(getClass() + ".file" + "_" + i, (filePaths.size() > i ? filePaths.get(i).toString() : ""));
		}
	}
}
