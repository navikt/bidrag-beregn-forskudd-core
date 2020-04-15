package no.nav.bidrag.beregn.forskudd.core;

import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.AVSLAG;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.bo.ResultatKode.INNVILGET_75_PROSENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.forskudd.core.bo.Avvik;
import no.nav.bidrag.beregn.forskudd.core.bo.AvvikType;
import no.nav.bidrag.beregn.forskudd.core.bo.BeregnForskuddResultat;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatPeriode;
import no.nav.bidrag.beregn.forskudd.core.dto.BeregnForskuddGrunnlagCore;
import no.nav.bidrag.beregn.forskudd.core.dto.BostatusPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.InntektPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.PeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SivilstandPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SjablonPeriodeCore;
import no.nav.bidrag.beregn.forskudd.core.dto.SoknadBarnCore;
import no.nav.bidrag.beregn.forskudd.core.periode.ForskuddPeriode;
import no.nav.bidrag.beregn.forskudd.core.periode.Periode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("ForskuddCore (dto test)")
public class ForskuddCoreTest {

  @InjectMocks
  private ForskuddCore forskuddCore = ForskuddCore.getInstance();

  @Mock
  private ForskuddPeriode forskuddPeriodeMock;

  private BeregnForskuddGrunnlagCore beregnForskuddGrunnlagCore;
  private BeregnForskuddResultat forskuddPeriodeResultat;
  private List<Avvik> avvikListe;

  @BeforeEach
  void initMocksAndService() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @DisplayName("Skal beregne forskudd")
  void skalBeregneForskudd() {
    byggForskuddPeriodeGrunnlagCore();
    byggForskuddPeriodeResultat();

    when(forskuddPeriodeMock.beregnPerioder(any())).thenReturn(forskuddPeriodeResultat);
    var beregnForskuddResultatCore = forskuddCore.beregnForskudd(beregnForskuddGrunnlagCore);

    assertAll(
        () -> assertThat(beregnForskuddResultatCore).isNotNull(),
        () -> assertThat(beregnForskuddResultatCore.getAvvikListe()).isEmpty(),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe()).isNotEmpty(),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().size()).isEqualTo(3),

        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoFra()
                .equals(LocalDate.parse("2017-01-01"))),
        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatDatoFraTil().getPeriodeDatoTil()
                .equals(LocalDate.parse("2018-01-01"))),
        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBelop().equals(BigDecimal.valueOf(1600))),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatKode()
            .equals("INNVILGET_100_PROSENT")),
        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(0).getResultatBeregning().getResultatBeskrivelse().equals("REGEL 1")),

        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getPeriodeDatoFra()
                .equals(LocalDate.parse("2018-01-01"))),
        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(1).getResultatDatoFraTil().getPeriodeDatoTil()
                .equals(LocalDate.parse("2019-01-01"))),
        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBelop().equals(BigDecimal.valueOf(1200))),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatKode()
            .equals("INNVILGET_75_PROSENT")),
        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(1).getResultatBeregning().getResultatBeskrivelse().equals("REGEL 2")),

        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getPeriodeDatoFra()
                .equals(LocalDate.parse("2019-01-01"))),
        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(2).getResultatDatoFraTil().getPeriodeDatoTil()
                .equals(LocalDate.parse("2020-01-01"))),
        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBelop().equals(BigDecimal.valueOf(0))),
        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatKode().equals("AVSLAG")),
        () -> assertThat(
            beregnForskuddResultatCore.getResultatPeriodeListe().get(2).getResultatBeregning().getResultatBeskrivelse().equals("REGEL 11"))
    );
  }

  @Test
  @DisplayName("Skal ikke beregne forskudd ved avvik")
  void skalIkkeBeregneForskuddVedAvvik() {
    byggForskuddPeriodeGrunnlagCore();
    byggAvvik();

    when(forskuddPeriodeMock.validerInput(any())).thenReturn(avvikListe);
    var beregnForskuddResultatCore = forskuddCore.beregnForskudd(beregnForskuddGrunnlagCore);

    assertAll(
        () -> assertThat(beregnForskuddResultatCore).isNotNull(),
        () -> assertThat(beregnForskuddResultatCore.getAvvikListe()).isNotEmpty(),
        () -> assertThat(beregnForskuddResultatCore.getAvvikListe()).hasSize(1),
        () -> assertThat(beregnForskuddResultatCore.getAvvikListe().get(0).getAvvikTekst()).isEqualTo("beregnDatoTil må være etter beregnDatoFra"),
        () -> assertThat(beregnForskuddResultatCore.getAvvikListe().get(0).getAvvikType()).isEqualTo(AvvikType.DATO_FRA_ETTER_DATO_TIL.toString()),
        () -> assertThat(beregnForskuddResultatCore.getResultatPeriodeListe()).isEmpty()
    );
  }


  private void byggForskuddPeriodeGrunnlagCore() {
    var bostatusPeriode = new BostatusPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), "MED_FORELDRE");
    var bostatusPeriodeListe = new ArrayList<BostatusPeriodeCore>();
    bostatusPeriodeListe.add(bostatusPeriode);
    var soknadBarn = new SoknadBarnCore(LocalDate.parse("2006-05-12"), bostatusPeriodeListe);

    var bidragMottakerInntektPeriode = new InntektPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), null), BigDecimal.valueOf(0));
    var bidragMottakerInntektPeriodeListe = new ArrayList<InntektPeriodeCore>();
    bidragMottakerInntektPeriodeListe.add(bidragMottakerInntektPeriode);

    var bidragMottakerSivilstandPeriode = new SivilstandPeriodeCore(
        new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), "GIFT");
    var bidragMottakerSivilstandPeriodeListe = new ArrayList<SivilstandPeriodeCore>();
    bidragMottakerSivilstandPeriodeListe.add(bidragMottakerSivilstandPeriode);

    var bidragMottakerBarnPeriodeListe = new ArrayList<PeriodeCore>();
    bidragMottakerBarnPeriodeListe.add(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")));

    var sjablonPeriode = new SjablonPeriodeCore(new PeriodeCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01")), "0013",
        BigDecimal.valueOf(0));
    var sjablonPeriodeListe = new ArrayList<SjablonPeriodeCore>();
    sjablonPeriodeListe.add(sjablonPeriode);

    beregnForskuddGrunnlagCore = new BeregnForskuddGrunnlagCore(LocalDate.parse("2017-01-01"), LocalDate.parse("2020-01-01"), soknadBarn,
        bidragMottakerInntektPeriodeListe, bidragMottakerSivilstandPeriodeListe, bidragMottakerBarnPeriodeListe, sjablonPeriodeListe);
  }

  private void byggForskuddPeriodeResultat() {
    List<ResultatPeriode> periodeResultatListe = new ArrayList<>();
    periodeResultatListe
        .add(new ResultatPeriode(new Periode(LocalDate.parse("2017-01-01"), LocalDate.parse("2018-01-01")), new ResultatBeregning(
            BigDecimal.valueOf(1600), INNVILGET_100_PROSENT, "REGEL 1")));
    periodeResultatListe
        .add(new ResultatPeriode(new Periode(LocalDate.parse("2018-01-01"), LocalDate.parse("2019-01-01")), new ResultatBeregning(
            BigDecimal.valueOf(1200), INNVILGET_75_PROSENT, "REGEL 2")));
    periodeResultatListe
        .add(new ResultatPeriode(new Periode(LocalDate.parse("2019-01-01"), LocalDate.parse("2020-01-01")), new ResultatBeregning(
            BigDecimal.valueOf(0), AVSLAG, "REGEL 11")));
    forskuddPeriodeResultat = new BeregnForskuddResultat(periodeResultatListe);
  }

  private void byggAvvik() {
    avvikListe = new ArrayList<>();
    avvikListe.add(new Avvik("beregnDatoTil må være etter beregnDatoFra", AvvikType.DATO_FRA_ETTER_DATO_TIL));
  }
}
