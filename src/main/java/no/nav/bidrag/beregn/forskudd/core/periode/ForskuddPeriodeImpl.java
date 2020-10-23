package no.nav.bidrag.beregn.forskudd.core.periode;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.firstDayOfNextMonth;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import no.nav.bidrag.beregn.felles.InntektUtil;
import no.nav.bidrag.beregn.felles.PeriodeUtil;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.Rolle;
import no.nav.bidrag.beregn.felles.enums.SoknadType;
import no.nav.bidrag.beregn.felles.inntekt.InntektGrunnlag;
import no.nav.bidrag.beregn.felles.periode.Periodiserer;
import no.nav.bidrag.beregn.forskudd.core.beregning.ForskuddBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.AlderPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddListeGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode;
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
        .map(inntektPeriode -> new InntektGrunnlag(inntektPeriode.getInntektDatoFraTil(), inntektPeriode.getInntektType(),
            inntektPeriode.getInntektBelop()))
        .collect(toList()));
    beregnForskuddListeGrunnlag.setJustertBidragMottakerInntektPeriodeListe(
        inntektGrunnlagListe.stream()
            .map(inntektGrunnlag -> new InntektPeriode(inntektGrunnlag.getInntektDatoFraTil(), inntektGrunnlag.getInntektType(),
                inntektGrunnlag.getInntektBelop()))
            .sorted(comparing(inntektPeriode -> inntektPeriode.getInntektDatoFraTil().getDatoFra()))
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
        Optional.ofNullable(periodeGrunnlag.getBidragMottakerBarnPeriodeListe()).stream()
            .flatMap(Collection::stream)
            .map(Periode::new)
            .collect(toCollection(ArrayList::new)));

    beregnForskuddListeGrunnlag.setJustertBostatusPeriodeListe(
        periodeGrunnlag.getSoknadBarn().getSoknadBarnBostatusPeriodeListe().stream()
            .map(BostatusPeriode::new)
            .collect(toCollection(ArrayList::new)));

    beregnForskuddListeGrunnlag.setJustertAlderPeriodeListe(
        settBarnAlderPerioder(periodeGrunnlag.getSoknadBarn().getSoknadBarnFodselsdato(),
            periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil()).stream()
            .map(alderPeriode -> new AlderPeriode(alderPeriode.getAlderDatoFraTil(), alderPeriode.getAlder()))
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
    if (beregnForskuddListeGrunnlag.getBruddPeriodeListe().size() > 1) {
      if ((beregnForskuddListeGrunnlag.getBruddPeriodeListe().get(beregnForskuddListeGrunnlag.getBruddPeriodeListe().size() - 2).getDatoTil()
          .equals(periodeGrunnlag.getBeregnDatoTil())) &&
          (beregnForskuddListeGrunnlag.getBruddPeriodeListe().get(beregnForskuddListeGrunnlag.getBruddPeriodeListe().size() - 1).getDatoTil()
              == null)) {
        var nyPeriode = new Periode(
            beregnForskuddListeGrunnlag.getBruddPeriodeListe().get(beregnForskuddListeGrunnlag.getBruddPeriodeListe().size() - 2).getDatoFra(), null);
        beregnForskuddListeGrunnlag.getBruddPeriodeListe().remove(beregnForskuddListeGrunnlag.getBruddPeriodeListe().size() - 1);
        beregnForskuddListeGrunnlag.getBruddPeriodeListe().remove(beregnForskuddListeGrunnlag.getBruddPeriodeListe().size() - 1);
        beregnForskuddListeGrunnlag.getBruddPeriodeListe().add(nyPeriode);
      }
    }
  }

  // Løper gjennom alle bruddperioder og foretar beregning
  private void beregnForskuddPerPeriode(BeregnForskuddListeGrunnlag beregnForskuddListeGrunnlag) {

    // Løper gjennom periodene og finner matchende verdi for hver kategori
    for (Periode beregningsperiode : beregnForskuddListeGrunnlag.getBruddPeriodeListe()) {

      var inntektListe = beregnForskuddListeGrunnlag.getJustertInntektPeriodeListe().stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(inntektPeriode -> new Inntekt(inntektPeriode.getInntektType(), inntektPeriode.getInntektBelop())).collect(toList());

      var sivilstandKode = beregnForskuddListeGrunnlag.getJustertSivilstandPeriodeListe().stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(SivilstandPeriode::getSivilstandKode).findFirst().orElse(null);

      var alder = beregnForskuddListeGrunnlag.getJustertAlderPeriodeListe().stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(AlderPeriode::getAlder).findFirst().orElse(null);

      var bostatusKode = beregnForskuddListeGrunnlag.getJustertBostatusPeriodeListe().stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(BostatusPeriode::getBostatusKode).findFirst().orElse(null);

      var antallBarn = (int) beregnForskuddListeGrunnlag.getJustertBarnPeriodeListe().stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode)).count();

      var sjablonListe = beregnForskuddListeGrunnlag.getJustertSjablonPeriodeListe().stream()
          .filter(i -> i.getDatoFraTil().overlapperMed(beregningsperiode))
          .map(sjablonPeriode -> new Sjablon(sjablonPeriode.getSjablon().getSjablonNavn(),
              sjablonPeriode.getSjablon().getSjablonNokkelListe(),
              sjablonPeriode.getSjablon().getSjablonInnholdListe())).collect(toList());

      // Øk antall barn med 1 hvis søknadsbarnet bor hjemme
      if (BostatusKode.MED_FORELDRE.equals(bostatusKode)) {
        antallBarn = antallBarn + 1;
      }

      // Kaller beregningsmodulen for hver beregningsperiode
      var grunnlagBeregning = new GrunnlagBeregning(inntektListe, sivilstandKode, antallBarn, alder, bostatusKode, sjablonListe);

      beregnForskuddListeGrunnlag.getPeriodeResultatListe()
          .add(new ResultatPeriode(beregningsperiode, forskuddBeregning.beregn(grunnlagBeregning), grunnlagBeregning));
    }
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
      bruddAlderListe.add(new AlderPeriode(new Periode(beregnDatoFra.with(firstDayOfMonth()), barn11AarDato.with(firstDayOfMonth())), 0));
      if (barn18AarIPerioden) {
        bruddAlderListe.add(new AlderPeriode(new Periode(barn11AarDato.with(firstDayOfMonth()), barn18AarDato.with(firstDayOfMonth())), 11));
        bruddAlderListe.add(new AlderPeriode(new Periode(barn18AarDato.with(firstDayOfMonth()), null), 18));
      } else {
        bruddAlderListe.add(new AlderPeriode(new Periode(barn11AarDato.with(firstDayOfMonth()), null), 11));
      }

    } else {
      if (barn18AarIPerioden) {
        bruddAlderListe.add(new AlderPeriode(new Periode(beregnDatoFra.with(firstDayOfMonth()), barn18AarDato.with(firstDayOfMonth())), 11));
        bruddAlderListe.add(new AlderPeriode(new Periode(barn18AarDato.with(firstDayOfMonth()), null), 18));

      } else {
        bruddAlderListe.add(new AlderPeriode(new Periode(beregnDatoFra.with(firstDayOfMonth()), null), alderStartPeriode));
      }
    }

    return bruddAlderListe;
  }

  // Validerer at input-verdier til forskuddsberegning er gyldige
  public List<Avvik> validerInput(BeregnForskuddGrunnlag periodeGrunnlag) {

    // Sjekk beregn dato fra/til
    var avvikListe = new ArrayList<>(PeriodeUtil.validerBeregnPeriodeInput(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil()));

    // Sjekk perioder for inntekt
    var bidragMottakerInntektPeriodeListe = new ArrayList<Periode>();
    for (InntektPeriode bidragMottakerInntektPeriode : periodeGrunnlag.getBidragMottakerInntektPeriodeListe()) {
      bidragMottakerInntektPeriodeListe.add(bidragMottakerInntektPeriode.getDatoFraTil());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "bidragMottakerInntektPeriodeListe",
            bidragMottakerInntektPeriodeListe, false, true, false, true));

    // Sjekk perioder for sivilstand
    var bidragMottakerSivilstandPeriodeListe = new ArrayList<Periode>();
    for (SivilstandPeriode bidragMottakerSivilstandPeriode : periodeGrunnlag.getBidragMottakerSivilstandPeriodeListe()) {
      bidragMottakerSivilstandPeriodeListe.add(bidragMottakerSivilstandPeriode.getDatoFraTil());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "bidragMottakerSivilstandPeriodeListe",
            bidragMottakerSivilstandPeriodeListe, true, true, true, true));

    // Sjekk perioder for bostatus
    var soknadBarnBostatusPeriodeListe = new ArrayList<Periode>();
    for (BostatusPeriode soknadBarnBostatusPeriode : periodeGrunnlag.getSoknadBarn().getSoknadBarnBostatusPeriodeListe()) {
      soknadBarnBostatusPeriodeListe.add(soknadBarnBostatusPeriode.getDatoFraTil());
    }
    avvikListe.addAll(
        PeriodeUtil.validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "soknadBarnBostatusPeriodeListe",
            soknadBarnBostatusPeriodeListe, true, true, true, true));

    // Sjekk perioder for barn
    if (periodeGrunnlag.getBidragMottakerBarnPeriodeListe() != null) {
      var bidragMottakerBarnPeriodeListe = new ArrayList<Periode>();

      for (Periode bidragMottakerBarnPeriode : periodeGrunnlag.getBidragMottakerBarnPeriodeListe()) {
        bidragMottakerBarnPeriodeListe.add(bidragMottakerBarnPeriode.getDatoFraTil());
      }
      avvikListe.addAll(
          PeriodeUtil.validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "bidragMottakerBarnPeriodeListe",
              bidragMottakerBarnPeriodeListe, false, false, false, false));
    }

    // Sjekk perioder for sjablonliste
    var sjablonPeriodeListe = new ArrayList<Periode>();
    for (SjablonPeriode sjablonPeriode : periodeGrunnlag.getSjablonPeriodeListe()) {
      sjablonPeriodeListe.add(sjablonPeriode.getDatoFraTil());
    }
    avvikListe.addAll(PeriodeUtil
        .validerInputDatoer(periodeGrunnlag.getBeregnDatoFra(), periodeGrunnlag.getBeregnDatoTil(), "sjablonPeriodeListe", sjablonPeriodeListe, false,
            false, false, false));

    // Valider inntekter
    var inntektGrunnlagListe = periodeGrunnlag.getBidragMottakerInntektPeriodeListe().stream()
        .map(inntektPeriode -> new InntektGrunnlag(inntektPeriode.getInntektDatoFraTil(), inntektPeriode.getInntektType(),
            inntektPeriode.getInntektBelop()))
        .collect(toList());
    avvikListe.addAll(InntektUtil.validerInntekter(inntektGrunnlagListe, SoknadType.FORSKUDD, Rolle.BIDRAGSMOTTAKER));

    return avvikListe;
  }
}
