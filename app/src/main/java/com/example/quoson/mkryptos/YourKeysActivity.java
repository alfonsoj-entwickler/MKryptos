package com.example.quoson.mkryptos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


/**
 * YourKeysActivity — Stored cryptographic key browser and management screen.
 *
 * Displays all keys previously saved to the SQLite "KeysCipher" table as a
 * scrollable ListView. Each row shows the key's user-defined name, algorithm
 * type, and the timestamp when it was created.
 *
 * Interactions:
 *   - Tapping a list item navigates to {@link ShowInformationActivity} where
 *     the full key details (including the Base64 key material) are displayed.
 *   - The options menu ("action_settings") opens a deletion dialog that lists
 *     all stored keys and removes the selected one from the database.
 *
 * Internal classes:
 *   {@link KeyAdapter} — Custom ArrayAdapter that inflates the "llaves" row
 *   layout and binds each {@link ListKey} object's name, type, and date fields.
 */
public class YourKeysActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{

    ListView myList;       // The main list showing all stored keys
    ListKey [] listKey;    // In-memory array of key metadata loaded from SQLite
    private KeyAdapter myAdapter;  // Custom adapter driving the ListView


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_keys);

        // action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.mini_sql);

        // update list
        listKey = updateList();

        // Adapter
        this.myAdapter = new KeyAdapter( this );

        // ListView
        myList = (ListView)findViewById( R.id.listViewKey );
        myList.setAdapter( myAdapter );
        myList.setOnItemClickListener(this);



    }

    @Override
    public void onItemClick( AdapterView<?> adapter, View lista, int position, long arg3) {
        String item = listKey[position].getID();
        Intent nextIntent = new Intent( this, ShowInformationActivity.class );
        nextIntent.putExtra( "myID", item );
        startActivity(nextIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_your_keys, menu);
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

            loadYourkey();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * KeyAdapter — Inner ArrayAdapter that binds ListKey objects to list row views.
     *
     * Inflates the "llaves" row layout for each position and populates
     * the name, algorithm type, and creation date TextViews.
     */
    class KeyAdapter extends ArrayAdapter {
        private Activity context;

        public KeyAdapter( Activity context ) {
            super(context, R.layout.llaves, listKey);
            this.context = context;
        }

        /** Inflates the row layout and binds the key name, type, and date for the given position. */
        public View getView( int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.llaves, null);
            TextView nameKey = (TextView)item.findViewById( R.id.textName );
            nameKey.setText( listKey[position].getName() );
            TextView typeKey = (TextView)item.findViewById( R.id.textType );
            typeKey.setText( listKey[position].getAlgorithm());
            TextView dateKey = (TextView)item.findViewById( R.id.textDate );
            dateKey.setText( listKey[position].getTime());
            return item;
        }
    }

    /**
     * Refreshes the ListView after a key deletion.
     * Re-queries the database, rebuilds the adapter, and notifies it of the change.
     *
     * @param position The list position of the deleted item (currently unused in the query).
     */
    public void enabledPosition(int position) {
        Log.i("base", "(Despues)Longitud del array Lista: " + listKey.length);
        listKey = updateList(); // Reload key list from SQLite
        Log.i("base", "(Antes)Longitud del array Lista: " + listKey.length);
        this.myAdapter = new KeyAdapter( this );
        myList.setAdapter( myAdapter );
        myAdapter.notifyDataSetChanged();
    }

    /**
     * Shows a deletion dialog listing all stored key names.
     * When the user selects a name, the corresponding row is deleted from SQLite
     * using a DELETE query, and the list is refreshed via {@link #enabledPosition}.
     */
    public void loadYourkey( ) {
        String yourKey = "";
        ListKey [] myListKey;
        String [] showName;
        DataKeys mySQLKeys;
        SQLiteDatabase db;

        // Databases
        mySQLKeys = new DataKeys( this, "DBKeys", null, 4 );
        db = mySQLKeys.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM KeysCipher", null);
        int cuantos =0;

        if( c.moveToFirst() ){
            do {
                cuantos++;
            }while( c.moveToNext() );
        }
        else {
            Log.i( "base", "Error DB" );
        }

        //myListKey = new ListKey[cuantos];
        showName = new String[cuantos];
        int index = 0;

        if( c.moveToFirst() ){
            do {
                //myListKey[index] = new ListKey( c.getString(0), c.getString(1), c.getString(2), c.getString(4) );
                showName[index] = c.getString(1);
                index++;
            }while( c.moveToNext() );
        }
        else {
            Log.i( "base", "Error DB" );
        }

        // database closed
        db.close();

        final String [] mySelection = showName;

        AlertDialog.Builder alert_select = new AlertDialog.Builder( this );
        alert_select.setTitle( R.string.menuOptionSelectKey );
        alert_select.setItems(showName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Databases
                DataKeys selectSQLKeys = new DataKeys(getApplication(), "DBKeys", null, 4);
                SQLiteDatabase dbNew = selectSQLKeys.getWritableDatabase();
                Cursor c = dbNew.rawQuery("DELETE FROM KeysCipher WHERE name = '" + mySelection[which] + "'", null);

                if (c.moveToFirst()) {
                    do {
                        Log.i("base", "Deleted key: " + c.getString(0));
                    } while (c.moveToNext());
                }

                Log.i("base", "Deleted key: " + mySelection[which]);

                dbNew.close();
                enabledPosition( which );

            }
        });

        alert_select.show();

    }

    /**
     * Queries the full KeysCipher table and returns an array of {@link ListKey} objects
     * containing each key's ID, name, algorithm, and creation date.
     * Used to populate and refresh the ListView.
     *
     * @return Array of ListKey objects, one per row in the database.
     */
    public ListKey [] updateList() {

        DataKeys mySQLKeys;
        ListKey [] myListKey;
        SQLiteDatabase db;

        // Databases
        mySQLKeys = new DataKeys( this, "DBKeys", null, 4 );
        db = mySQLKeys.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM KeysCipher", null);
        int cuantos =0;

        if( c.moveToFirst() ){
            do {
                cuantos++;
            }while( c.moveToNext() );
        }
        else {
            Log.i( "base", "Error DB" );
        }

        myListKey = new ListKey[cuantos];
        int index = 0;

        if( c.moveToFirst() ){
            do {
                myListKey[index] = new ListKey( c.getString(0), c.getString(1), c.getString(2), c.getString(4) );
                index++;
            }while( c.moveToNext() );
        }
        else {
            Log.i( "base", "Error DB" );
        }

        // database closed
        db.close();
        return myListKey;
    }
}
