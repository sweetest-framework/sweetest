package com.mysugr.android.testing.v2.cucumberInterop

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    plugin = ["pretty"],
    features = ["src/test/resources/features"],
    glue = [
        "com.mysugr.android.testing.v2.cucumberInterop",
        "com.mysugr.android.testing.v2.example",
        "dev.sweetest.api.v2.cucumber" // has always to be included for sweetest
    ]
)
class CucumberLoginTest
