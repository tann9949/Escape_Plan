package com.example.chompk.escapeplan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chompk.escapeplan.Data.ConnectionData;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private Button btnOption;
    private Button btnPlay;
    private static String urlAddress;
    private EditText editName;

    static Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlAddress = "http://" + ConnectionData.getInstance().getIpAddress() + ":" + ConnectionData.getInstance().getPort();

        try {
            mSocket = IO.socket(urlAddress);
        } catch (URISyntaxException e) {
        }

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnOption = (Button) findViewById(R.id.btnOption);
        editName = (EditText) findViewById(R.id.playernamefield);

        mSocket.connect();


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Request Finding Match";
                mSocket.emit("req", message);

                System.out.println("text: "+editName.getText().toString());

                if (editName.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please insert player name!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.putExtra("playername", editName.getText().toString());
                    MainActivity.this.startActivity(intent);
                }
            }
        });

        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OptionActivity.class);
                intent.putExtra("status", mSocket.connected());
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

}
