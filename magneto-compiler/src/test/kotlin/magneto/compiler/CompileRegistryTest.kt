package magneto.compiler

import com.tschuchort.compiletesting.SourceFile
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CompileRegistryTest {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Test
    fun `Generate registry, prepare injectables`() {
        val compilate = temporaryFolder.compile(
            SourceFile.kotlin(
                "Model.kt",
                """
                    package magneto.test
                    import magneto.Injectable

                    @Injectable 
                    class TypeA
                    
                    @Injectable
                    class TypeB
                    
                    @Injectable
                    class TypeC(val typeB: TypeB)
                    
                    @Injectable
                    class TypeD(val typeA: TypeA, val typeC: TypeC)
                """
            )
        )

        compilate.assertGeneratedCode(
            """
                package magneto.generated.factories
                
                import magneto.internal.InjectableFactory
                import magneto.test.TypeB
                
                @InjectableFactory(metadata = "\n\u0012magneto.test.TypeB\u0012\u0012magneto.test.TypeB")
                fun magneto_test_TypeB(): TypeB = TypeB()
          
            """,
            """
                package magneto.generated.factories
                
                import magneto.internal.InjectableFactory
                import magneto.test.TypeB
                import magneto.test.TypeC
                
                @InjectableFactory(metadata =
                    "\n\u0012magneto.test.TypeC\u0012\u0012magneto.test.TypeC\u001a\u001b\n\u0005typeB\u0012\u0012magneto.test.TypeB")
                fun magneto_test_TypeC(typeB: TypeB): TypeC = TypeC(typeB)
            """,
            """
                package magneto.generated.factories
                
                import magneto.internal.InjectableFactory
                import magneto.test.TypeA
                import magneto.test.TypeC
                import magneto.test.TypeD
                
                @InjectableFactory(metadata =
                    "\n\u0012magneto.test.TypeD\u0012\u0012magneto.test.TypeD\u001a\u001b\n\u0005typeA\u0012\u0012magneto.test.TypeA\u001a\u001b\n\u0005typeC\u0012\u0012magneto.test.TypeC")
                fun magneto_test_TypeD(typeA: TypeA, typeC: TypeC): TypeD = TypeD(typeA, typeC)
            """,
            """
                package magneto.generated.factories
                
                import magneto.internal.InjectableFactory
                import magneto.test.TypeA
                
                @InjectableFactory(metadata = "\n\u0012magneto.test.TypeA\u0012\u0012magneto.test.TypeA")
                fun magneto_test_TypeA(): TypeA = TypeA()

            """
        )
    }

    @Test
    fun `Generate registry`() {
        val compilate = temporaryFolder.compile(
            SourceFile.kotlin(
                "Model.kt",
                """
                    package magneto.test
                     
                    class TypeA
                    class TypeB
                    class TypeC(val typeB: TypeB)
                    class TypeD(val typeA: TypeA, val typeC: TypeC)
                """
            ),
            SourceFile.kotlin(
                "magneto_test_TypeA.kt",
                """
                    package magneto.generated.factories
                    
                    import magneto.internal.InjectableFactory
                    import magneto.test.TypeA
                    
                    @InjectableFactory(metadata = "\n\u0012magneto.test.TypeA\u0012\u0012magneto.test.TypeA")
                    fun magneto_test_TypeA(): TypeA = TypeA()
                """
            ),
            SourceFile.kotlin(
                "magneto_test_TypeB.kt",
                """
                    package magneto.generated.factories
                    
                    import magneto.internal.InjectableFactory
                    import magneto.test.TypeB
                    
                    @InjectableFactory(metadata = "\n\u0012magneto.test.TypeB\u0012\u0012magneto.test.TypeB")
                    fun magneto_test_TypeB(): TypeB = TypeB()
                """
            ),
            SourceFile.kotlin(
                "magneto_test_TypeC.kt",
                """
                    package magneto.generated.factories
                    
                    import magneto.internal.InjectableFactory
                    import magneto.test.TypeB
                    import magneto.test.TypeC
                    
                    @InjectableFactory(metadata =
                        "\n\u0012magneto.test.TypeC\u0012\u0012magneto.test.TypeC\u001a\u001b\n\u0005typeB\u0012\u0012magneto.test.TypeB")
                    fun magneto_test_TypeC(typeB: TypeB): TypeC = TypeC(typeB)
                """
            ),
            SourceFile.kotlin(
                "magneto_test_TypeD.kt",
                """
                    package magneto.generated.factories
                    
                    import magneto.internal.InjectableFactory
                    import magneto.test.TypeA
                    import magneto.test.TypeC
                    import magneto.test.TypeD
                    
                    @InjectableFactory(metadata =
                        "\n\u0012magneto.test.TypeD\u0012\u0012magneto.test.TypeD\u001a\u001b\n\u0005typeA\u0012\u0012magneto.test.TypeA\u001a\u001b\n\u0005typeC\u0012\u0012magneto.test.TypeC")
                    fun magneto_test_TypeD(typeA: TypeA, typeC: TypeC): TypeD = TypeD(typeA, typeC)
                """
            ),
            SourceFile.kotlin(
                "ScopeA.kt",
                """
                    package test.main
                    
                    import magneto.Scope
                    import magneto.test.TypeA
                    import magneto.test.TypeB
                    import magneto.test.TypeC
                    import magneto.test.TypeD
                    
                    @Scope
                    abstract class ScopeA(
                        val typeA: TypeA,
                        val typeB: TypeB
                    ) {
                        abstract val typeC: TypeC
                        abstract val typeD: TypeD
                    }
                """
            ),
            SourceFile.kotlin(
                "Registry.kt",
                """
                    package test.main
                    import magneto.Registry
                    
                    @Registry
                    interface MainRegistry
                """
            )
        )

        compilate.assertGeneratedCode(
            """
                package test.main
                
                import magneto.generated.extensions.test_main_ScopeAExtension
                import magneto.internal.Magneto
                import magneto.test.TypeA
                import magneto.test.TypeB
                import magneto.test.TypeC
                import magneto.test.TypeD
                
                class MagnetoScopeA(
                  typeA: TypeA,
                  typeB: TypeB
                ) : ScopeA(typeA, typeB) {
                  private val _extension: test_main_ScopeAExtension =
                      Magneto.createScopeExtension(test_main_ScopeAExtension::class,typeA,typeB)
                
                  override val typeC: TypeC
                    get() = _extension.typeC
                
                  override val typeD: TypeD
                    get() = _extension.typeD
                }

            """,
            """
                package magneto.generated.extensions
                
                import magneto.internal.ScopeExtension
                import magneto.test.TypeA
                import magneto.test.TypeB
                import magneto.test.TypeC
                import magneto.test.TypeD
                
                @ScopeExtension(metadata =
                    "\n\u0010test.main.ScopeA\u0012\u001b\n\u0005typeA\u0012\u0012magneto.test.TypeA\u0012\u001b\n\u0005typeB\u0012\u0012magneto.test.TypeB\u001a\u001b\n\u0005typeC\u0012\u0012magneto.test.TypeC\u001a\u001b\n\u0005typeD\u0012\u0012magneto.test.TypeD")
                interface test_main_ScopeAExtension {
                  val typeA: TypeA
                
                  val typeB: TypeB
                
                  val typeC: TypeC
                
                  val typeD: TypeD
                }

            """,
            """
                package magneto.generated.extensions
                
                import magneto.test.TypeA
                import magneto.test.TypeB
                import magneto.test.TypeC
                import magneto.test.TypeD
                
                class test_main_MagnetoScopeAExtension(
                  override val typeA: TypeA,
                  override val typeB: TypeB
                ) : test_main_ScopeAExtension {
                  override val typeC: TypeC by lazy { TODO() }
                
                  override val typeD: TypeD by lazy { TODO() }
                }

            """
        )
    }
}
