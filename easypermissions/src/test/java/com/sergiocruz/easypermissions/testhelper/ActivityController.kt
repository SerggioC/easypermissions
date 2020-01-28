package com.sergiocruz.easypermissions.testhelper

import android.app.Activity

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import androidx.test.core.app.ActivityScenario

/**
 * Helper class to allow starting Activity, similar to the Robolectric ActivityConroller.
 */
class ActivityController<T : Activity>(clazz: Class<T>) {

    private val scenario: ActivityScenario<T> = ActivityScenario.launch(clazz)

    @Synchronized
    fun resume(): T {
        val ActivityFuture = CompletableFuture<T>()

        scenario.onActivity { Activity -> ActivityFuture.complete(Activity) }

        try {
            return ActivityFuture.get()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        }

    }

    fun reset() {
        scenario.recreate()
    }

}
