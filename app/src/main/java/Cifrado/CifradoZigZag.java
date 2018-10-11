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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class CifradoZigZag extends AppCompatActivity {

    //Variables a utilizar
    Button OpenFile;
    String text;
    Button Cifrar,Descifrar;
    TextView Ruta,Panel,PanelComprimido;
    EditText KeyCifrado;
    String error;
    String pathdestino =  "/storage/emulated/0/";
    Uri FileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cifradozigzag);

        OpenFile=(Button) findViewById(R.id.DocumentoCifZigZag);
        Ruta=(TextView) findViewById(R.id.rutaArchZigZag);
        Cifrar=(Button) findViewById(R.id.Cifzigzag);
        Descifrar=(Button) findViewById(R.id.Descifzigzag);
        KeyCifrado=(EditText) findViewById(R.id.keyzigzag);
        Panel=(EditText) findViewById(R.id.TextoDescifrado);

        OpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent()
                        .addCategory(Intent.CATEGORY_DEFAULT)
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Open File"),123);

            }
        });

        Cifrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int keyzigzag=Integer.parseInt(KeyCifrado.getText().toString());
                    Cifrar(text, keyzigzag);
                    KeyCifrado.setText("");
                }catch (Exception e){

                }
            }
        });

        Descifrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int keyzigzag=Integer.parseInt(KeyCifrado.getText().toString());
                    Descifrar(text, keyzigzag);
                    KeyCifrado.setText("");
                }catch (Exception e){

                }
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

    public void Cifrar(String cadena, int keyzigzag){
        //Toast.makeText(this,String.valueOf(cadena.length()),Toast.LENGTH_LONG).show();
        //Toast.makeText(this,String.valueOf(keyzigzag),Toast.LENGTH_LONG).show();
        StringBuilder stringBuilder=new StringBuilder();
        String texto;

        int niveles=keyzigzag;
        int tamanioOla=(niveles*2)-2;
        double division=(double) cadena.length()/tamanioOla;
        int NoOlas= (int) Math.ceil(division);
        int letrasFaltantes=(NoOlas*tamanioOla)-cadena.length();
        int tamanioBloque=2*NoOlas;

        stringBuilder.append(cadena);

        for(int i=0;i<letrasFaltantes;++i){
            stringBuilder.append(" ");
        }
        texto=stringBuilder.toString();

        String[][] matriz=new String[tamanioBloque][niveles];

        int contador=0;

        for(int i=0;i<NoOlas;++i){
            for(int columna=0;columna<niveles;++columna){
                if(columna<niveles-1) {
                    matriz[0+(2*i)][columna] = String.valueOf(texto.charAt(contador));
                    contador++;
                }else{
                    matriz[0+(2*i)][columna] =null;
                }
            }

            for(int columna=niveles-1;columna>=0;--columna){
                if(columna>0) {
                    matriz[1+(2*i)][columna] = String.valueOf(texto.charAt(contador));
                    contador++;
                }else{
                    matriz[1+(2*i)][columna] =null;
                }
            }
        }

        stringBuilder=new StringBuilder();
        for(int columna=0;columna<niveles;++columna){
            for(int fila=0;fila<tamanioBloque;++fila){
                String auxiliar=matriz[fila][columna];
                if(auxiliar!=null)
                    stringBuilder.append(matriz[fila][columna]);
            }
        }

        String textocifrado=stringBuilder.toString();
        Toast.makeText(this,textocifrado,Toast.LENGTH_LONG).show();



        try {

            File file = new File(FileUri.getPath());
            String nombreArchivo = file.getName().replace('.','_');
            File newfile=new File(pathdestino,nombreArchivo+".cif");
            if(!newfile.exists()){
                newfile.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(newfile);
            OutputStreamWriter writeArchivo=new OutputStreamWriter(fileOutputStream);
            BufferedWriter bufferedWriter=new BufferedWriter(writeArchivo);
            bufferedWriter.write(textocifrado);
            bufferedWriter.flush();
            bufferedWriter.close();

        }catch(IOException e){

        }
    }



    public void Descifrar(String cadena, int keyzigzag){
        StringBuilder stringBuilder=new StringBuilder();
        String texto;

        int niveles=keyzigzag;
        int tamanioOla=(niveles*2)-2;
        double division=(double) cadena.length()/tamanioOla;
        int NoOlas= (int) Math.ceil(division);
        int letrasFaltantes=(NoOlas*tamanioOla)-cadena.length();
        int tamanioBloque=2*NoOlas;

        stringBuilder.append(cadena);

        for(int i=0;i<letrasFaltantes;++i){
            stringBuilder.append(" ");
        }
        texto=stringBuilder.toString();

        String[][] matriz=new String[tamanioBloque][niveles-2];

        int contador=NoOlas;

        for(int columna=0;columna<niveles-2;++columna){
            for(int fila=0;fila<tamanioBloque;++fila){
                matriz[fila][columna]=String.valueOf(texto.charAt(contador));
                contador++;
            }
        }

        stringBuilder=new StringBuilder();

        for(int i=0;i<NoOlas;++i){
            stringBuilder.append(String.valueOf(texto.charAt(i)));
            for(int columna=0;columna<niveles-2;++columna) {
                //matriz[0+(2*i)][columna] = String.valueOf(texto.charAt(contador));
                stringBuilder.append(matriz[0 + (2 * i)][columna]);
            }
            stringBuilder.append(String.valueOf(texto.charAt(texto.length()-(NoOlas-i))));

            for(int columna=niveles-3;columna>=0;--columna) {

                //matriz[1+(2*i)][columna] = String.valueOf(texto.charAt(contador));
                stringBuilder.append(matriz[1 + (2 * i)][columna]);
            }
        }

        String textodescifrado=stringBuilder.toString();
        Toast.makeText(this,textodescifrado,Toast.LENGTH_LONG).show();
        Panel.setText(textodescifrado);

    }
}
