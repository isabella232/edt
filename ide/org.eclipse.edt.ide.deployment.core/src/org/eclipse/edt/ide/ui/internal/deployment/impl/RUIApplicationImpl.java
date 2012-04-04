/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.ide.ui.internal.deployment.RUIApplication;
import org.eclipse.edt.ide.ui.internal.deployment.RUIHandler;

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
 * An implementation of the model object '<em><b>RUI Application</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIApplicationImpl#getRuihandler <em>Ruihandler</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIApplicationImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIApplicationImpl#isDeployAllHandlers <em>Deploy All Handlers</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.RUIApplicationImpl#isSupportDynamicLoading <em>Support Dynamic Loading</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RUIApplicationImpl extends EObjectImpl implements RUIApplication
{
	/**
	 * The cached value of the '{@link #getRuihandler() <em>Ruihandler</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRuihandler()
	 * @generated
	 * @ordered
	 */
	protected EList<RUIHandler> ruihandler;

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
	 * The default value of the '{@link #isDeployAllHandlers() <em>Deploy All Handlers</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDeployAllHandlers()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DEPLOY_ALL_HANDLERS_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isDeployAllHandlers() <em>Deploy All Handlers</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDeployAllHandlers()
	 * @generated
	 * @ordered
	 */
	protected boolean deployAllHandlers = DEPLOY_ALL_HANDLERS_EDEFAULT;

	/**
	 * This is true if the Deploy All Handlers attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean deployAllHandlersESet;

	/**
	 * The default value of the '{@link #isSupportDynamicLoading() <em>Support Dynamic Loading</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSupportDynamicLoading()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SUPPORT_DYNAMIC_LOADING_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isSupportDynamicLoading() <em>Support Dynamic Loading</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSupportDynamicLoading()
	 * @generated
	 * @ordered
	 */
	protected boolean supportDynamicLoading = SUPPORT_DYNAMIC_LOADING_EDEFAULT;

	/**
	 * This is true if the Support Dynamic Loading attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean supportDynamicLoadingESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RUIApplicationImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass()
	{
		return DeploymentPackage.Literals.RUI_APPLICATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<RUIHandler> getRuihandler()
	{
		if (ruihandler == null)
		{
			ruihandler = new EObjectContainmentEList<RUIHandler>(RUIHandler.class, this, DeploymentPackage.RUI_APPLICATION__RUIHANDLER);
		}
		return ruihandler;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameters getParameters()
	{
		return parameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParameters(Parameters newParameters, NotificationChain msgs)
	{
		Parameters oldParameters = parameters;
		parameters = newParameters;
		if (eNotificationRequired())
		{
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DeploymentPackage.RUI_APPLICATION__PARAMETERS, oldParameters, newParameters);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParameters(Parameters newParameters)
	{
		if (newParameters != parameters)
		{
			NotificationChain msgs = null;
			if (parameters != null)
				msgs = ((InternalEObject)parameters).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.RUI_APPLICATION__PARAMETERS, null, msgs);
			if (newParameters != null)
				msgs = ((InternalEObject)newParameters).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DeploymentPackage.RUI_APPLICATION__PARAMETERS, null, msgs);
			msgs = basicSetParameters(newParameters, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.RUI_APPLICATION__PARAMETERS, newParameters, newParameters));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isDeployAllHandlers()
	{
		return deployAllHandlers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDeployAllHandlers(boolean newDeployAllHandlers)
	{
		boolean oldDeployAllHandlers = deployAllHandlers;
		deployAllHandlers = newDeployAllHandlers;
		boolean oldDeployAllHandlersESet = deployAllHandlersESet;
		deployAllHandlersESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.RUI_APPLICATION__DEPLOY_ALL_HANDLERS, oldDeployAllHandlers, deployAllHandlers, !oldDeployAllHandlersESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetDeployAllHandlers()
	{
		boolean oldDeployAllHandlers = deployAllHandlers;
		boolean oldDeployAllHandlersESet = deployAllHandlersESet;
		deployAllHandlers = DEPLOY_ALL_HANDLERS_EDEFAULT;
		deployAllHandlersESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.RUI_APPLICATION__DEPLOY_ALL_HANDLERS, oldDeployAllHandlers, DEPLOY_ALL_HANDLERS_EDEFAULT, oldDeployAllHandlersESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetDeployAllHandlers()
	{
		return deployAllHandlersESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSupportDynamicLoading()
	{
		return supportDynamicLoading;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSupportDynamicLoading(boolean newSupportDynamicLoading)
	{
		boolean oldSupportDynamicLoading = supportDynamicLoading;
		supportDynamicLoading = newSupportDynamicLoading;
		boolean oldSupportDynamicLoadingESet = supportDynamicLoadingESet;
		supportDynamicLoadingESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.RUI_APPLICATION__SUPPORT_DYNAMIC_LOADING, oldSupportDynamicLoading, supportDynamicLoading, !oldSupportDynamicLoadingESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetSupportDynamicLoading()
	{
		boolean oldSupportDynamicLoading = supportDynamicLoading;
		boolean oldSupportDynamicLoadingESet = supportDynamicLoadingESet;
		supportDynamicLoading = SUPPORT_DYNAMIC_LOADING_EDEFAULT;
		supportDynamicLoadingESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DeploymentPackage.RUI_APPLICATION__SUPPORT_DYNAMIC_LOADING, oldSupportDynamicLoading, SUPPORT_DYNAMIC_LOADING_EDEFAULT, oldSupportDynamicLoadingESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetSupportDynamicLoading()
	{
		return supportDynamicLoadingESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
	{
		switch (featureID)
		{
			case DeploymentPackage.RUI_APPLICATION__RUIHANDLER:
				return ((InternalEList<?>)getRuihandler()).basicRemove(otherEnd, msgs);
			case DeploymentPackage.RUI_APPLICATION__PARAMETERS:
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
	public Object eGet(int featureID, boolean resolve, boolean coreType)
	{
		switch (featureID)
		{
			case DeploymentPackage.RUI_APPLICATION__RUIHANDLER:
				return getRuihandler();
			case DeploymentPackage.RUI_APPLICATION__PARAMETERS:
				return getParameters();
			case DeploymentPackage.RUI_APPLICATION__DEPLOY_ALL_HANDLERS:
				return isDeployAllHandlers();
			case DeploymentPackage.RUI_APPLICATION__SUPPORT_DYNAMIC_LOADING:
				return isSupportDynamicLoading();
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
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
			case DeploymentPackage.RUI_APPLICATION__RUIHANDLER:
				getRuihandler().clear();
				getRuihandler().addAll((Collection<? extends RUIHandler>)newValue);
				return;
			case DeploymentPackage.RUI_APPLICATION__PARAMETERS:
				setParameters((Parameters)newValue);
				return;
			case DeploymentPackage.RUI_APPLICATION__DEPLOY_ALL_HANDLERS:
				setDeployAllHandlers((Boolean)newValue);
				return;
			case DeploymentPackage.RUI_APPLICATION__SUPPORT_DYNAMIC_LOADING:
				setSupportDynamicLoading((Boolean)newValue);
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
	public void eUnset(int featureID)
	{
		switch (featureID)
		{
			case DeploymentPackage.RUI_APPLICATION__RUIHANDLER:
				getRuihandler().clear();
				return;
			case DeploymentPackage.RUI_APPLICATION__PARAMETERS:
				setParameters((Parameters)null);
				return;
			case DeploymentPackage.RUI_APPLICATION__DEPLOY_ALL_HANDLERS:
				unsetDeployAllHandlers();
				return;
			case DeploymentPackage.RUI_APPLICATION__SUPPORT_DYNAMIC_LOADING:
				unsetSupportDynamicLoading();
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
	public boolean eIsSet(int featureID)
	{
		switch (featureID)
		{
			case DeploymentPackage.RUI_APPLICATION__RUIHANDLER:
				return ruihandler != null && !ruihandler.isEmpty();
			case DeploymentPackage.RUI_APPLICATION__PARAMETERS:
				return parameters != null;
			case DeploymentPackage.RUI_APPLICATION__DEPLOY_ALL_HANDLERS:
				return isSetDeployAllHandlers();
			case DeploymentPackage.RUI_APPLICATION__SUPPORT_DYNAMIC_LOADING:
				return isSetSupportDynamicLoading();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString()
	{
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (deployAllHandlers: ");
		if (deployAllHandlersESet) result.append(deployAllHandlers); else result.append("<unset>");
		result.append(", supportDynamicLoading: ");
		if (supportDynamicLoadingESet) result.append(supportDynamicLoading); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //RUIApplicationImpl
