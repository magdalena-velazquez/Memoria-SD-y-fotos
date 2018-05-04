package mx.unam.aragon.fes.diplo.imagenpresencial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PictureActivity extends AppCompatActivity {

    private  final int REQUEST_CODE=0;
    private  final String FOTOS="misFotos";
    private  final String folder_picture= Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_PICTURES)+"/"+FOTOS+"/";
    private File folder =new File(folder_picture);
    private  String filepath;

    private Button button;
    private ImageView imageView;
    private ListView listView;

    private static final String PREFIJO="pic_";
  //  private static File mFileName=null;
    private List<String>items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        button=(Button) findViewById(R.id.buttonFoto);
        imageView= (ImageView) findViewById(R.id.imageView);
        listView=(ListView) findViewById(R.id.listview);
        Log.i("RUTA",folder.toString());
        //folder.mkdirs();

        if (folder.exists()){
        loadList();}

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode==REQUEST_CODE){
                loadPicture(filepath);
                loadList();
            }else {
                Toast.makeText(getApplicationContext(),"Error al tomar la Foto",Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private  void loadPicture(String f){
        File imgFile=new File(f);
        if (imgFile.exists()){
            Bitmap bm= BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(bm);
        }
    }

    private void loadList(){
        items= new ArrayList<String>();
        File[] files= folder.listFiles();
        if(files.length>0) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isFile()) {
                    if (file.getName().contains(PREFIJO)) {
                        items.add(file.getName());
                    }
                }
            }
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadPicture(folder_picture+items.get(position));
                Toast.makeText(getApplicationContext(),folder_picture+items.get(position),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void takePicture(){
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyymmddhhmmss");
        String pictureName=PREFIJO + dateFormat.format(new Date());
        filepath=folder_picture+pictureName+".jpg";
        File myPicture= new File(filepath);
        try {
            myPicture.createNewFile();
            Log.i("PICTURE",filepath);

            Uri uri=Uri.fromFile(myPicture);
            Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(i,REQUEST_CODE);
            loadPicture(filepath);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
