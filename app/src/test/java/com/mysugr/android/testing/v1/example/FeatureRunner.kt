package com.mysugr.android.testing.v1.example

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    plugin = ["pretty"],
    features = ["src/test/resources/features"],
    glue = ["com.mysugr.android.testing.v1.example.feature", "com.mysugr.sweetest.framework.cucumber"]
)
class FeatureRunner
