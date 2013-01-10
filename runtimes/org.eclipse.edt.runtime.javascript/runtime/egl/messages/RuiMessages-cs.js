/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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
		'CRRUI0001E' : 'N\u00e1zev InitalUI[{0}]InitialUI je pr\u00e1zdn\u00fd. Vol\u00e1n\u00ed funkce nelze pou\u017e\u00edt k inicializaci n\u00e1zvu.',		
		'CRRUI0006E' : 'Skupina p\u0159ep\u00edna\u010d\u016f {0} (RadioGroup) obsahuje neplatn\u00fd argument.',
					
		'CRRUI1001E' : 'U prom\u011bnn\u00e9 {0} nelze {1} nastavit na {2}',
		'CRRUI1010E' : 'Funkce p\u0159eta\u017een\u00ed jsou odkazy na funkce, nikoli pole: {0}',
		'CRRUI1020E' : 'Runtime.asDictionary: lze p\u0159ev\u00e9st pouze popisova\u010de nebo z\u00e1znamy do slovn\u00edk\u016f, nikoli {0}.',
		'CRRUI1030E' : 'Nelze naj\u00edt definici pro {0}.{1}',
		'CRRUI1050E' : 'Prom\u011bnn\u00e1 {0} mus\u00ed zahrnovat slovo \"null\".',
		'CRRUI1051E' : 'Modul widget nelze p\u0159idat k prom\u011bnn\u00e9 {0}. ',
		'CRRUI1055E' : 'Pod\u0159\u00edzen\u00fd {1} nelze p\u0159idat k prom\u011bnn\u00e9 {2}.',		
		'CRRUI1057E' : 'Modul widget {0} nelze p\u0159idat k sob\u011b.',	
		'CRRUI1058E' : 'P\u0159edch\u016fdce {1} nelze p\u0159idat k prom\u011bnn\u00e9 {0}. {2} je aktu\u00e1ln\u00ed p\u0159edch\u016fdce.',
		'CRRUI1060E' : 'Pokus o nastaven\u00ed nad\u0159\u00edzen\u00e9ho prvku pro modul widget  \"{0}\" na {1}:{2}; nad\u0159\u00edzen\u00fd modul mus\u00ed b\u00fdt modul widget.',
		'CRRUI1070E' : 'Do\u0161lo k v\u00fdjimce {0} b\u011bhem zpracov\u00e1n\u00ed vol\u00e1n\u00ed funkce back. Pou\u017eijte try...OnException.',
		'CRRUI1071E' : 'Nebyla nalezena \u017e\u00e1dn\u00e1 obslu\u017en\u00e1 rutina v\u00fdjimek pro vol\u00e1n\u00ed slu\u017eby. P\u0159idejte obslu\u017enou rutinu v\u00fdjimek pro vol\u00e1n\u00ed slu\u017eby. ',
		'CRRUI1072E' : 'Do\u0161lo k v\u00fdjimce {0} v chybov\u00e9m vol\u00e1n\u00ed funkce back, k\u00f3du s try...OnException',		
		'CRRUI1080E' : 'Modul widget nem\u00e1 \u017e\u00e1dn\u00fd prvek DOM a nen\u00ed v dokumentu. <br>Aktu\u00e1ln\u00ed atributy tohoto modulu widget jsou:<P>{0}',
		'CRRUI1083E' : 'Vyskytla se chyba b\u011bhem zpracov\u00e1n\u00ed ud\u00e1losti prohl\u00ed\u017ee\u010de {0}.',
		'CRRUI1150E' : 'Pokus o odebr\u00e1n\u00ed hodnoty \"null\" z prom\u011bnn\u00e9 {0} se nezda\u0159il. ',		
		'CRRUI1151E' : 'Pokus o odebr\u00e1n\u00ed modulu widget, kter\u00fd nem\u00e1 \u017e\u00e1dn\u00fd objekt DOM, z prom\u011bnn\u00e9 {0} selhal.',		
		'CRRUI1155E' : 'Pod\u0159\u00edzen\u00fd typu {1} nelze odebrat z prom\u011bnn\u00e9 {0}: {2}.',
		'CRRUI1157E' : 'Modul widget z n\u011bho nelze odebrat. Typ modulu widget je {0}.',
			
		'CRRUI2002E' : '{1}<br>Do\u0161lo k chyb\u011b uvnit\u0159 {0}: {2}',
		'CRRUI2004E' : 'Historie {0} obsahuje neplatn\u00fd argument.',
		'CRRUI2005E' : 'Odkaz null nelze pou\u017e\u00edt. ',
		'CRRUI2006E' : '{0}', // The text of this message comes from some other error message.
		'CRRUI2007E' : 'Neplatn\u00fd argument: {0}.',
		'CRRUI2009E' : 'Ud\u00e1lost nelze p\u0159ev\u00e9st na instanci z popisova\u010de RUI.',
		'CRRUI2010E' : 'Zam\u00edtnut\u00e1 funkce: {0}.',
		
		'CRRUI2015E' : 'Soubor {0} pro RUIPropertiesLibrary {1} chyb\u00ed',
		'CRRUI2016E' : 'Program EGL vyu\u017e\u00edv\u00e1 p\u0159\u00edli\u0161 mnoho \u010dasu.',
		'CRRUI2017E' : 'Hodnotu \"{0}\" typu {1} nelze p\u0159ev\u00e9st na typ {2}.',
		'CRRUI2018E' : 'P\u0159ete\u010den\u00ed p\u0159i p\u0159i\u0159azen\u00ed {0} k typu {1}.',
		'CRRUI2019E' : 'Nelze p\u0159ipojit prvek {0} k matici. Maxim\u00e1ln\u00ed velikost matice je {1}',
		'CRRUI2020E' : 'Neplatn\u00fd argument {0} k funkci pol\u00ed setMaxSize()',
		'CRRUI2021E' : 'Nelze z\u00edskat dal\u0161\u00ed token z \u0159et\u011bzce, za\u010d\u00ednaj\u00edc\u00edho v indexu {0}',
		'CRRUI2022E' : 'Index {0} je mimo rozsah pro toto pole. Velikost pole je {1}.',
		'CRRUI2023E' : 'Byl pou\u017eit nulov\u00fd odkaz: {0}',
		'CRRUI2024E' : 'Dynamick\u00fd p\u0159\u00edstup ke kl\u00ed\u010di \"{0}\" nen\u00ed platn\u00fd na objektu typu {1}.',
		'CRRUI2025E' : 'Dynamick\u00fd p\u0159\u00edstup se nezda\u0159il: takov\u00fd kl\u00ed\u010d neexistuje, \"{0}\".',
		'CRRUI2030E' : 'Neplatn\u00fd argument pro XMLLib.convertFromXML. O\u010dek\u00e1v\u00e1n \u0159et\u011bzec. P\u0159ijato \"{0}\".',
		'CRRUI2031E' : 'Do\u0161lo k chyb\u011b p\u0159i anal\u00fdze XML: {0}',
		'CRRUI2032E' : 'Vzor \u010dasov\u00e9ho raz\u00edtka {0} je neplatn\u00fd.',
		'CRRUI2033E' : 'Hodnota {0}, zadan\u00e1 pro funkci pole resize(), je v\u011bt\u0161\u00ed ne\u017e je maxim\u00e1ln\u00ed velikost pole, {1}',
		'CRRUI2034E' : 'Typ, {0}, pro prvek pole nen\u00ed platn\u00fd.',
		'CRRUI2035E' : 'Po\u010det zm\u011bn\u011bn\u00fdch rozm\u011br\u016f je v\u011bt\u0161\u00ed ne\u017e po\u010det rozm\u011br\u016f v poli.',
		'CRRUI2036E' : 'Chyba dom\u00e9ny ve vol\u00e1n\u00ed {0}: argument mus\u00ed b\u00fdt mezi {1} a {2}.',
		'CRRUI2037E' : 'Po\u010detn\u00ed chyba: nelze d\u011blit nulou.',
		'CRRUI2038E' : 'Chyba dom\u00e9ny ve vol\u00e1n\u00ed {0}: argument mus\u00ed b\u00fdt v\u011bt\u0161\u00ed ne\u017e nula.',
		'CRRUI2039E' : 'Chyba dom\u00e9ny ve vol\u00e1n\u00ed {0}: argument mus\u00ed b\u00fdt v\u011bt\u0161\u00ed nebo roven nule.',
		'CRRUI2040E' : 'Chyba dom\u00e9ny ve vol\u00e1n\u00ed {0}: exponent pro nulov\u00fd z\u00e1klad mus\u00ed b\u00fdt v\u011bt\u0161\u00ed ne\u017e nula.',
		'CRRUI2041E' : 'Chyba dom\u00e9ny ve vol\u00e1n\u00ed {0}: exponent pro z\u00e1porn\u00fd z\u00e1klad mus\u00ed b\u00fdt cel\u00e9 \u010d\u00edslo.',
		'CRRUI2042E' : 'Neplatn\u00e9 indexy pod\u0159et\u011bzc\u016f {0}:{1}.',
		'CRRUI2050E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty abs() - {0}. O\u010dek\u00e1v\u00e1no 0 nebo 1.',
		'CRRUI2051E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty add() - {0}. O\u010dek\u00e1v\u00e1no 1 nebo 2.',
		'CRRUI2052E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty compareTo() - {0}. O\u010dek\u00e1v\u00e1no 1 nebo 2.',
		'CRRUI2053E' : 'Po\u010detn\u00ed chyba: divide() - z\u00e1porn\u00fd rozsah, {0}, neplatn\u00e9.',
		'CRRUI2054E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty divide() - {0}. O\u010dek\u00e1v\u00e1na hodnota 1 a\u017e 3.',
		'CRRUI2055E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty divideInteger() - {0}. O\u010dek\u00e1v\u00e1no 1 nebo 2.',
		'CRRUI2056E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty max() - {0}. O\u010dek\u00e1v\u00e1no 1 nebo 2.',
		'CRRUI2057E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty min() - {0}. O\u010dek\u00e1v\u00e1no 1 nebo 2.',
		'CRRUI2058E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty multiply() - {0}. O\u010dek\u00e1v\u00e1no 1 nebo 2.',
		'CRRUI2059E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty negate() - {0}. O\u010dek\u00e1v\u00e1no 0 nebo 1.',
		'CRRUI2060E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty plus() - {0}. O\u010dek\u00e1v\u00e1no 0 nebo 1.',
		'CRRUI2061E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty pow() - {0}. O\u010dek\u00e1v\u00e1no 1 nebo 2.',
		'CRRUI2062E' : 'Po\u010detn\u00ed chyba: pow() - z\u00e1porn\u00e9, {0}.',
		'CRRUI2063E' : 'Po\u010detn\u00ed chyba: pow() - p\u0159\u00edli\u0161 mnoho \u010d\u00edslic, {0}.',
		'CRRUI2064E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty remainder() - {0}. O\u010dek\u00e1v\u00e1no 1 nebo 2.',
		'CRRUI2065E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty subtract() - {0}. O\u010dek\u00e1v\u00e1no 1 nebo 2.',
		'CRRUI2066E' : 'Chyba p\u0159i porovn\u00e1n\u00ed: format() - {0} zadan\u00e9 argumenty. O\u010dek\u00e1v\u00e1no 2 nebo 6',
		'CRRUI2067E' : 'Po\u010detn\u00ed chyba: format() - p\u0159ete\u010den\u00ed exponentu, {0}.',
		'CRRUI2068E' : 'Po\u010detn\u00ed chyba: intValueExact() - desetinn\u00e1 \u010d\u00e1st je nenulov\u00e1, {0}.',
		'CRRUI2069E' : 'Po\u010detn\u00ed chyba: intValueExact() - p\u0159ete\u010den\u00ed p\u0159evodu, {0}.',
		'CRRUI2070E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty setScale() - {0}. O\u010dek\u00e1v\u00e1no 1 nebo 2.',
		'CRRUI2071E' : 'Po\u010detn\u00ed chyba: setScale() - z\u00e1porn\u00e9, {0}.',
		'CRRUI2072E' : 'Po\u010detn\u00ed chyba: intCheck() - chyba p\u0159evodu, {0}.',
		'CRRUI2073E' : 'Po\u010detn\u00ed chyba: dodivide() - p\u0159ete\u010den\u00ed cel\u00e9ho \u010d\u00edsla.',
		'CRRUI2074E' : 'Po\u010detn\u00ed chyba: \u0159et\u011bzec \"{1}\" nelze p\u0159ev\u00e9st na \u010d\u00edslo.',
		'CRRUI2075E' : 'Po\u010detn\u00ed chyba: \u010d\u00edslo argumentu {0} pro metodu {1} nen\u00ed platn\u00e9. Poskytnut\u00fd argument je {2}',
		'CRRUI2076E' : 'Po\u010detn\u00ed chyba: p\u0159\u00edli\u0161 mnoho \u010d\u00edslic - {0}.',
		'CRRUI2077E' : 'Po\u010detn\u00ed chyba: d\u00e1ny argumenty round() - {0}. O\u010dek\u00e1v\u00e1no 1 nebo 2.',
		'CRRUI2078E' : 'Po\u010detn\u00ed chyba: round() - nutno zaokrouhlit.',
		'CRRUI2079E' : 'Po\u010detn\u00ed chyba: round() - chybn\u00e1 zaokrouhlen\u00e1 hodnota, {0}.',
		'CRRUI2080E' : 'Po\u010detn\u00ed chyba: round() - exponent, {0}, p\u0159ete\u010de.',
		'CRRUI2081E' : 'Po\u010detn\u00ed chyba: finish() - exponent, {0}, p\u0159ete\u010de.',
		'CRRUI2082E' : 'Intern\u00ed chyba: p\u0159i vol\u00e1n\u00ed konstruktoru pro {0}.',
		'CRRUI2083E' : 'Intern\u00ed chyba: nastal probl\u00e9m p\u0159i definov\u00e1n\u00ed t\u0159\u00eddy {0}.',
		'CRRUI2084E' : 'Intern\u00ed chyba: p\u0159i definov\u00e1n\u00ed modulu widget {0}.{1} jako podt\u0159\u00eddy egl.ui.rui.RUIPropertiesLibrary.',
		'CRRUI2085E' : 'Intern\u00ed chyba: p\u0159i definov\u00e1n\u00ed modulu widget {0}.{1} jako podt\u0159\u00eddy {2}.{3}.',
		'CRRUI2086E' : 'Intern\u00ed chyba: nastal probl\u00e9m p\u0159i definov\u00e1n\u00ed popisova\u010de RUI {0}.',
		'CRRUI2087E' : 'Intern\u00ed chyba: nastal probl\u00e9m p\u0159i definov\u00e1n\u00ed modulu widget RUI {0}.',
		'CRRUI2088E' : 'Tento prohl\u00ed\u017ee\u010d nen\u00ed podporov\u00e1n rozhran\u00edm EGL Rich UI',
		'CRRUI2089E' : 'Nelze p\u0159ev\u00e1d\u011bt z form\u00e1tu JSON: "{0}", kv\u016fli {1}',
		'CRRUI2090E' : 'Nelze volat slu\u017ebu: {0}',
		'CRRUI2091E' : 'Nelze odeslat ud\u00e1lost na IDE platformy Eclipse: {0}',
		'CRRUI2092E' : 'Intern\u00ed chyba: do\u0161lo k probl\u00e9mu p\u0159i obsluze ud\u00e1losti IDE {0}.',
		'CRRUI2093E' : 'Intern\u00ed chyba: nelze instrumentovat funkci {0}.',
		'CRRUI2094E' : 'Toto jsou vol\u00e1n\u00ed funkc\u00ed EGL, kter\u00e1 vedla k chyb\u011b:',
		'CRRUI2095E' : 'Nelze vyhledat vol\u00e1n\u00ed funkc\u00ed EGL, kter\u00e1 vedla k chyb\u011b.',
		'CRRUI2097E' : 'Neplatn\u00e1 hodnota styl\u016f CSS "{1}" pro atribut {0}.',
		'CRRUI2097E' : 'Styl CSS {0} nebyl spr\u00e1vn\u011b analyzov\u00e1n. Zkontrolujte syntaxi nebo pou\u017eijte extern\u00ed seznam styl\u016f.',
		'CRRUI2098E' : 'Do\u0161lo k chyb\u011b b\u011bhem podpory p\u0159eta\u017een\u00ed: {0}',
		'CRRUI2099E' : 'V rozhran\u00ed Rich UI nen\u00ed operace "set" jazyka EGL podporov\u00e1na na typu {0}.',
		'CRRUI2100E' : 'Neplatn\u00fd argument pro RuiLib.convertFromXML. O\u010dek\u00e1v\u00e1n \u0159et\u011bzec. P\u0159ijat\u00fd objekt typu \"{0}\".',
		'CRRUI2101E' : 'Nelze vyhledat indexOf "{1}" v "{0}" kv\u016fli {2}.',
		'CRRUI2102E' : 'Nelze se\u0159adit pole.',
		'CRRUI2103E' : 'Neplatn\u00fd p\u0159\u00edstup k "{0}" v objektu typu "{1}" kv\u016fli {2}.',
		'CRRUI2104E' : 'Nelze analyzovat \u0159et\u011bzec JSON "{0}".',
		'CRRUI2105E' : 'Vyskytla se chyba anal\u00fdzy JSON p\u0159i pokusu o nastaven\u00ed {0}. Pole neexistuje v o\u010dek\u00e1van\u00e9m um\u00edst\u011bn\u00ed v \u0159et\u011bzci JSON.',
		'CRRUI2106E' : 'Pokus o nastaven\u00ed {0} na hodnotu null selhal p\u0159i anal\u00fdze JSON, proto\u017ee nebylo deklarov\u00e1no jako nulovateln\u00e9 pole.',
		'CRRUI2107E' : 'Funkce p\u0159evodu JSON knihovny slu\u017eeb funguj\u00ed pro z\u00e1znam nebo slovn\u00edk. {0} nen\u00ed podporovan\u00fd typ. ',
		'CRRUI2108E' : 'Funkce p\u0159evodu XML knihovny XML funguj\u00ed pro z\u00e1znam. {0} nen\u00ed podporovan\u00fd typ. ',
		
		'CRRUI2111E' : 'Chyba MathContext(): po\u010det uveden\u00fdch argument\u016f {0} nen\u00ed platn\u00fd; o\u010dek\u00e1v\u00e1no 1 a\u017e 4.',
		'CRRUI2112E' : 'Chyba MathContext(): po\u010det uveden\u00fdch \u010d\u00edslic {0} je p\u0159\u00edli\u0161 mal\u00fd.',
		'CRRUI2113E' : 'Chyba MathContext(): po\u010det uveden\u00fdch \u010d\u00edslic {0} je p\u0159\u00edli\u0161 velk\u00fd.',
		'CRRUI2114E' : 'Chyba MathContext(): uveden\u00e1 hodnota formul\u00e1\u0159e {0} nen\u00ed platn\u00e1.',
		'CRRUI2115E' : 'Chyba MathContext(): uveden\u00e1 hodnota re\u017eimu zaokrouhlov\u00e1n\u00ed {0} nen\u00ed platn\u00e1.',
		
	    'CRRUI2700E' : 'Nebyl p\u0159ijat \u017e\u00e1dn\u00fd vstup u pole, kde je po\u017eadov\u00e1n - zadejte znovu.',
		'CRRUI2702E' : 'Je chyba v datov\u00e9m typu na vstupu - zadejte znovu.',
		'CRRUI2703E' : 'Byl p\u0159ekro\u010den po\u010det p\u0159\u00edpustn\u00fdch v\u00fdznamn\u00fdch \u010d\u00edslic - zadejte znovu.',
		'CRRUI2704E' : 'Vstup nen\u00ed v definovan\u00e9m rozsahu od {0} do {1} - zadejte znovu.',
		'CRRUI2705E' : 'Je chyba v minim\u00e1ln\u00ed d\u00e9lce vstupu - zadejte znovu.',
		'CRRUI2707E' : 'Na vstupu do\u0161lo k chyb\u011b kontroly modulu - zadejte znovu.',
		'CRRUI2708E' : 'Vstup nen\u00ed pro definovan\u00fd datov\u00fd nebo \u010dasov\u00fd form\u00e1t {0} platn\u00fd.',
		'CRRUI2710E' : 'Vstup nen\u00ed jako pole s logickou hodnotou platn\u00fd.',
		'CRRUI2712E' : 'Hexadecim\u00e1ln\u00ed data nejsou platn\u00e1.',
		'CRRUI2713E' : 'Zadan\u00e1 hodnota je neplatn\u00e1, pon\u011bvad\u017e neodpov\u00edd\u00e1 nastaven\u00e9mu vzorku.',		
		'CRRUI2716E' : 'Vstup nen\u00ed v definovan\u00e9m seznamu platn\u00fdch hodnot - zadejte znovu.',
		'CRRUI2717E' : 'Zadan\u00fd form\u00e1t data a \u010dasu {0} je neplatn\u00fd.',	
		'CRRUI2719E' : 'Do\u0161lo k chyb\u011b v anal\u00fdze vstupn\u00ed hodnoty.',

		'CRRUI3650E' : 'Nelze naj\u00edt soubor deskriptoru implementace: \'{0}\'',
		'CRRUI3651E' : 'Kl\u00ed\u010d vazby slu\u017eby: \'{0}\' neexistuje v deskriptoru implementace \'{1}\'',
		'CRRUI3652E' : 'Chybn\u00fd typ vazby slu\u017eby \'{0}\', o\u010dek\u00e1v\u00e1n typ vazby \'{1}\'.',
		'CRRUI3653E' : 'Vyskytla se chyba b\u011bhem pokusu o vyvol\u00e1n\u00ed slu\u017eby REST na \'{0}\'.',
		'CRRUI3654E' : 'Vyskytla se chyba b\u011bhem sestaven\u00ed objektu po\u017eadavku: \'{0}\'.',
		'CRRUI3655E' : 'Vyskytla se chyba b\u011bhem zpracov\u00e1n\u00ed objektu odpov\u011bd\u00ed: \'{0}\'.',
		'CRRUI3656E' : '\'formdata\' nen\u00ed podporov\u00e1n jako form\u00e1t odpov\u011bdi.',
		'CRRUI3657E' : 'Nelze naj\u00edt proxy  pro vol\u00e1n\u00ed slu\u017eeb na \'{0}\'.',
		'CRRUI3658E' : 'Do\u0161lo k chyb\u011b na proxy na \'{0}\' b\u011bhem pokusu o vyvol\u00e1n\u00ed slu\u017eby na \'{1}\'.',
		'CRRUI3659E' : 'V\u00fdsledek anal\u00fdzy odezvy Json je null, p\u016fvodn\u00ed odezva je: \'{0}\'.',
		'CRRUI3660E' : 'Do\u0161lo k v\u00fdjimce, nelze o\u0161et\u0159it odezvu pro \'{0}\', p\u0159\u00ed\u010dina: \'{1}\'.',
		'CRRUI3661E' : 'Sch\u00e1z\u00ed informace o vazb\u011b slu\u017eby \'{0}\' b\u011bhem pokusu o vyvol\u00e1n\u00ed funkce slu\u017eby \'{1}\'.'
};

