package dk.twu.analytics.config;

import dk.twu.analytics.dto.InstrumentPrice;
import dk.twu.analytics.dto.Trade;
import dk.twu.analytics.dto.TradeKey;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;

public class IgniteCacheConfig {
    public static final String SQL_SCHEMA = "PUBLIC";
    public static final String CACHE_TRADE = "Trade";
    public static final String CACHE_INSTRUMENT_PRICE = "InstrumentPrice";

    public static IgniteCache<TradeKey, Trade> initTradeCache(Ignite ignite) {
        CacheConfiguration<TradeKey, Trade> cacheConfig = new CacheConfiguration<TradeKey, Trade>()
                .setSqlSchema(SQL_SCHEMA)
                .setName(CACHE_TRADE)
                .setIndexedTypes(TradeKey.class, Trade.class)
                .setCacheMode(CacheMode.PARTITIONED)
                .setBackups(1);
        return ignite.getOrCreateCache(cacheConfig);
    }

    public static IgniteCache<Long, InstrumentPrice> initInstrumentPrice(Ignite ignite) {
        CacheConfiguration<Long, InstrumentPrice> cacheConfig = new CacheConfiguration<Long, InstrumentPrice>()
                .setSqlSchema(SQL_SCHEMA)
                .setName(CACHE_INSTRUMENT_PRICE)
                .setIndexedTypes(Long.class, InstrumentPrice.class)
                .setCacheMode(CacheMode.REPLICATED);
        return ignite.getOrCreateCache(cacheConfig);
    }
}
