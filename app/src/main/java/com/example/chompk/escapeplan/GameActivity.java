package com.example.chompk.escapeplan;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
    String character;
    String turn;
    CountDownTimer cdt;
    String role;
    String playername;

    JSONArray prisonerIndex;
    JSONArray wardenIndex;
    JSONArray tunnelIndex;
    JSONArray[] obstaclesIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_game);

        initializeImageView();
        btnSkip = (Button) findViewById(R.id.btnskip);
        btnSurrender = (Button) findViewById(R.id.btnsurrender);
        timer = (TextView) findViewById(R.id.timer);
        playerstaus = (TextView) findViewById(R.id.playerstatus);
        board = findViewById(R.id.board);
        board.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            board.setDividerDrawable(getDrawable(R.drawable.border));
        }

        playername = getIntent().getExtras().getString("playername");

        setBoardListening();
        setOnSwipe();
        setWaitStatus();
        setConnectionStatus();
        setCharStatus();
        setOnEnd();
        setTurn();
        setOnInvalid();
        setOnClear();
        onFull();
        onBroadcast();

        btnSurrender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mSocket.emit("req", "leave");
                System.out.println("Surrender pressed");
                System.out.println("Emitting event: \"req\", arg: \"leave\" (75)");
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                GameActivity.this.startActivity(intent);
                if (cdt != null)
                    cdt.cancel();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (character == null || character.equals(""))
                    return;
                System.out.println("Skip pressed");
                System.out.println("Emitting event: \"move\", arg: \"skip\" (89)");
                MainActivity.mSocket.emit("move", "skip");
                cdt.cancel();
            }
        });

        if (!MainActivity.mSocket.connected())
            playerstaus.setText("Not connected to server");
        MainActivity.mSocket.emit("req", "join");
        System.out.println("Emitting event: \"req\", arg: \"join\" (95)");
        MainActivity.mSocket.emit("name", playername);
        System.out.println("Emitting event: \"name\", arg: " + playername + " (95)");
        System.out.println("Finished onCreate()");

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(GameActivity.this, "Press surrender to go back", Toast.LENGTH_SHORT).show();
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
                    if (cdt != null)
                        cdt.cancel();
                    JSONObject messageJson = new JSONObject(args[0].toString());
                    prisonerIndex = messageJson.getJSONArray("prisonerindex");
                    wardenIndex = messageJson.getJSONArray("wardenindex");
                    tunnelIndex = messageJson.getJSONArray("tunnelindex");
                    obstaclesIndex = new JSONArray[messageJson.getJSONArray("obstacleindex").length()];
                    for (int i = 0; i < obstaclesIndex.length; i++) {
                        obstaclesIndex[i] = messageJson.getJSONArray("obstacleindex").getJSONArray(i);
                    }
                    runOnUiThread(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {
                            System.out.println("Received on event: \"board\"");
                            for (int j = 0; j < 5; j++) {
                                for (int k = 0; k < 5; k++) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        blocks[j][k].setForeground(getDrawable(R.drawable.white));
                                    }
                                }
                            }
                            // implement code to set block
                            try {
                                int x[] = new int[5];
                                int y[] = new int[5];
                                for (int i = 0; i < obstaclesIndex.length; i++) {
                                    System.out.println("Obstales length: " + obstaclesIndex.length);
                                    for (int j = 0; j < obstaclesIndex.length; j++) {
                                        System.out.println("obs index " + j + " = " + obstaclesIndex[j]);
                                    }
                                    x[i] = (int) obstaclesIndex[i].get(0);
                                    y[i] = (int) obstaclesIndex[i].get(1);
                                    if (x[i] > 4) x[i] = 4;
                                    if (y[i] > 4) y[i] = 4;
                                    if (x[i] < 0) x[i] = 0;
                                    if (y[i] < 0) y[i] = 0;
                                    System.out.println("x[" + i + "] = " + x[i] + " , y[" + i + "] = " + y[i]);
                                    blocks[x[i]][y[i]].setForeground(getDrawable(R.drawable.coneicon));
                                }
                                int prisonerx = (int) prisonerIndex.get(0);
                                int prisonery = (int) prisonerIndex.get(1);
                                if (prisonerx > 4) prisonerx = 4;
                                if (prisonery > 4) prisonery = 4;
                                if (prisonerx < 0) prisonerx = 0;
                                if (prisonery < 0) prisonery = 0;
                                System.out.println("prisoner: (" + prisonerx + " , " + prisonery + ")");
                                blocks[prisonerx][prisonery].setForeground(getDrawable(R.drawable.prisonericon));
                                int wardenx = (int) wardenIndex.get(0);
                                int wardeny = (int) wardenIndex.get(1);
                                if (wardenx > 4) wardenx = 4;
                                if (wardeny > 4) wardeny = 4;
                                if (wardenx < 0) wardenx = 0;
                                if (wardeny < 0) wardeny = 0;
                                System.out.println("warden: (" + wardenx + " , " + wardeny + ")");
                                blocks[wardenx][wardeny].setForeground(getDrawable(R.drawable.policeicon));
                                int tunnelx = (int) tunnelIndex.get(0);
                                int tunnely = (int) tunnelIndex.get(1);
                                if (tunnelx > 4) tunnelx = 4;
                                if (tunnely > 4) tunnely = 4;
                                if (tunnelx < 0) tunnelx = 0;
                                if (tunnely < 0) tunnely = 0;
                                System.out.println("tunnel: (" + tunnelx + " , " + tunnely + ")");
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
                System.out.println("Received on event: \"waiting\"");
                status = args[0].toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cdt != null)
                            cdt.cancel();
                        timer.setText("00:00 seconds remainding!");
                        playerstaus.setText(status);
                    }
                });
            }
        });
    }

    private void setConnectionStatus() {
        MainActivity.mSocket.on("start", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cdt != null)
                            cdt.cancel();
                        System.out.println("Received on event: \"start\"");
                        Toast.makeText(getApplicationContext(), "Match found!", Toast.LENGTH_SHORT).show();
                        cdt = new CountDownTimer(5000, 10) {
                            public void onTick(long millisUntilFinished) {
                                int sec = (int) (millisUntilFinished / 1000);
                                timer.setText("Game starting in " + sec);
                            }

                            @Override
                            public void onFinish() {
                                System.out.println("Emitting event: \"ready\", arg: \"ready to play\" (245)");
                                MainActivity.mSocket.emit("ready", "ready to play");
                            }
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
                role = mess[0].toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Received on event: \"char\", args: \"" + mess.toString() + "\"");
                        if (role.equals("warden")) {
                            status = "You are warden!";
                        } else if (role.equals("prisoner")) {
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
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cdt != null)
                            cdt.cancel();
                        String role = args[0].toString();
                        System.out.println("Received on event: \"start\"");
                        turn = role;
                        System.out.println("You are " + character);
                        System.out.println("current turn is " + turn + "'s turn");
                        if(character == null) {
                            return;
                        }
                        System.out.println(character.equals(turn));
                        if (character.equals(turn)) {
                            cdt = new CountDownTimer(10000, 10) {
                                public void onTick(long millisUntilFinished) {
                                    int sec = (int) (millisUntilFinished / 1000);
                                    int msec = (int) (millisUntilFinished - sec * 1000) / 10;
                                    timer.setText(sec + " : " + msec + " seconds remainding!");
                                }

                                public void onFinish() {
                                    timer.setText("Time's up");
                                    System.out.println("Time out");
                                    System.out.println("Emitting event: \"move\", arg: \"skip\" (293)");
                                    MainActivity.mSocket.emit("move", "skip");
                                }
                            }.start();
                        } else {
                            System.out.println("character != turn");
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
                if (character.equals(turn)) {
                    System.out.println("moving down");
                    System.out.println("Emitting event: \"move\", arg: \"movedown\" (316)");
                    MainActivity.mSocket.emit("move", "movedown");
//                    cdt.cancel();
                }
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                if (character.equals(turn)) {
                    System.out.println("moving left");
                    System.out.println("Emitting event: \"move\", arg: \"moveleft\" (327)");
                    MainActivity.mSocket.emit("move", "moveleft");
//                    cdt.cancel();
                }
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                if (character.equals(turn)) {
                    System.out.println("moving right");
                    System.out.println("Emitting event: \"move\", arg: \"moveright\" (338)");
                    MainActivity.mSocket.emit("move", "moveright");
//                    cdt.cancel();
                }
            }

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                if (character.equals(turn)) {
                    System.out.println("moving up");
                    System.out.println("Emitting event: \"move\", arg: \"moveup\" (349)");
                    MainActivity.mSocket.emit("move", "moveup");
//                    cdt.cancel();
                }
            }
        });
    }

    private void setOnEnd() {
        MainActivity.mSocket.on("winner", new Emitter.Listener() {
            String[] player1;
            String[] player2;
            String winner;
            String[] you;
            String[] opponent;
            String[] name;
            String endStatus;
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(cdt != null)
                            cdt.cancel();
                        System.out.println("Received on event: \"winner\", args: \"" + args[0].toString() + "\"");
                        try {
                            JSONObject messageJson = new JSONObject(args[0].toString());
                            JSONArray role = messageJson.getJSONArray("roles");
                            JSONArray points = messageJson.getJSONArray("points");
                            JSONArray names = messageJson.getJSONArray("name");
                            winner = messageJson.getString("winner");
                            name = new String[]{names.getString(0), names.getString(1)};
                            player1 = new String[]{role.getString(0), points.getString(0), names.getString(0)};
                            player2 = new String[]{role.getString(1), points.getString(1), names.getString(1)};
                            if (character.equals(player1[0])) {
                                you = player1;
                                opponent = player2;
                                endStatus = "You lose!";
                            } else if (character.equals(player2[0])) {
                                you = player2;
                                opponent = player1;
                                endStatus = "You win!";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (winner.equals("prisoner")) {
                            timer.setText("prisoner wins the round!");
                        } else {
                            timer.setText("warden wins the round!");
                        }
                        character = "";

                        showDialog(GameActivity.this, endStatus, "Your score ("+you[2]+") : " + you[1] + "\nOpponent score ("+opponent[2]+") : " + opponent[1]);
                    }
                });
            }
        });
    }

    private void setOnInvalid() {
        MainActivity.mSocket.on("err", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Received on event: \"err\"");
                        Toast.makeText(getApplicationContext(), "Invalid Move!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setOnClear() {
        MainActivity.mSocket.on("clear", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Received on event: \"clear\"");
                        for (int j = 0; j < 5; j++) {
                            for (int k = 0; k < 5; k++) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    blocks[j][k].setForeground(getDrawable(R.drawable.white));
                                }
                            }
                        }
                        Toast.makeText(getApplicationContext(), "opponent leave the game", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    private void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("Rematch", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.mSocket.emit("rematch");
                cdt.cancel();
            }
        });
        builder.setNegativeButton("Surrender", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.mSocket.emit("req", "leave");
                cdt.cancel();
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void onFull() {
        MainActivity.mSocket.on("full", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Received on event: \"full\"");
                        Toast.makeText(GameActivity.this, "Room is currently full, Please wait", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GameActivity.this, MainActivity.class);
                        MainActivity.mSocket.disconnect();
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void onBroadcast() {
        MainActivity.mSocket.on("broadcast", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String serverMess = args[0].toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GameActivity.this, "Boardcast from server:\n"+serverMess, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
