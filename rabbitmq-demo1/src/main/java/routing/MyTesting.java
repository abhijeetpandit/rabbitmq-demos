package routing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MyTesting {
	public static final String QUEUE_PREFIX = "distQueue";
	public static final String EXCHANGE_NAME = "testEX2";
	public static final int MAX_THREADS = 4;

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("172.19.1.167");
		factory.setUsername("insync");
        factory.setPassword("admin123");
        Random r = new Random();
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
        	channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
        	//channel.confirmSelect();
        	
        	for(int count = 0; count < MAX_THREADS; count++) {
        		String queueName = QUEUE_PREFIX + count;
        		DeclareOk declareOk = channel.queueDeclare(queueName, true, false, false, null);
        		channel.queueBind(queueName, EXCHANGE_NAME, String.valueOf(count));
        	}
			String dt = new SimpleDateFormat("dd HH:mm:ss").format(new Date());
			for (int count = 1; count < 20; count++) {
				channel.txSelect();
				String partition = String.valueOf(r.nextInt(4));
				String message = dt + "Message no " + count;

				channel.basicPublish(EXCHANGE_NAME, partition, true, new AMQP.BasicProperties.Builder()
			               .contentType("text/plain")
			               .deliveryMode(2)
			               .priority(1)
			               .userId("insync")
			               .build(), message.getBytes("UTF-8"));
				channel.txCommit();
				System.out.println(" [x] Sent '" + "partition =" + partition + "':'" + message + "'" );
				Thread.sleep(5);
			}
        }
	}
}
