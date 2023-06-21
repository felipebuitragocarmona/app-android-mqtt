package com.example.demomqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {
    private String clientID="";
    private String host="tcp://fbc96.cloud.shiftr.io:1883";
    private String user="fbc96";
    private String pass="HtrhkVXeAqXgUi6k";
    private String topic="LED";
    private String ON="ON";
    private String OFF="OFF";

    private MqttAndroidClient client;
    private MqttConnectOptions options;
    private TextView lblClientID;
    private Button btnON;
    private Button btnOFF;
    private TextView lblInfo;
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
        });
        this.btnOFF.setOnClickListener(view -> {
            Toast.makeText(getBaseContext(),"Click en botón OFF",Toast.LENGTH_SHORT).show();
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
}