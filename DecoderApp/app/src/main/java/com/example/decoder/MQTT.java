package com.example.decoder;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.BlockingQueue;

public class MQTT {
    public static MqttClient client;

    public static String gTopic = "videoEn";

    public static MqttConnectOptions options;

    public static String mqttHost = "tcp://134.208.0.9:1883";

    private final BlockingQueue<byte[]> sharedQ;

    public MQTT(final BlockingQueue<byte[]> shared){
        this.sharedQ = shared;
        try {
            client = new MqttClient(mqttHost, "phone2",new MemoryPersistence());//id : googleglass or phone
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName("run");
            options.setPassword("run".toCharArray());
            options.setConnectionTimeout(0);// in sec?? or nano sec?
            options.setKeepAliveInterval(15);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //renew connection
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    sharedQ.put(message.getPayload());
                    //from topic get video or text or audio
                    //from message get payload
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void startSub(){
        try{
            int[] Qos = {1}; //set your config here 0,1,2
            String[] topic = {"videoEn"}; // set multiple topic here
            client.subscribe(topic, Qos);
            Log.i("MQTT","start subscribe");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void startPub(String m){
        try{
            MqttTopic topic = client.getTopic(gTopic); // your topic here
            MqttMessage message = new MqttMessage(m.getBytes());
            message.setQos(0);
            client.publish(gTopic, message);
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
