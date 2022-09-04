package com.example.rent_home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAHome extends AppCompatActivity {
    FirebaseAuth mAuth;

    String[] DivisionsStringVariable1;
    String[] SylhetDivisionDistrictStringVariable1;
    String[] DhakaDivisionDistrictStringVariable1;
    String[] BarishalDivisionDistrictStringVariable1;
    String[] MymensinghDivisionDistrictStringVariable1;
    String[] KhulnaDivisionDistrictStringVariable1;
    String[] RangpurDivisionDistrictStringVariable1;
    String[] RajshahiDivisionDistrictStringVariable1;
    String[] ChittagongDivisionDistrictStringVariable1;

    String[] DhakaDistrictAreaStringVariable1;
    String[] GazipurDistrictAreaStringVariable1;

    String[] RentRangeStringVariable1;
    String[] RoomsStringVariable1;

    private Spinner DivisionSpinnerVariable1;
    private Spinner DistrictSpinnerVariable1;
    private Spinner AreaSpinnerVariable1;
    private ImageButton homeImg;
   // SocialAutoCompleteTextView description;
    private EditText description;
    private StorageReference picOfPostHome;
    private static final int galleryPic = 1;
    private Uri ImageUri ;

    String SelectDistrict;
    String nameHome,contactNo,beds,price,area;
    String saveCurrentDate, saveCurrentTime,descrip;
    private String randomKey;
    NavigationView sidenav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    private String downloadUri,iUri;

    private Button homePic,details;
    private ImageButton postBtn,tracMap;
    private TextView text;
    private EditText homeName,subArea, rent;
    private EditText phoNo, room;
    private ProgressDialog pd;

    private DatabaseReference postDataRef;
    CircleImageView SNpropic;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_a_home);
        mAuth=FirebaseAuth.getInstance();

        auth= FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");

        retrivePicture();
        Toolbar toolbar2;
        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);
        sidenav = (NavigationView) findViewById(R.id.sidenavmenu);
        SNpropic=(CircleImageView)sidenav.getHeaderView(0).findViewById(R.id.profile_pic_SN);
        drawerLayout = (DrawerLayout) findViewById(R.id.draw);
        toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar2,
                R.string.open,
                R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        sidenav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.profileSN:
                        //Toast.makeText(getApplicationContext(), "Profile will Open", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent = new Intent(PostAHome.this, Profile.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.logoutSN:
                        Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        FirebaseAuth.getInstance().signOut();

                        Intent intent5 = new Intent(PostAHome.this, Login.class);
                        startActivity(intent5);
                        break;


                }
                return true;
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.postAHome);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.homePage:
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(), search.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.postAHome:
                        return true;

                }
                return false;
            }

        });


        DivisionsStringVariable1=getResources().getStringArray(R.array.DivisionsString);// ei variable e values er declare kora string recieve korbe
        DhakaDivisionDistrictStringVariable1=getResources().getStringArray(R.array.DhakaDivisionsDistrictsString);//same
        SylhetDivisionDistrictStringVariable1=getResources().getStringArray(R.array.SylhetDivisionDistrictString);//same
        BarishalDivisionDistrictStringVariable1=getResources().getStringArray(R.array.BarishalDivisionsDistrictsString);
        MymensinghDivisionDistrictStringVariable1=getResources().getStringArray(R.array.MymensinghDivisionsDistrictsString);
        RajshahiDivisionDistrictStringVariable1=getResources().getStringArray(R.array.RajshahiDivisionsDistrictsString);
        KhulnaDivisionDistrictStringVariable1=getResources().getStringArray(R.array.KhulnaDivisionsDistrictsString);
        RangpurDivisionDistrictStringVariable1=getResources().getStringArray(R.array.RangpurDivisionsDistrictsString);
        ChittagongDivisionDistrictStringVariable1=getResources().getStringArray(R.array.ChittagongDivisionsDistrictsString);

        RentRangeStringVariable1=getResources().getStringArray(R.array.Rent);
        RoomsStringVariable1=getResources().getStringArray(R.array.Room);

        DhakaDistrictAreaStringVariable1=getResources().getStringArray(R.array.dhakaDisArea);
        GazipurDistrictAreaStringVariable1=getResources().getStringArray(R.array.gazipurDisArea);

        DivisionSpinnerVariable1=(Spinner) findViewById(R.id.spinnerDivison1); // divison spinner jeta activity_search.xml e ase oita ke variable e set korbe
        DistrictSpinnerVariable1=(Spinner) findViewById(R.id.spinnerDistrict1);//same
        AreaSpinnerVariable1=(Spinner)findViewById(R.id.spinnerArea1);

        ArrayAdapter<String> DivisionAdapter1 = new ArrayAdapter<String>(this, R.layout.spinnerdisplay1, R.id.spinnerDisplay1, DivisionsStringVariable1);// ei adapter division er nam gula ke spinner display layout er maddome adapter e set korbe
        ArrayAdapter<String> SylhetDivisionAdapter1= new ArrayAdapter<String>(this, R.layout.spinnerdisplay1, R.id.spinnerDisplay1, SylhetDivisionDistrictStringVariable1);//same
        ArrayAdapter<String> DhakaDivisionAdapter1 = new ArrayAdapter<String>(this, R.layout.spinnerdisplay1, R.id.spinnerDisplay1, DhakaDivisionDistrictStringVariable1);//same
        ArrayAdapter<String> BarishalDivisionAdapter1 = new ArrayAdapter<String>(this, R.layout.spinnerdisplay1, R.id.spinnerDisplay1, BarishalDivisionDistrictStringVariable1);//same
        ArrayAdapter<String> MymensinghDivisionAdapter1 = new ArrayAdapter<String>(this, R.layout.spinnerdisplay1, R.id.spinnerDisplay1, MymensinghDivisionDistrictStringVariable1);//same
        ArrayAdapter<String> KhulnaDivisionAdapter1 = new ArrayAdapter<String>(this, R.layout.spinnerdisplay1, R.id.spinnerDisplay1, KhulnaDivisionDistrictStringVariable1);//same
        ArrayAdapter<String> RajshahiDivisionAdapter1 = new ArrayAdapter<String>(this, R.layout.spinnerdisplay1, R.id.spinnerDisplay1, RajshahiDivisionDistrictStringVariable1);//same
        ArrayAdapter<String> RangpurDivisionAdapter1 = new ArrayAdapter<String>(this, R.layout.spinnerdisplay1, R.id.spinnerDisplay1, RangpurDivisionDistrictStringVariable1);//same
        ArrayAdapter<String> ChittagongDivisionAdapter1 = new ArrayAdapter<String>(this, R.layout.spinnerdisplay1, R.id.spinnerDisplay1, ChittagongDivisionDistrictStringVariable1);//same


        ArrayAdapter<String> DhakaDistrictAreaAdapter = new ArrayAdapter<String>(this, R.layout.spinnerdisplay1, R.id.spinnerDisplay1, DhakaDistrictAreaStringVariable1);//same
        ArrayAdapter<String> GazipurDistrictAreaAdapter = new ArrayAdapter<String>(this, R.layout.spinnerdisplay1, R.id.spinnerDisplay1, GazipurDistrictAreaStringVariable1);//same

        DivisionSpinnerVariable1.setAdapter(DivisionAdapter1);// set kora divison gulu spinner e show korbe

        DivisionSpinnerVariable1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==2)
                {
                    DistrictSpinnerVariable1.setAdapter(DhakaDivisionAdapter1);
                    DistrictSpinnerVariable1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position==0) {
                                AreaSpinnerVariable1.setAdapter(DhakaDistrictAreaAdapter);
                            }
                            if(position==1) {
                                AreaSpinnerVariable1.setAdapter(GazipurDistrictAreaAdapter);

                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                if(position==7)
                {
                    DistrictSpinnerVariable1.setAdapter(SylhetDivisionAdapter1);
                }
                if(position==0)
                {
                    DistrictSpinnerVariable1.setAdapter(BarishalDivisionAdapter1);
                }
                if(position==1)
                {
                    DistrictSpinnerVariable1.setAdapter(ChittagongDivisionAdapter1);
                }
                if(position==3)
                {
                    DistrictSpinnerVariable1.setAdapter(KhulnaDivisionAdapter1);
                }
                if(position==4)
                {
                    DistrictSpinnerVariable1.setAdapter(MymensinghDivisionAdapter1);
                }
                if(position==5)
                {
                    DistrictSpinnerVariable1.setAdapter(RajshahiDivisionAdapter1);
                }
                if(position==6)
                {
                    DistrictSpinnerVariable1.setAdapter(RangpurDivisionAdapter1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        picOfPostHome= FirebaseStorage.getInstance().getReference().child("home_pictures");

        postDataRef = FirebaseDatabase.getInstance().getReference().child("Rent_posts");
        homeImg= findViewById(R.id.homeImage);
      //  upBtn= findViewById(R.id.upBtn);
//        description= findViewById(R.id.des);
      //  cur_user = FirebaseAuth.getInstance().getCurrentUser();
        postBtn = findViewById(R.id.button_post);
      //  details = findViewById(R.id.details);
        homeName = findViewById(R.id.homeName);
        rent = findViewById(R.id.rentRange);
        phoNo = findViewById(R.id.phnNo);
        room = findViewById(R.id.room);
//        tracMap = findViewById(R.id.mapApi);
        pd = new ProgressDialog(this);


        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(PostAHome.this);
                openGallery();
            }
        });
//        tracMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(PostAHome.this, tracLocation.class));
//
//            }
//        });



        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectData();
            }
        });



    }

    private void retrivePicture() {
        databaseReference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("image")) {
                    String image = snapshot.child("image").getValue().toString();
                    Picasso.get().load(image).into(SNpropic);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openGallery() {
        Intent galleryIntent= new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPic);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galleryPic && resultCode== RESULT_OK && data!=null)
        {
            ImageUri= data.getData();
            homeImg.setImageURI(ImageUri);
        }

    }



    private void collectData() {
        nameHome= homeName.getText().toString();
        contactNo= phoNo.getText().toString();
        beds= room.getText().toString();
        price=rent.getText().toString();

        // localArea= subArea.getText().toString();
//        descrip= description.getText().toString();
       // area=SelectDistrict.toString();

        if(ImageUri==null){
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(contactNo))
        {
            Toast.makeText(this, "Please give your Contact No, its mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(beds))
        {
            Toast.makeText(this, "Please provide all the information", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(price))
        {
            Toast.makeText(this, "Please provide all the information", Toast.LENGTH_SHORT).show();
        }

        /*else if(TextUtils.isEmpty(localArea))
        {
            Toast.makeText(this, "Please provide all the information", Toast.LENGTH_SHORT).show();
        }*/
        else {
            storeData();
        }
    }

    private void storeData() {
        pd.setMessage("Posting");
        pd.show();
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate= currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH: mm: ss a");
        saveCurrentTime= currentTime.format(calendar.getTime());

           randomKey= saveCurrentDate+ saveCurrentTime;

       final StorageReference file= picOfPostHome.child(ImageUri.getLastPathSegment()+ randomKey + ".jpg");

        final UploadTask uploadTask= file.putFile(ImageUri);



        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(PostAHome.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(PostAHome.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadUri = file.getDownloadUrl().toString();
                        return file.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadUri=task.getResult().toString();
                            Toast.makeText(PostAHome.this, "Done", Toast.LENGTH_SHORT).show();
                             UpdateDatabase();
                        }
                    }
                });
            }
        });

    }

    private void UpdateDatabase() {
        HashMap<String, Object>map= new HashMap<>();
        map.put("pId",randomKey);
        map.put("date",saveCurrentDate);
        map.put("time",saveCurrentTime);
        map.put("image",downloadUri);
        map.put("homeName",nameHome);
        map.put("contactNo", contactNo);
        map.put("room",beds);
        map.put("rentCost",price);
        map.put("Publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());


        postDataRef.child(randomKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(PostAHome.this, "Posted", Toast.LENGTH_SHORT).show();
                }
                else{
                    pd.dismiss();
                    String msg=task.getException().toString();
                    Toast.makeText(PostAHome.this, "Error"+msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}