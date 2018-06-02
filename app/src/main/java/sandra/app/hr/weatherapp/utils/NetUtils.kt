package sandra.app.hr.weatherapp.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import sandra.app.hr.weatherapp.R

const val RESPONSE_OK = 200

fun checkConnection(context: Context) {
    if (!isConnectedToInternet(context)) {
        showConnectionAlert(context)
    }
}

fun showConnectionAlert(context: Context) {
    AlertDialog.Builder(context).apply {
        setTitle(context.getString(R.string.no_connection))
        setMessage(context.getString(R.string.check_conn))
        setPositiveButton(context.getString(R.string.ok), { dialog, _ ->
            dialog.cancel()
            (context as Activity).finish()
        })
        show()
    }
}

fun isConnectedToInternet(context: Context): Boolean {
    val manager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = manager.activeNetworkInfo
    return info != null && info.isConnected
}

