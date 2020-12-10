package dev.sweetest.demo.v2.cucumberInterop

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    plugin = ["pretty"],
    features = ["src/test/resources/features"],
    glue = [
        "dev.sweetest.demo.v2.cucumberInterop",
        "dev.sweetest.demo.v2.example",
        "dev.sweetest.v2.cucumber2" // has always to be included for sweetest
    ]
)
class CucumberLoginTest
