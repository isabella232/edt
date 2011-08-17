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

import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.DeployExt;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentBuildDescriptor;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentProject;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget;
import org.eclipse.edt.ide.ui.internal.deployment.EGLBinding;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.IMSJ2CProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.IMSTCPProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.Include;
import org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.LocalProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.NativeBinding;
import org.eclipse.edt.ide.ui.internal.deployment.Parameter;
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.Protocols;
import org.eclipse.edt.ide.ui.internal.deployment.RUIApplication;
import org.eclipse.edt.ide.ui.internal.deployment.RUIHandler;
import org.eclipse.edt.ide.ui.internal.deployment.RUIResource;
import org.eclipse.edt.ide.ui.internal.deployment.RUIResourceOmissions;
import org.eclipse.edt.ide.ui.internal.deployment.ReferenceProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.Resource;
import org.eclipse.edt.ide.ui.internal.deployment.ResourceOmissions;
import org.eclipse.edt.ide.ui.internal.deployment.RestBinding;
import org.eclipse.edt.ide.ui.internal.deployment.Restservice;
import org.eclipse.edt.ide.ui.internal.deployment.Restservices;
import org.eclipse.edt.ide.ui.internal.deployment.SOAPVersionType;
import org.eclipse.edt.ide.ui.internal.deployment.StyleTypes;
import org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.WebBinding;
import org.eclipse.edt.ide.ui.internal.deployment.Webservice;
import org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType;
import org.eclipse.edt.ide.ui.internal.deployment.Webservices;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
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
public class DeploymentPackageImpl extends EPackageImpl implements DeploymentPackage {
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
	private EClass cicseciProtocolEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass cicsj2CProtocolEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass cicssslProtocolEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass cicswsProtocolEClass = null;

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
	private EClass deploymentBuildDescriptorEClass = null;

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
	private EClass eglBindingEClass = null;

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
	private EClass imsj2CProtocolEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass imstcpProtocolEClass = null;

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
	private EClass java400J2cProtocolEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass java400ProtocolEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localProtocolEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass nativeBindingEClass = null;

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
	private EClass protocolEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass protocolsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass referenceProtocolEClass = null;

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
	private EClass restBindingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass restserviceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass restservicesEClass = null;

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
	private EClass ruiResourceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ruiResourceOmissionsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass systemIProtocolEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass tcpipProtocolEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass webBindingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass webserviceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass webservicesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum soapVersionTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum styleTypesEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum webserviceRuntimeTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType soapVersionTypeObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType styleTypesObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType webserviceRuntimeTypeObjectEDataType = null;

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
	private DeploymentPackageImpl() {
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
	public static DeploymentPackage init() {
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
	public EClass getBindings() {
		return bindingsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getBindings_EglBinding() {
		return (EReference)bindingsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getBindings_WebBinding() {
		return (EReference)bindingsEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getBindings_NativeBinding() {
		return (EReference)bindingsEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getBindings_RestBinding() {
		return (EReference)bindingsEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCICSECIProtocol() {
		return cicseciProtocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSECIProtocol_ConversionTable() {
		return (EAttribute)cicseciProtocolEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSECIProtocol_CtgLocation() {
		return (EAttribute)cicseciProtocolEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSECIProtocol_CtgPort() {
		return (EAttribute)cicseciProtocolEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSECIProtocol_Location() {
		return (EAttribute)cicseciProtocolEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSECIProtocol_ServerID() {
		return (EAttribute)cicseciProtocolEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCICSJ2CProtocol() {
		return cicsj2CProtocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSJ2CProtocol_ConversionTable() {
		return (EAttribute)cicsj2CProtocolEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSJ2CProtocol_Location() {
		return (EAttribute)cicsj2CProtocolEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCICSSSLProtocol() {
		return cicssslProtocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSSSLProtocol_ConversionTable() {
		return (EAttribute)cicssslProtocolEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSSSLProtocol_CtgKeyStore() {
		return (EAttribute)cicssslProtocolEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSSSLProtocol_CtgKeyStorePassword() {
		return (EAttribute)cicssslProtocolEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSSSLProtocol_CtgLocation() {
		return (EAttribute)cicssslProtocolEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSSSLProtocol_CtgPort() {
		return (EAttribute)cicssslProtocolEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSSSLProtocol_Location() {
		return (EAttribute)cicssslProtocolEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSSSLProtocol_ServerID() {
		return (EAttribute)cicssslProtocolEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getCICSWSProtocol() {
		return cicswsProtocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSWSProtocol_Transaction() {
		return (EAttribute)cicswsProtocolEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getCICSWSProtocol_UserID() {
		return (EAttribute)cicswsProtocolEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeployExt() {
		return deployExtEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeployment() {
		return deploymentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_Bindings() {
		return (EReference)deploymentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_Protocols() {
		return (EReference)deploymentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_Webservices() {
		return (EReference)deploymentEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_Restservices() {
		return (EReference)deploymentEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_Ruiapplication() {
		return (EReference)deploymentEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_ResourceOmissions() {
		return (EReference)deploymentEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeployment_TargetGroup() {
		return (EAttribute)deploymentEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_Target() {
		return (EReference)deploymentEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_Include() {
		return (EReference)deploymentEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeployment_DeployExtGroup() {
		return (EAttribute)deploymentEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeployment_DeployExt() {
		return (EReference)deploymentEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeployment_WebserviceRuntime() {
		return (EAttribute)deploymentEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeployment_Alias() {
		return (EAttribute)deploymentEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeploymentBuildDescriptor() {
		return deploymentBuildDescriptorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeploymentBuildDescriptor_FileName() {
		return (EAttribute)deploymentBuildDescriptorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeploymentProject() {
		return deploymentProjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeploymentTarget() {
		return deploymentTargetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDeploymentTarget_Parameters() {
		return (EReference)deploymentTargetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeploymentTarget_Name() {
		return (EAttribute)deploymentTargetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGLBinding() {
		return eglBindingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGLBinding_ProtocolGroup() {
		return (EAttribute)eglBindingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLBinding_Protocol() {
		return (EReference)eglBindingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGLBinding_Alias() {
		return (EAttribute)eglBindingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGLBinding_Name() {
		return (EAttribute)eglBindingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGLBinding_ServiceName() {
		return (EAttribute)eglBindingEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEGLDeploymentRoot() {
		return eglDeploymentRootEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getEGLDeploymentRoot_Mixed() {
		return (EAttribute)eglDeploymentRootEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_XMLNSPrefixMap() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_XSISchemaLocation() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_DeployExt() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_Deployment() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_Protocol() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_ProtocolCicseci() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_ProtocolCicsj2c() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_ProtocolCicsssl() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_ProtocolCicsws() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_ProtocolImsj2c() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_ProtocolImstcp() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_ProtocolJava400() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_ProtocolJava400j2c() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_ProtocolLocal() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_ProtocolRef() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_ProtocolSystemILocal() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_ProtocolTcpip() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_Target() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(18);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_TargetBuildDescriptor() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(19);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getEGLDeploymentRoot_TargetProject() {
		return (EReference)eglDeploymentRootEClass.getEStructuralFeatures().get(20);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getIMSJ2CProtocol() {
		return imsj2CProtocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getIMSJ2CProtocol_Attribute1() {
		return (EAttribute)imsj2CProtocolEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getIMSTCPProtocol() {
		return imstcpProtocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getIMSTCPProtocol_Attribute1() {
		return (EAttribute)imstcpProtocolEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInclude() {
		return includeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getInclude_Location() {
		return (EAttribute)includeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getJava400J2cProtocol() {
		return java400J2cProtocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJava400J2cProtocol_ConversionTable() {
		return (EAttribute)java400J2cProtocolEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJava400J2cProtocol_CurrentLibrary() {
		return (EAttribute)java400J2cProtocolEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJava400J2cProtocol_Libraries() {
		return (EAttribute)java400J2cProtocolEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJava400J2cProtocol_Location() {
		return (EAttribute)java400J2cProtocolEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJava400J2cProtocol_Password() {
		return (EAttribute)java400J2cProtocolEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJava400J2cProtocol_UserID() {
		return (EAttribute)java400J2cProtocolEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getJava400Protocol() {
		return java400ProtocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJava400Protocol_ConversionTable() {
		return (EAttribute)java400ProtocolEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJava400Protocol_Library() {
		return (EAttribute)java400ProtocolEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJava400Protocol_Location() {
		return (EAttribute)java400ProtocolEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJava400Protocol_Password() {
		return (EAttribute)java400ProtocolEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getJava400Protocol_UserID() {
		return (EAttribute)java400ProtocolEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalProtocol() {
		return localProtocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNativeBinding() {
		return nativeBindingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNativeBinding_ProtocolGroup() {
		return (EAttribute)nativeBindingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNativeBinding_Protocol() {
		return (EReference)nativeBindingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNativeBinding_Name() {
		return (EAttribute)nativeBindingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getParameter() {
		return parameterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Name() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getParameter_Value() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getParameters() {
		return parametersEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getParameters_Parameter() {
		return (EReference)parametersEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getProtocol() {
		return protocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProtocol_Name() {
		return (EAttribute)protocolEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getProtocols() {
		return protocolsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProtocols_ProtocolGroup() {
		return (EAttribute)protocolsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getProtocols_Protocol() {
		return (EReference)protocolsEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getReferenceProtocol() {
		return referenceProtocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getReferenceProtocol_Ref() {
		return (EAttribute)referenceProtocolEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getResource() {
		return resourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getResource_Id() {
		return (EAttribute)resourceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getResourceOmissions() {
		return resourceOmissionsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getResourceOmissions_Resource() {
		return (EReference)resourceOmissionsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRestBinding() {
		return restBindingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRestBinding_BaseURI() {
		return (EAttribute)restBindingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRestBinding_EnableGeneration() {
		return (EAttribute)restBindingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRestBinding_Name() {
		return (EAttribute)restBindingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRestBinding_PreserveRequestHeaders() {
		return (EAttribute)restBindingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRestBinding_SessionCookieId() {
		return (EAttribute)restBindingEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRestservice() {
		return restserviceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRestservice_Parameters() {
		return (EReference)restserviceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRestservice_EnableGeneration() {
		return (EAttribute)restserviceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRestservice_Implementation() {
		return (EAttribute)restserviceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRestservice_ImplType() {
		return (EAttribute)restserviceEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRestservice_Protocol() {
		return (EAttribute)restserviceEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRestservice_Stateful() {
		return (EAttribute)restserviceEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRestservice_Uri() {
		return (EAttribute)restserviceEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRestservices() {
		return restservicesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRestservices_Restservice() {
		return (EReference)restservicesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRUIApplication() {
		return ruiApplicationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRUIApplication_Ruihandler() {
		return (EReference)ruiApplicationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRUIApplication_ResourceOmissions() {
		return (EReference)ruiApplicationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRUIApplication_Parameters() {
		return (EReference)ruiApplicationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRUIApplication_DeployAllHandlers() {
		return (EAttribute)ruiApplicationEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRUIApplication_SupportDynamicLoading() {
		return (EAttribute)ruiApplicationEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRUIHandler() {
		return ruiHandlerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRUIHandler_Parameters() {
		return (EReference)ruiHandlerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRUIHandler_EnableGeneration() {
		return (EAttribute)ruiHandlerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRUIHandler_Implementation() {
		return (EAttribute)ruiHandlerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRUIResource() {
		return ruiResourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getRUIResource_Id() {
		return (EAttribute)ruiResourceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRUIResourceOmissions() {
		return ruiResourceOmissionsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getRUIResourceOmissions_Resource() {
		return (EReference)ruiResourceOmissionsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSystemIProtocol() {
		return systemIProtocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSystemIProtocol_Binddir() {
		return (EAttribute)systemIProtocolEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSystemIProtocol_Library() {
		return (EAttribute)systemIProtocolEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getTCPIPProtocol() {
		return tcpipProtocolEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getTCPIPProtocol_Location() {
		return (EAttribute)tcpipProtocolEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getTCPIPProtocol_ServerID() {
		return (EAttribute)tcpipProtocolEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWebBinding() {
		return webBindingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebBinding_EnableGeneration() {
		return (EAttribute)webBindingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebBinding_Interface() {
		return (EAttribute)webBindingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebBinding_Name() {
		return (EAttribute)webBindingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebBinding_SoapVersion() {
		return (EAttribute)webBindingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebBinding_Uri() {
		return (EAttribute)webBindingEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebBinding_WsdlLocation() {
		return (EAttribute)webBindingEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebBinding_WsdlPort() {
		return (EAttribute)webBindingEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebBinding_WsdlService() {
		return (EAttribute)webBindingEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWebservice() {
		return webserviceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWebservice_Parameters() {
		return (EReference)webserviceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_EnableGeneration() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_Implementation() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_ImplType() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_Protocol() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_SoapVersion() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_Style() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_Transaction() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_Uri() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_UseExistingWSDL() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_UserID() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_WsdlLocation() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_WsdlPort() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWebservice_WsdlService() {
		return (EAttribute)webserviceEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWebservices() {
		return webservicesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWebservices_Webservice() {
		return (EReference)webservicesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getSOAPVersionType() {
		return soapVersionTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getStyleTypes() {
		return styleTypesEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getWebserviceRuntimeType() {
		return webserviceRuntimeTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getSOAPVersionTypeObject() {
		return soapVersionTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getStyleTypesObject() {
		return styleTypesObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getWebserviceRuntimeTypeObject() {
		return webserviceRuntimeTypeObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentFactory getDeploymentFactory() {
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
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		bindingsEClass = createEClass(BINDINGS);
		createEReference(bindingsEClass, BINDINGS__EGL_BINDING);
		createEReference(bindingsEClass, BINDINGS__WEB_BINDING);
		createEReference(bindingsEClass, BINDINGS__NATIVE_BINDING);
		createEReference(bindingsEClass, BINDINGS__REST_BINDING);

		cicseciProtocolEClass = createEClass(CICSECI_PROTOCOL);
		createEAttribute(cicseciProtocolEClass, CICSECI_PROTOCOL__CONVERSION_TABLE);
		createEAttribute(cicseciProtocolEClass, CICSECI_PROTOCOL__CTG_LOCATION);
		createEAttribute(cicseciProtocolEClass, CICSECI_PROTOCOL__CTG_PORT);
		createEAttribute(cicseciProtocolEClass, CICSECI_PROTOCOL__LOCATION);
		createEAttribute(cicseciProtocolEClass, CICSECI_PROTOCOL__SERVER_ID);

		cicsj2CProtocolEClass = createEClass(CICSJ2C_PROTOCOL);
		createEAttribute(cicsj2CProtocolEClass, CICSJ2C_PROTOCOL__CONVERSION_TABLE);
		createEAttribute(cicsj2CProtocolEClass, CICSJ2C_PROTOCOL__LOCATION);

		cicssslProtocolEClass = createEClass(CICSSSL_PROTOCOL);
		createEAttribute(cicssslProtocolEClass, CICSSSL_PROTOCOL__CONVERSION_TABLE);
		createEAttribute(cicssslProtocolEClass, CICSSSL_PROTOCOL__CTG_KEY_STORE);
		createEAttribute(cicssslProtocolEClass, CICSSSL_PROTOCOL__CTG_KEY_STORE_PASSWORD);
		createEAttribute(cicssslProtocolEClass, CICSSSL_PROTOCOL__CTG_LOCATION);
		createEAttribute(cicssslProtocolEClass, CICSSSL_PROTOCOL__CTG_PORT);
		createEAttribute(cicssslProtocolEClass, CICSSSL_PROTOCOL__LOCATION);
		createEAttribute(cicssslProtocolEClass, CICSSSL_PROTOCOL__SERVER_ID);

		cicswsProtocolEClass = createEClass(CICSWS_PROTOCOL);
		createEAttribute(cicswsProtocolEClass, CICSWS_PROTOCOL__TRANSACTION);
		createEAttribute(cicswsProtocolEClass, CICSWS_PROTOCOL__USER_ID);

		deployExtEClass = createEClass(DEPLOY_EXT);

		deploymentEClass = createEClass(DEPLOYMENT);
		createEReference(deploymentEClass, DEPLOYMENT__BINDINGS);
		createEReference(deploymentEClass, DEPLOYMENT__PROTOCOLS);
		createEReference(deploymentEClass, DEPLOYMENT__WEBSERVICES);
		createEReference(deploymentEClass, DEPLOYMENT__RESTSERVICES);
		createEReference(deploymentEClass, DEPLOYMENT__RUIAPPLICATION);
		createEReference(deploymentEClass, DEPLOYMENT__RESOURCE_OMISSIONS);
		createEAttribute(deploymentEClass, DEPLOYMENT__TARGET_GROUP);
		createEReference(deploymentEClass, DEPLOYMENT__TARGET);
		createEReference(deploymentEClass, DEPLOYMENT__INCLUDE);
		createEAttribute(deploymentEClass, DEPLOYMENT__DEPLOY_EXT_GROUP);
		createEReference(deploymentEClass, DEPLOYMENT__DEPLOY_EXT);
		createEAttribute(deploymentEClass, DEPLOYMENT__WEBSERVICE_RUNTIME);
		createEAttribute(deploymentEClass, DEPLOYMENT__ALIAS);

		deploymentBuildDescriptorEClass = createEClass(DEPLOYMENT_BUILD_DESCRIPTOR);
		createEAttribute(deploymentBuildDescriptorEClass, DEPLOYMENT_BUILD_DESCRIPTOR__FILE_NAME);

		deploymentProjectEClass = createEClass(DEPLOYMENT_PROJECT);

		deploymentTargetEClass = createEClass(DEPLOYMENT_TARGET);
		createEReference(deploymentTargetEClass, DEPLOYMENT_TARGET__PARAMETERS);
		createEAttribute(deploymentTargetEClass, DEPLOYMENT_TARGET__NAME);

		eglBindingEClass = createEClass(EGL_BINDING);
		createEAttribute(eglBindingEClass, EGL_BINDING__PROTOCOL_GROUP);
		createEReference(eglBindingEClass, EGL_BINDING__PROTOCOL);
		createEAttribute(eglBindingEClass, EGL_BINDING__ALIAS);
		createEAttribute(eglBindingEClass, EGL_BINDING__NAME);
		createEAttribute(eglBindingEClass, EGL_BINDING__SERVICE_NAME);

		eglDeploymentRootEClass = createEClass(EGL_DEPLOYMENT_ROOT);
		createEAttribute(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__MIXED);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__XMLNS_PREFIX_MAP);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__XSI_SCHEMA_LOCATION);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__DEPLOY_EXT);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__DEPLOYMENT);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSECI);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSJ2C);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSSSL);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL_CICSWS);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSJ2C);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL_IMSTCP);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL_JAVA400J2C);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL_LOCAL);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL_REF);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL_SYSTEM_ILOCAL);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__PROTOCOL_TCPIP);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__TARGET);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__TARGET_BUILD_DESCRIPTOR);
		createEReference(eglDeploymentRootEClass, EGL_DEPLOYMENT_ROOT__TARGET_PROJECT);

		imsj2CProtocolEClass = createEClass(IMSJ2C_PROTOCOL);
		createEAttribute(imsj2CProtocolEClass, IMSJ2C_PROTOCOL__ATTRIBUTE1);

		imstcpProtocolEClass = createEClass(IMSTCP_PROTOCOL);
		createEAttribute(imstcpProtocolEClass, IMSTCP_PROTOCOL__ATTRIBUTE1);

		includeEClass = createEClass(INCLUDE);
		createEAttribute(includeEClass, INCLUDE__LOCATION);

		java400J2cProtocolEClass = createEClass(JAVA400_J2C_PROTOCOL);
		createEAttribute(java400J2cProtocolEClass, JAVA400_J2C_PROTOCOL__CONVERSION_TABLE);
		createEAttribute(java400J2cProtocolEClass, JAVA400_J2C_PROTOCOL__CURRENT_LIBRARY);
		createEAttribute(java400J2cProtocolEClass, JAVA400_J2C_PROTOCOL__LIBRARIES);
		createEAttribute(java400J2cProtocolEClass, JAVA400_J2C_PROTOCOL__LOCATION);
		createEAttribute(java400J2cProtocolEClass, JAVA400_J2C_PROTOCOL__PASSWORD);
		createEAttribute(java400J2cProtocolEClass, JAVA400_J2C_PROTOCOL__USER_ID);

		java400ProtocolEClass = createEClass(JAVA400_PROTOCOL);
		createEAttribute(java400ProtocolEClass, JAVA400_PROTOCOL__CONVERSION_TABLE);
		createEAttribute(java400ProtocolEClass, JAVA400_PROTOCOL__LIBRARY);
		createEAttribute(java400ProtocolEClass, JAVA400_PROTOCOL__LOCATION);
		createEAttribute(java400ProtocolEClass, JAVA400_PROTOCOL__PASSWORD);
		createEAttribute(java400ProtocolEClass, JAVA400_PROTOCOL__USER_ID);

		localProtocolEClass = createEClass(LOCAL_PROTOCOL);

		nativeBindingEClass = createEClass(NATIVE_BINDING);
		createEAttribute(nativeBindingEClass, NATIVE_BINDING__PROTOCOL_GROUP);
		createEReference(nativeBindingEClass, NATIVE_BINDING__PROTOCOL);
		createEAttribute(nativeBindingEClass, NATIVE_BINDING__NAME);

		parameterEClass = createEClass(PARAMETER);
		createEAttribute(parameterEClass, PARAMETER__NAME);
		createEAttribute(parameterEClass, PARAMETER__VALUE);

		parametersEClass = createEClass(PARAMETERS);
		createEReference(parametersEClass, PARAMETERS__PARAMETER);

		protocolEClass = createEClass(PROTOCOL);
		createEAttribute(protocolEClass, PROTOCOL__NAME);

		protocolsEClass = createEClass(PROTOCOLS);
		createEAttribute(protocolsEClass, PROTOCOLS__PROTOCOL_GROUP);
		createEReference(protocolsEClass, PROTOCOLS__PROTOCOL);

		referenceProtocolEClass = createEClass(REFERENCE_PROTOCOL);
		createEAttribute(referenceProtocolEClass, REFERENCE_PROTOCOL__REF);

		resourceEClass = createEClass(RESOURCE);
		createEAttribute(resourceEClass, RESOURCE__ID);

		resourceOmissionsEClass = createEClass(RESOURCE_OMISSIONS);
		createEReference(resourceOmissionsEClass, RESOURCE_OMISSIONS__RESOURCE);

		restBindingEClass = createEClass(REST_BINDING);
		createEAttribute(restBindingEClass, REST_BINDING__BASE_URI);
		createEAttribute(restBindingEClass, REST_BINDING__ENABLE_GENERATION);
		createEAttribute(restBindingEClass, REST_BINDING__NAME);
		createEAttribute(restBindingEClass, REST_BINDING__PRESERVE_REQUEST_HEADERS);
		createEAttribute(restBindingEClass, REST_BINDING__SESSION_COOKIE_ID);

		restserviceEClass = createEClass(RESTSERVICE);
		createEReference(restserviceEClass, RESTSERVICE__PARAMETERS);
		createEAttribute(restserviceEClass, RESTSERVICE__ENABLE_GENERATION);
		createEAttribute(restserviceEClass, RESTSERVICE__IMPLEMENTATION);
		createEAttribute(restserviceEClass, RESTSERVICE__IMPL_TYPE);
		createEAttribute(restserviceEClass, RESTSERVICE__PROTOCOL);
		createEAttribute(restserviceEClass, RESTSERVICE__STATEFUL);
		createEAttribute(restserviceEClass, RESTSERVICE__URI);

		restservicesEClass = createEClass(RESTSERVICES);
		createEReference(restservicesEClass, RESTSERVICES__RESTSERVICE);

		ruiApplicationEClass = createEClass(RUI_APPLICATION);
		createEReference(ruiApplicationEClass, RUI_APPLICATION__RUIHANDLER);
		createEReference(ruiApplicationEClass, RUI_APPLICATION__RESOURCE_OMISSIONS);
		createEReference(ruiApplicationEClass, RUI_APPLICATION__PARAMETERS);
		createEAttribute(ruiApplicationEClass, RUI_APPLICATION__DEPLOY_ALL_HANDLERS);
		createEAttribute(ruiApplicationEClass, RUI_APPLICATION__SUPPORT_DYNAMIC_LOADING);

		ruiHandlerEClass = createEClass(RUI_HANDLER);
		createEReference(ruiHandlerEClass, RUI_HANDLER__PARAMETERS);
		createEAttribute(ruiHandlerEClass, RUI_HANDLER__ENABLE_GENERATION);
		createEAttribute(ruiHandlerEClass, RUI_HANDLER__IMPLEMENTATION);

		ruiResourceEClass = createEClass(RUI_RESOURCE);
		createEAttribute(ruiResourceEClass, RUI_RESOURCE__ID);

		ruiResourceOmissionsEClass = createEClass(RUI_RESOURCE_OMISSIONS);
		createEReference(ruiResourceOmissionsEClass, RUI_RESOURCE_OMISSIONS__RESOURCE);

		systemIProtocolEClass = createEClass(SYSTEM_IPROTOCOL);
		createEAttribute(systemIProtocolEClass, SYSTEM_IPROTOCOL__BINDDIR);
		createEAttribute(systemIProtocolEClass, SYSTEM_IPROTOCOL__LIBRARY);

		tcpipProtocolEClass = createEClass(TCPIP_PROTOCOL);
		createEAttribute(tcpipProtocolEClass, TCPIP_PROTOCOL__LOCATION);
		createEAttribute(tcpipProtocolEClass, TCPIP_PROTOCOL__SERVER_ID);

		webBindingEClass = createEClass(WEB_BINDING);
		createEAttribute(webBindingEClass, WEB_BINDING__ENABLE_GENERATION);
		createEAttribute(webBindingEClass, WEB_BINDING__INTERFACE);
		createEAttribute(webBindingEClass, WEB_BINDING__NAME);
		createEAttribute(webBindingEClass, WEB_BINDING__SOAP_VERSION);
		createEAttribute(webBindingEClass, WEB_BINDING__URI);
		createEAttribute(webBindingEClass, WEB_BINDING__WSDL_LOCATION);
		createEAttribute(webBindingEClass, WEB_BINDING__WSDL_PORT);
		createEAttribute(webBindingEClass, WEB_BINDING__WSDL_SERVICE);

		webserviceEClass = createEClass(WEBSERVICE);
		createEReference(webserviceEClass, WEBSERVICE__PARAMETERS);
		createEAttribute(webserviceEClass, WEBSERVICE__ENABLE_GENERATION);
		createEAttribute(webserviceEClass, WEBSERVICE__IMPLEMENTATION);
		createEAttribute(webserviceEClass, WEBSERVICE__IMPL_TYPE);
		createEAttribute(webserviceEClass, WEBSERVICE__PROTOCOL);
		createEAttribute(webserviceEClass, WEBSERVICE__SOAP_VERSION);
		createEAttribute(webserviceEClass, WEBSERVICE__STYLE);
		createEAttribute(webserviceEClass, WEBSERVICE__TRANSACTION);
		createEAttribute(webserviceEClass, WEBSERVICE__URI);
		createEAttribute(webserviceEClass, WEBSERVICE__USE_EXISTING_WSDL);
		createEAttribute(webserviceEClass, WEBSERVICE__USER_ID);
		createEAttribute(webserviceEClass, WEBSERVICE__WSDL_LOCATION);
		createEAttribute(webserviceEClass, WEBSERVICE__WSDL_PORT);
		createEAttribute(webserviceEClass, WEBSERVICE__WSDL_SERVICE);

		webservicesEClass = createEClass(WEBSERVICES);
		createEReference(webservicesEClass, WEBSERVICES__WEBSERVICE);

		// Create enums
		soapVersionTypeEEnum = createEEnum(SOAP_VERSION_TYPE);
		styleTypesEEnum = createEEnum(STYLE_TYPES);
		webserviceRuntimeTypeEEnum = createEEnum(WEBSERVICE_RUNTIME_TYPE);

		// Create data types
		soapVersionTypeObjectEDataType = createEDataType(SOAP_VERSION_TYPE_OBJECT);
		styleTypesObjectEDataType = createEDataType(STYLE_TYPES_OBJECT);
		webserviceRuntimeTypeObjectEDataType = createEDataType(WEBSERVICE_RUNTIME_TYPE_OBJECT);
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
	public void initializePackageContents() {
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
		cicseciProtocolEClass.getESuperTypes().add(this.getProtocol());
		cicsj2CProtocolEClass.getESuperTypes().add(this.getProtocol());
		cicssslProtocolEClass.getESuperTypes().add(this.getProtocol());
		cicswsProtocolEClass.getESuperTypes().add(this.getProtocol());
		deploymentBuildDescriptorEClass.getESuperTypes().add(this.getDeploymentTarget());
		deploymentProjectEClass.getESuperTypes().add(this.getDeploymentTarget());
		imsj2CProtocolEClass.getESuperTypes().add(this.getProtocol());
		imstcpProtocolEClass.getESuperTypes().add(this.getProtocol());
		java400J2cProtocolEClass.getESuperTypes().add(this.getProtocol());
		java400ProtocolEClass.getESuperTypes().add(this.getProtocol());
		localProtocolEClass.getESuperTypes().add(this.getProtocol());
		referenceProtocolEClass.getESuperTypes().add(this.getProtocol());
		systemIProtocolEClass.getESuperTypes().add(this.getProtocol());
		tcpipProtocolEClass.getESuperTypes().add(this.getProtocol());

		// Initialize classes and features; add operations and parameters
		initEClass(bindingsEClass, Bindings.class, "Bindings", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getBindings_EglBinding(), this.getEGLBinding(), null, "eglBinding", null, 0, -1, Bindings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getBindings_WebBinding(), this.getWebBinding(), null, "webBinding", null, 0, -1, Bindings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getBindings_NativeBinding(), this.getNativeBinding(), null, "nativeBinding", null, 0, -1, Bindings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getBindings_RestBinding(), this.getRestBinding(), null, "restBinding", null, 0, -1, Bindings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(cicseciProtocolEClass, CICSECIProtocol.class, "CICSECIProtocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCICSECIProtocol_ConversionTable(), theXMLTypePackage.getString(), "conversionTable", null, 0, 1, CICSECIProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCICSECIProtocol_CtgLocation(), theXMLTypePackage.getString(), "ctgLocation", null, 0, 1, CICSECIProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCICSECIProtocol_CtgPort(), theXMLTypePackage.getString(), "ctgPort", null, 0, 1, CICSECIProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCICSECIProtocol_Location(), theXMLTypePackage.getString(), "location", null, 0, 1, CICSECIProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCICSECIProtocol_ServerID(), theXMLTypePackage.getString(), "serverID", null, 0, 1, CICSECIProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(cicsj2CProtocolEClass, CICSJ2CProtocol.class, "CICSJ2CProtocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCICSJ2CProtocol_ConversionTable(), theXMLTypePackage.getString(), "conversionTable", null, 0, 1, CICSJ2CProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCICSJ2CProtocol_Location(), theXMLTypePackage.getString(), "location", null, 0, 1, CICSJ2CProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(cicssslProtocolEClass, CICSSSLProtocol.class, "CICSSSLProtocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCICSSSLProtocol_ConversionTable(), theXMLTypePackage.getString(), "conversionTable", null, 0, 1, CICSSSLProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCICSSSLProtocol_CtgKeyStore(), theXMLTypePackage.getString(), "ctgKeyStore", null, 0, 1, CICSSSLProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCICSSSLProtocol_CtgKeyStorePassword(), theXMLTypePackage.getString(), "ctgKeyStorePassword", null, 0, 1, CICSSSLProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCICSSSLProtocol_CtgLocation(), theXMLTypePackage.getString(), "ctgLocation", null, 0, 1, CICSSSLProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCICSSSLProtocol_CtgPort(), theXMLTypePackage.getString(), "ctgPort", null, 0, 1, CICSSSLProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCICSSSLProtocol_Location(), theXMLTypePackage.getString(), "location", null, 0, 1, CICSSSLProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCICSSSLProtocol_ServerID(), theXMLTypePackage.getString(), "serverID", null, 0, 1, CICSSSLProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(cicswsProtocolEClass, CICSWSProtocol.class, "CICSWSProtocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCICSWSProtocol_Transaction(), theXMLTypePackage.getString(), "transaction", null, 0, 1, CICSWSProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCICSWSProtocol_UserID(), theXMLTypePackage.getString(), "userID", null, 0, 1, CICSWSProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(deployExtEClass, DeployExt.class, "DeployExt", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(deploymentEClass, Deployment.class, "Deployment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDeployment_Bindings(), this.getBindings(), null, "bindings", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_Protocols(), this.getProtocols(), null, "protocols", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_Webservices(), this.getWebservices(), null, "webservices", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_Restservices(), this.getRestservices(), null, "restservices", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_Ruiapplication(), this.getRUIApplication(), null, "ruiapplication", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_ResourceOmissions(), this.getResourceOmissions(), null, "resourceOmissions", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeployment_TargetGroup(), ecorePackage.getEFeatureMapEntry(), "targetGroup", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_Target(), this.getDeploymentTarget(), null, "target", null, 0, 1, Deployment.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_Include(), this.getInclude(), null, "include", null, 0, -1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeployment_DeployExtGroup(), ecorePackage.getEFeatureMapEntry(), "deployExtGroup", null, 0, -1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeployment_DeployExt(), this.getDeployExt(), null, "deployExt", null, 0, -1, Deployment.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeployment_WebserviceRuntime(), this.getWebserviceRuntimeType(), "webserviceRuntime", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeployment_Alias(), theXMLTypePackage.getNCName(), "alias", null, 0, 1, Deployment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(deploymentBuildDescriptorEClass, DeploymentBuildDescriptor.class, "DeploymentBuildDescriptor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDeploymentBuildDescriptor_FileName(), theXMLTypePackage.getString(), "fileName", null, 1, 1, DeploymentBuildDescriptor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(deploymentProjectEClass, DeploymentProject.class, "DeploymentProject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(deploymentTargetEClass, DeploymentTarget.class, "DeploymentTarget", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDeploymentTarget_Parameters(), this.getParameters(), null, "parameters", null, 0, 1, DeploymentTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeploymentTarget_Name(), theXMLTypePackage.getString(), "name", "", 0, 1, DeploymentTarget.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eglBindingEClass, EGLBinding.class, "EGLBinding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEGLBinding_ProtocolGroup(), ecorePackage.getEFeatureMapEntry(), "protocolGroup", null, 1, 1, EGLBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGLBinding_Protocol(), this.getProtocol(), null, "protocol", null, 1, 1, EGLBinding.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGLBinding_Alias(), theXMLTypePackage.getNCName(), "alias", null, 0, 1, EGLBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGLBinding_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, EGLBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEGLBinding_ServiceName(), theXMLTypePackage.getNCName(), "serviceName", null, 0, 1, EGLBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(eglDeploymentRootEClass, EGLDeploymentRoot.class, "EGLDeploymentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEGLDeploymentRoot_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_XMLNSPrefixMap(), ecorePackage.getEStringToStringMapEntry(), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_XSISchemaLocation(), ecorePackage.getEStringToStringMapEntry(), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_DeployExt(), this.getDeployExt(), null, "deployExt", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_Deployment(), this.getDeployment(), null, "deployment", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_Protocol(), this.getProtocol(), null, "protocol", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_ProtocolCicseci(), this.getCICSECIProtocol(), null, "protocolCicseci", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_ProtocolCicsj2c(), this.getCICSJ2CProtocol(), null, "protocolCicsj2c", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_ProtocolCicsssl(), this.getCICSSSLProtocol(), null, "protocolCicsssl", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_ProtocolCicsws(), this.getCICSWSProtocol(), null, "protocolCicsws", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_ProtocolImsj2c(), this.getIMSJ2CProtocol(), null, "protocolImsj2c", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_ProtocolImstcp(), this.getIMSTCPProtocol(), null, "protocolImstcp", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_ProtocolJava400(), this.getJava400Protocol(), null, "protocolJava400", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_ProtocolJava400j2c(), this.getJava400J2cProtocol(), null, "protocolJava400j2c", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_ProtocolLocal(), this.getLocalProtocol(), null, "protocolLocal", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_ProtocolRef(), this.getReferenceProtocol(), null, "protocolRef", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_ProtocolSystemILocal(), this.getSystemIProtocol(), null, "protocolSystemILocal", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_ProtocolTcpip(), this.getTCPIPProtocol(), null, "protocolTcpip", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_Target(), this.getDeploymentTarget(), null, "target", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_TargetBuildDescriptor(), this.getDeploymentBuildDescriptor(), null, "targetBuildDescriptor", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getEGLDeploymentRoot_TargetProject(), this.getDeploymentProject(), null, "targetProject", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(imsj2CProtocolEClass, IMSJ2CProtocol.class, "IMSJ2CProtocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIMSJ2CProtocol_Attribute1(), theXMLTypePackage.getString(), "attribute1", null, 0, 1, IMSJ2CProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(imstcpProtocolEClass, IMSTCPProtocol.class, "IMSTCPProtocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIMSTCPProtocol_Attribute1(), theXMLTypePackage.getString(), "attribute1", null, 0, 1, IMSTCPProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(includeEClass, Include.class, "Include", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getInclude_Location(), theXMLTypePackage.getAnyURI(), "location", null, 0, 1, Include.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(java400J2cProtocolEClass, Java400J2cProtocol.class, "Java400J2cProtocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getJava400J2cProtocol_ConversionTable(), theXMLTypePackage.getString(), "conversionTable", null, 0, 1, Java400J2cProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJava400J2cProtocol_CurrentLibrary(), theXMLTypePackage.getString(), "currentLibrary", null, 0, 1, Java400J2cProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJava400J2cProtocol_Libraries(), theXMLTypePackage.getString(), "libraries", null, 0, 1, Java400J2cProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJava400J2cProtocol_Location(), theXMLTypePackage.getString(), "location", null, 0, 1, Java400J2cProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJava400J2cProtocol_Password(), theXMLTypePackage.getString(), "password", null, 0, 1, Java400J2cProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJava400J2cProtocol_UserID(), theXMLTypePackage.getString(), "userID", null, 0, 1, Java400J2cProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(java400ProtocolEClass, Java400Protocol.class, "Java400Protocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getJava400Protocol_ConversionTable(), theXMLTypePackage.getString(), "conversionTable", null, 0, 1, Java400Protocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJava400Protocol_Library(), theXMLTypePackage.getString(), "library", null, 0, 1, Java400Protocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJava400Protocol_Location(), theXMLTypePackage.getString(), "location", null, 0, 1, Java400Protocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJava400Protocol_Password(), theXMLTypePackage.getString(), "password", null, 0, 1, Java400Protocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJava400Protocol_UserID(), theXMLTypePackage.getString(), "userID", null, 0, 1, Java400Protocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(localProtocolEClass, LocalProtocol.class, "LocalProtocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(nativeBindingEClass, NativeBinding.class, "NativeBinding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getNativeBinding_ProtocolGroup(), ecorePackage.getEFeatureMapEntry(), "protocolGroup", null, 1, 1, NativeBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNativeBinding_Protocol(), this.getProtocol(), null, "protocol", null, 1, 1, NativeBinding.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getNativeBinding_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, NativeBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parameterEClass, Parameter.class, "Parameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParameter_Name(), theXMLTypePackage.getString(), "name", null, 1, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Value(), theXMLTypePackage.getString(), "value", null, 1, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parametersEClass, Parameters.class, "Parameters", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getParameters_Parameter(), this.getParameter(), null, "parameter", null, 0, -1, Parameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(protocolEClass, Protocol.class, "Protocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProtocol_Name(), theXMLTypePackage.getNCName(), "name", null, 0, 1, Protocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(protocolsEClass, Protocols.class, "Protocols", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProtocols_ProtocolGroup(), ecorePackage.getEFeatureMapEntry(), "protocolGroup", null, 0, -1, Protocols.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProtocols_Protocol(), this.getProtocol(), null, "protocol", null, 0, -1, Protocols.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(referenceProtocolEClass, ReferenceProtocol.class, "ReferenceProtocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getReferenceProtocol_Ref(), theXMLTypePackage.getNCName(), "ref", null, 0, 1, ReferenceProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(resourceEClass, Resource.class, "Resource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getResource_Id(), theXMLTypePackage.getString(), "id", null, 1, 1, Resource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(resourceOmissionsEClass, ResourceOmissions.class, "ResourceOmissions", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getResourceOmissions_Resource(), this.getResource(), null, "resource", null, 0, -1, ResourceOmissions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(restBindingEClass, RestBinding.class, "RestBinding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRestBinding_BaseURI(), theXMLTypePackage.getAnyURI(), "baseURI", null, 0, 1, RestBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRestBinding_EnableGeneration(), theXMLTypePackage.getBoolean(), "enableGeneration", null, 0, 1, RestBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRestBinding_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, RestBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRestBinding_PreserveRequestHeaders(), theXMLTypePackage.getBoolean(), "preserveRequestHeaders", null, 0, 1, RestBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRestBinding_SessionCookieId(), theXMLTypePackage.getString(), "sessionCookieId", null, 0, 1, RestBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(restserviceEClass, Restservice.class, "Restservice", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRestservice_Parameters(), this.getParameters(), null, "parameters", null, 0, 1, Restservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRestservice_EnableGeneration(), theXMLTypePackage.getBoolean(), "enableGeneration", "true", 0, 1, Restservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRestservice_Implementation(), theXMLTypePackage.getNCName(), "implementation", null, 0, 1, Restservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRestservice_ImplType(), theXMLTypePackage.getInt(), "implType", null, 0, 1, Restservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRestservice_Protocol(), theXMLTypePackage.getNCName(), "protocol", null, 0, 1, Restservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRestservice_Stateful(), theXMLTypePackage.getBoolean(), "stateful", "false", 0, 1, Restservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRestservice_Uri(), theXMLTypePackage.getString(), "uri", null, 0, 1, Restservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(restservicesEClass, Restservices.class, "Restservices", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRestservices_Restservice(), this.getRestservice(), null, "restservice", null, 0, -1, Restservices.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(ruiApplicationEClass, RUIApplication.class, "RUIApplication", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRUIApplication_Ruihandler(), this.getRUIHandler(), null, "ruihandler", null, 0, -1, RUIApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRUIApplication_ResourceOmissions(), this.getRUIResourceOmissions(), null, "resourceOmissions", null, 0, 1, RUIApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRUIApplication_Parameters(), this.getParameters(), null, "parameters", null, 0, 1, RUIApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRUIApplication_DeployAllHandlers(), theXMLTypePackage.getBoolean(), "deployAllHandlers", "true", 0, 1, RUIApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRUIApplication_SupportDynamicLoading(), theXMLTypePackage.getBoolean(), "supportDynamicLoading", "true", 0, 1, RUIApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(ruiHandlerEClass, RUIHandler.class, "RUIHandler", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRUIHandler_Parameters(), this.getParameters(), null, "parameters", null, 0, 1, RUIHandler.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRUIHandler_EnableGeneration(), theXMLTypePackage.getBoolean(), "enableGeneration", "true", 0, 1, RUIHandler.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRUIHandler_Implementation(), theXMLTypePackage.getNCName(), "implementation", null, 1, 1, RUIHandler.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(ruiResourceEClass, RUIResource.class, "RUIResource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRUIResource_Id(), theXMLTypePackage.getString(), "id", null, 1, 1, RUIResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(ruiResourceOmissionsEClass, RUIResourceOmissions.class, "RUIResourceOmissions", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getRUIResourceOmissions_Resource(), this.getRUIResource(), null, "resource", null, 0, -1, RUIResourceOmissions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(systemIProtocolEClass, SystemIProtocol.class, "SystemIProtocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSystemIProtocol_Binddir(), theXMLTypePackage.getString(), "binddir", null, 0, 1, SystemIProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSystemIProtocol_Library(), theXMLTypePackage.getString(), "library", null, 0, 1, SystemIProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(tcpipProtocolEClass, TCPIPProtocol.class, "TCPIPProtocol", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTCPIPProtocol_Location(), theXMLTypePackage.getString(), "location", null, 0, 1, TCPIPProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTCPIPProtocol_ServerID(), theXMLTypePackage.getString(), "serverID", null, 0, 1, TCPIPProtocol.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(webBindingEClass, WebBinding.class, "WebBinding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getWebBinding_EnableGeneration(), theXMLTypePackage.getBoolean(), "enableGeneration", null, 0, 1, WebBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebBinding_Interface(), theXMLTypePackage.getNCName(), "interface", null, 1, 1, WebBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebBinding_Name(), theXMLTypePackage.getNCName(), "name", null, 1, 1, WebBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebBinding_SoapVersion(), this.getSOAPVersionType(), "soapVersion", null, 0, 1, WebBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebBinding_Uri(), theXMLTypePackage.getAnyURI(), "uri", null, 0, 1, WebBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebBinding_WsdlLocation(), theXMLTypePackage.getAnyURI(), "wsdlLocation", null, 1, 1, WebBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebBinding_WsdlPort(), theXMLTypePackage.getAnyURI(), "wsdlPort", null, 1, 1, WebBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebBinding_WsdlService(), theXMLTypePackage.getAnyURI(), "wsdlService", null, 0, 1, WebBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(webserviceEClass, Webservice.class, "Webservice", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getWebservice_Parameters(), this.getParameters(), null, "parameters", null, 0, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_EnableGeneration(), theXMLTypePackage.getBoolean(), "enableGeneration", "true", 0, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_Implementation(), theXMLTypePackage.getNCName(), "implementation", null, 0, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_ImplType(), theXMLTypePackage.getInt(), "implType", null, 0, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_Protocol(), theXMLTypePackage.getNCName(), "protocol", null, 0, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_SoapVersion(), this.getSOAPVersionType(), "soapVersion", null, 0, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_Style(), this.getStyleTypes(), "style", "document-wrapped", 0, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_Transaction(), theXMLTypePackage.getString(), "transaction", null, 0, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_Uri(), theXMLTypePackage.getString(), "uri", null, 0, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_UseExistingWSDL(), theXMLTypePackage.getBoolean(), "useExistingWSDL", null, 0, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_UserID(), theXMLTypePackage.getString(), "userID", null, 0, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_WsdlLocation(), theXMLTypePackage.getAnyURI(), "wsdlLocation", null, 1, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_WsdlPort(), theXMLTypePackage.getAnyURI(), "wsdlPort", null, 1, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWebservice_WsdlService(), theXMLTypePackage.getAnyURI(), "wsdlService", null, 0, 1, Webservice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(webservicesEClass, Webservices.class, "Webservices", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getWebservices_Webservice(), this.getWebservice(), null, "webservice", null, 0, -1, Webservices.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(soapVersionTypeEEnum, SOAPVersionType.class, "SOAPVersionType");
		addEEnumLiteral(soapVersionTypeEEnum, SOAPVersionType.SOAP11);
		addEEnumLiteral(soapVersionTypeEEnum, SOAPVersionType.SOAP12);

		initEEnum(styleTypesEEnum, StyleTypes.class, "StyleTypes");
		addEEnumLiteral(styleTypesEEnum, StyleTypes.DOCUMENT_WRAPPED);
		addEEnumLiteral(styleTypesEEnum, StyleTypes.RPC);

		initEEnum(webserviceRuntimeTypeEEnum, WebserviceRuntimeType.class, "WebserviceRuntimeType");
		addEEnumLiteral(webserviceRuntimeTypeEEnum, WebserviceRuntimeType.JAXRPC);
		addEEnumLiteral(webserviceRuntimeTypeEEnum, WebserviceRuntimeType.JAXWS);

		// Initialize data types
		initEDataType(soapVersionTypeObjectEDataType, SOAPVersionType.class, "SOAPVersionTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS);
		initEDataType(styleTypesObjectEDataType, StyleTypes.class, "StyleTypesObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS);
		initEDataType(webserviceRuntimeTypeObjectEDataType, WebserviceRuntimeType.class, "WebserviceRuntimeTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS);

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
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";		
		addAnnotation
		  (bindingsEClass, 
		   source, 
		   new String[] {
			 "name", "Bindings",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getBindings_EglBinding(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "eglBinding"
		   });		
		addAnnotation
		  (getBindings_WebBinding(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "webBinding"
		   });		
		addAnnotation
		  (getBindings_NativeBinding(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "nativeBinding"
		   });		
		addAnnotation
		  (getBindings_RestBinding(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "restBinding"
		   });		
		addAnnotation
		  (cicseciProtocolEClass, 
		   source, 
		   new String[] {
			 "name", "CICSECIProtocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getCICSECIProtocol_ConversionTable(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "conversionTable"
		   });		
		addAnnotation
		  (getCICSECIProtocol_CtgLocation(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "ctgLocation"
		   });		
		addAnnotation
		  (getCICSECIProtocol_CtgPort(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "ctgPort"
		   });		
		addAnnotation
		  (getCICSECIProtocol_Location(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "location"
		   });		
		addAnnotation
		  (getCICSECIProtocol_ServerID(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "serverID"
		   });		
		addAnnotation
		  (cicsj2CProtocolEClass, 
		   source, 
		   new String[] {
			 "name", "CICSJ2CProtocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getCICSJ2CProtocol_ConversionTable(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "conversionTable"
		   });		
		addAnnotation
		  (getCICSJ2CProtocol_Location(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "location"
		   });		
		addAnnotation
		  (cicssslProtocolEClass, 
		   source, 
		   new String[] {
			 "name", "CICSSSLProtocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getCICSSSLProtocol_ConversionTable(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "conversionTable"
		   });		
		addAnnotation
		  (getCICSSSLProtocol_CtgKeyStore(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "ctgKeyStore"
		   });		
		addAnnotation
		  (getCICSSSLProtocol_CtgKeyStorePassword(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "ctgKeyStorePassword"
		   });		
		addAnnotation
		  (getCICSSSLProtocol_CtgLocation(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "ctgLocation"
		   });		
		addAnnotation
		  (getCICSSSLProtocol_CtgPort(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "ctgPort"
		   });		
		addAnnotation
		  (getCICSSSLProtocol_Location(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "location"
		   });		
		addAnnotation
		  (getCICSSSLProtocol_ServerID(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "serverID"
		   });		
		addAnnotation
		  (cicswsProtocolEClass, 
		   source, 
		   new String[] {
			 "name", "CICSWSProtocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getCICSWSProtocol_Transaction(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "transaction"
		   });		
		addAnnotation
		  (getCICSWSProtocol_UserID(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "userID"
		   });		
		addAnnotation
		  (deployExtEClass, 
		   source, 
		   new String[] {
			 "name", "Deploy-Ext",
			 "kind", "empty"
		   });		
		addAnnotation
		  (deploymentEClass, 
		   source, 
		   new String[] {
			 "name", "Deployment",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getDeployment_Bindings(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "bindings"
		   });		
		addAnnotation
		  (getDeployment_Protocols(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocols"
		   });		
		addAnnotation
		  (getDeployment_Webservices(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "webservices"
		   });		
		addAnnotation
		  (getDeployment_Restservices(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "restservices"
		   });		
		addAnnotation
		  (getDeployment_Ruiapplication(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "ruiapplication"
		   });		
		addAnnotation
		  (getDeployment_ResourceOmissions(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "resource-omissions"
		   });		
		addAnnotation
		  (getDeployment_TargetGroup(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "target:group",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getDeployment_Target(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "target",
			 "namespace", "##targetNamespace",
			 "group", "target:group"
		   });		
		addAnnotation
		  (getDeployment_Include(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "include"
		   });		
		addAnnotation
		  (getDeployment_DeployExtGroup(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "deploy-ext:group",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getDeployment_DeployExt(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "deploy-ext",
			 "namespace", "##targetNamespace",
			 "group", "deploy-ext:group"
		   });		
		addAnnotation
		  (getDeployment_WebserviceRuntime(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "webserviceRuntime"
		   });		
		addAnnotation
		  (getDeployment_Alias(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "alias"
		   });		
		addAnnotation
		  (deploymentBuildDescriptorEClass, 
		   source, 
		   new String[] {
			 "name", "DeploymentBuildDescriptor",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getDeploymentBuildDescriptor_FileName(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "fileName"
		   });		
		addAnnotation
		  (deploymentProjectEClass, 
		   source, 
		   new String[] {
			 "name", "DeploymentProject",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (deploymentTargetEClass, 
		   source, 
		   new String[] {
			 "name", "DeploymentTarget",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getDeploymentTarget_Parameters(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "parameters"
		   });		
		addAnnotation
		  (getDeploymentTarget_Name(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (eglBindingEClass, 
		   source, 
		   new String[] {
			 "name", "EGLBinding",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getEGLBinding_ProtocolGroup(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "protocol:group",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getEGLBinding_Protocol(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol",
			 "namespace", "##targetNamespace",
			 "group", "protocol:group"
		   });		
		addAnnotation
		  (getEGLBinding_Alias(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "alias"
		   });		
		addAnnotation
		  (getEGLBinding_Name(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (getEGLBinding_ServiceName(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "serviceName"
		   });		
		addAnnotation
		  (eglDeploymentRootEClass, 
		   source, 
		   new String[] {
			 "name", "",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_Mixed(), 
		   source, 
		   new String[] {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_XMLNSPrefixMap(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "xmlns:prefix"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_XSISchemaLocation(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "xsi:schemaLocation"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_DeployExt(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "deploy-ext",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_Deployment(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "deployment",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_Protocol(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_ProtocolCicseci(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol.cicseci",
			 "namespace", "##targetNamespace",
			 "affiliation", "protocol"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_ProtocolCicsj2c(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol.cicsj2c",
			 "namespace", "##targetNamespace",
			 "affiliation", "protocol"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_ProtocolCicsssl(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol.cicsssl",
			 "namespace", "##targetNamespace",
			 "affiliation", "protocol"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_ProtocolCicsws(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol.cicsws",
			 "namespace", "##targetNamespace",
			 "affiliation", "protocol"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_ProtocolImsj2c(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol.imsj2c",
			 "namespace", "##targetNamespace",
			 "affiliation", "protocol"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_ProtocolImstcp(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol.imstcp",
			 "namespace", "##targetNamespace",
			 "affiliation", "protocol"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_ProtocolJava400(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol.java400",
			 "namespace", "##targetNamespace",
			 "affiliation", "protocol"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_ProtocolJava400j2c(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol.java400j2c",
			 "namespace", "##targetNamespace",
			 "affiliation", "protocol"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_ProtocolLocal(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol.local",
			 "namespace", "##targetNamespace",
			 "affiliation", "protocol"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_ProtocolRef(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol.ref",
			 "namespace", "##targetNamespace",
			 "affiliation", "protocol"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_ProtocolSystemILocal(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol.system-i.local",
			 "namespace", "##targetNamespace",
			 "affiliation", "protocol"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_ProtocolTcpip(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol.tcpip",
			 "namespace", "##targetNamespace",
			 "affiliation", "protocol"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_Target(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "target",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_TargetBuildDescriptor(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "target.buildDescriptor",
			 "namespace", "##targetNamespace",
			 "affiliation", "target"
		   });		
		addAnnotation
		  (getEGLDeploymentRoot_TargetProject(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "target.project",
			 "namespace", "##targetNamespace",
			 "affiliation", "target"
		   });		
		addAnnotation
		  (imsj2CProtocolEClass, 
		   source, 
		   new String[] {
			 "name", "IMSJ2CProtocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getIMSJ2CProtocol_Attribute1(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "attribute1"
		   });		
		addAnnotation
		  (imstcpProtocolEClass, 
		   source, 
		   new String[] {
			 "name", "IMSTCPProtocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getIMSTCPProtocol_Attribute1(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "attribute1"
		   });		
		addAnnotation
		  (includeEClass, 
		   source, 
		   new String[] {
			 "name", "Include",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getInclude_Location(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "location"
		   });		
		addAnnotation
		  (java400J2cProtocolEClass, 
		   source, 
		   new String[] {
			 "name", "Java400J2cProtocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getJava400J2cProtocol_ConversionTable(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "conversionTable"
		   });		
		addAnnotation
		  (getJava400J2cProtocol_CurrentLibrary(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "currentLibrary"
		   });		
		addAnnotation
		  (getJava400J2cProtocol_Libraries(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "libraries"
		   });		
		addAnnotation
		  (getJava400J2cProtocol_Location(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "location"
		   });		
		addAnnotation
		  (getJava400J2cProtocol_Password(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "password"
		   });		
		addAnnotation
		  (getJava400J2cProtocol_UserID(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "userID"
		   });		
		addAnnotation
		  (java400ProtocolEClass, 
		   source, 
		   new String[] {
			 "name", "Java400Protocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getJava400Protocol_ConversionTable(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "conversionTable"
		   });		
		addAnnotation
		  (getJava400Protocol_Library(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "library"
		   });		
		addAnnotation
		  (getJava400Protocol_Location(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "location"
		   });		
		addAnnotation
		  (getJava400Protocol_Password(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "password"
		   });		
		addAnnotation
		  (getJava400Protocol_UserID(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "userID"
		   });		
		addAnnotation
		  (localProtocolEClass, 
		   source, 
		   new String[] {
			 "name", "LocalProtocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (nativeBindingEClass, 
		   source, 
		   new String[] {
			 "name", "NativeBinding",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getNativeBinding_ProtocolGroup(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "protocol:group",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getNativeBinding_Protocol(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol",
			 "namespace", "##targetNamespace",
			 "group", "protocol:group"
		   });		
		addAnnotation
		  (getNativeBinding_Name(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (parameterEClass, 
		   source, 
		   new String[] {
			 "name", "Parameter",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getParameter_Name(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (getParameter_Value(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "value"
		   });		
		addAnnotation
		  (parametersEClass, 
		   source, 
		   new String[] {
			 "name", "Parameters",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getParameters_Parameter(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "parameter"
		   });		
		addAnnotation
		  (protocolEClass, 
		   source, 
		   new String[] {
			 "name", "Protocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getProtocol_Name(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (protocolsEClass, 
		   source, 
		   new String[] {
			 "name", "Protocols",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getProtocols_ProtocolGroup(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "protocol:group",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getProtocols_Protocol(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol",
			 "namespace", "##targetNamespace",
			 "group", "protocol:group"
		   });		
		addAnnotation
		  (referenceProtocolEClass, 
		   source, 
		   new String[] {
			 "name", "ReferenceProtocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getReferenceProtocol_Ref(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "ref"
		   });		
		addAnnotation
		  (resourceEClass, 
		   source, 
		   new String[] {
			 "name", "Resource",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getResource_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });		
		addAnnotation
		  (resourceOmissionsEClass, 
		   source, 
		   new String[] {
			 "name", "ResourceOmissions",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getResourceOmissions_Resource(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "resource"
		   });		
		addAnnotation
		  (restBindingEClass, 
		   source, 
		   new String[] {
			 "name", "RestBinding",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getRestBinding_BaseURI(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "baseURI"
		   });		
		addAnnotation
		  (getRestBinding_EnableGeneration(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "enableGeneration"
		   });		
		addAnnotation
		  (getRestBinding_Name(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (getRestBinding_PreserveRequestHeaders(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "preserveRequestHeaders"
		   });		
		addAnnotation
		  (getRestBinding_SessionCookieId(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "sessionCookieId"
		   });		
		addAnnotation
		  (restserviceEClass, 
		   source, 
		   new String[] {
			 "name", "Restservice",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getRestservice_Parameters(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "parameters"
		   });		
		addAnnotation
		  (getRestservice_EnableGeneration(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "enableGeneration"
		   });		
		addAnnotation
		  (getRestservice_Implementation(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "implementation"
		   });		
		addAnnotation
		  (getRestservice_ImplType(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "implType"
		   });		
		addAnnotation
		  (getRestservice_Protocol(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "protocol"
		   });		
		addAnnotation
		  (getRestservice_Stateful(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "stateful"
		   });		
		addAnnotation
		  (getRestservice_Uri(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "uri"
		   });		
		addAnnotation
		  (restservicesEClass, 
		   source, 
		   new String[] {
			 "name", "Restservices",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getRestservices_Restservice(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "restservice"
		   });		
		addAnnotation
		  (ruiApplicationEClass, 
		   source, 
		   new String[] {
			 "name", "RUIApplication",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getRUIApplication_Ruihandler(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "ruihandler"
		   });		
		addAnnotation
		  (getRUIApplication_ResourceOmissions(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "resource-omissions"
		   });		
		addAnnotation
		  (getRUIApplication_Parameters(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "parameters"
		   });		
		addAnnotation
		  (getRUIApplication_DeployAllHandlers(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "deployAllHandlers"
		   });		
		addAnnotation
		  (getRUIApplication_SupportDynamicLoading(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "supportDynamicLoading"
		   });		
		addAnnotation
		  (ruiHandlerEClass, 
		   source, 
		   new String[] {
			 "name", "RUIHandler",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getRUIHandler_Parameters(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "parameters"
		   });		
		addAnnotation
		  (getRUIHandler_EnableGeneration(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "enableGeneration"
		   });		
		addAnnotation
		  (getRUIHandler_Implementation(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "implementation"
		   });		
		addAnnotation
		  (ruiResourceEClass, 
		   source, 
		   new String[] {
			 "name", "RUIResource",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getRUIResource_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });		
		addAnnotation
		  (ruiResourceOmissionsEClass, 
		   source, 
		   new String[] {
			 "name", "RUIResourceOmissions",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getRUIResourceOmissions_Resource(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "resource"
		   });		
		addAnnotation
		  (soapVersionTypeEEnum, 
		   source, 
		   new String[] {
			 "name", "SOAPVersionType"
		   });		
		addAnnotation
		  (soapVersionTypeObjectEDataType, 
		   source, 
		   new String[] {
			 "name", "SOAPVersionType:Object",
			 "baseType", "SOAPVersionType"
		   });		
		addAnnotation
		  (styleTypesEEnum, 
		   source, 
		   new String[] {
			 "name", "StyleTypes"
		   });		
		addAnnotation
		  (styleTypesObjectEDataType, 
		   source, 
		   new String[] {
			 "name", "StyleTypes:Object",
			 "baseType", "StyleTypes"
		   });		
		addAnnotation
		  (systemIProtocolEClass, 
		   source, 
		   new String[] {
			 "name", "System-iProtocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getSystemIProtocol_Binddir(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "binddir"
		   });		
		addAnnotation
		  (getSystemIProtocol_Library(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "library"
		   });		
		addAnnotation
		  (tcpipProtocolEClass, 
		   source, 
		   new String[] {
			 "name", "TCPIPProtocol",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getTCPIPProtocol_Location(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "location"
		   });		
		addAnnotation
		  (getTCPIPProtocol_ServerID(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "serverID"
		   });		
		addAnnotation
		  (webBindingEClass, 
		   source, 
		   new String[] {
			 "name", "WebBinding",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getWebBinding_EnableGeneration(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "enableGeneration"
		   });		
		addAnnotation
		  (getWebBinding_Interface(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "interface"
		   });		
		addAnnotation
		  (getWebBinding_Name(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (getWebBinding_SoapVersion(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "soapVersion"
		   });		
		addAnnotation
		  (getWebBinding_Uri(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "uri"
		   });		
		addAnnotation
		  (getWebBinding_WsdlLocation(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "wsdlLocation"
		   });		
		addAnnotation
		  (getWebBinding_WsdlPort(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "wsdlPort"
		   });		
		addAnnotation
		  (getWebBinding_WsdlService(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "wsdlService"
		   });		
		addAnnotation
		  (webserviceEClass, 
		   source, 
		   new String[] {
			 "name", "Webservice",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getWebservice_Parameters(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "parameters"
		   });		
		addAnnotation
		  (getWebservice_EnableGeneration(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "enableGeneration"
		   });		
		addAnnotation
		  (getWebservice_Implementation(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "implementation"
		   });		
		addAnnotation
		  (getWebservice_ImplType(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "implType"
		   });		
		addAnnotation
		  (getWebservice_Protocol(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "protocol"
		   });		
		addAnnotation
		  (getWebservice_SoapVersion(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "soapVersion"
		   });		
		addAnnotation
		  (getWebservice_Style(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "style"
		   });		
		addAnnotation
		  (getWebservice_Transaction(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "transaction"
		   });		
		addAnnotation
		  (getWebservice_Uri(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "uri"
		   });		
		addAnnotation
		  (getWebservice_UseExistingWSDL(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "useExistingWSDL"
		   });		
		addAnnotation
		  (getWebservice_UserID(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "userID"
		   });		
		addAnnotation
		  (getWebservice_WsdlLocation(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "wsdlLocation"
		   });		
		addAnnotation
		  (getWebservice_WsdlPort(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "wsdlPort"
		   });		
		addAnnotation
		  (getWebservice_WsdlService(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "wsdlService"
		   });		
		addAnnotation
		  (webserviceRuntimeTypeEEnum, 
		   source, 
		   new String[] {
			 "name", "WebserviceRuntimeType"
		   });		
		addAnnotation
		  (webserviceRuntimeTypeObjectEDataType, 
		   source, 
		   new String[] {
			 "name", "WebserviceRuntimeType:Object",
			 "baseType", "WebserviceRuntimeType"
		   });		
		addAnnotation
		  (webservicesEClass, 
		   source, 
		   new String[] {
			 "name", "Webservices",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getWebservices_Webservice(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "webservice"
		   });
	}

} //DeploymentPackageImpl
