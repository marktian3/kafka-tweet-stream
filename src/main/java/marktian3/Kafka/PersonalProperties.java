package marktian3.Kafka;

import java.io.IOException;
import java.util.Properties;

public class PersonalProperties {

    String consumerKey;
    String consumerSecret;
    String token;
    String secret;
    String bootstrap_server_ip;

    public PersonalProperties() throws IOException {
        Properties properties = new Properties();
        properties.loadFromXML(getClass().getResourceAsStream("/properties.xml"));
        this.consumerKey = properties.getProperty("consumerKey");
        this.consumerSecret = properties.getProperty("consumerSecret");
        this.token = properties.getProperty("token");
        this.secret = properties.getProperty("secret");
        this.bootstrap_server_ip = properties.getProperty("bootstrap_server_ip");
    }
}
