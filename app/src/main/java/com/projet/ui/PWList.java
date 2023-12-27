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
import com.projet.adapters.PWAdapter;
import com.projet.adapters.StudentPWAdapter;
import com.projet.entities.PW;
import com.projet.entities.PW;
import com.projet.entities.Tooth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PWList extends AppCompatActivity {

    private List<PW> pwList = new ArrayList<>();
    private ListView PWList;
    RequestQueue requestQueue;
    PWAdapter PWAdapter ;
    private Button backToHome;
    private static final String TAG = "GetPWs";

   // private final String PWS_URL = "http://192.168.11.191:8080/api/pws/student/1500";
    private String studentId;

    private final String PWS_URL = "http://192.168.11.191:8080/api/pws/student/";
    //private final String PWS_URL = "http://192.168.43.91:8080/api/pws/student/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_list);

        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");
        
        
        PWAdapter = new PWAdapter(pwList, this);
        backToHome = findViewById(R.id.backToHomeBtn);

        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PWList.this, MainActivity.class);
                startActivity(intent);
                PWList.this.finish();
            }
        });

        getPWs();

    }

    public void getPWs(){
        // Créer une file d'attente pour les requêtes Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Créer une requête GET pour récupérer la liste des étudiants
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, PWS_URL+studentId, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Traitement de la réponse JSON
                        List<PW> studentPWList = parseStudentPWList(response);

                        PWList = findViewById(R.id.idPWLV);
                        PWAdapter.updateStudentsList(studentPWList);
                        PWList.setAdapter(PWAdapter);

                        for (PW studentPW : studentPWList) {
                            // Faire quelque chose avec chaque étudiant (par exemple, l'afficher dans Logcat)
                            Log.d(TAG, "PW ID: " + studentPW.getId() + " Title : " + studentPW.getTitle()
                            + " Tooth name : " + studentPW.getTooth().getName()
                            + " Tooth ID : " + studentPW.getTooth().getId());
                        }
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


    private List<PW> parseStudentPWList(JSONArray jsonArray) {
        List<PW> studentPWList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject pwJson = jsonArray.getJSONObject(i);
                PW pw = new PW();
                pw.setId(pwJson.getInt("id"));
                pw.setTitle(pwJson.getString("title"));
                pw.setObjectif(pwJson.getString("objectif"));

                // Récupérer l'objet tooth
                JSONObject toothJson = pwJson.getJSONObject("tooth");
                Tooth tooth = new Tooth();
                tooth.setId(toothJson.getInt("id"));
                tooth.setName(toothJson.getString("name"));
                pw.setTooth(tooth);


                studentPWList.add(pw);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return studentPWList;
    }
}