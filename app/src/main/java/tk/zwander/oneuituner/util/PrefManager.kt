package tk.zwander.oneuituner.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.preference.PreferenceManager
import tk.zwander.oneuituner.R

class PrefManager private constructor(context: Context) : ContextWrapper(context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            if (instance == null) instance = PrefManager(context.applicationContext)

            return instance!!
        }

        const val CLOCK_FORMAT = "clock_format"
        const val CUSTOM_QS_DATE_FORMAT = "custom_qs_date_format"
        const val QS_DATE_FORMAT = "qs_date_format"
        const val CLOCK_TYPE = "clock_type"

        const val HEADER_COUNT_PORTRAIT = "header_count_portrait"
        const val HEADER_COUNT_LANDSCAPE = "header_count_landscape"
        const val HIDE_QS_TILE_BACKGROUND = "hide_qs_tile_background"
        const val QS_ROW_COUNT_PORTRAIT = "qs_row_count_portrait"
        const val QS_ROW_COUNT_LANDSCAPE = "qs_row_count_landscape"
        const val QS_COL_COUNT_PORTRAIT = "qs_col_count_portrait"
        const val QS_COL_COUNT_LANDSCAPE = "qs_col_count_landscape"

        const val OLD_RECENTS = "old_recents"
        const val NAV_HEIGHT = "nav_height"
        const val STATUS_BAR_HEIGHT = "status_bar_height"

        const val LEFT_SYSTEM_ICONS = "left_system_icons"
        const val HIDE_STATUS_BAR_CARRIER = "hide_status_bar_carrier"
        const val DISABLE_5GE = "disable_5ge"
        const val DISABLE_FLASHING_WIFI = "disable_flashing_wifi"

        const val LOCK_SCREEN_ROTATION = "lock_screen_rotation"
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(this)

    val clockTypeDefault = resourceString(R.string.clock_type_default)
    val clockTypeCustom = resourceString(R.string.clock_type_custom)
    val clockTypeAosp = resourceString(R.string.clock_type_aosp)

    val clockType: String
        get() = getString(CLOCK_TYPE, resourceString(R.string.default_clock_type))

    val clockFormat: String
        get() = getString(CLOCK_FORMAT, resourceString(R.string.custom_clock_format_default))

    val customQsDateFormat: Boolean
        get() = getBoolean(CUSTOM_QS_DATE_FORMAT, resourceBool(R.bool.custom_qs_date_default))

    val qsDateFormat: String
        get() = getString(QS_DATE_FORMAT, resourceString(R.string.custom_qs_date_format_default))

    val headerCountPortrait: Int
        get() = getInt(HEADER_COUNT_PORTRAIT, resourceInt(R.integer.header_count_portrait_default))

    val headerCountLandscape: Int
        get() = getInt(HEADER_COUNT_LANDSCAPE, resourceInt(R.integer.header_count_landscape_default))

    val qsRowCountPortrait: Int
        get() = getInt(QS_ROW_COUNT_PORTRAIT, resourceInt(R.integer.qs_row_count_portrait_default))

    val qsRowCountLandscape: Int
        get() = getInt(QS_ROW_COUNT_LANDSCAPE, resourceInt(R.integer.qs_row_count_landscape_default))

    val qsColCountPortrait: Int
        get() = getInt(QS_COL_COUNT_PORTRAIT, resourceInt(R.integer.qs_col_count_portrait_default))

    val qsColCountLandscape: Int
        get() = getInt(QS_COL_COUNT_LANDSCAPE, resourceInt(R.integer.qs_col_count_landscape_default))

    val hideQsTileBackground: Boolean
        get() = getBoolean(HIDE_QS_TILE_BACKGROUND, resourceBool(R.bool.hide_qs_tile_background_default))

    val oldRecents: Boolean
        get() = getBoolean(OLD_RECENTS, resourceBool(R.bool.old_recents_default))

    val navHeight: Float
        get() = getInt(NAV_HEIGHT, resourceInt(R.integer.nav_height_unscaled_default)) / 10f

    val statusBarHeight: Float
        get() = getInt(STATUS_BAR_HEIGHT, resourceInt(R.integer.status_bar_height_unscaled_default)) / 10f

    val leftSystemIcons: Boolean
        get() = getBoolean(LEFT_SYSTEM_ICONS, resourceBool(R.bool.left_system_icons_default))

    val hideStatusBarCarrier: Boolean
        get() = getBoolean(HIDE_STATUS_BAR_CARRIER, resourceBool(R.bool.hide_status_bar_carrier_default))

    val disable5ge: Boolean
        get() = getBoolean(DISABLE_5GE, resourceBool(R.bool.disable_5ge_default))

    val disableFlashingWiFi: Boolean
        get() = getBoolean(DISABLE_FLASHING_WIFI, resourceBool(R.bool.disable_flashing_wifi_default))

    val lockScreenRotation: Boolean
        get() = getBoolean(LOCK_SCREEN_ROTATION, resourceBool(R.bool.lock_screen_rotation_default))

    fun getInt(key: String, def: Int = 0) = prefs.getInt(key, def)
    fun getString(key: String, def: String): String = prefs.getString(key, def) ?: def
    fun getBoolean(key: String, def: Boolean = false) = prefs.getBoolean(key, def)

    fun putInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()
    fun putString(key: String, value: String?) = prefs.edit().putString(key, value).apply()
    fun putBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()

    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
            prefs.registerOnSharedPreferenceChangeListener(listener)

    fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
            prefs.unregisterOnSharedPreferenceChangeListener(listener)

    private fun resourceInt(id: Int) = resources.getInteger(id)
    private fun resourceString(id: Int) = resources.getString(id)
    private fun resourceBool(id: Int) = resources.getBoolean(id)
}