package com.pivot.aham.api.web.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.PathProvider;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.service.Parameter;

import java.util.ArrayList;
import java.util.List;


/**
 * 注册swagger
 *
 * @author addison
 * @since 2018年12月01日
 */
@Configuration
@EnableWebMvc
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket platformApi() {
		//添加head参数start
		List<Parameter> pars = new ArrayList<>();
		ParameterBuilder tokenPar = new ParameterBuilder();
		tokenPar.name("token").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
		ParameterBuilder signPar = new ParameterBuilder();
		signPar.name("sign").description("签名").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
		ParameterBuilder timePar = new ParameterBuilder();
		timePar.name("timestamp").description("时间戳").modelRef(new ModelRef("string")).parameterType("header").required(false).build();

		pars.add(tokenPar.build());
		pars.add(signPar.build());
		pars.add(timePar.build());
		//添加head参数end
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.regex("/api/v1/.*"))
				.build()
				.host("aham-api-web-qa-02.pintec.com")
				.apiInfo(apiInfo())
				.forCodeGeneration(true)
				.globalOperationParameters(pars);
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Aham-API").description("©2020 Copyright. Powered By SquirrelSave.")
				// .termsOfServiceUrl("")
				.contact(new Contact("Aham", "", "wooitatt.khor@ezyit.asia")).version("2.0").build();
	}

}