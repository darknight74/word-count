package com.fabris.wordcounter.configuration;

import com.rabbitmq.http.client.Client;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

@Configuration
@ConfigurationProperties(prefix = "rabbit")
@Validated
public class RabbitConfiguration {

    public static final String LINES_TO_BE_COUNTED_QUEUE = "lines-to-be-counted";

    @NotNull
    private String host;

    @NotNull
    private Integer port;

    @NotNull
    private Integer adminPort;

    @NotNull
    private String user;

    @NotNull
    private String password;


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(user);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public Queue linesToBeCountedQueue() {
        return new Queue(LINES_TO_BE_COUNTED_QUEUE);
    }

    @Bean
    public Client rabbitAdminClient() throws MalformedURLException, URISyntaxException {
        return new Client("http://" + host + ":" + adminPort + "/api/", user, password);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getAdminPort() {
        return adminPort;
    }

    public void setAdminPort(Integer adminPort) {
        this.adminPort = adminPort;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
