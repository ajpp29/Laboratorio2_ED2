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
import java.math.BigInteger;

public class CifradoSDES extends AppCompatActivity {

    //Variables a utilizar
    Button OpenFile;
    String text;
    Button Cifrar,Descifrar;
    TextView Ruta,Panel,PanelComprimido;
    EditText KeyCifrado;
    int[] key1,key2;
    //String error;
    //String pathdestino =  "/storage/emulated/0/";
    Uri FileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cifradosdes);

        OpenFile=(Button) findViewById(R.id.DocumentoSDes);
        Ruta=(TextView) findViewById(R.id.rutaArchSDes);
        KeyCifrado=(EditText) findViewById(R.id.keySDes);
        Cifrar=(Button) findViewById(R.id.cifSDes);

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

        Cifrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String primarikey=KeyCifrado.getText().toString();
                    int[] prikey=new int[10];

                    for(int i=0;i<10;++i){
                        prikey[i]=Integer.parseInt(String.valueOf(primarikey.charAt(i)));
                    }
                    GenerarLlaves(prikey);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Debe ingresar un numero de 10 digitos",Toast.LENGTH_LONG).show();
                }

                try{
                    Cifrar(text,key1,key2);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Verifique que el archivo no este corrupto",Toast.LENGTH_LONG).show();
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
            Toast.makeText(this,selectedfile.getAuthority(),Toast.LENGTH_LONG).show();


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

    /**
     *Generación de llaves
     */

    private void GenerarLlaves(int[] llave){
        int[] mediallaveizquierda =new int[5];
        int[] mediallavederecha =new int[5];

        //Paso 0 : permutación 10 de la llave
        llave=Permutacion10(llave);

        //Paso 1 : separar la llave en bloques de 5
        for(int i=0;i<llave.length;i++){
            if(i<(llave.length/2))
                mediallaveizquierda[i]=llave[i];
            else
                mediallavederecha[i-(llave.length/2)]=llave[i];
        }

        //Paso 2: left shift una posición
        mediallaveizquierda=LeftShift(mediallaveizquierda,1);
        mediallavederecha=LeftShift(mediallavederecha,1);

        //Paso 3: Unir las dos mitades y formar la primera llave, con P8
        llave=UnirContrasenia(mediallaveizquierda,mediallavederecha);
        key1=Permutacion8(llave);

        //Paso 4: left shift dos posiciones
        mediallaveizquierda=LeftShift(mediallaveizquierda,2);
        mediallavederecha=LeftShift(mediallavederecha,2);

        //Paso 5: Unir las dos mitades y format la segunda llave, con P8
        llave=UnirContrasenia(mediallaveizquierda,mediallavederecha);
        key2=Permutacion8(llave);
    }

    /**
     *Funciones para generar la llave
     */

    private int[] UnirContrasenia (int[] keyleft, int[] keyrigth){
        int[] newkey= new int[keyleft.length+keyrigth.length];

        for(int i=0;i<keyleft.length;++i){
            newkey[i]=keyleft[i];
        }

        for(int i=keyleft.length;i<(keyleft.length+keyrigth.length);++i){
            newkey[i]=keyrigth[i-(keyleft.length)];
        }

        return newkey;

    }

    private int[] LeftShift(int[] cadena,int posiciones){
        int aux1,aux2;
        if(posiciones==1){
            aux1=cadena[0];
            for(int i=posiciones;i<cadena.length;++i){
                cadena[i-1]=cadena[i];
            }
            cadena[cadena.length-1]=aux1;
        }
        else if(posiciones==2) {
            aux1=cadena[0];
            aux2=cadena[1];
            for(int i=posiciones;i<cadena.length;++i){
                cadena[i-2]=cadena[i];
            }
            cadena[cadena.length-2]=aux1;
            cadena[cadena.length-1]=aux2;

        }
        return cadena;
    }

    private int[] Permutacion10(int[] key){
        //Original: 1,2,3,4,5,6,7,8,9,10
        //Permutacion: 2,4,6,8,10,1,3,5,7,9

        int[] original=new int[]{0,1,2,3,4,5,6,7,8,9};
        int[] permutacion=new int[]{1,3,5,7,9,0,2,4,6,8};

        int[] newkey=new int[permutacion.length];

        for(int i=0;i<permutacion.length;++i){
            newkey[original[i]]=key[permutacion[i]];
        }

        return newkey;
    }

    private int[] Permutacion8(int[] key){
        //Original: 1,2,3,4,5,6,7,8,9,10
        //Permutacion: 2,4,6,8,10,1,3,5,7,9

        int[] original=new int[]{0,1,2,3,4,5,6,7,8,9};
        int[] permutacion=new int[]{0,1,4,5,8,9,2,3};

        int[] newkey=new int[permutacion.length];

        for(int i=0;i<permutacion.length;++i){
            newkey[original[i]]=key[permutacion[i]];
        }

        return newkey;
    }
    

    /**
     *Convertir a Hexagesimal, Convertir a Binario
     */

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String ConvertToBinary(String character){
        byte[] b = character.getBytes();
        String hexa=bytesToHex(b);
        String binario= new BigInteger(hexa,16).toString(2);
        return String.format("%8s",binario).replace(' ','0');
    }

    /**
     *Funciones para Cifrar
     */


    public void Cifrar(String texto,int[] k1,int[] k2){
        for(int i=0;i<texto.length();++i){
            String CaracterBinario = ConvertToBinary(String.valueOf(texto.charAt(i)));
            SDes(CaracterBinario,k1,k2);
        }
    }
    public void SDes(String caracter,int[] k1,int[] k2){

    }



}
