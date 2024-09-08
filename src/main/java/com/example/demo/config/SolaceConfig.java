package com.example.demo.config;

import com.solacesystems.jcsmp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolaceConfig {

    @Value("${solace.java.host}")
    private String solaceHost;

    @Value("${solace.java.msgVpn}")
    private String msgVpn;

    @Value("${solace.java.clientUsername}")
    private String clientUsername;

    @Value("${solace.java.clientPassword}")
    private String clientPassword;

    @Bean
    public JCSMPSession jcsmpSession() throws JCSMPException {
        var properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, solaceHost);        // host:port
        properties.setProperty(JCSMPProperties.VPN_NAME, msgVpn);        // message VPN
        properties.setProperty(JCSMPProperties.USERNAME, clientUsername); // client username
        properties.setProperty(JCSMPProperties.PASSWORD, clientPassword); // client password

        // Create a session
        return JCSMPFactory.onlyInstance().createSession(properties);
    }
}
