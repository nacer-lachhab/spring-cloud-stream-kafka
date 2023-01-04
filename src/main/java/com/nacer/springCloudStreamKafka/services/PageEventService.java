package com.nacer.springCloudStreamKafka.services;

import com.nacer.springCloudStreamKafka.entities.PageEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
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
}
