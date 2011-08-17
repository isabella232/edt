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
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType;
import org.eclipse.edt.ide.ui.internal.deployment.StyleTypes;
import org.eclipse.edt.ide.ui.internal.deployment.Webservice;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Webservice</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#isEnableGeneration <em>Enable Generation</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#getImplementation <em>Implementation</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#getImplType <em>Impl Type</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#getProtocol <em>Protocol</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#getSoapVersion <em>Soap Version</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#getStyle <em>Style</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#getTransaction <em>Transaction</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#getUri <em>Uri</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#isUseExistingWSDL <em>Use Existing WSDL</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#getUserID <em>User ID</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#getWsdlLocation <em>Wsdl Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#getWsdlPort <em>Wsdl Port</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.WebserviceImpl#getWsdlService <em>Wsdl Service</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WebserviceImpl extends EObjectImpl implements Webservice {
	/**
	 * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameters()
	 * @generated
	 * @ordered
	 */
	protected Parameters parameters;

	/**
	 * The default value of the '{@link #isEnableGeneration() <em>Enable Generation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEnableGeneration()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ENABLE_GENERATION_EDEFAULT = true;

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
	 * The default value of the '{@link #getImplementation() <em>Implementation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplementation()
	 * @generated
	 * @ordered
	 */
	protected static final String IMPLEMENTATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getImplementation() <em>Implementation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplementation()
	 * @generated
	 * @ordered
	 */
	protected String implementation = IMPLEMENTATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getImplType() <em>Impl Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplType()
	 * @generated
	 * @ordered
	 */
	protected static final int IMPL_TYPE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getImplType() <em>Impl Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplType()
	 * @generated
	 * @ordered
	 */
	protected int implType = IMPL_TYPE_EDEFAULT;

	/**
	 * This is true if the Impl Type attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean implTypeESet;

	/**
	 * The default value of the '{@link #getProtocol() <em>Protocol</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProtocol()
	 * @generated
	 * @ordered
	 */
	protected static final String PROTOCOL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProtocol() <em>Protocol</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProtocol()
	 * @generated
	 * @ordered
	 */
	protected String protocol = PROTOCOL_EDEFAULT;

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
	 * The default value of the '{@link #getStyle() <em>Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStyle()
	 * @generated
	 * @ordered
	 */
	protected static final StyleTypes STYLE_EDEFAULT = StyleTypes.DOCUMENT_WRAPPED;

	/**
	 * The cached value of the '{@link #getStyle() <em>Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStyle()
	 * @generated
	 * @ordered
	 */
	protected StyleTypes style = STYLE_EDEFAULT;

	/**
	 * This is true if the Style attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean styleESet;

	/**
	 * The default value of the '{@link #getTransaction() <em>Transaction</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransaction()
	 * @generated
	 * @ordered
	 */
	protected static final String TRANSACTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTransaction() <em>Transaction</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransaction()
	 * @generated
	 * @ordered
	 */
	protected String transaction = TRANSACTION_EDEFAULT;

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
	 * The default value of the '{@link #isUseExistingWSDL() <em>Use Existing WSDL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUseExistingWSDL()
	 * @generated
	 * @ordered
	 */
	protected static final boolean USE_EXISTING_WSDL_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isUseExistingWSDL() <em>Use Existing WSDL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isUseExistingWSDL()
	 * @generated
	 * @ordered
	 */
	protected boolean useExistingWSDL = USE_EXISTING_WSDL_EDEFAULT;

	/**
	 * This is true if the Use Existing WSDL attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean useExistingWSDLESet;

	/**
	 * The default value of the '{@link #getUserID() <em>User ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUserID()
	 * @generated
	 * @ordered
	 */
	protected static final String USER_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUserID() <em>User ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUserID()
	 * @generated
	 * @ordered
	 */
	protected String userID = USER_ID_EDEFAULT;

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
	protected WebserviceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.WEBSERVICE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameters getParameters() {
		return parameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParameters(Parameters newParameters, NotificationChain msgs) {
		Parameters oldParameters = parameters;
		parameters = newParameters;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__PARAMETERS, oldParameters, newParameters);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParameters(Parameters newParameters) {
		if (newParameters != parameters) {
			NotificationChain msgs = null;
			if (parameters != null)
				msgs = ((InternalEObject)parameters).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.WEBSERVICE__PARAMETERS, null, msgs);
			if (newParameters != null)
				msgs = ((InternalEObject)newParameters).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.WEBSERVICE__PARAMETERS, null, msgs);
			msgs = basicSetParameters(newParameters, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__PARAMETERS, newParameters, newParameters));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__ENABLE_GENERATION, oldEnableGeneration, enableGeneration, !oldEnableGenerationESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.WEBSERVICE__ENABLE_GENERATION, oldEnableGeneration, ENABLE_GENERATION_EDEFAULT, oldEnableGenerationESet));
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
	public String getImplementation() {
		return implementation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImplementation(String newImplementation) {
		String oldImplementation = implementation;
		implementation = newImplementation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__IMPLEMENTATION, oldImplementation, implementation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getImplType() {
		return implType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImplType(int newImplType) {
		int oldImplType = implType;
		implType = newImplType;
		boolean oldImplTypeESet = implTypeESet;
		implTypeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__IMPL_TYPE, oldImplType, implType, !oldImplTypeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetImplType() {
		int oldImplType = implType;
		boolean oldImplTypeESet = implTypeESet;
		implType = IMPL_TYPE_EDEFAULT;
		implTypeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.WEBSERVICE__IMPL_TYPE, oldImplType, IMPL_TYPE_EDEFAULT, oldImplTypeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetImplType() {
		return implTypeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocol(String newProtocol) {
		String oldProtocol = protocol;
		protocol = newProtocol;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__PROTOCOL, oldProtocol, protocol));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__SOAP_VERSION, oldSoapVersion, soapVersion, !oldSoapVersionESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.WEBSERVICE__SOAP_VERSION, oldSoapVersion, SOAP_VERSION_EDEFAULT, oldSoapVersionESet));
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
	public StyleTypes getStyle() {
		return style;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStyle(StyleTypes newStyle) {
		StyleTypes oldStyle = style;
		style = newStyle == null ? STYLE_EDEFAULT : newStyle;
		boolean oldStyleESet = styleESet;
		styleESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__STYLE, oldStyle, style, !oldStyleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetStyle() {
		StyleTypes oldStyle = style;
		boolean oldStyleESet = styleESet;
		style = STYLE_EDEFAULT;
		styleESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.WEBSERVICE__STYLE, oldStyle, STYLE_EDEFAULT, oldStyleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetStyle() {
		return styleESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTransaction() {
		return transaction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTransaction(String newTransaction) {
		String oldTransaction = transaction;
		transaction = newTransaction;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__TRANSACTION, oldTransaction, transaction));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__URI, oldUri, uri));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isUseExistingWSDL() {
		return useExistingWSDL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUseExistingWSDL(boolean newUseExistingWSDL) {
		boolean oldUseExistingWSDL = useExistingWSDL;
		useExistingWSDL = newUseExistingWSDL;
		boolean oldUseExistingWSDLESet = useExistingWSDLESet;
		useExistingWSDLESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__USE_EXISTING_WSDL, oldUseExistingWSDL, useExistingWSDL, !oldUseExistingWSDLESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetUseExistingWSDL() {
		boolean oldUseExistingWSDL = useExistingWSDL;
		boolean oldUseExistingWSDLESet = useExistingWSDLESet;
		useExistingWSDL = USE_EXISTING_WSDL_EDEFAULT;
		useExistingWSDLESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.WEBSERVICE__USE_EXISTING_WSDL, oldUseExistingWSDL, USE_EXISTING_WSDL_EDEFAULT, oldUseExistingWSDLESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetUseExistingWSDL() {
		return useExistingWSDLESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUserID(String newUserID) {
		String oldUserID = userID;
		userID = newUserID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__USER_ID, oldUserID, userID));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__WSDL_LOCATION, oldWsdlLocation, wsdlLocation));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__WSDL_PORT, oldWsdlPort, wsdlPort));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.WEBSERVICE__WSDL_SERVICE, oldWsdlService, wsdlService));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DeploymentPackage.WEBSERVICE__PARAMETERS:
				return basicSetParameters(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DeploymentPackage.WEBSERVICE__PARAMETERS:
				return getParameters();
			case DeploymentPackage.WEBSERVICE__ENABLE_GENERATION:
				return isEnableGeneration();
			case DeploymentPackage.WEBSERVICE__IMPLEMENTATION:
				return getImplementation();
			case DeploymentPackage.WEBSERVICE__IMPL_TYPE:
				return getImplType();
			case DeploymentPackage.WEBSERVICE__PROTOCOL:
				return getProtocol();
			case DeploymentPackage.WEBSERVICE__SOAP_VERSION:
				return getSoapVersion();
			case DeploymentPackage.WEBSERVICE__STYLE:
				return getStyle();
			case DeploymentPackage.WEBSERVICE__TRANSACTION:
				return getTransaction();
			case DeploymentPackage.WEBSERVICE__URI:
				return getUri();
			case DeploymentPackage.WEBSERVICE__USE_EXISTING_WSDL:
				return isUseExistingWSDL();
			case DeploymentPackage.WEBSERVICE__USER_ID:
				return getUserID();
			case DeploymentPackage.WEBSERVICE__WSDL_LOCATION:
				return getWsdlLocation();
			case DeploymentPackage.WEBSERVICE__WSDL_PORT:
				return getWsdlPort();
			case DeploymentPackage.WEBSERVICE__WSDL_SERVICE:
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
			case DeploymentPackage.WEBSERVICE__PARAMETERS:
				setParameters((Parameters)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__ENABLE_GENERATION:
				setEnableGeneration((Boolean)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__IMPLEMENTATION:
				setImplementation((String)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__IMPL_TYPE:
				setImplType((Integer)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__PROTOCOL:
				setProtocol((String)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__SOAP_VERSION:
				setSoapVersion((SOAPVersionType)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__STYLE:
				setStyle((StyleTypes)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__TRANSACTION:
				setTransaction((String)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__URI:
				setUri((String)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__USE_EXISTING_WSDL:
				setUseExistingWSDL((Boolean)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__USER_ID:
				setUserID((String)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__WSDL_LOCATION:
				setWsdlLocation((String)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__WSDL_PORT:
				setWsdlPort((String)newValue);
				return;
			case DeploymentPackage.WEBSERVICE__WSDL_SERVICE:
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
			case DeploymentPackage.WEBSERVICE__PARAMETERS:
				setParameters((Parameters)null);
				return;
			case DeploymentPackage.WEBSERVICE__ENABLE_GENERATION:
				unsetEnableGeneration();
				return;
			case DeploymentPackage.WEBSERVICE__IMPLEMENTATION:
				setImplementation(IMPLEMENTATION_EDEFAULT);
				return;
			case DeploymentPackage.WEBSERVICE__IMPL_TYPE:
				unsetImplType();
				return;
			case DeploymentPackage.WEBSERVICE__PROTOCOL:
				setProtocol(PROTOCOL_EDEFAULT);
				return;
			case DeploymentPackage.WEBSERVICE__SOAP_VERSION:
				unsetSoapVersion();
				return;
			case DeploymentPackage.WEBSERVICE__STYLE:
				unsetStyle();
				return;
			case DeploymentPackage.WEBSERVICE__TRANSACTION:
				setTransaction(TRANSACTION_EDEFAULT);
				return;
			case DeploymentPackage.WEBSERVICE__URI:
				setUri(URI_EDEFAULT);
				return;
			case DeploymentPackage.WEBSERVICE__USE_EXISTING_WSDL:
				unsetUseExistingWSDL();
				return;
			case DeploymentPackage.WEBSERVICE__USER_ID:
				setUserID(USER_ID_EDEFAULT);
				return;
			case DeploymentPackage.WEBSERVICE__WSDL_LOCATION:
				setWsdlLocation(WSDL_LOCATION_EDEFAULT);
				return;
			case DeploymentPackage.WEBSERVICE__WSDL_PORT:
				setWsdlPort(WSDL_PORT_EDEFAULT);
				return;
			case DeploymentPackage.WEBSERVICE__WSDL_SERVICE:
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
			case DeploymentPackage.WEBSERVICE__PARAMETERS:
				return parameters != null;
			case DeploymentPackage.WEBSERVICE__ENABLE_GENERATION:
				return isSetEnableGeneration();
			case DeploymentPackage.WEBSERVICE__IMPLEMENTATION:
				return IMPLEMENTATION_EDEFAULT == null ? implementation != null : !IMPLEMENTATION_EDEFAULT.equals(implementation);
			case DeploymentPackage.WEBSERVICE__IMPL_TYPE:
				return isSetImplType();
			case DeploymentPackage.WEBSERVICE__PROTOCOL:
				return PROTOCOL_EDEFAULT == null ? protocol != null : !PROTOCOL_EDEFAULT.equals(protocol);
			case DeploymentPackage.WEBSERVICE__SOAP_VERSION:
				return isSetSoapVersion();
			case DeploymentPackage.WEBSERVICE__STYLE:
				return isSetStyle();
			case DeploymentPackage.WEBSERVICE__TRANSACTION:
				return TRANSACTION_EDEFAULT == null ? transaction != null : !TRANSACTION_EDEFAULT.equals(transaction);
			case DeploymentPackage.WEBSERVICE__URI:
				return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
			case DeploymentPackage.WEBSERVICE__USE_EXISTING_WSDL:
				return isSetUseExistingWSDL();
			case DeploymentPackage.WEBSERVICE__USER_ID:
				return USER_ID_EDEFAULT == null ? userID != null : !USER_ID_EDEFAULT.equals(userID);
			case DeploymentPackage.WEBSERVICE__WSDL_LOCATION:
				return WSDL_LOCATION_EDEFAULT == null ? wsdlLocation != null : !WSDL_LOCATION_EDEFAULT.equals(wsdlLocation);
			case DeploymentPackage.WEBSERVICE__WSDL_PORT:
				return WSDL_PORT_EDEFAULT == null ? wsdlPort != null : !WSDL_PORT_EDEFAULT.equals(wsdlPort);
			case DeploymentPackage.WEBSERVICE__WSDL_SERVICE:
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
		result.append(", implementation: ");
		result.append(implementation);
		result.append(", implType: ");
		if (implTypeESet) result.append(implType); else result.append("<unset>");
		result.append(", protocol: ");
		result.append(protocol);
		result.append(", soapVersion: ");
		if (soapVersionESet) result.append(soapVersion); else result.append("<unset>");
		result.append(", style: ");
		if (styleESet) result.append(style); else result.append("<unset>");
		result.append(", transaction: ");
		result.append(transaction);
		result.append(", uri: ");
		result.append(uri);
		result.append(", useExistingWSDL: ");
		if (useExistingWSDLESet) result.append(useExistingWSDL); else result.append("<unset>");
		result.append(", userID: ");
		result.append(userID);
		result.append(", wsdlLocation: ");
		result.append(wsdlLocation);
		result.append(", wsdlPort: ");
		result.append(wsdlPort);
		result.append(", wsdlService: ");
		result.append(wsdlService);
		result.append(')');
		return result.toString();
	}

} //WebserviceImpl
