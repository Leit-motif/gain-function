package com.example.gainfunction.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gainfunction.data.models.ExerciseDefinition
import com.example.gainfunction.data.models.ExerciseLog
import com.example.gainfunction.data.models.SetEntry
import com.example.gainfunction.data.models.Template
import com.example.gainfunction.data.models.TemplateExerciseEntry
import com.example.gainfunction.data.models.Workout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Define your entities here, for now, it's an empty array
// import com.example.gainfunction.data.models.YourEntity

@Database(
    entities = [
        Workout::class,
        ExerciseLog::class,
        SetEntry::class,
        Template::class,
        TemplateExerciseEntry::class,
        ExerciseDefinition::class
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
    abstract fun exerciseDefinitionDao(): ExerciseDefinitionDao

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
                // Add callback to prepopulate the database with default exercises
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                // Populate the database in the background
                                preloadExerciseDefinitions(database.exerciseDefinitionDao())
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        /**
         * Preload the database with common exercise definitions.
         */
        private suspend fun preloadExerciseDefinitions(exerciseDefinitionDao: ExerciseDefinitionDao) {
            // List of common strength training exercises
            val exercises = listOf(
                // Upper Body - Chest
                "Bench Press",
                "Incline Bench Press",
                "Decline Bench Press",
                "Dumbbell Bench Press",
                "Incline Dumbbell Press",
                "Decline Dumbbell Press",
                "Chest Fly",
                "Cable Crossover",
                "Push-Up",
                "Dips",
                
                // Upper Body - Back
                "Pull-Up",
                "Chin-Up",
                "Lat Pulldown",
                "Seated Cable Row",
                "Bent-Over Row",
                "T-Bar Row",
                "Dumbbell Row",
                "Face Pull",
                "Reverse Fly",
                "Deadlift",
                
                // Upper Body - Shoulders
                "Overhead Press",
                "Military Press",
                "Arnold Press",
                "Lateral Raise",
                "Front Raise",
                "Upright Row",
                "Shrug",
                
                // Upper Body - Arms
                "Bicep Curl",
                "Hammer Curl",
                "Preacher Curl",
                "Concentration Curl",
                "Tricep Extension",
                "Tricep Pushdown",
                "Skull Crusher",
                "Close-Grip Bench Press",
                
                // Lower Body - Legs
                "Squat",
                "Front Squat",
                "Leg Press",
                "Lunge",
                "Romanian Deadlift",
                "Leg Extension",
                "Leg Curl",
                "Calf Raise",
                "Hip Thrust",
                "Glute Bridge",
                
                // Core
                "Crunch",
                "Sit-Up",
                "Plank",
                "Russian Twist",
                "Leg Raise",
                "Mountain Climber",
                "Ab Wheel Rollout"
            )
            
            // Create exercise definition objects and insert them
            val exerciseDefinitions = exercises.map { name ->
                ExerciseDefinition(name = name, isCustom = false)
            }
            
            exerciseDefinitionDao.insertExerciseDefinitions(exerciseDefinitions)
        }
    }
} 