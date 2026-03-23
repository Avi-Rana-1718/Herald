package com.notification.herald;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan("com.notification.herald.configurations")
@SpringBootApplication
public class HeraldApplication {

	public static void main(String[] args) {
//        ElasticApmAttacher.attach();
    SpringApplication.run(HeraldApplication.class, args);
	}

}
