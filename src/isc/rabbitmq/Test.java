package isc.rabbitmq;

public class Test {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = -1;
        String user = "guest";
        String pass = "guest";
        String virtualHost = "/";
        String queue = "Tr1";
        int durable = 1;

        API api = new API(host, port, user, pass, virtualHost, queue, durable);


    }
}
