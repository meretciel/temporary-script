package language.socket;

import lombok.extern.slf4j.Slf4j;

import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Server {
    private static int PORT_NUMBER = 4000;

    private static void handleSocketConnection(Socket clientSocket) {
        String clientSocketInfo = clientSocket.getInetAddress() + ":" + clientSocket.getPort();
        log.info("Start a new connection {}", clientSocketInfo);

        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), false);
                OutputStream os = clientSocket.getOutputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));)
        {
            clientSocket.setTcpNoDelay(true);
            log.info("Buffer size: {}", clientSocket.getSendBufferSize());
//            clientSocket.setSendBufferSize(131072);
            clientSocket.setSendBufferSize(1024 * 1024);
            clientSocket.setReceiveBufferSize(8192);
            String inputLine;
            out.flush();
            while ((inputLine = in.readLine()) != null) {

                log.info("Received message of size {} from {}", inputLine.length(), clientSocketInfo);
                long start = System.nanoTime();
//                out.println(inputLine);
//                out.flush();
                final byte[] bytes = inputLine.getBytes("UTF-8");

                final int step = 8192;
                int k = 0;
                int l;
                while (k < bytes.length) {

                    if (k + step < bytes.length) {
                        l = step;
                    } else {
                        l = bytes.length - k;
                    }
                    os.write(bytes, k, l);
                    os.flush();
                    k += l;
                }
                os.write('\n');

                log.info("socket, {}, {}", System.nanoTime() - start, inputLine.length());

            }

        } catch (final IOException e) {
            throw new RuntimeException("Socket connection error.");
        } finally {
            try {
                log.info("Close socket connection {}", clientSocketInfo);
                clientSocket.close();
            } catch (IOException e) {
                log.error("IOException when closing socket.");
            }
        }

    }


    public static void main(String[] args) throws IOException {
        log.info("Starting program.");
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleSocketConnection(clientSocket));
            } catch (Exception e) {

            }
            Thread.yield();

        }
    }
}