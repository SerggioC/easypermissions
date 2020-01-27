package pub.devrel.easypermissions.helper

import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AppCompatActivity

/**
 * Permissions helper for [AppCompatActivity].
 */
internal class AppCompatActivityPermissionsHelper(host: AppCompatActivity) :
    BaseSupportPermissionsHelper<AppCompatActivity>(host) {

    override val supportFragmentManager: FragmentManager
        get() = host.supportFragmentManager

    override val context: Context
        get() = host

    override fun directRequestPermissions(requestCode: Int, vararg perms: String) {
        ActivityCompat.requestPermissions(host, perms, requestCode)
    }

    override fun shouldShowRequestPermissionRationale(perm: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(host, perm)
    }
}
