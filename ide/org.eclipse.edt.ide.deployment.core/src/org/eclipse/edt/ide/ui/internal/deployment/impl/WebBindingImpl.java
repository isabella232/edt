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
import org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType;
import org.eclipse.edt.ide.ui.internal.deployment.WebBinding;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Web Binding</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebBindingImpl#isEnableGeneration <em>Enable Generation</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebBindingImpl#getInterface <em>Interface</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebBindingImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebBindingImpl#getSoapVersion <em>Soap Version</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebBindingImpl#getUri <em>Uri</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebBindingImpl#getWsdlLocation <em>Wsdl Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebBindingImpl#getWsdlPort <em>Wsdl Port</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebBindingImpl#getWsdlService <em>Wsdl Service</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WebBindingImpl extends EObjectImpl implements WebBinding {
	/**
	 * The default value of the '{@link #isEnableGeneration() <em>Enable Generation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEnableGeneration()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ENABLE_GENERATION_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isEnableGeneration() <em>Enable Generation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEnableGeneration()
	 * @generated
	 * @ordered
	 */
	protected boolean enableGeneration = ENABLE_GENERATION_EDEFAULT;

	/**
	 * This is true if the Enable Generation attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean enableGenerationESet;

	/**
	 * The default value of the '{@link #getInterface() <em>Interface</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterface()
	 * @generated
	 * @ordered
	 */
	protected static final String INTERFACE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getInterface() <em>Interface</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterface()
	 * @generated
	 * @ordered
	 */
	protected String interface_ = INTERFACE_EDEFAULT;

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
	 * The default value of the '{@link #getSoapVersion() <em>Soap Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSoapVersion()
	 * @generated
	 * @ordered
	 */
	protected static final SOAPVersionType SOAP_VERSION_EDEFAULT = SOAPVersionType.SOAP11;

	/**
	 * The cached value of the '{@link #getSoapVersion() <em>Soap Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSoapVersion()
	 * @generated
	 * @ordered
	 */
	protected SOAPVersionType soapVersion = SOAP_VERSION_EDEFAULT;

	/**
	 * This is true if the Soap Version attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean soapVersionESet;

	/**
	 * The default value of the '{@link #getUri() <em>Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUri()
	 * @generated
	 * @ordered
	 */
	protected static final String URI_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUri() <em>Uri</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUri()
	 * @generated
	 * @ordered
	 */
	protected String uri = URI_EDEFAULT;

	/**
	 * The default value of the '{@link #getWsdlLocation() <em>Wsdl Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWsdlLocation()
	 * @generated
	 * @ordered
	 */
	protected static final String WSDL_LOCATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getWsdlLocation() <em>Wsdl Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWsdlLocation()
	 * @generated
	 * @ordered
	 */
	protected String wsdlLocation = WSDL_LOCATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getWsdlPort() <em>Wsdl Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWsdlPort()
	 * @generated
	 * @ordered
	 */
	protected static final String WSDL_PORT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getWsdlPort() <em>Wsdl Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWsdlPort()
	 * @generated
	 * @ordered
	 */
	protected String wsdlPort = WSDL_PORT_EDEFAULT;

	/**
	 * The default value of the '{@link #getWsdlService() <em>Wsdl Service</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWsdlService()
	 * @generated
	 * @ordered
	 */
	protected static final String WSDL_SERVICE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getWsdlService() <em>Wsdl Service</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWsdlService()
	 * @generated
	 * @ordered
	 */
	protected String wsdlService = WSDL_SERVICE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WebBindingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.WEB_BINDING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isEnableGeneration() {
		return enableGeneration;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEnableGeneration(boolean newEnableGeneration) {
		boolean oldEnableGeneration = enableGeneration;
		enableGeneration = newEnableGeneration;
		boolean oldEnableGenerationESet = enableGenerationESet;
		enableGenerationESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEB_BINDING__ENABLE_GENERATION, oldEnableGeneration, enableGeneration, !oldEnableGenerationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetEnableGeneration() {
		boolean oldEnableGeneration = enableGeneration;
		boolean oldEnableGenerationESet = enableGenerationESet;
		enableGeneration = ENABLE_GENERATION_EDEFAULT;
		enableGenerationESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.WEB_BINDING__ENABLE_GENERATION, oldEnableGeneration, ENABLE_GENERATION_EDEFAULT, oldEnableGenerationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetEnableGeneration() {
		return enableGenerationESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getInterface() {
		return interface_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInterface(String newInterface) {
		String oldInterface = interface_;
		interface_ = newInterface;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEB_BINDING__INTERFACE, oldInterface, interface_));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEB_BINDING__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SOAPVersionType getSoapVersion() {
		return soapVersion;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSoapVersion(SOAPVersionType newSoapVersion) {
		SOAPVersionType oldSoapVersion = soapVersion;
		soapVersion = newSoapVersion == null ? SOAP_VERSION_EDEFAULT : newSoapVersion;
		boolean oldSoapVersionESet = soapVersionESet;
		soapVersionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEB_BINDING__SOAP_VERSION, oldSoapVersion, soapVersion, !oldSoapVersionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetSoapVersion() {
		SOAPVersionType oldSoapVersion = soapVersion;
		boolean oldSoapVersionESet = soapVersionESet;
		soapVersion = SOAP_VERSION_EDEFAULT;
		soapVersionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.WEB_BINDING__SOAP_VERSION, oldSoapVersion, SOAP_VERSION_EDEFAULT, oldSoapVersionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetSoapVersion() {
		return soapVersionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUri(String newUri) {
		String oldUri = uri;
		uri = newUri;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEB_BINDING__URI, oldUri, uri));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getWsdlLocation() {
		return wsdlLocation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWsdlLocation(String newWsdlLocation) {
		String oldWsdlLocation = wsdlLocation;
		wsdlLocation = newWsdlLocation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEB_BINDING__WSDL_LOCATION, oldWsdlLocation, wsdlLocation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getWsdlPort() {
		return wsdlPort;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWsdlPort(String newWsdlPort) {
		String oldWsdlPort = wsdlPort;
		wsdlPort = newWsdlPort;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEB_BINDING__WSDL_PORT, oldWsdlPort, wsdlPort));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getWsdlService() {
		return wsdlService;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWsdlService(String newWsdlService) {
		String oldWsdlService = wsdlService;
		wsdlService = newWsdlService;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEB_BINDING__WSDL_SERVICE, oldWsdlService, wsdlService));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DeploymentPackage.WEB_BINDING__ENABLE_GENERATION:
				return isEnableGeneration();
			case DeploymentPackage.WEB_BINDING__INTERFACE:
				return getInterface();
			case DeploymentPackage.WEB_BINDING__NAME:
				return getName();
			case DeploymentPackage.WEB_BINDING__SOAP_VERSION:
				return getSoapVersion();
			case DeploymentPackage.WEB_BINDING__URI:
				return getUri();
			case DeploymentPackage.WEB_BINDING__WSDL_LOCATION:
				return getWsdlLocation();
			case DeploymentPackage.WEB_BINDING__WSDL_PORT:
				return getWsdlPort();
			case DeploymentPackage.WEB_BINDING__WSDL_SERVICE:
				return getWsdlService();
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
			case DeploymentPackage.WEB_BINDING__ENABLE_GENERATION:
				setEnableGeneration((Boolean)newValue);
				return;
			case DeploymentPackage.WEB_BINDING__INTERFACE:
				setInterface((String)newValue);
				return;
			case DeploymentPackage.WEB_BINDING__NAME:
				setName((String)newValue);
				return;
			case DeploymentPackage.WEB_BINDING__SOAP_VERSION:
				setSoapVersion((SOAPVersionType)newValue);
				return;
			case DeploymentPackage.WEB_BINDING__URI:
				setUri((String)newValue);
				return;
			case DeploymentPackage.WEB_BINDING__WSDL_LOCATION:
				setWsdlLocation((String)newValue);
				return;
			case DeploymentPackage.WEB_BINDING__WSDL_PORT:
				setWsdlPort((String)newValue);
				return;
			case DeploymentPackage.WEB_BINDING__WSDL_SERVICE:
				setWsdlService((String)newValue);
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
			case DeploymentPackage.WEB_BINDING__ENABLE_GENERATION:
				unsetEnableGeneration();
				return;
			case DeploymentPackage.WEB_BINDING__INTERFACE:
				setInterface(INTERFACE_EDEFAULT);
				return;
			case DeploymentPackage.WEB_BINDING__NAME:
				setName(NAME_EDEFAULT);
				return;
			case DeploymentPackage.WEB_BINDING__SOAP_VERSION:
				unsetSoapVersion();
				return;
			case DeploymentPackage.WEB_BINDING__URI:
				setUri(URI_EDEFAULT);
				return;
			case DeploymentPackage.WEB_BINDING__WSDL_LOCATION:
				setWsdlLocation(WSDL_LOCATION_EDEFAULT);
				return;
			case DeploymentPackage.WEB_BINDING__WSDL_PORT:
				setWsdlPort(WSDL_PORT_EDEFAULT);
				return;
			case DeploymentPackage.WEB_BINDING__WSDL_SERVICE:
				setWsdlService(WSDL_SERVICE_EDEFAULT);
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
			case DeploymentPackage.WEB_BINDING__ENABLE_GENERATION:
				return isSetEnableGeneration();
			case DeploymentPackage.WEB_BINDING__INTERFACE:
				return INTERFACE_EDEFAULT == null ? interface_ != null : !INTERFACE_EDEFAULT.equals(interface_);
			case DeploymentPackage.WEB_BINDING__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DeploymentPackage.WEB_BINDING__SOAP_VERSION:
				return isSetSoapVersion();
			case DeploymentPackage.WEB_BINDING__URI:
				return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
			case DeploymentPackage.WEB_BINDING__WSDL_LOCATION:
				return WSDL_LOCATION_EDEFAULT == null ? wsdlLocation != null : !WSDL_LOCATION_EDEFAULT.equals(wsdlLocation);
			case DeploymentPackage.WEB_BINDING__WSDL_PORT:
				return WSDL_PORT_EDEFAULT == null ? wsdlPort != null : !WSDL_PORT_EDEFAULT.equals(wsdlPort);
			case DeploymentPackage.WEB_BINDING__WSDL_SERVICE:
				return WSDL_SERVICE_EDEFAULT == null ? wsdlService != null : !WSDL_SERVICE_EDEFAULT.equals(wsdlService);
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
		result.append(" (enableGeneration: ");
		if (enableGenerationESet) result.append(enableGeneration); else result.append("<unset>");
		result.append(", interface: ");
		result.append(interface_);
		result.append(", name: ");
		result.append(name);
		result.append(", soapVersion: ");
		if (soapVersionESet) result.append(soapVersion); else result.append("<unset>");
		result.append(", uri: ");
		result.append(uri);
		result.append(", wsdlLocation: ");
		result.append(wsdlLocation);
		result.append(", wsdlPort: ");
		result.append(wsdlPort);
		result.append(", wsdlService: ");
		result.append(wsdlService);
		result.append(')');
		return result.toString();
	}

} //WebBindingImpl
