package at.fh.swengb.lunatschek

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(Note: Note)

    @Query("DELETE FROM Note")
    fun deleteAllNotes()

    @Query("SELECT * FROM Note where id = :id")
    fun findNoteById(id:String): Note?

    @Query("SELECT * FROM Note")
    fun getAllNotes(): List<Note>
}