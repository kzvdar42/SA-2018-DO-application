package com.example.kzvdar42.deliveryoperatorapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.kzvdar42.deliveryoperatorapp.activity.MyNavigationActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mapbox.api.directions.v5.DirectionsAdapterFactory
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.v5.navigation.NavigationConstants

/**
 * Use this class to launch the navigation UI
 *
 *
 * You can launch the UI a route you have already retrieved from
 * [com.mapbox.services.android.navigation.v5.navigation.NavigationRoute].
 *
 *
 * For testing, you can launch with simulation, in which our
 * [com.mapbox.services.android.navigation.v5.location.replay.ReplayRouteLocationEngine] will begin
 * following the given [DirectionsRoute] once the UI is initialized.
 *
 */
object MyNavigationLauncher {

    /**
     * Starts the UI with a [DirectionsRoute] already retrieved from
     * [com.mapbox.services.android.navigation.v5.navigation.NavigationRoute]
     *
     * @param activity must be launched from another [Activity]
     * @param options  with fields to customize the navigation view
     */
    fun startNavigation(activity: Activity, options: NavigationLauncherOptions) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = preferences.edit()

        storeDirectionsRouteValue(options, editor)
        storeConfiguration(options, editor)

        storeThemePreferences(options, editor)

        editor.apply()

        val navigationActivity = Intent(activity, MyNavigationActivity::class.java)
        storeInitialMapPosition(options, navigationActivity)
        activity.startActivity(navigationActivity)
    }

    private fun storeDirectionsRouteValue(options: NavigationLauncherOptions, editor: SharedPreferences.Editor) {
        editor.putString(NavigationConstants.NAVIGATION_VIEW_ROUTE_KEY, GsonBuilder()
                .registerTypeAdapterFactory(DirectionsAdapterFactory.create()).create().toJson(options.directionsRoute()))
    }

    private fun storeConfiguration(options: NavigationLauncherOptions, editor: SharedPreferences.Editor) {
        editor.putBoolean(NavigationConstants.NAVIGATION_VIEW_SIMULATE_ROUTE, options.shouldSimulateRoute())
        editor.putString(NavigationConstants.NAVIGATION_VIEW_ROUTE_PROFILE_KEY, options.directionsProfile())
    }

    private fun storeThemePreferences(options: NavigationLauncherOptions, editor: SharedPreferences.Editor) {
        val preferenceThemeSet = options.lightThemeResId() != null || options.darkThemeResId() != null
        editor.putBoolean(NavigationConstants.NAVIGATION_VIEW_PREFERENCE_SET_THEME, preferenceThemeSet)

        if (preferenceThemeSet) {
            if (options.lightThemeResId() != null) {
                editor.putInt(NavigationConstants.NAVIGATION_VIEW_LIGHT_THEME, options.lightThemeResId()!!)
            }
            if (options.darkThemeResId() != null) {
                editor.putInt(NavigationConstants.NAVIGATION_VIEW_DARK_THEME, options.darkThemeResId()!!)
            }
        }
    }

    private fun storeInitialMapPosition(options: NavigationLauncherOptions, navigationActivity: Intent) {
        if (options.initialMapCameraPosition() != null) {
            navigationActivity.putExtra(
                    NavigationConstants.NAVIGATION_VIEW_INITIAL_MAP_POSITION, options.initialMapCameraPosition()
            )
        }
    }
}
