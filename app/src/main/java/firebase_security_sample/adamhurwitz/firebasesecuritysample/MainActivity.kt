package firebase_security_sample.adamhurwitz.firebasesecuritysample

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var signInStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(
                this,
                FirebaseOptions.Builder()
                        .setApplicationId("1:63924060965:android:c387085babd1a8a4") // Required for Analytics.
                        .setApiKey("AIzaSyCi4h6WBX495xmzaRsLYro2_Vd9UcB3bpg") // Required for Auth.
                        .setDatabaseUrl("https://security-rules-sample.firebaseio.com") // Required for RTDB.
                        .setProjectId("security-rules-sample")
                        .build(),
                "firestoreSecuritySample")
        signInStatus = findViewById(R.id.signInStatus) as TextView

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            signInStatus.text = user.displayName
        } else {
            signInStatus.text = "logged out"
        }

        signInButton.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser == null) {
                val providers = Arrays.asList<AuthUI.IdpConfig>(
                        AuthUI.IdpConfig.GoogleBuilder().build())

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        123)
            } else {
                AuthUI.getInstance().signOut(this).addOnCompleteListener {
                    signInStatus.text = "logged out"
                }
            }
        }

        getDataButton.setOnClickListener {
            FirebaseFirestore.getInstance(FirebaseApp.getInstance("firestoreSecuritySample"))
                    .collection("testCollection").document("testDoc").get().addOnCompleteListener {
                        if (FirebaseAuth.getInstance().currentUser != null) {
                            firestoreResult.text = it.result!!.get("testField").toString()
                        }
                    }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    signInStatus.text = user.displayName
                }
            }
        }

    }
}
