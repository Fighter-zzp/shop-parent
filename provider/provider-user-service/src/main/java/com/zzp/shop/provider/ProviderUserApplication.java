package com.zzp.shop.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzp.shop.provider.sink.UserSink;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 见名知义
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/2 17:54
 * @see  ProviderUserApplication
 **/
@SpringBootApplication
@MapperScan(basePackages = "com.zzp.shop.provider.mapper")
@EnableBinding({UserSink.class})
public class ProviderUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderUserApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
