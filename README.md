# bidrag-beregn-forskudd-core

![](https://github.com/navikt/bidrag-beregn-forskudd-core/workflows/maven%20deploy/badge.svg)

Maven-modul som inneholder forretningsregler for beregning og periodisering av bidragsforskudd.

### Funksjonalitet
Modulen tar inn parametre knyttet til bidragsmottaker og barnet det søkes om bidrag for, samt sjablonverdier som er relevante for forskuddsberegning. Modulen er delt i to nivåer; periodisering og beregning.

#### Periodisering
Det sjekkes hvilken periode (fra-/til-dato) de ulike inputparametrene gjelder for. Det gjøres en kontroll av at datoene er gyldige. Deretter samles alle datoer i en liste og det dannes bruddperioder. For hver bruddperiode finnes gjeldende verdier for de aktuelle parametrene og det foretas en beregning av forskudd. Resultatet som returneres er enten en liste av forskuddsberegninger eller en liste av avvik (hvis det er feil).

#### Forskuddsberegning
Forskudd beregnes basert på inputdata, sjablonverdier og et sett med regler. Regelverket er beskrevet i eget regneark. Ut fra forskuddsberegningen kommer det et beløp, hvilken regel som er brukt og en bekrivelse av regelen.

### Sikkerhet
Det er ingen sikkerhet, da tjenesten ikke behandler sensitive data.


## Changelog:

Versjon | Endringstype      | Beskrivelse
--------|-------------------|------------
0.0.7   |  Endring          | Lagt til dokumentasjon av modulen i README.md
0.0.6   |  Endring          | Justert coredto til å tillate null-verdi på periodeDatoTil
0.0.5   |  Endring          | Lagt inn avvikshåndtering / inputkontroll
0.0.4   |  Endring          | Gjort om dataklasser til Kotlin, omstrukturert og lagd mer logiske navn
0.0.3   |  Endring          | Lagt til håndtering av sjablonverdier mottatt fra kallende tjeneste
0.0.2   |  Endring          | Lagt til Kotlin + nytt ytre lag for å skille mellom ekstern og intern DTO
0.0.1   |  Opprettet        | Første versjon
