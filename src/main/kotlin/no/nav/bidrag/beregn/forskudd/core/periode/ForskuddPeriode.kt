package no.nav.bidrag.beregn.forskudd.core.periode

import no.nav.bidrag.beregn.felles.PeriodeUtil
import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.periode.Periodiserer
import no.nav.bidrag.beregn.forskudd.core.beregning.ForskuddBeregning
import no.nav.bidrag.beregn.forskudd.core.bo.Alder
import no.nav.bidrag.beregn.forskudd.core.bo.AlderPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.BarnIHusstanden
import no.nav.bidrag.beregn.forskudd.core.bo.BarnIHusstandenPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddListeGrunnlag
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat
import no.nav.bidrag.beregn.forskudd.core.bo.Bostatus
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.Sivilstand
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters.firstDayOfMonth
import java.time.temporal.TemporalAdjusters.firstDayOfNextMonth
import java.util.function.Consumer
import java.util.stream.Collectors.toCollection

open class ForskuddPeriode(private val forskuddBeregning: ForskuddBeregning) {
    open fun beregnPerioder(periodeGrunnlag: BeregnForskuddGrunnlag): BeregnForskuddResultat {
        val beregnForskuddListeGrunnlag = BeregnForskuddListeGrunnlag()

        // Juster datoer
        justerDatoerGrunnlagslister(periodeGrunnlag, beregnForskuddListeGrunnlag)

        // Lag bruddperioder
        lagBruddperioder(periodeGrunnlag, beregnForskuddListeGrunnlag)

        // Foreta beregning
        beregnForskuddPerPeriode(beregnForskuddListeGrunnlag)
        return BeregnForskuddResultat(beregnForskuddListeGrunnlag.periodeResultatListe)
    }

    // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)
    private fun justerDatoerGrunnlagslister(periodeGrunnlag: BeregnForskuddGrunnlag, beregnForskuddListeGrunnlag: BeregnForskuddListeGrunnlag) {

        beregnForskuddListeGrunnlag.justertInntektPeriodeListe = periodeGrunnlag.inntektPeriodeListe.stream()
            .map { InntektPeriode(it) }
            .collect(toCollection { ArrayList() })

        beregnForskuddListeGrunnlag.justertSivilstandPeriodeListe = periodeGrunnlag.sivilstandPeriodeListe.stream()
            .map { SivilstandPeriode(it) }
            .collect(toCollection { ArrayList() })

        beregnForskuddListeGrunnlag.justertBarnIHusstandenPeriodeListe = periodeGrunnlag.barnIHusstandenPeriodeListe.stream()
            .map { BarnIHusstandenPeriode(it) }
            .collect(toCollection { ArrayList() })

        beregnForskuddListeGrunnlag.justertBostatusPeriodeListe = periodeGrunnlag.bostatusPeriodeListe.stream()
            .map { BostatusPeriode(it) }
            .collect(toCollection { ArrayList() })

        beregnForskuddListeGrunnlag.justertAlderPeriodeListe = settBarnAlderPerioder(
            periodeGrunnlag.soknadBarn.fodselsdato, periodeGrunnlag.beregnDatoFra, periodeGrunnlag.beregnDatoTil
        ).stream()
            .map { AlderPeriode(periodeGrunnlag.soknadBarn.referanse, it.alderPeriode, it.alder) }
            .collect(toCollection { ArrayList() })

        beregnForskuddListeGrunnlag.justertSjablonPeriodeListe = periodeGrunnlag.sjablonPeriodeListe.stream()
            .map { SjablonPeriode(it) }
            .collect(toCollection { ArrayList() })
    }

    // Lagger bruddperioder ved å løpe gjennom alle periodelistene
    private fun lagBruddperioder(periodeGrunnlag: BeregnForskuddGrunnlag, beregnForskuddListeGrunnlag: BeregnForskuddListeGrunnlag) {

        // Bygger opp liste over perioder, basert på alle typer inputparametre
        beregnForskuddListeGrunnlag.bruddPeriodeListe = Periodiserer()
            .addBruddpunkt(periodeGrunnlag.beregnDatoFra) //For å sikre bruddpunkt på start beregning fra-dato
            .addBruddpunkter(beregnForskuddListeGrunnlag.justertInntektPeriodeListe)
            .addBruddpunkter(beregnForskuddListeGrunnlag.justertSivilstandPeriodeListe)
            .addBruddpunkter(beregnForskuddListeGrunnlag.justertBarnIHusstandenPeriodeListe)
            .addBruddpunkter(beregnForskuddListeGrunnlag.justertBostatusPeriodeListe)
            .addBruddpunkter(beregnForskuddListeGrunnlag.justertAlderPeriodeListe)
            .addBruddpunkter(beregnForskuddListeGrunnlag.justertSjablonPeriodeListe)
            .addBruddpunkt(periodeGrunnlag.beregnDatoTil) //For å sikre bruddpunkt på start beregning til-dato
            .finnPerioder(periodeGrunnlag.beregnDatoFra, periodeGrunnlag.beregnDatoTil)
            .toMutableList()

        // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
        val bruddPeriodeListeAntallElementer = beregnForskuddListeGrunnlag.bruddPeriodeListe.size
        if (bruddPeriodeListeAntallElementer > 1) {
            val nestSisteTilDato = beregnForskuddListeGrunnlag.bruddPeriodeListe[bruddPeriodeListeAntallElementer - 2].datoTil
            val sisteTilDato = beregnForskuddListeGrunnlag.bruddPeriodeListe[bruddPeriodeListeAntallElementer - 1].datoTil
            if (periodeGrunnlag.beregnDatoTil == nestSisteTilDato && null == sisteTilDato) {
                val nyPeriode = Periode(
                    beregnForskuddListeGrunnlag.bruddPeriodeListe[bruddPeriodeListeAntallElementer - 2].datoFom, null
                )
                beregnForskuddListeGrunnlag.bruddPeriodeListe.removeAt(bruddPeriodeListeAntallElementer - 1)
                beregnForskuddListeGrunnlag.bruddPeriodeListe.removeAt(bruddPeriodeListeAntallElementer - 2)
                beregnForskuddListeGrunnlag.bruddPeriodeListe.add(nyPeriode)
            }
        }
    }

    // Løper gjennom alle bruddperioder og foretar beregning
    private fun beregnForskuddPerPeriode(beregnForskuddListeGrunnlag: BeregnForskuddListeGrunnlag) {

        // Løper gjennom periodene og finner matchende verdi for hver kategori
        // Kaller beregningsmodulen for hver beregningsperiode

        beregnForskuddListeGrunnlag.bruddPeriodeListe.forEach(Consumer { beregningsperiode: Periode ->
            val inntektListe = beregnForskuddListeGrunnlag.justertInntektPeriodeListe.stream()
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Inntekt(it.referanse, it.type, it.belop) }
                .toList()
            val sivilstand = beregnForskuddListeGrunnlag.justertSivilstandPeriodeListe.stream()
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Sivilstand(it.referanse, it.kode) }
                .findFirst()
                .orElseThrow { IllegalArgumentException("Grunnlagsobjekt SIVILSTAND mangler data for periode: " + beregningsperiode.getPeriode()) }
            val alder = beregnForskuddListeGrunnlag.justertAlderPeriodeListe.stream()
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Alder(it.referanse, it.alder) }
                .findFirst()
                .orElseThrow { IllegalArgumentException("Ikke mulig å beregne søknadsbarnets alder for periode: " + beregningsperiode.getPeriode()) }
            val bostatus = beregnForskuddListeGrunnlag.justertBostatusPeriodeListe.stream()
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Bostatus(it.referanse, it.kode) }
                .findFirst()
                .orElseThrow { IllegalArgumentException("Grunnlagsobjekt BOSTATUS mangler data for periode: " + beregningsperiode.getPeriode()) }
            val barnIHusstanden = beregnForskuddListeGrunnlag.justertBarnIHusstandenPeriodeListe.stream()
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { BarnIHusstanden(it.referanse, it.antall) }
                .findFirst()
                .orElseThrow { IllegalArgumentException("Grunnlagsobjekt BARN_I_HUSSTAND mangler data for periode: " + beregningsperiode.getPeriode()) }
            val sjablonListe = beregnForskuddListeGrunnlag.justertSjablonPeriodeListe.stream()
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .toList()
            val grunnlagBeregning = GrunnlagBeregning(inntektListe, sivilstand, barnIHusstanden, alder, bostatus, sjablonListe)
            beregnForskuddListeGrunnlag.periodeResultatListe
                .add(ResultatPeriode(beregningsperiode, forskuddBeregning.beregn(grunnlagBeregning), grunnlagBeregning))
        })
    }

    // Deler opp i aldersperioder med utgangspunkt i fødselsdato
    private fun settBarnAlderPerioder(fodselDato: LocalDate, beregnDatoFra: LocalDate, beregnDatoTil: LocalDate): List<AlderPeriode> {
        val bruddAlderListe = ArrayList<AlderPeriode>()
        val barn11AarDato = fodselDato.plusYears(11).with(firstDayOfMonth())
        val barn18AarDato = fodselDato.plusYears(18).with(firstDayOfNextMonth())

        var alderStartPeriode = 0
        if (!barn11AarDato.isAfter(beregnDatoFra)) {
            alderStartPeriode = if (!barn18AarDato.isAfter(beregnDatoFra)) 18 else 11
        }

        // Barn fyller 11 år i perioden
        val barn11AarIPerioden = barn11AarDato.isAfter(beregnDatoFra.minusDays(1)) && barn11AarDato.isBefore(beregnDatoTil.plusDays(1))

        // Barn fyller 18 år i perioden
        val barn18AarIPerioden = barn18AarDato.isAfter(beregnDatoFra.minusDays(1)) && barn18AarDato.isBefore(beregnDatoTil.plusDays(1))
        if (barn11AarIPerioden) {
            bruddAlderListe.add(
                AlderPeriode(
                    "",
                    Periode(beregnDatoFra.with(firstDayOfMonth()), barn11AarDato.with(firstDayOfMonth())),
                    0
                )
            )
            if (barn18AarIPerioden) {
                bruddAlderListe.add(
                    AlderPeriode(
                        "",
                        Periode(barn11AarDato.with(firstDayOfMonth()), barn18AarDato.with(firstDayOfMonth())),
                        11
                    )
                )
                bruddAlderListe.add(AlderPeriode("", Periode(barn18AarDato.with(firstDayOfMonth()), null), 18))
            } else {
                bruddAlderListe.add(AlderPeriode("", Periode(barn11AarDato.with(firstDayOfMonth()), null), 11))
            }
        } else {
            if (barn18AarIPerioden) {
                bruddAlderListe.add(
                    AlderPeriode(
                        "",
                        Periode(beregnDatoFra.with(firstDayOfMonth()), barn18AarDato.with(firstDayOfMonth())),
                        11
                    )
                )
                bruddAlderListe.add(AlderPeriode("", Periode(barn18AarDato.with(firstDayOfMonth()), null), 18))
            } else {
                bruddAlderListe.add(AlderPeriode("", Periode(beregnDatoFra.with(firstDayOfMonth()), null), alderStartPeriode))
            }
        }
        return bruddAlderListe
    }

    // Validerer at input-verdier til forskuddsberegning er gyldige
    open fun validerInput(grunnlag: BeregnForskuddGrunnlag): List<Avvik> {

        // Sjekk beregn dato fra/til
        val avvikListe = PeriodeUtil.validerBeregnPeriodeInput(grunnlag.beregnDatoFra, grunnlag.beregnDatoTil)

        // Sjekk perioder for inntekt
        val bidragMottakerInntektPeriodeListe = mutableListOf<Periode>()
        for (bidragMottakerInntektPeriode in grunnlag.inntektPeriodeListe) {
            bidragMottakerInntektPeriodeListe.add(bidragMottakerInntektPeriode.getPeriode())
        }
        avvikListe.addAll(
            PeriodeUtil.validerInputDatoer(
                grunnlag.beregnDatoFra, grunnlag.beregnDatoTil, "bidragMottakerInntektPeriodeListe", bidragMottakerInntektPeriodeListe,
                false, true, false, true
            )
        )

        // Sjekk perioder for sivilstand
        val bidragMottakerSivilstandPeriodeListe = mutableListOf<Periode>()
        for (bidragMottakerSivilstandPeriode in grunnlag.sivilstandPeriodeListe) {
            bidragMottakerSivilstandPeriodeListe.add(bidragMottakerSivilstandPeriode.getPeriode())
        }
        avvikListe.addAll(
            PeriodeUtil.validerInputDatoer(
                grunnlag.beregnDatoFra, grunnlag.beregnDatoTil, "bidragMottakerSivilstandPeriodeListe",
                bidragMottakerSivilstandPeriodeListe, true, true, true, true
            )
        )

        // Sjekk perioder for bostatus
        val soknadBarnBostatusPeriodeListe = mutableListOf<Periode>()
        for (soknadBarnBostatusPeriode in grunnlag.bostatusPeriodeListe) {
            soknadBarnBostatusPeriodeListe.add(soknadBarnBostatusPeriode.getPeriode())
        }
        avvikListe.addAll(
            PeriodeUtil.validerInputDatoer(
                grunnlag.beregnDatoFra, grunnlag.beregnDatoTil, "soknadBarnBostatusPeriodeListe", soknadBarnBostatusPeriodeListe,
                true, true, true, true
            )
        )

        // Sjekk perioder for barn
        val bidragMottakerBarnPeriodeListe = mutableListOf<Periode>()
        for (bidragMottakerBarnIHusstandenPeriode in grunnlag.barnIHusstandenPeriodeListe) {
            bidragMottakerBarnPeriodeListe.add(bidragMottakerBarnIHusstandenPeriode.getPeriode())
        }
        avvikListe.addAll(
            PeriodeUtil.validerInputDatoer(
                grunnlag.beregnDatoFra, grunnlag.beregnDatoTil, "bidragMottakerBarnPeriodeListe", bidragMottakerBarnPeriodeListe,
                false, false, false, false
            )
        )

        // Sjekk perioder for sjablonliste
        val sjablonPeriodeListe = mutableListOf<Periode>()
        for (sjablonPeriode in grunnlag.sjablonPeriodeListe) {
            sjablonPeriodeListe.add(sjablonPeriode.getPeriode())
        }
        avvikListe.addAll(
            PeriodeUtil
                .validerInputDatoer(
                    grunnlag.beregnDatoFra, grunnlag.beregnDatoTil, "sjablonPeriodeListe", sjablonPeriodeListe, false,
                    false, false, false
                )
        )
        return avvikListe
    }
}