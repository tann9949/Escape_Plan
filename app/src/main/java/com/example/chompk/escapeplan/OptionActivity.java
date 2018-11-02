package com.example.chompk.escapeplan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chompk.escapeplan.Data.ConnectionData;

public class OptionActivity extends AppCompatActivity {

    private Button btnsave;
    private Button btnback;
    private EditText editip;
    private EditText editport;
    private TextView textAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        btnsave = (Button)findViewById(R.id.btnsave);
        btnback = (Button)findViewById(R.id.btnback);
        editip = (EditText) findViewById(R.id.ip);
        editport = (EditText) findViewById(R.id.port);
        textAddress = (TextView) findViewById(R.id.textAddress);
        String status;

        boolean statusok = (boolean) getIntent().getExtras().get("status");
        if(statusok)
            status = "CONNECTED";
        else
            status = "DISCONNECTED";

        textAddress.setText("IP address: "+ConnectionData.getInstance().getIpAddress()+"\nPort: "+ConnectionData.getInstance().getPort()+"\nStatus: "+status);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionActivity.this, MainActivity.class);
                OptionActivity.this.startActivity(intent);
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = editip.getText().toString();
                String port = editport.getText().toString();
                Intent intent = new Intent(OptionActivity.this, MainActivity.class);
                if (port.matches("") || ip.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please fill in all of the spaces", Toast.LENGTH_LONG).show();
                } else {
                    ConnectionData.getInstance().setPort(Integer.parseInt(port));
                    ConnectionData.getInstance().setIpAddress(ip);
                    OptionActivity.this.startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OptionActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
