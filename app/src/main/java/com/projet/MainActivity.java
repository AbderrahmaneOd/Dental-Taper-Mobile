package com.projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;  // Add this import statement

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.projet.ui.PWList;
import com.projet.ui.StudentPWList;

public class MainActivity extends AppCompatActivity {

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Unable to load OpenCV!");
        } else {
            Log.d("OpenCV", "OpenCV loaded Successfully!");
        }
    }

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final String SUBMIT_URL = "http://192.168.11.191:8080/api/student-pws";
    private ImageView imageView;
    private TextView anglesTextView;

    private Bitmap selectedBitmap;

    private Mat imageMat;

    // Stocke les points du contour
    private List<org.opencv.core.Point> contourPoints = new ArrayList<>();
    private Button studentPWListBtn;
    private Button pwListBtn;
    private Button nextBtn, cameraBtn, resetBtn, submitResultsBtn;
    private TextView stepTxtView;
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private List<org.opencv.core.Point> selectedPoints = new ArrayList<>();
    private String studentId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");

        imageView = findViewById(R.id.imageView);
        anglesTextView = findViewById(R.id.anglesTextView);
        studentPWListBtn = findViewById(R.id.PWListBtn);
        pwListBtn = findViewById(R.id.AllPWBtn);
        submitResultsBtn = findViewById(R.id.submitResultBtn);
        nextBtn = findViewById(R.id.idNextBtn);
        resetBtn = findViewById(R.id.idResetBtn);
        stepTxtView = findViewById(R.id.idStepTxtView);
        cameraBtn = findViewById(R.id.idCameraBtn);


        studentPWListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("StudentID", "Student ID : " + studentId);
                Intent newIntent = new Intent(MainActivity.this, StudentPWList.class);
                newIntent.putExtra("studentId", studentId);
                startActivity(newIntent);
            }
        });

        pwListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("StudentID", "Student ID : " + studentId);
                Intent newIntent = new Intent(MainActivity.this, PWList.class);
                newIntent.putExtra("studentId", studentId);
                startActivity(newIntent);
            }
        });

        submitResultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitResults();
            }
        });


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCameraPermission();
            }
        });
    }

    public void submitResults() {

        // Créez un objet JSON avec les données requises
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("date", "2023-12-23");
            jsonBody.put("angleInterneG", 20707.42);
            jsonBody.put("angleInterneD", 1226.31);
            jsonBody.put("angleExterneG", 7870.25);
            jsonBody.put("angleExterneD", 21809.42);
            jsonBody.put("angledepouilleG", 6586.31);
            jsonBody.put("angledepouilleD", 27597.99);
            jsonBody.put("angleConvergence", 15267.08);

            JSONObject studentObj = new JSONObject();
            studentObj.put("id", studentId);
            jsonBody.put("student", studentObj);

            JSONObject pwObj = new JSONObject();
            pwObj.put("id", 1);
            jsonBody.put("pw", pwObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SUBMIT_URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Traitement de la réponse en cas de succès
                        Log.d("VolleyResponse", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Traitement des erreurs
                Log.e("VolleyError", error.toString());
            }
        });

        // Ajoutez la requête à la RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Vérifiez s'il y a une application de caméra pour gérer cette intention
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Lancez l'intention et attendez le résultat dans onActivityResult
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            // Aucune application de caméra disponible, gestion d'erreur
            Toast.makeText(this, "Aucune application de caméra disponible", Toast.LENGTH_SHORT).show();
        }
    }

    // Demande de la permission d'accéder à la caméra
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void calculateAnglesVertical(List<org.opencv.core.Point> points) {
        if (points.size() < 4) {
            // Gérer le cas où les points ne sont pas suffisants
            System.out.println("Veuillez cliquer sur quatre points pour définir les lignes.");
            return;
        }

        // Calcul des distances pour l'angle de rétrécissement (taper angle)
        double deltaY = Math.abs(points.get(0).y - points.get(1).y);
        double deltaX = Math.abs(points.get(0).x - points.get(1).x);
        double L = Math.hypot(deltaY, deltaX);

        // Calcul de l'angle de rétrécissement (taper angle) par rapport à l'horizontale en degrés
        double taperAngleRad = Math.atan(deltaY / deltaX);
        double taperAngleDeg = Math.toDegrees(taperAngleRad);

        // Calcul de l'angle de l'autre côté par rapport à la verticale en degrés
        double deltaY2 = Math.abs(points.get(2).y - points.get(3).y);
        double taperAngleRad2 = Math.atan(deltaY2 / deltaX);
        double taperAngleDeg2 = Math.toDegrees(taperAngleRad2);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String resultText = String.format(
                        "Taper gauche: %.2f degrés\n" +
                                "Taper droite: %.2f degrés\n",
                        (90 - taperAngleDeg), (90 - taperAngleDeg2));
                anglesTextView.setText(resultText);
            }
        });
    }

    private void calculateAnglesHorizontal(List<org.opencv.core.Point> points) {
        if (points.size() < 4) {
            // Gérer le cas où les points ne sont pas suffisants
            System.out.println("Veuillez cliquer sur quatre points pour définir les lignes.");
            return;
        }

        // Calcul des angles par rapport à l'horizontale pour chaque droite
        double angle1Rad = Math.atan2(points.get(1).y - points.get(0).y, points.get(1).x - points.get(0).x);
        double angle1Deg = Math.toDegrees(angle1Rad);

        double angle2Rad = Math.atan2(points.get(3).y - points.get(2).y, points.get(3).x - points.get(2).x);
        double angle2Deg = Math.toDegrees(angle2Rad);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String resultText = String.format(
                        "Taper gauche: %.2f degrés\n" +
                                "Taper droite: %.2f degrés\n",
                        (angle1Deg), (-angle2Deg));
                anglesTextView.setText(resultText);
            }
        });
    }

    private Point findIntersection(Point p1, Point p2, Point p3, Point p4) {
        double x1 = p1.x, y1 = p1.y;
        double x2 = p2.x, y2 = p2.y;
        double x3 = p3.x, y3 = p3.y;
        double x4 = p4.x, y4 = p4.y;

        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));

        double x = x1 + ua * (x2 - x1);
        double y = y1 + ua * (y2 - y1);

        return new Point(x, y);
    }

    private void drawLinesBetweenPoints(List<org.opencv.core.Point> points, Mat image) {
        // Vérifier s'il y a exactement quatre points

        if (points.size() == 4) {

            Point intersection = findIntersection(points.get(0), points.get(1), points.get(2), points.get(3));


            // Dessiner la première ligne entre les points 0 et 1
            Imgproc.line(image, points.get(0), intersection, new Scalar(255, 0, 0), 2);

            // Dessiner la deuxième ligne entre les points 2 et 3
            Imgproc.line(image, points.get(2), intersection, new Scalar(255, 0, 0), 2);

            // Dessiner une ligne verticale bleue passant par le point d'indice 0
            org.opencv.core.Point point0 = points.get(0);
            // Imgproc.line(image, new org.opencv.core.Point(point0.x, 0), new org.opencv.core.Point(point0.x, image.rows()), new Scalar(255, 0, 0), 2);

            // Dessiner une ligne verticale bleue passant par le point d'indice 2
            org.opencv.core.Point point2 = points.get(2);
            //Imgproc.line(image, new org.opencv.core.Point(point2.x, 0), new org.opencv.core.Point(point2.x, image.rows()), new Scalar(244, 0, 0), 2);

            updateImageView();
        } else {
            // Gérer l'erreur si le nombre de points est incorrect
            Log.e("Point Selection", "Veuillez sélectionner exactement quatre points");
            return;
        }
    }

    private void drawPoint(Mat image, org.opencv.core.Point point) {

        Scalar color = new Scalar(255, 0, 0);

        // Dessiner chaque point
        Imgproc.circle(image, point, 3, color, -2); // Dessiner un cercle (point) sur l'image
        updateImageView();
    }

    // Méthode pour convertir les coordonnées Android en coordonnées OpenCV
    private org.opencv.core.Point convertToOpenCVPoint(float touchX, float touchY, int imageWidth, int imageHeight) {
        // Effectuer des calculs pour ajuster les coordonnées en fonction de la taille de l'image
        // Par exemple, si l'image est redimensionnée ou affichée dans une certaine partie de imageView

        // Exemple de calcul pour adapter les coordonnées à une image redimensionnée
        double openCvX = (touchX / imageView.getWidth()) * imageWidth;
        double openCvY = (touchY / imageView.getHeight()) * imageHeight;

        return new org.opencv.core.Point(openCvX, openCvY);
    }

    public void choosePhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /*

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                // Retrieve the captured image as a Bitmap
                bitmap = (Bitmap) extras.get("data");
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        imageView.setVisibility(View.VISIBLE);
        anglesTextView.setVisibility(View.VISIBLE);


        selectedBitmap = bitmap;
        imageView.setImageBitmap(bitmap);

        imageMat = new Mat();
        Utils.bitmapToMat(bitmap, imageMat);

        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);

        Imgproc.GaussianBlur(imageMat, imageMat, new Size(5, 5), 0);

        Imgproc.Canny(imageMat, imageMat, 50, 150);

        Mat dilatedImage = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.dilate(imageMat, dilatedImage, kernel);

        Mat lines = new Mat();
        Imgproc.HoughLinesP(dilatedImage, lines, 1, Math.PI / 180, 50, 50, 10);

        Mat contoursImage = new Mat(imageMat.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        Imgproc.cvtColor(imageMat, contoursImage, Imgproc.COLOR_GRAY2BGR);
        Imgproc.drawContours(contoursImage, getContours(lines), -1, new Scalar(0, 0, 255), 2);




        // Appel de la fonction pour extraire les points du contour
        //extractContourPoints(imageMat);

        Bitmap contoursBitmap = Bitmap.createBitmap(contoursImage.cols(), contoursImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(contoursImage, contoursBitmap);

        imageView.setImageBitmap(contoursBitmap);


        double leftTaperAngle = calculateTaperAngle(lines, true);
        double rightTaperAngle = calculateTaperAngle(lines, false);

        //drawArbitraryLines(imageMat);

        Mat imagMatCopy = imageMat.clone();

        //Log.d("Debug", "Reference de imageMat à l'extérieur : " + System.identityHashCode(imageMat));

        // Set the onTouchListener for selecting points on the image
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();

                    org.opencv.core.Point openCvPoint = convertToOpenCVPoint(x, y, imageMat.cols(), imageMat.rows());

                    org.opencv.core.Point selectedPoint = findNearestContourPoint(x, y);

                    selectedPoints.add(selectedPoint);


                    drawPoint(imageMat, selectedPoint);

                    if (selectedPoints.size() == 5) {
                        // Effacer les lignes et les points
                        imageMat = imagMatCopy.clone(); // Réinitialiser l'image à son état d'origine
                        imageView.setImageBitmap(null); // Effacer l'image affichée dans l'imageView
                        selectedPoints.clear(); // Vider la liste des points sélectionnés
                    } else if (selectedPoints.size() == 4) {

                        // Calculate angles and convergence using the four selected points
                        calculateAnglesAndConvergence(selectedPoints);

                        // Dessiner les lignes entre les points sélectionnés
                        drawLinesBetweenPoints(selectedPoints, imageMat);
                    }
                }
                return true;
            }
        });

        // Afficher l'image avec les lignes après toutes les transformations
        imageView.post(new Runnable() {
            @Override
            public void run() {

                Log.d("Debug", "Reference de imageMat à post : " + System.identityHashCode(imageMat));


                // Convertir l'image avec les lignes en bitmap
                Bitmap finalBitmap = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(imageMat, finalBitmap);

                // Afficher le bitmap dans l'imageView
                imageView.setImageBitmap(finalBitmap);
            }
        });
    }
   */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;
        imageView.setVisibility(View.VISIBLE);
        anglesTextView.setVisibility(View.VISIBLE);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                // Retrieve the captured image as a Bitmap
                bitmap = (Bitmap) extras.get("data");
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Passer a l'etape 2
        stepTxtView.setText("Étape 2. Versants internes (G-D)");


        selectedBitmap = bitmap;
        imageView.setImageBitmap(bitmap);

        imageMat = new Mat();
        Utils.bitmapToMat(bitmap, imageMat);

        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);

        Imgproc.GaussianBlur(imageMat, imageMat, new Size(5, 5), 0);

        Imgproc.Canny(imageMat, imageMat, 50, 150);

        Mat dilatedImage = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.dilate(imageMat, dilatedImage, kernel);

        Mat lines = new Mat();
        Imgproc.HoughLinesP(dilatedImage, lines, 1, Math.PI / 180, 50, 50, 10);

        Mat contoursImage = new Mat(imageMat.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        Imgproc.cvtColor(imageMat, contoursImage, Imgproc.COLOR_GRAY2BGR);
        Imgproc.drawContours(contoursImage, getContours(lines), -1, new Scalar(0, 0, 255), 2);

        // Appel de la fonction pour extraire les points du contour
        extractContourPoints(imageMat);

        Bitmap contoursBitmap = Bitmap.createBitmap(contoursImage.cols(), contoursImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(contoursImage, contoursBitmap);

        imageView.setImageBitmap(contoursBitmap);


        double leftTaperAngle = calculateTaperAngle(lines, true);
        double rightTaperAngle = calculateTaperAngle(lines, false);


        Mat imagMatCopy = imageMat.clone();

        //Log.d("Debug", "Reference de imageMat à l'extérieur : " + System.identityHashCode(imageMat));

        final int[] totalPointsSelected = {0};
        final int[]  currentStep = {2};

        // Set the onTouchListener for selecting points on the image
        imageView.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();


                    org.opencv.core.Point selectedPoint = findNearestContourPoint(x, y);
                    selectedPoints.add(selectedPoint);
                    drawPoint(imageMat, selectedPoint);

                    totalPointsSelected[0]++;

                    if (currentStep[0] == 2 && totalPointsSelected[0] <= 5) {

                        if(totalPointsSelected[0] == 5){
                            // Effacer les lignes et les points
                            imageMat = imagMatCopy.clone(); // Réinitialiser l'image à son état d'origine
                            imageView.setImageBitmap(null); // Effacer l'image affichée dans l'imageView
                            selectedPoints.clear(); // Vider la liste des points sélectionnés
                            stepTxtView.setText("Étape 3. Versants externes (G-D)");

                            currentStep[0] = 3;

                        } else if(totalPointsSelected[0] == 4){
                            calculateAnglesHorizontal(selectedPoints);
                            drawLinesBetweenPoints(selectedPoints, imageMat);
                        }


                    } else if (currentStep[0] == 3 && totalPointsSelected[0] <= 10) {

                        if(totalPointsSelected[0] == 10){
                            // Effacer les lignes et les points
                            imageMat = imagMatCopy.clone(); // Réinitialiser l'image à son état d'origine
                            imageView.setImageBitmap(null); // Effacer l'image affichée dans l'imageView
                            selectedPoints.clear(); // Vider la liste des points sélectionnés
                            stepTxtView.setText("Étape 4. Angles de dépouille (G-D)");

                            currentStep[0] = 4;

                        } else if(totalPointsSelected[0] == 9){
                            calculateAnglesVertical(selectedPoints);
                            drawLinesBetweenPoints(selectedPoints, imageMat);


                        }
                    } else if (currentStep[0] == 4 && totalPointsSelected[0] <= 15) {

                        if(totalPointsSelected[0] == 15){
                            // Effacer les lignes et les points
                            imageMat = imagMatCopy.clone(); // Réinitialiser l'image à son état d'origine
                            imageView.setImageBitmap(null); // Effacer l'image affichée dans l'imageView
                            selectedPoints.clear(); // Vider la liste des points sélectionnés

                            resetBtn.setVisibility(View.VISIBLE);
                            totalPointsSelected[0] = 0;
                            currentStep[0] = 2;

                        } else if(totalPointsSelected[0] == 14){
                            calculateAnglesVertical(selectedPoints);
                            drawLinesBetweenPoints(selectedPoints, imageMat);


                        }
                    }

                    resetBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            totalPointsSelected[0] = 0;
                            currentStep[0] = 2;

                            stepTxtView.setText("Étape 2. Versants internes (G-D)");
                        }
                    });



                }
                return true;
            }
        });


        // Afficher l'image avec les lignes après toutes les transformations
        imageView.post(new Runnable() {
            @Override
            public void run() {

                Log.d("Debug", "Reference de imageMat à post : " + System.identityHashCode(imageMat));


                // Convertir l'image avec les lignes en bitmap
                Bitmap finalBitmap = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(imageMat, finalBitmap);

                // Afficher le bitmap dans l'imageView
                imageView.setImageBitmap(finalBitmap);
            }
        });
    }



    /*private void resetImageToOriginal() {
        imageMat = imagMatCopy.clone(); // Réinitialiser l'image à son état d'origine
        imageView.setImageBitmap(null); // Effacer l'image affichée dans l'imageView
        selectedPoints.clear(); // Vider la liste des points sélectionnés
    }*/

    // Après avoir obtenu la liste des contours de l'image
    private void extractContourPoints(Mat image) {
        Mat edges = new Mat();
        Imgproc.Canny(image, edges, 50, 150);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint contour : contours) {
            for (org.opencv.core.Point point : contour.toList()) {
                contourPoints.add(point); // Ajouter chaque point du contour à la liste
                //drawPoint(imageMat, point);
            }
        }
    }

    // Méthode pour trouver le point du contour le plus proche du clic
    private org.opencv.core.Point findNearestContourPoint(float touchX, float touchY) {
        org.opencv.core.Point touchPoint = convertToOpenCVPoint(touchX, touchY, imageMat.cols(), imageMat.rows());

        org.opencv.core.Point nearestPoint = null;
        double minDistance = Double.MAX_VALUE;

        for (org.opencv.core.Point contourPoint : contourPoints) {
            double distance = Math.sqrt(Math.pow(touchPoint.x - contourPoint.x, 2) + Math.pow(touchPoint.y - contourPoint.y, 2));

            if (distance < minDistance) {
                minDistance = distance;
                nearestPoint = contourPoint;
            }
        }

        return nearestPoint;
    }

    public void updateImageView() {

        // Afficher l'image avec les lignes après toutes les transformations
        imageView.post(new Runnable() {
            @Override
            public void run() {

                Log.d("Debug", "Reference de imageMat à post : " + System.identityHashCode(imageMat));


                // Convertir l'image avec les lignes en bitmap
                Bitmap finalBitmap = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(imageMat, finalBitmap);

                // Afficher le bitmap dans l'imageView
                imageView.setImageBitmap(finalBitmap);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {

            Toast.makeText(this, "Permission refusée pour accéder au stockage externe", Toast.LENGTH_SHORT).show();
        }
    }

    private List<MatOfPoint> getContours(Mat lines) {
        List<MatOfPoint> contours = new ArrayList<>();
        for (int i = 0; i < lines.rows(); i++) {
            double[] l = lines.get(i, 0);
            double x1 = l[0], y1 = l[1], x2 = l[2], y2 = l[3];
            MatOfPoint contour = new MatOfPoint(new org.opencv.core.Point(x1, y1), new org.opencv.core.Point(x2, y2));
            contours.add(contour);
        }
        return contours;
    }

    private double calculateTaperAngle(Mat lines, boolean isLeft) {
        double sumAngles = 0;
        int countAngles = 0;

        for (int i = 0; i < lines.rows(); i++) {
            double[] l = lines.get(i, 0);
            double x1 = l[0], y1 = l[1], x2 = l[2], y2 = l[3];

            double angle = Math.atan2(y2 - y1, x2 - x1) * (180 / Math.PI);

            if ((isLeft && angle < 0) || (!isLeft && angle > 0)) {
                sumAngles += angle;
                countAngles++;
            }
        }

        if (countAngles > 0) {
            return sumAngles / countAngles;
        } else {
            return 0;
        }
    }

}