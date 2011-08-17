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

import org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class CICSSSLProtocolItemProvider
	extends ProtocolItemProvider
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
	public CICSSSLProtocolItemProvider(AdapterFactory adapterFactory) {
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

			addConversionTablePropertyDescriptor(object);
			addCtgKeyStorePropertyDescriptor(object);
			addCtgKeyStorePasswordPropertyDescriptor(object);
			addCtgLocationPropertyDescriptor(object);
			addCtgPortPropertyDescriptor(object);
			addLocationPropertyDescriptor(object);
			addServerIDPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Conversion Table feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addConversionTablePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_CICSSSLProtocol_conversionTable_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_CICSSSLProtocol_conversionTable_feature", "_UI_CICSSSLProtocol_type"),
				 DeploymentPackage.Literals.CICSSSL_PROTOCOL__CONVERSION_TABLE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Ctg Key Store feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addCtgKeyStorePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_CICSSSLProtocol_ctgKeyStore_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_CICSSSLProtocol_ctgKeyStore_feature", "_UI_CICSSSLProtocol_type"),
				 DeploymentPackage.Literals.CICSSSL_PROTOCOL__CTG_KEY_STORE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Ctg Key Store Password feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addCtgKeyStorePasswordPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_CICSSSLProtocol_ctgKeyStorePassword_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_CICSSSLProtocol_ctgKeyStorePassword_feature", "_UI_CICSSSLProtocol_type"),
				 DeploymentPackage.Literals.CICSSSL_PROTOCOL__CTG_KEY_STORE_PASSWORD,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Ctg Location feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addCtgLocationPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_CICSSSLProtocol_ctgLocation_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_CICSSSLProtocol_ctgLocation_feature", "_UI_CICSSSLProtocol_type"),
				 DeploymentPackage.Literals.CICSSSL_PROTOCOL__CTG_LOCATION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Ctg Port feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addCtgPortPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_CICSSSLProtocol_ctgPort_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_CICSSSLProtocol_ctgPort_feature", "_UI_CICSSSLProtocol_type"),
				 DeploymentPackage.Literals.CICSSSL_PROTOCOL__CTG_PORT,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Location feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addLocationPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_CICSSSLProtocol_location_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_CICSSSLProtocol_location_feature", "_UI_CICSSSLProtocol_type"),
				 DeploymentPackage.Literals.CICSSSL_PROTOCOL__LOCATION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Server ID feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addServerIDPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_CICSSSLProtocol_serverID_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_CICSSSLProtocol_serverID_feature", "_UI_CICSSSLProtocol_type"),
				 DeploymentPackage.Literals.CICSSSL_PROTOCOL__SERVER_ID,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This returns CICSSSLProtocol.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/CICSSSLProtocol"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((CICSSSLProtocol)object).getName();
		return label == null || label.length() == 0 ?
			getString("_UI_CICSSSLProtocol_type") :
			getString("_UI_CICSSSLProtocol_type") + " " + label;
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

		switch (notification.getFeatureID(CICSSSLProtocol.class)) {
			case DeploymentPackage.CICSSSL_PROTOCOL__CONVERSION_TABLE:
			case DeploymentPackage.CICSSSL_PROTOCOL__CTG_KEY_STORE:
			case DeploymentPackage.CICSSSL_PROTOCOL__CTG_KEY_STORE_PASSWORD:
			case DeploymentPackage.CICSSSL_PROTOCOL__CTG_LOCATION:
			case DeploymentPackage.CICSSSL_PROTOCOL__CTG_PORT:
			case DeploymentPackage.CICSSSL_PROTOCOL__LOCATION:
			case DeploymentPackage.CICSSSL_PROTOCOL__SERVER_ID:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
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
	}

}
