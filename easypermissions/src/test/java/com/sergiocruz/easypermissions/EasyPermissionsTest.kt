package com.sergiocruz.easypermissions

import android.Manifest
import android.app.Application
import android.app.Dialog
import android.app.Fragment
import android.content.pm.PackageManager
import android.widget.TextView

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

import java.util.ArrayList
import java.util.Arrays
import java.util.Collections

import androidx.test.core.app.ApplicationProvider
import com.sergiocruz.easypermissions.testhelper.ActivityController
import com.sergiocruz.easypermissions.testhelper.FragmentController
import com.sergiocruz.easypermissions.testhelper.TestActivity
import com.sergiocruz.easypermissions.testhelper.TestAppCompatActivity
import com.sergiocruz.easypermissions.testhelper.TestFragment
import com.sergiocruz.easypermissions.testhelper.TestSupportFragmentActivity

import com.google.common.truth.Truth.assertThat
import junit.framework.Assert.fail
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.robolectric.Shadows.shadowOf

/**
 * Basic Robolectric tests for [com.sergiocruz.easypermissions.EasyPermissions].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class EasyPermissionsTest {

    private var shadowApp: ShadowApplication? = null
    private var app: Application? = null
    private var spyActivity: TestActivity? = null
    private var spySupportFragmentActivity: TestSupportFragmentActivity? = null
    private var spyAppCompatActivity: TestAppCompatActivity? = null
    private var spyFragment: TestFragment? = null
    private var fragmentController: FragmentController<TestFragment>? = null
    private var activityController: ActivityController<TestActivity>? = null
    private var supportFragmentActivityController: ActivityController<TestSupportFragmentActivity>? =
        null
    private var appCompatActivityController: ActivityController<TestAppCompatActivity>? = null
    @Captor
    private val integerCaptor: ArgumentCaptor<Int>? = null
    @Captor
    private val listCaptor: ArgumentCaptor<ArrayList<String>>? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        app = ApplicationProvider.getApplicationContext()
        shadowApp = shadowOf(app)

        activityController = ActivityController(TestActivity::class.java)
        supportFragmentActivityController =
            ActivityController(TestSupportFragmentActivity::class.java)
        appCompatActivityController = ActivityController(TestAppCompatActivity::class.java)
        fragmentController = FragmentController(TestFragment::class.java)

        spyActivity = Mockito.spy(activityController!!.resume())
        spySupportFragmentActivity = Mockito.spy(supportFragmentActivityController!!.resume())
        spyAppCompatActivity = Mockito.spy(appCompatActivityController!!.resume())
        spyFragment = Mockito.spy(fragmentController!!.resume())
    }

    // ------ General tests ------

    @Test
    fun shouldNotHavePermissions_whenNoPermissionsGranted() {
        assertThat(EasyPermissions.hasPermissions(app!!, *ALL_PERMS)).isFalse()
    }

    @Test
    fun shouldNotHavePermissions_whenNotAllPermissionsGranted() {
        shadowApp!!.grantPermissions(*ONE_PERM)
        assertThat(EasyPermissions.hasPermissions(app!!, *ALL_PERMS)).isFalse()
    }

    @Test
    fun shouldHavePermissions_whenAllPermissionsGranted() {
        shadowApp!!.grantPermissions(*ALL_PERMS)
        assertThat(EasyPermissions.hasPermissions(app!!, *ALL_PERMS)).isTrue()
    }

    @Test
    fun shouldThrowException_whenHasPermissionsWithNullContext() {
        try {
            EasyPermissions.hasPermissions(null!!, *ALL_PERMS)
            fail("IllegalStateException expected because of null context.")
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat()
                .isEqualTo("Can't check permissions for null context")
        }

    }

    // ------ From Activity ------

    @Test
    fun shouldCorrectlyCallback_whenOnRequestPermissionResultCalledFromActivity() {
        EasyPermissions.onRequestPermissionsResult(
            TestActivity.REQUEST_CODE,
            ALL_PERMS,
            SMS_DENIED_RESULT,
            spyActivity!!
        )

        verify<TestActivity>(spyActivity, times(1))
            .onPermissionsGranted(integerCaptor!!.capture(), listCaptor!!.capture())
        assertThat(integerCaptor.value).isEqualTo(TestActivity.REQUEST_CODE)
        assertThat(listCaptor.value)
            .containsAllIn(ArrayList(listOf(Manifest.permission.ACCESS_FINE_LOCATION)))

        verify<TestActivity>(spyActivity, times(1))
            .onPermissionsDenied(integerCaptor.capture(), listCaptor.capture())
        assertThat(integerCaptor.value).isEqualTo(TestActivity.REQUEST_CODE)
        assertThat(listCaptor.value)
            .containsAllIn(ArrayList(listOf(Manifest.permission.READ_SMS)))

        verify<TestActivity>(spyActivity, never()).afterPermissionGranted()
    }

    @Test
    fun shouldCallbackOnPermissionGranted_whenRequestAlreadyGrantedPermissionsFromActivity() {
        grantPermissions(ALL_PERMS)

        EasyPermissions.requestPermissions(
            spyActivity!!,
            RATIONALE,
            TestActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        verify<TestActivity>(spyActivity, times(1))
            .onPermissionsGranted(integerCaptor!!.capture(), listCaptor!!.capture())
        verify<TestActivity>(
            spyActivity,
            never()
        ).requestPermissions(any(Array<String>::class.java), anyInt())
        assertThat(integerCaptor.value).isEqualTo(TestActivity.REQUEST_CODE)
        assertThat(listCaptor.value).containsAllIn(ALL_PERMS)
    }

    @Test
    fun shouldCallbackAfterPermissionGranted_whenRequestAlreadyGrantedPermissionsFromActivity() {
        grantPermissions(ALL_PERMS)

        EasyPermissions.requestPermissions(
            spyActivity!!,
            RATIONALE,
            TestActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        // Called 2 times because this is a spy and library implementation invokes super classes annotated methods as well
        verify<TestActivity>(spyActivity, times(2)).afterPermissionGranted()
    }

    @Test
    fun shouldNotCallbackAfterPermissionGranted_whenRequestNotGrantedPermissionsFromActivity() {
        grantPermissions(ONE_PERM)

        EasyPermissions.requestPermissions(
            spyActivity!!,
            RATIONALE,
            TestActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        verify<TestActivity>(spyActivity, never()).afterPermissionGranted()
    }

    @Test
    fun shouldRequestPermissions_whenMissingPermissionAndNotShowRationaleFromActivity() {
        grantPermissions(ONE_PERM)
        showRationale(false, *ALL_PERMS)

        EasyPermissions.requestPermissions(
            spyActivity!!,
            RATIONALE,
            TestActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        verify<TestActivity>(spyActivity, times(1))
            .requestPermissions(ALL_PERMS, TestActivity.REQUEST_CODE)
    }

    @Test
    fun shouldShowCorrectDialog_whenMissingPermissionsAndShowRationaleFromActivity() {
        grantPermissions(ONE_PERM)
        showRationale(true, *ALL_PERMS)

        EasyPermissions.requestPermissions(
            spyActivity!!,
            RATIONALE,
            TestActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        val dialogFragment = spyActivity!!.fragmentManager
            .findFragmentByTag(RationaleDialogFragment.TAG)
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragment::class.java)

        val dialog = (dialogFragment as RationaleDialogFragment).dialog
        assertThatHasExpectedRationale(dialog, RATIONALE)
    }

    @Test
    fun shouldShowCorrectDialogUsingRequest_whenMissingPermissionsAndShowRationaleFromActivity() {
        grantPermissions(ONE_PERM)
        showRationale(true, *ALL_PERMS)

        val request =
            PermissionRequest.Builder(spyActivity!!, TestActivity.REQUEST_CODE, *ALL_PERMS)
                .setPositiveButtonText(android.R.string.ok)
                .setNegativeButtonText(android.R.string.cancel)
                .setRationale(android.R.string.unknownName)
                .setTheme(R.style.Theme_AppCompat)
                .build()
        EasyPermissions.requestPermissions(request)

        val dialogFragment = spyActivity!!.fragmentManager
            .findFragmentByTag(RationaleDialogFragment.TAG)
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragment::class.java)

        val dialog = (dialogFragment as RationaleDialogFragment).dialog
        assertThatHasExpectedButtonsAndRationale(
            dialog, android.R.string.unknownName,
            android.R.string.ok, android.R.string.cancel
        )
    }

    @Test
    fun shouldHaveSomePermissionDenied_whenShowRationaleFromActivity() {
        showRationale(true, *ALL_PERMS)

        assertThat(EasyPermissions.somePermissionDenied(spyActivity!!, *ALL_PERMS)).isTrue()
    }

    @Test
    fun shouldNotHaveSomePermissionDenied_whenNotShowRationaleFromActivity() {
        showRationale(false, *ALL_PERMS)

        assertThat(EasyPermissions.somePermissionDenied(spyActivity!!, *ALL_PERMS)).isFalse()
    }

    @Test
    fun shouldHaveSomePermissionPermanentlyDenied_whenNotShowRationaleFromActivity() {
        showRationale(false, *ALL_PERMS)

        assertThat(
            EasyPermissions.somePermissionPermanentlyDenied(
                spyActivity!!,
                Arrays.asList(*ALL_PERMS)
            )
        ).isTrue()
    }

    @Test
    fun shouldNotHaveSomePermissionPermanentlyDenied_whenShowRationaleFromActivity() {
        showRationale(true, *ALL_PERMS)

        assertThat(
            EasyPermissions.somePermissionPermanentlyDenied(
                spyActivity!!,
                Arrays.asList(*ALL_PERMS)
            )
        ).isFalse()
    }

    @Test
    fun shouldHavePermissionPermanentlyDenied_whenNotShowRationaleFromActivity() {
        showRationale(false, Manifest.permission.READ_SMS)

        assertThat(
            EasyPermissions.permissionPermanentlyDenied(
                spyActivity!!,
                Manifest.permission.READ_SMS
            )
        ).isTrue()
    }

    @Test
    fun shouldNotHavePermissionPermanentlyDenied_whenShowRationaleFromActivity() {
        showRationale(true, Manifest.permission.READ_SMS)

        assertThat(
            EasyPermissions.permissionPermanentlyDenied(
                spyActivity!!,
                Manifest.permission.READ_SMS
            )
        ).isFalse()
    }

    @Test
    fun shouldCorrectlyCallback_whenOnRequestPermissionResultCalledFromAppCompatActivity() {
        EasyPermissions.onRequestPermissionsResult(
            TestAppCompatActivity.REQUEST_CODE,
            ALL_PERMS,
            SMS_DENIED_RESULT,
            spyAppCompatActivity!!
        )

        verify<TestAppCompatActivity>(spyAppCompatActivity, times(1))
            .onPermissionsGranted(integerCaptor!!.capture(), listCaptor!!.capture())
        assertThat(integerCaptor.value).isEqualTo(TestAppCompatActivity.REQUEST_CODE)
        assertThat(listCaptor.value)
            .containsAllIn(ArrayList(listOf(Manifest.permission.ACCESS_FINE_LOCATION)))

        verify<TestAppCompatActivity>(spyAppCompatActivity, times(1))
            .onPermissionsDenied(integerCaptor.capture(), listCaptor.capture())
        assertThat(integerCaptor.value).isEqualTo(TestAppCompatActivity.REQUEST_CODE)
        assertThat(listCaptor.value)
            .containsAllIn(ArrayList(listOf(Manifest.permission.READ_SMS)))

        verify<TestAppCompatActivity>(spyAppCompatActivity, never()).afterPermissionGranted()
    }

    @Test
    fun shouldCallbackOnPermissionGranted_whenRequestAlreadyGrantedPermissionsFromAppCompatActivity() {
        grantPermissions(ALL_PERMS)

        EasyPermissions.requestPermissions(
            spyAppCompatActivity!!,
            RATIONALE,
            TestAppCompatActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        verify<TestAppCompatActivity>(spyAppCompatActivity, times(1))
            .onPermissionsGranted(integerCaptor!!.capture(), listCaptor!!.capture())
        verify<TestAppCompatActivity>(
            spyAppCompatActivity,
            never()
        ).requestPermissions(any(Array<String>::class.java), anyInt())
        assertThat(integerCaptor.value).isEqualTo(TestAppCompatActivity.REQUEST_CODE)
        assertThat(listCaptor.value).containsAllIn(ALL_PERMS)
    }

    @Test
    fun shouldCallbackAfterPermissionGranted_whenRequestAlreadyGrantedPermissionsFromAppCompatActivity() {
        grantPermissions(ALL_PERMS)

        EasyPermissions.requestPermissions(
            spyAppCompatActivity!!,
            RATIONALE,
            TestAppCompatActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        // Called 2 times because this is a spy and library implementation invokes super classes annotated methods as well
        verify<TestAppCompatActivity>(spyAppCompatActivity, times(2)).afterPermissionGranted()
    }

    @Test
    fun shouldNotCallbackAfterPermissionGranted_whenRequestNotGrantedPermissionsFromAppCompatActivity() {
        grantPermissions(ONE_PERM)

        EasyPermissions.requestPermissions(
            spyAppCompatActivity!!,
            RATIONALE,
            TestAppCompatActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        verify<TestAppCompatActivity>(spyAppCompatActivity, never()).afterPermissionGranted()
    }

    @Test
    fun shouldRequestPermissions_whenMissingPermissionAndNotShowRationaleFromAppCompatActivity() {
        grantPermissions(ONE_PERM)
        showRationale(false, *ALL_PERMS)

        EasyPermissions.requestPermissions(
            spyAppCompatActivity!!,
            RATIONALE,
            TestAppCompatActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        verify<TestAppCompatActivity>(spyAppCompatActivity, times(1))
            .requestPermissions(ALL_PERMS, TestAppCompatActivity.REQUEST_CODE)
    }

    @Test
    fun shouldShowCorrectDialog_whenMissingPermissionsAndShowRationaleFromAppCompatActivity() {
        grantPermissions(ONE_PERM)
        showRationale(true, *ALL_PERMS)

        EasyPermissions.requestPermissions(
            spyAppCompatActivity!!,
            RATIONALE,
            TestAppCompatActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        val dialogFragment = spyAppCompatActivity!!.supportFragmentManager
            .findFragmentByTag(RationaleDialogFragmentCompat.TAG)
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragmentCompat::class.java)

        val dialog = (dialogFragment as RationaleDialogFragmentCompat).dialog
        assertThatHasExpectedRationale(dialog!!, RATIONALE)
    }

    @Test
    fun shouldShowCorrectDialog_whenMissingPermissionsAndShowRationaleFromSupportFragmentActivity() {
        grantPermissions(ONE_PERM)
        showRationale(true, *ALL_PERMS)

        EasyPermissions.requestPermissions(
            spySupportFragmentActivity!!,
            RATIONALE,
            TestSupportFragmentActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        val dialogFragment = spySupportFragmentActivity!!.fragmentManager
            .findFragmentByTag(RationaleDialogFragment.TAG)
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragment::class.java)

        val dialog = (dialogFragment as RationaleDialogFragment).dialog
        assertThatHasExpectedRationale(dialog, RATIONALE)
    }

    @Test
    fun shouldShowCorrectDialogUsingRequest_whenMissingPermissionsAndShowRationaleFromAppCompatActivity() {
        grantPermissions(ONE_PERM)
        showRationale(true, *ALL_PERMS)

        val request = PermissionRequest.Builder(
            spyAppCompatActivity!!,
            TestAppCompatActivity.REQUEST_CODE,
            *ALL_PERMS
        )
            .setPositiveButtonText(android.R.string.ok)
            .setNegativeButtonText(android.R.string.cancel)
            .setRationale(android.R.string.unknownName)
            .setTheme(R.style.Theme_AppCompat)
            .build()
        EasyPermissions.requestPermissions(request)

        val dialogFragment = spyAppCompatActivity!!.supportFragmentManager
            .findFragmentByTag(RationaleDialogFragmentCompat.TAG)
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragmentCompat::class.java)

        val dialog = (dialogFragment as RationaleDialogFragmentCompat).dialog
        assertThatHasExpectedButtonsAndRationale(
            dialog!!, android.R.string.unknownName,
            android.R.string.ok, android.R.string.cancel
        )
    }

    @Test
    fun shouldHaveSomePermissionDenied_whenShowRationaleFromAppCompatActivity() {
        showRationale(true, *ALL_PERMS)

        assertThat(
            EasyPermissions.somePermissionDenied(
                spyAppCompatActivity!!,
                *ALL_PERMS
            )
        ).isTrue()
    }

    @Test
    fun shouldNotHaveSomePermissionDenied_whenNotShowRationaleFromAppCompatActivity() {
        showRationale(false, *ALL_PERMS)

        assertThat(
            EasyPermissions.somePermissionDenied(
                spyAppCompatActivity!!,
                *ALL_PERMS
            )
        ).isFalse()
    }

    @Test
    fun shouldHaveSomePermissionPermanentlyDenied_whenNotShowRationaleFromAppCompatActivity() {
        showRationale(false, *ALL_PERMS)

        assertThat(
            EasyPermissions.somePermissionPermanentlyDenied(
                spyAppCompatActivity!!,
                Arrays.asList(*ALL_PERMS)
            )
        ).isTrue()
    }

    @Test
    fun shouldNotHaveSomePermissionPermanentlyDenied_whenShowRationaleFromAppCompatActivity() {
        showRationale(true, *ALL_PERMS)

        assertThat(
            EasyPermissions.somePermissionPermanentlyDenied(
                spyAppCompatActivity!!,
                Arrays.asList(*ALL_PERMS)
            )
        ).isFalse()
    }

    @Test
    fun shouldHavePermissionPermanentlyDenied_whenNotShowRationaleFromAppCompatActivity() {
        showRationale(false, Manifest.permission.READ_SMS)

        assertThat(
            EasyPermissions.permissionPermanentlyDenied(
                spyAppCompatActivity!!,
                Manifest.permission.READ_SMS
            )
        ).isTrue()
    }

    @Test
    fun shouldNotHavePermissionPermanentlyDenied_whenShowRationaleFromAppCompatActivity() {
        showRationale(true, Manifest.permission.READ_SMS)

        assertThat(
            EasyPermissions.permissionPermanentlyDenied(
                spyAppCompatActivity!!,
                Manifest.permission.READ_SMS
            )
        ).isFalse()
    }

    @Test
    fun shouldCorrectlyCallback_whenOnRequestPermissionResultCalledFromFragment() {
        EasyPermissions.onRequestPermissionsResult(
            TestFragment.REQUEST_CODE, ALL_PERMS, SMS_DENIED_RESULT,
            spyFragment!!
        )

        verify<TestFragment>(spyFragment, times(1))
            .onPermissionsGranted(integerCaptor!!.capture(), listCaptor!!.capture())
        assertThat(integerCaptor.value).isEqualTo(TestFragment.REQUEST_CODE)
        assertThat(listCaptor.value)
            .containsAllIn(ArrayList(listOf(Manifest.permission.ACCESS_FINE_LOCATION)))

        verify<TestFragment>(spyFragment, times(1))
            .onPermissionsDenied(integerCaptor.capture(), listCaptor.capture())
        assertThat(integerCaptor.value).isEqualTo(TestFragment.REQUEST_CODE)
        assertThat(listCaptor.value)
            .containsAllIn(ArrayList(listOf(Manifest.permission.READ_SMS)))

        verify<TestFragment>(spyFragment, never()).afterPermissionGranted()
    }

    @Test
    fun shouldCallbackOnPermissionGranted_whenRequestAlreadyGrantedPermissionsFromFragment() {
        grantPermissions(ALL_PERMS)

        EasyPermissions.requestPermissions(
            spyFragment!!, RATIONALE,
            TestFragment.REQUEST_CODE, *ALL_PERMS
        )

        verify<TestFragment>(spyFragment, times(1))
            .onPermissionsGranted(integerCaptor!!.capture(), listCaptor!!.capture())
        verify<TestFragment>(
            spyFragment,
            never()
        ).requestPermissions(any(Array<String>::class.java), anyInt())
        assertThat(integerCaptor.value).isEqualTo(TestFragment.REQUEST_CODE)
        assertThat(listCaptor.value).containsAllIn(ALL_PERMS)
    }

    @Test
    fun shouldCallbackAfterPermissionGranted_whenRequestAlreadyGrantedPermissionsFragment() {
        grantPermissions(ALL_PERMS)

        EasyPermissions.requestPermissions(
            spyFragment!!,
            RATIONALE,
            TestFragment.REQUEST_CODE,
            *ALL_PERMS
        )

        // Called 2 times because this is a spy and library implementation invokes super classes annotated methods as well
        verify<TestFragment>(spyFragment, times(2)).afterPermissionGranted()
    }

    @Test
    fun shouldNotCallbackAfterPermissionGranted_whenRequestNotGrantedPermissionsFromFragment() {
        grantPermissions(ONE_PERM)

        EasyPermissions.requestPermissions(
            spyFragment!!,
            RATIONALE,
            TestFragment.REQUEST_CODE,
            *ALL_PERMS
        )

        verify<TestFragment>(spyFragment, never()).afterPermissionGranted()
    }

    @Test
    fun shouldRequestPermissions_whenMissingPermissionsAndNotShowRationaleFromFragment() {
        grantPermissions(ONE_PERM)
        showRationale(false, *ALL_PERMS)

        EasyPermissions.requestPermissions(
            spyFragment!!,
            RATIONALE,
            TestFragment.REQUEST_CODE,
            *ALL_PERMS
        )

        verify<TestFragment>(spyFragment, times(1))
            .requestPermissions(ALL_PERMS, TestFragment.REQUEST_CODE)
    }

    @Test
    fun shouldShowCorrectDialog_whenMissingPermissionsAndShowRationaleFromFragment() {
        grantPermissions(ONE_PERM)
        showRationale(true, *ALL_PERMS)

        EasyPermissions.requestPermissions(
            spyFragment!!,
            RATIONALE,
            TestFragment.REQUEST_CODE,
            *ALL_PERMS
        )

        val dialogFragment = spyFragment!!.childFragmentManager
            .findFragmentByTag(RationaleDialogFragmentCompat.TAG)
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragmentCompat::class.java)

        val dialog = (dialogFragment as RationaleDialogFragmentCompat).dialog
        assertThatHasExpectedRationale(dialog!!, RATIONALE)
    }

    @Test
    fun shouldShowCorrectDialogUsingRequest_whenMissingPermissionsAndShowRationaleFromFragment() {
        grantPermissions(ONE_PERM)
        showRationale(true, *ALL_PERMS)

        val request =
            PermissionRequest.Builder(spyFragment!!, TestFragment.REQUEST_CODE, *ALL_PERMS)
                .setPositiveButtonText(POSITIVE)
                .setNegativeButtonText(NEGATIVE)
                .setRationale(RATIONALE)
                .setTheme(R.style.Theme_AppCompat)
                .build()
        EasyPermissions.requestPermissions(request)

        val dialogFragment = spyFragment!!.childFragmentManager
            .findFragmentByTag(RationaleDialogFragmentCompat.TAG)
        assertThat(dialogFragment).isInstanceOf(RationaleDialogFragmentCompat::class.java)

        val dialog = (dialogFragment as RationaleDialogFragmentCompat).dialog
        assertThatHasExpectedButtonsAndRationale(dialog!!, RATIONALE, POSITIVE, NEGATIVE)
    }

    @Test
    fun shouldHaveSomePermissionDenied_whenShowRationaleFromFragment() {
        showRationale(true, *ALL_PERMS)

        assertThat(EasyPermissions.somePermissionDenied(spyFragment!!, *ALL_PERMS)).isTrue()
    }

    @Test
    fun shouldNotHaveSomePermissionDenied_whenNotShowRationaleFromFragment() {
        showRationale(false, *ALL_PERMS)

        assertThat(EasyPermissions.somePermissionDenied(spyFragment!!, *ALL_PERMS)).isFalse()
    }

    @Test
    fun shouldHaveSomePermissionPermanentlyDenied_whenNotShowRationaleFromFragment() {
        showRationale(false, *ALL_PERMS)

        assertThat(
            EasyPermissions.somePermissionPermanentlyDenied(
                spyFragment!!,
                Arrays.asList(*ALL_PERMS)
            )
        ).isTrue()
    }

    @Test
    fun shouldNotHaveSomePermissionPermanentlyDenied_whenShowRationaleFromFragment() {
        showRationale(true, *ALL_PERMS)

        assertThat(
            EasyPermissions.somePermissionPermanentlyDenied(
                spyFragment!!,
                Arrays.asList(*ALL_PERMS)
            )
        ).isFalse()
    }


    @Test
    fun shouldHavePermissionPermanentlyDenied_whenNotShowRationaleFromFragment() {
        showRationale(false, Manifest.permission.READ_SMS)

        assertThat(
            EasyPermissions.permissionPermanentlyDenied(
                spyFragment!!,
                Manifest.permission.READ_SMS
            )
        ).isTrue()
    }

    @Test
    fun shouldNotHavePermissionPermanentlyDenied_whenShowRationaleFromFragment() {
        showRationale(true, Manifest.permission.READ_SMS)

        assertThat(
            EasyPermissions.permissionPermanentlyDenied(
                spyFragment!!,
                Manifest.permission.READ_SMS
            )
        ).isFalse()
    }

    private fun assertThatHasExpectedButtonsAndRationale(
        dialog: Dialog, rationale: Int,
        positive: Int, negative: Int
    ) {
        val dialogMessage = dialog.findViewById<TextView>(android.R.id.message)
        assertThat(dialogMessage.text.toString()).isEqualTo(app!!.getString(rationale))
        val positiveMessage = dialog.findViewById<TextView>(android.R.id.button1)
        assertThat(positiveMessage.text.toString()).isEqualTo(app!!.getString(positive))
        val negativeMessage = dialog.findViewById<TextView>(android.R.id.button2)
        assertThat(negativeMessage.text.toString()).isEqualTo(app!!.getString(negative))
    }

    private fun assertThatHasExpectedButtonsAndRationale(
        dialog: Dialog, rationale: String,
        positive: Int, negative: Int
    ) {
        val dialogMessage = dialog.findViewById<TextView>(android.R.id.message)
        assertThat(dialogMessage.text.toString()).isEqualTo(rationale)
        val positiveMessage = dialog.findViewById<TextView>(android.R.id.button1)
        assertThat(positiveMessage.text.toString()).isEqualTo(app!!.getString(positive))
        val negativeMessage = dialog.findViewById<TextView>(android.R.id.button2)
        assertThat(negativeMessage.text.toString()).isEqualTo(app!!.getString(negative))
    }

    private fun assertThatHasExpectedButtonsAndRationale(
        dialog: Dialog, rationale: String,
        positive: String, negative: String
    ) {
        val dialogMessage = dialog.findViewById<TextView>(android.R.id.message)
        assertThat(dialogMessage.text.toString()).isEqualTo(rationale)
        val positiveMessage = dialog.findViewById<TextView>(android.R.id.button1)
        assertThat(positiveMessage.text.toString()).isEqualTo(positive)
        val negativeMessage = dialog.findViewById<TextView>(android.R.id.button2)
        assertThat(negativeMessage.text.toString()).isEqualTo(negative)
    }

    private fun assertThatHasExpectedRationale(dialog: Dialog, rationale: String) {
        val dialogMessage = dialog.findViewById<TextView>(android.R.id.message)
        assertThat(dialogMessage.text.toString()).isEqualTo(rationale)
    }

    private fun grantPermissions(perms: Array<String>) {
        shadowApp!!.grantPermissions(*perms)
    }

    private fun showRationale(show: Boolean, vararg perms: String) {
        for (perm in perms) {
            `when`(spyActivity!!.shouldShowRequestPermissionRationale(perm)).thenReturn(show)
            `when`(spySupportFragmentActivity!!.shouldShowRequestPermissionRationale(perm)).thenReturn(
                show
            )
            `when`(spyAppCompatActivity!!.shouldShowRequestPermissionRationale(perm)).thenReturn(
                show
            )
            `when`(spyFragment!!.shouldShowRequestPermissionRationale(perm)).thenReturn(show)
        }
    }

    companion object {

        private val RATIONALE = "RATIONALE"
        private val POSITIVE = "POSITIVE"
        private val NEGATIVE = "NEGATIVE"
        private val ONE_PERM = arrayOf(Manifest.permission.READ_SMS)
        private val ALL_PERMS =
            arrayOf(Manifest.permission.READ_SMS, Manifest.permission.ACCESS_FINE_LOCATION)
        private val SMS_DENIED_RESULT =
            intArrayOf(PackageManager.PERMISSION_DENIED, PackageManager.PERMISSION_GRANTED)
    }
}
