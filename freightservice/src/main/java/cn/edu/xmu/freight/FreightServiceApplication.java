package cn.edu.xmu.freight;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad", "cn.edu.xmu.freight"})
@MapperScan("cn.edu.xmu.freight.mapper")
public class FreightServiceApplication implements ApplicationRunner {

    private  static  final Logger logger = LoggerFactory.getLogger(FreightServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FreightServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
