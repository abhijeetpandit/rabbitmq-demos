package routing;

import java.util.Random;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLog {

	private static final String EXCHANGE_NAME = "direct_logs";

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Random r = new Random();

		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.exchangeDeclare(EXCHANGE_NAME, "direct");

			for (int count = 50; count < 60; count++) {
				String severity = String.valueOf(r.nextInt(3));
				String message = "Message no " + count;

				channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes("UTF-8"));
				System.out.println(" [x] Sent '" + severity + "':'" + message + "'");
				Thread.sleep(500);
			}
		}
	}
	// ..
}