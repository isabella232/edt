/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import java.util.Collection;

import org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Profile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.ProfilePackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Format Profiles</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.FormatProfilesImpl#getDefaultProfile <em>Default Profile</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.FormatProfilesImpl#getProfile <em>Profile</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.FormatProfilesImpl#getSelection <em>Selection</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.FormatProfilesImpl#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FormatProfilesImpl extends EObjectImpl implements FormatProfiles {
	/**
	 * The cached value of the '{@link #getDefaultProfile() <em>Default Profile</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefaultProfile()
	 * @generated
	 * @ordered
	 */
	protected DefaultProfile defaultProfile = null;

	/**
	 * The cached value of the '{@link #getProfile() <em>Profile</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProfile()
	 * @generated
	 * @ordered
	 */
	protected EList profile = null;

	/**
	 * The default value of the '{@link #getSelection() <em>Selection</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSelection()
	 * @generated
	 * @ordered
	 */
	protected static final String SELECTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSelection() <em>Selection</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSelection()
	 * @generated
	 * @ordered
	 */
	protected String selection = SELECTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected static final String VERSION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected String version = VERSION_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FormatProfilesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ProfilePackage.Literals.FORMAT_PROFILES;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DefaultProfile getDefaultProfile() {
		return defaultProfile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDefaultProfile(DefaultProfile newDefaultProfile, NotificationChain msgs) {
		DefaultProfile oldDefaultProfile = defaultProfile;
		defaultProfile = newDefaultProfile;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ProfilePackage.FORMAT_PROFILES__DEFAULT_PROFILE, oldDefaultProfile, newDefaultProfile);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDefaultProfile(DefaultProfile newDefaultProfile) {
		if (newDefaultProfile != defaultProfile) {
			NotificationChain msgs = null;
			if (defaultProfile != null)
				msgs = ((InternalEObject)defaultProfile).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ProfilePackage.FORMAT_PROFILES__DEFAULT_PROFILE, null, msgs);
			if (newDefaultProfile != null)
				msgs = ((InternalEObject)newDefaultProfile).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ProfilePackage.FORMAT_PROFILES__DEFAULT_PROFILE, null, msgs);
			msgs = basicSetDefaultProfile(newDefaultProfile, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProfilePackage.FORMAT_PROFILES__DEFAULT_PROFILE, newDefaultProfile, newDefaultProfile));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getProfile() {
		if (profile == null) {
			profile = new EObjectContainmentEList(Profile.class, this, ProfilePackage.FORMAT_PROFILES__PROFILE);
		}
		return profile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSelection() {
		return selection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSelection(String newSelection) {
		String oldSelection = selection;
		selection = newSelection;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProfilePackage.FORMAT_PROFILES__SELECTION, oldSelection, selection));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVersion(String newVersion) {
		String oldVersion = version;
		version = newVersion;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProfilePackage.FORMAT_PROFILES__VERSION, oldVersion, version));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ProfilePackage.FORMAT_PROFILES__DEFAULT_PROFILE:
				return basicSetDefaultProfile(null, msgs);
			case ProfilePackage.FORMAT_PROFILES__PROFILE:
				return ((InternalEList)getProfile()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ProfilePackage.FORMAT_PROFILES__DEFAULT_PROFILE:
				return getDefaultProfile();
			case ProfilePackage.FORMAT_PROFILES__PROFILE:
				return getProfile();
			case ProfilePackage.FORMAT_PROFILES__SELECTION:
				return getSelection();
			case ProfilePackage.FORMAT_PROFILES__VERSION:
				return getVersion();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ProfilePackage.FORMAT_PROFILES__DEFAULT_PROFILE:
				setDefaultProfile((DefaultProfile)newValue);
				return;
			case ProfilePackage.FORMAT_PROFILES__PROFILE:
				getProfile().clear();
				getProfile().addAll((Collection)newValue);
				return;
			case ProfilePackage.FORMAT_PROFILES__SELECTION:
				setSelection((String)newValue);
				return;
			case ProfilePackage.FORMAT_PROFILES__VERSION:
				setVersion((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID) {
		switch (featureID) {
			case ProfilePackage.FORMAT_PROFILES__DEFAULT_PROFILE:
				setDefaultProfile((DefaultProfile)null);
				return;
			case ProfilePackage.FORMAT_PROFILES__PROFILE:
				getProfile().clear();
				return;
			case ProfilePackage.FORMAT_PROFILES__SELECTION:
				setSelection(SELECTION_EDEFAULT);
				return;
			case ProfilePackage.FORMAT_PROFILES__VERSION:
				setVersion(VERSION_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ProfilePackage.FORMAT_PROFILES__DEFAULT_PROFILE:
				return defaultProfile != null;
			case ProfilePackage.FORMAT_PROFILES__PROFILE:
				return profile != null && !profile.isEmpty();
			case ProfilePackage.FORMAT_PROFILES__SELECTION:
				return SELECTION_EDEFAULT == null ? selection != null : !SELECTION_EDEFAULT.equals(selection);
			case ProfilePackage.FORMAT_PROFILES__VERSION:
				return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (selection: ");
		result.append(selection);
		result.append(", version: ");
		result.append(version);
		result.append(')');
		return result.toString();
	}

} //FormatProfilesImpl
