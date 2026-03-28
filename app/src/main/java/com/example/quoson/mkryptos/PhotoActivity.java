package com.example.quoson.mkryptos;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


public class PhotoActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageButton search_photo, click_cipher, click_decoding;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    private Toast changeAlert;
    private DESCipher myDESCipher;
    private EditText editKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        // action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.mini_photo);

        this.search_photo = (ImageButton)findViewById( R.id.search_photo );
        this.search_photo.setOnClickListener( this );
        this.click_cipher = (ImageButton)findViewById( R.id.click_cipher );
        this.click_cipher.setOnClickListener( this );
        this.click_decoding = (ImageButton)findViewById( R.id.click_decoding );
        this.click_decoding.setOnClickListener( this );

        this.editKey = (EditText)findViewById( R.id.editKey );




    }

    @Override
    public void onClick( View v ) {

        switch( v.getId() ) {
            case ( R.id.search_photo ):
                // your photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

                break;
            case ( R.id.click_cipher ):
                this.myDESCipher = new DESCipher( this.editKey.getText().toString() );
                String paht_new_photo = this.myDESCipher.doCipherFile( this.imgDecodableString,  "cipher" );
                // Add the Photo to a Gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(paht_new_photo);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                // show the cipher photo
                ImageView imgViewCipher = (ImageView) findViewById( R.id.cipher_photo );
                imgViewCipher.setVisibility(View.VISIBLE);
                // Set the Image in ImageView after decoding the String
                Log.i("imagen", "Imagen save encontrada: " + paht_new_photo);
                imgViewCipher.setImageBitmap(BitmapFactory.decodeFile(paht_new_photo));

                if( isExternalStorageWritable() ) {
                    Log.i("imagen", "external storage is available for read and write." );
                }
                else {
                    Log.i("imagen", "external storage nein " );
                }



                this.changeAlert = Toast.makeText( getApplicationContext(), paht_new_photo, Toast.LENGTH_SHORT);
                this.changeAlert.show();
                break;
            case ( R.id.click_decoding ):

                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
                // When an Image is picked
                if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                    // Get the Image from data
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    ImageView imgView = (ImageView) findViewById( R.id.original_photo );
                    imgView.setVisibility(View.VISIBLE);
                    // Set the Image in ImageView after decoding the String
                    imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

                    Log.i( "imagen", "Imagen load encontrada: " + imgDecodableString );

                    this.changeAlert = Toast.makeText( getApplicationContext(), R.string.menuOptionLoadPhoto, Toast.LENGTH_SHORT);
                    this.changeAlert.show();
                }
                else {
                      Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                }
        }
        catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(""), albumName);
        if (!file.mkdirs()) {
            Log.e("imagen", "Directory not created");
        }
        return file;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
