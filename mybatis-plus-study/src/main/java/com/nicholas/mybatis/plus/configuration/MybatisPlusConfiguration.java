package com.nicholas.mybatis.plus.configuration;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

//@Configuration
public class MybatisPlusConfiguration {

   /* @Bean
    @ConfigurationProperties
    public DataSource hikariDataSource() {
        return DataSourceBuilder.create().build();
    }*/

    /*@Bean
    public MybatisPlusProperties mybatisPlusProperties() {
        return new MybatisPlusProperties();
    }*/

   /* @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean() {
        return new SqlSessionFactoryBean();
    }*/
}
