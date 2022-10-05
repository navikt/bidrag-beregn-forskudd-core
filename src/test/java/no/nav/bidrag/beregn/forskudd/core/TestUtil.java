package no.nav.bidrag.beregn.forskudd.core;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.bidrag.beregn.felles.enums.BostatusKode.MED_ANDRE_ENN_FORELDRE;
import static no.nav.bidrag.beregn.felles.enums.BostatusKode.MED_FORELDRE;
import static no.nav.bidrag.beregn.felles.enums.SivilstandKode.ENSLIG;
import static no.nav.bidrag.beregn.felles.enums.SivilstandKode.GIFT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.AVSLAG;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.FORHOYET_FORSKUDD_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.bo.Avvik;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.Sjablon;
import no.nav.bidrag.beregn.felles.bo.SjablonInnhold;
import no.nav.bidrag.beregn.felles.bo.SjablonNavnVerdi;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.dto.PeriodeCore;
import no.nav.bidrag.beregn.felles.enums.AvvikType;
import no.nav.bidrag.beregn.felles.enums.BostatusKode;
import no.nav.bidrag.beregn.felles.enums.InntektType;
import no.nav.bidrag.beregn.felles.enums.SivilstandKode;
import no.nav.bidrag.beregn.felles.enums.SjablonInnholdNavn;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.forskudd.core.bo.Alder;
import no.nav.bidrag.beregn.forskudd.core.bo.BarnIHusstanden;
import no.nav.bidrag.beregn.forskudd.core.bo.BarnIHusstandenPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.Bostatus;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.Sivilstand;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SoknadBarn;
import no.nav.bidrag.beregn.forskudd.core.dto.BarnIHusstandenPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SivilstandPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnCore;

public class TestUtil {

  private static final String INNTEKT_REFERANSE_1 = "INNTEKT_REFERANSE_1";
  private static final String INNTEKT_REFERANSE_2 = "INNTEKT_REFERANSE_2";
  private static final String INNTEKT_REFERANSE_3 = "INNTEKT_REFERANSE_3";
  private static final String INNTEKT_REFERANSE_4 = "INNTEKT_REFERANSE_4";
  private static final String INNTEKT_REFERANSE_5 = "INNTEKT_REFERANSE_5";
  private static final String SIVILSTAND_REFERANSE_GIFT = "SIVILSTAND_REFERANSE_GIFT";
  private static final String SIVILSTAND_REFERANSE_ENSLIG = "SIVILSTAND_REFERANSE_ENSLIG";
  private static final String BARN_I_HUSSTANDEN_REFERANSE_1 = "BARN_I_HUSSTANDEN_REFERANSE_1";
  private static final String BARN_I_HUSSTANDEN_REFERANSE_2 = "BARN_I_HUSSTANDEN_REFERANSE_2";
  private static final String BARN_I_HUSSTANDEN_REFERANSE_3 = "BARN_I_HUSSTANDEN_REFERANSE_3";
  private static final String BARN_I_HUSSTANDEN_REFERANSE_4 = "BARN_I_HUSSTANDEN_REFERANSE_4";
  private static final String SOKNADBARN_REFERANSE = "SOKNADBARN_REFERANSE";
  private static final String BOSTATUS_REFERANSE_MED_FORELDRE_1 = "BOSTATUS_REFERANSE_MED_FORELDRE_1";
  private static final String BOSTATUS_REFERANSE_MED_FORELDRE_2 = "BOSTATUS_REFERANSE_MED_FORELDRE_2";
  private static final String BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE = "BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE";

  public static List<Sjablon> byggSjablonListe() {

    var sjablonListe = new ArrayList<Sjablon>();

    // Sjablontall
    sjablonListe.add(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1710)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(320)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(270200)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(419700)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(336500)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(61700)))));
    sjablonListe.add(new Sjablon(SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP.getNavn(), emptyList(),
        singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1280)))));

    return sjablonListe;
  }

  public static List<SjablonNavnVerdi> byggSjablonNavnVerdiListe() {

    var sjablonListe = new ArrayList<SjablonNavnVerdi>();

    // Sjablontall
    sjablonListe.add(new SjablonNavnVerdi(SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP.getNavn(), BigDecimal.valueOf(1280)));
    sjablonListe.add(new SjablonNavnVerdi(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), BigDecimal.valueOf(1710)));
    sjablonListe.add(new SjablonNavnVerdi(SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.getNavn(), BigDecimal.valueOf(320)));
    sjablonListe.add(new SjablonNavnVerdi(SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.getNavn(), BigDecimal.valueOf(270200)));
    sjablonListe.add(new SjablonNavnVerdi(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.getNavn(), BigDecimal.valueOf(419700)));
    sjablonListe.add(new SjablonNavnVerdi(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.getNavn(), BigDecimal.valueOf(336500)));
    sjablonListe.add(new SjablonNavnVerdi(SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.getNavn(), BigDecimal.valueOf(61700)));

    return sjablonListe;
  }

  public static List<SjablonPeriodeNavnVerdi> byggSjablonPeriodeNavnVerdiListe() {

    var sjablonListe = new ArrayList<SjablonPeriodeNavnVerdi>();

    // Sjablontall
    sjablonListe.add(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), null),
        SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP.getNavn(), BigDecimal.valueOf(1280)));
    sjablonListe.add(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), null),
        SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), BigDecimal.valueOf(1710)));
    sjablonListe.add(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), null),
        SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.getNavn(), BigDecimal.valueOf(320)));
    sjablonListe.add(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), null),
        SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.getNavn(), BigDecimal.valueOf(270200)));
    sjablonListe.add(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), null),
        SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.getNavn(), BigDecimal.valueOf(419700)));
    sjablonListe.add(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), null),
        SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.getNavn(), BigDecimal.valueOf(336500)));
    sjablonListe.add(new SjablonPeriodeNavnVerdi(new Periode(LocalDate.parse("2017-01-01"), null),
        SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.getNavn(), BigDecimal.valueOf(61700)));

    return sjablonListe;
  }

  public static List<SjablonPeriode> byggSjablonPeriodeListe() {

    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();

    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1710))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(320))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(270200))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(419700))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(336500))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(61700))))));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null),
        new Sjablon(SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP.getNavn(), emptyList(),
            singletonList(new SjablonInnhold(SjablonInnholdNavn.SJABLON_VERDI.getNavn(), BigDecimal.valueOf(1280))))));

    return sjablonPeriodeListe;
  }

  public static BeregnForskuddGrunnlagCore byggForskuddGrunnlagCore() {
    var bostatusPeriode = new BostatusPeriodeCore(BOSTATUS_REFERANSE_MED_FORELDRE_1,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")),
        BostatusKode.MED_FORELDRE.toString());
    var soknadBarn = new SoknadBarnCore(SOKNADBARN_REFERANSE, LocalDate.parse("2006-05-12"), singletonList(bostatusPeriode));

    var bidragMottakerInntektPeriode = new InntektPeriodeCore(INNTEKT_REFERANSE_1,
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER.toString(), BigDecimal.valueOf(0));
    var bidragMottakerInntektPeriodeListe = singletonList(bidragMottakerInntektPeriode);

    var bidragMottakerSivilstandPeriode1 = new SivilstandPeriodeCore(SIVILSTAND_REFERANSE_GIFT,
        new PeriodeCore(LocalDate.parse("2018-01-01"), LocalDate.parse("2020-01-01")), SivilstandKode.GIFT.toString());
    var bidragMottakerSivilstandPeriode2 = new SivilstandPeriodeCore(SIVILSTAND_REFERANSE_ENSLIG,
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), SivilstandKode.ENSLIG.toString());
    var bidragMottakerSivilstandPeriodeListe = new ArrayList<SivilstandPeriodeCore>();
    bidragMottakerSivilstandPeriodeListe.add(bidragMottakerSivilstandPeriode1);
    bidragMottakerSivilstandPeriodeListe.add(bidragMottakerSivilstandPeriode2);

    var barnIHusstandenPeriode = new BarnIHusstandenPeriodeCore(
        BARN_I_HUSSTANDEN_REFERANSE_1, new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), 1d);
    var bidragMottakerBarnPeriodeListe = singletonList(barnIHusstandenPeriode);

    return new BeregnForskuddGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"), soknadBarn,
        bidragMottakerInntektPeriodeListe, bidragMottakerSivilstandPeriodeListe, bidragMottakerBarnPeriodeListe, emptyList());
  }

  public static BeregnForskuddResultat byggForskuddResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();
    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(1600), FORHOYET_FORSKUDD_100_PROSENT, "REGEL 1", TestUtil.byggSjablonPeriodeNavnVerdiListe()),
        new GrunnlagBeregning(singletonList(
            new Inntekt(INNTEKT_REFERANSE_1, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(500000))),
            new Sivilstand(SIVILSTAND_REFERANSE_ENSLIG, SivilstandKode.ENSLIG),
            new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 2d),
            new Alder(SOKNADBARN_REFERANSE, 10),
            new Bostatus(BOSTATUS_REFERANSE_MED_FORELDRE_1, BostatusKode.MED_FORELDRE),
            TestUtil.byggSjablonPeriodeListe())));
    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(1200), ORDINAERT_FORSKUDD_75_PROSENT, "REGEL 2", TestUtil.byggSjablonPeriodeNavnVerdiListe()),
        new GrunnlagBeregning(singletonList(
            new Inntekt(INNTEKT_REFERANSE_2, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(500000))),
            new Sivilstand(SIVILSTAND_REFERANSE_ENSLIG, SivilstandKode.ENSLIG),
            new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 2d),
            new Alder(SOKNADBARN_REFERANSE, 10),
            new Bostatus(BOSTATUS_REFERANSE_MED_FORELDRE_1, BostatusKode.MED_FORELDRE),
            TestUtil.byggSjablonPeriodeListe())));
    periodeResultatListe.add(new ResultatPeriode(
        new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")),
        new ResultatBeregning(BigDecimal.valueOf(0), AVSLAG, "REGEL 11", TestUtil.byggSjablonPeriodeNavnVerdiListe()),
        new GrunnlagBeregning(singletonList(
            new Inntekt(INNTEKT_REFERANSE_3, InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(500000))),
            new Sivilstand(SIVILSTAND_REFERANSE_ENSLIG, SivilstandKode.ENSLIG),
            new BarnIHusstanden(BARN_I_HUSSTANDEN_REFERANSE_1, 2d),
            new Alder(SOKNADBARN_REFERANSE, 10),
            new Bostatus(BOSTATUS_REFERANSE_MED_FORELDRE_1, BostatusKode.MED_FORELDRE),
            TestUtil.byggSjablonPeriodeListe())));
    return new BeregnForskuddResultat(periodeResultatListe);
  }

  public static List<Avvik> byggAvvikListe() {
    return singletonList(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FOM_ETTER_DATO_TIL));
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlag() {
    return TestUtil.byggForskuddGrunnlag("2017-01-01", "2019-08-01");
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlag(String beregnDatoFra, String beregnDatoTil) {
    var sBFodselsdato = LocalDate.parse("2006-12-19");
    var sBBostedStatusListe = new ArrayList<BostatusPeriode>();
    sBBostedStatusListe.add(new BostatusPeriode(BOSTATUS_REFERANSE_MED_FORELDRE_1,
        new Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2018-08-16")), MED_FORELDRE));
    sBBostedStatusListe.add(new BostatusPeriode(BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE,
        new Periode(LocalDate.parse("2018-08-16"), LocalDate.parse("2018-11-13")), MED_ANDRE_ENN_FORELDRE));
    sBBostedStatusListe.add(new BostatusPeriode(BOSTATUS_REFERANSE_MED_FORELDRE_2, new Periode(LocalDate.parse("2018-11-13"), null), MED_FORELDRE));
    var soknadBarn = new SoknadBarn(SOKNADBARN_REFERANSE, sBFodselsdato, sBBostedStatusListe);

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(250000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_2, new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_3, new Periode(LocalDate.parse("2019-01-01"), null),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(500000)));

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(SIVILSTAND_REFERANSE_GIFT, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-17")),
        GIFT));
    bmSivilstandListe
        .add(new SivilstandPeriode(SIVILSTAND_REFERANSE_ENSLIG, new Periode(LocalDate.parse("2018-04-17"), LocalDate.parse("2019-08-01")),
            ENSLIG));

    var bmBarnListe = new ArrayList<BarnIHusstandenPeriode>();
    bmBarnListe.add(new BarnIHusstandenPeriode(BARN_I_HUSSTANDEN_REFERANSE_1, new Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2017-01-01")), 1d));
    bmBarnListe.add(new BarnIHusstandenPeriode(BARN_I_HUSSTANDEN_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-17")), 3d));
    bmBarnListe.add(new BarnIHusstandenPeriode(BARN_I_HUSSTANDEN_REFERANSE_2, new Periode(LocalDate.parse("2018-06-17"), LocalDate.parse("2018-08-16")), 2d));
    bmBarnListe.add(new BarnIHusstandenPeriode(BARN_I_HUSSTANDEN_REFERANSE_2, new Periode(LocalDate.parse("2018-08-16"), LocalDate.parse("2018-11-13")), 1d));
    bmBarnListe.add(new BarnIHusstandenPeriode(BARN_I_HUSSTANDEN_REFERANSE_3, new Periode(LocalDate.parse("2018-11-13"), LocalDate.parse("2019-03-31")), 2d));
    bmBarnListe.add(new BarnIHusstandenPeriode(BARN_I_HUSSTANDEN_REFERANSE_4, new Periode(LocalDate.parse("2019-03-31"), null), 3d));

    var sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

    return new BeregnForskuddGrunnlag(LocalDate.parse(beregnDatoFra), LocalDate.parse(beregnDatoTil), soknadBarn, bmInntektListe, bmSivilstandListe,
        bmBarnListe, sjablonPeriodeListe);
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagMedAvvik() {
    var beregnDatoFra = LocalDate.parse("2017-01-01");
    var beregnDatoTil = LocalDate.parse("2017-01-01");

    var sBFodselsdato = LocalDate.parse("2006-12-19");
    var sBBostedStatusListe = new ArrayList<BostatusPeriode>();
    sBBostedStatusListe
        .add(new BostatusPeriode(BOSTATUS_REFERANSE_MED_FORELDRE_1, new Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2018-08-16")),
            MED_FORELDRE));
    sBBostedStatusListe.add(new BostatusPeriode(BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE, new Periode(LocalDate.parse("2018-08-16"), null),
        MED_ANDRE_ENN_FORELDRE));
    sBBostedStatusListe.add(new BostatusPeriode(BOSTATUS_REFERANSE_MED_FORELDRE_2, new Periode(LocalDate.parse("2018-11-13"), null), MED_FORELDRE));
    var soknadBarn = new SoknadBarn(SOKNADBARN_REFERANSE, sBFodselsdato, sBBostedStatusListe);

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(250000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_2, new Periode(LocalDate.parse("2018-01-04"), LocalDate.parse("2019-01-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_3, new Periode(LocalDate.parse("2019-01-01"), null),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(500000)));

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(SIVILSTAND_REFERANSE_GIFT, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-01")),
        GIFT));
    bmSivilstandListe
        .add(new SivilstandPeriode(SIVILSTAND_REFERANSE_ENSLIG, new Periode(LocalDate.parse("2018-03-17"), LocalDate.parse("2019-07-01")),
            ENSLIG));

    var bmBarnListe = new ArrayList<BarnIHusstandenPeriode>();
    bmBarnListe.add(new BarnIHusstandenPeriode(
        BARN_I_HUSSTANDEN_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), null), 1d));
    bmBarnListe.add(new BarnIHusstandenPeriode(
        BARN_I_HUSSTANDEN_REFERANSE_2, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-17")), 1d));
    bmBarnListe.add(new BarnIHusstandenPeriode(
        BARN_I_HUSSTANDEN_REFERANSE_3, new Periode(LocalDate.parse("2019-03-31"), LocalDate.parse("2018-06-17")), 1d));

    var sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

    return new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadBarn, bmInntektListe, bmSivilstandListe, bmBarnListe, sjablonPeriodeListe);
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagMedUgyldigInntekt() {
    var beregnDatoFra = LocalDate.parse("2017-01-01");
    var beregnDatoTil = LocalDate.parse("2019-01-01");

    var sBFodselsdato = LocalDate.parse("2006-12-19");
    var sBBostedStatusListe = new ArrayList<BostatusPeriode>();
    sBBostedStatusListe.add(new BostatusPeriode(BOSTATUS_REFERANSE_MED_FORELDRE_1, new Periode(LocalDate.parse("2006-12-19"),
        LocalDate.parse("2018-08-16")), MED_FORELDRE));
    sBBostedStatusListe.add(new BostatusPeriode(BOSTATUS_REFERANSE_MED_ANDRE_ENN_FORELDRE, new Periode(LocalDate.parse("2018-08-16"),
        LocalDate.parse("2018-11-13")), MED_ANDRE_ENN_FORELDRE));
    sBBostedStatusListe.add(new BostatusPeriode(BOSTATUS_REFERANSE_MED_FORELDRE_2, new Periode(LocalDate.parse("2018-11-13"), null), MED_FORELDRE));
    var soknadBarn = new SoknadBarn(SOKNADBARN_REFERANSE, sBFodselsdato, sBBostedStatusListe);

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")),
        InntektType.ALOYSE, BigDecimal.valueOf(250000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_2, new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_3, new Periode(LocalDate.parse("2019-01-01"), null),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(500000)));

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(SIVILSTAND_REFERANSE_GIFT, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-17")),
        GIFT));
    bmSivilstandListe
        .add(new SivilstandPeriode(SIVILSTAND_REFERANSE_ENSLIG, new Periode(LocalDate.parse("2018-04-17"), LocalDate.parse("2019-08-01")),
            ENSLIG));

    var bmBarnListe = new ArrayList<BarnIHusstandenPeriode>();
    bmBarnListe.add(new BarnIHusstandenPeriode(
        BARN_I_HUSSTANDEN_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), null), 1d));
    bmBarnListe.add(new BarnIHusstandenPeriode(
        BARN_I_HUSSTANDEN_REFERANSE_2, new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-17")), 1d));
    bmBarnListe.add(new BarnIHusstandenPeriode(
        BARN_I_HUSSTANDEN_REFERANSE_3, new Periode(LocalDate.parse("2019-03-31"), null), 1d));

    var sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

    return new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadBarn, bmInntektListe, bmSivilstandListe, bmBarnListe, sjablonPeriodeListe);
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagUtenAndreBarn() {
    var beregnDatoFra = LocalDate.parse("2017-01-01");
    var beregnDatoTil = LocalDate.parse("2017-02-01");

    var sBFodselsdato = LocalDate.parse("2006-12-19");
    var sBBostedStatusListe = new ArrayList<BostatusPeriode>();
    sBBostedStatusListe.add(new BostatusPeriode(BOSTATUS_REFERANSE_MED_FORELDRE_1, new Periode(LocalDate.parse("2006-12-19"), null), MED_FORELDRE));
    var soknadBarn = new SoknadBarn(SOKNADBARN_REFERANSE, sBFodselsdato, sBBostedStatusListe);

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), null),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(250000)));

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(SIVILSTAND_REFERANSE_GIFT, new Periode(LocalDate.parse("2017-01-01"), null), GIFT));

    var bmBarnListe = new ArrayList<BarnIHusstandenPeriode>();
    bmBarnListe.add(new BarnIHusstandenPeriode(
        BARN_I_HUSSTANDEN_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), null), 1d));

    var sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

    return new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadBarn, bmInntektListe, bmSivilstandListe, bmBarnListe,
        sjablonPeriodeListe);
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagMedFlereInntekterISammePeriode(List<InntektPeriode> bmInntektListe) {
    var beregnDatoFra = LocalDate.parse("2017-01-01");
    var beregnDatoTil = LocalDate.parse("2018-01-01");

    var sBFodselsdato = LocalDate.parse("2007-12-19");
    var sBBostedStatusListe = new ArrayList<BostatusPeriode>();
    sBBostedStatusListe.add(new BostatusPeriode(BOSTATUS_REFERANSE_MED_FORELDRE_1, new Periode(LocalDate.parse("2017-01-01"), null), MED_FORELDRE));
    var soknadBarn = new SoknadBarn(SOKNADBARN_REFERANSE, sBFodselsdato, sBBostedStatusListe);

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(SIVILSTAND_REFERANSE_ENSLIG, new Periode(LocalDate.parse("2017-01-01"), null), ENSLIG));

    var bmBarnListe = singletonList(new BarnIHusstandenPeriode(
        BARN_I_HUSSTANDEN_REFERANSE_1, new Periode(LocalDate.parse("2017-01-01"), null), 2d));

    var sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

    return new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadBarn, bmInntektListe, bmSivilstandListe, bmBarnListe, sjablonPeriodeListe);
  }

  public static BeregnForskuddGrunnlag byggForskuddGrunnlagMedJusteringAvInntekter() {
    var beregnDatoFra = LocalDate.parse("2018-01-01");
    var beregnDatoTil = LocalDate.parse("2020-07-01");

    var sBFodselsdato = LocalDate.parse("2015-01-01");
    var sBBostedStatusListe = new ArrayList<BostatusPeriode>();
    sBBostedStatusListe.add(new BostatusPeriode(BOSTATUS_REFERANSE_MED_FORELDRE_1, new Periode(LocalDate.parse("2018-01-01"), null), MED_FORELDRE));
    var soknadBarn = new SoknadBarn(SOKNADBARN_REFERANSE, sBFodselsdato, sBBostedStatusListe);

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(SIVILSTAND_REFERANSE_ENSLIG, new Periode(LocalDate.parse("2018-01-01"), null), ENSLIG));

    var bmBarnListe = singletonList(new BarnIHusstandenPeriode(
        BARN_I_HUSSTANDEN_REFERANSE_1, new Periode(LocalDate.parse("2018-01-01"), null), 2d));

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_1, new Periode(LocalDate.parse("2018-01-01"), null),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(200000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_2, new Periode(LocalDate.parse("2018-06-01"), LocalDate.parse("2018-12-31")),
        InntektType.INNTEKTSOPPLYSNINGER_ARBEIDSGIVER, BigDecimal.valueOf(150000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_3, new Periode(LocalDate.parse("2019-01-01"), null),
        InntektType.SAKSBEHANDLER_BEREGNET_INNTEKT, BigDecimal.valueOf(300000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_4, new Periode(LocalDate.parse("2019-01-01"), null),
        InntektType.KAPITALINNTEKT_EGNE_OPPLYSNINGER, BigDecimal.valueOf(100000)));
    bmInntektListe.add(new InntektPeriode(INNTEKT_REFERANSE_5, new Periode(LocalDate.parse("2020-01-01"), null), InntektType.ATTFORING_AAP,
        BigDecimal.valueOf(250000)));

    var sjablonPeriodeListe = TestUtil.byggSjablonPeriodeListe();

    return new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadBarn, bmInntektListe, bmSivilstandListe, bmBarnListe, sjablonPeriodeListe);
  }
}
