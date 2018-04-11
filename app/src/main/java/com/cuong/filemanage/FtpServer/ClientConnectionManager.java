/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuong.filemanage.FtpServer;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Cuong
 */
public class ClientConnectionManager {
    ArrayList<ClientConnectionHandler> connections;
    private static ClientConnectionManager INSTANCE =new ClientConnectionManager();
    public void sendShutdownMsg(ClientConnectionHandler reciever) throws IOException{
        for (int i=0; i< connections.size(); i++){
            if (connections.get(i)==reciever){
                connections.get(i).println("221 Server is shutdown");
                remove(reciever);
            }
        }
    }
   
    private ClientConnectionManager(){
        connections = new ArrayList();
    }
    public static ClientConnectionManager getInstance(){
        return INSTANCE;
    }
    public void add(ClientConnectionHandler connection) {
        connection.start();
        connections.add(connection);
    }
    public void remove(ClientConnectionHandler connection) {
        connections.remove(connection);
    } 
    public int countConnection(){
        return connections.size();
    }
    
}