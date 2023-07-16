package webserver.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Response {
    public enum MIME {
        HTML("text/html"),
        CSS("text/css"),
        JS("text/javascript"),
        ICO("image/vnd.microsoft.icon"),
        PNG("image/png"),
        JPG("image/jpeg");

        private final String mime;
        MIME(String mime) {
            this.mime = mime;
        }

        public String getMime() {
            return this.mime;
        }

        public MIME getMimeByExtension(String extension) {
            return Arrays.stream(MIME.values())
                    .filter(mime -> extension.equalsIgnoreCase(mime.toString()))
                    .collect(Collectors.toList())
                    .get(0);
        }
    }
    public enum STATUS {
        OK(200, "OK"),
        CREATED(201, "Created"),
        BAD_REQUEST(400, "Bad Request"),
        NOT_FOUND(404, "Not Found");

        private final int statusCode;
        private final String statusMessage;
        STATUS(int statusCode, String statusMessage) {
            this.statusCode = statusCode;
            this.statusMessage = statusMessage;
        }

        public int getStatusCode() {
            return statusCode;
        }
        public String getStatusMessage() {
            return statusMessage;
        }
    }

    private final STATUS status;
    private final String version;
    private final Map<String, String> headerMap;
    private final String body;

    public Response(STATUS status, String version,
                    Map<String, String> headerMap, String body) {
        this.status = status;
        this.version = version;
        this.headerMap = headerMap;
        this.body = body;
    }

}
