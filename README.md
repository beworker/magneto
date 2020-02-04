
| Feature | Dagger | Magneto | Magnet | 
| ----- | ------ | ------ | ------ | 
| Hierarchy | DAG of Components | DAG of Scopes | Tree of Scopes | 
| Dependency validation | Strong compile-time | Strong compile-time | Weak compile-time, Strong runtime | 
| Entities | Components, Subcomponents, Modules | Scopes | Scopes |
| Annotations | Provides, Binds, Inject | Injectable | Instance |
| Provide into set | In single Component | Global (between modules) | Global (between modules) | 
| Provide into map | In single Component | No | No |
| Code generation | apt | kapt | apt |
| Disposable | No | Scopes | Scopes |
| Full generics support | Yes | Yes | No |
| Support default arguments | No | Yes | Yes (w/ JvmOverloads) |
| Support nullable types | No | Yes | Yes |
| Target Language | Java | Kotlin | Java, Kotlin |
