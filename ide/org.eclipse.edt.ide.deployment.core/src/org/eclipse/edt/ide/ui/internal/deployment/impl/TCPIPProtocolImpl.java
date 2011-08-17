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
import org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>TCPIP Protocol</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.TCPIPProtocolImpl#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.TCPIPProtocolImpl#getServerID <em>Server ID</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TCPIPProtocolImpl extends ProtocolImpl implements TCPIPProtocol {
	/**
	 * The default value of the '{@link #getLocation() <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected static final String LOCATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected String location = LOCATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getServerID() <em>Server ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServerID()
	 * @generated
	 * @ordered
	 */
	protected static final String SERVER_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getServerID() <em>Server ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServerID()
	 * @generated
	 * @ordered
	 */
	protected String serverID = SERVER_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TCPIPProtocolImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.TCPIP_PROTOCOL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocation(String newLocation) {
		String oldLocation = location;
		location = newLocation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.TCPIP_PROTOCOL__LOCATION, oldLocation, location));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getServerID() {
		return serverID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setServerID(String newServerID) {
		String oldServerID = serverID;
		serverID = newServerID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.TCPIP_PROTOCOL__SERVER_ID, oldServerID, serverID));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DeploymentPackage.TCPIP_PROTOCOL__LOCATION:
				return getLocation();
			case DeploymentPackage.TCPIP_PROTOCOL__SERVER_ID:
				return getServerID();
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
			case DeploymentPackage.TCPIP_PROTOCOL__LOCATION:
				setLocation((String)newValue);
				return;
			case DeploymentPackage.TCPIP_PROTOCOL__SERVER_ID:
				setServerID((String)newValue);
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
			case DeploymentPackage.TCPIP_PROTOCOL__LOCATION:
				setLocation(LOCATION_EDEFAULT);
				return;
			case DeploymentPackage.TCPIP_PROTOCOL__SERVER_ID:
				setServerID(SERVER_ID_EDEFAULT);
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
			case DeploymentPackage.TCPIP_PROTOCOL__LOCATION:
				return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
			case DeploymentPackage.TCPIP_PROTOCOL__SERVER_ID:
				return SERVER_ID_EDEFAULT == null ? serverID != null : !SERVER_ID_EDEFAULT.equals(serverID);
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
		result.append(" (location: ");
		result.append(location);
		result.append(", serverID: ");
		result.append(serverID);
		result.append(')');
		return result.toString();
	}

} //TCPIPProtocolImpl
