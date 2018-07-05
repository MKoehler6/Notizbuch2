package com.example.micha.notizbuch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

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
            }
        });
    }
}
