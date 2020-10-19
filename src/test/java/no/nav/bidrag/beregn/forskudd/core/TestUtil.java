package no.nav.bidrag.beregn.forskudd.core;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.felles.enums.BostatusKode.MED_ANDRE_ENN_FORELDRE;
import static no.nav.bidrag.beregn.felles.enums.BostatusKode.MED_FORELDRE;
import static no.nav.bidrag.beregn.felles.enums.SivilstandKode.ENSLIG;
import static no.nav.bidrag.beregn.felles.enums.SivilstandKode.GIFT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.AVSLAG;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_75_PROSENT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNokkel;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.dto.SjablonCore;
import no.nav.bidrag.beregn.felles.dto.SjablonInnholdCore;
import no.nav.bidrag.beregn.felles.dto.SjablonNokkelCore;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SivilstandKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SoknadBarn;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SivilstandPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnCore;

public class TestUtil {

  public static List<Sjablon> byggSjablonListe() {

    var sjablonListe = new ArrayList<Sjablon>();

    // Sjablontall
    sjablonListe.add(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 1600d))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 320d))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 270200d))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 419700d))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 336500d))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 61700d))));

    return sjablonListe;
  }

  public static List<SjablonCore> byggSjablonCoreListeFraSjablonListe(List<Sjablon> resultatGrunnlagSjablonListe) {

    var resultatGrunnlagSjablonListeCore = new ArrayList<SjablonCore>();

    for (Sjablon resultatGrunnlagSjablon : resultatGrunnlagSjablonListe) {
      var sjablonNokkelListeCore = new ArrayList<SjablonNokkelCore>();
      var sjablonInnholdListeCore = new ArrayList<SjablonInnholdCore>();
      for (SjablonNokkel sjablonNokkel : resultatGrunnlagSjablon.getSjablonNokkelListe()) {
        sjablonNokkelListeCore.add(new SjablonNokkelCore(sjablonNokkel.getSjablonNokkelNavn(), sjablonNokkel.getSjablonNokkelVerdi()));
      }
      for (SjablonInnhold sjablonInnhold : resultatGrunnlagSjablon.getSjablonInnholdListe()) {
        sjablonInnholdListeCore.add(new SjablonInnholdCore(sjablonInnhold.getSjablonInnholdNavn(), sjablonInnhold.getSjablonInnholdVerdi()));
      }
      resultatGrunnlagSjablonListeCore
          .add(new SjablonCore(resultatGrunnlagSjablon.getSjablonNavn(), sjablonNokkelListeCore, sjablonInnholdListeCore));
    }

    return resultatGrunnlagSjablonListeCore;
  }

  public static List<SjablonPeriode> byggSjablonPeriodeListe() {

    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 1600d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 320d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 270200d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 419700d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 336500d)))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), 61700d)))));

    return sjablonPeriodeListe;
  }

  public static BeregnForskuddGrunnlagCore byggForskuddGrunnlagCore() {
    var bostatusPeriode = new BostatusPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BostatusKode.MED_FORELDRE.toString());
    var bostatusPeriodeListe = new ArrayList<BostatusPeriodeCore>();
    bostatusPeriodeListe.add(bostatusPeriode);
    var soknadBarn = new SoknadBarnCore(LocalDate.parse("2006-05-12"), bostatusPeriodeListe);

    var bidragMottakerInntektPeriode = new InntektPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER.toString(), BigDecimal.valueOf(0));
    var bidragMottakerInntektPeriodeListe = new ArrayList<InntektPeriodeCore>();
    bidragMottakerInntektPeriodeListe.add(bidragMottakerInntektPeriode);

    var bidragMottakerSivilstandPeriode1 = new SivilstandPeriodeCore(
        new PeriodeCore(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-01-01")), SivilstandKode.GIFT.toString());
    var bidragMottakerSivilstandPeriode2 = new SivilstandPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), SivilstandKode.ENSLIG.toString());
    var bidragMottakerSivilstandPeriodeListe = new ArrayList<SivilstandPeriodeCore>();
    bidragMottakerSivilstandPeriodeListe.add(bidragMottakerSivilstandPeriode1);
    bidragMottakerSivilstandPeriodeListe.add(bidragMottakerSivilstandPeriode2);

    var bidragMottakerBarnPeriodeListe = new ArrayList<PeriodeCore>();
    bidragMottakerBarnPeriodeListe.add(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")));

    return new BeregnForskuddGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"), soknadBarn,
        bidragMottakerInntektPeriodeListe, bidragMottakerSivilstandPeriodeListe, bidragMottakerBarnPeriodeListe, emptyList());
  }

  public static BeregnForskuddResultat byggForskuddResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();
    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(1600), INNVILGET_100_PROSENT, "REGEL 1"),
        new GrunnlagBeregning(Collections.singletonList(new Inntekt(InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(500000))),
            SivilstandKode.ENSLIG, 2, 10, BostatusKode.MED_FORELDRE, TestUtil.byggSjablonListe())));
    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(1200), INNVILGET_75_PROSENT, "REGEL 2"),
        new GrunnlagBeregning(Collections.singletonList(new Inntekt(InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(500000))),
            SivilstandKode.ENSLIG, 2, 10, BostatusKode.MED_FORELDRE, TestUtil.byggSjablonListe())));
    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(0), AVSLAG, "REGEL 11"),
        new GrunnlagBeregning(Collections.singletonList(new Inntekt(InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(500000))),
            SivilstandKode.ENSLIG, 2, 10, BostatusKode.MED_FORELDRE, TestUtil.byggSjablonListe())));
    return new BeregnForskuddResultat(periodeResultatListe);
  }

  public static List<Avvik> byggAvvikListe() {
    return singletonList(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FRA_ETTER_DATO_TIL));
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlag() {
    return TestUtil.byggForskuddGrunnlag("2017-01-01", "2019-08-01");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlag(String beregnDatoFra, String beregnDatoTil) {
    var sBFodselsdato = LocalDate.parse("2006-12-19");
    var sBBostedStatusListe = new ArrayList<BostatusPeriode>();
    sBBostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2018-08-16")), MED_FORELDRE));
    sBBostedStatusListe
        .add(new BostatusPeriode(new Periode(LocalDate.parse("2018-08-16"), LocalDate.parse("2018-11-13")), MED_ANDRE_ENN_FORELDRE));
    sBBostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2018-11-13"), null), MED_FORELDRE));
    var soknadBarn = new SoknadBarn(sBFodselsdato, sBBostedStatusListe);

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), InntektType.INNTEKTSOPPL_ARBEIDSGIVER,
            BigDecimal.valueOf(250000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")), InntektType.INNTEKTSOPPL_ARBEIDSGIVER,
            BigDecimal.valueOf(400000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(500000)));

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-17")), GIFT));
    bmSivilstandListe.add(new SivilstandPeriode(new Periode(LocalDate.parse("2018-04-17"), LocalDate.parse("2019-08-01")), ENSLIG));

    var bmBarnListe = new ArrayList<Periode>();
    bmBarnListe.add(new Periode(LocalDate.parse("2017-01-01"), null));
    bmBarnListe.add(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-17")));
    bmBarnListe.add(new Periode(LocalDate.parse("2019-03-31"), null));

    var sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

    return new BeregnForskuddGrunnlag(LocalDate.parse(beregnDatoFra), LocalDate.parse(beregnDatoTil), soknadBarn, bmInntektListe,
        bmSivilstandListe, bmBarnListe, sjablonPeriodeListe);
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagMedAvvik() {
    var beregnDatoFra = LocalDate.parse("2017-01-01");
    var beregnDatoTil = LocalDate.parse("2017-01-01");

    var sBFodselsdato = LocalDate.parse("2006-12-19");
    var sBBostedStatusListe = new ArrayList<BostatusPeriode>();
    sBBostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2018-08-16")), MED_FORELDRE));
    sBBostedStatusListe
        .add(new BostatusPeriode(new Periode(LocalDate.parse("2018-08-16"), null), MED_ANDRE_ENN_FORELDRE));
    sBBostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2018-11-13"), null), MED_FORELDRE));
    var soknadBarn = new SoknadBarn(sBFodselsdato, sBBostedStatusListe);

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), InntektType.INNTEKTSOPPL_ARBEIDSGIVER,
            BigDecimal.valueOf(250000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2018-01-04"), LocalDate.parse("2019-01-01")), InntektType.INNTEKTSOPPL_ARBEIDSGIVER,
            BigDecimal.valueOf(400000)));
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(500000)));

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-01")), GIFT));
    bmSivilstandListe.add(new SivilstandPeriode(new Periode(LocalDate.parse("2018-03-17"), LocalDate.parse("2019-07-01")), ENSLIG));

    var bmBarnListe = new ArrayList<Periode>();
    bmBarnListe.add(new Periode(LocalDate.parse("2017-01-01"), null));
    bmBarnListe.add(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-17")));
    bmBarnListe.add(new Periode(LocalDate.parse("2019-03-31"), LocalDate.parse("2018-06-17")));

    var sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

    return new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadBarn, bmInntektListe, bmSivilstandListe, bmBarnListe,
        sjablonPeriodeListe);
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenBarn() {
    var beregnDatoFra = LocalDate.parse("2017-01-01");
    var beregnDatoTil = LocalDate.parse("2017-02-01");

    var sBFodselsdato = LocalDate.parse("2006-12-19");
    var sBBostedStatusListe = new ArrayList<BostatusPeriode>();
    sBBostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2006-12-19"), null), MED_FORELDRE));
    var soknadBarn = new SoknadBarn(sBFodselsdato, sBBostedStatusListe);

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe
        .add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), null), InntektType.INNTEKTSOPPL_ARBEIDSGIVER, BigDecimal.valueOf(250000)));

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(new Periode(LocalDate.parse("2017-01-01"), null), GIFT));

    var sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

    return new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadBarn, bmInntektListe, bmSivilstandListe, emptyList(),
        sjablonPeriodeListe);
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagMedFlereInntekterISammePeriode(List<InntektPeriode> bmInntektListe) {
    var beregnDatoFra = LocalDate.parse("2017-01-01");
    var beregnDatoTil = LocalDate.parse("2018-01-01");

    var sBFodselsdato = LocalDate.parse("2007-12-19");
    var sBBostedStatusListe = new ArrayList<BostatusPeriode>();
    sBBostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2017-01-01"), null), MED_FORELDRE));
    var soknadBarn = new SoknadBarn(sBFodselsdato, sBBostedStatusListe);

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(new Periode(LocalDate.parse("2017-01-01"), null), ENSLIG));

    var bmBarnListe = new ArrayList<Periode>();
    bmBarnListe.add(new Periode(LocalDate.parse("2017-01-01"), null));

    var sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

    return new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadBarn, bmInntektListe, bmSivilstandListe, bmBarnListe,
        sjablonPeriodeListe);
  }
}
