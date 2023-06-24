package com.example.demomqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    private String clientID="";
    private String host="tcp://fbc96.cloud.shiftr.io:1883";
    private String user="fbc96";
    private String pass="HtrhkVXeAqXgUi6k";
    public static String topic="LED";
    private String ON="ON";
    private String OFF="OFF";

    private MqttAndroidClient client;
    private MqttConnectOptions options;
    private TextView lblClientID;
    private Button btnON;
    private Button btnOFF;
    private TextView lblInfo;
    private boolean permission=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvents();
        getClientName();
        connectBroker();
    }
    public void initViews(){
        this.lblClientID=findViewById(R.id.lblClientID);
        this.btnON=findViewById(R.id.btnON);
        this.btnOFF=findViewById(R.id.btnOFF);
        this.lblInfo=findViewById(R.id.lblInfo);
    }
    public void initEvents(){
        this.btnON.setOnClickListener(view -> {
            Toast.makeText(getBaseContext(),"Click en botón ON",Toast.LENGTH_SHORT).show();
            sendMessage(topic,ON);
        });
        this.btnOFF.setOnClickListener(view -> {
            Toast.makeText(getBaseContext(),"Click en botón OFF",Toast.LENGTH_SHORT).show();
            sendMessage(topic,OFF);
        });


    }
    public void getClientName(){
        String manufacturer= Build.MANUFACTURER;
        String model=Build.MODEL;
        String user=Build.USER;
        this.clientID=manufacturer+"-"+model+"-"+user;
        this.lblClientID.setText(this.clientID);
    }
    private  void connectBroker(){
        this.client=new MqttAndroidClient(getApplicationContext(),this.host,this.clientID);
        this.options=new MqttConnectOptions();
        this.options.setUserName(this.user);
        this.options.setPassword(this.pass.toCharArray());
        try{
            IMqttToken token=this.client.connect(this.options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getBaseContext(),"Conectado",Toast.LENGTH_SHORT);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getBaseContext(),"Error en la conexión",Toast.LENGTH_SHORT);
                }
            });
        }catch (MqttException e){
            e.printStackTrace();
        }

    }
    public void checkConnection(){
        if (this.client.isConnected()){
            this.permission=true;
        }else{
            this.permission=false;
        }
    }
    private  void sendMessage(String topic, String msg){
        checkConnection();
        if (this.permission){
            //Si 0 entonces no se queda esperando si llega el mensaje o no
            int qos=0;
            try {
                this.client.publish(topic,msg.getBytes(),qos,false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
    private void subscribeToTopic(){
        try {
            this.client.subscribe(this.topic,0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        this.client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(getBaseContext(),"Conexión Perdida",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.matches(MainActivity.topic)){
                    String msg=new String(message.getPayload());
                    if(msg.matches(ON)){
                        lblInfo.setBackgroundColor(Color.GREEN);
                    }else if(msg.matches(OFF)){
                        lblInfo.setBackgroundColor(Color.RED);
                    }
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}