/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuong.filemanage.HttpServer;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author Cuong
 */
public class ConnectionHandler extends Thread {
    Socket mConnection;
    BufferedReader in;
    PrintStream out;
    ConnectionManager mConnectionManager;
    @Override
    public void run() {

            try {
                String route="";
                String line;
                int method = 0;
                int l = 0;
                while (!TextUtils.isEmpty(line = in.readLine())) {

                    if (line.startsWith("GET /")) {
                        int start = line.indexOf('/') + 1;
                        int end = line.indexOf(' ', start);
                        route = line.substring(start, end);

                    System.out.println("Route is: "+route);
                    File directory = new File("/"+route);
                    if (directory.isDirectory()) {
                        String bytes = new HtmlHelper(route).getByteArray();
                        // Send out the content.
                        out.println("HTTP/1.0 200 OK");
                        out.println("Content-Type: " + "text/html");
                        out.println("Content-Length: " + bytes.length());
                        out.println();
                        out.println(bytes);
                        out.flush();
                        break;
                    }
                    else {
                        byte[] bytes = loadContent("/"+directory);
                        // Send out the content.
                        out.println("HTTP/1.0 200 OK");
                        out.println("Content-Type: " + "application/octet-stream");
                        out.println("Content-Length: " + bytes.length);
                        writeFileToStream(out,"/"+directory);
                        out.flush();
                        break;
                    }

                    }
                }
                this.mConnection.close();
                ConnectionManager.getInstance().remove(this);


            } catch (IOException e) {

            }



    }

    private byte[] loadContent(String fileName) throws IOException {
        File file = new File(fileName);
        byte[] fileData = new byte[(int) file.length()];
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        dis.readFully(fileData);
        dis.close();
        System.out.println("File lenght" +fileName.length());
        return fileData;
    }
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
    public ConnectionHandler(Socket sk) {
        this.mConnection = sk;
        mConnectionManager = ConnectionManager.getInstance();
        
        try {
        in = new BufferedReader(new InputStreamReader(sk.getInputStream()));
        } catch (IOException e){
            Log.log("Can't get input stream"+e.toString());
        }
        
        try {
        out = new PrintStream(sk.getOutputStream());
        
        } catch ( IOException e){
            Log.log("Can't get output stream"+e.toString());
        }
        
    }
   public void send(String message) {
       out.println(message);
       
   }
}
