package dev.sweetest.internal

@RequiresOptIn(
    "This API is for implementing integrations for specific testing frameworks (e.g. JUnit and Cucumber) " +
        "and should not be depended on by test implementations."
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY)
annotation class SweetestIntegrationsApi
