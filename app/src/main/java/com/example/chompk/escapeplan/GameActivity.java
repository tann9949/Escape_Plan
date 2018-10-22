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

public class GameActivity extends AppCompatActivity {

    ImageView[][] blocks = new ImageView[5][5];
    Button btnSkip;
    Button btnSurrender;
    TextView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_game);

        initializeImageView();
        btnSkip = (Button)findViewById(R.id.btnskip);
        btnSurrender = (Button)findViewById(R.id.btnsurrender);
        timer = (TextView)findViewById(R.id.timer);


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

}
