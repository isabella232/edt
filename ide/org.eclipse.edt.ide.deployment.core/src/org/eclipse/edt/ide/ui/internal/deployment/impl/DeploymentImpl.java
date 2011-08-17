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

import java.util.Collection;

import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.DeployExt;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget;
import org.eclipse.edt.ide.ui.internal.deployment.Include;
import org.eclipse.edt.ide.ui.internal.deployment.Protocols;
import org.eclipse.edt.ide.ui.internal.deployment.RUIApplication;
import org.eclipse.edt.ide.ui.internal.deployment.ResourceOmissions;
import org.eclipse.edt.ide.ui.internal.deployment.Restservices;
import org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType;
import org.eclipse.edt.ide.ui.internal.deployment.Webservices;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Deployment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getBindings <em>Bindings</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getProtocols <em>Protocols</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getWebservices <em>Webservices</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getRestservices <em>Restservices</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getRuiapplication <em>Ruiapplication</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getResourceOmissions <em>Resource Omissions</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getTargetGroup <em>Target Group</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getInclude <em>Include</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getDeployExtGroup <em>Deploy Ext Group</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getDeployExt <em>Deploy Ext</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getWebserviceRuntime <em>Webservice Runtime</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.DeploymentImpl#getAlias <em>Alias</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DeploymentImpl extends EObjectImpl implements Deployment {
	/**
	 * The cached value of the '{@link #getBindings() <em>Bindings</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBindings()
	 * @generated
	 * @ordered
	 */
	protected Bindings bindings;

	/**
	 * The cached value of the '{@link #getProtocols() <em>Protocols</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProtocols()
	 * @generated
	 * @ordered
	 */
	protected Protocols protocols;

	/**
	 * The cached value of the '{@link #getWebservices() <em>Webservices</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWebservices()
	 * @generated
	 * @ordered
	 */
	protected Webservices webservices;

	/**
	 * The cached value of the '{@link #getRestservices() <em>Restservices</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRestservices()
	 * @generated
	 * @ordered
	 */
	protected Restservices restservices;

	/**
	 * The cached value of the '{@link #getRuiapplication() <em>Ruiapplication</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRuiapplication()
	 * @generated
	 * @ordered
	 */
	protected RUIApplication ruiapplication;

	/**
	 * The cached value of the '{@link #getResourceOmissions() <em>Resource Omissions</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResourceOmissions()
	 * @generated
	 * @ordered
	 */
	protected ResourceOmissions resourceOmissions;

	/**
	 * The cached value of the '{@link #getTargetGroup() <em>Target Group</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetGroup()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap targetGroup;

	/**
	 * The cached value of the '{@link #getInclude() <em>Include</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInclude()
	 * @generated
	 * @ordered
	 */
	protected EList<Include> include;

	/**
	 * The cached value of the '{@link #getDeployExtGroup() <em>Deploy Ext Group</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeployExtGroup()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap deployExtGroup;

	/**
	 * The default value of the '{@link #getWebserviceRuntime() <em>Webservice Runtime</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWebserviceRuntime()
	 * @generated
	 * @ordered
	 */
	protected static final WebserviceRuntimeType WEBSERVICE_RUNTIME_EDEFAULT = WebserviceRuntimeType.JAXRPC;

	/**
	 * The cached value of the '{@link #getWebserviceRuntime() <em>Webservice Runtime</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWebserviceRuntime()
	 * @generated
	 * @ordered
	 */
	protected WebserviceRuntimeType webserviceRuntime = WEBSERVICE_RUNTIME_EDEFAULT;

	/**
	 * This is true if the Webservice Runtime attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean webserviceRuntimeESet;

	/**
	 * The default value of the '{@link #getAlias() <em>Alias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAlias()
	 * @generated
	 * @ordered
	 */
	protected static final String ALIAS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAlias() <em>Alias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAlias()
	 * @generated
	 * @ordered
	 */
	protected String alias = ALIAS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DeploymentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.DEPLOYMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bindings getBindings() {
		return bindings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetBindings(Bindings newBindings, NotificationChain msgs) {
		Bindings oldBindings = bindings;
		bindings = newBindings;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__BINDINGS, oldBindings, newBindings);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBindings(Bindings newBindings) {
		if (newBindings != bindings) {
			NotificationChain msgs = null;
			if (bindings != null)
				msgs = ((InternalEObject)bindings).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.DEPLOYMENT__BINDINGS, null, msgs);
			if (newBindings != null)
				msgs = ((InternalEObject)newBindings).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.DEPLOYMENT__BINDINGS, null, msgs);
			msgs = basicSetBindings(newBindings, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__BINDINGS, newBindings, newBindings));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Protocols getProtocols() {
		return protocols;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProtocols(Protocols newProtocols, NotificationChain msgs) {
		Protocols oldProtocols = protocols;
		protocols = newProtocols;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__PROTOCOLS, oldProtocols, newProtocols);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProtocols(Protocols newProtocols) {
		if (newProtocols != protocols) {
			NotificationChain msgs = null;
			if (protocols != null)
				msgs = ((InternalEObject)protocols).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.DEPLOYMENT__PROTOCOLS, null, msgs);
			if (newProtocols != null)
				msgs = ((InternalEObject)newProtocols).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.DEPLOYMENT__PROTOCOLS, null, msgs);
			msgs = basicSetProtocols(newProtocols, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__PROTOCOLS, newProtocols, newProtocols));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Webservices getWebservices() {
		return webservices;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetWebservices(Webservices newWebservices, NotificationChain msgs) {
		Webservices oldWebservices = webservices;
		webservices = newWebservices;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__WEBSERVICES, oldWebservices, newWebservices);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWebservices(Webservices newWebservices) {
		if (newWebservices != webservices) {
			NotificationChain msgs = null;
			if (webservices != null)
				msgs = ((InternalEObject)webservices).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.DEPLOYMENT__WEBSERVICES, null, msgs);
			if (newWebservices != null)
				msgs = ((InternalEObject)newWebservices).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.DEPLOYMENT__WEBSERVICES, null, msgs);
			msgs = basicSetWebservices(newWebservices, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__WEBSERVICES, newWebservices, newWebservices));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Restservices getRestservices() {
		return restservices;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRestservices(Restservices newRestservices, NotificationChain msgs) {
		Restservices oldRestservices = restservices;
		restservices = newRestservices;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__RESTSERVICES, oldRestservices, newRestservices);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRestservices(Restservices newRestservices) {
		if (newRestservices != restservices) {
			NotificationChain msgs = null;
			if (restservices != null)
				msgs = ((InternalEObject)restservices).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.DEPLOYMENT__RESTSERVICES, null, msgs);
			if (newRestservices != null)
				msgs = ((InternalEObject)newRestservices).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.DEPLOYMENT__RESTSERVICES, null, msgs);
			msgs = basicSetRestservices(newRestservices, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__RESTSERVICES, newRestservices, newRestservices));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RUIApplication getRuiapplication() {
		return ruiapplication;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRuiapplication(RUIApplication newRuiapplication, NotificationChain msgs) {
		RUIApplication oldRuiapplication = ruiapplication;
		ruiapplication = newRuiapplication;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__RUIAPPLICATION, oldRuiapplication, newRuiapplication);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRuiapplication(RUIApplication newRuiapplication) {
		if (newRuiapplication != ruiapplication) {
			NotificationChain msgs = null;
			if (ruiapplication != null)
				msgs = ((InternalEObject)ruiapplication).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.DEPLOYMENT__RUIAPPLICATION, null, msgs);
			if (newRuiapplication != null)
				msgs = ((InternalEObject)newRuiapplication).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.DEPLOYMENT__RUIAPPLICATION, null, msgs);
			msgs = basicSetRuiapplication(newRuiapplication, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__RUIAPPLICATION, newRuiapplication, newRuiapplication));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ResourceOmissions getResourceOmissions() {
		return resourceOmissions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetResourceOmissions(ResourceOmissions newResourceOmissions, NotificationChain msgs) {
		ResourceOmissions oldResourceOmissions = resourceOmissions;
		resourceOmissions = newResourceOmissions;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__RESOURCE_OMISSIONS, oldResourceOmissions, newResourceOmissions);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setResourceOmissions(ResourceOmissions newResourceOmissions) {
		if (newResourceOmissions != resourceOmissions) {
			NotificationChain msgs = null;
			if (resourceOmissions != null)
				msgs = ((InternalEObject)resourceOmissions).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.DEPLOYMENT__RESOURCE_OMISSIONS, null, msgs);
			if (newResourceOmissions != null)
				msgs = ((InternalEObject)newResourceOmissions).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.DEPLOYMENT__RESOURCE_OMISSIONS, null, msgs);
			msgs = basicSetResourceOmissions(newResourceOmissions, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__RESOURCE_OMISSIONS, newResourceOmissions, newResourceOmissions));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getTargetGroup() {
		if (targetGroup == null) {
			targetGroup = new BasicFeatureMap(this, DeploymentPackage.DEPLOYMENT__TARGET_GROUP);
		}
		return targetGroup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentTarget getTarget() {
		return (DeploymentTarget)getTargetGroup().get(DeploymentPackage.Literals.DEPLOYMENT__TARGET, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTarget(DeploymentTarget newTarget, NotificationChain msgs) {
		return ((FeatureMap.Internal)getTargetGroup()).basicAdd(DeploymentPackage.Literals.DEPLOYMENT__TARGET, newTarget, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Include> getInclude() {
		if (include == null) {
			include = new EObjectContainmentEList<Include>(Include.class, this, DeploymentPackage.DEPLOYMENT__INCLUDE);
		}
		return include;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getDeployExtGroup() {
		if (deployExtGroup == null) {
			deployExtGroup = new BasicFeatureMap(this, DeploymentPackage.DEPLOYMENT__DEPLOY_EXT_GROUP);
		}
		return deployExtGroup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<DeployExt> getDeployExt() {
		return getDeployExtGroup().list(DeploymentPackage.Literals.DEPLOYMENT__DEPLOY_EXT);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WebserviceRuntimeType getWebserviceRuntime() {
		return webserviceRuntime;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWebserviceRuntime(WebserviceRuntimeType newWebserviceRuntime) {
		WebserviceRuntimeType oldWebserviceRuntime = webserviceRuntime;
		webserviceRuntime = newWebserviceRuntime == null ? WEBSERVICE_RUNTIME_EDEFAULT : newWebserviceRuntime;
		boolean oldWebserviceRuntimeESet = webserviceRuntimeESet;
		webserviceRuntimeESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__WEBSERVICE_RUNTIME, oldWebserviceRuntime, webserviceRuntime, !oldWebserviceRuntimeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetWebserviceRuntime() {
		WebserviceRuntimeType oldWebserviceRuntime = webserviceRuntime;
		boolean oldWebserviceRuntimeESet = webserviceRuntimeESet;
		webserviceRuntime = WEBSERVICE_RUNTIME_EDEFAULT;
		webserviceRuntimeESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.DEPLOYMENT__WEBSERVICE_RUNTIME, oldWebserviceRuntime, WEBSERVICE_RUNTIME_EDEFAULT, oldWebserviceRuntimeESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetWebserviceRuntime() {
		return webserviceRuntimeESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAlias(String newAlias) {
		String oldAlias = alias;
		alias = newAlias;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.DEPLOYMENT__ALIAS, oldAlias, alias));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DeploymentPackage.DEPLOYMENT__BINDINGS:
				return basicSetBindings(null, msgs);
			case DeploymentPackage.DEPLOYMENT__PROTOCOLS:
				return basicSetProtocols(null, msgs);
			case DeploymentPackage.DEPLOYMENT__WEBSERVICES:
				return basicSetWebservices(null, msgs);
			case DeploymentPackage.DEPLOYMENT__RESTSERVICES:
				return basicSetRestservices(null, msgs);
			case DeploymentPackage.DEPLOYMENT__RUIAPPLICATION:
				return basicSetRuiapplication(null, msgs);
			case DeploymentPackage.DEPLOYMENT__RESOURCE_OMISSIONS:
				return basicSetResourceOmissions(null, msgs);
			case DeploymentPackage.DEPLOYMENT__TARGET_GROUP:
				return ((InternalEList<?>)getTargetGroup()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.DEPLOYMENT__TARGET:
				return basicSetTarget(null, msgs);
			case DeploymentPackage.DEPLOYMENT__INCLUDE:
				return ((InternalEList<?>)getInclude()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.DEPLOYMENT__DEPLOY_EXT_GROUP:
				return ((InternalEList<?>)getDeployExtGroup()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.DEPLOYMENT__DEPLOY_EXT:
				return ((InternalEList<?>)getDeployExt()).basicRemove(otherEnd, msgs);
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
			case DeploymentPackage.DEPLOYMENT__BINDINGS:
				return getBindings();
			case DeploymentPackage.DEPLOYMENT__PROTOCOLS:
				return getProtocols();
			case DeploymentPackage.DEPLOYMENT__WEBSERVICES:
				return getWebservices();
			case DeploymentPackage.DEPLOYMENT__RESTSERVICES:
				return getRestservices();
			case DeploymentPackage.DEPLOYMENT__RUIAPPLICATION:
				return getRuiapplication();
			case DeploymentPackage.DEPLOYMENT__RESOURCE_OMISSIONS:
				return getResourceOmissions();
			case DeploymentPackage.DEPLOYMENT__TARGET_GROUP:
				if (coreType) return getTargetGroup();
				return ((FeatureMap.Internal)getTargetGroup()).getWrapper();
			case DeploymentPackage.DEPLOYMENT__TARGET:
				return getTarget();
			case DeploymentPackage.DEPLOYMENT__INCLUDE:
				return getInclude();
			case DeploymentPackage.DEPLOYMENT__DEPLOY_EXT_GROUP:
				if (coreType) return getDeployExtGroup();
				return ((FeatureMap.Internal)getDeployExtGroup()).getWrapper();
			case DeploymentPackage.DEPLOYMENT__DEPLOY_EXT:
				return getDeployExt();
			case DeploymentPackage.DEPLOYMENT__WEBSERVICE_RUNTIME:
				return getWebserviceRuntime();
			case DeploymentPackage.DEPLOYMENT__ALIAS:
				return getAlias();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DeploymentPackage.DEPLOYMENT__BINDINGS:
				setBindings((Bindings)newValue);
				return;
			case DeploymentPackage.DEPLOYMENT__PROTOCOLS:
				setProtocols((Protocols)newValue);
				return;
			case DeploymentPackage.DEPLOYMENT__WEBSERVICES:
				setWebservices((Webservices)newValue);
				return;
			case DeploymentPackage.DEPLOYMENT__RESTSERVICES:
				setRestservices((Restservices)newValue);
				return;
			case DeploymentPackage.DEPLOYMENT__RUIAPPLICATION:
				setRuiapplication((RUIApplication)newValue);
				return;
			case DeploymentPackage.DEPLOYMENT__RESOURCE_OMISSIONS:
				setResourceOmissions((ResourceOmissions)newValue);
				return;
			case DeploymentPackage.DEPLOYMENT__TARGET_GROUP:
				((FeatureMap.Internal)getTargetGroup()).set(newValue);
				return;
			case DeploymentPackage.DEPLOYMENT__INCLUDE:
				getInclude().clear();
				getInclude().addAll((Collection<? extends Include>)newValue);
				return;
			case DeploymentPackage.DEPLOYMENT__DEPLOY_EXT_GROUP:
				((FeatureMap.Internal)getDeployExtGroup()).set(newValue);
				return;
			case DeploymentPackage.DEPLOYMENT__WEBSERVICE_RUNTIME:
				setWebserviceRuntime((WebserviceRuntimeType)newValue);
				return;
			case DeploymentPackage.DEPLOYMENT__ALIAS:
				setAlias((String)newValue);
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
			case DeploymentPackage.DEPLOYMENT__BINDINGS:
				setBindings((Bindings)null);
				return;
			case DeploymentPackage.DEPLOYMENT__PROTOCOLS:
				setProtocols((Protocols)null);
				return;
			case DeploymentPackage.DEPLOYMENT__WEBSERVICES:
				setWebservices((Webservices)null);
				return;
			case DeploymentPackage.DEPLOYMENT__RESTSERVICES:
				setRestservices((Restservices)null);
				return;
			case DeploymentPackage.DEPLOYMENT__RUIAPPLICATION:
				setRuiapplication((RUIApplication)null);
				return;
			case DeploymentPackage.DEPLOYMENT__RESOURCE_OMISSIONS:
				setResourceOmissions((ResourceOmissions)null);
				return;
			case DeploymentPackage.DEPLOYMENT__TARGET_GROUP:
				getTargetGroup().clear();
				return;
			case DeploymentPackage.DEPLOYMENT__INCLUDE:
				getInclude().clear();
				return;
			case DeploymentPackage.DEPLOYMENT__DEPLOY_EXT_GROUP:
				getDeployExtGroup().clear();
				return;
			case DeploymentPackage.DEPLOYMENT__WEBSERVICE_RUNTIME:
				unsetWebserviceRuntime();
				return;
			case DeploymentPackage.DEPLOYMENT__ALIAS:
				setAlias(ALIAS_EDEFAULT);
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
			case DeploymentPackage.DEPLOYMENT__BINDINGS:
				return bindings != null;
			case DeploymentPackage.DEPLOYMENT__PROTOCOLS:
				return protocols != null;
			case DeploymentPackage.DEPLOYMENT__WEBSERVICES:
				return webservices != null;
			case DeploymentPackage.DEPLOYMENT__RESTSERVICES:
				return restservices != null;
			case DeploymentPackage.DEPLOYMENT__RUIAPPLICATION:
				return ruiapplication != null;
			case DeploymentPackage.DEPLOYMENT__RESOURCE_OMISSIONS:
				return resourceOmissions != null;
			case DeploymentPackage.DEPLOYMENT__TARGET_GROUP:
				return targetGroup != null && !targetGroup.isEmpty();
			case DeploymentPackage.DEPLOYMENT__TARGET:
				return getTarget() != null;
			case DeploymentPackage.DEPLOYMENT__INCLUDE:
				return include != null && !include.isEmpty();
			case DeploymentPackage.DEPLOYMENT__DEPLOY_EXT_GROUP:
				return deployExtGroup != null && !deployExtGroup.isEmpty();
			case DeploymentPackage.DEPLOYMENT__DEPLOY_EXT:
				return !getDeployExt().isEmpty();
			case DeploymentPackage.DEPLOYMENT__WEBSERVICE_RUNTIME:
				return isSetWebserviceRuntime();
			case DeploymentPackage.DEPLOYMENT__ALIAS:
				return ALIAS_EDEFAULT == null ? alias != null : !ALIAS_EDEFAULT.equals(alias);
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
		result.append(" (targetGroup: ");
		result.append(targetGroup);
		result.append(", deployExtGroup: ");
		result.append(deployExtGroup);
		result.append(", webserviceRuntime: ");
		if (webserviceRuntimeESet) result.append(webserviceRuntime); else result.append("<unset>");
		result.append(", alias: ");
		result.append(alias);
		result.append(')');
		return result.toString();
	}

} //DeploymentImpl
