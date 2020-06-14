package huuduc.nhd.convertcurrency.Entity;

public class Currency {
   private String countryName;
   private Double currency;

    public Currency(String countryName, Double currency) {
        this.countryName = countryName;
        this.currency = currency;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Double getCurrency() {
        return currency;
    }

    public void setCurrency(Double currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "countryName='" + countryName + '\'' +
                ", currency=" + currency +
                '}';
    }
}
