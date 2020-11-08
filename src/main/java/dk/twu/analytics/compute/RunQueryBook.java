package dk.twu.analytics.compute;

import dk.twu.analytics.config.IgniteCacheConfig;
import dk.twu.analytics.dto.Trade;
import dk.twu.analytics.dto.TradeKey;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.lang.IgniteRunnable;

import javax.cache.Cache;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RunQueryBook implements IgniteRunnable {
    private final Ignite ignite;
    private final Long bookId;

    public RunQueryBook(Ignite ignite, Long bookId) {
        this.ignite = ignite;
        this.bookId = bookId;
    }

    @Override
    public void run() {
        IgniteCache<TradeKey, Trade> tradeCache = IgniteCacheConfig.initTradeCache(ignite);
        QueryCursor<Cache.Entry<TradeKey, Trade>> queryCursor = tradeCache.query(new ScanQuery<>((k, p) -> k.getBookId() == bookId));
        for (Cache.Entry<TradeKey, Trade> entry : queryCursor) {
            System.out.println(entry.getValue());
        }
        System.out.println(String.format("Cache size: [%d,%d]", tradeCache.size(), tradeCache.localSize()));
        Iterable<Cache.Entry<TradeKey, Trade>> entries = tradeCache.localEntries();
        Map<Long, Long> countBooks = StreamSupport.stream(entries.spliterator(), false)
                .map(i -> i.getKey().getBookId())
                .collect(Collectors.groupingBy(k -> k, Collectors.counting()));
        System.out.println("Count local books: ");
        System.out.println(countBooks);

    }
}
