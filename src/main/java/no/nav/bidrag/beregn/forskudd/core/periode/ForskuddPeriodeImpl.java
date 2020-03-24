package no.nav.bidrag.beregn.forskudd.core.periode;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.firstDayOfNextMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.util.stream.Collectors.toCollection;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.AVSLAG;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.forskudd.core.beregning.ForskuddBeregning;
import no.nav.bidrag.beregn.forskudd.core.beregning.ResultatBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.AlderPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusKode;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SjablonPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SjablonPeriodeVerdi;

public class ForskuddPeriodeImpl implements ForskuddPeriode {

  private ForskuddBeregning forskuddBeregning = ForskuddBeregning.getInstance();

  public BeregnForskuddResultat beregnPerioder(BeregnForskuddGrunnlag periodeGrunnlag) {

    var periodeResultatListe = new ArrayList<ResultatPeriode>();

    // Justerer datoer på grunnlagslistene
    var justertInntektPeriodeListe = periodeGrunnlag.getBidragMottakerInntektPeriodeListe().stream()
        .map(iP -> new InntektPeriode(PeriodeUtil.justerPeriode(iP.getDatoFraTil()), iP.getInntektBelop())).collect(toCollection(ArrayList::new));
    var justertSivilstandPeriodeListe = periodeGrunnlag.getBidragMottakerSivilstandPeriodeListe().stream()
        .map(sP -> new SivilstandPeriode(PeriodeUtil.justerPeriode(sP.getDatoFraTil()), sP.getSivilstandKode()))
        .collect(toCollection(ArrayList::new));
    var justertBarnPeriodeListe = periodeGrunnlag.getBidragMottakerBarnPeriodeListe().stream()
        .map(PeriodeUtil::justerPeriode).collect(toCollection(ArrayList::new));
    var justertBostatusPeriodeListe = periodeGrunnlag.getSoknadBarn().getSoknadBarnBostatusPeriodeListe().stream()
        .map(bP -> new BostatusPeriode(PeriodeUtil.justerPeriode(bP.getDatoFraTil()), bP.getBostatusKode()))
        .collect(toCollection(ArrayList::new));
    var justertSjablon0005PeriodeListe = justerSjablonPeriodeListe(periodeGrunnlag.getSjablonPeriodeListe(), "0005");
    var justertSjablon0013PeriodeListe = justerSjablonPeriodeListe(periodeGrunnlag.getSjablonPeriodeListe(), "0013");
    var justertSjablon0033PeriodeListe = justerSjablonPeriodeListe(periodeGrunnlag.getSjablonPeriodeListe(), "0033");
    var justertSjablon0034PeriodeListe = justerSjablonPeriodeListe(periodeGrunnlag.getSjablonPeriodeListe(), "0034");
    var justertSjablon0035PeriodeListe = justerSjablonPeriodeListe(periodeGrunnlag.getSjablonPeriodeListe(), "0035");
    var justertSjablon0036PeriodeListe = justerSjablonPeriodeListe(periodeGrunnlag.getSjablonPeriodeListe(), "0036");

    // Bygger opp liste over perioder
    List<Periode> perioder = new Periodiserer()
        .addBruddpunkt(periodeGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start beregning fra dato
        .addBruddpunkter(justertInntektPeriodeListe)
        .addBruddpunkter(justertSivilstandPeriodeListe)
        .addBruddpunkter(justertBarnPeriodeListe)
        .addBruddpunkter(justertBostatusPeriodeListe)
        .addBruddpunkter(periodeGrunnlag.getSoknadBarn().getSoknadBarnFodselsdato(), periodeGrunnlag.getBeregnDatoFra(),
            periodeGrunnlag.getBeregnDatoTil())
        .addBruddpunkter(justertSjablon0005PeriodeListe)
        .addBruddpunkter(justertSjablon0013PeriodeListe)
        .addBruddpunkter(justertSjablon0033PeriodeListe)
        .addBruddpunkter(justertSjablon0034PeriodeListe)
        .addBruddpunkter(justertSjablon0035PeriodeListe)
        .addBruddpunkter(justertSjablon0036PeriodeListe)
        .finnPerioder(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil());

    // Løper gjennom periodene og finner matchende verdi for hver kategori. Kaller beregningsmodulen for hver beregningsperiode
    for (Periode beregningsperiode : perioder) {
      var inntektBelop = justertInntektPeriodeListe.stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode)).map(InntektPeriode::getInntektBelop).findFirst().orElse(null);

      var sivilstandKode = justertSivilstandPeriodeListe.stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode)).map(SivilstandPeriode::getSivilstandKode).findFirst().orElse(null);

      var alder = settBarnAlderPerioder(periodeGrunnlag.getSoknadBarn().getSoknadBarnFodselsdato(), periodeGrunnlag.getBeregnDatoFra(),
          periodeGrunnlag.getBeregnDatoTil()).stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode)).map(AlderPeriode::getAlder).findFirst().orElse(null);

      var bostatusKode = justertBostatusPeriodeListe.stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode)).map(BostatusPeriode::getBostatusKode).findFirst().orElse(null);

      var antallBarn = (int) justertBarnPeriodeListe.stream().filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode)).count();

      var forskuddssats100Prosent = hentSjablonVerdi(justertSjablon0005PeriodeListe, beregningsperiode);
      var multiplikatorMaksInntektsgrense = hentSjablonVerdi(justertSjablon0013PeriodeListe, beregningsperiode);
      var inntektsgrense100ProsentForskudd = hentSjablonVerdi(justertSjablon0033PeriodeListe, beregningsperiode);
      var inntektsgrenseEnslig75ProsentForskudd = hentSjablonVerdi(justertSjablon0034PeriodeListe, beregningsperiode);
      var inntektsgrenseGift75ProsentForskudd = hentSjablonVerdi(justertSjablon0035PeriodeListe, beregningsperiode);
      var inntektsintervallForskudd = hentSjablonVerdi(justertSjablon0036PeriodeListe, beregningsperiode);

      // Øk antall barn med 1 hvis søknadsbarnet bor hjemme
      if (BostatusKode.MED_FORELDRE.equals(bostatusKode)) {
        antallBarn = antallBarn + 1;
      }

      periodeResultatListe.add(new ResultatPeriode(beregningsperiode, forskuddBeregning
          .beregn(new GrunnlagBeregning(inntektBelop, sivilstandKode, antallBarn, alder, bostatusKode, forskuddssats100Prosent,
              multiplikatorMaksInntektsgrense, inntektsgrense100ProsentForskudd, inntektsgrenseEnslig75ProsentForskudd,
              inntektsgrenseGift75ProsentForskudd, inntektsintervallForskudd))));
    }

    //Slår sammen perioder med samme resultat
    return mergePerioder(periodeResultatListe);
  }

  // Justerer sjablonperiodelister
  private ArrayList<SjablonPeriodeVerdi> justerSjablonPeriodeListe(List<SjablonPeriode> sjablonPeriode, String sjablonType) {
    return sjablonPeriode.stream().filter(sP -> sP.getSjablonType().equals(sjablonType))
        .map(sP -> new SjablonPeriodeVerdi(PeriodeUtil.justerPeriode(sP.getSjablonDatoFraTil()), sP.getSjablonVerdi()))
        .collect(toCollection(ArrayList::new));
  }

  // Henter sjablonverdi
  private Integer hentSjablonVerdi(List<SjablonPeriodeVerdi> sjablonListe, Periode beregningsperiode) {
    return sjablonListe.stream().filter(periodeListe -> periodeListe.getDatoFraTil().overlapperMed(beregningsperiode))
        .map(SjablonPeriodeVerdi::getSjablonVerdi).findFirst().orElse(null);
  }

  // Slår sammen perioder hvis Beløp, ResultatKode og ResultatBeskrivelse er like i tilgrensende perioder
  private BeregnForskuddResultat mergePerioder(ArrayList<ResultatPeriode> periodeResultatListe) {
    var filtrertPeriodeResultatListe = new ArrayList<ResultatPeriode>();
    var periodeResultatForrige = new ResultatPeriode(new Periode(LocalDate.MIN, LocalDate.MAX),
        new ResultatBeregning(BigDecimal.ZERO, AVSLAG, ""));
    var datoFra = periodeResultatListe.get(0).getResultatDatoFraTil().getDatoFra();
    var mergePerioder = false;
    int count = 0;

    for (ResultatPeriode periodeResultat : periodeResultatListe) {
      count++;

      if (periodeResultat.getResultatBeregning().kanMergesMed(periodeResultatForrige.getResultatBeregning())) {
        mergePerioder = true;
      } else {
        if (mergePerioder) {
          periodeResultatForrige.getResultatDatoFraTil().setDatoFra(datoFra);
          mergePerioder = false;
        }
        if (count > 1) {
          filtrertPeriodeResultatListe.add(periodeResultatForrige);
        }
        datoFra = periodeResultat.getResultatDatoFraTil().getDatoFra();
      }

      periodeResultatForrige = periodeResultat;
    }

    if (count > 0) {
      if (mergePerioder) {
        periodeResultatForrige.getResultatDatoFraTil().setDatoFra(datoFra);
      }
      filtrertPeriodeResultatListe.add(periodeResultatForrige);
    }

    return new BeregnForskuddResultat(filtrertPeriodeResultatListe);
  }

  // Deler opp i aldersperioder med utgangspunkt i fødselsdato
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
