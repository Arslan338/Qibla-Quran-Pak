package com.stickers.qiblaquranpak.data.quran

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import android.util.Xml

data class Surah(
    val number: Int,
    val ayahCount: Int,
    val startIndex: Int,
    val nameArabic: String,
    val nameTransliterated: String,
    val nameEnglish: String,
    val isMeccan: Boolean,
    val revelationOrder: Int,
)

data class Juz(val number: Int, val surah: Int, val ayah: Int)

data class Ayah(
    val surah: Int,
    val number: Int,
    val arabic: String,
    val translation: String,
    val juz: Int,
    val sajda: Boolean,
)

enum class Translation(val asset: String, val label: String, val rtl: Boolean) {
    ENGLISH("quran/en_saheeh.txt", "English - Saheeh International", false),
    URDU("quran/ur_junagarhi.txt", "اردو - محمد جوناگڑھی", true),
}

/**
 * Offline Quran: Tanzil Uthmani text + metadata, and bundled QuranEnc translations.
 * Everything is parsed once on a background thread and kept in memory (~6 MB of strings).
 */
class QuranRepository(private val context: Context) {

    private val mutex = Mutex()
    private var meta: Meta? = null
    private var arabic: Array<String>? = null
    private val translations = HashMap<Translation, Array<String>>()

    private class Meta(val surahs: List<Surah>, val juz: List<Juz>, val sajdas: Set<Long>)

    suspend fun surahs(): List<Surah> = meta().surahs

    suspend fun juzList(): List<Juz> = meta().juz

    suspend fun surah(number: Int): Surah = meta().surahs[number - 1]

    suspend fun ayahs(surahNumber: Int, translation: Translation?): List<Ayah> = withContext(Dispatchers.Default) {
        val m = meta()
        val s = m.surahs[surahNumber - 1]
        val ar = arabic()
        val tr = translation?.let { translation(it) }
        (0 until s.ayahCount).map { i ->
            val idx = s.startIndex + i
            // Tanzil prefixes the first ayah of every surah (except 1 and 9) with the Bismillah;
            // the reader shows it as a separate header instead.
            val text = if (i == 0 && surahNumber != 1 && surahNumber != 9 && ar[idx].startsWith(ar[0])) {
                ar[idx].substring(ar[0].length).trim()
            } else ar[idx]
            Ayah(
                surah = surahNumber,
                number = i + 1,
                arabic = text,
                translation = tr?.get(idx).orEmpty(),
                juz = juzOf(m.juz, surahNumber, i + 1),
                sajda = m.sajdas.contains(key(surahNumber, i + 1)),
            )
        }
    }

    suspend fun ayah(surah: Int, ayah: Int, translation: Translation?): Ayah =
        ayahs(surah, translation)[ayah - 1]

    /** Search transliterated/English surah names and translation text. */
    suspend fun search(query: String, translation: Translation): List<Ayah> = withContext(Dispatchers.Default) {
        val q = query.trim()
        if (q.length < 3) return@withContext emptyList()
        val m = meta()
        val ar = arabic()
        val tr = translation(translation)
        val out = ArrayList<Ayah>()
        for (s in m.surahs) {
            for (i in 0 until s.ayahCount) {
                val idx = s.startIndex + i
                if (tr[idx].contains(q, ignoreCase = true) || ar[idx].contains(q)) {
                    out += Ayah(s.number, i + 1, ar[idx], tr[idx], juzOf(m.juz, s.number, i + 1), false)
                    if (out.size >= 200) return@withContext out
                }
            }
        }
        out
    }

    private fun juzOf(juz: List<Juz>, surah: Int, ayah: Int): Int {
        var result = 1
        for (j in juz) {
            if (j.surah < surah || (j.surah == surah && j.ayah <= ayah)) result = j.number else break
        }
        return result
    }

    private fun key(s: Int, a: Int) = s.toLong() * 1000 + a

    private suspend fun meta(): Meta = mutex.withLock {
        meta ?: withContext(Dispatchers.IO) { parseMeta() }.also { meta = it }
    }

    private suspend fun arabic(): Array<String> = mutex.withLock {
        arabic ?: withContext(Dispatchers.IO) { readLines("quran/quran_uthmani.txt") }.also { arabic = it }
    }

    private suspend fun translation(t: Translation): Array<String> = mutex.withLock {
        translations[t] ?: withContext(Dispatchers.IO) { readLines(t.asset) }.also { translations[t] = it }
    }

    /** Reads "sura|aya|text" lines into a flat array indexed by global ayah index. */
    private fun readLines(asset: String): Array<String> {
        val out = arrayOfNulls<String>(TOTAL_AYAHS)
        var i = 0
        context.assets.open(asset).bufferedReader().useLines { lines ->
            for (line in lines) {
                if (line.isEmpty() || line[0] == '#') continue
                val second = line.indexOf('|', line.indexOf('|') + 1)
                if (second < 0 || i >= TOTAL_AYAHS) continue
                out[i++] = line.substring(second + 1)
            }
        }
        return Array(TOTAL_AYAHS) { out[it].orEmpty() }
    }

    private fun parseMeta(): Meta {
        val surahs = ArrayList<Surah>(114)
        val juz = ArrayList<Juz>(30)
        val sajdas = HashSet<Long>()
        context.assets.open("quran/quran_data.xml").use { input ->
            val p = Xml.newPullParser()
            p.setInput(input, "UTF-8")
            while (p.next() != XmlPullParser.END_DOCUMENT) {
                if (p.eventType != XmlPullParser.START_TAG) continue
                when (p.name) {
                    "sura" -> surahs += Surah(
                        number = p.attr("index").toInt(),
                        ayahCount = p.attr("ayas").toInt(),
                        startIndex = p.attr("start").toInt(),
                        nameArabic = p.attr("name"),
                        nameTransliterated = p.attr("tname"),
                        nameEnglish = p.attr("ename"),
                        isMeccan = p.attr("type") == "Meccan",
                        revelationOrder = p.attr("order").toInt(),
                    )
                    "juz" -> juz += Juz(p.attr("index").toInt(), p.attr("sura").toInt(), p.attr("aya").toInt())
                    "sajda" -> sajdas += key(p.attr("sura").toInt(), p.attr("aya").toInt())
                }
            }
        }
        return Meta(surahs, juz, sajdas)
    }

    private fun XmlPullParser.attr(name: String): String = getAttributeValue(null, name)

    companion object {
        const val TOTAL_AYAHS = 6236
    }
}
