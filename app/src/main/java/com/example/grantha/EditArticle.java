package com.example.grantha;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import Model.Post;
import io.github.mthli.knife.KnifeText;

public class EditArticle extends AppCompatActivity {

    private String postId;
    private ImageView close;
    private TextView save;
    private Button editImage;
    private KnifeText knife;
    private MaterialEditText title;
    private String authorId;

    private FirebaseUser fUser;
    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);

        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");
        authorId=intent.getStringExtra("authorId");


        knife=findViewById(R.id.knife);
        save=findViewById(R.id.save);
        close=findViewById(R.id.close);
        editImage=findViewById(R.id.edit_image);
        title=findViewById(R.id.title);


        //setting up rich text editor
        knife.setSelection(knife.getEditableText().length());

        setupBold();
        setupItalic();
        setupUnderline();
        setupStrikethrough();
        setupBullet();
        setupQuote();
        setupLink();
        setupClear();
        //setting up storage ref for images..is STORAGE section of firebase
        storageReference= FirebaseStorage.getInstance().getReference().child("Uploads");


        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post=snapshot.getValue(Post.class);
                        if(post!=null){
                            title.setText(post.getTitle());
                            knife.fromHtml(post.getArticle());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //working of close button
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        //changing post image..
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity().setAspectRatio(1,1)
                        .start(EditArticle.this);
            }
        });

        //saving the editted article..
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(title.getText().toString(),knife.toHtml());
                finish();

            }

        });





    }


    //updating article..
    private void updateProfile(String title, String knifeData) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);
        HashMap<String,Object> hashMap =new HashMap<>();
        hashMap.put("title", title);
        hashMap.put("article", knifeData);
        //updating the article..
        reference.updateChildren(hashMap);


    }


    //functions for rich text

    private void setupBold() {
        ImageButton bold = (ImageButton) findViewById(R.id.bold);

        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.bold(!knife.contains(KnifeText.FORMAT_BOLD));
            }
        });

        bold.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EditArticle.this,"toast_bold", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupItalic() {
        ImageButton italic = (ImageButton) findViewById(R.id.italic);

        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.italic(!knife.contains(KnifeText.FORMAT_ITALIC));
            }
        });

        italic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EditArticle.this, "toast_italic", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupUnderline() {
        ImageButton underline = (ImageButton) findViewById(R.id.underline);

        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.underline(!knife.contains(KnifeText.FORMAT_UNDERLINED));
            }
        });

        underline.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EditArticle.this, R.string.toast_underline, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupStrikethrough() {
        ImageButton strikethrough = (ImageButton) findViewById(R.id.strikethrough);

        strikethrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.strikethrough(!knife.contains(KnifeText.FORMAT_STRIKETHROUGH));
            }
        });

        strikethrough.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EditArticle.this, R.string.toast_strikethrough, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupBullet() {
        ImageButton bullet = (ImageButton) findViewById(R.id.bullet);

        bullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.bullet(!knife.contains(KnifeText.FORMAT_BULLET));
            }
        });


        bullet.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EditArticle.this,"toast_bullet", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupQuote() {
        ImageButton quote = (ImageButton) findViewById(R.id.quote);

        quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.quote(!knife.contains(KnifeText.FORMAT_QUOTE));
            }
        });

        quote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EditArticle.this, R.string.toast_quote, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupLink() {
        ImageButton link = (ImageButton) findViewById(R.id.link);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLinkDialog();
            }
        });

        link.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EditArticle.this,"toast_insert_link", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupClear() {
        ImageButton clear = (ImageButton) findViewById(R.id.clear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                knife.clearFormats();
            }
        });

        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EditArticle.this, R.string.toast_format_clear, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void showLinkDialog() {
        final int start = knife.getSelectionStart();
        final int end = knife.getSelectionEnd();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_link, null, false);
        final EditText editText = (EditText) view.findViewById(R.id.edit);
        builder.setView(view);
        builder.setTitle(R.string.dialog_title);

        builder.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String link = editText.getText().toString().trim();
                if (TextUtils.isEmpty(link)) {
                    return;
                }

                // When KnifeText lose focus, use this method
                knife.link(link, start, end);
            }
        });

        builder.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // DO NOTHING HERE
            }
        });

        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.undo:
                knife.undo();
                break;
            case R.id.redo:
                knife.redo();
                break;
            case R.id.github:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_repo)));
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }


    //for post image changing..
    private  void uploadImage()
    {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if(mImageUri!=null)
        {
            final StorageReference fileRef=storageReference.child(System.currentTimeMillis()+".jpg");

            uploadTask=fileRef.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                        throw task.getException();


                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri=task.getResult();
                        String url=downloadUri.toString();
                        //adding to database
                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(postId).child("imageurl").setValue(url);

                        //HashMap<String,Object> hashMap =new HashMap<>();
                        //hashMap.put("imageurl",""+url);
                        pd.dismiss();
                    }
                    else {
                        Toast.makeText(EditArticle.this,"failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditArticle.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
        else
            Toast.makeText(EditArticle.this,"no image selected",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            mImageUri=result.getUri();
            //upload the image in storage and return the downloaded url
            uploadImage();
        }
        else{
            Toast.makeText(EditArticle.this,"PLEASE TRY AGAIN", Toast.LENGTH_SHORT).show();
        }

    }
}