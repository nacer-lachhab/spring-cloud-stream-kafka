package com.nacer.springCloudStreamKafka.web;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.nacer.springCloudStreamKafka.entities.PageEvent;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class PageEventRestController {

    @Autowired
    private StreamBridge streamBridge;

    @Autowired //objet dont on va nous servire pour interoger le store "page-count"
    private InteractiveQueryService interactiveQueryService;

    @GetMapping("/publish/{topic}/{name}")//name:nomDePage
    public PageEvent publish(@PathVariable String topic, @PathVariable String name){
        PageEvent pageEvent = new PageEvent(
                                            name,
                                            Math.random()>0.5?"u1":"u2",
                                            new Date(),
                                            new Random().nextInt(500));
        streamBridge.send(topic,pageEvent);
        return  pageEvent;
    }

    //push each 1sec a Map<nameOfPage,numberOfVisits> to the client -subscriber-
    @GetMapping(path = "/analytics",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String,Long>> analytics(){
        return Flux.interval(Duration.ofSeconds(1))//generer flux chaque seconde
                   .map(sequence->{
                       Map<String,Long> stringLongMap=new HashMap<>();
                       ReadOnlyWindowStore<String, Long> windowStore =
                               interactiveQueryService
                                    .getQueryableStore("page-count",
                                                                QueryableStoreTypes.windowStore());
                       Instant now = Instant.now();
                       Instant from = now.minusMillis(5000);
                       KeyValueIterator<Windowed<String>, Long> fetch5secondes =
                               windowStore.fetchAll(from, now);
                       while (fetch5secondes.hasNext()){
                           KeyValue<Windowed<String>,Long> next = fetch5secondes.next();
                           stringLongMap.put(next.key.key(),next.value);
                           //key in the name of page (p1/or/p2)
                       }
                       return stringLongMap;
                   }).share();
        //share: partagé par plusieurs clients -subscriber in SSE- reception du mm flux par tous
    }
}
