package com.projet.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.projet.LoginActivity;
import com.projet.MainActivity;
import com.projet.R;
import com.projet.entities.PW;
import com.projet.entities.StudentPW;
import com.projet.ui.PWList;

import java.util.List;

public class PWAdapter extends BaseAdapter {

    private List<PW> studentPWs;
    private LayoutInflater inflater;

    public PWAdapter(List<PW> studentPWs, Activity activity) {
        this.studentPWs = studentPWs;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void updateStudentsList(List<PW> newStudentPWs) {
        studentPWs.clear();
        studentPWs.addAll(newStudentPWs);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return studentPWs.size();
    }

    @Override
    public Object getItem(int i) {
        return studentPWs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return studentPWs.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = inflater.inflate(R.layout.pw_item, null);

        TextView id = view.findViewById(R.id.idPWItem);
        TextView title = view.findViewById(R.id.idTitleItem);
        TextView objectif = view.findViewById(R.id.idObjectifItem);
        TextView tooth = view.findViewById(R.id.idToothNameItem);

        id.setText(studentPWs.get(i).getId()+"");
        title.setText(studentPWs.get(i).getTitle());
        objectif.setText(studentPWs.get(i).getObjectif());
        tooth.setText(studentPWs.get(i).getTooth().getName());

        // Ajout de l'écouteur de clic
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Utilisation du contexte pour initialiser l'Intent
                Context context = view.getContext();
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("pwId", studentPWs.get(i).getId()+"");
                Log.d("pwID", "PW ID : " + studentPWs.get(i).getId());
                // Utilisez la méthode startActivity à partir du contexte pour démarrer l'activité
                context.startActivity(intent);
            }
        });*/

        return view;
    }
}