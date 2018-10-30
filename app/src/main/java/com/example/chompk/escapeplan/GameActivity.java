package com.example.chompk.escapeplan;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
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
    boolean stopTimer = false;
    TextView playerstaus;
    TableLayout board;
    String status;
    String character;
    String turn;

    JSONArray prisonerIndex;
    JSONArray wardenIndex;
    JSONArray tunnelIndex;
    JSONArray[] obstaclesIndex;

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
                        @TargetApi(Build.VERSION_CODES.M)
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {
                            for(int j=0; j<5; j++) {
                                for(int k=0; k<5; k++) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        blocks[j][k].setForeground(getDrawable(R.drawable.white));
                                    }
                                }
                            }
                            // implement code to set block
                            try {
                                prisonerIndex = messageJson.getJSONArray("prisonerindex");
                                wardenIndex = messageJson.getJSONArray("wardenindex");
                                tunnelIndex = messageJson.getJSONArray("tunnelindex");
                                obstaclesIndex = new JSONArray[messageJson.getJSONArray("obstacleindex").length()];
                                int x[] = new int[5];
                                int y[] = new int[5];
                                for(int i=0; i<obstaclesIndex.length; i++) {
                                    obstaclesIndex[i] = messageJson.getJSONArray("obstacleindex").getJSONArray(i);
                                    x[i] = (int)obstaclesIndex[i].get(0);
                                    y[i] = (int)obstaclesIndex[i].get(1);
                                    if(x[i]>4) x[i] = 4;
                                    if(y[i]>4) y[i] = 4;
                                    if(x[i]<0) x[i] = 0;
                                    if(y[i]<0) y[i] = 0;
                                    System.out.println("x["+i+"] = "+x[i]+" , y["+i+"] = "+y[i]);
                                    blocks[x[i]][y[i]].setForeground(getDrawable(R.drawable.coneicon));
                                }
                                int prisonerx = (int)prisonerIndex.get(0);
                                int prisonery = (int)prisonerIndex.get(1);
                                if(prisonerx>4) prisonerx = 4;
                                if(prisonery>4) prisonery = 4;
                                if(prisonerx<0) prisonerx = 0;
                                if(prisonery<0) prisonery = 0;
                                System.out.println("prisoner: ("+prisonerx+" , "+prisonery+")");
                                blocks[prisonerx][prisonery].setForeground(getDrawable(R.drawable.prisonericon));
                                int wardenx = (int)wardenIndex.get(0);
                                int wardeny = (int)wardenIndex.get(1);
                                if(wardenx>4) wardenx = 4;
                                if(wardeny>4) wardeny = 4;
                                if(wardenx<0) wardenx = 0;
                                if(wardeny<0) wardeny = 0;
                                System.out.println("warden: ("+wardenx+" , "+wardeny+")");
                                blocks[wardenx][wardeny].setForeground(getDrawable(R.drawable.policeicon));
                                int tunnelx = (int)tunnelIndex.get(0);
                                int tunnely = (int)tunnelIndex.get(1);
                                if(tunnelx>4) tunnelx = 4;
                                if(tunnely>4) tunnely = 4;
                                if(tunnelx<0) tunnelx = 0;
                                if(tunnely<0) tunnely = 0;
                                System.out.println("tunnel: ("+tunnelx+" , "+tunnely+")");
                                blocks[tunnelx][tunnely].setForeground(getDrawable(R.drawable.exiticon));
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
                MainActivity.mSocket.emit("ready", "ready");
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
                        if(role.equals("warden")) {
                            status = "You are warden!";
                        } else if(role.equals("prisoner")) {
                            status = "You are prisoner!";
                        }
                        playerstaus.setText(status);
                        character = role;
                    }
                });
            }
        });
    }

    private void setTurn() {
        MainActivity.mSocket.on("turn", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String role = args[0].toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        turn = role;
                        if(character.equals(role)) {
                            new CountDownTimer(10000, 10) {
                                public void onTick(long millisUntilFinished) {
                                    int sec = (int) (millisUntilFinished/1000);
                                    int msec = (int) (millisUntilFinished - sec*1000)/10;
                                    timer.setText(sec+" : "+msec+" seconds remainding!");
                                    if(stopTimer)
                                        timer.setText("opponent's turn");
                                }

                                public void onFinish() {
                                    timer.setText("opponent's turn");
                                    MainActivity.mSocket.emit("move", "skip");
                                }
                            }.start();
                        } else {
                            timer.setText("opponent's turn");
                        }

                    }
                });
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnSwipe() {
        board.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                if(character.equals(turn)) {
                    MainActivity.mSocket.emit("move", "movedown");
                    stopTimer = true;
                }
                System.out.println("moving down");
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                if(character.equals(turn)) {
                    MainActivity.mSocket.emit("move", "moveleft");
                    stopTimer = true;
                }
                System.out.println("moving left");
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                if(character.equals(turn)) {
                    MainActivity.mSocket.emit("move", "moveright");
                    stopTimer = true;
                }
                System.out.println("moving right");
            }

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                if(character.equals(turn)) {
                    MainActivity.mSocket.emit("move", "moveup");
                    stopTimer = true;
                }
                System.out.println("moving up");
            }
        });
    }

}
