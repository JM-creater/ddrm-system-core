package ph.gov.tabunok.drrm;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DrrmSystemServerApplication {

	public static void main(String[] args) {
		// Report dates ("reports today", alert timestamps) follow Philippine time.
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Manila"));
		SpringApplication.run(DrrmSystemServerApplication.class, args);
	}

}
