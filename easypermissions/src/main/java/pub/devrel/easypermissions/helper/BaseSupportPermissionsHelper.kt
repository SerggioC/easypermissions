package pub.devrel.easypermissions.helper

import android.util.Log
import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentManager
import pub.devrel.easypermissions.RationaleDialogFragmentCompat

/**
 * Implementation of [PermissionHelper] for Support Library host classes.
 */
abstract class BaseSupportPermissionsHelper<T>(host: T) : PermissionHelper<T>(host) {

    abstract val supportFragmentManager: FragmentManager

    override fun showRequestPermissionRationale(
        rationale: String,
        positiveButton: String,
        negativeButton: String,
        @StyleRes theme: Int,
        requestCode: Int,
        vararg perms: String
    ) {

        val fm = supportFragmentManager

        // Check if fragment is already showing
        val fragment = fm.findFragmentByTag(RationaleDialogFragmentCompat.TAG)
        if (fragment is RationaleDialogFragmentCompat) {
            Log.d(TAG, "Found existing fragment, not showing rationale.")
            return
        }

        RationaleDialogFragmentCompat
            .newInstance(rationale, positiveButton, negativeButton, theme, requestCode, perms)
            .showAllowingStateLoss(fm, RationaleDialogFragmentCompat.TAG)
    }

    companion object {

        private val TAG = "BSPermissionsHelper"
    }
}
