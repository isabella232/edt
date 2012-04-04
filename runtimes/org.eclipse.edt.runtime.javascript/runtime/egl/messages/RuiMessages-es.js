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
// NLS_ENCODING=UTF-8
// NLS_MESSAGEFORMAT_VAR
//
// Guidelines for defining Rich UI messages:  
// (1) DO NOT change the variable, "egl.eze$$rscBundles["RuiMessages"]"
// (2) The general format of a message is:
//          'MessageId'  :   'Message Text: {n}'
// (3) 'MessageId' (eg: CRRUI2007E) is composed of 4 parts:
//        - the first 5 characters is product identification, must be 'CRRUI'  
//        - the sixth character is a digit which identifies the component:
//                '0' -  Others       ( used to be 'E' )
//                '1' -  Widget       ( used to be 'I' )
//                '2' -  Runtime      ( used to be 'R' )
//                '3' -  Service      ( used to be 'S' )
//        - the 7th to 9th characters identifies message number, eg: '001'
//        - the 10th character identifies the message type:
//                'E' - error message
//                'I' - informational message
//                'W' - warning message
// (5)The message text may contain 1 or more message inserts, 
//         eg: {n},  where n is a 0 based number
// (6)Insert the message in alphabetical and numeric sort order  
//    against the MessageId to avoid conflicts which can be 
//    difficult to track at runtime            
//
egl.eze$$rscBundles["RuiMessages"] =
{
		'CRRUI0001E' : 'El nombre InitalUI[{0}]InitialUI est\u00e1 vac\u00edo. La llamada a la funci\u00f3n no puede utilizarse para inicializar el nombre.',		
		'CRRUI0006E' : 'El grupo RadioGroup {0}  contiene un argumento no permitido.',
					
		'CRRUI1001E' : 'Para la variable {0}, {1} no puede establecerse en {2}',
		'CRRUI1010E' : 'Las funciones de arrastrar y soltar son referencias de funciones, no matrices: {0}',
		'CRRUI1020E' : 'Runtime.asDictionary: solamente se pueden convertir manejadores o registros en Diccionarios, no {0}',
		'CRRUI1030E' : 'No se ha podido encontrar una definici\u00f3n para {0}.{1}',
		'CRRUI1050E' : 'La variable {0} debe incluir la palabra \"null\".',
		'CRRUI1051E' : 'El widget no puede a\u00f1adirse a la variable {0}.',
		'CRRUI1055E' : 'El hijo {1} no puede a\u00f1adirse a la variable {2}.',		
		'CRRUI1057E' : 'El widget {0} no puede a\u00f1adirse a s\u00ed mismo.',	
		'CRRUI1058E' : 'El ancestro {1} no puede a\u00f1adirse a la variable {0}. {2} es el ancestro actual.',
		'CRRUI1060E' : 'Se ha intentado establecer el padre para el widget \"{0}\" a {1}:{2}; El padre debe ser un widget',
		'CRRUI1070E' : 'Se ha producido una excepci\u00f3n {0} al procesar la funci\u00f3n de devoluci\u00f3n de llamada. Utilice try...OnException.',
		'CRRUI1071E' : 'No se ha encontrado ning\u00fan manejador de excepciones para la llamada al servicio. A\u00f1ada un manejador de excepciones para la llamada al servicio.',
		'CRRUI1072E' : 'Se ha producido la excepci\u00f3n {0} en la funci\u00f3n de devoluci\u00f3n de llamada de error, codif\u00edquela con try...OnException',		
		'CRRUI1080E' : 'El widget no tiene elemento DOM (modelo de objeto de documento) y no se encuentra en el documento. <br>Los atributos actuales de este widget son:<P>{0}',
		'CRRUI1083E' : 'Se ha producido un error mientras se estaba manejando el evento de navegador {0}.',
		'CRRUI1150E' : 'Un intento de eliminar \"null\" de una variable {0} ha fallado.',		
		'CRRUI1151E' : 'Un intento de eliminar un widget que no tiene elemento DOM (modelo de objeto de documento) de una variable {0} ha fallado.',		
		'CRRUI1155E' : 'El hijo de tipo {1} no ha podido eliminarse de una variable {0}: {2}.',
		'CRRUI1157E' : 'Un widget no puede eliminarse de s\u00ed mismo. El tipo de widget es {0}.',
			
		'CRRUI2002E' : '{1}<br>Ha ocurrido un error en {0}: {2}',
		'CRRUI2004E' : 'El historial {0} contiene un argumento no permitido.',
		'CRRUI2005E' : 'No puede utilizarse una referencia nula.',
		'CRRUI2006E' : '{0}', // The text of this message comes from some other error message.
		'CRRUI2007E' : 'Argumento no permitido: {0}.',
		'CRRUI2009E' : 'No puede crearse una instancia del evento desde un manejador de RUI.',
		'CRRUI2010E' : 'Funci\u00f3n en desuso: {0}.',
		
		'CRRUI2015E' : 'Falta el archivo {0} para RUIPropertiesLibrary {1}',
		'CRRUI2016E' : 'El programa EGL est\u00e1 tardando demasiado',
		'CRRUI2017E' : 'El valor \"{0}\" del tipo {1} no se puede convertir a tipo {2}',
		'CRRUI2018E' : 'Desbordamiento al asignar {0} al tipo {1}',
		'CRRUI2019E' : 'No se puede a\u00f1adir el elemento {0} a la matriz. El tama\u00f1o m\u00e1ximo de la matriz es {1}',
		'CRRUI2020E' : 'Argumento {0} no v\u00e1lido para la funci\u00f3n de matriz setMaxSize()',
		'CRRUI2021E' : 'No se puede recibir la se\u00f1al siguiente de la serie que empieza en el \u00edndice {0}',
		'CRRUI2022E' : 'El \u00edndice {0} est\u00e1 fuera de l\u00edmites para esta matriz. El tama\u00f1o de la matriz es {1}',
		'CRRUI2023E' : 'Se ha utilizado una referencia nula: {0}',
		'CRRUI2024E' : 'Acceso din\u00e1mico a la clave \"{0}\" no es v\u00e1lido en un objeto de tipo {1}',
		'CRRUI2025E' : 'El acceso din\u00e1mico ha fallado: la clave no existe, \"{0}\"',
		'CRRUI2030E' : 'Argumento no v\u00e1lido para XMLLib.convertFromXML. Serie esperada. Recibida \"{0}\"',
		'CRRUI2031E' : 'Se ha producido un error al analizar XML: {0}',
		'CRRUI2032E' : 'El patr\u00f3n de indicaci\u00f3n de la hora {0} no es v\u00e1lido',
		'CRRUI2033E' : 'El valor {0} especificado para la funci\u00f3n de matriz resize() es superior al tama\u00f1o m\u00e1ximo de la matriz, {1}',
		'CRRUI2034E' : 'El tipo, {0}, para el elemento de matriz no es v\u00e1lido',
		'CRRUI2035E' : 'El n\u00famero de dimensiones redimensionadas es mayor que el n\u00famero de dimensiones de la matriz',
		'CRRUI2036E' : 'Error de dominio en la llamada a {0}: el argumento debe estar entre {1} y {2}',
		'CRRUI2037E' : 'Error de coincidencia: no se puede dividir un n\u00famero por 0',
		'CRRUI2038E' : 'Error de dominio en la llamada a {0}: el argumento debe ser mayor que cero',
		'CRRUI2039E' : 'Error de dominio en la llamada a {0}: el argumento debe ser mayor o igual a cero',
		'CRRUI2040E' : 'Error de dominio en la llamada a {0}: el exponente para una base de cero debe ser mayor que cero',
		'CRRUI2041E' : 'Error de dominio en la llamada a {0}: el exponente para una base negativa debe ser un entero',
		'CRRUI2042E' : '\u00cdndices de subserie no v\u00e1lidos {0}:{1}.',
		'CRRUI2050E' : 'Error de coincidencia: abs() - {0} argumentos dados. Se esperaba 0 o 1',
		'CRRUI2051E' : 'Error de coincidencia: add() - {0} argumentos dados. Se esperaban 1 o 2',
		'CRRUI2052E' : 'Error de coincidencia: compareTo() - {0} argumentos dados. Se esperaban 1 o 2',
		'CRRUI2053E' : 'Error de coincidencia: divide() - la escala negativa, {0}, no es v\u00e1lida',
		'CRRUI2054E' : 'Error de coincidencia: divide() - {0} argumentos dados. Se esperaban entre 1 y 3',
		'CRRUI2055E' : 'Error de coincidencia: divideInteger() - {0} argumentos dados. Se esperaban 1 o 2',
		'CRRUI2056E' : 'Error de coincidencia: max() - {0} argumentos dados. Se esperaban 1 o 2',
		'CRRUI2057E' : 'Error de coincidencia: min() - {0} argumentos dados. Se esperaban 1 o 2',
		'CRRUI2058E' : 'Error de coincidencia: multiply() - {0} argumentos dados. Se esperaban 1 o 2',
		'CRRUI2059E' : 'Error de coincidencia: negate() - {0} argumentos dados. Se esperaba 0 o 1',
		'CRRUI2060E' : 'Error de coincidencia: plus() - {0} argumentos dados. Se esperaba 0 o 1',
		'CRRUI2061E' : 'Error de coincidencia: pow() - {0} argumentos dados. Se esperaban 1 o 2',
		'CRRUI2062E' : 'Error de coincidencia: pow() - alimentaci\u00f3n negativa, {0}',
		'CRRUI2063E' : 'Error de coincidencia: pow() - demasiados d\u00edgitos, {0}',
		'CRRUI2064E' : 'Error de coincidencia: remainder() - {0} argumentos dados. Se esperaban 1 o 2',
		'CRRUI2065E' : 'Error de coincidencia: subtract() - {0} argumentos dados. Se esperaban 1 o 2',
		'CRRUI2066E' : 'Error matem\u00e1tico: format() - {0} argumentos dados. Se esperaban 2 o 6',
		'CRRUI2067E' : 'Error de coincidencia: format() - desbordamiento del exponente, {0}',
		'CRRUI2068E' : 'Error de coincidencia: intValueExact() - parte decimal no cero, {0}',
		'CRRUI2069E' : 'Error de coincidencia: intValueExact() - desbordamiento de la conversi\u00f3n, {0}',
		'CRRUI2070E' : 'Error de coincidencia: setScale() - {0} argumentos dados. Se esperaban 1 o 2',
		'CRRUI2071E' : 'Error de coincidencia: setScale() - escala negativa, {0}',
		'CRRUI2072E' : 'Error de coincidencia: intCheck() - error de conversi\u00f3n, {0}',
		'CRRUI2073E' : 'Error de coincidencia: dodivide() - desbordamiento del entero',
		'CRRUI2074E' : 'Error de coincidencia: no se ha podido convertir la serie \"{1}\" en un n\u00famero',
		'CRRUI2075E' : 'Error de coincidencia: El n\u00famero de argumento {0} para el m\u00e9todo {1} no es v\u00e1lido.  El argumento proporcionado es {2}',
		'CRRUI2076E' : 'Error de coincidencia: demasiados d\u00edgitos - {0}',
		'CRRUI2077E' : 'Error de coincidencia: round() - {0} argumentos dados. Se esperaban 1 o 2',
		'CRRUI2078E' : 'Error de coincidencia: round() - es necesario un redondeo',
		'CRRUI2079E' : 'Error de coincidencia: round() - valor de redondeo incorrecto, {0}',
		'CRRUI2080E' : 'Error de coincidencia: round() - exponente, {0}, desbordamientos',
		'CRRUI2081E' : 'Error de coincidencia: finish() - exponente, {0}, desbordamientos',
		'CRRUI2082E' : 'Error interno: al llamar al constructor para {0}',
		'CRRUI2083E' : 'Error interno: se ha producido un problema al definir la clase {0}',
		'CRRUI2084E' : 'Error interno: al definir el widget {0}.{1} como subclase de egl.ui.rui.RUIPropertiesLibrary',
		'CRRUI2085E' : 'Error interno: al definir el widget {0}.{1} como subclase de {2}.{3}',
		'CRRUI2086E' : 'Error interno: se ha producido un problema al definir el manejador de UI enriquecida {0}',
		'CRRUI2087E' : 'Error interno: se ha producido un problema al definir el widget de la interfaz de usuario enriquecida {0}',
		'CRRUI2088E' : 'La interfaz de usuario enriquecida EGL no soporta este navegador',
		'CRRUI2089E' : 'No se ha podido convertir del formato JSON: "{0}", debido a {1}',
		'CRRUI2090E' : 'No se ha podido llamar al servicio: {0}',
		'CRRUI2091E' : 'Se ha producido un error al enviar el evento al IDE de Eclipse: {0}',
		'CRRUI2092E' : 'Error interno: se ha producido un problema al manejar un evento de IDE {0}',
		'CRRUI2093E' : 'Error interno: no se ha podido instrumentar la funci\u00f3n {0}',
		'CRRUI2094E' : 'Estas son las llamadas de funci\u00f3n EGL que han llevado a este error:',
		'CRRUI2095E' : 'No se han podido encontrar las llamadas de funci\u00f3n EGL que han llevado a este error',
		'CRRUI2097E' : 'Valor de estilo CSS no v\u00e1lido "{1}" para atributo {0}',
		'CRRUI2097E' : 'No ha podido analizar correctamente el estilo CSS {0}. Compruebe la sintaxis o utilice una hora de estilo externa.',
		'CRRUI2098E' : 'Se ha producido un error al soportar la opci\u00f3n Arrastrar y soltar: {0}',
		'CRRUI2099E' : 'En una interfaz de usuario enriquecida, no est\u00e1 soportada la operaci\u00f3n "set" EGL en el tipo {0}',
		'CRRUI2100E' : 'Argumento no v\u00e1lido para RuiLib.convertFromXML. Se esperaba una serie. Objeto recibido del tipo {0}',
		'CRRUI2101E' : 'No se puede ubicar indexOf "{1}" dentro de "{0}" debido a {2}',
		'CRRUI2102E' : 'No se ha podido ordenar la matriz',
		'CRRUI2103E' : 'Acceso a "{0}" no permitido en un objeto del tipo "{1}" debido a {2}',
		'CRRUI2104E' : 'No se ha podido analizar la serie JSON "{0}"',
		'CRRUI2105E' : 'Se ha producido un error de an\u00e1lisis de JSON al intentar establecer {0}. El campo no existe en la ubicaci\u00f3n esperada de la serie JSON.',
		'CRRUI2106E' : 'Ha fallado un intento de establecer {0} a nulo al analizar JSON porque no estaba declarado como campo anulable.',
		'CRRUI2107E' : 'Las funciones de conversi\u00f3n JSON de la biblioteca de servicio operan en un Registro o Diccionario. {0} no es un tipo soportado.',
		'CRRUI2108E' : 'Las funciones de conversi\u00f3n XML de la biblioteca XML operan en un Registro. {0} no es un tipo soportado.',
		
		'CRRUI2111E' : 'Error de MathContext(): El n\u00famero de argumentos especificado, {0}, no es v\u00e1lido; se esperaba de 1 a 4.',
		'CRRUI2112E' : 'Error de MathContext(): El n\u00famero de d\u00edgitos especificado, {0}, es demasiado peque\u00f1o.',
		'CRRUI2113E' : 'Error de MathContext(): Es n\u00famero de d\u00edgitos especificado, {0}, es demasiado grande.',
		'CRRUI2114E' : 'Error de MathContext(): El valor de formulario especificado, {0}, no es v\u00e1lido.',
		'CRRUI2115E' : 'Error de MathContext(): El valor de modalidad de redondeo especificado, {0}, no es v\u00e1lido.',
		
	    'CRRUI2700E' : 'No se ha recibido ninguna entrada para el campo necesario; vuelva a realizar la entrada.',
		'CRRUI2702E' : 'Error de tipo de datos en la entrada; vuelva a realizar la entrada.',
		'CRRUI2703E' : 'Se ha sobrepasado el n\u00famero de d\u00edgitos significativos permitidos; vuelva a realizar la entrada.',
		'CRRUI2704E' : 'La entrada no est\u00e1 en el rango definido de {0} a {1} - vuelva a realizar la entrada.',
		'CRRUI2705E' : 'Error de longitud m\u00ednima de entrada; vuelva a realizar la entrada.',
		'CRRUI2707E' : 'Error de comprobaci\u00f3n de m\u00f3dulo en la entrada; vuelva a realizar la entrada.',
		'CRRUI2708E' : 'La entrada no es v\u00e1lida para el formato de fecha u hora definido {0}.',
		'CRRUI2710E' : 'La entrada no es v\u00e1lida para el campo booleano.',
		'CRRUI2712E' : 'Los datos hexadecimales no son v\u00e1lidos.',
		'CRRUI2713E' : 'El valor entrado no es v\u00e1lido y no coincide con el patr\u00f3n establecido.',		
		'CRRUI2716E' : 'La entrada no est\u00e1 en la lista definida de valores v\u00e1lidos: vuelva a realizar la entrada.',
		'CRRUI2717E' : 'El formato de fecha y hora especificado de {0} no es v\u00e1lido.',	
		'CRRUI2719E' : 'Error al analizar el valor de entrada.',

		'CRRUI3650E' : 'No se ha podido encontrar el archivo descriptor de despliegue : \'{0}\'',
		'CRRUI3651E' : 'La clave de enlace del servicio : \'{0}\' no existe en el descriptor de despliegue \'{1}\'',
		'CRRUI3652E' : 'Tipo de enlace del servicio incorrecto \'{0}\', tipo de enlace esperado \'{1}\'',
		'CRRUI3653E' : 'Se ha producido un error al intentar invocar el servicio rest en \'{0}\'',
		'CRRUI3654E' : 'Se ha producido un error al construir el objeto de solicitud: \'{0}\'',
		'CRRUI3655E' : 'Se ha producido un error al procesar el objeto de respuesta: \'{0}\'',
		'CRRUI3656E' : '\'formdata\' no est\u00e1 soportado como formato de respuesta',
		'CRRUI3657E' : 'No se ha podido encontrar el proxy en \'{0}\' para hacer las llamadas de servicio',
		'CRRUI3658E' : 'Se ha producido un error en el proxy en \'{0}\' al intentar invocar el servicio en \'{1}\'',
		'CRRUI3659E' : 'El an\u00e1lisis de respuesta Json ha resultado nulo, la respuesta original es: \'{0}\'',
		'CRRUI3660E' : 'Se ha producido una excepci\u00f3n, no se ha podido manejar la respuesta para \'{0}\', raz\u00f3n: \'{1}\'',
		'CRRUI3661E' : 'Falta la informaci\u00f3n de enlace de servicio para \'{0}\' al intentar invocar la funci\u00f3n de servicio \'{1}\'.'
};

