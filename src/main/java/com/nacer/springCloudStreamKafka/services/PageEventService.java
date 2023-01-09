package com.nacer.springCloudStreamKafka.services;

import com.nacer.springCloudStreamKafka.entities.PageEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class PageEventService {
    @Bean
    public Consumer<PageEvent> consumer(){
        return (input)->{
          System.out.println("***********");
//          System.out.println("nom de page: "+input.getName());
//          System.out.println("nom du visiteur: "+input.getUser());
          System.out.println(input.toString());
          System.out.println("***********");
        };
    }

    @Bean //par defaut supplier va s executer chaque 1s
    public Supplier<PageEvent> pageEventSupplier(){
        return ()->new PageEvent(
                Math.random()>0.5?"p1":"p2",
                Math.random()>0.5?"u1":"u2",
                new Date(),
                new Random().nextInt(500));
    }

    //Function<input,output>
    @Bean
    public Function<PageEvent,PageEvent> pageEventFunction(){
        return (input)->{
            input.setName("catched & edited");
            input.setUser("PageEvent-Function");
            return input;
        };
    }

    //l'objectif est de faire du data analitics -statistiques-
    @Bean
    public Function<KStream<String,PageEvent>,KStream<String,Long>> kStreamFunction(){
        return (input)->{
          return input.filter((k,v)->v.getDuration()>100)
                  .map((k,v)->new KeyValue<>(v.getName(),0l))
                  //pour serialiser la cle sous form string
                  .groupBy((k,v)->k,Grouped.with(Serdes.String(),Serdes.Long()))
                  //Serdes = serializer/deserializer
                  //enregistrement des dernieres 5s, fenetre temporelle
                  .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMillis(5000)))
                  .count(Materialized.as("page-count"))
                  //count : fait le compte de combien de fois la page k(p1/p2) est visité
                  //"page-count": store de type windowStore
                  .toStream()
                  //convertir kTable to KStream
                  //value = count, on fait convertion de key to string
                  .map((k,v)->
                          new KeyValue<>("=>"
                                          +k.window().startTime()
                                          +k.window().endTime()
                                          +k.key()
                                        ,v));
        };
    }
}
