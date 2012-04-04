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
		'CRRUI0001E' : 'O nome InitalUI[{0}]InitialUI est\u00e1 vazio. A chamada de fun\u00e7\u00e3o n\u00e3o pode ser usada para inicializar o nome.',		
		'CRRUI0006E' : 'O grupo {0} RadioGroup cont\u00e9m um argumento ilegal.',
					
		'CRRUI1001E' : 'Para a vari\u00e1vel {0}, {1} n\u00e3o pode ser configurado como {2}',
		'CRRUI1010E' : 'As fun\u00e7\u00f5es arrastar e soltar s\u00e3o refer\u00eancias de fun\u00e7\u00f5es, n\u00e3o matrizes: {0}',
		'CRRUI1020E' : 'Runtime.asDictionary: s\u00f3 \u00e9 poss\u00edvel converter manipuladores ou registros para Dicion\u00e1rios, n\u00e3o {0}',
		'CRRUI1030E' : 'N\u00e3o foi poss\u00edvel localizar uma defini\u00e7\u00e3o para {0}.{1}',
		'CRRUI1050E' : 'A vari\u00e1vel {0} deve incluir a palavra \"null\".',
		'CRRUI1051E' : 'O widget n\u00e3o pode ser inclu\u00eddo na vari\u00e1vel {0}.',
		'CRRUI1055E' : 'O filho {1} n\u00e3o pode ser inclu\u00eddo na vari\u00e1vel {2}.',		
		'CRRUI1057E' : 'O widget {0} n\u00e3o pode ser inclu\u00eddo em si mesmo.',	
		'CRRUI1058E' : 'O ancestral {1} n\u00e3o pode ser inclu\u00eddo na vari\u00e1vel {0}. {2} \u00e9 o ancestral atual.',
		'CRRUI1060E' : 'Tentativa de configurar o pai para o widget \"{0}\" como {1}:{2}; O pai precisa ser um widget',
		'CRRUI1070E' : 'Ocorreu uma exce\u00e7\u00e3o {0} ao processar a fun\u00e7\u00e3o de retorno de chamada. Use o try...OnException.',
		'CRRUI1071E' : 'Nenhum manipulador de exce\u00e7\u00f5es localizado para a chamada de servi\u00e7o. Inclua um manipulador de exce\u00e7\u00f5es para a chamada de servi\u00e7o.',
		'CRRUI1072E' : 'Ocorreu uma exce\u00e7\u00e3o {0} na fun\u00e7\u00e3o de retorno de chamada de erro, c\u00f3digo com try...OnException',		
		'CRRUI1080E' : 'O widget n\u00e3o possui um elemento de modelo de objeto do documento (DOM) e n\u00e3o est\u00e1 no documento.<br>Os atributos atuais para este widget s\u00e3o:<P>{0}',
		'CRRUI1083E' : 'Ocorreu um erro enquanto o evento do navegador {0} estava sendo tratado.',
		'CRRUI1150E' : 'Uma tentativa de remover \"null\" de uma vari\u00e1vel {0} falhou.',		
		'CRRUI1151E' : 'Uma tentativa de remover o widget que n\u00e3o possui um elemento modelo de objeto do documento (DOM) de uma vari\u00e1vel {0} falhou.',		
		'CRRUI1155E' : 'O filho do tipo {1} n\u00e3o pode ser removido de um {0}: vari\u00e1vel {2}.',
		'CRRUI1157E' : 'Um widget n\u00e3o pode ser removido de si mesmo. O tipo do widget \u00e9 {0}.',
			
		'CRRUI2002E' : '{1}<br>Ocorreu um erro em {0}: {2}',
		'CRRUI2004E' : 'O hist\u00f3rico {0} cont\u00e9m um argumento ilegal.',
		'CRRUI2005E' : 'Uma refer\u00eancia nula n\u00e3o pode ser usada.',
		'CRRUI2006E' : '{0}', // The text of this message comes from some other error message.
		'CRRUI2007E' : 'Argumento inv\u00e1lido: {0}.',
		'CRRUI2009E' : 'O evento n\u00e3o pode ser instanciado a partir de um manipulador RUI.',
		'CRRUI2010E' : 'Fun\u00e7\u00e3o reprovada: {0}.',
		
		'CRRUI2015E' : 'O arquivo {0} para RUIPropertiesLibrary {1} est\u00e1 ausente',
		'CRRUI2016E' : 'O programa EGL est\u00e1 usando muito tempo',
		'CRRUI2017E' : 'O valor \"{0}\" do tipo {1} n\u00e3o pode ser convertido no tipo {2}',
		'CRRUI2018E' : 'Estouro ao designar {0} ao tipo {1}',
		'CRRUI2019E' : 'N\u00e3o \u00e9 poss\u00edvel anexar o elemento {0} \u00e0 matriz. O tamanho m\u00e1ximo da matriz \u00e9 {1}',
		'CRRUI2020E' : 'Argumento inv\u00e1lido {0} para a fun\u00e7\u00e3o de matriz, setMaxSize()',
		'CRRUI2021E' : 'N\u00e3o \u00e9 poss\u00edvel recuperar o pr\u00f3ximo token da cadeia iniciando no \u00edndice {0}',
		'CRRUI2022E' : 'O \u00edndice {0} est\u00e1 fora dos limites para esta matriz. O tamanho da matriz \u00e9 {1}',
		'CRRUI2023E' : 'Foi utilizada uma refer\u00eancia nula: {0}',
		'CRRUI2024E' : 'O acesso din\u00e2mico \u00e0 chave \"{0}\" n\u00e3o \u00e9 v\u00e1lido em um objeto do tipo {1}',
		'CRRUI2025E' : 'Falha no acesso din\u00e2mico: n\u00e3o existe essa chave, \"{0}\"',
		'CRRUI2030E' : 'Argumento inv\u00e1lido para XMLLib.convertFromXML. Esperava-se uma cadeia. Foi recebido \"{0}\"',
		'CRRUI2031E' : 'Ocorreu um erro ao analisar XML: {0}',
		'CRRUI2032E' : 'O padr\u00e3o de registro de data e hora {0} \u00e9 inv\u00e1lido',
		'CRRUI2033E' : 'O valor {0} especificado para a fun\u00e7\u00e3o de matriz, resize(), \u00e9 maior que o tamanho m\u00e1ximo da matriz, {1}',
		'CRRUI2034E' : 'O tipo {0} para o elemento da matriz n\u00e3o \u00e9 v\u00e1lido',
		'CRRUI2035E' : 'O n\u00famero de dimens\u00f5es redimensionadas \u00e9 maior que o n\u00famero de dimens\u00f5es da matriz',
		'CRRUI2036E' : 'Erro de dom\u00ednio na chamada para {0}: o argumento deve estar entre {1} e {2}',
		'CRRUI2037E' : 'Erro de matem\u00e1tica: n\u00e3o \u00e9 poss\u00edvel dividir um n\u00famero por 0',
		'CRRUI2038E' : 'Erro de dom\u00ednio na chamada para {0}: o argumento deve ser maior que zero',
		'CRRUI2039E' : 'Erro de dom\u00ednio na chamada para {0}: o argumento deve ser maior ou igual a zero',
		'CRRUI2040E' : 'Erro de dom\u00ednio na chamada para {0}: o expoente de uma base zero deve ser maior que zero',
		'CRRUI2041E' : 'Erro de dom\u00ednio na chamada para {0}: o expoente de uma base negativa deve ser um inteiro',
		'CRRUI2042E' : '\u00cdndices da subsequ\u00eancia inv\u00e1lidos {0}:{1}.',
		'CRRUI2050E' : 'Erro de matem\u00e1tica: abs() - {0} argumentos especificados. Esperava-se 0 ou 1',
		'CRRUI2051E' : 'Erro de matem\u00e1tica: add() - {0} argumentos especificados. Esperava-se 1 ou 2',
		'CRRUI2052E' : 'Erro de matem\u00e1tica: compareTo() - {0} argumentos especificados. Esperava-se 1 ou 2',
		'CRRUI2053E' : 'Erro de matem\u00e1tica: divide() - A escala negativa {0} n\u00e3o \u00e9 v\u00e1lida',
		'CRRUI2054E' : 'Erro de matem\u00e1tica: divide() - {0} argumentos especificados. Esperava-se entre 1 e 3',
		'CRRUI2055E' : 'Erro de matem\u00e1tica: divideInteger() - {0} argumentos especificados. Esperava-se 1 ou 2',
		'CRRUI2056E' : 'Erro de matem\u00e1tica: max() - {0} argumentos especificados. Esperava-se 1 ou 2',
		'CRRUI2057E' : 'Erro de matem\u00e1tica: min() - {0} argumentos especificados. Esperava-se 1 ou 2',
		'CRRUI2058E' : 'Erro de matem\u00e1tica: multiply() - {0} argumentos especificados. Esperava-se 1 ou 2',
		'CRRUI2059E' : 'Erro de matem\u00e1tica: negate() - {0} argumentos especificados. Esperava-se 0 ou 1',
		'CRRUI2060E' : 'Erro de matem\u00e1tica: plus() - {0} argumentos especificados. Esperava-se 0 ou 1',
		'CRRUI2061E' : 'Erro de matem\u00e1tica: pow() - {0} argumentos especificados. Esperava-se 1 ou 2',
		'CRRUI2062E' : 'Erro de matem\u00e1tica: pow() - Pot\u00eancia negativa, {0}',
		'CRRUI2063E' : 'Erro de matem\u00e1tica: pow() - Excesso de d\u00edgitos, {0}',
		'CRRUI2064E' : 'Erro de matem\u00e1tica: remainder() - {0} argumentos especificados. Esperava-se 1 ou 2',
		'CRRUI2065E' : 'Erro de matem\u00e1tica: subtract() - {0} argumentos especificados. Esperava-se 1 ou 2',
		'CRRUI2066E' : 'Erro de matem\u00e1tica: format() - {0} argumentos fornecidos. Esperava-se 2 ou 6',
		'CRRUI2067E' : 'Erro de matem\u00e1tica: format() - estouro de expoente, {0}',
		'CRRUI2068E' : 'Erro de matem\u00e1tica: intValueExact() - Parte decimal n\u00e3o zero, {0}',
		'CRRUI2069E' : 'Erro de matem\u00e1tica: intValueExact() - estouro na convers\u00e3o, {0}',
		'CRRUI2070E' : 'Erro de matem\u00e1tica: setScale() - {0} argumentos especificados. Esperava-se 1 ou 2',
		'CRRUI2071E' : 'Erro de matem\u00e1tica: setScale() - escala negativa, {0}',
		'CRRUI2072E' : 'Erro de matem\u00e1tica: intCheck() - erro na convers\u00e3o, {0}',
		'CRRUI2073E' : 'Erro de matem\u00e1tica: dodivide() - estouro de inteiro, {0}',
		'CRRUI2074E' : 'Erro de matem\u00e1tica: N\u00e3o foi poss\u00edvel converter a cadeia \"{1}\" em um n\u00famero',
		'CRRUI2075E' : 'Erro de matem\u00e1tica: O n\u00famero do argumento {0} para o m\u00e9todo {1} n\u00e3o \u00e9 v\u00e1lido.  O argumento fornecido \u00e9 {2}',
		'CRRUI2076E' : 'Erro de matem\u00e1tica: Excesso de d\u00edgitos - {0}',
		'CRRUI2077E' : 'Erro de matem\u00e1tica: round() - {0} argumentos especificados. Esperava-se 1 ou 2',
		'CRRUI2078E' : 'Erro de matem\u00e1tica: round() - Arredondamento necess\u00e1rio',
		'CRRUI2079E' : 'Erro de matem\u00e1tica: round() - Valor de arredondamento inv\u00e1lido, {0}',
		'CRRUI2080E' : 'Erro de matem\u00e1tica: round() - Estouro no expoente {0}',
		'CRRUI2081E' : 'Erro de matem\u00e1tica: finish() - Estouro no expoente {0}',
		'CRRUI2082E' : 'Erro Interno: Ao chamar o construtor para {0}',
		'CRRUI2083E' : 'Erro Interno: Ocorreu um problema ao definir a classe {0}',
		'CRRUI2084E' : 'Erro Interno: Ao definir o widget {0}.{1} como uma subclasse de egl.ui.rui.RUIPropertiesLibrary',
		'CRRUI2085E' : 'Erro Interno: Ao definir o widget {0}.{1} como uma subclasse de {2}.{3}',
		'CRRUI2086E' : 'Erro Interno: Ocorreu um problema ao definir o Manipulador RUI {0}',
		'CRRUI2087E' : 'Erro Interno: Ocorreu um problema ao definir o Widget RUI {0}',
		'CRRUI2088E' : 'Este navegador n\u00e3o \u00e9 suportado pelo EGL Rich UI',
		'CRRUI2089E' : 'N\u00e3o foi poss\u00edvel converter do formato JSON: "{0}", devido a {1}',
		'CRRUI2090E' : 'N\u00e3o foi poss\u00edvel chamar o servi\u00e7o: {0}',
		'CRRUI2091E' : 'N\u00e3o \u00e9 poss\u00edvel enviar o evento para o Eclipse IDE: {0}',
		'CRRUI2092E' : 'Erro Interno: Aconteceu um problema ao manipular um evento IDE {0}',
		'CRRUI2093E' : 'Erro Interno: N\u00e3o foi poss\u00edvel instrumentar a fun\u00e7\u00e3o {0}',
		'CRRUI2094E' : 'Aqui est\u00e3o as chamadas de fun\u00e7\u00e3o EGL que levam a este erro:',
		'CRRUI2095E' : 'N\u00e3o foi poss\u00edvel localizar as chamadas de fun\u00e7\u00e3o EGL que levam a este erro',
		'CRRUI2097E' : 'Valor de estilo CSS inv\u00e1lido "{1}" para o atributo {0}',
		'CRRUI2097E' : 'N\u00e3o foi poss\u00edvel analisar corretamente o estilo CSS {0}. Verifique a sintaxe ou utilize uma folha de estilo externa.',
		'CRRUI2098E' : 'Aconteceu um erro durante o suporte a Arrastar e Soltar: {0}',
		'CRRUI2099E' : 'No Rich UI, a opera\u00e7\u00e3o "set" do EGL n\u00e3o \u00e9 suportada no tipo {0}',
		'CRRUI2100E' : 'Argumento inv\u00e1lido para RuiLib.convertFromXML. Esperava-se uma cadeia. Foi recebido um objeto do tipo {0}',
		'CRRUI2101E' : 'N\u00e3o \u00e9 poss\u00edvel localizar indexOf "{1}" dentro de "{0}", devido a {2}',
		'CRRUI2102E' : 'N\u00e3o foi poss\u00edvel classificar a Matriz',
		'CRRUI2103E' : 'Acesso inv\u00e1lido a "{0}" no objeto do tipo "{1}", devido a {2}',
		'CRRUI2104E' : 'N\u00e3o foi poss\u00edvel analisar a cadeia JSON "{0}"',
		'CRRUI2105E' : 'Ocorreu um erro de an\u00e1lise JSON ao tentar configurar {0}. O campo n\u00e3o existe no local esperado na cadeia JSON.',
		'CRRUI2106E' : 'Uma tentativa de configurar {0} como nulo falhou ao analisar o JSON porque ele n\u00e3o foi declarado como um campo anul\u00e1vel.',
		'CRRUI2107E' : 'As fun\u00e7\u00f5es de convers\u00e3o de JSON da biblioteca de servi\u00e7os operam em um Registro ou Dicion\u00e1rio. {0} n\u00e3o \u00e9 um tipo suportado.',
		'CRRUI2108E' : 'As fun\u00e7\u00f5es de convers\u00e3o de XML da biblioteca XML operam em um Registro. {0} n\u00e3o \u00e9 um tipo suportado.',
		
		'CRRUI2111E' : 'Erro de MathContext(): O n\u00famero de argumentos especificado, {0}, n\u00e3o \u00e9 v\u00e1lido; esperava-se 1 a 4.',
		'CRRUI2112E' : 'Erro de MathContext(): O n\u00famero de d\u00edgitos especificado, {0}, \u00e9 muito baixo.',
		'CRRUI2113E' : 'Erro de MathContext(): O n\u00famero de d\u00edgitos especificado, {0}, \u00e9 muito alto.',
		'CRRUI2114E' : 'Erro de MathContext(): O valor de formul\u00e1rio especificado, {0}, n\u00e3o \u00e9 v\u00e1lido.',
		'CRRUI2115E' : 'Erro de MathContext(): O valor do modo de arredondamento especificado, {0}, n\u00e3o \u00e9 v\u00e1lido.',
		
	    'CRRUI2700E' : 'Nenhuma entrada recebida para o campo necess\u00e1rio - digite novamente.',
		'CRRUI2702E' : 'Erro no tipo de dados na entrada - digite novamente.',
		'CRRUI2703E' : 'N\u00famero de d\u00edgitos significativos permitido excedido - digite novamente.',
		'CRRUI2704E' : 'A entrada n\u00e3o est\u00e1 dentro do intervalo definido de {0} a {1} - digite novamente.',
		'CRRUI2705E' : 'Erro de comprimento m\u00ednimo de entrada - digite novamente.',
		'CRRUI2707E' : 'Erro de verifica\u00e7\u00e3o de m\u00f3dulo na entrada - digite novamente.',
		'CRRUI2708E' : 'A entrada n\u00e3o \u00e9 v\u00e1lida para o formato definido de data e hora {0}.',
		'CRRUI2710E' : 'Entrada n\u00e3o v\u00e1lida para campo booleano.',
		'CRRUI2712E' : 'Dados hexadecimais n\u00e3o s\u00e3o v\u00e1lidos.',
		'CRRUI2713E' : 'O valor digitado \u00e9 inv\u00e1lido pois n\u00e3o corresponde ao padr\u00e3o que est\u00e1 definido.',		
		'CRRUI2716E' : 'A entrada n\u00e3o est\u00e1 dentro da lista definida de valores v\u00e1lidos - digite novamente.',
		'CRRUI2717E' : 'O formato especificado de data e hora de {0} n\u00e3o \u00e9 v\u00e1lido.',	
		'CRRUI2719E' : 'Erro ao analisar o valor de entrada.',

		'CRRUI3650E' : 'N\u00e3o \u00e9 poss\u00edvel localizar o arquivo descritor de implementa\u00e7\u00e3o : \'{0}\'',
		'CRRUI3651E' : 'A chave de liga\u00e7\u00e3o de servi\u00e7o: \'{0}\' n\u00e3o existe no descritor de implementa\u00e7\u00e3o \'{1}\'',
		'CRRUI3652E' : 'Tipo de liga\u00e7\u00e3o de servi\u00e7o incorreto \'{0}\'. Esperando o tipo de liga\u00e7\u00e3o \'{1}\'',
		'CRRUI3653E' : 'Ocorreu um erro ao tentar chamar o servi\u00e7o rest em \'{0}\'',
		'CRRUI3654E' : 'Ocorreu um erro ao construir o objeto de pedido: \'{0}\'',
		'CRRUI3655E' : 'Ocorreu um erro ao processar um objeto de resposta: \'{0}\'',
		'CRRUI3656E' : '\'formdata\' n\u00e3o \u00e9 suportado como formato de resposta',
		'CRRUI3657E' : 'N\u00e3o \u00e9 poss\u00edvel localizar um proxy em \'{0}\' para fazer chamadas de servi\u00e7o',
		'CRRUI3658E' : 'Ocorreu um erro no proxy em \'{0}\' ao tentar chamar o servi\u00e7o em \'{1}\'',
		'CRRUI3659E' : 'A an\u00e1lise da resposta de Json resultou em nulo; a resposta original \u00e9: \'{0}\'',
		'CRRUI3660E' : 'Ocorreu uma exce\u00e7\u00e3o, n\u00e3o foi poss\u00edvel tratar da resposta para \'{0}\'; motivo: \'{1}\'',
		'CRRUI3661E' : 'As informa\u00e7\u00f5es sobre liga\u00e7\u00e3o de servi\u00e7o est\u00e3o ausentes para \'{0}\' durante a tentativa de invocar a fun\u00e7\u00e3o do servi\u00e7o \'{1}\'.'
};

