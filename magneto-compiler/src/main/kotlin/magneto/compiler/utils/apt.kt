package magneto.compiler.utils

import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element

inline fun <reified T> AnnotationMirror.isOfAnnotationType(): Boolean =
    this.annotationType.toString() == T::class.java.name

inline fun <reified A> Element.forEachAttributeOf(
    block: (name: String, value: AnnotationValue) -> Unit
) {
    for (annotationMirror in annotationMirrors) {
        if (annotationMirror.isOfAnnotationType<A>()) {
            for (entry in annotationMirror.elementValues.entries) {
                block(entry.key.simpleName.toString(), entry.value)
            }
        }
    }
}
