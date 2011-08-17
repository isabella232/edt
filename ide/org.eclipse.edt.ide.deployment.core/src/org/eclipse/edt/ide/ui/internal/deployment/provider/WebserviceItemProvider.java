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
package org.eclipse.edt.ide.ui.internal.deployment.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.Webservice;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link org.eclipse.edt.ide.ui.internal.deployment.Webservice} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class WebserviceItemProvider
	extends ItemProviderAdapter
	implements
		IEditingDomainItemProvider,
		IStructuredItemContentProvider,
		ITreeItemContentProvider,
		IItemLabelProvider,
		IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WebserviceItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addEnableGenerationPropertyDescriptor(object);
			addImplementationPropertyDescriptor(object);
			addImplTypePropertyDescriptor(object);
			addProtocolPropertyDescriptor(object);
			addSoapVersionPropertyDescriptor(object);
			addStylePropertyDescriptor(object);
			addTransactionPropertyDescriptor(object);
			addUriPropertyDescriptor(object);
			addUseExistingWSDLPropertyDescriptor(object);
			addUserIDPropertyDescriptor(object);
			addWsdlLocationPropertyDescriptor(object);
			addWsdlPortPropertyDescriptor(object);
			addWsdlServicePropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Enable Generation feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addEnableGenerationPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_enableGeneration_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_enableGeneration_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__ENABLE_GENERATION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Implementation feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addImplementationPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_implementation_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_implementation_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__IMPLEMENTATION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Impl Type feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addImplTypePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_implType_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_implType_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__IMPL_TYPE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Protocol feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addProtocolPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_protocol_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_protocol_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__PROTOCOL,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Soap Version feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addSoapVersionPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_soapVersion_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_soapVersion_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__SOAP_VERSION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Style feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addStylePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_style_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_style_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__STYLE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Transaction feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addTransactionPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_transaction_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_transaction_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__TRANSACTION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Uri feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addUriPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_uri_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_uri_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__URI,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Use Existing WSDL feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addUseExistingWSDLPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_useExistingWSDL_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_useExistingWSDL_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__USE_EXISTING_WSDL,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the User ID feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addUserIDPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_userID_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_userID_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__USER_ID,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Wsdl Location feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addWsdlLocationPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_wsdlLocation_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_wsdlLocation_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__WSDL_LOCATION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Wsdl Port feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addWsdlPortPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_wsdlPort_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_wsdlPort_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__WSDL_PORT,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Wsdl Service feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addWsdlServicePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Webservice_wsdlService_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Webservice_wsdlService_feature", "_UI_Webservice_type"),
				 DeploymentPackage.Literals.WEBSERVICE__WSDL_SERVICE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(DeploymentPackage.Literals.WEBSERVICE__PARAMETERS);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns Webservice.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/Webservice"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		Webservice webservice = (Webservice)object;
		return getString("_UI_Webservice_type") + " " + webservice.isEnableGeneration();
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(Webservice.class)) {
			case DeploymentPackage.WEBSERVICE__ENABLE_GENERATION:
			case DeploymentPackage.WEBSERVICE__IMPLEMENTATION:
			case DeploymentPackage.WEBSERVICE__IMPL_TYPE:
			case DeploymentPackage.WEBSERVICE__PROTOCOL:
			case DeploymentPackage.WEBSERVICE__SOAP_VERSION:
			case DeploymentPackage.WEBSERVICE__STYLE:
			case DeploymentPackage.WEBSERVICE__TRANSACTION:
			case DeploymentPackage.WEBSERVICE__URI:
			case DeploymentPackage.WEBSERVICE__USE_EXISTING_WSDL:
			case DeploymentPackage.WEBSERVICE__USER_ID:
			case DeploymentPackage.WEBSERVICE__WSDL_LOCATION:
			case DeploymentPackage.WEBSERVICE__WSDL_PORT:
			case DeploymentPackage.WEBSERVICE__WSDL_SERVICE:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
			case DeploymentPackage.WEBSERVICE__PARAMETERS:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add
			(createChildParameter
				(DeploymentPackage.Literals.WEBSERVICE__PARAMETERS,
				 DeploymentFactory.eINSTANCE.createParameters()));
	}

	/**
	 * Return the resource locator for this item provider's resources.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return EglddEditPlugin.INSTANCE;
	}

}
