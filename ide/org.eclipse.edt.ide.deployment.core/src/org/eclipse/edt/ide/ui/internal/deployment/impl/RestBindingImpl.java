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
import org.eclipse.edt.ide.ui.internal.deployment.RestBinding;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Rest Binding</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestBindingImpl#getBaseURI <em>Base URI</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestBindingImpl#isEnableGeneration <em>Enable Generation</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestBindingImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestBindingImpl#isPreserveRequestHeaders <em>Preserve Request Headers</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RestBindingImpl#getSessionCookieId <em>Session Cookie Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RestBindingImpl extends EObjectImpl implements RestBinding {
	/**
	 * The default value of the '{@link #getBaseURI() <em>Base URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBaseURI()
	 * @generated
	 * @ordered
	 */
	protected static final String BASE_URI_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBaseURI() <em>Base URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBaseURI()
	 * @generated
	 * @ordered
	 */
	protected String baseURI = BASE_URI_EDEFAULT;

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
	 * The default value of the '{@link #isPreserveRequestHeaders() <em>Preserve Request Headers</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPreserveRequestHeaders()
	 * @generated
	 * @ordered
	 */
	protected static final boolean PRESERVE_REQUEST_HEADERS_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isPreserveRequestHeaders() <em>Preserve Request Headers</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPreserveRequestHeaders()
	 * @generated
	 * @ordered
	 */
	protected boolean preserveRequestHeaders = PRESERVE_REQUEST_HEADERS_EDEFAULT;

	/**
	 * This is true if the Preserve Request Headers attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean preserveRequestHeadersESet;

	/**
	 * The default value of the '{@link #getSessionCookieId() <em>Session Cookie Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSessionCookieId()
	 * @generated
	 * @ordered
	 */
	protected static final String SESSION_COOKIE_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSessionCookieId() <em>Session Cookie Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSessionCookieId()
	 * @generated
	 * @ordered
	 */
	protected String sessionCookieId = SESSION_COOKIE_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RestBindingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.REST_BINDING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBaseURI() {
		return baseURI;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBaseURI(String newBaseURI) {
		String oldBaseURI = baseURI;
		baseURI = newBaseURI;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.REST_BINDING__BASE_URI, oldBaseURI, baseURI));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.REST_BINDING__ENABLE_GENERATION, oldEnableGeneration, enableGeneration, !oldEnableGenerationESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.REST_BINDING__ENABLE_GENERATION, oldEnableGeneration, ENABLE_GENERATION_EDEFAULT, oldEnableGenerationESet));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.REST_BINDING__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isPreserveRequestHeaders() {
		return preserveRequestHeaders;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPreserveRequestHeaders(boolean newPreserveRequestHeaders) {
		boolean oldPreserveRequestHeaders = preserveRequestHeaders;
		preserveRequestHeaders = newPreserveRequestHeaders;
		boolean oldPreserveRequestHeadersESet = preserveRequestHeadersESet;
		preserveRequestHeadersESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.REST_BINDING__PRESERVE_REQUEST_HEADERS, oldPreserveRequestHeaders, preserveRequestHeaders, !oldPreserveRequestHeadersESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetPreserveRequestHeaders() {
		boolean oldPreserveRequestHeaders = preserveRequestHeaders;
		boolean oldPreserveRequestHeadersESet = preserveRequestHeadersESet;
		preserveRequestHeaders = PRESERVE_REQUEST_HEADERS_EDEFAULT;
		preserveRequestHeadersESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.REST_BINDING__PRESERVE_REQUEST_HEADERS, oldPreserveRequestHeaders, PRESERVE_REQUEST_HEADERS_EDEFAULT, oldPreserveRequestHeadersESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetPreserveRequestHeaders() {
		return preserveRequestHeadersESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSessionCookieId() {
		return sessionCookieId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSessionCookieId(String newSessionCookieId) {
		String oldSessionCookieId = sessionCookieId;
		sessionCookieId = newSessionCookieId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.REST_BINDING__SESSION_COOKIE_ID, oldSessionCookieId, sessionCookieId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DeploymentPackage.REST_BINDING__BASE_URI:
				return getBaseURI();
			case DeploymentPackage.REST_BINDING__ENABLE_GENERATION:
				return isEnableGeneration();
			case DeploymentPackage.REST_BINDING__NAME:
				return getName();
			case DeploymentPackage.REST_BINDING__PRESERVE_REQUEST_HEADERS:
				return isPreserveRequestHeaders();
			case DeploymentPackage.REST_BINDING__SESSION_COOKIE_ID:
				return getSessionCookieId();
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
			case DeploymentPackage.REST_BINDING__BASE_URI:
				setBaseURI((String)newValue);
				return;
			case DeploymentPackage.REST_BINDING__ENABLE_GENERATION:
				setEnableGeneration((Boolean)newValue);
				return;
			case DeploymentPackage.REST_BINDING__NAME:
				setName((String)newValue);
				return;
			case DeploymentPackage.REST_BINDING__PRESERVE_REQUEST_HEADERS:
				setPreserveRequestHeaders((Boolean)newValue);
				return;
			case DeploymentPackage.REST_BINDING__SESSION_COOKIE_ID:
				setSessionCookieId((String)newValue);
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
			case DeploymentPackage.REST_BINDING__BASE_URI:
				setBaseURI(BASE_URI_EDEFAULT);
				return;
			case DeploymentPackage.REST_BINDING__ENABLE_GENERATION:
				unsetEnableGeneration();
				return;
			case DeploymentPackage.REST_BINDING__NAME:
				setName(NAME_EDEFAULT);
				return;
			case DeploymentPackage.REST_BINDING__PRESERVE_REQUEST_HEADERS:
				unsetPreserveRequestHeaders();
				return;
			case DeploymentPackage.REST_BINDING__SESSION_COOKIE_ID:
				setSessionCookieId(SESSION_COOKIE_ID_EDEFAULT);
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
			case DeploymentPackage.REST_BINDING__BASE_URI:
				return BASE_URI_EDEFAULT == null ? baseURI != null : !BASE_URI_EDEFAULT.equals(baseURI);
			case DeploymentPackage.REST_BINDING__ENABLE_GENERATION:
				return isSetEnableGeneration();
			case DeploymentPackage.REST_BINDING__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DeploymentPackage.REST_BINDING__PRESERVE_REQUEST_HEADERS:
				return isSetPreserveRequestHeaders();
			case DeploymentPackage.REST_BINDING__SESSION_COOKIE_ID:
				return SESSION_COOKIE_ID_EDEFAULT == null ? sessionCookieId != null : !SESSION_COOKIE_ID_EDEFAULT.equals(sessionCookieId);
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
		result.append(" (baseURI: ");
		result.append(baseURI);
		result.append(", enableGeneration: ");
		if (enableGenerationESet) result.append(enableGeneration); else result.append("<unset>");
		result.append(", name: ");
		result.append(name);
		result.append(", preserveRequestHeaders: ");
		if (preserveRequestHeadersESet) result.append(preserveRequestHeaders); else result.append("<unset>");
		result.append(", sessionCookieId: ");
		result.append(sessionCookieId);
		result.append(')');
		return result.toString();
	}

} //RestBindingImpl
