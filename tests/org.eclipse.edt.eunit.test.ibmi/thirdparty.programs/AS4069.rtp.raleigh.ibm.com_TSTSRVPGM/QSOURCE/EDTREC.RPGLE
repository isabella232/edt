000100120305     H NOMAIN
000101120305
000102120305     D USANULL         DS                  qualified
000103120611     D  dMonth                        2s 0
000104120611     D  dDay                          2s 0
000105120611     D  dYear                         4s 0
000106120305
000107120611     D TIMENULLSEP     DS                  qualified
000108120611     D  tHour                         2s 0
000109120611     D  tMin                          2s 0
000110120611     D  tSec                          2s 0
000111120606
000112120606
000113120305     D RECORD1         DS                  qualified
000114120606     D   c1                          20a
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
000137120305     D   f4                           4f
000138120305     D   f8                           8f
000139120305
000140120305
000141120305     D PREC            PR
000142120305     D rec                                 likeds(Record1)
000143120305
000165120222
000166120222
000167120305     P PREC            B                   export
000168120221     D                 PI
000169120305     D rec                                 likeds(Record1)
000170120305      /free
000171120306       rec.i1 = rec.i1 + 98;
000175120305       rec.i2 = rec.i2 + 998;
000176120305       rec.i4 = rec.i4 + 9998;
000177120305       rec.i8 = rec.i8 + 99998;
000178120305       rec.u1 = rec.u1 + 98;
000190120305       rec.u2 = rec.u2 + 998;
000191120305       rec.u4 = rec.u4 + 9998;
000192120305       rec.u8 = rec.u8 + 99998;
000193120305       rec.f4 = rec.f4 + 99e-2;
000204120305       rec.f8 = rec.f8 + 99e-2;
000205120306       rec.z112 = 98 + rec.z112;
000217120306       rec.z150 = 998 + rec.z150;
000218120305       rec.p174 = rec.p174 + 9998;
000230120305       rec.p80 = rec.p80 + 99998;
000231120305       rec.c1 = %trimr(rec.c1) + 'YYYYY';
000243120606       rec.c2 = %trimr(rec.c2) + 'YYYYY';
000244120606       rec.uc1 = %subst(rec.uc1:1:4) + %ucs2('ZZZZZZZZZZ');
000245120606       rec.uc2 = %subst(rec.uc2:1:4) + %ucs2('ZZZZZZZZZZ');
000246120611       rec.d1 = rec.d1 + %days(5) + %months(5) + %years(5);
000255120611       rec.d2 = rec.d2 + %days(5) + %months(5) + %years(5);
000256120611       rec.d3.dMonth = rec.d3.dMonth + 5;
000257120611       rec.d3.dDay =  rec.d3.dDay + 5;
000258120611       rec.d3.dYear = rec.d3.dYear + 5;
000259120611       rec.t1 = rec.t1 + %hours(5) + %minutes(5) + %seconds(5);
000260120611       rec.t2 = rec.t2 + %hours(5) + %minutes(5) + %seconds(5);
000261120611       rec.t3.tHour = rec.t3.tHour + 5;
000262120611       rec.t3.tMin = rec.t3.tMin + 5;
000263120611       rec.t3.tSec = rec.t3.tSec + 5;
000264120606       rec.ts = rec.ts + %days(5) + %months(5) + %years(5);
000268120305       rec.ts = rec.ts + %hours(5) + %minutes(5) + %seconds(5);
000269120222      /end-free
000270120305     p PREC            e
000271120222
