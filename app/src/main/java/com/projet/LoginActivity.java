package com.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.projet.entities.Student;
import com.projet.entities.User;
import com.projet.ui.PWList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "GetStudents";
    //private static final String STUDENTS_URL = "http://192.168.43.91:8080/api/students";
    private static final String STUDENTS_URL = "http://192.168.11.191:8080/api/students";
    //private static final  String TOEKN = "JSESSIONID=rB6PI83hHhY_cV6T4xxXJM15EadzeBtrzpHwyoWA; XSRF-TOKEN=230a3667-fe7a-4d32-b335-8db5e82b0119";

    private EditText usernameEdt;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_login);

        usernameEdt = findViewById(R.id.usernameEdt);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //startActivity(intent);
                getStudents();
            }
        });

    }

    public void getStudents() {
        // Créer une file d'attente pour les requêtes Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Créer une requête GET pour récupérer la liste des étudiants
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, STUDENTS_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Traitement de la réponse JSON
                        List<Student> studentList = parseStudentList(response);
                        for (Student student : studentList) {
                            //Log.d(TAG, "Student ID: " + student.getId() + ", Login: " + student.getUser().getLogin());

                            if(usernameEdt.getText().toString().equals(student.getUser().getLogin())){
                                Intent intent = new Intent(LoginActivity.this, PWList.class);
                                intent.putExtra("studentId", student.getId()+"");
                                Log.d(TAG, "Login successful " + student.getId());
                                startActivity(intent);
                            }
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

    private List<Student> parseStudentList(JSONArray jsonArray) {
        List<Student> studentList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject studentJson = jsonArray.getJSONObject(i);
                Student student = new Student();
                student.setId(studentJson.getInt("id"));
                //student.setNumber(studentJson.getString("number"));
                //student.setBirthDay(studentJson.getString("birthDay"));

                // Récupérer l'objet User
                JSONObject userJson = studentJson.getJSONObject("user");
                User user = new User();
                user.setCreatedBy(userJson.getString("createdBy"));
                user.setCreatedDate(userJson.getString("createdDate"));
                user.setLastModifiedBy(userJson.getString("lastModifiedBy"));
                user.setLastModifiedDate(userJson.getString("lastModifiedDate"));
                user.setId(userJson.getInt("id"));
                user.setLogin(userJson.getString("login"));
                user.setFirstName(userJson.getString("firstName"));
                user.setLastName(userJson.getString("lastName"));
                user.setEmail(userJson.getString("email"));
                user.setActivated(userJson.getBoolean("activated"));
                user.setLangKey(userJson.getString("langKey"));
                user.setImageUrl(userJson.getString("imageUrl"));
                user.setResetDate(userJson.getString("resetDate"));

                student.setUser(user);

                // Autres attributs si nécessaire (comme "groupe")

                studentList.add(student);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return studentList;
    }
}