package tk.zwander.oneuituner.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import com.android.apksig.ApkSigner
import eu.chainfire.libsuperuser.Shell
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tk.zwander.oneuituner.data.OverlayInfo
import tk.zwander.oneuituner.data.ResourceData
import tk.zwander.oneuituner.data.ResourceFileData
import tk.zwander.oneuituner.data.ResourceImageData
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate

fun Context.install(which: String, listener: ((apk: File) -> Unit)?) {
    GlobalScope.launch {
        val data = when (which) {
            Keys.clock -> {
                OverlayInfo(
                    Keys.systemuiPkg,
                    Keys.clockPkg,
                    mutableListOf<ResourceFileData>().apply {
                        val clockFormat = prefs.clockFormat
                        val qsDateFormat = prefs.qsDateFormat

                        when(prefs.clockType) {
                            prefs.clockTypeCustom -> {
                                if (clockFormat.isValidClockFormat) {
                                    add(
                                        ResourceFileData(
                                            "qs_status_bar_clock.xml",
                                            "layout",
                                            getResourceXmlFromAsset(
                                                "clock/layout",
                                                "qs_status_bar_clock_custom.xml"
                                            ).replace("h:mm a", clockFormat)
                                        )
                                    )
                                }
                            }

                            prefs.clockTypeAosp -> {
                                add(
                                    ResourceFileData(
                                        "qs_status_bar_clock.xml",
                                        "layout",
                                        getResourceXmlFromAsset(
                                            "clock/layout",
                                            "qs_status_bar_clock_aosp.xml"
                                        ).replace("h:mm a", clockFormat)
                                    )
                                )
                            }
                        }

                        if (prefs.customQsDateFormat && qsDateFormat.isValidClockFormat) {
                            add(
                                ResourceFileData(
                                    "strings.xml",
                                    "values",
                                    makeResourceXml(
                                        ResourceData(
                                            "string",
                                            "system_ui_quick_panel_date_pattern",
                                            qsDateFormat
                                        )
                                    )
                                )
                            )
                        }
                    }
                )
            }
            Keys.qs -> {
                OverlayInfo(
                    Keys.systemuiPkg,
                    Keys.qsPkg,
                    arrayListOf(
                        ResourceFileData(
                            "integers.xml",
                            "values",
                            makeResourceXml(
                                ResourceData(
                                    "integer",
                                    "quick_qs_tile_num",
                                    prefs.headerCountPortrait.toString()
                                ),
                                ResourceData(
                                    "integer",
                                    "quick_qs_tile_min_num",
                                    "2"
                                ),
                                ResourceData(
                                    "integer",
                                    "qspanel_screen_grid_columns_5",
                                    prefs.qsColCountPortrait.toString()
                                ),
                                ResourceData(
                                    "integer",
                                    "qspanel_screen_grid_rows",
                                    prefs.qsRowCountPortrait.toString()
                                )
                            )
                        ),
                        ResourceFileData(
                            "integers.xml",
                            "values-land",
                            makeResourceXml(
                                ResourceData(
                                    "integer",
                                    "quick_qs_tile_num",
                                    prefs.headerCountLandscape.toString()
                                ),
                                ResourceData(
                                    "integer",
                                    "qspanel_screen_grid_columns_5",
                                    prefs.qsColCountLandscape.toString()
                                ),
                                ResourceData(
                                    "integer",
                                    "qspanel_screen_grid_rows",
                                    prefs.qsRowCountLandscape.toString()
                                )
                            )
                        ),
                        ResourceFileData(
                            "dimens.xml",
                            "values",
                            makeResourceXml(
                                ResourceData(
                                    "dimen",
                                    "qs_tile_height_5x3_ratio",
                                    prefs.qsRowCountPortrait.run {
                                        when {
                                            this > 4 -> "9.5"
                                            this > 3 -> "8.0"
                                            else -> "7.1"
                                        }
                                    }
                                ),
                                ResourceData(
                                    "dimen",
                                    "qs_tile_icon_size_5x3_ratio",
                                    prefs.qsRowCountPortrait.run {
                                        when {
                                            this > 4 -> "21.0"
                                            else -> "19.43"
                                        }
                                    }
                                ),
                                ResourceData(
                                    "dimen",
                                    "qs_tile_text_size",
                                    prefs.qsRowCountPortrait.run {
                                        when {
                                            this > 4 -> "12sp"
                                            else -> "13sp"
                                        }
                                    }
                                )
                            )
                        ),
                        ResourceFileData(
                            "dimens.xml",
                            "values-sw410dp",
                            makeResourceXml(
                                ResourceData(
                                    "dimen",
                                    "qs_tile_text_size",
                                    prefs.qsRowCountPortrait.run {
                                        when {
                                            this > 4 -> "13sp"
                                            else -> "14sp"
                                        }
                                    }
                                )
                            )
                        )
                    ).apply {
                        if (prefs.hideQsTileBackground) {
                            add(
                                ResourceFileData(
                                    "colors.xml",
                                    "values",
                                    makeResourceXml(
                                        ResourceData(
                                            "color",
                                            "qs_tile_round_background_on",
                                            "@android:color/transparent"
                                        )
                                    )
                                )
                            )
                        }
                    }
                )
            }
            Keys.misc -> {
                OverlayInfo(
                    Keys.androidPkg,
                    Keys.miscPkg,
                    arrayListOf(
                        ResourceFileData(
                            "config.xml",
                            "values",
                            makeResourceXml(
                                mutableListOf(
                                    ResourceData(
                                        "dimen",
                                        "navigation_bar_height",
                                        "${prefs.navHeight}dp"
                                    ),
                                    ResourceData(
                                        "dimen",
                                        "navigation_bar_width",
                                        "${prefs.navHeight}dp"
                                    ),
                                    ResourceData(
                                        "dimen",
                                        "status_bar_height_portrait",
                                        "${prefs.statusBarHeight}dp"
                                    )
                                ).apply {
                                    if (prefs.oldRecents) {
                                        add(
                                            ResourceData(
                                                "string",
                                                "config_recentsComponentName",
                                                "com.android.systemui/.recents.RecentsActivity",
                                                "translatable=\"false\""
                                            )
                                        )
                                    }
                                }
                            )
                        )
                    )
                )
            }
            Keys.statusBar -> {
                OverlayInfo(
                    Keys.systemuiPkg,
                    Keys.statusBarPkg,
                    ArrayList<ResourceFileData>()
                        .apply {
                            if (prefs.leftSystemIcons) {
                                add(
                                    ResourceFileData(
                                        "status_bar.xml",
                                        "layout",
                                        getResourceXmlFromAsset("statusbar/layout", "status_bar.xml")
                                    )
                                )
                            }

                            add(
                                ResourceFileData(
                                    "bools.xml",
                                    "values",
                                    makeResourceXml(
                                        arrayListOf(
                                            ResourceData(
                                                "bool",
                                                "config_showOperatorNameInStatusBar",
                                                "${!prefs.hideStatusBarCarrier}"
                                            )
                                        )
                                    )
                                )
                            )

                            if (prefs.hideStatusBarCarrier) {
                                add(
                                    ResourceFileData(
                                        "keyguard_status_bar.xml",
                                        "layout",
                                        getResourceXmlFromAsset("statusbar/layout", "keyguard_status_bar.xml")
                                    )
                                )
                            }

                            if (prefs.disable5ge) {
                                add(
                                    ResourceFileData(
                                        "stat_sys_data_connected_5ge_att",
                                        "drawable",
                                        StringBuilder()
                                            .append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
                                            .append("<bitmap xmlns:android=\"https://schemas.android.com/apk/res/android\" android:src=\"@drawable/stat_sys_data_connected_lte_att\" />\n")
                                            .toString()
                                    )
                                )
                            }

                            if (prefs.disableFlashingWiFi) {
                                add(
                                    ResourceFileData(
                                        "stat_sys_wifi_signal_checking",
                                        "drawable",
                                        StringBuilder()
                                            .append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
                                            .append("<bitmap xmlns:android=\"https://schemas.android.com/apk/res/android\" android:src=\"@drawable/stat_sys_wifi_signal_checking_dim\" />\n")
                                            .toString()
                                    )
                                )
                            }
                        }
                )
            }
            Keys.lockScreen -> {
                OverlayInfo(
                    Keys.systemuiPkg,
                    Keys.lockScreenPkg,
                    ArrayList<ResourceFileData>()
                        .apply {
                            add(
                                ResourceFileData(
                                    "bools.xml",
                                    "values",
                                    makeResourceXml(
                                        ResourceData(
                                            "bool",
                                            "config_enableLockScreenRotation",
                                            "${prefs.lockScreenRotation}"
                                        )
                                    )
                                )
                            )
                        }
                )
            }
            else -> return@launch
        }

        doCompileAlignAndSign(
            data,
            listener
        )
    }
}

fun Context.uninstall(which: String) {
    val pkg = when (which) {
        Keys.clock -> Keys.clockPkg
        Keys.qs -> Keys.qsPkg
        Keys.misc -> Keys.miscPkg
        Keys.lockScreen -> Keys.lockScreenPkg
        Keys.statusBar -> Keys.statusBarPkg
        else -> return
    }

    if (Shell.SU.available()) {
        app.ipcReceiver.postIPCAction { it.uninstallPkg(pkg) }
    } else {
        workaroundInstaller.uninstallPackage(pkg)
    }
}

fun Context.doCompileAlignAndSign(
    overlayInfo: OverlayInfo,
    listener: ((apk: File) -> Unit)? = null
) {
    val base = makeBaseDir(overlayInfo.overlayPkg)
    val manifest = getManifest(base, overlayInfo.targetPkg, overlayInfo.overlayPkg)
    val unsignedUnaligned = makeOverlayFile(base, overlayInfo.overlayPkg, OverlayType.UNSIGNED_UNALIGNED)
    val unsigned = makeOverlayFile(base, overlayInfo.overlayPkg, OverlayType.UNSIGNED)
    val signed = makeOverlayFile(base, overlayInfo.overlayPkg, OverlayType.SIGNED)
    val resDir = makeResDir(base)

    overlayInfo.data.forEach {
        val dir = File(resDir, it.fileDirectory)

        dir.mkdirs()
        dir.mkdir()

        val resFile = File(dir, it.filename)
        if (resFile.exists()) resFile.delete()

        if (it is ResourceImageData) {
            it.image?.let {
                FileOutputStream(resFile).use { stream ->
                    it.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
            }
        } else {
            OutputStreamWriter(resFile.outputStream()).use { writer ->
                writer.write(it.contents)
                writer.write("\n")
            }
        }
    }

    compileOverlay(manifest, unsignedUnaligned, resDir, overlayInfo.targetPkg)
    alignOverlay(unsignedUnaligned, unsigned)
    signOverlay(unsigned, signed)

    Shell.run("sh", arrayOf("cp ${signed.absolutePath} ${signed.absolutePath}"), null, true)

    listener?.invoke(signed)
}

fun Context.compileOverlay(manifest: File, overlayFile: File, resFile: File, targetPackage: String) {
    if (overlayFile.exists()) {
        overlayFile.delete()
    }

    val aaptCmd = StringBuilder()
        .append(aapt)
        .append(" p")
        .append(" -M ")
        .append(manifest)
        .append(" -I ")
        .append("/system/framework/framework-res.apk")
        .apply {
            if (targetPackage != "android") {
                append(" -I ")
                append(packageManager.getApplicationInfo(targetPackage, 0).sourceDir)
            }
        }
        .append(" -S ")
        .append(resFile)
        .append(" -F ")
        .append(overlayFile)
        .toString()

    loggedSh(aaptCmd)
    Shell.SH.run("chmod 777 ${overlayFile.absolutePath}")
}

fun Context.alignOverlay(overlayFile: File, alignedOverlayFile: File) {
    if (alignedOverlayFile.exists()) alignedOverlayFile.delete()

    val zipalignCmd = StringBuilder()
        .append(zipalign)
        .append(" 4 ")
        .append(overlayFile.absolutePath)
        .append(" ")
        .append(alignedOverlayFile.absolutePath)
        .toString()

    Shell.run("sh", arrayOf(zipalignCmd), null, true)

    Shell.SH.run("chmod 777 ${alignedOverlayFile.absolutePath}")
}

fun Context.signOverlay(overlayFile: File, signed: File) {
    Shell.SH.run("chmod 777 ${overlayFile.absolutePath}")

    val key = File(cacheDir, "/signing-key-new")
    val pass = "overlay".toCharArray()

    if (key.exists()) key.delete()

    val store = KeyStore.getInstance(KeyStore.getDefaultType())
    store.load(assets.open("signing-key-new"), pass)

    val privKey = store.getKey("key", pass) as PrivateKey
    val certs = ArrayList<X509Certificate>()

    certs.add(store.getCertificateChain("key")[0] as X509Certificate)

    val signConfig = ApkSigner.SignerConfig.Builder("overlay", privKey, certs).build()
    val signConfigs = ArrayList<ApkSigner.SignerConfig>()

    signConfigs.add(signConfig)

    val signer = ApkSigner.Builder(signConfigs)
    signer.setV1SigningEnabled(true)
        .setV2SigningEnabled(true)
        .setInputApk(overlayFile)
        .setOutputApk(signed)
        .setMinSdkVersion(Build.VERSION.SDK_INT)
        .build()
        .sign()

    Shell.SH.run("chmod 777 ${signed.absolutePath}")
}