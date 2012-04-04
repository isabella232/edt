/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.compiler.IGenerator;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.IIDECompiler;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.preferences.CompilerSelectionPreferencePage;
import org.eclipse.edt.ide.ui.internal.preferences.PropertyAndPreferencePage;
import org.eclipse.edt.ide.ui.internal.util.PixelConverter;
import org.eclipse.edt.ide.ui.internal.util.TabFolderLayout;
import org.eclipse.edt.ide.ui.internal.wizards.IStatusChangeListener;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.ibm.icu.text.MessageFormat;

public class CompilerPropertyAndPreferencePage extends PropertyAndPreferencePage {

	public static final String PROP_ID = "org.eclipse.edt.ide.ui.folderCompilerPropertyPage";//$NON-NLS-1$

	public static final String EXTENSIONPOINT_GENERATOR_TABS = org.eclipse.edt.ide.ui.EDTUIPlugin.PLUGIN_ID + ".edtGeneratorTabs"; //$NON-NLS-1$
	public static final String GENERATOR_TAB = "generatorTab"; //$NON-NLS-1$
	public static final String GENERAL_TAB = "generalTab"; //$NON-NLS-1$
	public static final String GENERATOR_TAB_GENERATOR_ID = "generatorId"; //$NON-NLS-1$
	public static final String GENERAL_TAB_NAME = "tabName"; //$NON-NLS-1$
	public static final String GENERAL_TAB_COMPILER_ID = "compilerId"; //$NON-NLS-1$
	public static final String TAB_CLASS = "class"; //$NON-NLS-1$

	// The resource for a property page (project, folder, or file)
	protected IResource resource;

	private CompilerAndGeneratorControls compilerAndGeneratorControls;

	// Currently selected compiler name
	private String  selectedCompiler = new String();

	// Currently selected generator names
	protected List<String> selectedGenerators = new ArrayList<String>();
	
	// Generator properties override section
	private Composite generatorComposite;
	protected Button generatorOverrideCheckbox;
	private ControlEnableState generatorEnableState;
	

	// Folder and composite to update
	protected TabFolder tabFolder;
	protected Composite tabComposite;

	protected List<TabItem> currTabItems = new ArrayList<TabItem>();
	protected List<IGeneratorTabProvider> currTabProviders =  new ArrayList<IGeneratorTabProvider>();
	protected List<IGeneratorTabProvider> allTabProviders =  new ArrayList<IGeneratorTabProvider>();

	// Default workspace compiler id in case the stored value is now invalid
	protected String defaultWSCompilerId;
	
	/**
	 *  A listener that allows messages to be displayed on the preference or property
	 *   page that contains this generator tab.
	 */
	private IStatusChangeListener statusChangeListener;
	
	protected StatusInfo latestStatus = new StatusInfo();
	
	public CompilerPropertyAndPreferencePage() {
		this.statusChangeListener = getNewStatusChangedListener();
	}
	
	/**
	 * Create the content for this page.
	 * 
	 * @param Composite
	 * @return Composite
	 */
	protected Composite createPreferenceContent( Composite parent ) {		
		initialize();
		
		Composite composite = new Composite(parent, SWT.NULL);

		//GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setFont(parent.getFont());

		//GridData
		GridData data = new GridData(GridData.FILL);
		data.horizontalIndent = 0;
		data.verticalAlignment = GridData.FILL;
		composite.setLayoutData(data);		

		if( this.resource == null && !isValidWorkspaceExtensions() ) {
			return composite;
		}		
 		compilerAndGeneratorControls = new CompilerAndGeneratorControls( this );

		// Preference page
		if( this.resource == null ) {
			// Compiler selection preference page
			if(	getPreferencePageCompilerId() == CompilerSelectionPreferencePage.COMPILER_SELECTION_ID ) {
				// Set selectedCompiler
				setSelectedCompiler( convertCompilerIdToName( defaultWSCompilerId ) );
				createCompilerComposite( composite );
			} else if( getPreferencePageCompilerId().length() > 0 ) {
				// Set selectedGenerators
				List<String> genNamesInCompiler = new ArrayList<String>();
				List<String> genList = getWorkspaceSelectedGenerators();
				for( String genName : genList ) {
					if( isGeneratorForCompiler( convertGeneratorNameToId( genName ), getPreferencePageCompilerId() ) ) {
						genNamesInCompiler.add( genName );
					}
				}
				setSelectedGenerators( new ArrayList<String>( genNamesInCompiler ) );
				createGeneratorsComposite( composite );
				createGeneratorTabsComposite( composite );
				createTabProviders();
				addCompilerTabs( convertCompilerIdToName( getPreferencePageCompilerId() ) );	

			}
		} else {
			// Property page
			IProject project = resource.getProject();
			setSelectedCompiler( getCompilerName( project ) );
			createCompilerComposite( composite );
			
			if (resource.getType() != IResource.PROJECT) {
				fUseProjectSettings.setEnabled( false );
			}

			validateGenerators();
			if( latestStatus.isOK() ) {
				// Get generator ids from the project.  If none, get from workspace preferences.
				setSelectedGenerators( getGeneratorNames() );
				
				generatorOverrideCheckbox = new Button( composite, SWT.CHECK );
				data = new GridData( GridData.FILL_HORIZONTAL );
				data.verticalIndent = 10;
				generatorOverrideCheckbox.setLayoutData( data );
				
				generatorComposite = new Composite( composite, SWT.NULL );
				layout = new GridLayout();
				layout.numColumns = 1;
				generatorComposite.setLayout(layout);
				generatorComposite.setFont(parent.getFont());
				data = new GridData(GridData.FILL_BOTH);
				data.horizontalIndent = 0;
				generatorComposite.setLayoutData(data);
				
				createGeneratorsComposite( generatorComposite );
				createGeneratorTabsComposite( generatorComposite );
				createTabProviders();
				addCompilerTabs( this.selectedCompiler );
				
				generatorOverrideCheckbox.addSelectionListener( new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						updateGeneratorControlEnablement(((Button)e.widget).getSelection());
					};
				});
				
				// Initialize label and checked state
				Preferences genPrefs = new ProjectScope( resource.getProject() ).getNode( EDTCoreIDEPlugin.PLUGIN_ID).node( ProjectSettingsUtility.PROPERTY_GENERATOR_IDS );
				IPath parentSettingPath = null;
				if (resource.getType() != IResource.PROJECT) {
					parentSettingPath = ProjectSettingsUtility.findPathForSetting( resource.getFullPath().removeLastSegments( 1 ), genPrefs );
				}
				
				// Label is based on the parent path
				if (parentSettingPath == null) {
					generatorOverrideCheckbox.setText( UINlsStrings.CompilerPropertyPage_overrideWorkspaceGenPrefs );
				}
				else {
					generatorOverrideCheckbox.setText( NLS.bind( UINlsStrings.CompilerPropertyPage_overrideResourceGenPrefs, parentSettingPath.toString() ) );
				}
				
				initGeneratorOverrideCheckboxState();
			}
		}

		Dialog.applyDialogFont(composite);
		return composite;
	}
	
	public void updateGeneratorControlEnablement(boolean enabled) {
		if (enabled) {
			if (generatorEnableState != null) {
				generatorEnableState.restore();
				generatorEnableState = null;
			}
		} else {
			if (generatorEnableState == null) {
				generatorEnableState = ControlEnableState.disable(generatorComposite);
			}
		}
	}
	
	private void initGeneratorOverrideCheckboxState() {
		// Enablement is based on whether this specific resource has the setting
		Preferences genPrefs = new ProjectScope( resource.getProject() ).getNode( EDTCoreIDEPlugin.PLUGIN_ID).node( ProjectSettingsUtility.PROPERTY_GENERATOR_IDS );
		setGeneratorOverrideCheckboxState( ProjectSettingsUtility.findSetting( resource.getFullPath(), genPrefs, false ) != null );
	}
	
	public void setGeneratorOverrideCheckboxState(boolean checked) {
		if ( checked ) {
			generatorOverrideCheckbox.setSelection( true ); // won't fire an event
			updateGeneratorControlEnablement( true );
		}
		else {
			generatorOverrideCheckbox.setSelection( false ); // won't fire an event
			updateGeneratorControlEnablement( false );
		}
	}
	
	protected boolean isValidWorkspaceExtensions() {
		validateCompilers();
		if( !latestStatus.isOK() ) {
			return false;
		} 
		// Validate the saved compiler still exists in the workspace
		defaultWSCompilerId = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.COMPILER_ID );
		if( defaultWSCompilerId != null & defaultWSCompilerId.length() > 0 ) {
			if( !validateCompilerExists( defaultWSCompilerId ) ) {
				promptForNewCompiler( defaultWSCompilerId );
				if( !latestStatus.isOK() ) {
					return false;
				} else {
					defaultWSCompilerId = getFirstValidCompiler();
				}
			}
		}
		// If on a compiler preference page, verify the compiler still exists in the workspace
		if ( this.resource == null ) {
			String id = getPreferencePageCompilerId();
			if( id != CompilerSelectionPreferencePage.COMPILER_SELECTION_ID ) {
				if( !validateCompilerExists( id ) ) {
					String message = MessageFormat.format( UINlsStrings.CompilerPreferencePage_compilerNoLongerExistsError, 
							new Object[] { id } );
					latestStatus.setWarning( message );
					statusChangeListener.statusChanged( latestStatus );
					return false;
				}
				validateGenerators();
				if( !latestStatus.isOK() ) {
					return false;
				}
				validateWSSelectedGeneratorsExist();
				if( !latestStatus.isOK() ) {
					return false;
				}
			}
		}
		return true;
	}
	
	protected boolean isValidCompilers() {
		validateCompilers();
		return latestStatus.isOK();
	}
	
	protected boolean isValidGenerators() {
		validateGenerators();
		return latestStatus.isOK();
	}
	
	protected void validateCompilers() {
		IIDECompiler[] availableCompilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
		List<String> compilerIds = new ArrayList<String>();
		List<String> compilerNames = new ArrayList<String>();
		for( IIDECompiler comp : availableCompilers ) {			
			if( compilerNames.contains( comp.getName() ) ) {
				latestStatus.setWarning( MessageFormat.format( UINlsStrings.CompilerPreferencePage_duplicateCompilerNames, 
						new Object[] { comp.getName() } ) );
				statusChangeListener.statusChanged( latestStatus );
			} else {
				compilerNames.add( comp.getName() );
			}
			if( compilerIds.contains( comp.getId() ) ) {
				latestStatus.setWarning( MessageFormat.format( UINlsStrings.CompilerPreferencePage_duplicateCompilerIds, 
						new Object[] { comp.getId() } ) );
				statusChangeListener.statusChanged( latestStatus );
			} else {
				compilerIds.add( comp.getId() );
			}
		}
	}
	
	/**
	 * Return whether a compiler id is available in the workspace
	 * (i.e. an extension was defined for that compiler)
	 * 
	 * @param id
	 * @return whether a compiler exists in the workspace
	 */
	protected boolean validateCompilerExists( String id ) {
		IIDECompiler[] availableCompilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
		for( IIDECompiler comp : availableCompilers ) {		
			if( comp.getId().equalsIgnoreCase( id ) ) {
				return true;
			}
		}
		return false;
	}
	
	protected void validateGenerators() {
		IIDECompiler[] availableCompilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
		for( IIDECompiler compiler : availableCompilers ) {
			List<IGenerator> availableGenerators = compiler.getGenerators();
			List<String> generatorIds = new ArrayList<String>();
			List<String> generatorNames = new ArrayList<String>();
			for( IGenerator gen : availableGenerators ) {			
				if( generatorNames.contains( gen.getName() ) ) {
					latestStatus.setWarning( MessageFormat.format( UINlsStrings.CompilerPreferencePage_duplicateGeneratorNames, 
							new Object[] { gen.getName() } ) );
					statusChangeListener.statusChanged( latestStatus );
				} else {
					generatorNames.add( gen.getName() );
				}
				if( generatorIds.contains( gen.getId() ) ) {
					latestStatus.setWarning( MessageFormat.format( UINlsStrings.CompilerPreferencePage_duplicateGeneratorIds, 
							new Object[] { gen.getId() } ) );
					statusChangeListener.statusChanged( latestStatus );
				} else {
					generatorIds.add( gen.getId() );
				}
			}
		}
	}
	
	protected boolean validateWSSelectedGeneratorsExist() {
		IGenerator[] availableGens = EDTCoreIDEPlugin.getPlugin().getGenerators();
		List<IGenerator> availableGensList = Arrays.asList( availableGens );
		List<String> selectedGens = getWorkspaceSelectedGenerators();
		for( String genName : selectedGens ) {
			String id = convertGeneratorNameToId( genName );
			IGenerator gen = getGeneratorFromId( id );
			if( !availableGensList.contains( gen ) ) {
				return false;
			}
		}
		return true;
	}
	
	protected void promptForNewCompiler( String errorCompilerId ) {
		String message = MessageFormat.format( UINlsStrings.CompilerPreferencePage_compilerNoLongerAvailable, 
				new Object[] { errorCompilerId } );
		MessageDialog dialog = new MessageDialog(getShell(), 
				UINlsStrings.CompilerPreferencePage_compilerNoLongerAvailable_title, 
				null, message, MessageDialog.QUESTION, 
				new String[] { IDialogConstants.YES_LABEL, 
				IDialogConstants.NO_LABEL }, 2)
		{
			protected int getMinimumMessageWidth() {
				return convertHorizontalDLUsToPixels(
						(int)(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH * 1.5));
	        }
	        protected Control createCustomArea(Composite parent) {
	        	Composite composite = new Composite(parent, SWT.NONE);
	            GridLayout layout= new GridLayout();
	 			composite.setLayout(layout);
	 			GridData gridData= new GridData(SWT.FILL, SWT.FILL, false, false);
		        gridData.widthHint= this.getMinimumMessageWidth();
		        composite.setLayoutData(gridData);
	            return composite;
	        }
	 	};
		int res = dialog.open();
		if (res == IDialogConstants.CANCEL_ID) { // cancel pressed
			latestStatus.setWarning( MessageFormat.format( UINlsStrings.CompilerPreferencePage_cannotDisplayCompiler, 
					new Object[] { errorCompilerId } ) );
			statusChangeListener.statusChanged( latestStatus );
		}
	}

	/**
	 * Initialize
	 */
	protected void initialize() {
		setDescription( UINlsStrings.CompilerPreferencePage_description );
	}

	protected void createCompilerComposite( Composite composite ) {
		compilerAndGeneratorControls.createCompilersComposite( composite );
	}

	protected void createGeneratorsComposite( Composite composite ) {
		compilerAndGeneratorControls.createGeneratorsComposite( composite );
	}

	/**
	 * Create a composite to hold generator tabs for a property
	 * page or a preference page for a compiler.
	 * 
	 * @param composite
	 */
	protected void createGeneratorTabsComposite( Composite composite ) {
		this.tabComposite = new Composite(composite, SWT.NONE);	

		GridLayout tabLayout = new GridLayout();
		tabLayout.marginWidth = 0;
		tabLayout.numColumns = 1;
		PixelConverter pixelConverter = new PixelConverter(composite);
		tabLayout.verticalSpacing = (int)(1.5 * pixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING));
		tabLayout.horizontalSpacing = pixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		tabLayout.marginTop = 3;
		tabLayout.marginWidth = pixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		
		this.tabComposite.setLayout(tabLayout);
		this.tabComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.tabFolder = new TabFolder( this.tabComposite, SWT.NONE );
		this.tabFolder.setLayout(new TabFolderLayout());	
		this.tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	/**
	 * Return a list of default generator names derived from workspace preferences.
	 * 
	 * @return
	 */
	protected List<String> getWorkspaceSelectedGenerators() {
		String genString = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.GENERATOR_IDS );
		if( genString != null && genString.length() > 0 ) {
			genString = genString.trim();
			if( genString.length() != 0 ) {
				String[] gens = genString.split( "," );					
				// trim each value
				List<String> explicitGens = new ArrayList<String>(gens.length);
				for( int i = 0; i < gens.length; i++ ) {
					String id = gens[i].trim();
					IGenerator gen = getGeneratorFromId(id);
					if (gen != null && gen.getEnabledWith() != null && gen.getEnabledWith().length() > 0) {
						continue;
					}
					explicitGens.add( convertGeneratorIdToName(id) );
				}
				return explicitGens;
			}
		}
		return new ArrayList<String>(0);
	}

	protected void createTabProviders() {
		IGeneratorTabProvider newTabProvider;
		// Get all tab extensions
		IConfigurationElement[] generatorTabContributions = Platform.getExtensionRegistry().getConfigurationElementsFor( EXTENSIONPOINT_GENERATOR_TABS );
		if( generatorTabContributions != null ) {
			String compilerId;
			String generatorId;
			// Loop through generator/general tab extensions
			for( IConfigurationElement currContribution : generatorTabContributions ) {
				generatorId = null;
				compilerId = currContribution.getAttribute( GENERAL_TAB_COMPILER_ID );
				if( compilerId == null ) {
					// Must be a generatorTab extension
					generatorId = currContribution.getAttribute( GENERATOR_TAB_GENERATOR_ID);
					IGenerator[] gens = EDTCoreIDEPlugin.getPlugin().getGenerators();
					for( IGenerator gen : gens ) {
						if( gen.getId().equalsIgnoreCase( generatorId ) ) {
							compilerId = gen.getCompiler().getId();
							break;
						}
					}
				}
				try {
					Object o = currContribution.createExecutableExtension( TAB_CLASS );
					if( o instanceof IGeneratorTabProvider ) {
						newTabProvider = (IGeneratorTabProvider)o ;
						// TODO EDT Get list of default prefs/values from tab provider to put in ws pref store
						
						// Resource must be set in provider when the tab is displayed on
						// a properties page.
						if( this.resource != null ) {
							newTabProvider.setResource( this.resource );
						}
						newTabProvider.setStatusChangeListener( getNewStatusChangedListener() );
						if( compilerId != null ) {
							newTabProvider.setCompilerId( compilerId );
						}
						if( generatorId != null ) {
							newTabProvider.setGeneratorId( generatorId );
							String name = convertGeneratorIdToName( generatorId );
							newTabProvider.setTitle( name );
						} else {
							// Use name from tab
							String name = currContribution.getAttribute( GENERAL_TAB_NAME );
							newTabProvider.setTitle( name );
						}
						if( !this.allTabProviders.contains( newTabProvider ) ) {
							this.allTabProviders.add( newTabProvider );
						}
					}
				} catch (CoreException ce) {
					EDTUIPlugin.log( ce );
				}
			}
		}
	}
	
	/**
	 * Add a generator tab for each generator in a compiler.
	 * 
	 * @param compilerName
	 */
	protected void addCompilerTabs( String compilerName ) {		
		String title;
		IGeneratorTabProvider tabProvider;
		for( int i = 0; i < this.allTabProviders.size(); i++ ) {
			tabProvider = this.allTabProviders.get(i);
			title = tabProvider.getTitle();
			boolean compilerOk = false;
			boolean generalTab = false;
			if( tabProvider.getGeneratorId() == null ) { // general tab
				if( tabProvider.getCompilerId().equalsIgnoreCase(convertCompilerNameToId( compilerName ))) {
					compilerOk = true;
					generalTab = true;
				}
			}  else { // generator tab
				compilerOk = isGeneratorForCompiler( convertGeneratorNameToId( title ), 
						convertCompilerNameToId( compilerName ) );
			}
			if( compilerOk ) {
				// If preference page or generator is selected in properties
				if( this.selectedGenerators.contains( title ) || generalTab ) {
					addTabItemForProvider( this.allTabProviders.get(i) );
				}
			}
		}
		// Select the first generator tab that was added
		if( currTabItems.size() > 1 ) {
			tabFolder.setSelection( 0 );
		}
	}

	/**
	 * Adds a tab for the generator named genName.
	 * 
	 * @param genName
	 */
	protected void addGeneratorTab( String genName ) {
		IGeneratorTabProvider provider = getGenTabProviderForName( genName );
		if( provider != null ) {
			addTabItemForProvider( provider );
		}
	}

	/**
	 * Creates a tab using a generator tab provider.
	 * 
	 * @param provider
	 */
	protected void addTabItemForProvider( IGeneratorTabProvider provider ) {
		TabItem item = new TabItem( this.tabFolder, SWT.NONE );

		int index = allTabProviders.indexOf( provider );
		item.setText( allTabProviders.get( index ).getTitle() );
		if( provider.getImage() != null ) {
			item.setImage( provider.getImage() );
		}

		item.setControl( provider.getTabContent( this.tabFolder ) );
		this.currTabItems.add( item );
		this.currTabProviders.add( provider );
		provider.performAddition();
		this.tabFolder.setSelection( item );
		this.tabFolder.getParent().getParent().layout(); 	
	}

	/**
	 * Return the generator tab provider for the given generator
	 * name.
	 * 
	 * @param genName
	 * @return a generator tab provider
	 */
	protected IGeneratorTabProvider getGenTabProviderForName( String genName ) {
		for( int i = 0; i < this.allTabProviders.size(); i++ ) {
			if( this.allTabProviders.get(i).getTitle().equalsIgnoreCase( genName ) ) {
				return this.allTabProviders.get(i);
			}
		}
		return null;
	}

	protected List<IConfigurationElement> getTabContributionsForSelectedCompiler() {
		List<IConfigurationElement> tabContrs = new ArrayList<IConfigurationElement>();
		// Get all tab extensions
		IConfigurationElement[] generatorTabContributions = Platform.getExtensionRegistry().getConfigurationElementsFor( EXTENSIONPOINT_GENERATOR_TABS );
		if( generatorTabContributions != null ) {
			String compilerId;
			// Loop through generator/general tab extensions
			for( IConfigurationElement currContribution : generatorTabContributions ) {
				compilerId = currContribution.getAttribute( GENERAL_TAB_COMPILER_ID );
				if( compilerId == null ) {
					// Must be a generatorTab extension
					String generatorId = currContribution.getAttribute( GENERATOR_TAB_GENERATOR_ID);
					IGenerator[] gens = EDTCoreIDEPlugin.getPlugin().getGenerators();
					for( IGenerator gen : gens ) {
						if( gen.getId().equalsIgnoreCase( generatorId ) ) {
							compilerId = gen.getCompiler().getId();
							break;
						}
					}
				}
				if( this.resource != null ) {	
					// If property page, return tab if compilerId equals selected compiler
					if( convertCompilerNameToId( this.selectedCompiler ).equalsIgnoreCase( compilerId ) ) 
					{
						tabContrs.add( currContribution );
					} 
				}  else {
					// Check if the compiler id on the tab matches the compiler id
					// of this preference page.
					if( getPreferencePageCompilerId().equalsIgnoreCase( compilerId ) ) {
						tabContrs.add( currContribution );
					}
				}
			}
		}
		return tabContrs;
	}	

	protected List<IConfigurationElement> getTabContributions() {
		List<IConfigurationElement> tabContrs = new ArrayList<IConfigurationElement>();
		// Get all tab extensions
		IConfigurationElement[] generatorTabContributions = Platform.getExtensionRegistry().getConfigurationElementsFor( EXTENSIONPOINT_GENERATOR_TABS );
		if( generatorTabContributions != null ) {
			String compilerId;
			// Loop through generator/general tab extensions
			for( IConfigurationElement currContribution : generatorTabContributions ) {
				compilerId = currContribution.getAttribute( GENERAL_TAB_COMPILER_ID );
				if( compilerId == null ) {
					// Must be a generatorTab extension
					String generatorId = currContribution.getAttribute( GENERATOR_TAB_GENERATOR_ID);
					IGenerator[] gens = EDTCoreIDEPlugin.getPlugin().getGenerators();
					for( IGenerator gen : gens ) {
						if( gen.getId().equalsIgnoreCase( generatorId ) ) {
							compilerId = gen.getCompiler().getId();
							break;
						}
					}
				}
				tabContrs.add( currContribution );
			}
		}
		return tabContrs;
	}	

	/**
	 * Dispose of all current tab items.  Clear the lists containing
	 * the current tab providers and tab items.
	 */
	protected void removeCurrentCompilerTabs() {
		// Dispose of each tab item
		for( TabItem currItem : this.currTabItems ) {
			currItem.dispose();
		}
		// Clear the lists
		this.currTabProviders.clear();
		this.currTabItems.clear();
	}

	/**
	 * Dispose of the tab item and provider for a specific generator.
	 * 
	 * @param genName
	 */
	protected void removeGeneratorTab( String genName ) {
		TabItem currItem;
		for( int i=0; i < currTabItems.size(); i++ ) {
			currItem = currTabItems.get(i);
			if( currItem.getText().equalsIgnoreCase( genName )) {
				currItem.dispose();
				this.currTabItems.remove(i);
				this.currTabProviders.get(i).performRemoval();
				this.currTabProviders.remove(i);
				break;
			}									
		}
		
		for(int i=0; i < currTabProviders.size(); i++) {
			this.currTabProviders.get(i).performAddition();
		}
	}

	/**
	 * Return the id of the preference page corresponding to the selected
	 * compiler from the compiler's extension.
	 * 
	 * @return String
	 */
	protected String getPreferencePageID() {
		String compilerId = convertCompilerNameToId( selectedCompiler );
		IIDECompiler[] compilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
		for( IIDECompiler currCompiler : compilers ) {
			if( currCompiler.getId().equalsIgnoreCase( compilerId ) ) {
				return currCompiler.getPreferencePageId();
			}
		}
		return null;
	}

	/**
	 * Return the id of the compilers property page.
	 * 
	 * @return String
	 */
	protected String getPropertyPageID() {
		return PROP_ID;
	}

	/**
	 * Return the id of the compiler that this preference page is for.
	 * This method should only be called when processing workspace preference
	 * pages, not property pages.
	 * 
	 * @return String
	 */
	protected String getPreferencePageCompilerId() {
		return "";
	}

	/**
	 * Return true if the project has data stored in its preference store.
	 */
	protected boolean hasProjectSpecificOptions(IProject project) {
		if( ( project != null ) && useDefaultCompilerAndGenerators( project ) ) {
			return false;
		} else {
			return true;
		}
	}	
	
	protected boolean useDefaultCompilerAndGenerators( IProject project ) {
		if( ProjectSettingsUtility.getCompilerId( project ) == null &&
				ProjectSettingsUtility.getGeneratorIds( project ) == null ) {
			return true;
		} else {
			return false;
		}
	}

	public void performDefaults() {
		if( resource == null ) {
			if( getPreferencePageCompilerId() == CompilerSelectionPreferencePage.COMPILER_SELECTION_ID ) {
				String defaultCompilerId = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getDefaultString( EDTCorePreferenceConstants.COMPILER_ID );
				setSelectedCompiler( convertCompilerIdToName( defaultCompilerId ) );
				compilerAndGeneratorControls.getCompilerComponent().selectCompiler( selectedCompiler );
			} else {
				String genString = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getDefaultString( EDTCorePreferenceConstants.GENERATOR_IDS );
				if( genString != null ) {
					genString = genString.trim();
					if( genString.length() != 0 ) {
						String[] gens = genString.split( "," );					
						// trim each value
						for( int i = 0; i < gens.length; i++ ) {
							gens[i] = convertGeneratorIdToName( gens[i].trim() );
						}
						clearSelectedGenerators();
						setSelectedGenerators( new ArrayList<String>( Arrays.asList( gens ) ) );
					}
				}
				compilerAndGeneratorControls.getCompilerComponent().showCompilerPreferencePageBookPage();
				
				removeCurrentCompilerTabs();
				addCompilerTabs( convertCompilerIdToName( getPreferencePageCompilerId() ) );
				for( IGeneratorTabProvider currProvider : this.currTabProviders ) {
					currProvider.performDefaults();
				}
			}
		} else {
			// Revert ControlEnableStates before we do anything else, which might be enabling/disabling controls programatically.
			enablePreferenceContent( true );
			updateGeneratorControlEnablement( true );
			
			// Check box if using workspace defaults
			boolean useProjectSettings= hasProjectSpecificOptions(getProject());
			enableProjectSpecificSettings(useProjectSettings);

			setSelectedCompiler( getCompilerName( getProject() ) );
			setSelectedGenerators( getGeneratorNames() );

			compilerAndGeneratorControls.getCompilerComponent().showCompilerPropertyPageBookPage( selectedCompiler );

			removeCurrentCompilerTabs();
			addCompilerTabs( selectedCompiler );
			for( IGeneratorTabProvider currProvider : this.currTabProviders ) {
				currProvider.performDefaults();
			}
			
			initGeneratorOverrideCheckboxState();
		}
	}

	public boolean performCancel() {
		if( this.resource != null && !useProjectSettings() ) {
			return true;
		}
		boolean retValue = true;
		for( IGeneratorTabProvider currProvider : this.currTabProviders ) {
			if( !currProvider.performCancel() ) {
				retValue = false;
			}				
		}
		return retValue;
	}
	
	public void performApply() {
		doPerformOk( true );
	}

	public boolean performOk() {
		return doPerformOk( false );
	}
	
	protected boolean doPerformOk( final boolean isApply ) {
		final boolean[] retValue = {true};
		
		// Use a WorkspaceModifyOperation to prevent builds from running before we've finished updating settings.
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			protected void execute( IProgressMonitor monitor ) throws CoreException, InvocationTargetException, InterruptedException {
				// If 'enable project specific settings' is not checked for a project,
				// use workspace settings.
				if ( resource != null && !useProjectSettings() ) {
					try {
						ProjectSettingsUtility.setCompiler( getProject(),  null );
						ProjectSettingsUtility.clearAllGeneratorIds( getProject() );
						for ( IGeneratorTabProvider tabProvider : allTabProviders ) {
							tabProvider.removePreferencesForAllResources();
						}
					}
					catch ( BackingStoreException e ) {
						EDTUIPlugin.log( e );
						retValue[0] = false;
					}
					return;
				}			
				else if ( resource == null || generatorOverrideCheckbox.getSelection() ) {
					for ( IGeneratorTabProvider currProvider : currTabProviders ) {
						if ( isApply ) {
							currProvider.performApply();
						}
						else {
							if ( !currProvider.performOk() ) {
								retValue[0] = false;
							}
						}
					}
				}
				else {
					for ( IGeneratorTabProvider currProvider : allTabProviders ) {
						currProvider.removePreferencesForAResource();
					}
				}
				storeValues();
				// If a generator is no longer selected, remove its properties from the
				// resources' preference store.
				if ( resource != null && generatorOverrideCheckbox.getSelection() ) {
					removeTabPropertiesFromProjectResources();
				}
			}
		};
		
		try {
			PlatformUI.getWorkbench().getProgressService().run( false, true, op );
		}
		catch ( InterruptedException e ) {
		}
		catch ( InvocationTargetException e ) {
			EDTUIPlugin.log( e.getTargetException() );
		}
		
		return retValue[0];
	}

	/**
	 * If on a property page, save the compilers and generators that 
	 * were selected on the project's preferences.
	 */
	protected void storeValues() {
		if( this.resource == null ) {
			if( getPreferencePageCompilerId() == CompilerSelectionPreferencePage.COMPILER_SELECTION_ID  ) {
				if( this.selectedCompiler.length() > 0 ) {
					String id = convertCompilerNameToId( this.selectedCompiler );
					if( id.length() > 0 ) {
						EDTCoreIDEPlugin.getPlugin().getPreferenceStore().setValue( 
								EDTCorePreferenceConstants.COMPILER_ID, id
						);
					}
				}
			} else {
				String[] genIds = new String[this.selectedGenerators.size()];
				String compilerId = selectedGenerators.size() == 0 ? "" : getGeneratorFromId(convertGeneratorNameToId(selectedGenerators.get(0))).getCompiler().getId();
				int index = 0;
				StringBuilder buffer = new StringBuilder(100);
				List<String> addedIDs = new ArrayList(this.selectedGenerators.size() + 10);
				for( String genName : this.selectedGenerators ) {
					genIds[index] = convertGeneratorNameToId( genName );
					
					if (!addedIDs.contains(genIds[index])) {
						if (index != 0) {
							buffer.append(',');
						}
						buffer.append( genIds[index] );
						addedIDs.add(genIds[index]);
					}
					
					// If there's a generator depending on this one, add it
					for (IGenerator gen : EDTCoreIDEPlugin.getPlugin().getGenerators()) {
						if (gen.getCompiler().getId().equals(compilerId)) {
							String enabledWith = gen.getEnabledWith();
							if (enabledWith != null && enabledWith.equals(genIds[index]) && !addedIDs.contains(gen.getId())) {
								buffer.append(',');
								buffer.append( gen.getId() );
								addedIDs.add(gen.getId());
							}
						}
					}
					
					index++;
				}
				replaceGeneratorsInPrefStoreForCompiler( getPreferencePageCompilerId(), buffer.toString() );
			}
		} else {
			try {	
				// Store compiler
				String compilerId = convertCompilerNameToId( this.selectedCompiler );
				String previousCompilerId = ProjectSettingsUtility.getCompilerId( getProject() );
				if( !compilerId.equalsIgnoreCase( previousCompilerId ) ) {
					// Compiler changed
					if( previousCompilerId != null ) {
						// Remove old generators for resources in the project.
						ProjectSettingsUtility.clearAllGeneratorIds( getProject() );

						// Remove old generator tab preferences for resources in the project.
						for( IGeneratorTabProvider tabProvider : this.allTabProviders) {
							if ( tabProvider.getCompilerId().equals( previousCompilerId ) ) {
								tabProvider.removePreferencesForAllResources();
							}
						}
					}
					// Set the new compiler.
					ProjectSettingsUtility.setCompiler( getProject(), compilerId );
				}				

				// Store generators
				if (generatorOverrideCheckbox.getSelection()) {
					List<String> genIds = new ArrayList<String>();
					List<String> newSelectedGens = new ArrayList<String>();
					String id;
					for( String genName : this.selectedGenerators ) {
						id = convertGeneratorNameToId( genName );
						IGenerator gen = getGeneratorFromId( id );
						// At the project level, only store the generators corresponding
						// to the selected compiler.  At the folder/file level, only the
						// selected compiler is available so don't need to check this.
						if( resource instanceof IProject ) {
							if( !gen.getCompiler().getId().equalsIgnoreCase( compilerId ) ) {
								continue;
							}
						}
						
						if (!genIds.contains(id)){
							genIds.add( id );
						}
						
						// If there's a generator depending on this one, add it
						for (IGenerator next : EDTCoreIDEPlugin.getPlugin().getGenerators()) {
							if (next.getCompiler().getId().equals(compilerId)) {
								String enabledWith = next.getEnabledWith();
								if (enabledWith != null && enabledWith.equals(id) && !genIds.contains(next.getId())) {
									genIds.add(next.getId());
								}
							}
						}
						
						newSelectedGens.add( genName );
					}
					ProjectSettingsUtility.setGeneratorIds( this.resource, genIds.toArray( new String[genIds.size()] ) );
					setSelectedGenerators( newSelectedGens );
				}
				else {
					ProjectSettingsUtility.setGeneratorIds( this.resource, null );
					setSelectedGenerators( new ArrayList<String>( 0 ) );
				}
			}
			catch( BackingStoreException e ) {
				EDTUIPlugin.log( e );
			}
		}
	}

	protected IGenerator getGeneratorFromId( String generatorId ) {
		IGenerator[] gens = EDTCoreIDEPlugin.getPlugin().getGenerators();
		for( IGenerator gen : gens ) {
			if( gen.getId().equalsIgnoreCase( generatorId ) ) {
				return gen;
			}
		}
		// Shouldn't happen
		return null;
	}
	
	protected boolean isSelectedGeneratorsSameAsDefault( List<String> newSelectedGens ) {
		String defaultGens = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( 
				EDTCorePreferenceConstants.GENERATOR_IDS );
		if( defaultGens != null && defaultGens.length() > 0 ) {
			defaultGens = defaultGens.trim();
			if( defaultGens.length() != 0 ) {
				String[] gens = defaultGens.split( "," );	
				if( gens.length != newSelectedGens.size()) {
					return false;
				}
				for( int i = 0; i < gens.length; i++ ) {
					gens[i] = gens[i].trim();
					if( !newSelectedGens.contains(gens[i]) ) {
						return false;
					}
				}
			}
		}
		return true;		
	}

	protected boolean isGeneratorForCompiler( String genId, String compilerId ) {
		IGenerator gen = getGeneratorFromId( genId );
		if( gen != null && gen.getCompiler().getId().equalsIgnoreCase( compilerId ) ) {
			return true;
		} else {
			return false;
		}		
	}

	protected void replaceGeneratorsInPrefStoreForCompiler( String compilerId, String newGens ) {
		String genString = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( 
				EDTCorePreferenceConstants.GENERATOR_IDS );
		if( genString != null && genString.length() > 0 ) {
			genString = genString.trim();
			if( genString.length() != 0 ) {
				String[] gens = genString.split( "," );	
				IGenerator gen;
				List<String> idsToKeep = new ArrayList<String>();
				for( int i = 0; i < gens.length; i++ ) {
					gens[i] = gens[i].trim();
					gen = getGeneratorFromId( gens[i] );
					// Keep the gen ids that don't belong to this compiler
					if( gen != null && !gen.getCompiler().getId().equalsIgnoreCase( compilerId ) ) {
						idsToKeep.add( gens[i] );
					}
				}
				if( idsToKeep.size() > 0 ) {
					StringBuilder buf = new StringBuilder(200);
					for (int i = 0; i < idsToKeep.size(); i++) {
						if (i != 0) {
							buf.append(',');
						}
						buf.append( idsToKeep.get(i) );
					}
					String gensToStore;
					if( newGens.length() > 0 ) {
						gensToStore = buf.toString() + ',' + newGens;
					} else {
						gensToStore = buf.toString();
					}
					EDTCoreIDEPlugin.getPlugin().getPreferenceStore().setValue( 
							EDTCorePreferenceConstants.GENERATOR_IDS, gensToStore );
					return;
				}
			}
		} 
		EDTCoreIDEPlugin.getPlugin().getPreferenceStore().setValue( 
				EDTCorePreferenceConstants.GENERATOR_IDS, newGens );		
	}

	/**
	 * When a generator is no longer selected, allow the generator
	 * tab provider to remove any preferences it had added to the
	 * project's preference store for this resource.
	 */
	protected void removeTabPropertiesFromProjectResources() {
		IGeneratorTabProvider tabProvider;
		for( int i = 0; i < this.allTabProviders.size(); i++ ) {
			tabProvider = this.allTabProviders.get(i);
			// If generator isn't selected
			if( !this.selectedGenerators.contains( tabProvider.getTitle() ) ) {
				tabProvider.removePreferencesForAResource();
			}
		}
	}

	protected String convertCompilerIdToName( String id ) {
		if( id != null && id.length() > 0 ) {
			IIDECompiler[] compilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
			for( final IIDECompiler currentCompiler : compilers ) {
				if( currentCompiler.getId().equalsIgnoreCase( id ) )  {
					return currentCompiler.getName();
				}
			}
		}
		return "";
	}

	protected String convertGeneratorIdToName( String id ) {
		if( id != null && id.length() > 0 ) {
			IGenerator[] generators = EDTCoreIDEPlugin.getPlugin().getGenerators();
			for( final IGenerator currentGenerator : generators ) {
				if( currentGenerator.getId().equalsIgnoreCase( id ) )  {
					return currentGenerator.getName();
				}
			}
		}
		return "";
	}

	protected String[] convertGeneratorIdsToNames( List<String> ids ) {
		List<String> names = new ArrayList<String>(ids.size());
		for( String id : ids ) {
			names.add( convertGeneratorIdToName( id ) );
		}
		return names.toArray(new String[names.size()]);
	}

	protected String convertCompilerNameToId( String name ) {
		if( name != null && name.length() > 0 ) {
			IIDECompiler[] compilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
			for( final IIDECompiler currentCompiler : compilers ) {
				if( currentCompiler.getName().equalsIgnoreCase( name ) )  {
					return currentCompiler.getId();
				}
			}
		}
		return "";
	}

	protected String convertGeneratorNameToId( String name ) {
		if( name != null && name.length() > 0 ) {
			IGenerator[] generators = EDTCoreIDEPlugin.getPlugin().getGenerators();
			for( final IGenerator currentGenerator : generators ) {
				if( currentGenerator.getName().equalsIgnoreCase( name ) )  {
					return currentGenerator.getId();
				}
			}
		}
		return "";
	}

	/**
	 * Return the project for the selected resource.
	 * 
	 * @return IProject
	 */
	protected IProject getProject() {
		if( this.resource != null ) {
			if( this.resource instanceof IProject ) {
				return (IProject)this.resource;
			} else {
				return this.resource.getProject();
			}	
		}
		return null;
	}

	/**
	 * Return the resource.
	 * 
	 * @return IResource
	 */
	protected IResource getResource() {
		return this.resource;
	}

	public IAdaptable getElement() {
		return this.resource;
	}

	public void setElement(IAdaptable element) {
		this.resource = (IResource) element.getAdapter(IResource.class);
		// Set project in superclass or it will think this is a preference page
		super.setElement( getProject() );
	}

	protected String getSelectedCompiler() {
		return this.selectedCompiler;
	}

	protected void setSelectedCompiler( String compilerName ) {
		this.selectedCompiler = compilerName;
	}

	protected void clearSelectedGenerators() {
		selectedGenerators.clear();
	}

	protected List<String> getSelectedGenerators() {
		return this.selectedGenerators;
	}

	protected void setSelectedGenerators( List<String> newList ) {
		this.selectedGenerators = newList;
	}

	protected void addSelectedGenerator( String genId ) {
		if( !this.selectedGenerators.contains( genId ) ) {
			this.selectedGenerators.add( genId );
		}
	}

	protected void removeSelectedGenerator( String genId ) {
		if( this.selectedGenerators.contains( genId ) ) {
			this.selectedGenerators.remove( genId );
			validateGenerators();
		}
	}	
	
	protected String getFirstValidCompiler() {
		IIDECompiler[] availableCompilers = EDTCoreIDEPlugin.getPlugin().getCompilers();
		if( availableCompilers.length > 0 ) {
			return availableCompilers[0].getId();
		}
		return null;
	}
	
	/**
	 * Get the compiler id from the project.  If none, get from workspace preferences.
	 * 
	 * @param project
	 * @return
	 */
	protected String getCompilerName( IProject project ) {
		String compilerId = ProjectSettingsUtility.getCompilerId( project );
		if( compilerId == null ) {
			compilerId = EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.COMPILER_ID );
		}
		return convertCompilerIdToName( compilerId );
	}
	
	/**
	 * Get generator ids from the project.  If none, get from workspace preferences.
	 * 
	 * @param project
	 * @return
	 */
	protected List<String> getGeneratorNames() {
		String[] genIds = ProjectSettingsUtility.getGeneratorIds( resource );
		String[] genNames;
		if( genIds != null ) {
			List<String> displayIDs = new ArrayList<String>(genIds.length);
			for (String id : genIds) {
				IGenerator gen = getGeneratorFromId(id);
				String enabledWith = gen == null ? null : gen.getEnabledWith();
				if (enabledWith == null || enabledWith.length() == 0) {
					displayIDs.add(id);
				}
			}
			
			genNames = convertGeneratorIdsToNames( displayIDs );
		} else {
			List<String> genList = getWorkspaceSelectedGenerators();
			genNames = genList.toArray( new String[genList.size()]);
		}
		return( new ArrayList<String>( Arrays.asList( genNames ) ) );
	}
	
	protected boolean isUsingWorkspaceDefaults() {
		String compilerId = ProjectSettingsUtility.getCompilerId( getProject() );
		String[] genIds = ProjectSettingsUtility.getGeneratorIds( resource );
		if( compilerId == null && genIds == null ) {
			return true;
		}
		return false;
	}
	

	protected boolean useProjectSettings() {
		if(generatorOverrideCheckbox!= null && generatorOverrideCheckbox.getSelection()){
			return true;
		}else{
			return super.useProjectSettings();
		}
	}
	
}
