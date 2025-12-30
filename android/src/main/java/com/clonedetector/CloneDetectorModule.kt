package com.clonedetector

import android.content.pm.PackageManager
import android.os.Build
import com.facebook.react.bridge.*

class CloneDetectorModule(
    private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = "CloneDetectorModule"

    @ReactMethod
    fun detectClone(promise: Promise) {
        try {
            var score = 0

            // The application's data directory path can reveal the execution environment.
            // On a normal Android installation, apps running under the primary user
            // are typically located under "/data/user/0/<package_name>".
            //
            // App cloners, dual apps, work profiles, and sandboxed environments often
            // redirect the app's data directory to alternative paths.
            //
            // If the data directory does not match the expected primary-user path,
            // it strongly suggests that the app is running inside a sandbox or cloned environment.
            val dataDir = reactContext.applicationInfo.dataDir
            val isSandboxSuspicious = !dataDir.contains("/data/user/0/")
            if (isSandboxSuspicious) score += 50

            // Android assigns a unique UID to each app process.
            // The UID encodes the Android user/profile ID in its higher digits.
            // By dividing the UID by 100000, we can infer the userId the app is running under.
            //
            // On most consumer devices, the primary user/profile IDs are small (usually 0–10).
            // App cloners, parallel space apps, and sandbox environments often run apps
            // under higher user/profile IDs.
            //
            // If the inferred userId is unusually high, this is a strong signal that the app
            // may be running inside a cloned or virtualized environment.
            val uid = reactContext.applicationInfo.uid
            val userId = uid / 100000
            if (userId > 10) score += 30

            val knownCloners = listOf(
                "com.parallel.space",
                "com.lbe.parallel",
                "com.dualspace",
                "com.excelliance.multiaccounts",
                "com.applisto.appcloner"
            )

            val pm: PackageManager = reactContext.packageManager
            val installed = pm.getInstalledPackages(0)

            val hasKnownCloner = installed.any { pkg ->
                knownCloners.any { cloner ->
                    pkg.packageName.contains(cloner)
                }
            }
            if (hasKnownCloner) score += 40

            val result = Arguments.createMap().apply {
                putBoolean("isCloned", score >= 50)
                putInt("score", score)

                val signals = Arguments.createMap().apply {
                    putBoolean("sandbox", isSandboxSuspicious)
                    putInt("userId", userId)
                    putBoolean("knownCloner", hasKnownCloner)
                }

                putMap("signals", signals)
            }

            promise.resolve(result)

        } catch (e: Exception) {
            promise.reject("CLONE_ERROR", e)
        }
    }
}
