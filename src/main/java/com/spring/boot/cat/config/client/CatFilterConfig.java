package com.spring.boot.cat.config.client;

import com.dianping.cat.servlet.CatFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

/**
 * 可以定义在通用的starter 或者 公共包中
 * cat-filter 可以拦截请求URL并上报到CAT控制台Transaction
 * cat-servlet-filter 用于springcloud openFeign跨服务调用链路监控
 * @author fenglijian
 * @date 2022-10-25 14:49
 */
@Configuration
@ConditionalOnProperty(prefix = "cat", name = "switch.client", havingValue = "true")
public class CatFilterConfig {

    @Value("${cat.switch.chain}")
    private boolean chain;

    @Bean
    public FilterRegistrationBean catFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        if (chain) {
            CatContextServletFilter filter = new CatContextServletFilter();
            registration.setFilter(filter);
            registration.addUrlPatterns("/*");
            registration.setName("cat-servlet-filter");
            registration.setOrder(1);
            return registration;
        } else {
            CatFilter catFilter = new CatFilter();
            registration.setFilter(catFilter);
            registration.addUrlPatterns("/*");
            registration.setName("cat-filter");
            registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
            registration.setOrder(1);
            return registration;
        }
    }

    /**
     * 集成熔断器配置bean，配合 FeignHystrixConcurrencyStrategy
     * @return
     */
    /*@Bean
    @ConditionalOnBean(HystrixFeign.class)
    @ConditionalOnProperty(name = "feign.hystrix.enabled", havingValue = "true", matchIfMissing = false)
    public FeignHystrixConcurrencyStrategy feignHystrixConcurrencyStrategy() {
        return new FeignHystrixConcurrencyStrategy();
    }*/
}
