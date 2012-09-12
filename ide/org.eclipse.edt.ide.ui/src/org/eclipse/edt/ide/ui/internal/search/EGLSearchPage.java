/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.RowLayouter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.ISearchResultViewEntry;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class EGLSearchPage extends DialogPage implements ISearchPage, IEGLSearchConstants {

	public static final String EXTENSION_POINT_ID= "org.eclipse.edt.ide.ui.search.EGLSearchPage"; //$NON-NLS-1$

	// Dialog store id constants
	private final static String PAGE_NAME= "EGLSearchPage"; //$NON-NLS-1$
	private final static String STORE_CASE_SENSITIVE= PAGE_NAME + "CASE_SENSITIVE"; //$NON-NLS-1$


	private static List fgPreviousSearchPatterns= new ArrayList(20);

	private SearchPatternData fInitialData;
	private IStructuredSelection fStructuredSelection;
	private IEGLElement feglElement;
	private boolean fFirstTime= true;
	private IDialogSettings fDialogSettings;
	private boolean fIsCaseSensitive;
	
	private Combo fPattern;
	private ISearchPageContainer fContainer;
	private Button fCaseSensitive;
	
	private Button[] fSearchFor;
	private int[] fSearchForValues = { PROGRAM_PART, ALL_FUNCTIONS, LIBRARY_PART, RECORD_PART, HANDLER_PART, SERVICE_PART, INTERFACE_PART, DELEGATE_PART, EXTERNALTYPE_PART, ENUMERATION_PART, CLASS_PART, ANNOTATION_PART, STEREOTYPE_PART, ALL_ELEMENTS };		
	private String[] fSearchForText= {
		EGLSearchMessages.EGLSearchPageSearchForProgram,
		EGLSearchMessages.EGLSearchPageSearchForFunction,
		EGLSearchMessages.EGLSearchPageSearchForLibrary,
		EGLSearchMessages.EGLSearchPageSearchForRecord,
		EGLSearchMessages.EGLSearchPageSearchForHandler,
		EGLSearchMessages.EGLSearchPageSearchForService,
		EGLSearchMessages.EGLSearchPageSearchForInterface,
		EGLSearchMessages.EGLSearchPageSearchForDelegate,
		EGLSearchMessages.EGLSearchPageSearchForExternalType,
		EGLSearchMessages.EGLSearchPageSearchForEnum,
		EGLSearchMessages.EGLSearchPageSearchForClass,
		EGLSearchMessages.EGLSearchPageSearchForAnnotation,
		EGLSearchMessages.EGLSearchPageSearchForStereotype,
		EGLSearchMessages.EGLSearchPageSearchForAny
	};

	private Button[] fLimitTo;
	private int[] fLimitToValues = { DECLARATIONS, REFERENCES, ALL_OCCURRENCES };	
	private String[] fLimitToText= {
		EGLSearchMessages.EGLSearchPageLimitToDeclarations,
		EGLSearchMessages.EGLSearchPageLimitToReferences,
		EGLSearchMessages.EGLSearchPageLimitToAllOccurrences	
	};

	private static class SearchPatternData {
		int			searchFor;
		int			limitTo;
		String			pattern;
		boolean		isCaseSensitive;
		IEGLElement	eglElement;
		int			scope;
		IWorkingSet[]	 	workingSets;
				
		public SearchPatternData(int s, int l, boolean i, String p, IEGLElement element) {
			this(s, l, p, i, element, ISearchPageContainer.WORKSPACE_SCOPE, null);
		}
	
		public SearchPatternData(int s, int l, String p, boolean i, IEGLElement element, int scope, IWorkingSet[] workingSets) {
			searchFor= s;
			limitTo= l;
			pattern= p;
			isCaseSensitive= i;
			eglElement= element;
			this.scope = scope;
			this.workingSets = workingSets;
		}
		
	}
	
	public boolean performNewSearch()
	{
		org.eclipse.search.ui.NewSearchUI.activateSearchResultView();
		
		SearchPatternData patternData= getPatternData();

		// Setup search scope
		IEGLSearchScope scope= null;
		String scopeDescription= ""; //$NON-NLS-1$
		switch (getContainer().getSelectedScope()) {
			case ISearchPageContainer.WORKSPACE_SCOPE:
				scopeDescription= EGLSearchMessages.WorkspaceScope;
				scope= SearchEngine.createWorkspaceScope();
				break;
			case ISearchPageContainer.SELECTION_SCOPE:
				scopeDescription= EGLSearchMessages.SelectionScope;
				scope= EGLSearchScopeFactory.getInstance().createEGLSearchScope(fStructuredSelection);
				break;
			case ISearchPageContainer.SELECTED_PROJECTS_SCOPE:
				scope= EGLSearchScopeFactory.getInstance().createEGLProjectSearchScope(fStructuredSelection);
				IProject[] projects= EGLSearchScopeFactory.getInstance().getProjects(scope);
				if (projects.length >= 1) {
					if (projects.length == 1)
						scopeDescription= EGLSearchMessages.bind(EGLSearchMessages.EnclosingProjectScope, projects[0].getName()); //$NON-NLS-1$
					else
						scopeDescription= EGLSearchMessages.bind(EGLSearchMessages.EnclosingProjectsScope, projects[0].getName()); //$NON-NLS-1$
				} else 
					scopeDescription= EGLSearchMessages.bind(EGLSearchMessages.EnclosingProjectScope, ""); //$NON-NLS-1$ //$NON-NLS-2$
				break;				
			case ISearchPageContainer.WORKING_SET_SCOPE:
				IWorkingSet[] workingSets= getContainer().getSelectedWorkingSets();
				// should not happen - just to be sure
				if (workingSets == null || workingSets.length < 1)
					return false;
				scopeDescription=EGLSearchMessages.bind(EGLSearchMessages.WorkingSetScope, SearchUtil.toString(workingSets));
				scope= EGLSearchScopeFactory.getInstance().createEGLSearchScope(getContainer().getSelectedWorkingSets());
				//SearchUtil.updateLRUWorkingSets(getContainer().getSelectedWorkingSets());
		}
					
		EGLSearchQuery wsJob = new EGLSearchQuery(patternData.pattern, patternData.isCaseSensitive, 
		        								  patternData.searchFor, patternData.limitTo,
		        								  scope, scopeDescription,false);
		NewSearchUI.activateSearchResultView();
		NewSearchUI.runQueryInBackground(wsJob);
	
		return true;
	}

	public boolean performAction() {
		return performNewSearch();
		
	}

	private int getLimitTo() {
		for (int i= 0; i < fLimitTo.length; i++) {
			if (fLimitTo[i].getSelection())
				return fLimitToValues[ i ];
		}
		return UNKNOWN;
	}
	
	private void setLimitTo(int searchFor ) {
	
		for( int i = 0; i < fLimitTo.length; i++ )
			fLimitTo[ i ].setEnabled( true );				
	}

	private String[] getPreviousSearchPatterns() {
		// Search results are not persistent
		int patternCount= fgPreviousSearchPatterns.size();
		String [] patterns= new String[patternCount];
		for (int i= 0; i < patternCount; i++)
			patterns[i]= ((SearchPatternData) fgPreviousSearchPatterns.get(patternCount - 1 - i)).pattern;
		return patterns;
	}
	
	private int getSearchFor() {	
		for (int i= 0; i < fSearchFor.length; i++) {
			if (fSearchFor[i].getSelection())
				return fSearchForValues[ i ] ;
		}
		return UNKNOWN;
	}	
	
	private String getPattern() {
		return fPattern.getText();
	}
	


	/**
	 * Return search pattern data and update previous searches.
	 * An existing entry will be updated.
	 */
	private SearchPatternData getPatternData() {
		String pattern= getPattern();
		SearchPatternData match= null;
		int i= 0;
		int size= fgPreviousSearchPatterns.size();
		while (match == null && i < size) {
			match= (SearchPatternData) fgPreviousSearchPatterns.get(i);
			i++;
			if (!pattern.equals(match.pattern))
				match= null;
		};
		if (match == null) {
			match= new SearchPatternData(
							getSearchFor(),
							getLimitTo(),
							pattern,
							fCaseSensitive.getSelection(),
							feglElement,
							getContainer().getSelectedScope(),
							getContainer().getSelectedWorkingSets());
			fgPreviousSearchPatterns.add(match);
		}
		else {
			match.searchFor= getSearchFor();
			match.limitTo= getLimitTo();
			match.isCaseSensitive= fCaseSensitive.getSelection();
			match.eglElement= feglElement;
			match.scope= getContainer().getSelectedScope();
			match.workingSets= getContainer().getSelectedWorkingSets();
		};
		return match;
	}

	/*
	 * Implements method from IDialogPage
	 */
	public void setVisible(boolean visible) {
		if (visible && fPattern != null) {
			if (fFirstTime) {
				fFirstTime= false;
				// Set item and text here to prevent page from resizing
				fPattern.setItems(getPreviousSearchPatterns());
				initSelections();
			}
			fPattern.setFocus();
			getContainer().setPerformActionEnabled(fPattern.getText().length() > 0);
		}
		super.setVisible(visible);
	}
	
	public boolean isValid() {
		return true;
	}

	//---- Widget creation ------------------------------------------------

	/**
	 * Creates the page's content.
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		readConfiguration();
		
		GridData gd;
		Composite result= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout(2, false);
		layout.horizontalSpacing= 10;
		result.setLayout(layout);
		result.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		RowLayouter layouter= new RowLayouter(layout.numColumns);
		gd= new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.verticalAlignment= GridData.VERTICAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_FILL;
	
		layouter.setDefaultGridData(gd, 0);
		layouter.setDefaultGridData(gd, 1);
		layouter.setDefaultSpan();

		layouter.perform(createExpression(result));
		layouter.perform(createSearchFor(result), createLimitTo(result), -1);
		
		SelectionAdapter eglElementInitializer= new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (getSearchFor() == fInitialData.searchFor)
					feglElement= fInitialData.eglElement;
				else
					feglElement= null;
				//handleAllElements(event);
				setLimitTo(getSearchFor());
				updateCaseSensitiveCheckbox();
			}
		};

		int icnt = fSearchFor.length;
		for(int x=0; x<icnt; x++)
			fSearchFor[x].addSelectionListener(eglElementInitializer);
			
		setControl(result);

		Dialog.applyDialogFont(result);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(result, IUIHelpConstants.EGL_SEARCH_DIALOG);
	}
	
	private Control createExpression(Composite parent) {
		Composite result= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout(2, false);
		result.setLayout(layout);
		GridData gd= new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan= 2;
		gd.horizontalIndent= 0;
		result.setLayoutData(gd);

		// Pattern text + info
		Label label= new Label(result, SWT.LEFT);
		label.setText(EGLSearchMessages.EGLSearchPageExpressionLabel);
		gd= new GridData(GridData.BEGINNING);
		gd.horizontalSpan= 2;
//		gd.horizontalIndent= -gd.horizontalIndent;
		label.setLayoutData(gd);

		// Pattern combo
		fPattern= new Combo(result, SWT.SINGLE | SWT.BORDER);
		fPattern.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handlePatternSelected();
			}
		});
		fPattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getContainer().setPerformActionEnabled(getPattern().length() > 0);
				updateCaseSensitiveCheckbox();
			}
		});
		gd= new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalIndent= -gd.horizontalIndent;
		fPattern.setLayoutData(gd);


		// Ignore case checkbox		
		fCaseSensitive= new Button(result, SWT.CHECK);
		fCaseSensitive.setText(EGLSearchMessages.EGLSearchPageExpressionCaseSensitive);
		gd= new GridData();
		fCaseSensitive.setLayoutData(gd);
		fCaseSensitive.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fIsCaseSensitive= fCaseSensitive.getSelection();
				writeConfiguration();
			}
		});
		
		return result;
	}

	private void updateCaseSensitiveCheckbox() {
		if (fInitialData != null && getPattern().equals(fInitialData.pattern) && feglElement != null) {
			fCaseSensitive.setEnabled(false);
			fCaseSensitive.setSelection(true);
		}
		else {
			fCaseSensitive.setEnabled(true);
			fCaseSensitive.setSelection(fIsCaseSensitive);
		}
	}

	private void handlePatternSelected() {

		if( fPattern.getSelectionIndex() < 0 )
			return;
			
		int index = fgPreviousSearchPatterns.size() - 1 - fPattern.getSelectionIndex();
		fInitialData = (SearchPatternData) fgPreviousSearchPatterns.get( index );
		
		updateSelections();

		if( fInitialData.workingSets != null )
			getContainer().setSelectedWorkingSets( fInitialData.workingSets );
		else
			getContainer().setSelectedScope( fInitialData.scope );

	}
	
	private void updateSelections(){
		if (fInitialData == null)
			fInitialData = trySimpleTextSelection( getContainer().getSelection() );
		if (fInitialData == null)
			fInitialData = getDefaultInitValues();

		feglElement = fInitialData.eglElement;
		fIsCaseSensitive = fInitialData.isCaseSensitive;
		fCaseSensitive.setSelection( fInitialData.isCaseSensitive );
		fCaseSensitive.setEnabled( fInitialData.eglElement == null );
		
		//HashSet set = new HashSet( fInitialData.searchFor );
		
		//boolean enabled = ! set.contains( fSearchForValues[ fSearchFor.length - 1 ] );
		
		for (int i = 0; i < fSearchFor.length; i++){
			fSearchFor[i].setSelection( fSearchForValues[i] == fInitialData.searchFor );
			//fSearchFor[i].setEnabled( enabled );			
		}
		
//		if( !enabled )
//			fSearchFor[ fSearchFor.length - 1 ].setEnabled( true );
			
		setLimitTo( fInitialData.searchFor );
			
		for (int i = 0; i < fLimitTo.length; i++)
			fLimitTo[i].setSelection( fLimitToValues[i] == fInitialData.limitTo );

		fPattern.setText( fInitialData.pattern );
	}
	
	private Control createSearchFor(Composite parent) {
		Group result= new Group(parent, SWT.NONE);
		result.setText(EGLSearchMessages.EGLSearchPageSearchForLabel);
		GridLayout layout= new GridLayout();
		layout.numColumns= 5;
		result.setLayout(layout);

		fSearchFor= new Button[fSearchForText.length];
		for (int i= 0; i < fSearchForText.length; i++) {
			Button button= new Button(result, SWT.RADIO);
			button.setText(fSearchForText[i]);
			fSearchFor[i]= button;
		}

		return result;		
	}
	
	private Control createLimitTo(Composite parent) {
		Group result= new Group(parent, SWT.NONE);
		result.setText(EGLSearchMessages.EGLSearchPageLimitToLabel);
		GridLayout layout= new GridLayout();
		layout.numColumns= 1;
		result.setLayout(layout);

		fLimitTo= new Button[fLimitToText.length];
		for (int i= 0; i < fLimitToText.length; i++) {
			Button button= new Button(result, SWT.RADIO);
			button.setText(fLimitToText[i]);
			fLimitTo[i]= button;
		}
		return result;		
	}	
	
	private void initSelections() {	    
		fStructuredSelection = asStructuredSelection();
		fInitialData = tryStructuredSelection( fStructuredSelection );
		updateSelections();
		
	}

	private SearchPatternData tryStructuredSelection(IStructuredSelection selection) {

		if( selection == null || selection.size() > 1 )
			return null;

		Object o = selection.getFirstElement();
		if( o instanceof IEGLElement ) {
			return determineInitValuesFrom( (IEGLElement)o );
		} else if( o instanceof ISearchResultViewEntry ) {
		    IEGLElement element = SearchUtil.getEGLElement( ((ISearchResultViewEntry)o).getSelectedMarker() );
			return determineInitValuesFrom( element );
		} else if( o instanceof IAdaptable ) {
		    IEGLElement element = (IEGLElement)((IAdaptable)o).getAdapter( IEGLElement.class );
			if( element != null ) {
				return determineInitValuesFrom( element );
			} else {
				IWorkbenchAdapter adapter= (IWorkbenchAdapter)((IAdaptable)o).getAdapter( IWorkbenchAdapter.class );
				if( adapter != null ){
					return new SearchPatternData( ALL_ELEMENTS, DECLARATIONS, fIsCaseSensitive, adapter.getLabel(o), null );
				}
			}
		}
		return null;		
	}

	private SearchPatternData determineInitValuesFrom(IEGLElement element) {
		if (element == null)
			return null;
			
		return null;

	}
	
	private SearchPatternData trySimpleTextSelection(ISelection selection) {

		SearchPatternData result= null;
		if (selection instanceof ITextSelection) {
			BufferedReader reader= new BufferedReader(new StringReader(((ITextSelection)selection).getText()));
			String text;
			try {
				text= reader.readLine();
				if (text == null)
					text= ""; //$NON-NLS-1$
			} catch (IOException ex) {
				text= ""; //$NON-NLS-1$
			}
			
			result= new SearchPatternData( ALL_ELEMENTS, REFERENCES, fIsCaseSensitive, text, null);
		}
		return result;
		
	}
	
	private SearchPatternData getDefaultInitValues() {
		return new SearchPatternData(ALL_ELEMENTS, REFERENCES, fIsCaseSensitive, "", null); //$NON-NLS-1$
	}	

	/*
	 * Implements method from ISearchPage
	 */
	public void setContainer(ISearchPageContainer container) {
		fContainer= container;
	}
	
	/**
	 * Returns the search page's container.
	 */
	private ISearchPageContainer getContainer() {
		return fContainer;
	}
	
	/**
	 * Returns the structured selection from the selection.
	 */
	private IStructuredSelection asStructuredSelection() {
		IWorkbenchWindow wbWindow= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (wbWindow != null) {
			IWorkbenchPage page= wbWindow.getActivePage();
			if (page != null) {
				IWorkbenchPart part= page.getActivePart();
				if (part != null)
					try {
						return getStructuredSelection(part);
					} catch (Exception ex) {
					}
			}
		}
		return StructuredSelection.EMPTY;
	}
	
	//--------------- Configuration handling --------------
	
	/**
	 * Returns the page settings for this Java search page.
	 * 
	 * @return the page settings to be used
	 */
	private IDialogSettings getDialogSettings() {
		IDialogSettings settings= EDTUIPlugin.getDefault().getDialogSettings();
		fDialogSettings= settings.getSection(PAGE_NAME);
		if (fDialogSettings == null)
			fDialogSettings= settings.addNewSection(PAGE_NAME);
		return fDialogSettings;
	}
	
	/**
	 * Initializes itself from the stored page settings.
	 */
	private void readConfiguration() {
		IDialogSettings s= getDialogSettings();
		fIsCaseSensitive= s.getBoolean(STORE_CASE_SENSITIVE);
	}
	
	/**
	 * Stores it current configuration in the dialog store.
	 */
	private void writeConfiguration() {
		IDialogSettings s= getDialogSettings();
		s.put(STORE_CASE_SENSITIVE, fIsCaseSensitive);
	}

	/**
	 * Copied from SelectionConverter.getStructuredSelection(IWorkbenchPart)
	 * 
	 * Converts the selection provided by the given part into a structured selection.
	 * The following conversion rules are used:
	 * <ul>
	 *	<li><code>part instanceof JavaEditor</code>: returns a structured selection
	 * 	using code resolve to convert the editor's text selection.</li>
	 * <li><code>part instanceof IWorkbenchPart</code>: returns the part's selection
	 * 	if it is a structured selection.</li>
	 * <li><code>default</code>: returns an empty structured selection.</li>
	 * </ul>
	 */
	public IStructuredSelection getStructuredSelection(IWorkbenchPart part) { //throws JavaModelException {
		ISelectionProvider provider= part.getSite().getSelectionProvider();
		if (provider != null) {
			ISelection selection= provider.getSelection();
			if (selection instanceof IStructuredSelection)
				return (IStructuredSelection)selection;
		}
		return StructuredSelection.EMPTY;
	}

}
