package magneto.compiler

import com.google.common.truth.Truth
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language
import org.junit.rules.TemporaryFolder

fun TemporaryFolder.compile(vararg sourceFiles: SourceFile): KotlinCompilation.Result =
    KotlinCompilation()
        .apply {
            workingDir = root
            annotationProcessors = listOf(MagnetoProcessor())
            inheritClassPath = true
            sources = sourceFiles.asList()
            verbose = false
        }
        .compile()

fun KotlinCompilation.Result.assertGeneratedCode(@Language("kotlin") vararg expected: String) {
    if (exitCode != KotlinCompilation.ExitCode.OK) {
        error("\nCompilation error:\n$messages")
    }
    Truth.assertThat(sourcesGeneratedByAnnotationProcessor).hasSize(expected.size)
    for ((index, expectedText) in expected.withIndex()) {
        val generated = sourcesGeneratedByAnnotationProcessor[index].readText()
        Truth.assertThat(generated).isEqualTo(expectedText.trimIndent())
    }
}
