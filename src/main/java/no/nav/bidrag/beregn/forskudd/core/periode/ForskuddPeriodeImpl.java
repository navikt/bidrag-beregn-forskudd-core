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
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.forskudd.core.beregning.ForskuddBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.AlderPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.Avvik;
import no.nav.bidrag.beregn.forskudd.core.bo.AvvikType;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusKode;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SjablonPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SjablonPeriodeVerdi;

public class ForskuddPeriodeImpl implements ForskuddPeriode {

  private final ForskuddBeregning forskuddBeregning = ForskuddBeregning.getInstance();

  public BeregnForskuddResultat beregnPerioder(BeregnForskuddGrunnlag periodeGrunnlag) {

    var periodeResultatListe = new ArrayList<ResultatPeriode>();

    // Justerer datoer på grunnlagslistene
    var justertInntektPeriodeListe = periodeGrunnlag.getBidragMottakerInntektPeriodeListe().stream().map(InntektPeriode::new)
        .collect(toCollection(ArrayList::new));
    var justertSivilstandPeriodeListe = periodeGrunnlag.getBidragMottakerSivilstandPeriodeListe().stream().map(SivilstandPeriode::new)
        .collect(toCollection(ArrayList::new));
    var justertBarnPeriodeListe = periodeGrunnlag.getBidragMottakerBarnPeriodeListe().stream().map(Periode::new)
        .collect(toCollection(ArrayList::new));
    var justertBostatusPeriodeListe = periodeGrunnlag.getSoknadBarn().getSoknadBarnBostatusPeriodeListe().stream().map(BostatusPeriode::new)
        .collect(toCollection(ArrayList::new));

    // Danner lister for hver sjablontype
    var justertSjablon0005PeriodeListe = justerSjablonPeriodeListe(periodeGrunnlag.getSjablonPeriodeListe(), "0005");
    var justertSjablon0013PeriodeListe = justerSjablonPeriodeListe(periodeGrunnlag.getSjablonPeriodeListe(), "0013");
    var justertSjablon0033PeriodeListe = justerSjablonPeriodeListe(periodeGrunnlag.getSjablonPeriodeListe(), "0033");
    var justertSjablon0034PeriodeListe = justerSjablonPeriodeListe(periodeGrunnlag.getSjablonPeriodeListe(), "0034");
    var justertSjablon0035PeriodeListe = justerSjablonPeriodeListe(periodeGrunnlag.getSjablonPeriodeListe(), "0035");
    var justertSjablon0036PeriodeListe = justerSjablonPeriodeListe(periodeGrunnlag.getSjablonPeriodeListe(), "0036");

    // Bygger opp liste over perioder, basert på alle typer inputparametre
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

    // Løper gjennom periodene og finner matchende verdi for hver kategori
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

      // Kaller beregningsmodulen for hver beregningsperiode
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
    return sjablonPeriode.stream().filter(sP -> sP.getSjablonType().equals(sjablonType)).map(SjablonPeriodeVerdi::new)
        .collect(toCollection(ArrayList::new));
  }

  // Henter sjablonverdi for aktuell periode
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

  // Validerer at input-verdier til forskuddsberegning er gyldige
  public List<Avvik> validerInput(BeregnForskuddGrunnlag periodeGrunnlag) {
    var avvikListe = new ArrayList<Avvik>();

    // Sjekk perioder for inntekt
    var bidragMottakerInntektPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode bidragMottakerInntektPeriode : periodeGrunnlag.getBidragMottakerInntektPeriodeListe()) {
      bidragMottakerInntektPeriodeListe.add(bidragMottakerInntektPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("bidragMottakerInntektPeriodeListe", bidragMottakerInntektPeriodeListe, true, true, true));

    // Sjekk perioder for sivilstand
    var bidragMottakerSivilstandPeriodeListe = new ArrayList<Periode>();
    for (SivilstandPeriode bidragMottakerSivilstandPeriode : periodeGrunnlag.getBidragMottakerSivilstandPeriodeListe()) {
      bidragMottakerSivilstandPeriodeListe.add(bidragMottakerSivilstandPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("bidragMottakerSivilstandPeriodeListe", bidragMottakerSivilstandPeriodeListe, true, true, true));

    // Sjekk perioder for bostatus
    var soknadBarnBostatusPeriodeListe = new ArrayList<Periode>();
    for (BostatusPeriode soknadBarnBostatusPeriode : periodeGrunnlag.getSoknadBarn().getSoknadBarnBostatusPeriodeListe()) {
      soknadBarnBostatusPeriodeListe.add(soknadBarnBostatusPeriode.getDatoFraTil());
    }
    avvikListe.addAll(validerInput("soknadBarnBostatusPeriodeListe", soknadBarnBostatusPeriodeListe, true, true, true));

    // Sjekk perioder for barn
    if (periodeGrunnlag.getBidragMottakerBarnPeriodeListe() != null) {
      var bidragMottakerBarnPeriodeListe = new ArrayList<Periode>();

      for (Periode bidragMottakerBarnPeriode : periodeGrunnlag.getBidragMottakerBarnPeriodeListe()) {
        bidragMottakerBarnPeriodeListe.add(bidragMottakerBarnPeriode.getDatoFraTil());
      }
      avvikListe.addAll(validerInput("bidragMottakerBarnPeriodeListe", bidragMottakerBarnPeriodeListe, false, false, false));
    }

    // Sjekk beregn dato fra/til 
    avvikListe.addAll(validerBeregnPeriodeInput(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil()));

    return avvikListe;
  }

  // Validerer at datoer er gyldige
  private List<Avvik> validerInput(String dataElement, List<Periode> periodeListe, boolean sjekkOverlapp, boolean sjekkOpphold, boolean sjekkNull) {
    var avvikListe = new ArrayList<Avvik>();
    int indeks = 0;
    Periode forrigePeriode = null;

    for (Periode dennePeriode : periodeListe) {
      indeks++;

      //Sjekk om perioder overlapper
      if (sjekkOverlapp) {
        if (dennePeriode.overlapper(forrigePeriode)) {
          var feilmelding = "Overlappende perioder i " + dataElement + ": periodeDatoTil=" + forrigePeriode.getDatoTil() + ", periodeDatoFra=" +
              dennePeriode.getDatoFra();
          avvikListe.add(new Avvik(feilmelding, AvvikType.PERIODER_OVERLAPPER));
        }
      }

      //Sjekk om det er opphold mellom perioder
      if (sjekkOpphold) {
        if (dennePeriode.harOpphold(forrigePeriode)) {
          var feilmelding = "Opphold mellom perioder i " + dataElement + ": periodeDatoTil=" + forrigePeriode.getDatoTil() + ", periodeDatoFra=" +
              dennePeriode.getDatoFra();
          avvikListe.add(new Avvik(feilmelding, AvvikType.PERIODER_HAR_OPPHOLD));
        }
      }

      //Sjekk om dato er null
      if (sjekkNull) {
        if ((indeks != periodeListe.size()) && (dennePeriode.getDatoTil() == null)) {
          var feilmelding = "periodeDatoTil kan ikke være null i " + dataElement + ": periodeDatoFra=" + dennePeriode.getDatoFra() +
              ", periodeDatoTil=" + dennePeriode.getDatoTil();
          avvikListe.add(new Avvik(feilmelding, AvvikType.NULL_VERDI_I_DATO));
        }
        if ((indeks != 1) && (dennePeriode.getDatoFra() == null)) {
          var feilmelding = "periodeDatoFra kan ikke være null i " + dataElement + ": periodeDatoFra=" + dennePeriode.getDatoFra() +
              ", periodeDatoTil=" + dennePeriode.getDatoTil();
          avvikListe.add(new Avvik(feilmelding, AvvikType.NULL_VERDI_I_DATO));
        }
      }

      //Sjekk om dato fra er etter dato til
      if (!(dennePeriode.datoTilErEtterDatoFra())) {
        var feilmelding = "periodeDatoTil må være etter periodeDatoFra i " + dataElement + ": periodeDatoFra=" + dennePeriode.getDatoFra() +
            ", periodeDatoTil=" + dennePeriode.getDatoTil();
        avvikListe.add(new Avvik(feilmelding, AvvikType.DATO_FRA_ETTER_DATO_TIL));
      }

      forrigePeriode = new Periode(dennePeriode.getDatoFra(), dennePeriode.getDatoTil());
    }

    return avvikListe;
  }

  // Validerer at beregningsperiode fra/til er gyldig
  private List<Avvik> validerBeregnPeriodeInput(LocalDate beregnDatoFra, LocalDate beregnDatoTil) {
    var avvikListe = new ArrayList<Avvik>();

    if (beregnDatoFra == null) {
      avvikListe.add(new Avvik("beregnDatoFra kan ikke være null", AvvikType.NULL_VERDI_I_DATO));
    }
    if (beregnDatoTil == null) {
      avvikListe.add(new Avvik("beregnDatoTil kan ikke være null", AvvikType.NULL_VERDI_I_DATO));
    }
    if (!new Periode(beregnDatoFra, beregnDatoTil).datoTilErEtterDatoFra()) {
      avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FRA_ETTER_DATO_TIL));
    }

    return avvikListe;
  }
}