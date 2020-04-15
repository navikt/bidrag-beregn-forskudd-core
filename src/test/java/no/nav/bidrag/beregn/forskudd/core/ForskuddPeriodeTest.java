package no.nav.bidrag.beregn.forskudd.core;

import static no.nav.bidrag.beregn.forskudd.core.bo.BostatusKode.MED_ANDRE_ENN_FORELDRE;
import static no.nav.bidrag.beregn.forskudd.core.bo.BostatusKode.MED_FORELDRE;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_125_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_50_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_75_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.SivilstandKode.ENSLIG;
import static no.nav.bidrag.beregn.forskudd.core.bo.SivilstandKode.GIFT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import no.nav.bidrag.beregn.forskudd.core.bo.Avvik;
import no.nav.bidrag.beregn.forskudd.core.bo.AvvikType;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddGrunnlag;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.BostatusPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.InntektPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.Periode;
import no.nav.bidrag.beregn.forskudd.core.bo.SivilstandPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SjablonPeriode;
import no.nav.bidrag.beregn.forskudd.core.bo.SoknadBarn;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ForskuddsperiodeberegningTest")
class ForskuddPeriodeTest {

  private BeregnForskuddGrunnlag grunnlag;

  private final ForskuddPeriode forskuddPeriode = ForskuddPeriode.getInstance();

  @Test
  @DisplayName("Test utvidet grunnlag")
  void testUtvidetGrunnlag() {
    lagGrunnlag();
    var resultat = forskuddPeriode.beregnPerioder(grunnlag);
    assertAll(
        () -> assertThat(resultat).isNotNull(),
        () -> assertThat(resultat.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(resultat.getResultatPeriodeListe().size()).isEqualTo(8),

        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2017-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKode())
            .isEqualTo(INNVILGET_100_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 8"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2017-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatKode())
            .isEqualTo(INNVILGET_125_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 7"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-05-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatKode())
            .isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 15"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-05-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatKode())
            .isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(3).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-09-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2018-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatBeregning().getResultatKode())
            .isEqualTo(INNVILGET_125_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(4).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 4"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2018-12-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatBeregning().getResultatKode())
            .isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(5).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(6).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-01-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(6).getResultatDatoFraTil().getDatoTil()).isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(6).getResultatBeregning().getResultatKode())
            .isEqualTo(INNVILGET_50_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(6).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 12"),

        () -> assertThat(resultat.getResultatPeriodeListe().get(7).getResultatDatoFraTil().getDatoFra()).isEqualTo(LocalDate.parse("2019-04-01")),
        () -> assertThat(resultat.getResultatPeriodeListe().get(7).getResultatDatoFraTil().getDatoTil()).isNull(),
        () -> assertThat(resultat.getResultatPeriodeListe().get(7).getResultatBeregning().getResultatKode())
            .isEqualTo(INNVILGET_75_PROSENT),
        () -> assertThat(resultat.getResultatPeriodeListe().get(7).getResultatBeregning().getResultatBeskrivelse()).isEqualTo("REGEL 11")
    );
    printGrunnlagResultat(resultat);
  }


  @Test
  @DisplayName("Test utvidet grunnlag med avvik")
  @Disabled
  void testUtvidetGrunnlagMedAvvik() {
    lagGrunnlagMedAvvik();
    var avvikListe = forskuddPeriode.validerInput(grunnlag);
    assertAll(
        () -> assertThat(avvikListe).isNotEmpty(),
        () -> assertThat(avvikListe).hasSize(6),

        () -> assertThat(avvikListe.get(0).getAvvikTekst())
            .isEqualTo("Overlappende perioder i bidragMottakerInntektPeriodeListe: periodeDatoTil=2018-01-02, periodeDatoFra=2018-01-01"),
        () -> assertThat(avvikListe.get(0).getAvvikType()).isEqualTo(AvvikType.PERIODER_OVERLAPPER),

        () -> assertThat(avvikListe.get(1).getAvvikTekst())
            .isEqualTo("periodeDatoTil kan ikke være null i bidragMottakerInntektPeriodeListe: periodeDatoFra=2018-01-01, periodeDatoTil=null"),
        () -> assertThat(avvikListe.get(1).getAvvikType()).isEqualTo(AvvikType.NULL_VERDI_I_DATO),

        () -> assertThat(avvikListe.get(2).getAvvikTekst())
            .isEqualTo("Opphold mellom perioder i bidragMottakerSivilstandPeriodeListe: periodeDatoTil=2018-04-01, periodeDatoFra=2018-04-17"),
        () -> assertThat(avvikListe.get(2).getAvvikType()).isEqualTo(AvvikType.PERIODER_HAR_OPPHOLD),

        () -> assertThat(avvikListe.get(3).getAvvikTekst())
            .isEqualTo("periodeDatoFra kan ikke være null i soknadBarnBostatusPeriodeListe: periodeDatoFra=null, periodeDatoTil=2018-11-13"),
        () -> assertThat(avvikListe.get(3).getAvvikType()).isEqualTo(AvvikType.NULL_VERDI_I_DATO),

        () -> assertThat(avvikListe.get(4).getAvvikTekst()).isEqualTo(
            "periodeDatoTil må være etter periodeDatoFra i bidragMottakerBarnPeriodeListe: periodeDatoFra=2019-03-31, periodeDatoTil=2018-06-17"),
        () -> assertThat(avvikListe.get(4).getAvvikType()).isEqualTo(AvvikType.DATO_FRA_ETTER_DATO_TIL),

        () -> assertThat(avvikListe.get(5).getAvvikTekst()).isEqualTo("beregnDatoTil må være etter beregnDatoFra"),
        () -> assertThat(avvikListe.get(5).getAvvikType()).isEqualTo(AvvikType.DATO_FRA_ETTER_DATO_TIL)
    );
    printAvvikListe(avvikListe);
  }

  private void lagGrunnlag() {
    var beregnDatoFra = LocalDate.parse("2017-01-01");
    var beregnDatoTil = LocalDate.parse("2019-08-01");

    var sBFodselsdato = LocalDate.parse("2006-12-19");
    var sBBostedStatusListe = new ArrayList<BostatusPeriode>();
    sBBostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2018-08-16")), MED_FORELDRE));
    sBBostedStatusListe
        .add(new BostatusPeriode(new Periode(LocalDate.parse("2018-08-16"), LocalDate.parse("2018-11-13")), MED_ANDRE_ENN_FORELDRE));
    sBBostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2018-11-13"), null), MED_FORELDRE));
    var soknadBarn = new SoknadBarn(sBFodselsdato, sBBostedStatusListe);

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), BigDecimal.valueOf(250000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")), BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), null), BigDecimal.valueOf(500000)));

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-17")), GIFT));
    bmSivilstandListe.add(new SivilstandPeriode(new Periode(LocalDate.parse("2018-04-17"), null), ENSLIG));

    var bmBarnListe = new ArrayList<Periode>();
    bmBarnListe.add(new Periode(LocalDate.parse("2017-01-01"), null));
    bmBarnListe.add(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-17")));
    bmBarnListe.add(new Periode(LocalDate.parse("2019-03-31"), null));

    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null), "0005", 1600));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null), "0013", 320));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null), "0033", 270200));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null), "0034", 419700));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null), "0035", 336500));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null), "0036", 61700));

    grunnlag = new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadBarn, bmInntektListe, bmSivilstandListe, bmBarnListe,
        sjablonPeriodeListe);
  }

  private void lagGrunnlagMedAvvik() {
    var beregnDatoFra = LocalDate.parse("2017-01-01");
    var beregnDatoTil = LocalDate.parse("2017-01-01");

    var sBFodselsdato = LocalDate.parse("2006-12-19");
    var sBBostedStatusListe = new ArrayList<BostatusPeriode>();
    sBBostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2006-12-19"), LocalDate.parse("2018-08-16")), MED_FORELDRE));
    sBBostedStatusListe
        .add(new BostatusPeriode(new Periode(null, LocalDate.parse("2018-11-13")), MED_ANDRE_ENN_FORELDRE));
    sBBostedStatusListe.add(new BostatusPeriode(new Periode(LocalDate.parse("2018-11-13"), null), MED_FORELDRE));
    var soknadBarn = new SoknadBarn(sBFodselsdato, sBBostedStatusListe);

    var bmInntektListe = new ArrayList<InntektPeriode>();
    bmInntektListe.add(new InntektPeriode(new Periode(null, LocalDate.parse("2018-01-02")), BigDecimal.valueOf(250000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2018-01-01"), null), BigDecimal.valueOf(400000)));
    bmInntektListe.add(new InntektPeriode(new Periode(LocalDate.parse("2019-01-01"), null), BigDecimal.valueOf(500000)));

    var bmSivilstandListe = new ArrayList<SivilstandPeriode>();
    bmSivilstandListe.add(new SivilstandPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-04-01")), GIFT));
    bmSivilstandListe.add(new SivilstandPeriode(new Periode(LocalDate.parse("2018-04-17"), null), ENSLIG));

    var bmBarnListe = new ArrayList<Periode>();
    bmBarnListe.add(new Periode(LocalDate.parse("2017-01-01"), null));
    bmBarnListe.add(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-06-17")));
    bmBarnListe.add(new Periode(LocalDate.parse("2019-03-31"), LocalDate.parse("2018-06-17")));

    var sjablonPeriodeListe = new ArrayList<SjablonPeriode>();
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null), "0005", 1600));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null), "0013", 320));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null), "0033", 270200));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null), "0034", 419700));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null), "0035", 336500));
    sjablonPeriodeListe.add(new SjablonPeriode(new Periode(LocalDate.parse("2017-01-01"), null), "0036", 61700));

    grunnlag = new BeregnForskuddGrunnlag(beregnDatoFra, beregnDatoTil, soknadBarn, bmInntektListe, bmSivilstandListe, bmBarnListe,
        sjablonPeriodeListe);
  }

  private void printGrunnlagResultat(BeregnForskuddResultat resultat) {
    resultat.getResultatPeriodeListe().stream().sorted(Comparator.comparing(pR -> pR.getResultatDatoFraTil().getDatoFra()))
        .forEach(sortedPR -> System.out.println(
            "Dato fra: " + sortedPR.getResultatDatoFraTil().getDatoFra() + "; " + "Dato til: " + sortedPR.getResultatDatoFraTil().getDatoTil()
                + "; " + "Beløp: " + sortedPR.getResultatBeregning().getResultatBelop().intValue() + "; " + "Resultatkode: " + sortedPR
                .getResultatBeregning().getResultatKode()
                + "; " + "Regel: " + sortedPR.getResultatBeregning().getResultatBeskrivelse()));
  }

  private void printAvvikListe(List<Avvik> avvikListe) {
    avvikListe.forEach(avvik -> System.out.println("Avvik tekst: " + avvik.getAvvikTekst() + "; " + "Avvik type: " + avvik.getAvvikType()));
  }
}
