package com.esi.easyorder.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.preference.PreferenceManager;
import com.esi.easyorder.ServerClient;
import com.esi.easyorder.ServerMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class ServerService extends Service {

    boolean bound = false;
    boolean connected = false;
    ServerClient client;
    String serverIP;
    String DatabaseName;
    @Override
    public void onCreate() {
        super.onCreate();

        serverIP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("IPAddress", "185.181.10.83");

        DatabaseName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("databaseName", "vffoodzina"); // change later
    }

    public class Binder extends android.os.Binder {
        public ServerService getSevice() {
            return ServerService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bound = false;
        connected = false;
        try {
            client.Destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void ConnectServer(Activity activity) {
        if(!connected) {
            // client = new ServerClient(activity, "196.218.98.134", 2550, null);
            client = new ServerClient(activity, serverIP, 2550, null);
            connected = true;
            JSONObject msg = new JSONObject();
            try {
                String type = PreferenceManager.getDefaultSharedPreferences(activity).getString("storeType", "storeType");
                msg.put("Msg", "reg_db");
                msg.put("db", DatabaseName);
                msg.put("type", type);
                sendMessage(msg.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMessage(ServerMessage r) {
        if(connected && client != null) {
            client.setServerMessage(r);
        }
    }

    public void sendMessage(String message) throws IOException {
        if(connected && client != null) {
            client.SendMessage(message);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
