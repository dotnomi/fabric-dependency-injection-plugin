import com.dotnomi.fabricdependencyinjection.annotation.ModInject;
import com.dotnomi.fabricdependencyinjection.annotation.ModScoped;

class UnmanagedService {}

@ModScoped
class MyController {
  @ModInject
  private <error descr="Cannot inject an unmanaged bean. The target class must be annotated with @ModScoped.">UnmanagedService</error> unmanagedService;
}