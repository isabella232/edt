/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.formatting.profile.impl;

import org.eclipse.edt.ide.ui.internal.formatting.profile.Category;
import org.eclipse.edt.ide.ui.internal.formatting.profile.CheckControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ComboControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Control;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Controls;
import org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot;
import org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Group;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preference;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preview;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Profile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfileFactory;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage;
import org.eclipse.edt.ide.ui.internal.formatting.profile.RadioControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ReferenceControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Setting;
import org.eclipse.edt.ide.ui.internal.formatting.profile.TextControl;
import org.eclipse.edt.ide.ui.internal.formatting.profile.TreeControl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ProfileFactoryImpl extends EFactoryImpl implements ProfileFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ProfileFactory init() {
		try {
			ProfileFactory theProfileFactory = (ProfileFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.ibm.com/xmlns/egl/formatting/1.0"); 
			if (theProfileFactory != null) {
				return theProfileFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ProfileFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProfileFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case ProfilePackage.CATEGORY: return createCategory();
			case ProfilePackage.CHECK_CONTROL: return createCheckControl();
			case ProfilePackage.COMBO_CONTROL: return createComboControl();
			case ProfilePackage.CONTROL: return createControl();
			case ProfilePackage.CONTROLS: return createControls();
			case ProfilePackage.DEFAULT_PROFILE: return createDefaultProfile();
			case ProfilePackage.EGL_FORMAT_PROFILE_ROOT: return createEGLFormatProfileRoot();
			case ProfilePackage.FORMAT_PROFILES: return createFormatProfiles();
			case ProfilePackage.GROUP: return createGroup();
			case ProfilePackage.PREFERENCE: return createPreference();
			case ProfilePackage.PREVIEW: return createPreview();
			case ProfilePackage.PROFILE: return createProfile();
			case ProfilePackage.RADIO_CONTROL: return createRadioControl();
			case ProfilePackage.REFERENCE_CONTROL: return createReferenceControl();
			case ProfilePackage.SETTING: return createSetting();
			case ProfilePackage.TEXT_CONTROL: return createTextControl();
			case ProfilePackage.TREE_CONTROL: return createTreeControl();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Category createCategory() {
		CategoryImpl category = new CategoryImpl();
		return category;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CheckControl createCheckControl() {
		CheckControlImpl checkControl = new CheckControlImpl();
		return checkControl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComboControl createComboControl() {
		ComboControlImpl comboControl = new ComboControlImpl();
		return comboControl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Control createControl() {
		ControlImpl control = new ControlImpl();
		return control;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Controls createControls() {
		ControlsImpl controls = new ControlsImpl();
		return controls;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DefaultProfile createDefaultProfile() {
		DefaultProfileImpl defaultProfile = new DefaultProfileImpl();
		return defaultProfile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGLFormatProfileRoot createEGLFormatProfileRoot() {
		EGLFormatProfileRootImpl eglFormatProfileRoot = new EGLFormatProfileRootImpl();
		return eglFormatProfileRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FormatProfiles createFormatProfiles() {
		FormatProfilesImpl formatProfiles = new FormatProfilesImpl();
		return formatProfiles;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Group createGroup() {
		GroupImpl group = new GroupImpl();
		return group;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Preference createPreference() {
		PreferenceImpl preference = new PreferenceImpl();
		return preference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Preview createPreview() {
		PreviewImpl preview = new PreviewImpl();
		return preview;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Profile createProfile() {
		ProfileImpl profile = new ProfileImpl();
		return profile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RadioControl createRadioControl() {
		RadioControlImpl radioControl = new RadioControlImpl();
		return radioControl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReferenceControl createReferenceControl() {
		ReferenceControlImpl referenceControl = new ReferenceControlImpl();
		return referenceControl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Setting createSetting() {
		SettingImpl setting = new SettingImpl();
		return setting;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TextControl createTextControl() {
		TextControlImpl textControl = new TextControlImpl();
		return textControl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TreeControl createTreeControl() {
		TreeControlImpl treeControl = new TreeControlImpl();
		return treeControl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProfilePackage getProfilePackage() {
		return (ProfilePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	public static ProfilePackage getPackage() {
		return ProfilePackage.eINSTANCE;
	}

} //ProfileFactoryImpl
