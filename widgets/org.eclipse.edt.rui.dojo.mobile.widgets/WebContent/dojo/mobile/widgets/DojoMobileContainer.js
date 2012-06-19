////////////////////////////////////////////////////////////////////////////////
// This sample is provided AS IS.
// Permission to use, copy and modify this software for any purpose and
// without fee is hereby granted. provided that the name of IBM not be used in
// advertising or publicity pertaining to distribution of the software without
// specific written permission.
//
// IBM DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SAMPLE, INCLUDING ALL
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL IBM
// BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY
// DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER
// IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING
// OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SAMPLE.
////////////////////////////////////////////////////////////////////////////////

egl.defineWidget(
	'dojo.mobile.widgets', 'DojoMobileContainer',
	'dojo.mobile.widgets', 'DojoMobileBase',
	'div',
{
	
	"_appendChild" : function( child, containerWidget ) {
		var dojoWidget = this._getContainerWidget(containerWidget);
		var thisNode = this.eze$$DOMElement;
		if (!dojoWidget)
			throw egl.createRuntimeException( "Can't append child to this dojoWidget. The dojoWidget has not been created.", [thisNode.tagName]);
		thisNode = dojoWidget.domNode;
		if (!child)
			throw egl.createRuntimeException( "Can't append a null child.", [thisNode.tagName]);
		if (child == this)
			throw egl.createRuntimeException( "Can't append the widget itself as a child.", [thisNode.tagName]);
		
		/* @Smyle: Depreciated
		if(this.childTypes){ 
			this._checkChildType(child);          
		}
		 */
		
		var childNode = child.eze$$DOMElement;
		if (!childNode)
			throw egl.createRuntimeException( "The child node does not exist.", [thisNode.tagName]);
		try {
			child.eze$$parent = this;
		}
		catch (e) {
			throw egl.createRuntimeException( "Append child error.", [child.getTagName(), egl.inferSignature(this), this]);
		}
		thisNode.appendChild( childNode );			
		this.childrenChanged();
	},
	"checkChildrenType" : function( childToDetect ){
		var _this = this;
		var returnResult = true;
		if( 
			!childToDetect && _this.children && _this.children.length > 0 
			&& _this.acceptChildrenTypes 
		)
			for( var i = 0; i < _this.children.length; ++ i )
			{
				var child = _this.children[i];
				if( 
					(child.eze$$package+"."+child.eze$$typename) in _this.acceptChildrenTypes 
					&& _this.acceptChildrenTypes[(child.eze$$package+"."+child.eze$$typename)] 
				)
					continue;
				else{
					var widgetId = _this.id || (_this.dojoWidget && _this.dojoWidget.id);
					var childId = child.id || (child.dojoWidget && child.dojoWidget.id);
					widgetId = widgetId ? (widgetId+" ") : "";
					childId  = childId ? (childId+" ") : "";
					_this.log(
						"<b>Warning: </b>"
						+ _this.eze$$package+"."+_this.eze$$typename + 
						" " +   widgetId +
						"don't support child " + childId + "of type " + child.eze$$package+"."+child.eze$$typename
						+ "<br>"
					);
					returnResult = false;
					_this.children.splice( i, 1 );
					--i;
				}
			}
		else if( childToDetect && _this.acceptChildrenTypes ){
			if( 
				(childToDetect.eze$$package+"."+childToDetect.eze$$typename) in _this.acceptChildrenTypes 
				&& _this.acceptChildrenTypes[(childToDetect.eze$$package+"."+childToDetect.eze$$typename)] 
			)
				returnResult = true;
			else {
				var widgetId = _this.id || (_this.dojoWidget && _this.dojoWidget.id);
				var childToDetectId = childToDetect.id || (childToDetect.dojoWidget && childToDetect.dojoWidget.id);
				widgetId = widgetId ? ( widgetId + " " ) : "";
				childToDetectId  = childToDetectId ? (childToDetectId + " ") : "";
				_this.log(
					"<b>Warning: </b>"
					+ _this.eze$$package+"."+_this.eze$$typename + 
					" " +   widgetId +
					"can't contain child " + childToDetectId + "of " + child.eze$$package+"."+child.eze$$typename
					+ "<br>"
				);
				returnResult = false;
			}
		}
		return returnResult;
	},
	"appendChild" : function( child, containerWidget ) {
		if( !this.checkChildrenType(child) )
			return;
		
		var dojoWidget = this._getContainerWidget(containerWidget);
		
		this.children = this.children || [];
		this.children.push(child);
		
		if( dojoWidget ){			
			this._appendChild( child, dojoWidget );
		}
	},
	"appendChildren" : function( children, containerWidget ) {
		var dojoWidget = this._getContainerWidget(containerWidget);
		this.children = this.children || [];
		this.checkChildrenType();
		for(var n=0; n<children.length; n++){				
			this.children.push(children[n]);
			if( dojoWidget ){
				this._appendChild( children[n], dojoWidget );
			} 
		}		
	},
	"setChildren" : function( children, containerWidget ) {
		var dojoWidget = this._getContainerWidget(containerWidget);
		if ( dojoWidget ) {
			this.removeChildren( dojoWidget );
			this.children = children;
			this.checkChildrenType();
			if( children ) {
				for (var n=0; n< children.length; n++) {
					this._appendChild( children[n], dojoWidget );
				}
			}			 
		}
		else{
			this.children = children;
			this.checkChildrenType();
		}
	},
	"getChildren" : function() {
		return this.children;
	},
	"_removeChild" : function( child, containerWidget ) {
		var dojoWidget = this._getContainerWidget(containerWidget);
		if( !(dojoWidget.domNode) )
			alert("no dom");
		
		// fixed some ugly asynchronous initializing problems
		for( var i = 0; i < dojoWidget.domNode.childNodes.length; ++ i )
			if( dojoWidget.domNode.childNodes[i] == child.eze$$DOMElement )
				dojoWidget.domNode.removeChild( child.eze$$DOMElement );
	},
	"removeChild" : function( child, containerWidget ) {
		var dojoWidget = this._getContainerWidget(containerWidget);
		if(!this.children){
			return;
		}
		for (var n=0; n< this.children.length; n++) {
			if(this.children[n] == child){
				this.children.splice( n, 1 );
				if( dojoWidget ){
					this._removeChild( child, dojoWidget );
				}
				return;
			}
		}
		throw this.eze$$typename+".removeChild: the child is not found.";
		
	},
	"removeChildren" : function( containerWidget ) {
		this._removeChildren( 0, containerWidget );
	},
	"_removeChildren" : function( leastChild, containerWidget ) {
		if(!this.children){
			return;
		}
		var dojoWidget = this._getContainerWidget(containerWidget);
		if( dojoWidget && dojoWidget.domNode && dojoWidget.domNode.childNodes.length > leastChild){
			for (var n=0; n< this.children.length; n++) {
				this._removeChild( this.children[n], dojoWidget );
			}			
		}
		this.children = [];
	},
	"_getContainerWidget" : function( maybeContainerWidget ){
		return maybeContainerWidget ? maybeContainerWidget : 
			( this.containerWidget ? this.containerWidget : this.dojoWidget );
	}	
});
