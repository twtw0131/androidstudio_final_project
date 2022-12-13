package com.course.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class FoodCreate extends AppCompatActivity {

    DBHelper helper;
    SQLiteDatabase db;
    ImageButton btnCamera, saveButton;
    ImageView imageView;
    Bitmap imageBitmap;
    EditText foodNameEditText, foodCountEditText, contentEditText, timeEditText, placeEditText;
    Intent mainIntent;
    ImageButton place;
    String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_create);

        // db 생성
        helper = new DBHelper(this, "food.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);

        mainIntent = getIntent();
        btnCamera = (ImageButton) findViewById(R.id.imageButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        saveButton = findViewById(R.id.imageButton2);
        foodCountEditText = findViewById(R.id.foodCountEditText);
        foodNameEditText = findViewById(R.id.foodNameEditText);
        contentEditText = findViewById(R.id.contentEditText);
        timeEditText = findViewById(R.id.timeText);
        place = findViewById(R.id.placeButton);
        placeEditText = findViewById(R.id.placeEditText);

        // 장소 입력
        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodCreate.this, Google_Map.class);
                intent.putExtra("date",mainIntent.getStringExtra("date"));
                startActivity(intent);
            }
        });
        // 장소 입력 후 데이터 가지고 복귀
        // Intent place_text = getIntent();

        if(!TextUtils.isEmpty(mainIntent.getStringExtra("text"))){
            text =  mainIntent.getStringExtra("text");
            place.setVisibility(View.GONE);
            placeEditText.setVisibility(View.VISIBLE);
            placeEditText.setText(text);

            btnCamera.setEnabled(true);
            imageView.setEnabled(true);
            foodNameEditText.setEnabled(true);
            foodCountEditText.setEnabled(true);
            contentEditText.setEnabled(true);
            timeEditText.setEnabled(true);

        }
        else{
            Toast.makeText(getApplicationContext(), "장소 입력 먼저 해주세요", Toast.LENGTH_LONG).show();
            btnCamera.setEnabled(false);
            imageView.setEnabled(false);
            foodNameEditText.setEnabled(false);
            foodCountEditText.setEnabled(false);
            contentEditText.setEnabled(false);
            timeEditText.setEnabled(false);
        }

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 카메라촬영 클릭 이벤트
                // 카메라 기능을 Intent
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 0);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( placeEditText.getText().toString().equals("") || timeEditText.getText().toString().equals("") ||  imageBitmap == null || foodCountEditText.getText().toString().equals("") || foodNameEditText.getText().toString().equals("") || contentEditText.getText().toString().equals("") ) {
                    Toast.makeText(getApplicationContext(), "입력되지 않은 값이 존재합니다!!!", Toast.LENGTH_LONG).show();
                    return;
                }
                ContentValues values = new ContentValues();
                values.put("food_name", foodNameEditText.getText().toString());
                values.put("food_count", foodCountEditText.getText().toString());
                values.put("place", placeEditText.getText().toString());
                values.put("content", contentEditText.getText().toString());
                values.put("date", mainIntent.getStringExtra("date"));
                values.put("time",timeEditText.getText().toString());
                byte[] image = getByteFromBitmap(imageBitmap);
                values.put("image", image);
                db.insert("food", null, values);
                Toast.makeText(getApplicationContext(), "성공적으로 저장되었습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

    public byte[] getByteFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)    {
        super.onActivityResult(requestCode, resultCode, data);

        // 카메라 촬영을 하면 이미지뷰에 사진 삽입
        if(requestCode == 0 && resultCode == RESULT_OK) {
            // Bundle로 데이터를 입력
            Bundle extras = data.getExtras();

            // Bitmap으로 컨버전
            imageBitmap = (Bitmap) extras.get("data");

            // 이미지뷰에 Bitmap으로 이미지를 입력
            imageView.setImageBitmap(imageBitmap);
        }
    }
}