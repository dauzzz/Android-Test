package com.example.myapplication;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

public class MQTT extends Thread{
    public static MqttClient client;

    public static String gTopic = "videoEn";

    public static MqttConnectOptions options;

    public static String mqttHost = "tcp://134.208.0.9:1883";

    private final BlockingQueue<byte[]> sharedQ;

    public MQTT(BlockingQueue<byte[]> sharedQ){
        this.sharedQ = sharedQ;
        try {
            client = new MqttClient(mqttHost, "phone1",new MemoryPersistence());//id : googleglass or phone
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName("run");
            options.setPassword("run".toCharArray());
            options.setConnectionTimeout(0);// in sec?? or nano sec?
            options.setKeepAliveInterval(15);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    try {
                        client.reconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    //renew connection
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    //from topic get video or text or audio
                    //from message get payload
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.i("MQTT","Thread:"+Thread.currentThread());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void startSub(){
        try{
            int[] Qos = {}; //set your config here 0,1,2
            String[] topic = {}; // set multiple topic here
            client.subscribe(topic, Qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void release(){
        try {
            client.close();
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void startPub(byte[] m){
        try{
            Log.i("MQTT","setTopic");
            MqttTopic topic = client.getTopic(gTopic); // your topic here
            Log.i("MQTT","getMessage");
            MqttMessage message = new MqttMessage(m);
            message.setQos(1);
            client.publish(gTopic, message);
            Log.i("MQTT","published");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while(!Thread.interrupted()){
            try {
                Log.i("MQTT","Thread:"+Thread.currentThread());
                if(!client.isConnected()) {
                    client.connect(options);
                    Log.i("MQTT","connected");
                }
                /*ByteBuffer bf = (ByteBuffer) sharedQ.take();
                Log.i("MQTT",bf.toString());
                if(bf.remaining()>0){
                    byte[] myB = new byte[bf.remaining()];
                    Log.i("MQTT","startSending");
                    bf.get(myB);
                    startPub(myB);
                    Log.i("MQTT","endSending");
                }*/
                startPub(sharedQ.take());
            } catch (InterruptedException | MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
