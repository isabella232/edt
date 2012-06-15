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
		if(this.childTypes){ 
			this._checkChildType(child);          
		}		
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
	"_checkChildType" : function(child) {
		for(var n=0;n<this.childTypes.length;n++){
			if ( child.eze$$package+"."+child.eze$$typename == this.childTypes[n]) {
				return;
			} 
		}
		throw this.eze$$typename+".addChild(child="+child.eze$$typename +"): Invalid child type";
	},	
	"appendChild" : function( child, containerWidget ) {
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
			if(children){
				for (var n=0; n< children.length; n++) {
					this._appendChild( children[n], dojoWidget );
				}
			}			 
		}
		this.children = children;
	},
	"getChildren" : function() {
		return this.children;
	},
	"_removeChild" : function( child, containerWidget ) {
		var dojoWidget = this._getContainerWidget(containerWidget);
		if(!(dojoWidget.domNode))
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
