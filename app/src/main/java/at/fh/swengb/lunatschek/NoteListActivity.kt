package at.fh.swengb.lunatschek

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_note_list.*
import java.lang.System.currentTimeMillis
import java.util.*

class NoteListActivity : AppCompatActivity() {

    companion object {
        val EXTRA_NOTE_ID = "NOTE_ID_EXTRA"
        val VIEW_REQUEST = 1
    }

    val noteAdapter = NoteAdapter {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra(EXTRA_NOTE_ID, it.id)
        startActivityForResult(intent, VIEW_REQUEST)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)


        note_recycler_view.layoutManager = StaggeredGridLayoutManager(2,1)
        note_recycler_view.adapter = noteAdapter
        noteAdapter.updateList(NoteRepository.notesListDatabase(this))

        syncNotes();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == VIEW_REQUEST && resultCode == Activity.RESULT_OK){
            noteAdapter.updateList(NoteRepository.notesListDatabase(this))
            syncNotes();
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.note_list_new_menu -> {

                val uuidString = UUID.randomUUID().toString()

                val intent = Intent(this, NoteActivity::class.java)
                intent.putExtra(EXTRA_NOTE_ID, uuidString)
                startActivityForResult(intent, VIEW_REQUEST)

                true
            }
            R.id.note_list_logout_menu -> {

                NoteRepository.deleteAllNotes(this) //delete all local notes

                val sharedPreferences= getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().remove("KEY_ACCESS_TOKEN").apply()
                sharedPreferences.edit().remove("KEY_LAST_SYNC").apply()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun syncNotes(){
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        NoteRepository.notes(sharedPreferences.getString("KEY_ACCESS_TOKEN", null) ?: "NotFound", sharedPreferences.getLong("KEY_LAST_SYNC", 0),
            success =
            {
                sharedPreferences.edit().putLong("KEY_LAST_SYNC", it.lastSync).apply()
                for(singleNote in it.notes)
                {
                    NoteRepository.addNote(this,singleNote)
                }

                noteAdapter.updateList(NoteRepository.notesListDatabase(this))
                //noteAdapter.updateList(it.notes)
            },
            error =
            {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        )
    }
}
