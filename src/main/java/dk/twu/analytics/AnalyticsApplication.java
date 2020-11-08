package dk.twu.analytics;

import dk.twu.analytics.dto.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@SpringBootApplication
@RestController
public class AnalyticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyticsApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s, let's play with Apache Ignite!", name);
    }


    @GetMapping("/loadCache")
    public String loadCache(@Autowired IgniteService igniteService,
                            @RequestParam(value = "sizeInstrument", defaultValue = "500") String sizeInstrument,
                            @RequestParam(value = "sizeTrade", defaultValue = "10000") String sizeTrade,
                            @RequestParam(value = "sizeBook", defaultValue = "50") String sizeBook,
                            @RequestParam(value = "maxAgeDays", defaultValue = "30") String maxAgeDays) {
        igniteService.loadCache(Integer.parseInt(sizeInstrument), Integer.parseInt(sizeTrade), Integer.parseInt(sizeBook), Integer.parseInt(maxAgeDays));
        return "Cache has been loaded!";
    }

    @GetMapping("/cleanCache")
    public String cleanCache(@Autowired IgniteService igniteService) {
        igniteService.cleanCache();
        return "Cache has been cleaned!";
    }

    @GetMapping("/findTradesById")
    public List<Trade> findTradesById(@Autowired IgniteService igniteService,
                                      @RequestParam(value = "tradeId") String tradeId) {
        return igniteService.findTradesById(Long.parseLong(tradeId));
    }

    @GetMapping("/findTradesByBook")
    public Object findTradesByBook(@Autowired IgniteService igniteService,
                                   @RequestParam(value = "bookId") String bookId) {
        return igniteService.findTradesByBook(Long.parseLong(bookId));
    }

    @GetMapping("/countTradeByBook")
    public Object countTradeByBook(@Autowired IgniteService igniteService) {
        return igniteService.countTradeByBook();
    }
}
