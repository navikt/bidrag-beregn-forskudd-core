package no.nav.bidrag.beregn.forskudd.core.beregning;

import static no.nav.bidrag.beregn.felles.enums.BostatusKode.BOR_MED_FORELDRE;
import static no.nav.bidrag.beregn.felles.enums.SivilstandKode.ENSLIG;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.AVSLAG;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.FORHOYET_FORSKUDD_100_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.FORHOYET_FORSKUDD_11_AAR_125_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.ORDINAERT_FORSKUDD_75_PROSENT;
import static no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode.REDUSERT_FORSKUDD_50_PROSENT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.nav.bidrag.beregn.felles.SjablonUtil;
import no.nav.bidrag.beregn.felles.bo.Periode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriode;
import no.nav.bidrag.beregn.felles.bo.SjablonPeriodeNavnVerdi;
import no.nav.bidrag.beregn.felles.enums.SivilstandKode;
import no.nav.bidrag.beregn.felles.enums.SjablonTallNavn;
import no.nav.bidrag.beregn.forskudd.core.bo.GrunnlagBeregning;
import no.nav.bidrag.beregn.forskudd.core.bo.Inntekt;
import no.nav.bidrag.beregn.forskudd.core.bo.ResultatBeregning;
import no.nav.bidrag.beregn.forskudd.core.enums.ResultatKode;

public class ForskuddBeregningImpl implements ForskuddBeregning {

  private BigDecimal maksInntektForskuddMottakerMultiplikator;
  private BigDecimal inntektsintervallForskuddBelop;
  private BigDecimal forskuddssats75ProsentBelop;
  private BigDecimal forskuddssats100ProsentBelop;
  private BigDecimal inntektsgrense100ProsentForskuddBelop;
  private BigDecimal inntektsgrenseEnslig75ProsentForskuddBelop;
  private BigDecimal inntektsgrenseGiftSamboer75ProsentForskuddBelop;

  public ForskuddBeregningImpl() {
    this.maksInntektForskuddMottakerMultiplikator = BigDecimal.ZERO;
    this.inntektsintervallForskuddBelop = BigDecimal.ZERO;
    this.forskuddssats75ProsentBelop = BigDecimal.ZERO;
    this.forskuddssats100ProsentBelop = BigDecimal.ZERO;
    this.inntektsgrense100ProsentForskuddBelop = BigDecimal.ZERO;
    this.inntektsgrenseEnslig75ProsentForskuddBelop = BigDecimal.ZERO;
    this.inntektsgrenseGiftSamboer75ProsentForskuddBelop = BigDecimal.ZERO;
  }

  @Override
  public ResultatBeregning beregn(GrunnlagBeregning grunnlag) {

    hentSjablonVerdier(grunnlag.getSjablonListe());

    var maksInntektsgrense = forskuddssats100ProsentBelop.multiply(maksInntektForskuddMottakerMultiplikator);
    //Inntektsintervall regnes ut med antall barn utover ett
    var inntektsIntervallTotal = inntektsintervallForskuddBelop.multiply(BigDecimal.valueOf(grunnlag.getBarnIHusstanden().getAntall() - 1));
    if (inntektsIntervallTotal.compareTo(BigDecimal.ZERO) < 0) {
      inntektsIntervallTotal = BigDecimal.ZERO;
    }

    ResultatKode resultatKode;
    String regel;

    // Legger sammen inntektene
    var bidragMottakerInntekt = grunnlag.getInntektListe().stream().map(Inntekt::getBelop)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Søknadsbarn er over 18 år (REGEL 1)
    if (grunnlag.getSoknadBarnAlder().getAlder() >= 18) {
      resultatKode = AVSLAG;
      regel = "REGEL 1";

      // Søknadsbarn bor alene eller ikke med foreldre (REGEL 2/3)
    } else if (!(grunnlag.getSoknadBarnBostatus().getKode().equals(BOR_MED_FORELDRE))) {
      resultatKode =
          (grunnlag.getSoknadBarnAlder().getAlder() >= 11) ? FORHOYET_FORSKUDD_11_AAR_125_PROSENT : FORHOYET_FORSKUDD_100_PROSENT;
      regel = (resultatKode.equals(FORHOYET_FORSKUDD_11_AAR_125_PROSENT) ? "REGEL 2" : "REGEL 3");

      // Over maks inntektsgrense for forskudd (REGEL 4)
    } else if (!(erUnderInntektsGrense(maksInntektsgrense, bidragMottakerInntekt))) {
      resultatKode = AVSLAG;
      regel = "REGEL 4";

      // Under maks inntektsgrense for fullt forskudd (REGEL 5/6)
    } else if (erUnderInntektsGrense(inntektsgrense100ProsentForskuddBelop, bidragMottakerInntekt)) {
      resultatKode = (grunnlag.getSoknadBarnAlder().getAlder() >= 11) ? FORHOYET_FORSKUDD_11_AAR_125_PROSENT : FORHOYET_FORSKUDD_100_PROSENT;
      regel = (resultatKode.equals(FORHOYET_FORSKUDD_11_AAR_125_PROSENT) ? "REGEL 5" : "REGEL 6");

      // Resterende regler (gift/enslig) (REGEL 7/8/9/10/11/12/13/14)
    } else {
      resultatKode = (erUnderInntektsGrense(
          settInntektsgrense(grunnlag.getSivilstand().getKode(), inntektsgrenseEnslig75ProsentForskuddBelop,
              inntektsgrenseGiftSamboer75ProsentForskuddBelop).add(inntektsIntervallTotal), bidragMottakerInntekt)) ? ORDINAERT_FORSKUDD_75_PROSENT
          : REDUSERT_FORSKUDD_50_PROSENT;
      if (grunnlag.getSivilstand().getKode().equals(ENSLIG)) {
        if (grunnlag.getBarnIHusstanden().getAntall() == 1) {
          regel = (resultatKode.equals(ORDINAERT_FORSKUDD_75_PROSENT) ? "REGEL 7" : "REGEL 8");
        } else {
          regel = (resultatKode.equals(ORDINAERT_FORSKUDD_75_PROSENT) ? "REGEL 9" : "REGEL 10");
        }
      } else {
        if (grunnlag.getBarnIHusstanden().getAntall() == 1) {
          regel = (resultatKode.equals(ORDINAERT_FORSKUDD_75_PROSENT) ? "REGEL 11" : "REGEL 12");
        } else {
          regel = (resultatKode.equals(ORDINAERT_FORSKUDD_75_PROSENT) ? "REGEL 13" : "REGEL 14");
        }
      }
    }

    return new ResultatBeregning(beregnForskudd(resultatKode, forskuddssats75ProsentBelop), resultatKode, regel,
        byggSjablonListe(grunnlag.getSjablonListe()));
  }

  // Henter sjablonverdier
  private void hentSjablonVerdier(List<SjablonPeriode> sjablonPeriodeListe) {
    var sjablonListe = sjablonPeriodeListe.stream()
        .map(SjablonPeriode::getSjablon)
        .toList();
    forskuddssats75ProsentBelop = SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP);
    forskuddssats100ProsentBelop = SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.FORSKUDDSSATS_BELOP);
    maksInntektForskuddMottakerMultiplikator =
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR);
    inntektsgrense100ProsentForskuddBelop = SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP);
    inntektsgrenseEnslig75ProsentForskuddBelop =
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP);
    inntektsgrenseGiftSamboer75ProsentForskuddBelop =
        SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP);
    inntektsintervallForskuddBelop = SjablonUtil.hentSjablonverdi(sjablonListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP);
  }

  // Mapper ut sjablonverdier til ResultatBeregning (dette for å sikre at kun sjabloner som faktisk er brukt legges ut i grunnlaget for beregning)
  private List<SjablonPeriodeNavnVerdi> byggSjablonListe(List<SjablonPeriode> sjablonPeriodeListe) {
    var sjablonListe = new ArrayList<SjablonPeriodeNavnVerdi>();
    sjablonListe.add(new SjablonPeriodeNavnVerdi(hentPeriode(sjablonPeriodeListe, SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP.getNavn()),
        SjablonTallNavn.FORSKUDDSSATS_75PROSENT_BELOP.getNavn(), forskuddssats75ProsentBelop));
    sjablonListe.add(new SjablonPeriodeNavnVerdi(hentPeriode(sjablonPeriodeListe, SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn()),
        SjablonTallNavn.FORSKUDDSSATS_BELOP.getNavn(), forskuddssats100ProsentBelop));
    sjablonListe.add(new SjablonPeriodeNavnVerdi(hentPeriode(sjablonPeriodeListe,
        SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.getNavn()),
        SjablonTallNavn.MAKS_INNTEKT_FORSKUDD_MOTTAKER_MULTIPLIKATOR.getNavn(), maksInntektForskuddMottakerMultiplikator));
    sjablonListe.add(new SjablonPeriodeNavnVerdi(hentPeriode(sjablonPeriodeListe, SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.getNavn()),
        SjablonTallNavn.OVRE_INNTEKTSGRENSE_FULLT_FORSKUDD_BELOP.getNavn(), inntektsgrense100ProsentForskuddBelop));
    sjablonListe.add(new SjablonPeriodeNavnVerdi(hentPeriode(sjablonPeriodeListe,
        SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.getNavn()),
        SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_EN_BELOP.getNavn(), inntektsgrenseEnslig75ProsentForskuddBelop));
    sjablonListe.add(new SjablonPeriodeNavnVerdi(hentPeriode(sjablonPeriodeListe,
        SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.getNavn()),
        SjablonTallNavn.OVRE_INNTEKTSGRENSE_75PROSENT_FORSKUDD_GS_BELOP.getNavn(), inntektsgrenseGiftSamboer75ProsentForskuddBelop));
    sjablonListe.add(new SjablonPeriodeNavnVerdi(hentPeriode(sjablonPeriodeListe, SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.getNavn()),
        SjablonTallNavn.INNTEKTSINTERVALL_FORSKUDD_BELOP.getNavn(), inntektsintervallForskuddBelop));
    return sjablonListe;
  }

  private Periode hentPeriode(List<SjablonPeriode> sjablonPeriodeListe, String sjablonNavn) {
    return sjablonPeriodeListe.stream()
        .filter(sjablonPeriode -> sjablonPeriode.getSjablon().getNavn().equals(sjablonNavn))
        .map(SjablonPeriode::getPeriode)
        .findFirst()
        .orElse(new Periode(LocalDate.MIN, LocalDate.MAX));
  }

  // Beregner forskuddsbeløp basert på resultatkode
  // Forskudd 50%  = Sjablon 0038 * 2/3
  // Forskudd 75%  = Sjablon 0038
  // Forskudd 100% = Sjablon 0038 * 4/3
  // Forskudd 125% = Sjablon 0038 * 5/3
  private static BigDecimal beregnForskudd(ResultatKode resultatKode, BigDecimal forskuddssats75ProsentBelop) {
    return switch (resultatKode) {
      case REDUSERT_FORSKUDD_50_PROSENT ->
          forskuddssats75ProsentBelop.multiply(BigDecimal.valueOf(2)).divide(BigDecimal.valueOf(3), -1, RoundingMode.HALF_UP);
      case ORDINAERT_FORSKUDD_75_PROSENT -> forskuddssats75ProsentBelop;
      case FORHOYET_FORSKUDD_100_PROSENT ->
          forskuddssats75ProsentBelop.multiply(BigDecimal.valueOf(4)).divide(BigDecimal.valueOf(3), -1, RoundingMode.HALF_UP);
      case FORHOYET_FORSKUDD_11_AAR_125_PROSENT ->
          forskuddssats75ProsentBelop.multiply(BigDecimal.valueOf(5)).divide(BigDecimal.valueOf(3), -1, RoundingMode.HALF_UP);
      default -> BigDecimal.ZERO;
    };
  }

  private static boolean erUnderInntektsGrense(BigDecimal inntektsgrense, BigDecimal inntekt) {
    return inntekt.compareTo(inntektsgrense) < 1;
  }

  private static BigDecimal settInntektsgrense(SivilstandKode sivilstandKode, BigDecimal inntektsgrenseEnslig75Prosent,
      BigDecimal inntektsgrenseGift75Prosent) {
    return sivilstandKode.equals(ENSLIG) ? inntektsgrenseEnslig75Prosent : inntektsgrenseGift75Prosent;
  }
}
