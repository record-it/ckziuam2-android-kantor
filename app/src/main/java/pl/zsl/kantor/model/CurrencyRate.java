package pl.zsl.kantor.model;

public class CurrencyRate {
    private String currency;
    private String code;
    private double mid;

    public CurrencyRate(String currency, String code, double mid) {
        this.currency = currency;
        this.code = code;
        this.mid = mid;
    }

    public CurrencyRate() {
    }

    public String getCurrency() {
        return currency;
    }

    public String getCode() {
        return code;
    }

    public double getMid() {
        return mid;
    }

    @Override
    public String toString() {
        return code;
    }
}
