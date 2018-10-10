package com.example.angel.laboratorio2_ed2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import Cifrado.CifradoZigZag;

public class MainActivity extends AppCompatActivity {

    Button CifrarZigZag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CifrarZigZag=(Button) findViewById(R.id.CifrarZigZag);


    }
}
