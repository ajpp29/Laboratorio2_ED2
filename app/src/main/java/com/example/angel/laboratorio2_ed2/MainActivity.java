package com.example.angel.laboratorio2_ed2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import Cifrado.CifradoZigZag;

public class MainActivity extends AppCompatActivity {

    Button ShowZigZag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShowZigZag=(Button) findViewById(R.id.CifrarZigZag);

        ShowZigZag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent zigzag =new Intent(getApplicationContext(),CifradoZigZag.class);
                startActivity(zigzag);
            }
        });


    }
}
