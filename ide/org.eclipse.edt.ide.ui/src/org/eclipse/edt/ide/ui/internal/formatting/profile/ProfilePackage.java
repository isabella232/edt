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
package org.eclipse.edt.ide.ui.internal.formatting.profile;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ProfileFactory
 * @model kind="package"
 * @generated
 */
public interface ProfilePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "profile";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.ibm.com/xmlns/egl/formatting/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "egl";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ProfilePackage eINSTANCE = org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.CategoryImpl <em>Category</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.CategoryImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getCategory()
	 * @generated
	 */
	int CATEGORY = 0;

	/**
	 * The feature id for the '<em><b>Preview</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATEGORY__PREVIEW = 0;

	/**
	 * The feature id for the '<em><b>Group</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATEGORY__GROUP = 1;

	/**
	 * The feature id for the '<em><b>Display</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATEGORY__DISPLAY = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATEGORY__ID = 3;

	/**
	 * The number of structural features of the '<em>Category</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CATEGORY_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ControlImpl <em>Control</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ControlImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getControl()
	 * @generated
	 */
	int CONTROL = 3;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL__CLASS = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL__NAME = 1;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL__STYLE = 2;

	/**
	 * The number of structural features of the '<em>Control</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.CheckControlImpl <em>Check Control</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.CheckControlImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getCheckControl()
	 * @generated
	 */
	int CHECK_CONTROL = 1;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHECK_CONTROL__CLASS = CONTROL__CLASS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHECK_CONTROL__NAME = CONTROL__NAME;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHECK_CONTROL__STYLE = CONTROL__STYLE;

	/**
	 * The number of structural features of the '<em>Check Control</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHECK_CONTROL_FEATURE_COUNT = CONTROL_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ComboControlImpl <em>Combo Control</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ComboControlImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getComboControl()
	 * @generated
	 */
	int COMBO_CONTROL = 2;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMBO_CONTROL__CLASS = CONTROL__CLASS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMBO_CONTROL__NAME = CONTROL__NAME;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMBO_CONTROL__STYLE = CONTROL__STYLE;

	/**
	 * The feature id for the '<em><b>Choices</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMBO_CONTROL__CHOICES = CONTROL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Combo Control</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMBO_CONTROL_FEATURE_COUNT = CONTROL_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ControlsImpl <em>Controls</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ControlsImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getControls()
	 * @generated
	 */
	int CONTROLS = 4;

	/**
	 * The feature id for the '<em><b>Control Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROLS__CONTROL_GROUP = 0;

	/**
	 * The feature id for the '<em><b>Control</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROLS__CONTROL = 1;

	/**
	 * The number of structural features of the '<em>Controls</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROLS_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.DefaultProfileImpl <em>Default Profile</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.DefaultProfileImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getDefaultProfile()
	 * @generated
	 */
	int DEFAULT_PROFILE = 5;

	/**
	 * The feature id for the '<em><b>Preview</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEFAULT_PROFILE__PREVIEW = 0;

	/**
	 * The feature id for the '<em><b>Controls</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEFAULT_PROFILE__CONTROLS = 1;

	/**
	 * The feature id for the '<em><b>Category</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEFAULT_PROFILE__CATEGORY = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEFAULT_PROFILE__NAME = 3;

	/**
	 * The number of structural features of the '<em>Default Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEFAULT_PROFILE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl <em>EGL Format Profile Root</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getEGLFormatProfileRoot()
	 * @generated
	 */
	int EGL_FORMAT_PROFILE_ROOT = 6;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_FORMAT_PROFILE_ROOT__MIXED = 0;

	/**
	 * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_FORMAT_PROFILE_ROOT__XMLNS_PREFIX_MAP = 1;

	/**
	 * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_FORMAT_PROFILE_ROOT__XSI_SCHEMA_LOCATION = 2;

	/**
	 * The feature id for the '<em><b>Control</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_FORMAT_PROFILE_ROOT__CONTROL = 3;

	/**
	 * The feature id for the '<em><b>Control Check</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_FORMAT_PROFILE_ROOT__CONTROL_CHECK = 4;

	/**
	 * The feature id for the '<em><b>Control Combo</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_FORMAT_PROFILE_ROOT__CONTROL_COMBO = 5;

	/**
	 * The feature id for the '<em><b>Control Radio</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_FORMAT_PROFILE_ROOT__CONTROL_RADIO = 6;

	/**
	 * The feature id for the '<em><b>Control Ref</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_FORMAT_PROFILE_ROOT__CONTROL_REF = 7;

	/**
	 * The feature id for the '<em><b>Control Text</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_FORMAT_PROFILE_ROOT__CONTROL_TEXT = 8;

	/**
	 * The feature id for the '<em><b>Control Tree</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_FORMAT_PROFILE_ROOT__CONTROL_TREE = 9;

	/**
	 * The feature id for the '<em><b>Format Profiles</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_FORMAT_PROFILE_ROOT__FORMAT_PROFILES = 10;

	/**
	 * The number of structural features of the '<em>EGL Format Profile Root</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EGL_FORMAT_PROFILE_ROOT_FEATURE_COUNT = 11;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.FormatProfilesImpl <em>Format Profiles</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.FormatProfilesImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getFormatProfiles()
	 * @generated
	 */
	int FORMAT_PROFILES = 7;

	/**
	 * The feature id for the '<em><b>Default Profile</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORMAT_PROFILES__DEFAULT_PROFILE = 0;

	/**
	 * The feature id for the '<em><b>Profile</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORMAT_PROFILES__PROFILE = 1;

	/**
	 * The feature id for the '<em><b>Selection</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORMAT_PROFILES__SELECTION = 2;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORMAT_PROFILES__VERSION = 3;

	/**
	 * The number of structural features of the '<em>Format Profiles</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORMAT_PROFILES_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.GroupImpl <em>Group</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.GroupImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getGroup()
	 * @generated
	 */
	int GROUP = 8;

	/**
	 * The feature id for the '<em><b>Pref</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP__PREF = 0;

	/**
	 * The feature id for the '<em><b>Display</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP__DISPLAY = 1;

	/**
	 * The number of structural features of the '<em>Group</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GROUP_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreferenceImpl <em>Preference</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreferenceImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getPreference()
	 * @generated
	 */
	int PREFERENCE = 9;

	/**
	 * The feature id for the '<em><b>Preview</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREFERENCE__PREVIEW = 0;

	/**
	 * The feature id for the '<em><b>Control Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREFERENCE__CONTROL_GROUP = 1;

	/**
	 * The feature id for the '<em><b>Control</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREFERENCE__CONTROL = 2;

	/**
	 * The feature id for the '<em><b>Alt Display</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREFERENCE__ALT_DISPLAY = 3;

	/**
	 * The feature id for the '<em><b>Display</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREFERENCE__DISPLAY = 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREFERENCE__ID = 5;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREFERENCE__VALUE = 6;

	/**
	 * The feature id for the '<em><b>Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREFERENCE__VISIBLE = 7;

	/**
	 * The number of structural features of the '<em>Preference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREFERENCE_FEATURE_COUNT = 8;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreviewImpl <em>Preview</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreviewImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getPreview()
	 * @generated
	 */
	int PREVIEW = 10;

	/**
	 * The feature id for the '<em><b>Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREVIEW__CODE = 0;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREVIEW__REF = 1;

	/**
	 * The number of structural features of the '<em>Preview</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PREVIEW_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfileImpl <em>Profile</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfileImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getProfile()
	 * @generated
	 */
	int PROFILE = 11;

	/**
	 * The feature id for the '<em><b>Setting</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE__SETTING = 0;

	/**
	 * The feature id for the '<em><b>Base</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE__BASE = 1;

	/**
	 * The feature id for the '<em><b>Is Build In</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE__IS_BUILD_IN = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE__NAME = 3;

	/**
	 * The number of structural features of the '<em>Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.RadioControlImpl <em>Radio Control</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.RadioControlImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getRadioControl()
	 * @generated
	 */
	int RADIO_CONTROL = 12;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RADIO_CONTROL__CLASS = CONTROL__CLASS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RADIO_CONTROL__NAME = CONTROL__NAME;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RADIO_CONTROL__STYLE = CONTROL__STYLE;

	/**
	 * The feature id for the '<em><b>Choices</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RADIO_CONTROL__CHOICES = CONTROL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Radio Control</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RADIO_CONTROL_FEATURE_COUNT = CONTROL_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ReferenceControlImpl <em>Reference Control</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ReferenceControlImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getReferenceControl()
	 * @generated
	 */
	int REFERENCE_CONTROL = 13;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CONTROL__CLASS = CONTROL__CLASS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CONTROL__NAME = CONTROL__NAME;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CONTROL__STYLE = CONTROL__STYLE;

	/**
	 * The feature id for the '<em><b>Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CONTROL__REF = CONTROL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Reference Control</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_CONTROL_FEATURE_COUNT = CONTROL_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.SettingImpl <em>Setting</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.SettingImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getSetting()
	 * @generated
	 */
	int SETTING = 14;

	/**
	 * The feature id for the '<em><b>Category</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SETTING__CATEGORY = 0;

	/**
	 * The feature id for the '<em><b>Pref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SETTING__PREF = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SETTING__VALUE = 2;

	/**
	 * The number of structural features of the '<em>Setting</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SETTING_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.TextControlImpl <em>Text Control</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.TextControlImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getTextControl()
	 * @generated
	 */
	int TEXT_CONTROL = 15;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_CONTROL__CLASS = CONTROL__CLASS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_CONTROL__NAME = CONTROL__NAME;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_CONTROL__STYLE = CONTROL__STYLE;

	/**
	 * The number of structural features of the '<em>Text Control</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEXT_CONTROL_FEATURE_COUNT = CONTROL_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.TreeControlImpl <em>Tree Control</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.TreeControlImpl
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getTreeControl()
	 * @generated
	 */
	int TREE_CONTROL = 16;

	/**
	 * The feature id for the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TREE_CONTROL__CLASS = CONTROL__CLASS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TREE_CONTROL__NAME = CONTROL__NAME;

	/**
	 * The feature id for the '<em><b>Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TREE_CONTROL__STYLE = CONTROL__STYLE;

	/**
	 * The number of structural features of the '<em>Tree Control</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TREE_CONTROL_FEATURE_COUNT = CONTROL_FEATURE_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Category <em>Category</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Category</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Category
	 * @generated
	 */
	EClass getCategory();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Category#getPreview <em>Preview</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Preview</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Category#getPreview()
	 * @see #getCategory()
	 * @generated
	 */
	EReference getCategory_Preview();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Category#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Group</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Category#getGroup()
	 * @see #getCategory()
	 * @generated
	 */
	EReference getCategory_Group();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Category#getDisplay <em>Display</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Display</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Category#getDisplay()
	 * @see #getCategory()
	 * @generated
	 */
	EAttribute getCategory_Display();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Category#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Category#getId()
	 * @see #getCategory()
	 * @generated
	 */
	EAttribute getCategory_Id();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.CheckControl <em>Check Control</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Check Control</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.CheckControl
	 * @generated
	 */
	EClass getCheckControl();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.ComboControl <em>Combo Control</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Combo Control</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ComboControl
	 * @generated
	 */
	EClass getComboControl();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.ComboControl#getChoices <em>Choices</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Choices</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ComboControl#getChoices()
	 * @see #getComboControl()
	 * @generated
	 */
	EAttribute getComboControl_Choices();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Control <em>Control</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Control</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Control
	 * @generated
	 */
	EClass getControl();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Control#getClass_ <em>Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Class</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Control#getClass_()
	 * @see #getControl()
	 * @generated
	 */
	EAttribute getControl_Class();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Control#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Control#getName()
	 * @see #getControl()
	 * @generated
	 */
	EAttribute getControl_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Control#getStyle <em>Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Style</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Control#getStyle()
	 * @see #getControl()
	 * @generated
	 */
	EAttribute getControl_Style();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Controls <em>Controls</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Controls</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Controls
	 * @generated
	 */
	EClass getControls();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Controls#getControlGroup <em>Control Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Control Group</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Controls#getControlGroup()
	 * @see #getControls()
	 * @generated
	 */
	EAttribute getControls_ControlGroup();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Controls#getControl <em>Control</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Control</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Controls#getControl()
	 * @see #getControls()
	 * @generated
	 */
	EReference getControls_Control();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile <em>Default Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Default Profile</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile
	 * @generated
	 */
	EClass getDefaultProfile();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getPreview <em>Preview</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Preview</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getPreview()
	 * @see #getDefaultProfile()
	 * @generated
	 */
	EReference getDefaultProfile_Preview();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getControls <em>Controls</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Controls</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getControls()
	 * @see #getDefaultProfile()
	 * @generated
	 */
	EReference getDefaultProfile_Controls();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getCategory <em>Category</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Category</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getCategory()
	 * @see #getDefaultProfile()
	 * @generated
	 */
	EReference getDefaultProfile_Category();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile#getName()
	 * @see #getDefaultProfile()
	 * @generated
	 */
	EAttribute getDefaultProfile_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot <em>EGL Format Profile Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EGL Format Profile Root</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot
	 * @generated
	 */
	EClass getEGLFormatProfileRoot();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getMixed()
	 * @see #getEGLFormatProfileRoot()
	 * @generated
	 */
	EAttribute getEGLFormatProfileRoot_Mixed();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getXMLNSPrefixMap()
	 * @see #getEGLFormatProfileRoot()
	 * @generated
	 */
	EReference getEGLFormatProfileRoot_XMLNSPrefixMap();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XSI Schema Location</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getXSISchemaLocation()
	 * @see #getEGLFormatProfileRoot()
	 * @generated
	 */
	EReference getEGLFormatProfileRoot_XSISchemaLocation();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControl <em>Control</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Control</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControl()
	 * @see #getEGLFormatProfileRoot()
	 * @generated
	 */
	EReference getEGLFormatProfileRoot_Control();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlCheck <em>Control Check</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Control Check</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlCheck()
	 * @see #getEGLFormatProfileRoot()
	 * @generated
	 */
	EReference getEGLFormatProfileRoot_ControlCheck();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlCombo <em>Control Combo</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Control Combo</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlCombo()
	 * @see #getEGLFormatProfileRoot()
	 * @generated
	 */
	EReference getEGLFormatProfileRoot_ControlCombo();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlRadio <em>Control Radio</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Control Radio</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlRadio()
	 * @see #getEGLFormatProfileRoot()
	 * @generated
	 */
	EReference getEGLFormatProfileRoot_ControlRadio();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlRef <em>Control Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Control Ref</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlRef()
	 * @see #getEGLFormatProfileRoot()
	 * @generated
	 */
	EReference getEGLFormatProfileRoot_ControlRef();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlText <em>Control Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Control Text</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlText()
	 * @see #getEGLFormatProfileRoot()
	 * @generated
	 */
	EReference getEGLFormatProfileRoot_ControlText();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlTree <em>Control Tree</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Control Tree</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getControlTree()
	 * @see #getEGLFormatProfileRoot()
	 * @generated
	 */
	EReference getEGLFormatProfileRoot_ControlTree();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getFormatProfiles <em>Format Profiles</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Format Profiles</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot#getFormatProfiles()
	 * @see #getEGLFormatProfileRoot()
	 * @generated
	 */
	EReference getEGLFormatProfileRoot_FormatProfiles();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles <em>Format Profiles</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Format Profiles</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles
	 * @generated
	 */
	EClass getFormatProfiles();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getDefaultProfile <em>Default Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Default Profile</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getDefaultProfile()
	 * @see #getFormatProfiles()
	 * @generated
	 */
	EReference getFormatProfiles_DefaultProfile();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getProfile <em>Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Profile</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getProfile()
	 * @see #getFormatProfiles()
	 * @generated
	 */
	EReference getFormatProfiles_Profile();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getSelection <em>Selection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Selection</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getSelection()
	 * @see #getFormatProfiles()
	 * @generated
	 */
	EAttribute getFormatProfiles_Selection();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles#getVersion()
	 * @see #getFormatProfiles()
	 * @generated
	 */
	EAttribute getFormatProfiles_Version();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Group <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Group</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Group
	 * @generated
	 */
	EClass getGroup();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Group#getPref <em>Pref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Pref</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Group#getPref()
	 * @see #getGroup()
	 * @generated
	 */
	EReference getGroup_Pref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Group#getDisplay <em>Display</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Display</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Group#getDisplay()
	 * @see #getGroup()
	 * @generated
	 */
	EAttribute getGroup_Display();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference <em>Preference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Preference</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Preference
	 * @generated
	 */
	EClass getPreference();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getPreview <em>Preview</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Preview</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getPreview()
	 * @see #getPreference()
	 * @generated
	 */
	EReference getPreference_Preview();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getControlGroup <em>Control Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Control Group</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getControlGroup()
	 * @see #getPreference()
	 * @generated
	 */
	EAttribute getPreference_ControlGroup();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getControl <em>Control</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Control</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getControl()
	 * @see #getPreference()
	 * @generated
	 */
	EReference getPreference_Control();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getAltDisplay <em>Alt Display</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Alt Display</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getAltDisplay()
	 * @see #getPreference()
	 * @generated
	 */
	EAttribute getPreference_AltDisplay();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getDisplay <em>Display</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Display</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getDisplay()
	 * @see #getPreference()
	 * @generated
	 */
	EAttribute getPreference_Display();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getId()
	 * @see #getPreference()
	 * @generated
	 */
	EAttribute getPreference_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#getValue()
	 * @see #getPreference()
	 * @generated
	 */
	EAttribute getPreference_Value();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#isVisible <em>Visible</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Visible</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Preference#isVisible()
	 * @see #getPreference()
	 * @generated
	 */
	EAttribute getPreference_Visible();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preview <em>Preview</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Preview</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Preview
	 * @generated
	 */
	EClass getPreview();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preview#getCode <em>Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Code</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Preview#getCode()
	 * @see #getPreview()
	 * @generated
	 */
	EAttribute getPreview_Code();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Preview#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Preview#getRef()
	 * @see #getPreview()
	 * @generated
	 */
	EAttribute getPreview_Ref();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile <em>Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Profile</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Profile
	 * @generated
	 */
	EClass getProfile();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#getSetting <em>Setting</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Setting</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#getSetting()
	 * @see #getProfile()
	 * @generated
	 */
	EReference getProfile_Setting();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#getBase <em>Base</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Base</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#getBase()
	 * @see #getProfile()
	 * @generated
	 */
	EAttribute getProfile_Base();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#isIsBuildIn <em>Is Build In</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Build In</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#isIsBuildIn()
	 * @see #getProfile()
	 * @generated
	 */
	EAttribute getProfile_IsBuildIn();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Profile#getName()
	 * @see #getProfile()
	 * @generated
	 */
	EAttribute getProfile_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.RadioControl <em>Radio Control</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Radio Control</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.RadioControl
	 * @generated
	 */
	EClass getRadioControl();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.RadioControl#getChoices <em>Choices</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Choices</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.RadioControl#getChoices()
	 * @see #getRadioControl()
	 * @generated
	 */
	EAttribute getRadioControl_Choices();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.ReferenceControl <em>Reference Control</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Reference Control</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ReferenceControl
	 * @generated
	 */
	EClass getReferenceControl();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.ReferenceControl#getRef <em>Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.ReferenceControl#getRef()
	 * @see #getReferenceControl()
	 * @generated
	 */
	EAttribute getReferenceControl_Ref();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Setting <em>Setting</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Setting</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Setting
	 * @generated
	 */
	EClass getSetting();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Setting#getCategory <em>Category</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Category</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Setting#getCategory()
	 * @see #getSetting()
	 * @generated
	 */
	EAttribute getSetting_Category();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Setting#getPref <em>Pref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pref</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Setting#getPref()
	 * @see #getSetting()
	 * @generated
	 */
	EAttribute getSetting_Pref();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.Setting#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.Setting#getValue()
	 * @see #getSetting()
	 * @generated
	 */
	EAttribute getSetting_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.TextControl <em>Text Control</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Text Control</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.TextControl
	 * @generated
	 */
	EClass getTextControl();

	/**
	 * Returns the meta object for class '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.TreeControl <em>Tree Control</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Tree Control</em>'.
	 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.TreeControl
	 * @generated
	 */
	EClass getTreeControl();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ProfileFactory getProfileFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.CategoryImpl <em>Category</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.CategoryImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getCategory()
		 * @generated
		 */
		EClass CATEGORY = eINSTANCE.getCategory();

		/**
		 * The meta object literal for the '<em><b>Preview</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CATEGORY__PREVIEW = eINSTANCE.getCategory_Preview();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CATEGORY__GROUP = eINSTANCE.getCategory_Group();

		/**
		 * The meta object literal for the '<em><b>Display</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CATEGORY__DISPLAY = eINSTANCE.getCategory_Display();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CATEGORY__ID = eINSTANCE.getCategory_Id();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.CheckControlImpl <em>Check Control</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.CheckControlImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getCheckControl()
		 * @generated
		 */
		EClass CHECK_CONTROL = eINSTANCE.getCheckControl();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ComboControlImpl <em>Combo Control</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ComboControlImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getComboControl()
		 * @generated
		 */
		EClass COMBO_CONTROL = eINSTANCE.getComboControl();

		/**
		 * The meta object literal for the '<em><b>Choices</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMBO_CONTROL__CHOICES = eINSTANCE.getComboControl_Choices();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ControlImpl <em>Control</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ControlImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getControl()
		 * @generated
		 */
		EClass CONTROL = eINSTANCE.getControl();

		/**
		 * The meta object literal for the '<em><b>Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTROL__CLASS = eINSTANCE.getControl_Class();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTROL__NAME = eINSTANCE.getControl_Name();

		/**
		 * The meta object literal for the '<em><b>Style</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTROL__STYLE = eINSTANCE.getControl_Style();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ControlsImpl <em>Controls</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ControlsImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getControls()
		 * @generated
		 */
		EClass CONTROLS = eINSTANCE.getControls();

		/**
		 * The meta object literal for the '<em><b>Control Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONTROLS__CONTROL_GROUP = eINSTANCE.getControls_ControlGroup();

		/**
		 * The meta object literal for the '<em><b>Control</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROLS__CONTROL = eINSTANCE.getControls_Control();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.DefaultProfileImpl <em>Default Profile</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.DefaultProfileImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getDefaultProfile()
		 * @generated
		 */
		EClass DEFAULT_PROFILE = eINSTANCE.getDefaultProfile();

		/**
		 * The meta object literal for the '<em><b>Preview</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEFAULT_PROFILE__PREVIEW = eINSTANCE.getDefaultProfile_Preview();

		/**
		 * The meta object literal for the '<em><b>Controls</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEFAULT_PROFILE__CONTROLS = eINSTANCE.getDefaultProfile_Controls();

		/**
		 * The meta object literal for the '<em><b>Category</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEFAULT_PROFILE__CATEGORY = eINSTANCE.getDefaultProfile_Category();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEFAULT_PROFILE__NAME = eINSTANCE.getDefaultProfile_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl <em>EGL Format Profile Root</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.EGLFormatProfileRootImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getEGLFormatProfileRoot()
		 * @generated
		 */
		EClass EGL_FORMAT_PROFILE_ROOT = eINSTANCE.getEGLFormatProfileRoot();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EGL_FORMAT_PROFILE_ROOT__MIXED = eINSTANCE.getEGLFormatProfileRoot_Mixed();

		/**
		 * The meta object literal for the '<em><b>XMLNS Prefix Map</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_FORMAT_PROFILE_ROOT__XMLNS_PREFIX_MAP = eINSTANCE.getEGLFormatProfileRoot_XMLNSPrefixMap();

		/**
		 * The meta object literal for the '<em><b>XSI Schema Location</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_FORMAT_PROFILE_ROOT__XSI_SCHEMA_LOCATION = eINSTANCE.getEGLFormatProfileRoot_XSISchemaLocation();

		/**
		 * The meta object literal for the '<em><b>Control</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_FORMAT_PROFILE_ROOT__CONTROL = eINSTANCE.getEGLFormatProfileRoot_Control();

		/**
		 * The meta object literal for the '<em><b>Control Check</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_FORMAT_PROFILE_ROOT__CONTROL_CHECK = eINSTANCE.getEGLFormatProfileRoot_ControlCheck();

		/**
		 * The meta object literal for the '<em><b>Control Combo</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_FORMAT_PROFILE_ROOT__CONTROL_COMBO = eINSTANCE.getEGLFormatProfileRoot_ControlCombo();

		/**
		 * The meta object literal for the '<em><b>Control Radio</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_FORMAT_PROFILE_ROOT__CONTROL_RADIO = eINSTANCE.getEGLFormatProfileRoot_ControlRadio();

		/**
		 * The meta object literal for the '<em><b>Control Ref</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_FORMAT_PROFILE_ROOT__CONTROL_REF = eINSTANCE.getEGLFormatProfileRoot_ControlRef();

		/**
		 * The meta object literal for the '<em><b>Control Text</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_FORMAT_PROFILE_ROOT__CONTROL_TEXT = eINSTANCE.getEGLFormatProfileRoot_ControlText();

		/**
		 * The meta object literal for the '<em><b>Control Tree</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_FORMAT_PROFILE_ROOT__CONTROL_TREE = eINSTANCE.getEGLFormatProfileRoot_ControlTree();

		/**
		 * The meta object literal for the '<em><b>Format Profiles</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EGL_FORMAT_PROFILE_ROOT__FORMAT_PROFILES = eINSTANCE.getEGLFormatProfileRoot_FormatProfiles();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.FormatProfilesImpl <em>Format Profiles</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.FormatProfilesImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getFormatProfiles()
		 * @generated
		 */
		EClass FORMAT_PROFILES = eINSTANCE.getFormatProfiles();

		/**
		 * The meta object literal for the '<em><b>Default Profile</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FORMAT_PROFILES__DEFAULT_PROFILE = eINSTANCE.getFormatProfiles_DefaultProfile();

		/**
		 * The meta object literal for the '<em><b>Profile</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FORMAT_PROFILES__PROFILE = eINSTANCE.getFormatProfiles_Profile();

		/**
		 * The meta object literal for the '<em><b>Selection</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FORMAT_PROFILES__SELECTION = eINSTANCE.getFormatProfiles_Selection();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FORMAT_PROFILES__VERSION = eINSTANCE.getFormatProfiles_Version();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.GroupImpl <em>Group</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.GroupImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getGroup()
		 * @generated
		 */
		EClass GROUP = eINSTANCE.getGroup();

		/**
		 * The meta object literal for the '<em><b>Pref</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GROUP__PREF = eINSTANCE.getGroup_Pref();

		/**
		 * The meta object literal for the '<em><b>Display</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GROUP__DISPLAY = eINSTANCE.getGroup_Display();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreferenceImpl <em>Preference</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreferenceImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getPreference()
		 * @generated
		 */
		EClass PREFERENCE = eINSTANCE.getPreference();

		/**
		 * The meta object literal for the '<em><b>Preview</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PREFERENCE__PREVIEW = eINSTANCE.getPreference_Preview();

		/**
		 * The meta object literal for the '<em><b>Control Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PREFERENCE__CONTROL_GROUP = eINSTANCE.getPreference_ControlGroup();

		/**
		 * The meta object literal for the '<em><b>Control</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PREFERENCE__CONTROL = eINSTANCE.getPreference_Control();

		/**
		 * The meta object literal for the '<em><b>Alt Display</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PREFERENCE__ALT_DISPLAY = eINSTANCE.getPreference_AltDisplay();

		/**
		 * The meta object literal for the '<em><b>Display</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PREFERENCE__DISPLAY = eINSTANCE.getPreference_Display();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PREFERENCE__ID = eINSTANCE.getPreference_Id();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PREFERENCE__VALUE = eINSTANCE.getPreference_Value();

		/**
		 * The meta object literal for the '<em><b>Visible</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PREFERENCE__VISIBLE = eINSTANCE.getPreference_Visible();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreviewImpl <em>Preview</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.PreviewImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getPreview()
		 * @generated
		 */
		EClass PREVIEW = eINSTANCE.getPreview();

		/**
		 * The meta object literal for the '<em><b>Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PREVIEW__CODE = eINSTANCE.getPreview_Code();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PREVIEW__REF = eINSTANCE.getPreview_Ref();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfileImpl <em>Profile</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfileImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getProfile()
		 * @generated
		 */
		EClass PROFILE = eINSTANCE.getProfile();

		/**
		 * The meta object literal for the '<em><b>Setting</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROFILE__SETTING = eINSTANCE.getProfile_Setting();

		/**
		 * The meta object literal for the '<em><b>Base</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE__BASE = eINSTANCE.getProfile_Base();

		/**
		 * The meta object literal for the '<em><b>Is Build In</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE__IS_BUILD_IN = eINSTANCE.getProfile_IsBuildIn();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE__NAME = eINSTANCE.getProfile_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.RadioControlImpl <em>Radio Control</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.RadioControlImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getRadioControl()
		 * @generated
		 */
		EClass RADIO_CONTROL = eINSTANCE.getRadioControl();

		/**
		 * The meta object literal for the '<em><b>Choices</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RADIO_CONTROL__CHOICES = eINSTANCE.getRadioControl_Choices();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ReferenceControlImpl <em>Reference Control</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ReferenceControlImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getReferenceControl()
		 * @generated
		 */
		EClass REFERENCE_CONTROL = eINSTANCE.getReferenceControl();

		/**
		 * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE_CONTROL__REF = eINSTANCE.getReferenceControl_Ref();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.SettingImpl <em>Setting</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.SettingImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getSetting()
		 * @generated
		 */
		EClass SETTING = eINSTANCE.getSetting();

		/**
		 * The meta object literal for the '<em><b>Category</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SETTING__CATEGORY = eINSTANCE.getSetting_Category();

		/**
		 * The meta object literal for the '<em><b>Pref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SETTING__PREF = eINSTANCE.getSetting_Pref();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SETTING__VALUE = eINSTANCE.getSetting_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.TextControlImpl <em>Text Control</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.TextControlImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getTextControl()
		 * @generated
		 */
		EClass TEXT_CONTROL = eINSTANCE.getTextControl();

		/**
		 * The meta object literal for the '{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.TreeControlImpl <em>Tree Control</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.TreeControlImpl
		 * @see org.eclipse.edt.ide.ui.internal.formatting.profile.impl.ProfilePackageImpl#getTreeControl()
		 * @generated
		 */
		EClass TREE_CONTROL = eINSTANCE.getTreeControl();

	}

} //ProfilePackage
