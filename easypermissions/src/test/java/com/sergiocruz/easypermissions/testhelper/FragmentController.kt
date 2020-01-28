package com.sergiocruz.easypermissions.testhelper

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario

/**
 * Helper class to allow starting Fragments, similar to the old SupportFragmentController.
 */
class FragmentController<T : Fragment>(clazz: Class<T>) {

    private val scenario: FragmentScenario<T>

    init {
        scenario = FragmentScenario.launch(clazz)
    }

    @Synchronized
    fun resume(): T {
        val fragmentFuture = CompletableFuture<T>()

        scenario.onFragment { fragment -> fragmentFuture.complete(fragment) }

        try {
            return fragmentFuture.get()
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
