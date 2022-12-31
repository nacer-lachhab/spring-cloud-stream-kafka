package isga.kafka.test1.services;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import isga.kafka.test1.entities.PageEvent;

import java.util.function.Consumer;

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
}
