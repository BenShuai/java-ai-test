package com.it.sun;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 当开启了 SSL 的 HTTPS 服务时，还需要 http 访问就需要这个
 */
@Configuration
public class HttpConfig {
//    @Value("${server.http.port}")
//    private int httpPort;
//
//    @Bean
//    public EmbeddedServletContainerCustomizer containerCustomizer() {
//        return new EmbeddedServletContainerCustomizer() {
//            @Override
//            public void customize(ConfigurableEmbeddedServletContainer container) {
//                if (container instanceof TomcatEmbeddedServletContainerFactory) {
//                    TomcatEmbeddedServletContainerFactory containerFactory =
//                            (TomcatEmbeddedServletContainerFactory) container;
//
//                    Connector connector = new Connector(TomcatEmbeddedServletContainerFactory.DEFAULT_PROTOCOL);
//                    connector.setPort(httpPort);
//                    containerFactory.addAdditionalTomcatConnectors(connector);
//                }
//            }
//        };
//    }
}
