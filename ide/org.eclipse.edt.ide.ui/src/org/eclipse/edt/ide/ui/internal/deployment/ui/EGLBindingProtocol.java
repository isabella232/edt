/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.internal.util.Encoder;
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class EGLBindingProtocol {

	public static final int COLINDEX_ATTRIBNAME = 0;
	public static final int COLINDEX_ATTRIBVALUE = 1;
	
	private static final char PASSWORD_ECHO = '*';
	private static final String[] TABLE_PROTOCOL_ATTRIB_COLUMN_PROPERTIES={"COL_ATRIBNAME", "COL_ATTRIBVALUE"};	 //$NON-NLS-1$ //$NON-NLS-2$	
	
	public static class EObjectAttributeItem{
		private EObject fEObj;
		private EAttribute fAttribute;
		
		public EObjectAttributeItem(EObject eobj, EAttribute attrib){
			fEObj = eobj;
			fAttribute = attrib;
		}
		
		public boolean isPasswordAttribute(){
			String attribNameLower = fAttribute.getName().toLowerCase();
			if(attribNameLower.indexOf("password") != -1)		//if password field //$NON-NLS-1$
				return true;
			return false;
		}		
		
		public boolean isAttributeValueSet(){
			return fEObj.eIsSet(fAttribute);
		}
		
		public Object getAttributeValue(){
			return fEObj.eGet(fAttribute);
		}
		
		public void setAttributeValue(Object newValue){
			fEObj.eSet(fAttribute, newValue);
		}
		
		public String getAttributeName(){
			return fAttribute.getName();
		}
	}
		
	private static class ProtocolAttributeCellModifier implements ICellModifier{

		private TextCellEditor textCellEditor;
		
		public void setTextCellEditor(TextCellEditor textCellEditor){
			this.textCellEditor = textCellEditor;
		}
		
		public boolean canModify(Object element, String property) {
			if(property.equals(TABLE_PROTOCOL_ATTRIB_COLUMN_PROPERTIES[COLINDEX_ATTRIBVALUE]))
				return true;
			return false;
		}

		public Object getValue(Object element, String property) {
			String value = ""; //$NON-NLS-1$
			if(element instanceof EObjectAttributeItem){
				EObjectAttributeItem protocolitem = (EObjectAttributeItem)element;
				if(property.equals(TABLE_PROTOCOL_ATTRIB_COLUMN_PROPERTIES[COLINDEX_ATTRIBVALUE])){
					if(protocolitem.isAttributeValueSet())					
						value = protocolitem.getAttributeValue().toString();
					char echoChar = '\0';
					if(protocolitem.isPasswordAttribute()){
						echoChar = PASSWORD_ECHO;
						if(Encoder.isEncoded(value))
							value = getEchoedPassword(Encoder.decode(value));
					}						
					((Text)(textCellEditor.getControl())).setEchoChar(echoChar);						
				}					
			}
			return value;
		}

		public void modify(Object element, String property, Object value) {
			if (element instanceof TableItem) {
				TableItem tablitem = (TableItem) element;
				Object tableitemData = tablitem.getData();
				if(tableitemData instanceof EObjectAttributeItem){
					EObjectAttributeItem protocolitem = (EObjectAttributeItem)tableitemData;
					if(property.equals(TABLE_PROTOCOL_ATTRIB_COLUMN_PROPERTIES[COLINDEX_ATTRIBVALUE])){
						String strValue = (String)value;
						String displayValue = strValue;
						if(protocolitem.isPasswordAttribute() && !Encoder.isEncoded(strValue)){
							displayValue = getEchoedPassword(strValue);
							strValue = Encoder.encode(strValue);
						}
						protocolitem.setAttributeValue(strValue);
						
						//refresh the UI with the new value
						tablitem.setText(COLINDEX_ATTRIBVALUE, displayValue);
					}
				}
			}
			
		}
		
	}
	
	private static List getAttributes(Protocol protocol){
		List /*<ProtocolAttributeItem>*/attribs = new ArrayList();
		
		EList attributes = protocol.eClass().getEAttributes();
		for(Iterator it = attributes.iterator(); it.hasNext();)
			attribs.add(new EObjectAttributeItem(protocol, (EAttribute)it.next()));
		return attribs;			
	}
	
	public static class ProtocolContentProvider implements IStructuredContentProvider{
		public Object[] getElements(Object inputElement) {
			List children = new ArrayList();
			if(inputElement instanceof Protocol){
				Protocol protocol = (Protocol)inputElement;
				children = getAttributes(protocol);
			}
			return children.toArray();
		}

		public void dispose() {}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			//if(oldInput != newInput)
				viewer.refresh();
		}		
	}

	public static class ProtocolLabelProvider extends LabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if(element instanceof EObjectAttributeItem){
				EObjectAttributeItem protocolItem = (EObjectAttributeItem)element;
				if(columnIndex == COLINDEX_ATTRIBNAME){
					return protocolItem.getAttributeName();
				}
				else if(columnIndex == COLINDEX_ATTRIBVALUE){
					if(protocolItem.isAttributeValueSet()){		
						String value = protocolItem.getAttributeValue().toString();
						//check to see if this is the password field
						if(protocolItem.isPasswordAttribute() && value != null && Encoder.isEncoded(value)){
							String decodedValue = Encoder.decode(value);
							return getEchoedPassword(decodedValue);
						}
						return value;
					}
				}
					
			}
			return ""; //$NON-NLS-1$
		}
		
		public String getText(Object element) {
			return getColumnText(element, COLINDEX_ATTRIBNAME);
		}
	}
	
	private static String getEchoedPassword(String decodedValue) {
		StringBuffer echoedString = new StringBuffer();
		for(int x=0; x<decodedValue.length(); x++){
			echoedString.append(PASSWORD_ECHO);
		}
		return echoedString.toString();
	}		
	
	public static TableViewer createEObjectAttributeTableViewer(Table t, IContentProvider contentProvider){
		TableViewer tv = new TableViewer(t);
		
		CellEditor[] cellEditors = new CellEditor[TABLE_PROTOCOL_ATTRIB_COLUMN_PROPERTIES.length];
		TextCellEditor textCellEditor = new TextCellEditor(t);
		cellEditors[COLINDEX_ATTRIBVALUE] = textCellEditor;		
		tv.setCellEditors(cellEditors);

		ProtocolAttributeCellModifier cellModifier = new ProtocolAttributeCellModifier();
		tv.setCellModifier(cellModifier);
		cellModifier.setTextCellEditor(textCellEditor);
		tv.setColumnProperties(TABLE_PROTOCOL_ATTRIB_COLUMN_PROPERTIES);
		tv.setContentProvider(contentProvider);
		tv.setLabelProvider(new ProtocolLabelProvider());
		
		return tv;			
	}
	
	public static TableViewer createProtocolAttributeTableViewer(Table t){
		return createEObjectAttributeTableViewer(t, new ProtocolContentProvider());
	}

	public boolean canModify(Object element, String property) {
		return false;
	}

	public Object getValue(Object element, String property) {
		return null;
	}

	public void modify(Object element, String property, Object value) {
	}
}
