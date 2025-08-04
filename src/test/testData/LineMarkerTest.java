import com.dotnomi.fabricdependencyinjection.annotation.ModInject;
import com.dotnomi.fabricdependencyinjection.annotation.ModScoped;

@ModScoped
class ManagedService {}

@ModScoped
class MyOtherController {
  @ModInject
  private ManagedService managedService;
}