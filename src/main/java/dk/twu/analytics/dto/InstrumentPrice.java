package dk.twu.analytics.dto;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class InstrumentPrice {
    @QuerySqlField(index = true)
    private Long instrumentId;
    @QuerySqlField(index = false)
    private final Double price;

    public InstrumentPrice(Long instrumentId, Double price) {
        this.instrumentId = instrumentId;
        this.price = price;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "InstrumentPrice{" +
                "instrumentId=" + instrumentId +
                ", price=" + price +
                '}';
    }
}
