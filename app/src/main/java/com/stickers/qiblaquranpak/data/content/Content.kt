package com.stickers.qiblaquranpak.data.content

data class HomeDua(val id: String, val title: String, val arabic: String)

data class IslamicPlace(val id: String, val name: String, val imageUrl: String?, val description: String)

object Content {
    val homeDuas = listOf(
        HomeDua(
            "waking_up", "When Waking up",
            "لَا إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَلَا إِلَهَ إِلاَّ اللَّهُ، وَاللَّهُ أَكْبَرُ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلاَّ بِاللَّهِ الْعَلِيِّ الْعَظِيمِ، رَبِّ اغْفِرْ لِي",
        ),
        HomeDua("surah_infitar", "Surah: Infitar", "يَعْلَمُونَ مَا تَفْعَلُونَ"),
        HomeDua(
            "before_sleep", "Before Sleeping",
            "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
        ),
        HomeDua(
            "leaving_home", "Leaving the Home",
            "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
        ),
    )

    val places = listOf(
        IslamicPlace("kaaba", "Kaaba (Makkah, Saudi Arabia)", null, "The Kaaba is the most sacred site in Islam, located at the centre of Masjid al-Haram in Makkah. Muslims around the world face it in prayer."),
        IslamicPlace("masjid_nabawi", "Masjid an-Nabawi (Madinah, Saudi Arabia)", null, "The Prophet's Mosque in Madinah, built by Prophet Muhammad ﷺ after the Hijrah. It contains his resting place under the Green Dome."),
        IslamicPlace("zamzam", "Zamzam Well (Makkah, Saudi Arabia)", null, "The blessed well inside Masjid al-Haram that sprang for Hajar and her son Ismail (AS)."),
        IslamicPlace("al_aqsa", "Masjid al-Aqsa (Jerusalem)", null, "The first Qibla of Muslims and the third holiest mosque in Islam."),
        IslamicPlace("quba", "Masjid Quba (Madinah)", null, "The first mosque built in Islam, founded by the Prophet ﷺ on his arrival at Madinah."),
        IslamicPlace("hira", "Cave Hira (Jabal al-Nour, Makkah)", null, "The cave where the first verses of the Quran were revealed to the Prophet ﷺ."),
        IslamicPlace("arafat", "Mount Arafat (Makkah)", null, "The plain where pilgrims stand on the 9th of Dhul-Hijjah, the most important rite of Hajj."),
    )
}
