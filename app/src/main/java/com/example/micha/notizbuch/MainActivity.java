package com.example.micha.notizbuch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private Button btnNeuerSchub;
    private Button btnOK;
    public CardView cardView;
    EditText editText;
    TagebuchDB db = new TagebuchDB(this);
    RecyclerAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardView = findViewById(R.id.cardView);
        cardView.setVisibility(View.INVISIBLE);
        editText = findViewById(R.id.editText2);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new RecyclerAdapter(db, this, cardView, editText);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        btnNeuerSchub = findViewById(R.id.button);
        btnNeuerSchub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.insert(1);
                adapter.notifyDataSetChanged();
            }
        });
        btnOK = findViewById(R.id.button3);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.insertText(adapter.id, editText.getText().toString());
                cardView.setVisibility(View.INVISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnOK.getWindowToken(), 0);
                adapter.notifyDataSetChanged();

                new AddNotizToDB().execute(adapter.id, adapter.textDate, editText.getText().toString());
            }
        });
    }

    private class AddNotizToDB extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg) {
            // TODO Auto-generated method stub
            String id = arg[0];
            String date = arg[1];
            String notiz = arg[2];
            Log.d("Notiz: param", "> " + id + date + notiz);

            String body = null;
            try {
                body = "method=" + "INSERT" + "&" + "id=" + URLEncoder.encode(id, "UTF-8") + "&" +
                        "date=" + URLEncoder.encode(date, "UTF-8") + "&" +
                        "notiz=" + URLEncoder.encode(notiz, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String address = "http://www.koehlerplay.de/upload/test.php";
            URL url = null;
            try {
                url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", String.valueOf(body.length()));

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(body);
                writer.flush();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                // liest alles, was im php-Script mit print_r geschrieben wird
                for (String line; (line = reader.readLine()) != null; ) {
                    Log.d("Notiz: param", "> " + line);
                }

                writer.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
