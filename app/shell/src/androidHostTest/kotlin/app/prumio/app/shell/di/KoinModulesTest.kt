package app.prumio.app.shell.di

import android.content.Context
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify
import kotlin.test.Test

class KoinModulesTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `every definition in prumioModules can be created`() {
        module { includes(prumioModules) }.verify(extraTypes = listOf(Context::class))
    }
}
