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

    ImageView[] blocks = new ImageView[25];
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
        blocks[0] = (ImageView) findViewById(R.id.A);
        blocks[1] = (ImageView) findViewById(R.id.B);
        blocks[2] = (ImageView) findViewById(R.id.C);
        blocks[3] = (ImageView) findViewById(R.id.D);
        blocks[4] = (ImageView) findViewById(R.id.E);

        blocks[5] = (ImageView) findViewById(R.id.F);
        blocks[6] = (ImageView) findViewById(R.id.G);
        blocks[7] = (ImageView) findViewById(R.id.H);
        blocks[8] = (ImageView) findViewById(R.id.I);
        blocks[9] = (ImageView) findViewById(R.id.J);

        blocks[10] = (ImageView) findViewById(R.id.K);
        blocks[11] = (ImageView) findViewById(R.id.L);
        blocks[12] = (ImageView) findViewById(R.id.M);
        blocks[13] = (ImageView) findViewById(R.id.N);
        blocks[14] = (ImageView) findViewById(R.id.O);

        blocks[15] = (ImageView) findViewById(R.id.P);
        blocks[16] = (ImageView) findViewById(R.id.Q);
        blocks[17] = (ImageView) findViewById(R.id.R);
        blocks[18] = (ImageView) findViewById(R.id.S);
        blocks[19] = (ImageView) findViewById(R.id.T);

        blocks[20] = (ImageView) findViewById(R.id.U);
        blocks[21] = (ImageView) findViewById(R.id.V);
        blocks[22] = (ImageView) findViewById(R.id.W);
        blocks[23] = (ImageView) findViewById(R.id.X);
        blocks[24] = (ImageView) findViewById(R.id.Y);

    }

}
