package pl.zsl.kantor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class CurrencyRatesTable {
    private String table;
    private String no;
    private String effectiveDate;
    @JsonIgnore
    private String trandingDate;
    private List<CurrencyRate> rates;

    public String getTable() {
        return table;
    }

    public String getNo() {
        return no;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public List<CurrencyRate> getRates() {
        return rates;
    }
}
