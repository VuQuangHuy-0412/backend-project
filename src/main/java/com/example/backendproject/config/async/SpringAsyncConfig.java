package com.example.backendproject.config.async;


import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

@Configuration
@EnableAsync
public class SpringAsyncConfig {

    @Bean(name = "async-thread-pool")
    public ThreadPoolTaskExecutor exportThreadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("ASYNC_THREAD_POOL_");
        executor.setTaskDecorator(new LoggingTaskDecorator());

        return executor;
    }

    private final class LoggingTaskDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            Map<String, String> webThreadContext = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    MDC.setContextMap(webThreadContext);
                    runnable.run();
                } catch (Exception ignored) {
                } finally {
                    MDC.clear();
                }
            };
        }

    }
}
