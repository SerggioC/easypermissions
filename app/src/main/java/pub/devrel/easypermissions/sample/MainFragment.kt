package com.serggioc.easypermissions.sample

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.serggioc.easypermissions.AfterPermissionGranted
import com.serggioc.easypermissions.EasyPermissions

/**
 * Created in [R.layout.activity_main]
 */
class MainFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // Create view
        val v = inflater.inflate(R.layout.fragment_main, container)

        // Button click listener
        v.findViewById<View>(R.id.button_sms).setOnClickListener { smsTask() }

        return v
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(RC_SMS_PERM)
    private fun smsTask() {
        if (EasyPermissions.hasPermissions(context, Manifest.permission.READ_SMS)) {
            // Have permission, do the thing!
            Toast.makeText(activity, "TODO: SMS things", Toast.LENGTH_LONG).show()
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(
                this, getString(R.string.rationale_sms),
                RC_SMS_PERM, Manifest.permission.READ_SMS
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size)
    }

    companion object {
        private val TAG = "MainFragment"
        private const val RC_SMS_PERM = 122
    }
}
