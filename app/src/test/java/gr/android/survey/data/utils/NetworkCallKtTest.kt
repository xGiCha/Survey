package gr.android.survey.data.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response

@ExperimentalCoroutinesApi
class NetworkCallKtTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var serviceCallMock: suspend () -> Response<String>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test call success`() = runTest  {
        // Given
        val responseBody = "Response Body"
        val successResponse = Response.success(responseBody)
        val callBack: (suspend () -> Response<String>) = {Response.success(responseBody)}

        Mockito.`when`(serviceCallMock.invoke()).thenReturn(callBack.invoke())

        // When
        val result = call{successResponse}

        // Then
        assert(result is Result.Success)
        assert((result as Result.Success).data == responseBody)
    }

    @Test
    fun `test call client error`() = runTest {
        // Given
        val clientErrorResponse = Response.error<String>(400, mock())

        Mockito.`when`(serviceCallMock.invoke()).thenReturn(clientErrorResponse)

        // When
        val result = call{clientErrorResponse}

        // Then
        assert(result is Result.ClientError)
        assert((result as Result.ClientError).httpCode == 400)
        assert(result.errorMessage == "Bad Request")
    }

    @Test
    fun `test call server error`() = runBlocking {
        // Given
        val serverErrorResponse = Response.error<String>(500, mock())

        Mockito.`when`(serviceCallMock.invoke()).doReturn(serverErrorResponse)

        // When
        val result = call { serverErrorResponse }

        // Then
        assert(result is Result.ServerError)
        assert((result as Result.ServerError).httpCode == 500)
        assert(result.errorMessage == "Internal Server Error")
    }
}
