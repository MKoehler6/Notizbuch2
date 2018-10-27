package com.example.micha.notizbuch;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends Adapter {

    TagebuchDB db;
    Context context;
    public static final String KEY = "TEXTEINGABE";
    public static final int REQUEST_ID = 1;
    CardView cv;
    EditText et;
    String id;
    int positionAngeklickt;

    RecyclerAdapter(TagebuchDB db, Context context, CardView cv, EditText et) {
        this.db = db;
        this.context = context;
        this.cv = cv;
        this.et = et;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.eine_zeile, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return db.readDate().size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView mItemText1;
        private TextView mItemText2;

        public ListViewHolder(View itemView) {

            super(itemView);
            mItemText1 = itemView.findViewById(R.id.textzeile1);
            mItemText2 = itemView.findViewById(R.id.textzeile2);
            Log.d("MEINLOG", "textzeile1");
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public void bindView(int position) {
            String date = db.readDate().get(position);
            String tag = date.substring(8);
            String monat = date.substring(5,7);
            String jahr = date.substring(0,4);
            String text = tag + "." + monat + "." + jahr;
            mItemText1.setText(text);
            mItemText2.setText(db.readNotiz().get(position));
        }

        public void onClick(View view) {
            ArrayList<String> ids = db.readID();
            positionAngeklickt = getAdapterPosition();
            id = ids.get(getAdapterPosition());
            et.setText(db.readNotiz().get(positionAngeklickt));
            cv.setVisibility(View.VISIBLE);
            notifyItemChanged(getAdapterPosition());
        }
        public boolean onLongClick(View view) {
            ArrayList<String> ids = db.readID();
            id = ids.get(getAdapterPosition());
            db.delete(id);
            notifyItemRemoved(getAdapterPosition());
            return true;
        }
    }
}
