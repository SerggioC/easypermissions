package pub.devrel.easypermissions

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import pub.devrel.easypermissions.AppSettingsDialog.CREATOR.DEFAULT_SETTINGS_REQ_CODE
import pub.devrel.easypermissions.testhelper.ActivityController
import pub.devrel.easypermissions.testhelper.FragmentController
import pub.devrel.easypermissions.testhelper.TestActivity
import pub.devrel.easypermissions.testhelper.TestFragment
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23])
class AppSettingsDialogTest {
    private var shadowApp: ShadowApplication? = null
    private var spyActivity: TestActivity? = null
    private var spyFragment: TestFragment? = null
    private var fragmentController: FragmentController<TestFragment>? = null
    private var activityController: ActivityController<TestActivity>? = null
    @Mock
    private val positiveListener: DialogInterface.OnClickListener? = null
    @Mock
    private val negativeListener: DialogInterface.OnClickListener? = null
    @Captor
    private val integerCaptor: ArgumentCaptor<Int>? = null
    @Captor
    private val intentCaptor: ArgumentCaptor<Intent>? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        shadowApp = shadowOf(ApplicationProvider.getApplicationContext<Context>() as Application)

        activityController = ActivityController(TestActivity::class.java)
        fragmentController = FragmentController(TestFragment::class.java)

        spyActivity = Mockito.spy(activityController!!.resume())
        spyFragment = Mockito.spy(fragmentController!!.resume())
    }

    // ------ From Activity ------

    @Test
    fun shouldShowExpectedSettingsDialog_whenBuildingFromActivity() {
        AppSettingsDialog.Builder(spyActivity!!)
            .setTitle(android.R.string.dialog_alert_title)
            .setRationale(android.R.string.unknownName)
            .setPositiveButton(android.R.string.ok)
            .setNegativeButton(android.R.string.cancel)
            .setThemeResId(R.style.Theme_AppCompat)
            .build()
            .show()

        verify<TestActivity>(spyActivity, times(1))
            .startActivityForResult(intentCaptor!!.capture(), integerCaptor!!.capture())
        assertThat(integerCaptor.value).isEqualTo(DEFAULT_SETTINGS_REQ_CODE)
        assertThat(Objects.requireNonNull<ComponentName>(intentCaptor.value.component).getClassName())
            .isEqualTo(AppSettingsDialogHolderActivity::class.java.name)

        val startedIntent = shadowApp!!.nextStartedActivity
        val shadowIntent = shadowOf(startedIntent)
        assertThat(shadowIntent.intentClass).isEqualTo(AppSettingsDialogHolderActivity::class.java)
    }

    @Test
    fun shouldPositiveListener_whenClickingPositiveButtonFromActivity() {
        val alertDialog = AppSettingsDialog.Builder(spyActivity!!)
            .setTitle(TITLE)
            .setRationale(RATIONALE)
            .setPositiveButton(POSITIVE)
            .setNegativeButton(NEGATIVE)
            .setThemeResId(R.style.Theme_AppCompat)
            .build()
            .showDialog(positiveListener, negativeListener)
        val positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positive.performClick()

        verify<DialogInterface.OnClickListener>(positiveListener, times(1))
            .onClick(any(DialogInterface::class.java), anyInt())
    }

    @Test
    fun shouldNegativeListener_whenClickingPositiveButtonFromActivity() {
        val alertDialog = AppSettingsDialog.Builder(spyActivity!!)
            .setTitle(TITLE)
            .setRationale(RATIONALE)
            .setPositiveButton(POSITIVE)
            .setNegativeButton(NEGATIVE)
            .setThemeResId(R.style.Theme_AppCompat)
            .build()
            .showDialog(positiveListener, negativeListener)
        val positive = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        positive.performClick()

        verify<DialogInterface.OnClickListener>(negativeListener, times(1))
            .onClick(any(DialogInterface::class.java), anyInt())
    }

    @Test
    fun shouldShowExpectedSettingsDialog_whenBuildingFromSupportFragment() {
        AppSettingsDialog.Builder(spyFragment!!)
            .setTitle(android.R.string.dialog_alert_title)
            .setRationale(android.R.string.unknownName)
            .setPositiveButton(android.R.string.ok)
            .setNegativeButton(android.R.string.cancel)
            .setThemeResId(R.style.Theme_AppCompat)
            .build()
            .show()

        verify<TestFragment>(spyFragment, times(1))
            .startActivityForResult(intentCaptor!!.capture(), integerCaptor!!.capture())
        assertThat(integerCaptor.value).isEqualTo(DEFAULT_SETTINGS_REQ_CODE)
        assertThat(Objects.requireNonNull<ComponentName>(intentCaptor.value.component).getClassName())
            .isEqualTo(AppSettingsDialogHolderActivity::class.java.name)

        val startedIntent = shadowApp!!.nextStartedActivity
        val shadowIntent = shadowOf(startedIntent)
        assertThat(shadowIntent.intentClass).isEqualTo(AppSettingsDialogHolderActivity::class.java)
    }

    @Test
    fun shouldPositiveListener_whenClickingPositiveButtonFromSupportFragment() {
        val alertDialog = AppSettingsDialog.Builder(spyFragment!!)
            .setTitle(TITLE)
            .setRationale(RATIONALE)
            .setPositiveButton(POSITIVE)
            .setNegativeButton(NEGATIVE)
            .setThemeResId(R.style.Theme_AppCompat)
            .build()
            .showDialog(positiveListener, negativeListener)
        val positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positive.performClick()

        verify<DialogInterface.OnClickListener>(positiveListener, times(1))
            .onClick(any(DialogInterface::class.java), anyInt())
    }

    @Test
    fun shouldNegativeListener_whenClickingPositiveButtonFromSupportFragment() {
        val alertDialog = AppSettingsDialog.Builder(spyFragment!!)
            .setTitle(TITLE)
            .setRationale(RATIONALE)
            .setPositiveButton(POSITIVE)
            .setNegativeButton(NEGATIVE)
            .setThemeResId(R.style.Theme_AppCompat)
            .build()
            .showDialog(positiveListener, negativeListener)
        val positive = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        positive.performClick()

        verify<DialogInterface.OnClickListener>(negativeListener, times(1))
            .onClick(any(DialogInterface::class.java), anyInt())
    }

    companion object {

        private val TITLE = "TITLE"
        private val RATIONALE = "RATIONALE"
        private val NEGATIVE = "NEGATIVE"
        private val POSITIVE = "POSITIVE"
    }

}
