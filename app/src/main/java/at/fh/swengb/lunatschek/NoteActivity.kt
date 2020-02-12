package at.fh.swengb.lunatschek

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity() {

    private var noteIdIntent:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        noteIdIntent = intent.getStringExtra(NoteListActivity.EXTRA_NOTE_ID)

        val note = NoteRepository.findNoteById(this, noteIdIntent)
        note_title_editText.setText(note?.title)
        note_content_editText.setText(note?.text)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.note_save_menu -> {

                if (note_title_editText.text.isNotEmpty() || note_content_editText.text.isNotEmpty())
                {
                    //val noteId = NoteRepository.findNoteById(this, noteIdIntent)

                    val noteContent = note_content_editText?.text?.toString() ?: ""
                    val noteTitle = note_title_editText?.text?.toString() ?: ""
                    val note = Note(noteIdIntent, noteTitle, noteContent, true)

                    NoteRepository.addNote(this, note)

                    val sharedPreferences= getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

                    NoteRepository.addOrUpdateNote(sharedPreferences.getString("KEY_ACCESS_TOKEN", null) ?: "NotFound", note,
                        success =
                        {
                            NoteRepository.addNote(this, it)
                            val resultIntent = Intent()
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        },
                        error =
                        {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                            val resultIntent = Intent()
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    )

                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
