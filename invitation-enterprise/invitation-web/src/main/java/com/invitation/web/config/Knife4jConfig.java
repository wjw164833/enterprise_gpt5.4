package com.invitation.web.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Knife4j API文档配置
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class Knife4jConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.invitation.web.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("多端邀请函系统 - 企业级版本 API文档")
                .description("多端邀请函系统企业级版本后端接口文档，包含认证、邀请函、嘉宾、模板、分享、支付、AI等模块")
                .contact(new Contact("开发团队", "https://invitation.example.com", "dev@invitation.example.com"))
                .version("1.0.0")
                .build();
    }
    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider
                        || bean instanceof springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
        Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
        if (field == null) {
            return java.util.Collections.emptyList();
        }
        ReflectionUtils.makeAccessible(field);
        return (List<RequestMappingInfoHandlerMapping>) ReflectionUtils.getField(field, bean);
    }

    private static void customizeSpringfoxHandlerMappings(List<RequestMappingInfoHandlerMapping> mappings) {
        List<RequestMappingInfoHandlerMapping> copy = mappings.stream()
                .filter(mapping -> mapping.getPatternParser() == null)
                .collect(Collectors.toList());
        mappings.clear();
        mappings.addAll(copy);
    }
}
