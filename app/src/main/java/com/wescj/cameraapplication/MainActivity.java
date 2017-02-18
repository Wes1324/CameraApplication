package com.wescj.cameraapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int TAKE_PICTURE_REQUEST_CODE = 0;
    private ImageView capturedImage;
    private String mImageFileLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        capturedImage = (ImageView) findViewById(R.id.captured_image_imageView);
    }

    public void takePhoto(View v) {
        Intent takePictureIntent = new Intent();
        takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch(IOException e) {
            e.printStackTrace();
        }


        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE);
        }

        //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile)); //Tells camera where to save picture taken
        //startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == RESULT_OK){
            //Toast.makeText(this, "picture taken", Toast.LENGTH_SHORT).show();

            //Bundle extras = data.getExtras();
            //Bitmap photoCapturedBitmap = (Bitmap) extras.get("data");
            //capturedImage.setImageBitmap(photoCapturedBitmap);

            //Bitmap photoCapturedBitmap = BitmapFactory.decodeFile(mImageFileLocation); //Goes to where camera saved picture and decodes it
            //capturedImage.setImageBitmap(photoCapturedBitmap);

            getReducedImageSize();
        }
    }

    File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timestamp + "_";

        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        mImageFileLocation = imageFile.getAbsolutePath();

        return imageFile;
    }

    public void getReducedImageSize() {
        int imageViewHeight = capturedImage.getHeight();
        int imageViewWidth = capturedImage.getWidth();

        BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
        bitmapFactoryOptions.inJustDecodeBounds = true;                             //Set options so that decoder of BitmapFactory just does
                                                                                    // a 'dummy read' of the image taken with camera to read the sizes of that image.
                                                                                    // i.e. decoder queries the bitmap without having to allocate memory for its pixels
        BitmapFactory.decodeFile(mImageFileLocation, bitmapFactoryOptions);         //Decode the image with that inJustDecodeBounds option set
        int actualCameraImageWidth = bitmapFactoryOptions.outWidth;                 //Read the image sizes from the inner class
        int actualCameraImageHeight = bitmapFactoryOptions.outHeight;

        int heightScale = actualCameraImageHeight/imageViewHeight;
        int widthScale = actualCameraImageWidth/imageViewWidth;

        Integer factorToUse;

        if(heightScale == widthScale) {
            Toast.makeText(this, "scales are the same: " + heightScale, Toast.LENGTH_SHORT).show();
            factorToUse = heightScale;
        } else {
            factorToUse = Math.min(heightScale, widthScale);
            Toast.makeText(this, "scales are DIFFERENT: " + factorToUse, Toast.LENGTH_SHORT).show();
        }

        bitmapFactoryOptions.inSampleSize = factorToUse;        //Tell the inner class how large we want the image to be
        bitmapFactoryOptions.inJustDecodeBounds = false;        //Don't want to do a dummy read this time, actually want to read the pixels in
                                                                // so we can then show them in the imageView

        Bitmap reducedSizePhoto = BitmapFactory.decodeFile(mImageFileLocation, bitmapFactoryOptions);   //decode the image with that scale option set

        capturedImage.setImageBitmap(reducedSizePhoto);
    }
}
