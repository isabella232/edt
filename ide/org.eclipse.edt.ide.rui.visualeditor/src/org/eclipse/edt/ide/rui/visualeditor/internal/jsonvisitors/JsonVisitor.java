/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.jsonvisitors;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.javart.json.NameValuePairNode;
import org.eclipse.edt.javart.json.ObjectNode;

/**
 * An ObjectNode visitor that is only interested in the following child objects:
 * <ul>
 * <li>NameValuePairNode</li>
 * <li>ObjectNode</li>
 * </ul>
 * 
 * Sample hierarchy (DojoGridSample.egl) using the print method of the super abstract class.
 * <code>
 * 
 * {"0":{"ele":{"type":"VBox","height":110,"width":969,"offset":795,"len":94,"x":0,"y":0},"children":{"0":{"ele":{"type":"TextLabel","height":31,"width":290,"offset":855,"len":4,"x":21,"y":21}},"1":{"ele":{"type":"Button","height":36,"width":142,"offset":864,"len":6,"x":21,"y":52}},"2":{"ele":{"type":"HBox","height":0,"width":290,"offset":875,"len":8,"x":21,"y":88}}}}}
 * 
 * ObjectNode {0 : {ele : {type : VBox, height : 110, width : 969, offset : 795, len : 94, x : 0, y : 0}, children : {0 : {ele : {type : TextLabel, height : 31, width : 290, offset : 855, len : 4, x : 21, y : 21}}, 1 : {ele : {type : Button, height : 36, width : 142, offset : 864, len : 6, x : 21, y : 52}}, 2 : {ele : {type : HBox, height : 0, width : 290, offset : 875, len : 8, x : 21, y : 88}}}}}
 *   NameValuePairNode
 *       StringNode 0
 *       ObjectNode {ele : {type : VBox, height : 110, width : 969, offset : 795, len : 94, x : 0, y : 0}, children : {0 : {ele : {type : TextLabel, height : 31, width : 290, offset : 855, len : 4, x : 21, y : 21}}, 1 : {ele : {type : Button, height : 36, width : 142, offset : 864, len : 6, x : 21, y : 52}}, 2 : {ele : {type : HBox, height : 0, width : 290, offset : 875, len : 8, x : 21, y : 88}}}}
 *           NameValuePairNode
 *               StringNode ele
 *               ObjectNode {type : VBox, height : 110, width : 969, offset : 795, len : 94, x : 0, y : 0}
 *                   NameValuePairNode
 *                     StringNode type
 *                     StringNode VBox
 *                   NameValuePairNode
 *                     StringNode height
 *                     IntegerNode 110
 *                   NameValuePairNode
 *                     StringNode width
 *                     IntegerNode 969
 *                   NameValuePairNode
 *                     StringNode offset
 *                     IntegerNode 795
 *                   NameValuePairNode
 *                     StringNode len
 *                     IntegerNode 94
 *                   NameValuePairNode
 *                     StringNode x
 *                     IntegerNode 0
 *                   NameValuePairNode
 *                     StringNode y
 *                     IntegerNode 0
 *           NameValuePairNode
 *               StringNode children
 *               ObjectNode {0 : {ele : {type : TextLabel, height : 31, width : 290, offset : 855, len : 4, x : 21, y : 21}}, 1 : {ele : {type : Button, height : 36, width : 142, offset : 864, len : 6, x : 21, y : 52}}, 2 : {ele : {type : HBox, height : 0, width : 290, offset : 875, len : 8, x : 21, y : 88}}}
 *                 NameValuePairNode
 *                   StringNode 0
 *                   ObjectNode {ele : {type : TextLabel, height : 31, width : 290, offset : 855, len : 4, x : 21, y : 21}}
 *                       NameValuePairNode
 *                           StringNode ele
 *                           ObjectNode {type : TextLabel, height : 31, width : 290, offset : 855, len : 4, x : 21, y : 21}
 *                               NameValuePairNode
 *                                 StringNode type
 *                                 StringNode TextLabel
 *                               NameValuePairNode
 *                                 StringNode height
 *                                 IntegerNode 31
 *                               NameValuePairNode
 *                                 StringNode width
 *                                 IntegerNode 290
 *                               NameValuePairNode
 *                                 StringNode offset
 *                                 IntegerNode 855
 *                               NameValuePairNode
 *                                 StringNode len
 *                                 IntegerNode 4
 *                               NameValuePairNode
 *                                 StringNode x
 *                                 IntegerNode 21
 *                               NameValuePairNode
 *                                 StringNode y
 *                                 IntegerNode 21
 *                 NameValuePairNode
 *                   StringNode 1
 *                   ObjectNode {ele : {type : Button, height : 36, width : 142, offset : 864, len : 6, x : 21, y : 52}}
 *                       NameValuePairNode
 *                           StringNode ele
 *                           ObjectNode {type : Button, height : 36, width : 142, offset : 864, len : 6, x : 21, y : 52}
 *                               NameValuePairNode
 *                                 StringNode type
 *                                 StringNode Button
 *                               NameValuePairNode
 *                                 StringNode height
 *                                 IntegerNode 36
 *                               NameValuePairNode
 *                                 StringNode width
 *                                 IntegerNode 142
 *                               NameValuePairNode
 *                                 StringNode offset
 *                                 IntegerNode 864
 *                               NameValuePairNode
 *                                 StringNode len
 *                                 IntegerNode 6
 *                               NameValuePairNode
 *                                 StringNode x
 *                                 IntegerNode 21
 *                               NameValuePairNode
 *                                 StringNode y
 *                                 IntegerNode 52
 *                 NameValuePairNode
 *                   StringNode 2
 *                   ObjectNode {ele : {type : HBox, height : 0, width : 290, offset : 875, len : 8, x : 21, y : 88}}
 *                       NameValuePairNode
 *                           StringNode ele
 *                           ObjectNode {type : HBox, height : 0, width : 290, offset : 875, len : 8, x : 21, y : 88}
 *                               NameValuePairNode
 *                                 StringNode type
 *                                 StringNode HBox
 *                               NameValuePairNode
 *                                 StringNode height
 *                                 IntegerNode 0
 *                               NameValuePairNode
 *                                 StringNode width
 *                                 IntegerNode 290
 *                               NameValuePairNode
 *                                 StringNode offset
 *                                 IntegerNode 875
 *                               NameValuePairNode
 *                                 StringNode len
 *                                 IntegerNode 8
 *                               NameValuePairNode
 *                                 StringNode x
 *                                 IntegerNode 21
 *                               NameValuePairNode
 *                                 StringNode y
 *                                 IntegerNode 88
 * </code>
 */
public class JsonVisitor extends JsonVisitorAbstract {
	protected WidgetPart	_widgetPart		= null;

	/**
	 * Main constructor called by the widget manager. 
	 */
	public JsonVisitor( WidgetPart widgetPart ) {
		_widgetPart = widgetPart;
	}
	
	/**
	 * Constructor called by the value pair node visitor
	 */
	public JsonVisitor( WidgetPart widgetPart, int iIndentation ){
		super( iIndentation );
		
		_widgetPart = widgetPart;
	}

	/**
	 * Here for debug purposes 
	 */
	public void endVisit(NameValuePairNode object) {
		print( "endVisit NameValuePairNode" );
	}

	/**
	 * Here for debug purposes 
	 */
	public void endVisit(ObjectNode object) {
		print( "endVisit ObjectNode" );
	}

	/**
	 * Creates a name value pair visitor that visits the children of a name value pair.
	 */
	public boolean visit( NameValuePairNode pair ) throws RuntimeException {
		print( "visit NameValuePairNode" );
		
		pair.visitChildren( new JsonVisitorNameValuePair( _widgetPart, super._iIndentation + 1 ) );
		
		return false;
	}

	/**
	 * Returns true to ensure that child object nodes are visited.
	 */
	public boolean visit( ObjectNode objectNode ) throws RuntimeException {
		print( "visit ObjectNode " + objectNode.toJava() );
		return true;
	}
}

