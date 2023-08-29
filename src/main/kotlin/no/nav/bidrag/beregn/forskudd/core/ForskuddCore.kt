package no.nav.bidrag.beregn.forskudd.core

import no.nav.bidrag.beregn.felles.bo.Avvik
import no.nav.bidrag.beregn.felles.bo.Periode
import no.nav.bidrag.beregn.felles.bo.Sjablon
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi
import no.nav.bidrag.beregn.felles.enums.BostatusKode
import no.nav.bidrag.beregn.felles.enums.InntektType
import no.nav.bidrag.beregn.felles.enums.SivilstandKode
import no.nav.bidrag.beregn.forskudd.core.bo.BarnIHusstandenPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode
import no.nav.bidrag.beregn.forskudd.core.bo.SoknadBarn
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode
import no.nav.bidrag.transport.beregning.felles.AvvikCore
import no.nav.bidrag.transport.beregning.felles.PeriodeCore
import no.nav.bidrag.transport.beregning.felles.SjablonResultatGrunnlagCore
import no.nav.bidrag.transport.beregning.forskudd.core.request.BarnIHusstandenPeriodeCore
import no.nav.bidrag.transport.beregning.forskudd.core.request.BeregnForskuddGrunnlagCore
import no.nav.bidrag.transport.beregning.forskudd.core.request.BostatusPeriodeCore
import no.nav.bidrag.transport.beregning.forskudd.core.request.InntektPeriodeCore
import no.nav.bidrag.transport.beregning.forskudd.core.request.SivilstandPeriodeCore
import no.nav.bidrag.transport.beregning.forskudd.core.request.SoknadBarnCore
import no.nav.bidrag.transport.beregning.forskudd.core.response.BeregnetForskuddResultatCore
import no.nav.bidrag.transport.beregning.forskudd.core.response.ResultatBeregningCore
import no.nav.bidrag.transport.beregning.forskudd.core.response.ResultatPeriodeCore
import java.time.format.DateTimeFormatter
import java.util.Comparator.comparing

open class ForskuddCore(private val forskuddPeriode: ForskuddPeriode) {
    fun beregnForskudd(grunnlag: BeregnForskuddGrunnlagCore): BeregnetForskuddResultatCore {
        val beregnForskuddGrunnlag = mapTilBusinessObject(grunnlag)
        val avvikListe = forskuddPeriode.validerInput(beregnForskuddGrunnlag)
        val beregnForskuddResultat =
            if (avvikListe.isEmpty()) forskuddPeriode.beregnPerioder(beregnForskuddGrunnlag) else BeregnForskuddResultat(emptyList())
        return mapFraBusinessObject(avvikListe, beregnForskuddResultat)
    }

    private fun mapTilBusinessObject(grunnlag: BeregnForskuddGrunnlagCore) =
        BeregnForskuddGrunnlag(
            beregnDatoFra = grunnlag.beregnDatoFra,
            beregnDatoTil = grunnlag.beregnDatoTil,
            soknadBarn = mapSoknadBarn(grunnlag.soknadBarn),
            bostatusPeriodeListe = mapBostatusPeriodeListe(grunnlag.bostatusPeriodeListe),
            inntektPeriodeListe = mapInntektPeriodeListe(grunnlag.inntektPeriodeListe),
            sivilstandPeriodeListe = mapSivilstandPeriodeListe(grunnlag.sivilstandPeriodeListe),
            barnIHusstandenPeriodeListe = mapBarnIHusstandenPeriodeListe(grunnlag.barnIHusstandenPeriodeListe),
            sjablonPeriodeListe = mapSjablonPeriodeListe(grunnlag.sjablonPeriodeListe)
        )

    private fun mapSoknadBarn(soknadBarnCore: SoknadBarnCore) =
        SoknadBarn(soknadBarnCore.referanse, soknadBarnCore.fodselsdato)

    private fun mapBostatusPeriodeListe(bostatusPeriodeListeCore: List<BostatusPeriodeCore>): List<BostatusPeriode> {
        val bostatusPeriodeListe = mutableListOf<BostatusPeriode>()
        bostatusPeriodeListeCore.forEach {
            bostatusPeriodeListe.add(BostatusPeriode(it.referanse, Periode(it.periode.datoFom, it.periode.datoTil), BostatusKode.valueOf(it.kode)))
        }
        return bostatusPeriodeListe.stream()
            .sorted(comparing { bostatusPeriode: BostatusPeriode -> bostatusPeriode.getPeriode().datoFom })
            .toList()
    }

    private fun mapInntektPeriodeListe(bidragMottakerInntektPeriodeListeCore: List<InntektPeriodeCore>): List<InntektPeriode> {
        val bidragMottakerInntektPeriodeListe = mutableListOf<InntektPeriode>()
        bidragMottakerInntektPeriodeListeCore.forEach {
            bidragMottakerInntektPeriodeListe.add(
                InntektPeriode(it.referanse, Periode(it.periode.datoFom, it.periode.datoTil), InntektType.valueOf(it.type), it.belop)
            )
        }
        return bidragMottakerInntektPeriodeListe.stream()
            .sorted(comparing { inntektPeriode: InntektPeriode -> inntektPeriode.getPeriode().datoFom })
            .toList()
    }

    private fun mapSivilstandPeriodeListe(bidragMottakerSivilstandPeriodeListeCore: List<SivilstandPeriodeCore>): List<SivilstandPeriode> {
        val bidragMottakerSivilstandPeriodeListe = mutableListOf<SivilstandPeriode>()
        bidragMottakerSivilstandPeriodeListeCore.forEach {
            bidragMottakerSivilstandPeriodeListe.add(
                SivilstandPeriode(it.referanse, Periode(it.periode.datoFom, it.periode.datoTil), SivilstandKode.valueOf(it.kode))
            )
        }
        return bidragMottakerSivilstandPeriodeListe.stream()
            .sorted(comparing { sivilstandPeriode: SivilstandPeriode -> sivilstandPeriode.getPeriode().datoFom })
            .toList()
    }

    private fun mapBarnIHusstandenPeriodeListe(barnIHusstandenPeriodeListeCore: List<BarnIHusstandenPeriodeCore>): List<BarnIHusstandenPeriode> {
        val barnIHusstandenPeriodeListe = mutableListOf<BarnIHusstandenPeriode>()
        barnIHusstandenPeriodeListeCore.forEach {
            barnIHusstandenPeriodeListe.add(BarnIHusstandenPeriode(it.referanse, Periode(it.periode.datoFom, it.periode.datoTil), it.antall))
        }
        return barnIHusstandenPeriodeListe.stream()
            .sorted(comparing { barnIHusstandenPeriode: BarnIHusstandenPeriode -> barnIHusstandenPeriode.getPeriode().datoFom })
            .toList()
    }

    private fun mapSjablonPeriodeListe(sjablonPeriodeListeCore: List<no.nav.bidrag.transport.beregning.felles.SjablonPeriodeCore>): List<SjablonPeriode> {
        val sjablonPeriodeListe = mutableListOf<SjablonPeriode>()
        sjablonPeriodeListeCore.forEach {
            val sjablonNokkelListe = mutableListOf<SjablonNokkel>()
            val sjablonInnholdListe = mutableListOf<SjablonInnhold>()
            it.nokkelListe!!.forEach { nokkel ->
                sjablonNokkelListe.add(SjablonNokkel(nokkel.navn, nokkel.verdi))
            }
            it.innholdListe.forEach { innhold ->
                sjablonInnholdListe.add(SjablonInnhold(innhold.navn, innhold.verdi))
            }
            sjablonPeriodeListe.add(
                SjablonPeriode(Periode(it.periode.datoFom, it.periode.datoTil), Sjablon(it.navn, sjablonNokkelListe, sjablonInnholdListe))
            )
        }
        return sjablonPeriodeListe
    }

    private fun mapFraBusinessObject(avvikListe: List<Avvik>, resultat: BeregnForskuddResultat) =
        BeregnetForskuddResultatCore(
            mapResultatPeriode(resultat.beregnetForskuddPeriodeListe),
            mapSjablonGrunnlagListe(resultat.beregnetForskuddPeriodeListe),
            mapAvvik(avvikListe)
        )

    private fun mapResultatPeriode(periodeResultatListe: List<ResultatPeriode>): List<ResultatPeriodeCore> {
        val resultatPeriodeCoreListe = ArrayList<ResultatPeriodeCore>()
        for (periodeResultat in periodeResultatListe) {
            val (belop, kode, regel) = periodeResultat.resultat
            resultatPeriodeCoreListe.add(
                ResultatPeriodeCore(
                    PeriodeCore(periodeResultat.periode.datoFom, periodeResultat.periode.datoTil),
                    ResultatBeregningCore(belop, kode.toString(), regel),
                    mapReferanseListe(periodeResultat)
                )
            )
        }
        return resultatPeriodeCoreListe
    }

    private fun mapReferanseListe(resultatPeriode: ResultatPeriode): List<String> {
        val (inntektListe, sivilstand, barnIHusstanden, soknadBarnAlder, soknadBarnBostatus) = resultatPeriode.grunnlag
        val sjablonListe = resultatPeriode.resultat.sjablonListe
        val referanseListe = mutableListOf<String>()
        inntektListe.forEach {
            referanseListe.add(it.referanse)
        }
        referanseListe.add(sivilstand.referanse)
        referanseListe.add(barnIHusstanden.referanse)
        referanseListe.add(soknadBarnAlder.referanse)
        referanseListe.add(soknadBarnBostatus.referanse)
        referanseListe.addAll(
            sjablonListe.stream()
                .map { lagSjablonReferanse(it) }
                .distinct()
                .toList()
        )
        return referanseListe
    }

    private fun lagSjablonReferanse(sjablon: SjablonPeriodeNavnVerdi) =
        "Sjablon_${sjablon.navn}_${sjablon.periode.datoFom.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}"

    private fun mapSjablonGrunnlagListe(periodeResultatListe: List<ResultatPeriode>) =
        periodeResultatListe.stream()
            .map { mapSjablonListe(it.resultat.sjablonListe) }
            .flatMap { it.stream() }
            .distinct()
            .toList()

    private fun mapSjablonListe(sjablonListe: List<SjablonPeriodeNavnVerdi>) =
        sjablonListe.stream()
            .map {
                SjablonResultatGrunnlagCore(
                    lagSjablonReferanse(it),
                    PeriodeCore(it.periode.datoFom, it.periode.datoTil),
                    it.navn,
                    it.verdi
                )
            }
            .toList()

    private fun mapAvvik(avvikListe: List<Avvik>): List<AvvikCore> {
        val avvikCoreListe = mutableListOf<AvvikCore>()
        avvikListe.forEach {
            avvikCoreListe.add(AvvikCore(it.avvikTekst, it.avvikType.toString()))
        }
        return avvikCoreListe
    }
}
