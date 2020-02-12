package no.nav.bidrag.beregn.forskudd.beregning.grunnlag;

import java.math.BigDecimal;

public class ForskuddBeregningGrunnlag {

  public ForskuddBeregningGrunnlag() {
  }

  private BigDecimal bidragMottakerInntekt;
  private SivilstandKode bidragMottakerSivilstandKode;
  private Integer antallBarnIHusstand;
  private Integer soknadBarnAlder;
  private BostedStatusKode soknadBarnBostedStatusKode;

  public ForskuddBeregningGrunnlag(BigDecimal bidragMottakerInntekt, SivilstandKode bidragMottakerSivilstandKode, Integer antallBarnIHusstand,
      Integer soknadBarnAlder, BostedStatusKode soknadBarnBostedStatusKode) {
    this.bidragMottakerInntekt = bidragMottakerInntekt;
    this.bidragMottakerSivilstandKode = bidragMottakerSivilstandKode;
    this.antallBarnIHusstand = antallBarnIHusstand;
    this.soknadBarnAlder = soknadBarnAlder;
    this.soknadBarnBostedStatusKode = soknadBarnBostedStatusKode;
  }

  public BigDecimal getBidragMottakerInntekt() {
    return bidragMottakerInntekt;
  }

  public void setBidragMottakerInntekt(BigDecimal bidragMottakerInntekt) {
    this.bidragMottakerInntekt = bidragMottakerInntekt;
  }

  public SivilstandKode getBidragMottakerSivilstandKode() {
    return bidragMottakerSivilstandKode;
  }

  public void setBidragMottakerSivilstandKode(SivilstandKode bidragMottakerSivilstandKode) {
    this.bidragMottakerSivilstandKode = bidragMottakerSivilstandKode;
  }

  public Integer getAntallBarnIHusstand() {
    return antallBarnIHusstand;
  }

  public void setAntallBarnIHusstand(Integer antallBarnIHusstand) {
    this.antallBarnIHusstand = antallBarnIHusstand;
  }

  public Integer getSoknadBarnAlder() {
    return soknadBarnAlder;
  }

  public void setSoknadBarnAlder(Integer soknadBarnAlder) {
    this.soknadBarnAlder = soknadBarnAlder;
  }

  public BostedStatusKode getSoknadBarnBostedStatusKode() {
    return soknadBarnBostedStatusKode;
  }

  public void setSoknadBarnBostedStatusKode(BostedStatusKode soknadBarnBostedStatusKode) {
    this.soknadBarnBostedStatusKode = soknadBarnBostedStatusKode;
  }
}
