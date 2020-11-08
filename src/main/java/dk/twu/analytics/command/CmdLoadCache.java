package dk.twu.analytics.command;

import dk.twu.analytics.config.IgniteCacheConfig;
import dk.twu.analytics.config.IgniteConfigHelper;
import dk.twu.analytics.dto.InstrumentPrice;
import dk.twu.analytics.dto.Trade;
import dk.twu.analytics.dto.TradeKey;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CmdLoadCache {
    private static AtomicLong tradeIdSeed = new AtomicLong(1);
    private static AtomicLong instrumentIdSeed = new AtomicLong(1);

    private static Supplier<Long> tradeIdSupplier = () -> tradeIdSeed.getAndIncrement();
    private static Supplier<Long> instrumentIdSupplier = () -> instrumentIdSeed.getAndIncrement();

    public static void main(String[] args) throws IgniteException {
        int sizeInstrument = 2;
        int sizeTrade = 10000;
        int sizeBook = 2;
        int maxAgeDays = 14;

        Ignition.setClientMode(true);
        IgniteConfiguration config = IgniteConfigHelper.getDefaultConfig(IgniteConfigHelper.getLocalIpFinder());
        try (Ignite ignite = Ignition.start(config)) {
            new CmdLoadCache().execute(ignite, sizeInstrument, sizeTrade, sizeBook, maxAgeDays);
        }
    }

    public void execute(Ignite ignite, int sizeInstrument, int sizeTrade, int sizeBook, int maxAgeDays) {
        Map<Long, InstrumentPrice> priceMap = generateInstrumentPrice(sizeInstrument);
        Map<TradeKey, Trade> tradeMap = generateTrades(sizeTrade, sizeInstrument, sizeBook, maxAgeDays);
        saveToCache(priceMap, tradeMap, ignite);
    }

    private void saveToCache(Map<Long, InstrumentPrice> priceMap, Map<TradeKey, Trade> tradeMap, Ignite ignite) {
        IgniteCache<TradeKey, Trade> tradeCache = ignite.getOrCreateCache(IgniteCacheConfig.CACHE_TRADE);
        IgniteCache<Long, InstrumentPrice> instrumentPriceCache = ignite.getOrCreateCache(IgniteCacheConfig.CACHE_INSTRUMENT_PRICE);
        System.out.println("Start sending to cache...");
        long startMills = System.currentTimeMillis();
        tradeCache.putAll(tradeMap);
        instrumentPriceCache.putAll(priceMap);
        long finishMills = System.currentTimeMillis();
        System.out.println(String.format("Put trades to cache spent %d ms", finishMills - startMills));
    }

    private Map<TradeKey, Trade> generateTrades(int sizeTrade, int sizeInstrument, int sizeBook, int maxAgeDays) {
        RandomDataGenerator dataGenerator = new RandomDataGenerator();
        LocalDateTime anchor = LocalDateTime.now();
        EasyRandomParameters easyRandomParameters = new EasyRandomParameters()
                .seed(1)
                .charset(StandardCharsets.UTF_8)
                .stringLengthRange(6, 6)
                .randomize(f -> "tradeId".equals(f.getName()), () -> tradeIdSupplier.get())
                .randomize(f -> "instrumentId".equals(f.getName()), () -> dataGenerator.nextLong(1, sizeInstrument))
                .randomize(f -> "bookId".equals(f.getName()), () -> dataGenerator.nextLong(1, sizeBook))
                .randomize(f -> "notional".equals(f.getName()), () -> dataGenerator.nextLong(10000, 5000000))
                .randomize(f -> "tradeDateTime".equals(f.getName()), () -> anchor.minusSeconds(dataGenerator.nextLong(0, 60 * 60 * 24 * maxAgeDays)))
                .ignoreRandomizationErrors(true);
        return new EasyRandom(easyRandomParameters)
                .objects(Trade.class, sizeTrade)
                .collect(Collectors.toMap(Trade::getTradeKey, t -> t));
    }

    private Map<Long, InstrumentPrice> generateInstrumentPrice(int sizeInstrument) {
        RandomDataGenerator dataGenerator = new RandomDataGenerator();
        EasyRandomParameters easyRandomParameters = new EasyRandomParameters()
                .seed(1)
                .randomize(f -> "price".equals(f.getName()), () -> dataGenerator.nextGaussian(1, 0.05d))
                .randomize(f -> "instrumentId".equals(f.getName()), () -> instrumentIdSupplier.get());
        return new EasyRandom(easyRandomParameters)
                .objects(InstrumentPrice.class, sizeInstrument)
                .collect(Collectors.toMap(InstrumentPrice::getInstrumentId, p -> p));
    }
}
