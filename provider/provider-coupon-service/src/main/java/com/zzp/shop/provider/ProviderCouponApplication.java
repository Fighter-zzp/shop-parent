package com.zzp.shop.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzp.shop.provider.sink.CouponSink;
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
 * @date 2020/3/2 22:32
 * @see  ProviderCouponApplication
 **/
@SpringBootApplication
@MapperScan(basePackages = "com.zzp.shop.provider.mapper")
@EnableBinding({CouponSink.class})
public class ProviderCouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderCouponApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
