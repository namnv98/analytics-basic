package dk.twu.analytics.command;

import dk.twu.analytics.config.IgniteCacheConfig;
import dk.twu.analytics.config.IgniteConfigHelper;
import dk.twu.analytics.dto.Trade;
import dk.twu.analytics.dto.TradeKey;
import io.vavr.Tuple2;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.IgniteConfiguration;

import javax.cache.Cache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CmdQueryCache {
    public static void main(String[] args) throws IgniteException {
        Ignition.setClientMode(true);
        IgniteConfiguration config = IgniteConfigHelper.getDefaultConfig(IgniteConfigHelper.getLocalIpFinder());
        try (Ignite ignite = Ignition.start(config)) {
            List<Trade> tradeById = new CmdQueryCache().findTradeById(ignite, 1L);
            System.out.println(tradeById);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Trade> findTradeById(Ignite ignite, Long tradeId) {
        IgniteCache<TradeKey, Trade> tradeCache = IgniteCacheConfig.initTradeCache(ignite);
        QueryCursor<Cache.Entry<TradeKey, Trade>> cursor = tradeCache.query(new ScanQuery<>((k, p) -> k.getTradeId().equals(tradeId)));
        List<Cache.Entry<TradeKey, Trade>> all = cursor.getAll();
        List<Trade> res = new ArrayList<>();
        for (Cache.Entry<TradeKey, Trade> e : all) {
            res.add(e.getValue());
        }
        return res;
    }

    public List<Trade> findTradeByBookId(Ignite ignite, Long bookId) {
        IgniteCache<TradeKey, Trade> tradeCache = IgniteCacheConfig.initTradeCache(ignite);
        QueryCursor<Cache.Entry<TradeKey, Trade>> cursor = tradeCache.query(new ScanQuery<>((k, p) -> p.getTradeKey().getBookId() == bookId));
        return cursor.getAll().stream().map(e -> e.getValue()).collect(Collectors.toList());
    }

    public List<Tuple2<Long, Long>> countTradesByBook(Ignite ignite) {
        IgniteCache<TradeKey, Trade> tradeCache = IgniteCacheConfig.initTradeCache(ignite);
        String sql = "select BOOKID, count(*) as COUNT, 'STRING' as STR from TRADE group by BOOKID";
        FieldsQueryCursor<List<?>> queryRes = tradeCache.query(new SqlFieldsQuery(sql).setDistributedJoins(true));
        List<Tuple2<Long, Long>> result = extract2(queryRes, "BOOKID", Long.class, "COUNT", Long.class);
        return result;
    }

    private static <T, K> List<Tuple2<T, K>> extract2(FieldsQueryCursor<List<?>> queryRes, String columnName1, Class<T> class1, String columnName2, Class<K> class2) {
        List<List<?>> data = queryRes.getAll();
        Map<String, Integer> fieldsMap = getFieldsMap(queryRes);
        int idx1 = fieldsMap.get(columnName1);
        int idx2 = fieldsMap.get(columnName2);
        List<Tuple2<T, K>> result = new ArrayList<>();
        for (List<?> next : data) {
            T fBookId = (T) next.get(idx1);
            K fCount = (K) next.get(idx2);
            result.add(new Tuple2<>(fBookId, fCount));
        }
        return result;
    }

    private static Map<String, Integer> getFieldsMap(FieldsQueryCursor<List<?>> queryRes) {
        Map<String, Integer> fieldsMap = new HashMap<>();
        int columnsCount = queryRes.getColumnsCount();
        for (int i = 0; i < columnsCount; i++) {
            String fieldName = queryRes.getFieldName(i);
            fieldsMap.put(fieldName, i);
        }
        return fieldsMap;
    }
}
