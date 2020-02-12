package at.fh.swengb.lunatschek

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NoteRepository {
    private val notes: List<Note>

    init {
        notes = listOf(
            Note("0","TestNote1","testcontent1",false),
            Note("1","TestNote2","testcontent2",false),
            Note("2","TestNote3","testcontent3",false)
        )
    }

    fun notesListRepository(): List<Note> {
        return notes
    }

    fun addNote(context: Context, note: Note) {
        val applicationContext = context.applicationContext
        val db = NoteDatabase.getDatabase(applicationContext)
        db.NoteDao.insert(note)
    }

    fun findNoteById(context: Context, id: String): Note? {
        val applicationContext = context.applicationContext
        val db = NoteDatabase.getDatabase(applicationContext)
        return db.NoteDao.findNoteById(id)
    }

    fun notesListDatabase(context: Context): List<Note>{
        val applicationContext = context.applicationContext
        val db = NoteDatabase.getDatabase(applicationContext)
        return db.NoteDao.getAllNotes()
    }

    fun deleteAllNotes(context: Context) {
        val applicationContext = context.applicationContext
        val db = NoteDatabase.getDatabase(applicationContext)
        db.NoteDao.deleteAllNotes()
    }

    fun notes(accessToken:String, lastSync:Long, success: (notes: NotesResponse) -> Unit, error: (errorMessage: String) -> Unit) {
        NotesApi.retrofitService.notes(accessToken, lastSync).enqueue(object: Callback<NotesResponse>{
            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                error("The call 'notes' failed")
            }

            override fun onResponse(call: Call<NotesResponse>, response: Response<NotesResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                } else {
                    error("Sync failed")
                }
            }

        })
    }

    fun login(body: AuthRequest, success: (response: AuthResponse) -> Unit, error: (errorMessage: String) -> Unit) {
        NotesApi.retrofitService.login(body).enqueue(object: Callback<AuthResponse>{
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                error("The call 'login' failed")
            }

            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                } else {
                    error("Login failed")
                }
            }

        })
    }

    fun addOrUpdateNote(accessToken: String, body: Note, success: (note:Note) -> Unit, error: (errorMessage: String) -> Unit) {
        NotesApi.retrofitService.addOrUpdateNote(accessToken, body).enqueue(object: Callback<Note> {
            override fun onFailure(call: Call<Note>, t: Throwable) {
                error("The call 'addOrUpdateNote' failed")
            }

            override fun onResponse(call: Call<Note>, response: Response<Note>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                } else {
                    error("Update failed")
                }
            }
        })
    }
}