package marktian3.Kafka;

import java.io.IOException;
import java.util.Properties;

public class TwitterSecrets {

    String consumerKey;
    String consumerSecret;
    String token;
    String secret;

    public TwitterSecrets() throws IOException {
        Properties properties = new Properties();
        properties.loadFromXML(getClass().getResourceAsStream("/properties.xml"));
        this.consumerKey = properties.getProperty("consumerKey");
        this.consumerSecret = properties.getProperty("consumerSecret");
        this.token = properties.getProperty("token");
        this.secret = properties.getProperty("secret");
    }
}
