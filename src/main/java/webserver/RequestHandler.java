package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String STATIC_FILEPATH = "./src/main/resources/static";
    private static final String TEMPLATE_FILEPATH = "./src/main/resources/templates";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // Request
            // http 메세지를 저장하기 위한 StringBuilder 생성
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            logger.debug("request: {}", line);
            // 경로 parsing
            String route = line.split(" ")[1];
            logger.debug("route: {}", route);
            // 나머지 확인
            while(!line.equals("")) {
                line = br.readLine();
                sb.append(line);
                sb.append("\r\n");
                logger.debug("header: {}", line);
            }

            // Response
            DataOutputStream dos = new DataOutputStream(out);
            // 요청 경로의 파일을 반환
            File f;
            byte[] body;
            // 두 가지의 경로 모두를 조회해야 합니다.
            if((f = new File(STATIC_FILEPATH + route)).exists()) {
                body = Files.readAllBytes(f.toPath());
            } else {
                f = new File(TEMPLATE_FILEPATH + route);
                body = Files.readAllBytes(f.toPath());
            }
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
