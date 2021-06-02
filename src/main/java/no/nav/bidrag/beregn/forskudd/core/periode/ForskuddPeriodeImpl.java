package no.nav.bidrag.beregn.forskudd.core.periode;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.firstDayOfNextMonth;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.InntektUtil;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.Rolle;
import no.nav.bidrag.beregn.felles.enums.SoknadType;
import no.nav.bidrag.beregn.felles.inntekt.InntektPeriodeGrunnlag;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.forskudd.core.beregning.ForskuddBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.Alder;
import no.nav.bidrag.beregn.forskudd.core.bo.AlderPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.Barn;
import no.nav.bidrag.beregn.forskudd.core.bo.BarnPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddListeGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.Bostatus;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.Sivilstand;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode;

public class ForskuddPeriodeImpl implements ForskuddPeriode {

  private final ForskuddBeregning forskuddBeregning;

  public ForskuddPeriodeImpl(ForskuddBeregning forskuddBeregning) {
    this.forskuddBeregning = forskuddBeregning;
  }

  public BeregnForskuddResultat beregnPerioder(BeregnForskuddGrunnlag periodeGrunnlag) {

    var beregnForskuddListeGrunnlag = new BeregnForskuddListeGrunnlag();

    // Juster inntekter for å unngå overlapp innenfor samme inntektsgruppe
    justerInntekter(periodeGrunnlag.getBidragMottakerInntektPeriodeListe(), beregnForskuddListeGrunnlag);

    // Juster datoer
    justerDatoerGrunnlagslister(periodeGrunnlag, beregnForskuddListeGrunnlag);

    // Lag bruddperioder
    lagBruddperioder(periodeGrunnlag, beregnForskuddListeGrunnlag);

    // Foreta beregning
    beregnForskuddPerPeriode(beregnForskuddListeGrunnlag);

    return new BeregnForskuddResultat(beregnForskuddListeGrunnlag.getPeriodeResultatListe());
  }

  // Justerer inntekter for å unngå overlapp innenfor samme inntektsgruppe
  private void justerInntekter(List<InntektPeriode> inntektPeriodeListe, BeregnForskuddListeGrunnlag beregnForskuddListeGrunnlag) {

    var inntektGrunnlagListe = InntektUtil.justerInntekter(inntektPeriodeListe.stream()
        .map(inntektPeriode -> new InntektPeriodeGrunnlag(inntektPeriode.getReferanse(), inntektPeriode.getPeriode(), inntektPeriode.getType(),
            inntektPeriode.getBelop(), false, false))
        .collect(toList()));

    beregnForskuddListeGrunnlag.setJustertBidragMottakerInntektPeriodeListe(
        inntektGrunnlagListe.stream()
            .map(inntektGrunnlag -> new InntektPeriode(inntektGrunnlag.getReferanse(), inntektGrunnlag.getPeriode(), inntektGrunnlag.getType(),
                inntektGrunnlag.getBelop()))
            .sorted(comparing(inntektPeriode -> inntektPeriode.getPeriode().getDatoFom()))
            .collect(toList())
    );
  }

  // Justerer datoer på grunnlagslistene (blir gjort implisitt i xxxPeriode::new)
  private void justerDatoerGrunnlagslister(BeregnForskuddGrunnlag periodeGrunnlag, BeregnForskuddListeGrunnlag beregnForskuddListeGrunnlag) {
    beregnForskuddListeGrunnlag.setJustertInntektPeriodeListe(
        beregnForskuddListeGrunnlag.getJustertBidragMottakerInntektPeriodeListe().stream()
            .map(InntektPeriode::new)
            .collect(toCollection(ArrayList::new)));

    beregnForskuddListeGrunnlag.setJustertSivilstandPeriodeListe(
        periodeGrunnlag.getBidragMottakerSivilstandPeriodeListe().stream()
            .map(SivilstandPeriode::new)
            .collect(toCollection(ArrayList::new)));

    beregnForskuddListeGrunnlag.setJustertBarnPeriodeListe(
        periodeGrunnlag.getBidragMottakerBarnPeriodeListe().stream()
            .map(BarnPeriode::new)
            .collect(toCollection(ArrayList::new)));

    beregnForskuddListeGrunnlag.setJustertBostatusPeriodeListe(
        periodeGrunnlag.getSoknadBarn().getBostatusPeriodeListe().stream()
            .map(BostatusPeriode::new)
            .collect(toCollection(ArrayList::new)));

    beregnForskuddListeGrunnlag.setJustertAlderPeriodeListe(
        settBarnAlderPerioder(periodeGrunnlag.getSoknadBarn().getFodselsdato(),
            periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil()).stream()
            .map(alderPeriode -> new AlderPeriode(periodeGrunnlag.getSoknadBarn().getReferanse(), alderPeriode.getAlderPeriode(), alderPeriode.getAlder()))
            .collect(toCollection(ArrayList::new)));

    beregnForskuddListeGrunnlag.setJustertSjablonPeriodeListe(
        periodeGrunnlag.getSjablonPeriodeListe().stream()
            .map(SjablonPeriode::new)
            .collect(toCollection(ArrayList::new)));
  }

  // Lagger bruddperioder ved å løpe gjennom alle periodelistene
  private void lagBruddperioder(BeregnForskuddGrunnlag periodeGrunnlag, BeregnForskuddListeGrunnlag beregnForskuddListeGrunnlag) {

    // Bygger opp liste over perioder, basert på alle typer inputparametre
    beregnForskuddListeGrunnlag.setBruddPeriodeListe(
        new Periodiserer()
            .addBruddpunkt(periodeGrunnlag.getBeregnDatoFra()) //For å sikre bruddpunkt på start beregning fra-dato
            .addBruddpunkter(beregnForskuddListeGrunnlag.getJustertInntektPeriodeListe())
            .addBruddpunkter(beregnForskuddListeGrunnlag.getJustertSivilstandPeriodeListe())
            .addBruddpunkter(beregnForskuddListeGrunnlag.getJustertBarnPeriodeListe())
            .addBruddpunkter(beregnForskuddListeGrunnlag.getJustertBostatusPeriodeListe())
            .addBruddpunkter(beregnForskuddListeGrunnlag.getJustertAlderPeriodeListe())
            .addBruddpunkter(beregnForskuddListeGrunnlag.getJustertSjablonPeriodeListe())
            .addBruddpunkt(periodeGrunnlag.getBeregnDatoTil()) //For å sikre bruddpunkt på start beregning til-dato
            .finnPerioder(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil()));

    // Hvis det ligger 2 perioder på slutten som i til-dato inneholder hhv. beregningsperiodens til-dato og null slås de sammen
    var bruddPeriodeListeAntallElementer =
        (beregnForskuddListeGrunnlag.getBruddPeriodeListe() != null) ? beregnForskuddListeGrunnlag.getBruddPeriodeListe().size() : 0;
    if (bruddPeriodeListeAntallElementer > 1) {
      var nestSisteTilDato = beregnForskuddListeGrunnlag.getBruddPeriodeListe().get(bruddPeriodeListeAntallElementer - 2).getDatoTil();
      var sisteTilDato = beregnForskuddListeGrunnlag.getBruddPeriodeListe().get(bruddPeriodeListeAntallElementer - 1).getDatoTil();
      if ((periodeGrunnlag.getBeregnDatoTil().equals(nestSisteTilDato)) && (null == sisteTilDato)) {
        var nyPeriode = new Periode(
            beregnForskuddListeGrunnlag.getBruddPeriodeListe().get(bruddPeriodeListeAntallElementer - 2).getDatoFom(), null);
        beregnForskuddListeGrunnlag.getBruddPeriodeListe().remove(bruddPeriodeListeAntallElementer - 1);
        beregnForskuddListeGrunnlag.getBruddPeriodeListe().remove(bruddPeriodeListeAntallElementer - 2);
        beregnForskuddListeGrunnlag.getBruddPeriodeListe().add(nyPeriode);
      }
    }
  }

  // Løper gjennom alle bruddperioder og foretar beregning
  private void beregnForskuddPerPeriode(BeregnForskuddListeGrunnlag beregnForskuddListeGrunnlag) {

    // Løper gjennom periodene og finner matchende verdi for hver kategori
    // Kaller beregningsmodulen for hver beregningsperiode
    beregnForskuddListeGrunnlag.getBruddPeriodeListe().forEach(beregningsperiode -> {
      var inntektListe = beregnForskuddListeGrunnlag.getJustertInntektPeriodeListe().stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getReferanse(), inntektPeriode.getType(), inntektPeriode.getBelop()))
          .collect(toList());
      var sivilstand = beregnForskuddListeGrunnlag.getJustertSivilstandPeriodeListe().stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(sivilstandPeriode -> new Sivilstand(sivilstandPeriode.getReferanse(), sivilstandPeriode.getKode()))
          .findFirst()
          .orElse(null);
      var alder = beregnForskuddListeGrunnlag.getJustertAlderPeriodeListe().stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(alderPeriode -> new Alder(alderPeriode.getReferanse(), alderPeriode.getAlder()))
          .findFirst()
          .orElse(null);
      var bostatus = beregnForskuddListeGrunnlag.getJustertBostatusPeriodeListe().stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(bostatusPeriode -> new Bostatus(bostatusPeriode.getReferanse(), bostatusPeriode.getKode()))
          .findFirst()
          .orElse(null);
      var barnReferanseListe = beregnForskuddListeGrunnlag.getJustertBarnPeriodeListe().stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .map(BarnPeriode::getReferanse)
          .collect(toList());
      var antallBarn = new Barn(barnReferanseListe, barnReferanseListe.size() + soknadsbarnBorHjemme(bostatus.getKode()));
      var sjablonListe = beregnForskuddListeGrunnlag.getJustertSjablonPeriodeListe().stream()
          .filter(i -> i.getPeriode().overlapperMed(beregningsperiode))
          .collect(toList());
      var grunnlagBeregning = new GrunnlagBeregning(inntektListe, sivilstand, antallBarn, alder, bostatus, sjablonListe);
      beregnForskuddListeGrunnlag.getPeriodeResultatListe()
          .add(new ResultatPeriode(beregningsperiode, forskuddBeregning.beregn(grunnlagBeregning), grunnlagBeregning));
    });
  }

  // Deler opp i aldersperioder med utgangspunkt i fødselsdato
  private List<AlderPeriode> settBarnAlderPerioder(LocalDate fodselDato, LocalDate beregnDatoFra, LocalDate beregnDatoTil) {
    var bruddAlderListe = new ArrayList<AlderPeriode>();
    var barn11AarDato = fodselDato.plusYears(11).with(firstDayOfMonth());
    var barn18AarDato = fodselDato.plusYears(18).with(firstDayOfNextMonth());

    var alderStartPeriode = 0;
    if (!(barn11AarDato.isAfter(beregnDatoFra))) {
      if (!(barn18AarDato.isAfter(beregnDatoFra))) {
        alderStartPeriode = 18;
      } else {
        alderStartPeriode = 11;
      }
    }

    // Barn fyller 11 år i perioden
    var barn11AarIPerioden = (barn11AarDato.isAfter(beregnDatoFra.minusDays(1)) && barn11AarDato.isBefore(beregnDatoTil.plusDays(1)));

    // Barn fyller 18 år i perioden
    var barn18AarIPerioden = (barn18AarDato.isAfter(beregnDatoFra.minusDays(1)) && barn18AarDato.isBefore(beregnDatoTil.plusDays(1)));

    if (barn11AarIPerioden) {
      bruddAlderListe.add(new AlderPeriode("", new Periode(beregnDatoFra.with(firstDayOfMonth()), barn11AarDato.with(firstDayOfMonth())), 0));
      if (barn18AarIPerioden) {
        bruddAlderListe.add(new AlderPeriode("", new Periode(barn11AarDato.with(firstDayOfMonth()), barn18AarDato.with(firstDayOfMonth())), 11));
        bruddAlderListe.add(new AlderPeriode("", new Periode(barn18AarDato.with(firstDayOfMonth()), null), 18));
      } else {
        bruddAlderListe.add(new AlderPeriode("", new Periode(barn11AarDato.with(firstDayOfMonth()), null), 11));
      }

    } else {
      if (barn18AarIPerioden) {
        bruddAlderListe.add(new AlderPeriode("", new Periode(beregnDatoFra.with(firstDayOfMonth()), barn18AarDato.with(firstDayOfMonth())), 11));
        bruddAlderListe.add(new AlderPeriode("", new Periode(barn18AarDato.with(firstDayOfMonth()), null), 18));

      } else {
        bruddAlderListe.add(new AlderPeriode("", new Periode(beregnDatoFra.with(firstDayOfMonth()), null), alderStartPeriode));
      }
    }

    return bruddAlderListe;
  }

  // Sjekker om søknadsbarnet bor hjemme
  private Integer soknadsbarnBorHjemme(BostatusKode bostatusKode) {
    return BostatusKode.MED_FORELDRE.equals(bostatusKode) ? 1 : 0;
  }


  // Validerer at input-verdier til forskuddsberegning er gyldige
  public List<Avvik> validerInput(BeregnForskuddGrunnlag periodeGrunnlag) {

    // Sjekk beregn dato fra/til
    var avvikListe = new ArrayList<>(PeriodeUtil.validerBeregnPeriodeInput(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil()));

    // Sjekk perioder for inntekt
    var bidragMottakerInntektPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode bidragMottakerInntektPeriode : periodeGrunnlag.getBidragMottakerInntektPeriodeListe()) {
      bidragMottakerInntektPeriodeListe.add(bidragMottakerInntektPeriode.getPeriode());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "bidragMottakerInntektPeriodeListe",
            bidragMottakerInntektPeriodeListe, false, true, false, true));

    // Sjekk perioder for sivilstand
    var bidragMottakerSivilstandPeriodeListe = new ArrayList<Periode>();
    for (SivilstandPeriode bidragMottakerSivilstandPeriode : periodeGrunnlag.getBidragMottakerSivilstandPeriodeListe()) {
      bidragMottakerSivilstandPeriodeListe.add(bidragMottakerSivilstandPeriode.getPeriode());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "bidragMottakerSivilstandPeriodeListe",
            bidragMottakerSivilstandPeriodeListe, true, true, true, true));

    // Sjekk perioder for bostatus
    var soknadBarnBostatusPeriodeListe = new ArrayList<Periode>();
    for (BostatusPeriode soknadBarnBostatusPeriode : periodeGrunnlag.getSoknadBarn().getBostatusPeriodeListe()) {
      soknadBarnBostatusPeriodeListe.add(soknadBarnBostatusPeriode.getPeriode());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "soknadBarnBostatusPeriodeListe",
            soknadBarnBostatusPeriodeListe, true, true, true, true));

    // Sjekk perioder for barn
    var bidragMottakerBarnPeriodeListe = new ArrayList<Periode>();

    for (BarnPeriode bidragMottakerBarnPeriode : periodeGrunnlag.getBidragMottakerBarnPeriodeListe()) {
      bidragMottakerBarnPeriodeListe.add(bidragMottakerBarnPeriode.getPeriode());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "bidragMottakerBarnPeriodeListe",
            bidragMottakerBarnPeriodeListe, false, false, false, false));

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : periodeGrunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getPeriode());
    }
    avvikListe.addAll(PeriodeUtil
        .validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "sjablonPeriodeListe", sjablonPeriodeListe, false,
            false, false, false));

    // Valider inntekter
    var inntektGrunnlagListe = periodeGrunnlag.getBidragMottakerInntektPeriodeListe().stream()
        .map(inntektPeriode -> new InntektPeriodeGrunnlag(inntektPeriode.getReferanse(), inntektPeriode.getPeriode(), inntektPeriode.getType(),
            inntektPeriode.getBelop(), false, false))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.FORSKUDD, Rolle.BIDRAGSMOTTAKER));

    return avvikListe;
  }
}
