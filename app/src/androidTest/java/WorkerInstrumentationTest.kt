import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.example.bluromatic.KEY_IMAGE_URI
import com.example.bluromatic.workers.BlurWorker
import com.example.bluromatic.workers.CleanupWorker
import com.example.bluromatic.workers.SaveImageToFileWorker
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class WorkerInstrumentationTest {
    private val mockUriInput = KEY_IMAGE_URI to "android.resource://com.example.bluromatic/drawable/android_cupcake"
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun cleanupWorker_doWork_resultSuccess() {
        val worker = TestListenableWorkerBuilder<CleanupWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assertTrue(result is ListenableWorker.Result.Success)
        }
    }

    @Test
    fun blurWorker_doWork_resultSuccessReturnsUri() {
        val worker = TestListenableWorkerBuilder<BlurWorker>(context)
            .setInputData(workDataOf(mockUriInput))
            .build()

        runBlocking {
            val result = worker.doWork()
            val resultUri = result.outputData.getString(mockUriInput.first)
            assertTrue(result is ListenableWorker.Result.Success)
            assertTrue(result.outputData.keyValueMap.containsKey(mockUriInput.first))
            assertTrue(
                resultUri?.startsWith("file:///data/user/0/com.example.bluromatic/files/blur_filter_outputs/blur-filter-output-")
                    ?: false
            )
        }
    }

    @Test
    fun saveImageToFileWorker_doWork_resultSuccessReturnsUrl() {
        val worker = TestListenableWorkerBuilder<SaveImageToFileWorker>(context)
            .setInputData(workDataOf(mockUriInput))
            .build()

        runBlocking {
            val result = worker.doWork()
            val resultUri = result.outputData.getString(mockUriInput.first)
            assertTrue(result is ListenableWorker.Result.Success)
            assertTrue(result.outputData.keyValueMap.containsKey(mockUriInput.first))
            assertTrue(
                resultUri?.startsWith("content://media/external/images/media/")
                    ?: false
            )
        }
    }
}