package language.socket;



import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class JavaClient {
    private static int PORT_NUMBER = 4000;
    private static String HOST_NAME = "127.0.0.1";

    public static void main(String[] args) {
        try (
            Socket sock = new Socket(HOST_NAME, PORT_NUMBER);
            PrintWriter out = new PrintWriter(sock.getOutputStream(), false);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(sock.getInputStream()));

        )
        {
            sock.setTcpNoDelay(true);

            int size = 1000 * 1000;
            sock.setSendBufferSize(1024 * 1024);
            sock.setReceiveBufferSize(8192);

            int count = 100;
            String message = StringUtils.repeat("*", size);

            for (int i = 0; i < count; ++i) {
                long t0 = System.nanoTime();
                out.println(message);
                out.flush();
                long t1 = System.nanoTime();
                in.readLine();
                long t2 = System.nanoTime();
                Thread.sleep(5);

                log.info("send,{}", t1 - t0);
                log.info("recv,{}", t2 - t1);

            }

        } catch (final IOException e) {

        } catch (Exception e) {

        }



    }
}
