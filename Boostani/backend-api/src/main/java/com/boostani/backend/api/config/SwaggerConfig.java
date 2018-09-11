package com.boostani.backend.api.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * @author Loay
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.boostani.backend.api.web")).build().apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo("Boostani REST API", "This is the Backend REST API for Boostani Merchants Application",
				"1.0", "http://boostini.postaffiliatepro.com", new Contact("Boostani",
						"http://boostini.postaffiliatepro.com/merchants/login.php", "ta.na.mails@gmail.com"),
				"", "", Collections.emptyList());
	}
}
