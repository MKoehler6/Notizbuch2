package com.example.micha.notizbuch;

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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button btnNeuerSchub;
    private Button btnOK;
    public CardView cardView;
    EditText editText;
    TagebuchDB db = new TagebuchDB(this);
    RecyclerAdapter adapter;
    String notiz;
    public static final String KEY = "TEXTEINGABE";
    public static final int REQUEST_ID = 1;

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
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnOK.getWindowToken(), 0);
                adapter.notifyDataSetChanged();
                writeFile();
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            uploadFile("/storage/emulated/0/Android/data/com.example.micha.notizbuch/files/Text.txt");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        });
    }
    public int uploadFile(String sourceFileUri)
    {

        final String uploadFileName = "Text.txt";
        String fileName = uploadFileName;
        int serverResponseCode = 0;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        //File sourceFile = new File(getExternalFilesDir(""),"Text.txt");
        File sourceFile = new File(sourceFileUri);


        try {
            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL("http://www.koehlerplay.de/upload/upload37tg65.php");

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);
            conn.setRequestProperty("authkey", "test");
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                    + fileName + "\"" + lineEnd);

            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            Log.d("BytesRead", String.valueOf(bytesRead));

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            if(serverResponseCode == 200) {
                Log.d("Serverantwort", "erfolgreich");
            }

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            Log.d("Serverantwort", "nicht erfolgreich");
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "Exception : "
                    + e.getMessage(), e);
        }
        return serverResponseCode;
    }

    public void writeFile() {
        try {
            // catches IOException below
            final String TESTSTRING = new String("Jetzt mit Authentifizierung 2");

            /* We have to use the openFileOutput()-method
             * the ActivityContext provides, to
             * protect your file from others and
             * This is done for security-reasons.
             * We chose MODE_WORLD_READABLE, because
             *  we have nothing to hide in our file */
            File file = new File("/storage/emulated/0/Android/data/com.example.micha.notizbuch/files/Text.txt");
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            osw.write(TESTSTRING);

            /* ensure that everything is
             * really written out and close */
            osw.flush();
            osw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
