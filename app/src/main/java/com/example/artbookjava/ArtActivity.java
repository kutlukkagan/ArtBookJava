package com.example.artbookjava;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.TokenWatcher;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.artbookjava.databinding.ActivityArtBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class ArtActivity extends AppCompatActivity {

    private ActivityArtBinding binding;
    ActivityResultLauncher<Intent>activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityArtBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        registerLauncer();
    }

    public void save(View view){
String name=binding.nameText.getText().toString();
String artistName=binding.artistText.getText().toString();
String year=binding.yearText.getText().toString();
Bitmap smallImage=makeSmallerImage((selectedImage),300);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray=outputStream.toByteArray();

        try {
            database=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artname VARCHAR,paintername VARCHAR,year VARCHAR,image BLOB) ");
           // database.execSQL("INSERT INTO arts(artname,paintername,year,image)VALUES()";
            String sqlString ="INSERT INTO arts(artname,paintername,year,image)VALUES(?,?,?,?)";
            SQLiteStatement
        }catch (Exception e){
            e.printStackTrace();
        }


    }
public Bitmap makeSmallerImage(Bitmap image,int maximumSize){
        int width=image.getWidth();
        int height=image.getHeight();
        float bitmapRatio=(float) width/(float) height;
        if (bitmapRatio>1){
            //landscape image
            width=maximumSize;
            height=(int) (width/bitmapRatio);

        } else {
            //portrait image
            height=maximumSize;
            width=(int)(height*bitmapRatio);
        }


        return image.createScaledBitmap(image,width,height,true);
}

public void selectImage(View view)
{
    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
        //Android 33+ -> READ_MEDIA_IMAGES
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                    }
                }).show();

            } else {
                //request permission

                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        }
        else
        {
            //gallery
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }

    }
    else {
        //Android 32- -> READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();

            } else {
                //request permission

                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else
        {
            //gallery
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }


    }
    }



        public  void registerLauncer()
        {
    activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode()==RESULT_OK){
                Intent intentFromResult=result.getData();
                if (intentFromResult!=null){

                   Uri imageData=  intentFromResult.getData();
                  // binding.imageView.setImageURI(imageData);
                try {
                   if (Build.VERSION.SDK_INT>=28){

                    ImageDecoder.Source source=ImageDecoder.createSource(ArtActivity.this.getContentResolver(),imageData);
                     selectedImage=ImageDecoder.decodeBitmap(source);
                     binding.imageView.setImageBitmap(selectedImage);}else {
                       selectedImage=MediaStore.Images.Media.getBitmap(ArtActivity.this.getContentResolver(),imageData);
                       binding.imageView.setImageBitmap(selectedImage);
                   }
                }catch (Exception e){
                    e.printStackTrace();
                }

                }
            }
        }
    });
permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
    @Override
    public void onActivityResult(Boolean result) {
        if (result){
            //permission granted
            Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }    else {
            //permission denied
            Toast.makeText(ArtActivity.this, "Permission needed", Toast.LENGTH_LONG).show();
        }
    }
});

        }

}