package com.serggioc.easypermissions

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import androidx.fragment.app.Fragment

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import java.util.Arrays

import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class RationaleDialogClickListenerTest {
    @Mock
    private val dialogFragment: RationaleDialogFragment? = null
    @Mock
    private val dialogFragmentCompat: RationaleDialogFragmentCompat? = null
    @Mock
    private val dialogConfig: RationaleDialogConfig? = null
    @Mock
    private val permissionCallbacks: EasyPermissions.PermissionCallbacks? = null
    @Mock
    private val rationaleCallbacks: EasyPermissions.RationaleCallbacks? = null
    @Mock
    private val dialogInterface: DialogInterface? = null
    @Mock
    private val activity: Activity? = null
    @Mock
    private val fragment: Fragment? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        `when`(dialogFragment!!.activity).thenReturn(activity)
        dialogConfig!!.requestCode = REQUEST_CODE
        dialogConfig.permissions = PERMS
    }

    @Test
    fun shouldOnRationaleAccepted_whenPositiveButtonWithRationaleCallbacks() {
        val listener = RationaleDialogClickListener(
            dialogFragment!!, dialogConfig!!,
            permissionCallbacks, rationaleCallbacks
        )
        listener.onClick(dialogInterface!!, Dialog.BUTTON_POSITIVE)

        verify<EasyPermissions.RationaleCallbacks>(rationaleCallbacks, times(1)).onRationaleAccepted(REQUEST_CODE)
    }

    @Test
    fun shouldNotOnRationaleAccepted_whenPositiveButtonWithoutRationaleCallbacks() {
        val listener = RationaleDialogClickListener(
            dialogFragment!!, dialogConfig!!,
            permissionCallbacks,
            null
        )
        listener.onClick(dialogInterface!!, Dialog.BUTTON_POSITIVE)

        verify<EasyPermissions.RationaleCallbacks>(rationaleCallbacks, never()).onRationaleAccepted(anyInt())
    }

    @Test
    fun shouldRequestPermissions_whenPositiveButtonFromActivity() {
        val listener = RationaleDialogClickListener(
            dialogFragment!!, dialogConfig!!,
            permissionCallbacks, rationaleCallbacks
        )
        listener.onClick(dialogInterface!!, Dialog.BUTTON_POSITIVE)

        verify<Activity>(activity, times(1)).requestPermissions(PERMS, REQUEST_CODE)
    }

    @Test
    fun shouldRequestPermissions_whenPositiveButtonFromFragment() {
        `when`<Fragment>(dialogFragmentCompat!!.parentFragment).thenReturn(fragment)

        val listener = RationaleDialogClickListener(
            dialogFragmentCompat, dialogConfig!!,
            permissionCallbacks, rationaleCallbacks
        )
        listener.onClick(dialogInterface!!, Dialog.BUTTON_POSITIVE)

        verify<Fragment>(fragment, times(1)).requestPermissions(PERMS, REQUEST_CODE)
    }

    @Test
    fun shouldOnRationaleDenied_whenNegativeButtonWithRationaleCallbacks() {
        val listener = RationaleDialogClickListener(
            dialogFragment!!, dialogConfig!!,
            permissionCallbacks, rationaleCallbacks
        )
        listener.onClick(dialogInterface!!, Dialog.BUTTON_NEGATIVE)

        verify<EasyPermissions.RationaleCallbacks>(rationaleCallbacks, times(1)).onRationaleDenied(REQUEST_CODE)
    }

    @Test
    fun shouldNotOnRationaleDenied_whenNegativeButtonWithoutRationaleCallbacks() {
        val listener = RationaleDialogClickListener(
            dialogFragment!!, dialogConfig!!,
            permissionCallbacks, null
        )
        listener.onClick(dialogInterface!!, Dialog.BUTTON_NEGATIVE)

        verify<EasyPermissions.RationaleCallbacks>(rationaleCallbacks, never()).onRationaleDenied(anyInt())
    }

    @Test
    fun shouldOnPermissionsDenied_whenNegativeButtonWithPermissionCallbacks() {
        val listener = RationaleDialogClickListener(
            dialogFragment!!, dialogConfig!!,
            permissionCallbacks, rationaleCallbacks
        )
        listener.onClick(dialogInterface!!, Dialog.BUTTON_NEGATIVE)

        verify<EasyPermissions.PermissionCallbacks>(permissionCallbacks, times(1))
            .onPermissionsDenied(REQUEST_CODE, Arrays.asList(*PERMS))
    }

    @Test
    fun shouldNotOnPermissionsDenied_whenNegativeButtonWithoutPermissionCallbacks() {
        val listener =
            RationaleDialogClickListener(dialogFragment!!, dialogConfig!!, null, rationaleCallbacks)
        listener.onClick(dialogInterface!!, Dialog.BUTTON_NEGATIVE)

        verify<EasyPermissions.PermissionCallbacks>(permissionCallbacks, never()).onPermissionsDenied(
            anyInt(),
            ArgumentMatchers.anyList<String>()
        )
    }

    companion object {
        private val REQUEST_CODE = 5
        private val PERMS =
            arrayOf(Manifest.permission.READ_SMS, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
