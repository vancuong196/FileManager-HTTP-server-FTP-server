/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuong.filemanage.HttpServer;

import java.util.ArrayList;

/**
 *
 * @author Cuong
 */
public class ConnectionManager {
    ArrayList<ConnectionHandler> connections;
    private static ConnectionManager INSTANCE =new ConnectionManager();
    public void broadcast(String message, ConnectionHandler sender){
        for (int i=0; i< connections.size(); i++){
            if (connections.get(i)!=sender){

               // connections.get(i).send(Constants.POST_CMD);

                Log.log("Message sent to all client"+ message);
                connections.get(i).send(message);
               
            }
        }
    }
   
    private ConnectionManager(){
        connections = new ArrayList();
    }
    public static ConnectionManager getInstance(){
        return INSTANCE;
    }
    public void add(ConnectionHandler connection) {
        connection.start();
        connections.add(connection);
    }
    public void remove(ConnectionHandler connection) {
       
        connections.remove(connection);
    } 
    
}
