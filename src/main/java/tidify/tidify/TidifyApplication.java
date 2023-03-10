package tidify.tidify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class TidifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TidifyApplication.class, args);
	}

}
