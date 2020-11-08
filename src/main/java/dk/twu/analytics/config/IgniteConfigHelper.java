package dk.twu.analytics.config;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.Arrays;

public class IgniteConfigHelper {
    public static TcpDiscoveryIpFinder getLocalIpFinder() {
        return new TcpDiscoveryMulticastIpFinder().setAddresses(Arrays.asList("127.0.0.1:47500..47509"));
    }

    public static TcpDiscoveryIpFinder getKubernetesIpFinder() {
        return new TcpDiscoveryKubernetesIpFinder();
    }

    public static IgniteConfiguration getDefaultConfig(TcpDiscoveryIpFinder ipFinder) {
        return new IgniteConfiguration()
                .setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));
    }
}
