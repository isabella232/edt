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
package org.eclipse.edt.ide.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.editor.EGLContentAssistInvocationContext;
import org.eclipse.edt.ide.ui.internal.dialogs.OptionalMessageDialog;
import org.eclipse.edt.ide.ui.internal.preferences.Messages;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionListenerExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistantExtension2;
import org.eclipse.jface.text.contentassist.IContentAssistantExtension3;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

public class EGLContentAssistProcessor implements IContentAssistProcessor {
	
	private final class CompletionListener implements ICompletionListener, ICompletionListenerExtension {
		public void assistSessionStarted(ContentAssistEvent event) {
			if (event.processor != EGLContentAssistProcessor.this)
				return;

			fIterationGesture= getIterationGesture();
			KeySequence binding= getIterationBinding();

			// This may show the warning dialog if all categories are disabled
			setCategoryIteration();
			for (Iterator it= fCategories.iterator(); it.hasNext();) {
				EGLCompletionProposalCategory cat= (EGLCompletionProposalCategory) it.next();
				cat.sessionStarted();
			}

			fRepetition= 0;
			if (event.assistant instanceof IContentAssistantExtension2) {
				IContentAssistantExtension2 extension= (IContentAssistantExtension2) event.assistant;

				if (fCategoryIteration.size() == 1) {
					extension.setRepeatedInvocationMode(false);
					extension.setShowEmptyList(false);
				} else {
					extension.setRepeatedInvocationMode(true);
					extension.setStatusLineVisible(true);
					extension.setStatusMessage(createIterationMessage());
					extension.setShowEmptyList(true);
					if (extension instanceof IContentAssistantExtension3) {
						IContentAssistantExtension3 ext3= (IContentAssistantExtension3) extension;
						((ContentAssistant) ext3).setRepeatedInvocationTrigger(binding);
					}
				}

			}
		}

		public void assistSessionEnded(ContentAssistEvent event) {
			if (event.processor != EGLContentAssistProcessor.this)
				return;

			for (Iterator it= fCategories.iterator(); it.hasNext();) {
				EGLCompletionProposalCategory cat= (EGLCompletionProposalCategory) it.next();
				cat.sessionEnded();
			}

			fCategoryIteration= null;
			fRepetition= -1;
			fIterationGesture= null;
			if (event.assistant instanceof IContentAssistantExtension2) {
				IContentAssistantExtension2 extension= (IContentAssistantExtension2) event.assistant;
				extension.setShowEmptyList(false);
				extension.setRepeatedInvocationMode(false);
				extension.setStatusLineVisible(false);
				if (extension instanceof IContentAssistantExtension3) {
					IContentAssistantExtension3 ext3= (IContentAssistantExtension3) extension;
					((ContentAssistant) ext3).setRepeatedInvocationTrigger(null);
				}
			}
		}

		public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
		}

		public void assistSessionRestarted(ContentAssistEvent event) {
			fRepetition= 0;
		}
	}
	
	private EGLCompletionProposalComparator comparator;
	
	private static final Comparator ORDER_COMPARATOR= new Comparator() {

		public int compare(Object o1, Object o2) {
			EGLCompletionProposalCategory d1= (EGLCompletionProposalCategory) o1;
			EGLCompletionProposalCategory d2= (EGLCompletionProposalCategory) o2;

			return d1.getSortOrder() - d2.getSortOrder();
		}

	};

	private static final String PREF_WARN_ABOUT_EMPTY_ASSIST_CATEGORY= "EmptyDefaultAssistCategory"; //$NON-NLS-1$

	private IEditorPart editor;
	private IContextInformationValidator fValidator;
	
	private EGLCompletionProposalComputerRegistry fComputerRegistry;
	private final List fCategories;
	private final String fPartition;
	private final ContentAssistant fAssistant;
	
	/* cycling stuff */
	private int fRepetition= -1;
	private List/*<List<CompletionProposalCategory>>*/ fCategoryIteration= null;
	private String fIterationGesture= null;
	private int fNumberOfComputedResults= 0;
	private String fErrorMessage;

	/**
	 * 
	 */
	public EGLContentAssistProcessor(IEditorPart editor, ContentAssistant assistant, String partition) {
		super();
		this.editor = editor;
		this.fComputerRegistry = EGLCompletionProposalComputerRegistry.getDefault();
		this.fAssistant = assistant;
		this.fPartition = partition;
		this.fCategories = fComputerRegistry.getProposalCategories();
		this.fAssistant.addCompletionListener(new CompletionListener());
		this.comparator = new EGLCompletionProposalComparator();
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		
		clearState();
		IProgressMonitor monitor= createProgressMonitor();
		monitor.beginTask(EGLTextMessages.ContentAssistProcessor_computing_proposals, fCategories.size() + 1);
		EGLContentAssistInvocationContext context= createContext(viewer, editor, documentOffset);
		monitor.subTask(EGLTextMessages.ContentAssistProcessor_collecting_proposals);
		List proposals= collectProposals(viewer, documentOffset, monitor, context);
		monitor.subTask(EGLTextMessages.ContentAssistProcessor_sorting_proposals);
		List filtered= filterAndSortProposals(proposals, monitor, context);
		fNumberOfComputedResults= filtered.size();
		ICompletionProposal[] result= (ICompletionProposal[]) filtered.toArray(new ICompletionProposal[filtered.size()]);
		monitor.done();

		return result;
		
	}
	
	protected List filterAndSortProposals(List proposals, IProgressMonitor monitor, EGLContentAssistInvocationContext context) {
		removeAllNullProposals(proposals);
		Collections.sort(proposals, this.comparator);
		return proposals;
	}
	
	private void removeAllNullProposals(List proposals){
		List nullList = new ArrayList();
		nullList.add(null);
		proposals.removeAll(nullList);
	}

	protected IProgressMonitor createProgressMonitor() {
		return new NullProgressMonitor();
	}
	
	protected EGLContentAssistInvocationContext createContext(ITextViewer viewer, IEditorPart aEditor, int offset) {
		return new EGLContentAssistInvocationContext(viewer, aEditor, offset);
	}
	
	private void clearState() {
		fErrorMessage=null;
		fNumberOfComputedResults= 0;
	}
	
	private List getCategories() {
		if (fCategoryIteration == null)
			return fCategories;

		int iteration= fRepetition % fCategoryIteration.size();
		fAssistant.setStatusMessage(createIterationMessage());
		fAssistant.setEmptyMessage(createEmptyMessage());
		fRepetition++;

		return (List) fCategoryIteration.get(iteration);
	}
	
	private String createIterationMessage() {
		return Messages.format(EGLTextMessages.ContentAssistProcessor_toggle_affordance_update_message, new String[]{ getCategoryLabel(fRepetition), fIterationGesture, getCategoryLabel(fRepetition + 1) });
	}
	
	private String getCategoryLabel(int repetition) {
		int iteration= repetition % fCategoryIteration.size();
		if (iteration == 0)
			return EGLTextMessages.ContentAssistProcessor_defaultProposalCategory;
		return toString((EGLCompletionProposalCategory) ((List) fCategoryIteration.get(iteration)).get(0));
	}
	
	
	private String toString(EGLCompletionProposalCategory category) {
		return category.getDisplayName();
	}
	
	private String createEmptyMessage() {
		return Messages.format(EGLTextMessages.ContentAssistProcessor_empty_message, new String[]{getCategoryLabel(fRepetition)});
	}
	
	private List collectProposals(ITextViewer viewer, int offset, IProgressMonitor monitor, EGLContentAssistInvocationContext context) {
		List proposals= new ArrayList();
		List providers= getCategories();
		for (Iterator it= providers.iterator(); it.hasNext();) {
			EGLCompletionProposalCategory cat= (EGLCompletionProposalCategory) it.next();
			List computed= cat.computeCompletionProposals(context, fPartition, new SubProgressMonitor(monitor, 1));
			proposals.addAll(computed);
			if (fErrorMessage == null)
				fErrorMessage= cat.getErrorMessage();
		}

		return proposals;
	}
	
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		return new IContextInformation[0];
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] {'.', '@'};
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return new char[0];
	}

	public IContextInformationValidator getContextInformationValidator() {
		if (fValidator == null)
			fValidator= new EGLParameterListValidator();
		return fValidator;
	}

	public String getErrorMessage() {
		if (fErrorMessage != null)
			return fErrorMessage;
		if (fNumberOfComputedResults > 0)
			return null;
		return EGLTextMessages.EGLEditor_codeassist_noCompletions;
	}
	
	private String getIterationGesture() {
		TriggerSequence binding= getIterationBinding();
		return binding != null ?Messages.format(EGLTextMessages.ContentAssistProcessor_toggle_affordance_press_gesture, new Object[] { binding.format() })
				: EGLTextMessages.ContentAssistProcessor_toggle_affordance_click_gesture;
	}
	
	private KeySequence getIterationBinding() {
	    final IBindingService bindingSvc= (IBindingService) PlatformUI.getWorkbench().getAdapter(IBindingService.class);
		TriggerSequence binding= bindingSvc.getBestActiveBindingFor(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		if (binding instanceof KeySequence)
			return (KeySequence) binding;
		return null;
    }
	
	private void setCategoryIteration() {
		fCategoryIteration= getCategoryIteration();
	}
	
	private List getCategoryIteration() {
		List sequence= new ArrayList();
		sequence.add(getDefaultCategories());
		for (Iterator it= getSeparateCategories().iterator(); it.hasNext();) {
			EGLCompletionProposalCategory cat= (EGLCompletionProposalCategory) it.next();
			sequence.add(Collections.singletonList(cat));
		}
		return sequence;
	}
	
	private List getSeparateCategories() {
		ArrayList sorted= new ArrayList();
		for (Iterator it= fCategories.iterator(); it.hasNext();) {
			EGLCompletionProposalCategory category= (EGLCompletionProposalCategory) it.next();
			if (category.isSeparateCommand() && category.hasComputers(fPartition))
				sorted.add(category);
		}
		Collections.sort(sorted, ORDER_COMPARATOR);
		return sorted;
	}
	
	private List getDefaultCategories() {
		// default mix - enable all included computers
		List included= getDefaultCategoriesUnchecked();

		if (fComputerRegistry.hasUninstalledComputers(fPartition, included)) {
			if (informUserAboutEmptyDefaultCategory())
				// preferences were restored - recompute the default categories
				included= getDefaultCategoriesUnchecked();
			fComputerRegistry.resetUnistalledComputers();
		}

		return included;
	}
	
	private List getDefaultCategoriesUnchecked() {
		List included= new ArrayList();
		for (Iterator it= fCategories.iterator(); it.hasNext();) {
			EGLCompletionProposalCategory category= (EGLCompletionProposalCategory) it.next();
			if (category.isIncluded() && category.hasComputers(fPartition))
				included.add(category);
		}
		return included;
	}
	
	private boolean informUserAboutEmptyDefaultCategory() {
		if (OptionalMessageDialog.isDialogEnabled(PREF_WARN_ABOUT_EMPTY_ASSIST_CATEGORY)) {
			final Shell shell= EDTUIPlugin.getActiveWorkbenchShell();
			String title= EGLTextMessages.ContentAssistProcessor_all_disabled_title;
			String message= EGLTextMessages.ContentAssistProcessor_all_disabled_message;
			// see PreferencePage#createControl for the 'defaults' label
			final String restoreButtonLabel= JFaceResources.getString("defaults"); //$NON-NLS-1$
			final String linkMessage= Messages.format(EGLTextMessages.ContentAssistProcessor_all_disabled_preference_link, LegacyActionTools.removeMnemonics(restoreButtonLabel));
			final int restoreId= IDialogConstants.CLIENT_ID + 10;
			final int settingsId= IDialogConstants.CLIENT_ID + 11;
			final OptionalMessageDialog dialog= new OptionalMessageDialog(PREF_WARN_ABOUT_EMPTY_ASSIST_CATEGORY, shell, title, null /* default image */, message, MessageDialog.WARNING, new String[] { restoreButtonLabel, IDialogConstants.CLOSE_LABEL }, 1) {
				/*
				 * @see org.eclipse.jdt.internal.ui.dialogs.OptionalMessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
				 */
				protected Control createCustomArea(Composite composite) {
					// wrap link and checkbox in one composite without space
					Composite parent= new Composite(composite, SWT.NONE);
					GridLayout layout= new GridLayout();
					layout.marginHeight= 0;
					layout.marginWidth= 0;
					layout.verticalSpacing= 0;
					parent.setLayout(layout);

					Composite linkComposite= new Composite(parent, SWT.NONE);
					layout= new GridLayout();
					layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
					layout.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
					layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
					linkComposite.setLayout(layout);

	        		Link link= new Link(linkComposite, SWT.NONE);
	        		link.setText(linkMessage);
	        		link.addSelectionListener(new SelectionAdapter() {
	        			public void widgetSelected(SelectionEvent e) {
	        				setReturnCode(settingsId);
	        				close();
	        			}
	        		});
	        		GridData gridData= new GridData(SWT.FILL, SWT.BEGINNING, true, false);
	        		gridData.widthHint= this.getMinimumMessageWidth();
					link.setLayoutData(gridData);

					super.createCustomArea(parent);

					return parent;
	        	}

				protected void createButtonsForButtonBar(Composite parent) {
			        Button[] buttons= new Button[2];
					buttons[0]= createButton(parent, restoreId, restoreButtonLabel, false);
			        buttons[1]= createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
			        setButtons(buttons);
				}
	        };
	        int returnValue= dialog.open();
	        if (restoreId == returnValue || settingsId == returnValue) {
	        	if (restoreId == returnValue) {
	        		IPreferenceStore store= EDTUIPlugin.getDefault().getPreferenceStore();
	        		store.setToDefault(EDTUIPreferenceConstants.CODEASSIST_CATEGORY_ORDER);
	        		store.setToDefault(EDTUIPreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
	        	}
	        	if (settingsId == returnValue)
					PreferencesUtil.createPreferenceDialogOn(shell, "org.eclipse.edt.ide.ui.preferences.CodeAssistPreferenceAdvanced", null, null).open(); //$NON-NLS-1$
	        	fComputerRegistry.reload();
	        	return true;
	        }
		}
		return false;
	}


}
