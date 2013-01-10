/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.ide.ui.internal.deployment.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class DeploymentFactoryImpl extends EFactoryImpl implements DeploymentFactory
{
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static DeploymentFactory init()
	{
		try
		{
			DeploymentFactory theDeploymentFactory = (DeploymentFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/xmlns/edt/deployment/1.0"); 
			if (theDeploymentFactory != null)
			{
				return theDeploymentFactory;
			}
		}
		catch (Exception exception)
		{
			EcorePlugin.INSTANCE.log(exception);
		}
		return new DeploymentFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentFactoryImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass)
	{
		switch (eClass.getClassifierID())
		{
			case DeploymentPackage.BINDING: return createBinding();
			case DeploymentPackage.BINDINGS: return createBindings();
			case DeploymentPackage.DEPLOY_EXT: return createDeployExt();
			case DeploymentPackage.DEPLOYMENT: return createDeployment();
			case DeploymentPackage.DEPLOYMENT_PROJECT: return createDeploymentProject();
			case DeploymentPackage.DEPLOYMENT_TARGET: return createDeploymentTarget();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT: return createEGLDeploymentRoot();
			case DeploymentPackage.INCLUDE: return createInclude();
			case DeploymentPackage.PARAMETER: return createParameter();
			case DeploymentPackage.PARAMETERS: return createParameters();
			case DeploymentPackage.RESOURCE: return createResource();
			case DeploymentPackage.RESOURCE_OMISSIONS: return createResourceOmissions();
			case DeploymentPackage.RUI_APPLICATION: return createRUIApplication();
			case DeploymentPackage.RUI_HANDLER: return createRUIHandler();
			case DeploymentPackage.SERVICE: return createService();
			case DeploymentPackage.SERVICES: return createServices();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Binding createBinding()
	{
		BindingImpl binding = new BindingImpl();
		return binding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bindings createBindings()
	{
		BindingsImpl bindings = new BindingsImpl();
		return bindings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeployExt createDeployExt()
	{
		DeployExtImpl deployExt = new DeployExtImpl();
		return deployExt;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Deployment createDeployment()
	{
		DeploymentImpl deployment = new DeploymentImpl();
		return deployment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentProject createDeploymentProject()
	{
		DeploymentProjectImpl deploymentProject = new DeploymentProjectImpl();
		return deploymentProject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentTarget createDeploymentTarget()
	{
		DeploymentTargetImpl deploymentTarget = new DeploymentTargetImpl();
		return deploymentTarget;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGLDeploymentRoot createEGLDeploymentRoot()
	{
		EGLDeploymentRootImpl eglDeploymentRoot = new EGLDeploymentRootImpl();
		return eglDeploymentRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Include createInclude()
	{
		IncludeImpl include = new IncludeImpl();
		return include;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter createParameter()
	{
		ParameterImpl parameter = new ParameterImpl();
		return parameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameters createParameters()
	{
		ParametersImpl parameters = new ParametersImpl();
		return parameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Resource createResource()
	{
		ResourceImpl resource = new ResourceImpl();
		return resource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ResourceOmissions createResourceOmissions()
	{
		ResourceOmissionsImpl resourceOmissions = new ResourceOmissionsImpl();
		return resourceOmissions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RUIApplication createRUIApplication()
	{
		RUIApplicationImpl ruiApplication = new RUIApplicationImpl();
		return ruiApplication;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RUIHandler createRUIHandler()
	{
		RUIHandlerImpl ruiHandler = new RUIHandlerImpl();
		return ruiHandler;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Service createService()
	{
		ServiceImpl service = new ServiceImpl();
		return service;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Services createServices()
	{
		ServicesImpl services = new ServicesImpl();
		return services;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentPackage getDeploymentPackage()
	{
		return (DeploymentPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static DeploymentPackage getPackage()
	{
		return DeploymentPackage.eINSTANCE;
	}

} //DeploymentFactoryImpl
