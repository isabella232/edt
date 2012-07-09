/*******************************************************************************
 * Copyright � 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

/**
 * Synchronor is designed for synchronous usage where egl widgets & dojo widgets are mostly loaded 
 * asynchronously.
 * @author Smyle, IBM Corporation
 * @todo deadlock avoiding mechanism
 */
define(
	[
	 	"dojo/_base/declare"
	 ],
	function( declare ){
		declare(
				"SynMapItem", null,
				{
					 'increment' : function(){
					 	 if( !this._incremented && this._synitem ){
				 			++ this._synitem._current;
				 			this._incremented = true;
					 	 }
					 },
					 'trigger' : function(){
						 this._synitem && this._synitem.trigger();
					 },
					 'constructor' : function( options ){
						 this._incremented = false;
						 this._synitem = null;
						 if( options['SynItem'] ){
							 this._synitem = options['SynItem'];
						 }
						 else
							 console.error( "ERROR: Options for synchronor map item is not complete." );
					 }
				}
		);
		
		declare(
			"SynItem", null,
			{
			   	'trigger' : function(){
			   		  if( !this._triggered && this._expected === this._current ){
			   			  this._callback();
			   			  this._triggered = true;
			   		  }
			   	  },
				'constructor' : function( options ){
					this._current   = 0;
					this._expected  = -1;
					this._callback  = null;
					this._flag      = '';
					this._triggered = false;
					
					if( options['current'] != undefined && options['expected'] != undefined
					&& options['callback'] && options['flag'] ){
						this._current  = options['current'];
						this._expected = options['expected'];
						this._callback = options['callback'];
						this._flag 	   = options['flag'];
					}
					else
						console.error( 'ERROR: Options for synchronor item is not complete.' );
				}
			}
		);
		 
		declare(
			"Synchronor", null,
			{
				'constructor' : function(){
					this._synID  = 0;
					this._synmap = {};
				},
				'wait' : function( obj, flag, callback ){
					if( !obj || !obj.length || (obj.length && obj.length == 0 ) ){
						callback();
						return;
					}
					var expected = obj.length;
					var curr = 0;
					var synitem = new SynItem({
						'current'   : curr,
						'expected'  : expected,
						'flag'		: flag,
						'callback'  : callback
					});
					
					for( var i = 0; i < obj.length; ++ i ){
						if( !obj[i] || ! (obj[i] instanceof egl.dojo.widgets.DojoBase) ){
							synitem._current++;
							continue;
						}
						
						if(  !obj[i]['__SYNID__'] )
							obj[i]['__SYNID__'] = ++ this._synID;
						
						if( this._synmap[obj[i]['__SYNID__']] )
							this._synmap[obj[i]['__SYNID__']].push(
								new SynMapItem({ 'SynItem' : synitem }) 
							);
						else
							this._synmap[obj[i]['__SYNID__']] = 
								[ new SynMapItem({'SynItem' : synitem}) ];
							
						if( obj[i][flag] )
							synitem._current ++;
						else
							obj[i][flag] = false;
					}
					
					synitem.trigger();
				},
				'trigger' : function( obj, flag ){
					if( obj && flag ) obj[flag] = true;
					if( obj['__SYNID__'] ){
						var synMapitems = this._synmap[ obj['__SYNID__'] ];
						if( synMapitems && synMapitems.length ){
							for( var i = 0; i < synMapitems.length; ++ i ){
								var synMapitem = synMapitems[i];
								// trigger specified flag callback
								if( flag ){
									if( flag == synMapitem._synitem['_flag'] ){
										synMapitem.increment();
										synMapitem.trigger();
									}
								}
								// trigger all related callbacks
								else{
									if( obj[synMapitem._synitem['_flag']] ){
										synMapitem.increment();
										synMapitem.trigger();
									}
								}
							}
						}
					}
				}
			}
		);
		var _syn = new Synchronor()
		return _syn;
	}		
);