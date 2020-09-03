package com.example.rent_home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddAHome extends AppCompatActivity {
    private ImageView imaged_added, close;
    private TextView post;
    private Uri image_uri;
    private String imgUrl;





    ProgressDialog pd;

    SocialAutoCompleteTextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_home);










        imaged_added= findViewById(R.id.image_added);
        close= findViewById(R.id.close);
        description= findViewById(R.id.description);
        post= findViewById(R.id.post);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddAHome.this, Profile.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

        CropImage.activity().start(AddAHome.this);

    }



    private void upload() {
        pd= new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if(image_uri!=null){
            final StorageReference file = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "."+ getFileExtension(image_uri));
            StorageTask uptask= file.putFile(image_uri);

            uptask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return file.getDownloadUrl();                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    Uri down_uri= (Uri) task.getResult();
                    imgUrl= down_uri.toString();

                    DatabaseReference refre= FirebaseDatabase.getInstance().getReference("Homes");
                    String postID = refre.push().getKey();
                    HashMap<String, Object> map= new HashMap<>();
                    map.put("Post Id",postID);
                    map.put("Image Url",imgUrl);
                    map.put("Description",description.getText().toString());
                    map.put("Punlisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    refre.child(postID).setValue(map);

                    DatabaseReference hasHTagRef= FirebaseDatabase.getInstance().getReference().child("HashTags");
                    List<String> hashtags= description.getHashtags();
                    if(!hashtags.isEmpty()){
                        for (String tag: hashtags){
                            map.clear();
                            map.put("tag",tag.toLowerCase());
                            map.put("Post ID", postID);
                            hasHTagRef.child(tag.toLowerCase()).setValue(map);
                        }
                    }

                    pd.dismiss();
                    startActivity(new Intent(AddAHome.this, Profile.class));
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddAHome.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }

        else{
            Toast.makeText(this, "Image missing", Toast.LENGTH_SHORT).show();

        }

    }
    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Objects.equals(requestCode,CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) ){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            image_uri= result.getUri();
            imaged_added.setImageURI(image_uri);
            Toast.makeText(this, "hoise", Toast.LENGTH_SHORT).show();
        }
        else if( Objects.equals(requestCode,RESULT_OK)){
            Toast.makeText(this, "hoy nai", Toast.LENGTH_SHORT).show();
        }

        else
        {
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddAHome.this, Profile.class));
            finish();
        }


    }
}