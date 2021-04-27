package ac.id.poltektegal.catatanku.config

import ac.id.poltektegal.catatanku.Config.Notes
import androidx.room.*

@Dao
interface DaoNotes {
    @Query("SELECT*FROM notes")
    fun GetAll():List<Notes>
    @Insert
    fun insert(notes: Notes)
    @Update
    fun update(notes: Notes)
    @Delete
    fun delete(notes: Notes)
}
