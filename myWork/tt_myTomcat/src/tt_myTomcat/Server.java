package tt_myTomcat;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static ServerSocket serverSocket;
    private static ExecutorService executorService;
    private final static int POOL_SIZE = 15;

    public static void main(String[] args) throws Exception {
        serverSocket = new ServerSocket(8080);
        Socket socket = null;
        executorService = Executors.newFixedThreadPool(POOL_SIZE);

        while (true) {
            socket = serverSocket.accept();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html;charset=UTF-8");
            writer.println();

            executorService.execute(new Handler(socket, writer));
        }
    }
}
