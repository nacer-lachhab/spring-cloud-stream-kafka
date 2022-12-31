package isga.kafka.test1;

import java.util.function.Consumer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import isga.kafka.test1.entities.PageEvent;

@SpringBootApplication
public class KafkaTest1Application implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(KafkaTest1Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("running with success");
	}
	
//	@Bean
//    public Consumer<PageEvent> consumer(){
//        return (input)->{
//          System.out.println("***********");
////          System.out.println("nom de page: "+input.getName());
////          System.out.println("nom du visiteur: "+input.getUser());
//          System.out.println(input.toString());
//          System.out.println("***********");
//        };
//	}

}
