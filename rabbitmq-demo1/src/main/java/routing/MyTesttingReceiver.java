package routing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MyTesttingReceiver {
	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("172.19.1.167");
		factory.setUsername("insync");
		factory.setPassword("admin123");
		
		
		/*
		 * for (int count = 0; count < MyTesting.MAX_THREADS; count++) { ExecutorService
		 * es = Executors.newFixedThreadPool(1); Connection connection =
		 * factory.newConnection(es); Channel channel = connection.createChannel();
		 * String queueName = MyTesting.QUEUE_PREFIX + count;
		 * channel.queueDeclare(queueName, true, false, false, null);
		 * 
		 * final int partition = count; DeliverCallback deliverCallback = (consumerTag,
		 * delivery) -> { try { if (partition == 1) Thread.sleep(1); else
		 * Thread.sleep(5); System.out.println(Thread.currentThread().getName()); }
		 * catch (Exception e) { e.printStackTrace(); } String message = new
		 * String(delivery.getBody(), StandardCharsets.UTF_8);
		 * System.out.println(partition + " [x] Received '" + message + "'");
		 * 
		 * }; channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
		 * }); }
		 */
		
		for (int count = 0; count < MyTesting.MAX_THREADS; count++) {
			ExecutorService es = Executors.newFixedThreadPool(1);
			Connection connection = factory.newConnection(es);
			Channel channel = connection.createChannel();
			String queueName = MyTesting.QUEUE_PREFIX + count;
			channel.queueDeclare(queueName, true, false, false, null);

			final int partition = count;
			
			channel.basicConsume(queueName, false, "a-consumer-tag" + partition, new DefaultConsumer(channel) {

				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
						byte[] body) throws IOException {
					try {
						if (partition == 1)
							Thread.sleep(1);
						else
							Thread.sleep(5);
						System.out.println(Thread.currentThread().getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					String message = new String(body, StandardCharsets.UTF_8);
					System.out.println(partition + " [x] Received '" + message + "'");
					long deliveryTag = envelope.getDeliveryTag();
					channel.basicAck(deliveryTag, false);
				}
			});
		}

	}
}
