package marktian3.Kafka;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitterProducer {

    Logger logger = LoggerFactory.getLogger(TwitterProducer.class.getName());
    PersonalProperties personalProperties = new PersonalProperties();

    //List<String> terms = Lists.newArrayList("bitcoin");
    List<String> terms;

    public TwitterProducer() throws IOException {}

    public static void main(String[] args) throws IOException {
        new TwitterProducer().run();
    }

    public void run() throws IOException {

        logger.info("Initializing application");

        System.out.println("Please enter terms to subscribe to: ");
        this.terms = UserInput.readUserTerms();
        logger.info("Subscribing to following terms: " + terms);

        //Twitter Client
        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);
        Client client = createTwitterClient(msgQueue);
        client.connect();

        //Kafka Producer
        KafkaProducer<String, String> producer = createKafkaProducer();

        //Shutdown hook
        addShutDownHook(client, producer);

        //Send tweets to Kafka
        // on a different thread, or multiple different threads....
        while (!client.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }

            if(msg != null){
                logger.info(msg);
                //Pre-create topic in kafka as producer cannot produce to topics that don't yet exist
                producer.send(new ProducerRecord<>("tweets_topic_01", null, msg), (recordMetadata, e) -> {
                    if(e != null){
                        logger.error("Error: ", e);
                    }
                });
            }
        }
        logger.info("End of application");
    }


    public Client createTwitterClient(BlockingQueue<String> msgQueue){

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

        // Optional: set up some followings and track terms
        //List<Long> followings = Lists.newArrayList(1234L, 566788L);
        //List<String> terms = Lists.newArrayList("twitter", "api");
        //hosebirdEndpoint.followings(followings);
        hosebirdEndpoint.trackTerms(terms);

        Authentication hosebirdAuth = new OAuth1(personalProperties.consumerKey, personalProperties.consumerSecret, personalProperties.token, personalProperties.secret);

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        Client hosebirdClient = builder.build();
        return hosebirdClient;
    }

    public KafkaProducer<String, String> createKafkaProducer(){
        String bootstrapServers = personalProperties.bootstrap_server_ip;

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create the producer, key and value are both strings
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        return producer;
    }

    private void addShutDownHook(Client client, KafkaProducer<String, String> producer) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Application...");
            logger.info("Shutting down twitter client...");
            client.stop();
            logger.info("Closing Kafka producer...");
            producer.close();
            logger.info("Application stopped successfully");
        }));
    }

}
