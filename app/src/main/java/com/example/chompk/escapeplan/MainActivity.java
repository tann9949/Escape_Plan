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
import com.github.nkzawa.emitter.Emitter;
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
    private Button btnMute;
    private boolean muted;
    Intent bgm;

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
        btnMute = (Button) findViewById(R.id.mute);
        bgm = new Intent(this, BackgroundSoundService.class);
        startService(bgm);

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

        btnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BackgroundSoundService.muted) {
                    BackgroundSoundService.muted = false;
                    BackgroundSoundService.player.start();
                    btnMute.setText(R.string.mute);
                } else {
                    BackgroundSoundService.muted = true;
                    BackgroundSoundService.player.pause();
                    btnMute.setText(R.string.unmute);
                }
            }
        });
        onBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        stopService(bgm);
    }

    private void onBroadcast() {
        MainActivity.mSocket.on("broadcast", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String serverMess = args[0].toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Boardcast from server:\n"+serverMess, Toast.LENGTH_SHORT).show();
                        System.out.println("broadcast being called");
                        System.out.println("messege is: "+serverMess);
                    }
                });
            }
        });
    }

}
