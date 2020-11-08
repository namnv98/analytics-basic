package dk.twu.analytics;

import dk.twu.analytics.command.CmdCleanCache;
import dk.twu.analytics.command.CmdLoadCache;
import dk.twu.analytics.command.CmdQueryCache;
import dk.twu.analytics.config.IgniteCacheConfig;
import dk.twu.analytics.config.IgniteConfigHelper;
import dk.twu.analytics.dto.Trade;
import io.vavr.Tuple2;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IgniteService {
    private static Ignite instance;
    private final CmdLoadCache cmdLoadCache = new CmdLoadCache();

    public IgniteService() {
        if (instance == null) {
            String discoveryMode = System.getenv("IGNITE_DISCOVERY_MODE");
            TcpDiscoveryIpFinder ipFinder = "KUBE".equals(discoveryMode) ?
                    IgniteConfigHelper.getKubernetesIpFinder() :
                    IgniteConfigHelper.getLocalIpFinder();
            instance = Ignition.start(IgniteConfigHelper.getDefaultConfig(ipFinder));
            IgniteCacheConfig.initTradeCache(instance);
            IgniteCacheConfig.initInstrumentPrice(instance);
        }
    }

    public void loadCache(int sizeInstrument, int sizeTrade, int sizeBook, int maxAgeDays) {
        cmdLoadCache.execute(instance, sizeInstrument, sizeTrade, sizeBook, maxAgeDays);
    }

    public void cleanCache() {
        new CmdCleanCache().execute(instance);
    }

    public List<Tuple2<Long, Long>> countTradeByBook() {
        return new CmdQueryCache().countTradesByBook(instance);
    }

    public List<Trade> findTradesById(Long tradeId) {
        return new CmdQueryCache().findTradeById(instance, tradeId);
    }

    public List<Trade> findTradesByBook(Long bookId) {
        return new CmdQueryCache().findTradeByBookId(instance, bookId);
    }
}
