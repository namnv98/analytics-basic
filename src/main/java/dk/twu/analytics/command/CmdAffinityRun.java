package dk.twu.analytics.command;

import dk.twu.analytics.compute.RunQueryBook;
import dk.twu.analytics.config.IgniteCacheConfig;
import dk.twu.analytics.config.IgniteConfigHelper;
import org.apache.ignite.*;
import org.apache.ignite.configuration.IgniteConfiguration;

public class CmdAffinityRun {
    public static void main(String[] args) throws IgniteException {
        Ignition.setClientMode(true);
        IgniteConfiguration config = IgniteConfigHelper.getDefaultConfig(IgniteConfigHelper.getLocalIpFinder());
        try (Ignite ignite = Ignition.start(config)) {
            IgniteCluster cluster = ignite.cluster();
            IgniteCompute compute = ignite.compute(cluster.forRemotes());
            compute.affinityRun(IgniteCacheConfig.CACHE_TRADE, 1, new RunQueryBook(ignite, 1L));
        }
    }
}
