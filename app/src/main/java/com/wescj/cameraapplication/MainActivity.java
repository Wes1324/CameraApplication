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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private static final int TAKE_PICTURE_REQUEST_CODE = 0;
    private String mImageFileLocation;
    private static final String CURRENTLY_DISPLAYED_IMAGE = "imageCurrentlyDisplayed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        Intent startIntent = getIntent();
//        String intentAction = startIntent.getAction();
//        String intentCategory = (getIntent().getCategories().toArray())[0].toString();

//        Log.i("INTENT ACTION: ", getIntent().getAction());

/*        Object [] startIntentCategoriesArray = getIntent().getCategories().toArray();
        Log.i("NUMBER OF CATEGORIES: ", Integer.toString(startIntentCategoriesArray.length));
        for(Object category : startIntentCategoriesArray) {
            Log.i("START INTENT CATEGORY: ", category.toString());
        }
*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.captured_image_imageView);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if(savedInstanceState != null){
            mImageFileLocation = savedInstanceState.getString(CURRENTLY_DISPLAYED_IMAGE);

            Bitmap photoCapturedBitmap = BitmapFactory.decodeFile(mImageFileLocation); //Goes to where camera saved picture and decodes it
            imageView.setImageBitmap(photoCapturedBitmap); //Todo: Make sure this is the best way to display image again or whether should call getReducedImage()

//            getReducedImageSize();    //Todo: if uncomment this, when run app it will crash if turn screen straight away after launch
        }

        int imageViewHeight = imageView.getHeight();
        int imageViewWidth = imageView.getWidth();
        Log.i("IV SIZE:", "w: " + Integer.toString(imageViewWidth) + ", h: " + Integer.toString(imageViewHeight));

/*
        if(getIntent().getAction() == null) {

            mImageFileLocation = getIntent().getStringExtra("mImageFileLocation");

            //getReducedImageSize();
        }
*/
//        Log.i("TEST OF CONDITION", (getIntent().getCategories().toArray())[0].toString());

    }

    //Todo: At the moment, if press action button and then turn screen when camera is on, app crashes.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_card_add_new:
                takePhoto();   //Tried getting takePhoto() to return boolean, but returns straight away after camera activity started
                return true;

            default:
                return super.onOptionsItemSelected(item);   // If we got here, the user's action was not recognized. Invoke the superclass to handle it.

        }
    }

    public void takePhoto() {
        Intent takePictureIntent = new Intent();
        takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE); //Todo: CAN INCORPORATE INTO ABOVE LINE

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch(IOException e) {
            e.printStackTrace();
        }


        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);      // Before, the second arg was set to Uri.fromFile(photoFile) BUT this didn't work, had to use file provider instead.
            startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE);
        }
    }

    File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timestamp + "_";

        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);        //Directory in which to store image
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDirectory);      //File object pointing to actual photo file in storageDirectory, which will hold pixels created by camera

        mImageFileLocation = imageFile.getAbsolutePath();

        return imageFile;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == RESULT_OK){
/*
            Intent showImageIntent = new Intent(this, MainActivity.class);
            showImageIntent.putExtra("mImageFileLocation", mImageFileLocation);
            startActivity(showImageIntent);
*/
            getReducedImageSize();    //Now going to do this in onCreate() of MainActivity instance will view the image in
        }
    }

    public void getReducedImageSize() {
        int imageViewHeight = imageView.getHeight();
        int imageViewWidth = imageView.getWidth();

/*        Log.i("IV BEFORE image shown:", "w: " + Integer.toString(imageViewWidth) + ", h: " + Integer.toString(imageViewHeight));

        if((imageViewHeight == 0) || (imageViewWidth == 0)) {
            Bitmap photoCapturedBitmap = BitmapFactory.decodeFile(mImageFileLocation); //Goes to where camera saved picture and decodes it
            imageView.setImageBitmap(photoCapturedBitmap);
            Toast.makeText(this, "No image reduction done", Toast.LENGTH_LONG).show();

            imageViewHeight = imageView.getHeight();
            imageViewWidth = imageView.getWidth();
            Log.i("IV AFTER image shown:", "w: " + Integer.toString(imageViewWidth) + ", h: " + Integer.toString(imageViewHeight));

        } else { */
            BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
            bitmapFactoryOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mImageFileLocation, bitmapFactoryOptions);
            int actualCameraImageWidth = bitmapFactoryOptions.outWidth;
            int actualCameraImageHeight = bitmapFactoryOptions.outHeight;

            int heightScale = actualCameraImageHeight / imageViewHeight;
            int widthScale = actualCameraImageWidth / imageViewWidth;

            Integer factorToUse;

            if (heightScale == widthScale) {
                Toast.makeText(this, "scales are the same: " + heightScale, Toast.LENGTH_SHORT).show();
                factorToUse = heightScale;
            } else {
                factorToUse = Math.min(heightScale, widthScale);
                Toast.makeText(this, "scales are DIFFERENT: " + factorToUse, Toast.LENGTH_SHORT).show();
            }

            bitmapFactoryOptions.inSampleSize = factorToUse;
            bitmapFactoryOptions.inJustDecodeBounds = false;

            Bitmap reducedSizePhoto = BitmapFactory.decodeFile(mImageFileLocation, bitmapFactoryOptions);   //decode the image with that scale option set

            imageView.setImageBitmap(reducedSizePhoto);
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(CURRENTLY_DISPLAYED_IMAGE, mImageFileLocation);
    }

}