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

/**
 * A general solution to the display:none problem which usually 
 * makes dojo widgets' initialization fail 
 * @author Smyle, IBM Corporation
 */

define(
	[ "dojo/_base/declare" ], 
	function( declare  ) {
		declare( 
			"DisplayNoneController", null,
			{
				/**
				 * @param nodeToCheck HTMLDomElement,
				 * @param objToStoreResult JSONObject, optional, if leave it default, no result will be stored
				 * @param isResultStore Boolean, optional, default true
				 * @return Boolean, return true if find display:none element causing offset height to 0, 
				 * otherwise return false
				 */
				"checkOffsetHeightZeroForDisplayNone" : function(
					nodeToCheck, objToStoreResult, isResultToStore
				){
					if( isResultToStore == undefined || isResultToStore == null ) isResultToStore = true;
					if( objToStoreResult == undefined ) objToStoreResult = null;
					
					var currentNode = nodeToCheck;
					var result = {};
					var toStoreResult = isResultToStore ?  (objToStoreResult ? true : false ) : false; 
					var hierachy = 0;
					
					if( currentNode && currentNode.offsetHeight != 0 ) return false;
					
					while(
						nodeToCheck.offsetHeight == 0 
						&& currentNode 
					){
						if( currentNode.style.display == "none" ){
							if( toStoreResult )
								result[hierachy+""] = {
									"node" : currentNode,
									"originalVisiblity" : currentNode.style.visiblity ? currentNode.style.visiblity : "visible"
								};
							currentNode.style.visiblity = "hidden";
							currentNode.style.display = "";
							++ hierachy;
							
							if( currentNode.offsetHeight == 0 )
								currentNode = currentNode.parentElement;
							else{
								if( toStoreResult ) objToStoreResult[ "__CHECK_DISPLAY_NONE__" ] = result;
								for( var key in result ){
									result[key]["node"].style.display = "none";
									result[key]["node"].style.visiblity = result[key]["originalVisiblity"];
								}
								return true
							}
						}
						else
							++ hierachy, currentNode = currentNode.parentElement;
					}
					return false;
				},
				"makeVisiblityHidden" : function( storeObj ){
					if( !storeObj["__CHECK_DISPLAY_NONE__"] ) return;
					for( var key in storeObj["__CHECK_DISPLAY_NONE__"] ){
						if( storeObj["__CHECK_DISPLAY_NONE__"][key]["node"] ){
							storeObj["__CHECK_DISPLAY_NONE__"][key]["node"].style.visiblity = "hidden";
							storeObj["__CHECK_DISPLAY_NONE__"][key]["node"].style.display = "";
						}
					}
				},
				"makeDisplayNone" : function( storeObj ){
					if( !storeObj["__CHECK_DISPLAY_NONE__"] ) return;
					for( var key in storeObj["__CHECK_DISPLAY_NONE__"] ){
						if( storeObj["__CHECK_DISPLAY_NONE__"][key]["node"] ){
							storeObj["__CHECK_DISPLAY_NONE__"][key]["node"].style.display = "none";
							storeObj["__CHECK_DISPLAY_NONE__"][key]["node"].style.visiblity = storeObj["__CHECK_DISPLAY_NONE__"][key]["originalVisiblity"];
						}
					}
				}
			}
		);
		var _dnc = new DisplayNoneController();
		return _dnc;
	}
);