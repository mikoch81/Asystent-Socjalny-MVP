package pl.mikoch.asystentsocjalny.core.di

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.mikoch.asystentsocjalny.core.data.CaseDocumentStore
import pl.mikoch.asystentsocjalny.core.data.CaseStore
import pl.mikoch.asystentsocjalny.core.data.DraftStore
import pl.mikoch.asystentsocjalny.core.data.KnowledgeRepository
import pl.mikoch.asystentsocjalny.core.data.LastLocationStore
import pl.mikoch.asystentsocjalny.core.data.SimpleNoteDraftStore
import pl.mikoch.asystentsocjalny.core.data.WorkerProfileStore

/**
 * Smoke test that proves the SingletonComponent graph wires up
 * and every store registered in [AppModule] can be injected.
 *
 * If somebody removes a @Provides or breaks the @Inject constructor of
 * a downstream class, this test will fail at injection time.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AppModuleInjectionTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var knowledgeRepository: KnowledgeRepository
    @Inject lateinit var draftStore: DraftStore
    @Inject lateinit var caseStore: CaseStore
    @Inject lateinit var caseDocumentStore: CaseDocumentStore
    @Inject lateinit var workerProfileStore: WorkerProfileStore
    @Inject lateinit var lastLocationStore: LastLocationStore
    @Inject lateinit var simpleNoteDraftStore: SimpleNoteDraftStore

    @Before
    fun inject() {
        hiltRule.inject()
    }

    @Test
    fun appModule_providesAllStores() {
        assertNotNull(knowledgeRepository)
        assertNotNull(draftStore)
        assertNotNull(caseStore)
        assertNotNull(caseDocumentStore)
        assertNotNull(workerProfileStore)
        assertNotNull(lastLocationStore)
        assertNotNull(simpleNoteDraftStore)
    }
}
