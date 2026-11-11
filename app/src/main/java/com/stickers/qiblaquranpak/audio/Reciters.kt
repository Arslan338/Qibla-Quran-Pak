package com.stickers.qiblaquranpak.audio

data class Reciter(
    val id: String,
    val name: String,
    /** everyayah.com folder for per-ayah audio (enables ayah highlighting). */
    val ayahFolder: String?,
    /** mp3quran.net server for full-surah audio, ends with '/'. */
    val surahServer: String,
) {
    fun ayahUrl(surah: Int, ayah: Int): String? =
        ayahFolder?.let { "https://everyayah.com/data/$it/%03d%03d.mp3".format(surah, ayah) }

    fun surahUrl(surah: Int): String = "$surahServer%03d.mp3".format(surah)
}

object Reciters {
    private const val MP3Q = "https://cdn.mp3quran.net/audio"

    val all = listOf(
        Reciter("alafasy", "Mishary Rashid Alafasy", "Alafasy_128kbps", "$MP3Q/mishary-alafasy/r1/"),
        Reciter("abdulbasit_murattal", "Abdul Basit Abdul Samad (Murattal)", "Abdul_Basit_Murattal_192kbps", "$MP3Q/abdulbasit-abdulsamad/r3/"),
        Reciter("abdulbasit_mujawwad", "Abdul Basit Abdul Samad (Mujawwad)", "Abdul_Basit_Mujawwad_128kbps", "$MP3Q/abdulbasit-abdulsamad/r1/"),
        Reciter("sudais", "Abdur-Rahman As-Sudais", "Abdurrahmaan_As-Sudais_192kbps", "$MP3Q/abdulrahman-sudais/r1/"),
        Reciter("shuraim", "Saud Ash-Shuraim", "Saood_ash-Shuraym_128kbps", "$MP3Q/saud-shuraim/r1/"),
        Reciter("husary", "Mahmoud Khalil Al-Husary", "Husary_128kbps", "$MP3Q/mahmoud-husary/r1/"),
        Reciter("minshawi", "Muhammad Siddiq Al-Minshawi", "Minshawy_Murattal_128kbps", "$MP3Q/muhammad-minshawi/r1/"),
        Reciter("muaiqly", "Maher Al-Muaiqly", "MaherAlMuaiqly128kbps", "$MP3Q/maher-muaiqly/r1/"),
        Reciter("ghamdi", "Saad Al-Ghamdi", "Ghamadi_40kbps", "$MP3Q/saad-ghamdi/r1/"),
        Reciter("dosari", "Yasser Al-Dosari", "Yasser_Ad-Dussary_128kbps", "$MP3Q/yasser-dosari/r1/"),
        Reciter("shatri", "Abu Bakr Ash-Shatri", "Abu_Bakr_Ash-Shaatree_128kbps", "$MP3Q/abubakr-shatri/r1/"),
        Reciter("ajmi", "Ahmed Al-Ajmi", "ahmed_ibn_ali_al_ajamy_128kbps", "$MP3Q/ahmad-ajmi/r1/"),
        Reciter("hudhaifi", "Ali Al-Hudhaifi", "Hudhaify_128kbps", "$MP3Q/ali-hudhaifi/r1/"),
        Reciter("jibreel", "Muhammad Jibreel", "Muhammad_Jibreel_128kbps", "$MP3Q/muhammad-jibreel/r1/"),
        Reciter("basfar", "Abdullah Basfar", "Abdullah_Basfar_192kbps", "$MP3Q/abdullah-basfar/r1/"),
        Reciter("rifai", "Hani Ar-Rifai", "Hani_Rifai_192kbps", "$MP3Q/hani-rifai/r1/"),
        Reciter("qatami", "Nasser Al-Qatami", "Nasser_Alqatami_128kbps", "$MP3Q/nasser-qatami/r1/"),
        Reciter("budair", "Salah Al-Budair", "Salah_Al_Budair_128kbps", "$MP3Q/salah-budair/r1/"),
        Reciter("juhani", "Abdullah Awad Al-Juhani", "Abdullaah_3awwaad_Al-Juhaynee_128kbps", "$MP3Q/abdullah-juhani/r1/"),
        Reciter("matrood", "Abdullah Al-Matrood", "Abdullah_Matroud_128kbps", "$MP3Q/abdullah-matrood/r1/"),
        Reciter("ayyub", "Muhammad Ayyub", "Muhammad_Ayyoub_128kbps", "$MP3Q/muhammad-ayyub/r1/"),
        Reciter("abbad", "Fares Abbad", "Fares_Abbad_64kbps", "$MP3Q/fares-abbad/r1/"),
        Reciter("qahtani", "Khalid Al-Qahtani", "Khaalid_Abdullaah_al-Qahtaanee_192kbps", "$MP3Q/khalid-qahtani/r1/"),
        Reciter("jaber", "Ali Jaber", "Ali_Jaber_64kbps", "$MP3Q/ali-jaber/r1/"),
        Reciter("bukhatir", "Salah Bukhatir", "Salaah_AbdulRahman_Bukhatir_128kbps", "$MP3Q/salah-bukhatir/r1/"),
        Reciter("abkar", "Idrees Abkar", null, "$MP3Q/idrees-abkar/r1/"),
    )

    fun byId(id: String?): Reciter = all.firstOrNull { it.id == id } ?: all.first()
}
