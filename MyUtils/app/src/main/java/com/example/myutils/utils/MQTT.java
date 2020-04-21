package com.example.myutils.utils;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.logging.LoggingPermission;

public class MQTT {
    public static MqttClient client;

    public static String topic = "";

    public static MqttConnectOptions options;

    public static String mqttHost = "";

    public MQTT(){
        try {
            client = new MqttClient(mqttHost, "",new MemoryPersistence());//id : googleglass or phone
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName("");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
