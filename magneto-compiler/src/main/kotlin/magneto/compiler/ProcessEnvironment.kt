package magneto.compiler

import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class ProcessEnvironment(
    val messager: Messager,
    val elements: Elements,
    val types: Types,
    val filer: Filer,
    val round: RoundEnvironment
)

class CompilationException(message: String, val element: Element) : Exception(message)

fun Element.failCompilation(message: String): Nothing {
    throw CompilationException(message, this)
}
