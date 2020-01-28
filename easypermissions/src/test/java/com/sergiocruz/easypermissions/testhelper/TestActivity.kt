package com.sergiocruz.easypermissions.testhelper

import android.app.Activity
import com.sergiocruz.easypermissions.AfterPermissionGranted
import com.sergiocruz.easypermissions.EasyPermissions

class TestActivity : Activity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

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
        const val REQUEST_CODE = 1
    }
}
