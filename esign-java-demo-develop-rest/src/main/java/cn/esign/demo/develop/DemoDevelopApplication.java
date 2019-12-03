package cn.esign.demo.develop;

import cn.esign.demo.base.client.EsignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * 项目启动类
 */
@ComponentScan(basePackages = {"cn.esign.demo.develop"})
@ServletComponentScan
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DemoDevelopApplication {
    public DemoDevelopApplication() {
    }

    public static void main(String[] args) {
        (new EsignClient()).build();
        SpringApplication.run(DemoDevelopApplication.class, args);
    }
}
