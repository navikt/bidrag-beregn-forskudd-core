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
0.6.0   | Endret            | Endring av grensesnitt for å få med mer informasjon om sjabloner
0.5.0   | Endret            | Endring av grensesnitt mot rest-tjenesten (referanser). Oppdatert versjoner. Noe refaktorering.
0.4.10  | Endret            | Liten justering på måten 250% forskudd beregnes. Oppdatert tester til å gjenspeile de nyeste sjablonsatsene.
0.4.9   | Endret            | Liten justering på måten 125% forskudd beregnes
0.4.8   | Endret            | Litt justeringer på beregningsreglene, med utgangspunkt i sjablon 00038 i stedet for 0005
0.4.7   | Endret            | Endret på teksten i resultatkodene. Resultatbeløp avrundes nå til nærmeste tier.
0.4.6   | Endret            | Oppdatert til siste versjon av bidrag-beregn-felles. Endret fra Double til BigDecimal.
0.4.5   | Endret            | Skrevet om ForskuddPerideImpl pga caching-problematikk (feil bruk av instanc-variabler)
0.4.4   | Endret            | Debug
0.4.3   | Endret            | Debug
0.4.2   | Endret            | Debug
0.4.1   | Opprettet         | Lagt til validering av inntekter
0.4.0   | Endret            | Endret måten sjabloner legges ut på i resultatgrunnlaget (inneholder nå navn/verdi på de som faktisk er brukt)
0.3.0   | Endret            | Lagt inn ny måte å håndtere sjabloner på og ny validering av datoer + mindre endringer
0.2.5   | Endret            | Forbedret input-kontroll på datoer (rettelse av feil oppdaget i forrige versjon)
0.2.4   | Endret            | Forbedret input-kontroll på datoer
0.2.3   | Endret            | Beregn-til-dato lagt til i periodisering, likt de andre beregningene
0.2.2   | Endret            | Nyere versjoner av bidrag-beregn-felles og springboot
0.2.1   | Endret            | Rettet feil knyttet til periodehåndtering / inntekt
0.2.0   | Opprettet         | Lagt til mulighet for å ha flere inntekter + sortering av datolister
0.1.1   | Endret            | Flyttet ENUMs til felles
0.1.0   | Endret/slettet    | Lagt til grunnlaget for beregningen som en del av resultatet, samt fjernet merge av like perioder
0.0.8   | Endret            | Refaktorert kode og skrevet om dataklasser
0.0.7   | Opprettet         | Lagt til dokumentasjon av modulen i README.md
0.0.6   | Endret            | Justert coredto til å tillate null-verdi på periodeDatoTil
0.0.5   | Opprettet         | Lagt inn avvikshåndtering / inputkontroll
0.0.4   | Endret            | Gjort om dataklasser til Kotlin, omstrukturert og lagd mer logiske navn
0.0.3   | Opprettet         | Lagt til håndtering av sjablonverdier mottatt fra kallende tjeneste
0.0.2   | Opprettet         | Lagt til Kotlin + nytt ytre lag for å skille mellom ekstern og intern DTO
0.0.1   | Opprettet         | Første versjon
