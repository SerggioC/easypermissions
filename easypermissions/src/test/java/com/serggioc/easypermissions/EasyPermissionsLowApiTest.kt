package com.serggioc.easypermissions

import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.serggioc.easypermissions.testhelper.*
import java.util.*

/**
 * Low-API (SDK = 19) tests for [com.serggioc.easypermissions.EasyPermissions].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [19])
class EasyPermissionsLowApiTest {

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
    fun shouldHavePermission_whenHasPermissionsBeforeMarshmallow() {
        assertThat(
            EasyPermissions.hasPermissions(
                ApplicationProvider.getApplicationContext<Context>(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ).isTrue()
    }

    // ------ From Activity ------

    @Test
    fun shouldCallbackOnPermissionGranted_whenRequestFromActivity() {
        EasyPermissions.requestPermissions(
            spyActivity!!,
            RATIONALE,
            TestActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        verify<TestActivity>(spyActivity, times(1))
            .onPermissionsGranted(integerCaptor!!.capture(), listCaptor!!.capture())
        assertThat(integerCaptor.value).isEqualTo(TestActivity.REQUEST_CODE)
        assertThat(listCaptor.value).containsAllIn(ALL_PERMS)
    }

    // ------ From Support Activity ------

    @Test
    fun shouldCallbackOnPermissionGranted_whenRequestFromSupportFragmentActivity() {
        EasyPermissions.requestPermissions(
            spySupportFragmentActivity!!,
            RATIONALE,
            TestSupportFragmentActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        verify<TestSupportFragmentActivity>(spySupportFragmentActivity, times(1))
            .onPermissionsGranted(integerCaptor!!.capture(), listCaptor!!.capture())
        assertThat(integerCaptor.value).isEqualTo(TestSupportFragmentActivity.REQUEST_CODE)
        assertThat(listCaptor.value).containsAllIn(ALL_PERMS)
    }


    @Test
    fun shouldCallbackOnPermissionGranted_whenRequestFromAppCompatActivity() {
        EasyPermissions.requestPermissions(
            spyAppCompatActivity!!,
            RATIONALE,
            TestAppCompatActivity.REQUEST_CODE,
            *ALL_PERMS
        )

        verify<TestAppCompatActivity>(spyAppCompatActivity, times(1))
            .onPermissionsGranted(integerCaptor!!.capture(), listCaptor!!.capture())
        assertThat(integerCaptor.value).isEqualTo(TestAppCompatActivity.REQUEST_CODE)
        assertThat(listCaptor.value).containsAllIn(ALL_PERMS)
    }

    @Test
    fun shouldCallbackOnPermissionGranted_whenRequestFromFragment() {
        EasyPermissions.requestPermissions(
            spyFragment!!,
            RATIONALE,
            TestFragment.REQUEST_CODE,
            *ALL_PERMS
        )

        verify<TestFragment>(spyFragment, times(1))
            .onPermissionsGranted(integerCaptor!!.capture(), listCaptor!!.capture())
        assertThat(integerCaptor.value).isEqualTo(TestFragment.REQUEST_CODE)
        assertThat(listCaptor.value).containsAllIn(ALL_PERMS)
    }

    companion object {

        private val RATIONALE = "RATIONALE"
        private val ALL_PERMS =
            arrayOf(Manifest.permission.READ_SMS, Manifest.permission.ACCESS_FINE_LOCATION)
    }

}
