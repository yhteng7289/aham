package sg.com.aham;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SquirrelSaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(SquirrelSaveApplication.class, args);
    }

}
