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

import org.eclipse.edt.ide.ui.internal.deployment.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
public class DeploymentFactoryImpl extends EFactoryImpl implements DeploymentFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static DeploymentFactory init() {
		try {
			DeploymentFactory theDeploymentFactory = (DeploymentFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/xmlns/edt/deployment/1.0"); 
			if (theDeploymentFactory != null) {
				return theDeploymentFactory;
			}
		}
		catch (Exception exception) {
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
	public DeploymentFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case DeploymentPackage.BINDINGS: return createBindings();
			case DeploymentPackage.CICSECI_PROTOCOL: return createCICSECIProtocol();
			case DeploymentPackage.CICSJ2C_PROTOCOL: return createCICSJ2CProtocol();
			case DeploymentPackage.CICSSSL_PROTOCOL: return createCICSSSLProtocol();
			case DeploymentPackage.CICSWS_PROTOCOL: return createCICSWSProtocol();
			case DeploymentPackage.DEPLOY_EXT: return createDeployExt();
			case DeploymentPackage.DEPLOYMENT: return createDeployment();
			case DeploymentPackage.DEPLOYMENT_BUILD_DESCRIPTOR: return createDeploymentBuildDescriptor();
			case DeploymentPackage.DEPLOYMENT_PROJECT: return createDeploymentProject();
			case DeploymentPackage.DEPLOYMENT_TARGET: return createDeploymentTarget();
			case DeploymentPackage.EGL_BINDING: return createEGLBinding();
			case DeploymentPackage.EGL_DEPLOYMENT_ROOT: return createEGLDeploymentRoot();
			case DeploymentPackage.IMSJ2C_PROTOCOL: return createIMSJ2CProtocol();
			case DeploymentPackage.IMSTCP_PROTOCOL: return createIMSTCPProtocol();
			case DeploymentPackage.INCLUDE: return createInclude();
			case DeploymentPackage.JAVA400_J2C_PROTOCOL: return createJava400J2cProtocol();
			case DeploymentPackage.JAVA400_PROTOCOL: return createJava400Protocol();
			case DeploymentPackage.LOCAL_PROTOCOL: return createLocalProtocol();
			case DeploymentPackage.NATIVE_BINDING: return createNativeBinding();
			case DeploymentPackage.PARAMETER: return createParameter();
			case DeploymentPackage.PARAMETERS: return createParameters();
			case DeploymentPackage.PROTOCOL: return createProtocol();
			case DeploymentPackage.PROTOCOLS: return createProtocols();
			case DeploymentPackage.REFERENCE_PROTOCOL: return createReferenceProtocol();
			case DeploymentPackage.RESOURCE: return createResource();
			case DeploymentPackage.RESOURCE_OMISSIONS: return createResourceOmissions();
			case DeploymentPackage.REST_BINDING: return createRestBinding();
			case DeploymentPackage.RESTSERVICE: return createRestservice();
			case DeploymentPackage.RESTSERVICES: return createRestservices();
			case DeploymentPackage.RUI_APPLICATION: return createRUIApplication();
			case DeploymentPackage.RUI_HANDLER: return createRUIHandler();
			case DeploymentPackage.RUI_RESOURCE: return createRUIResource();
			case DeploymentPackage.RUI_RESOURCE_OMISSIONS: return createRUIResourceOmissions();
			case DeploymentPackage.SQL_DATABASE_BINDING: return createSQLDatabaseBinding();
			case DeploymentPackage.SYSTEM_IPROTOCOL: return createSystemIProtocol();
			case DeploymentPackage.TCPIP_PROTOCOL: return createTCPIPProtocol();
			case DeploymentPackage.WEB_BINDING: return createWebBinding();
			case DeploymentPackage.WEBSERVICE: return createWebservice();
			case DeploymentPackage.WEBSERVICES: return createWebservices();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case DeploymentPackage.SOAP_VERSION_TYPE:
				return createSOAPVersionTypeFromString(eDataType, initialValue);
			case DeploymentPackage.STYLE_TYPES:
				return createStyleTypesFromString(eDataType, initialValue);
			case DeploymentPackage.WEBSERVICE_RUNTIME_TYPE:
				return createWebserviceRuntimeTypeFromString(eDataType, initialValue);
			case DeploymentPackage.SOAP_VERSION_TYPE_OBJECT:
				return createSOAPVersionTypeObjectFromString(eDataType, initialValue);
			case DeploymentPackage.STYLE_TYPES_OBJECT:
				return createStyleTypesObjectFromString(eDataType, initialValue);
			case DeploymentPackage.WEBSERVICE_RUNTIME_TYPE_OBJECT:
				return createWebserviceRuntimeTypeObjectFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case DeploymentPackage.SOAP_VERSION_TYPE:
				return convertSOAPVersionTypeToString(eDataType, instanceValue);
			case DeploymentPackage.STYLE_TYPES:
				return convertStyleTypesToString(eDataType, instanceValue);
			case DeploymentPackage.WEBSERVICE_RUNTIME_TYPE:
				return convertWebserviceRuntimeTypeToString(eDataType, instanceValue);
			case DeploymentPackage.SOAP_VERSION_TYPE_OBJECT:
				return convertSOAPVersionTypeObjectToString(eDataType, instanceValue);
			case DeploymentPackage.STYLE_TYPES_OBJECT:
				return convertStyleTypesObjectToString(eDataType, instanceValue);
			case DeploymentPackage.WEBSERVICE_RUNTIME_TYPE_OBJECT:
				return convertWebserviceRuntimeTypeObjectToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Bindings createBindings() {
		BindingsImpl bindings = new BindingsImpl();
		return bindings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CICSECIProtocol createCICSECIProtocol() {
		CICSECIProtocolImpl cicseciProtocol = new CICSECIProtocolImpl();
		return cicseciProtocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CICSJ2CProtocol createCICSJ2CProtocol() {
		CICSJ2CProtocolImpl cicsj2CProtocol = new CICSJ2CProtocolImpl();
		return cicsj2CProtocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CICSSSLProtocol createCICSSSLProtocol() {
		CICSSSLProtocolImpl cicssslProtocol = new CICSSSLProtocolImpl();
		return cicssslProtocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CICSWSProtocol createCICSWSProtocol() {
		CICSWSProtocolImpl cicswsProtocol = new CICSWSProtocolImpl();
		return cicswsProtocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeployExt createDeployExt() {
		DeployExtImpl deployExt = new DeployExtImpl();
		return deployExt;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Deployment createDeployment() {
		DeploymentImpl deployment = new DeploymentImpl();
		return deployment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentBuildDescriptor createDeploymentBuildDescriptor() {
		DeploymentBuildDescriptorImpl deploymentBuildDescriptor = new DeploymentBuildDescriptorImpl();
		return deploymentBuildDescriptor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentProject createDeploymentProject() {
		DeploymentProjectImpl deploymentProject = new DeploymentProjectImpl();
		return deploymentProject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentTarget createDeploymentTarget() {
		DeploymentTargetImpl deploymentTarget = new DeploymentTargetImpl();
		return deploymentTarget;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGLBinding createEGLBinding() {
		EGLBindingImpl eglBinding = new EGLBindingImpl();
		return eglBinding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGLDeploymentRoot createEGLDeploymentRoot() {
		EGLDeploymentRootImpl eglDeploymentRoot = new EGLDeploymentRootImpl();
		return eglDeploymentRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IMSJ2CProtocol createIMSJ2CProtocol() {
		IMSJ2CProtocolImpl imsj2CProtocol = new IMSJ2CProtocolImpl();
		return imsj2CProtocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IMSTCPProtocol createIMSTCPProtocol() {
		IMSTCPProtocolImpl imstcpProtocol = new IMSTCPProtocolImpl();
		return imstcpProtocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Include createInclude() {
		IncludeImpl include = new IncludeImpl();
		return include;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Java400J2cProtocol createJava400J2cProtocol() {
		Java400J2cProtocolImpl java400J2cProtocol = new Java400J2cProtocolImpl();
		return java400J2cProtocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Java400Protocol createJava400Protocol() {
		Java400ProtocolImpl java400Protocol = new Java400ProtocolImpl();
		return java400Protocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalProtocol createLocalProtocol() {
		LocalProtocolImpl localProtocol = new LocalProtocolImpl();
		return localProtocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NativeBinding createNativeBinding() {
		NativeBindingImpl nativeBinding = new NativeBindingImpl();
		return nativeBinding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter createParameter() {
		ParameterImpl parameter = new ParameterImpl();
		return parameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameters createParameters() {
		ParametersImpl parameters = new ParametersImpl();
		return parameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Protocol createProtocol() {
		ProtocolImpl protocol = new ProtocolImpl();
		return protocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Protocols createProtocols() {
		ProtocolsImpl protocols = new ProtocolsImpl();
		return protocols;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ReferenceProtocol createReferenceProtocol() {
		ReferenceProtocolImpl referenceProtocol = new ReferenceProtocolImpl();
		return referenceProtocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Resource createResource() {
		ResourceImpl resource = new ResourceImpl();
		return resource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ResourceOmissions createResourceOmissions() {
		ResourceOmissionsImpl resourceOmissions = new ResourceOmissionsImpl();
		return resourceOmissions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RestBinding createRestBinding() {
		RestBindingImpl restBinding = new RestBindingImpl();
		return restBinding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Restservice createRestservice() {
		RestserviceImpl restservice = new RestserviceImpl();
		return restservice;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Restservices createRestservices() {
		RestservicesImpl restservices = new RestservicesImpl();
		return restservices;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RUIApplication createRUIApplication() {
		RUIApplicationImpl ruiApplication = new RUIApplicationImpl();
		return ruiApplication;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RUIHandler createRUIHandler() {
		RUIHandlerImpl ruiHandler = new RUIHandlerImpl();
		return ruiHandler;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RUIResource createRUIResource() {
		RUIResourceImpl ruiResource = new RUIResourceImpl();
		return ruiResource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RUIResourceOmissions createRUIResourceOmissions() {
		RUIResourceOmissionsImpl ruiResourceOmissions = new RUIResourceOmissionsImpl();
		return ruiResourceOmissions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SQLDatabaseBinding createSQLDatabaseBinding() {
		SQLDatabaseBindingImpl sqlDatabaseBinding = new SQLDatabaseBindingImpl();
		return sqlDatabaseBinding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SystemIProtocol createSystemIProtocol() {
		SystemIProtocolImpl systemIProtocol = new SystemIProtocolImpl();
		return systemIProtocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TCPIPProtocol createTCPIPProtocol() {
		TCPIPProtocolImpl tcpipProtocol = new TCPIPProtocolImpl();
		return tcpipProtocol;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WebBinding createWebBinding() {
		WebBindingImpl webBinding = new WebBindingImpl();
		return webBinding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Webservice createWebservice() {
		WebserviceImpl webservice = new WebserviceImpl();
		return webservice;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Webservices createWebservices() {
		WebservicesImpl webservices = new WebservicesImpl();
		return webservices;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SOAPVersionType createSOAPVersionTypeFromString(EDataType eDataType, String initialValue) {
		SOAPVersionType result = SOAPVersionType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSOAPVersionTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StyleTypes createStyleTypesFromString(EDataType eDataType, String initialValue) {
		StyleTypes result = StyleTypes.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertStyleTypesToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WebserviceRuntimeType createWebserviceRuntimeTypeFromString(EDataType eDataType, String initialValue) {
		WebserviceRuntimeType result = WebserviceRuntimeType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertWebserviceRuntimeTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SOAPVersionType createSOAPVersionTypeObjectFromString(EDataType eDataType, String initialValue) {
		return createSOAPVersionTypeFromString(DeploymentPackage.Literals.SOAP_VERSION_TYPE, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSOAPVersionTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertSOAPVersionTypeToString(DeploymentPackage.Literals.SOAP_VERSION_TYPE, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StyleTypes createStyleTypesObjectFromString(EDataType eDataType, String initialValue) {
		return createStyleTypesFromString(DeploymentPackage.Literals.STYLE_TYPES, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertStyleTypesObjectToString(EDataType eDataType, Object instanceValue) {
		return convertStyleTypesToString(DeploymentPackage.Literals.STYLE_TYPES, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WebserviceRuntimeType createWebserviceRuntimeTypeObjectFromString(EDataType eDataType, String initialValue) {
		return createWebserviceRuntimeTypeFromString(DeploymentPackage.Literals.WEBSERVICE_RUNTIME_TYPE, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertWebserviceRuntimeTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertWebserviceRuntimeTypeToString(DeploymentPackage.Literals.WEBSERVICE_RUNTIME_TYPE, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeploymentPackage getDeploymentPackage() {
		return (DeploymentPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static DeploymentPackage getPackage() {
		return DeploymentPackage.eINSTANCE;
	}

} //DeploymentFactoryImpl
