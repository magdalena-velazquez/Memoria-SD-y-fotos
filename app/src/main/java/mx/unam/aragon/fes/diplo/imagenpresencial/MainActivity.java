package mx.unam.aragon.fes.diplo.imagenpresencial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private static final String TEXTARCH= "notas.txt";
    boolean leerSD=false;
    File ruta_sd_global= Environment.getExternalStorageDirectory();

    EditText et;
    TextView tw;
    Button bg,bl;
    Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        et=(EditText) findViewById(R.id.editText);
        tw= (TextView) findViewById(R.id.textView);
        bg= (Button) findViewById(R.id.buttonGuardar);
        bl=(Button) findViewById(R.id.buttonLeer);
        aSwitch=(Switch) findViewById(R.id.switch1);

        leerSD=aSwitch.isChecked();
        cambioBotones();

        bl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(leerSD){
                    et.setText(leerSD());
                }else {
                    et.setText(leerMI());
                }
            }
        });

        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean escrito=false;
                String msg="",msgStatus="";
                if (leerSD){
                    escrito=escribirSD(et.getText().toString());
                    msg=ruta_sd_global.getAbsolutePath()+"/"+TEXTARCH;
                }else {
                    escrito= escribirMI(et.getText().toString());
                    msg="Memoria Interna/"+TEXTARCH;
                }
                if (escrito){
                    msgStatus="Archivo guardado con exito en \n"+msg;
                }else {
                    msgStatus="Error al guardar el archivo en \n"+msg;
                }
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                leerSD=isChecked;
                cambioBotones();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id==R.id.action_picture){
            Intent i=new Intent(this,PictureActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public  String leerMI(){
        String[]archivos= fileList();
        String texto="";
        if (existe(archivos,TEXTARCH)){
            try{
                InputStreamReader archivo= new InputStreamReader(openFileInput(TEXTARCH));
                BufferedReader br =new BufferedReader(archivo);
                String linea=br.readLine();
                while (linea!=null){
                    texto += linea+ "\n";
                    linea= br.readLine();
                }
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return  texto;
    }

    private boolean escribirMI(String datos){
        boolean escrito=false;
        try {
            FileOutputStream file=openFileOutput(TEXTARCH, Context.MODE_APPEND);
            file.write(datos.getBytes());
            file.close();
            escrito=true;
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return escrito;
    }

    private  String leerSD(){
        String texto="";
        try{
            File file=new File(getExternalFilesDir(ruta_sd_global.getAbsolutePath()),TEXTARCH);
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String linea= br.readLine();
            while (linea!=null){
                texto+= linea+"\n";
                linea= br.readLine();
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return  texto;
    }

    private boolean escribirSD(String datos){
        boolean escrito=false;
        boolean sdDisponible=false;
        boolean sdEscritura=false;
        String estadoSD=Environment.getExternalStorageState();

        Toast.makeText(getApplicationContext(),estadoSD,Toast.LENGTH_SHORT).show();
        if (estadoSD.equals(Environment.MEDIA_MOUNTED)){
            sdDisponible=true;
            sdEscritura=true;
        }else if (estadoSD.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            sdEscritura=false;
            sdDisponible=true;
        }
        if (sdDisponible&&sdEscritura){
            try {
                File file=new File(getExternalFilesDir(ruta_sd_global.getAbsolutePath()),TEXTARCH);
                OutputStreamWriter os=new OutputStreamWriter(new FileOutputStream(file));
                os.write(datos);
                os.close();
                escrito=true;
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return escrito;
    }

    private boolean existe(String[] archivos,String archivo){
        for (int f=0; f<archivos.length; f++){
            if (archivo.equals(archivos[f]))
                return true;
        }
        return false;
    }

    private void cambioBotones(){
        if(leerSD){
            bl.setText("Leer de SD");
            bg.setText("Guardar en SD");
            tw.setText("Origen SD");
            aSwitch.setText("Guardar en SD");
        }else{
            bl.setText("Leer de MI");
            bg.setText("Guardar en MI");
            tw.setText("Origen MI");
            aSwitch.setText("Guardar en MI");
        }
    }
}
