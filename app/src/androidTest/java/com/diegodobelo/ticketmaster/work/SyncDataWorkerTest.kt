package com.diegodobelo.ticketmaster.work

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.diegodobelo.ticketmaster.helper.createFakeCityEvent
import com.diegodobelo.ticketmaster.models.transformers.EventDataTransformer
import com.diegodobelo.ticketmaster.network.TicketMasterService
import com.diegodobelo.ticketmaster.storage.AppDatabase
import com.diegodobelo.ticketmaster.storage.dao.CityEventDao
import com.diegodobelo.ticketmaster.storage.repositories.CityEventRepository
import com.diegodobelo.ticketmaster.usecases.FormatDateUseCase
import com.diegodobelo.ticketmaster.usecases.SyncDataUseCase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
class SyncDataWorkerTest {
    private lateinit var context: Context
    private lateinit var syncDataUseCase: SyncDataUseCase
    private lateinit var ticketMasterService: TicketMasterService
    private lateinit var cityEventRepository: CityEventRepository
    private lateinit var eventDataTransformer: EventDataTransformer
    private lateinit var formatDateUseCase: FormatDateUseCase

    private lateinit var cityEventDao: CityEventDao

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        formatDateUseCase = FormatDateUseCase()
        eventDataTransformer = EventDataTransformer(formatDateUseCase)
        context = ApplicationProvider.getApplicationContext()

        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        cityEventDao = db.conversionsDao()

        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        cityEventRepository = CityEventRepository(cityEventDao)
        ticketMasterService = Mockito.mock()
        syncDataUseCase = SyncDataUseCase(
            eventDataTransformer,
            ticketMasterService,
            cityEventRepository
        )
    }

    @Test
    fun checkWorkerSyncsDataSuccessfully() = runTest {
        Mockito.`when`(ticketMasterService.getEvents("")).thenReturn(createFakeCityEvent()).thenReturn(createFakeCityEvent())
        val worker = TestListenableWorkerBuilder<SyncDataWorker>(context)
            .setWorkerFactory(TestWorkerFactory(syncDataUseCase))
            .build()
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.success(), result)

    }

    @Test
    fun checkWorkerSyncDataRetryWhenThereIsAnException() = runTest {
        Mockito.`when`(ticketMasterService.getEvents("")).thenThrow(RuntimeException("cannot get events"))
        val worker = TestListenableWorkerBuilder<SyncDataWorker>(context)
            .setWorkerFactory(TestWorkerFactory(syncDataUseCase))
            .build()
        runBlocking {
            val result = worker.doWork()
            assertEquals(ListenableWorker.Result.retry(), result)
        }
    }

}

class TestWorkerFactory(private val syncUseCase: SyncDataUseCase) : WorkerFactory() {
    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker {
        return SyncDataWorker(appContext, workerParameters, syncUseCase)
    }
}