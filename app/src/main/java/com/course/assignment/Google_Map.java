package com.course.assignment;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class Google_Map extends FragmentActivity implements OnMapReadyCallback {

    private com.google.android.gms.maps.GoogleMap map;
    EditText searchBox;
    TextView locationText;
    Button searchButton, moveButton;
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        //food create에서 date 받아오기
        Intent intent = getIntent();
        date = intent.getStringExtra("date");

        // 구글 맵 프래그먼트를 띄운다
        // SupprotMapFragment 를 통해 레이아웃에 만든 fragment 의 ID 를 참조하고
        // 구글맵을 호출한다.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // getMapAsync 는 무조건 main Thread 에서 호출되어야한다
        mapFragment.getMapAsync(this);

        // 구글맵에서 검색창 editText 와 검색된 위치 불러올 textView 초기화
        searchBox = findViewById(R.id.shop_editText_search);
        locationText = findViewById(R.id.shop_text_location);

        // text
        moveButton = findViewById(R.id.move_button);

        // 구글맵 검색 하는 부분
        searchButton = findViewById(R.id.shop_button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 검색창에서 텍스트를 가져온다
                String searchText = searchBox.getText().toString();

                Geocoder geocoder = new Geocoder(getBaseContext());
                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocationName(searchText, 3);
                    if (addresses != null && !addresses.equals(" ")) {
                        search(addresses);
                        moveButton.setVisibility(View.VISIBLE);
                    }
                } catch(Exception e) {

                }
                //serach return
                // click button after button create
                //Intent intent = new Intent(google.this, ) // change display
                // put extra
            }
        });
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Google_Map.this,FoodCreate.class);
                String text = locationText.getText().toString();
                intent.putExtra("text",text);
                intent.putExtra("date",date);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        map = googleMap;

        map.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);

        oneMarker();
    }

    public void oneMarker() {
        // 서울 여의도에 대한 위치 설정
        LatLng seoul = new LatLng(37.52487, 126.92723);

        // 구글맵에 표시할 마커에 대한 옵션 설정
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions
                .position(seoul)
                .title("원하는 위치(위도, 경도)에 마커를 표시했습니다")
                .snippet("여기는 여의도입니다")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .alpha(0.5f); // 알파는 좌표의 투명도

        // 마커를 생성한다
        map.addMarker(markerOptions); // .showInfoWindow() 를 쓰면 처음부터 마커에 상세정보가 뜬다

        // 정보창 클릭리스너
        map.setOnInfoWindowClickListener(infoWindowClickListener);

        // 마커 클릭 리스너
        map.setOnMarkerClickListener(markerClickListener);

        // 카메라를 여의도 위치로 옮겨준다
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 16));

        map.setOnMarkerClickListener(new com.google.android.gms.maps.GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(Google_Map.this, "눌렀습니다!!", Toast.LENGTH_LONG);
                return false;
            }
        });
    }

    // 정보창 클릭 리스너
    com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            String markerId = marker.getId();
            Toast.makeText(Google_Map.this, "정보창 클릭 Marker ID : "
                    + markerId, Toast.LENGTH_SHORT).show();
        }
    };

    // 마커 클릭 리스너
    com.google.android.gms.maps.GoogleMap.OnMarkerClickListener markerClickListener = new com.google.android.gms.maps.GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            String markerId = marker.getId();
            // 선택한 타겟의 위치
            LatLng location = marker.getPosition();
            Toast.makeText(Google_Map.this, "마커 클릭 Marker ID : "
                            + markerId + "(" + location.latitude + " " + location.longitude + ")",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    };
    // 구글맵 주소 검색 메서드
    public void search(List<Address> addresses) {
        Address address = addresses.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        String addressText = String.format(
                "%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : " ", address.getFeatureName());

        locationText.setVisibility(View.VISIBLE);
        locationText.setText(""+address.getAddressLine(0).toString());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(addressText);

        map.clear();
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

}
