package isc.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

import java.nio.file.*;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by eduard on 06.10.2017.
 */
public class API {
    private final Channel _channel;

    private final String _queue;

    private final Connection _connection;

    public API(String host, int port, String user, String pass, String virtualHost, String queue)  throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(user);
        factory.setPassword(pass);
        factory.setVirtualHost(virtualHost);


        _connection = factory.newConnection();
        _channel = _connection.createChannel();
        _channel.queueDeclare(queue, false, false, false, null);
        _queue = queue;
    }

    public void sendMessage(byte[] msg, String correlationId, String messageId) throws Exception {
        sendMessageToQueue(_queue, msg, correlationId, messageId);
    }

    public void sendMessage(byte[] msg) throws Exception {
        sendMessageToQueue(_queue, msg);
    }

    public void sendMessageToQueue(String queue, byte[] msg) throws Exception {
        //log(msg);
        _channel.basicPublish("", queue, null, msg);
    }

    public void sendMessageToQueue(String queue, byte[] msg, String correlationId, String messageId) throws Exception {
        AMQP.BasicProperties props = createProperties(correlationId, messageId);
        _channel.basicPublish("", queue, props, msg);
    }

    public byte[] readMessageStream(String[] result) throws Exception {
        GetResponse response = readMessage(result);
        if (response == null) {
            return new byte[0];
        }
        return response.getBody();
    }

    public String[] readMessageString() throws Exception {
        String[] result = new String[16];

        GetResponse response = readMessage(result);
        if (response == null) {
            // No message retrieved.
        } else {
            result[15] = new String(response.getBody());
        }
        return result;
    }

    /*
    Get message and fill basic array props
     */
    private GetResponse readMessage(String[] msg) throws IOException {
        boolean autoAck = true;
        GetResponse response = _channel.basicGet(_queue, autoAck);
        if (response == null) {
            // No message retrieved.
            response = new GetResponse(null, null, new byte[0], 0);
        } else {
            AMQP.BasicProperties props = response.getProps();
            msg[0] =  Integer.toString(response.getBody().length);
            msg[1] =  Integer.toString(response.getMessageCount());
            msg[2] = props.getContentType();
            msg[3] = props.getContentEncoding();
            msg[4] = props.getCorrelationId();
            msg[5] = props.getReplyTo();
            msg[6] = props.getExpiration();
            msg[7] = props.getMessageId();
            msg[8] = props.getType();
            msg[9] = props.getUserId();
            msg[10] = props.getAppId();
            msg[11] = props.getClusterId();
            msg[12] = props.getDeliveryMode() != null ? Integer.toString(props.getDeliveryMode()) : null;
            msg[13] = props.getPriority() != null ? Integer.toString(props.getPriority()) : null;
            msg[14] = props.getTimestamp() != null ? props.getTimestamp().toString() : null;

            //log(response.getBody());
        }
        return response;

    }

    private void log(byte[] msg) throws IOException {
        Path path = Paths.get("C:\\InterSystems\\java.log");
        Files.write(path, msg);
    }

    public void close()throws Exception {
        _channel.close();
        _connection.close();
    }

    private AMQP.BasicProperties createProperties(String correlationId, String messageId) throws Exception
    {
        String contentType = null;
        String contentEncoding = null;
        HashMap<String, Object> headers = null;
        Integer deliveryMode = null;
        Integer priority = null;
        //String correlationId= null;
        String replyTo = null;
        String expiration= null;
        //String messageId= null;
        Date timestamp= null;
        String type = null;
        String userId= null;
        String appId = null;
        String clusterId= null;
        AMQP.BasicProperties props = new AMQP.BasicProperties(contentType, contentEncoding, headers, deliveryMode, priority, correlationId, replyTo, expiration, messageId, timestamp, type, userId, appId, clusterId);
        return props;
    }

}
