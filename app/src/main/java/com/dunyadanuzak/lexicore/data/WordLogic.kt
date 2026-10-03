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
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

private val TURKISH_LOCALE: Locale = Locale.Builder().setLanguage("tr").setRegion("TR").build()
private val TURKISH_COLLATOR: Collator = Collator.getInstance(TURKISH_LOCALE).apply {
    strength = Collator.PRIMARY
}

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
    @Query("SELECT word FROM words WHERE length <= :maxLen AND length >= 2")
    suspend fun findPotentialWords(maxLen: Int): List<String>
}

@Database(entities = [WordEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}

class WordRepository @Inject constructor(
    private val wordDao: WordDao
) {

    fun getWords(letters: String): Flow<Result<Map<Int, List<String>>>> = flow {
        emit(runCatching { findWords(letters) })
    }.flowOn(Dispatchers.IO)

    private suspend fun findWords(letters: String): Map<Int, List<String>> {
        val sanitized = letters.lowercase(TURKISH_LOCALE).filter { it.isLetter() }
        if (sanitized.isEmpty()) return emptyMap()

        val available = sanitized.groupingBy { it.withoutCircumflex() }.eachCount()
        return wordDao.findPotentialWords(sanitized.length)
            .filter { word ->
                val used = mutableMapOf<Char, Int>()
                word.all { char ->
                    val letter = char.withoutCircumflex()
                    val count = used.getOrDefault(letter, 0) + 1
                    used[letter] = count
                    count <= available.getOrDefault(letter, 0)
                }
            }
            .groupBy { it.length }
            .mapValues { (_, words) -> words.sortedWith(TURKISH_COLLATOR) }
            .toSortedMap(reverseOrder())
    }

    private fun Char.withoutCircumflex(): Char = when (this) {
        'â' -> 'a'
        'î' -> 'i'
        'û' -> 'u'
        else -> this
    }
}
