package dev.sweetest.internal

@RequiresOptIn("This API is to be customized, e.g. by specific testing frameworks like JUnit and Cucumber.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY)
annotation class CustomizableSweetestApi
