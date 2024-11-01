package at.rayman.saveTheSpire.util;

import at.rayman.saveTheSpire.Result;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import static at.rayman.saveTheSpire.SaveTheSpire.logger;

public class NetworkService {

    private static NetworkService instance;

    private NetworkService() {
    }

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NetworkService();
        }
        return instance;
    }

    public static Result uploadSave() {
        HttpURLConnection connection = null;
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        String lineEnd = "\r\n";
        String charset = "UTF-8";

        try {
            URL url = new URL(Constants.URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("User-Agent", Constants.USER_AGENT);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (OutputStream outputStream = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true)) {

                File file = new File(Constants.ZIP_PATH);
                String fileName = file.getName();
                writer.append("--").append(boundary).append(lineEnd)
                    .append("Content-Disposition: form-data; name=\"" + Constants.ZIP_NAME + "\"; filename=\"").append(fileName).append("\"").append(lineEnd)
                    .append("Content-Type: ").append(HttpURLConnection.guessContentTypeFromName(fileName)).append(lineEnd)
                    .append("Content-Transfer-Encoding: binary").append(lineEnd)
                    .append(lineEnd)
                    .flush();

                try (FileInputStream inputStream = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                }
                writer.append(lineEnd)
                    .flush();

                writer.append("--").append(boundary).append("--").append(lineEnd)
                    .flush();
            }

            // Read the response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int responseCode = connection.getResponseCode();
            return Result.error("Backend returned status: " + responseCode);
        } catch (IOException e) {
            logger.error("Unknown error while uploading save", e);
            return Result.error(e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public Result downloadSave() {
        HttpURLConnection connection = null;
        try {
            URL url = new URI(Constants.URL).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", Constants.USER_AGENT);

            try (InputStream is = connection.getInputStream();
                 FileOutputStream fos = new FileOutputStream(Constants.ZIP_PATH)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

            int responseCode = connection.getResponseCode();
            return Result.success("Backend returned status: " + responseCode);
        } catch (Exception e) {
            logger.error("Error while downloading save", e);
            return Result.error(e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
