package at.fh.swengb.lunatschek

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T

// USERNAME: swengb02
// PASSWORD: 3.minutes.havana.67

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        if (sharedPreferences.getString("KEY_ACCESS_TOKEN", null) != null){
            val intent = Intent(this, NoteListActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_main)

        main_login_button.setOnClickListener {
            val intent = Intent(this, NoteListActivity::class.java)
            if(main_username_editText.text.isNotEmpty() || main_password_editText.text.isNotEmpty())
            {
                val authReq = AuthRequest(main_username_editText.text.toString(), main_password_editText.text.toString())
                NoteRepository.login(authReq,
                    success = {
                         sharedPreferences.edit().putString("KEY_ACCESS_TOKEN", it.token).apply()
                         startActivity(intent)
                         finish()
                    },
                    error = {
                         Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    }
                )
            }

        }
    }
}
