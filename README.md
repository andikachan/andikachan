# NDiChan - Streaming Anime & Reading Manga

Aplikasi Android modern dan elegan untuk streaming video anime dan membaca komik/manga secara legal dan cepat dengan tema **Dark Gold Premium** dan gaya desain minimalis ala iOS.

---

## Fitur Utama

- **Home / Beranda**:
  - Hero Slider Banner interaktif
  - Quick Resume Bar (Lanjutkan Nonton / Baca dengan progress indicator)
  - Episode Terbaru & Anime Terpopuler
  - Manga Populer Hari Ini & Update Rilis Terbaru
- **Katalog Anime**:
  - Filter Populer, Episode Baru, Sedang Tayang (Ongoing), dan Kategori Genre
  - Jadwal Rilis Mingguan (Senin - Minggu)
- **Detail & Video Streaming Anime**:
  - Banner backdrop resolusi tinggi & info studio/status/sinopsis
  - Pencarian & pengurutan nomor episode
  - **Media3 ExoPlayer Video Player**:
    - Kontrol custom Dark Gold
    - Pemilihan Server & Resolusi video (360p, 480p, 720p, 1080p)
    - Tombol Maju & Mundur 10 detik
    - Navigasi Episode Selanjutnya otomatis
    - Penyimpanan riwayat durasi tonton otomatis ke Room DB
- **Katalog & Pembaca Manga**:
  - Filter Populer, Terbaru, Project, Semua Komik, dan Filter Multi-Genre
  - Detail Komik dengan chapter lengkap & rekomendasi komik terkait
  - **Webtoon & Continuous Scroll Reader**:
    - Mode baca vertikal continuous
    - Indikator halaman mengambang (misal `Halaman 12 / 45`)
    - Chapter Switcher Modal Bottom Sheet
    - Tombol Chapter Sebelumnya & Selanjutnya
    - Penyimpanan riwayat halaman baca terakhir otomatis ke Room DB
- **Pencarian Global**:
  - Real-time live search dengan debouncing 500ms
  - Tab kategori Anime dan Manga
- **Koleksi & Riwayat (Library)**:
  - Bookmark anime & manga tersimpan offline
  - Riwayat tonton & baca dengan progress bar
  - Fitur hapus item atau bersihkan seluruh riwayat
- **Pengaturan & Optimalisasi**:
  - Cache manager (menghitung dan membersihkan cache HTTP & Coil)
  - Preferensi pemutar video & pembaca manga
  - Desain ringan dan hemat baterai / RAM / CPU untuk perangkat lama maupun baru

---

## Tech Stack & Arsitektur

- **Bahasa**: 100% Kotlin
- **UI Framework**: Jetpack Compose + Material 3 + Navigation Compose
- **Arsitektur**: MVVM + Clean Architecture + Repository Pattern
- **Dependency Injection**: Dagger Hilt
- **Network**: Retrofit 2 + OkHttp 3 + Gson + Caching
- **Database Lokal**: Room Database (Entity, DAO, Flow)
- **Image Loader**: Coil Compose (Memory & Disk Cache teroptimasi)
- **Media Player**: AndroidX Media3 ExoPlayer
- **Gaya Desain**: Dark Charcoal (`#0D0E11`), Gold Accent (`#D4AF37`), Smooth Transitions, Tanpa Emoji

---

## Cara Build & Menjalankan di Android Studio

1. Buka Android Studio (Hedgehog / Iguana / Jellyfish / Ladybug atau versi lebih baru).
2. Pilih **Open** dan arahkan ke folder project ini.
3. Biarkan Gradle melakukan sync dependensi.
4. Hubungkan perangkat Android fisik atau Emulator (Android 7.0 / SDK 24 ke atas).
5. Klik **Run 'app'** atau jalankan command:
   ```bash
   ./gradlew assembleDebug
   ```
   File APK output akan berada di: `app/build/outputs/apk/debug/app-debug.apk`.
