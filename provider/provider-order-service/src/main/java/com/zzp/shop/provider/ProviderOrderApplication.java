package com.zzp.shop.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzp.shop.common.utils.IDWorker;
import com.zzp.shop.provider.bind.OrderSource;
import com.zzp.shop.provider.sink.OrderSink;
import com.zzp.shop.provider.sink.PaySink;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 见名知义
 * <p>
 * 人皆知有用之用，而莫知无用之用也
 * </p>
 *
 * @author 佐斯特勒
 * @version v1.0.0
 * @date 2020/3/2 18:11
 * @see ProviderOrderApplication
 **/
@SpringBootApplication
@MapperScan(basePackages = "com.zzp.shop.provider.mapper")
@EnableBinding({OrderSource.class, OrderSink.class, PaySink.class})
public class ProviderOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderOrderApplication.class, args);
    }

    /**
     * id生成器注入
     *
     * @return .
     */
    @Bean
    public IDWorker getIdWorker() {
        return new IDWorker(1, 1);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
