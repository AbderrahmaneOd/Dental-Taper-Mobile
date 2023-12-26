package com.projet.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.projet.R;
import com.projet.entities.StudentPW;

import java.util.List;

public class StudentPWAdapter extends BaseAdapter{
    
    private List<StudentPW> studentPWs;
    private LayoutInflater inflater;

    public StudentPWAdapter(List<StudentPW> studentPWs, Activity activity) {
        this.studentPWs = studentPWs;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void updateStudentsList(List<StudentPW> newStudentPWs) {
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
            view = inflater.inflate(R.layout.student_pw_item, null);

        TextView id = view.findViewById(R.id.idStudentPWItem);
        TextView mesure1 = view.findViewById(R.id.idMesure1Item);
        TextView mesure2 = view.findViewById(R.id.idMesure2Item);
        TextView pw = view.findViewById(R.id.idPWTitleItem);

        id.setText(studentPWs.get(i).getId()+"");
        mesure1.setText(studentPWs.get(i).getMesureAngle1());
        mesure2.setText(studentPWs.get(i).getMesureAngle2());
        pw.setText(studentPWs.get(i).getPw().getTitle());
        return view;
    }
}
