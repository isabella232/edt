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
package org.eclipse.edt.ide.ui;

import java.util.StringTokenizer;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposalComputerRegistry;
import org.eclipse.edt.ide.ui.internal.preferences.IColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.texteditor.AbstractTextEditor;

/**
 * Preference constants used in the EDT UI preference store. Clients should only read the
 * EDT UI preference store using these values. Clients are not allowed to modify the
 * preference store programmatically.
 * <p>
 * This class it is not intended to be instantiated or subclassed by clients.
 * 
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
  */
public class EDTUIPreferenceConstants {

	private EDTUIPreferenceConstants() {
	}
	/**
	 * A named preference that controls if the Java code assist gets auto activated.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public final static String CODEASSIST_AUTOACTIVATION = "content_assist_autoactivation"; //$NON-NLS-1$
	
	public final static String CODEASSIST_AUTOACTIVATION_DELAY= "content_assist_autoactivation_delay"; //$NON-NLS-1$
	
	/**
	 * A named preference that controls which completion proposal categories
	 * have been excluded from the default proposal list.
	 * <p>
	 * Value is of type <code>String</code>, a "\0"-separated list of identifiers.
	 * </p>
	 *
	 * @see #getExcludedCompletionProposalCategories()
	 * @see #setExcludedCompletionProposalCategories(String[])
	 * 
	 */
	public final static String CODEASSIST_EXCLUDED_CATEGORIES = "content_assist_disabled_categories";//$NON-NLS-1$
	
	/**
	 * A named preference that controls which the order of the specific code assist commands.
	 * <p>
	 * Value is of type <code>String</code>, a "\0"-separated list with categoryId:cycleState where
	 * <ul>
	 * <li>categoryId is the <code>String</code> holding the category ID</li>
	 * <li>cycleState is an <code>int</code> which specifies the rank and the enablement:
	 * <ul>
	 *		<li>enabled= cycleState < 65535</li>
	 *		<li>rank= enabled ? cycleState : cycleState - 65535)</li>
	 * </ul></li>
	 * </ul>
	 * 
	 * </p>
	 * 
	 * @since 3.2
	 */
	public static final String CODEASSIST_CATEGORY_ORDER= "content_assist_category_order"; //$NON-NLS-1$

	/**
	 * A named preference that holds the color used as the text background.
	 * This value has not effect if the system default color is used.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a string
	 * using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String EDITOR_BACKGROUND_COLOR = AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND;

	/**
	 * A named preference that describes if the system default background color
	 * is used as the text background.
	 * <p>
	 * Value is of type <code>Boolean</code>. 
	 * </p>
	 */
	public final static String EDITOR_BACKGROUND_DEFAULT_COLOR = AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT;

	/**
	 * Preference key suffix for bold text style preference keys.
	 */
	public static final String EDITOR_BOLD_SUFFIX = "_bold"; //$NON-NLS-1$
	
	/**
	 * A named preference that controls whether egl default text is rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public final static String EDITOR_DEFAULT_BOLD = IColorConstants.EGL_DEFAULT + EDITOR_BOLD_SUFFIX;
	
	/**
	 * A named preference that holds the color used to render egl default text.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a string
	 * using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String EDITOR_DEFAULT_COLOR = IColorConstants.EGL_DEFAULT;

	/**
	 * A named preference that controls whether the editor shows problem indicators in text (squiggly lines). 
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public final static String EDITOR_ERROR_INDICATION = "errorIndication"; //$NON-NLS-1$
	
	/**
	 * A named preference that controls whether the overview ruler shows error
	 * indicators.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */
	public final static String EDITOR_ERROR_INDICATION_IN_OVERVIEW_RULER = "errorIndicationInOverviewRuler"; //$NON-NLS-1$ 
	
	/**
	 * A named preference that stores the value for comments folding for the default folding provider.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */	
	public static final String EDITOR_FOLDING_COMMENTS = "editor_folding_comments"; //$NON-NLS-1$
	
	/**
	 * A named preference that controls whether folding is enabled in the Ant editor.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * 
	 */
	public static final String EDITOR_FOLDING_ENABLED = "editor_folding_enabled"; //$NON-NLS-1$
	
	/**
	 * A named preference that stores the value for functions folding for the default folding provider.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */		
	public static final String EDITOR_FOLDING_FUNCTIONS = "editor_folding_functions"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for imports folding for the default folding provider.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */	
	public static final String EDITOR_FOLDING_IMPORTS = "editor_folding_imports"; //$NON-NLS-1$
	
	/**
	 * A named preference that stores the value for partitions(sql, dli) folding for the default folding provider.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */		
	public static final String EDITOR_FOLDING_PARTITIONS = "editor_folding_partitions"; //$NON-NLS-1$
	
	/**
	 * A named preference that stores the value for parts(generatible) folding for the default folding provider.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */		
	public static final String EDITOR_FOLDING_PARTS = "editor_folding_parts"; //$NON-NLS-1$
	
	/**
	 * A named preference that stores the value for properties blocks folding for the default folding provider.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */	
	public static final String EDITOR_FOLDING_PROPERTIESBLOCKS = "editor_folding_propertiesblocks"; //$NON-NLS-1$
	
	/**
	 * A named preference that stores the value for properties blocks threshold number of lines
	 * <p>
	 * Value is of type <code>Int</code>.
	 */		
	public static final String EDITOR_FOLDING_PROPERTIESBLOCKS_THRESHOLD = "editor_folding_propertiesblocks_threshold"; //$NON-NLS-1$
	
	/**
	 * A named preference that stores the configured folding provider.
	 * <p>
	 * Value is of type <code>String</code>.
	 */
	public static final String EDITOR_FOLDING_PROVIDER = "editor_folding_provider"; //$NON-NLS-1$
	
	/**
	 * A named preference that holds the color used as the text foreground.
	 * This value has not effect if the system default color is used.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a string
	 * using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String EDITOR_FOREGROUND_COLOR = AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND;
	
	/**
	 * A named preference that describes if the system default foreground color
	 * is used as the text foreground.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */
	public final static String EDITOR_FOREGROUND_DEFAULT_COLOR = AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT;

	
	/**
	 * A named preference that controls whether the editor shows problem indicators in text (squiggly lines). 
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */
	public final static String EDITOR_HANDLE_DYNAMIC_PROBLEMS = "handleDynamicProblems"; //$NON-NLS-1$
	
	/**
	 * A named preference that controls whether keywords are rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */
	public final static String EDITOR_KEYWORD_BOLD = IColorConstants.EGL_KEYWORD + EDITOR_BOLD_SUFFIX;
	
	/**
	 * A named preference that holds the color used to render egl keywords.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a string
	 * using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String EDITOR_KEYWORD_COLOR = IColorConstants.EGL_KEYWORD;

	/**
	 * A named preference that controls if the line number ruler is shown in the UI.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */
//	public final static String EDITOR_LINE_NUMBER_RULER = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER;

	/**
	 * A named preference that controls whether multi line comments are rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> multi line comments are rendered
	 * in bold. If <code>false</code> the are rendered using no font style attribute.
	 */
	public final static String EDITOR_MULTI_LINE_COMMENT_BOLD = IColorConstants.EGL_MULTI_LINE_COMMENT + EDITOR_BOLD_SUFFIX; 

	/**
	 * A named preference that holds the color used to render multi line comments.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a string
	 * using class <code>PreferenceConverter</code>
	 * </p>
	 * 
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String EDITOR_MULTI_LINE_COMMENT_COLOR = IColorConstants.EGL_MULTI_LINE_COMMENT;
	
	/**
	 * A named preference that controls if segmented view (show selected element only) is turned on or off.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */
	public static final String EDITOR_SHOW_SEGMENTS = "org.eclipse.jdt.ui.editor.showSegments"; //$NON-NLS-1$
	
	/**
	 * A named preference that controls whether single line comments are rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true</code> single line comments are rendered
	 * in bold. If <code>false</code> the are rendered using no font style attribute.
	 * </p>
	 */
	public final static String EDITOR_SINGLE_LINE_COMMENT_BOLD = IColorConstants.EGL_SINGLE_LINE_COMMENT + EDITOR_BOLD_SUFFIX; 

	/**
	 * A named preference that holds the color used to render single line comments.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a string
	 * using class <code>PreferenceConverter</code>
	 * <p>
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String EDITOR_SINGLE_LINE_COMMENT_COLOR = IColorConstants.EGL_SINGLE_LINE_COMMENT;

	/**
	 * A named preference that controls whether string constants are rendered in bold.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 */
	public final static String EDITOR_STRING_BOLD= IColorConstants.EGL_STRING + EDITOR_BOLD_SUFFIX;

	/**
	 * A named preference that holds the color used to render string constants.
	 * <p>
	 * Value is of type <code>String</code>. A RGB color value encoded as a string
	 * using class <code>PreferenceConverter</code>
	 * <p>
	 * @see org.eclipse.jface.resource.StringConverter
	 * @see org.eclipse.jface.preference.PreferenceConverter
	 */
	public final static String EDITOR_STRING_COLOR= IColorConstants.EGL_STRING;
	
	/**
	 * A named preference that holds the number of spaces used per tab in the editor.
	 * <p>
	 * Value is of type <code>Int</code>: positive int value specifying the number of
	 * spaces per tab.
	 */
	public final static String EDITOR_TAB_WIDTH = "org.eclipse.jdt.ui.editor.tab.width"; //$NON-NLS-1$

	/**
	 * The symbolic font name for the EGL editor text font 
	 * (value <code>"org.eclipse.jdt.ui.editors.textfont"</code>).
	 */
	public final static String EDITOR_TEXT_FONT = "org.eclipse.edt.ide.ui.editors.textfont"; //$NON-NLS-1$

	/**
	 * A named preference that holds a list of comma separated package names. The list specifies the import order used by
	 * the "Organize Imports" operation.
	 * <p>
	 * Value is of type <code>String</code>: semicolon separated list of package
	 * names
	 */
	public static final String ORGIMPORTS_IMPORTORDER = "org.eclipse.egl.ui.importorder"; //$NON-NLS-1$
	
	/**
	 * A named preference that specifies the number of imports added before a star-import declaration is used.
	 * <p>
	 * Value is of type <code>Int</code>: positive value specifing the number of non star-import is used
	 */
	public static final String ORGIMPORTS_ONDEMANDTHRESHOLD = "org.eclipse.egl.ui.ondemandthreshold"; //$NON-NLS-1$
	
	/**
	 * A named preference that specifies the base package name in the egl project wizard
	 * <p>
	 * Value is of type <code>String</code>
	 */
	public static final String NEWPROJECTWIZARD_BASEPACKAGE = "newProjectWizard_BasePackage"; //$NON-NLS-1$
	public static final String NEWPROJECTWIZARD_SELECTEDTEMPLATE = "newProjectWizard_SelectedTemplate"; //$NON-NLS-1$


	/**
	 * Initializes the given preference store with the default values.
	 * 
	 * @param store the preference store to be initialized
	 */
	public static void initializeDefaultValues(IPreferenceStore store) {
		// Editor preference page
		store.setDefault( CODEASSIST_AUTOACTIVATION, true );
		store.setDefault(CODEASSIST_AUTOACTIVATION_DELAY, 500);
		store.setDefault( EDITOR_ERROR_INDICATION, true );
		store.setDefault( EDITOR_ERROR_INDICATION_IN_OVERVIEW_RULER, true );
		store.setDefault( EDITOR_HANDLE_DYNAMIC_PROBLEMS,  true );
		
		// Folding preference page
		store.setDefault( EDITOR_FOLDING_COMMENTS, false );
		store.setDefault( EDITOR_FOLDING_ENABLED,  true );
		store.setDefault( EDITOR_FOLDING_FUNCTIONS, false );
		store.setDefault( EDITOR_FOLDING_IMPORTS, true );
		store.setDefault( EDITOR_FOLDING_PARTITIONS, true );
		store.setDefault( EDITOR_FOLDING_PARTS, false );
		store.setDefault( EDITOR_FOLDING_PROPERTIESBLOCKS, false );
		store.setDefault( EDITOR_FOLDING_PROPERTIESBLOCKS_THRESHOLD, 5 );		
		store.setDefault( EDITOR_FOLDING_PROVIDER, "org.eclipse.edt.ide.ui.internal.editor.folding.eglFoldingStructureProviders"); //$NON-NLS-1$
		
		// Organize imports preference page
		store.setDefault( ORGIMPORTS_IMPORTORDER, "org;edt" ); //$NON-NLS-1$
		store.setDefault( ORGIMPORTS_ONDEMANDTHRESHOLD, 99 );
		
		//Content Assist default setting
		store.setDefault(EDTUIPreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES, ""); //$NON-NLS-1$
		store.setDefault(EDTUIPreferenceConstants.CODEASSIST_CATEGORY_ORDER, "org.eclipse.edt.ide.ui.keywordProposalCategory:2\0org.eclipse.edt.ide.ui.TemplateProposalCategory:5\0org.eclipse.edt.ide.ui.ReferenceProposalCategory:4\0"); //$NON-NLS-1$	
		
		initializeDefaultEGLColorPreferences( store );
	}
	
	/**
	 * Initializes the given preference store with the default values for the EGL Color Preferences.
	 * 
	 * @param store the preference store to be initialized
	 */
	public static void initializeDefaultEGLColorPreferences(IPreferenceStore store) {
		
		store.setDefault(EDITOR_FOREGROUND_DEFAULT_COLOR, true);
		store.setDefault(EDITOR_BACKGROUND_DEFAULT_COLOR, true);

		PreferenceConverter.setDefault(store, EDITOR_MULTI_LINE_COMMENT_COLOR, new RGB(63, 127, 95));
		store.setDefault(EDITOR_MULTI_LINE_COMMENT_BOLD, false);

		PreferenceConverter.setDefault(store, EDITOR_SINGLE_LINE_COMMENT_COLOR, new RGB(63, 127, 95));
		store.setDefault(EDITOR_SINGLE_LINE_COMMENT_BOLD, false);

		PreferenceConverter.setDefault(store, EDITOR_KEYWORD_COLOR, new RGB(127, 0, 85));
		store.setDefault(EDITOR_KEYWORD_BOLD, true);

		PreferenceConverter.setDefault(store, EDITOR_STRING_COLOR, new RGB(42, 0, 255));
		store.setDefault(EDITOR_STRING_BOLD, false);

		PreferenceConverter.setDefault(store, EDITOR_DEFAULT_COLOR, new RGB(0, 0, 0));
		store.setDefault(EDITOR_DEFAULT_BOLD, false);

	}

	/**
	 * Returns the EDT UI preference store.
	 * 
	 * @return the EDT UI preference store
	 */
	public static IPreferenceStore getPreferenceStore() {
		return EDTUIPlugin.getDefault().getPreferenceStore();
	}
	
	/**
	 * Returns the value for the given key in the given context.
	 * 
	 * @param key The preference key
	 * @param project The current context or <code>null</code> if no context is available and the
	 * workspace setting should be taken. Note that passing <code>null</code> should
	 * be avoided.
	 * @return Returns the current value for the string.
	 * @since 3.1
	 */
	public static String getPreference(String key, IEGLProject project) {
		String val;
		if (project != null) {
			val= new ProjectScope(project.getProject()).getNode(EDTUIPlugin.PLUGIN_ID).get(key, null);
			if (val != null) {
				return val;
			}
		}
		val= new InstanceScope().getNode(EDTUIPlugin.PLUGIN_ID).get(key, null);
		if (val != null) {
			return val;
		}
		return new DefaultScope().getNode(EDTUIPlugin.PLUGIN_ID).get(key, null);
	}
	
	/**
	 * Returns the completion proposal categories which
	 * are excluded from the default proposal list.
	 *
	 * @return an array with the IDs of the excluded categories
	 * @see #CODEASSIST_EXCLUDED_CATEGORIES
	 * @since 3.4
	 */
	public static String[] getExcludedCompletionProposalCategories() {
		String encodedPreference= getPreference(CODEASSIST_EXCLUDED_CATEGORIES, null);
		StringTokenizer tokenizer= new StringTokenizer(encodedPreference, "\0"); //$NON-NLS-1$
		String[] result= new String[tokenizer.countTokens()];
		for (int i= 0; i < result.length; i++)
			result[i]= tokenizer.nextToken();
		return result;
	}

	/**
	 * Sets the completion proposal categories which are excluded from the
	 * default proposal list and reloads the registry.
	 *
	 * @param categories the array with the IDs of the excluded categories
	 * @see #CODEASSIST_EXCLUDED_CATEGORIES
	 * @since 3.4
	 */
	public static void setExcludedCompletionProposalCategories(String[] categories) {
		Assert.isLegal(categories != null);
		StringBuffer buf= new StringBuffer(50 * categories.length);
		for (int i= 0; i < categories.length; i++) {
			buf.append(categories[i]);
			buf.append('\0');
		}
		getPreferenceStore().setValue(CODEASSIST_EXCLUDED_CATEGORIES, buf.toString());
		EGLCompletionProposalComputerRegistry.getDefault().reload();
	}
}

