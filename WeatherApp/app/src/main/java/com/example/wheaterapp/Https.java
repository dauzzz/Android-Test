package com.example.wheaterapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class Https extends AsyncTask<Void,Void,JSONObject>{
    private String weatherURL = "https://opendata.cwb.gov.tw/fileapi/v1/opendataapi/F-A0010-001?Authorization=CWB-D1C038E5-7E4E-4A75-A32D-6E42A1B835C1&downloadType=WEB&format=JSON";

    private JSONObject jsonObject;
    private BufferedReader bufferedReader;
    private HttpsURLConnection httpsConnection;

    @SuppressLint("StaticFieldLeak")
    private Context context;

    Https(Context context){
        this.context = context;
    }

    private void setHttpsConnection(){
        try {
            //private String
            URL url = new URL(weatherURL);
            httpsConnection = (HttpsURLConnection) url.openConnection(); //not establish a connection with the server, need to call connect()
            //httpsConnection.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
            httpsConnection.setDoInput(true);
            Log.i("http","connecting");
            httpsConnection.connect();
            bufferedReader = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream())); // if it's too long maybe I would change it to more readable way.

            StringBuilder stringBuffer = new StringBuilder(); //first I use stringbuffer, but the gradle recommended me to use stringbuilder(not thread safe)
            String line = "";
            while((line = bufferedReader.readLine())!= null){
                stringBuffer.append(line);// don't need \n
               //Log.i("httpGetData",line);
            }
            //Log.i("httpString",stringBuffer.toString());
            jsonObject = new JSONObject(stringBuffer.toString());
            //int in = jsonObject.length();
            //Log.i("httpsThread","length "+in);
            //Log.i("httpThread",jsonObject.getJSONObject("北部地區").toString());
            //Iterator<String> iterator = jsonObject.keys();
            /*while(iterator.hasNext()){
                String key = iterator.next();
                //Log.i("httpsThread","key: "+key);
                //Log.i("httpsThread","value: "+jsonObject.getString(key));
            }*/
            //Log.i("httpsThread","out while");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        finally {
            if(bufferedReader != null)
                try{
                    bufferedReader.close();
                    httpsConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public JSONObject getJsonObject(){
        return jsonObject;
    }
    //maybe don't need this if the default ca could do all the work
    private SSLContext setSSLContext(){ //manual, auto --> SSLContextFactory.getDefault();
        try{
            InputStream inputStream = context.getAssets().open("");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca = cf.generateCertificate(inputStream);
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null,null);
            keyStore.setCertificateEntry("ca",ca);
            String trustManagerAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(trustManagerAlgorithm);
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,trustManagers,null);
            return  sslContext;
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        setHttpsConnection();
        return jsonObject;
    }
}
