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

import org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>CICSJ2C Protocol</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSJ2CProtocolImpl#getConversionTable <em>Conversion Table</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSJ2CProtocolImpl#getLocation <em>Location</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CICSJ2CProtocolImpl extends ProtocolImpl implements CICSJ2CProtocol {
	/**
	 * The default value of the '{@link #getConversionTable() <em>Conversion Table</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConversionTable()
	 * @generated
	 * @ordered
	 */
	protected static final String CONVERSION_TABLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getConversionTable() <em>Conversion Table</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConversionTable()
	 * @generated
	 * @ordered
	 */
	protected String conversionTable = CONVERSION_TABLE_EDEFAULT;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CICSJ2CProtocolImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.CICSJ2C_PROTOCOL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getConversionTable() {
		return conversionTable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setConversionTable(String newConversionTable) {
		String oldConversionTable = conversionTable;
		conversionTable = newConversionTable;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.CICSJ2C_PROTOCOL__CONVERSION_TABLE, oldConversionTable, conversionTable));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.CICSJ2C_PROTOCOL__LOCATION, oldLocation, location));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DeploymentPackage.CICSJ2C_PROTOCOL__CONVERSION_TABLE:
				return getConversionTable();
			case DeploymentPackage.CICSJ2C_PROTOCOL__LOCATION:
				return getLocation();
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
			case DeploymentPackage.CICSJ2C_PROTOCOL__CONVERSION_TABLE:
				setConversionTable((String)newValue);
				return;
			case DeploymentPackage.CICSJ2C_PROTOCOL__LOCATION:
				setLocation((String)newValue);
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
			case DeploymentPackage.CICSJ2C_PROTOCOL__CONVERSION_TABLE:
				setConversionTable(CONVERSION_TABLE_EDEFAULT);
				return;
			case DeploymentPackage.CICSJ2C_PROTOCOL__LOCATION:
				setLocation(LOCATION_EDEFAULT);
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
			case DeploymentPackage.CICSJ2C_PROTOCOL__CONVERSION_TABLE:
				return CONVERSION_TABLE_EDEFAULT == null ? conversionTable != null : !CONVERSION_TABLE_EDEFAULT.equals(conversionTable);
			case DeploymentPackage.CICSJ2C_PROTOCOL__LOCATION:
				return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
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
		result.append(" (conversionTable: ");
		result.append(conversionTable);
		result.append(", location: ");
		result.append(location);
		result.append(')');
		return result.toString();
	}

} //CICSJ2CProtocolImpl
