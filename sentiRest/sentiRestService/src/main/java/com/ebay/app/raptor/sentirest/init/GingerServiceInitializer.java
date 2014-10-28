package com.ebay.app.raptor.sentirest.init;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import com.ebay.raptor.kernel.lifecycle.DependencyInjectionInitializer;

@Singleton @DependencyInjectionInitializer
public class GingerServiceInitializer {

	 
    GingerServiceInitializer() {
        System.out.println("Initializer called.");
    }
 
//  Called after this bean is instantiated by DI framework
	@PostConstruct
    public void postInit() {
        System.out.println("PostInit called");
    }
 
//	Called before this bean is destroyed by DI framework     
    @PreDestroy
    public void shutdown() {
        System.out.println("Shutdown called");
    }
}
