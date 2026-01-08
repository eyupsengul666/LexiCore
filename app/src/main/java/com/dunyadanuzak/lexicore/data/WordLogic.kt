package com.dunyadanuzak.lexicore.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Locale
import javax.inject.Inject

private val TURKISH_LOCALE: Locale = Locale.Builder().setLanguage("tr").setRegion("TR").build()

@Entity(
    tableName = "words",
    indices = [
        Index(value = ["length", "word"]),
        Index(value = ["word"], unique = true)
    ]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val word: String,
    val length: Int
)

@Dao
interface WordDao {
    @Query("SELECT COUNT(*) FROM words")
    suspend fun getCount(): Int

    @Query("SELECT word FROM words WHERE length <= :maxLen AND length >= 2 ORDER BY length, word")
    suspend fun findPotentialWords(maxLen: Int): List<String>
}

@Database(entities = [WordEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}

class WordRepository @Inject constructor(
    private val wordDao: WordDao
) {


    fun getWords(letters: String): Flow<Result<Map<Int, List<String>>>> = flow<Result<Map<Int, List<String>>>> {
        try {
            val sanitized = letters.lowercase(TURKISH_LOCALE).filter { it.isLetter() }
            if (sanitized.isEmpty()) {
                emit(Result.success(emptyMap()))
                return@flow
            }
            
            val userCounts = mutableMapOf<Char, Int>()
            for (char in sanitized) {
                userCounts[char] = userCounts.getOrDefault(char, 0) + 1
            }
            
            val potential = wordDao.findPotentialWords(sanitized.length)
            val filtered: Map<Int, List<String>> = potential.filter { word ->
                if (word.length > sanitized.length) return@filter false
                
                val wordCounts = mutableMapOf<Char, Int>()
                var possible = true
                for (char in word) {
                    val count = wordCounts.getOrDefault(char, 0) + 1
                    wordCounts[char] = count
                    if (count > userCounts.getOrDefault(char, 0)) {
                        possible = false
                        break
                    }
                }
                possible
            }
            .groupBy { it.length }
            .toSortedMap(reverseOrder())
            
            emit(Result.success(filtered))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
