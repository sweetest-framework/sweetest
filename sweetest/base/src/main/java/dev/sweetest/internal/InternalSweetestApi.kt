package dev.sweetest.internal

@RequiresOptIn("This API is internal in sweetest. It can change any time and should not publicly be depended on.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY)
annotation class InternalSweetestApi
