package com.zulmipc.firebasezulmi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zulmipc.firebasezulmi.Fragments.KontakFragment;
import com.zulmipc.firebasezulmi.Fragments.PesanFragment;
import com.zulmipc.firebasezulmi.Fragments.ProfileFragment;
import com.zulmipc.firebasezulmi.Model.Pesan;
import com.zulmipc.firebasezulmi.Model.User;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {

    DatabaseReference reference;
    BottomNavigationView bottomNavigationView;
    TextView username;
    CircleImageView profile_image;
    MenuItem mnPesan;

    FirebaseUser firebaseUser;

    private PesanFragment pesanFragment;
    private KontakFragment kontakFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.nav_bottom_widget);
        mnPesan = bottomNavigationView.getMenu().findItem(R.id.mnPesan);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("berhasil..............................");
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pesan pesan = snapshot.getValue(Pesan.class);
                    if (pesan.getReceiver().equals(firebaseUser.getUid()) && !pesan.getIsseen()) {
                        unread++;
                    }

                    if (unread == 0) {
                        mnPesan.setTitle("Pesan");
                    } else {
                        mnPesan.setTitle("(" + unread + ") Pesan");
                        System.out.println("(" + unread + ") Pesan");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        Home binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        pesanFragment = new PesanFragment();
        kontakFragment = new KontakFragment();
        profileFragment = new ProfileFragment();

        username = findViewById(R.id.txtUsername);
        profile_image = findViewById(R.id.profile_image);

        setFragment(kontakFragment);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("Userrrrrrrrrrrrrrrrrrrrrrrr");
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnKontak:
                        setFragment(kontakFragment);
                        item.setCheckable(true);
                        break;
                    case R.id.mnPesan:
                        setFragment(pesanFragment);
                        item.setCheckable(true);
                        break;
                    case R.id.mnProfile:
                        setFragment(profileFragment);
                        item.setCheckable(true);
                        break;
                    default:
                        setFragment(kontakFragment);
                        item.setCheckable(true);
                        break;
                }
                return true;
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Home.this, Register.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return false;
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
