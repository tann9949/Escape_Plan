package com.example.chompk.escapeplan;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chompk.escapeplan.listeners.OnSwipeTouchListener;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameActivity extends AppCompatActivity {

    ImageView[][] blocks = new ImageView[5][5];
    Button btnSkip;
    Button btnSurrender;
    TextView timer;
    TextView playerstaus;
    View board;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_game);

        initializeImageView();
        btnSkip = (Button)findViewById(R.id.btnskip);
        btnSurrender = (Button)findViewById(R.id.btnsurrender);
        timer = (TextView)findViewById(R.id.timer);
        playerstaus = (TextView)findViewById(R.id.playerstatus);
        board = findViewById(R.id.board);

        setBoardListening();
        setOnSwipe();
        setWaitStatus();

        if(!MainActivity.mSocket.connected())
            playerstaus.setText("Not connected to server");
        MainActivity.mSocket.emit("req", "join");

    }

    private void initializeImageView() {
        blocks[0][0] = (ImageView) findViewById(R.id.A);
        blocks[0][1] = (ImageView) findViewById(R.id.B);
        blocks[0][2] = (ImageView) findViewById(R.id.C);
        blocks[0][3] = (ImageView) findViewById(R.id.D);
        blocks[0][4] = (ImageView) findViewById(R.id.E);

        blocks[1][0] = (ImageView) findViewById(R.id.F);
        blocks[1][1] = (ImageView) findViewById(R.id.G);
        blocks[1][2] = (ImageView) findViewById(R.id.H);
        blocks[1][3] = (ImageView) findViewById(R.id.I);
        blocks[1][4] = (ImageView) findViewById(R.id.J);

        blocks[2][0] = (ImageView) findViewById(R.id.K);
        blocks[2][1] = (ImageView) findViewById(R.id.L);
        blocks[2][2] = (ImageView) findViewById(R.id.M);
        blocks[2][3] = (ImageView) findViewById(R.id.N);
        blocks[2][4] = (ImageView) findViewById(R.id.O);

        blocks[3][0] = (ImageView) findViewById(R.id.P);
        blocks[3][1] = (ImageView) findViewById(R.id.Q);
        blocks[3][2] = (ImageView) findViewById(R.id.R);
        blocks[3][3] = (ImageView) findViewById(R.id.S);
        blocks[3][4] = (ImageView) findViewById(R.id.T);

        blocks[4][0] = (ImageView) findViewById(R.id.U);
        blocks[4][1] = (ImageView) findViewById(R.id.V);
        blocks[4][2] = (ImageView) findViewById(R.id.W);
        blocks[4][3] = (ImageView) findViewById(R.id.X);
        blocks[4][4] = (ImageView) findViewById(R.id.Y);

    }

    private void setBoardListening() {
        MainActivity.mSocket.on("board", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    JSONArray warderIndex = messageJson.getJSONArray("warderindex");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // implement code to set block

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setWaitStatus() {
        MainActivity.mSocket.on("status", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                status = args[0].toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playerstaus.setText(status);
                    }
                });
            }
        });
    }

    private void setOnSwipe() {
        board.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                MainActivity.mSocket.on("movedown", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // send movedown to server

                            }
                        });
                    }
                });
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                MainActivity.mSocket.on("moveleft", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // send moveleft to server

                            }
                        });
                    }
                });
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                MainActivity.mSocket.on("moveright", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // send moveright to server

                            }
                        });
                    }
                });
            }

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                MainActivity.mSocket.on("moveup", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // send moveup to server

                            }
                        });
                    }
                });
            }
        });
    }

}
