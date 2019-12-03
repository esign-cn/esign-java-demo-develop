package cn.esign.demo.develop.config;

import com.google.common.base.Predicate;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration extends WebMvcConfigurerAdapter {
    private final String ROOT = "/demo-develop";
    private final String SWAGGER_SCAN_BASE_PACKAGE = "cn.esign.demo.develop";

    public SwaggerConfiguration() {
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(new String[]{ROOT+"/**"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/"});
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController(ROOT+"/v2/api-docs", "/v2/api-docs");
        registry.addRedirectViewController(ROOT+"/configuration/ui", "/configuration/ui");
        registry.addRedirectViewController(ROOT+"/configuration/security", "/configuration/security");
        registry.addRedirectViewController(ROOT+"/swagger-resources", "/swagger-resources");
        registry.addRedirectViewController(ROOT, ROOT+"/doc.html");
        registry.addRedirectViewController(ROOT+"/", ROOT+"/doc.html");
        registry.addRedirectViewController("", ROOT+"/doc.html");
        registry.addRedirectViewController("/", ROOT+"/doc.html");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public Docket createRestApi() {
        Predicate<RequestHandler> predicate = (input) -> {
            return true;
        };
        Docket docket = (new Docket(DocumentationType.SWAGGER_2)).apiInfo(this.apiInfo()).select().apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_BASE_PACKAGE)).apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).apis(predicate).paths(SwaggerPathSelectors.any()).build();
        return docket;
    }

    private ApiInfo apiInfo() {
        return (new ApiInfoBuilder()).title("demo-develop online doc").description("demo-develop online doc").termsOfServiceUrl("https://www.tsign.cn/").contact(new Contact("", "", "xxx@tsign.cn")).version("1.0").build();
    }
}
