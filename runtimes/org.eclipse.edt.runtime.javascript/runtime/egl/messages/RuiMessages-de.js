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
		'CRRUI0001E' : 'Der InitalUI[{0}]InitialUI-Name ist leer. Der Funktionsaufruf kann zum Initialisieren des Namens nicht verwendet werden.',		
		'CRRUI0006E' : 'Die Gruppe {0} RadioGroup enth\u00e4lt ein unzul\u00e4ssiges Argument.',
					
		'CRRUI1001E' : 'F\u00fcr die Variable {0} kann {1} nicht auf {2} gesetzt werden.',
		'CRRUI1010E' : 'Drag-and-drop-Funktionen sind Funktionsverweise, keine Array: {0}',
		'CRRUI1020E' : 'Runtime.asDictionary: Kann nur Handler oder Datens\u00e4tze in W\u00f6rterverzeichnisse konvertieren, nicht {0}.',
		'CRRUI1030E' : 'Keine Definition f\u00fcr {0}.{1} gefunden.',
		'CRRUI1050E' : 'Die Variable {0} muss das Wort \"null\" enthalten.',
		'CRRUI1051E' : 'Das Widget kann der Variablen {0} nicht hinzugef\u00fcgt werden.',
		'CRRUI1055E' : 'Das untergeordnete Element {1} kann der Variablen {0} nicht hinzugef\u00fcgt werden.',		
		'CRRUI1057E' : 'Das Widget {0} kann nicht sich selbst hinzugef\u00fcgt werden.',	
		'CRRUI1058E' : 'Das \u00fcbergeordnete Element {1} kann der Variablen {0} nicht hinzugef\u00fcgt werden. {2} ist das aktuelle \u00fcbergeordnete Element.',
		'CRRUI1060E' : 'Versuch, \u00fcbergeordnetes Element f\u00fcr Widget \"{0}\" auf{1}:{2} zu setzen. \u00dcbergeordnetes Element muss ein Widget sein.',
		'CRRUI1070E' : 'Die Ausnahmebedingung {0} trat bei der Verarbeitung der Callback-Funktion auf. Verwenden Sie try...OnException.',
		'CRRUI1071E' : 'Kein Ausnahmebedingungshandler f\u00fcr Serviceaufruf gefunden. F\u00fcgen Sie einen Ausnahmebedingungshandler f\u00fcr Serviceaufruf hinzu.',
		'CRRUI1072E' : 'Die Ausnahmebedingung {0} trat in der Fehlerr\u00fcckruffunktion (error call back function) auf; codieren Sie mit try...OnException',		
		'CRRUI1080E' : 'Das Widget verf\u00fcgt \u00fcber kein DOM-Element und ist nicht im Dokument vorhanden.<br>Die aktuellen Attribute dieses Widgets sind Folgende:<P>{0}',
		'CRRUI1083E' : 'Bei der Bearbeitung des Browserereignisses {0} trat ein Fehler auf.',
		'CRRUI1150E' : 'Der Versuch, \"null\" aus {0} zu entfernen, schlug fehl.',		
		'CRRUI1151E' : 'Der Versuch, das Widget, das kein DOM-Element (document object model) von einer Variable {0} hat, zu entfernen, schlug fehl.',		
		'CRRUI1155E' : 'Das untergeordnete Element des Typs {1} kann nicht aus {0} entfernt werden: Variable {2}.',
		'CRRUI1157E' : 'Ein Widget kann nicht von sich selbst entfernt werden. Der Widget-Typ ist {0}.',
			
		'CRRUI2002E' : '{1}<br>Innerhalb von {0} trat ein Fehler auf: {2}',
		'CRRUI2004E' : 'Das Protokoll {0} enth\u00e4lt ein unzul\u00e4ssiges Argument.',
		'CRRUI2005E' : 'Ein Nullverweis kann nicht verwendet werden.',
		'CRRUI2006E' : '{0}', // The text of this message comes from some other error message.
		'CRRUI2007E' : 'Unzul\u00e4ssiges Argument: {0}.',
		'CRRUI2009E' : 'Ereignis kann nicht von einem RUI-Handler instanziiert werden.',
		'CRRUI2010E' : 'Nicht mehr unterst\u00fctzte Funktion: {0}.',
		
		'CRRUI2015E' : 'Die Datei {0} f\u00fcr RUIPropertiesLibrary {1} fehlt.',
		'CRRUI2016E' : 'Das EGL-Programm ben\u00f6tigt zu viel Zeit.',
		'CRRUI2017E' : 'Der Wert \"{0}\" vom Typ {1} kann nicht in den Typ {2} konvertiert werden.',
		'CRRUI2018E' : '\u00dcberlauf bei Zuordnung von {0} zu Typ {1}',
		'CRRUI2019E' : 'Element {0} kann nicht an den Array angeh\u00e4ngt werden. Die maximale Gr\u00f6\u00dfe des Array ist {1}',
		'CRRUI2020E' : 'Ung\u00fcltiges Argument {0} f\u00fcr die Array-Funktion setMaxSize()',
		'CRRUI2021E' : 'Das n\u00e4chste Token kann von der Zeichenfolge nicht abgerufen werden, die bei Index {0} startet',
		'CRRUI2022E' : 'Der Index {0} liegt au\u00dferhalb des g\u00fcltigen Bereichs f\u00fcr diese Feldgruppe. Die Array-Gr\u00f6\u00dfe ist {1}.',
		'CRRUI2023E' : 'Ein Nullverweis wurde verwendet: {0}',
		'CRRUI2024E' : 'Der dynamische Zugriff auf den Schl\u00fcssel \"{0}\" ist bei einem Objekt des Typs {1} ung\u00fcltig.',
		'CRRUI2025E' : 'Der dynamische Zugriff ist fehlgeschlagen: Es gibt keinen Schl\u00fcssel \"{0}\".',
		'CRRUI2030E' : 'Ung\u00fcltiges Argument f\u00fcr XMLLib.convertFromXML. Erwartet wurde eine Zeichenfolge. Empfangen wurde \"{0}\".',
		'CRRUI2031E' : 'Fehler beim Parsing von XML: {0}',
		'CRRUI2032E' : 'Das Zeitmarkenmuster {0} ist ung\u00fcltig.',
		'CRRUI2033E' : 'Der Wert {0}, der f\u00fcr die Array-Funktion resize() angegeben wurde, ist gr\u00f6\u00dfer als die maximale Gr\u00f6\u00dfe des Arrays, {1}',
		'CRRUI2034E' : 'Der Typ {0} f\u00fcr das Feldgruppenelement ist ung\u00fcltig.',
		'CRRUI2035E' : 'Die Anzahl der Dimensionen mit ge\u00e4nderter Gr\u00f6\u00dfe \u00fcberschreitet die Anzahl der Dimensionen der Feldgruppe.',
		'CRRUI2036E' : 'Dom\u00e4nenfehler im Aufruf von {0}: Das Argument muss zwischen {1} und {2} liegen.',
		'CRRUI2037E' : 'Mathematischer Fehler: Division einer Zahl durch 0 ist nicht m\u00f6glich.',
		'CRRUI2038E' : 'Dom\u00e4nenfehler im Aufruf von {0}: Das Argument muss gr\u00f6\u00dfer als Null sein.',
		'CRRUI2039E' : 'Dom\u00e4nenfehler im Aufruf von {0}: Das Argument muss gr\u00f6\u00dfer-gleich Null sein.',
		'CRRUI2040E' : 'Dom\u00e4nenfehler im Aufruf von {0}: Der Exponent f\u00fcr die Basis Null muss gr\u00f6\u00dfer als Null sein.',
		'CRRUI2041E' : 'Dom\u00e4nenfehler im Aufruf von {0}: Der Exponent f\u00fcr eine negative Basis muss eine ganze Zahl sein.',
		'CRRUI2042E' : 'Ung\u00fcltige Substring-Indizes {0}:{1}.',
		'CRRUI2050E' : 'Mathematischer Fehler: abs() - {0} Argumente angegeben. Erwartet wurde 0 oder 1.',
		'CRRUI2051E' : 'Mathematischer Fehler: add() - {0} Argumente angegeben. Erwartet wurden 1 oder 2.',
		'CRRUI2052E' : 'Mathematischer Fehler: compareTo() - {0} Argumente angegeben. Erwartet wurden 1 oder 2.',
		'CRRUI2053E' : 'Mathematischer Fehler: divide() - Negative Nachkommastelle - {0} - ist ung\u00fcltig.',
		'CRRUI2054E' : 'Mathematischer Fehler: divide() - {0} Argumente angegeben. Erwartet wurde ein Wert zwischen 1 und 3.',
		'CRRUI2055E' : 'Mathematischer Fehler: divideInteger() - {0} Argumente angegeben. Erwartet wurden 1 oder 2.',
		'CRRUI2056E' : 'Mathematischer Fehler: max() - {0} Argumente angegeben. Erwartet wurden 1 oder 2.',
		'CRRUI2057E' : 'Mathematischer Fehler: min() - {0} Argumente angegeben. Erwartet wurden 1 oder 2.',
		'CRRUI2058E' : 'Mathematischer Fehler: multiply() - {0} Argumente angegeben. Erwartet wurden 1 oder 2.',
		'CRRUI2059E' : 'Mathematischer Fehler: negate() - {0} Argumente angegeben. Erwartet wurde 0 oder 1.',
		'CRRUI2060E' : 'Mathematischer Fehler: plus() - {0} Argumente angegeben. Erwartet wurde 0 oder 1.',
		'CRRUI2061E' : 'Mathematischer Fehler: pow() - {0} Argumente angegeben. Erwartet wurden 1 oder 2.',
		'CRRUI2062E' : 'Mathematischer Fehler: pow() - Negative Potenz {0}.',
		'CRRUI2063E' : 'Mathematischer Fehler: pow() - Zu viele Ziffern: {0}.',
		'CRRUI2064E' : 'Mathematischer Fehler: remainder() - {0} Argumente angegeben. Erwartet wurden 1 oder 2.',
		'CRRUI2065E' : 'Mathematischer Fehler: subtract() - {0} Argumente angegeben. Erwartet wurden 1 oder 2.',
		'CRRUI2066E' : 'Mathematischer Fehler: format() - {0} Argumente angegeben. Erwartet wurden 2 oder 6.',
		'CRRUI2067E' : 'Mathematischer Fehler: format() - Exponenten\u00fcberlauf: {0}',
		'CRRUI2068E' : 'Mathematischer Fehler: intValueExact() - Dezimalteil ist ungleich null: {0}',
		'CRRUI2069E' : 'Mathematischer Fehler: intValueExact() - Konvertierungs\u00fcberlauf: {0}',
		'CRRUI2070E' : 'Mathematischer Fehler: setScale() - {0} Argumente angegeben. Erwartet wurden 1 oder 2.',
		'CRRUI2071E' : 'Mathematischer Fehler: setScale() - Negative Nachkommastelle {0}',
		'CRRUI2072E' : 'Mathematischer Fehler: intCheck() - Konvertierungsfehler: {0}',
		'CRRUI2073E' : 'Mathematischer Fehler: dodivide() - Integer\u00fcberlauf: {0}',
		'CRRUI2074E' : 'Mathematischer Fehler: Zeichenfolge \"{1}\" konnte nicht in eine Zahl konvertiert werden.',
		'CRRUI2075E' : 'Mathematischer Fehler: Argumentanzahl {0} f\u00fcr Methode {1} ist nicht g\u00fcltig. Das bereitgestellte Argument ist {2}',
		'CRRUI2076E' : 'Mathematischer Fehler: Zu viele Ziffern - {0}',
		'CRRUI2077E' : 'Mathematischer Fehler: round() - {0} Argumente angegeben. Erwartet wurden 1 oder 2.',
		'CRRUI2078E' : 'Mathematischer Fehler: round() - Rundung ist erforderlich.',
		'CRRUI2079E' : 'Mathematischer Fehler: round() - Falscher Rundungswert {0}.',
		'CRRUI2080E' : 'Mathematischer Fehler: round() - Exponent {0} verursacht \u00dcberlauf.',
		'CRRUI2081E' : 'Mathematischer Fehler: finish() - Exponent {0} verursacht \u00dcberlauf.',
		'CRRUI2082E' : 'Interner Fehler beim Aufruf des Konstruktors f\u00fcr {0}.',
		'CRRUI2083E' : 'Interner Fehler: Problem beim Definieren der Klasse {0}.',
		'CRRUI2084E' : 'Interner Fehler beim Definieren des Widgets {0}.{1} als Unterklasse von egl.ui.rui.RUIPropertiesLibrary',
		'CRRUI2085E' : 'Interner Fehler beim Definieren des Widgets {0}.{1} als Unterklasse von {2}.{3}.',
		'CRRUI2086E' : 'Interner Fehler: Problem beim Definieren von RUI-Handler {0}.',
		'CRRUI2087E' : 'Interner Fehler: Problem beim Definieren von RUI-Widget {0}.',
		'CRRUI2088E' : 'Dieser Browser wird von EGL Rich UI nicht unterst\u00fctzt.',
		'CRRUI2089E' : 'Die Konvertierung aus dem JSON-Format "{0}" war aufgrund von {1} nicht m\u00f6glich.',
		'CRRUI2090E' : 'Der Service konnte nicht aufgerufen werden: {0}',
		'CRRUI2091E' : 'Es konnte kein Ereignis an die Eclipse-IDE gesendet werden: {0}',
		'CRRUI2092E' : 'Interner Fehler: Problem beim Verarbeiten eines IDE-Ereignisses {0}',
		'CRRUI2093E' : 'Interner Fehler: Funktion {0} konnte nicht instrumentiert werden.',
		'CRRUI2094E' : 'Die folgenden EGL-Funktionsaufrufe f\u00fchren zu diesem Fehler:',
		'CRRUI2095E' : 'Die EGL-Funktionsaufrufe, die zu diesem Fehler f\u00fchren, konnten nicht gefunden werden.',
		'CRRUI2097E' : 'Ung\u00fcltiger Wert f\u00fcr CSS-Darstellung "{1}" f\u00fcr Attribut {0}',
		'CRRUI2097E' : 'Die CSS-Darstellung {0} konnte nicht ordnungsgem\u00e4\u00df analysiert werden. \u00dcberpr\u00fcfen Sie die Syntax oder verwenden Sie ein externes Style-Sheet.',
		'CRRUI2098E' : 'Fehler bei Drag-and-drop-Unterst\u00fctzung: {0}',
		'CRRUI2099E' : 'Bei Rich-UI wird die EGL-Operation "set" f\u00fcr den Typ {0} nicht unterst\u00fctzt.',
		'CRRUI2100E' : 'Ung\u00fcltiges Argument f\u00fcr RuiLib.convertFromXML. Erwartet wurde eine Zeichenfolge. Empfangen wurde ein Objekt des Typs {0}.',
		'CRRUI2101E' : 'indexOf-Wert "{1}" wurde in "{0}" aufgrund von {2} nicht gefunden.',
		'CRRUI2102E' : 'Feldgruppe konnte nicht sortiert werden.',
		'CRRUI2103E' : 'Ung\u00fcltiger Zugriff auf "{0}" im Objekt des Typs "{1}" aufgrund von {2}.',
		'CRRUI2104E' : 'Die JSON-Zeichenfolge "{0}" konnte nicht syntaktisch analysiert werden.',
		'CRRUI2105E' : 'Bei dem Versuch, {0} festzulegen, trat ein JSON-Parsing-Fehler auf. Das Feld ist an der erwarteten Stelle in der JSON-Zeichenfolge nicht vorhanden.',
		'CRRUI2106E' : 'Der Versuch, {0} auf Null zu setzen, ist beim JSON-Parsing fehlgeschlagen, weil das Feld gem\u00e4\u00df seiner Deklaration keine Nullwerte enthalten kann.',
		'CRRUI2107E' : 'JSON-Konvertierungsfunktionen der Servicebibliothek basieren auf einem Datensatz oder einem W\u00f6rterverzeichnis. {0} ist kein unterst\u00fctzter Typ.',
		'CRRUI2108E' : 'XML-Konvertierungsfunktionen der XML-Bibliothek basieren auf einem Datensatz. {0} ist kein unterst\u00fctzter Typ.',
		
		'CRRUI2111E' : 'MathContext()-Fehler: Die Anzahl {0} der angegebenen Argumente ist nicht g\u00fcltig; erwartet werden 1 bis 4.',
		'CRRUI2112E' : 'MathContext()-Fehler: Die Anzahl {0} der angegebenen Stellen ist zu klein.',
		'CRRUI2113E' : 'MathContext()-Fehler: Die Anzahl {0} der angegebenen Stellen ist zu gro\u00df.',
		'CRRUI2114E' : 'MathContext()-Fehler: Der angegebene Formatwert {0} ist nicht g\u00fcltig.',
		'CRRUI2115E' : 'MathContext()-Fehler: Der angegebene Wert {0} f\u00fcr den Ausf\u00fchrungsmodus ist nicht g\u00fcltig.',
		
	    'CRRUI2700E' : 'F\u00fcr ein erforderliches Feld wurde keine Eingabe empfangen. Wiederholen Sie die Eingabe.',
		'CRRUI2702E' : 'Datentypfehler in der Eingabe. Wiederholen Sie die Eingabe.',
		'CRRUI2703E' : 'Die Anzahl der zul\u00e4ssigen signifikanten Ziffern wurde \u00fcberschritten. Wiederholen Sie die Eingabe.',
		'CRRUI2704E' : 'Die Eingabe liegt au\u00dferhalb des definierten Bereichs von {0} bis {1}. Wiederholen Sie die Eingabe.',
		'CRRUI2705E' : 'Bei der Minimall\u00e4nge der Eingabe ist ein Fehler aufgetreten. Wiederholen Sie die Eingabe.',
		'CRRUI2707E' : 'Bei der Eingabe ist ein Moduluspr\u00fcffehler aufgetreten. Wiederholen Sie die Eingabe.',
		'CRRUI2708E' : 'Die Eingabe ist f\u00fcr das definierte Datums- oder Zeitformat {0} ung\u00fcltig.',
		'CRRUI2710E' : 'Die Eingabe f\u00fcr das Boolesche Feld ist ung\u00fcltig.',
		'CRRUI2712E' : 'Die hexadezimalen Daten sind ung\u00fcltig.',
		'CRRUI2713E' : 'Der eingegebene Wert ist ung\u00fcltig, weil er nicht mit dem festgelegten Muster \u00fcbereinstimmt.',		
		'CRRUI2716E' : 'Die Eingabe befindet sich nicht in der definierten Liste g\u00fcltiger Werte. Wiederholen Sie die Eingabe.',
		'CRRUI2717E' : 'Das angegebene Datums- und Zeitformat von {0} ist ung\u00fcltig.',	
		'CRRUI2719E' : 'Bei der Syntaxanalyse des Eingabewerts ist ein Fehler aufgetreten.',

		'CRRUI3650E' : 'Implementierungsdeskriptordatei nicht gefunden: \'{0}\'',
		'CRRUI3651E' : 'Service-Binding-Schl\u00fcssel \'{0}\' ist im Implementierungsdeskriptor \'{1}\' nicht vorhanden.',
		'CRRUI3652E' : 'Falscher Service-Binding-Typ \'{0}\', erwarteter Bindingtyp \'{1}\'',
		'CRRUI3653E' : 'Fehler beim Versuch, REST-Service f\u00fcr \'{0}\' aufzurufen.',
		'CRRUI3654E' : 'Fehler bei Erstellung des Anforderungsobjekts: \'{0}\'',
		'CRRUI3655E' : 'Fehler bei Verarbeitung des Antwortobjekts: \'{0}\'',
		'CRRUI3656E' : '\'formdata\' wird als Antwortformat nicht unterst\u00fctzt.',
		'CRRUI3657E' : 'Proxy f\u00fcr Serviceaufrufe wurde bei \'{0}\' nicht gefunden.',
		'CRRUI3658E' : 'Fehler f\u00fcr Proxy bei \'{0}\' w\u00e4hrend versuchten Serviceaufrufs f\u00fcr \'{1}\'',
		'CRRUI3659E' : 'JSON-Antwortparsing ergab Null, urspr\u00fcngliche Antwort war \'{0}\'',
		'CRRUI3660E' : 'Ausnahmebedingung aufgetreten: Antwort f\u00fcr \'{0}\' konnte nicht verarbeitet werden. Ursache: \'{1}\'',
		'CRRUI3661E' : 'Service-Binding-Informationen fehlen f\u00fcr \'{0}\', w\u00e4hrend vesucht wird, Servicefunktion \'{1}\' aufzurufen.'
};

