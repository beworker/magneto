
| Feature | Dagger | Magnet | Magneto |
| ----- | ------ | ------ | ------ | 
| Hierarchy | DAG of Components | Tree of Scopes | DAG of Scopes |  
| Dependency validation | Strong compile-time | Weak compile-time, Strong runtime | Strong compile-time |
| Entities | Components, Subcomponents, Modules | Scopes | Scopes |
| Annotations | Provides, Binds, Inject | Instance | Injectable |
| Provide into set | Within Component | Global (between modules) | Global (between modules) | 
| Provide into map | Within Component | No | No |
| Reflection | No | Single class | No |
| Disposable | No | Scopes | Scopes |
| Injection of generics | Yes | No | Yes |
| Target Language | Java | Java, Kotlin | Kotlin |
| Inject w/ default arguments | No | Yes (w/ JvmOverloads) | Yes |
