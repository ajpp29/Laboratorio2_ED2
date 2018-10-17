package com.example.angel.laboratorio2_ed2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import Cifrado.CifradoSDES;
import Cifrado.CifradoZigZag;

public class MainActivity extends AppCompatActivity {

    Button ShowZigZag,ShowSDes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShowZigZag=(Button) findViewById(R.id.CifrarZigZag);
        ShowSDes=(Button) findViewById(R.id.CifrarSDes);

        ShowZigZag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent zigzag =new Intent(getApplicationContext(),CifradoZigZag.class);
                startActivity(zigzag);
            }
        });

        ShowSDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s_des = new Intent(getApplicationContext(), CifradoSDES.class);
                startActivity(s_des);
            }
        });


    }
}
