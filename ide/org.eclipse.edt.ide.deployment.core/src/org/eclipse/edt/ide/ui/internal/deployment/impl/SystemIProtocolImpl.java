/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.deployment.impl;

import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>System IProtocol</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SystemIProtocolImpl#getBinddir <em>Binddir</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SystemIProtocolImpl#getLibrary <em>Library</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SystemIProtocolImpl extends ProtocolImpl implements SystemIProtocol {
	/**
	 * The default value of the '{@link #getBinddir() <em>Binddir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBinddir()
	 * @generated
	 * @ordered
	 */
	protected static final String BINDDIR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBinddir() <em>Binddir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBinddir()
	 * @generated
	 * @ordered
	 */
	protected String binddir = BINDDIR_EDEFAULT;

	/**
	 * The default value of the '{@link #getLibrary() <em>Library</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLibrary()
	 * @generated
	 * @ordered
	 */
	protected static final String LIBRARY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLibrary() <em>Library</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLibrary()
	 * @generated
	 * @ordered
	 */
	protected String library = LIBRARY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SystemIProtocolImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.SYSTEM_IPROTOCOL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBinddir() {
		return binddir;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBinddir(String newBinddir) {
		String oldBinddir = binddir;
		binddir = newBinddir;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.SYSTEM_IPROTOCOL__BINDDIR, oldBinddir, binddir));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLibrary() {
		return library;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLibrary(String newLibrary) {
		String oldLibrary = library;
		library = newLibrary;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.SYSTEM_IPROTOCOL__LIBRARY, oldLibrary, library));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DeploymentPackage.SYSTEM_IPROTOCOL__BINDDIR:
				return getBinddir();
			case DeploymentPackage.SYSTEM_IPROTOCOL__LIBRARY:
				return getLibrary();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DeploymentPackage.SYSTEM_IPROTOCOL__BINDDIR:
				setBinddir((String)newValue);
				return;
			case DeploymentPackage.SYSTEM_IPROTOCOL__LIBRARY:
				setLibrary((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case DeploymentPackage.SYSTEM_IPROTOCOL__BINDDIR:
				setBinddir(BINDDIR_EDEFAULT);
				return;
			case DeploymentPackage.SYSTEM_IPROTOCOL__LIBRARY:
				setLibrary(LIBRARY_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case DeploymentPackage.SYSTEM_IPROTOCOL__BINDDIR:
				return BINDDIR_EDEFAULT == null ? binddir != null : !BINDDIR_EDEFAULT.equals(binddir);
			case DeploymentPackage.SYSTEM_IPROTOCOL__LIBRARY:
				return LIBRARY_EDEFAULT == null ? library != null : !LIBRARY_EDEFAULT.equals(library);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (binddir: ");
		result.append(binddir);
		result.append(", library: ");
		result.append(library);
		result.append(')');
		return result.toString();
	}

} //SystemIProtocolImpl
