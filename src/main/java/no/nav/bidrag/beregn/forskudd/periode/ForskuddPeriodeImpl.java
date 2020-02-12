package no.nav.bidrag.beregn.forskudd.periode;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.firstDayOfNextMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.util.stream.Collectors.toCollection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.forskudd.beregning.ForskuddBeregningImpl;
import no.nav.bidrag.beregn.forskudd.beregning.grunnlag.BostedStatusKode;
import no.nav.bidrag.beregn.forskudd.beregning.grunnlag.ForskuddBeregningGrunnlag;
import no.nav.bidrag.beregn.forskudd.beregning.resultat.ForskuddBeregningResultat;
import no.nav.bidrag.beregn.forskudd.beregning.resultat.ResultatKode;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.AlderPeriode;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.ForskuddPeriodeGrunnlag;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.Periode;
import no.nav.bidrag.beregn.forskudd.periode.grunnlag.SivilstandPeriode;
import no.nav.bidrag.beregn.forskudd.periode.resultat.ForskuddPeriodeResultat;
import no.nav.bidrag.beregn.forskudd.periode.resultat.PeriodeResultat;

public class ForskuddPeriodeImpl implements ForskuddPeriode {

  public ForskuddPeriodeResultat beregnPerioder(ForskuddPeriodeGrunnlag periodeGrunnlag) {

    var forskuddBeregning = new ForskuddBeregningImpl();
    var periodeResultatListe = new ArrayList<PeriodeResultat>();

    // Justerer datoer på grunnlagslistene
    var justertInntektPeriodeListe = periodeGrunnlag.getBidragMottakerInntektPeriodeListe().stream()
        .map(iP -> new InntektPeriode(PeriodeUtil.justerPeriode(iP.getDatoFraTil()), iP.getBelop())).collect(toCollection(ArrayList::new));
    var justertSivilstandPeriodeListe = periodeGrunnlag.getBidragMottakerSivilstandPeriodeListe().stream()
        .map(sP -> new SivilstandPeriode(PeriodeUtil.justerPeriode(sP.getDatoFraTil()), sP.getSivilstandKode())).collect(toCollection(ArrayList::new));
    var justertBarnPeriodeListe = periodeGrunnlag.getBidragMottakerBarnPeriodeListe().stream()
        .map(PeriodeUtil::justerPeriode).collect(toCollection(ArrayList::new));
    var justertBostatusPeriodeListe = periodeGrunnlag.getSoknadBarn().getSoknadBarnBostatusPeriodeListe().stream()
        .map(bP -> new BostatusPeriode(PeriodeUtil.justerPeriode(bP.getDatoFraTil()), bP.getBostedStatusKode())).collect(toCollection(ArrayList::new));

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(periodeGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start beregning fra dato
        .addBruddpunkter(justertInntektPeriodeListe)
        .addBruddpunkter(justertSivilstandPeriodeListe)
        .addBruddpunkter(justertBarnPeriodeListe)
        .addBruddpunkter(justertBostatusPeriodeListe)
        .addBruddpunkter(periodeGrunnlag.getSoknadBarn().getFodselDato(), periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil())
        .finnPerioder(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil());

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {
      var inntektBelop = justertInntektPeriodeListe.stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode)).map(InntektPeriode::getBelop).findFirst().orElse(null);

      var sivilstandKode = justertSivilstandPeriodeListe.stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode)).map(SivilstandPeriode::getSivilstandKode).findFirst().orElse(null);

      var alder = settBarnAlderPerioder(periodeGrunnlag.getSoknadBarn().getFodselDato(), periodeGrunnlag.getBeregnDatoFra(),
          periodeGrunnlag.getBeregnDatoTil()).stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode)).map(AlderPeriode::getAlder).findFirst().orElse(null);

      var bostedStatusKode = justertBostatusPeriodeListe.stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode)).map(BostatusPeriode::getBostedStatusKode).findFirst().orElse(null);

      var antallBarn = (int) justertBarnPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode)).count();

      // Øk antall barn med 1 hvis søknadsbarnet bor hjemme
      if (BostedStatusKode.MED_FORELDRE.equals(bostedStatusKode)) {
        antallBarn = antallBarn + 1;
      }

      periodeResultatListe.add(new PeriodeResultat(beregningsperiode, forskuddBeregning
          .beregn(new ForskuddBeregningGrunnlag(inntektBelop, sivilstandKode, antallBarn, alder, bostedStatusKode))));
    }

    //Slår sammen perioder med samme resultat
    return mergePerioder(periodeResultatListe);
  }

  // Slår sammen perioder hvis Beløp, ResultatKode og ResultatBeskrivelse er like i tilgrensende perioder
  private ForskuddPeriodeResultat mergePerioder(ArrayList<PeriodeResultat> periodeResultatListe) {
    var filtrertPeriodeResultatListe = new ArrayList<PeriodeResultat>();
    var periodeResultatForrige = new PeriodeResultat(new Periode(LocalDate.MIN, LocalDate.MAX), new ForskuddBeregningResultat(BigDecimal.ZERO, ResultatKode.AVSLAG, ""));
    var datoFra = periodeResultatListe.get(0).getDatoFraTil().getDatoFra();
    var mergePerioder = false;
    int count = 0;

    for (PeriodeResultat periodeResultat : periodeResultatListe) {
      count++;

      if (periodeResultat.getForskuddBeregningResultat().kanMergesMed(periodeResultatForrige.getForskuddBeregningResultat())) {
        mergePerioder = true;
      } else {
        if (mergePerioder) {
          periodeResultatForrige.getDatoFraTil().setDatoFra(datoFra);
          mergePerioder = false;
        }
        if (count > 1) {
          filtrertPeriodeResultatListe.add(periodeResultatForrige);
        }
        datoFra = periodeResultat.getDatoFraTil().getDatoFra();
      }

      periodeResultatForrige = periodeResultat;
    }

    if (count > 0) {
      if (mergePerioder) {
        periodeResultatForrige.getDatoFraTil().setDatoFra(datoFra);
      }
      filtrertPeriodeResultatListe.add(periodeResultatForrige);
    }

    return new ForskuddPeriodeResultat(filtrertPeriodeResultatListe);
  }

  // Deler opp ialdersperioder med utgangspunkt i fødselsdato
  private List<AlderPeriode> settBarnAlderPerioder(LocalDate fodselDato, LocalDate beregnDatoFra, LocalDate beregnDatoTil) {
    var bruddAlderListe = new ArrayList<AlderPeriode>();
    var barn11AarDato = fodselDato.plusYears(11).with(firstDayOfMonth());
    var barn18AarDato = fodselDato.plusYears(18).with(firstDayOfNextMonth());

    // Barn fyller 11 år i perioden
    var barn11AarIPerioden = (barn11AarDato.isAfter(beregnDatoFra.minusDays(1)) && barn11AarDato.isBefore(beregnDatoTil.plusDays(1)));

    // Barn fyller 18 år i perioden
    var barn18AarIPerioden = (barn18AarDato.isAfter(beregnDatoFra.minusDays(1)) && barn18AarDato.isBefore(beregnDatoTil.plusDays(1)));

    if (barn11AarIPerioden) {
      bruddAlderListe
          .add(new AlderPeriode(new Periode(beregnDatoFra.with(firstDayOfMonth()), barn11AarDato.minusMonths(1).with(lastDayOfMonth())), 0));
      if (barn18AarIPerioden) {
        bruddAlderListe
            .add(new AlderPeriode(new Periode(barn11AarDato.with(firstDayOfMonth()), barn18AarDato.minusMonths(1).with(lastDayOfMonth())), 11));
        bruddAlderListe.add(new AlderPeriode(new Periode(barn18AarDato.with(firstDayOfMonth()), null), 18));
      } else {
        bruddAlderListe.add(new AlderPeriode(new Periode(barn11AarDato.with(firstDayOfMonth()), null), 11));
      }
    } else {
      if (barn18AarIPerioden) {
        bruddAlderListe
            .add(new AlderPeriode(new Periode(beregnDatoFra.with(firstDayOfMonth()), barn18AarDato.minusMonths(1).with(lastDayOfMonth())), 11));
        bruddAlderListe.add(new AlderPeriode(new Periode(barn18AarDato.with(firstDayOfMonth()), null), 18));
      }
    }

    return bruddAlderListe;
  }
}
