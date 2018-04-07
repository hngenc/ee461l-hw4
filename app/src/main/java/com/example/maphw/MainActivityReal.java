package com.example.maphw;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivityReal extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_real);
    }
    /** Called when the user clicks the Send button */
    public void sendQuery(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        EditText editText =(EditText) findViewById(R.id.mapQuery);
        String query = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, query);
        startActivity(intent);
    }
}
