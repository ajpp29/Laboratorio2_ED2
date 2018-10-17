package Cifrado;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.angel.laboratorio2_ed2.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CifradoSDES extends AppCompatActivity {

    //Variables a utilizar
    Button OpenFile;
    String text;
    //Button Cifrar,Descifrar;
    TextView Ruta,Panel,PanelComprimido;
    //EditText KeyCifrado;
    //String error;
    //String pathdestino =  "/storage/emulated/0/";
    Uri FileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cifradosdes);

        OpenFile=(Button) findViewById(R.id.DocumentoSDes);
        Ruta=(TextView) findViewById(R.id.rutaArchSDes);

        OpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent()
                        .addCategory(Intent.CATEGORY_DEFAULT)
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Open File"),123);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==123&& resultCode==RESULT_OK){
            Uri selectedfile=data.getData();
            FileUri=selectedfile;
            //Toast.makeText(this,selectedfile.toString(),Toast.LENGTH_LONG).show();
            //Toast.makeText(this,selectedfile.getPath(),Toast.LENGTH_LONG).show();
            //Toast.makeText(this,selectedfile.getAuthority(),Toast.LENGTH_LONG).show();


            try{
                text=readTextFromUri(selectedfile);
                Ruta.setText(FileUri.getPath());
            }catch (IOException e){
                Toast.makeText(this,"Hubo un error al obtener el texto del archivo",Toast.LENGTH_LONG).show();
            }

        }
    }

    private String readTextFromUri(Uri uri) throws IOException{
        InputStream inputStream=getContentResolver().openInputStream(uri);
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));


        StringBuilder stringBuilder=new StringBuilder();
        String linea;
        linea=reader.readLine();
        while(linea!=null){
            stringBuilder.append(linea);
            linea=reader.readLine();
            if(linea!=null)
                stringBuilder.append(" ");
        }
        inputStream.close();
        reader.close();

        return stringBuilder.toString();
    }

    public void Cifrar(Character character){
        
    }
}
