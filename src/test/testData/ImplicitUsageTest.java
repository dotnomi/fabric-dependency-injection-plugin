import com.dotnomi.fabricdependencyinjection.annotation.ModScoped;

class UnusedClass {}

@ModScoped
class Dependency {}

@ModScoped
class UsedImplicitlyClass {
  @ModInject
  public ServiceWithInjectableConstructor(Dependency dependency) {}
}