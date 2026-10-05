package com.example.wife

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Firebase dihapus dari aplikasi ini. Gemini dipanggil langsung via REST ke
 * generativelanguage.googleapis.com memakai key dari local.properties.
 *
 * Konsekuensi disengaja: tidak ada lagi blok FirebaseOptions berisi key hardcoded,
 * tidak butuh google-services.json, dan 401 UNAUTHENTICATED dari proxy Firebase AI Logic
 * yang tidak bisa dijelaskan sudah hilang.
 */
@HiltAndroidApp
class WifeApplication : Application()
