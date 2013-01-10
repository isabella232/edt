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
		'CRRUI0001E' : 'Le nom InitalUI[{0}]InitialUI est vide. L\'appel de fonction ne peut pas \u00eatre utilis\u00e9 pour initialiser le nom.',		
		'CRRUI0006E' : 'Le groupe {0} RadioGroup contient un argument interdit.',
					
		'CRRUI1001E' : 'Pour la variable {0}, {1} ne peut pas \u00eatre d\u00e9fini sur {2}',
		'CRRUI1010E' : 'Les fonctions de glisser-d\u00e9poser sont des r\u00e9f\u00e9rences de fonction, et non des tableaux : {0}',
		'CRRUI1020E' : 'Runtime.asDictionary : la conversion en dictionnaires n\'est possible que pour les gestionnaires et les enregistrements, et non pour les {0}',
		'CRRUI1030E' : 'Impossible de trouver une d\u00e9finition pour {0}.{1}',
		'CRRUI1050E' : 'La variable {0} doit inclure le mot \"null\".',
		'CRRUI1051E' : 'Le widget ne peut pas \u00eatre ajout\u00e9 \u00e0 la variable {0}.',
		'CRRUI1055E' : 'L\'enfant {1} ne peut pas \u00eatre ajout\u00e9 \u00e0 la variable {2}.',		
		'CRRUI1057E' : 'Le widget {0} ne peut pas s\'ajouter lui-m\u00eame.',	
		'CRRUI1058E' : 'L\'anc\u00eatre {1} ne peut pas \u00eatre ajout\u00e9 \u00e0 la variable {0}. {2} est l\'anc\u00eatre actuel.',
		'CRRUI1060E' : 'Tentative de d\u00e9finition du parent du widget \"{0}\" sur {1}:{2} ; le parent doit \u00eatre un widget',
		'CRRUI1070E' : 'Une exception {0} s\'est produite lors du traitement de la fonction de rappel. Utilisez try...OnException.',
		'CRRUI1071E' : 'Aucun gestionnaire d\'exceptions n\'a \u00e9t\u00e9 trouv\u00e9 pour l\'appel de service. Ajoutez un gestionnaire d\'exceptions pour l\'appel de service.',
		'CRRUI1072E' : 'Une exception {0} s\'est produite dans la fonction de rappel d\'erreur, code avec try...OnException',		
		'CRRUI1080E' : 'Le widget ne contient pas d\'\u00e9l\u00e9ment DOM (mod\u00e8le d\'objet de document) et ne figure pas dans le document.<br>`Les attributs actuels de ce widget sont :<P>{0}',
		'CRRUI1083E' : 'Une erreur s\'est produite lors de la gestion de l\'\u00e9v\u00e9nement de navigateur {0}.',
		'CRRUI1150E' : 'Une tentative de suppression de \"null\" \u00e0 partir d\'une variable {0} a \u00e9chou\u00e9.',		
		'CRRUI1151E' : 'Une tentative de suppression d\'un widget d\u00e9pourvu d\'\u00e9l\u00e9ment DOM d\'une variable {0} a \u00e9chou\u00e9.',		
		'CRRUI1155E' : 'L\'enfant de type {1} ne peut pas \u00eatre supprim\u00e9 d\'une variable {0} : {2}.',
		'CRRUI1157E' : 'Un widget ne peut se supprimer lui-m\u00eame. Le type de widget est {0}.',
			
		'CRRUI2002E' : '{1}<br>Une erreur s\'est produite \u00e0 l\'int\u00e9rieur de {0} : {2}',
		'CRRUI2004E' : 'L\'historique {0} contient un argument interdit.',
		'CRRUI2005E' : 'Une r\u00e9f\u00e9rence nulle ne peut pas \u00eatre utilis\u00e9e.',
		'CRRUI2006E' : '{0}', // The text of this message comes from some other error message.
		'CRRUI2007E' : 'Argument ill\u00e9gal : {0}.',
		'CRRUI2009E' : 'L\'\u00e9v\u00e9nement ne peut pas \u00eatre instanci\u00e9 \u00e0 partir d\'un gestionnaire RUI.',
		'CRRUI2010E' : 'Fonction d\u00e9pr\u00e9ci\u00e9e : {0}.',
		
		'CRRUI2015E' : 'Le fichier {0} pour RUIPropertiesLibrary {1} est manquant',
		'CRRUI2016E' : 'Le programme EGL utilise trop de temps',
		'CRRUI2017E' : 'La valeur \"{0}\" du type {1} ne peut pas \u00eatre convertie en type {2}',
		'CRRUI2018E' : 'D\u00e9bordement lors de l\'affectation de {0} au type {1}',
		'CRRUI2019E' : 'Impossible d\'ajouter l\'\u00e9l\u00e9ment {0} au tableau. La taille maximale du tableau est {1}',
		'CRRUI2020E' : 'Argument non valide {0} transmis \u00e0 la fonction de tableau, setMaxSize()',
		'CRRUI2021E' : 'Impossible d\'extraire l\'unit\u00e9 syntaxique suivante de la cha\u00eene commen\u00e7ant \u00e0 l\'index {0}',
		'CRRUI2022E' : 'L\'index {0} est hors limites pour ce tableau. La taille du tableau est {1}',
		'CRRUI2023E' : 'Une r\u00e9f\u00e9rence nulle a \u00e9t\u00e9 utilis\u00e9e : {0}',
		'CRRUI2024E' : 'L\'acc\u00e8s dynamique \u00e0 la cl\u00e9 \"{0}\" n\'est pas valide sur un objet du type {1}',
		'CRRUI2025E' : 'L\'acc\u00e8s dynamique a \u00e9chou\u00e9 : cl\u00e9 inexistante : \"{0}\"',
		'CRRUI2030E' : 'Argument non valide pass\u00e9 \u00e0 XMLLib.convertFromXML. Une cha\u00eene (String) \u00e9tait attendue, mais \"{0}\" a \u00e9t\u00e9 re\u00e7u.',
		'CRRUI2031E' : 'Une erreur est survenue lors de l\'analyse syntaxique XML : {0}',
		'CRRUI2032E' : 'Le masque d\'horodate {0} n\'est pas valide',
		'CRRUI2033E' : 'La valeur {0} sp\u00e9cifi\u00e9e pour la fonction de tableau, resize(), est sup\u00e9rieure \u00e0 la taille maximale du tableau, {1}',
		'CRRUI2034E' : 'Le type {0} pour l\'\u00e9l\u00e9ment de tableau n\'est pas valide',
		'CRRUI2035E' : 'Le nombre de dimensions redimensionn\u00e9es est sup\u00e9rieur au nombre de dimensions du tableau',
		'CRRUI2036E' : 'Erreur de domaine dans l\'appel \u00e0 {0} : l\'argument doit \u00eatre compris entre {1} et {2}',
		'CRRUI2037E' : 'Erreur math\u00e9matique : impossible de diviser un nombre par 0',
		'CRRUI2038E' : 'Erreur de domaine dans l\'appel \u00e0 {0} : l\'argument doit \u00eatre sup\u00e9rieur \u00e0 z\u00e9ro',
		'CRRUI2039E' : 'Erreur de domaine dans l\'appel \u00e0 {0} : l\'argument doit \u00eatre sup\u00e9rieur ou \u00e9gal \u00e0 z\u00e9ro',
		'CRRUI2040E' : 'Erreur de domaine dans l\'appel \u00e0 {0} : l\'exposant pour une base z\u00e9ro doit \u00eatre sup\u00e9rieur \u00e0 z\u00e9ro',
		'CRRUI2041E' : 'Erreur de domaine dans l\'appel \u00e0 {0} : l\'exposant pour une base n\u00e9gative doit \u00eatre un entier',
		'CRRUI2042E' : 'Index de sous-cha\u00eene non valide {0}:{1}.',
		'CRRUI2050E' : 'Erreur math\u00e9matique : abs() - {0} arguments fournis alors que 0 ou 1 \u00e9taient attendus',
		'CRRUI2051E' : 'Erreur math\u00e9matique : add() - {0} arguments fournis alors que 1 ou 2 \u00e9taient attendus',
		'CRRUI2052E' : 'Erreur math\u00e9matique : compareTo() - {0} arguments fournis alors que 1 ou 2 \u00e9taient attendus',
		'CRRUI2053E' : 'Erreur math\u00e9matique : divide() - L\'\u00e9chelle n\u00e9gative {0} n\'est pas valide',
		'CRRUI2054E' : 'Erreur math\u00e9matique : divide() - {0} arguments fournis alors que 1 \u00e0 3 \u00e9taient attendus',
		'CRRUI2055E' : 'Erreur math\u00e9matique : divideInteger() - {0} arguments fournis alors que 1 ou 2 \u00e9taient attendus',
		'CRRUI2056E' : 'Erreur math\u00e9matique : max() - {0} arguments fournis alors que 1 ou 2 \u00e9taient attendus',
		'CRRUI2057E' : 'Erreur math\u00e9matique : min() - {0} arguments fournis alors que 1 ou 2 \u00e9taient attendus',
		'CRRUI2058E' : 'Erreur math\u00e9matique : multiply() - {0} arguments fournis alors que 1 ou 2 \u00e9taient attendus',
		'CRRUI2059E' : 'Erreur math\u00e9matique : negate() - {0} arguments fournis alors que 0 ou 1 \u00e9taient attendus',
		'CRRUI2060E' : 'Erreur math\u00e9matique : plus() - {0} arguments fournis alors que 0 ou 1 \u00e9taient attendus',
		'CRRUI2061E' : 'Erreur math\u00e9matique : pow() - {0} arguments fournis alors que 1 ou 2 \u00e9taient attendus',
		'CRRUI2062E' : 'Erreur math\u00e9matique : pow() - Puissance n\u00e9gative, {0}',
		'CRRUI2063E' : 'Erreur math\u00e9matique : pow() - Trop de chiffres, {0}',
		'CRRUI2064E' : 'Erreur math\u00e9matique : remainder() - {0} arguments fournis alors que 1 ou 2 \u00e9taient attendus',
		'CRRUI2065E' : 'Erreur math\u00e9matique : subtract() - {0} arguments fournis alors que 1 ou 2 \u00e9taient attendus',
		'CRRUI2066E' : 'Erreur math\u00e9matique : format() - {0} arguments fournis alors que 2 ou 6 \u00e9taient attendus',
		'CRRUI2067E' : 'Erreur math\u00e9matique : format() - D\u00e9bordement de l\'exposant, {0}',
		'CRRUI2068E' : 'Erreur math\u00e9matique : intValueExact() - Partie d\u00e9cimale diff\u00e9rente de z\u00e9ro, {0}',
		'CRRUI2069E' : 'Erreur math\u00e9matique : intValueExact() - D\u00e9bordement de conversion, {0}',
		'CRRUI2070E' : 'Erreur math\u00e9matique : setScale() - {0} arguments fournis alors que 1 ou 2 \u00e9taient attendus',
		'CRRUI2071E' : 'Erreur math\u00e9matique : setScale() - Echelle n\u00e9gative, {0}',
		'CRRUI2072E' : 'Erreur math\u00e9matique : intCheck() - Erreur de conversion, {0}',
		'CRRUI2073E' : 'Erreur math\u00e9matique : dodivide() - D\u00e9bordement d\'entier',
		'CRRUI2074E' : 'Erreur math\u00e9matique : La cha\u00eene \"{1}\" n\'a pas pu \u00eatre convertie en nombre',
		'CRRUI2075E' : 'Erreur math\u00e9matique : L\'argument num\u00e9ro {0} pass\u00e9 \u00e0 la m\u00e9thode {1} n\'est pas valide. L\'argument  fourni est {2}',
		'CRRUI2076E' : 'Erreur math\u00e9matique : Trop de chiffres - {0}',
		'CRRUI2077E' : 'Erreur math\u00e9matique : round() - {0} arguments fournis alors que 1 ou 2 \u00e9taient attendus',
		'CRRUI2078E' : 'Erreur math\u00e9matique : round() - Arrondi n\u00e9cessaire',
		'CRRUI2079E' : 'Erreur math\u00e9matique : round() - Valeur d\'arrondi incorrecte, {0}',
		'CRRUI2080E' : 'Erreur math\u00e9matique : round() - L\'exposant {0} entra\u00eene un d\u00e9bordement',
		'CRRUI2081E' : 'Erreur math\u00e9matique : finish() - L\'exposant {0} entra\u00eene un d\u00e9bordement',
		'CRRUI2082E' : 'Erreur interne : Lors de l\'appel du constructeur pour {0}',
		'CRRUI2083E' : 'Erreur interne : Probl\u00e8me rencontr\u00e9 lors de la d\u00e9finition de la classe {0}',
		'CRRUI2084E' : 'Erreur interne : Lors de la d\u00e9finition du widget {0}.{1} comme sous-classe de egl.ui.RUI.RUIPropertiesLibrary',
		'CRRUI2085E' : 'Erreur interne : Lors de la d\u00e9finition du widget {0}.{1} comme sous-classe de {2}.{3}',
		'CRRUI2086E' : 'Erreur interne : Probl\u00e8me rencontr\u00e9 lors de la d\u00e9finition du gestionnaire Rich UI {0}',
		'CRRUI2087E' : 'Erreur interne : Probl\u00e8me rencontr\u00e9 lors de la d\u00e9finition du widget Rich UI {0}',
		'CRRUI2088E' : 'Ce navigateur n\'est pas pris en charge par l\'interface utilisateur riche (Rich UI) EGL',
		'CRRUI2089E' : 'Conversion impossible \u00e0 partir du format JSON : "{0}", en raison de {1}',
		'CRRUI2090E' : 'Impossible d\'appeler le service : {0}',
		'CRRUI2091E' : 'Impossible d\'envoyer un \u00e9v\u00e9nement \u00e0 l\'IDE Eclipse : {0}',
		'CRRUI2092E' : 'Erreur interne : Probl\u00e8me rencontr\u00e9 lors du traitement d\'un \u00e9v\u00e9nement de l\'IDE : {0}',
		'CRRUI2093E' : 'Erreur interne : Impossible d\'instrumenter la fonction {0}',
		'CRRUI2094E' : 'Voici les appels de fonction EGL qui conduisent \u00e0 cette erreur :',
		'CRRUI2095E' : 'Impossible de trouver les appels de fonction EGL qui conduisent \u00e0 cette erreur',
		'CRRUI2097E' : 'Valeur de style CSS "{1}" non valide pour l\'attribut {0}',
		'CRRUI2097E' : 'Impossible d\'interpr\u00e9ter correctement le style CSS {0}. V\u00e9rifiez la syntaxe ou utilisez une feuille de style externe.',
		'CRRUI2098E' : 'Une erreur s\'est produite durant la prise en charge du glisser-d\u00e9poser : {0}',
		'CRRUI2099E' : 'Dans une interface utilisateur riche (Rich UI), l\'op\u00e9ration EGL "set" n\'est pas prise en charge sur le type {0}',
		'CRRUI2100E' : 'Argument non valide pass\u00e9 \u00e0 RuiLib.convertFromXML. Une cha\u00eene (String) \u00e9tait attendue, mais un objet du type {0} a \u00e9t\u00e9 re\u00e7u.',
		'CRRUI2101E' : 'Impossible de localiser indexOf "{1}" \u00e0 l\'int\u00e9rieur de "{0}" en raison de {2}',
		'CRRUI2102E' : 'Le tableau n\'a pas pu \u00eatre tri\u00e9',
		'CRRUI2103E' : 'Acc\u00e8s ill\u00e9gal \u00e0 "{0}" dans un objet du type "{1}" en raison de {2}',
		'CRRUI2104E' : 'Impossible d\'interpr\u00e9ter la cha\u00eene JSON "{0}"',
		'CRRUI2105E' : 'Erreur d\'analyse syntaxique JSON en tentant de d\u00e9finir {0}. Cette zone n\'existe pas \u00e0 l\'endroit attendu dans la cha\u00eene JSON.',
		'CRRUI2106E' : 'Echec de la tentative de mise \u00e0 \'null\' de {0} lors de l\'analyse syntaxique JSON, car il n\'a pas \u00e9t\u00e9 d\u00e9clar\u00e9 comme zone nullable.',
		'CRRUI2107E' : 'Les fonctions de conversion JSON de la biblioth\u00e8que de services s\'appliquent \u00e0 un enregistrement ou \u00e0 un dictionnaire. {0} n\'est pas un type pris en charge.',
		'CRRUI2108E' : 'Les fonctions de conversion de la biblioth\u00e8que XML s\'appliquent \u00e0 un enregistrement. {0} n\'est pas un type pris en charge.',
		
		'CRRUI2111E' : 'Erreur dans MathContext() : le nombre d\'arguments sp\u00e9cifi\u00e9s, {0}, n\'est pas valide ; la fonction en  attend de 1 \u00e0 4.',
		'CRRUI2112E' : 'Erreur dans MathContext() : le nombre de chiffres sp\u00e9cifi\u00e9s, {0}, est trop petit.',
		'CRRUI2113E' : 'Erreur dans MathContext() : le nombre de chiffres sp\u00e9cifi\u00e9s, {0}, est trop grand.',
		'CRRUI2114E' : 'Erreur dans MathContext() : la valeur de format sp\u00e9cifi\u00e9e, {0}, n\'est pas valide.',
		'CRRUI2115E' : 'Erreur dans MathContext() : la valeur de mode d\'arrondi sp\u00e9cifi\u00e9e, {0}, n\'est pas valide.',
		
	    'CRRUI2700E' : 'Aucune entr\u00e9e re\u00e7ue pour une zone obligatoire. Recommencez la saisie.',
		'CRRUI2702E' : 'Erreur de type de donn\u00e9es dans l\'entr\u00e9e. Recommencez la saisie.',
		'CRRUI2703E' : 'D\u00e9passement du nombre de chiffres significatifs autoris\u00e9. Recommencez la saisie.',
		'CRRUI2704E' : 'Entr\u00e9e non comprise dans la plage d\u00e9finie de {0} \u00e0 {1}. Recommencez la saisie.',
		'CRRUI2705E' : 'La longueur minimale entr\u00e9e est erron\u00e9e. Recommencez la saisie.',
		'CRRUI2707E' : 'Erreur de validit\u00e9 du modulo dans l\'entr\u00e9e. Recommencez la saisie.',
		'CRRUI2708E' : 'Entr\u00e9e non valide compte tenu du format de date ou d\'heure d\u00e9fini, {0}.',
		'CRRUI2710E' : 'Entr\u00e9e non valide pour une zone bool\u00e9enne.',
		'CRRUI2712E' : 'Donn\u00e9es hexad\u00e9cimales non valides.',
		'CRRUI2713E' : 'La valeur entr\u00e9e n\'est pas valide, car elle ne correspond pas au masque d\u00e9fini.',		
		'CRRUI2716E' : 'Entr\u00e9e non comprise dans la liste d\u00e9finie de valeurs admises. Recommencez la saisie.',
		'CRRUI2717E' : 'Le format de date et d\'heure sp\u00e9cifi\u00e9, {0}, n\'est pas valide.',	
		'CRRUI2719E' : 'Erreur \u00e0 l\'interpr\u00e9tation de la valeur d\'entr\u00e9e.',

		'CRRUI3650E' : 'Impossible de trouver le fichier de descripteur de d\u00e9ploiement : \'{0}\'',
		'CRRUI3651E' : 'La cl\u00e9 de liaison de service \'{0}\' n\'existe pas dans le descripteur de d\u00e9ploiement \'{1}\'',
		'CRRUI3652E' : '\'{0}\' est un type de liaison de service incorrect ; le type de liaison attendu est \'{1}\'',
		'CRRUI3653E' : 'Erreur lors d\'une tentative d\'invocation du service REST sur \'{0}\'',
		'CRRUI3654E' : 'Erreur lors de la g\u00e9n\u00e9ration de l\'objet de demande : \'{0}\'',
		'CRRUI3655E' : 'Erreur lors du traitement de l\'objet de r\u00e9ponse : \'{0}\'',
		'CRRUI3656E' : '\'formdata\' n\'est pas accept\u00e9 comme format de r\u00e9ponse',
		'CRRUI3657E' : 'Proxy introuvable \u00e0 \'{0}\' pour envoyer les appels au service',
		'CRRUI3658E' : 'Une erreur s\'est produite sur le proxy \u00e0 \'{0}\' lors d\'une tentative d\'appel du service sur \'{1}\'',
		'CRRUI3659E' : 'L\'interpr\u00e9tation de la r\u00e9ponse JSON a eu pour r\u00e9sultat \'null\', la r\u00e9ponse d\'origine est : \'{0}\'',
		'CRRUI3660E' : 'Une exception s\'est produite, impossible de traiter la r\u00e9ponse pour \'{0}\', motif : \'{1}\'',
		'CRRUI3661E' : 'Informations de liaison de service manquantes pour \'{0}\' lors d\'une tentative d\'appel de la fonction de service \'{1}\'.'
};

