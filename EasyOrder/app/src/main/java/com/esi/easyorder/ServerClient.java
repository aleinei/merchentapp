package com.esi.easyorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClient {

    Activity callerActivity;
    String serverIP;
    int serverSocket;
    Socket client;
    ServerMessage r;
    PrintWriter outputStream;
    BufferedReader inputStream;
    Thread connect;
    Thread sendMessage;
    boolean isConnected = false;
    public ServerClient(Activity caller, String serverIP, int serverSocket, ServerMessage r) {
        this.serverIP = serverIP;
        this.serverSocket = serverSocket;
        this.callerActivity = caller;
        this.r = r;
        connect = new Thread(new ConnectToServer());
        connect.start();
    }

    public void SendMessage(String message) throws IOException {
        sendMessage = new Thread(new SendMessage(message));
        sendMessage.start();
    }
    public void setServerMessage(ServerMessage m) {
        this.r = m;
    }
    public class SendMessage implements Runnable {
        String message;
        public SendMessage(String message) {
            this.message = message;
        }
        @Override
        public void run() {
            if(client == null) {
                try {
                    while(true) {
                        if(client != null) break;
                        sendMessage.sleep(10);
                    }
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"), true);
                    out.println(message);
                } catch (InterruptedException ex) {
                } catch (IOException ex) {
                }
            } else {
                try {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"), true);
                    out.println(message);
                    System.out.println("message sent");
                } catch (IOException ex) {
                }
            }
        }

    }
    public void Destroy() throws IOException, JSONException {
        JSONObject message = new JSONObject();
        message.put("Msg", "close_connection");
        this.SendMessage(message.toString());
        this.connect.interrupt();
        this.sendMessage.interrupt();
    }

    @SuppressLint("StaticFieldLeak")
    private class SendMessageAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            while(true) {
                if(client.isConnected()) {
                    outputStream.println(strings[0]);
                    Log.d("Message", "Sent");
                    Toast.makeText(callerActivity, "message sent", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    Log.d("Connection", "Still not connecting");
                }
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class ConnectToServer implements Runnable {

        @Override
        public void run() {
            try {
                client = new Socket(serverIP, serverSocket);
                BufferedReader ie = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
                Log.d("Server Connection", "Connected");
                String message;
                while((message = ie.readLine()) != null) {
                    r.setMessage(message);
                    callerActivity.runOnUiThread((Runnable)r);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
