package cn.edu.xmu.freight;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDubbo(scanBasePackages = "cn.edu.xmu.freight.service")
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad", "cn.edu.xmu.freight"})
@MapperScan("cn.edu.xmu.freight.mapper")
@EnableDiscoveryClient
public class FreightServiceApplication implements ApplicationRunner {

    private  static  final Logger logger = LoggerFactory.getLogger(FreightServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FreightServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
