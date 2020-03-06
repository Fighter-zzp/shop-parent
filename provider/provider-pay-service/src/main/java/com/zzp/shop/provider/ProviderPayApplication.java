package com.zzp.shop.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzp.shop.common.utils.IDWorker;
import com.zzp.shop.provider.bind.PaySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 见名知义
 * <p>
 *  人皆知有用之用，而莫知无用之用也
 * </p>
 * @version v1.0.0
 * @author 佐斯特勒
 * @date 2020/3/5 12:04
 * @see  ProviderPayApplication
 **/
@SpringBootApplication
@MapperScan(basePackages = "com.zzp.shop.provider.mapper")
@EnableBinding(value = {PaySource.class})
public class ProviderPayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderPayApplication.class, args);
    }

    @Bean
    public IDWorker idWorker(){
        return new IDWorker(1, 2);
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    /**
     * 注入线程任务池
     * @return {@link ThreadPoolTaskExecutor}
     */
    @Bean
    public ThreadPoolTaskExecutor getThreadPool() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("Pool-A");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
