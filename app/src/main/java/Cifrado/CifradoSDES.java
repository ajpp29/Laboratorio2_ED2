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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;

public class CifradoSDES extends AppCompatActivity {

    //Variables a utilizar
    Button OpenFile;
    String text;
    Button Cifrar,Descifrar;
    TextView Ruta,Panel,PanelComprimido;
    EditText KeyCifrado;
    int[] key1,key2;
    String[][] matrizS0,matrizS1;
    //String error;
    String pathdestino =  "/storage/emulated/0/";
    Uri FileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cifradosdes);

        OpenFile=(Button) findViewById(R.id.DocumentoSDes);
        Ruta=(TextView) findViewById(R.id.rutaArchSDes);
        KeyCifrado=(EditText) findViewById(R.id.keySDes);
        Cifrar=(Button) findViewById(R.id.cifSDes);
        Descifrar=(Button) findViewById(R.id.descifSDes);
        Panel=(EditText) findViewById(R.id.TextoDescifradoSDes);

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
                    LlenarMatrizes();
                    GenerarLlaves(prikey);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Debe ingresar un numero de 10 digitos",Toast.LENGTH_LONG).show();
                }
                //////////////
                try{
                    Cifrar(text,key1,key2);
                    KeyCifrado.setText("");
                    Ruta.setText("");
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Verifique que el archivo no este corrupto",Toast.LENGTH_LONG).show();
                }
            }
        });

        Descifrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String primarikey=KeyCifrado.getText().toString();
                    int[] prikey=new int[10];

                    for(int i=0;i<10;++i){
                        prikey[i]=Integer.parseInt(String.valueOf(primarikey.charAt(i)));
                    }
                    LlenarMatrizes();
                    GenerarLlaves(prikey);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Debe ingresar un numero de 10 digitos",Toast.LENGTH_LONG).show();
                }
                //////////////
                try{
                    Panel.setText(Descifrar(text,key2,key1));
                    KeyCifrado.setText("");
                    Ruta.setText("");
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
        int[] mediallaveizquierda =new int[llave.length/2];
        int[] mediallavederecha =new int[llave.length/2];

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
        //Permutacion: 1,2,5,6,9,10,3,4

        int[] original=new int[]{0,1,2,3,4,5,6,7,8,9};
        int[] permutacion=new int[]{0,1,4,5,8,9,2,3};

        int[] newkey=new int[permutacion.length];

        for(int i=0;i<permutacion.length;++i){
            newkey[original[i]]=key[permutacion[i]];
        }

        return newkey;
    }

    private int[] PermutacionInicial(int[] key,int orden){
        //Original: 1,2,3,4,5,6,7,8
        //Permutacion: 8,5,2,7,4,1,6,3

        int[] original=new int[]{0,1,2,3,4,5,6,7};
        int[] permutacion=new int[]{7,4,1,6,3,0,5,2};

        int[] newkey=new int[permutacion.length];

        if(orden==0) {
            for (int i = 0; i < permutacion.length; ++i) {
                newkey[original[i]] = key[permutacion[i]];
            }
        }
        else if(orden==1){
            for (int i = 0; i < original.length; ++i) {
                newkey[permutacion[i]] = key[original[i]];
            }
        }

        return newkey;
    }

    private int[] PermutacionExpandida(int[] key){
        //Original: 1,2,3,4
        //Permutacion: 2,4,3,1,3,4,1,2

        int[] original=new int[]{0,1,2,3};
        int[] permutacion=new int[]{1,3,2,0,2,3,0,1};

        int[] newkey=new int[permutacion.length];

        for(int i=0;i<original.length;++i){
            newkey[original[i]]=key[permutacion[i]];
            newkey[original[i]+(original.length)]=key[permutacion[i+(original.length)]];
        }

        return newkey;
    }

    private int[] Permutacion4(int[] key){
        //Original: 1,2,3,4
        //Permutacion: 4,1,3,2

        int[] original=new int[]{0,1,2,3,4};
        int[] permutacion=new int[]{3,0,2,1};

        int[] newkey=new int[permutacion.length];

        for(int i=0;i<permutacion.length;++i){
            newkey[original[i]]=key[permutacion[i]];
        }

        return newkey;
    }

    private int[] Xor(int[] arreglo,int[] key){
        int[] newresult=new int[key.length];

        for(int i=0;i<key.length;++i){
            if(arreglo[i]==key[i])
                newresult[i]=0;
            else
                newresult[i]=1;
        }

        return newresult;
    }

    private void LlenarMatrizes(){
        matrizS0= new String[][]{{"01","00","11","10"},{"11","10","01","00"},{"00","10","01","11"},{"11","01","11","10"}};
        matrizS1= new String[][]{{"00","01","10","11"},{"10","00","01","11"},{"11","00","01","00"},{"10","01","00","11"}};
    }

    private int[] ValoresMatriz(int[] cadena, int noMatriz){
        int fila=ReturnValue(cadena[0],cadena[3]);
        int columna=ReturnValue(cadena[1],cadena[2]);
        String aux;

        int[] resultado=new int[cadena.length/2];

        if(noMatriz==0) {
            aux = matrizS0[fila][columna];
            resultado[0]=Integer.parseInt(String.valueOf(String.valueOf(aux).charAt(0)));
            resultado[1]=Integer.parseInt(String.valueOf(String.valueOf(aux).charAt(1)));

        }
        else if(noMatriz==1){
            aux = matrizS1[fila][columna];
            resultado[0]=Integer.parseInt(String.valueOf(String.valueOf(aux).charAt(0)));
            resultado[1]=Integer.parseInt(String.valueOf(String.valueOf(aux).charAt(1)));
        }

        return resultado;
    }

    private int ReturnValue(int n1,int n2){
        int resultado=0;
        if(n1==0&n2==0)
            resultado=0;
        else if(n1==0&n2==1)
            resultado=1;
        else if(n1==1&n2==0)
            resultado=2;
        else
            resultado=3;

        return resultado;
    }

    private int[] DarFormato(int[] cadena){
        int[] newcadena= new int[8];

        for(int i=0;i<newcadena.length;++i){
            newcadena[i]=cadena[cadena.length-(newcadena.length-i)];
        }
        return newcadena;
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

    public static int binaryToDecimal(int number) {
        int decimal = 0;
        int binary = number;
        int power = 0;

        while (binary != 0) {
            int lastDigit = binary % 10;
            decimal += lastDigit * Math.pow(2, power);
            power++;
            binary = binary / 10;
        }
        return decimal;
    }

    /**
     *Funciones para Cifrar
     */

    private void WriteText(String caractercifrado){
        try {

            File file = new File(FileUri.getPath());
            String nombreArchivo = file.getName().replace('.','_');
            File newfile=new File(pathdestino,nombreArchivo+".scif");
            if(!newfile.exists()){
                newfile.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(newfile);
            OutputStreamWriter writeArchivo=new OutputStreamWriter(fileOutputStream);
            BufferedWriter bufferedWriter=new BufferedWriter(writeArchivo);
            bufferedWriter.append(caractercifrado);
            bufferedWriter.flush();
            bufferedWriter.close();

        }catch(IOException e){

        }
    }

    public void Cifrar(String texto,int[] k1,int[] k2){
        for(int i=0;i<texto.length();++i){
            String CaracterBinario = ConvertToBinary(String.valueOf(texto.charAt(i)));
            int[] CharBinario= new int[CaracterBinario.length()];
            for(int j=0;j<CaracterBinario.length();++j){
                CharBinario[j]=Integer.parseInt(String.valueOf(CaracterBinario.charAt(j)));
            }
            if(CharBinario.length>8)
                CharBinario=DarFormato(CharBinario);
            
            String binario=SDes(CharBinario,k1,k2);
            int decimal=binaryToDecimal(Integer.parseInt(binario));
            String caractercif=String.valueOf(Character.toChars(decimal));
            WriteText(caractercif);
        }
    }

    public String Descifrar(String texto,int[] k1, int[] k2){
        StringBuilder textodescifrado=new StringBuilder();

        for(int i=0;i<texto.length();++i){
            String CaracterBinario = ConvertToBinary(String.valueOf(texto.charAt(i)));
            int[] CharBinario= new int[CaracterBinario.length()];
            for(int j=0;j<CaracterBinario.length();++j){
                CharBinario[j]=Integer.parseInt(String.valueOf(CaracterBinario.charAt(j)));
            }
            if(CharBinario.length>8)
                CharBinario=DarFormato(CharBinario);


            String binario=SDes(CharBinario,k1,k2);
            int decimal=binaryToDecimal(Integer.parseInt(binario));
            String caracterdescif=String.valueOf(Character.toChars(decimal));
            textodescifrado.append(caracterdescif);
        }

        return textodescifrado.toString();
    }

    public String SDes(int[] charbinario,int[] k1,int[] k2){
        int[] Partleft =new int[charbinario.length/2];
        int[] PartRight =new int[charbinario.length/2];

        //Paso 0 : permutación inicial del caracter
        charbinario=PermutacionInicial(charbinario,0);

        //Paso 1 : separar la llave en bloques de 4
        for(int i=0;i<charbinario.length;i++){
            if(i<(charbinario.length/2))
                Partleft[i]=charbinario[i];
            else
                PartRight[i-(charbinario.length/2)]=charbinario[i];
        }

        //Se crea una copia para trabajar con ellas
        int[] Copileft=Partleft;
        int[] CopiRight=PartRight;

        //Paso 2: Expandir y permutar
        CopiRight=PermutacionExpandida(CopiRight);

        //Paso 3: Hacer un Xor con la llave 1
        CopiRight=Xor(CopiRight,k1);

        //Paso 4: Separar el resultado en bloques de 4
        int[] CopiRLeft =new int[CopiRight.length/2];
        int[] CopiRRight =new int[CopiRight.length/2];

        for(int i=0;i<CopiRight.length;i++){
            if(i<(CopiRight.length/2))
                CopiRLeft[i]=CopiRight[i];
            else
                CopiRRight[i-(CopiRight.length/2)]=CopiRight[i];
        }

        //Paso 5: Utilizar las matrices
        CopiRLeft=ValoresMatriz(CopiRLeft,0);
        CopiRRight=ValoresMatriz(CopiRRight,1);

        //Paso 6: Unir los bits y hacer P4
        CopiRight=UnirContrasenia(CopiRLeft,CopiRRight);
        CopiRight=Permutacion4(CopiRight);

        //Paso 7: Se hace un Xor con el lado izquierdo
        Partleft=Xor(Copileft,CopiRight);

        //Se Igualan los componenetes
        Copileft=Partleft;
        CopiRight=PartRight;

        //Se cambia derecha izquierda
        Partleft=CopiRight;
        PartRight=Copileft;

        //Se vuelve a igualar los componentes
        Copileft=Partleft;
        CopiRight=PartRight;

        /////////////////////////////////////
        //SE REPITEN LOS PASOS PRIMARIOS
        //////////////////////////////////////

        //Paso 2: Expandir y permutar
        CopiRight=PermutacionExpandida(CopiRight);

        //Paso 3: Hacer un Xor con la llave 2
        CopiRight=Xor(CopiRight,k2);

        //Paso 4: Separar el resultado en bloques de 4
        CopiRLeft =new int[CopiRight.length/2];
        CopiRRight =new int[CopiRight.length/2];

        for(int i=0;i<CopiRight.length;i++){
            if(i<(CopiRight.length/2))
                CopiRLeft[i]=CopiRight[i];
            else
                CopiRRight[i-(CopiRight.length/2)]=CopiRight[i];
        }

        //Paso 5: Utilizar las matrices
        CopiRLeft=ValoresMatriz(CopiRLeft,0);
        CopiRRight=ValoresMatriz(CopiRRight,1);

        //Paso 6: Unir los bits y hacer P4
        CopiRight=UnirContrasenia(CopiRLeft,CopiRRight);
        CopiRight=Permutacion4(CopiRight);

        //Paso 7: Se hace un Xor con el lado izquierdo
        Partleft=Xor(Copileft,CopiRight);

        //Paso 9: Se juntan ambas partes
        charbinario=UnirContrasenia(Partleft,PartRight);

        //Paso 10: Aplicar el inverso de la permutacion inicial
        charbinario=PermutacionInicial(charbinario,1);

        StringBuilder binario=new StringBuilder();

        for(int i=0;i<charbinario.length;++i){
            binario.append(charbinario[i]);
        }

        return binario.toString();

    }



}
