# VoxelLive Android 2.0.2-alpha2 — cara build dan uji

Paket ini adalah **source test-candidate**, bukan APK rilis yang sudah lulus uji perangkat.

## File yang diperbarui

- `build.gradle` — Chaquopy 17.2.0.
- `app/build.gradle` — Python 3.14, arm64-v8a, target Android 34.
- `app/src/main/python/requirements.txt` — TikTokLive 7.0.1 dan pasangan Pydantic yang dipin.
- `app/src/main/python/server.py` — reconnect TikTok dan placeholder PlayerName memakai username pengirim bila tersedia.
- `app/src/main/python/web/app.js` — Auto Save Gift Mapping, Follow Mapping, dan cooldown; status reconnect.
- `app/src/main/python/web/index.html` dan `style.css` — status Auto Save dan petunjuk APK.
- `app/src/main/java/com/voxellive/android/MainActivity.java` — meminta izin notifikasi Android 13+.
- `app/src/main/java/com/voxellive/android/VoxelLiveService.java` serta `AndroidManifest.xml` — foreground service dan bounded partial wake lock.

## Build di Android Studio

1. Ekstrak ZIP ini.
2. Buka folder `VoxelLive-Android` sebagai project di Android Studio.
3. Pastikan JDK 17 dipilih, lalu tekan **Sync Project with Gradle Files**.
4. Tunggu sampai semua dependency berhasil diunduh.
5. Pilih **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. Jika build berhasil, APK debug berada di `app/build/outputs/apk/debug/app-debug.apk`.

Build ini arm64-v8a saja. Jangan gunakan APK ini pada perangkat 32-bit-only.

## Uji saat menjalankan LIVE

1. Install APK dan izinkan notifikasi VoxelLive.
2. Buka VoxelLive dan login memakai metode akses yang saat ini dikonfigurasi di Cloud Worker.
3. Buka Minecraft Bedrock, masuk world, dan aktifkan Cheats serta izin operator yang diperlukan.
4. Jalankan command `/connect` memakai alamat WebSocket yang ditampilkan dashboard.
5. Pastikan status Minecraft berubah menjadi **Connected**.
6. Masukkan username TikTok yang sedang LIVE secara publik, lalu tekan **Connect**.
7. Tunggu status **Connected** sebelum menguji gift/follow.
8. Buat mapping gift dengan command seperti `/say PlayerName mengirim NameGift`. Nama pengirim yang dipakai placeholder `PlayerName` adalah username TikTok bila tersedia.
9. Pastikan setiap perubahan Gift Mapping dan Follow Mapping menunjukkan status tersimpan otomatis.
10. Biarkan notifikasi foreground VoxelLive terlihat selama LIVE. Matikan optimasi baterai untuk VoxelLive bila Android menghentikan proses latar belakang.

## Batas yang perlu diketahui

- TikTokLive adalah integrasi tidak resmi (reverse-engineered); perubahan internal TikTok dapat memutus kompatibilitas.
- APK ini menggunakan kandidat wheel `pydantic-core` Android dari index tambahan. Penerbit wheel menyatakan wheel diuji di Termux; Chaquopy disebut sebagai runtime lain yang diharapkan kompatibel, tetapi belum diuji CI secara resmi. Karena itu import native di Chaquopy tetap harus dibuktikan pada hasil build dan perangkat nyata.
- Sesi TikTok LIVE sungguhan belum dites pada paket ini.
- UI login Nama + Password + Redeem Code yang baru belum diimplementasikan dalam paket ini; alur akses cloud saat ini tetap mengikuti Worker yang sedang terpasang.
- Source check lokal yang sudah dilakukan: `server.py` lolos `py_compile`, `app.js` lolos `node --check`, dan smoke test lokal untuk multi-command, placeholder, Follow config, serta penyimpanan preset lulus.
- Build Gradle/APK belum dilakukan di lingkungan pembuat source ini karena Android SDK/Gradle build environment tidak tersedia.

Jika Gradle Sync gagal, simpan dan kirim seluruh teks error pertama dari jendela **Build**. Jangan menginstal `pydantic` di Termux sebagai pengganti; environment Python Termux tidak memperbaiki dependency yang dibundel di APK.
