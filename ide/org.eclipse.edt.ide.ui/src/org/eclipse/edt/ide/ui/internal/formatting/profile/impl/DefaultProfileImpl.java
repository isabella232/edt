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

import org.eclipse.edt.ide.ui.internal.formatting.profile.Category;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Controls;
import org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Preview;
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
 * An implementation of the model object '<em><b>Default Profile</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.DefaultProfileImpl#getPreview <em>Preview</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.DefaultProfileImpl#getControls <em>Controls</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.DefaultProfileImpl#getCategory <em>Category</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.formatting.profile.impl.DefaultProfileImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DefaultProfileImpl extends EObjectImpl implements DefaultProfile {
	/**
	 * The cached value of the '{@link #getPreview() <em>Preview</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPreview()
	 * @generated
	 * @ordered
	 */
	protected Preview preview = null;

	/**
	 * The cached value of the '{@link #getControls() <em>Controls</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getControls()
	 * @generated
	 * @ordered
	 */
	protected Controls controls = null;

	/**
	 * The cached value of the '{@link #getCategory() <em>Category</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCategory()
	 * @generated
	 * @ordered
	 */
	protected EList category = null;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DefaultProfileImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ProfilePackage.Literals.DEFAULT_PROFILE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Preview getPreview() {
		return preview;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPreview(Preview newPreview, NotificationChain msgs) {
		Preview oldPreview = preview;
		preview = newPreview;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ProfilePackage.DEFAULT_PROFILE__PREVIEW, oldPreview, newPreview);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPreview(Preview newPreview) {
		if (newPreview != preview) {
			NotificationChain msgs = null;
			if (preview != null)
				msgs = ((InternalEObject)preview).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ProfilePackage.DEFAULT_PROFILE__PREVIEW, null, msgs);
			if (newPreview != null)
				msgs = ((InternalEObject)newPreview).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ProfilePackage.DEFAULT_PROFILE__PREVIEW, null, msgs);
			msgs = basicSetPreview(newPreview, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProfilePackage.DEFAULT_PROFILE__PREVIEW, newPreview, newPreview));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Controls getControls() {
		return controls;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetControls(Controls newControls, NotificationChain msgs) {
		Controls oldControls = controls;
		controls = newControls;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ProfilePackage.DEFAULT_PROFILE__CONTROLS, oldControls, newControls);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setControls(Controls newControls) {
		if (newControls != controls) {
			NotificationChain msgs = null;
			if (controls != null)
				msgs = ((InternalEObject)controls).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ProfilePackage.DEFAULT_PROFILE__CONTROLS, null, msgs);
			if (newControls != null)
				msgs = ((InternalEObject)newControls).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ProfilePackage.DEFAULT_PROFILE__CONTROLS, null, msgs);
			msgs = basicSetControls(newControls, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProfilePackage.DEFAULT_PROFILE__CONTROLS, newControls, newControls));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getCategory() {
		if (category == null) {
			category = new EObjectContainmentEList(Category.class, this, ProfilePackage.DEFAULT_PROFILE__CATEGORY);
		}
		return category;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProfilePackage.DEFAULT_PROFILE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ProfilePackage.DEFAULT_PROFILE__PREVIEW:
				return basicSetPreview(null, msgs);
			case ProfilePackage.DEFAULT_PROFILE__CONTROLS:
				return basicSetControls(null, msgs);
			case ProfilePackage.DEFAULT_PROFILE__CATEGORY:
				return ((InternalEList)getCategory()).basicRemove(otherEnd, msgs);
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
			case ProfilePackage.DEFAULT_PROFILE__PREVIEW:
				return getPreview();
			case ProfilePackage.DEFAULT_PROFILE__CONTROLS:
				return getControls();
			case ProfilePackage.DEFAULT_PROFILE__CATEGORY:
				return getCategory();
			case ProfilePackage.DEFAULT_PROFILE__NAME:
				return getName();
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
			case ProfilePackage.DEFAULT_PROFILE__PREVIEW:
				setPreview((Preview)newValue);
				return;
			case ProfilePackage.DEFAULT_PROFILE__CONTROLS:
				setControls((Controls)newValue);
				return;
			case ProfilePackage.DEFAULT_PROFILE__CATEGORY:
				getCategory().clear();
				getCategory().addAll((Collection)newValue);
				return;
			case ProfilePackage.DEFAULT_PROFILE__NAME:
				setName((String)newValue);
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
			case ProfilePackage.DEFAULT_PROFILE__PREVIEW:
				setPreview((Preview)null);
				return;
			case ProfilePackage.DEFAULT_PROFILE__CONTROLS:
				setControls((Controls)null);
				return;
			case ProfilePackage.DEFAULT_PROFILE__CATEGORY:
				getCategory().clear();
				return;
			case ProfilePackage.DEFAULT_PROFILE__NAME:
				setName(NAME_EDEFAULT);
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
			case ProfilePackage.DEFAULT_PROFILE__PREVIEW:
				return preview != null;
			case ProfilePackage.DEFAULT_PROFILE__CONTROLS:
				return controls != null;
			case ProfilePackage.DEFAULT_PROFILE__CATEGORY:
				return category != null && !category.isEmpty();
			case ProfilePackage.DEFAULT_PROFILE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
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
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //DefaultProfileImpl
