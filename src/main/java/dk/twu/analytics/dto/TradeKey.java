package dk.twu.analytics.dto;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.util.Objects;

public class TradeKey {
    @QuerySqlField(index = true)
    private Long tradeId;
    @QuerySqlField(index = true)
    private final Long instrumentId;
    @AffinityKeyMapped
    @QuerySqlField(index = true)
    private final Long bookId;

    public TradeKey(Long tradeId, Long instrumentId, Long bookId) {
        this.tradeId = tradeId;
        this.instrumentId = instrumentId;
        this.bookId = bookId;
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public Long getBookId() {
        return bookId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeKey tradeKey = (TradeKey) o;
        return Objects.equals(tradeId, tradeKey.tradeId) &&
                Objects.equals(instrumentId, tradeKey.instrumentId) &&
                Objects.equals(bookId, tradeKey.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeId, instrumentId, bookId);
    }

    @Override
    public String toString() {
        return "TradeKey{" +
                "tradeId=" + tradeId +
                ", instrumentId=" + instrumentId +
                ", bookId=" + bookId +
                '}';
    }
}
