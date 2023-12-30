package com.projet.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.projet.MainActivity;
import com.projet.R;
import com.projet.adapters.StudentPWAdapter;
import com.projet.entities.PW;
import com.projet.entities.StudentPW;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentPWList extends AppCompatActivity {

    private List<StudentPW> employes = new ArrayList<>();
    private ListView StudentPWList;
    RequestQueue requestQueue;
    StudentPWAdapter studentPWAdapter ;
    private static final String TAG = "GetStudents";

    //private final String STUDENTS_URL = "http://192.168.11.191:8080/api/student-pws/student/1";
    //private final String STUDENTS_URL = "http://192.168.1.104:8080/api/student-pws/student/1";
    private final String STUDENTS_URL = "http://192.168.1.104:8080/api/student-pws/student/";
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_pw_list);

        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");

        studentPWAdapter = new StudentPWAdapter(employes, this);

        /*backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentPWList.this, MainActivity.class);
                startActivity(intent);
                StudentPWList.this.finish();
            }
        });*/

        getStudentPW();

    }
    public void getStudentPW(){
        // Créer une file d'attente pour les requêtes Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Créer une requête GET pour récupérer la liste des étudiants
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, STUDENTS_URL+studentId, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Traitement de la réponse JSON
                        List<StudentPW> studentPWList = parseStudentPWList(response);

                        StudentPWList = findViewById(R.id.idStudentPWLV);
                        studentPWAdapter.updateStudentsList(studentPWList);
                        StudentPWList.setAdapter(studentPWAdapter);

                        /*for (StudentPW studentPW : studentPWList) {
                            // Faire quelque chose avec chaque étudiant (par exemple, l'afficher dans Logcat)
                            Log.d(TAG, "StudentPW ID: " + studentPW.getId() );
                        }*/
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Gestion des erreurs
                        Log.e(TAG, "Error fetching students: " + error.toString());
                    }
                }) {
        };

        // Ajouter la requête à la file d'attente
        requestQueue.add(jsonArrayRequest);
    }


    private List<StudentPW> parseStudentPWList(JSONArray jsonArray) {
        List<StudentPW> studentPWList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject studentJson = jsonArray.getJSONObject(i);
                StudentPW studentPW = new StudentPW();
                studentPW.setId(studentJson.getInt("id"));
                studentPW.setAngleInterneG(studentJson.getDouble("angleInterneG"));
                studentPW.setAngleInterneD(studentJson.getDouble("angleInterneD"));

                // Récupérer l'objet PW
                JSONObject pwJson = studentJson.getJSONObject("pw");
                PW pw = new PW();
                pw.setId(pwJson.getInt("id"));
                pw.setTitle(pwJson.getString("title"));
                studentPW.setPw(pw);

                studentPWList.add(studentPW);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return studentPWList;
    }

}