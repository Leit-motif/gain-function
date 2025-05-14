package com.example.gainfunction.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gainfunction.data.models.ExerciseLog
import com.example.gainfunction.data.models.SetEntry
import com.example.gainfunction.data.models.Template
import com.example.gainfunction.data.models.TemplateExerciseEntry
import com.example.gainfunction.data.models.Workout

// Define your entities here, for now, it's an empty array
// import com.example.gainfunction.data.models.YourEntity

@Database(
    entities = [
        Workout::class,
        ExerciseLog::class,
        SetEntry::class,
        Template::class,
        TemplateExerciseEntry::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseLogDao(): ExerciseLogDao
    abstract fun setEntryDao(): SetEntryDao
    abstract fun templateDao(): TemplateDao
    abstract fun templateExerciseDao(): TemplateExerciseDao

    companion object {
        // Volatile annotation ensures that writes to this field are immediately
        // made visible to other threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gain_function_database"
                )
                // Wipes and rebuilds instead of migrating if no Migration object.
                // Migration is not part of this task.
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
} 