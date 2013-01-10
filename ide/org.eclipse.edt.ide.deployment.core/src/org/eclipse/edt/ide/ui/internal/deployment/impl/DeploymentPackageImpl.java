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

import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.DeployExt;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentProject;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Include;
import org.eclipse.edt.ide.ui.internal.deployment.Parameter;
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.ide.ui.internal.deployment.RUIApplication;
import org.eclipse.edt.ide.ui.internal.deployment.RUIHandler;
import org.eclipse.edt.ide.ui.internal.deployment.Resource;
import org.eclipse.edt.ide.ui.internal.deployment.ResourceOmissions;
import org.eclipse.edt.ide.ui.internal.deployment.Service;
import org.eclipse.edt.ide.ui.internal.deployment.Services;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class DeploymentPackageImpl extends EPackageImpl implements DeploymentPackage
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bindingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass bindingsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deployExtEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deploymentEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deploymentProjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deploymentTargetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass eglDeploymentRootEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass includeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parameterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parametersEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass resourceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass resourceOmissionsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ruiApplicationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ruiHandlerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass serviceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass servicesEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private DeploymentPackageImpl()
	{
		super(eNS_URI, DeploymentFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link DeploymentPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static DeploymentPackage init()
	{
		if (isInited) return (DeploymentPackage)EPackage.Registry.INSTANCE.getEPackage(DeploymentPackage.eNS_URI);

		// Obtain or create and register package
		DeploymentPackageImpl theDeploymentPackage = (DeploymentPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof DeploymentPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new DeploymentPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		XMLTypePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theDeploymentPackage.createPackageContents();

		// Initialize created meta-data
		theDeploymentPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theDeploymentPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(DeploymentPackage.eNS_URI, theDeploymentPackage);
		return theDeploymentPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBinding()
	{
		return bindingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getBinding_Parameters()
	{
		return (EReference)bindingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getBinding_Name()
	{
		return (EAttribute)bindingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getBinding_Type()
	{
		return (EAttribute)bindingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getBinding_Uri()
	{
		return (EAttribute)bindingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getBinding_UseURI()
	{
		return (EAttribute)bindingEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getBindings()
	{
		return bindingsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getBindings_Binding()
	{
		return (EReference)bindingsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeployExt()
	{
		return deployExtEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeployment()
	{
		return deploymentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_Bindings()
	{
		return (EReference)deploymentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_Services()
	{
		return (EReference)deploymentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_Ruiapplication()
	{
		return (EReference)deploymentEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_ResourceOmissions()
	{
		return (EReference)deploymentEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeployment_TargetGroup()
	{
		return (EAttribute)deploymentEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_Target()
	{
		return (EReference)deploymentEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_Include()
	{
		return (EReference)deploymentEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeployment_DeployExtGroup()
	{
		return (EAttribute)deploymentEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_DeployExt()
	{
		return (EReference)deploymentEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeploymentProject()
	{
		return deploymentProjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeploymentTarget()
	{
		return deploymentTargetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeploymentTarget_Parameters()
	{
		return (EReference)deploymentTargetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeploymentTarget_Name()
	{
		return (EAttribute)deploymentTargetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGLDeploymentRoot()
	{
		return eglDeploymentRootEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGLDeploymentRoot_Mixed()
	{
		return (EAttribute)eglDeploymentRootEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_XMLNSPrefixMap()
	{
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_XSISchemaLocation()
	{
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_DeployExt()
	{
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_Deployment()
	{
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_Target()
	{
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_TargetProject()
	{
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInclude()
	{
		return includeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getInclude_Location()
	{
		return (EAttribute)includeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getParameter()
	{
		return parameterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Name()
	{
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Type()
	{
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Value()
	{
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getParameters()
	{
		return parametersEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getParameters_Parameter()
	{
		return (EReference)parametersEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getResource()
	{
		return resourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getResource_Id()
	{
		return (EAttribute)resourceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getResourceOmissions()
	{
		return resourceOmissionsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getResourceOmissions_Resource()
	{
		return (EReference)resourceOmissionsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRUIApplication()
	{
		return ruiApplicationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRUIApplication_Ruihandler()
	{
		return (EReference)ruiApplicationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRUIApplication_Parameters()
	{
		return (EReference)ruiApplicationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRUIApplication_DeployAllHandlers()
	{
		return (EAttribute)ruiApplicationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRUIApplication_SupportDynamicLoading()
	{
		return (EAttribute)ruiApplicationEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRUIHandler()
	{
		return ruiHandlerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRUIHandler_Parameters()
	{
		return (EReference)ruiHandlerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRUIHandler_EnableGeneration()
	{
		return (EAttribute)ruiHandlerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRUIHandler_Implementation()
	{
		return (EAttribute)ruiHandlerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getService()
	{
		return serviceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getService_Parameters()
	{
		return (EReference)serviceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getService_Implementation()
	{
		return (EAttribute)serviceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getService_Type()
	{
		return (EAttribute)serviceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getServices()
	{
		return servicesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getServices_Service()
	{
		return (EReference)servicesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentFactory getDeploymentFactory()
	{
		return (DeploymentFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents()
	{
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		bindingEClass = createEClass(BINDING);
		createEReference(bindingEClass, BINDING__PARAMETERS);
		createEAttribute(bindingEClass, BINDING__NAME);
		createEAttribute(bindingEClass, BINDING__TYPE);
		createEAttribute(bindingEClass, BINDING__URI);
		createEAttribute(bindingEClass, BINDING__USE_URI);

		bindingsEClass = createEClass(BINDINGS);
		createEReference(bindingsEClass, BINDINGS__BINDING);

		deployExtEClass = createEClass(DEPLOY_EXT);

		deploymentEClass = createEClass(DEPLOYMENT);
		createEReference(deploymentEClass, DEPLOYMENT__BINDINGS);
		createEReference(deploymentEClass, DEPLOYMENT__SERVICES);
		createEReference(deploymentEClass, DEPLOYMENT__RUIAPPLICATION);
		createEReference(deploymentEClass, DEPLOYMENT__RESOURCE_OMISSIONS);
		createEAttribute(deploymentEClass, DEPLOYMENT__TARGET_GROUP);
		createEReference(deploymentEClass, DEPLOYMENT__TARGET);
		createEReference(deploymentEClass, DEPLOYMENT__INCLUDE);
		createEAttribute(deploymentEClass, DEPLOYMENT__DEPLOY_EXT_GROUP);
		createEReference(deploymentEClass, DEPLOYMENT__DEPLOY_EXT);

		deploymentProjectEClass = createEClass(DEPLOYMENT_PROJECT);

		deploymentTargetEClass = createEClass(DEPLOYMENT_TARGET);
		createEReference(deploymentTargetEClass, DEPLOYMENT_TARGET__PARAMETERS);
		createEAttribute(deploymentTargetEClass, DEPLOYMENT_TARGET__NAME);

		eglDeploymentRootEClass = createEClass(EGL_DEPLOYMENT_ROOT);
		createEAttribute(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__MIXED);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__XMLNS_PREFIX_MAP);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__XSI_SCHEMA_LOCATION);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__DEPLOY_EXT);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__DEPLOYMENT);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__TARGET);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__TARGET_PROJECT);

		includeEClass = createEClass(INCLUDE);
		createEAttribute(includeEClass, INCLUDE__LOCATION);

		parameterEClass = createEClass(PARAMETER);
		createEAttribute(parameterEClass, PARAMETER__NAME);
		createEAttribute(parameterEClass, PARAMETER__TYPE);
		createEAttribute(parameterEClass, PARAMETER__VALUE);

		parametersEClass = createEClass(PARAMETERS);
		createEReference(parametersEClass, PARAMETERS__PARAMETER);

		resourceEClass = createEClass(RESOURCE);
		createEAttribute(resourceEClass, RESOURCE__ID);

		resourceOmissionsEClass = createEClass(RESOURCE_OMISSIONS);
		createEReference(resourceOmissionsEClass, RESOURCE_OMISSIONS__RESOURCE);

		ruiApplicationEClass = createEClass(RUI_APPLICATION);
		createEReference(ruiApplicationEClass, RUI_APPLICATION__RUIHANDLER);
		createEReference(ruiApplicationEClass, RUI_APPLICATION__PARAMETERS);
		createEAttribute(ruiApplicationEClass, RUI_APPLICATION__DEPLOY_ALL_HANDLERS);
		createEAttribute(ruiApplicationEClass, RUI_APPLICATION__SUPPORT_DYNAMIC_LOADING);

		ruiHandlerEClass = createEClass(RUI_HANDLER);
		createEReference(ruiHandlerEClass, RUI_HANDLER__PARAMETERS);
		createEAttribute(ruiHandlerEClass, RUI_HANDLER__ENABLE_GENERATION);
		createEAttribute(ruiHandlerEClass, RUI_HANDLER__IMPLEMENTATION);

		serviceEClass = createEClass(SERVICE);
		createEReference(serviceEClass, SERVICE__PARAMETERS);
		createEAttribute(serviceEClass, SERVICE__IMPLEMENTATION);
		createEAttribute(serviceEClass, SERVICE__TYPE);

		servicesEClass = createEClass(SERVICES);
		createEReference(servicesEClass, SERVICES__SERVICE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents()
	{
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		deploymentProjectEClass.getESuperTypes().add(this.getDeploymentTarget());

		// Initialize classes and features; add operations and parameters
		initEClass(bindingEClass, Binding.class, "Binding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getBinding_Parameters(), this.getParameters(), null, "parameters", null, 0, 1, Binding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBinding_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, Binding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBinding_Type(), theXMLTypePackage.getString(), "type", null, 1, 1, Binding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBinding_Uri(), theXMLTypePackage.getAnyURI(), "uri", null, 0, 1, Binding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBinding_UseURI(), theXMLTypePackage.getBoolean(), "useURI", null, 0, 1, Binding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(bindingsEClass, Bindings.class, "Bindings", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getBindings_Binding(), this.getBinding(), null, "binding", null, 0, -1, Bindings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(deployExtEClass, DeployExt.class, "DeployExt", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(deploymentEClass, Deployment.class, "Deployment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDeployment_Bindings(), this.getBindings(), null, "bindings", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_Services(), this.getServices(), null, "services", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_Ruiapplication(), this.getRUIApplication(), null, "ruiapplication", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_ResourceOmissions(), this.getResourceOmissions(), null, "resourceOmissions", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeployment_TargetGroup(), ecorePackage.getEFeatureMapEntry(), "targetGroup", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_Target(), this.getDeploymentTarget(), null, "target", null, 0, 1, Deployment.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_Include(), this.getInclude(), null, "include", null, 0, -1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeployment_DeployExtGroup(), ecorePackage.getEFeatureMapEntry(), "deployExtGroup", null, 0, -1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_DeployExt(), this.getDeployExt(), null, "deployExt", null, 0, -1, Deployment.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(deploymentProjectEClass, DeploymentProject.class, "DeploymentProject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(deploymentTargetEClass, DeploymentTarget.class, "DeploymentTarget", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDeploymentTarget_Parameters(), this.getParameters(), null, "parameters", null, 0, 1, DeploymentTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeploymentTarget_Name(), theXMLTypePackage.getString(), "name", "", 0, 1, DeploymentTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eglDeploymentRootEClass, EGLDeploymentRoot.class, "EGLDeploymentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEGLDeploymentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_DeployExt(), this.getDeployExt(), null, "deployExt", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_Deployment(), this.getDeployment(), null, "deployment", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_Target(), this.getDeploymentTarget(), null, "target", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_TargetProject(), this.getDeploymentProject(), null, "targetProject", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(includeEClass, Include.class, "Include", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getInclude_Location(), theXMLTypePackage.getAnyURI(), "location", null, 0, 1, Include.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parameterEClass, Parameter.class, "Parameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParameter_Name(), theXMLTypePackage.getString(), "name", null, 1, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Type(), theXMLTypePackage.getString(), "type", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Value(), theXMLTypePackage.getString(), "value", null, 1, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parametersEClass, Parameters.class, "Parameters", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getParameters_Parameter(), this.getParameter(), null, "parameter", null, 0, -1, Parameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(resourceEClass, Resource.class, "Resource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getResource_Id(), theXMLTypePackage.getString(), "id", null, 1, 1, Resource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(resourceOmissionsEClass, ResourceOmissions.class, "ResourceOmissions", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getResourceOmissions_Resource(), this.getResource(), null, "resource", null, 0, -1, ResourceOmissions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(ruiApplicationEClass, RUIApplication.class, "RUIApplication", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRUIApplication_Ruihandler(), this.getRUIHandler(), null, "ruihandler", null, 0, -1, RUIApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRUIApplication_Parameters(), this.getParameters(), null, "parameters", null, 0, 1, RUIApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRUIApplication_DeployAllHandlers(), theXMLTypePackage.getBoolean(), "deployAllHandlers", "true", 0, 1, RUIApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRUIApplication_SupportDynamicLoading(), theXMLTypePackage.getBoolean(), "supportDynamicLoading", "true", 0, 1, RUIApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(ruiHandlerEClass, RUIHandler.class, "RUIHandler", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRUIHandler_Parameters(), this.getParameters(), null, "parameters", null, 0, 1, RUIHandler.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRUIHandler_EnableGeneration(), theXMLTypePackage.getBoolean(), "enableGeneration", "true", 0, 1, RUIHandler.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRUIHandler_Implementation(), theXMLTypePackage.getNCName(), "implementation", null, 1, 1, RUIHandler.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(serviceEClass, Service.class, "Service", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getService_Parameters(), this.getParameters(), null, "parameters", null, 0, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getService_Implementation(), theXMLTypePackage.getNCName(), "implementation", null, 1, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getService_Type(), theXMLTypePackage.getString(), "type", null, 1, 1, Service.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(servicesEClass, Services.class, "Services", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getServices_Service(), this.getService(), null, "service", null, 0, -1, Services.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations()
	{
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";		
		addAnnotation
		  (bindingEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Binding",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getBinding_Parameters(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "parameters"
		   });		
		addAnnotation
		  (getBinding_Name(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (getBinding_Type(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "type"
		   });		
		addAnnotation
		  (getBinding_Uri(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "uri"
		   });		
		addAnnotation
		  (getBinding_UseURI(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "useURI"
		   });		
		addAnnotation
		  (bindingsEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Bindings",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getBindings_Binding(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "binding"
		   });		
		addAnnotation
		  (deployExtEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Deploy-Ext",
			 "kind", "empty"
		   });		
		addAnnotation
		  (deploymentEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Deployment",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getDeployment_Bindings(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "bindings"
		   });		
		addAnnotation
		  (getDeployment_Services(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "services"
		   });		
		addAnnotation
		  (getDeployment_Ruiapplication(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "ruiapplication"
		   });		
		addAnnotation
		  (getDeployment_ResourceOmissions(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "resource-omissions"
		   });		
		addAnnotation
		  (getDeployment_TargetGroup(), 
		   source, 
		   new String[] 
		   {
			 "kind", "group",
			 "name", "target:group",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getDeployment_Target(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "target",
			 "namespace", "##targetNamespace",
			 "group", "target:group"
		   });		
		addAnnotation
		  (getDeployment_Include(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "include"
		   });		
		addAnnotation
		  (getDeployment_DeployExtGroup(), 
		   source, 
		   new String[] 
		   {
			 "kind", "group",
			 "name", "deploy-ext:group",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getDeployment_DeployExt(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "deploy-ext",
			 "namespace", "##targetNamespace",
			 "group", "deploy-ext:group"
		   });		
		addAnnotation
		  (deploymentProjectEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "DeploymentProject",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (deploymentTargetEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "DeploymentTarget",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getDeploymentTarget_Parameters(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "parameters"
		   });		
		addAnnotation
		  (getDeploymentTarget_Name(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (eglDeploymentRootEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_Mixed(), 
		   source, 
		   new String[] 
		   {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_XMLNSPrefixMap(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "xmlns:prefix"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_XSISchemaLocation(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "xsi:schemaLocation"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_DeployExt(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "deploy-ext",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_Deployment(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "deployment",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_Target(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "target",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_TargetProject(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "target.project",
			 "namespace", "##targetNamespace",
			 "affiliation", "target"
		   });		
		addAnnotation
		  (includeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Include",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getInclude_Location(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "location"
		   });		
		addAnnotation
		  (parameterEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Parameter",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getParameter_Name(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (getParameter_Type(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "type"
		   });		
		addAnnotation
		  (getParameter_Value(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "value"
		   });		
		addAnnotation
		  (parametersEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Parameters",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getParameters_Parameter(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "parameter"
		   });		
		addAnnotation
		  (resourceEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Resource",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getResource_Id(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "id"
		   });		
		addAnnotation
		  (resourceOmissionsEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "ResourceOmissions",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getResourceOmissions_Resource(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "resource"
		   });		
		addAnnotation
		  (ruiApplicationEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "RUIApplication",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getRUIApplication_Ruihandler(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "ruihandler"
		   });		
		addAnnotation
		  (getRUIApplication_Parameters(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "parameters"
		   });		
		addAnnotation
		  (getRUIApplication_DeployAllHandlers(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "deployAllHandlers"
		   });		
		addAnnotation
		  (getRUIApplication_SupportDynamicLoading(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "supportDynamicLoading"
		   });		
		addAnnotation
		  (ruiHandlerEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "RUIHandler",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getRUIHandler_Parameters(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "parameters"
		   });		
		addAnnotation
		  (getRUIHandler_EnableGeneration(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "enableGeneration"
		   });		
		addAnnotation
		  (getRUIHandler_Implementation(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "implementation"
		   });		
		addAnnotation
		  (serviceEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Service",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getService_Parameters(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "parameters"
		   });		
		addAnnotation
		  (getService_Implementation(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "implementation"
		   });		
		addAnnotation
		  (getService_Type(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "type"
		   });		
		addAnnotation
		  (servicesEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Services",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getServices_Service(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "service"
		   });
	}

} //DeploymentPackageImpl
