package ac.id.poltektegal.catatanku.config

import ac.id.poltektegal.catatanku.Config.Notes
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Notes::class),version = 1)
abstract class DatabaseNotes:RoomDatabase(){
abstract fun notesDao():DaoNotes
companion object {
    private var INSTANCE:DatabaseNotes? = null
        fun getInstance(context: Context):DatabaseNotes?{
            if (INSTANCE==null){
                synchronized(DatabaseNotes::class){
                    INSTANCE= Room.databaseBuilder(context.applicationContext,
                    DatabaseNotes::class.java, "dbsimplenotes.db")
                        .build()
                }
            }
            return INSTANCE
        }
    }
}