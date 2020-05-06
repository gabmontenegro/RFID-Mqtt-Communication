package com.itbam.gabrielvillacrez.peopletracker.activities;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.itbam.gabrielvillacrez.peopletracker.R;
import com.itbam.gabrielvillacrez.peopletracker.adapters.AdapterPersonalizado;
import com.itbam.gabrielvillacrez.peopletracker.objects.Person;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;


public class MainActivity extends AppCompatActivity
{
    private Button btnConectar;
    private Button btnLimpar;


    private String Tag = "TAG";
    private String URL;
    private int myQOS = 2;
    private String SubscribeTopic = "T2/1/Recebido"; // recebido do BD//"Contador/contagem";
    private String PublishTopic = "T2/1/Enviado";   // Enviado para o sw de controle
    private String zerarTopic = "T2/1/Limpar";


    String limpar = "Limpar";

    String clientId;
    MqttAndroidClient client;

    String clientIdP;
    MqttAndroidClient clientPub;

    boolean flagBotao = false;

    HashSet<String> seenPerson;


    /* ListView Personalizada */
    List<Person> list;
    AdapterPersonalizado adapter;
    int nVezes = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        /*Componentes da view */
        final EditText caminhoMqtt = findViewById(R.id.editTextIp);

        btnConectar = (Button) findViewById(R.id.btnConnectar);
        btnLimpar = (Button) findViewById(R.id.btnLimpar);

        btnConectar.setBackgroundResource(android.R.drawable.btn_default);
        btnLimpar.setBackgroundResource(android.R.drawable.btn_default);

        list = new ArrayList<Person>();
        adapter = new AdapterPersonalizado(list, this);

        ListView listviewNomes = (ListView) findViewById(R.id.listaNomes);



        listviewNomes.setAdapter(adapter);
        seenPerson = new HashSet<String>();

        /* Acao dos Botões */
        btnConectar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                setURL(caminhoMqtt.getText().toString());
                clientId = MqttClient.generateClientId();
                client = new MqttAndroidClient(MainActivity.this, URL, clientId);
                try
                {
                    connect(client);

                }
                catch (Exception e)
                {
                    Log.d(Tag, "[setOnClickListener]message: " + e.getMessage());
                }

            }
        });

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clientIdP = MqttClient.generateClientId();
                //clientPub = new MqttAndroidClient(MainActivity.this, URL, clientId);

                publish(client, limpar);
                Toast.makeText(MainActivity.this, "Pode passar novamente!", Toast.LENGTH_SHORT).show();


            }
        });
    }



    public void connect(final MqttAndroidClient client)
    {
        try
        {
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(false);


            IMqttToken token = client.connect(mqttConnectOptions);

            token.setActionCallback(new IMqttActionListener()
            {
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    // We are connected
                    Log.d(Tag, "[connect]onSuccess");
                    flagBotao = true;

                    Toast.makeText(MainActivity.this, "Conectado ao Servidor MQTT com sucesso!!\nip: " + getURL(), Toast.LENGTH_SHORT).show();
                    if(flagBotao)
                    {
                        btnConectar.setBackgroundColor(Color.GREEN);
                        btnConectar.setText("Conectado");
                    }

                    subscribe(client, SubscribeTopic);

                    client.setCallback(new MqttCallback()
                    {

                        @Override
                        public void connectionLost(Throwable cause)
                        {
                            Log.d(Tag, "[clientCallback] - connectionLost");
                            flagBotao = false;
                        }
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception
                        {
                            Log.d(Tag, "[clientCallback - messageArrived]  topic: "+ topic +" message: "+message.toString());
                            //String nomeValido = message.toString().substring(2);

                            if ( (topic.equals(SubscribeTopic)) )
                            {
                                String nomeValido = message.toString().substring(2);
                                inserirNomeLista(nomeValido);

                            }


                        }

                        /*public void messageArrived(String topic, MqttMessage message) throws Exception
                        {
                            Log.d(Tag, "[clientCallback - messageArrived]  topic: "+topic+" message: "+message.toString());

                            if ((topic.equals(SubscribeTopic) ) && (!message.isDuplicate()) ) // && (!message.isDuplicate()) )
                            {
                                //Removendo o identificador CR da frente da string
                                String nomeValido = message.toString().substring(2);
                                if(!seenPerson.contains(nomeValido))
                                {
                                    inserirNomeLista(nomeValido);
                                    seenPerson.add(nomeValido);
                                }

                            }

                        }*/

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token)
                        {

                        }
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    // Something went wrong e.g. connection timeout or firewall problems
                    // Log.d("file", "onFailure");
                    Log.d(Tag,"[connect]onFailure");
                    flagBotao = false;

                }
            });
        }
        catch (MqttException e)
        {
            e.printStackTrace();
        }
    }

    public void zerarHashSet()
    {
        seenPerson.clear();
    }

    public boolean temNaLista(String nome)
    {
        return list.contains(nome);
    }
    public void inserirNomeLista(String nome)
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss a \t dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        Person p = new Person(nome, formattedDate);

        list.add(p);

        adapter.notifyDataSetChanged();

    }

    public void limparLista()
    {

        //list.clear();
        //zerarHashSet();
        //adapter.notifyDataSetChanged();

    }


    public void subscribe(MqttAndroidClient client , String topic)
    {
        try
        {
            IMqttToken subToken = client.subscribe(topic, myQOS);
            subToken.setActionCallback(new IMqttActionListener()
            {
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    // The message was published
                    Log.d(Tag,"[subscribe]onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    Log.d(Tag,"[subscribe]onFailure");
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        }
        catch (MqttException e)
        {
            e.printStackTrace();
        }
    }


    public void publish(MqttAndroidClient client, String payload)
    {
        byte[] encodedPayload = new byte[0];
        try
        {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(zerarTopic, message);
            Log.d(Tag,"[publish]Mensagem enviada!");
        }
        catch (UnsupportedEncodingException | MqttException e)
        {
            e.printStackTrace();
        }
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = "tcp://" + URL + ":1883";
    }
}
