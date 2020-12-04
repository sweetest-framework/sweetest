package dev.sweetest.internal

@RequiresOptIn("This API is internal in sweetest. It can change any time and should not publicly be depended on.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY)
annotation class InternalSweetestApi

@RequiresOptIn(
    "This API is for implementing integrations for specific testing frameworks (e.g. JUnit and Cucumber) " +
        "and should not be depended on by test implementations."
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY)
annotation class SweetestIntegrationsApi
