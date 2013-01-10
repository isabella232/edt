000100120305     H NOMAIN
000101120305
000102120305     D USANULL         DS                  qualified
000103120611     D  dMonth                        2s 0
000104120611     D  dDay                          2s 0
000105120611     D  dYear                         4s 0
000106120305
000107120611     D TIMENULLSEP     DS
000108120611     D  tHour                         2s 0
000109120611     D  tMin                          2s 0
000110120611     D  tSec                          2s 0
000111120606
000112120305
000113120305     D RECORD1         DS                  qualified
000114120305     D   c1                          20a
000115120606     D   c2                          20a
000116120606     d   uc1                         20c
000117120606     d   uc2                         20c
000118120606     D   i1                           3i 0
000119120305     d   i2                           5i 0
000120120305     d   i4                          10i 0
000121120305     d   i8                          20i 0
000122120305     D   p174                        17p 4
000123120305     D   p80                          8p 0
000124120306     D   z150                        15S 0
000125120306     D   z112                        11S 2
000126120306     D   u1                           3u 0
000127120305     d   u2                           5u 0
000128120305     d   u4                          10u 0
000129120305     d   u8                          20u 0
000130120611     D   d1                            d   datfmt(*ISO)
000131120611     D   d2                            d   datfmt(*EUR)
000132120611     D   d3                                likeds(USANULL)
000133120611     D   t1                            t   timfmt(*ISO)
000134120611     D   t2                            t   timfmt(*EUR)
000135120611     D   t3                                likeds(TIMENULLSEP)
000136120606     D   ts                            z
000137120306     D   f4                           4f
000138120305     D   f8                           8f
000139120305
000140120306     D RECORD11        DS                  qualified
000141120306     D   rec                                likeds(RECORD1)
000142120306     D   c1                          20a   Dim(4)
000143120606     D   c2                          20a   Dim(4)
000144120606     d   uc1                         20c   Dim(4)
000145120606     d   uc2                         20c   Dim(4)
000146120606     D   i1                           3i 0 Dim(4)
000147120306     d   i2                           5i 0 Dim(4)
000148120306     d   i4                          10i 0 Dim(4)
000149120306     d   i8                          20i 0 Dim(4)
000150120306     D   p174                        17p 4 Dim(4)
000151120306     D   p80                          8p 0 Dim(4)
000152120306     D   z150                        15S 0 Dim(4)
000153120306     D   z112                        11S 2 Dim(4)
000154120306     D   u1                           3u 0 Dim(4)
000155120306     d   u2                           5u 0 Dim(4)
000156120306     d   u4                          10u 0 Dim(4)
000157120306     d   u8                          20u 0 Dim(4)
000158120611     D   d1                            d   datfmt(*ISO) Dim(4)
000168120611     D   d2                            d   datfmt(*EUR) Dim(4)
000169120611     D   d3                                likeds(USANULL) Dim(4)
000170120611     D   t1                            t   timfmt(*ISO) Dim(4)
000171120611     D   t2                            t   timfmt(*EUR) Dim(4)
000172120611     D   t3                                likeds(TIMENULLSEP) Dim(4)
000173120606     D   ts                            z   Dim(4)
000174120306     D   f4                           4f   Dim(4)
000175120306     D   f8                           8f   Dim(4)
000176120306     D   recA                               likeds(RECORD1) dim(4)
000177120306     d   cnt                         10i 0
000178120306
000179120305
000180120306     D PRECS           PR
000181120306     D recS                                 likeds(Record11) DIM(5)
000182120306     d cnt                           10i 0
000183120306
000184120222
000185120222
000186120306     P PRECS           B                   export
000187120306     D                 PI
000188120306
000189120306     D recS                                 likeds(Record11) DIM(5)
000190120306     d cnt                           10i 0
000191120306     d x               S              5i 0
000192120306     d y               S              5i 0
000193120306      /free
000194120306       FOR x = 1 TO 2;
000195120306         recs(x).rec.i1 = recs(x).rec.i1 + 98;
000196120306         recs(x).rec.i2 = recs(x).rec.i2 + 998;
000197120306         recs(x).rec.i4 = recs(x).rec.i4 + 9998;
000198120306         recs(x).rec.i8 = recs(x).rec.i8 + 99998;
000199120306         recs(x).rec.u1 = recs(x).rec.u1 + 98;
000200120306         recs(x).rec.u2 = recs(x).rec.u2 + 998;
000201120306         recs(x).rec.u4 = recs(x).rec.u4 + 9998;
000202120306         recs(x).rec.u8 = recs(x).rec.u8 + 99998;
000203120306         recs(x).rec.f4 = recs(x).rec.f4 + 99e-2;
000204120306         recs(x).rec.f8 = recs(x).rec.f8 + 99e-2;
000205120306         recs(x).rec.z112 = 98 + recs(x).rec.z112;
000206120306         recs(x).rec.z150 = 998 + recs(x).rec.z150;
000207120306         recs(x).rec.p174 = recs(x).rec.p174 + 9998;
000208120306         recs(x).rec.p80 = recs(x).rec.p80 + 99998;
000209120306         recs(x).rec.c1 = %trimr(recs(x).rec.c1) + 'YYYYY';
000210120606         recs(x).rec.c2 = %trimr(recs(x).rec.c2) + 'YYYYY';
000211120606         recs(x).rec.uc1 = %subst(recs(x).rec.uc1:1:4) + %ucs2('ZZZZZZZZZZ');
000212120606         recs(x).rec.uc2 = %subst(recs(x).rec.uc2:1:4) + %ucs2('ZZZZZZZZZZ');
000213120611         recs(x).rec.d1 = recs(x).rec.d1 +
000214120306                              %days(5) + %months(5) + %years(5);
000215120611         recs(x).rec.d2 = recs(x).rec.d2 +
000216120306                              %days(5) + %months(5) + %years(5);
000217120611         recs(x).rec.d3.dMonth = recs(x).rec.d3.dMonth + 5;
000219120611         recs(x).rec.d3.dDay = recs(x).rec.d3.dDay + 5;
000220120611         recs(x).rec.d3.dYear = recs(x).rec.d3.dYear + 5;
000222120611         recs(x).rec.t1 = recs(x).rec.t1 +
000223120606                              %hours(5) + %minutes(5) + %seconds(5);
000224120611         recs(x).rec.t2 = recs(x).rec.t2 +
000225120606                              %hours(5) + %minutes(5) + %seconds(5);
000226120611         recs(x).rec.t3.tHour = recs(x).rec.t3.tHour + 5;
000228120611         recs(x).rec.t3.tMin =  recs(x).rec.t3.tMin + 5;
000229120611         recs(x).rec.t3.tSec = recs(x).rec.t3.tSec + 5;
000231120606         recs(x).rec.ts = recs(x).rec.ts + %days(5) + %months(5) + %years(5);
000232120606         recs(x).rec.ts = recs(x).rec.ts +
000233120306                              %hours(5) + %minutes(5) + %seconds(5);
000234120306         FOR y = 1 TO 2;
000271120306           recs(x).i1(y) = recs(x).i1(y) + 98;
000272120306           recs(x).i2(y) = recs(x).i2(y) + 998;
000273120306           recs(x).i4(y) = recs(x).i4(y) + 9998;
000274120306           recs(x).i8(y) = recs(x).i8(y) + 99998;
000275120306           recs(x).u1(y) = recs(x).u1(y) + 98;
000276120306           recs(x).u2(y) = recs(x).u2(y) + 998;
000277120306           recs(x).u4(y) = recs(x).u4(y) + 9998;
000278120306           recs(x).u8(y) = recs(x).u8(y) + 99998;
000279120306           recs(x).f4(y) = recs(x).f4(y) + 99e-2;
000280120306           recs(x).f8(y) = recs(x).f8(y) + 99e-2;
000281120306           recs(x).z112(y) = 98 + recs(x).z112(y);
000282120306           recs(x).z150(y) = 998 + recs(x).z150(y);
000283120306           recs(x).p174(y) = recs(x).p174(y) + 9998;
000284120306           recs(x).p80(y) = recs(x).p80(y) + 99998;
000285120306           recs(x).c1(y) = %trimr(recs(x).c1(y)) + 'YYYYY';
000286120606           recs(x).c2(y) = %trimr(recs(x).c2(y)) + 'YYYYY';
000287120606           recs(x).uc1(y) = %subst(recs(x).uc1(y):1:4) +
000288120306                                  %ucs2('ZZZZZZZZZZ');
000289120606           recs(x).uc2(y) = %subst(recs(x).uc2(y):1:4) +
000290120606                                  %ucs2('ZZZZZZZZZZ');
000291120611           recs(x).d1(y) = recs(x).d1(y) +
000292120306                              %days(5) + %months(5) + %years(5);
000293120611           recs(x).d2(y) = recs(x).d2(y) +
000294120306                              %days(5) + %months(5) + %years(5);
000295120611           recs(x).d3(y).dMonth = recs(x).d3(y).dMonth + 5;
000297120611           recs(x).d3(y).dDay = recs(x).d3(y).dDay + 5;
000299120611           recs(x).d3(y).dYear = recs(x).d3(y).dYear + 5;
000301120611           recs(x).t1(y) = recs(x).t1(y) +
000302120606                              %hours(5) + %minutes(5) + %seconds(5);
000303120611           recs(x).t2(y) = recs(x).t2(y) +
000304120606                              %hours(5) + %minutes(5) + %seconds(5);
000305120611           recs(x).t3(y).tHour = recs(x).t3(y).tHour + 5;
000307120611           recs(x).t3(y).tMin = recs(x).t3(y).tMin + 5;
000309120611           recs(x).t3(y).tSec = recs(x).t3(y).tSec + 5;
000311120606           recs(x).ts(y) = recs(x).ts(y) +
000312120306                      %days(5) + %months(5) + %years(5);
000313120606           recs(x).ts(y) = recs(x).ts(y) +
000314120306                              %hours(5) + %minutes(5) + %seconds(5);
000315120306           recs(x).recA(y).i1 = recs(x).recA(y).i1 + 98;
000316120306           recs(x).recA(y).i2 = recs(x).recA(y).i2 + 998;
000317120306           recs(x).recA(y).i4 = recs(x).recA(y).i4 + 9998;
000318120306           recs(x).recA(y).i8 = recs(x).recA(y).i8 + 99998;
000319120306           recs(x).recA(y).u1 = recs(x).recA(y).u1 + 98;
000320120306           recs(x).recA(y).u2 = recs(x).recA(y).u2 + 998;
000321120306           recs(x).recA(y).u4 = recs(x).recA(y).u4 + 9998;
000322120306           recs(x).recA(y).u8 = recs(x).recA(y).u8 + 99998;
000323120306           recs(x).recA(y).f4 = recs(x).recA(y).f4 + 99e-2;
000324120306           recs(x).recA(y).f8 = recs(x).recA(y).f8 + 99e-2;
000325120306           recs(x).recA(y).z112 = 98 + recs(x).recA(y).z112;
000326120306           recs(x).recA(y).z150 = 998 + recs(x).recA(y).z150;
000327120306           recs(x).recA(y).p174 = recs(x).recA(y).p174 + 9998;
000328120306           recs(x).recA(y).p80 = recs(x).recA(y).p80 + 99998;
000329120612           recs(x).recA(y).c1 = %trimr(recs(x).recA(y).c1) + 'YYYYY';
000330120612           recs(x).recA(y).c2 = %trimr(recs(x).recA(y).c2) + 'YYYYY';
000331120612           recs(x).recA(y).uc1 = %subst(recs(x).recA(y).uc1:1:4) +
000332120612                                 %ucs2('ZZZZZZZZZZ');
000333120612           recs(x).recA(y).uc2 = %subst(recs(x).recA(y).uc2:1:4) +
000334120612                                 %ucs2('ZZZZZZZZZZ');
000335120612           recs(x).recA(y).d1 = recs(x).recA(y).d1 +
000336120306                              %days(5) + %months(5) + %years(5);
000337120612           recs(x).recA(y).d2 = recs(x).recA(y).d2 +
000338120306                              %days(5) + %months(5) + %years(5);
000339120612           recs(x).recA(y).d3.dMonth = recs(x).recA(y).d3.dMonth + 5;
000340120612           recs(x).recA(y).d3.dDay = recs(x).recA(y).d3.dDay + 5;
000341120612           recs(x).recA(y).d3.dYear = recs(x).recA(y).d3.dYear + 5;
000342120612           recs(x).recA(y).t1 = recs(x).recA(y).t1 +
000350120612                              %hours(5) + %minutes(5) + %seconds(5);
000351120612           recs(x).recA(y).t2 = recs(x).recA(y).t2 +
000352120612                              %hours(5) + %minutes(5) + %seconds(5);
000353120612           recs(x).recA(y).t3.tHour = recs(x).recA(y).t3.tHour + 5;
000354120612           recs(x).recA(y).t3.tMin = recs(x).recA(y).t3.tMin + 5;
000355120612           recs(x).recA(y).t3.tSec = recs(x).recA(y).t3.tSec + 5;
000356120612           recs(x).recA(y).ts = recs(x).recA(y).ts +
000357120612                      %days(5) + %months(5) + %years(5);
000358120612           recs(x).recA(y).ts = recs(x).recA(y).ts +
000359120612                              %hours(5) + %minutes(5) + %seconds(5);
000360120612         ENDFOR;
000361120306         recs(x).cnt = 2;
000362120306       ENDFOR;
000363120306       cnt = 2;
000364120222      /end-free
000365120306     p PRECS           e
000366120222
