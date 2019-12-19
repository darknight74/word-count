package com.fabris.wordcounter.configuration;

import com.rabbitmq.http.client.Client;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
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

    private String exchange;

    @NotNull
    private String queue;

    @NotNull
    private String addresses;

    @NotNull
    private String adminHost;

    @NotNull
    private Integer adminPort;

    @NotNull
    private String user;

    @NotNull
    private String password;


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(addresses);
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
        return new Queue(queue);
    }

    @Bean
    public Client rabbitAdminClient() throws MalformedURLException, URISyntaxException {
        return new Client("http://" + adminHost + ":" + adminPort + "/api/", user, password);
    }

    public String getAdminHost() {
        return this.adminHost;
    }

    public void setAdminHost(String host) {
        this.adminHost = host;
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

    public String getAddresses() {
        return this.addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public String getQueue() {
        return this.queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}
