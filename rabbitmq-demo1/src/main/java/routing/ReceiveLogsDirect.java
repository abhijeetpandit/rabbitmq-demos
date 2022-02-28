package routing;
import com.rabbitmq.client.*;

public class ReceiveLogsDirect {

  private static final String EXCHANGE_NAME = "direct_logs";

  public static void main(String[] argvs) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    

    String[] argv = new String[] {"0", "1", "2"};
    
    for (String severity : argv) {
    	Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, severity);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println();
            try {
            	System.out.println(severity+"->>>>>"+Thread.currentThread().getName() + "--->" + " [x] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            	Thread.sleep(1000);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

   
  }
}