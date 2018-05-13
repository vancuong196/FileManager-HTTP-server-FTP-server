package com.cuong.filemanage.HttpServer;

/**
 * Created by Cuong on 3/19/2018.
 */

import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
public class HttpServer implements Runnable {

    private static final String TAG = "HttpServer";

    private final int mPort;

    private final AssetManager mAssets;

    private boolean mIsRunning;

    private ServerSocket mServerSocket;
    public boolean isRunning(){
        return this.mIsRunning;
    }
    public HttpServer(int port, AssetManager assets) throws IOException {
        mPort = port;
        mAssets = assets;
        mServerSocket = new ServerSocket(mPort);
    }

    public void start() {
        mIsRunning = true;
        new Thread(this).start();
    }

    /**
     * This method stops the web server
     */
    public void stop() {
        try {
            mIsRunning = false;
            if (null != mServerSocket) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing the server socket.", e);
        }
    }

    public int getPort() {
        return mPort;
    }

    @Override
    public void run() {
        try {


            while (mIsRunning) {
                Socket socket = mServerSocket.accept();
                ConnectionManager connectionManager = ConnectionManager.getInstance();
                connectionManager.add(new ConnectionHandler(socket));
                //handle(socket);
                //socket.close();
            }
        } catch (SocketException e) {
            // The server was stopped; ignore.
        } catch (IOException e) {
            Log.e(TAG, "Web server error.", e);
        }
    }

    /**
     * Respond to a request from a client.
     *
     * @param socket The client socket.
     * @throws IOException
     */
    private void handle(Socket socket) throws IOException {
        BufferedReader reader = null;
        PrintStream output = null;
        try {
            String route = null;

            // Read HTTP headers and parse out the route.
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            int method=0;
            int l=0;
            while (!TextUtils.isEmpty(line = reader.readLine())) {
                if (line.startsWith("GET /")) {
                    int start = line.indexOf('/') + 1;
                    int end = line.indexOf(' ', start);
                    route = line.substring(start, end);
                    break;
                } else
                 {
                     while (true) {
                     if (line.contains("filename=")){
                         int start = line.indexOf("filename=") + 10;
                         int end = line.length()-1;
                         String filename = line.substring(start, end);
                         System.out.println(reader.readLine());
/*
                         InputStream myInput = new DataInputStream(socket.getInputStream());
                                 // Path to the just created empty db
                         String outFileName = Environment.getExternalStorageDirectory().toString()+"/"+filename;

                         //Open the empty db as the output stream
                         OutputStream myOutput = new FileOutputStream(outFileName);

                         //transfer bytes from the inputfile to the outputfile
                         byte[] buffer = new byte[1024];
                         int length;
                         while ((length=myInput.read(buffer))==1024){
                             myOutput.write(buffer,0,length);
                         }
                            length = myInput.read(buffer);
                            myOutput.write(buffer,0,length);
                            System.out.println("Xong down load");
                         //Close the streams
                         myOutput.flush();
                         myOutput.close();
*/

                     }

                     method=1;
                     line = reader.readLine();
                     System.out.println(line);
                    }
                }

            }
            if (method==1) {
                output = new PrintStream(socket.getOutputStream());
                writeServerError(output);
                output.close();
                return;
            }
            // Output stream that we send the response to
            output = new PrintStream(socket.getOutputStream());

            // Prepare the content to send.

            if (null == route) {
                writeServerError(output);
                return;
            }
            if (route.equals("favicon.ico")) {
                writeServerError(output);
                return;
            }
            System.out.println("Route is: "+route);
            File directory = new File("/"+route);
            if (directory.isDirectory()) {
                String bytes = new HtmlHelper(route).getByteArray();
                // Send out the content.
                output.println("HTTP/1.0 200 OK");
                output.println("Content-Type: " + "text/html");
                output.println("Content-Length: " + bytes.length());
                output.println();
                output.println(bytes);
                output.flush();
            }
            else {

                // Send out the content.
                output.println("HTTP/1.0 200 OK");
                output.println("Content-Type: " + "application/octet-stream");
                writeFileToStream(output,"/"+directory);
                output.flush();
            }
        } finally {
            if (null != output) {
                output.close();
            }
            if (null != reader) {
                reader.close();
            }
        }
    }

    /**
     * Writes a server error response (HTTP/1.0 500) to the given output stream.
     *
     * @param output The output stream.
     */
    private void writeServerError(PrintStream output) {
        output.println("HTTP/1.0 500 Internal Server Error");
        output.flush();
    }

    /**
     * Loads all the content of {@code fileName}.
     *
     * @param fileName The name of the file.
     * @return The content of the file.
     * @throws IOException
     */
// change here
    public void writeFileToStream(PrintStream out, String fileName) throws IOException {
        File file = new File(fileName);
        byte[] fileData = new byte[1024];
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        out.println("Content-Length: " + file.length());
        out.append('\r').append('\n').flush();
        while (dis.read(fileData)>0) {
            out.write(fileData);
        }

        out.append('\r').append('\n').flush();
        dis.close();
        System.out.println("File lenght" +fileName.length());

    }
    /**
     * Detects the MIME type from the {@code fileName}.
     *
     * @param fileName The name of the file.
     * @return A MIME type.
     */
    private String detectMimeType(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        } else if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else {
            return "application/octet-stream";
        }
    }

}