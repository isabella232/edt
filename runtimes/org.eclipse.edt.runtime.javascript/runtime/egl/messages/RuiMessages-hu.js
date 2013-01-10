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
		'CRRUI0001E' : 'Az InitalUI[{0}]InitialUI n\u00e9v \u00fcres. A f\u00fcggv\u00e9nyh\u00edv\u00e1s a n\u00e9v inicializ\u00e1l\u00e1s\u00e1ra nem haszn\u00e1lhat\u00f3.',		
		'CRRUI0006E' : 'A(z) {0} RadioGroup csoport illeg\u00e1lis argumentumot tartalmaz.',
					
		'CRRUI1001E' : 'A(z) {0} v\u00e1ltoz\u00f3 eset\u00e9ben a(z) {1} nem \u00e1ll\u00edthat\u00f3 be {2} \u00e9rt\u00e9kre',
		'CRRUI1010E' : 'A fogd \u00e9s vidd f\u00fcggv\u00e9nyek f\u00fcggv\u00e9nyhivatkoz\u00e1sok, nem t\u00f6mb\u00f6k: {0}',
		'CRRUI1020E' : 'Runtime.asDictionary: csak a kezel\u0151k \u00e9s rekordok alak\u00edthat\u00f3k sz\u00f3t\u00e1rakk\u00e1, a k\u00f6vetkez\u0151 nem: {0}',
		'CRRUI1030E' : 'Nem tal\u00e1lhat\u00f3 a(z) {0}.{1} meghat\u00e1roz\u00e1sa',
		'CRRUI1050E' : 'A(z) {0} v\u00e1ltoz\u00f3nak a \"null\" sz\u00f3t tartalmaznia kell.',
		'CRRUI1051E' : 'A fel\u00fcleti elem a(z) {0} v\u00e1ltoz\u00f3hoz nem adhat\u00f3 hozz\u00e1.',
		'CRRUI1055E' : 'A(z) {1} ut\u00f3d a(z) {2} v\u00e1ltoz\u00f3hoz nem adhat\u00f3 hozz\u00e1.',		
		'CRRUI1057E' : 'A(z) {0} fel\u00fcleti elem nem adhat\u00f3 hozz\u00e1 saj\u00e1t mag\u00e1hoz.',	
		'CRRUI1058E' : 'A(z) {1} \u0151s a(z) {0} v\u00e1ltoz\u00f3hoz nem adhat\u00f3 hozz\u00e1. A jelenlegi \u0151s a k\u00f6vetkez\u0151: {2}.',
		'CRRUI1060E' : 'K\u00eds\u00e9rlet a(z) \"{0}\" fel\u00fcleti elem sz\u00fcl\u0151j\u00e9nek k\u00f6vetkez\u0151re \u00e1ll\u00edt\u00e1s\u00e1ra: {1}:{2}; A sz\u00fcl\u0151nek fel\u00fcleti elemnek kell lennie',
		'CRRUI1070E' : '{0} kiv\u00e9tel t\u00f6rt\u00e9nt a visszah\u00edv\u00e1si f\u00fcggv\u00e9ny feldolgoz\u00e1sa sor\u00e1n.  Haszn\u00e1lja a try...OnException blokkot.',
		'CRRUI1071E' : 'A szolg\u00e1ltat\u00e1sh\u00edv\u00e1shoz nem tal\u00e1lhat\u00f3 kiv\u00e9telkezel\u0151. Vegyen fel kiv\u00e9telkezel\u0151t a szolg\u00e1ltat\u00e1sh\u00edv\u00e1shoz.',
		'CRRUI1072E' : '{0} kiv\u00e9tel t\u00f6rt\u00e9nt a hiba visszah\u00edv\u00e1si f\u00fcggv\u00e9nyben, a k\u00f3dol\u00e1shoz haszn\u00e1ljon try...OnException blokkot',		
		'CRRUI1080E' : 'A fel\u00fcleti elem nem rendelkezik dokumentum objektummodell (DOM) elemmel, \u00e9s nem jelenik meg a dokumentumban.<br>A fel\u00fcleti elemhez tartoz\u00f3 aktu\u00e1lis attrib\u00fatumok a k\u00f6vetkez\u0151k:<P>{0}',
		'CRRUI1083E' : 'Hiba t\u00f6rt\u00e9nt a(z) {0} b\u00f6ng\u00e9sz\u0151esem\u00e9ny kezel\u00e9se sor\u00e1n.',
		'CRRUI1150E' : 'A k\u00eds\u00e9rlet a \"null\" elt\u00e1vol\u00edt\u00e1s\u00e1ra egy {0} v\u00e1ltoz\u00f3b\u00f3l meghi\u00fasult.',		
		'CRRUI1151E' : 'A dokumentum objektummodell (DOM) elemmel nem rendelkez\u0151 fel\u00fcleti elem {0} v\u00e1ltoz\u00f3b\u00f3l v\u00e9gzett elt\u00e1vol\u00edt\u00e1s\u00e1ra tett k\u00eds\u00e9rlet meghi\u00fasult.',		
		'CRRUI1155E' : 'A(z) {1} t\u00edpus ut\u00f3dja {0}: {2} v\u00e1ltoz\u00f3b\u00f3l nem t\u00e1vol\u00edthat\u00f3 el.',
		'CRRUI1157E' : 'Fel\u00fcleti elem saj\u00e1t mag\u00e1b\u00f3l nem t\u00e1vol\u00edthat\u00f3 el. A fel\u00fcleti elem t\u00edpusa a k\u00f6vetkez\u0151: {0}.',
			
		'CRRUI2002E' : '{1}<br>A(z) {0} belsej\u00e9ben hiba t\u00f6rt\u00e9nt: {2}',
		'CRRUI2004E' : 'A(z) {0} t\u00f6rt\u00e9net illeg\u00e1lis argumentumot tartalmaz.',
		'CRRUI2005E' : 'Null hivatkoz\u00e1s nem haszn\u00e1lhat\u00f3.',
		'CRRUI2006E' : '{0}', // The text of this message comes from some other error message.
		'CRRUI2007E' : 'Illeg\u00e1lis param\u00e9ter: {0}.',
		'CRRUI2009E' : 'Esem\u00e9ny RUI kezel\u0151b\u0151l nem p\u00e9ld\u00e1nyos\u00edthat\u00f3.',
		'CRRUI2010E' : 'El\u00e9v\u00fclt funkci\u00f3: {0}.',
		
		'CRRUI2015E' : 'A(z) {1} RUIPropertiesLibrary {0} f\u00e1jlja hi\u00e1nyzik.',
		'CRRUI2016E' : 'Az EGL program t\u00fal sok id\u0151t haszn\u00e1l',
		'CRRUI2017E' : '{1} t\u00edpus\u00fa \"{0}\" \u00e9rt\u00e9k nem alak\u00edthat\u00f3 {2} t\u00edpus\u00fav\u00e1',
		'CRRUI2018E' : 'T\u00falcsordul\u00e1s a(z) {0} {1} t\u00edpushoz rendel\u00e9sekor',
		'CRRUI2019E' : 'A(z) {0} nem f\u0171zhet\u0151 hozz\u00e1 a t\u00f6mbh\u00f6z.  A t\u00f6mb maxim\u00e1lis m\u00e9rete a k\u00f6vetkez\u0151: {1}',
		'CRRUI2020E' : '\u00c9rv\u00e9nytelen {0} argumentum a setMaxSize() t\u00f6mb f\u00fcggv\u00e9ny\u00e9hez',
		'CRRUI2021E' : 'A(z) {0} indexn\u00e9l kezd\u0151d\u0151 String-b\u0151l nem k\u00e9rhet\u0151 le a k\u00f6vetkez\u0151 jelsor',
		'CRRUI2022E' : 'A(z) {0} index k\u00edv\u00fcl esik a hat\u00e1ron. A t\u00f6mb m\u00e9rete: {1}',
		'CRRUI2023E' : 'Null hivatkoz\u00e1s haszn\u00e1lat\u00e1ra ker\u00fclt sor: {0}',
		'CRRUI2024E' : 'A(z) \"{0}\" kulcs dinamikus el\u00e9r\u00e9se {1} t\u00edpus\u00fa objektum eset\u00e9n \u00e9rv\u00e9nytelen',
		'CRRUI2025E' : 'Dinamikus hozz\u00e1f\u00e9r\u00e9si hiba: nincs ilyen kulcs \"{0}\"',
		'CRRUI2030E' : '\u00c9rv\u00e9nytelen XMLLib.convertFromXML argumentum. A rendszer karaktersorozatot v\u00e1rt. Kapott argumentum: \"{0}\"',
		'CRRUI2031E' : 'Hiba t\u00f6rt\u00e9nt az XML \u00e9rtelmez\u00e9se sor\u00e1n: {0}',
		'CRRUI2032E' : 'A(z) {0} id\u0151pecs\u00e9t minta \u00e9rv\u00e9nytelen',
		'CRRUI2033E' : 'A resize() t\u00f6mbf\u00fcggv\u00e9nyhez megadott {0} \u00e9rt\u00e9k nagyobb, mint a t\u00f6mb maxim\u00e1lis m\u00e9rete ({1})',
		'CRRUI2034E' : 'A(z) {0} t\u00edpus a t\u00f6mb elemhez \u00e9rv\u00e9nytelen',
		'CRRUI2035E' : 'Az \u00e1tm\u00e9retezett dimenzi\u00f3k sz\u00e1ma nagyobb a t\u00f6mb dimenzi\u00f3inak sz\u00e1m\u00e1n\u00e1l',
		'CRRUI2036E' : 'Tartom\u00e1nyhiba a(z) {0} h\u00edv\u00e1sa sor\u00e1n: az argumentumnak a(z) {1} - {2} tartom\u00e1nyba kell esnie',
		'CRRUI2037E' : 'Matematikai hiba: nem v\u00e9gezhet\u0151 null\u00e1val oszt\u00e1s',
		'CRRUI2038E' : 'Tartom\u00e1nyhiba a(z) {0} h\u00edv\u00e1sa sor\u00e1n: az argumentumnak null\u00e1n\u00e1l nagyobbnak kell lennie',
		'CRRUI2039E' : 'Tartom\u00e1nyhiba a(z) {0} h\u00edv\u00e1sa sor\u00e1n: az argumentum nem lehet null\u00e1n\u00e1l kisebb',
		'CRRUI2040E' : 'Tartom\u00e1nyhiba a(z) {0} h\u00edv\u00e1sa sor\u00e1n: a nulla alap kitev\u0151j\u00e9nek null\u00e1n\u00e1l nagyobbnak kell lennie',
		'CRRUI2041E' : 'Tartom\u00e1nyhiba a(z) {0} h\u00edv\u00e1sa sor\u00e1n: a negat\u00edv alap kitev\u0151j\u00e9nek eg\u00e9sz sz\u00e1mnak kell lennie',
		'CRRUI2042E' : '\u00c9rv\u00e9nytelen r\u00e9sz-karaktersorozat indexek: {0}:{1}.',
		'CRRUI2050E' : 'Matematikai hiba: abs() - {0} argumentum van megadva. A rendszer 0-\u00e1t vagy 1-et v\u00e1rt',
		'CRRUI2051E' : 'Matematikai hiba: add() - {0} argumentum van megadva. A rendszer 1-et vagy 2-t v\u00e1rt',
		'CRRUI2052E' : 'Matematikai hiba: compareTo() - {0} argumentum van megadva. A rendszer 1-et vagy 2-t v\u00e1rt',
		'CRRUI2053E' : 'Matematikai hiba: divide() - A negat\u00edv sk\u00e1la - {0} - \u00e9rv\u00e9nytelen',
		'CRRUI2054E' : 'Matematikai hiba: divide() - {0} argumentum van megadva. A rendszer 1 \u00e9s 3 k\u00f6z\u00f6tti \u00e9rt\u00e9ket v\u00e1rt',
		'CRRUI2055E' : 'Matematikai hiba: divideInteger() - {0} argumentum van megadva. A rendszer 1-et vagy 2-t v\u00e1rt',
		'CRRUI2056E' : 'Matematikai hiba: max() - {0} argumentum van megadva. A rendszer 1-et vagy 2-t v\u00e1rt',
		'CRRUI2057E' : 'Matematikai hiba: min() - {0} argumentum van megadva. A rendszer 1-et vagy 2-t v\u00e1rt',
		'CRRUI2058E' : 'Matematikai hiba: multiply() - {0} argumentum van megadva. A rendszer 1-et vagy 2-t v\u00e1rt',
		'CRRUI2059E' : 'Matematikai hiba: negate() - {0} argumentum van megadva. A rendszer 0-\u00e1t vagy 1-et v\u00e1rt',
		'CRRUI2060E' : 'Matematikai hiba: plus() - {0} argumentum van megadva. A rendszer 0-\u00e1t vagy 1-et v\u00e1rt',
		'CRRUI2061E' : 'Matematikai hiba: pow() - {0} argumentum van megadva. A rendszer 1-et vagy 2-t v\u00e1rt',
		'CRRUI2062E' : 'Matematikai hiba: pow() - Negat\u00edv kitev\u0151, {0}',
		'CRRUI2063E' : 'Matematikai hiba: pow() - T\u00fal sok sz\u00e1mjegy, {0}',
		'CRRUI2064E' : 'Matematikai hiba: remainder() - {0} argumentum van megadva. A rendszer 1-et vagy 2-t v\u00e1rt',
		'CRRUI2065E' : 'Matematikai hiba: subtract() - {0} argumentum van megadva. A rendszer 1-et vagy 2-t v\u00e1rt',
		'CRRUI2066E' : 'Matematikai hiba: format() - {0} argumentum van megadva. A rendszer 2-t vagy 6-ot v\u00e1rt',
		'CRRUI2067E' : 'Matematikai hiba: format() - kitev\u0151 t\u00falcsordul\u00e1sa, {0}',
		'CRRUI2068E' : 'Matematikai hiba: intValueExact() - A tizedes r\u00e9sz nem nulla, {0}',
		'CRRUI2069E' : 'Matematikai hiba: intValueExact() - Konverzi\u00f3s t\u00falcsordul\u00e1s, {0}',
		'CRRUI2070E' : 'Matematikai hiba: setScale() - {0} argumentum van megadva. A rendszer 1-et vagy 2-t v\u00e1rt',
		'CRRUI2071E' : 'Matematikai hiba: setScale() - negat\u00edv sk\u00e1la, {0}',
		'CRRUI2072E' : 'Matematikai hiba: intCheck() - konverzi\u00f3s hiba, {0}',
		'CRRUI2073E' : 'Matematikai hiba: dodivide() - eg\u00e9sz sz\u00e1m t\u00falcsordul\u00e1s',
		'CRRUI2074E' : 'Matematikai hiba: A(z) \"{1}\" karaktersorozat nem alak\u00edthat\u00f3 sz\u00e1mm\u00e1',
		'CRRUI2075E' : 'Matematikai hiba: A(z) {1} met\u00f3dus {0} argumentumsz\u00e1ma \u00e9rv\u00e9nytelen. A megadott argumentum a k\u00f6vetkez\u0151: {2}',
		'CRRUI2076E' : 'Matematikai hiba: T\u00fal sok sz\u00e1mjegy - {0}',
		'CRRUI2077E' : 'Matematikai hiba: round() - {0} argumentum van megadva. A rendszer 1-et vagy 2-t v\u00e1rt',
		'CRRUI2078E' : 'Matematikai hiba: round() - Kerek\u00edt\u00e9s sz\u00fcks\u00e9ges',
		'CRRUI2079E' : 'Matematikai hiba: round() - Rossz kerek\u00edt\u00e9si \u00e9rt\u00e9k, {0}',
		'CRRUI2080E' : 'Matematikai hiba: round() - {0} kitev\u0151 t\u00falcsordul\u00e1sok',
		'CRRUI2081E' : 'Matematikai hiba: finish() - {0} kitev\u0151 t\u00falcsordul\u00e1sok',
		'CRRUI2082E' : 'Bels\u0151 hiba: A(z) {0} konstruktor\u00e1nak megh\u00edv\u00e1sa sor\u00e1n',
		'CRRUI2083E' : 'Bels\u0151 hiba: Hiba t\u00f6rt\u00e9nt a(z) {0} oszt\u00e1ly megad\u00e1sa sor\u00e1n',
		'CRRUI2084E' : 'Bels\u0151 hiba: A(z) {0}.{1} fel\u00fclet elem egl.ui.rui.RUIPropertiesLibrary aloszt\u00e1lyk\u00e9nt t\u00f6rt\u00e9n\u0151 megad\u00e1sa sor\u00e1n',
		'CRRUI2085E' : 'Bels\u0151 hiba: A(z) {0}.{1} fel\u00fclet elem {2}.{3} aloszt\u00e1lyk\u00e9nt t\u00f6rt\u00e9n\u0151 megad\u00e1sa sor\u00e1n',
		'CRRUI2086E' : 'Bels\u0151 hiba: Hiba t\u00f6rt\u00e9nt a(z) {0} RUI kezel\u0151 megad\u00e1sa sor\u00e1n',
		'CRRUI2087E' : 'Bels\u0151 hiba: Hiba t\u00f6rt\u00e9nt a(z) {0} RUI fel\u00fcleti elem megad\u00e1sa sor\u00e1n',
		'CRRUI2088E' : 'A b\u00f6ng\u00e9sz\u0151t az EGL gazdag UI nem t\u00e1mogatja',
		'CRRUI2089E' : 'Nem v\u00e9gezhet\u0151 \u00e1talak\u00edt\u00e1s JSON form\u00e1tumr\u00f3l: "{0}", ok: {1}',
		'CRRUI2090E' : 'A szolg\u00e1ltat\u00e1s nem h\u00edvhat\u00f3 meg: {0}',
		'CRRUI2091E' : 'Nem k\u00fcldhet\u0151 esem\u00e9ny az Eclipse IDE-nek: {0}',
		'CRRUI2092E' : 'Bels\u0151 hiba: A hiba a(z) {0} IDE esem\u00e9ny kezel\u00e9s sor\u00e1n t\u00f6rt\u00e9nt',
		'CRRUI2093E' : 'Bels\u0151 hiba: A(z) {0} f\u00fcggv\u00e9ny nem k\u00e9sz\u00edthet\u0151 fel',
		'CRRUI2094E' : 'A hib\u00e1hoz vezet\u0151 EGL f\u00fcggv\u00e9nyh\u00edv\u00e1sok:',
		'CRRUI2095E' : 'A hib\u00e1hoz vezet\u0151 EGL f\u00fcggv\u00e9nyh\u00edv\u00e1sok nem tal\u00e1lhat\u00f3k',
		'CRRUI2097E' : '\u00c9rv\u00e9nytelen "{1}" CSS st\u00edlus\u00e9rt\u00e9k a(z) {0} attrib\u00fatumhoz',
		'CRRUI2097E' : 'A(z) {0} CSS st\u00edlus nem \u00e9rtelmezhet\u0151 helyesen. Ellen\u0151rizze a szintaxist, vagy haszn\u00e1ljon k\u00fcls\u0151 st\u00edluslapot.',
		'CRRUI2098E' : 'Hiba t\u00f6rt\u00e9nt a fogd \u00e9s vidd t\u00e1mogat\u00e1s sor\u00e1n: {0}',
		'CRRUI2099E' : 'A gazdag UI-ban az EGL "set" m\u0171velet {0} t\u00edpuson nem t\u00e1mogatott',
		'CRRUI2100E' : '\u00c9rv\u00e9nytelen RuiLib.convertFromXML argumentum. A rendszer karaktersorozatot v\u00e1rt. {0} t\u00edpus\u00fa objektum \u00e9rkezett',
		'CRRUI2101E' : 'Az indexOf "{1}" {2} miatt nem tal\u00e1lhat\u00f3 a k\u00f6vetkez\u0151n bel\u00fcl: "{0}"',
		'CRRUI2102E' : 'A t\u00f6mb nem rendezhet\u0151',
		'CRRUI2103E' : '"{0}" illeg\u00e1lis el\u00e9r\u00e9se a(z) "{1}" t\u00edpus\u00fa objektumban, a k\u00f6vetkez\u0151 miatt: {2}',
		'CRRUI2104E' : 'A(z) "{0}" JSON karaktersorozat nem \u00e9rtelmezhet\u0151',
		'CRRUI2105E' : 'JSON \u00e9rtelmez\u00e9si hiba t\u00f6rt\u00e9nt a(z) {0} be\u00e1ll\u00edt\u00e1s\u00e1ra tett k\u00eds\u00e9rlet k\u00f6zben. A mez\u0151 nem l\u00e9tezik a JSON karaktersorozat v\u00e1rt pontj\u00e1n.',
		'CRRUI2106E' : 'A(z) {0} null\u00e9rt\u00e9kre t\u00f6rt\u00e9n\u0151 be\u00e1ll\u00edt\u00e1s\u00e1ra tett k\u00eds\u00e9rlet a JSON \u00e9rtelmez\u00e9se k\u00f6zben meghi\u00fasult, mivel a mez\u0151 nem ker\u00fclt nullk\u00e9pes mez\u0151k\u00e9nt deklar\u00e1l\u00e1sra.',
		'CRRUI2107E' : 'A szolg\u00e1ltat\u00e1si f\u00fcggv\u00e9nyt\u00e1r JSON \u00e1talak\u00edt\u00e1si f\u00fcggv\u00e9nyei egy rekordon vagy sz\u00f3t\u00e1ron v\u00e9geznek m\u0171veletet. A(z) {0} nem t\u00e1mogatott t\u00edpus.',
		'CRRUI2108E' : 'Az XML f\u00fcggv\u00e9nyt\u00e1r XML \u00e1talak\u00edt\u00e1si f\u00fcggv\u00e9nyei egy rekordon v\u00e9geznek m\u0171veletet. A(z) {0} nem t\u00e1mogatott t\u00edpus.',
		
		'CRRUI2111E' : 'MathContext() hiba: A megadott param\u00e9terek sz\u00e1ma, {0}, \u00e9rv\u00e9nytelen; a v\u00e1rt \u00e9rt\u00e9k 1-4 k\u00f6z\u00f6tti.',
		'CRRUI2112E' : 'MathContext() hiba: A megadott sz\u00e1mjegyek sz\u00e1ma, {0}, \u00e9rv\u00e9nytelen; t\u00fal kev\u00e9s.',
		'CRRUI2113E' : 'MathContext() hiba: A megadott sz\u00e1mjegyek sz\u00e1ma, {0}, \u00e9rv\u00e9nytelen; t\u00fal sok.',
		'CRRUI2114E' : 'MathContext() hiba: A megadott \u0171rlap\u00e9rt\u00e9k, {0}, \u00e9rv\u00e9nytelen.',
		'CRRUI2115E' : 'MathContext() hiba: A megadott kerek\u00edt\u00e9si m\u00f3d \u00e9rt\u00e9ke, {0}, \u00e9rv\u00e9nytelen.',
		
	    'CRRUI2700E' : 'Nem \u00e9rkezett bevitel egy k\u00f6telez\u0151 mez\u0151h\u00f6z - adja meg \u00fajra.',
		'CRRUI2702E' : 'Hib\u00e1s adatt\u00edpus a bevitelben - adja meg \u00fajra.',
		'CRRUI2703E' : 'T\u00fall\u00e9pte a megengedett jelent\u0151s sz\u00e1mjegyek sz\u00e1m\u00e1t - adja meg \u00fajra.',
		'CRRUI2704E' : 'A bevitel nincs a meghat\u00e1rozott - {0} - {1} - tartom\u00e1nyban, \u00edrja be \u00fajra.',
		'CRRUI2705E' : 'Minim\u00e1lis hossz hiba a bevitelben - adja meg \u00fajra.',
		'CRRUI2707E' : 'Beviteli modulus-ellen\u0151rz\u00e9si hiba - adja meg \u00fajra.',
		'CRRUI2708E' : 'A bevitel nem felel meg a meghat\u00e1rozott d\u00e1tum- vagy id\u0151form\u00e1tumnak: {0}.',
		'CRRUI2710E' : 'A bevitel nem \u00e9rv\u00e9nyes logikai mez\u0151h\u00f6z.',
		'CRRUI2712E' : 'A hexadecim\u00e1lis adatok nem \u00e9rv\u00e9nyesek.',
		'CRRUI2713E' : 'A megadott \u00e9rt\u00e9k \u00e9rv\u00e9nytelen, mivel nem felel meg a be\u00e1ll\u00edtott mint\u00e1nak.',		
		'CRRUI2716E' : 'A bevitel nincs a meghat\u00e1rozott \u00e9rv\u00e9nyes \u00e9rt\u00e9kek list\u00e1j\u00e1ban - \u00edrja be \u00fajra.',
		'CRRUI2717E' : 'A(z) {0} megadott d\u00e1tum- \u00e9s id\u0151form\u00e1tuma \u00e9rv\u00e9nytelen.',	
		'CRRUI2719E' : 'Hiba a beviteli \u00e9rt\u00e9kek \u00e9rtelmez\u00e9sekor.',

		'CRRUI3650E' : 'A telep\u00edt\u00e9s le\u00edr\u00f3 f\u00e1jl nem tal\u00e1lhat\u00f3: \'{0}\'',
		'CRRUI3651E' : 'A(z) \'{0}\' szolg\u00e1ltat\u00e1si k\u00f6t\u00e9s nem l\u00e9tezik a(z) \'{1}\' telep\u00edt\u00e9si le\u00edr\u00f3ban',
		'CRRUI3652E' : 'Rossz \'{0}\' szolg\u00e1ltat\u00e1sk\u00f6t\u00e9si t\u00edpus. A rendszer \'{1}\' k\u00f6t\u00e9si t\u00edpust v\u00e1rt',
		'CRRUI3653E' : 'Hiba t\u00f6rt\u00e9nt rest szolg\u00e1ltat\u00e1s megh\u00edv\u00e1s\u00e1ra tett k\u00eds\u00e9rlet sor\u00e1n a k\u00f6vetkez\u0151n: \'{0}\'',
		'CRRUI3654E' : 'Hiba t\u00f6rt\u00e9nt a k\u00e9r\u00e9s objektum \u00f6ssze\u00e9p\u00edt\u00e9se sor\u00e1n: \'{0}\'',
		'CRRUI3655E' : 'Hiba t\u00f6rt\u00e9nt a v\u00e1lasz objektum feldolgoz\u00e1sa sor\u00e1n: \'{0}\'',
		'CRRUI3656E' : 'A \'formdata\' nem t\u00e1mogatott v\u00e1laszform\u00e1tum',
		'CRRUI3657E' : 'A szolg\u00e1ltat\u00e1sh\u00edv\u00e1shoz nem tal\u00e1lhat\u00f3 proxy a k\u00f6vetkez\u0151 helyen: \'{0}\'',
		'CRRUI3658E' : 'Hiba t\u00f6rt\u00e9nt a(z) \'{0}\' helyen l\u00e9v\u0151 proxy-n a szolg\u00e1ltat\u00e1s k\u00f6vetkez\u0151 elemen t\u00f6rt\u00e9n\u0151 megh\u00edv\u00e1sa sor\u00e1n: \'{1}\'',
		'CRRUI3659E' : 'A Json v\u00e1lasz \u00e9rtelmez\u00e9se null\u00e9rt\u00e9ket eredm\u00e9nyezett, az eredeti v\u00e1lasz: \'{0}\'',
		'CRRUI3660E' : 'Kiv\u00e9tel t\u00f6rt\u00e9nt. A(z) \'{0}\' v\u00e1lasza nem kezelhet\u0151, ok: \'{1}\'',
		'CRRUI3661E' : 'A(z) \'{0}\' szolg\u00e1ltat\u00e1s\u00f6sszerendel\u00e9si inform\u00e1ci\u00f3i hi\u00e1nyoznak a(z) \'{1}\' szolg\u00e1ltat\u00e1sf\u00fcggv\u00e9ny megh\u00edv\u00e1s\u00e1ra tett k\u00eds\u00e9rlet sor\u00e1n.'
};

