package com.mr_captain.mathematicesvision;

import static com.mr_captain.mathematicesvision.FreeUsesOf.count;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.gms.tasks.Task;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mr_captain.mathematicesvision.Other.ADDStudentModal;
import com.mr_captain.mathematicesvision.Other.PaymentModal;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Add_Student_Activity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Dialog dialog;
    Button btn;
    CircleImageView img;
    private Uri filePath;
    FirebaseDatabase database;
    TextInputEditText input1,in2,in3,in4,in5,in6,in7,in8;
    AutoCompleteTextView autoCompleteTextView;
    private String name,sf,mn,dob,bn,sa,tp,py,sortBy;
    private final int SELECT_IMG_CODE = 1;
    String[] items = {"12th","11th"};
    ArrayAdapter<String> arrayAdapter;
    int tempVar = 0;
    DatabaseReference readRef;
    ValueEventListener listener;
    FreeUsesOf freeUsesOf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               finish();
            }
        });

        autoCompleteTextView = findViewById(R.id.get_item_select);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_items,items);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sortBy = adapterView.getItemAtPosition(i).toString();
            }
        });
        freeUsesOf = new FreeUsesOf();
        freeUsesOf.countStudent();
        btn = findViewById(R.id.pickupImage);
        img = findViewById(R.id.imageTop);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,SELECT_IMG_CODE);

            }
        });

        in4 = findViewById(R.id.IN4);
        in4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDatePickup();
            }
        });


        input1 = findViewById(R.id.IN1);
        in2 = findViewById(R.id.IN2);
        in3 = findViewById(R.id.IN3);
        in5 = findViewById(R.id.IN5);
        in6 = findViewById(R.id.IN6);
        in7 = findViewById(R.id.IN7);
        in8 = findViewById(R.id.IN8);

        dialog = new Dialog(Add_Student_Activity.this);
        in3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                {
                    tempVar = 1;
                    dialog.setContentView(R.layout.loading_layout);
                    dialog.setCancelable(false);
                    if(!Add_Student_Activity.this.isFinishing()) {
                        dialog.show();
                    }
                    readRef =  FirebaseDatabase.getInstance().getReference("Student");
                    listener =  readRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean check = true;
                            if(tempVar==1) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        if (ds.exists()) {
                                            for (DataSnapshot ds1 : ds.getChildren()) {
                                                if (ds1.exists()) {
                                                    if (ds1.child("active").exists()) {
                                                        if (ds1.child("active").getValue().toString().equals("1")) {
                                                            if (ds1.child("mobileNumber").getValue().toString().equals(in3.getText().toString())) {
                                                                dialog.dismiss();
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(Add_Student_Activity.this);
                                                                builder.setTitle("Already Register");
                                                                builder.setMessage("This number is already register with other student");
                                                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                    }
                                                                });
                                                                if (!Add_Student_Activity.this.isFinishing()) {
                                                                    builder.show();
                                                                }
                                                                check = false;
                                                                in3.setText("");
                                                                readRef.removeEventListener(listener);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if(check)
                            {
                                if((dialog!=null) && dialog.isShowing())
                                {
                                    dialog.dismiss();
                                    tempVar = 1;
                                    readRef.removeEventListener(listener);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
        Button sumbit = findViewById(R.id.Add_Student);

        sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                name = input1.getText().toString();
                sf = in2.getText().toString();
                mn = in3.getText().toString();
                dob = in4.getText().toString();
                bn = in5.getText().toString();
                sa = in6.getText().toString();
                tp = in7.getText().toString();
                py = in8.getText().toString();
                if(name.isEmpty() || sf.isEmpty() || dob.isEmpty() || bn.isEmpty() || sa.isEmpty() || tp.isEmpty() || py.isEmpty() || sortBy.isEmpty())
                {
                    dialog.setContentView(R.layout.error_dialog);
                    if(!Add_Student_Activity.this.isFinishing())
                          dialog.show();

                }
                else
                {
                    if(Integer.parseInt(tp)>=Integer.parseInt(py))
                        uploadData();
                    else
                    {
                        dialog.setContentView(R.layout.error_dialog);
                        if(Add_Student_Activity.this.isFinishing())
                               dialog.show();
                        in7.setText("");
                        in8.setText("");
                    }

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            if (requestCode == SELECT_IMG_CODE) {
                filePath = data.getData();
                img.setImageURI(data.getData());
            }
    }

    private void ShowDatePickup()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        int m = i1+1;
        in4.setText(""+i2+"/"+m+"/"+i);
    }
    private void uploadData()
    {
        if(!Add_Student_Activity.this.isFinishing()) {
            dialog.setContentView(R.layout.loading_layout);
            dialog.setCancelable(false);
            dialog.show();
        }
        database = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReference();
        if(filePath!=null)
        {
            StorageReference ref = reference.child(freeUsesOf.getYear()).child(mn+".jpg");
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String sId = freeUsesOf.getStudentId(sortBy);
                            Task<Void> addRef = database.getReference("Student").child(freeUsesOf.getYear()).child(sId).setValue(new ADDStudentModal(name,sf,mn,dob,bn,sa,"1",uri.toString(),freeUsesOf.getDate(),sortBy,tp,"1"));
                            addRef.addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Task<Void> payRef;
                                    if(Integer.parseInt(tp)==Integer.valueOf(py))
                                    {
                                        payRef  = database.getReference("Payment").child(freeUsesOf.getYear()).child(sortBy).child(freeUsesOf.paymentIdGen()).setValue(new PaymentModal(mn,sId,py,tp, freeUsesOf.getDate()));
                                    }
                                    else
                                    {
                                        payRef = database.getReference("Payment").child(freeUsesOf.getYear()).child(sortBy).child(freeUsesOf.paymentIdGen()).setValue(new PaymentModal(mn,sId,py,tp, freeUsesOf.getDate()));
                                    }

                                    payRef.addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            if((dialog!=null)&&dialog.isShowing())
                                                dialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Add_Student_Activity.this);
                                            builder.setTitle("Success");
                                            builder.setMessage("Student Data added Successfully...");
                                            builder.setCancelable(false);
                                            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                  startActivity(new Intent(Add_Student_Activity.this,Add_Student_Activity.class));
                                                  finish();
                                                }
                                            });
                                            builder.create();
                                            if(!Add_Student_Activity.this.isFinishing())
                                                builder.show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if((dialog!=null)&&dialog.isShowing() && !Add_Student_Activity.this.isFinishing()) {
                                        dialog.dismiss();
                                        dialog.setContentView(R.layout.error);
                                        dialog.setCancelable(true);
                                        dialog.show();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if((dialog!=null)&&dialog.isShowing()) {
                                dialog.dismiss();
                                dialog.setContentView(R.layout.error);
                                dialog.setCancelable(true);
                                dialog.show();
                            }

                        }
                    });
                }
            });
        }
    }

}
