package no.nav.bidrag.beregn.forskudd.core.periode

import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.periode.Periodiserer
import no.nav.bidrag.beregn.felles.util.PeriodeUtil
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

open class ForskuddPeriodeImpl(private val forskuddBeregning: ForskuddBeregning) : ForskuddPeriode {

    override fun beregnPerioder(grunnlag: BeregnForskuddGrunnlag): BeregnForskuddResultat {
        val beregnForskuddListeGrunnlag = BeregnForskuddListeGrunnlag()

        // Juster datoer
        justerDatoerGrunnlagslister(periodeGrunnlag = grunnlag, beregnForskuddListeGrunnlag = beregnForskuddListeGrunnlag)

        // Lag bruddperioder
        lagBruddperioder(periodeGrunnlag = grunnlag, beregnForskuddListeGrunnlag = beregnForskuddListeGrunnlag)

        // Foreta beregning
        beregnForskuddPerPeriode(beregnForskuddListeGrunnlag)

        return BeregnForskuddResultat(beregnForskuddListeGrunnlag.periodeResultatListe)
    }

    // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)
    private fun justerDatoerGrunnlagslister(periodeGrunnlag: BeregnForskuddGrunnlag, beregnForskuddListeGrunnlag: BeregnForskuddListeGrunnlag) {
        beregnForskuddListeGrunnlag.justertInntektPeriodeListe = periodeGrunnlag.inntektPeriodeListe
            .map { InntektPeriode(it) }

        beregnForskuddListeGrunnlag.justertSivilstandPeriodeListe = periodeGrunnlag.sivilstandPeriodeListe
            .map { SivilstandPeriode(it) }

        beregnForskuddListeGrunnlag.justertBarnIHusstandenPeriodeListe = periodeGrunnlag.barnIHusstandenPeriodeListe
            .map { BarnIHusstandenPeriode(it) }

        beregnForskuddListeGrunnlag.justertBostatusPeriodeListe = periodeGrunnlag.bostatusPeriodeListe
            .map { BostatusPeriode(it) }

        beregnForskuddListeGrunnlag.justertAlderPeriodeListe = settBarnAlderPerioder(
            fodselDato = periodeGrunnlag.soknadBarn.fodselsdato,
            beregnDatoFra = periodeGrunnlag.beregnDatoFra,
            beregnDatoTil = periodeGrunnlag.beregnDatoTil
        )
            .map { AlderPeriode(referanse = periodeGrunnlag.soknadBarn.referanse, alderPeriode = it.alderPeriode, alder = it.alder) }

        beregnForskuddListeGrunnlag.justertSjablonPeriodeListe = periodeGrunnlag.sjablonPeriodeListe
            .map { SjablonPeriode(it) }
    }

    // Lagger bruddperioder ved å løpe gjennom alle periodelistene
    private fun lagBruddperioder(periodeGrunnlag: BeregnForskuddGrunnlag, beregnForskuddListeGrunnlag: BeregnForskuddListeGrunnlag) {
        // Bygger opp liste over perioder, basert på alle typer inputparametre
        beregnForskuddListeGrunnlag.bruddPeriodeListe = Periodiserer()
            .addBruddpunkt(periodeGrunnlag.beregnDatoFra) // For å sikre bruddpunkt på start beregning fra-dato
            .addBruddpunkter(beregnForskuddListeGrunnlag.justertInntektPeriodeListe)
            .addBruddpunkter(beregnForskuddListeGrunnlag.justertSivilstandPeriodeListe)
            .addBruddpunkter(beregnForskuddListeGrunnlag.justertBarnIHusstandenPeriodeListe)
            .addBruddpunkter(beregnForskuddListeGrunnlag.justertBostatusPeriodeListe)
            .addBruddpunkter(beregnForskuddListeGrunnlag.justertAlderPeriodeListe)
            .addBruddpunkter(beregnForskuddListeGrunnlag.justertSjablonPeriodeListe)
            .addBruddpunkt(periodeGrunnlag.beregnDatoTil) // For å sikre bruddpunkt på start beregning til-dato
            .finnPerioder(beregnDatoFom = periodeGrunnlag.beregnDatoFra, beregnDatoTil = periodeGrunnlag.beregnDatoTil)
            .toMutableList()

        // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
        val bruddPeriodeListeAntallElementer = beregnForskuddListeGrunnlag.bruddPeriodeListe.size
        if (bruddPeriodeListeAntallElementer > 1) {
            val nestSisteTilDato = beregnForskuddListeGrunnlag.bruddPeriodeListe[bruddPeriodeListeAntallElementer - 2].datoTil
            val sisteTilDato = beregnForskuddListeGrunnlag.bruddPeriodeListe[bruddPeriodeListeAntallElementer - 1].datoTil
            if (periodeGrunnlag.beregnDatoTil == nestSisteTilDato && null == sisteTilDato) {
                val nyPeriode = Periode(
                    datoFom = beregnForskuddListeGrunnlag.bruddPeriodeListe[bruddPeriodeListeAntallElementer - 2].datoFom,
                    datoTil = null
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

        beregnForskuddListeGrunnlag.bruddPeriodeListe.forEach { beregningsperiode: Periode ->
            val inntektListe = beregnForskuddListeGrunnlag.justertInntektPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Inntekt(referanse = it.referanse, type = it.type, belop = it.belop) }

            val sivilstand = beregnForskuddListeGrunnlag.justertSivilstandPeriodeListe.stream()
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Sivilstand(referanse = it.referanse, kode = it.kode) }
                .findFirst()
                .orElseThrow { IllegalArgumentException("Grunnlagsobjekt SIVILSTAND mangler data for periode: ${beregningsperiode.getPeriode()}") }

            val alder = beregnForskuddListeGrunnlag.justertAlderPeriodeListe.stream()
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Alder(referanse = it.referanse, alder = it.alder) }
                .findFirst()
                .orElseThrow { IllegalArgumentException("Ikke mulig å beregne søknadsbarnets alder for periode: ${beregningsperiode.getPeriode()}") }

            val bostatus = beregnForskuddListeGrunnlag.justertBostatusPeriodeListe.stream()
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { Bostatus(referanse = it.referanse, kode = it.kode) }
                .findFirst()
                .orElseThrow { IllegalArgumentException("Grunnlagsobjekt BOSTATUS mangler data for periode: ${beregningsperiode.getPeriode()}") }

            val barnIHusstandenListe = beregnForskuddListeGrunnlag.justertBarnIHusstandenPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }
                .map { BarnIHusstanden(referanse = it.referanse) }

            val sjablonListe = beregnForskuddListeGrunnlag.justertSjablonPeriodeListe
                .filter { it.getPeriode().overlapperMed(beregningsperiode) }

            val grunnlagBeregning = GrunnlagBeregning(
                inntektListe = inntektListe,
                sivilstand = sivilstand,
                barnIHusstandenListe = barnIHusstandenListe,
                soknadBarnAlder = alder,
                soknadBarnBostatus = bostatus,
                sjablonListe = sjablonListe
            )

            beregnForskuddListeGrunnlag.periodeResultatListe
                .add(
                    ResultatPeriode(
                        periode = beregningsperiode,
                        resultat = forskuddBeregning.beregn(grunnlagBeregning),
                        grunnlag = grunnlagBeregning
                    )
                )
        }
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
                    referanse = "",
                    alderPeriode = Periode(datoFom = beregnDatoFra.with(firstDayOfMonth()), datoTil = barn11AarDato.with(firstDayOfMonth())),
                    alder = 0
                )
            )
            if (barn18AarIPerioden) {
                bruddAlderListe.add(
                    AlderPeriode(
                        referanse = "",
                        alderPeriode = Periode(datoFom = barn11AarDato.with(firstDayOfMonth()), datoTil = barn18AarDato.with(firstDayOfMonth())),
                        alder = 11
                    )
                )
                bruddAlderListe.add(
                    AlderPeriode(
                        referanse = "",
                        alderPeriode = Periode(datoFom = barn18AarDato.with(firstDayOfMonth()), datoTil = null),
                        alder = 18
                    )
                )
            } else {
                bruddAlderListe.add(
                    AlderPeriode(
                        referanse = "",
                        alderPeriode = Periode(datoFom = barn11AarDato.with(firstDayOfMonth()), datoTil = null),
                        alder = 11
                    )
                )
            }
        } else {
            if (barn18AarIPerioden) {
                bruddAlderListe.add(
                    AlderPeriode(
                        referanse = "",
                        alderPeriode = Periode(datoFom = beregnDatoFra.with(firstDayOfMonth()), datoTil = barn18AarDato.with(firstDayOfMonth())),
                        alder = 11
                    )
                )
                bruddAlderListe.add(
                    AlderPeriode(
                        referanse = "",
                        alderPeriode = Periode(datoFom = barn18AarDato.with(firstDayOfMonth()), datoTil = null),
                        alder = 18
                    )
                )
            } else {
                bruddAlderListe.add(
                    AlderPeriode(
                        referanse = "",
                        alderPeriode = Periode(datoFom = beregnDatoFra.with(firstDayOfMonth()), datoTil = null),
                        alder = alderStartPeriode
                    )
                )
            }
        }
        return bruddAlderListe
    }

    // Validerer at input-verdier til forskuddsberegning er gyldige
    override fun validerInput(grunnlag: BeregnForskuddGrunnlag): List<Avvik> {
        // Sjekk beregn dato fra/til
        val avvikListe =
            PeriodeUtil.validerBeregnPeriodeInput(beregnDatoFra = grunnlag.beregnDatoFra, beregnDatoTil = grunnlag.beregnDatoTil).toMutableList()

        // Sjekk perioder for inntekt
        avvikListe.addAll(
            PeriodeUtil.validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "bidragMottakerInntektPeriodeListe",
                periodeListe = grunnlag.inntektPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = false,
                sjekkOpphold = true,
                sjekkNull = false,
                sjekkBeregnPeriode = true
            )
        )

        // Sjekk perioder for sivilstand
        avvikListe.addAll(
            PeriodeUtil.validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "bidragMottakerSivilstandPeriodeListe",
                periodeListe = grunnlag.sivilstandPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = true,
                sjekkOpphold = true,
                sjekkNull = true,
                sjekkBeregnPeriode = true
            )
        )

        // Sjekk perioder for bostatus
        avvikListe.addAll(
            PeriodeUtil.validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "soknadBarnBostatusPeriodeListe",
                periodeListe = grunnlag.bostatusPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = true,
                sjekkOpphold = true,
                sjekkNull = true,
                sjekkBeregnPeriode = true
            )
        )

        // Sjekk perioder for barn
        avvikListe.addAll(
            PeriodeUtil.validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "bidragMottakerBarnPeriodeListe",
                periodeListe = grunnlag.barnIHusstandenPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = false,
                sjekkOpphold = false,
                sjekkNull = false,
                sjekkBeregnPeriode = false
            )
        )

        // Sjekk perioder for sjablonliste
        avvikListe.addAll(
            PeriodeUtil.validerInputDatoer(
                beregnDatoFom = grunnlag.beregnDatoFra,
                beregnDatoTil = grunnlag.beregnDatoTil,
                dataElement = "sjablonPeriodeListe",
                periodeListe = grunnlag.sjablonPeriodeListe.map { it.getPeriode() },
                sjekkOverlapp = false,
                sjekkOpphold = false,
                sjekkNull = false,
                sjekkBeregnPeriode = false
            )
        )

        return avvikListe
    }
}
