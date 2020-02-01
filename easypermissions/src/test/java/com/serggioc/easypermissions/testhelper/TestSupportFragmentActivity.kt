package com.serggioc.easypermissions.testhelper

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.serggioc.easypermissions.AfterPermissionGranted
import com.serggioc.easypermissions.EasyPermissions

class TestSupportFragmentActivity : FragmentActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {

    }

    @AfterPermissionGranted(REQUEST_CODE)
    fun afterPermissionGranted() {

    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }

    companion object {
        const val REQUEST_CODE = 5
    }
}
