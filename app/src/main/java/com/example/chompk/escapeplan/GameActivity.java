package com.example.chompk.escapeplan;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    TableLayout board;
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
        board.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            board.setDividerDrawable(getDrawable(R.drawable.border));
        }

        setBoardListening();
        setOnSwipe();
        setWaitStatus();
        setConnectionStatus();
        setCharStatus();

        setTurn();

        btnSurrender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mSocket.emit("req", "leave");
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                GameActivity.this.startActivity(intent);
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mSocket.emit("move", "skip");
            }
        });

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
                    final JSONObject messageJson = new JSONObject(args[0].toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // implement code to set block
                            try {
                                JSONArray prisonerIndex = messageJson.getJSONArray("prisonerindex");
                                JSONArray warderIndex = messageJson.getJSONArray("warderindex");
                                JSONArray tunnelIndex = messageJson.getJSONArray("tunnelindex");
                                JSONArray obstaclesIndex = messageJson.getJSONArray("obstacleindex");
                                System.out.println((int)prisonerIndex.get(0));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setWaitStatus() {
        MainActivity.mSocket.on("waiting", new Emitter.Listener() {
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

    private void setConnectionStatus() {
        MainActivity.mSocket.on("connected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Match found!", Toast.LENGTH_SHORT).show();
                        new CountDownTimer(5000, 10) {
                            public void onTick(long millisUntilFinished) {
                                int sec = (int) (millisUntilFinished/1000);
                                timer.setText("Game starting in "+sec);
                            }
                            @Override
                            public void onFinish() { }
                        }.start();
                    }
                });
            }
        });
    }

    private void setCharStatus() {
        MainActivity.mSocket.on("char", new Emitter.Listener() {
            @Override
            public void call(final Object... mess) {
                runOnUiThread(new Runnable() {
                    String role = mess[0].toString();
                    @Override
                    public void run() {
                        if(role.equals("warder")) {
                            status = "You are warder!";
                        } else if(role.equals("prisoner")) {
                            status = "You are prisoner!";
                        }
                        playerstaus.setText(status);
                    }
                });
            }
        });
    }

    private void setTurn() {
        MainActivity.mSocket.on("turn", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                status = args[0].toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new CountDownTimer(10000, 10) {

                            public void onTick(long millisUntilFinished) {
                                int sec = (int) (millisUntilFinished/1000);
                                int msec = (int) (millisUntilFinished - sec*1000)/10;
                                timer.setText(sec+" : "+msec+" seconds remainding!");
                            }

                            public void onFinish() {
                                timer.setText("Times up!");
                            }
                        }.start();
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
                MainActivity.mSocket.emit("move", "movedown");
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                MainActivity.mSocket.emit("move", "moveleft");
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                MainActivity.mSocket.emit("move", "moveright");
            }

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                MainActivity.mSocket.emit("move", "moveup");
            }
        });
    }

}
