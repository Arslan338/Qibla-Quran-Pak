package com.stickers.qiblaquranpak.data.content

data class Lesson(val id: Int, val title: String, val subtitle: String)

/** Sample course outlines; the Learning screens fill in each lesson's content. */
object Lessons {
    val qaida: List<Lesson> = listOf(
        Lesson(1, "Huroof Mufradat", "Single letters (ا - ي)"),
        Lesson(2, "Huroof Murakkabat", "Joined letters"),
        Lesson(3, "Huroof Muqatta'at", "Disjointed letters"),
        Lesson(4, "Harakat", "Fatha, Kasra, Damma"),
        Lesson(5, "Tanween", "Double harakat"),
        Lesson(6, "Harakat & Tanween Exercise", "Practice"),
        Lesson(7, "Standing Harakat", "Khari Zabar, Zer, Pesh"),
        Lesson(8, "Huroof Maddah", "Long vowels"),
        Lesson(9, "Huroof Leen", "Soft letters"),
        Lesson(10, "Sukoon / Jazm", "Resting letters"),
        Lesson(11, "Tashdeed", "Doubled letters"),
        Lesson(12, "Tashdeed with Sukoon", "Practice"),
    )

    val quran: List<Lesson> = listOf(
        Lesson(1, "Surah Al-Fatiha", "The Opening"),
        Lesson(2, "Surah An-Nas", "Mankind"),
        Lesson(3, "Surah Al-Falaq", "The Daybreak"),
        Lesson(4, "Surah Al-Ikhlas", "Sincerity"),
        Lesson(5, "Surah Al-Masad", "The Palm Fibre"),
        Lesson(6, "Surah An-Nasr", "The Help"),
        Lesson(7, "Surah Al-Kafirun", "The Disbelievers"),
        Lesson(8, "Surah Al-Kawthar", "Abundance"),
        Lesson(9, "Surah Al-Ma'un", "Small Kindnesses"),
        Lesson(10, "Surah Quraysh", "Quraysh"),
    )
}
