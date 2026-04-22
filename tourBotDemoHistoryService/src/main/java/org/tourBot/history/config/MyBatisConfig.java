package org.tourBot.history.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "org.tourBot.history.mapper")
public class MyBatisConfig {
}
