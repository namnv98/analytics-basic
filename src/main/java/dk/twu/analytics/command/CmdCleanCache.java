package dk.twu.analytics.command;

import dk.twu.analytics.config.IgniteCacheConfig;
import dk.twu.analytics.config.IgniteConfigHelper;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;

public class CmdCleanCache {
    public static void main(String[] args) throws IgniteException {
        Ignition.setClientMode(true);
        IgniteConfiguration config = IgniteConfigHelper.getDefaultConfig(IgniteConfigHelper.getLocalIpFinder());
        try (Ignite ignite = Ignition.start(config)) {
            new CmdCleanCache().execute(ignite);
        }
    }

    public void execute(Ignite ignite) {
        ignite.getOrCreateCache(IgniteCacheConfig.CACHE_TRADE).clear();
        ignite.getOrCreateCache(IgniteCacheConfig.CACHE_INSTRUMENT_PRICE).clear();
    }
}
