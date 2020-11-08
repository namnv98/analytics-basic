package dk.twu.analytics.dto;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.time.LocalDateTime;

public class Trade {
    private final TradeKey tradeKey;
    @QuerySqlField(index = true)
    private final LocalDateTime tradeDateTime;
    @QuerySqlField(index = false)
    private final Long notional;

    public Trade(TradeKey tradeKey, LocalDateTime tradeDateTime, Long notional) {
        this.tradeKey = tradeKey;
        this.tradeDateTime = tradeDateTime;
        this.notional = notional;
    }

    public TradeKey getTradeKey() {
        return tradeKey;
    }

    public LocalDateTime getTradeDateTime() {
        return tradeDateTime;
    }

    public Long getNotional() {
        return notional;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "tradeKey=" + tradeKey +
                ", tradeDateTime=" + tradeDateTime +
                ", notional=" + notional +
                '}';
    }
}
